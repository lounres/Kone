/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.feature

import dev.lounres.kone.collections.standard.KoneIterableList
import dev.lounres.kone.collections.standard.KoneMutableIterableList
import dev.lounres.kone.collections.standard.utils.indices
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke
import kotlin.reflect.KClass


public interface FeatureProvider<in T>: KoneContext {
    public fun <F: Any> T.getFeature(type: KClass<F>): F?
}

context(FeatureProvider<T>)
public inline fun <T, reified F: Any> T.getFeature(): F? = getFeature(F::class)

internal fun <T, F: Any> FeatureProvider<T>.provideFeatureFor(instance: T, type: KClass<F>): F? = this { instance.getFeature(type) }

public operator fun <T> FeatureProvider<T>.plus(other: FeatureProvider<T>): FeatureProvider<T> =
    object : FeatureProvider<T> {
        override fun <F : Any> T.getFeature(type: KClass<F>): F? =
            this@plus.provideFeatureFor(this, type) ?:
            other.provideFeatureFor(this, type)
    }

public fun <T> combine(vararg providers: FeatureProvider<T>): FeatureProvider<T> =
    object : FeatureProvider<T> {
        override fun <F : Any> T.getFeature(type: KClass<F>): F? {
            for (index in providers.indices) {
                val feature = providers[index].provideFeatureFor(this, type)
                if (feature != null) return feature
            }
            return null
        }
    }

public interface FeatureStorage {
    public fun <F: Any> getFeature(key: Any, type: KClass<F>): F?
    public fun <F: Any> storeFeature(key: Any, type: KClass<F>, value: F)
}

public inline fun <F: Any> FeatureStorage.getOrStoreFeatureMaybe(key: Any, type: KClass<F>, valueComputer: () -> F?): F? =
    getFeature(key, type) ?: valueComputer().also { if (it != null) storeFeature(key, type, it) }

public inline fun <F: Any> FeatureStorage.getOrStoreFeature(key: Any, type: KClass<F>, valueComputer: () -> F): F =
    getFeature(key, type) ?: valueComputer().also { storeFeature(key, type, it) }

public fun <F: Any> Any?.tryGetOrStoreFeatureMaybe(key: Any, type: KClass<F>, valueComputer: () -> F?): F? =
    (if (this is FeatureStorage) getFeature(key, type) else null) ?: valueComputer().also { if (it != null && this is FeatureStorage) storeFeature(key, type, it) }

public fun <F: Any> Any?.tryGetOrStoreFeature(key: Any, type: KClass<F>, valueComputer: () -> F): F =
    (if (this is FeatureStorage) getFeature(key, type) else null) ?: valueComputer().also { if (this is FeatureStorage) storeFeature(key, type, it) }

public interface FeatureProviderHolder<in T>: FeatureProvider<T> {
    public val featureProviders: KoneIterableList<FeatureProvider<T>>
    public val featureStorageKey: Any get() = this

    override fun <F : Any> T.getFeature(type: KClass<F>): F? = this.tryGetOrStoreFeatureMaybe(featureStorageKey, type) {
        for (index in featureProviders.indices) {
            val feature = featureProviders[index].provideFeatureFor(this, type)
            if (feature != null) return@tryGetOrStoreFeatureMaybe feature
        }
        return@tryGetOrStoreFeatureMaybe null
    }
}

public interface ExtendableFeatureProviderHolder<T>: FeatureProviderHolder<T> {
    override val featureProviders: KoneMutableIterableList<FeatureProvider<T>>
    public fun install(featureProvider: FeatureProvider<T>) {
        featureProviders.add(featureProvider)
    }
}
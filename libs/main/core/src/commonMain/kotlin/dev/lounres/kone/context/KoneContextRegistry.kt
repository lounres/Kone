/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.context

import dev.lounres.kone.util.registry.Registry
import dev.lounres.kone.util.registry.RegistryBuilder
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.registry.getOrDefault
import dev.lounres.kone.util.registry.getOrElse
import dev.lounres.kone.util.registry.getOrNull
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


// TODO: Docs in progress.

/**
 * Type-safe registry (a.k.a. type-safe map) made especially for contexts in Kone.
 */
@JvmInline
public value class KoneContextRegistry @PublishedApi internal constructor(
    /**
     * Underlying type-safe registry.
     */
    public val contexts: Registry
)

/**
 * Provides receiver for Kone context registry.
 */
public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    return block(this)
}

/**
 * Shortcut for getting context of type [Context] from [KoneContextRegistry] by corresponding key
 * or throw if nothing is associated with the key.
 */
public fun <Context> KoneContextRegistry.load(key: RegistryKey<Context>): Context = this.contexts[key]
/**
 * Shortcut for getting context of type [Context] from [KoneContextRegistry] by corresponding key
 * or null if nothing is associated with the key.
 */
public fun <Context> KoneContextRegistry.loadOrNull(key: RegistryKey<Context>): Context? = this.contexts.getOrNull(key)
/**
 * Shortcut for getting context of type [Context] from [KoneContextRegistry] by corresponding key
 * or default value if nothing is associated with the key.
 */
public fun <Context> KoneContextRegistry.loadOrDefault(key: RegistryKey<Context>, default: Context): Context = this.contexts.getOrDefault(key, default)
/**
 * Shortcut for getting context of type [Context] from [KoneContextRegistry] by corresponding key
 * or computes and returns value via [block] if nothing is associated with the key.
 */
public inline fun <Context> KoneContextRegistry.loadOrElse(key: RegistryKey<Context>, block: () -> Context): Context = this.contexts.getOrElse(key, block)

/**
 * Builder for [KoneContextRegistry].
 */
@JvmInline
public value class KoneContextRegistryBuilder @PublishedApi internal constructor(public val contextsBuilder: RegistryBuilder)

/**
 * Builder function for [KoneContextRegistry].
 */
public inline fun KoneContextRegistry(block: KoneContextRegistryBuilder.() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(
        Registry {
            KoneContextRegistryBuilder(this).block()
        }
    )
}

/**
 * Installs provided [context] associating it by corresponding [key].
 */
public fun <T> KoneContextRegistryBuilder.install(key: RegistryKey<T>, context: T) {
    contextsBuilder[key] = context
}

/**
 * Installs contexts associated by corresponding keys from provided [otherKoneContextRegistry].
 */
public fun KoneContextRegistryBuilder.installAllFrom(otherKoneContextRegistry: KoneContextRegistry) {
    contextsBuilder.setFrom(otherKoneContextRegistry.contexts)
}
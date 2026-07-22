/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.ProviderRegistryWrapper
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


/**
 * Type-safe registry (a.k.a. type-safe map) made especially for contexts in Kone.
 */
@JvmInline
public value class KoneContextRegistry(
    /**
     * Underlying type-safe registry.
     */
    override val registry: OwnedProviderRegistry<KoneContextRegistry>
) : ProviderRegistryWrapper<KoneContextRegistry> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneContextRegistry
    }
    
    public data object Key : RegistryKey<KoneContextRegistry> {
        override fun toString(): String = "dev.lounres.kone.contexts.KoneContextRegistry.Key"
    }
}

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
public annotation class KoneContextRegistryBuilderDsl

/**
 * Builder function for [KoneContextRegistry].
 */
public inline fun KoneContextRegistry.Companion.build(block: (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneContextRegistry.Companion.buildWithProvider(
    block: context(KoneContextRegistry.Provider) (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit
): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneContextRegistry.Provider {
        var result: KoneContextRegistry? = null
        override fun get(): KoneContextRegistry =
            result ?: error("KoneContextRegistry is not yet initialized but was requested by its properties.")
    }
    val result = KoneContextRegistry(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}

public fun KoneContextRegistry.tryToGetAll() {
    @Suppress("ControlFlowWithEmptyBody", "DestructuringDeclaration")
    for (_ in this.asRegistrationIterable()) {}
}

///**
// * Provides receiver for Kone context registry.
// */
//public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    return block(this)
//}
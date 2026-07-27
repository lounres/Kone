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
 *
 * Wraps an [OwnedProviderRegistry] whose owner type is [KoneContextRegistry] itself, enabling
 * lazy, provider-based storage and retrieval of Kone context values. Implements [ProviderRegistryWrapper]
 * and delegates registry operations to the underlying [registry].
 *
 * @param registry The underlying [OwnedProviderRegistry] that stores key–provider associations for this context registry.
 */
@JvmInline
public value class KoneContextRegistry(
    /**
     * The underlying type-safe owned provider registry that this value class wraps.
     *
     * All lookup, containment, and iteration operations are delegated to this registry.
     */
    override val registry: OwnedProviderRegistry<KoneContextRegistry>
) : ProviderRegistryWrapper<KoneContextRegistry> {
    public companion object;
    
    /**
     * A provider of a [KoneContextRegistry] instance, intended for use as a context parameter.
     *
     * Allows registry entries to refer to the registry currently being built before it is fully
     * constructed, which is required for mutually dependent or self-referential context values.
     */
    public fun interface Provider {
        /**
         * Returns the [KoneContextRegistry] supplied by this provider.
         *
         * @return The [KoneContextRegistry] instance. During [buildWithProvider], this throws if
         * called before the registry has been initialized.
         */
        public fun get(): KoneContextRegistry
    }
    
    /**
     * The [RegistryKey] that identifies a [KoneContextRegistry] value inside other registries.
     *
     * Use this key to store or retrieve a [KoneContextRegistry] from a parent or enclosing registry.
     */
    public data object Key : RegistryKey<KoneContextRegistry> {
        override fun toString(): String = "dev.lounres.kone.contexts.KoneContextRegistry.Key"
    }
}

/**
 * Marks the DSL scope used when building a [KoneContextRegistry].
 *
 * Prevents accidental nesting of registry-builder lambdas from outer scopes.
 */
@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
public annotation class KoneContextRegistryBuilderDsl

/**
 * Builds a [KoneContextRegistry] from key–provider associations configured in [block].
 *
 * Creates a new [OwnedProviderRegistry] for [KoneContextRegistry] and wraps it in a [KoneContextRegistry]
 * value. The [block] lambda is invoked exactly once.
 *
 * @param block A lambda with [MutableOwnedProviderRegistry] of
 * [KoneContextRegistry] as its receiver. Use it to register context values and providers. Its result
 * is ignored.
 * @return A [KoneContextRegistry] containing the associations configured in [block].
 */
public inline fun KoneContextRegistry.Companion.build(block: (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(OwnedProviderRegistry.build { this.block() })
}

/**
 * Builds a [KoneContextRegistry] while supplying the registry-under-construction through a context parameter.
 *
 * Similar to [build], but additionally provides a [KoneContextRegistry.Provider] as a context parameter
 * inside [block]. This allows registered providers to access the registry being built (via
 * [KoneContextRegistry.Provider.get]) before construction completes, which is needed for entries that
 * depend on other entries in the same registry. The [block] lambda is invoked exactly once.
 *
 * @param block A lambda with [KoneContextRegistry.Provider] as a context parameter and
 * [MutableOwnedProviderRegistry] of [KoneContextRegistry] as its receiver.
 * Use the context parameter to obtain the registry under construction; use the receiver to register
 * context values and providers. Its result is ignored.
 * @return A [KoneContextRegistry] containing the associations configured in [block].
 */
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

/**
 * Eagerly evaluates every value registered in this context registry.
 *
 * Iterates over [asRegistrationIterable] and resolves each entry, forcing all lazy providers to run.
 * Useful for validating a registry at construction time or warming caches before use.
 *
 * @receiver The [KoneContextRegistry] whose registered values should be evaluated.
 */
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
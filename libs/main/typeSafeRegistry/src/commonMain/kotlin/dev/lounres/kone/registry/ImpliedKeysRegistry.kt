/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.EmptyIterator
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Represents a registration of an implied key with its mapping function.
 * 
 * @param key The [RegistryKey] that can be implied.
 * @param mapping A function that computes the value for the implied key from the source value.
 * @param I The type of the source value from which implied values are derived.
 * @param T The type of the value that the implied key will produce.
 */
public data class ImplicationRegistration<in I, T>(
    val key: RegistryKey<T>,
    val mapping: (I) -> T,
)

/**
 * A registry that stores keys that can be implied from other keys, along with their mapping functions.
 * 
 * This allows defining relationships between keys where the presence of one key implies
 * the presence of other keys, with values computed from the source value.
 * 
 * @param I The type of the source value from which implied keys can be derived.
 */
public interface ImpliedKeysRegistry<in I> : Iterable<ImplicationRegistration<I, *>> {
    /**
     * Checks if this registry contains the specified key.
     * 
     * @param registryKey The [RegistryKey] to check for presence.
     * @return `true` if the [registryKey] is registered as an implied key, `false` otherwise.
     */
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    
    /**
     * Gets the mapping function for the specified key.
     * 
     * @param registryKey The [RegistryKey] to get the mapping function for.
     * @return A function that computes the value for the implied key from the source value of type [I].
     * @throws IllegalArgumentException if the [registryKey] is not present in this registry.
     */
    public operator fun <T> get(registryKey: RegistryKey<out T>): (I) -> T
    
    /**
     * An empty implied keys registry that contains no implied keys.
     */
    public object Empty : ImpliedKeysRegistry<Any?> {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> get(registryKey: RegistryKey<out T>): (Any?) -> T = error(TODO())
        override fun iterator(): Iterator<ImplicationRegistration<Any?, *>> = EmptyIterator
    }
}

/**
 * Creates a new [ImpliedKeysRegistry] using the builder pattern.
 * 
 * @param I The type of the source value from which implied keys can be derived.
 * @param builder A lambda that configures the [ImpliedKeysRegistryBuilder].
 * @return A new [ImpliedKeysRegistry] configured according to the [builder].
 */
public inline fun <I> ImpliedKeysRegistry(builder: ImpliedKeysRegistryBuilder<I>.() -> Unit): ImpliedKeysRegistry<I> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return ImpliedKeysRegistryBuilder<I>().apply(builder)
}

/**
 * Builder for creating [ImpliedKeysRegistry] instances.
 * 
 * This builder allows adding implied keys with their mapping functions through a fluent API.
 * 
 * @param I The type of the source value from which implied keys can be derived.
 */
@Suppress("UNCHECKED_CAST")
public class ImpliedKeysRegistryBuilder<I> @PublishedApi internal constructor() : ImpliedKeysRegistry<I> {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, (I) -> Any?> = mutableMapOf()
    
    override operator fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override operator fun <T> get(registryKey: RegistryKey<out T>): (I) -> T {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return content[RegistryKeyMapWrapper(registryKey)] as (I) -> T
    }
    
    override fun iterator(): Iterator<ImplicationRegistration<I, *>> {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return content.entries.map { (val key, val mapping = value) -> ImplicationRegistration(key.key as RegistryKey<Any?>, mapping) }.iterator()
    }
    
    /**
     * Adds an implied key with a custom mapping function.
     * 
     * @receiver The [RegistryKey] to register as an implied key.
     * @param T The type of the value that the implied key will produce.
     * @param value A function that computes the value for this implied key from the source value of type [I].
     */
    public infix fun <T> RegistryKey<in T>.implies(value: (I) -> T) {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        content[RegistryKeyMapWrapper(this)] = value
    }
    
    /**
     * Adds an implied key that maps directly to the source value.
     * 
     * This is a convenience method for the common case where the implied key should have
     * the same value as the source value (cast to the appropriate type). It is equivalent to:
     * ```kotlin
     * this implies { it }
     * ```
     * 
     * @receiver The [RegistryKey] of type [I] to register as an implied key with identity mapping.
     */
    public fun RegistryKey<in I>.impliesSame() {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        content[RegistryKeyMapWrapper(this)] = { it }
    }
    
    public companion object {
        private const val NO_CONTENT_MESSAGE = "The implied keys registry builder is already finalized. Apply the operation to the built result."
    }
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.internal.EmptyIterable
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.collections.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Represents a registration entry in a registry, containing a key-value pair.
 * 
 * @param key The [RegistryKey] associated with the value.
 * @param value The value stored in the registry for the given [key].
 * @param T The type of the value.
 */
public data class Registration<T>(
    val key: RegistryKey<T>,
    val value: T,
)

/**
 * Represents a type-safe associative array.
 * 
 * It stores associations like `Key<T> -> T` for arbitrary types `T`, allowing type-safe
 * retrieval of values based on their associated keys.
 */
public interface Registry {
    /**
     * Checks if the [registryKey] is stored in the registry and has an association with a value.
     * 
     * @param registryKey The [RegistryKey] to check for presence in this registry.
     * @return `true` if the [registryKey] is present, `false` otherwise.
     */
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    
    // TODO: Create specific type of errors for the method.
    /**
     * Retrieves the value associated with the specified [registryKey].
     * 
     * @param registryKey The [RegistryKey] for which to retrieve the value.
     * @return The value of type [T] associated with the [registryKey].
     * @throws IllegalArgumentException if the [registryKey] is not present in this registry.
     */
    public operator fun <T> get(registryKey: RegistryKey<out T>): T
    
    /**
     * Returns an iterable of all key-value pairs registered in this registry.
     * 
     * @return An [Iterable] containing all [Registration] entries in this registry.
     */
    public fun asRegistrationIterable(): Iterable<Registration<*>>
    
    public companion object;
    
    public object Empty : Registry {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> get(registryKey: RegistryKey<out T>): T {
            TODO("Not yet implemented")
        }
        override fun asRegistrationIterable(): Iterable<Registration<*>> = EmptyIterable
        override fun toString(): String = "{}"
    }
}

/**
 * Retrieves value by the specified [registryKey] or returns `null` if no association is present.
 * 
 * @receiver The [Registry] in which to look up the [registryKey].
 * @param T The type of the value associated with the [registryKey].
 * @param registryKey The [RegistryKey] to look up in this registry.
 * @return The value associated with the [registryKey], or `null` if the [registryKey] is not present.
 */
public fun <T> Registry.getOrNull(registryKey: RegistryKey<T>): T? = if (registryKey in this) this[registryKey] else null
/**
 * Retrieves value wrapped in [Some] by the specified [registryKey] or returns [None] if no association is present.
 * 
 * @receiver The [Registry] in which to look up the [registryKey].
 * @param T The type of the value associated with the [registryKey].
 * @param registryKey The [RegistryKey] to look up in this registry.
 * @return A [Some] containing the value if present, or [None] if the [registryKey] is not found.
 */
public fun <T> Registry.getMaybe(registryKey: RegistryKey<T>): Maybe<T> = if (registryKey in this) Some(this[registryKey]) else None
/**
 * Retrieves value by the specified [registryKey] or returns the [default] value if no association is present.
 * 
 * @receiver The [Registry] in which to look up the [registryKey].
 * @param T The type of the value associated with the [registryKey].
 * @param registryKey The [RegistryKey] to look up in this registry.
 * @param default The default value to return if the [registryKey] is not found.
 * @return The value associated with the [registryKey], or the [default] value if the [registryKey] is not present.
 */
public fun <T> Registry.getOrDefault(registryKey: RegistryKey<T>, default: T): T = if (registryKey in this) this[registryKey] else default

/**
 * Retrieves value by the specified [registryKey] or computes the [block] and returns its value if no association is present.
 * 
 * @receiver The [Registry] in which to look up the [registryKey].
 * @param T The type of the value associated with the [registryKey].
 * @param registryKey The [RegistryKey] to look up in this registry.
 * @param block A lambda that will be invoked to compute a default value if the [registryKey] is not found.
 * @return The value associated with the [registryKey], or the result of [block] if the [registryKey] is not present.
 */
public inline fun <T> Registry.getOrElse(registryKey: RegistryKey<out T>, block: () -> T): T = if (registryKey in this) this[registryKey] else block()

/**
 * A mutable registry that allows adding, updating, and removing key-value associations.
 */
public interface MutableRegistry : Registry {
    /**
     * Associates the specified [value] with the specified [registryKey] in this registry.
     * 
     * @param registryKey The [RegistryKey] to associate with the [value].
     * @param value The value of type [T] to associate with the [registryKey].
     */
    public operator fun <T> set(registryKey: RegistryKey<in T>, value: T)
    
    /**
     * Copies all associations from the [from] registry into this registry.
     * 
     * @param from The [Registry] to copy associations from.
     */
    public fun setFrom(from: Registry)
    
    /**
     * Removes the association for the specified [registryKey] from this registry.
     * 
     * @param registryKey The [RegistryKey] to remove from this registry.
     */
    public fun remove(registryKey: RegistryKey<*>)
}

/**
 * DSL function to associate this registry key with a value in the context registry.
 * 
 * @receiver The [RegistryKey] to associate with the [value].
 * @param registry The [MutableRegistry] context in which this function is called.
 * @param T The type of the value that this registry key accepts.
 * @param value The value to associate with this key.
 */
context(registry: MutableRegistry)
public infix fun <T> RegistryKey<in T>.correspondsTo(value: T) {
    registry[this] = value
}

@Suppress("UNCHECKED_CAST")
private class MutableRegistryImpl(private val content: MutableMap<RegistryKeyMapWrapper<*>, Any?>) : MutableRegistry {
    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
        RegistryKeyMapWrapper(registryKey) in content
    override fun <T> get(registryKey: RegistryKey<out T>): T =
        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as T else throw IllegalArgumentException("$registryKey is absent.")
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun setFrom(from: Registry) {
        for ((val registryKey = key, val value) in from.asRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun remove(registryKey: RegistryKey<*>) {
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun asRegistrationIterable(): Iterable<Registration<*>> =
        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }
    
    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
}

/**
 * Creates a new empty mutable registry.
 * 
 * @return A new instance of [MutableRegistry].
 */
public fun MutableRegistry(): MutableRegistry = MutableRegistryImpl(mutableMapOf())

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class RegistryBuilder : MutableRegistry {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, Any?>? = mutableMapOf()
    
    override fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override fun <T> get(registryKey: RegistryKey<out T>): T {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        if (RegistryKeyMapWrapper(registryKey) !in content)  throw IllegalArgumentException("$registryKey is absent.")
        return content[RegistryKeyMapWrapper(registryKey)] as T
    }
    
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun setFrom(from: Registry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        for ((val registryKey = key, val value) in from.asRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = value
    }
    
    override fun remove(registryKey: RegistryKey<*>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun asRegistrationIterable(): Iterable<Registration<*>> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }
    }
    
    @PublishedApi
    internal fun build(): Registry {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        this.content = null
        val result = MutableRegistryImpl(content)
        return result
    }
    
    override fun toString(): String {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }
}

/**
 * Builder function for [Registry].
 * 
 * Creates a new [Registry] using the builder pattern. The [block] lambda receives a [MutableRegistry]
 * that can be configured with the desired key-value associations.
 * 
 * @param block A lambda that configures the registry by adding key-value associations.
 * @return A new [Registry] configured according to the [block].
 */
public inline fun Registry.Companion.build(block: MutableRegistry.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder().apply(block).build()
}
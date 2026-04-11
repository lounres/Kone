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


public data class Registration<T>(
    val key: RegistryKey<T>,
    val value: T,
)

/**
 * Represents a type-safe associative array.
 * It means that it stores association like `Key<T> -> T` for arbitrary types `T`.
 */
public interface Registry {
    /**
     * Checks if the [registryKey] is stored in the registry and have association with anything.
     */
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    
    public operator fun <T> get(registryKey: RegistryKey<out T>): T
    
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
 * Retrieves value by this [registryKey] or returns `null` if no association is present.
 */
public fun <T> Registry.getOrNull(registryKey: RegistryKey<T>): T? = if (registryKey in this) this[registryKey] else null
/**
 * Retrieves value by this [registryKey] or returns `null` if no association is present.
 */
public fun <T> Registry.getMaybe(registryKey: RegistryKey<T>): Maybe<T> = if (registryKey in this) Some(this[registryKey]) else None
/**
 * Retrieves value by this [registryKey] or returns [default] value if no association is present.
 */
public fun <T> Registry.getOrDefault(registryKey: RegistryKey<T>, default: T): T = if (registryKey in this) this[registryKey] else default

/**
 * Retrieves value by this [registryKey] or computes [block] and returns its value if no association is present.
 */
public inline fun <T> Registry.getOrElse(registryKey: RegistryKey<out T>, block: () -> T): T = if (registryKey in this) this[registryKey] else block()

public interface MutableRegistry : Registry {
    public operator fun <T> set(registryKey: RegistryKey<in T>, value: T)
    
    public fun setFrom(from: Registry)
    
    public fun remove(registryKey: RegistryKey<*>)
}

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
        val result = MutableRegistryImpl(content)
        this.content = null
        return result
    }
    
    override fun toString(): String {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }
}

/**
 * Builder function for [ProviderRegistry].
 */
public inline fun Registry.Companion.build(block: MutableRegistry.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder().apply(block).build()
}
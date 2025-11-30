/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Describes how [RegistryKey]s are equated and stored in [Registry].
 */
public interface RegistryKeyContext {
    /**
     * Checks equality between the [left] and the [right] keys.
     *
     * This equality is used by [Registry] to decide how the keys are stored and what value to retrieve by the key.
     */
    public fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean
    
    /**
     * Defines hash code of the key.
     *
     * This hash code is used by [Registry] to decide how the keys are stored and what value to retrieve by the key.
     */
    public fun hashCodeOf(key: RegistryKey<*>): Int
}

/**
 * Naive implementation of the [RegistryKeyContext].
 */
// TODO: Review `NaiveRegistryKeyContext` and all `RegistryKey` implementations
public object NaiveRegistryKeyContext : RegistryKeyContext {
    override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean = left::class == right::class && left == right
    override fun hashCodeOf(key: RegistryKey<*>): Int = key.hashCode()
}

/**
 * A key that is used to retrieve a value of type [T] from [Registry].
 */
public interface RegistryKey<T> {
    /**
     * Key context that describes equality between this key and the others.
     */
    public val context: RegistryKeyContext get() = NaiveRegistryKeyContext
    public val superkeys: List<RegistryKey<in T>> get() = emptyList()
}

/**
 * A wrapper of [RegistryKey] that can be used in Kotlin stdlib's maps.
 */
// TODO: Implement custom map for registry keys and remove `RegistryKeyMapWrapper`.
public class RegistryKeyMapWrapper<T> internal constructor(public val key: RegistryKey<T>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistryKeyMapWrapper<*>) return false
        val thisEquality = this.key.context
        val otherEquality = other.key.context
        if (thisEquality !== otherEquality) return false
        
        return thisEquality.checkEqualityOf(this.key, other.key)
    }
    override fun hashCode(): Int = key.context.hashCodeOf(key)
    override fun toString(): String = key.toString()
}

/**
 * Represents a type-safe associative array.
 * It means that it stores association like `Key<T> -> T` for arbitrary types `T`.
 */
public interface Registry {
    /**
     * Checks if the [registryKey] is stored in the registry and have association with anything.
     */
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    
    /**
     * Retrieves value by this [registryKey] or throws exception if no association is present.
     */
    // TODO: Define exception that is thrown by this method
    public operator fun <T> get(registryKey: RegistryKey<out T>): T
    
    /**
     * Represents this registry as Kotlin stdlib's map.
     */
    public fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?>
    
    public companion object;
    
    public object Empty : Registry {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> get(registryKey: RegistryKey<out T>): T {
            TODO("Not yet implemented")
        }
        override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> = emptyMap()
    }
}

/**
 * Retrieves value by this [registryKey] or returns `null` if no association is present.
 */
public fun <T> Registry.getOrNull(registryKey: RegistryKey<T>): T? =
    if (contains(registryKey)) get(registryKey) else null

/**
 * Retrieves value by this [registryKey] or returns [default] value if no association is present.
 */
public fun <T> Registry.getOrDefault(registryKey: RegistryKey<T>, default: T): T =
    if (contains(registryKey)) get(registryKey) else default

/**
 * Retrieves value by this [registryKey] or computes [block] and returns its value if no association is present.
 */
public inline fun <T> Registry.getOrElse(registryKey: RegistryKey<T>, block: () -> T): T =
    if (contains(registryKey)) get(registryKey) else block()

public interface MutableRegistry : Registry {
    public operator fun <T> set(registryKey: RegistryKey<in T>, value: T)
    
    public fun setFrom(from: Registry)
    
    public fun remove(registryKey: RegistryKey<*>)
}

context(registry: MutableRegistry)
public infix fun <T> RegistryKey<in T>.correspondsTo(value: T) {
    registry[this] = value
}

context(_: MutableRegistry)
public val <T> RegistryKey<in T>.withSuperkeys: Set<RegistryKeyMapWrapper<in T>>
    get() = buildSet {
        val keysToCheck = mutableSetOf(RegistryKeyMapWrapper(this@withSuperkeys))
        while (keysToCheck.isNotEmpty()) {
            val nextKey = keysToCheck.first()
            keysToCheck.remove(nextKey)
            add(nextKey)
            for (newKey in nextKey.key.superkeys) {
                val newKeyWrapper = RegistryKeyMapWrapper(newKey)
                if (newKeyWrapper !in this) keysToCheck.add(newKeyWrapper)
            }
        }
    }

public operator fun <T> MutableRegistry.set(registryKeys: Set<RegistryKeyMapWrapper<in T>>, value: T) {
    for (key in registryKeys) set(key.key, value)
}

context(_: MutableRegistry)
public infix fun <T> Set<RegistryKeyMapWrapper<in T>>.correspondsTo(value: T) {
    for (key in this) key.key correspondsTo value
}

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class MutableRegistryImpl(private val content: MutableMap<RegistryKeyMapWrapper<*>, Any?>) : MutableRegistry {
    override operator fun contains(registryKey: RegistryKey<*>): Boolean = RegistryKeyMapWrapper(registryKey) in content
    override operator fun <T> get(registryKey: RegistryKey<out T>): T =  content[RegistryKeyMapWrapper(registryKey)] as T
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun setFrom(from: Registry) {
        content.putAll(from.toMap())
    }
    override fun remove(registryKey: RegistryKey<*>) {
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> = content
}

public fun MutableRegistry(): MutableRegistry = MutableRegistryImpl(mutableMapOf())

/**
 * Mutable version of [Registry] that is used by builder function.
 */
@Suppress("UNCHECKED_CAST")
public class RegistryBuilder<Owner> @PublishedApi internal constructor() : MutableRegistry {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, Any?>? = mutableMapOf()
    
    override operator fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override operator fun <T> get(registryKey: RegistryKey<out T>): T {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content[RegistryKeyMapWrapper(registryKey)] as T
    }
    
    /**
     * Associates provided [registryKey] with provided [value] overriding existing association of the [registryKey].
     */
    override operator fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    
    /**
     * Copies associations from the [from] registry overriding existing ones if needed.
     */
    override fun setFrom(from: Registry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.putAll(from.toMap())
    }
    
    override fun remove(registryKey: RegistryKey<*>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.toMap()
    }
    
    @PublishedApi
    internal fun build(): Registry {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        val result = MutableRegistryImpl(content)
        this.content = null
        return result
    }
}

/**
 * Builder function for [Registry].
 */
public inline fun <Owner> Registry.Companion.build(@BuilderInference block: RegistryBuilder<Owner>.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder<Owner>().apply(block).build()
}
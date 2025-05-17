/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.suppliedTypes.SuppliedType
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
    override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean = left::class == right::class /*&& left == right*/
    override fun hashCodeOf(key: RegistryKey<*>): Int = 0 /*key.hashCode()*/
}

/**
 * A key that is used to retrieve a value of type [T] from [Registry].
 */
public interface RegistryKey<T> {
    /**
     * Type supplier that describes type argument [T] to distinguish similar keys of different type argument.
     */
    public val typeKey: SuppliedType.Regular
    
    /**
     * Key context that describes equality between this key and the others.
     */
    public val context: RegistryKeyContext get() = NaiveRegistryKeyContext
}

/**
 * A wrapper of [RegistryKey] that can be used in Kotlin stdlib's maps.
 */
// TODO: Implement custom map for registry keys and remove `RegistryKeyMapWrapper`.
public class RegistryKeyMapWrapper<T> internal constructor(public val key: RegistryKey<T>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistryKeyMapWrapper<*>) return false
        if (this.key.typeKey != other.key.typeKey) return false
        val thisEquality = this.key.context
        val otherEquality = other.key.context
        if (thisEquality !== otherEquality) return false
        
        return thisEquality.checkEqualityOf(this.key, other.key)
    }
    override fun hashCode(): Int = key.typeKey.hashCode() * 31 + key.context.hashCodeOf(key)
}

/**
 * Represents a type-safe associative array.
 * It means that it stores association like `Key<T> -> T` for arbitrary types `T`.
 */
public interface Registry {
    /**
     * Checks if the [registryKey] is stored in the registry and have association with anything.
     */
    public operator fun <T> contains(registryKey: RegistryKey<T>): Boolean
    
    /**
     * Retrieves value by this [registryKey] or throws exception if no association is present.
     */
    // TODO: Define exception that is thrown by this method
    public operator fun <T> get(registryKey: RegistryKey<T>): T
    
    /**
     * Represents this registry as Kotlin stdlib's map.
     */
    public fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?>
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

/**
 * Builder function for [Registry].
 */
public inline fun Registry(block: RegistryBuilder.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder().apply(block).build()
}

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class RegistryImpl(private val content: Map<RegistryKeyMapWrapper<*>, Any?>) : Registry {
    override operator fun <T> contains(registryKey: RegistryKey<T>): Boolean = RegistryKeyMapWrapper(registryKey) in content
    override operator fun <T> get(registryKey: RegistryKey<T>): T =  content[RegistryKeyMapWrapper(registryKey)] as T
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> = content
}

/**
 * Mutable version of [Registry] that is used by builder function.
 */
@Suppress("UNCHECKED_CAST")
public class RegistryBuilder @PublishedApi internal constructor() : Registry {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, Any?>? = mutableMapOf()
    
    override operator fun <T> contains(registryKey: RegistryKey<T>): Boolean {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override operator fun <T> get(registryKey: RegistryKey<T>): T {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content[RegistryKeyMapWrapper(registryKey)] as T
    }
    
    /**
     * Associates provided [registryKey] with provided [value] overriding existing association of the [registryKey].
     */
    public operator fun <T> set(registryKey: RegistryKey<T>, value: T) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    
    /**
     * Associates [this] registry key with provided [value] overriding existing association of [this] registry key.
     */
    public infix fun <T> RegistryKey<T>.correspondsTo(value: T) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(this)] = value
    }
    
    /**
     * Copies associations from the [from] registry overriding existing ones if needed.
     */
    public fun setFrom(from: Registry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.putAll(from.toMap())
    }
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.toMap()
    }
    
    @PublishedApi
    internal fun build(): Registry {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        val result = RegistryImpl(content)
        this.content = null
        return result
    }
}


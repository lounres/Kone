/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface RegistryKeyContext {
    public fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean
    public fun hashCodeOf(any: RegistryKey<*>): Int
}

// TODO: Review `NaiveRegistryKeyContext` and all `RegistryKey` implementations
public object NaiveRegistryKeyContext : RegistryKeyContext {
    override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean = left::class == right::class /*&& left == right*/
    override fun hashCodeOf(key: RegistryKey<*>): Int = 0 /*key.hashCode()*/
}

public interface RegistryKey<T> {
    public val typeKey: SuppliedType.Regular<T>
    public val context: RegistryKeyContext get() = NaiveRegistryKeyContext
}

// TODO: Implement custom map for registry keys and remove `RegistryKeyMapWrapper`.
public class RegistryKeyMapWrapper<T> /*internal*/ constructor(public val key: RegistryKey<T>) {
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

public interface Registry {
    public operator fun <T> contains(registryKey: RegistryKey<T>): Boolean
    public operator fun <T> get(registryKey: RegistryKey<T>): T
    
    public fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?>
}

public fun <T> Registry.getOrNull(registryKey: RegistryKey<T>): T? =
    if (contains(registryKey)) get(registryKey) else null

public fun <T> Registry.getOrDefault(registryKey: RegistryKey<T>, default: T): T =
    if (contains(registryKey)) get(registryKey) else default

public inline fun <T> Registry.getOrElse(registryKey: RegistryKey<T>, block: () -> T): T =
    if (contains(registryKey)) get(registryKey) else block()

public inline fun Registry(block: RegistryBuilder.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryImpl(RegistryBuilder().apply(block).toMap())
}

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class RegistryImpl(private val content: Map<RegistryKeyMapWrapper<*>, Any?>) : Registry {
    override operator fun <T> contains(registryKey: RegistryKey<T>): Boolean = RegistryKeyMapWrapper(registryKey) in content
    override operator fun <T> get(registryKey: RegistryKey<T>): T =  content[RegistryKeyMapWrapper(registryKey)] as T
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> = content
}

@Suppress("UNCHECKED_CAST")
public class RegistryBuilder @PublishedApi internal constructor() : Registry {
    private val content: MutableMap<RegistryKeyMapWrapper<*>, Any?> = mutableMapOf()
    
    override operator fun <T> contains(registryKey: RegistryKey<T>): Boolean = RegistryKeyMapWrapper(registryKey) in content
    override operator fun <T> get(registryKey: RegistryKey<T>): T =  content[RegistryKeyMapWrapper(registryKey)] as T
    
    public operator fun <T> set(registryKey: RegistryKey<T>, value: T) {
        content[RegistryKeyMapWrapper(registryKey)] = value
    }
    public infix fun <T> RegistryKey<T>.correspondsTo(value: T) {
        content[RegistryKeyMapWrapper(this)] = value
    }
    public fun setFrom(from: Registry) {
        content.putAll(from.toMap())
    }
    
    override fun toMap(): Map<RegistryKeyMapWrapper<*>, Any?> = content.toMap()
}


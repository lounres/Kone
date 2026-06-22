/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.ifSome
import dev.lounres.kone.registry.internal.EmptyIterable
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.collections.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public fun interface RegisteredValueProvider<out T> {
    public fun get(): T
    
    public companion object;
}

public fun <T> RegisteredValueProvider.Companion.cached(provider: RegisteredValueProvider<T>): RegisteredValueProvider<T> =
    object : SynchronizedObject(), RegisteredValueProvider<T> {
        private var result: Maybe<T> = None
        override fun get(): T {
            result.ifSome { return it }
            
            synchronized(this) {
                result.ifSome { return it }
                return provider.get().also { result = Some(it) }
            }
        }
    }

public data class ProvidingRegistration<T>(
    val key: RegistryKey<T>,
    val provider: RegisteredValueProvider<T>,
)

/**
 * Represents a type-safe associative array.
 * It means that it stores association like `Key<T> -> T` for arbitrary types `T`.
 */
public interface ProviderRegistry : Registry {
    public fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>
    
    public fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>>
    
    public companion object;
    
    public object Empty : ProviderRegistry {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> {
            TODO("Not yet implemented")
        }
        override fun <T> get(registryKey: RegistryKey<out T>): T {
            TODO("Not yet implemented")
        }
        override fun asRegistrationIterable(): Iterable<Registration<*>> = EmptyIterable
        override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> = EmptyIterable
        override fun toString(): String = "{}"
    }
}

public fun <T> ProviderRegistry.provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? =
    if (registryKey in this) this.provide(registryKey) else null

public interface MutableProviderRegistry : ProviderRegistry, MutableRegistry {
    public operator fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>)
    
    public fun setFrom(from: ProviderRegistry)
}

context(registry: MutableProviderRegistry)
public infix fun <T> RegistryKey<in T>.correspondsTo(provider: RegisteredValueProvider<T>) {
    registry[this] = provider
}

@Suppress("UNCHECKED_CAST")
private class MutableProviderRegistryImpl(private val content: MutableMap<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>>) : MutableProviderRegistry {
    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
        RegistryKeyMapWrapper(registryKey) in content
    override fun <T> get(registryKey: RegistryKey<out T>): T = provide(registryKey).get()
    override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> =
        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T> else throw IllegalArgumentException("$registryKey is absent.")
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        set(registryKey) { value }
    }
    override fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>) {
        content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    override fun setFrom(from: Registry) {
        for ((val registryKey = key, val value) in from.asRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = RegisteredValueProvider { value }
    }
    override fun setFrom(from: ProviderRegistry) {
        for ((val registryKey = key, val provider) in from.asProvidingRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    override fun remove(registryKey: RegistryKey<*>) {
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun asRegistrationIterable(): Iterable<Registration<*>> =
        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value.get()) }
    override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> =
        content.entries.map { ProvidingRegistration(it.key.key as RegistryKey<Any?>, it.value) }
    
    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
}

public fun MutableProviderRegistry(): MutableProviderRegistry = MutableProviderRegistryImpl(mutableMapOf())

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class ProviderRegistryBuilder : MutableProviderRegistry {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>>? = mutableMapOf()
    
    override fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override fun <T> get(registryKey: RegistryKey<out T>): T = provide(registryKey).get()
    override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        if (RegistryKeyMapWrapper(registryKey) !in content)  throw IllegalArgumentException("$registryKey is absent.")
        return content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T>
    }
    
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) {
        set(registryKey) { value }
    }
    override fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    override fun setFrom(from: Registry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        for ((val registryKey = key, val value) in from.asRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = RegisteredValueProvider { value }
    }
    override fun setFrom(from: ProviderRegistry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        for ((val registryKey = key, val provider) in from.asProvidingRegistrationIterable()) content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    
    override fun remove(registryKey: RegistryKey<*>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun asRegistrationIterable(): Iterable<Registration<*>> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value.get()) }
    }
    override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.entries.map { ProvidingRegistration(it.key.key as RegistryKey<Any?>, it.value) }
    }
    
    @PublishedApi
    internal fun build(): ProviderRegistry {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        this.content = null
        val result = MutableProviderRegistryImpl(content)
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
public inline fun ProviderRegistry.Companion.build(block: MutableProviderRegistry.() -> Unit): ProviderRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return ProviderRegistryBuilder().apply(block).build()
}
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


/**
 * A provider that can compute or supply a value of type [T].
 * 
 * Providers are used in [ProviderRegistry] to enable lazy computation of values.
 * Implementations can cache results, compute values on demand, or use any other strategy
 * for providing values.
 * 
 * @param T The type of value provided by this provider.
 */
public fun interface RegisteredValueProvider<out T> {
    /**
     * Gets the value provided by this provider.
     * 
     * @return The provided value of type [T].
     */
    public fun get(): T
    
    public companion object;
}

/**
 * Creates a cached provider that memorizes the result of the original provider.
 * 
 * The returned provider will compute the value from the original provider on the first call,
 * and then cache and return the same value for all subsequent calls. This is thread-safe.
 * 
 * @receiver The [RegisteredValueProvider] companion object.
 * @param T The type of value provided by the provider.
 * @param provider The provider whose result should be cached.
 * @return A new [RegisteredValueProvider] that caches the result.
 */
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

/**
 * Represents a registration entry in a provider registry, containing a key-provider pair.
 * 
 * @param key The [RegistryKey] associated with the provider.
 * @param provider The [RegisteredValueProvider] that computes the value for the given [key].
 * @param T The type of the value provided by the provider.
 */
public data class ProvidingRegistration<T>(
    val key: RegistryKey<T>,
    val provider: RegisteredValueProvider<T>,
)

/**
 * Represents a type-safe associative array that stores associations like `Key<T> -> Provider<T>`.
 * 
 * Provider registries extend [Registry] by allowing values to be computed lazily through providers.
 * This enables deferred computation, caching, and other advanced value provision strategies.
 */
public interface ProviderRegistry : Registry {
    /**
     * Gets the provider associated with the specified [registryKey].
     * 
     * @param registryKey The [RegistryKey] to get the provider for.
     * @return The [RegisteredValueProvider] associated with the [registryKey].
     * @throws IllegalArgumentException if the [registryKey] is not present in this registry.
     */
    public fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>
    
    /**
     * Returns an iterable of all key-provider pairs in this registry.
     * 
     * @return An [Iterable] containing all [ProvidingRegistration] entries.
     */
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

/**
 * Gets the provider associated with the specified [registryKey], or returns `null` if no association is present.
 * 
 * @receiver The [ProviderRegistry] in which to look up the [registryKey].
 * @param T The type of value provided by the provider.
 * @param registryKey The [RegistryKey] to get the provider for.
 * @return The [RegisteredValueProvider] associated with the [registryKey], or `null` if the [registryKey] is not present.
 */
public fun <T> ProviderRegistry.provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? =
    if (registryKey in this) this.provide(registryKey) else null

/**
 * A mutable provider registry that allows adding, updating, and removing key-provider associations.
 */
public interface MutableProviderRegistry : ProviderRegistry, MutableRegistry {
    /**
     * Associates the specified [provider] with the specified [registryKey] in this registry.
     * 
     * @param registryKey The [RegistryKey] to associate with the [provider].
     * @param provider The [RegisteredValueProvider] to associate with the [registryKey].
     */
    public operator fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>)
    
    /**
     * Copies all key-provider associations from the [from] provider registry into this registry.
     * 
     * @param from The [ProviderRegistry] to copy associations from.
     */
    public fun setFrom(from: ProviderRegistry)
}

/**
 * DSL function to associate this registry key with a provider in the context registry.
 * 
 * @receiver The [RegistryKey] to associate with the [provider].
 * @param registry The [MutableProviderRegistry] context in which this function is called.
 * @param T The type of value that this registry key accepts.
 * @param provider The [RegisteredValueProvider] to associate with this key.
 */
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

/**
 * Creates a new empty mutable provider registry.
 * 
 * @return A new instance of [MutableProviderRegistry].
 */
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
 * 
 * Creates a new [ProviderRegistry] using the builder pattern. The [block] lambda receives a [MutableProviderRegistry]
 * that can be configured with the desired key-provider associations.
 * 
 * @param block A lambda that configures the registry by adding key-provider associations.
 * @return A new [ProviderRegistry] configured according to the [block].
 */
public inline fun ProviderRegistry.Companion.build(block: MutableProviderRegistry.() -> Unit): ProviderRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return ProviderRegistryBuilder().apply(block).build()
}
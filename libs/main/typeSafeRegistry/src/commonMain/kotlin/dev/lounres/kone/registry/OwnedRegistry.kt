/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.EmptyIterable
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.collections.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * A type-safe associative array with an associated owner type.
 * 
 * Extends [Registry] to add an owner type parameter [Owner], enabling type-safe distinction
 * between registries intended for different owners or contexts.
 * 
 * @param Owner The type representing the owner or context of this registry.
 */
public interface OwnedRegistry<Owner> : Registry {
    public companion object
}

private data object EmptyOwnedRegistry : OwnedRegistry<Any?> {
    override fun contains(registryKey: RegistryKey<*>): Boolean = false
    override fun <T> get(registryKey: RegistryKey<out T>): T {
        TODO("Not yet implemented")
    }
    override fun asRegistrationIterable(): Iterable<Registration<*>> = EmptyIterable
    override fun toString(): String = "{}"
}

/**
 * Creates an empty [OwnedRegistry] for the specified owner type.
 * 
 * @receiver The [OwnedRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned registry.
 * @return An empty [OwnedRegistry] instance for the specified [Owner] type.
 */
public fun <Owner> OwnedRegistry.Companion.empty(): OwnedRegistry<Owner> = EmptyOwnedRegistry as OwnedRegistry<Owner>

/**
 * A wrapper that adapts a non-owned [Registry] to an [OwnedRegistry] with a specific owner type.
 * 
 * Delegates all operations to the underlying [registry] while providing the [OwnedRegistry] interface
 * with the specified [Owner] type.
 * 
 * @param Owner The type representing the owner or context of this wrapper.
 */
public interface RegistryWrapper<Owner> : OwnedRegistry<Owner> {
    /**
     * The underlying [Registry] that this wrapper delegates to.
     */
    public val registry: Registry
    
    override fun contains(registryKey: RegistryKey<*>): Boolean = registryKey in registry
    override fun <T> get(registryKey: RegistryKey<out T>): T = registry[registryKey]
    override fun asRegistrationIterable(): Iterable<Registration<*>> = registry.asRegistrationIterable()
}

/**
 * Creates a [RegistryWrapper] for the specified owner type, wrapping the given [Registry].
 * 
 * Unwraps any existing [RegistryWrapper] instances to get to the underlying non-wrapper registry
 * before creating the new wrapper.
 * 
 * @receiver The [OwnedRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned wrapper.
 * @param registry The [Registry] to wrap.
 * @return A [RegistryWrapper] for the specified [Owner] type, wrapping the unwrapped [registry].
 */
public fun <Owner> OwnedRegistry.Companion.wrapFor(registry: Registry): OwnedRegistry<Owner> {
    var registry = registry
    while (registry is RegistryWrapper<*>) {
        registry = registry.registry
    }
    return object : RegistryWrapper<Owner> {
        override val registry: Registry = registry
    }
}

/**
 * A mutable owned registry that allows adding, updating, and removing key-value associations.
 * 
 * Extends both [OwnedRegistry] and [MutableRegistry], combining ownership typing with
 * mutability for dynamic registry management.
 * 
 * @param Owner The type representing the owner or context of this mutable registry.
 */
public interface MutableOwnedRegistry<Owner> : OwnedRegistry<Owner>, MutableRegistry {
    public companion object
}

private class MutableOwnedRegistryImpl<Owner>(private val content: MutableMap<RegistryKeyMapWrapper<*>, Any?>) : MutableOwnedRegistry<Owner> {
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
 * Creates a new empty mutable owned registry for the specified owner type.
 * 
 * @param Owner The type representing the owner or context of the returned registry.
 * @return A new empty [MutableOwnedRegistry] instance for the specified [Owner] type.
 */
public fun <Owner> MutableOwnedRegistry(): MutableOwnedRegistry<Owner> = MutableOwnedRegistryImpl(mutableMapOf())

/**
 * A mutable wrapper that adapts a [MutableRegistry] to a [MutableOwnedRegistry] with a specific owner type.
 * 
 * Extends [RegistryWrapper] with [MutableOwnedRegistry] capabilities, delegating mutable operations
 * to the underlying [MutableRegistry].
 * 
 * @param Owner The type representing the owner or context of this mutable wrapper.
 */
public interface MutableRegistryWrapper<Owner> : RegistryWrapper<Owner>, MutableOwnedRegistry<Owner> {
    /**
     * The underlying [MutableRegistry] that this wrapper delegates to.
     */
    override val registry: MutableRegistry
    
    override fun <T> set(registryKey: RegistryKey<in T>, value: T) { registry[registryKey] = value }
    override fun remove(registryKey: RegistryKey<*>) { registry.remove(registryKey) }
    override fun setFrom(from: Registry) { registry.setFrom(from) }
}

/**
 * Creates a [MutableRegistryWrapper] for the specified owner type, wrapping the given [MutableRegistry].
 * 
 * Unwraps any existing [MutableRegistryWrapper] instances to get to the underlying non-wrapper registry
 * before creating the new wrapper.
 * 
 * @receiver The [MutableOwnedRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned wrapper.
 * @param registry The [MutableRegistry] to wrap.
 * @return A [MutableRegistryWrapper] for the specified [Owner] type, wrapping the unwrapped [registry].
 */
public fun <Owner> MutableOwnedRegistry.Companion.wrapFor(registry: MutableRegistry): MutableOwnedRegistry<Owner> {
    var registry = registry
    while (registry is MutableRegistryWrapper<*>) {
        registry = registry.registry
    }
    return object : MutableRegistryWrapper<Owner> {
        override val registry: MutableRegistry = registry
    }
}

@PublishedApi
internal class OwnedRegistryBuilder<Owner> : MutableOwnedRegistry<Owner> {
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
    internal fun build(): OwnedRegistry<Owner> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        this.content = null
        val result = MutableOwnedRegistryImpl<Owner>(content)
        return result
    }
    
    override fun toString(): String {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }
}

/**
 * Builder function for [OwnedRegistry].
 * 
 * Creates a new [OwnedRegistry] using the builder pattern. The [block] lambda receives a [MutableOwnedRegistry]
 * that can be configured with the desired key-value associations for the specified owner type.
 * 
 * @receiver The [OwnedRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned registry.
 * @param block A lambda that configures the registry by adding key-value associations.
 * @return A new [OwnedRegistry] configured according to the [block].
 */
public inline fun <Owner> OwnedRegistry.Companion.build(block: MutableOwnedRegistry<Owner>.() -> Unit): OwnedRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedRegistryBuilder<Owner>().apply(block).build()
}

/**
 * A type-safe associative array with an associated owner type that supports provider-based value retrieval.
 * 
 * Extends both [OwnedRegistry] and [ProviderRegistry], combining ownership typing with
 * provider-based lazy value computation.
 * 
 * @param Owner The type representing the owner or context of this provider registry.
 */
public interface OwnedProviderRegistry<Owner> : OwnedRegistry<Owner>, ProviderRegistry {
    public companion object
}

private object EmptyOwnedProviderRegistry : OwnedProviderRegistry<Any?> {
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

/**
 * Creates an empty [OwnedProviderRegistry] for the specified owner type.
 * 
 * @receiver The [OwnedProviderRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned registry.
 * @return An empty [OwnedProviderRegistry] instance for the specified [Owner] type.
 */
public fun <Owner> OwnedProviderRegistry.Companion.empty(): OwnedProviderRegistry<Owner> = EmptyOwnedProviderRegistry as OwnedProviderRegistry<Owner>

/**
 * A wrapper that adapts a [ProviderRegistry] to an [OwnedProviderRegistry] with a specific owner type.
 * 
 * Extends [RegistryWrapper] with [ProviderRegistry] capabilities, delegating provider operations
 * to the underlying [registry].
 * 
 * @param Owner The type representing the owner or context of this wrapper.
 */
public interface ProviderRegistryWrapper<Owner> : RegistryWrapper<Owner>, OwnedProviderRegistry<Owner> {
    /**
     * The underlying [ProviderRegistry] that this wrapper delegates to.
     */
    override val registry: ProviderRegistry
    
    override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> = registry.provide(registryKey)
    override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> = registry.asProvidingRegistrationIterable()
}

/**
 * Creates a [ProviderRegistryWrapper] for the specified owner type, wrapping the given [ProviderRegistry].
 * 
 * Unwraps any existing [ProviderRegistryWrapper] instances to get to the underlying non-wrapper registry
 * before creating the new wrapper.
 * 
 * @receiver The [OwnedProviderRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned wrapper.
 * @param registry The [ProviderRegistry] to wrap.
 * @return A [ProviderRegistryWrapper] for the specified [Owner] type, wrapping the unwrapped [registry].
 */
public fun <Owner> OwnedProviderRegistry.Companion.wrapFor(registry: ProviderRegistry): OwnedProviderRegistry<Owner> {
    var registry = registry
    while (registry is ProviderRegistryWrapper<*>) {
        registry = registry.registry
    }
    return object : ProviderRegistryWrapper<Owner> {
        override val registry: ProviderRegistry = registry
    }
}

/**
 * A mutable owned registry that supports both direct values and provider-based value retrieval.
 * 
 * Extends [OwnedProviderRegistry], [MutableOwnedRegistry], and [MutableProviderRegistry],
 * combining ownership typing with full mutability and provider support.
 * 
 * @param Owner The type representing the owner or context of this mutable provider registry.
 */
public interface MutableOwnedProviderRegistry<Owner> : OwnedProviderRegistry<Owner>, MutableOwnedRegistry<Owner>, MutableProviderRegistry {
    public companion object
}

@Suppress("UNCHECKED_CAST")
private class MutableOwnedProviderRegistryImpl<Owner>(private val content: MutableMap<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>>) : MutableOwnedProviderRegistry<Owner> {
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
 * Creates a new empty mutable owned provider registry for the specified owner type.
 * 
 * @param Owner The type representing the owner or context of the returned registry.
 * @return A new empty [MutableOwnedProviderRegistry] instance for the specified [Owner] type.
 */
public fun <Owner> MutableOwnedProviderRegistry(): MutableOwnedProviderRegistry<Owner> = MutableOwnedProviderRegistryImpl(mutableMapOf())

/**
 * A mutable wrapper that adapts a [MutableProviderRegistry] to a [MutableOwnedProviderRegistry] with a specific owner type.
 * 
 * Extends [ProviderRegistryWrapper] and [MutableRegistryWrapper] with [MutableOwnedProviderRegistry] capabilities,
 * delegating all operations to the underlying [MutableProviderRegistry].
 * 
 * @param Owner The type representing the owner or context of this mutable wrapper.
 */
public interface MutableProviderRegistryWrapper<Owner> : ProviderRegistryWrapper<Owner>, MutableRegistryWrapper<Owner>, MutableOwnedProviderRegistry<Owner> {
    /**
     * The underlying [MutableProviderRegistry] that this wrapper delegates to.
     */
    override val registry: MutableProviderRegistry
    
    override fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>) { registry[registryKey] = provider }
    override fun setFrom(from: ProviderRegistry) { registry.setFrom(from) }
}

/**
 * Creates a [MutableProviderRegistryWrapper] for the specified owner type, wrapping the given [MutableProviderRegistry].
 * 
 * Unwraps any existing [MutableProviderRegistryWrapper] instances to get to the underlying non-wrapper registry
 * before creating the new wrapper.
 * 
 * @receiver The [MutableOwnedProviderRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned wrapper.
 * @param registry The [MutableProviderRegistry] to wrap.
 * @return A [MutableProviderRegistryWrapper] for the specified [Owner] type, wrapping the unwrapped [registry].
 */
public fun <Owner> MutableOwnedProviderRegistry.Companion.wrapFor(registry: MutableProviderRegistry): MutableOwnedProviderRegistry<Owner> {
    var registry = registry
    while (registry is MutableProviderRegistryWrapper<*>) {
        registry = registry.registry
    }
    return object : MutableProviderRegistryWrapper<Owner> {
        override val registry: MutableProviderRegistry = registry
    }
}

@PublishedApi
internal class OwnedProviderRegistryBuilder<Owner> : MutableOwnedProviderRegistry<Owner> {
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
    internal fun build(): OwnedProviderRegistry<Owner> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        this.content = null
        val result = MutableOwnedProviderRegistryImpl<Owner>(content)
        return result
    }
    
    override fun toString(): String {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
    }
}

/**
 * Builder function for [OwnedProviderRegistry].
 * 
 * Creates a new [OwnedProviderRegistry] using the builder pattern. The [block] lambda receives a [MutableOwnedProviderRegistry]
 * that can be configured with the desired key-provider associations for the specified owner type.
 * 
 * @receiver The [OwnedProviderRegistry.Companion] companion object.
 * @param Owner The type representing the owner or context of the returned registry.
 * @param block A lambda that configures the registry by adding key-provider associations.
 * @return A new [OwnedProviderRegistry] configured according to the [block].
 */
public inline fun <Owner> OwnedProviderRegistry.Companion.build(block: MutableOwnedProviderRegistry<Owner>.() -> Unit): OwnedProviderRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedProviderRegistryBuilder<Owner>().apply(block).build()
}
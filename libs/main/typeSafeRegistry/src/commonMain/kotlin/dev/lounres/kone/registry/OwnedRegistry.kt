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

public fun <Owner> OwnedRegistry.Companion.empty(): OwnedRegistry<Owner> = EmptyOwnedRegistry as OwnedRegistry<Owner>

public interface MutableOwnedRegistry<Owner> : OwnedRegistry<Owner>, MutableRegistry

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

public fun <Owner> MutableOwnedRegistry(): MutableOwnedRegistry<Owner> = MutableOwnedRegistryImpl(mutableMapOf())

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

public inline fun <Owner> OwnedRegistry.Companion.build(block: MutableOwnedRegistry<Owner>.() -> Unit): OwnedRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedRegistryBuilder<Owner>().apply(block).build()
}

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

public fun <Owner> OwnedProviderRegistry.Companion.empty(): OwnedProviderRegistry<Owner> = EmptyOwnedProviderRegistry as OwnedProviderRegistry<Owner>

public interface MutableOwnedProviderRegistry<Owner> : OwnedProviderRegistry<Owner>, MutableOwnedRegistry<Owner>, MutableProviderRegistry

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

public fun <Owner> MutableOwnedProviderRegistry(): MutableOwnedProviderRegistry<Owner> = MutableOwnedProviderRegistryImpl(mutableMapOf())

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

public inline fun <Owner> OwnedProviderRegistry.Companion.build(block: MutableOwnedProviderRegistry<Owner>.() -> Unit): OwnedProviderRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedProviderRegistryBuilder<Owner>().apply(block).build()
}
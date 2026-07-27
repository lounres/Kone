/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.collections.set


/**
 * Represents an implication relationship that can create registries with implied keys.
 * 
 * This interface provides methods to create either a [Registry] or a [ProviderRegistry]
 * that includes both the original key and all its implied keys.
 * 
 * @param T The type of the source value from which implications are derived.
 */
public interface ProviderRegistryImplication<in T> {
    /**
     * Creates a [Registry] with the implied keys, using a concrete value.
     * 
     * @param value The source value of type [T] to compute implied values from.
     * @return A [Registry] containing the original key and all implied keys with their computed values.
     */
    public fun substitute(value: T): Registry
    
    /**
     * Creates a [ProviderRegistry] with the implied keys, using a provider.
     * 
     * @param provider The [RegisteredValueProvider] of type [T] that supplies the source value.
     * @return A [ProviderRegistry] containing the original key and all implied keys with their providers.
     */
    public fun substitute(provider: RegisteredValueProvider<T>): ProviderRegistry
}

/**
 * Provides an implication for this registry key that includes all keys implied by it.
 * 
 * This property creates a [ProviderRegistryImplication] that can be used to generate registries
 * containing both this key and all keys that can be implied from it, based on the
 * [impliedKeys][RegistryKey.impliedKeys] defined in [this] key.
 * 
 * The implication ensures that when this key is present, all implied keys are also included
 * with their values computed from the source value. However, it prohibits cyclic implications
 * together with multiple implications of the same key by throwing an error.
 * 
 * @receiver The registry key for which to create the implication.
 * @param T The type of values that this registry key accepts.
 * @return A [ProviderRegistryImplication] that can generate registries with this key and its implied keys.
 */
public val <T> RegistryKey<in T>.withImplied: ProviderRegistryImplication<T>
    get() {
        data class RegistryKeyInfo(
            val path: List<RegistryKeyMapWrapper<*>>,
            val producer: (T) -> Any?,
        )
        val results = buildMap<RegistryKeyMapWrapper<*>, RegistryKeyInfo> {
            val keysToCheck = mutableMapOf<RegistryKeyMapWrapper<*>, RegistryKeyInfo>(
                RegistryKeyMapWrapper(this@withImplied) to RegistryKeyInfo(emptyList(), { it })
            )
            while (keysToCheck.isNotEmpty()) {
                (val nextKey = key, val nextInfo = value) = keysToCheck.entries.first()
                keysToCheck.remove(nextKey)
                this[nextKey] = nextInfo
                val newPath = nextInfo.path + nextKey
                for ((val newKey = key, val newProducer = mapping) in nextKey.key.impliedKeys) {
                    @Suppress("UNCHECKED_CAST")
                    newProducer as (Any?) -> Any?
                    val newInfo = RegistryKeyInfo(
                        path = newPath,
                        producer = { newProducer(nextInfo.producer(it)) },
                    )
                    if (RegistryKeyMapWrapper(newKey) in newPath) error("Cyclic implications: ${(newPath + RegistryKeyMapWrapper(newKey)).joinToString(separator = " -> ") { it.key.toString() }}")
                    val info = this[RegistryKeyMapWrapper(newKey)] ?: keysToCheck[RegistryKeyMapWrapper(newKey)]
                    if (info != null) {
                        if (info.path.withIndex().any { (val index, val pathKey = value) -> newPath[index] != pathKey })
                            error(
                                "Overload implications for key ${newKey}: " +
                                        "path # 1 is ${(info.path + RegistryKeyMapWrapper(newKey)).joinToString(separator = " -> ") { it.key.toString() }}, " +
                                        "path # 2 is ${(newPath + RegistryKeyMapWrapper(newKey)).joinToString(separator = " -> ") { it.key.toString() }}"
                            )
                    } else {
                        keysToCheck[RegistryKeyMapWrapper(newKey)] = newInfo
                    }
                }
            }
        }
        
        return object : ProviderRegistryImplication<T> {
            override fun substitute(value: T): Registry =
                @Suppress("UNCHECKED_CAST")
                object : Registry {
                    private val content: Map<RegistryKeyMapWrapper<*>, Any?> =
                        results.mapValues { it.value.producer(value) }
                    
                    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                        RegistryKeyMapWrapper(registryKey) in content
                    override fun <T> get(registryKey: RegistryKey<out T>): T =
                        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as T else throw IllegalArgumentException("$registryKey is absent.")
                    
                    override fun asRegistrationIterable(): Iterable<Registration<*>> =
                        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }
                    
                    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
                }
            
            override fun substitute(provider: RegisteredValueProvider<T>): ProviderRegistry =
                @Suppress("UNCHECKED_CAST")
                object : ProviderRegistry {
                    private val content: Map<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>> =
                        results.mapValues { RegisteredValueProvider { it.value.producer(provider.get()) } }
                    
                    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                        RegistryKeyMapWrapper(registryKey) in content
                    override fun <T> get(registryKey: RegistryKey<out T>): T = provide(registryKey).get()
                    override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> =
                        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T> else throw IllegalArgumentException("$registryKey is absent.")
                    
                    override fun asRegistrationIterable(): Iterable<Registration<*>> =
                        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value.get()) }
                    override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> =
                        content.entries.map { ProvidingRegistration(it.key.key as RegistryKey<Any?>, it.value) }
                    
                    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
                }
        }
    }

/**
 * Provides an implication for this registry key that includes all keys implied by it,
 * using the first encountered mapping for each implied key.
 * 
 * This is similar to [withImplied] but processes implied keys in a breadth-first manner,
 * using the first mapping encountered for each key. This allows to ignore multiple implications of the same key.
 * 
 * @receiver The registry key for which to create the implication.
 * @param T The type of values that this registry key accepts.
 * @return A [ProviderRegistryImplication] that can generate registries with this key and its implied keys.
 */
public val <T> RegistryKey<in T>.withImpliedUsingFirst: ProviderRegistryImplication<T>
    get() {
        val results = buildMap<RegistryKeyMapWrapper<*>, (T) -> Any?> {
            data class KeyToProcess(
                val key: RegistryKeyMapWrapper<*>,
                val producer: (T) -> Any?,
            )
            val keysToProcess = ArrayDeque<KeyToProcess>()
            keysToProcess.addLast(
                KeyToProcess(
                    key = RegistryKeyMapWrapper(this@withImpliedUsingFirst),
                    producer = { it },
                )
            )
            while (keysToProcess.isNotEmpty()) {
                (val nextKey = key, val nextProducer = producer) = keysToProcess.removeFirst()
                if (nextKey in this) continue
                this[nextKey] = nextProducer
                for ((val newKey = key, val newProducer = mapping) in nextKey.key.impliedKeys) {
                    @Suppress("UNCHECKED_CAST")
                    newProducer as (Any?) -> Any?
                    keysToProcess.addLast(
                        KeyToProcess(
                            key = RegistryKeyMapWrapper(newKey),
                            producer = { newProducer(nextProducer(it)) },
                        )
                    )
                }
            }
        }
        
        return object : ProviderRegistryImplication<T> {
            override fun substitute(value: T): Registry =
                @Suppress("UNCHECKED_CAST")
                object : Registry {
                    private val content: Map<RegistryKeyMapWrapper<*>, Any?> =
                        results.mapValues { it.value(value) }
                    
                    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                        RegistryKeyMapWrapper(registryKey) in content
                    override fun <T> get(registryKey: RegistryKey<out T>): T =
                        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as T else throw IllegalArgumentException("$registryKey is absent.")
                    
                    override fun asRegistrationIterable(): Iterable<Registration<*>> =
                        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }
                    
                    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
                }
            
            override fun substitute(provider: RegisteredValueProvider<T>): ProviderRegistry =
                @Suppress("UNCHECKED_CAST")
                object : ProviderRegistry {
                    private val content: Map<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>> =
                        results.mapValues { RegisteredValueProvider { it.value(provider.get()) } }
                    
                    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                        RegistryKeyMapWrapper(registryKey) in content
                    override fun <T> get(registryKey: RegistryKey<out T>): T = provide(registryKey).get()
                    override fun <T> provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> =
                        if (RegistryKeyMapWrapper(registryKey) in content) content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T> else throw IllegalArgumentException("$registryKey is absent.")
                    
                    override fun asRegistrationIterable(): Iterable<Registration<*>> =
                        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value.get()) }
                    override fun asProvidingRegistrationIterable(): Iterable<ProvidingRegistration<*>> =
                        content.entries.map { ProvidingRegistration(it.key.key as RegistryKey<Any?>, it.value) }
                    
                    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
                }
        }
    }

/**
 * Sets all key-value pairs from a [ProviderRegistryImplication] into this registry.
 *
 * This copies all associations (both the original key and implied keys) from the implication,
 * using the provided concrete value as the source.
 *
 * @receiver The [MutableRegistry] to set the key-value pairs into.
 * @param T The type of the source value from which implications are derived.
 * @param registryImplication The [ProviderRegistryImplication] containing the keys and mappings to set.
 * @param value The concrete value of type [T] to use as the source.
 */
public operator fun <T> MutableRegistry.set(registryImplication: ProviderRegistryImplication<T>, value: T) {
    setFrom(registryImplication.substitute(value))
}

/**
 * Sets all key-value pairs from a [ProviderRegistryImplication] into this provider registry.
 * 
 * This copies all associations (both the original key and implied keys) from the implication,
 * using the provided provider as the source.
 * 
 * @receiver The [MutableProviderRegistry] to set the key-value pairs into.
 * @param T The type of the source value from which implications are derived.
 * @param registryImplication The [ProviderRegistryImplication] containing the keys and mappings to set.
 * @param provider The [RegisteredValueProvider] of type [T] that supplies the source value.
 */
public operator fun <T> MutableProviderRegistry.set(registryImplication: ProviderRegistryImplication<T>, provider: RegisteredValueProvider<T>) {
    setFrom(registryImplication.substitute(provider))
}

/**
 * DSL function to associate all keys from this implication with a value in the context registry.
 *
 * @receiver The [ProviderRegistryImplication] containing the keys and mappings to set.
 * @param registry The [MutableRegistry] context in which this function is called.
 * @param T The type of the source value from which implications are derived.
 * @param value The concrete value of type [T] to use as the source for computing implied values.
 */
context(registry: MutableRegistry)
public infix fun <T> ProviderRegistryImplication<T>.correspondsTo(value: T) {
    registry.setFrom(this.substitute(value))
}

/**
 * DSL function to associate all keys from this implication with a provider in the context provider registry.
 * 
 * @receiver The [ProviderRegistryImplication] containing the keys and mappings to set.
 * @param registry The [MutableProviderRegistry] context in which this function is called.
 * @param T The type of the source value from which implications are derived.
 * @param provider The [RegisteredValueProvider] of type [T] to use as the source for computing implied values.
 */
context(registry: MutableProviderRegistry)
public infix fun <T> ProviderRegistryImplication<T>.correspondsTo(provider: RegisteredValueProvider<T>) {
    registry.setFrom(this.substitute(provider))
}
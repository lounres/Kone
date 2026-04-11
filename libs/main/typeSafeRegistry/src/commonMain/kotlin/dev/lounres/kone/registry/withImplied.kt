/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.collections.set


public interface ProviderRegistryImplication<in T> {
    public fun substitute(value: T): Registry
    public fun substitute(provider: RegisteredValueProvider<T>): ProviderRegistry
}

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

public operator fun <T> MutableProviderRegistry.set(registryImplication: ProviderRegistryImplication<T>, provider: RegisteredValueProvider<T>) {
    setFrom(registryImplication.substitute(provider))
}

public operator fun <T> MutableRegistry.set(registryImplication: ProviderRegistryImplication<T>, value: T) {
    setFrom(registryImplication.substitute(value))
}

context(registry: MutableProviderRegistry)
public infix fun <T> ProviderRegistryImplication<T>.correspondsTo(provider: RegisteredValueProvider<T>) {
    registry.setFrom(this.substitute(provider))
}

context(registry: MutableRegistry)
public infix fun <T> ProviderRegistryImplication<T>.correspondsTo(value: T) {
    registry.setFrom(this.substitute(value))
}
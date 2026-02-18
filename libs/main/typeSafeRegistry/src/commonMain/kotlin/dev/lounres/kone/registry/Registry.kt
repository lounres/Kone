/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.ifSome
import dev.lounres.kone.registry.internal.EmptyIterator
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.plus
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

public data class Registration<T>(
    val key: RegistryKey<T>,
    val value: RegisteredValueProvider<T>,
)

/**
 * Represents a type-safe associative array.
 * It means that it stores association like `Key<T> -> T` for arbitrary types `T`.
 */
public interface Registry : Iterable<Registration<*>> {
    /**
     * Checks if the [registryKey] is stored in the registry and have association with anything.
     */
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    
    public fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>?
    
    public companion object;
    
    public object Empty : Registry {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> {
            TODO("Not yet implemented")
        }
        override fun iterator(): Iterator<Registration<*>> = EmptyIterator
        override fun toString(): String = "{}"
    }
}

public fun <T> Registry.provide(registryKey: RegistryKey<out T>): RegisteredValueProvider<T> =
    provideOrNull(registryKey) ?: throw IllegalArgumentException("Cannot provide registry key $registryKey as it is absent.")

/**
 * Retrieves value by this [registryKey] or throws exception if no association is present.
 */
// TODO: Define exception that is thrown by this method
public operator fun <T> Registry.get(registryKey: RegistryKey<out T>): T = provide(registryKey).get()
/**
 * Retrieves value by this [registryKey] or returns `null` if no association is present.
 */
public fun <T> Registry.getOrNull(registryKey: RegistryKey<T>): T? = provideOrNull(registryKey)?.get()
/**
 * Retrieves value by this [registryKey] or returns [default] value if no association is present.
 */
public fun <T> Registry.getOrDefault(registryKey: RegistryKey<T>, default: T): T {
    val provider = provideOrNull(registryKey)
    return if (provider != null) provider.get() else default
}

/**
 * Retrieves value by this [registryKey] or computes [block] and returns its value if no association is present.
 */
public inline fun <T> Registry.getOrElse(registryKey: RegistryKey<out T>, block: () -> T): T {
    val provider = provideOrNull(registryKey)
    return if (provider != null) provider.get() else block()
}

public interface MutableRegistry : Registry {
    public operator fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>)
    
    public fun setFrom(from: Registry)
    
    public fun remove(registryKey: RegistryKey<*>)
}

public operator fun <T> MutableRegistry.set(registryKey: RegistryKey<in T>, value: T) {
    set(registryKey) { value }
}

context(registry: MutableRegistry)
public infix fun <T> RegistryKey<in T>.correspondsTo(provider: RegisteredValueProvider<T>) {
    registry[this] = provider
}

context(registry: MutableRegistry)
public infix fun <T> RegistryKey<in T>.correspondsTo(value: T) {
    registry[this] = value
}

public fun interface RegistryImplication<in T> {
    public fun substitute(provider: RegisteredValueProvider<T>): Registry
}

public val <T> RegistryKey<in T>.withImplied: RegistryImplication<T>
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
                val (nextKey, nextInfo) = keysToCheck.entries.first()
                keysToCheck.remove(nextKey)
                this[nextKey] = nextInfo
                val newPath = nextInfo.path + nextKey
                for ((newKey, newProducer) in nextKey.key.impliedKeys) {
                    @Suppress("UNCHECKED_CAST")
                    newProducer as (Any?) -> Any?
                    val newInfo = RegistryKeyInfo(
                        path = newPath,
                        producer = { newProducer(nextInfo.producer(it)) },
                    )
                    if (RegistryKeyMapWrapper(newKey) in newPath) error("Cyclic implications: ${(newPath + RegistryKeyMapWrapper(newKey)).joinToString(separator = " -> ") { it.key.toString() }}")
                    val info = this[RegistryKeyMapWrapper(newKey)] ?: keysToCheck[RegistryKeyMapWrapper(newKey)]
                    if (info != null) {
                        if (info.path.withIndex().any { (index, pathKey) -> newPath[index] != pathKey })
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
        return RegistryImplication { provider ->
            @Suppress("UNCHECKED_CAST")
            object : Registry {
                private val content: Map<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>> =
                    results.mapValues { RegisteredValueProvider { it.value.producer(provider.get()) } }
                
                override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                    RegistryKeyMapWrapper(registryKey) in content
                override fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? =
                    content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T>?
                
                override fun iterator(): Iterator<Registration<*>> =
                    content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }.iterator()
                
                override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
            }
        }
    }

public val <T> RegistryKey<in T>.withImpliedUsingFirst: RegistryImplication<T>
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
                val (nextKey, nextProducer) = keysToProcess.removeFirst()
                if (nextKey in this) continue
                this[nextKey] = nextProducer
                for ((newKey, newProducer) in nextKey.key.impliedKeys) {
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
        return RegistryImplication { provider ->
            @Suppress("UNCHECKED_CAST")
            object : Registry {
                private val content: Map<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>> =
                    results.mapValues { RegisteredValueProvider { it.value(provider.get()) } }
                
                override operator fun contains(registryKey: RegistryKey<*>): Boolean =
                    RegistryKeyMapWrapper(registryKey) in content
                override fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? =
                    content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T>?
                
                override fun iterator(): Iterator<Registration<*>> =
                    content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }.iterator()
                
                override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
            }
        }
    }

public operator fun <T> MutableRegistry.set(registryImplication: RegistryImplication<T>, provider: RegisteredValueProvider<T>) {
    setFrom(registryImplication.substitute(provider))
}

public operator fun <T> MutableRegistry.set(registryImplication: RegistryImplication<T>, value: T) {
    setFrom(registryImplication.substitute { value })
}

context(registry: MutableRegistry)
public infix fun <T> RegistryImplication<T>.correspondsTo(provider: RegisteredValueProvider<T>) {
    registry.setFrom(this.substitute(provider))
}

context(registry: MutableRegistry)
public infix fun <T> RegistryImplication<T>.correspondsTo(value: T) {
    registry.setFrom(this.substitute { value })
}

@Suppress("UNCHECKED_CAST")
private class MutableRegistryImpl(private val content: MutableMap<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>>) : MutableRegistry {
    override operator fun contains(registryKey: RegistryKey<*>): Boolean =
        RegistryKeyMapWrapper(registryKey) in content
    override fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? =
        content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T>?
    override fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>) {
        content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    override fun setFrom(from: Registry) {
        for ((registryKey, value) in from) content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun remove(registryKey: RegistryKey<*>) {
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun iterator(): Iterator<Registration<*>> =
        content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }.iterator()
    
    override fun toString(): String = content.keys.joinToString(separator = ", ", prefix = "{", postfix = "}")
}

public fun MutableRegistry(): MutableRegistry = MutableRegistryImpl(mutableMapOf())

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class RegistryBuilder : MutableRegistry {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, RegisteredValueProvider<*>>? = mutableMapOf()
    
    override fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override fun <T> provideOrNull(registryKey: RegistryKey<out T>): RegisteredValueProvider<T>? {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content[RegistryKeyMapWrapper(registryKey)] as RegisteredValueProvider<T>?
    }
    
    override fun <T> set(registryKey: RegistryKey<in T>, provider: RegisteredValueProvider<T>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content[RegistryKeyMapWrapper(registryKey)] = provider
    }
    
    override fun setFrom(from: Registry) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        for ((registryKey, value) in from) content[RegistryKeyMapWrapper(registryKey)] = value
    }
    
    override fun remove(registryKey: RegistryKey<*>) {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun iterator(): Iterator<Registration<*>> {
        val content = content ?: error("The registry builder is already finalized. Apply the operation to the built result.")
        return content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }.iterator()
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
 * Builder function for [Registry].
 */
public inline fun Registry.Companion.build(block: MutableRegistry.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder().apply(block).build()
}
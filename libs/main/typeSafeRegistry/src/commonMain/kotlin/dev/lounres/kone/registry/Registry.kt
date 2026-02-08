/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.EmptyIterator
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class Registration<T>(
    val key: RegistryKey<T>,
    val value: T,
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
    
    /**
     * Retrieves value by this [registryKey] or throws exception if no association is present.
     */
    // TODO: Define exception that is thrown by this method
    public operator fun <T> get(registryKey: RegistryKey<out T>): T
    
    public companion object;
    
    public object Empty : Registry {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> get(registryKey: RegistryKey<out T>): T {
            TODO("Not yet implemented")
        }
        override fun iterator(): Iterator<Registration<*>> = EmptyIterator
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

public fun interface RegistryImplication<in T> {
    public fun substitute(value: T): Registry
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
        return { value ->
            MutableRegistryImpl(
                results.mapValuesTo(mutableMapOf()) { it.value.producer(value) },
            )
        }
    }

public operator fun <T> MutableRegistry.set(registryImplication: RegistryImplication<T>, value: T) {
    setFrom(registryImplication.substitute(value))
}

context(registry: MutableRegistry)
public infix fun <T> RegistryImplication<T>.correspondsTo(value: T) {
    registry.setFrom(this.substitute(value))
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
        for ((registryKey, value) in from) content[RegistryKeyMapWrapper(registryKey)] = value
    }
    override fun remove(registryKey: RegistryKey<*>) {
        content.remove(RegistryKeyMapWrapper(registryKey))
    }
    
    override fun iterator(): Iterator<Registration<*>> = content.entries.map { Registration(it.key.key as RegistryKey<Any?>, it.value) }.iterator()
}

public fun MutableRegistry(): MutableRegistry = MutableRegistryImpl(mutableMapOf())

/**
 * Mutable version of [Registry] that is used by builder function.
 */
@Suppress("UNCHECKED_CAST")
public class RegistryBuilder @PublishedApi internal constructor() : MutableRegistry {
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
}

/**
 * Builder function for [Registry].
 */
public inline fun Registry.Companion.build(block: RegistryBuilder.() -> Unit): Registry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RegistryBuilder().apply(block).build()
}
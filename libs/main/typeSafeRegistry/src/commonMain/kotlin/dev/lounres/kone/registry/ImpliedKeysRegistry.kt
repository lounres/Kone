/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.registry.internal.EmptyIterator
import dev.lounres.kone.registry.internal.RegistryKeyMapWrapper
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class ImplicationRegistration<in I, T>(
    val key: RegistryKey<T>,
    val mapping: (I) -> T,
)

public interface ImpliedKeysRegistry<in I> : Iterable<ImplicationRegistration<I, *>> {
    public operator fun contains(registryKey: RegistryKey<*>): Boolean
    public operator fun <T> get(registryKey: RegistryKey<out T>): (I) -> T
    
    public object Empty : ImpliedKeysRegistry<Any?> {
        override fun contains(registryKey: RegistryKey<*>): Boolean = false
        override fun <T> get(registryKey: RegistryKey<out T>): (Any?) -> T = error(TODO())
        override fun iterator(): Iterator<ImplicationRegistration<Any?, *>> = EmptyIterator
    }
}

public inline fun <I> ImpliedKeysRegistry(builder: ImpliedKeysRegistryBuilder<I>.() -> Unit): ImpliedKeysRegistry<I> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return ImpliedKeysRegistryBuilder<I>().apply(builder)
}

@Suppress("UNCHECKED_CAST")
public class ImpliedKeysRegistryBuilder<I> @PublishedApi internal constructor() : ImpliedKeysRegistry<I> {
    private var content: MutableMap<RegistryKeyMapWrapper<*>, (I) -> Any?> = mutableMapOf()
    
    override operator fun contains(registryKey: RegistryKey<*>): Boolean {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return RegistryKeyMapWrapper(registryKey) in content
    }
    override operator fun <T> get(registryKey: RegistryKey<out T>): (I) -> T {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return content[RegistryKeyMapWrapper(registryKey)] as (I) -> T
    }
    
    override fun iterator(): Iterator<ImplicationRegistration<I, *>> {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        return content.entries.map { (val key, val mapping = value) -> ImplicationRegistration(key.key as RegistryKey<Any?>, mapping) }.iterator()
    }
    
    public infix fun <T> RegistryKey<in T>.implies(value: (I) -> T) {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        content[RegistryKeyMapWrapper(this)] = value
    }
    
    public fun RegistryKey<in I>.impliesSame() {
        val content = content /*?: error(NO_CONTENT_MESSAGE)*/
        content[RegistryKeyMapWrapper(this)] = { it }
    }
    
    public companion object {
        private const val NO_CONTENT_MESSAGE = "The implied keys registry builder is already finalized. Apply the operation to the built result."
    }
}
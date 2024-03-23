/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.NoMatchingKeyException
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public interface KoneContextualMap<K, in KE: Equality<K>, out V> {
    public val size: UInt
    context(KE)
    public fun containsKey(key: K): Boolean
    context(Equality<V>)
    public fun containsValue(value: @UnsafeVariance V): Boolean

    context(KE)
    public operator fun get(key: K): V = getMaybe(key).orThrow { NoMatchingKeyException("There is no value for key $key") }
    context(KE)
    public fun getMaybe(key: K): Option<V>

    public val keys: KoneContextualIterableSet<K, KE>
    public val values: KoneContextualIterableCollection<V, Equality<V>>
    public val entries: KoneContextualIterableSet<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>> // TODO: Maybe `Equality<Entry<K, V>>` should be replaced with something concrete
}

public interface KoneContextualMutableMap<K, in KE: Equality<K>, V>: KoneContextualMap<K, KE, V> {
    context(KE)
    public operator fun set(key: K, value: V)
    context(KE)
    public fun getAndSet(key: K, value: V): V = getMaybeAndSet(key, value).orThrow { NoMatchingKeyException("There is no value for key $key") }
    context(KE)
    public fun getMaybeAndSet(key: K, value: V): Option<V> = getMaybe(key).also { this[key] = value }
    context(KE)
    public fun getOrSet(key: K, defaultValue: () -> V): V = getMaybe(key).orElse { defaultValue().also { this[key] = it } }

    // TODO: Think about "definitely remove".
    context(KE)
    public fun remove(key: K)
    context(KE)
    public fun getAndRemove(key: K): V = getMaybeAndRemove(key).orThrow { NoMatchingKeyException("There is no value for key $key") }
    context(KE)
    public fun getMaybeAndRemove(key: K): Option<V> = getMaybe(key).also { remove(key) }
    public fun removeAllThat(predicate: (key: K, value: V) -> Boolean)

    // TODO: Think about bulk operations.
    context(KE)
    public fun setAll(from: KoneContextualMap<out K, *, V>) { // TODO: I am not sure about `KE` parameter of `from` argument
        for ((key, value) in from) set(key, value)
    }
    context(KE)
    public fun setAll(from: KoneIterable<KoneMapEntry<K, V>>) {
        for ((key, value) in from) set(key, value)
    }
    public fun clear()
}
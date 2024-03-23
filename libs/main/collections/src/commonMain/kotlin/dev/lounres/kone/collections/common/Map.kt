/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.NoMatchingKeyException
import dev.lounres.kone.collections.next
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public interface KoneMap<K, out V> {
    public val size: UInt
    public fun containsKey(key: K): Boolean
    public fun containsValue(value: @UnsafeVariance V): Boolean

    public operator fun get(key: K): V = getMaybe(key).orThrow { NoMatchingKeyException("There is no value for key $key") }
    public fun getMaybe(key: K): Option<V>

    public val keys: KoneIterableSet<K>
    public val values: KoneIterableCollection<V>
    public val entries: KoneIterableSet<KoneMapEntry<K, V>>
}

public interface KoneMutableMap<K, V>: KoneMap<K, V> {
    public operator fun set(key: K, value: V)
    public fun getAndSet(key: K, value: V): V = getMaybeAndSet(key, value).orThrow { NoMatchingKeyException("There is no value for key $key") }
    public fun getMaybeAndSet(key: K, value: V): Option<V> = getMaybe(key).also { this[key] = value }
    public fun getOrSet(key: K, defaultValue: () -> V): V = getMaybe(key).orElse { defaultValue().also { this[key] = it } }

    // TODO: Think about "definitely remove".
    public fun remove(key: K)
    public fun getAndRemove(key: K): V = getMaybeAndRemove(key).orThrow { NoMatchingKeyException("There is no value for key $key") }
    public fun getMaybeAndRemove(key: K): Option<V> = getMaybe(key).also { remove(key) }
    public fun removeAllThat(predicate: (key: K, value: V) -> Boolean)

    // TODO: Think about bulk operations.
    public fun setAll(from: KoneMap<out K, V>) {
        for ((key, value) in from) set(key, value)
    }
    public fun setAll(from: KoneIterable<KoneMapEntry<K, V>>) {
        for ((key, value) in from) set(key, value)
    }
    public fun clear()
}
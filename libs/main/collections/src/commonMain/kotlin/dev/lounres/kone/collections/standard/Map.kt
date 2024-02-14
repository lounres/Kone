/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public class NoMatchingKeyException: NoSuchElementException {
    public constructor(): super()
    public constructor(message: String?): super(message)
}

public interface KoneMap<K, out V> {
    public val size: UInt
    public fun containsKey(key: K): Boolean
    public fun containsValue(value: @UnsafeVariance V): Boolean

    public operator fun get(key: K): V = getMaybe(key).orThrow { NoMatchingKeyException() }
    public fun getMaybe(key: K): Option<V>

    public val keys: KoneIterableSet<K>
    public val values: KoneIterableCollection<V>
    public val entries: KoneIterableSet<Entry<K, V>>

    public open class Entry<out K, out V>(public val key: K, public open val value: V) {
        public operator fun component1(): K = key
        public operator fun component2(): V = value
    }
}

public interface KoneMutableMap<K, V>: KoneMap<K, V> {
    public operator fun set(key: K, value: V)
    public fun getAndSet(key: K, value: V): V = getMaybeAndSet(key, value).orThrow { NoMatchingKeyException() }
    public fun getMaybeAndSet(key: K, value: V): Option<V> = getMaybe(key).also { this[key] = value }
    public fun getOrSet(key: K, defaultValue: () -> V): V = getMaybe(key).orElse { defaultValue().also { this[key] = it } }

    // TODO: Think about "definitely remove".
    public fun remove(key: K)
    public fun getAndRemove(key: K): V = getOptionalAndRemove(key).orThrow { NoMatchingKeyException() }
    public fun getOptionalAndRemove(key: K): Option<V> = getMaybe(key).also { remove(key) }
    public fun removeAllThat(predicate: (key: K, value: V) -> Boolean)

    // TODO: Think about bulk operations.
    public fun setAll(from: KoneMap<out K, V>)
    public fun setAll(from: KoneIterable<KoneMap.Entry<K, V>>)
    public fun clear()
}
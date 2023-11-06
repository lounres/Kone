/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.option.Option
import dev.lounres.kone.option.orThrow


public class NoMatchingKeyException: NoSuchElementException {
    public constructor(): super()
    public constructor(message: String?): super(message)
}

public interface KoneMap<K, out V> {
    public val size: Int
    public fun containsKey(key: K): Boolean
    public fun containsValue(value: @UnsafeVariance V): Boolean

    public operator fun get(key: K): V = getOptional(key).orThrow { NoMatchingKeyException() }
    public fun getOptional(key: K): Option<V>

    public val keys: KoneIterableSet<K>
    public val values: KoneIterableCollection<V>
    public val entries: KoneIterableSet<Entry<K, V>>

    public open class Entry<out K, out V>(public val key: K, public open val value: V)
}

public interface KoneMutableMap<K, V>: KoneMap<K, V> {
    public operator fun set(key: K, value: V)
    public fun getAndSet(key: K, value: V): V = getOptionalAndSet(key, value).orThrow { NoMatchingKeyException() }
    public fun getOptionalAndSet(key: K, value: V): Option<V> = getOptional(key).also { set(key, value) }

    // TODO: Think about "definitely remove".
    public fun remove(key: K)
    public fun getAndRemove(key: K): V = getOptionalAndRemove(key).orThrow { NoMatchingKeyException() }
    public fun getOptionalAndRemove(key: K): Option<V> = getOptional(key).also { remove(key) }
}
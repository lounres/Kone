/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.complex.KoneIterableCollection
import dev.lounres.kone.collections.complex.KoneIterableSet
import dev.lounres.kone.collections.complex.KoneMap
import dev.lounres.kone.collections.complex.isEmpty
import dev.lounres.kone.collections.noMatchingKeyException
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option


internal open class EmptyKoneMapTemplate<K, V> : KoneMap<K, V> {
    override val size: UInt = 0u
    override fun containsKey(key: K): Boolean = false
    override fun containsValue(value: V): Boolean = false

    override fun get(key: K): Nothing {
        noMatchingKeyException(key)
    }
    override fun getMaybe(key: K): Option<Nothing> = None

    override val keys: KoneIterableSet<K> = EmptyKoneIterableSet
    override val values: KoneIterableCollection<V> = EmptyKoneIterableList
    override val entries: KoneIterableSet<KoneMapEntry<K, V>> = EmptyKoneIterableSet

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object EmptyKoneMap: EmptyKoneMapTemplate<Any?, Nothing>()
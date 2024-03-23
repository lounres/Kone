/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.common.KoneIterableCollection
import dev.lounres.kone.collections.common.KoneIterableSet
import dev.lounres.kone.collections.common.KoneMap
import dev.lounres.kone.collections.common.isEmpty
import dev.lounres.kone.collections.noMatchingKeyException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option


//internal object EmptyKoneMap: KoneMap<Any?, Nothing> {
//    override val size: UInt = 0u
//    override fun containsKey(key: Any?): Boolean = false
//    override fun containsValue(value: Nothing): Boolean = false
//
//    override fun get(key: Any?): Nothing {
//        throw NoMatchingKeyException("Empty map doesn't contain any element")
//    }
//    override fun getMaybe(key: Any?): Option<Nothing> = None
//
//    override val keys: KoneIterableSet<Any?> = EmptyKoneIterableSet
//    override val values: KoneIterableCollection<Nothing> = EmptyKoneIterableList
//    override val entries: KoneIterableSet<KoneMapEntry<Any?, Nothing>> = EmptyKoneIterableSet
//
//    override fun toString(): String = "{}"
//    override fun hashCode(): Int = 0
//    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
//}

internal open class EmptyKoneMapTemplate<K, V>: KoneMap<K, V> {
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
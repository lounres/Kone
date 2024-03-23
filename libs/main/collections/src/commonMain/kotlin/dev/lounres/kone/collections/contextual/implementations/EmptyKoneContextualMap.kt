/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.contextual.KoneContextualIterableCollection
import dev.lounres.kone.collections.contextual.KoneContextualIterableSet
import dev.lounres.kone.collections.contextual.KoneContextualMap
import dev.lounres.kone.collections.contextual.isEmpty
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

internal open class EmptyKoneContextualMapTemplate<K, V>: KoneContextualMap<K, Equality<K>, V> {
    override val size: UInt = 0u
    context(Equality<K>)
    override fun containsKey(key: K): Boolean = false
    context(Equality<V>)
    override fun containsValue(value: V): Boolean = false

    context(Equality<K>)
    override fun get(key: K): Nothing {
        noMatchingKeyException(key)
    }
    context(Equality<K>)
    override fun getMaybe(key: K): Option<Nothing> = None

    override val keys: KoneContextualIterableSet<K, Equality<K>> = EmptyKoneContextualIterableSet
    override val values: KoneContextualIterableCollection<V, Equality<V>> = EmptyKoneContextualIterableList
    override val entries: KoneContextualIterableSet<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>> = EmptyKoneContextualIterableSet

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneContextualMap<*, *, *> && other.isEmpty()
}

internal object EmptyKoneContextualMap: EmptyKoneContextualMapTemplate<Any?, Nothing>()
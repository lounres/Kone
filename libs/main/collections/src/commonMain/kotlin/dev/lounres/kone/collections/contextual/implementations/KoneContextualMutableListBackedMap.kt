/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.utils.firstMaybe
import dev.lounres.kone.collections.contextual.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneContextualMutableListBackedMap<K, V> internal constructor(
    internal val backingList: KoneContextualMutableIterableList<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>>
) : KoneContextualMutableMap<K, Equality<K>, V> {

    override val size: UInt
        get() = backingList.size
    override val keys: KoneContextualIterableSet<K, Equality<K>>
        get() = KoneContextualListBackedSet(backingList.map { it.key })
    override val values: KoneContextualIterableCollection<V, Equality<V>>
        get() = backingList.map { it.value }
    override val entries: KoneContextualIterableSet<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>>
        get() = KoneContextualListBackedSet(backingList)

    context(Equality<K>)
    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> entry.key eq key } < backingList.size

    context(Equality<V>)
    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> entry.value eq value } < backingList.size

    context(Equality<K>)
    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { it.key eq key }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction

    context(Equality<K>)
    override fun set(key: K, value: V) {
        val index = backingList.indexThat { _, entry -> entry.key eq key }
        if (index == backingList.size) backingList.addAtTheEnd(KoneMapEntry(key, value))
        else backingList[index] = KoneMapEntry(key, value)
    }

    context(Equality<K>)
    override fun remove(key: K) {
        // TODO: This code can be optimised if it's replaced with `backingList.removeFirstThat { it.key eq key }`
        val index = backingList.indexThat { _, entry -> entry.key eq key }
        backingList.removeAt(index)
    }

    override fun removeAllThat(predicate: (key: K, value: V) -> Boolean) {
        backingList.removeAllThat { (key, value) -> predicate(key, value) }
    }

    override fun clear() {
        backingList.clear()
    }
}
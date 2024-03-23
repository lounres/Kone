/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.common.*
import dev.lounres.kone.collections.common.utils.firstMaybe
import dev.lounres.kone.collections.common.utils.map
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneMutableListBackedMap<K, V> internal constructor(
    internal val backingList: KoneMutableIterableList<KoneMapEntry<K, V>>
) : KoneMutableMap<K, V> {

    override val size: UInt
        get() = backingList.size
    override val keys: KoneIterableSet<K>
        get() = KoneListBackedSet(backingList.map { it.key })
    override val values: KoneIterableCollection<V>
        get() = backingList.map { it.value }
    override val entries: KoneIterableSet<KoneMapEntry<K, V>>
        get() = KoneListBackedSet(backingList)

    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> entry.key == key } < backingList.size

    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> entry.value == value } < backingList.size

    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { it.key == key }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction

    override fun set(key: K, value: V) {
        val index = backingList.indexThat { _, entry -> entry.key == key }
        if (index == backingList.size) backingList.addAtTheEnd(KoneMapEntry(key, value))
        else backingList[index] = KoneMapEntry(key, value)
    }

    override fun remove(key: K) {
        // TODO: This code can be optimised if it's replaced with `backingList.removeFirstThat { it.key eq key }`
        val index = backingList.indexThat { _, entry -> entry.key == key }
        backingList.removeAt(index)
    }

    override fun removeAllThat(predicate: (key: K, value: V) -> Boolean) {
        backingList.removeAllThat { (key, value) -> predicate(key, value) }
    }

    override fun clear() {
        backingList.clear()
    }
}
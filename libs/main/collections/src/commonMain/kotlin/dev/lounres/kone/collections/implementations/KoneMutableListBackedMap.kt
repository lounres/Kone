/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.utils.firstMaybe
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneMutableListBackedMap<K, V> internal constructor(
    private val keyContext: Equality<K>,
    private val valueContext: Equality<V>,
    internal val backingList: KoneMutableIterableList<KoneMapEntry<K, V>>,
) : KoneMutableMap<K, V> {

    override val size: UInt
        get() = backingList.size
    override val keys: KoneIterableSet<K>
        get() = KoneListBackedSet(backingList.map(context = keyContext) { it.key })
    override val values: KoneIterableCollection<V>
        get() = backingList.map(context = valueContext) { it.value }
    override val entries: KoneIterableSet<KoneMapEntry<K, V>>
        get() = KoneListBackedSet(backingList)

    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> entry.key == key } < backingList.size

    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> entry.value == value } < backingList.size

    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { it.key == key }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction

    override fun set(key: K, value: V) {
        val index = backingList.indexThat { _, entry -> entry.key == key }
        if (index == backingList.size) backingList.add(KoneMapEntry(key, value))
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

    override fun removeAll() {
        backingList.removeAll()
    }

    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('{')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext())
        while (iterator.hasNext()) {
            append(", ")
            append(iterator.getAndMoveNext())
        }
        append('}')
    }
}
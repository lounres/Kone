/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstMaybe
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneMutableListBackedMap<K, KC: Equality<K>, V, VC: Equality<V>> @PublishedApi internal constructor(
    override val keyContext: KC,
    override val valueContext: VC,
    internal val backingList: KoneMutableIterableList<KoneMapEntry<K, V>>,
) : KoneMutableMapWithContext<K, KC, V, VC> {

    override val size: UInt
        get() = backingList.size
    override val keys: KoneIterableSet<K>
        get() = KoneListBackedSet(keyContext, backingList.map(elementContext = keyContext) { it.key })
    override val values: KoneIterableCollection<V>
        get() = backingList.map(elementContext = valueContext) { it.value }
    override val entries: KoneIterableSet<KoneMapEntry<K, V>>
        get() = KoneListBackedSet(koneMapEntryEquality(keyContext, valueContext), backingList)

    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> keyContext { entry.key eq key } } < backingList.size

    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> valueContext { entry.value eq value } } < backingList.size

    override fun get(key: K): V = backingList.first{ keyContext { it.key eq key } }.value

    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { keyContext { it.key eq key } }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction

    override fun set(key: K, value: V) {
        val index = backingList.indexThat { _, entry -> entry.key == key }
        if (index == backingList.size) backingList.add(KoneMapEntry(key, value))
        else backingList[index] = KoneMapEntry(key, value)
    }

    override fun set(entry: KoneMapEntry<K, V>) {
        val index = backingList.indexThat { _, currentEntry -> entry.key == currentEntry.key }
        if (index == backingList.size) backingList.add(entry)
        else backingList[index] = entry
    }

    override fun remove(key: K) {
        // TODO: This code can be optimised if it's replaced with `backingList.removeFirstThat { it.key eq key }`
        val index = backingList.indexThat { _, entry -> keyContext { entry.key eq key } }
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
        for (element in iterator) {
            append(", ")
            append(element)
        }
        append('}')
    }
}
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
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneListBackedMap<K, KC: Equality<K>, V, VC: Equality<V>> @PublishedApi internal constructor(
    override val keyContext: KC,
    override val valueContext: VC,
    internal val backingList: KoneIterableList<KoneMapEntry<K, V>>,
) : KoneMapWithContext<K, KC, V, VC> {

    override val size: UInt
        get() = backingList.size
    override val keysView: KoneIterableSet<K>
        get() = KoneListBackedSet(keyContext, backingList.map(elementContext = keyContext) { it.key })
    override val valuesView: KoneIterableCollection<V>
        get() = backingList.map(elementContext = valueContext) { it.value }
    override val entriesView: KoneIterableSet<KoneMapEntry<K, V>>
        get() = KoneListBackedSet(koneMapEntryEquality(keyContext, valueContext), backingList)

    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> entry.key == key } < backingList.size

    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> entry.value == value } < backingList.size

    override fun get(key: K): V = backingList.first { it.key == key }.value

    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { it.key == key }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction

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
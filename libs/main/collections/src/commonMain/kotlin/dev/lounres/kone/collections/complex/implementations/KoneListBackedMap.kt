/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.complex.KoneIterableCollection
import dev.lounres.kone.collections.complex.KoneIterableSet
import dev.lounres.kone.collections.complex.KoneMap
import dev.lounres.kone.collections.complex.KoneMutableIterableList
import dev.lounres.kone.collections.complex.utils.map
import dev.lounres.kone.collections.utils.KoneMapEntryEquality
import dev.lounres.kone.collections.utils.firstMaybe
import dev.lounres.kone.collections.utils.koneMapEntryEquality
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.computeOn


public class KoneListBackedMap<K, V> internal constructor(
    override val keyContext: Equality<K>,
    override val valueContext: Equality<V>,
    private val entryEquality: KoneMapEntryEquality<K, V>,
    internal val backingList: KoneMutableIterableList<KoneMapEntry<K, V>>,
) : KoneMap<K, V> {

    override val size: UInt
        get() = backingList.size
    override val keys: KoneIterableSet<K>
        get() = KoneListBackedSet(context = keyContext, backingList.map(context = keyContext) { it.key })
    override val values: KoneIterableCollection<V>
        get() = backingList.map(context = valueContext) { it.value }
    override val entries: KoneIterableSet<KoneMapEntry<K, V>>
        get() = KoneListBackedSet(context = entryEquality, backingList)

    override fun containsKey(key: K): Boolean = backingList.indexThat { _, entry -> entry.key == key } < backingList.size

    override fun containsValue(value: V): Boolean = backingList.indexThat { _, entry -> entry.value == value } < backingList.size

    override fun getMaybe(key: K): Option<V> = backingList.firstMaybe { it.key == key }.computeOn { it.value } // TODO: This code can be optimised by eliminating first `Option` construction
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.contextual.KoneContextualIterableCollection
import dev.lounres.kone.collections.contextual.KoneContextualIterableSet
import dev.lounres.kone.collections.contextual.KoneContextualMap
import dev.lounres.kone.collections.contextual.KoneContextualMutableIterableList
import dev.lounres.kone.collections.contextual.utils.firstMaybe
import dev.lounres.kone.collections.contextual.utils.map
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.computeOn


public class KoneContextualListBackedMap<K, V> internal constructor(
    internal val backingList: KoneContextualMutableIterableList<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>>
) : KoneContextualMap<K, Equality<K>, V> {

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
    override fun getMaybe(key: K): Option<V> {
        for (entry in backingList) if (entry.key eq key) return Some(entry.value)
        return None
    }
}
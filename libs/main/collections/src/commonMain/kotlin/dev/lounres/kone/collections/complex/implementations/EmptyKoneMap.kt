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
import dev.lounres.kone.collections.complex.utils.emptyKoneIterableList
import dev.lounres.kone.collections.complex.utils.emptyKoneIterableSet
import dev.lounres.kone.collections.noMatchingKeyException
import dev.lounres.kone.collections.utils.KoneMapEntryEquality
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option


internal class EmptyKoneMap<K, V>(
    override val keyContext: Equality<K>,
    override val valueContext: Equality<V>
): KoneMap<K, V> {
    override val size: UInt = 0u
    override fun containsKey(key: K): Boolean = false
    override fun containsValue(value: V): Boolean = false
    private val entryContext = KoneMapEntryEquality(keyContext = keyContext, valueContext = valueContext)

    override fun get(key: K): Nothing {
        noMatchingKeyException(key)
    }
    override fun getMaybe(key: K): Option<Nothing> = None

    override val keys: KoneIterableSet<K> = emptyKoneIterableSet(context = keyContext)
    override val values: KoneIterableCollection<V> = emptyKoneIterableList(context = valueContext)
    override val entries: KoneIterableSet<KoneMapEntry<K, V>> = emptyKoneIterableSet(context = entryContext)

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}
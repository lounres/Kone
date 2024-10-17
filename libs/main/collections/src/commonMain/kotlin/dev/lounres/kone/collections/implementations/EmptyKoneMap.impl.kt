/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option


internal open class EmptyKoneMapTemplate<K, V> : KoneMapWithContext<K, Equality<K>, V, Equality<V>> {
    override val keyContext: Equality<K> get() = defaultHashing()
    override val valueContext: Equality<V> get() = defaultHashing()

    override val size: UInt = 0u
    override fun containsKey(key: K): Boolean = false
    override fun containsValue(value: V): Boolean = false

    override fun get(key: K): Nothing {
        noMatchingKeyException(key)
    }
    override fun getMaybe(key: K): Option<Nothing> = None

    override val keysView: KoneIterableSet<K> = EmptyKoneIterableSet
    override val valuesView: KoneIterableCollection<V> = EmptyKoneIterableList
    override val entriesView: KoneIterableSet<KoneMapEntry<K, V>> = EmptyKoneIterableSet

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object EmptyKoneMap: EmptyKoneMapTemplate<Any?, Nothing>()
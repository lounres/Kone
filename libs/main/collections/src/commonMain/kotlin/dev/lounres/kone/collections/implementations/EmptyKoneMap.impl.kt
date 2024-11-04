/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing


internal open class EmptyKoneMapTemplate<K, V> : KoneMapWithContext<K, Equality<K>, V> {
    override val keyContext: Equality<K> get() = defaultHashing()

    override val size: UInt = 0u
    
    override fun getNodeOrNull(key: K): KoneMapNode<K, V>? = null
    
    override val nodesView: KoneSet<KoneMapNode<K, V>> get() = EmptyKoneSet
    override val keysView: KoneSet<K> get() = EmptyKoneSet
    override val valuesView: KoneIterable<V> get() = EmptyKoneList
    override val entriesView: KoneIterable<KoneMapEntry<K, V>> get() = EmptyKoneList

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object EmptyKoneMap: EmptyKoneMapTemplate<Any?, Nothing>()
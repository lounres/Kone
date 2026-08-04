/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.empty

import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterable.empty.KoneEmptySettableLinearIterable
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapNode
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.isEmpty
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.empty.KoneEmptyNoddedReifiedSet


internal open class KoneEmptyMapReifiedTemplate<Key, Value> : KoneReifiedMap<Key, Value> {
    override val size: UInt get() = 0u
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = null
    
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> get() = KoneEmptyNoddedReifiedSet
    override val keysView: KoneReifiedSet<Key> get() = KoneEmptyNoddedReifiedSet
    override val valuesView: KoneIterable<Value> get() = KoneEmptySettableLinearIterable

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object KoneEmptyReifiedMap: KoneEmptyMapReifiedTemplate<Any?, Nothing>()
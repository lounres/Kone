/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


internal open class KoneEmptyMapReifiedTemplate<Key, Value> : KoneReifiedMap<Key, Value> {
    override val size: UInt get() = 0u
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = null
    
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> get() = KoneEmptyNoddedReifiedSet
    override val keysView: KoneReifiedSet<Key> get() = KoneEmptyNoddedReifiedSet
    override val valuesView: KoneIterable<Value> get() = KoneEmptySettableLinearIterable
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> get() = KoneEmptySettableLinearIterable

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object KoneEmptyReifiedMap: KoneEmptyMapReifiedTemplate<Any?, Nothing>()
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing


internal open class KoneEmptyMapTemplate<Key, Value> : KoneMapWithContext<Key, Equality<Key>, Value> {
    override val keyContext: Equality<Key> get() = defaultHashing()

    override val size: UInt = 0u
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = null
    
    override val nodesView: KoneSet<KoneMapNode<Key, Value>> get() = KoneEmptyNoddedSet
    override val keysView: KoneSet<Key> get() = KoneEmptyNoddedSet
    override val valuesView: KoneIterable<Value> get() = KoneEmptyLinearIterable
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> get() = KoneEmptyLinearIterable

    override fun toString(): String = "{}"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneMap<*, *> && other.isEmpty()
}

internal object KoneEmptyMap: KoneEmptyMapTemplate<Any?, Nothing>()
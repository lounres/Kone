/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.absoluteReifiedEquality


// TODO: Describe contracts on equals and hashCode.

public interface KoneReifiedMap<out Key, out Value> : KoneMap<@UnsafeVariance Key, @UnsafeVariance Value> {
    override val keysView: KoneReifiedSet<Key>
    override val keys: KoneReifiedSet<Key> get() = keysView
    override val valuesView: KoneIterable<Value>
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>>
    
    override fun getNodeOrNull(key: @UnsafeVariance Key): KoneMapNode<Key, Value>?
}

public interface KoneMutableReifiedMap<Key, Value> : KoneReifiedMap<Key, Value>, KoneMutableMap<Key, Value> {
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
        get() = nodesView.toKoneReifiedSet(absoluteReifiedEquality())
    override val keysView: KoneReifiedSet<Key>
    override val keys: KoneReifiedSet<Key>
}
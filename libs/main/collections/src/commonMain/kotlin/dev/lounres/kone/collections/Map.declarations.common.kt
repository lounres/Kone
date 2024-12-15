/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.absoluteReifiedEquality


// TODO: Describe contracts on equals and hashCode.

public interface KoneMap<Key, out Value> {
    public val size: UInt
    
    public val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>>
    public val nodes: KoneReifiedSet<KoneMapNode<Key, Value>> get() = nodesView
    public val keysView: KoneSet<Key>
    public val keys: KoneSet<Key> get() = keysView
    public val valuesView: KoneIterable<Value>
    public val entriesView: KoneIterable<KoneMapEntry<Key, Value>>

    public fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>?
}

public interface KoneMutableMap<Key, Value> : KoneMap<Key, Value> {
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
        get() = nodesView.toKoneReifiedSet(absoluteReifiedEquality())
    override val keys: KoneSet<Key>
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>?
    
    public operator fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value>
    
    public fun remove(key: Key)
    
    public fun removeAll()
    
    // TODO: Think about bulk operations.
//    public fun removeAllThat(predicate: (key: K, value: V) -> Boolean)
//    public fun setAllFrom(from: KoneMap<out K, V>) {
//        for ((key, value) in from) set(key, value)
//    }
//    public fun setAllFrom(from: KoneIterable<KoneMapEntry<K, V>>) {
//        for ((key, value) in from) set(key, value)
//    }
//    public fun setSeveral(number: UInt, builder: (UInt) -> KoneMapEntry<K, V>) {
//        repeat(number) { set(builder(it)) }
//    }
//    public fun setSeveral(number: UInt, builder: (UInt) -> KoneMapNode<K, V>) {
//        repeat(number) { set(builder(it)) }
//    }
}
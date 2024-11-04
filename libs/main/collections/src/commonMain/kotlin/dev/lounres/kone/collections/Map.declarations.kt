/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: Describe contracts on equals and hashCode.

public interface KoneMap<K, out V> {
    public val size: UInt
    
    public val nodesView: KoneSet<KoneMapNode<K, V>>
    public val keysView: KoneSet<K>
    public val valuesView: KoneIterable<V>
    public val entriesView: KoneIterable<KoneMapEntry<K, V>>

    public fun getNodeOrNull(key: K): KoneMapNode<K, V>?
}

public interface KoneMutableMap<K, V>: KoneMap<K, V> {
    override val nodesView: KoneSet<KoneMutableMapNode<K, V>>
    
    override fun getNodeOrNull(key: K): KoneMutableMapNode<K, V>?
    
    public operator fun set(key: K, value: V): KoneMutableMapNode<K, V>
    
    // TODO: Think about bulk operations.
//    public fun removeAllThat(predicate: (key: K, value: V) -> Boolean)
//    public fun setAllFrom(from: KoneMap<out K, V>) {
//        for ((key, value) in from) set(key, value)
//    }
//    public fun setAllFrom(from: KoneIterable<KoneMapEntry<K, V>>) {
//        for ((key, value) in from) set(key, value)
//    }
//    public fun removeAll() {
//        for (node in nodes) node.remove()
//    }
}
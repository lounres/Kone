/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


// TODO: Describe contracts on equals and hashCode.

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMap<Key, out Value> {
    public val size: UInt
    
    public val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>>
    public val nodes: KoneReifiedSet<KoneMapNode<Key, Value>> get() = nodesView
    public val keysView: KoneSet<Key>
    public val keys: KoneSet<Key> get() = keysView
    public val valuesView: KoneIterable<Value>

    public fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>?
    
    public companion object
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableMap<Key, Value> : KoneMap<Key, Value> {
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
        get() = nodesView.toKoneReifiedSet(
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    override val keys: KoneSet<Key>
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>?
    
    @IgnorableReturnValue
    public operator fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value>
    
    public fun remove(key: Key)
    
    public fun removeAll()
    
    // TODO: Think about bulk operations.
    public fun removeAllThat(predicate: (key: Key, value: Value) -> Boolean)
    public fun removeAllNodesThat(predicate: (node: KoneMutableMapNode<Key, Value>) -> Boolean)
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
    
    public companion object
}

// TODO: Design linked versions
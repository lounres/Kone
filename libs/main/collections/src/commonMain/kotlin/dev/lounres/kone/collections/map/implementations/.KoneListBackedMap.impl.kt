/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapNode
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedSet
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.eq
import dev.lounres.kone.context


public open class KoneListBackedMap<Key, Value> @PublishedApi internal constructor(
    public val keyEquality: Equality<Key>,
    internal val backingList: KoneList<Node<Key, Value>>,
) : KoneMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> = backingList.toKoneReifiedSet(elementEquality = absoluteEquality())
    override val keysView: KoneSet<Key> = KoneListBackedSet(keyEquality, backingList.map { it.key })
    override val valuesView: KoneIterable<Value> = backingList.map { it.value }
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = backingList.firstThatOrNull { context(keyEquality) { it.key eq key } }
    
    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('{')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext())
        for (element in iterator) {
            append(", ")
            append(element)
        }
        append('}')
    }
    
    @PublishedApi
    internal data class Node<out K, out V>(
        override val key: K,
        override val value: V
    ) : KoneMapNode<K, V> {
        override var isDetached: Boolean = false
            private set
        
        override fun toString(): String = "$key=$value"
    }
}

public class KoneListBackedReifiedMap<Key, Value> @PublishedApi internal constructor(
    public val keyReification: Reification<Key>,
    keyEquality: Equality<Key>,
    backingList: KoneList<Node<Key, Value>>,
) : KoneListBackedMap<Key, Value>(
    keyEquality = keyEquality,
    backingList = backingList,
), KoneReifiedMap<Key, Value> {
    override val keysView: KoneReifiedSet<Key> = KoneListBackedReifiedSet(keyReification, keyEquality, backingList.map { it.key })
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = if (key in keyReification) backingList.firstThatOrNull { context(keyEquality) { it.key eq key } } else null
}
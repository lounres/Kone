/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.set.implementations.KoneListBackedReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedSet
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapEntry
import dev.lounres.kone.collections.map.KoneMapNode
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.absoluteReifiedEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


public open class KoneListBackedMap<Key, KeyContext: Equality<Key>, Value> @PublishedApi internal constructor(
    public val keyContext: KeyContext,
    internal val backingList: KoneList<Node<Key, Value>>,
) : KoneMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> = backingList.toKoneReifiedSet(absoluteReifiedEquality())
    override val keysView: KoneSet<Key> = KoneListBackedSet(keyContext, backingList.map { it.key })
    override val valuesView: KoneIterable<Value> = backingList.map { it.value }
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> = backingList.map { KoneMapEntry(it.key, it.value) }
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = backingList.firstThatOrNull { keyContext { it.key eq key } }
    
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
    }
}

public class KoneListBackedReifiedMap<Key, KeyContext: ReifiedEquality<Key>, Value> @PublishedApi internal constructor(
    keyContext: KeyContext,
    backingList: KoneList<Node<Key, Value>>,
) : KoneListBackedMap<Key, KeyContext, Value>(
    keyContext = keyContext,
    backingList = backingList,
), KoneReifiedMap<Key, Value> {
    override val keysView: KoneReifiedSet<Key> = KoneListBackedReifiedSet(keyContext, backingList.map { it.key })
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = if (key in keyContext) backingList.firstThatOrNull { keyContext { it.key eq key } } else null
}
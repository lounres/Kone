/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


public class KoneListBackedMap<Key, KeyContext: Equality<Key>, Value> @PublishedApi internal constructor(
    override val keyContext: KeyContext,
    internal val backingList: KoneList<Node<Key, Value>>,
) : KoneMapWithContext<Key, KeyContext, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneSet<KoneMapNode<Key, Value>> = backingList.toKoneSet(absoluteEquality())
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
    internal data class Node<out K, out V>(override val key: K, override val value: V) : KoneMapNode<K, V>
}
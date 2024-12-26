/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMapNode
import dev.lounres.kone.collections.KoneReifiedMap
import dev.lounres.kone.collections.KoneReifiedSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.absoluteReifiedEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonMap<Key, Value>(
    val singleKey: Key,
    val singleValue: Value,
    open val keyContext: Equality<Key>,
) : KoneMap<Key, Value> {
    internal val singleEntry = KoneMapEntry(singleKey, singleValue)
    internal val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> =
        KoneSingletonNoddedReifiedSet( // TODO: Replace with `KoneSingletonNoddedAbsoluteReifiedSet`
            singleElement = singleNode,
            elementContext = absoluteReifiedEquality(),
        )
    override val keysView: KoneSet<Key> =
        KoneSingletonNoddedSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    override val valuesView: KoneIterable<Value> = Values(this)
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> = Entries(this)
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = if (keyContext { key eq singleKey }) singleNode else null
    
    override fun toString(): String = "{$singleKey=$singleValue}"
    override fun hashCode(): Int = singleKey.hashCode() xor singleValue.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (other.size != 1u) return false

        val (otherKey, otherValue) = other.entriesView.first()

        return singleKey == otherKey && singleValue == otherValue
    }
    
    internal class Node<Key, Value>(
        override val key: Key,
        override val value: Value,
    ) : KoneMapNode<Key, Value> {
        override val isDetached: Boolean get() = false
    }
    
    // TODO: Replace with singleton iterable.
    internal class Values<Value>(
        val map: KoneSingletonMap<*, Value>,
    ) : KoneIterable<Value> {
        override val size: UInt get() = 1u
        
        override fun iterator(): KoneIterator<Value> = Iterator(map)
        
        internal class Iterator<Value>(
            val map: KoneSingletonMap<*, Value>,
            var currentlyBeforeSingleElement: Boolean = true,
        ) : KoneIterator<Value> {
            override fun hasNext(): Boolean = currentlyBeforeSingleElement
            override fun getNext(): Value =
                if (!hasNext()) noNextElementInIteratorException()
                else map.singleValue
            override fun moveNext() {
                if (!hasNext()) noNextElementInIteratorException()
                currentlyBeforeSingleElement = false
            }
        }
    }
    
    // TODO: Replace with singleton iterable.
    internal class Entries<Key, Value>(
        val map: KoneSingletonMap<Key, Value>,
    ) : KoneIterable<KoneMapEntry<Key, Value>> {
        override val size: UInt get() = 1u
        
        override fun iterator(): KoneIterator<KoneMapEntry<Key, Value>> = Iterator(map)
        
        internal class Iterator<Key, Value>(
            val map: KoneSingletonMap<Key, Value>,
            var currentlyBeforeSingleElement: Boolean = true,
        ) : KoneIterator<KoneMapEntry<Key, Value>> {
            override fun hasNext(): Boolean = currentlyBeforeSingleElement
            override fun getNext(): KoneMapEntry<Key, Value> =
                if (!hasNext()) noNextElementInIteratorException()
                else map.singleEntry
            override fun moveNext() {
                if (!hasNext()) noNextElementInIteratorException()
                currentlyBeforeSingleElement = false
            }
        }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonReifiedMap<Key, Value>(
    singleKey: Key,
    singleValue: Value,
    override val keyContext: ReifiedEquality<Key>,
) : KoneSingletonMap<Key, Value>(
    singleKey = singleKey,
    singleValue = singleValue,
    keyContext = keyContext,
), KoneReifiedMap<Key, Value> {
    override val keysView: KoneReifiedSet<Key> =
        KoneSingletonNoddedReifiedSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? =
        if (key in keyContext && keyContext { key eq singleKey }) singleNode else null
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.singleton

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapEntry
import dev.lounres.kone.collections.map.KoneMapNode
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedReifiedSet
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedSet
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonMap<Key, Value>(
    val singleKey: Key,
    val singleValue: Value,
    open val keyEquality: Equality<Key>,
) : KoneMap<Key, Value> {
    internal val singleEntry = KoneMapEntry(singleKey, singleValue)
    internal val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> =
        KoneSingletonNoddedReifiedSet( // TODO: Replace with `KoneSingletonNoddedAbsoluteReifiedSet`
            singleElement = singleNode,
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
        )
    override val keysView: KoneSet<Key> =
        KoneSingletonNoddedSet(
            singleElement = singleKey,
            elementEquality = keyEquality
        )
    override val valuesView: KoneIterable<Value> = Values(this)
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = if (keyEquality { key eq singleKey }) singleNode else null
    
    override fun toString(): String = "{$singleKey=$singleValue}"
    override fun hashCode(): Int = singleKey.hashCode() xor singleValue.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (other.size != 1u) return false

        val otherEntry = other.nodesView.first()

        return singleKey == otherEntry.key && singleValue == otherEntry.value
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
    val keyReification: Reification<Key>,
    keyEquality: Equality<Key>,
) : KoneSingletonMap<Key, Value>(
    singleKey = singleKey,
    singleValue = singleValue,
    keyEquality = keyEquality,
), KoneReifiedMap<Key, Value> {
    override val keysView: KoneReifiedSet<Key> =
        KoneSingletonNoddedReifiedSet(
            singleElement = singleKey,
            elementReification = keyReification,
            elementEquality = keyEquality
        )
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? =
        if (key in keyReification && keyEquality { key eq singleKey }) singleNode else null
}
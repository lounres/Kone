/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.KoneMutableMapNode
import dev.lounres.kone.collections.map.KoneMutableReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedReifiedSet
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.collections.set.toKoneSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq


public open class KoneListBackedMutableMap<Key, Value> @PublishedApi internal constructor(
    public val keyEquality: Equality<Key>,
    internal val backingList: KoneMutableNoddedList<Node<Key, Value>>,
) : KoneMutableMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> = KoneListBackedReifiedSet(elementReification = Reification.defaultFor(), elementEquality = Equality.absoluteFor(), backingList)
    override val keysView: KoneSet<Key> = KeysView(this)
    override val keys: KoneSet<Key> get() = keysView.toKoneSet(elementEquality = keyEquality)
    override val valuesView: KoneIterable<Value> = ValuesView(this)
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? =
        backingList.firstThatOrNull { keyEquality { it.key eq key } }
    
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val index = backingList.firstIndexThat { _, entry -> keyEquality { entry.key eq key } }
        if (index == backingList.size) {
            val newNode = Node(key, value)
            val listNode = backingList.addNode(newNode)
            newNode.backingListNode = listNode
            return newNode
        } else {
            val node = backingList[index]
            node.value = value
            return node
        }
    }
    
    override fun remove(key: Key) {
        val index = backingList.firstIndexThat { _, entry -> keyEquality { entry.key eq key } }
        if (index != backingList.size) {
            val node = backingList[index]
            node.backingListNode.remove()
            node.detach()
        }
    }
    
    override fun removeAll() {
        backingList.forEach { it.detach() }
        backingList.removeAll()
    }
    
    override fun removeAllThat(predicate: (Key, Value) -> Boolean) {
        backingList.removeAllThat { node -> predicate(node.key, node.value).also { if (it) node.detach() } }
    }
    
    override fun removeAllNodesThat(predicate: (KoneMutableMapNode<Key, Value>) -> Boolean) {
        backingList.removeAllThat { node -> predicate(node).also { if (it) node.detach() } }
    }

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
    
    internal class Node<K, V>(
        override val key: K,
        override var value: V,
    ) : KoneMutableMapNode<K, V> {
        override var isDetached: Boolean = false
            private set
        
        private var _backingListNode: KoneMutableListNode<*>? = null
        internal var backingListNode: KoneMutableListNode<*>
            get() = _backingListNode!!
            set(value) { _backingListNode = value }
        
        fun detach() {
            _backingListNode = null
            isDetached = true
        }
        override fun remove() {
            val listNode = _backingListNode
            check(listNode != null) { "The map node is already removed" }
            listNode.remove()
            _backingListNode = null
        }
        
        override fun toString(): String = "$key=$value"
    }
    
    internal class KeysIterator<K>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, *>>) : KoneIterator<K> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): K = nodesIterator.getNext().key
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class KeysView<Key>(
        private val map: KoneListBackedMutableMap<Key, *>,
    ) : KoneSet<Key> {
        override val size: UInt get() = map.size
        
        override fun contains(element: Key): Boolean =
            map.backingList.any { currentNode -> map.keyEquality { element eq currentNode.key } }
        
        override fun iterator(): KoneIterator<Key> = KeysIterator(map.backingList.iterator())
    }
    
    internal class ValuesIterator<V>(private val nodesIterator: KoneIterator<KoneMutableMapNode<*, V>>) : KoneIterator<V> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): V = nodesIterator.getNext().value
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    internal class ValuesView<Value>(
        val map: KoneListBackedMutableMap<*, Value>,
    ) : KoneIterable<Value> {
        override val size: UInt get() = map.size
        override fun iterator(): KoneIterator<Value> = ValuesIterator(map.backingList.iterator())
    }
}

public class KoneListBackedMutableReifiedMap<Key, Value> @PublishedApi internal constructor(
    public val keyReification: Reification<Key>,
    keyEquality: Equality<Key>,
    backingList: KoneMutableNoddedList<Node<Key, Value>>,
) : KoneListBackedMutableMap<Key, Value>(
    keyEquality = keyEquality,
    backingList = backingList,
), KoneMutableReifiedMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val keysView: KoneReifiedSet<Key> = KeysView(this)
    override val keys: KoneReifiedSet<Key> get() = keysView.toKoneReifiedSet(elementReification = keyReification, elementEquality = keyEquality)
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? =
        if (key in keyReification) backingList.firstThatOrNull { keyEquality { it.key eq key } } else null
    
    internal class KeysIterator<K>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, *>>) : KoneIterator<K> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): K = nodesIterator.getNext().key
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class KeysView<Key>(
        private val map: KoneListBackedMutableReifiedMap<Key, *>,
    ) : KoneReifiedSet<Key> {
        override val size: UInt get() = map.size
        
        override fun contains(element: Key): Boolean =
            element in map.keyReification && map.backingList.any { currentNode -> map.keyEquality { element eq currentNode.key } }
        
        override fun iterator(): KoneIterator<Key> = KeysIterator(map.backingList.iterator())
    }
}
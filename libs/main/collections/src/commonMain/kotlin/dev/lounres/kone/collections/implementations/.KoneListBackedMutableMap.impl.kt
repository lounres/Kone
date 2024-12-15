/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.absoluteReifiedEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


public open class KoneMutableListBackedMap<Key, KeyContext: Equality<Key>, Value> @PublishedApi internal constructor(
    public val keyContext: KeyContext,
    internal val backingList: KoneMutableNoddedList<Node<Key, Value>>,
) : KoneMutableMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> = KoneListBackedReifiedSet(absoluteReifiedEquality(), backingList)
    override val keysView: KoneSet<Key> = KeysView()
    override val keys: KoneSet<Key> get() = keysView.toKoneSet(elementContext = keyContext)
    override val valuesView: KoneIterable<Value> = ValuesView()
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> = EntriesView()
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? =
        backingList.firstThatOrNull { keyContext { it.key eq key } }
    
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val index = backingList.firstIndexThat { _, entry -> keyContext { entry.key eq key } }
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
        val index = backingList.firstIndexThat { _, entry -> keyContext { entry.key eq key } }
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
    }
    
    internal class KeysIterator<K>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, *>>) : KoneIterator<K> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): K = nodesIterator.getNext().key
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class KeysView : KoneSet<Key> {
        override val size: UInt get() = this@KoneMutableListBackedMap.size
        
        override fun contains(element: Key): Boolean =
            backingList.any { currentNode -> keyContext { element eq currentNode.key } }
        
        override fun iterator(): KoneIterator<Key> = KeysIterator(backingList.iterator())
    }
    
    internal class ValuesIterator<V>(private val nodesIterator: KoneIterator<KoneMutableMapNode<*, V>>) : KoneIterator<V> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): V = nodesIterator.getNext().value
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    internal inner class ValuesView : KoneIterable<Value> {
        override val size: UInt get() = this@KoneMutableListBackedMap.size
        override fun iterator(): KoneIterator<Value> = ValuesIterator(backingList.iterator())
    }
    
    internal class EntriesIterator<K, V>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, V>>) : KoneIterator<KoneMapEntry<K, V>> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): KoneMapEntry<K, V> = nodesIterator.getNext().let { KoneMapEntry(it.key, it.value) }
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    internal inner class EntriesView : KoneIterable<KoneMapEntry<Key, Value>> {
        override val size: UInt get() = this@KoneMutableListBackedMap.size
        override fun iterator(): KoneIterator<KoneMapEntry<Key, Value>> = EntriesIterator(backingList.iterator())
    }
}

public class KoneMutableListBackedReifiedMap<Key, KeyContext: ReifiedEquality<Key>, Value> @PublishedApi internal constructor(
    keyContext: KeyContext,
    backingList: KoneMutableNoddedList<Node<Key, Value>>,
) : KoneMutableListBackedMap<Key, KeyContext, Value>(
    keyContext = keyContext,
    backingList = backingList,
), KoneMutableReifiedMap<Key, Value> {
    override val size: UInt
        get() = backingList.size
    
    override val keysView: KoneReifiedSet<Key> = KeysView()
    override val keys: KoneReifiedSet<Key> get() = keysView.toKoneReifiedSet(elementContext = keyContext)
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? =
        if (key in keyContext) backingList.firstThatOrNull { keyContext { it.key eq key } } else null
    
    internal class KeysIterator<K>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, *>>) : KoneIterator<K> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): K = nodesIterator.getNext().key
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class KeysView : KoneReifiedSet<Key> {
        override val size: UInt get() = this@KoneMutableListBackedReifiedMap.size
        
        override fun contains(element: Key): Boolean =
            element in keyContext && backingList.any { currentNode -> keyContext { element eq currentNode.key } }
        
        override fun iterator(): KoneIterator<Key> = KeysIterator(backingList.iterator())
    }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


public class KoneMutableListBackedMap<K, KC: Equality<K>, V> @PublishedApi internal constructor(
    override val keyContext: KC,
    internal val backingList: KoneMutableNoddedList<Node<K, V>>,
) : KoneMutableMapWithContext<K, KC, V> {
    override val size: UInt
        get() = backingList.size
    
    override val nodesView: KoneSet<KoneMutableMapNode<K, V>> = KoneListBackedSet(absoluteEquality(), backingList)
    override val keysView: KoneSet<K> = KeysView()
    override val valuesView: KoneIterable<V> = ValuesView()
    override val entriesView: KoneIterable<KoneMapEntry<K, V>> = EntriesView()
    
    override fun getNodeOrNull(key: K): KoneMutableMapNode<K, V>? =
        backingList.firstThatOrNull { keyContext { it.key eq key } }
    
    override fun set(key: K, value: V): KoneMutableMapNode<K, V> {
        val index = backingList.firstIndexThat { _, entry -> entry.key == key }
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
    ) : KoneMutableMapNode<K, V>, Disposable {
        private var _backingListNode: KoneMutableListNode<*>? = null
        internal var backingListNode: KoneMutableListNode<*>
            get() = _backingListNode!!
            set(value) { _backingListNode = value }
        
        override fun dispose() {
            _backingListNode = null
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
    
    internal inner class KeysView : KoneSet<K> {
        override val size: UInt get() = this@KoneMutableListBackedMap.size
        
        override fun contains(element: K): Boolean =
            backingList.firstIndexThat { _, currentNode -> keyContext { element eq currentNode.key } } != size
        
        override fun iterator(): KoneIterator<K> = KeysIterator(backingList.iterator())
    }
    
    internal class ValuesIterator<V>(private val nodesIterator: KoneIterator<KoneMutableMapNode<*, V>>) : KoneIterator<V> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): V = nodesIterator.getNext().value
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    internal inner class ValuesView : KoneIterable<V> {
        override fun iterator(): KoneIterator<V> = ValuesIterator(backingList.iterator())
    }
    
    internal class EntriesIterator<K, V>(private val nodesIterator: KoneIterator<KoneMutableMapNode<K, V>>) : KoneIterator<KoneMapEntry<K, V>> {
        override fun hasNext(): Boolean = nodesIterator.hasNext()
        override fun getNext(): KoneMapEntry<K, V> = nodesIterator.getNext().let { KoneMapEntry(it.key, it.value) }
        override fun moveNext() {
            nodesIterator.moveNext()
        }
    }
    
    internal inner class EntriesView : KoneIterable<KoneMapEntry<K, V>> {
        override fun iterator(): KoneIterator<KoneMapEntry<K, V>> = EntriesIterator(backingList.iterator())
    }
}
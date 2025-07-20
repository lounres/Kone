/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.collections.noCorrespondingSetNodeException
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.noPreviousElementInIteratorException
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.neq
import dev.lounres.kone.repeat
import dev.lounres.kone.contexts.invoke


//@Serializable(with = KoneListBackedMutableSetWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public open class KoneListBackedMutableLinkedNoddedSet<Element> @PublishedApi internal constructor(
    public val elementEquality: Equality<Element>,
    internal val backingList: KoneMutableNoddedList<Node<Element>>,
) : KoneMutableLinkedNoddedSet<Element> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: Element): Boolean = backingList.any { elementEquality { it.element eq element } }
    
    override fun nodeOfOrNull(element: Element): KoneMutableLinkedSetNode<Element>? =
        backingList.firstThatOrNull { elementEquality { it.element eq element } }
    override fun nodeOf(element: Element): KoneMutableLinkedSetNode<Element> =
        backingList.firstThatOrNull { elementEquality { it.element eq element } } ?: noCorrespondingSetNodeException()

    override fun add(element: Element) {
        if (backingList.all { elementEquality { it.element neq element } }) {
            val newNode = Node(element)
            val listNode = backingList.addNode(newNode)
            newNode.listNode = listNode
        }
    }
    override fun addNode(element: Element): KoneMutableLinkedSetNode<Element> {
        val node = backingList.firstThatOrNull { elementEquality { it.element eq element } }
        return if (node == null) {
            val newNode = Node(element)
            val listNode = backingList.addNode(newNode)
            newNode.listNode = listNode
            newNode
        } else node
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }

    override fun removeAll() {
        backingList.removeAll()
    }

    override fun remove(element: Element) {
        val index = backingList.firstIndexThat { _, node -> elementEquality { node.element eq element } }
        if (index != backingList.size) backingList.removeAt(index)
    }
    override fun removeAllThat(predicate: (element: Element) -> Boolean) {
        backingList.removeAllThat { predicate(it.element) }
    }
    
    override fun iterator(): KoneMutableLinkedNoddedSetIterator<Element> = Iterator(backingList.iterator())
    
    override val nodesView: KoneReifiedSet<KoneMutableLinkedSetNode<Element>> = NodesView(backingList)

    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('[')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext().element)
        for (element in iterator) {
            append(", ")
            append(element)
        }
        append(']')
    }
    
    internal class Node<Element>(
        override val element: Element,
    ) : KoneMutableLinkedSetNode<Element> {
        internal var _listNode: KoneMutableListNode<Node<Element>>? = null
        var listNode: KoneMutableListNode<Node<Element>>
            get() = _listNode!!
            set(value) { _listNode = value }
        
        override val isDetached: Boolean get() = _listNode == null
        
        override val nextNode: KoneMutableLinkedSetNode<Element>? get() = listNode.nextNode?.element
        override val previousNode: KoneMutableLinkedSetNode<Element>? get() = listNode.previousNode?.element
        
        override fun remove() {
            if (isDetached) return
            listNode.remove()
            _listNode = null
        }
        
        override fun iteratorFromBeforeHere(): KoneMutableLinkedNoddedSetIterator<Element> = Iterator(listNode.iteratorFromBeforeHere())
        override fun iteratorFromAfterHere(): KoneMutableLinkedNoddedSetIterator<Element> = Iterator(listNode.iteratorFromAfterHere())
    }
    
    internal class Iterator<Element>(
        val listIterator: KoneMutableNoddedListIterator<Node<Element>>
    ) : KoneMutableLinkedNoddedSetIterator<Element> {
        override fun hasNext(): Boolean = listIterator.hasNext()
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return listIterator.getNext().element
        }
        override fun getNextNode(): KoneMutableLinkedSetNode<Element> {
            if (!hasNext()) noNextElementInIteratorException()
            return listIterator.getNext()
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            listIterator.moveNext()
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            val node = listIterator.getNext()
            listIterator.removeNext()
            node._listNode = null
        }
        
        override fun hasPrevious(): Boolean = listIterator.hasPrevious()
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return listIterator.getPrevious().element
        }
        override fun getPreviousNode(): KoneMutableLinkedSetNode<Element> {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return listIterator.getPrevious()
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            listIterator.movePrevious()
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            listIterator.removePrevious()
        }
    }
    
    internal class NodesView<Element>(
        val backingList: KoneMutableNoddedList<Node<Element>>
    ) : KoneReifiedSet<KoneMutableLinkedSetNode<Element>> {
        override val size: UInt get() = backingList.size
        override fun contains(element: KoneMutableLinkedSetNode<Element>): Boolean = backingList.any { it === element }
        override fun iterator(): KoneSetIterator<KoneMutableLinkedSetNode<Element>> = backingList.iterator()
    }
    
    public companion object
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneListBackedMutableLinkedNoddedReifiedSet<Element> @PublishedApi internal constructor(
    public val elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    backingList: KoneMutableNoddedList<Node<Element>>,
) : KoneListBackedMutableLinkedNoddedSet<Element>(
    elementEquality = elementEquality,
    backingList = backingList,
), KoneMutableLinkedNoddedReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && super.contains(element)
    
    public companion object
}
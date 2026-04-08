/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.noPreviousElementInIteratorException
import dev.lounres.kone.repeat
import kotlinx.serialization.Serializable
import kotlin.js.JsName


@Serializable(with = KoneGCLinkedMeldableListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneGCLinkedMeldableList<Element> @PublishedApi internal constructor(
    startNode: Node<Element>? = null,
    endNode: Node<Element>? = null,
) : KoneMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        internal set
    
    @PublishedApi
    internal var start: Node<Element>? = startNode
    @PublishedApi
    internal var end: Node<Element>? = endNode
    
    override fun dispose() {
        if (isDisposed) return
        var currentNode = start
        while (currentNode != null) currentNode = currentNode._nextNode.also { currentNode.detach() }
        start = null
        end = null
        isDisposed = true
    }
    
    override val size: UInt
        get() {
            if (isDisposed) disposedInstanceException()
            var counter = 0u
            var currentNode = start
            while (currentNode != null) {
                counter++
                currentNode = currentNode._nextNode
            }
            return counter
        }
    
    internal fun getInternalNode(index: UInt): Node<Element> {
        var currentNode = start!!
        var currentIndex = 0u
        while (currentIndex < index) {
            currentNode = currentNode._nextNode!!
            currentIndex++
        }
        return currentNode
    }
    
    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return getInternalNode(index).element
    }
    
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return getInternalNode(index)
    }
    
    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        getInternalNode(index).element = element
    }
    
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        var currentNode = start
        while (currentNode != null) currentNode = currentNode._nextNode.also { currentNode.detach() }
        start = null
        end = null
    }
    
    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        val newNode = Node(element)
        newNode._previousNode = end
        end?._nextNode = newNode
        end = newNode
        if (size == 0u) start = newNode
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        val newNode = Node(element)
        newNode._previousNode = end
        end?._nextNode = newNode
        end = newNode
        if (size == 0u) start = newNode
        return newNode
    }
    
    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newNode = Node(element)
        if (index == size) {
            newNode._previousNode = end
            end?._nextNode = newNode
            end = newNode
            if (size == 0u) start = newNode
        } else {
            val nextNode = getInternalNode(index)
            val previousNode = nextNode._previousNode
            newNode._nextNode = nextNode
            newNode._previousNode = previousNode
            nextNode._previousNode = newNode
            previousNode?._nextNode = newNode
            if (previousNode == null) start = newNode
        }
    }
    
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newNode = Node(element)
        if (index == size) {
            newNode._previousNode = end
            end?._nextNode = newNode
            end = newNode
            if (size == 0u) start = newNode
        } else {
            val nextNode = getInternalNode(index)
            val previousNode = nextNode._previousNode
            newNode._nextNode = nextNode
            newNode._previousNode = previousNode
            nextNode._previousNode = newNode
            previousNode?._nextNode = newNode
            if (previousNode == null) start = newNode
        }
        return newNode
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        repeat(number) {
            val newNode = Node(builder(it))
            newNode._previousNode = end
            end?._nextNode = newNode
            end = newNode
            if (size == 0u && it == 0u) start = newNode
        }
    }
    
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (index == size) {
            repeat(number) {
                val newNode = Node(builder(it))
                newNode._previousNode = end
                end?._nextNode = newNode
                end = newNode
                if (size == 0u && it == 0u) start = newNode
            }
        } else {
            val nextNode = getInternalNode(index)
            var previousNode = nextNode._previousNode
            repeat(number) {
                val newNode = Node(builder(it))
                newNode._nextNode = nextNode
                newNode._previousNode = previousNode
                nextNode._previousNode = newNode
                previousNode?._nextNode = newNode
                if (previousNode == null) start = newNode
                previousNode = newNode
            }
        }
    }
    
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val nodeToRemove = getInternalNode(index)
        val previousNode = nodeToRemove._previousNode
        val nextNode = nodeToRemove._nextNode
        previousNode?._nextNode = nextNode
        nextNode?._previousNode = previousNode
        if (previousNode == null) start = nextNode
        if (nextNode == null) end = previousNode
        nodeToRemove.detach()
    }
    
    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        var currentNode = start
        var currentIndex = 0u
        var searchingForStart = true
        start = null
        end = null
        while (currentNode != null) {
            if (predicate(currentIndex, currentNode.element)) {
                currentNode._nextNode?._previousNode = currentNode._previousNode
                currentNode._previousNode?._nextNode = currentNode._nextNode
                currentNode = currentNode._nextNode.also { currentNode.detach() }
            } else {
                if (searchingForStart) {
                    start = currentNode
                    searchingForStart = false
                }
                end = currentNode
                currentNode = currentNode._nextNode
            }
            currentIndex++
        }
    }
    
    override fun iterator(): KoneMutableNoddedListIterator<Element> =
        Iterator(
            nextNode = start,
            previousNode = null,
            list = this,
            currentIndex = 0u,
        )
    
    override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> {
        val nextNode = getInternalNode(index)
        return Iterator(
            nextNode = nextNode,
            previousNode = nextNode._previousNode,
            list = this,
            currentIndex = index,
        )
    }
    
    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        var currentNode = start
        if (currentNode != null) {
            append(currentNode.element)
            currentNode = currentNode._nextNode
        }
        while (currentNode != null) {
            append(", ")
            append(currentNode.element)
            currentNode = currentNode._nextNode
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        var currentNode = start
        repeat(size) {
            hashCode = 31 * hashCode + currentNode!!.element.hashCode()
            currentNode = currentNode._nextNode
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        when (other) {
            is KoneGCLinkedMeldableList<*> -> {
                var thisCurrentNode = this.start
                var otherCurrentNode = other.start
                repeat(size) {
                    if (thisCurrentNode!!.element != otherCurrentNode!!.element) return false
                    thisCurrentNode = thisCurrentNode._nextNode
                    otherCurrentNode = otherCurrentNode._nextNode
                }
            }
            else -> {
                var thisCurrentNode = this.start
                val otherIterator = other.iterator()
                repeat(size) {
                    if (thisCurrentNode!!.element != otherIterator.getAndMoveNext()) return false
                    thisCurrentNode = thisCurrentNode._nextNode
                }
            }
        }
        
        return true
    }
    
    public class Node<Element> @PublishedApi internal constructor(
        override var element: Element,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        @PublishedApi
        internal var _nextNode: Node<Element>? = null
        @PublishedApi
        internal var _previousNode: Node<Element>? = null
        
        // Only the start and the end elements hold the reference
        internal var list: KoneGCLinkedMeldableList<Element>? = null
        
        internal fun detach() {
            if (isDetached) return
            _nextNode = null
            _previousNode = null
            list = null
            isDetached = true
        }
        
        override val index: UInt
            get() {
                if (isDetached) detachedNodeException()
                var currentIndex = 0u
                var currentNode: Node<Element> = this
                while (true) {
                    val previousNode = currentNode._previousNode ?: break
                    currentNode = previousNode
                    currentIndex++
                }
                return currentIndex
            }
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            _previousNode?._nextNode = _nextNode
            _nextNode?._previousNode = _previousNode
            if (_previousNode == null) list!!.start = _nextNode?.also { it.list = list }
            if (_nextNode == null) list!!.end = _previousNode?.also { it.list = list }
            _nextNode = null
            _previousNode = null
            list = null
            isDetached = true
        }
        
        override val nextNode: KoneMutableListNode<Element>?
            get() = if (isDetached) detachedNodeException() else _nextNode
        override val previousNode: KoneMutableListNode<Element>?
            get() = if (isDetached) detachedNodeException() else _previousNode
        
        override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element> =
            Iterator(
                nextNode = this,
                previousNode = _previousNode,
                list = list,
                currentIndex = null
            )
        
        override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element> =
            Iterator(
                nextNode = _nextNode,
                previousNode = this,
                list = list,
                currentIndex = null
            )
    }
    
    // Contract: (nextNode != null && previousNode != null) || list != null
    // TODO: Review the iterator!!!
    internal class Iterator<Element>(
        private var nextNode: Node<Element>?,
        private var previousNode: Node<Element>?,
        private var list: KoneGCLinkedMeldableList<Element>?,
        currentIndex: UInt?,
    ): KoneMutableNoddedListIterator<Element> {
        private var _nextIndex: UInt? = currentIndex
        @JsName("nextIndexField")
        val nextIndex: UInt get() = (_nextIndex ?: nextNode?.index ?: previousNode?.index?.let { it + 1u } ?: list?.size)!!.also { _nextIndex = it }

        override fun hasNext(): Boolean =
            if (list?.isDisposed == true || nextNode?.isDetached == true) disposedInstanceException()
            else nextNode != null
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return nextNode!!.element
        }
        override fun getNextNode(): KoneMutableListNode<Element> {
            if (!hasNext()) noNextElementInIteratorException()
            return nextNode!!
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            _nextIndex = _nextIndex?.let { it + 1u }
            nextNode = nextNode!!._nextNode
        }
        override fun nextIndex(): UInt = if (hasNext()) nextIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            nextNode!!.element = element
        }
        override fun addNext(element: Element) {
            if (nextNode == null) {
                val newNode = Node(element)
                newNode.list = list
                val previousNode = list!!.end
                newNode._previousNode = previousNode
                previousNode?._nextNode = newNode
                list!!.end = newNode
                when {
                    previousNode == null -> list!!.start = newNode
                    previousNode._previousNode != null -> previousNode.list = null
                }
                if (previousNode == null) list!!.start = newNode
                nextNode = newNode
            } else {
                val newNode = Node(element)
                val nextNode = nextNode!!
                newNode.list = nextNode.list
                val previousNode = nextNode._previousNode
                newNode._nextNode = nextNode
                newNode._previousNode = previousNode
                nextNode._previousNode = newNode
                previousNode?._nextNode = newNode
                if (previousNode == null) list!!.start = newNode
                this@Iterator.nextNode = newNode
            }
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            val nodeToRemove = nextNode!!
            val nextNode = nodeToRemove._nextNode
            val previousNode = nodeToRemove._previousNode
            nextNode?._previousNode = previousNode
            previousNode?._nextNode = nextNode
            nodeToRemove.detach()
            if (previousNode == null) list!!.start = nextNode
            if (nextNode == null) list!!.end = previousNode
            this.nextNode = nextNode
        }

        override fun hasPrevious(): Boolean =
            when {
                list?.isDisposed == true -> disposedInstanceException()
                nextNode != null -> nextNode!!._previousNode != null
                else -> list!!.end != null
            }
        override fun getPrevious(): Element = when {
            !hasPrevious() -> noPreviousElementInIteratorException()
            nextNode != null -> nextNode!!._previousNode!!.element
            else -> list!!.end!!.element
        }
        override fun getPreviousNode(): KoneMutableListNode<Element> = when {
            !hasPrevious() -> noPreviousElementInIteratorException()
            nextNode != null -> nextNode!!._previousNode!!
            else -> list!!.end!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            _nextIndex = _nextIndex?.let { it - 1u }
            nextNode = if (nextNode != null) nextNode!!._previousNode else list!!.end
        }
        override fun previousIndex(): UInt = if (!hasPrevious()) noPreviousElementInIteratorException() else nextIndex - 1u
        override fun setPrevious(element: Element) {
            when {
                !hasPrevious() -> noPreviousElementInIteratorException()
                nextNode != null -> nextNode!!._previousNode!!
                else -> list!!.end!!
            }.element = element
        }
        override fun addPrevious(element: Element) {
            val newNode = Node(element)
            if (nextNode == null) {
                newNode._previousNode = list!!.end
                list!!.end?._nextNode = newNode
                list!!.end = newNode
                if (list!!.start == null) list!!.start = newNode
            } else {
                val previousNode = nextNode!!._previousNode
                newNode._previousNode = previousNode
                newNode._nextNode = nextNode
                nextNode!!._previousNode = newNode
                previousNode?._nextNode = newNode
                if (list!!.start == nextNode) list!!.start = newNode
            }
            _nextIndex = _nextIndex?.let { it + 1u }
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            val nodeToRemove = if (nextNode == null) list!!.end!! else nextNode!!._previousNode!!
            val previousNode = nodeToRemove._previousNode
            val nextNode = nodeToRemove._nextNode
            previousNode?._nextNode = nextNode
            nextNode?._previousNode = previousNode
            nodeToRemove.detach()
            if (previousNode == null) list!!.start = nextNode
            if (nextNode == null) list!!.end = previousNode
            _nextIndex = _nextIndex?.let { it - 1u }
        }
    }
    
    public companion object
}
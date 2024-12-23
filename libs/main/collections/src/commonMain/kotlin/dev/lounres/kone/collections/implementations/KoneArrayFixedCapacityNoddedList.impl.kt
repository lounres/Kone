/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.KoneMutableListNode
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneMutableNoddedListIterator
import dev.lounres.kone.collections.capacityOverflowException
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.noPreviousElementInIteratorException
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


/**
 * Represents a nodded list that is laid out consecutively on a prefix of array of fixed capacity.
 *
 * # Implementation details
 *
 * This implementation holds a [KoneMutableArray] of provided capacity that stores nodes with elements in it
 * and proxies all operations straight to its prefix of the provided [size].
 * Any getting or setting is operated on corresponding indices of the array.
 * Any addition or removal is operated on corresponding indices of the array
 * moving values with greater indices.
 *
 * That's why it has perfect access time complexity
 * while having bad mutability time complexity.
 *
 * ## Time complexity of operations
 *
 * | Operation                                                                 | Worst case                                  | Average                                     |
 * |---------------------------------------------------------------------------|---------------------------------------------|---------------------------------------------|
 * | [size]                                                                    | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [get]                                                                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [set]                                                                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [add]                                                                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [addAt]                                                                   | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [addSeveral]                                                              | \(\Theta(\mathrm{number})\)                 | \(\Theta(\mathrm{number})\)                 |
 * | [addSeveralAt]                                                            | \(\Theta(\mathrm{size} + \mathrm{number})\) | \(\Theta(\mathrm{size} + \mathrm{number})\) |
 * | [removeAt]                                                                | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThat]                                                           | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThatIndexed]                                                    | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAll]                                                               | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator]                                                                | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iteratorFrom]                                                            | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasNext][KoneMutableLinearIterator.hasNext]                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasPrevious][KoneMutableLinearIterator.hasPrevious]             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getNext][KoneMutableLinearIterator.getNext]                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getPrevious][KoneMutableLinearIterator.getPrevious]             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.moveNext][KoneMutableLinearIterator.moveNext]                   | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.movePrevious][KoneMutableLinearIterator.movePrevious]           | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setNext][KoneMutableLinearIterator.setNext]                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setPrevious][KoneMutableLinearIterator.setPrevious]             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addNext][KoneMutableLinearIterator.addNext]                     | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.addPrevious][KoneMutableLinearIterator.addPrevious]             | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.removeNext][KoneMutableLinearIterator.removeNext]               | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.removePrevious][KoneMutableLinearIterator.removePrevious]       | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.nextIndex][KoneMutableLinearIterator.nextIndex]                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.previousIndex][KoneMutableLinearIterator.previousIndex]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.element][KoneMutableListNode.element]                               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.remove][KoneMutableListNode.remove]                                 | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [node.index][KoneMutableListNode.index]                                   | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.nextNode][KoneMutableListNode.nextNode]                             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.previousNode][KoneMutableListNode.previousNode]                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.iteratorFromBeforeHere][KoneMutableListNode.iteratorFromBeforeHere] | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.iteratorFromAfterHere][KoneMutableListNode.iteratorFromAfterHere]   | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 *
 * @usesMathJax
 */
//@Serializable(with = KoneFixedCapacityArrayListWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayFixedCapacityNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    data: KoneMutableArray<Node<Element>?>,
): KoneMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    init {
        repeat(size) { data[it]!!.list = this }
    }
    
    private var _data: KoneMutableArray<Node<Element>?>? = data
    internal val data: KoneMutableArray<Node<Element>?>
        get() = if (isDisposed) disposedInstanceException() else _data!!

    override fun dispose() {
        if (isDisposed) return
        repeat(size) {
            data[it]!!.detach()
            data[it] = null
        }
        _data = null
        isDisposed = true
    }
    
    private val capacity: UInt get() = data.size
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set
    
    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index]!!.element
    }
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index]!!
    }

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index]!!.element = element
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == capacity) capacityOverflowException(capacity)
        data[size] = Node(this, element, size)
        size++
    }
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (size == capacity) capacityOverflowException(capacity)
        val newNode = Node(this, element, size)
        data[size] = newNode
        size++
        return newNode
    }
    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (size == capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + 1u] = data[i].also { it!!.index = i + 1u }
        data[index] = Node(this, element, index)
        size++
    }
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (size == capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + 1u] = data[i].also { it!!.index = i + 1u }
        val newNode = Node(this, element, index)
        data[index] = newNode
        size++
        return newNode
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        repeat(number) { data[size + it] = Node(this, builder(it), size + it) }
        size = newSize
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i].also { it!!.index = i + number }
        repeat(number) { data[index + it] = Node(this, builder(it), index + it) }
        size = newSize
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index]!!.detach()
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u].also { it!!.index = i }
        data[size - 1u] = null
        size = newSize
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        val newSize: UInt
        scope {
            var checkingMark = 0u
            var resultMark = 0u
            while (checkingMark < size) {
                if (!predicate(checkingMark, data[checkingMark]!!.element)) {
                    data[resultMark] = data[checkingMark]
                    resultMark++
                } else {
                    data[checkingMark]!!.detach()
                }
                checkingMark++
            }
            newSize = resultMark
        }
        for (i in newSize ..< size) data[i] = null
        size = newSize
    }

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        repeat(size) {
            data[it]!!.detach()
            data[it] = null
        }
        size = 0u
    }
    
    public override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }
    override fun iterator(): KoneMutableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException() else Iterator(this, 0u)

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[0u]!!.element)
        for (i in 1u..<size) {
            append(", ")
            append(data[i]!!.element)
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + data[it]!!.element.hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayFixedCapacityNoddedList<*> ->
                repeat(size) {
                    if (this.data[it]!!.element != other.data[it]!!.element) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i]!!.element != otherIterator.getAndMoveNext()) return false
                }
            }
        }

        return true
    }

    @PublishedApi
    internal class Node<Element>(
        override var element: Element,
        index: UInt,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
        
        private var _list: KoneArrayFixedCapacityNoddedList<Element>? = null
        internal var list: KoneArrayFixedCapacityNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        override var index: UInt = index
            get() = if (isDetached) detachedNodeException() else field
            
        internal fun detach() {
            _list = null
            isDetached = true
        }
        
        constructor(list: KoneArrayFixedCapacityNoddedList<Element>, element: Element, index: UInt) : this(element, index) {
            this.list = list
        }

        override fun remove() {
            if (isDetached) detachedNodeException()
            list.removeAt(index)
            isDetached = true
        }

        override val nextNode: KoneMutableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                index + 1u < list.size -> list.data[index + 1u]!!
                else -> null
            }
        override val previousNode: KoneMutableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                index > 0u -> list.data[index - 1u]!!
                else -> null
            }

        override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index + 1u)
        override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index)
    }

    internal class Iterator<Element>(
        val list: KoneArrayFixedCapacityNoddedList<Element>,
        var currentIndex: UInt,
    ): KoneMutableNoddedListIterator<Element> {
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex]!!.element
        }
        override fun getNextNode(): KoneMutableListNode<Element> {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex]!!
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[currentIndex]!!.element = element
        }
        override fun addNext(element: Element) {
            list.addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            list.removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u]!!.element
        }
        override fun getPreviousNode(): KoneMutableListNode<Element> {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u]!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[currentIndex - 1u]!!.element = element
        }
        override fun addPrevious(element: Element) {
            list.addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.removeAt(--currentIndex)
        }
    }
}
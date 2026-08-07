/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneGrowableMutableNoddedList
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


@Serializable(with = KoneArrayGrowableNoddedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayGrowableNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    internal var sizeUpperBound: UInt = powerOf2ArraySizeGreaterOrEqualTo(size),
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray.generate(sizeUpperBound) { null },
) : KoneGrowableMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    init {
        repeat(size) { data[it]!!.list = this }
    }
    
    private var _data: KoneMutableArray<Node<Element>?>? = data
    internal var data: KoneMutableArray<Node<Element>?>
        get() = if (isDisposed) disposedInstanceException() else _data!!
        set(value) { _data = value }
    
    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        repeat(size) { this[it] = null }
    }
    override fun dispose() {
        if (isDisposed) return
        repeat(size) {
            data[it]!!.detach()
            data[it] = null
        }
        _data = null
        isDisposed = true
    }
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set
    
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
        while (newSize > sizeUpperBound) {
            sizeUpperBound = if (sizeUpperBound == 0u) 1u else sizeUpperBound shl 1
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        val oldData = data
        data = KoneMutableArray.generate(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
    }

    override fun ensureCapacity(minimalCapacity: UInt) {
        if (isDisposed) disposedInstanceException()
        if (sizeUpperBound < minimalCapacity) {
            reinitializeBounds(minimalCapacity)
            reinitializeData {
                when {
                    it < size -> get(it)
                    else -> null
                }
            }
        }
    }

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

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        reinitializeBoundsAndData(0u) { null }
    }
    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        val newNode = Node(this, element, size)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(it)
                    it == oldSize -> newNode
                    else -> null
                }
            }
        } else {
            data[size] = newNode
            size++
        }
    }
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        val newNode = Node(this, element, size)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(it)
                    it == oldSize -> newNode
                    else -> null
                }
            }
        } else {
            data[size] = newNode
            size++
        }
        return newNode
    }
    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newNode = Node(this, element, index)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> newNode
                    it <= oldSize -> get(it-1u).also { node -> node!!.index = it }
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i].also { it!!.index = i + 1u }
            data[index] = newNode
            size++
        }
    }
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newNode = Node(this, element, index)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> newNode
                    it <= oldSize -> get(it-1u).also { node -> node!!.index = it }
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i].also { it!!.index = i + 1u }
            data[index] = newNode
            size++
        }
        return newNode
    }
    
    @DelicateSeveralElementsInserterAPI
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    it < index + number -> null
                    it < newSize -> get(it - number).also { node -> node!!.index = it }
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i].also { it!!.index = i + number }
            size = newSize
        }
        return SeveralElementsInserter(this, index, number)
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u].also { if (it != null) it.index = i }
        data[size - 1u] = null
        size = newSize
    }
    
    @DelicateBulkElementsRemoverAPI
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> {
        if (isDisposed) disposedInstanceException()
        return BulkElementsRemover(this)
    }

    override fun iterator(): KoneMutableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException() else Iterator(this, 0u)
    public override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[0u]!!.element)
        for (i in 1u..<size) {
            append(", ")
            append(data[i]!!.element)
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + data[it]!!.element.hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        if (other is KoneArrayGrowableNoddedList<*>) {
            repeat(size) {
                if (this.data[it]!!.element != other.data[it]!!.element) return false
            }
        }else {
            val otherIterator = other.iterator()
            for (i in 0u ..< size) {
                if (this.data[i]!!.element != otherIterator.getAndMoveNext()) return false
            }
        }

        return true
    }
    
    @PublishedApi
    internal class Node<Element>(
        override var element: Element,
        override var index: UInt,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
        
        private var _list: KoneArrayGrowableNoddedList<Element>? = null
        var list: KoneArrayGrowableNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        constructor(list: KoneArrayGrowableNoddedList<Element>, element: Element, index: UInt) : this(element, index) {
            _list = list
        }
        
        fun detach() {
            if (isDetached) return
            _list = null
            isDetached = true
        }
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            list.removeAt(index)
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
        
        override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index)
        override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index + 1u)
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[$element]"
    }

    internal class Iterator<Element>(
        val list: KoneArrayGrowableNoddedList<Element>,
        var currentIndex: UInt = 0u
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
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
    
    internal class SeveralElementsInserter<Element>(
        val list: KoneArrayGrowableNoddedList<Element>,
        val newElementsStartIndex: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        var currentIndex: UInt = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsStartIndex) severalElementsInserterOverflowException()
            list.data[newElementsStartIndex + currentIndex] = Node(list, element, newElementsStartIndex + currentIndex)
        }
        
        override fun close() {
            if (currentIndex != newElementsStartIndex) severalElementsInserterElementsLackException()
        }
    }
    
    internal class BulkElementsRemover<Element>(
        val list: KoneArrayGrowableNoddedList<Element>,
    ) : KoneBulkElementsRemover<Element> {
        var checkingMark = 0u
        var resultMark = 0u
        
        override fun hasNext(): Boolean = checkingMark < list.size
        
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return list.data[checkingMark]!!.element
        }
        
        override fun nextIndex(): UInt {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return checkingMark
        }
        
        override fun moveNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            list.data[resultMark] = list.data[checkingMark].also { it!!.index = resultMark }
            resultMark++
            checkingMark++
        }
        
        override fun removeNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            checkingMark++
        }
        
        override fun close() {
            for (i in resultMark ..< list.size) list.data[i] = null
            list.size = resultMark
        }
    }
}
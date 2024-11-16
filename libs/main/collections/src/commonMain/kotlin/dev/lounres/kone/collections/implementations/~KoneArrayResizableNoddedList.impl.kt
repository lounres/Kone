/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlin.math.max


//@Serializable(with = KoneResizableArrayListWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayResizableNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray<Node<Element>?>(sizeUpperBound) { null },
) : KoneMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    init {
        repeat(size) { data[it]!!.list = this }
    }
    
    private var _data: KoneMutableArray<Node<Element>?>? = data
    private var data: KoneMutableArray<Node<Element>?>
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
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableArrayList implementation can not allocate array of size more than 2^31")
        when {
            newSize > sizeUpperBound -> {
                while (newSize > sizeUpperBound) {
                    dataSizeNumber++
                    sizeLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
            }
            newSize < sizeLowerBound -> {
                while (newSize < sizeLowerBound && dataSizeNumber >= 2u) {
                    dataSizeNumber--
                    sizeLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
            }
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
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
        dataSizeNumber = 1u
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        reinitializeData { null }
        size = 0u
    }
    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(it)
                    it == oldSize -> Node(this@KoneArrayResizableNoddedList, element, it)
                    else -> null
                }
            }
        } else {
            data[size] = Node(this@KoneArrayResizableNoddedList, element, size)
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
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> Node(this@KoneArrayResizableNoddedList, element, it)
                    it <= oldSize -> get(it-1u)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = Node(this@KoneArrayResizableNoddedList, element, size)
            size++
        }
    }
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newNode = Node(this, element, size)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> newNode
                    it <= oldSize -> get(it-1u)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = newNode
            size++
        }
        return newNode
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(newSize) {
                when {
                    it < oldSize -> get(it)
                    it < oldSize + number -> Node(this@KoneArrayResizableNoddedList, builder(it - oldSize), it)
                    else -> null
                }
            }
        } else {
            for (localIndex in 0u ..< number) data[localIndex + size] = Node(this, builder(localIndex), localIndex + size)
            size = newSize
        }
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    it < index + number -> Node(this@KoneArrayResizableNoddedList, builder(it - index), it)
                    it < newSize -> get(it - number)
                    else -> null
                }
            }
        } else {
            for (i in (size-1u) downTo index) data[i + number] = data[i]
            repeat(number) { data[index + it] = Node(this, builder(it), index + it) }
            size = newSize
        }
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    it < newSize -> get(it+1u)
                    else -> null
                }
            }
        } else {
            for (i in index..<newSize) data[i] = data[i + 1u]
            data[size - 1u] = null
            size = newSize
        }
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
                }
                checkingMark++
            }
            newSize = checkingMark
        }
        if (newSize < sizeLowerBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(it)
                    else -> null
                }
            }
        } else {
            for (i in newSize ..< size) data[i] = null
            size = newSize
        }
    }
    
    override fun iterator(): KoneMutableLinearIterator<Element> =
        if (isDisposed) disposedInstanceException() else Iterator(this, 0u)
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayResizableNoddedList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
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
        
        private var _list: KoneArrayResizableNoddedList<Element>? = null
        var list: KoneArrayResizableNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        constructor(list: KoneArrayResizableNoddedList<Element>, element: Element, index: UInt) : this(element, index) {
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
        
        override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index)
        override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element> =
            if (isDetached) detachedNodeException() else list.iteratorFrom(index + 1u)
    }

    internal class Iterator<Element>(
        val list: KoneArrayResizableNoddedList<Element>,
        var currentIndex: UInt = 0u
    ): KoneMutableLinearIterator<Element> {
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex]!!.element
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
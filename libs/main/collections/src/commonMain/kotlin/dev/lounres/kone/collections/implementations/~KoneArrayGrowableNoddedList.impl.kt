/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


//@Serializable(with = KoneGrowableArrayListWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneGrowableArrayNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var sizeUpperBound: UInt = powerOf2GreaterOrEqualTo(size),
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray(sizeUpperBound) { null },
) : KoneGrowableMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Node<Element>?>? = data
    private var data: KoneMutableArray<Node<Element>?>
        get() = if (isDisposed) disposedInstanceException() else _data!!
        set(value) { _data = value }
    
    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        repeat(size) { this[it] = null }
    }
    override fun dispose() {
        data.dispose(size)
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
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
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
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(it)
                    it == size -> newNode
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
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(it)
                    it == size -> newNode
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
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> newNode
                    it <= size -> get(it-1u).also { node -> node!!.index = it }
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
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> newNode
                    it <= size -> get(it-1u).also { node -> node!!.index = it }
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
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(it)
                    it < size + number -> Node(this@KoneGrowableArrayNoddedList, builder(it - size), it)
                    else -> null
                }
            }
        } else {
            repeat(number) {
                data[size + it] = Node(this, builder(it), size + it)
            }
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
                    it < index + number -> Node(this@KoneGrowableArrayNoddedList, builder(it - index), it)
                    it < newSize -> get(it - number).also { node -> node!!.index = it }
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i].also { it!!.index = i + number }
            repeat(number) {
                data[index + it] = Node(this, builder(it), index + it)
            }
            size = newSize
        }
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
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
                }
                checkingMark++
            }
            newSize = resultMark
        }
        for (i in newSize ..< size) data[i] = null
        size = newSize
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
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + data[it].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneGrowableArrayNoddedList<*> ->
                repeat(size) {
                    if (this.data[it] != other.data[it]) return false
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
        
        private var _list: KoneGrowableArrayNoddedList<Element>? = list
        val list: KoneGrowableArrayNoddedList<Element> get() = _list!!
        
        constructor(
            list: KoneGrowableArrayNoddedList<Element>,
            element: Element,
            index: UInt,
        ) : this(element, index) {
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
        val list: KoneGrowableArrayNoddedList<Element>,
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
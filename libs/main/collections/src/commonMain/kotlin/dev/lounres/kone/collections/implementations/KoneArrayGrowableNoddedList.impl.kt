/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneGrowableArrayListWithContextSerializer::class)
public class KoneGrowableArrayNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var sizeUpperBound: UInt = powerOf2GreaterOrEqualTo(size),
    private var data: KoneMutableArray<Node<Element>?> = KoneMutableArray(sizeUpperBound) { null },
) : KoneMutableList<Element>, /*KoneCollectionWithGrowableCapacity<E>,*/ Disposable {
    override var size: UInt = size
        private set

    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        repeat(size) { this[it] = null }
    }
    override fun dispose() {
        data.dispose(size)
    }
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

    // TODO: Apply corresponding interface and enable capacity growing
//    override fun ensureCapacity(minimalCapacity: UInt) {
//        if (sizeUpperBound < minimalCapacity) {
//            reinitializeBounds(minimalCapacity)
//            reinitializeData {
//                when {
//                    it < size -> get(it)
//                    else -> null
//                }
//            }
//        }
//    }

    override fun get(index: UInt): Element {
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index]!!.element = element
    }

    override fun removeAll() {
        reinitializeBoundsAndData(0u) { null }
    }
    override fun add(element: Element) {
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(it)
                    it == size -> Node(this@KoneGrowableArrayNoddedList, element, it)
                    else -> null
                }
            }
        } else {
            data[size] = Node(this, element, size)
            size++
        }
    }
    override fun addAt(index: UInt, element: Element) {
        if (index > size) indexOutOfBoundsException(index, size)
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> Node(this@KoneGrowableArrayNoddedList, element, it)
                    it <= size -> get(it-1u)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = Node(this, element, index)
            size++
        }
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(it)
                    localIndex < number -> Node(this@KoneGrowableArrayNoddedList, builder(localIndex), localIndex + size).also { localIndex++ }
                    else -> null
                }
            }
        } else {
            var index = size
            repeat(number) {
                data[index] = Node(this, builder(it), index)
                index++
            }
            size = newSize
        }
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    localIndex < number -> Node(this@KoneGrowableArrayNoddedList, builder(localIndex), localIndex + index).also { localIndex++ }
                    it < newSize -> get(it - number)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i]
            var index = index
            repeat(number) {
                data[index] = Node(this, builder(it), index)
                index++
            }
            size = newSize
        }
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        val newSize: UInt
        scope {
            var checkingMark = 0u
            var resultMark = 0u
            while (checkingMark < size) {
                if (!predicate(checkingMark, data[checkingMark] as Element)) {
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

    override fun iterator(): KoneMutableLinearIterator<Element> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> = Iterator(index)

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
    
    internal class Node<Element>(
        list: KoneGrowableArrayNoddedList<Element>,
        override var element: Element,
        override var index: UInt,
    ) : KoneMutableListNode<Element>, Disposable {
        private var _list: KoneGrowableArrayNoddedList<Element>? = list
        val list: KoneGrowableArrayNoddedList<Element> get() = _list!!
        
        override fun dispose() {
            _list = null
        }
        
        override fun remove() {
            list.removeAt(index)
        }
        
        override val nextNode: KoneMutableListNode<Element>?
            get() = if (index + 1u < list.size) list.data[index + 1u] else null
        override val previousNode: KoneMutableListNode<Element>?
            get() = if (index > 0u) list.data[index - 1u] else null
        
        override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element> = list.iteratorFrom(index)
        override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element> = list.iteratorFrom(index + 1u)
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<Element> {
        init {
            if (currentIndex > size) indexOutOfBoundsException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            data[currentIndex]!!.element = element
        }
        override fun addNext(element: Element) {
            addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            data[currentIndex - 1u]!!.element = element
        }
        override fun addPrevious(element: Element) {
            addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            removeAt(--currentIndex)
        }
    }
}
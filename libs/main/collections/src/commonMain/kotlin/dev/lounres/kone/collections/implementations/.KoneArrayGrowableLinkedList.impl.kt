/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneGrowableLinkedArrayListWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayGrowableLinkedList<Element> internal constructor(
    size: UInt,
    private var sizeUpperBound: UInt = powerOf2GreaterOrEqualTo(size),
    data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneGrowableMutableList<Element>, KoneDequeue<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    private var data: KoneMutableArray<Any?>
        get() = _data!!
        set(value) { _data = value }
    private var _nextNodeIndex: KoneMutableUIntArray? = nextNodeIndex
    private var nextNodeIndex: KoneMutableUIntArray
        get() = _nextNodeIndex!!
        set(value) { _nextNodeIndex = value }
    private var _previousNodeIndex: KoneMutableUIntArray? = previousNodeIndex
    private var previousNodeIndex: KoneMutableUIntArray
        get() = _previousNodeIndex!!
        set(value) { _previousNodeIndex = value }

    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        repeat(size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextNodeIndex[currentActualIndexToClear]
        }
    }
    override fun dispose() {
        if (isDisposed) return
        data.dispose(size)
        _data = null
        _nextNodeIndex = null
        _previousNodeIndex = null
        isDisposed = true
    }
    
    override var size: UInt = size
        private set
    
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneGrowableArrayList implementation can not allocate array of size more than 2^31")
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                sizeUpperBound = if (sizeUpperBound == 0u) 1u else sizeUpperBound shl 1
            }
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextNodeIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousNodeIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = if (size > 0u) size - 1u else sizeUpperBound - 1u
    }

    override fun ensureCapacity(minimalCapacity: UInt) {
        if (isDisposed) disposedInstanceException()
        if (sizeUpperBound < minimalCapacity) {
            reinitializeBounds(minimalCapacity)
            var actualIndex = start
            reinitializeData {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    else -> null
                }
            }
        }
    }

    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextNodeIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = start
                repeat(index) {
                    currentIndex = nextNodeIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = end
                for (i in index + 1u ..< size) {
                    currentIndex = previousNodeIndex[currentIndex]
                }
                currentIndex
            }
        }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
        for (index in 0u ..< newElementsNumber) {
            end = nextNodeIndex[end]
            data[end] = generator(index)
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: Element) {
        justAddAfterTheEnd(1u) { element }
    }
    private fun justAddBefore(actualIndex: UInt, element: Element) {
        val freeIndex = nextNodeIndex[end]
        val indexAfterTheFreeIndex = nextNodeIndex[freeIndex]
        nextNodeIndex[end] = indexAfterTheFreeIndex
        previousNodeIndex[indexAfterTheFreeIndex] = end

        val indexBeforeTheActualIndex = previousNodeIndex[actualIndex]
        nextNodeIndex[freeIndex] = actualIndex
        previousNodeIndex[freeIndex] = indexBeforeTheActualIndex
        nextNodeIndex[indexBeforeTheActualIndex] = freeIndex
        previousNodeIndex[actualIndex] = freeIndex

        if (actualIndex == start) start = freeIndex

        data[freeIndex] = element

        size++
    }
    private fun justRemoveAt(actualIndex: UInt) {
        data[actualIndex] = null
        val prev = previousNodeIndex[actualIndex]
        val next = nextNodeIndex[actualIndex]
        nextNodeIndex[prev] = next
        previousNodeIndex[next] = prev
        if (start == actualIndex) start = next
        if (end == actualIndex) end = prev
        size--

        val afterEnd = nextNodeIndex[end]
        nextNodeIndex[end] = actualIndex
        previousNodeIndex[afterEnd] = actualIndex
        nextNodeIndex[actualIndex] = afterEnd
        previousNodeIndex[actualIndex] = end
        if (size == 0u) start = actualIndex
    }

    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[actualIndex(index)] as Element
    }

    override fun getFirst(): Element =
        when {
            isDisposed -> disposedInstanceException()
            isEmpty() -> indexOutOfBoundsException(0u, size)
            else -> data[start] as Element
        }

    override fun getLast(): Element =
        when {
            isDisposed -> disposedInstanceException()
            isEmpty() -> indexOutOfBoundsException(size, size)
            else -> data[end] as Element
        }

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[actualIndex(index)] = element
    }
    
    // TODO: Actually, it's not O(size) but O(capacity)
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(element)
        }
    }

    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        when {
            size == sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        it == index -> element
                        it <= size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }

    override fun addFirst(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it == 0u -> element
                    it <= size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justAddBefore(start, element)
        }
    }

    override fun addLast(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(element)
        }
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    localIndex < number -> builder(localIndex++)
                    else -> null
                }
            }
        } else {
            var localIndex = 0u
            justAddAfterTheEnd(number) { builder(localIndex++) }
        }
    }

    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                var localIndex = 0u
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        localIndex < number -> builder(localIndex++)
                        it < newSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                var localIndex = 0u
                justAddAfterTheEnd(number) { builder(localIndex++) }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualLeftPartIndex = previousNodeIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextNodeIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    repeat(number) {
                        currentActualIndex = nextNodeIndex[currentActualIndex]
                        data[currentActualIndex] = builder(it)
                    }
                    actualInnerPartRightEndIndex = currentActualIndex
                }

                nextNodeIndex[end] = nextNodeIndex[actualInnerPartRightEndIndex]
                previousNodeIndex[nextNodeIndex[actualInnerPartRightEndIndex]] = end
                nextNodeIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
                previousNodeIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
                nextNodeIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
                previousNodeIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
            }
        }
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        justRemoveAt(actualIndex(index))
    }

    override fun removeFirst() {
        if (isDisposed) disposedInstanceException()
        justRemoveAt(start)
    }

    override fun removeLast() {
        if (isDisposed) disposedInstanceException()
        justRemoveAt(end)
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        val newSize: UInt
        val firstCellToClear: UInt
        scope {
            var checkingActualMark = 0u
            var checkingIndex = 0u
            var resultActualMark = 0u
            var resultSize = 0u
            while (checkingIndex < size) {
                if (!predicate(checkingIndex, data[checkingActualMark] as Element)) {
                    data[resultActualMark] = data[checkingActualMark]
                    resultActualMark = nextNodeIndex[resultActualMark]
                    resultSize++
                }
                checkingActualMark = nextNodeIndex[checkingActualMark]
                checkingIndex++
            }
            newSize = resultSize
            firstCellToClear = resultActualMark
        }
        var currentActualIndexToClear = firstCellToClear
        repeat(size - newSize) {
            data[currentActualIndexToClear] = null
            currentActualIndexToClear = nextNodeIndex[currentActualIndexToClear]
        }
    }

    override fun iterator(): KoneMutableLinearIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator(this, 0u)
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[start])
        var currentActualIndex = start
        for (i in 1u..<size) {
            currentActualIndex = nextNodeIndex[currentActualIndex]
            append(", ")
            append(data[currentActualIndex])
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        var currentActualIndex = start
        repeat(size) {
            hashCode = 31 * hashCode + data[currentActualIndex].hashCode()
            currentActualIndex = nextNodeIndex[currentActualIndex]
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayGrowableLinkedList<*> -> {
                var thisCurrentIndex = this.start
                var otherCurrentIndex = other.start
                repeat(size) {
                    if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
                    thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                    otherCurrentIndex = other.nextNodeIndex[otherCurrentIndex]
                }
            }
            else -> {
                var thisCurrentIndex = this.start
                val otherIterator = other.iterator()
                repeat(size) {
                    if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
                    thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                }
            }
        }

        return true
    }

    internal class Iterator<Element>(
        val list: KoneArrayGrowableLinkedList<Element>,
        var currentIndex: UInt = 0u,
    ): KoneMutableLinearIterator<Element> {
        var actualCurrentIndex = if (list.sizeUpperBound == 0u) 0u else list.actualIndex(currentIndex)
        
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[actualCurrentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
            actualCurrentIndex = list.nextNodeIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[currentIndex] = element
        }
        override fun addNext(element: Element) {
            if (currentIndex == list.size) list.justAddAfterTheEnd(element)
            else list.justAddBefore(list.nextNodeIndex[actualCurrentIndex], element)
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            list.justRemoveAt(actualCurrentIndex.also { actualCurrentIndex = list.nextNodeIndex[actualCurrentIndex] })
        }

        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[list.previousNodeIndex[actualCurrentIndex]] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
            actualCurrentIndex = list.previousNodeIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[list.previousNodeIndex[actualCurrentIndex]] = element
        }
        override fun addPrevious(element: Element) {
            list.justAddBefore(actualCurrentIndex, element)
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.justRemoveAt(list.previousNodeIndex[actualCurrentIndex])
        }
    }
}
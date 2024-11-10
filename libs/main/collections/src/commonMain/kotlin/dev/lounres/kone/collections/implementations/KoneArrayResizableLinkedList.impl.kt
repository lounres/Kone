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


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneResizableLinkedArrayListWithContextSerializer::class)
public class KoneArrayResizableLinkedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneMutableList<Element>, KoneDequeue<Element>, Disposable {
    override var size: UInt = size
        private set

    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableLinkedArrayList implementation can not allocate array of size more than 2^31")
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
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = (if (size > 0u) size - 1u else sizeUpperBound - 1u)
    }

    override fun dispose() {
        data.dispose(size)
    }

    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextCellIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = start
                repeat(index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = end
                for (i in index ..< size-1u) {
                    currentIndex = previousCellIndex[currentIndex]
                }
                currentIndex
            }
        }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {

        for (index in 0u ..< newElementsNumber) {
            end = nextCellIndex[end]
            data[end] = generator(index)
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: Element) {
        end = nextCellIndex[end]
        data[end] = element
        size += 1u
    }
    private fun justAddBefore(actualIndex: UInt, element: Element) {
        val freeIndex = nextCellIndex[end]
        val indexAfterTheFreeIndex = nextCellIndex[freeIndex]
        nextCellIndex[end] = indexAfterTheFreeIndex
        previousCellIndex[indexAfterTheFreeIndex] = end

        val indexBeforeTheActualIndex = previousCellIndex[actualIndex]
        nextCellIndex[freeIndex] = actualIndex
        previousCellIndex[freeIndex] = indexBeforeTheActualIndex
        nextCellIndex[indexBeforeTheActualIndex] = freeIndex
        previousCellIndex[actualIndex] = freeIndex

        if (actualIndex == start) start = freeIndex

        data[freeIndex] = element

        size++
    }
    private fun justRemoveAt(actualIndex: UInt) {
        data[actualIndex] = null
        val prev = previousCellIndex[actualIndex]
        val next = nextCellIndex[actualIndex]
        nextCellIndex[prev] = next
        previousCellIndex[next] = prev
        if (start == actualIndex) start = next
        if (end == actualIndex) end = prev
        size--

        val afterEnd = nextCellIndex[end]
        nextCellIndex[end] = actualIndex
        previousCellIndex[afterEnd] = actualIndex
        nextCellIndex[actualIndex] = afterEnd
        previousCellIndex[actualIndex] = end
        if (size == 0u) start = actualIndex
    }

    override fun get(index: UInt): Element {
        if (index >= size) indexOutOfBoundsException(index, size)
        val result = data[actualIndex(index)] as Element
        return result
    }

    override fun getFirst(): Element {
        if (isEmpty()) indexOutOfBoundsException(0u, size) // TODO: Maybe replace with another error
        return data[start] as Element
    }

    override fun getLast(): Element {
        if (isEmpty()) indexOutOfBoundsException(size, size) // TODO: Maybe replace with another error
        return data[end] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (index >= size) indexOutOfBoundsException(index, size)
        data[actualIndex(index)] = element
    }

    override fun removeAll() {
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: Element) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }

    override fun addFirst(element: Element) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it == 0u -> element
                    it <= size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else justAddBefore(start, element)
    }

    override fun addLast(element: Element) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }

    override fun addAt(index: UInt, element: Element) {
        if (index > size) indexOutOfBoundsException(index, size)
        when {
            size == sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        it == index -> element
                        it <= size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }

    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
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
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                var localIndex = 0u
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        localIndex < number -> builder(localIndex++)
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
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
                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextCellIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    for (localIndex in 0u ..< number) {
                        currentActualIndex = nextCellIndex[currentActualIndex]
                        data[currentActualIndex] = builder(localIndex)
                    }
                    actualInnerPartRightEndIndex = currentActualIndex
                }

                nextCellIndex[end] = nextCellIndex[actualInnerPartRightEndIndex]
                previousCellIndex[nextCellIndex[actualInnerPartRightEndIndex]] = end
                nextCellIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
                previousCellIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
                nextCellIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
                previousCellIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
            }
        }
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> {
                        if (it == index) actualIndex = nextCellIndex[actualIndex]
                        get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    }
                    else -> null
                }
            }
        } else {
            justRemoveAt(actualIndex(index))
        }
    }

    override fun removeFirst() {
        if (size == 0u) indexOutOfBoundsException(0u, size) // TODO: Maybe replace with another error
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = nextCellIndex[start]
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(start)
        }
    }

    override fun removeLast() {
        if (size == 0u) indexOutOfBoundsException(size, size) // TODO: Maybe replace with another error
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(end)
        }
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
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
                    resultActualMark = nextCellIndex[resultActualMark]
                    resultSize++
                }
                checkingActualMark = nextCellIndex[checkingActualMark]
                checkingIndex++
            }
            newSize = resultSize
            firstCellToClear = resultActualMark
        }
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            var currentActualIndexToClear = firstCellToClear
            repeat(size - newSize) {
                data[currentActualIndexToClear] = null
                currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
            }
            size = newSize
        }
    }

    override fun iterator(): KoneMutableLinearIterator<Element> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[start])
        var currentIndex = start
        for (i in 1u..<size) {
            currentIndex = nextCellIndex[currentIndex]
            append(", ")
            append(data[currentIndex])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayResizableLinkedList<*> -> {
                var thisCurrentIndex = this.start
                var otherCurrentIndex = other.start
                repeat(size) {
                    if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                    otherCurrentIndex = other.nextCellIndex[otherCurrentIndex]
                }
            }
            else -> {
                var thisCurrentIndex = this.start
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                }
            }
        }

        return true
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<Element> {
        init {
            if (currentIndex > size) indexOutOfBoundsException(currentIndex, size)
        }
        var actualCurrentIndex = actualIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return data[actualCurrentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            data[actualCurrentIndex] = element
        }
        override fun addNext(element: Element) {
            when {
                size == sizeUpperBound -> {
                    var actualIndex = start
                    reinitializeBoundsAndData(size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                            it == currentIndex -> element
                            it <= size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                            else -> null
                        }
                    }
                    actualCurrentIndex = currentIndex
                }
                currentIndex == size -> justAddAfterTheEnd(element)
                else -> {
                    justAddBefore(actualCurrentIndex, element)
                    actualCurrentIndex = previousCellIndex[actualCurrentIndex]
                }
            }
        }
        override fun removeNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            val newSize = size - 1u
            if (newSize < sizeLowerBound) {
                var actualIndex = start
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < newSize -> {
                            if (it == currentIndex) actualIndex = nextCellIndex[actualIndex]
                            get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        }
                        else -> null
                    }
                }
                actualCurrentIndex = currentIndex
            } else {
                justRemoveAt(actualCurrentIndex)
            }
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            return data[previousCellIndex[actualCurrentIndex]] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            currentIndex--
            actualCurrentIndex = previousCellIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            data[previousCellIndex[actualCurrentIndex]] = element
        }
        override fun addPrevious(element: Element) {
            justAddBefore(actualCurrentIndex, element)
        }
        override fun removePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            justRemoveAt(previousCellIndex[actualCurrentIndex])
        }
    }
}
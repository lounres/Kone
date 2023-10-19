/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import kotlin.math.min


@Suppress("UNCHECKED_CAST")
public class KoneResizableLinkedList<E> internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(min(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = sizeUpperBound - 1u,
): KoneMutableIterableList<E> {
    override var size: UInt = size
        private set

    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneGrowableArrayList implementation can not allocate array of size more than 2^31")
        when {
            newSize > sizeUpperBound -> {
                while (newSize > sizeUpperBound) {
                    dataSizeNumber++
                    sizeLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
            }
            newSize < sizeLowerBound -> {
                while (newSize < sizeUpperBound) {
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
        end = if (size > 0u) size - 1u else sizeUpperBound - 1u
    }

    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextCellIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = 0u
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = 0u
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
        }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> E) {
        for (index in 0u ..< newElementsNumber) {
            end = nextCellIndex[end]
            data[end] = generator(index)
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: E) {
        justAddAfterTheEnd(1u) { element }
    }
    private fun justAddBefore(actualIndex: UInt, element: E) {
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

        data[actualIndex] = element

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

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return data[actualIndex(index)] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[actualIndex(index)] = element
    }

    override fun clear() {
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: E) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(element)
        }
    }

    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
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
    override fun addAll(elements: KoneIterableCollection<E>) {
        val newSize = size + elements.size
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            val iter = elements.iterator()
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    iter.hasNext() -> iter.next()
                    else -> null
                }
            }
        } else {
            val iter = elements.iterator()
            justAddAfterTheEnd(elements.size) { iter.next() }
        }
    }

    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
        if (index > size) indexException(index, size)
        if (elements.size == 0u) return
        val newSize = size + elements.size
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                val iter = elements.iterator()
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        iter.hasNext() -> iter.next()
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                val iter = elements.iterator()
                justAddAfterTheEnd(elements.size) { iter.next() }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextCellIndex[end]
                val actualInnerPartRightEndIndex: UInt
                run {
                    var currentActualIndex = end
                    val iter = elements.iterator()
                    while (iter.hasNext()) {
                        currentActualIndex = nextCellIndex[currentActualIndex]
                        data[currentActualIndex] = iter.next()
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
    override fun remove(element: E) {
        val targetIndex: UInt
        val actualTargetIndex: UInt
        run {
            var actualCurrentIndex = start
            for (i in 0u ..< size) {
                if (data[actualCurrentIndex] == element) {
                    targetIndex = i
                    actualTargetIndex = actualCurrentIndex
                    return@run
                }
                actualCurrentIndex = nextCellIndex[actualCurrentIndex]
            }
            return
        }
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < targetIndex -> get(actualIndex).also { _ ->
                        actualIndex = nextCellIndex[actualIndex]
                        if (it == targetIndex - 1u) actualIndex = nextCellIndex[actualIndex]
                    }
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(actualTargetIndex)
        }
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(actualIndex).also { _ ->
                        actualIndex = nextCellIndex[actualIndex]
                        if (it == index - 1u) actualIndex = nextCellIndex[actualIndex]
                    }
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(actualIndex(index))
        }
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        val newSize: UInt
        val firstCellToClear: UInt
        run {
            var checkingActualMark = 0u
            var checkingIndex = 0u
            var resultActualMark = 0u
            var resultSize = 0u
            while (checkingIndex < size) {
                if (!predicate(checkingIndex, data[checkingActualMark] as E)) {
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
            for (i in 0u ..< size - newSize) {
                data[currentActualIndexToClear] = null
                currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
            }
        }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size >= 0u) append(data[start])
        var currentIndex = start
        for (i in 1u..<size) {
            currentIndex = nextCellIndex[currentIndex]
            append(", ")
            append(data[currentIndex])
        }
        append(']')
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var actualCurrentIndex = actualIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return data[actualCurrentIndex] as E
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, size)
            data[currentIndex] = element
        }
        override fun addNext(element: E) {
            if (currentIndex == size) justAddAfterTheEnd(element)
            else justAddBefore(nextCellIndex[actualCurrentIndex], element)
        }
        override fun removeNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            justRemoveAt(actualCurrentIndex.also { actualCurrentIndex = nextCellIndex[actualCurrentIndex] })
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return data[previousCellIndex[actualCurrentIndex]] as E
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
            actualCurrentIndex = previousCellIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, size)
            data[previousCellIndex[actualCurrentIndex]] = element
        }
        override fun addPrevious(element: E) {
            justAddBefore(actualCurrentIndex, element)
        }
        override fun removePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            justRemoveAt(previousCellIndex[actualCurrentIndex])
        }
    }
}
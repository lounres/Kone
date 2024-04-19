/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.misc.scope


@Suppress("UNCHECKED_CAST")
public class KoneGrowableLinkedArrayList<E, EC: Equality<E>> internal constructor(
    size: UInt,
    private var sizeUpperBound: UInt = powerOf2GreaterOrEqualTo(size),
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = sizeUpperBound - 1u,
    override val elementContext: EC,
) : KoneMutableIterableList<E>, KoneListWithContext<E, EC>, KoneCollectionWithGrowableCapacity<E>, KoneDequeue<E>, Disposable {
    override var size: UInt = size
        private set

    override fun contains(element: E): Boolean {
        var currentIndex = start
        for (index in 0u ..< size) {
            if (elementContext { (data[currentIndex] as E) eq element }) return true
            currentIndex = nextCellIndex[currentIndex]
        }
        return false
    }

    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }
    override fun dispose() {
        data.dispose(size)
    }
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

    override fun ensureCapacity(minimalCapacity: UInt) {
        if (sizeUpperBound < minimalCapacity) {
            reinitializeBounds(minimalCapacity)
            var actualIndex = start
            reinitializeData {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        }
    }

    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextCellIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = start
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = end
                for (i in index + 1u ..< end) {
                    currentIndex = previousCellIndex[currentIndex]
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

    override fun getFirst(): E = if (isEmpty()) indexException(0u, size) else data[start] as E

    override fun getLast(): E = if (isEmpty()) indexException(size, size) else data[end] as E

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[actualIndex(index)] = element
    }

    override fun removeAll() {
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

    override fun addFirst(element: E) {
        addAt(0u, element)
    }

    override fun addLast(element: E) {
        justAddAfterTheEnd(element)
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> E) {
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
    override fun addAllFrom(elements: KoneIterableCollection<E>) {
        val newSize = size + elements.size
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            val iter = elements.iterator()
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    iter.hasNext() -> iter.getAndMoveNext()
                    else -> null
                }
            }
        } else {
            val iter = elements.iterator()
            justAddAfterTheEnd(elements.size) { iter.getAndMoveNext() }
        }
    }

    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> E) {
        if (index > size) indexException(index, size)
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
    override fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>) {
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
                        iter.hasNext() -> iter.getAndMoveNext()
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                val iter = elements.iterator()
                justAddAfterTheEnd(elements.size) { iter.getAndMoveNext() }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextCellIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    val iter = elements.iterator()
                    while (iter.hasNext()) {
                        currentActualIndex = nextCellIndex[currentActualIndex]
                        data[currentActualIndex] = iter.getAndMoveNext()
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
        val actualTargetIndex: UInt
        scope {
            var actualCurrentIndex = start
            for (i in 0u ..< size) {
                if (elementContext { (data[actualCurrentIndex] as E) eq element }) {
                    actualTargetIndex = actualCurrentIndex
                    return@scope
                }
                actualCurrentIndex = nextCellIndex[actualCurrentIndex]
            }
            return
        }
        justRemoveAt(actualTargetIndex)
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        justRemoveAt(actualIndex(index))
    }

    override fun removeFirst() {
        justRemoveAt(start)
    }

    override fun removeLast() {
        justRemoveAt(end)
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        val newSize: UInt
        val firstCellToClear: UInt
        scope {
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
        var currentActualIndexToClear = firstCellToClear
        for (i in 0u ..< size - newSize) {
            data[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[start])
        var currentActualIndex = start
        for (i in 1u..<size) {
            currentActualIndex = nextCellIndex[currentActualIndex]
            append(", ")
            append(data[currentActualIndex])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        var currentActualIndex = start
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + data[currentActualIndex].hashCode()
            currentActualIndex = nextCellIndex[currentActualIndex]
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList<*, *> -> {
                var thisCurrentIndex = this.start
                var otherCurrentIndex = other.start
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                    otherCurrentIndex = other.nextCellIndex[otherCurrentIndex]
                }
            }
            is KoneIterableList<*> -> {
                var thisCurrentIndex = this.start
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                }
            }
            else -> {
                var thisCurrentIndex = this.start
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != other[i]) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                }
            }
        }

        return true
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
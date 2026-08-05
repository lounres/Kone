/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterator.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


@Suppress("UNCHECKED_CAST")
@Serializable(with = KoneArrayResizableLinkedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayResizableLinkedList<Element> @PublishedApi internal constructor(
    size: UInt,
    internal var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u,
    internal var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    internal var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    data: KoneMutableArray<Any?> = KoneMutableArray.generate<Any?>(sizeUpperBound) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneMutableList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal var data: KoneMutableArray<Any?>
        get() = _data!!
        set(value) { _data = value }
    private var _nextNodeIndex: KoneMutableUIntArray? = nextNodeIndex
    internal var nextNodeIndex: KoneMutableUIntArray
        get() = _nextNodeIndex!!
        set(value) { _nextNodeIndex = value }
    private var _previousNodeIndex: KoneMutableUIntArray? = previousNodeIndex
    internal var previousNodeIndex: KoneMutableUIntArray
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
        data = KoneMutableArray.generate(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextNodeIndex = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u }
        previousNodeIndex = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = (if (size > 0u) size - 1u else sizeUpperBound - 1u)
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
                for (_ in index ..< size-1u) {
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
        end = nextNodeIndex[end]
        data[end] = element
        size += 1u
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

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[actualIndex(index)] = element
    }

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    it == oldSize -> element
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }

    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        when {
            size == sizeUpperBound -> {
                val oldSize = size
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        it == index -> element
                        it <= oldSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }

    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < oldSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    it < oldSize + number -> builder(it - oldSize)
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(number) { builder(it) }
        }
    }

    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        it < index + number -> builder(it - index)
                        it < newSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                justAddAfterTheEnd(number) { builder(it) }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualInnerPartLeftEndIndex = nextNodeIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    for (localIndex in 0u ..< number) {
                        currentActualIndex = nextNodeIndex[currentActualIndex]
                        data[currentActualIndex] = builder(localIndex)
                    }
                    actualInnerPartRightEndIndex = currentActualIndex
                }

                nextNodeIndex[end] = nextNodeIndex[actualInnerPartRightEndIndex]
                previousNodeIndex[nextNodeIndex[actualInnerPartRightEndIndex]] = end
                val actualLeftPartIndex = previousNodeIndex[actualRightPartIndex]
                nextNodeIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
                previousNodeIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
                previousNodeIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
                nextNodeIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
                
                if (index == 0u) start = actualInnerPartLeftEndIndex
                size += number
            }
        }
    }
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> {
                        if (it == index) actualIndex = nextNodeIndex[actualIndex]
                        get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    }
                    else -> null
                }
            }
        } else {
            justRemoveAt(actualIndex(index))
        }
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        val newSize: UInt
        val firstNodeToClear: UInt
        scope {
            var checkingActualMark = start
            var checkingIndex = 0u
            var resultActualMark = start
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
            firstNodeToClear = resultActualMark
        }
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextNodeIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            end = previousNodeIndex[firstNodeToClear]
            scope {
                var currentActualIndexToClear = firstNodeToClear
                repeat(size - newSize) {
                    data[currentActualIndexToClear] = null
                    currentActualIndexToClear = nextNodeIndex[currentActualIndexToClear]
                }
            }
            size = newSize
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
        var currentIndex = start
        for (_ in 1u..<size) {
            currentIndex = nextNodeIndex[currentIndex]
            append(", ")
            append(data[currentIndex])
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
        
        if (other is KoneArrayResizableLinkedList<*>) {
            var thisCurrentIndex = this.start
            var otherCurrentIndex = other.start
            repeat(size) {
                if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
                thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                otherCurrentIndex = other.nextNodeIndex[otherCurrentIndex]
            }
        } else {
            var thisCurrentIndex = this.start
            val otherIterator = other.iterator()
            for (_ in 0u ..< size) {
                if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
                thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
            }
        }

        return true
    }
    
    // TODO: Review operations. Looks like they are incorrect.
    internal class Iterator<Element>(
        val list: KoneArrayResizableLinkedList<Element>,
        var currentIndex: UInt,
    ): KoneMutableLinearIterator<Element> {
        var actualCurrentIndex = list.actualIndex(currentIndex)
        
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
            list.data[actualCurrentIndex] = element
        }
        override fun addNext(element: Element) {
            if (list.isDisposed) disposedInstanceException()
            when {
                list.size == list.sizeUpperBound -> {
                    val oldSize = list.size
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(list.size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            it == currentIndex -> element
                            it <= oldSize -> get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            else -> null
                        }
                    }
                    actualCurrentIndex = currentIndex
                }
                currentIndex == list.size -> list.justAddAfterTheEnd(element)
                else -> {
                    list.justAddBefore(actualCurrentIndex, element)
                    actualCurrentIndex = list.previousNodeIndex[actualCurrentIndex]
                }
            }
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            val newSize = list.size - 1u
            when {
                newSize < list.sizeLowerBound -> {
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(newSize) {
                        when {
                            it < newSize -> {
                                if (it == currentIndex) actualIndex = list.nextNodeIndex[actualIndex]
                                get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            }
                            else -> null
                        }
                    }
                    actualCurrentIndex = currentIndex
                }
                currentIndex == 0u -> {
                    list.justRemoveAt(actualCurrentIndex)
                    actualCurrentIndex = list.start
                }
                else -> {
                    val actualPreviousIndex = list.previousNodeIndex[actualCurrentIndex]
                    list.justRemoveAt(actualCurrentIndex)
                    actualCurrentIndex = list.nextNodeIndex[actualPreviousIndex]
                }
            }
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
            if (list.isDisposed) disposedInstanceException()
            when {
                list.size == list.sizeUpperBound -> {
                    val oldSize = list.size
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(list.size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            it == currentIndex -> element
                            it <= oldSize -> get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            else -> null
                        }
                    }
                    currentIndex++
                    actualCurrentIndex = currentIndex
                }
                currentIndex == list.size -> {
                    list.justAddAfterTheEnd(element)
                    currentIndex++
                    actualCurrentIndex = list.nextNodeIndex[actualCurrentIndex]
                }
                else -> {
                    list.justAddBefore(actualCurrentIndex, element)
                    currentIndex++
                }
            }
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            val actualPreviousIndex = list.previousNodeIndex[actualCurrentIndex]
            when {
                list.size == list.sizeLowerBound -> {
                    val newSize = list.size - 1u
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(newSize) {
                        when {
                            it < newSize -> {
                                if (it == currentIndex - 1u) actualIndex = list.nextNodeIndex[actualIndex]
                                get(actualIndex).also { actualIndex = list.nextNodeIndex[actualIndex] }
                            }
                            else -> null
                        }
                    }
                    actualCurrentIndex = currentIndex - 1u
                }
                actualPreviousIndex == list.end -> {
                    list.justRemoveAt(actualPreviousIndex)
                    actualCurrentIndex = list.nextNodeIndex[list.end]
                }
                else -> {
                    list.justRemoveAt(actualPreviousIndex)
                }
            }
            currentIndex--
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex, actual current index = $actualCurrentIndex]"
    }
}
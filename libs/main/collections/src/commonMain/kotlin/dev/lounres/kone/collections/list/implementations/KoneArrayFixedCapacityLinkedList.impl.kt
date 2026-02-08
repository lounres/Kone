/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


/**
 * Represents a doubly linked list that is laid out on three arrays of the same fixed capacity
 * instead of using object nodes.
 *
 * # Implementation details
 *
 * Usual doubly linked list consists of nodes that store elements and references to the next and the previous nodes.
 * In case of this implementation the three jobs are spread between the three arrays [data], [nextNodeIndex], and [previousNodeIndex].
 *
 * Actually, there are two types of nodes, nodes with elements and nodes without them.
 * All the nodes have their own "actual" indices from `0` to [capacity] exclusive.
 * They are connected in a (oriented) cycle in a such way that element-containing and empty nodes
 * form two separate connected components.
 * (I.e. at first element-containing nodes are placed in the cycle and then empty nodes are placed in the cycle.)
 * For each node with actual index `i`, `i`th elements in [nextNodeIndex] and [previousNodeIndex]
 * are actual indices of the next and the previous nodes correspondingly.
 * As a corollary,
 * ```
 * nextNodeIndex[previousNodeIndex[i]] == i
 * previousNodeIndex[nextNodeIndex[i]] == i
 * ```
 *
 * There are also two references, [start] and [end]
 * that are pointing to the first and the last element-containing nodes.
 * But if all the nodes are empty, then [start] and [end] are pointing on any two consequent nodes,
 * where [end]'s node is going after [start]'s node.
 *
 * Then all element-containing nodes are consequently numbered by "virtual" indices from `0` to [size] exclusive.
 * This virtual indices are exposed as the list's indices.
 *
 * Finally, the [data] array contains the `i`th node's element in its `i`th position.
 *
 * After each operation the structure is preserved in a state that meets the invariants above.
 *
 * ## Time complexity of operations
 *
 * It's obvious that getting actual index corresponding to the list's index
 * takes \(\Theta(\mathrm{size})\) time (both in worst case and in average).
 * Thus, all operations that involve getting actual index
 * take (both in worst case and in average) at least \(\Theta(\mathrm{size})\) time.
 * Other operations that reuse already computed actual indices (including all iterator's operations)
 * take constant time.
 *
 * | Operation                                                          | Worst case                                  | Average                                     |
 * |--------------------------------------------------------------------|---------------------------------------------|---------------------------------------------|
 * | [size]                                                             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [get]                                                              | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [set]                                                              | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [add]                                                              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [addAt]                                                            | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [addSeveral]                                                       | \(\Theta(\mathrm{number})\)                 | \(\Theta(\mathrm{number})\)                 |
 * | [addSeveralAt]                                                     | \(\Theta(\mathrm{size} + \mathrm{number})\) | \(\Theta(\mathrm{size} + \mathrm{number})\) |
 * | [removeAt]                                                         | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThat]                                                    | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThatIndexed]                                             | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAll]                                                        | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator]                                                         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iteratorFrom]                                                     | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.hasNext][KoneMutableLinearIterator.hasNext]              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasPrevious][KoneMutableLinearIterator.hasPrevious]      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getNext][KoneMutableLinearIterator.getNext]              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getPrevious][KoneMutableLinearIterator.getPrevious]      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.moveNext][KoneMutableLinearIterator.moveNext]            | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.movePrevious][KoneMutableLinearIterator.movePrevious]    | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setNext][KoneMutableLinearIterator.setNext]              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setPrevious][KoneMutableLinearIterator.setPrevious]      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addNext][KoneMutableLinearIterator.addNext]              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addPrevious][KoneMutableLinearIterator.addPrevious]      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.removeNext][KoneMutableLinearIterator.removeNext]        | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.removePrevious][KoneMutableLinearIterator.removePrevious]| \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.nextIndex][KoneMutableLinearIterator.nextIndex]          | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.previousIndex][KoneMutableLinearIterator.previousIndex]  | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 *
 * @usesMathJax
 */
@Suppress("UNCHECKED_CAST")
@Serializable(with = KoneFixedCapacityLinkedArrayListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayFixedCapacityLinkedList<Element> internal constructor(
    size: UInt,
    internal val capacity: UInt,
    data: KoneMutableArray<Any?> = KoneMutableArray.generate<Any?>(capacity) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(capacity) { if (it == capacity - 1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(capacity) { if (it == 0u) capacity - 1u else it - 1u },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else capacity - 1u,
) : KoneMutableList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal val data: KoneMutableArray<Any?> get() = _data!!
    private var _nextNodeIndex: KoneMutableUIntArray? = nextNodeIndex
    internal val nextNodeIndex: KoneMutableUIntArray get() = _nextNodeIndex!!
    private var _previousNodeIndex: KoneMutableUIntArray? = previousNodeIndex
    internal val previousNodeIndex: KoneMutableUIntArray get() = _previousNodeIndex!!
    
    override fun dispose() {
        if (isDisposed) return
        var currentIndex = start
        repeat(size) {
            data[currentIndex] = null
            currentIndex = nextNodeIndex[currentIndex]
        }
        _data = null
        _nextNodeIndex = null
        _previousNodeIndex = null
        isDisposed = true
    }
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set

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
                for (_ in index + 1u ..< size) {
                    currentIndex = previousNodeIndex[currentIndex]
                }
                currentIndex
            }
        }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
        repeat(newElementsNumber) {
            end = nextNodeIndex[end]
            data[end] = generator(it)
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

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[actualIndex(index)] = element
    }

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        repeat(size) {
            data[start] = null
            start = nextNodeIndex[start]
        }
        size = 0u
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == capacity) capacityOverflowException(capacity)
        justAddAfterTheEnd(element)
    }

    override fun addAt(index: UInt, element: Element) {
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            size == capacity -> capacityOverflowException(capacity)
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (number == 0u) return
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)

        justAddAfterTheEnd(number) { builder(it) }
    }
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > capacity -> capacityOverflowException(capacity)
            index == size -> justAddAfterTheEnd(number) { builder(it) }
            else -> {
                val actualRightPartIndex = actualIndex(index)
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
        justRemoveAt(actualIndex(index))
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
        for (_ in 1u..<size) {
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
            is KoneArrayFixedCapacityLinkedList<*> -> {
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
        val list: KoneArrayFixedCapacityLinkedList<Element>,
        var currentIndex: UInt,
    ): KoneMutableLinearIterator<Element> {
        var actualCurrentIndex = if (list.capacity == 0u) 0u else list.actualIndex(currentIndex)
        
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else list.data[actualCurrentIndex] as Element
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
            if (list.size == list.capacity) capacityOverflowException(list.capacity)
            if (currentIndex == list.size) list.justAddAfterTheEnd(element)
            else {
                list.justAddBefore(actualCurrentIndex, element)
                actualCurrentIndex = list.previousNodeIndex[actualCurrentIndex]
            }
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIndex == 0u) {
                list.justRemoveAt(actualCurrentIndex)
                actualCurrentIndex = list.start
            } else {
                val actualPreviousIndex = list.previousNodeIndex[actualCurrentIndex]
                list.justRemoveAt(actualCurrentIndex)
                actualCurrentIndex = list.nextNodeIndex[actualPreviousIndex]
            }
        }

        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex > 0u
        override fun getPrevious(): Element =
            if (!hasPrevious()) noPreviousElementInIteratorException()
            else list.data[list.previousNodeIndex[actualCurrentIndex]] as Element
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
            if (list.size == list.capacity) capacityOverflowException(list.capacity)
            if (currentIndex == list.size) {
                list.justAddAfterTheEnd(element)
                currentIndex++
                actualCurrentIndex = list.nextNodeIndex[actualCurrentIndex]
            } else {
                list.justAddBefore(actualCurrentIndex, element)
                currentIndex++
            }
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            val actualPreviousIndex = list.previousNodeIndex[actualCurrentIndex]
            if (actualPreviousIndex == list.end) {
                list.justRemoveAt(actualPreviousIndex)
                actualCurrentIndex = list.nextNodeIndex[list.end]
            } else {
                list.justRemoveAt(actualPreviousIndex)
            }
            currentIndex--
        }
    }
}
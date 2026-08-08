/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityLinkedList.BulkElementsRemover
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


// TODO: Create `NodeIterator` that does not compute virtual index and does not take O(size) time for initialization
// TODO: Actualize time complexity table
/**
 * Represents a doubly linked nodded list that is laid out on three arrays of the same fixed capacity
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
 * Finally, the [data] array contains the `i`th actual node in its `i`th position.
 * Actual node is an object that is returned by [getNode] and [addNode].
 *
 * After each operation the structure is preserved in a state that meets the invariants above.
 *
 * ## Time complexity of operations
 *
 * It's obvious that getting actual index corresponding to the list's index
 * takes \(\Theta(\mathrm{size})\) time (both in worst case and in average).
 * Thus, all operations that involve getting actual index
 * take (both in worst case and in average) at least \(\Theta(\mathrm{size})\) time.
 * Other operations that reuse already computed actual indices (including all iterator's and node's operations)
 * take constant time.
 *
 * | Operation                                                                 | Worst case                                  | Average                                     |
 * |---------------------------------------------------------------------------|---------------------------------------------|---------------------------------------------|
 * | [size]                                                                    | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [get]                                                                     | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [set]                                                                     | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [add]                                                                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [addAt]                                                                   | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [addSeveral]                                                              | \(\Theta(\mathrm{number})\)                 | \(\Theta(\mathrm{number})\)                 |
 * | [addSeveralAt]                                                            | \(\Theta(\mathrm{size} + \mathrm{number})\) | \(\Theta(\mathrm{size} + \mathrm{number})\) |
 * | [removeAt]                                                                | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThat]                                                           | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThatIndexed]                                                    | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAll]                                                               | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator]                                                                | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iteratorFrom]                                                            | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.hasNext][KoneMutableNoddedListIterator.hasNext]                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasPrevious][KoneMutableNoddedListIterator.hasPrevious]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getNext][KoneMutableNoddedListIterator.getNext]                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getNextNode][KoneMutableNoddedListIterator.getNextNode]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getPrevious][KoneMutableNoddedListIterator.getPrevious]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getPreviousNode][KoneMutableNoddedListIterator.getPreviousNode] | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.moveNext][KoneMutableNoddedListIterator.moveNext]               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.movePrevious][KoneMutableNoddedListIterator.movePrevious]       | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setNext][KoneMutableNoddedListIterator.setNext]                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setPrevious][KoneMutableNoddedListIterator.setPrevious]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addNext][KoneMutableNoddedListIterator.setNext]                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addPrevious][KoneMutableNoddedListIterator.setPrevious]         | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.removeNext][KoneMutableNoddedListIterator.setNext]              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.removePrevious][KoneMutableNoddedListIterator.setPrevious]      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.nextIndex][KoneMutableNoddedListIterator.nextIndex]             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.previousIndex][KoneMutableNoddedListIterator.previousIndex]     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.element][KoneMutableListNode.element]                               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.index][KoneMutableListNode.index]                                   | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [node.remove][KoneMutableListNode.remove]                                 | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.nextNode][KoneMutableListNode.nextNode]                             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.previousNode][KoneMutableListNode.previousNode]                     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [node.iteratorFromBeforeHere][KoneMutableListNode.iteratorFromBeforeHere] | \(\Theta(\mathrm{size})\) (for now)         | \(\Theta(\mathrm{size})\) (for now)         |
 * | [node.iteratorFromAfterHere][KoneMutableListNode.iteratorFromAfterHere]   | \(\Theta(\mathrm{size})\) (for now)         | \(\Theta(\mathrm{size})\) (for now)         |
 *
 * @usesMathJax
 */
@Serializable(with = KoneArrayFixedCapacityLinkedNoddedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayFixedCapacityLinkedNoddedList<Element> internal constructor(
    size: UInt,
    internal val capacity: UInt,
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray.generate(capacity) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(capacity) { if (it == capacity - 1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(capacity) { if (it == 0u) capacity - 1u else it - 1u },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else capacity - 1u,
) : KoneMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    init {
        var actualIndex = start
        repeat(size) {
            data[actualIndex]!!.list = this
            actualIndex = nextNodeIndex[actualIndex]
        }
    }
    
    internal var _data: KoneMutableArray<Node<Element>?>? = data
    internal val data: KoneMutableArray<Node<Element>?> get() = _data!!
    internal var _nextNodeIndex: KoneMutableUIntArray? = nextNodeIndex
    internal val nextNodeIndex: KoneMutableUIntArray get() = _nextNodeIndex!!
    internal var _previousNodeIndex: KoneMutableUIntArray? = previousNodeIndex
    internal val previousNodeIndex: KoneMutableUIntArray get() = _previousNodeIndex!!

    override fun dispose() {
        if (isDisposed) return
        var currentIndex = start
        repeat(size) {
            data[currentIndex]!!.detach()
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
    private fun virtualIndex(actualIndex: UInt): UInt {
        var result = 0u
        var currentActualIndex = actualIndex
        while (currentActualIndex != start) {
            result++
            currentActualIndex = previousNodeIndex[currentActualIndex]
        }
        return result
    }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
        repeat(newElementsNumber) {
            end = nextNodeIndex[end]
            data[end] = Node(this, generator(it), end)
        }
        size += newElementsNumber
    }
    @IgnorableReturnValue
    private fun justAddAfterTheEnd(element: Element): Node<Element> {
        end = nextNodeIndex[end]
        val newNode = Node(this, element, end)
        data[end] = newNode
        size++
        return newNode
    }
    @IgnorableReturnValue
    private fun justAddBefore(actualIndex: UInt, element: Element): Node<Element> {
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

        val newNode = Node(this, element, freeIndex)
        data[freeIndex] = newNode

        size++
        
        return newNode
    }
    private fun justRemoveAt(actualIndex: UInt) {
        data[actualIndex]!!.detach()
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
        return data[actualIndex(index)]!!.element
    }

    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[actualIndex(index)]!!
    }

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[actualIndex(index)]!!.element = element
    }
    
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        repeat(size) {
            data[start]!!.detach()
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

    override fun addNode(element: Element): KoneMutableListNode<Element> =
        when {
            isDisposed -> disposedInstanceException()
            size == capacity -> capacityOverflowException(capacity)
            else -> justAddAfterTheEnd(element)
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

    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            size == capacity -> capacityOverflowException(capacity)
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    
    @DelicateSeveralElementsInserterAPI
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return ZeroElementsInserter
        val newSize = size + number
        return when {
            newSize > capacity -> capacityOverflowException(capacity)
            index == size -> EndAppendingSeveralElementsInserter(this, number)
            else -> InsideSeveralElementsInserter(this, index, number)
        }
    }

    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        justRemoveAt(actualIndex(index))
    }
    
    @DelicateBulkElementsRemoverAPI
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> {
        if (isDisposed) disposedInstanceException()
        return BulkElementsRemover(this)
    }

    override fun iterator(): KoneMutableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator(
            list = this,
            currentIndex = 0u,
            actualCurrentIndex = start,
        )
    public override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(
                list = this,
                currentIndex = index,
                actualCurrentIndex = if (capacity == 0u) 0u else actualIndex(index),
            )
        }

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[start]!!.element)
        var currentActualIndex = start
        for (_ in 1u..<size) {
            currentActualIndex = nextNodeIndex[currentActualIndex]
            append(", ")
            append(data[currentActualIndex]!!.element)
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        var currentActualIndex = start
        repeat(size) {
            hashCode = 31 * hashCode + data[currentActualIndex]!!.element.hashCode()
            currentActualIndex = nextNodeIndex[currentActualIndex]
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        if (other is KoneArrayFixedCapacityLinkedNoddedList<*>) {
            var thisCurrentIndex = this.start
            var otherCurrentIndex = other.start
            repeat(size) {
                if (this.data[thisCurrentIndex]!!.element != other.data[otherCurrentIndex]!!.element) return false
                thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                otherCurrentIndex = other.nextNodeIndex[otherCurrentIndex]
            }
        } else {
            var thisCurrentIndex = this.start
            val otherIterator = other.iterator()
            repeat(size) {
                if (this.data[thisCurrentIndex]!!.element != otherIterator.getAndMoveNext()) return false
                thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
            }
        }

        return true
    }

    internal class Node<Element>(
        override var element: Element,
        internal var actualIndex: UInt,
    ): KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        private var _list: KoneArrayFixedCapacityLinkedNoddedList<Element>? = null
        internal var list: KoneArrayFixedCapacityLinkedNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }

        fun detach() {
            if (isDetached) return
            _list = null
            isDetached = true
        }
        
        constructor(list: KoneArrayFixedCapacityLinkedNoddedList<Element>, element: Element, index: UInt) : this(element, index) {
            this.list = list
        }
        
        override val index: UInt get() = list.virtualIndex(actualIndex)

        override fun remove() {
            if (isDetached) detachedNodeException()
            list.justRemoveAt(actualIndex)
            detach()
        }

        override val nextNode: KoneMutableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                actualIndex != list.end -> list.data[list.nextNodeIndex[actualIndex]]
                else -> null
            }
        override val previousNode: KoneMutableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                actualIndex != list.start -> list.data[list.previousNodeIndex[actualIndex]]
                else -> null
            }

        override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException()
            else Iterator(
                list = list,
                currentIndex = list.virtualIndex(actualIndex),
                actualCurrentIndex = actualIndex,
            )
        override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException()
            else Iterator(
                list = list,
                currentIndex = list.virtualIndex(actualIndex) + 1u,
                actualCurrentIndex = list.nextNodeIndex[actualIndex],
            )
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[$element]"
    }

    internal class Iterator<Element>(
        val list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
        var currentIndex: UInt,
        var actualCurrentIndex: UInt,
    ): KoneMutableNoddedListIterator<Element> {
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[actualCurrentIndex]!!.element
        }
        override fun getNextNode(): KoneMutableListNode<Element> {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[actualCurrentIndex]!!
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
            actualCurrentIndex = list.nextNodeIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[actualCurrentIndex]!!.element = element
        }
        override fun addNext(element: Element) {
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
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[list.previousNodeIndex[actualCurrentIndex]]!!.element
        }
        override fun getPreviousNode(): KoneMutableListNode<Element> {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[list.previousNodeIndex[actualCurrentIndex]]!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
            actualCurrentIndex = list.previousNodeIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[list.previousNodeIndex[actualCurrentIndex]]!!.element = element
        }
        override fun addPrevious(element: Element) {
            if (list.size == list.capacity) capacityOverflowException(list.capacity)
            list.justAddBefore(actualCurrentIndex, element)
            currentIndex++
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
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex, actual current index = $actualCurrentIndex]"
    }
    
    internal object ZeroElementsInserter : KoneSeveralElementsInserter<Any?> {
        override val newElementsNumber: UInt get() = 0u
        override fun insert(element: Any?) {
            severalElementsInserterOverflowException()
        }
        override fun close() {}
    }
    
    internal class EndAppendingSeveralElementsInserter<Element>(
        private val list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        private var currentIndex = 0u
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            list.end = list.nextNodeIndex[list.end]
            list.data[list.end] = Node(list, element, list.end)
            list.size++
            currentIndex++
        }
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
        }
    }
    
    internal class InsideSeveralElementsInserter<Element>(
        private val list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
        private val index: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        private val actualRightPartIndex = list.actualIndex(index)
        private val actualInnerPartLeftEndIndex = list.nextNodeIndex[list.end]
        private var currentIndex = 0u
        private var currentActualIndex = list.end
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            currentActualIndex = list.nextNodeIndex[currentActualIndex]
            currentIndex++
            list.data[currentActualIndex] = Node(list, element, currentActualIndex)
        }
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
            val actualInnerPartRightEndIndex = currentActualIndex
            
            list.nextNodeIndex[list.end] = list.nextNodeIndex[actualInnerPartRightEndIndex]
            list.previousNodeIndex[list.nextNodeIndex[actualInnerPartRightEndIndex]] = list.end
            val actualLeftPartIndex = list.previousNodeIndex[actualRightPartIndex]
            list.nextNodeIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
            list.previousNodeIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
            list.previousNodeIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
            list.nextNodeIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
            
            if (index == 0u) list.start = actualInnerPartLeftEndIndex
            list.size += newElementsNumber
        }
    }
    
    internal class BulkElementsRemover<Element>(
        private val list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
    ) : KoneBulkElementsRemover<Element> {
        var checkingActualMark = list.start
        var checkingIndex = 0u
        var resultActualMark = list.start
        var resultSize = 0u
        
        override fun hasNext(): Boolean = checkingIndex < list.size
        override fun getNext(): Element {
            if (checkingIndex >= list.size) noNextElementInBulkElementsRemoverException()
            return list.data[checkingActualMark]!!.element
        }
        override fun nextIndex(): UInt {
            if (checkingIndex >= list.size) noNextElementInBulkElementsRemoverException()
            return checkingIndex
        }
        override fun moveNext() {
            if (checkingIndex >= list.size) noNextElementInBulkElementsRemoverException()
            list.data[resultActualMark] = list.data[checkingActualMark].also { it!!.actualIndex = resultActualMark }
            resultActualMark = list.nextNodeIndex[resultActualMark]
            resultSize++
            checkingActualMark = list.nextNodeIndex[checkingActualMark]
            checkingIndex++
        }
        override fun removeNext() {
            if (checkingIndex >= list.size) noNextElementInBulkElementsRemoverException()
            checkingActualMark = list.nextNodeIndex[checkingActualMark]
            checkingIndex++
        }
        override fun close() {
            if (resultSize == checkingIndex) return
            if (checkingIndex == list.size) {
                list.end = list.previousNodeIndex[resultActualMark]
            } else {
                val actualIndexBeforeResultMark = list.previousNodeIndex[resultActualMark]
                val actualIndexBeforeCheckingMark = list.previousNodeIndex[checkingActualMark]
                val actualIndexAfterEnd = list.nextNodeIndex[list.end]
                list.nextNodeIndex[actualIndexBeforeResultMark] = checkingActualMark
                list.previousNodeIndex[checkingActualMark] = actualIndexBeforeResultMark
                list.nextNodeIndex[actualIndexBeforeCheckingMark] = actualIndexAfterEnd
                list.previousNodeIndex[actualIndexAfterEnd] = actualIndexBeforeCheckingMark
                list.nextNodeIndex[list.end] = resultActualMark
                list.previousNodeIndex[resultActualMark] = list.end
                if (resultSize == 0u) list.start = checkingActualMark
            }
            resultSize += list.size - checkingIndex
            scope {
                var currentActualIndexToClear = resultActualMark
                repeat(list.size - resultSize) {
                    list.data[currentActualIndexToClear] = null
                    currentActualIndexToClear = list.nextNodeIndex[currentActualIndexToClear]
                }
            }
            list.size = resultSize
        }
    }
}
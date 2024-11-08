/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


////@Serializable(with = KoneFixedCapacityLinkedArrayListWithContextSerializer::class)
//public class KoneArrayFixedCapacityLinkedNoddedList<Element> internal constructor(
//    size: UInt,
//    private val capacity: UInt,
//    private val data: KoneMutableArray<Node<Element>?> = KoneMutableArray(capacity) { null },
//    private val nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(capacity) { if (it == capacity - 1u) 0u else it + 1u },
//    private val previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(capacity) { if (it == 0u) capacity - 1u else it - 1u },
//    private var start: UInt = 0u,
//    private var end: UInt = if (size > 0u) size - 1u else capacity - 1u,
//) : KoneMutableNoddedList<Element>, KoneDequeue<Element>, Disposable {
//    override var size: UInt = size
//        private set
//
//    override fun dispose() {
//        var currentIndex = start
//        repeat(size) {
//            data[currentIndex]!!.detach()
//            data[currentIndex] = null
//            currentIndex = nextCellIndex[currentIndex]
//        }
//    }
//
//    private fun actualIndex(index: UInt): UInt =
//        when {
//            index == size -> nextCellIndex[end]
//            index <= (size - 1u) / 2u -> {
//                var currentIndex = start
//                repeat(index) {
//                    currentIndex = nextCellIndex[currentIndex]
//                }
//                currentIndex
//            }
//            else -> {
//                var currentIndex = end
//                for (i in index + 1u ..< size) {
//                    currentIndex = previousCellIndex[currentIndex]
//                }
//                currentIndex
//            }
//        }
//    private fun virtualIndex(actualIndex: UInt): UInt {
//        var result = 0u
//        var currentActualIndex = actualIndex
//        while (currentActualIndex != start) {
//            result++
//            currentActualIndex = previousCellIndex[currentActualIndex]
//        }
//        return result
//    }
//    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
//        repeat(newElementsNumber) {
//            end = nextCellIndex[end]
//            data[end] = Node(this, generator(it), end)
//        }
//        size += newElementsNumber
//    }
//    private fun justAddAfterTheEnd(element: Element) {
//        justAddAfterTheEnd(1u) { element }
//    }
//    private fun justAddBefore(actualIndex: UInt, element: Element) {
//        val freeIndex = nextCellIndex[end]
//        val indexAfterTheFreeIndex = nextCellIndex[freeIndex]
//        nextCellIndex[end] = indexAfterTheFreeIndex
//        previousCellIndex[indexAfterTheFreeIndex] = end
//
//        val indexBeforeTheActualIndex = previousCellIndex[actualIndex]
//        nextCellIndex[freeIndex] = actualIndex
//        previousCellIndex[freeIndex] = indexBeforeTheActualIndex
//        nextCellIndex[indexBeforeTheActualIndex] = freeIndex
//        previousCellIndex[actualIndex] = freeIndex
//
//        if (actualIndex == start) start = freeIndex
//
//        data[freeIndex] = Node(this, element, freeIndex)
//
//        size++
//    }
//    private fun justRemoveAt(actualIndex: UInt) {
//        data[actualIndex]!!.detach()
//        data[actualIndex] = null
//        val prev = previousCellIndex[actualIndex]
//        val next = nextCellIndex[actualIndex]
//        nextCellIndex[prev] = next
//        previousCellIndex[next] = prev
//        if (start == actualIndex) start = next
//        if (end == actualIndex) end = prev
//        size--
//
//        val afterEnd = nextCellIndex[end]
//        nextCellIndex[end] = actualIndex
//        previousCellIndex[afterEnd] = actualIndex
//        nextCellIndex[actualIndex] = afterEnd
//        previousCellIndex[actualIndex] = end
//        if (size == 0u) start = actualIndex
//    }
//
//    override fun get(index: UInt): Element {
//        if (index >= size) indexException(index, size)
//        return data[actualIndex(index)]!!.element
//    }
//
//    override fun getNode(index: UInt): KoneMutableListNode<Element> {
//        if (index >= size) indexException(index, size)
//        return data[actualIndex(index)]!!
//    }
//
//    override fun getFirst(): Element = data[start]!!.element
//
//    override fun getLast(): Element = data[end]!!.element
//
//    override fun set(index: UInt, element: Element) {
//        if (index >= size) indexException(index, size)
//        val actualIndex = actualIndex(index)
//        data[actualIndex] = Node(this, element, actualIndex)
//    }
//
//    override fun removeAll() {
//        repeat(capacity) {
//            data[it]!!.detach()
//            data[it] = null
//            nextCellIndex[it] = if (it == capacity - 1u) 0u else it + 1u
//            previousCellIndex[it] = if (it == 0u) capacity - 1u else it - 1u
//            start = 0u
//            end = capacity - 1u
//        }
//    }
//
//    override fun addFirst(element: Element) {
//        if (size == capacity) capacityOverflowException(capacity)
//        justAddBefore(start, element)
//    }
//
//    override fun addLast(element: Element) {
//        if (size == capacity) capacityOverflowException(capacity)
//        justAddAfterTheEnd(element)
//    }
//
//    override fun add(element: Element) {
//        if (size == capacity) capacityOverflowException(capacity)
//        justAddAfterTheEnd(element)
//    }
//
//    override fun addNode(element: Element): KoneMutableListNode<Element> {
//        TODO("Not yet implemented")
//    }
//
//    override fun addAt(index: UInt, element: Element) {
//        if (index > size) indexException(index, size)
//        when {
//            size == capacity -> capacityOverflowException(capacity)
//            index == size -> justAddAfterTheEnd(element)
//            else -> justAddBefore(actualIndex(index), element)
//        }
//    }
//
//    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
//        TODO("Not yet implemented")
//    }
//
//    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
//        if (number == 0u) return
//        val newSize = size + number
//        if (newSize > capacity) capacityOverflowException(capacity)
//
//        var localIndex = 0u
//        justAddAfterTheEnd(number) { builder(localIndex++) }
//    }
//
//    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
//        if (index > size) indexException(index, size)
//        if (number == 0u) return
//        val newSize = size + number
//        when {
//            newSize > capacity -> capacityOverflowException(capacity)
//            index == size -> {
//                var localIndex = 0u
//                justAddAfterTheEnd(number) { builder(localIndex++) }
//            }
//            else -> {
//                val actualRightPartIndex = actualIndex(index)
//                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
//                val actualInnerPartLeftEndIndex = nextCellIndex[end]
//                val actualInnerPartRightEndIndex: UInt
//                scope {
//                    var currentActualIndex = end
//                    repeat(number) {
//                        currentActualIndex = nextCellIndex[currentActualIndex]
//                        data[currentActualIndex] = Node(this, builder(it), currentActualIndex)
//                    }
//                    actualInnerPartRightEndIndex = currentActualIndex
//                }
//
//                nextCellIndex[end] = nextCellIndex[actualInnerPartRightEndIndex]
//                previousCellIndex[nextCellIndex[actualInnerPartRightEndIndex]] = end
//                nextCellIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
//                previousCellIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
//                nextCellIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
//                previousCellIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
//            }
//        }
//    }
//
//    override fun removeAt(index: UInt) {
//        if (index >= size) indexException(index, size)
//        justRemoveAt(actualIndex(index))
//    }
//
//    override fun removeFirst() {
//        justRemoveAt(start)
//    }
//
//    override fun removeLast() {
//        justRemoveAt(end)
//    }
//
//    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
//        val newSize: UInt
//        val firstCellToClear: UInt
//        scope {
//            var checkingActualMark = 0u
//            var checkingIndex = 0u
//            var resultActualMark = 0u
//            var resultSize = 0u
//            while (checkingIndex < size) {
//                if (!predicate(checkingIndex, data[checkingActualMark]!!.element)) {
//                    data[resultActualMark] = data[checkingActualMark].also { it!!.actualIndex = resultActualMark }
//                    resultActualMark = nextCellIndex[resultActualMark]
//                    resultSize++
//                }
//                checkingActualMark = nextCellIndex[checkingActualMark]
//                checkingIndex++
//            }
//            newSize = resultSize
//            firstCellToClear = resultActualMark
//        }
//        var currentActualIndexToClear = firstCellToClear
//        repeat(size - newSize) {
//            data[currentActualIndexToClear]!!.detach()
//            data[currentActualIndexToClear] = null
//            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
//        }
//    }
//
//    override fun iterator(): KoneMutableLinearIterator<Element> =
//        Iterator(
//            list = this,
//            currentIndex = 0u,
//            actualCurrentIndex = start,
//        )
//    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> =
//        Iterator(
//            list = this,
//            currentIndex = index,
//            actualCurrentIndex = if (capacity == 0u) 0u else actualIndex(index),
//        )
//
//    override fun toString(): String = buildString {
//        append('[')
//        if (size > 0u) append(data[start])
//        var currentActualIndex = start
//        for (i in 1u..<size) {
//            currentActualIndex = nextCellIndex[currentActualIndex]
//            append(", ")
//            append(data[currentActualIndex])
//        }
//        append(']')
//    }
//    override fun hashCode(): Int {
//        var hashCode = 1
//        var currentActualIndex = start
//        repeat(size) {
//            hashCode = 31 * hashCode + data[currentActualIndex].hashCode()
//            currentActualIndex = nextCellIndex[currentActualIndex]
//        }
//        return hashCode
//    }
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KoneList<*>) return false
//        if (this.size != other.size) return false
//
//        when (other) {
//            is KoneArrayFixedCapacityLinkedNoddedList<*> -> {
//                var thisCurrentIndex = this.start
//                var otherCurrentIndex = other.start
//                repeat(size) {
//                    if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
//                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
//                    otherCurrentIndex = other.nextCellIndex[otherCurrentIndex]
//                }
//            }
//            else -> {
//                var thisCurrentIndex = this.start
//                val otherIterator = other.iterator()
//                repeat(size) {
//                    if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
//                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
//                }
//            }
//        }
//
//        return true
//    }
//
//    internal class Node<Element>(
//        list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
//        override var element: Element,
//        var actualIndex: UInt,
//    ): KoneMutableListNode<Element> {
//        private var _list: KoneArrayFixedCapacityLinkedNoddedList<Element>? = list
//        private val list: KoneArrayFixedCapacityLinkedNoddedList<Element> get() = _list!!
//
//        fun detach() {
//            _list = null
//        }
//
//        override val index: UInt get() = list.virtualIndex(actualIndex)
//
//        override fun remove() {
//            list.justRemoveAt(actualIndex)
//        }
//
//        override val nextNode: KoneMutableListNode<Element>?
//            get() = if (actualIndex != list.end) list.data[list.nextCellIndex[actualIndex]] else null
//        override val previousNode: KoneMutableListNode<Element>?
//            get() = if (actualIndex != list.start) list.data[list.previousCellIndex[actualIndex]] else null
//
//        override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element> =
//            Iterator(
//                list = list,
//                currentIndex = list.virtualIndex(actualIndex),
//                actualCurrentIndex = actualIndex,
//            )
//        override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element> =
//            Iterator(
//                list = list,
//                currentIndex = list.virtualIndex(actualIndex) + 1u,
//                actualCurrentIndex = list.nextCellIndex[actualIndex],
//            )
//    }
//
//    internal class Iterator<Element>(
//        val list: KoneArrayFixedCapacityLinkedNoddedList<Element>,
//        var currentIndex: UInt,
//        var actualCurrentIndex: UInt,
//    ): KoneMutableLinearIterator<Element> {
//        init {
//            if (currentIndex > list.size) indexException(currentIndex, list.size)
//        }
//
//
//        override fun hasNext(): Boolean = currentIndex < list.size
//        override fun getNext(): Element {
//            if (!hasNext()) indexException(currentIndex, list.size)
//            return list.data[actualCurrentIndex] as Element
//        }
//        override fun moveNext() {
//            if (!hasNext()) indexException(currentIndex, list.size)
//            currentIndex++
//            actualCurrentIndex = list.nextCellIndex[actualCurrentIndex]
//        }
//        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, list.size)
//        override fun setNext(element: Element) {
//            if (!hasNext()) indexException(currentIndex, list.size)
//            list.data[currentIndex]!!.element = element
//        }
//        override fun addNext(element: Element) {
//            if (list.size == list.capacity) capacityOverflowException(list.capacity)
//            if (currentIndex == list.size) list.justAddAfterTheEnd(element)
//            else list.justAddBefore(list.nextCellIndex[actualCurrentIndex], element)
//        }
//        override fun removeNext() {
//            if (!hasNext()) indexException(currentIndex, list.size)
//            list.justRemoveAt(actualCurrentIndex.also { actualCurrentIndex = list.nextCellIndex[actualCurrentIndex] })
//        }
//
//        override fun hasPrevious(): Boolean = currentIndex > 0u
//        override fun getPrevious(): Element {
//            if (!hasPrevious()) indexException(currentIndex, list.size)
//            return list.data[list.previousCellIndex[actualCurrentIndex]] as Element
//        }
//        override fun movePrevious() {
//            if (!hasPrevious()) indexException(currentIndex, list.size)
//            currentIndex--
//            actualCurrentIndex = list.previousCellIndex[actualCurrentIndex]
//        }
//        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, list.size)
//        override fun setPrevious(element: Element) {
//            if (!hasPrevious()) indexException(currentIndex, list.size)
//            list.data[list.previousCellIndex[actualCurrentIndex]]!!.element = element
//        }
//        override fun addPrevious(element: Element) {
//            if (list.size == list.capacity) capacityOverflowException(list.capacity)
//            list.justAddBefore(actualCurrentIndex, element)
//        }
//        override fun removePrevious() {
//            if (!hasPrevious()) indexException(currentIndex, list.size)
//            list.justRemoveAt(list.previousCellIndex[actualCurrentIndex])
//        }
//    }
//}
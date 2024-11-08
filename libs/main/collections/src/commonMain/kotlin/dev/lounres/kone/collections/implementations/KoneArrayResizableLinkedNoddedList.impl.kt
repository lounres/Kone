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
//@Serializable(with = KoneArrayResizableLinkedNoddedListWithContextSerializer::class)
public class KoneArrayResizableLinkedNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Node<Element>?> = KoneMutableArray<Node<Element>?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneMutableNoddedList<Element>, KoneDequeue<Element>, Disposable {
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
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
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
    private fun virtualIndex(actualIndex: UInt): UInt {
        var result = 0u
        var currentActualIndex = actualIndex
        while (currentActualIndex != start) {
            result++
            currentActualIndex = previousCellIndex[currentActualIndex]
        }
        return result
    }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
        for (index in 0u ..< newElementsNumber) {
            end = nextCellIndex[end]
            data[end] = Node(this, generator(index), end)
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: Element): Node<Element> {
        end = nextCellIndex[end]
        val newNode = Node(this, element, end)
        data[end] = newNode
        size += 1u
        return newNode
    }
    private fun justAddBefore(actualIndex: UInt, element: Element): Node<Element> {
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

        val newNode = Node(this, element, freeIndex)
        data[freeIndex] = newNode

        size++
        
        return newNode
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
        if (index >= size) indexException(index, size)
        val result = data[actualIndex(index)]!!
        return result.element
    }
    
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        if (index >= size) indexException(index, size)
        return data[actualIndex(index)]!!
    }

    override fun getFirst(): Element {
        if (isEmpty()) indexException(0u, size) // TODO: Maybe replace with another error
        return data[start]!!.element
    }

    override fun getLast(): Element {
        if (isEmpty()) indexException(size, size) // TODO: Maybe replace with another error
        return data[end]!!.element
    }

    override fun set(index: UInt, element: Element) {
        if (index >= size) indexException(index, size)
        data[actualIndex(index)]!!.element = element
    }

    override fun removeAll() {
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: Element) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextCellIndex[actualIndex]
                    }
                    it == size -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        return if (size == sizeUpperBound) {
            var actualIndex = start
            var newNode: Node<Element>? = null
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextCellIndex[actualIndex]
                    }
                    it == size -> Node(this@KoneArrayResizableLinkedNoddedList, element, it).also { newNode = it }
                    else -> null
                }
            }
            newNode!!
        } else justAddAfterTheEnd(element)
    }

    override fun addFirst(element: Element) {
        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it == 0u -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                    it <= size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextCellIndex[actualIndex]
                    }
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
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextCellIndex[actualIndex]
                    }
                    it == size -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }

    override fun addAt(index: UInt, element: Element) {
        if (index > size) indexException(index, size)
        when {
            size == sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
                        it == index -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                        it <= size -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
                        else -> null
                    }
                }
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }
    
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (index > size) indexException(index, size)
        return when {
            size == sizeUpperBound -> {
                var actualIndex = start
                var newNode: Node<Element>? = null
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
                        it == index -> Node(this@KoneArrayResizableLinkedNoddedList, element, it).also { newNode = it }
                        it <= size -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
                        else -> null
                    }
                }
                newNode!!
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
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextCellIndex[actualIndex]
                    }
                    localIndex < number -> Node(this@KoneArrayResizableLinkedNoddedList, builder(localIndex), it).also { localIndex++ }
                    else -> null
                }
            }
        } else {
            var localIndex = 0u
            justAddAfterTheEnd(number) { builder(localIndex++) }
        }
    }

    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (index > size) indexException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                var localIndex = 0u
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
                        localIndex < number -> Node(this@KoneArrayResizableLinkedNoddedList, builder(localIndex++), it)
                        it < newSize -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextCellIndex[actualIndex]
                        }
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
                        data[currentActualIndex] = Node(this, builder(localIndex), currentActualIndex)
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
        if (index >= size) indexException(index, size)
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
        if (size == 0u) indexException(0u, size) // TODO: Maybe replace with another error
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
        if (size == 0u) indexException(size, size) // TODO: Maybe replace with another error
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
            is KoneArrayResizableLinkedNoddedList<*> -> {
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
    
    internal class Node<Element>(
        list: KoneArrayResizableLinkedNoddedList<Element>,
        override var element: Element,
        var actualIndex: UInt,
    ) : KoneMutableListNode<Element>, Disposable {
        private var _list: KoneArrayResizableLinkedNoddedList<Element>? = list
        val list: KoneArrayResizableLinkedNoddedList<Element> get() = _list!!
        
        override val index: UInt get() = list.virtualIndex(actualIndex)
        
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
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var actualCurrentIndex = actualIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexException(currentIndex, size)
            return data[actualCurrentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexException(currentIndex, size)
            data[actualCurrentIndex]!!.element = element
        }
        override fun addNext(element: Element) {
            when {
                size == sizeUpperBound -> {
                    var actualIndex = start
                    reinitializeBoundsAndData(size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = nextCellIndex[actualIndex]
                            }
                            it == currentIndex -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                            it <= size -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = nextCellIndex[actualIndex]
                            }
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
            if (!hasNext()) indexException(currentIndex, size)
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
            if (!hasPrevious()) indexException(currentIndex, size)
            return data[previousCellIndex[actualCurrentIndex]] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            currentIndex--
            actualCurrentIndex = previousCellIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexException(currentIndex, size)
            data[previousCellIndex[actualCurrentIndex]]!!.element = element
        }
        override fun addPrevious(element: Element) {
            justAddBefore(actualCurrentIndex, element)
        }
        override fun removePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            justRemoveAt(previousCellIndex[actualCurrentIndex])
        }
    }
}
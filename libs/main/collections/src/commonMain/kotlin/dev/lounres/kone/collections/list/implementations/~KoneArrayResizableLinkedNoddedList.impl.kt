/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.isEmpty
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable
import kotlin.math.max


@Serializable(with = KoneArrayResizableLinkedNoddedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayResizableLinkedNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    internal var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    internal var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    internal var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray<Node<Element>?>(sizeUpperBound) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
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
    
    private var _data: KoneMutableArray<Node<Element>?>? = data
    internal var data: KoneMutableArray<Node<Element>?>
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
        for (i in 0u ..< size) {
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
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
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
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextNodeIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u }
        previousNodeIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
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
                for (i in index ..< size-1u) {
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
        for (index in 0u ..< newElementsNumber) {
            end = nextNodeIndex[end]
            data[end] = Node(this, generator(index), end)
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: Element): Node<Element> {
        end = nextNodeIndex[end]
        val newNode = Node(this, element, end)
        data[end] = newNode
        size += 1u
        return newNode
    }
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
        val result = data[actualIndex(index)]!!
        return result.element
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
        reinitializeBoundsAndData(0u) { null }
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    it == oldSize -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        return if (size == sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            var newNode: Node<Element>? = null
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    it == oldSize -> Node(this@KoneArrayResizableLinkedNoddedList, element, it).also { newNode = it }
                    else -> null
                }
            }
            newNode!!
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
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it == index -> Node(this@KoneArrayResizableLinkedNoddedList, element, it)
                        it <= oldSize -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
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
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        return when {
            size == sizeUpperBound -> {
                val oldSize = size
                var actualIndex = start
                var newNode: Node<Element>? = null
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it == index -> Node(this@KoneArrayResizableLinkedNoddedList, element, it).also { newNode = it }
                        it <= oldSize -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
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
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < oldSize -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    it < oldSize + number -> Node(this@KoneArrayResizableLinkedNoddedList, builder(it - oldSize), it)
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
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it < index + number -> Node(this@KoneArrayResizableLinkedNoddedList, builder(it - index), it)
                        it < newSize -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
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
                        data[currentActualIndex] = Node(this, builder(localIndex), currentActualIndex)
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
                        get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
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
                if (!predicate(checkingIndex, data[checkingActualMark]!!.element)) {
                    data[resultActualMark] = data[checkingActualMark].also { it!!.actualIndex = resultActualMark }
                    resultActualMark = nextNodeIndex[resultActualMark]
                    resultSize++
                } else {
                    data[checkingActualMark]!!.detach()
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
                    it < newSize -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
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
                actualCurrentIndex = actualIndex(index),
            )
        }

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[start]!!.element)
        var currentIndex = start
        for (i in 1u..<size) {
            currentIndex = nextNodeIndex[currentIndex]
            append(", ")
            append(data[currentIndex]!!.element)
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i]!!.element.hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayResizableLinkedNoddedList<*> -> {
                var thisCurrentIndex = this.start
                var otherCurrentIndex = other.start
                repeat(size) {
                    if (this.data[thisCurrentIndex]!!.element != other.data[otherCurrentIndex]!!.element) return false
                    thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                    otherCurrentIndex = other.nextNodeIndex[otherCurrentIndex]
                }
            }
            else -> {
                var thisCurrentIndex = this.start
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[thisCurrentIndex]!!.element != otherIterator.getAndMoveNext()) return false
                    thisCurrentIndex = this.nextNodeIndex[thisCurrentIndex]
                }
            }
        }

        return true
    }
    
    @PublishedApi
    internal class Node<Element>(
        override var element: Element,
        var actualIndex: UInt,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        private var _list: KoneArrayResizableLinkedNoddedList<Element>? = null
        internal var list: KoneArrayResizableLinkedNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        override val index: UInt get() = list.virtualIndex(actualIndex)
        
        fun detach() {
            _list = null
            isDetached = true
        }
        
        constructor(list: KoneArrayResizableLinkedNoddedList<Element>, element: Element, actualIndex: UInt) : this(element, actualIndex) {
            this.list = list
        }
        
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
                actualIndex != list.end -> list.data[list.nextNodeIndex[actualIndex]]
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
    }

    internal class Iterator<Element>(
        val list: KoneArrayResizableLinkedNoddedList<Element>,
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
            when {
                list.size == list.sizeUpperBound -> {
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(list.size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = list.nextNodeIndex[actualIndex]
                            }
                            it == currentIndex -> Node(list, element, it)
                            it <= list.size -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = list.nextNodeIndex[actualIndex]
                            }
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
                                get(actualIndex).also { node ->
                                    node!!.actualIndex = it
                                    actualIndex = list.nextNodeIndex[actualIndex]
                                }
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
            if (list.isDisposed) disposedInstanceException()
            when {
                list.size == list.sizeUpperBound -> {
                    val oldSize = list.size
                    var actualIndex = list.start
                    list.reinitializeBoundsAndData(list.size + 1u) {
                        when {
                            it < currentIndex -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = list.nextNodeIndex[actualIndex]
                            }
                            it == currentIndex -> Node(list = list, element = element, actualIndex = it)
                            it <= oldSize -> get(actualIndex).also { node ->
                                node!!.actualIndex = it
                                actualIndex = list.nextNodeIndex[actualIndex]
                            }
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
                                get(actualIndex).also { node ->
                                    node!!.actualIndex = it
                                    actualIndex = list.nextNodeIndex[actualIndex]
                                }
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
    }
}
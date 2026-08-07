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
import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneGrowableMutableNoddedList
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedListIterator
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


@Serializable(with = KoneArrayGrowableLinkedNoddedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayGrowableLinkedNoddedList<Element> internal constructor(
    size: UInt,
    internal var sizeUpperBound: UInt = powerOf2ArraySizeGreaterOrEqualTo(size),
    data: KoneMutableArray<Node<Element>?> = KoneMutableArray.generate(sizeUpperBound) { null },
    nextNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u },
    previousNodeIndex: KoneMutableUIntArray = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneGrowableMutableNoddedList<Element>, Disposable {
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
        repeat(size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextNodeIndex[currentActualIndexToClear]
        }
    }
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
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneGrowableArrayList implementation can not allocate array of size more than 2^31")
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                sizeUpperBound = if (sizeUpperBound == 0u) 1u else sizeUpperBound shl 1
            }
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
        val oldData = data
        data = KoneMutableArray.generate(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextNodeIndex = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == sizeUpperBound - 1u) 0u else it + 1u }
        previousNodeIndex = KoneMutableUIntArray.generate(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Node<Element>?>.(index: UInt) -> Node<Element>?) {
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
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    else -> null
                }
            }
        }
    }

    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> Element) {
        for (index in 0u ..< newElementsNumber) {
            end = nextNodeIndex[end]
            data[end] = Node(this, generator(index), end)
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
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    it == size -> Node(this@KoneArrayGrowableLinkedNoddedList, element, it)
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(element)
        }
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        return if (size == sizeUpperBound) {
            val newNode = Node(this@KoneArrayGrowableLinkedNoddedList, element, size)
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { node ->
                        node!!.actualIndex = it
                        actualIndex = nextNodeIndex[actualIndex]
                    }
                    it == size -> newNode
                    else -> null
                }
            }
            newNode
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
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it == index -> Node(this@KoneArrayGrowableLinkedNoddedList, element, it)
                        it <= size -> get(actualIndex).also { node ->
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
                val newNode = Node(this, element, index)
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it == index -> newNode
                        it <= size -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        else -> null
                    }
                }
                newNode
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }
    }
    
    @DelicateSeveralElementsInserterAPI
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (number == 0u) return EmptySeveralElementsInserter
        val newSize = size + number
        return when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        it < index + number -> null
                        it < newSize -> get(actualIndex).also { node ->
                            node!!.actualIndex = it
                            actualIndex = nextNodeIndex[actualIndex]
                        }
                        else -> null
                    }
                }
                ReinitializingSeveralElementsInserter(this, index, number)
            }
            index == size -> {
                size += number
                AppendingToTheEndSeveralElementsInserter(this, number)
            }
            else -> {
                InsertingInsideSeveralElementsInserter(this, index, number)
            }
        }
    }

    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        justRemoveAt(actualIndex(index))
    }
    
    @DelicateBulkElementsRemoverAPI
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> = BulkElementsRemover(this)

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
        var hashCode = 1
        var currentActualIndex = start
        repeat(size) {
            hashCode = 31 * hashCode + data[currentActualIndex]!!.element.hashCode()
            currentActualIndex = nextNodeIndex[currentActualIndex]
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        if (other is KoneArrayGrowableLinkedNoddedList<*>) {
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
        var actualIndex: UInt,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        private var _list: KoneArrayGrowableLinkedNoddedList<Element>? = null
        internal var list: KoneArrayGrowableLinkedNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        override val index: UInt get() = list.virtualIndex(actualIndex)
        
        fun detach() {
            _list = null
            isDetached = true
        }
        
        constructor(list: KoneArrayGrowableLinkedNoddedList<Element>, element: Element, actualIndex: UInt) : this(element, actualIndex) {
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
    
    // TODO: Review operations. Looks like they are incorrect.
    internal class Iterator<Element>(
        val list: KoneArrayGrowableLinkedNoddedList<Element>,
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
                            it == currentIndex -> Node(list = list, element = element, actualIndex = currentIndex)
                            it <= oldSize -> get(actualIndex).also { node ->
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
    
    internal object EmptySeveralElementsInserter : KoneSeveralElementsInserter<Any?> {
        override val newElementsNumber: UInt get() = 0u
        override fun insert(element: Any?) {
            severalElementsInserterOverflowException()
        }
        override fun close() {}
    }
    
    internal class ReinitializingSeveralElementsInserter<Element>(
        val list: KoneArrayGrowableLinkedNoddedList<Element>,
        val newElementsStartIndex: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        var currentIndex = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            list.data[newElementsStartIndex + currentIndex] = Node(list, element, currentIndex)
            currentIndex++
        }
        
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
        }
    }
    
    internal class AppendingToTheEndSeveralElementsInserter<Element>(
        val list: KoneArrayGrowableLinkedNoddedList<Element>,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        var currentIndex: UInt = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            list.end = list.nextNodeIndex[list.end]
            list.data[list.end] = Node(list, element, list.end)
        }
        
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
        }
    }
    
    internal class InsertingInsideSeveralElementsInserter<Element>(
        val list: KoneArrayGrowableLinkedNoddedList<Element>,
        val newElementsStartIndex: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        val actualRightPartIndex: UInt = list.actualIndex(newElementsStartIndex)
        val actualInnerPartLeftEndIndex: UInt = list.nextNodeIndex[list.end]
        var currentActualIndex = list.end
        var currentIndex: UInt = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            currentActualIndex = list.nextNodeIndex[currentActualIndex]
            list.data[currentActualIndex] = Node(list, element, currentActualIndex)
        }
        
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
            
            list.nextNodeIndex[list.end] = list.nextNodeIndex[currentActualIndex]
            list.previousNodeIndex[list.nextNodeIndex[currentActualIndex]] = list.end
            val actualLeftPartIndex = list.previousNodeIndex[actualRightPartIndex]
            list.nextNodeIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
            list.previousNodeIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
            list.previousNodeIndex[actualRightPartIndex] = currentActualIndex
            list.nextNodeIndex[currentActualIndex] = actualRightPartIndex
            
            if (newElementsStartIndex == 0u) list.start = actualInnerPartLeftEndIndex
            list.size += newElementsNumber
        }
    }
    
    internal class BulkElementsRemover<Element>(
        val list: KoneArrayGrowableLinkedNoddedList<Element>,
    ) : KoneBulkElementsRemover<Element> {
        var checkingActualMark = list.start
        var checkingIndex = 0u
        var resultActualMark = list.start
        var resultSize = 0u
        
        override fun hasNext(): Boolean = checkingIndex < list.size
        
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return list.data[checkingActualMark]!!.element
        }
        
        override fun nextIndex(): UInt {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return checkingIndex
        }
        
        override fun moveNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            list.data[resultActualMark] = list.data[checkingActualMark]
            resultActualMark = list.nextNodeIndex[resultActualMark]
            resultSize++
            checkingActualMark = list.nextNodeIndex[checkingActualMark]
            checkingIndex++
        }
        
        override fun removeNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            checkingActualMark = list.nextNodeIndex[checkingActualMark]
            checkingIndex++
        }
        
        override fun close() {
            while (hasNext()) moveNext() // TODO: Optimise (like in KoneFixedCapacityLinkedArrayListSerializer) to not use this cycle.
            list.end = list.previousNodeIndex[resultActualMark]
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
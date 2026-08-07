/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterator.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


// TODO: Actualize time complexity table
/**
 * Represents a list that is laid out consecutively on a prefix of array of fixed capacity.
 *
 * # Implementation details
 *
 * This implementation holds a [KoneMutableArray] of provided capacity
 * and proxies all operations straight to its prefix of the provided [size].
 * Any getting or setting is operated on corresponding indices of the array.
 * Any addition or removal is operated on corresponding indices of the array
 * moving values with greater indices.
 *
 * That's why it has perfect access time complexity
 * while having bad mutability time complexity.
 *
 * ## Time complexity of operations
 *
 * | Operation                                                           | Worst case                                  | Average                                     |
 * |---------------------------------------------------------------------|---------------------------------------------|---------------------------------------------|
 * | [size]                                                              | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [get]                                                               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [set]                                                               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [add]                                                               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [addAt]                                                             | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [addSeveral]                                                        | \(\Theta(\mathrm{number})\)                 | \(\Theta(\mathrm{number})\)                 |
 * | [addSeveralAt]                                                      | \(\Theta(\mathrm{size} + \mathrm{number})\) | \(\Theta(\mathrm{size} + \mathrm{number})\) |
 * | [removeAt]                                                          | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThat]                                                     | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAllThatIndexed]                                              | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [removeAll]                                                         | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator]                                                          | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iteratorFrom]                                                      | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasNext][KoneMutableLinearIterator.hasNext]               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.hasPrevious][KoneMutableLinearIterator.hasPrevious]       | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getNext][KoneMutableLinearIterator.getNext]               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.getPrevious][KoneMutableLinearIterator.getPrevious]       | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.moveNext][KoneMutableLinearIterator.moveNext]             | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.movePrevious][KoneMutableLinearIterator.movePrevious]     | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setNext][KoneMutableLinearIterator.setNext]               | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.setPrevious][KoneMutableLinearIterator.setPrevious]       | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.addNext][KoneMutableLinearIterator.addNext]               | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.addPrevious][KoneMutableLinearIterator.addPrevious]       | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.removeNext][KoneMutableLinearIterator.removeNext]         | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.removePrevious][KoneMutableLinearIterator.removePrevious] | \(\Theta(\mathrm{size})\)                   | \(\Theta(\mathrm{size})\)                   |
 * | [iterator.nextIndex][KoneMutableLinearIterator.nextIndex]           | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 * | [iterator.previousIndex][KoneMutableLinearIterator.previousIndex]   | \(\Theta(1)\)                               | \(\Theta(1)\)                               |
 *
 * @usesMathJax
 */
@Suppress("UNCHECKED_CAST")
@Serializable(with = KoneArrayFixedCapacityListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayFixedCapacityList<Element> @PublishedApi internal constructor(
    size: UInt,
    data: KoneMutableArray<Any?>,
): KoneMutableList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal val data: KoneMutableArray<Any?>
        get() = if (isDisposed) disposedInstanceException() else _data!!
    
    override fun dispose() {
        if (isDisposed) return
        repeat(size) { data[it] = null }
        _data = null
        isDisposed = true
    }
    
    internal val capacity: UInt get() = data.size
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set

    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index] = element
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == capacity) capacityOverflowException(capacity)
        data[size] = element
        size++
    }
    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (size == capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
        data[index] = element
        size++
    }
    @DelicateSeveralElementsInserterAPI
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i]
        size = newSize
        return SeveralElementsInserter(
            list = this,
            currentListIndex = index,
            newElementsNumber = number,
        )
    }
    
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }
    @DelicateBulkElementsRemoverAPI
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> {
        if (isDisposed) disposedInstanceException()
        return BulkElementsRemover(this)
    }
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        repeat(size) { data[it] = null }
        size = 0u
    }
    
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }
    override fun iterator(): KoneMutableLinearIterator<Element> =
        if (isDisposed) disposedInstanceException() else Iterator(this, 0u)

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + data[it].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        if (other is KoneArrayFixedCapacityList<*>) {
            repeat(size) {
                if (this.data[it] != other.data[it]) return false
            }
        } else {
            val otherIterator = other.iterator()
            for (i in 0u ..< size) {
                if (this.data[i] != otherIterator.getAndMoveNext()) return false
            }
        }

        return true
    }

    internal class Iterator<Element>(
        val list: KoneArrayFixedCapacityList<Element>,
        var currentIndex: UInt
    ): KoneMutableLinearIterator<Element> {
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[currentIndex] = element
        }
        override fun addNext(element: Element) {
            if (list.isDisposed) disposedInstanceException()
            list.addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            list.removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[currentIndex - 1u] = element
        }
        override fun addPrevious(element: Element) {
            if (list.isDisposed) disposedInstanceException()
            list.addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.removeAt(--currentIndex)
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
    
    internal class SeveralElementsInserter<Element>(
        val list: KoneArrayFixedCapacityList<Element>,
        var currentListIndex: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        var currentIndex: UInt = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsNumber) severalElementsInserterOverflowException()
            list.data[currentListIndex] = element
            currentListIndex++
            currentIndex++
        }
        
        override fun close() {
            if (currentIndex != newElementsNumber) severalElementsInserterElementsLackException()
        }
    }
    
    internal class BulkElementsRemover<Element>(
        val list: KoneArrayFixedCapacityList<Element>,
    ) : KoneBulkElementsRemover<Element> {
        var checkingMark = 0u
        var resultMark = 0u
        
        override fun hasNext(): Boolean = checkingMark < list.size
        
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return list.data[checkingMark] as Element
        }
        
        override fun nextIndex(): UInt {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return checkingMark
        }
        
        override fun moveNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            list.data[resultMark] = list.data[checkingMark]
            resultMark++
            checkingMark++
        }
        
        override fun removeNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            checkingMark++
        }
        
        override fun close() {
            while (hasNext()) moveNext()
            for (i in resultMark ..< list.size) list.data[i] = null
            list.size = resultMark
        }
    }
}
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
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        repeat(number) { data[size + it] = builder(it) }
        size = newSize
    }
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i]
        repeat(number) { data[index + it] = builder(it) }
        size = newSize
    }
    
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }
    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        val newSize: UInt
        scope {
            var checkingMark = 0u
            var resultMark = 0u
            while (checkingMark < size) {
                if (!predicate(checkingMark, data[checkingMark] as Element)) {
                    data[resultMark] = data[checkingMark]
                    resultMark++
                }
                checkingMark++
            }
            newSize = resultMark
        }
        for (i in newSize ..< size) data[i] = null
        size = newSize
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

        when (other) {
            is KoneArrayFixedCapacityList<*> ->
                repeat(size) {
                    if (this.data[it] != other.data[it]) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
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
    }
}
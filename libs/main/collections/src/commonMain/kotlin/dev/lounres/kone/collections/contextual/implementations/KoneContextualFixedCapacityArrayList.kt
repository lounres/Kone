/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.misc.scope


@Suppress("UNCHECKED_CAST")
public class KoneContextualFixedCapacityArrayList<E> internal constructor(
    size: UInt,
    private val capacity: UInt = size,
    private var data: KoneContextualMutableArray<Any?> = KoneContextualMutableArray<Any?>(capacity) { null },
): KoneContextualMutableIterableList<E, Equality<E>> {
    override var size: UInt = size
        private set

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[index] = element
    }

    override fun clear() {
        for (i in 0u..<size) data[i] = null
        size = 0u
    }
    override fun addAtTheEnd(element: E) {
        if (size == capacity) capacityOverflowException(capacity)
        data[size] = element
        size++
    }
    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
        if (size == capacity) capacityOverflowException(capacity)
        for (i in (size-1u) downTo index) data[i+1u] = data[i]
        data[index] = element
        size++
    }
    override fun addAllAtTheEnd(elements: KoneContextualIterableCollection<E, *>) {
        val newSize = size + elements.size
        if (newSize > capacity) capacityOverflowException(capacity)
        var index = size
        val iter = elements.iterator()
        while (iter.hasNext()) {
            data[index] = iter.getAndMoveNext()
            index++
        }
        size = newSize
    }
    override fun addAllAt(index: UInt, elements: KoneContextualIterableCollection<E, *>) {
        if (index > size) indexException(index, size)
        val elementsSize = elements.size
        val newSize = size + elementsSize
        if (newSize > capacity) capacityOverflowException(capacity)
        for (i in (size-1u) downTo index) data[i + elementsSize] = data[i]
        var index = index
        val iter = elements.iterator()
        while (iter.hasNext()) {
            data[index] = iter.getAndMoveNext()
            index++
        }
        size = newSize
    }
    context(Equality<E>)
    override fun remove(element: E) {
        val index = this.indexOf(element)
        if (index == size) return
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        val newSize: UInt
        scope {
            var checkingMark = 0u
            var resultMark = 0u
            while (checkingMark < size) {
                if (!predicate(checkingMark, data[checkingMark] as E)) {
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

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneContextualList<*, *>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneContextualFixedCapacityArrayList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
                }
            is KoneContextualIterableList<*, *> -> {
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
            }
            else ->
                for (i in 0u..<size) {
                    if (this.data[i] != other[i]) return false
                }
        }

        return true
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return data[currentIndex] as E
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, size)
            data[currentIndex] = element
        }
        override fun addNext(element: E) {
            addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return data[currentIndex - 1u] as E
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, size)
            data[currentIndex - 1u] = element
        }
        override fun addPrevious(element: E) {
            addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            removeAt(--currentIndex)
        }
    }
}
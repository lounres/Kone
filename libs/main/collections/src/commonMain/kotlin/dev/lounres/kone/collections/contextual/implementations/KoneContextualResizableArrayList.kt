/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.misc.scope
import kotlin.math.max


@Suppress("UNCHECKED_CAST")
public class KoneContextualResizableArrayList<E> internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneContextualMutableArray<Any?> = KoneContextualMutableArray<Any?>(sizeUpperBound) { null },
): KoneContextualMutableIterableList<E, Equality<E>> {
    override var size: UInt = size
        private set

    private fun KoneContextualMutableArray<Any?>.dispose(size: UInt) {
        for (i in 0u ..< size) this[i] = null
    }
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneGrowableArrayList implementation can not allocate array of size more than 2^31")
        when {
            newSize > sizeUpperBound -> {
                while (newSize > sizeUpperBound) {
                    dataSizeNumber++
                    sizeLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
            }
            newSize < sizeLowerBound -> {
                while (newSize < sizeUpperBound) {
                    dataSizeNumber--
                    sizeLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
            }
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneContextualMutableArray<Any?>.(index: UInt) -> Any?) {
        val oldData = data
        data = KoneContextualMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneContextualMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
    }

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[index] = element
    }

    override fun clear() {
        dataSizeNumber = 1u
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        reinitializeData { null }
        size = 0u
    }
    override fun addAtTheEnd(element: E) {
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(it)
                    it == size -> element
                    else -> null
                }
            }
        } else {
            data[size] = element
            size++
        }
    }
    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> element
                    it <= size -> get(it-1u)
                    else -> null
                }
            }
        } else {
            for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = element
            size++
        }
    }
    override fun addAllAtTheEnd(elements: KoneContextualIterableCollection<E, *>) {
        val newSize = size + elements.size
        if (newSize > sizeUpperBound) {
            val iter = elements.iterator()
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(it)
                    iter.hasNext() -> iter.getAndMoveNext()
                    else -> null
                }
            }
        } else {
            var index = size
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.getAndMoveNext()
                index++
            }
            size = newSize
        }
    }
    override fun addAllAt(index: UInt, elements: KoneContextualIterableCollection<E, *>) {
        if (index > size) indexException(index, size)
        val newSize = size + elements.size
        val elementsSize = elements.size
        if (newSize > sizeUpperBound) {
            val iter = elements.iterator()
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    iter.hasNext() -> iter.getAndMoveNext()
                    it < newSize -> get(it-elementsSize)
                    else -> null
                }
            }
        } else {
            for (i in (size-1u) downTo index) data[i + elementsSize] = data[i]
            var index = index
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.getAndMoveNext()
                index++
            }
            size = newSize
        }
    }
    context(Equality<E>)
    override fun remove(element: E) {
        val index = this.indexOf(element)
        if (index == size) return
        removeAt(index)
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    it < newSize -> get(it+1u)
                    else -> null
                }
            }
        } else {
            for (i in index..<newSize) data[i] = data[i + 1u]
            data[size - 1u] = null
            size = newSize
        }
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
            newSize = checkingMark
        }
        if (newSize < sizeLowerBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(it)
                    else -> null
                }
            }
        } else {
            for (i in newSize ..< size) data[i] = null
            size = newSize
        }
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
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneContextualList<*, *>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneContextualResizableArrayList<*> ->
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
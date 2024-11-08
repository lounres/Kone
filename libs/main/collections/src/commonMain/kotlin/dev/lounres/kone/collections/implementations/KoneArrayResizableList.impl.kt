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
//@Serializable(with = KoneResizableArrayListWithContextSerializer::class)
public class KoneArrayResizableList<Element> @PublishedApi internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
) : KoneMutableList<Element>, Disposable {
    override var size: UInt = size
        private set

    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        repeat(size) { this[it] = null }
    }
    override fun dispose() {
        data.dispose(size)
    }
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableArrayList implementation can not allocate array of size more than 2^31")
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
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
    }

    override fun get(index: UInt): Element {
        if (index >= size) indexException(index, size)
        return data[index] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (index >= size) indexException(index, size)
        data[index] = element
    }

    override fun removeAll() {
        dataSizeNumber = 1u
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        reinitializeData { null }
        size = 0u
    }
    override fun add(element: Element) {
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
    override fun addAt(index: UInt, element: Element) {
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
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = element
            size++
        }
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(it)
                    localIndex < number -> builder(localIndex++)
                    else -> null
                }
            }
        } else {
            var index = size
            for (localIndex in 0u ..< number) data[index++] = builder(localIndex)
            size = newSize
        }
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (index > size) indexException(index, size)
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    localIndex < number -> builder(localIndex++)
                    it < newSize -> get(it - number)
                    else -> null
                }
            }
        } else {
            for (i in (size-1u) downTo index) data[i + number] = data[i]
            var index = index
            repeat(number) { data[index++] = builder(it) }
            size = newSize
        }
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

    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
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

    override fun iterator(): KoneMutableLinearIterator<Element> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> = Iterator(index)

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
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArrayResizableList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
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

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<Element> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexException(currentIndex, size)
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexException(currentIndex, size)
            data[currentIndex] = element
        }
        override fun addNext(element: Element) {
            addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) indexException(currentIndex, size)
            removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexException(currentIndex, size)
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexException(currentIndex, size)
            data[currentIndex - 1u] = element
        }
        override fun addPrevious(element: Element) {
            addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            removeAt(--currentIndex)
        }
    }
}
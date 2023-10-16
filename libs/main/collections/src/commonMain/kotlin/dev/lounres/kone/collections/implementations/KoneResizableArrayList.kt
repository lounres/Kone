/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


private /*const*/ val powersOf2: KoneUIntArray = KoneUIntArray(33u) { if (it == 0u) 0u else 1u shl (it.toInt() - 1) }

@Suppress("UNCHECKED_CAST")
public class KoneResizableArrayList<E>: KoneMutableIterableList<E> {
    override var size: UInt = 0u
        private set
    private var dataSizeNumber = 1u
    private var sizeLowerBound = 0u
    private var sizeUpperBound = 2u
    private var data = KoneMutableArray<Any?>(sizeUpperBound) { null }
    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        for (i in 0u ..< size) this[i] = null
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
        data.dispose(size)
        data = KoneMutableArray(sizeUpperBound) { null }
        size = 0u
    }
    override fun add(element: E) {
        if (size == sizeUpperBound) {
            dataSizeNumber++
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < size -> oldData[it]
                    it == size -> element
                    else -> null
                }
            }
            oldData.dispose(size)
            size++
        } else {
            data[size] = element
            size++
        }
    }
    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
        if (size == sizeUpperBound) {
            dataSizeNumber++
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < index -> oldData[it]
                    it == index -> element
                    it <= size -> oldData[it-1u]
                    else -> null
                }
            }
            oldData.dispose(size)
            size++
        } else {
            for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = element
            size++
        }
    }
    override fun addAll(elements: KoneIterableCollection<E>) {
        val newSize = size + elements.size
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                dataSizeNumber++
                sizeUpperBound = powersOf2[dataSizeNumber+1u]
            }
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            val iter = elements.iterator()
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < size -> oldData[it]
                    iter.hasNext() -> iter.next()
                    else -> null
                }
            }
            oldData.dispose(size)
            size = newSize
        } else {
            var index = size
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.next()
                index++
            }
            size = newSize
        }
    }
    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
        if (index > size) indexException(index, size)
        val newSize = size + elements.size
        val elementsSize = elements.size
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                dataSizeNumber++
                sizeUpperBound = powersOf2[dataSizeNumber+1u]
            }
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            val iter = elements.iterator()
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < index -> oldData[it]
                    iter.hasNext() -> iter.next()
                    it < newSize -> oldData[it-elementsSize]
                    else -> null
                }
            }
            oldData.dispose(size)
            size = newSize
        } else {
            for (i in (size-1u) downTo index) data[i + elementsSize] = data[i]
            var index = index
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.next()
                index++
            }
            size = newSize
        }
    }
    override fun remove(element: E) {
        val index = data.indexOf(element)
        if (index == size) return
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            dataSizeNumber--
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < index -> oldData[it]
                    it < newSize -> oldData[it+1u]
                    else -> null
                }
            }
            oldData.dispose(size)
            size = newSize
        } else {
            for (i in index..<newSize) data[i] = data[i + 1u]
            data[size - 1u] = null
            size = newSize
        }
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            dataSizeNumber--
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < index -> oldData[it]
                    it < newSize -> oldData[it+1u]
                    else -> null
                }
            }
            oldData.dispose(size)
            size = newSize
        } else {
            for (i in index..<newSize) data[i] = data[i + 1u]
            data[size - 1u] = null
            size = newSize
        }
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        val newSize: UInt
        run {
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
            while (newSize < sizeLowerBound) {
                dataSizeNumber--
                sizeLowerBound = powersOf2[dataSizeNumber-1u]
            }
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < newSize -> oldData[it]
                    else -> null
                }
            }
            oldData.dispose(size)
            size = newSize
        } else {
            for (i in newSize ..< size) data[i] = null
            size = newSize
        }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size >= 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
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
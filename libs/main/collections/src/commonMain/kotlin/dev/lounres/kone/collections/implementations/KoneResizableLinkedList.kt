/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


private /*const*/ val powersOf2: KoneUIntArray = KoneUIntArray(33u) { if (it == 0u) 0u else 1u shl (it.toInt() - 1) }

@Suppress("UNCHECKED_CAST")
public class KoneResizableLinkedList<E>: KoneMutableIterableList<E> {
    override var size: UInt = 0u
        private set
    private var dataSizeNumber = 1u
    private var sizeLowerBound = 0u
    private var sizeUpperBound = 2u
    private var data = KoneMutableArray<Any?>(sizeUpperBound) { null }
    private var nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
    private var previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
    private var start = 0u
    private var end = sizeUpperBound - 1u
    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        for (i in 0u ..< size) this[i] = null
    }
    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextCellIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = 0u
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = 0u
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
        }

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return data[actualIndex(index)] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[actualIndex(index)] = element
    }

    override fun clear() {
        size = 0u
        dataSizeNumber = 1u
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        data.dispose(size)
        data = KoneMutableArray(sizeUpperBound) { null }
        nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
        end = sizeUpperBound - 1u
    }

    override fun add(element: E) {
        if (size == sizeUpperBound) {
            dataSizeNumber++
            sizeLowerBound = powersOf2[dataSizeNumber-1u]
            sizeUpperBound = powersOf2[dataSizeNumber+1u]
            val oldData = data
            var actualIndex = start
            data = KoneMutableArray(sizeUpperBound) {
                when {
                    it < size -> oldData[actualIndex].also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
            oldData.dispose(size)
            nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
            previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
            start = 0u
            end = size
            size++
        } else {
            end = nextCellIndex[end]
            data[end] = element
            size++
        }
    }

    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
        when {
            size == sizeUpperBound -> {
                dataSizeNumber++
                sizeLowerBound = powersOf2[dataSizeNumber-1u]
                sizeUpperBound = powersOf2[dataSizeNumber+1u]
                val oldData = data
                var actualIndex = start
                data = KoneMutableArray(sizeUpperBound) {
                    when {
                        it < index -> oldData[actualIndex].also { actualIndex = nextCellIndex[actualIndex] }
                        it == index -> element
                        it <= size -> oldData[actualIndex].also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
                oldData.dispose(size)
                nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
                previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
                start = 0u
                end = size
                size++
            }
            index == size -> {
                end = nextCellIndex[end]
                data[end] = element
                size++
            }
            else -> {
                val freeIndex = nextCellIndex[end]
                val indexAfterTheFreeIndex = nextCellIndex[freeIndex]
                nextCellIndex[end] = indexAfterTheFreeIndex
                previousCellIndex[indexAfterTheFreeIndex] = end

                val actualIndex = actualIndex(index)
                val indexBeforeTheActualIndex = previousCellIndex[actualIndex]
                nextCellIndex[freeIndex] = actualIndex
                previousCellIndex[freeIndex] = indexBeforeTheActualIndex
                nextCellIndex[indexBeforeTheActualIndex] = freeIndex
                previousCellIndex[actualIndex] = freeIndex

                if (index == 0u) start = freeIndex

                size++
            }
        }
    }
    override fun addAll(elements: KoneIterableCollection<E>) { TODO("Not yet implemented")
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

    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) { TODO("Not yet implemented")
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
    override fun remove(element: E) { TODO("Not yet implemented")
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
    override fun removeAt(index: UInt) { TODO("Not yet implemented")
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

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) { TODO("Not yet implemented")
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
        if (size >= 0u) append(data[start])
        var currentIndex = start
        for (i in 1u..<size) {
            currentIndex = nextCellIndex[currentIndex]
            append(", ")
            append(data[currentIndex])
        }
        append(']')
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var actualCurrentIndex = actualIndex(currentIndex)
        var lastIndex = UInt.MAX_VALUE
        var actualLastIndex = UInt.MAX_VALUE
        override fun hasNext(): Boolean = currentIndex < size
        override fun next(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            lastIndex = currentIndex++
            actualLastIndex = actualCurrentIndex
            return (data[actualCurrentIndex] as E).also { actualCurrentIndex = nextCellIndex[actualCurrentIndex] }
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun previous(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            lastIndex = --currentIndex
            actualLastIndex = previousCellIndex[actualCurrentIndex]
            return (data[actualCurrentIndex] as E).also { actualCurrentIndex = previousCellIndex[actualCurrentIndex] }
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)

        override fun set(element: E) {
            require(lastIndex != UInt.MAX_VALUE)
            if (lastIndex == currentIndex) {
                data[actualCurrentIndex] = element
            } else {
                data[previousCellIndex[actualCurrentIndex]] = element
            }
        }

        override fun add(element: E) {
            require(lastIndex != UInt.MAX_VALUE)
            TODO("Not yet implemented")
            this@KoneResizableLinkedList.addAt(lastIndex, element)
        }

        override fun remove() {
            require(lastIndex != UInt.MAX_VALUE)
            TODO("Not yet implemented")
            this@KoneResizableLinkedList.removeAt(lastIndex)
        }
    }
}
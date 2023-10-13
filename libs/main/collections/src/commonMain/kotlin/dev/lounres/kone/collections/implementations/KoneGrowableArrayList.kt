/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


private /*const*/ val powersOf2: UIntArray = UIntArray(32) { 1u shl it }

// TODO: Finish implementation
//@Suppress("UNCHECKED_CAST")
//public class KoneGrowableArrayList<E> : KoneMutableIterableList<E> {
//    override var size: UInt = 0u
//        private set
//    private var sizeUpperBound = 1u
//    private var upperBoundNumber = 0
//    private var data = KoneMutableArray<Any?>(sizeUpperBound) { null }
//    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
//        for (i in 0u ..< size) this[i] = null
//    }
//
//    override fun isEmpty(): Boolean = size == 0u
//    override fun contains(element: E): Boolean = data.contains(element)
//    override fun containsAll(elements: KoneIterableCollection<E>): Boolean {
//        for (e in elements) if (e !in data) return false
//        return true
//    }
//
//    override fun get(index: UInt): E {
//        if (index >= size) indexException(index, size)
//        return data[index] as E
//    }
//
//    override fun set(index: UInt, element: E) {
//        if (index >= size) indexException(index, size)
//        data[index] = element
//    }
//
//    override fun clear() {
//        size = 0u
//        dataSizeNumber = 1
//        sizeLowerBound = 0u
//        sizeUpperBound = 2u
//        data = KoneMutableArray(sizeUpperBound) { null }
//    }
//    override fun add(element: E) {
//        if (size == sizeUpperBound) {
//            dataSizeNumber++
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < size -> oldData[it]
//                    it == size -> element
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size++
//        } else {
//            data[size] = element
//            size++
//        }
//    }
//    override fun addAt(index: UInt, element: E) {
//        if (index > size) indexException(index, size)
//        require(index <= size)
//        if (size == sizeUpperBound) {
//            dataSizeNumber++
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < index -> oldData[it]
//                    it == index -> element
//                    it <= size -> oldData[it - 1u]
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size++
//        } else {
//            for (i in (size-1u) downTo index) data[i+1u] = data[i]
//            data[index] = element
//            size++
//        }
//    }
//    override fun addAll(elements: KoneIterableCollection<E>) {
//        val newSize = size + elements.size
//        if (newSize > sizeUpperBound) {
//            while (newSize > sizeUpperBound) {
//                dataSizeNumber++
//                sizeUpperBound = powersOf2[dataSizeNumber+1]
//            }
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            val iter = elements.iterator()
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < size -> oldData[it]
//                    iter.hasNext() -> iter.next()
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size = newSize
//        } else {
//            var index = size
//            val iter = elements.iterator()
//            while (iter.hasNext()) {
//                data[index] = iter.next()
//                index++
//            }
//            size = newSize
//        }
//    }
//    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
//        if (index > size) indexException(index, size)
//        val newSize = size + elements.size
//        val elementsSize = elements.size
//        if (newSize > sizeUpperBound) {
//            while (newSize > sizeUpperBound) {
//                dataSizeNumber++
//                sizeUpperBound = powersOf2[dataSizeNumber+1]
//            }
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            val iter = elements.iterator()
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < index -> oldData[it]
//                    iter.hasNext() -> iter.next()
//                    it < newSize -> oldData[it - elementsSize]
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size = newSize
//        } else {
//            for (i in (size-1u) downTo index) data[i + elementsSize] = data[i]
//            var index = index
//            val iter = elements.iterator()
//            while (iter.hasNext()) {
//                data[index] = iter.next()
//                index++
//            }
//            size = newSize
//        }
//    }
//    override fun remove(element: E) {
//        val index = data.indexOf(element)
//        if (index == size) return
//        val newSize = size - 1u
//        if (newSize < sizeLowerBound) {
//            dataSizeNumber--
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < index -> oldData[it]
//                    it < newSize -> oldData[it + 1u]
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size = newSize
//        } else {
//            for (i in index..<newSize) data[i] = data[i + 1u]
//            data[size - 1u] = null
//            size = newSize
//        }
//    }
//    override fun removeAt(index: UInt) {
//        if (index >= size) indexException(index, size)
//        val newSize = size - 1u
//        if (newSize < sizeLowerBound) {
//            dataSizeNumber--
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < index -> oldData[it]
//                    it < newSize -> oldData[it + 1u]
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size = newSize
//        } else {
//            for (i in index..<newSize) data[i] = data[i + 1u]
//            data[size - 1u] = null
//            size = newSize
//        }
//    }
//
//    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
//        val newSize: UInt
//        run {
//            var checkingMark = 0u
//            var resultMark = 0u
//            while (checkingMark < size) {
//                if (!predicate(checkingMark, data[checkingMark] as E)) {
//                    data[resultMark] = data[checkingMark]
//                    resultMark++
//                }
//                checkingMark++
//            }
//            newSize = checkingMark
//        }
//        if (newSize < sizeLowerBound) {
//            while (newSize < sizeLowerBound) {
//                dataSizeNumber--
//                sizeLowerBound = powersOf2[dataSizeNumber-1]
//            }
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            val oldData = data
//            data = KoneMutableArray(sizeUpperBound) {
//                when {
//                    it < newSize -> oldData[it]
//                    else -> null
//                }
//            }
//            oldData.dispose(size)
//            size = newSize
//        } else {
//            for (i in newSize ..< size) data[i] = null
//            size = newSize
//        }
//    }
//
//    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
//    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)
//
//    override fun toString(): String = buildString {
//        append('[')
//        if (size >= 0u) append(data[0u])
//        for (i in 1u..<size) {
//            append(", ")
//            append(data[i])
//        }
//        append(']')
//    }
//
//    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
//        var lastIndex = UInt.MAX_VALUE
//        override fun hasNext(): Boolean = currentIndex < size
//        override fun next(): E {
//            if (!hasNext()) throw NoSuchElementException("Index $currentIndex out of bounds for length $size")
//            lastIndex = currentIndex
//            return (data[currentIndex] as E).also { currentIndex++ }
//        }
//        override fun nextIndex(): UInt = currentIndex
//
//        override fun hasPrevious(): Boolean = currentIndex > 0u
//        override fun previous(): E {
//            if (!hasPrevious()) throw NoSuchElementException("Index $currentIndex out of bounds for length $size")
//            lastIndex = --currentIndex
//            return data[currentIndex] as E
//        }
//        override fun previousIndex(): UInt = currentIndex - 1u
//
//        override fun set(element: E) {
//            require(lastIndex != UInt.MAX_VALUE)
//            this@KoneGrowableArrayList[lastIndex] = element
//        }
//
//        override fun add(element: E) {
//            require(lastIndex != UInt.MAX_VALUE)
//            this@KoneGrowableArrayList.addAt(lastIndex, element)
//        }
//
//        override fun remove() {
//            require(lastIndex != UInt.MAX_VALUE)
//            this@KoneGrowableArrayList.removeAt(lastIndex)
//        }
//    }
//}
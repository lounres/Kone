/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


private val powersOf2: UIntArray = UIntArray(34) { if (it == 0) 0u else 1u shl (it - 1) }

@Suppress("UNCHECKED_CAST")
public class KoneArrayList<E> : KoneMutableIterableList<E> {
    override var size: UInt = 0u
        private set
    private var dataSizeNumber = 1
    private var sizeLowerBound = 0u
    private var sizeUpperBound = 2u
    private var data = KoneMutableInlineArray<Any?>(sizeUpperBound) { null }

    override fun isEmpty(): Boolean = size == 0u
    override fun contains(element: E): Boolean = data.contains(element)
    override fun containsAll(elements: KoneIterableCollection<E>): Boolean {
        for (e in elements) if (e !in data) return false
        return true
    }

    override fun get(index: UInt): E = data[index] as E
    override fun indexOf(element: E): UInt = data.indexOf(element)
    override fun lastIndexOf(element: E): UInt = data.lastIndexOf(element)

    override fun set(index: UInt, element: E) {
        if (index !in 0u..<size) throw IndexOutOfBoundsException("Index $index out of bounds for length $size")
        data[index] = element
    }

    override fun clear() {
        size = 0u
        dataSizeNumber = 1
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        data = KoneMutableInlineArray(sizeUpperBound) { null }
    }
    override fun add(element: E) {
        if (size == sizeUpperBound) {
            dataSizeNumber++
            sizeLowerBound = powersOf2[dataSizeNumber-1]
            sizeUpperBound = powersOf2[dataSizeNumber+1]
            data = KoneMutableInlineArray(sizeUpperBound) {
                when {
                    it < size -> data[it]
                    it == size -> element
                    else -> null
                }
            }
            size++
        } else {
            data[size] = element
            size++
        }
    }
    override fun addAt(index: UInt, element: E) {
        if (size == sizeUpperBound) {
            dataSizeNumber++
            sizeLowerBound = powersOf2[dataSizeNumber-1]
            sizeUpperBound = powersOf2[dataSizeNumber+1]
            data = KoneMutableInlineArray(sizeUpperBound) {
                when {
                    it < index -> data[it]
                    it == index -> element
                    it <= size -> data[it-1u]
                    else -> null
                }
            }
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
                sizeUpperBound = powersOf2[dataSizeNumber+1]
            }
            sizeLowerBound = powersOf2[dataSizeNumber-1]
            val iter = elements.iterator()
            data = KoneMutableInlineArray(sizeUpperBound) {
                when {
                    it < size -> data[it]
                    iter.hasNext() -> iter.next()
                    else -> null
                }
            }
            this.size = newSize
        } else {
            var index = size
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.next()
                index++
            }
            this.size = newSize
        }
    }
    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
        val newSize = size + elements.size
        val elementsSize = elements.size
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                dataSizeNumber++
                sizeUpperBound = powersOf2[dataSizeNumber+1]
            }
            sizeLowerBound = powersOf2[dataSizeNumber-1]
            val iter = elements.iterator()
            data = KoneMutableInlineArray(sizeUpperBound) {
                when {
                    it < index -> data[it]
                    iter.hasNext() -> iter.next()
                    it < newSize -> data[it-elementsSize]
                    else -> null
                }
            }
            this.size = newSize
        } else {
            for (i in (size-1u) downTo index) data[i + elementsSize] = data[i]
            var index = index
            val iter = elements.iterator()
            while (iter.hasNext()) {
                data[index] = iter.next()
                index++
            }
            this.size = newSize
        }
    }
    override fun remove(element: E) {
        val index = data.indexOf(element)
        for (i in index..<size) data[i] = data[i+1u]
        data[size-1u] = null
    }
    override fun removeAt(index: UInt) {
        for (i in index..(size-2u)) data[i] = data[i+1u]
        data[size-1u] = null
    }

    override fun removeAllThat(predicate: (E) -> Boolean) {
        var checkingMark = 0u
        var resultMark = 0u
        while (checkingMark < size) {
            if (!predicate(data[checkingMark] as E)) {
                data[resultMark] = data[checkingMark]
                resultMark++
            }
            checkingMark++
        }
        TODO("Implement data resizing")
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()

    override fun toString(): String = buildString {
        append('[')
        if (size >= 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }

    internal inner class Iterator: KoneMutableLinearIterator<E> {
        var currentIndex = 0u
        var lastIndex = UInt.MAX_VALUE
        override fun hasNext(): Boolean = currentIndex < size
        override fun next(): E {
            if (!hasNext()) throw NoSuchElementException("Index $currentIndex out of bounds for length $size")
            lastIndex = currentIndex
            return (data[currentIndex] as E).also { currentIndex++ }
        }
        override fun nextIndex(): UInt = currentIndex

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun previous(): E {
            if (!hasPrevious()) throw NoSuchElementException("Index $currentIndex out of bounds for length $size")
            lastIndex = --currentIndex
            return data[currentIndex] as E
        }
        override fun previousIndex(): UInt = currentIndex - 1u

        override fun set(element: E) {
            this@KoneArrayList[lastIndex] = element
        }

        override fun add(element: E) {
            this@KoneArrayList.addAt(lastIndex, element)
        }

        override fun remove() {
            this@KoneArrayList.removeAt(lastIndex)
        }
    }
}
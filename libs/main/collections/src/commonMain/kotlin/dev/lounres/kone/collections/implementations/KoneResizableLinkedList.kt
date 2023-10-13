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
}
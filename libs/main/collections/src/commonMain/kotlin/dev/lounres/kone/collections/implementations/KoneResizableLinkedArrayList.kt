/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.logging.koneLogger
import dev.lounres.kone.misc.scope
import kotlin.math.max


@Suppress("UNCHECKED_CAST")
public class KoneResizableLinkedArrayList<E, EC: Equality<E>> internal constructor(
    size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
    override val elementContext: EC,
) : KoneMutableIterableList<E>, KoneListWithContext<E, EC>, KoneDequeue<E>, Disposable {
    override var size: UInt = size
        private set

    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }
    private fun reinitializeBounds(newSize: UInt) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeBounds",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "newSize" to newSize,
//                )
//            }
//        ) { "started" }

        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableLinkedArrayList implementation can not allocate array of size more than 2^31")
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

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeBounds",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                )
//            }
//        ) { "finished" }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeData",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "oldSize" to oldSize,
//                    "newDataSize" to newDataSize,
//                )
//            }
//        ) { "started" }

        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeData",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeBoundsAndData",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "newSize" to newSize,
//                )
//            }
//        ) { "started" }

        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = (if (size > 0u) size - 1u else sizeUpperBound - 1u)

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.reinitializeBoundsAndData",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun dispose() {
        data.dispose(size)
    }

    private fun actualIndex(index: UInt): UInt =
        when {
            index == size -> nextCellIndex[end]
            index <= (size - 1u) / 2u -> {
                var currentIndex = start
                for (i in 0u..<index) {
                    currentIndex = nextCellIndex[currentIndex]
                }
                currentIndex
            }
            else -> {
                var currentIndex = end
                for (i in index ..< size-1u) {
                    currentIndex = previousCellIndex[currentIndex]
                }
                currentIndex
            }
        }
    private inline fun justAddAfterTheEnd(newElementsNumber: UInt, generator: (index: UInt) -> E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddAfterTheEnd(UInt, (UInt) -> E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "newElementsNumber" to newElementsNumber,
//                )
//            }
//        ) { "started" }

        for (index in 0u ..< newElementsNumber) {
            end = nextCellIndex[end]
            data[end] = generator(index)
        }
        size += newElementsNumber

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddAfterTheEnd(UInt, (UInt) -> E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }
    private fun justAddAfterTheEnd(element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddAfterTheEnd(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        justAddAfterTheEnd(1u) { element }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddAfterTheEnd(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }
    private fun justAddBefore(actualIndex: UInt, element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddBefore",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "actualIndex" to actualIndex,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        val freeIndex = nextCellIndex[end]
        val indexAfterTheFreeIndex = nextCellIndex[freeIndex]
        nextCellIndex[end] = indexAfterTheFreeIndex
        previousCellIndex[indexAfterTheFreeIndex] = end

        val indexBeforeTheActualIndex = previousCellIndex[actualIndex]
        nextCellIndex[freeIndex] = actualIndex
        previousCellIndex[freeIndex] = indexBeforeTheActualIndex
        nextCellIndex[indexBeforeTheActualIndex] = freeIndex
        previousCellIndex[actualIndex] = freeIndex

        if (actualIndex == start) start = freeIndex

        data[actualIndex] = element

        size++

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justAddBefore",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }
    }
    private fun justRemoveAt(actualIndex: UInt) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justRemoveAt",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "actualIndex" to actualIndex,
//                )
//            }
//        ) { "started" }

        data[actualIndex] = null
        val prev = previousCellIndex[actualIndex]
        val next = nextCellIndex[actualIndex]
        nextCellIndex[prev] = next
        previousCellIndex[next] = prev
        if (start == actualIndex) start = next
        if (end == actualIndex) end = prev
        size--

        val afterEnd = nextCellIndex[end]
        nextCellIndex[end] = actualIndex
        previousCellIndex[afterEnd] = actualIndex
        nextCellIndex[actualIndex] = afterEnd
        previousCellIndex[actualIndex] = end
        if (size == 0u) start = actualIndex

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.justRemoveAt",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun get(index: UInt): E {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.get(UInt)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                )
//            }
//        ) { "started" }

        if (index >= size) indexException(index, size)
        val result = data[actualIndex(index)] as E

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.get(UInt)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                    "result" to result,
//                )
//            }
//        ) { "finished" }
        return result
    }

    override fun getFirst(): E {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.getFirst",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        if (isEmpty()) indexException(0u, size) // TODO: Maybe replace with another error
        val result = data[start] as E

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.getFirst",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "result" to result,
//                )
//            }
//        ) { "finished" }
        return result
    }

    override fun getLast(): E {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.getLast",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        if (isEmpty()) indexException(size, size) // TODO: Maybe replace with another error
        val result = data[end] as E

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.getLast",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "result" to result,
//                )
//            }
//        ) { "finished" }
        return result
    }

    override fun set(index: UInt, element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.set(UInt, E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        if (index >= size) indexException(index, size)
        data[actualIndex(index)] = element

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.set(UInt, E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }

    override fun removeAll() {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAll()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        reinitializeBoundsAndData(0u) { null }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAll()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun add(element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.add(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else {
            justAddAfterTheEnd(element)
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.add(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun addFirst(element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addFirst(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it == 0u -> element
                    it <= size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else justAddBefore(start, element)

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addFirst(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }

    override fun addLast(element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addLast(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        if (size == sizeUpperBound) {
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    it == size -> element
                    else -> null
                }
            }
        } else justAddAfterTheEnd(element)

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addLast(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }

    override fun addAt(index: UInt, element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAt(UInt, E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        if (index > size) indexException(index, size)
        when {
            size == sizeUpperBound -> {
                var actualIndex = start
                reinitializeBoundsAndData(size + 1u) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        it == index -> element
                        it <= size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> justAddAfterTheEnd(element)
            else -> justAddBefore(actualIndex(index), element)
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAt(UInt, E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }

    override fun addSeveral(number: UInt, builder: (UInt) -> E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addSeveral(UInt, (UInt) -> E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "started" }

        val newSize = size + number
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            var localIndex = 0u
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    localIndex < number -> builder(localIndex++)
                    else -> null
                }
            }
        } else {
            var localIndex = 0u
            justAddAfterTheEnd(number) { builder(localIndex++) }
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAll(KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "finished" }
    }
    override fun addAllFrom(elements: KoneIterableCollection<E>) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAll(KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "started" }

        val newSize = size + elements.size
        if (newSize > sizeUpperBound) {
            var actualIndex = start
            val iter = elements.iterator()
            reinitializeBoundsAndData(newSize) {
                when {
                    it < size -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    iter.hasNext() -> iter.getAndMoveNext()
                    else -> null
                }
            }
        } else {
            val iter = elements.iterator()
            justAddAfterTheEnd(elements.size) { iter.getAndMoveNext() }
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAll(KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "finished" }
    }

    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAllAt(UInt, KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "started" }

        if (index > size) indexException(index, size)
        if (number == 0u) return
        val newSize = size + number
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                var localIndex = 0u
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        localIndex < number -> builder(localIndex++)
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                var localIndex = 0u
                justAddAfterTheEnd(number) { builder(localIndex++) }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextCellIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    for (localIndex in 0u ..< number) {
                        currentActualIndex = nextCellIndex[currentActualIndex]
                        data[currentActualIndex] = builder(localIndex)
                    }
                    actualInnerPartRightEndIndex = currentActualIndex
                }

                nextCellIndex[end] = nextCellIndex[actualInnerPartRightEndIndex]
                previousCellIndex[nextCellIndex[actualInnerPartRightEndIndex]] = end
                nextCellIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
                previousCellIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
                nextCellIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
                previousCellIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
            }
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAllAt(UInt, KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "finished" }
    }
    override fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAllAt(UInt, KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "started" }

        if (index > size) indexException(index, size)
        if (elements.size == 0u) return
        val newSize = size + elements.size
        when {
            newSize > sizeUpperBound -> {
                var actualIndex = start
                val iter = elements.iterator()
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < index -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        iter.hasNext() -> iter.getAndMoveNext()
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
            index == size -> {
                val iter = elements.iterator()
                justAddAfterTheEnd(elements.size) { iter.getAndMoveNext() }
            }
            else -> {
                val actualRightPartIndex = actualIndex(index)
                val actualLeftPartIndex = previousCellIndex[actualRightPartIndex]
                val actualInnerPartLeftEndIndex = nextCellIndex[end]
                val actualInnerPartRightEndIndex: UInt
                scope {
                    var currentActualIndex = end
                    val iter = elements.iterator()
                    while (iter.hasNext()) {
                        currentActualIndex = nextCellIndex[currentActualIndex]
                        data[currentActualIndex] = iter.getAndMoveNext()
                    }
                    actualInnerPartRightEndIndex = currentActualIndex
                }

                nextCellIndex[end] = nextCellIndex[actualInnerPartRightEndIndex]
                previousCellIndex[nextCellIndex[actualInnerPartRightEndIndex]] = end
                nextCellIndex[actualLeftPartIndex] = actualInnerPartLeftEndIndex
                previousCellIndex[actualInnerPartLeftEndIndex] = actualLeftPartIndex
                nextCellIndex[actualRightPartIndex] = actualInnerPartRightEndIndex
                previousCellIndex[actualInnerPartRightEndIndex] = actualRightPartIndex
            }
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.addAllAt(UInt, KoneIterableCollection<E>)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "elements" to elements,
//                )
//            }
//        ) { "finished" }
    }
    override fun remove(element: E) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.remove(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "started" }

        val targetIndex: UInt
        val actualTargetIndex: UInt
        scope {
            var actualCurrentIndex = start
            for (i in 0u ..< size) {
                if (elementContext { (data[actualCurrentIndex] as E) eq element }) {
                    targetIndex = i
                    actualTargetIndex = actualCurrentIndex
                    return@scope
                }
                actualCurrentIndex = nextCellIndex[actualCurrentIndex]
            }
            return
        }
        val newSize = size - 1u
        if (newSize < sizeLowerBound) { // TODO: Maybe this block can be rewritten with only one `reinitializeBoundsAndData`
            var actualIndex = start
            if (targetIndex == 0u) {
                actualIndex = nextCellIndex[actualIndex]
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            } else {
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < targetIndex -> get(actualIndex).also { _ ->
                            actualIndex = nextCellIndex[actualIndex]
                            if (it == targetIndex - 1u) actualIndex = nextCellIndex[actualIndex]
                        }
                        it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                        else -> null
                    }
                }
            }
        } else {
            justRemoveAt(actualTargetIndex)
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.remove(E)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "element" to element,
//                )
//            }
//        ) { "finished" }
    }
    override fun removeAt(index: UInt) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAt(UInt)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                )
//            }
//        ) { "started" }

        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(actualIndex).also { _ ->
                        actualIndex = nextCellIndex[actualIndex]
                        if (it == index - 1u) actualIndex = nextCellIndex[actualIndex] // TODO: Possible bug for `index = 0u`
                    }
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(actualIndex(index))
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAt(UInt)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                    "index" to index,
//                )
//            }
//        ) { "finished" }
    }

    override fun removeFirst() {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeFirst()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        if (size == 0u) indexException(0u, size) // TODO: Maybe replace with another error
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = nextCellIndex[start]
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(start)
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeFirst()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun removeLast() {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeFirst()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        if (size == 0u) indexException(size, size) // TODO: Maybe replace with another error
        val newSize = size - 1u
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            justRemoveAt(end)
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeFirst()",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAllThatIndexed((UInt, E) -> Boolean)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "started" }

        val newSize: UInt
        val firstCellToClear: UInt
        scope {
            var checkingActualMark = 0u
            var checkingIndex = 0u
            var resultActualMark = 0u
            var resultSize = 0u
            while (checkingIndex < size) {
                if (!predicate(checkingIndex, data[checkingActualMark] as E)) {
                    data[resultActualMark] = data[checkingActualMark]
                    resultActualMark = nextCellIndex[resultActualMark]
                    resultSize++
                }
                checkingActualMark = nextCellIndex[checkingActualMark]
                checkingIndex++
            }
            newSize = resultSize
            firstCellToClear = resultActualMark
        }
        if (newSize < sizeLowerBound) {
            var actualIndex = start
            reinitializeBoundsAndData(newSize) {
                when {
                    it < newSize -> get(actualIndex).also { actualIndex = nextCellIndex[actualIndex] }
                    else -> null
                }
            }
        } else {
            var currentActualIndexToClear = firstCellToClear
            for (i in 0u ..< size - newSize) {
                data[currentActualIndexToClear] = null
                currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
            }
            size = newSize
        }

//        koneLogger.debug(
//            source = "dev.lounres.kone.collections.complex.implementations.KoneResizableLinkedArrayList.removeAllThatIndexed((UInt, E) -> Boolean)",
//            items = {
//                mapOf(
//                    "this" to this,
//                    "size" to size,
//                    "dataSizeNumber" to dataSizeNumber,
//                    "sizeLowerBound" to sizeLowerBound,
//                    "sizeUpperBound" to sizeUpperBound,
//                    "data" to data,
//                    "nextCellIndex" to nextCellIndex,
//                    "previousCellIndex" to previousCellIndex,
//                    "start" to start,
//                    "end" to end,
//                )
//            }
//        ) { "finished" }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[start])
        var currentIndex = start
        for (i in 1u..<size) {
            currentIndex = nextCellIndex[currentIndex]
            append(", ")
            append(data[currentIndex])
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
            is KoneResizableLinkedArrayList<*, *> -> {
                var thisCurrentIndex = this.start
                var otherCurrentIndex = other.start
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != other.data[otherCurrentIndex]) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                    otherCurrentIndex = other.nextCellIndex[otherCurrentIndex]
                }
            }
            is KoneIterableList<*> -> {
                var thisCurrentIndex = this.start
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != otherIterator.getAndMoveNext()) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                }
            }
            else -> {
                var thisCurrentIndex = this.start
                for (i in 0u..<size) {
                    if (this.data[thisCurrentIndex] != other[i]) return false
                    thisCurrentIndex = this.nextCellIndex[thisCurrentIndex]
                }
            }
        }

        return true
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var actualCurrentIndex = actualIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return data[actualCurrentIndex] as E
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, size)
            data[currentIndex] = element
        }
        override fun addNext(element: E) {
            if (currentIndex == size) justAddAfterTheEnd(element)
            else justAddBefore(nextCellIndex[actualCurrentIndex], element)
        }
        override fun removeNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            justRemoveAt(actualCurrentIndex.also { actualCurrentIndex = nextCellIndex[actualCurrentIndex] })
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return data[previousCellIndex[actualCurrentIndex]] as E
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
            actualCurrentIndex = previousCellIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, size)
            data[previousCellIndex[actualCurrentIndex]] = element
        }
        override fun addPrevious(element: E) {
            justAddBefore(actualCurrentIndex, element)
        }
        override fun removePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            justRemoveAt(previousCellIndex[actualCurrentIndex])
        }
    }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableIterableListRegistry
import dev.lounres.kone.collections.KoneMutableRegistration
import dev.lounres.kone.collections.KoneMutableUIntArray
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import kotlin.math.max


public class KoneResizableLinkedArrayListRegistry<E, EC: Equality<E>> internal constructor(
    private var size: UInt,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u,
    private var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var data: KoneMutableArray<Registration?> = KoneMutableArray<Registration?>(sizeUpperBound) { null },
    private var nextCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u },
    private var previousCellIndex: KoneMutableUIntArray = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u },
    private var start: UInt = 0u,
    private var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
    private val elementContext: EC,
): KoneMutableIterableListRegistry<E>, Disposable {

    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
    }
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableLinkedArrayRegistry implementation can not allocate array of size more than 2^31")
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
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Registration?>.(index: UInt) -> Registration?) {
        val oldData = data
        data = KoneMutableArray(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == sizeUpperBound-1u) 0u else it + 1u }
        previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { if (it == 0u) sizeUpperBound - 1u else it - 1u }
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Registration?>.(index: UInt) -> Registration?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = (if (size > 0u) size - 1u else sizeUpperBound - 1u)
    }

    override fun dispose() {
        var currentActualIndexToClear = start
        for (i in 0u ..< size) {
            val registration = data[currentActualIndexToClear]
            registration!!._element = null
            data[currentActualIndexToClear] = null
            currentActualIndexToClear = nextCellIndex[currentActualIndexToClear]
        }
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
        for (index in 0u ..< newElementsNumber) {
            end = nextCellIndex[end]
            data[end] = Registration(end, generator(index))
        }
        size += newElementsNumber
    }
    private fun justAddAfterTheEnd(element: E): Registration {
        end = nextCellIndex[end]
        val newRegistration = Registration(end, element)
        data[end] = newRegistration
        size += 1u
        return newRegistration
    }
    private fun justAddBefore(actualIndex: UInt, element: E) {
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

        data[freeIndex] = Registration(freeIndex, element)

        size++
    }
    private fun justRemoveAt(actualIndex: UInt) {
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
    }

    override val elements: KoneIterableList<E> = Elements()
    override val registrations: KoneIterableSet<KoneMutableRegistration<E>> = Registrations()
    override fun find(element: E): KoneIterableList<KoneMutableRegistration<E>> {
        val accumulator = KoneGrowableArrayList<KoneMutableRegistration<E>>()
        var currentActualIndex = start
        for (currentIndex in 0u ..< size) {
            val registration = data[currentActualIndex]!!
            if (elementContext.invoke { registration.element eq element }) accumulator.add(registration)
            currentActualIndex = nextCellIndex[currentActualIndex]
        }
        return accumulator
    }
    override fun register(element: E): KoneMutableRegistration<E> =
        if (size == sizeUpperBound) {
            val newRegistration = Registration(size, element)
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < size -> get(actualIndex).also { registration ->
                        registration!!.actualIndex = actualIndex
                        actualIndex = nextCellIndex[actualIndex]
                    }
                    it == size -> newRegistration
                    else -> null
                }
            }
            newRegistration
        } else justAddAfterTheEnd(element)

    internal inner class Registration(var actualIndex: UInt, element: E): KoneMutableRegistration<E> {
        internal var _element: E? = element

        @Suppress("UNCHECKED_CAST")
        override var element: E
            get() = _element as E
            set(element) {
                _element = element
            }

        override fun remove() {
            val newSize = size - 1u
            if (newSize < sizeLowerBound) {
                var currnetActualIndex = start
                reinitializeBoundsAndData(newSize) {
                    when {
                        it < newSize -> {
                            if (currnetActualIndex == actualIndex) currnetActualIndex = nextCellIndex[currnetActualIndex]
                            get(currnetActualIndex).also { currnetActualIndex = nextCellIndex[currnetActualIndex] }
                        }
                        else -> null
                    }
                }
            } else {
                justRemoveAt(actualIndex)
            }
            _element = null
        }
    }

    internal inner class ElementsIterator(var currentIndex: UInt = 0u): KoneLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var actualCurrentIndex: UInt = actualIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return data[actualCurrentIndex]!!.element
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return data[previousCellIndex[actualCurrentIndex]]!!.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
            actualCurrentIndex = previousCellIndex[actualCurrentIndex]
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
    }

    internal inner class Elements : KoneIterableList<E>, KoneListWithContext<E, EC> {
        override val size: UInt get() = this@KoneResizableLinkedArrayListRegistry.size
        override val elementContext: EC get() = this@KoneResizableLinkedArrayListRegistry.elementContext
        override fun indexThat(predicate: (UInt, E) -> Boolean): UInt {
            var index = 0u
            var actualIndex = start
            while (index < this@KoneResizableLinkedArrayListRegistry.size) {
                if (predicate(index, data[actualIndex]!!.element)) break
                index++
                actualIndex = nextCellIndex[actualIndex]
            }
            return index
        }
        override fun lastIndexThat(predicate: (UInt, E) -> Boolean): UInt {
            var index = this@KoneResizableLinkedArrayListRegistry.size - 1u
            var actualIndex = end
            while (index != UInt.MAX_VALUE) {
                if (predicate(index, data[actualIndex]!!.element)) break
                index--
                actualIndex = previousCellIndex[actualIndex]
            }
            return index
        }

        override fun get(index: UInt): E {
            if (index >= size) indexException(index, size)
            return data[actualIndex(index)]!!.element
        }

        override fun iterator(): KoneLinearIterator<E> = ElementsIterator()
        override fun iteratorFrom(index: UInt): KoneLinearIterator<E> = ElementsIterator(index)
    }

    internal inner class RegistrationsIterator: KoneIterator<KoneMutableRegistration<E>> {
        var currentIndex: UInt = 0u
        var actualCurrentIndex: UInt = start
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): KoneMutableRegistration<E> {
            if (!hasNext()) noElementException(currentIndex, size)
            return data[actualCurrentIndex]!!
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            actualCurrentIndex = nextCellIndex[actualCurrentIndex]
        }
    }

    internal inner class Registrations: KoneIterableSet<KoneMutableRegistration<E>> {
        override val size: UInt get() = this@KoneResizableLinkedArrayListRegistry.size
        override fun contains(registration: KoneMutableRegistration<E>): Boolean {
            var actualIndex = start
            for (index in 0u ..< this@KoneResizableLinkedArrayListRegistry.size) {
                if (data[actualIndex] == registration) return true
                actualIndex = nextCellIndex[actualIndex]
            }
            return false
        }
        override fun iterator(): KoneIterator<KoneMutableRegistration<E>> = RegistrationsIterator()
    }
}
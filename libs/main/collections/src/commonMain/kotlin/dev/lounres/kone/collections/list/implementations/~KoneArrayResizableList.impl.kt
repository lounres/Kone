/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterator.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable


@Suppress("UNCHECKED_CAST")
@Serializable(with = KoneArrayResizableListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArrayResizableList<Element> @PublishedApi internal constructor(
    size: UInt,
    internal var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u,
    internal var sizeLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    internal var sizeUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    data: KoneMutableArray<Any?> = KoneMutableArray.generate<Any?>(sizeUpperBound) { null },
) : KoneMutableList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal var data: KoneMutableArray<Any?>
        get() = if (isDisposed) disposedInstanceException() else _data!!
        set(value) { _data = value }
    
    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        repeat(size) { this[it] = null }
    }
    override fun dispose() {
        if (isDisposed) return
        data.dispose(size)
        _data = null
        isDisposed = true
    }
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set
    
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
        data = KoneMutableArray.generate(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
    }

    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index] = element
    }
    
    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(it)
                    it == oldSize -> element
                    else -> null
                }
            }
        } else {
            data[size] = element
            size++
        }
    }
    override fun addAt(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (size == sizeUpperBound) {
            val oldSize = size
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < index -> get(it)
                    it == index -> element
                    it <= oldSize -> get(it-1u)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
            data[index] = element
            size++
        }
    }
    
    @DelicateSeveralElementsInserterAPI
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        val newSize = size + number
        if (newSize > sizeUpperBound) {
            reinitializeBoundsAndData(newSize) {
                when {
                    it < index -> get(it)
                    it < index + number -> null
                    it < newSize -> get(it - number)
                    else -> null
                }
            }
        } else {
            if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i]
            size = newSize
        }
        return SeveralElementsInserter(this, index, number)
    }
    
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
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
    
    @DelicateBulkElementsRemoverAPI
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> =
        if (isDisposed) disposedInstanceException()
        else BulkElementsRemover(this)
    
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        dataSizeNumber = 1u
        sizeLowerBound = 0u
        sizeUpperBound = 2u
        reinitializeData { null }
        size = 0u
    }
    
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }
    override fun iterator(): KoneMutableLinearIterator<Element> =
        if (isDisposed) disposedInstanceException() else Iterator(this, 0u)

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        if (other is KoneArrayResizableList<*>) {
            for (i in 0u ..< size) {
                if (this.data[i] != other.data[i]) return false
            }
        } else {
            val otherIterator = other.iterator()
            for (i in 0u ..< size) {
                if (this.data[i] != otherIterator.getAndMoveNext()) return false
            }
        }

        return true
    }

    internal class Iterator<Element>(
        val list: KoneArrayResizableList<Element>,
        var currentIndex: UInt,
    ): KoneMutableLinearIterator<Element> {
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[currentIndex] = element
        }
        override fun addNext(element: Element) {
            list.addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            list.removeAt(currentIndex)
        }

        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[currentIndex - 1u] = element
        }
        override fun addPrevious(element: Element) {
            list.addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.removeAt(--currentIndex)
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
    
    internal class SeveralElementsInserter<Element>(
        val list: KoneArrayResizableList<Element>,
        val newElementsStartIndex: UInt,
        override val newElementsNumber: UInt,
    ) : KoneSeveralElementsInserter<Element> {
        var currentIndex: UInt = 0u
        
        override fun insert(element: Element) {
            if (currentIndex >= newElementsStartIndex) severalElementsInserterOverflowException()
            list.data[newElementsStartIndex + currentIndex] = element
        }
        
        override fun close() {
            if (currentIndex != newElementsStartIndex) severalElementsInserterElementsLackException()
        }
    }
    
    internal class BulkElementsRemover<Element>(
        val list: KoneArrayResizableList<Element>,
    ) : KoneBulkElementsRemover<Element> {
        var checkingMark = 0u
        var resultMark = 0u
        
        override fun hasNext(): Boolean = checkingMark < list.size
        
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return list.data[checkingMark] as Element
        }
        
        override fun nextIndex(): UInt {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            return checkingMark
        }
        
        override fun moveNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            list.data[resultMark] = list.data[checkingMark]
            resultMark++
            checkingMark++
        }
        
        override fun removeNext() {
            if (!hasNext()) noNextElementInBulkElementsRemoverException()
            checkingMark++
        }
        
        override fun close() {
            if (resultMark < list.sizeLowerBound) {
                list.reinitializeBoundsAndData(resultMark) {
                    when {
                        it < resultMark -> get(it)
                        else -> null
                    }
                }
            } else {
                for (i in resultMark ..< list.size) list.data[i] = null
                list.size = resultMark
            }
        }
    }
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.implementations.*
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.utils.anyIndexed
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlin.math.max


//@Serializable(with = KoneResizableHashSetWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public open class KoneHashResizableSet<Element> @PublishedApi internal constructor(
    size: UInt = 0u,
    private val loadFactor: Float = DEFAULT_HASH_TABLE_LOAD_FACTOR,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(calculateHashTableCapacity(size, loadFactor), 2u)) - 1u,
    private var capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var sizeLowerBound: UInt = calculateHashTableSize(capacityLowerBound, loadFactor),
    private var sizeUpperBound: UInt = calculateHashTableSize(capacityUpperBound, loadFactor),
    data: KoneArray<KoneArrayResizableLinkedList<Element>> = KoneArray(capacityUpperBound) { KoneArrayResizableLinkedList() },
    public val elementEquality: Equality<Element>,
    public val elementHashing: Hashing<Element>,
) : KoneMutableSet<Element>, Disposable {
    final override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneArray<KoneArrayResizableLinkedList<Element>>? = data
    private var data: KoneArray<KoneArrayResizableLinkedList<Element>>
        get() = _data!!
        set(value) { _data = value }
    
    private fun KoneArray<KoneArrayResizableLinkedList<Element>>.dispose() {
        // KT-67409
//        @Suppress("UNCHECKED_CAST")
//        val array = this.array as Array<Any?>
        for (i in 0u ..< size) {
            this[i].dispose()
//            array[i.toInt()] = null
        }
    }
    override fun dispose() {
        if (isDisposed) return
        data.dispose()
        _data = null
        isDisposed = true
    }
    
    final override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set

    private fun Element.localHash(): Int {
        val contextHash = context(elementHashing) { this.hash() }
        return contextHash xor (contextHash ushr 16)
    }
    private fun Element.dataIndex(): UInt = localHash().toUInt() and (capacityUpperBound - 1u)
    
    private fun reinitializeBounds(newSize: UInt) {
        val newCapacity = calculateHashTableCapacity(newSize, loadFactor)
        if (newCapacity > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableHashMap implementation can not allocate array of size more than 2^31 needed for size $newSize and load factor $loadFactor")
        when {
            newCapacity > capacityUpperBound -> {
                while (newCapacity > capacityUpperBound) {
                    dataSizeNumber++
                    capacityLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    capacityUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
                sizeLowerBound = calculateHashTableSize(capacityLowerBound, loadFactor)
                sizeUpperBound = calculateHashTableSize(capacityUpperBound, loadFactor)
            }
            newCapacity < capacityLowerBound -> {
                while (newCapacity < capacityUpperBound && dataSizeNumber >= 2u) {
                    dataSizeNumber--
                    capacityLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    capacityUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
                sizeLowerBound = calculateHashTableSize(capacityLowerBound, loadFactor)
                sizeUpperBound = calculateHashTableSize(capacityUpperBound, loadFactor)
            }
        }
    }
    private fun reinitializeData(newDataSize: UInt = capacityUpperBound) {
        val oldData = data
        data = KoneArray(newDataSize) { KoneArrayResizableLinkedList() }
        for (linkedList in oldData) {
            for (element in linkedList) data[element.dataIndex()].add(element)
            linkedList.dispose()
        }
        oldData.dispose()
    }
    private fun reinitializeBoundsAndData(newSize: UInt) {
        reinitializeBounds(newSize)
        reinitializeData()
        size = newSize
    }

    override fun contains(element: Element): Boolean {
        if (isDisposed) disposedInstanceException()
        for (currentElement in data[element.dataIndex()]) if (context(elementEquality) { currentElement eq element }) return true
        return false
    }

    override fun add(element: Element) {
        if (isDisposed) disposedInstanceException()
        val iterator = data[element.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (context(elementEquality) { iterator.getNext() eq element }) {
                iterator.setNext(element)
                return
            }
            iterator.moveNext()
        }
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u)
            data[element.dataIndex()].add(element)
        } else {
            iterator.addNext(element)
            size++
        }
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        if (isDisposed) disposedInstanceException()
        repeat(number) { add(builder(it)) }
    }

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        dataSizeNumber = 1u
        capacityLowerBound = 0u
        capacityUpperBound = 2u
        sizeLowerBound = 0u
        sizeUpperBound = calculateHashTableSize(capacityUpperBound, loadFactor)
        data = KoneArray(capacityUpperBound) { KoneArrayResizableLinkedList() }
        size = 0u
    }

    override fun removeAllThat(predicate: (element: Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        var newSize = 0u
        for (linkedList in data) linkedList.removeAllThat { element -> predicate(element).also { if (!it) newSize += 1u } }
        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
        else size = newSize
    }

    override fun remove(element: Element) {
        if (isDisposed) disposedInstanceException()
        val iterator = data[element.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (context(elementEquality) { iterator.getNext() eq element }) {
                iterator.removeNext()
                if (size == sizeLowerBound) reinitializeBoundsAndData(size - 1u)
                else size--
                return
            }
            iterator.moveNext()
        }
    }

    override fun iterator(): KoneRemovableIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator()

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        scope {
            var dataInnerIndex = 0u
            while (data[dataInnerIndex].isEmpty()) if (++dataInnerIndex == data.size) return@scope
            var iterator = data[dataInnerIndex].iterator()
            append(iterator.getAndMoveNext())
            while (true) when {
                iterator.hasNext() -> {
                    append(", ")
                    append(iterator.getAndMoveNext())
                }
                ++dataInnerIndex == data.size -> return@scope
                else -> iterator = data[dataInnerIndex].iterator()
            }
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 0
        var dataInnerIndex = 0u
        var iterator = data[dataInnerIndex].iterator()
        while (true) when {
            iterator.hasNext() -> {
                hashCode = hashCode + iterator.getAndMoveNext().hashCode()
            }
            ++dataInnerIndex == data.size -> break
            else -> iterator = data[dataInnerIndex].iterator()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean = this === other

    internal inner class Iterator : KoneRemovableIterator<Element> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneRemovableIterator<Element> = data[currentBucket].iterator()

        override fun hasNext(): Boolean =
            if (isDisposed) disposedInstanceException()
            else currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return if (currentIterator.hasNext()) currentIterator.getNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first()
            }
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = data[nextIndex].iterator().also { it.moveNext() }
            }
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIterator.hasNext()) currentIterator.removeNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].iterator().removeNext()
            }
        }
    }
    
    public companion object
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneHashResizableReifiedSet<Element> @PublishedApi internal constructor(
    size: UInt = 0u,
    loadFactor: Float = DEFAULT_HASH_TABLE_LOAD_FACTOR,
    dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(calculateHashTableCapacity(size, loadFactor), 2u)) - 1u,
    capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    sizeLowerBound: UInt = calculateHashTableSize(capacityLowerBound, loadFactor),
    sizeUpperBound: UInt = calculateHashTableSize(capacityUpperBound, loadFactor),
    data: KoneArray<KoneArrayResizableLinkedList<Element>> = KoneArray(capacityUpperBound) { KoneArrayResizableLinkedList() },
    public val elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    elementHashing: Hashing<Element>,
) : KoneHashResizableSet<Element> (
    size = size,
    loadFactor = loadFactor,
    dataSizeNumber = dataSizeNumber,
    capacityLowerBound = capacityLowerBound,
    capacityUpperBound = capacityUpperBound,
    sizeLowerBound = sizeLowerBound,
    sizeUpperBound = sizeUpperBound,
    data = data,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
), KoneMutableReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && super.contains(element)
    
    public companion object
}
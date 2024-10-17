/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.anyIndexed
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope
import kotlinx.serialization.Serializable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


@Serializable(with = KoneResizableHashSetWithContextSerializer::class)
public class KoneResizableHashSet<E, EC: Hashing<E>> internal constructor(
    size: UInt = 0u,
    private val loadFactor: Float = 0.75f,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(calculateCapacity(size, loadFactor), 2u)) - 1u,
    private var capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var sizeLowerBound: UInt = calculateSize(capacityLowerBound, loadFactor),
    private var sizeUpperBound: UInt = calculateSize(capacityUpperBound, loadFactor),
    private var data: KoneArray<KoneResizableLinkedArrayList<E, Equality<E>>> =
        KoneArray(capacityUpperBound) { KoneResizableLinkedArrayList() },
    override val elementContext: EC,
) : KoneMutableIterableSet<E>, KoneMutableSetWithContext<E, EC>, Disposable {
    override var size: UInt = size
        private set

    private fun E.localHash(): Int {
        val contextHash = elementContext { hash() }
        return contextHash xor (contextHash ushr 16)
    }
    private fun E.dataIndex(): UInt = localHash().toUInt() and (capacityUpperBound - 1u)

    private fun KoneArray<KoneResizableLinkedArrayList<E, Equality<E>>>.dispose() {
        // FIXME: KT-67409
//        @Suppress("UNCHECKED_CAST")
//        val array = this.array as Array<Any?>
        for (i in 0u ..< size) {
            this[i].dispose()
//            array[i.toInt()] = null
        }
    }
    override fun dispose() {
        data.dispose()
    }
    private fun reinitializeBounds(newSize: UInt) {
        val newCapacity = calculateCapacity(newSize, loadFactor)
        if (newCapacity > MAX_CAPACITY) throw IllegalArgumentException("KoneResizableHashMap implementation can not allocate array of size more than 2^31 needed for size $newSize and load factor $loadFactor")
        when {
            newCapacity > capacityUpperBound -> {
                while (newCapacity > capacityUpperBound) {
                    dataSizeNumber++
                    capacityLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    capacityUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
                sizeLowerBound = calculateSize(capacityLowerBound, loadFactor)
                sizeUpperBound = calculateSize(capacityUpperBound, loadFactor)
            }
            newCapacity < capacityLowerBound -> {
                while (newCapacity < capacityUpperBound && dataSizeNumber >= 2u) {
                    dataSizeNumber--
                    capacityLowerBound = POWERS_OF_2[dataSizeNumber - 1u]
                    capacityUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
                }
                sizeLowerBound = calculateSize(capacityLowerBound, loadFactor)
                sizeUpperBound = calculateSize(capacityUpperBound, loadFactor)
            }
        }
    }
    private fun reinitializeData(newDataSize: UInt = capacityUpperBound) {
        val oldData = data
        data = KoneArray(newDataSize) { KoneResizableLinkedArrayList() }
        for (linkedList in oldData) for (element in linkedList) data[element.dataIndex()].add(element)
        oldData.dispose()
    }
    private fun reinitializeBoundsAndData(newSize: UInt) {
        reinitializeBounds(newSize)
        reinitializeData()
        size = newSize
    }

    override fun contains(element: E): Boolean {
        for (currentElement in data[element.dataIndex()]) if (elementContext { currentElement eq element }) return true
        return false
    }

    override fun add(element: E) {
        val iterator = data[element.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (elementContext { iterator.getNext() eq element }) {
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

    override fun removeAll() {
        dataSizeNumber = 1u
        capacityLowerBound = 0u
        capacityUpperBound = 2u
        sizeLowerBound = 0u
        sizeUpperBound = calculateSize(capacityUpperBound, loadFactor)
        data = KoneArray(capacityUpperBound) { KoneResizableLinkedArrayList() }
        size = 0u
    }

    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        var newSize = 0u
        for (linkedList in data) linkedList.removeAllThat { element -> predicate(element).also { if (!it) newSize += 1u } }
        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
        else size = newSize
    }

    override fun remove(element: E) {
        val iterator = data[element.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (elementContext { iterator.getNext() eq element }) {
                iterator.removeNext()
                if (size == sizeLowerBound) reinitializeBoundsAndData(size - 1u)
                else size--
                return
            }
            iterator.moveNext()
        }
    }

    override fun iterator(): KoneRemovableIterator<E> = Iterator()

    override fun toString(): String = buildString {
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false
        if (this.hashCode() != other.hashCode()) return false

        for (element in this) if (element !in other) return false

        return true
    }

    internal inner class Iterator : KoneRemovableIterator<E> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneRemovableIterator<E> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): E {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext()
            else {
                val nextIndex = data.indexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first()
            }
        }
        override fun moveNext() {
            if (!hasNext()) TODO("Exception is not yet implemented")
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = data.indexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = data[nextIndex].iterator().also { it.moveNext() }
            }
        }
        override fun removeNext() {
            if (!hasNext()) TODO("Exception is not yet implemented")
            if (currentIterator.hasNext()) currentIterator.removeNext()
            else {
                val nextIndex = data.indexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].iterator().removeNext()
            }
        }
    }

    public companion object {
        private fun calculateCapacity(size: UInt, loadFactor: Float): UInt = ceil(size.toFloat() / loadFactor).toUInt()
        private fun calculateSize(capacity: UInt, loadFactor: Float): UInt = floor(capacity.toFloat() * loadFactor).toUInt()
    }
}
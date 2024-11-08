/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneArray
import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMutableMapNode
import dev.lounres.kone.collections.KoneMutableMapWithContext
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.isEmpty
import dev.lounres.kone.collections.isNotEmpty
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.anyIndexed
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


public class KoneResizableHashMap<Key, KeyContext: Hashing<Key>, Value> internal constructor(
    size: UInt = 0u,
    private val loadFactor: Float = 0.75f,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(calculateCapacity(size, loadFactor), 2u)) - 1u,
    private var capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var sizeLowerBound: UInt = calculateSize(capacityLowerBound, loadFactor),
    private var sizeUpperBound: UInt = calculateSize(capacityUpperBound, loadFactor),
    private var data: KoneArray<KoneResizableLinkedArrayList<KoneMapEntry<Key, Value>>> =
        KoneArray(capacityUpperBound) { KoneResizableLinkedArrayList() },
    override val keyContext: KeyContext,
) : KoneMutableMapWithContext<Key, KeyContext, Value>, Disposable {
    override var size: UInt = size
        private set

    private fun Key.localHash(): Int {
        val contextHash = keyContext { this.hash() }
        return contextHash xor (contextHash ushr 16)
    }
    private fun Key.dataIndex(): UInt = localHash().toUInt() and (capacityUpperBound - 1u)

    private fun KoneArray<KoneResizableLinkedArrayList<KoneMapEntry<Key, Value>>>.dispose() {
        @Suppress("UNCHECKED_CAST")
        val array = this.array as Array<Any?>
        for (i in 0u ..< size) {
            this[i].dispose()
            array[i.toInt()] = null
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
        for (linkedList in oldData) for (entry in linkedList) data[entry.key.dataIndex()].add(entry)
        oldData.dispose()
    }
    private fun reinitializeBoundsAndData(newSize: UInt) {
        reinitializeBounds(newSize)
        reinitializeData()
        size = newSize
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? {
        TODO("Not yet implemented")
    }

    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val iterator = data[key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (keyContext { iterator.getNext().key eq key }) {
                iterator.setNext(KoneMapEntry(key, value))
                return TODO()
            }
            iterator.moveNext()
        }
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u)
            data[key.dataIndex()].add(KoneMapEntry(key, value))
            return TODO()
        } else {
            iterator.addNext(KoneMapEntry(key, value))
            size++
            return TODO()
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

//    override fun removeAllThat(predicate: (key: K, value: V) -> Boolean) {
//        var newSize = 0u
//        for (linkedList in data) linkedList.removeAllThat { entry -> predicate(entry.key, entry.value).also { if (!it) newSize += 1u } }
//        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
//        else size = newSize
//    }

    override fun remove(key: Key) {
        val iterator = data[key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (keyContext { iterator.getNext().key eq key }) {
                iterator.removeNext()
                if (size == sizeLowerBound) reinitializeBoundsAndData(size - 1u)
                else size--
                return
            }
        }
    }

    override fun toString(): String = buildString {
        append('{')
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
        append('}')
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
        if (other !is KoneMap<*, *>) return false
        if (this.size != other.size) return false
        if (this.hashCode() != other.hashCode()) return false

        return this.entriesView == other.entriesView
    }
    
    override val nodesView: KoneSet<KoneMutableMapNode<Key, Value>> get() = TODO("Not yet implemented")
    override val keysView: KoneSet<Key> get() = KeysSet()
    override val valuesView: KoneIterable<Value> get() = ValueCollection()
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> get() = EntriesSet()

    internal inner class KeyIterator : KoneIterator<Key> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<Key, Value>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): Key {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext().key
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first().key
            }
        }
        override fun moveNext() {
            if (!hasNext()) TODO("Exception is not yet implemented")
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }

    internal inner class ValueIterator : KoneIterator<Value> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<Key, Value>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): Value {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext().value
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first().value
            }
        }
        override fun moveNext() {
            if (!hasNext()) TODO("Exception is not yet implemented")
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }

    internal inner class EntryIterator : KoneIterator<KoneMapEntry<Key, Value>> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<Key, Value>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): KoneMapEntry<Key, Value> {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first()
            }
        }
        override fun moveNext() {
            if (!hasNext()) TODO("Exception is not yet implemented")
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }

    internal inner class KeysSet : KoneSet<Key> {
        override val size: UInt = this@KoneResizableHashMap.size
        override fun contains(element: Key): Boolean = data[element.dataIndex()].let { it.firstIndexThat { _, entry -> keyContext { entry.key eq element } } != it.size }
        override fun iterator(): KoneIterator<Key> = KeyIterator()
        // TODO: Override `toString`.
    }

    internal inner class ValueCollection : KoneIterable<Value> {
        override fun iterator(): KoneIterator<Value> = ValueIterator()
        // TODO: Override `toString`.
    }

    internal inner class EntriesSet : KoneIterable<KoneMapEntry<Key, Value>> {
        override fun iterator(): KoneIterator<KoneMapEntry<Key, Value>> = EntryIterator()
        // TODO: Override `toString`.
    }

    public companion object {
        private fun calculateCapacity(size: UInt, loadFactor: Float): UInt = ceil(size.toFloat() / loadFactor).toUInt()
        private fun calculateSize(capacity: UInt, loadFactor: Float): UInt = floor(capacity.toFloat() * loadFactor).toUInt()
    }
}
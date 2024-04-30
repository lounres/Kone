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
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.scope
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


public class KoneResizableHashMap<K, KC: Hashing<K>, V, VC: Equality<V>> internal constructor(
    size: UInt = 0u,
    private val loadFactor: Float = 0.75f,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(max(calculateCapacity(size, loadFactor), 2u)) - 1u,
    private var capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var sizeLowerBound: UInt = calculateSize(capacityLowerBound, loadFactor),
    private var sizeUpperBound: UInt = calculateSize(capacityUpperBound, loadFactor),
    private var data: KoneArray<KoneResizableLinkedArrayList<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>>> =
        KoneArray(capacityUpperBound) { KoneResizableLinkedArrayList() },
    override val keyContext: KC,
    override val valueContext: VC,
) : KoneMutableMapWithContext<K, KC, V, VC>, Disposable {
    override var size: UInt = size
        private set

    private fun K.localHash(): Int {
        val contextHash = keyContext { this.hash() }
        return contextHash xor (contextHash ushr 16)
    }
    private fun K.dataIndex(): UInt = localHash().toUInt() and (capacityUpperBound - 1u)

    private fun KoneArray<KoneResizableLinkedArrayList<KoneMapEntry<K, V>, Equality<KoneMapEntry<K, V>>>>.dispose() {
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
        for (linkedList in oldData) for (entry in linkedList) data[entry.key.dataIndex()].add(entry)
        oldData.dispose()
    }
    private fun reinitializeBoundsAndData(newSize: UInt) {
        reinitializeBounds(newSize)
        reinitializeData()
        size = newSize
    }

    override fun containsKey(key: K): Boolean {
        for ((currentKey, _) in data[key.dataIndex()]) if (keyContext { currentKey eq key }) return true
        return false
    }

    override fun containsValue(value: V): Boolean {
        for (linkedList in data) for ((_, currentValue) in linkedList) if (valueContext { currentValue eq value }) return true
        return false
    }

    override fun get(key: K): V {
        for ((currentKey, currentValue) in data[key.dataIndex()]) if (keyContext { currentKey eq key }) return currentValue
        noMatchingKeyException(key)
    }

    override fun getMaybe(key: K): Option<V> {
        for ((currentKey, currentValue) in data[key.dataIndex()]) if (keyContext { currentKey eq key }) return Some(currentValue)
        return None
    }

    override fun set(key: K, value: V) {
        val iterator = data[key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (keyContext { iterator.getNext().key eq key }) {
                iterator.setNext(KoneMapEntry(key, value))
                return
            }
            iterator.moveNext()
        }
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u)
            data[key.dataIndex()].add(KoneMapEntry(key, value))
        } else {
            iterator.addNext(KoneMapEntry(key, value))
            size++
        }
    }

    override fun set(entry: KoneMapEntry<K, V>) {
        val iterator = data[entry.key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            if (keyContext { iterator.getNext().key eq entry.key }) {
                iterator.setNext(entry)
                return
            }
        }
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u)
            data[entry.key.dataIndex()].add(entry)
        } else {
            iterator.addNext(entry)
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

    override fun removeAllThat(predicate: (key: K, value: V) -> Boolean) {
        var newSize = 0u
        for (linkedList in data) linkedList.removeAllThat { entry -> predicate(entry.key, entry.value).also { if (!it) newSize += 1u } }
        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
        else size = newSize
    }

    override fun remove(key: K) {
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

        return this.entries == other.entries
    }

    override val keys: KoneIterableSet<K> get() = KeysSet()
    override val values: KoneIterableCollection<V> get() = ValueCollection()
    override val entries: KoneIterableSet<KoneMapEntry<K, V>> get() = EntriesSet()

    internal inner class KeyIterator : KoneIterator<K> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<K, V>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): K {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext().key
            else {
                val nextIndex = data.indexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first().key
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
    }

    internal inner class ValueIterator : KoneIterator<V> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<K, V>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): V {
            if (!hasNext()) TODO("Exception is not yet implemented")
            return if (currentIterator.hasNext()) currentIterator.getNext().value
            else {
                val nextIndex = data.indexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                data[nextIndex].first().value
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
    }

    internal inner class EntryIterator : KoneIterator<KoneMapEntry<K, V>> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<KoneMapEntry<K, V>> = data[currentBucket].iterator()

        override fun hasNext(): Boolean = currentIterator.hasNext() || data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): KoneMapEntry<K, V> {
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
    }

    internal inner class KeysSet : KoneIterableSet<K> {
        override val size: UInt = this@KoneResizableHashMap.size
        override fun contains(element: K): Boolean = data[element.dataIndex()].let { it.indexThat { _, entry -> keyContext { entry.key eq element } } != it.size }
        override fun iterator(): KoneIterator<K> = KeyIterator()
    }

    internal inner class ValueCollection : KoneIterableCollection<V> {
        override val size: UInt = this@KoneResizableHashMap.size
        override fun contains(element: V): Boolean = data.indexThat { _, linkedList -> linkedList.indexThat { _, entry -> valueContext { entry.value eq element } } != linkedList.size } != data.size
        override fun iterator(): KoneIterator<V> = ValueIterator()
    }

    internal inner class EntriesSet : KoneIterableSet<KoneMapEntry<K, V>> {
        override val size: UInt = this@KoneResizableHashMap.size
        override fun contains(element: KoneMapEntry<K, V>): Boolean = data[element.key.dataIndex()].let { it.indexThat { _, entry -> (koneMapEntryEquality(keyContext, valueContext)) { entry eq element } } != it.size }
        override fun iterator(): KoneIterator<KoneMapEntry<K, V>> = EntryIterator()
    }

    public companion object {
        private fun calculateCapacity(size: UInt, loadFactor: Float): UInt = ceil(size.toFloat() / loadFactor).toUInt()
        private fun calculateSize(capacity: UInt, loadFactor: Float): UInt = floor(capacity.toFloat() * loadFactor).toUInt()
    }
}
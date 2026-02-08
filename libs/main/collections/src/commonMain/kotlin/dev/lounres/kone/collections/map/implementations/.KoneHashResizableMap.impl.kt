/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.implementations.*
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedList
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.collections.set.toKoneSet
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.collections.utils.anyIndexed
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.scope


// TODO: Add customizable list producer.
public open class KoneHashResizableMap<Key, Value> internal constructor(
    size: UInt = 0u,
    private val loadFactor: Float = 0.75f,
    private var dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(maxOf(calculateHashTableCapacity(size, loadFactor), 2u)) - 1u,
    private var capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    private var capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    private var sizeLowerBound: UInt = calculateHashTableSize(capacityLowerBound, loadFactor),
    private var sizeUpperBound: UInt = calculateHashTableSize(capacityUpperBound, loadFactor),
    data: KoneArray<KoneArrayResizableLinkedNoddedList<Node<Key, Value>>> = KoneArray.generate(capacityUpperBound) { KoneArrayResizableLinkedNoddedList() },
    public val keyEquality: Equality<Key>,
    public val keyHashing: Hashing<Key>,
) : KoneMutableMap<Key, Value>, Disposable {
    final override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneArray<KoneArrayResizableLinkedNoddedList<Node<Key, Value>>>? = data
    internal var data: KoneArray<KoneArrayResizableLinkedNoddedList<Node<Key, Value>>>
        get() = _data!!
        set(value) { _data = value }
    
    private fun KoneArray<KoneArrayResizableLinkedNoddedList<Node<Key, Value>>>.dispose() {
        // KT-67409
//        @Suppress("UNCHECKED_CAST")
//        val array = this.array as Array<Any?>
        for (i in 0u ..< size) {
            this[i].dispose()
//            array[i.toInt()] = null
        }
    }
    override fun dispose() {
        data.dispose()
        _data = null
        isDisposed = true
    }
    
    final override var size: UInt = size
        get() = if (isDisposed) disposedInstanceException() else field
        private set

    private fun Key.localHash(): Int {
        val contextHash = keyHashing { this.hash() }
        return contextHash xor (contextHash ushr 16)
    }
    protected fun Key.dataIndex(): UInt = localHash().toUInt() and (capacityUpperBound - 1u)
    
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
        data = KoneArray.generate(newDataSize) { KoneArrayResizableLinkedNoddedList() }
        for (linkedList in oldData) for (mapNode in linkedList) {
            val listNode = data[mapNode.key.dataIndex()].addNode(mapNode)
            mapNode.bucketListNode = listNode
        }
        oldData.dispose()
    }
    private fun reinitializeBoundsAndData(newSize: UInt) {
        reinitializeBounds(newSize)
        reinitializeData()
        size = newSize
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? =
        if (isDisposed) disposedInstanceException()
        else data[key.dataIndex()].firstThatOrNull { keyEquality { it.key eq key } }

    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        if (isDisposed) disposedInstanceException()
        val iterator = data[key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            val nextNode = iterator.getNext()
            if (keyEquality { nextNode.key eq key }) {
                nextNode.value = value
                return nextNode
            }
            iterator.moveNext()
        }
        val newNode = Node(this, key, value)
        if (size == sizeUpperBound) {
            reinitializeBoundsAndData(size + 1u)
            val listNode = data[key.dataIndex()].addNode(newNode)
            newNode.bucketListNode = listNode
        } else {
            val listNode = data[key.dataIndex()].addNode(newNode)
            newNode.bucketListNode = listNode
            size++
        }
        return newNode
    }

    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        
        for (linkedList in data) {
            for (mapNode in linkedList) mapNode.detach()
            linkedList.dispose()
        }
        
        dataSizeNumber = 1u
        capacityLowerBound = 0u
        capacityUpperBound = 2u
        sizeLowerBound = 0u
        sizeUpperBound = calculateHashTableSize(capacityUpperBound, loadFactor)
        data = KoneArray.generate(capacityUpperBound) { KoneArrayResizableLinkedNoddedList() }
        size = 0u
    }

    override fun removeAllThat(predicate: (key: Key, value: Value) -> Boolean) {
        var newSize = 0u
        for (linkedList in data) linkedList.removeAllThat { node -> predicate(node.key, node.value).also { if (!it) newSize += 1u } }
        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
        else size = newSize
    }
    
    override fun removeAllNodesThat(predicate: (nodes: KoneMutableMapNode<Key, Value>) -> Boolean) {
        var newSize = 0u
        for (linkedList in data) linkedList.removeAllThat { node -> predicate(node).also { if (!it) newSize += 1u } }
        if (newSize < sizeLowerBound) reinitializeBoundsAndData(newSize)
        else size = newSize
    }

    override fun remove(key: Key) {
        if (isDisposed) disposedInstanceException()
        
        val iterator = data[key.dataIndex()].iterator()
        while (iterator.hasNext()) {
            val nextNode = iterator.getNext()
            if (keyEquality { nextNode.key eq key }) {
                nextNode.remove()
                if (size == sizeLowerBound) reinitializeBoundsAndData(size - 1u)
                else size--
                return
            }
            iterator.moveNext()
        }
    }

    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        
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
        if (isDisposed) disposedInstanceException()
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
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (this.size != other.size) return false
        if (this.hashCode() != other.hashCode()) return false

        return this.nodesView == other.nodesView
    }
    
    protected open val _nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> = NodesSet(this)
    final override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>>
        get() = if (isDisposed) disposedInstanceException() else _nodesView
    protected open val _keysView: KoneSet<Key> = KeysSet(this)
    override val keysView: KoneSet<Key>
        get() = if (isDisposed) disposedInstanceException() else _keysView
    override val keys: KoneSet<Key> get() = keysView.toKoneSet(elementEquality = keyEquality, elementHashing = keyHashing)
    protected open val _valuesView: KoneIterable<Value> = ValueIterable(this)
    final override val valuesView: KoneIterable<Value>
        get() = if (isDisposed) disposedInstanceException() else _valuesView
    
    internal class Node<Key, Value>(
        map: KoneHashResizableMap<*, *>,
        override val key: Key,
        override var value: Value,
    ) : KoneMutableMapNode<Key, Value> {
        override var isDetached: Boolean = false
            private set
        
        private var map: KoneHashResizableMap<*, *>? = map
        var bucketListNode: KoneMutableListNode<Node<Key, Value>>? = null
        
        internal fun detach() {
            if (isDetached) return
            bucketListNode = null
            isDetached = true
        }
        
        override fun remove() {
            if (isDetached) return
            bucketListNode!!.remove()
            bucketListNode = null
            map!!.size--
            map = null
            isDetached = true
        }
        
        override fun toString(): String = "$key=$value"
    }
    
    internal class NodeIterator<Key, Value>(
        val map: KoneHashResizableMap<Key, Value>
    ) : KoneIterator<Node<Key, Value>> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<Node<Key, Value>> = map.data[currentBucket].iterator()
        
        override fun hasNext(): Boolean =
            if (map.isDisposed) disposedInstanceException()
            else currentIterator.hasNext() || map.data.anyIndexed { index, bucket -> index > currentBucket && bucket.isNotEmpty() }
        override fun getNext(): Node<Key, Value> {
            if (!hasNext()) noNextElementInIteratorException()
            return if (currentIterator.hasNext()) currentIterator.getNext()
            else {
                val nextIndex = map.data.firstIndexThat { index, list -> index > currentBucket && list.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator()
                currentIterator.getNext()
            }
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = map.data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }

    internal class KeyIterator<Key, Value>(
        val map: KoneHashResizableMap<Key, Value>
    ) : KoneIterator<Key> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<Node<Key, Value>> = map.data[currentBucket].iterator()

        override fun hasNext(): Boolean =
            if (map.isDisposed) disposedInstanceException()
            else currentIterator.hasNext() || map.data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): Key {
            if (!hasNext()) noNextElementInIteratorException()
            return if (currentIterator.hasNext()) currentIterator.getNext().key
            else {
                val nextIndex = map.data.firstIndexThat { index, list -> index > currentBucket && list.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator()
                currentIterator.getNext().key
            }
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = map.data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }

    internal class ValueIterator<Key, Value>(
        val map: KoneHashResizableMap<Key, Value>
    ) : KoneIterator<Value> {
        private var currentBucket: UInt = 0u
        private var currentIterator: KoneIterator<Node<Key, Value>> = map.data[currentBucket].iterator()

        override fun hasNext(): Boolean =
            if (map.isDisposed) disposedInstanceException()
            else currentIterator.hasNext() || map.data.anyIndexed { index, value -> index > currentBucket && value.isNotEmpty() }
        override fun getNext(): Value {
            if (!hasNext()) noNextElementInIteratorException()
            return if (currentIterator.hasNext()) currentIterator.getNext().value
            else {
                val nextIndex = map.data.firstIndexThat { index, list -> index > currentBucket && list.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator()
                currentIterator.getNext().value
            }
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            if (currentIterator.hasNext()) currentIterator.moveNext()
            else {
                val nextIndex = map.data.firstIndexThat { index, element -> index > currentBucket && element.isNotEmpty() }
                currentBucket = nextIndex
                currentIterator = map.data[nextIndex].iterator().also { it.moveNext() }
            }
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class NodesSet<Key, Value>(
        val map: KoneHashResizableMap<Key, Value>,
    ) :  KoneReifiedSet<Node<Key, Value>> {
        override val size: UInt get() = map.size
        @Suppress("USELESS_IS_CHECK")
        override fun contains(element: Node<Key, Value>): Boolean =
            when {
                map.isDisposed -> disposedInstanceException()
                element !is Node -> false
                else -> map.data.any { it.any { it === element } }
            }
        override fun iterator(): KoneIterator<Node<Key, Value>> =
            if (map.isDisposed) disposedInstanceException()
            else NodeIterator(map)
        // TODO: Override `toString`.
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class KeysSet<Key>(
        val map: KoneHashResizableMap<Key, *>,
    ) : KoneSet<Key> {
        override val size: UInt get() = map.size
        override fun contains(element: Key): Boolean =
            if (map.isDisposed) disposedInstanceException()
            else map.data[with(map) { element.dataIndex() }].any { entry -> context(map.keyEquality) { entry.key eq element } }
        override fun iterator(): KoneIterator<Key> =
            if (map.isDisposed) disposedInstanceException()
            else KeyIterator(map)
        // TODO: Override `toString`.
    }

    internal class ValueIterable<Value>(
        val map: KoneHashResizableMap<*, Value>,
    ) : KoneIterable<Value> {
        override val size: UInt get() = map.size
        override fun iterator(): KoneIterator<Value> =
            if (map.isDisposed) disposedInstanceException()
            else ValueIterator(map)
        // TODO: Override `toString`.
    }
}

public class KoneHashResizableReifiedMap<Key, Value> @PublishedApi internal constructor(
    size: UInt = 0u,
    loadFactor: Float = 0.75f,
    dataSizeNumber: UInt = powerOf2IndexGreaterOrEqualTo(maxOf(calculateHashTableCapacity(size, loadFactor), 2u)) - 1u,
    capacityLowerBound: UInt = POWERS_OF_2[dataSizeNumber - 1u],
    capacityUpperBound: UInt = POWERS_OF_2[dataSizeNumber + 1u],
    sizeLowerBound: UInt = calculateHashTableSize(capacityLowerBound, loadFactor),
    sizeUpperBound: UInt = calculateHashTableSize(capacityUpperBound, loadFactor),
    data: KoneArray<KoneArrayResizableLinkedNoddedList<Node<Key, Value>>> = KoneArray.generate(capacityUpperBound) { KoneArrayResizableLinkedNoddedList() },
    public val keyReification: Reification<Key>,
    keyEquality: Equality<Key>,
    keyHashing: Hashing<Key>,
) : KoneHashResizableMap<Key, Value>(
    size = size,
    loadFactor = loadFactor,
    dataSizeNumber = dataSizeNumber,
    capacityLowerBound = capacityLowerBound,
    capacityUpperBound = capacityUpperBound,
    sizeLowerBound = sizeLowerBound,
    sizeUpperBound = sizeUpperBound,
    data = data,
    keyEquality = keyEquality,
    keyHashing = keyHashing,
), KoneMutableReifiedMap<Key, Value> {
    override val _nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> = NodesSet(this)
    override val _keysView: KoneReifiedSet<Key> = KeysSet(this)
    override val keysView: KoneReifiedSet<Key>
        get() = if (isDisposed) disposedInstanceException() else _keysView
    override val keys: KoneReifiedSet<Key>
        get() = keysView.toKoneReifiedSet(elementReification = keyReification, elementEquality = keyEquality, elementHashing = keyHashing)
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class NodesSet<Key, Value>(
        val map: KoneHashResizableReifiedMap<Key, Value>,
    ) : KoneReifiedSet<Node<Key, Value>> {
        override val size: UInt get() = map.size
        @Suppress("USELESS_IS_CHECK")
        override fun contains(element: Node<Key, Value>): Boolean =
            when {
                map.isDisposed -> disposedInstanceException()
                element !is Node -> false
                else -> {
                    val key = element.key
                    key in map.keyReification && map.data[with(map) { key.dataIndex() }].any { it === element }
                }
            }
        override fun iterator(): KoneIterator<Node<Key, Value>> =
            if (map.isDisposed) disposedInstanceException()
            else NodeIterator(map)
        // TODO: Override `toString`.
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class KeysSet<Key>(
        val map: KoneHashResizableReifiedMap<Key, *>,
    ) : KoneReifiedSet<Key> {
        override val size: UInt get() = map.size
        override fun contains(element: Key): Boolean =
            if (map.isDisposed) disposedInstanceException()
            else element in map.keyReification && map.data[with(map) { element.dataIndex() }].any { entry -> context(map.keyEquality) { entry.key eq element } }
        override fun iterator(): KoneIterator<Key> =
            if (map.isDisposed) disposedInstanceException()
            else KeyIterator(map)
        // TODO: Override `toString`.
    }
}
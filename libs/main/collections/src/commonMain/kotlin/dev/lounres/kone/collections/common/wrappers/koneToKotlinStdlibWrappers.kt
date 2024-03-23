/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.wrappers

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.common.*
import dev.lounres.kone.collections.common.wrappers.asKotlinStdlib
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.collections.wrappers.asKotlinStdlib


// region Iterable collections
internal class KotlinStdlibWrapperKoneIterableCollectionItearator<out E>(
    private val iterator1: KoneRemovableIterator<E>,
    private val iterator2: KoneRemovableIterator<E>
): MutableIterator<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneReversibleRemovableIterator($iterator1)"

    private var canRemovePrevious = false
    override fun hasNext(): Boolean = iterator1.hasNext()
    override fun next(): E = iterator1.getAndMoveNext().also {
        if (canRemovePrevious) {
            iterator2.moveNext()
        }
        canRemovePrevious = true
    }

    override fun remove() {
        if (canRemovePrevious) {
            iterator2.removeNext()
            canRemovePrevious = false
        } else {
            throw NoSuchElementException("No element is defined to remove")
        }
    }
}

public fun <E> KoneIterableCollection<E>.asKotlinStdlib(): Collection<E> = KotlinStdlibWrapperKoneIterableCollection(this)
internal class KotlinStdlibWrapperKoneIterableCollection<out E>(private val collection: KoneIterableCollection<E>): Collection<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneIterableCollection($collection)"

    override val size: Int get() = collection.size.toInt()
    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKone())

    override fun iterator(): Iterator<E> = collection.iterator().asKotlinStdlib()
}

public fun <E> KoneMutableIterableCollection<E>.asKotlinStdlib(): MutableCollection<E> = KotlinStdlibWrapperKoneReversibleMutableIterableCollection(this)
internal class KotlinStdlibWrapperKoneReversibleMutableIterableCollection<E>(private val collection: KoneMutableIterableCollection<E>): MutableCollection<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneReversibleMutableIterableCollection($collection)"

    override val size: Int get() = collection.size.toInt()

    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKone())

    override fun iterator(): MutableIterator<E> = KotlinStdlibWrapperKoneIterableCollectionItearator(collection.iterator(), collection.iterator())

    override fun clear() {
        collection.clear()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val oldSize = collection.size
        collection.addAll(elements.asKone())
        return oldSize != collection.size
    }
    override fun add(element: E): Boolean {
        val oldSize = collection.size
        collection.add(element)
        return oldSize != collection.size
    }

    override fun remove(element: E): Boolean {
        val oldSize = collection.size
        collection.remove(element)
        return oldSize != collection.size
    }
    override fun removeAll(elements: Collection<E>): Boolean {
        val oldSize = collection.size
        collection.removeAllFrom(elements.asKone())
        return oldSize != collection.size
    }
    override fun retainAll(elements: Collection<E>): Boolean {
        val oldSize = collection.size
        collection.retainAllFrom(elements.asKone())
        return oldSize != collection.size
    }
}

public fun <E> KoneIterableList<E>.asKotlinStdlib(): List<E> = KotlinStdlibWrapperKoneIterableList(this)
@Suppress("LeakingThis")
internal open class KotlinStdlibWrapperKoneIterableList<out E>(
    protected open val list: KoneIterableList<E>,
    val fromIndex: UInt = 0u,
    open val untilIndex: UInt = list.size
): List<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneIterableList(list=$list, fromIndex=$fromIndex, untilIndex=$untilIndex)"
    override fun hashCode(): Int = list.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is List<*>) return false
        if (this.size != other.size) return false

        val thisIterator = list.iterator()
        val otherIterator = other.iterator()
        for (i in 0..<size) {
            if (thisIterator.getAndMoveNext() != otherIterator.next()) return false
        }

        return true
    }

    init {
        require(fromIndex <= untilIndex && untilIndex <= list.size)
    }
    override val size: Int = (untilIndex - fromIndex).toInt()

    override fun isEmpty(): Boolean = size == 0

    override fun contains(element: @UnsafeVariance E): Boolean =
        list.indexThat { index, currentElement -> index == untilIndex || (index in fromIndex ..< untilIndex && element == currentElement) } != untilIndex
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = elements.all { it in this }

    override fun get(index: Int): E {
        val index = index.toUInt()
        require(index < untilIndex - fromIndex)
        return list[fromIndex + index]
    }

    override fun indexOf(element: @UnsafeVariance E): Int =
        list.indexThat { index, currentElement -> index == untilIndex || (index in fromIndex ..< untilIndex && element == currentElement) }
            .let { if (it != untilIndex) (it - fromIndex).toInt() else -1 }
    override fun lastIndexOf(element: @UnsafeVariance E): Int =
        list.lastIndexThat { index, currentElement -> index == (fromIndex-1u) || (index in fromIndex ..< untilIndex && element == currentElement) }
            .let { if (it != fromIndex-1u) (it - fromIndex).toInt() else -1 }

    override fun iterator(): Iterator<E> = WrapperIterator(iterator = list.iteratorFrom(fromIndex), fromIndex = fromIndex, untilIndex = untilIndex)
    override fun listIterator(): ListIterator<E> =
        WrapperIterator(
            iterator = list.iterator(),
            fromIndex = fromIndex,
            untilIndex = untilIndex
        )
    override fun listIterator(index: Int): ListIterator<E> {
        require(index <= size)
        return WrapperIterator(
            iterator = list.iteratorFrom(index.toUInt()),
            fromIndex = fromIndex,
            untilIndex = untilIndex,
            currentIndex = index.toUInt() + fromIndex
        )
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> =
        KotlinStdlibWrapperKoneIterableList(list = list, fromIndex = fromIndex.toUInt() + this.fromIndex, untilIndex = toIndex.toUInt() + this.fromIndex)

    class WrapperIterator<E>(val iterator: KoneLinearIterator<E>, val fromIndex: UInt, val untilIndex: UInt, var currentIndex: UInt = fromIndex): ListIterator<E> {
        override fun hasNext(): Boolean = currentIndex < untilIndex
        override fun next(): E =
            if (hasNext()) iterator.getAndMoveNext().also { currentIndex++ }
            else throw NoSuchElementException()
        override fun nextIndex(): Int =
            if (hasNext()) currentIndex.toInt()
            else throw NoSuchElementException()

        override fun hasPrevious(): Boolean = currentIndex > fromIndex
        override fun previous(): E =
            if (hasPrevious()) iterator.getAndMovePrevious().also { currentIndex-- }
            else throw NoSuchElementException()
        override fun previousIndex(): Int =
            if (hasPrevious()) (currentIndex-1u).toInt()
            else throw NoSuchElementException()
    }
}

public fun <E> KoneMutableIterableList<E>.asKotlinStdlib(): MutableList<E> = KotlinStdlibWrapperKoneMutableIterableList(this)
internal class KotlinStdlibWrapperKoneMutableIterableList<E>(
    override val list: KoneMutableIterableList<E>,
    fromIndex: UInt = 0u,
    override var untilIndex: UInt = list.size
): MutableList<E>, KotlinStdlibWrapperKoneIterableList<E>(list, fromIndex, untilIndex) {
    override fun toString(): String = "KotlinStdlibWrapperKoneMutableIterableList(list=$list, fromIndex=$fromIndex, untilIndex=$untilIndex)"
    override fun hashCode(): Int = list.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is List<*>) return false
        if (this.size != other.size) return false

        val thisIterator = list.iterator()
        val otherIterator = other.iterator()
        for (i in 0..<size) {
            if (thisIterator.getAndMoveNext() != otherIterator.next()) return false
        }

        return true
    }

    override fun set(index: Int, element: E): E = list[index.toUInt()].also { list[index.toUInt()] = element }

    override fun clear() {
        list.clear()
    }

    override fun add(element: E): Boolean {
        val oldSize = list.size
        list.add(element)
        return oldSize != list.size
    }
    override fun add(index: Int, element: E) {
        list.addAt(index.toUInt(), element)
    }
    override fun addAll(elements: Collection<E>): Boolean {
        val oldSize = list.size
        list.addAll(elements.asKone())
        return oldSize != list.size
    }
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        list.addAllAt(index.toUInt(), elements.asKone())
        return elements.isNotEmpty()
    }

    override fun remove(element: E): Boolean {
        val oldSize = list.size
        list.remove(element)
        return oldSize != list.size
    }
    override fun removeAt(index: Int): E = list[index.toUInt()].also { list.removeAt(index.toUInt()) }
    override fun removeAll(elements: Collection<E>): Boolean {
        val oldSize = list.size
        list.removeAllFrom(elements.asKone())
        return oldSize != list.size
    }
    override fun retainAll(elements: Collection<E>): Boolean {
        val oldSize = list.size
        list.retainAllFrom(elements.asKone())
        return oldSize != list.size
    }

    override fun indexOf(element: @UnsafeVariance E): Int = list.indexOf(element).toInt()
    override fun lastIndexOf(element: @UnsafeVariance E): Int = list.lastIndexOf(element).toInt()

    override fun iterator(): MutableIterator<E> = list.iterator().asKotlinStdlib()
    override fun listIterator(): MutableListIterator<E> = list.iterator().asKotlinStdlib()
    override fun listIterator(index: Int): MutableListIterator<E> = list.iteratorFrom(index.toUInt()).asKotlinStdlib()

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        KotlinStdlibWrapperKoneMutableIterableList(list = list, fromIndex = fromIndex.toUInt() + this.fromIndex, untilIndex = toIndex.toUInt() + this.fromIndex)
}

public fun <E> KoneIterableSet<E>.asKotlinStdlib(): Set<E> = KotlinStdlibWrapperKoneIterableSet(this)
internal open class KotlinStdlibWrapperKoneIterableSet<out E>(protected open val set: KoneIterableSet<E>): Set<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneIterableSet(list=$set)"
    override fun hashCode(): Int = set.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        if (this.size != other.size) return false

        return this.containsAll(other)
    }

    override val size: Int get() = set.size.toInt()
    override fun isEmpty(): Boolean = set.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = set.containsAll(elements.asKone())

    override fun iterator(): Iterator<E> = set.iterator().asKotlinStdlib()
}

public fun <E> KoneMutableIterableSet<E>.asKotlinStdlib(): MutableSet<E> = KotlinStdlibWrapperKoneMutableIterableSet(this)
internal class KotlinStdlibWrapperKoneMutableIterableSet<E>(override val set: KoneMutableIterableSet<E>): KotlinStdlibWrapperKoneIterableSet<E>(set), MutableSet<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneMutableIterableSet(list=$set)"
    override fun hashCode(): Int = set.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        if (this.size != other.size) return false

        return this.containsAll(other)
    }

    override fun add(element: E): Boolean = (element !in set).also { set.add(element) }
    override fun addAll(elements: Collection<E>): Boolean = elements.any { it !in set }.also { set.addAll(elements.asKone()) }

    override fun remove(element: E): Boolean = (element in set).also { set.remove(element) }
    override fun removeAll(elements: Collection<E>): Boolean = elements.any { it in set }.also { set.removeAllThat { it in elements } }
    override fun retainAll(elements: Collection<E>): Boolean = set.any { it !in elements }.also { set.removeAllThat { it !in elements } }
    override fun clear() {
        set.clear()
    }

    override fun iterator(): MutableIterator<E> = KotlinStdlibWrapperKoneIterableCollectionItearator(set.iterator(), set.iterator())
}
// endregion

// region Maps
internal class KotlinStdlibWrapperKoneMapEntriesKoneIteratorAsMapEntriesIterator<out K, out V>(private val iterator: KoneIterator<KoneMapEntry<K, V>>): Iterator<Map.Entry<K, V>> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Map.Entry<K, V> = iterator.getAndMoveNext().let {
        object : Map.Entry<K, V> {
            override val key: K = it.key
            override val value: V = it.value
        }
    }
}

internal class KoneWrapperMapEntryIterator<out K, out V>(private val iterator: Iterator<Map.Entry<K, V>>): KoneIterator<KoneMapEntry<K, V>> {
    var holder: @UnsafeVariance KoneMapEntry<@UnsafeVariance K, @UnsafeVariance V>? = null
    var doesHolderConatinAnything = false
    override fun hasNext(): Boolean = doesHolderConatinAnything || iterator.hasNext()
    override fun getNext(): KoneMapEntry<K, V> = when {
        doesHolderConatinAnything -> holder as KoneMapEntry<K, V>
        iterator.hasNext() ->
            iterator.next()
                .let { KoneMapEntry(it.key, it.value) }
                .also {
                    holder = it
                    doesHolderConatinAnything = true
                }
        else -> throw NoSuchElementException()
    }
    override fun moveNext() {
        if (doesHolderConatinAnything) {
            holder = null
            doesHolderConatinAnything = false
        } else {
            iterator.next()
        }
    }
}

internal class KoneMappingWrapperMapEntryCollection<out K, out V>(private val collection: Collection<Map.Entry<K, V>>): KoneIterableCollection<KoneMapEntry<K, V>> {
    override fun toString(): String = "KoneWrapperIterableCollection($collection)"

    override val size: UInt get() = collection.size.toUInt()

    override fun contains(element: KoneMapEntry<@UnsafeVariance K, @UnsafeVariance V>): Boolean =
        collection.contains(
            object : Map.Entry<K, V> {
                override val key: K = element.key
                override val value: V = element.value
            }
        )

    override fun iterator(): KoneIterator<KoneMapEntry<K, V>> = KoneWrapperMapEntryIterator(collection.iterator())
}

internal class KotlinStdlibWrapperKoneMapEntries<K, V>(private val entries: KoneIterableSet<KoneMapEntry<K, V>>): Set<Map.Entry<K, V>> {
    override val size: Int get() = entries.size.toInt()
    override fun isEmpty(): Boolean = entries.isEmpty()

    override fun contains(element: Map.Entry<K, V>): Boolean =
        entries.contains(KoneMapEntry(element.key, element.value))
    override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean = entries.containsAll(KoneMappingWrapperMapEntryCollection(elements))

    override fun iterator(): Iterator<Map.Entry<K, V>> = KotlinStdlibWrapperKoneMapEntriesKoneIteratorAsMapEntriesIterator(entries.iterator())
}

public fun <K, V> KoneMap<K, V>.asKotlinStdlib(): Map<K, V> = KotlinStdlibWrapperKoneMap(this)
internal open class KotlinStdlibWrapperKoneMap<K, V>(protected open val map: KoneMap<K, V>): Map<K, V> {
    override val size: Int get() = map.size.toInt()
    override val keys: Set<K> get() = map.keys.asKotlinStdlib()
    override val values: Collection<V> get() = map.values.asKotlinStdlib()
    override val entries: Set<Map.Entry<K, V>> get() = KotlinStdlibWrapperKoneMapEntries(map.entries)

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun get(key: K): V? = map.getOrNull(key)

    override fun containsKey(key: K): Boolean = map.containsKey(key)
    override fun containsValue(value: V): Boolean = map.containsValue(value)
}
// endregion
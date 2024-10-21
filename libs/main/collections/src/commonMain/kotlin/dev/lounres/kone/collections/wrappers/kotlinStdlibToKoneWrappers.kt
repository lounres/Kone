/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.repeat


// region Iterators
public fun <E> Iterator<E>.asKone(): KoneIterator<E> = KoneWrapperIterator(this)
internal class KoneWrapperIterator<out E>(private val iterator: Iterator<E>): KoneIterator<E> {
    override fun toString(): String = "KoneWrapperIterator($iterator)"

    var holder: @UnsafeVariance E? = null
    var doesHolderContainAnything = false
    override fun hasNext(): Boolean = doesHolderContainAnything || iterator.hasNext()
    override fun getNext(): E = when {
        doesHolderContainAnything -> holder!!
        iterator.hasNext() -> iterator.next().also {
            holder = it
            doesHolderContainAnything = true
        }
        else -> throw NoSuchElementException()
    }
    override fun moveNext() {
        if (doesHolderContainAnything) {
            holder = null
            doesHolderContainAnything = false
        } else {
            iterator.next()
        }
    }
}

public fun <E> MutableIterator<E>.asKone(): KoneRemovableIterator<E> = KoneWrapperRemovableIterator(this)
internal class KoneWrapperRemovableIterator<out E>(private val iterator: MutableIterator<E>): KoneRemovableIterator<E> {
    override fun toString(): String = "KoneWrapperRemovableIterator($iterator)"

    var holder: @UnsafeVariance E? = null
    var doesHolderConatinAnything = false
    override fun hasNext(): Boolean = doesHolderConatinAnything || iterator.hasNext()
    override fun getNext(): E = when {
        doesHolderConatinAnything -> holder!!
        iterator.hasNext() -> iterator.next().also {
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
    override fun removeNext() {
        if (doesHolderConatinAnything) {
            iterator.remove()
            holder = null
            doesHolderConatinAnything = false
        } else {
            iterator.next()
            iterator.remove()
        }
    }
}

public fun <E> ListIterator<E>.asKone(): KoneLinearIterator<E> = KoneWrapperListIterator(this)
internal class KoneWrapperListIterator<out E>(private val iterator: ListIterator<E>): KoneLinearIterator<E> {
    override fun toString(): String = "KoneWrapperListIterator($iterator)"

    override fun hasNext(): Boolean = iterator.hasNext()
    override fun getNext(): E =
        if (hasNext()) iterator.next().also { iterator.previous() }
        else throw NoSuchElementException()
    override fun moveNext() {
        if (hasNext()) iterator.next()
        else throw NoSuchElementException()
    }
    override fun nextIndex(): UInt =
        if (hasNext()) iterator.nextIndex().toUInt()
        else throw NoSuchElementException()

    override fun hasPrevious(): Boolean = iterator.hasPrevious()
    override fun getPrevious(): E =
        if (hasPrevious()) iterator.previous().also { iterator.next() }
        else throw NoSuchElementException()
    override fun movePrevious() {
        if (hasPrevious()) iterator.previous()
        else throw NoSuchElementException()
    }
    override fun previousIndex(): UInt =
        if (hasPrevious()) iterator.previousIndex().toUInt()
        else throw NoSuchElementException()
}

public fun <E> MutableListIterator<E>.asKone(): KoneMutableLinearIterator<E> = KoneWrapperMutableListIterator(this)
internal class KoneWrapperMutableListIterator<E>(private val iterator: MutableListIterator<E>): KoneMutableLinearIterator<E> {
    override fun toString(): String = "KoneWrapperMutableListIterator($iterator)"

    override fun hasNext(): Boolean = iterator.hasNext()
    override fun getNext(): E =
        if (hasNext()) iterator.next().also { iterator.previous() }
        else throw NoSuchElementException()
    override fun moveNext() {
        if (hasNext()) iterator.next()
        else throw NoSuchElementException()
    }
    override fun nextIndex(): UInt =
        if (hasNext()) iterator.nextIndex().toUInt()
        else throw NoSuchElementException()
    override fun setNext(element: E) {
        if (!hasNext()) throw NoSuchElementException()
        iterator.next()
        iterator.previous()
        iterator.set(element)
    }
    override fun addNext(element: E) {
        iterator.add(element)
        iterator.previous()
    }
    override fun removeNext() {
        iterator.next()
        iterator.remove()
    }

    override fun hasPrevious(): Boolean = iterator.hasPrevious()
    override fun getPrevious(): E =
        if (hasPrevious()) iterator.previous().also { iterator.next() }
        else throw NoSuchElementException()
    override fun movePrevious() {
        if (hasPrevious()) iterator.previous()
        else throw NoSuchElementException()
    }
    override fun previousIndex(): UInt =
        if (hasPrevious()) iterator.previousIndex().toUInt()
        else throw NoSuchElementException()
    override fun setPrevious(element: E) {
        if (!hasPrevious()) throw NoSuchElementException()
        iterator.previous()
        iterator.next()
        iterator.set(element)
    }
    override fun addPrevious(element: E) {
        iterator.add(element)
    }
    override fun removePrevious() {
        iterator.previous()
        iterator.remove()
    }
}
// endregion

// region Iterables
public fun <E> Iterable<E>.asKone(): KoneIterable<E> = KoneWrapperIterable(this)
internal class KoneWrapperIterable<E>(private val iterable: Iterable<E>): KoneIterable<E> {
    override fun toString(): String = "KoneWrapperIterable($iterable)"

    override fun iterator(): KoneIterator<E> = iterable.iterator().asKone()
}

public fun <E> MutableIterable<E>.asKone(): KoneRemovableIterable<E> = KoneWrapperRemovableIterable(this)
internal class KoneWrapperRemovableIterable<E>(private val iterable: MutableIterable<E>): KoneRemovableIterable<E> {
    override fun toString(): String = "KoneWrapperRemovableIterable($iterable)"

    override fun iterator(): KoneRemovableIterator<E> = iterable.iterator().asKone()
}
// endregion

// region Collections
public fun <E> Collection<E>.asKone(): KoneWrapperIterableCollection<E> = KoneWrapperIterableCollection(this)
public class KoneWrapperIterableCollection<E> internal constructor(private val collection: Collection<E>): KoneIterableCollection<E>, KoneCollectionWithContext<E, Hashing<E>> {
    override fun toString(): String = "KoneWrapperIterableCollection($collection)"

    override val elementContext: Hashing<E> get() = defaultHashing<E>()

    override val size: UInt get() = collection.size.toUInt()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)

    override fun iterator(): KoneIterator<E> = collection.iterator().asKone()
}

public fun <E> MutableCollection<E>.asKone(): KoneWrapperMutableIterableCollection<E> = KoneWrapperMutableIterableCollection(this)
public class KoneWrapperMutableIterableCollection<E> internal constructor(private val collection: MutableCollection<E>): KoneMutableIterableCollection<E>, KoneMutableCollectionWithContext<E, Hashing<E>> {
    override fun toString(): String = "KoneWrapperMutableIterableCollection($collection)"

    override val elementContext: Hashing<E> get() = defaultHashing()

    override val size: UInt get() = collection.size.toUInt()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)

    override fun add(element: E) {
        collection.add(element)
    }

    override fun remove(element: E) { collection.remove(element) }
    override fun removeAllThat(predicate: (E) -> Boolean) {
        val iterator = collection.iterator()
        while (iterator.hasNext()) {
            val nextElement = iterator.next()
            if (predicate(nextElement)) iterator.remove()
        }
    }
    override fun removeAll() { collection.clear() }

    override fun iterator(): KoneRemovableIterator<E> = collection.iterator().asKone()
}

public fun <E> List<E>.asKone(): KoneWrapperList<E> = KoneWrapperList(this)
public class KoneWrapperList<E> internal constructor(private val list: List<E>): KoneIterableList<E>, KoneDefaultList<E> {
    override fun toString(): String = "KoneWrapperList($list)"
    override fun hashCode(): Int = list.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneWrapperList<*> -> return this.list == other.list
            is KoneIterableList<*> -> {
                val thisIterator = list.iterator()
                val otherIterator = other.iterator()
                repeat(size) {
                    if (thisIterator.next() != otherIterator.getAndMoveNext()) return false
                }
            }
            else -> {
                val thisIterator = list.iterator()
                for (i in 0u..<size) {
                    if (thisIterator.next() != other[i]) return false
                }
            }
        }

        return true
    }

    override val size: UInt get() = list.size.toUInt()

    override fun get(index: UInt): E = list.get(index.toInt())
    override fun contains(element: @UnsafeVariance E): Boolean = list.contains(element)

    override fun iterator(): KoneLinearIterator<E> = list.listIterator().asKone()
}

public fun <E> MutableList<E>.asKone(): KoneWrapperMutableList<E> = KoneWrapperMutableList(this)
public class KoneWrapperMutableList<E> internal constructor(private val list: MutableList<E>): KoneMutableIterableList<E>, KoneDefaultList<E> {
    override fun toString(): String = "KoneWrapperMutableList($list)"
    override fun hashCode(): Int = list.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneWrapperMutableList<*> -> return this.list == other.list
            is KoneIterableList<*> -> {
                val thisIterator = list.iterator()
                val otherIterator = other.iterator()
                repeat(size) {
                    if (thisIterator.next() != otherIterator.getAndMoveNext()) return false
                }
            }
            else -> {
                val thisIterator = list.iterator()
                repeat(size) {
                    if (thisIterator.next() != other[it]) return false
                }
            }
        }

        return true
    }

    override val size: UInt get() = list.size.toUInt()
    override fun contains(element: E): Boolean = list.contains(element)

    override fun get(index: UInt): E = list.get(index.toInt())

    override fun removeAll() { list.clear() }
    override fun add(element: E) { list.add(element) }
    override fun addAt(index: UInt, element: E) { list.add(index.toInt(), element) }
    override fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>) { list.addAll(index.toInt(), elements.asKotlinStdlib()) }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> E) {
        list.addAll(index.toInt(), List(number.toInt()) { builder(it.toUInt()) })
    }
    override fun set(index: UInt, element: E) { list.set(index.toInt(), element)}
    override fun remove(element: E) { list.remove(element) }
    override fun removeAt(index: UInt) { list.removeAt(index.toInt()) }
    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        var index = 0u
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val nextElement = iterator.next()
            if (predicate(index, nextElement)) iterator.remove()
            index++
        }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = list.listIterator().asKone()
}

public fun <E> Set<E>.asKone(): KoneWrapperSet<E> = KoneWrapperSet(this)
public class KoneWrapperSet<E> internal constructor(private val set: Set<E>): KoneIterableSet<E>, KoneSetWithContext<E, Hashing<E>> {
    override val elementContext: Hashing<E> get() = defaultHashing()
    override fun toString(): String = "KoneWrapperSet($set)"
    override fun hashCode(): Int = set.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneWrapperSet<*>) return this.set == other.set

        return other.containsAll(this)
    }

    override val size: UInt get() = set.size.toUInt()

    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)

    override fun iterator(): KoneIterator<E> = set.iterator().asKone()
}

public fun <E> MutableSet<E>.asKone(): KoneWrapperMutableSet<E> = KoneWrapperMutableSet(this)
public class KoneWrapperMutableSet<E> internal constructor(private val set: MutableSet<E>): KoneMutableIterableSet<E>, KoneMutableSetWithContext<E, Hashing<E>> {
    override val elementContext: Hashing<E> get() = defaultHashing()
    override fun toString(): String = "KoneWrapperMutableSet($set)"
    override fun hashCode(): Int = set.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneWrapperMutableSet<*>) return this.set == other.set

        return other.containsAll(this)
    }

    override val size: UInt get() = set.size.toUInt()

    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)

    override fun add(element: E) { set.add(element) }
    override fun addAllFrom(elements: KoneIterableCollection<E>) { set.addAll(elements.asKotlinStdlib()) }

    override fun remove(element: @UnsafeVariance E) { set.remove(element) }
    override fun removeAllThat(predicate: (E) -> Boolean) {
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val nextElement = iterator.next()
            if (predicate(nextElement)) iterator.remove()
        }
    }
    override fun removeAll() { set.clear() }

    override fun iterator(): KoneRemovableIterator<E> = set.iterator().asKone()
}
//endregion

//region Maps
internal class KoneMappingMapIterator<K, V>(private val iterator: Iterator<Map.Entry<K, V>>):
    KoneIterator<KoneMapEntry<K, V>> {
    override fun toString(): String = "KoneMappingMapIterator($iterator)"

    var holder: @UnsafeVariance KoneMapEntry<K, V>? = null
    var doesHolderConatinAnything = false
    override fun hasNext(): Boolean = doesHolderConatinAnything || iterator.hasNext()
    override fun getNext(): KoneMapEntry<K, V> = when {
        doesHolderConatinAnything -> holder as KoneMapEntry<K, V>
        iterator.hasNext() -> {
            iterator.next()
                .let { KoneMapEntry(it.key, it.value) }
                .also {
                    holder = it
                    doesHolderConatinAnything = true
                }
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

internal class KoneWrapperMapEntries<K, V>(private val entries: Set<Map.Entry<K, V>>) : KoneIterableSet<KoneMapEntry<K, V>> {
    override fun toString(): String = "KoneWrapperMapEntries($entries)"
    override fun hashCode(): Int = entries.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneWrapperMapEntries<*, *>) return this.entries == other.entries

        return other.containsAll(this)
    }

    override val size: UInt get() = entries.size.toUInt()

    override fun contains(element: KoneMapEntry<K, V>): Boolean =
        entries.contains(
            object : Map.Entry<K, V> {
                override val key: K = element.key
                override val value: V = element.value
            }
        )

    override fun iterator(): KoneIterator<KoneMapEntry<K, V>> = KoneMappingMapIterator(entries.iterator())
}

internal class KotlinStdlibWrapperKoneMapEntriesKoneIteratorAsPairsIterator<out K, V>(private val iterator: KoneIterator<KoneMapEntry<K, V>>): Iterator<Pair<K, V>> {
    override fun toString(): String = "KotlinStdlibWrapperKoneMapEntriesKoneIteratorAsPairsIterator($iterator)"

    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Pair<K, V> = iterator.getAndMoveNext().let { it.key to it.value }
}
internal class KotlinStdlibWrapperKoneMapEntriesKoneIterable<K, V>(private val iterable: KoneIterable<KoneMapEntry<K, V>>): Iterable<Pair<K, V>> {
    override fun toString(): String = "KotlinStdlibWrapperKoneMapEntriesKoneIterable($iterable)"

    override fun iterator(): Iterator<Pair<K, V>> = KotlinStdlibWrapperKoneMapEntriesKoneIteratorAsPairsIterator(iterable.iterator())
}

public fun <K, V> Map<K, V>.asKone(): KoneMapWithContext<K, Hashing<K>, V, Hashing<V>> = KoneWrapperMap(this)
@Suppress("UNCHECKED_CAST")
internal class KoneWrapperMap<K, V>(private val map: Map<K, V>): KoneMapWithContext<K, Hashing<K>, V, Hashing<V>> {
    override val keyContext: Hashing<K> get() = defaultHashing()
    override val valueContext: Hashing<V> get() = defaultHashing()
    override fun toString(): String = "KoneWrapperMap($map)"
    override fun hashCode(): Int = map.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (this.size != other.size) return false

        if (other is KoneWrapperMap<*, *>) return this.map == other.map

        other as KoneMap<K, V>

        for ((key, value) in this) {
            when (val otherValue = other.getMaybe(key)) {
                None -> return false
                is Some -> if (value != otherValue.value) return false
            }
        }

        return true
    }

    override val size: UInt get() = map.size.toUInt()
    override fun containsValue(value: V): Boolean = map.containsValue(value)
    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun get(key: K): V =
        if (key in map) map[key] as V
        else noMatchingKeyException(key)
    override fun getMaybe(key: K): Option<V> =
        if (key in map) Some(map[key] as V)
        else None

    override val keysView: KoneIterableSet<K> get() = map.keys.asKone()
    override val valuesView: KoneIterableCollection<V> get() = map.values.asKone()
    override val entriesView: KoneIterableSet<KoneMapEntry<K, V>> get() = KoneWrapperMapEntries(map.entries)
}

public fun <K, V> MutableMap<K, V>.asKone(): KoneMutableMapWithContext<K, Hashing<K>, V, Hashing<V>> = KoneWrapperMutableMap(this)
@Suppress("UNCHECKED_CAST")
internal class KoneWrapperMutableMap<K, V>(private val map: MutableMap<K, V>): KoneMutableMapWithContext<K, Hashing<K>, V, Hashing<V>> {
    override val keyContext: Hashing<K> get() = defaultHashing()
    override val valueContext: Hashing<V> get() = defaultHashing()
    override fun toString(): String = "KoneWrapperMutableMap($map)"
    override fun hashCode(): Int = map.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (this.size != other.size) return false

        if (other is KoneWrapperMutableMap<*, *>) return this.map == other.map

        other as KoneMap<K, V>

        for ((key, value) in this) {
            when (val otherValue = other.getMaybe(key)) {
                None -> return false
                is Some -> if (value != otherValue.value) return false
            }
        }

        return true
    }

    override val size: UInt get() = map.size.toUInt()
    override fun containsValue(value: V): Boolean = map.containsValue(value)
    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun get(key: K): V =
        if (key in map) map[key] as V
        else noMatchingKeyException(key)

    override fun getMaybe(key: K): Option<V> =
        if (key in map) Some(map[key] as V)
        else None

    override val keysView: KoneIterableSet<K> get() = map.keys.asKone()
    override val valuesView: KoneIterableCollection<V> get() = map.values.asKone()
    override val entriesView: KoneIterableSet<KoneMapEntry<K, V>> get() = KoneWrapperMapEntries(map.entries)

    override operator fun set(key: K, value: V) {
        map[key] = value
    }

    override fun set(entry: KoneMapEntry<K, V>) {
        map[entry.key] = entry.value
    }

    override fun remove(key: K) {
        map.remove(key)
    }

    override fun removeAllThat(predicate: (key: K, value: V) -> Boolean) {
        for ((key, value ) in map)
            if (predicate(key, value)) map.remove(key)
    }

    override fun setAllFrom(from: KoneMap<out K, V>) {
        map.putAll(from.asKotlinStdlib())
    }
    override fun setAllFrom(from: KoneIterable<KoneMapEntry<K, V>>) {
        map.putAll(KotlinStdlibWrapperKoneMapEntriesKoneIterable(from))
    }
    override fun removeAll() {
        map.clear()
    }
}
//endregion
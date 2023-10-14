/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*

public fun <E> Iterator<E>.asKone(): KoneIterator<E> = KoneWrapperIterator(this)
internal class KoneWrapperIterator<out E>(private val iterator: Iterator<E>): KoneIterator<E> {
    override fun next(): E = iterator.next()
    override fun hasNext(): Boolean = iterator.hasNext()
}

public fun <E> MutableIterator<E>.asKone(): KoneRemovableIterator<E> = KoneWrapperRemovableIterator(this)
internal class KoneWrapperRemovableIterator<out E>(private val iterator: MutableIterator<E>): KoneRemovableIterator<E> {
    override fun next(): E = iterator.next()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun remove() = iterator.remove()
}

public fun <E> ListIterator<E>.asKone(): KoneLinearIterator<E> = KoneWrapperListIterator(this)
internal class KoneWrapperListIterator<out E>(private val iterator: ListIterator<E>): KoneLinearIterator<E> {
    override fun next(): E = iterator.next()
    override fun nextIndex(): UInt = iterator.nextIndex().toUInt()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun previous(): E = iterator.previous()
    override fun previousIndex(): UInt = iterator.previousIndex().toUInt()
    override fun hasPrevious(): Boolean = iterator.hasPrevious()
}

public fun <E> MutableListIterator<E>.asKone(): KoneMutableLinearIterator<E> = KoneWrapperMutableListIterator(this)
internal class KoneWrapperMutableListIterator<E>(private val iterator: MutableListIterator<E>): KoneMutableLinearIterator<E> {
    override fun next(): E = iterator.next()
    override fun nextIndex(): UInt = iterator.nextIndex().toUInt()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun previous(): E = iterator.previous()
    override fun previousIndex(): UInt = iterator.previousIndex().toUInt()
    override fun hasPrevious(): Boolean = iterator.hasPrevious()

    override fun add(element: E) = iterator.add(element)
    override fun set(element: E) = iterator.set(element)
    override fun remove() = iterator.remove()
}

public fun <E> Iterable<E>.asKone(): KoneIterable<E> = KoneWrapperIterable(this)
internal class KoneWrapperIterable<E>(private val iterable: Iterable<E>): KoneIterable<E> {
    override fun iterator(): KoneIterator<E> = iterable.iterator().asKone()
}

public fun <E> MutableIterable<E>.asKone(): KoneRemovableIterable<E> = KoneWrapperRemovableIterable(this)
internal class KoneWrapperRemovableIterable<E>(private val iterable: MutableIterable<E>): KoneRemovableIterable<E> {
    override fun iterator(): KoneRemovableIterator<E> = iterable.iterator().asKone()
}

public fun <E> Collection<E>.asKone(): KoneIterableCollection<E> = KoneWrapperIterableCollection(this)
internal class KoneWrapperIterableCollection<out E>(private val collection: Collection<E>): KoneIterableCollection<E> {
    override val size: UInt get() = collection.size.toUInt()
    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKotlinStdlib())

    override fun iterator(): KoneIterator<E> = collection.iterator().asKone()
}

public fun <E> MutableCollection<E>.asKone(): KoneRemovableIterableCollection<E> = KoneWrapperMutableIterableCollection(this)
internal class KoneWrapperMutableIterableCollection<E>(private val collection: MutableCollection<E>):
    KoneRemovableIterableCollection<E> {
    override val size: UInt get() = collection.size.toUInt()
    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKotlinStdlib())

    override fun remove(element: E) { collection.remove(element) }
    override fun removeAllThat(predicate: (E) -> Boolean) {
        val iterator = collection.iterator()
        while (iterator.hasNext()) {
            val nextElement = iterator.next()
            if (predicate(nextElement)) iterator.remove()
        }
    }
    override fun clear() { collection.clear() }

    override fun iterator(): KoneRemovableIterator<E> = collection.iterator().asKone()
}

public fun <E> List<E>.asKone(): KoneIterableList<E> = KoneWrapperList(this)
internal class KoneWrapperList<out E>(private val list: List<E>): KoneIterableList<E> {
    override val size: UInt get() = list.size.toUInt()
    override fun isEmpty(): Boolean = list.isEmpty()

    override fun get(index: UInt): E = list.get(index.toInt())
    override fun contains(element: @UnsafeVariance E): Boolean = list.contains(element)
    override fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean = list.containsAll(elements.asKotlinStdlib())

    override fun iterator(): KoneLinearIterator<E> = list.listIterator().asKone()
}

public fun <E> MutableList<E>.asKone(): KoneMutableIterableList<E> = KoneWrapperMutableList(this)
internal class KoneWrapperMutableList<E>(private val list: MutableList<E>): KoneMutableIterableList<E> {
    override val size: UInt get() = list.size.toUInt()
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun contains(element: E): Boolean = list.contains(element)
    override fun containsAll(elements: KoneIterableCollection<E>): Boolean = list.containsAll(elements.asKotlinStdlib())

    override fun get(index: UInt): E = list.get(index.toInt())

    override fun clear() = list.clear()
    override fun add(element: E) { list.add(element) }
    override fun addAt(index: UInt, element: E) { list.add(index.toInt(), element) }
    override fun addAll(elements: KoneIterableCollection<E>) { list.addAll(elements.asKotlinStdlib()) }
    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) { list.addAll(index.toInt(), elements.asKotlinStdlib()) }
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

public fun <E> Set<E>.asKone(): KoneSet<E> = KoneWrapperSet(this)
internal class KoneWrapperSet<out E>(private val set: Set<E>): KoneIterableSet<E> {
    override val size: UInt get() = set.size.toUInt()
    override fun isEmpty(): Boolean = set.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)
    override fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean = set.containsAll(elements.asKotlinStdlib())

    override fun iterator(): KoneIterator<E> = set.iterator().asKone()
}

public fun <E> MutableSet<E>.asKone(): KoneMutableIterableSet<E> = KoneWrapperMutableSet(this)
internal class KoneWrapperMutableSet<E>(private val set: MutableSet<E>): KoneMutableIterableSet<E> {
    override val size: UInt get() = set.size.toUInt()
    override fun isEmpty(): Boolean = set.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)
    override fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean = set.containsAll(elements.asKotlinStdlib())

    override fun add(element: E) { set.add(element) }
    override fun addAll(elements: KoneIterableCollection<E>) { set.addAll(elements.asKotlinStdlib()) }

    override fun remove(element: @UnsafeVariance E) { set.remove(element) }
    override fun removeAllThat(predicate: (E) -> Boolean) {
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val nextElement = iterator.next()
            if (predicate(nextElement)) iterator.remove()
        }
    }
    override fun clear() { set.clear() }

    override fun iterator(): KoneRemovableIterator<E> = set.iterator().asKone()
}
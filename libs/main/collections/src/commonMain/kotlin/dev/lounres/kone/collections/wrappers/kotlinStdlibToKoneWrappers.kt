/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*


public fun <E> Iterator<E>.asKone(): KoneIterator<E> = KoneWrapperIterator(this)
internal class KoneWrapperIterator<out E>(private val iterator: Iterator<E>): KoneIterator<E> {
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
}

public fun <E> MutableIterator<E>.asKone(): KoneRemovableIterator<E> = KoneWrapperRemovableIterator(this)
internal class KoneWrapperRemovableIterator<out E>(private val iterator: MutableIterator<E>): KoneRemovableIterator<E> {
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

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)

    override fun iterator(): KoneIterator<E> = collection.iterator().asKone()
}

// TODO
//public fun <E> MutableCollection<E>.asKone(): KoneMutableIterableCollection<E> = KoneWrapperMutableIterableCollection(this)
//internal class KoneWrapperMutableIterableCollection<E>(private val collection: MutableCollection<E>):
//    KoneRemovableIterableCollection<E>, KoneMutableCollection<E> {
//    override val size: UInt get() = collection.size.toUInt()
//
//    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
//
//    override fun add(element: E) {
//        collection.add(element)
//    }
//
//    override fun remove(element: E) { collection.remove(element) }
//    override fun removeAllThat(predicate: (E) -> Boolean) {
//        val iterator = collection.iterator()
//        while (iterator.hasNext()) {
//            val nextElement = iterator.next()
//            if (predicate(nextElement)) iterator.remove()
//        }
//    }
//    override fun clear() { collection.clear() }
//
//    override fun iterator(): KoneRemovableIterator<E> = collection.iterator().asKone()
//}

public fun <E> List<E>.asKone(): KoneIterableList<E> = KoneWrapperList(this)
internal class KoneWrapperList<out E>(private val list: List<E>): KoneIterableList<E> {
    override val size: UInt get() = list.size.toUInt()

    override fun get(index: UInt): E = list.get(index.toInt())
    override fun contains(element: @UnsafeVariance E): Boolean = list.contains(element)

    override fun iterator(): KoneLinearIterator<E> = list.listIterator().asKone()
}

public fun <E> MutableList<E>.asKone(): KoneMutableIterableList<E> = KoneWrapperMutableList(this)
internal class KoneWrapperMutableList<E>(private val list: MutableList<E>): KoneMutableIterableList<E> {
    override val size: UInt get() = list.size.toUInt()
    override fun contains(element: E): Boolean = list.contains(element)

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

// TODO
//public fun <E> Set<E>.asKone(): KoneSet<E> = KoneWrapperSet(this)
//internal class KoneWrapperSet<out E>(private val set: Set<E>): KoneIterableSet<E> {
//    override val size: UInt get() = set.size.toUInt()
//
//    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)
//
//    override fun iterator(): KoneReversibleIterator<E> = set.iterator().asKone()
//}

// TODO
//public fun <E> MutableSet<E>.asKone(): KoneMutableIterableSet<E> = KoneWrapperMutableSet(this)
//internal class KoneWrapperMutableSet<E>(private val set: MutableSet<E>): KoneMutableIterableSet<E> {
//    override val size: UInt get() = set.size.toUInt()
//
//    override fun contains(element: @UnsafeVariance E): Boolean = set.contains(element)
//
//    override fun add(element: E) { set.add(element) }
//    override fun addAll(elements: KoneIterableCollection<E>) { set.addAll(elements.asKotlinStdlib()) }
//
//    override fun remove(element: @UnsafeVariance E) { set.remove(element) }
//    override fun removeAllThat(predicate: (E) -> Boolean) {
//        val iterator = set.iterator()
//        while (iterator.hasNext()) {
//            val nextElement = iterator.next()
//            if (predicate(nextElement)) iterator.remove()
//        }
//    }
//    override fun clear() { set.clear() }
//
//    override fun iterator(): KoneRemovableIterator<E> = set.iterator().asKone()
//}
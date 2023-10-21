/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*


// region Iterators
public fun <E> KoneIterator<E>.asKotlinStdlib(): Iterator<E> = KotlinStdlibWrapperKoneIterator(this)
internal class KotlinStdlibWrapperKoneIterator<out E>(private val iterator: KoneIterator<E>): Iterator<E> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext()
}

public fun <E> KoneReversibleRemovableIterator<E>.asKotlinStdlib(): MutableIterator<E> = KotlinStdlibWrapperKoneReversibleRemovableIterator(this)
internal class KotlinStdlibWrapperKoneReversibleRemovableIterator<out E>(private val iterator: KoneReversibleRemovableIterator<E>): MutableIterator<E> {
    private var canRemovePrevious = false
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext()

    override fun remove() {
        if (canRemovePrevious) {
            iterator.removePrevious()
            canRemovePrevious = false
        } else {
            throw NoSuchElementException("No element is defined to remove")
        }
    }
}

public fun <E> KoneLinearIterator<E>.asKotlinStdlib(): ListIterator<E> = KotlinStdlibWrapperKoneLinearIterator(this)
internal class KotlinStdlibWrapperKoneLinearIterator<out E>(private val iterator: KoneLinearIterator<E>): ListIterator<E> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext()
    override fun nextIndex(): Int = iterator.nextIndex().toInt()

    override fun hasPrevious(): Boolean = iterator.hasPrevious()
    override fun previous(): E = iterator.getAndMovePrevious()
    override fun previousIndex(): Int = iterator.previousIndex().toInt()
}

public fun <E> KoneMutableLinearIterator<E>.asKotlinStdlib(): MutableListIterator<E> = KotlinStdlibWrapperKoneMutableLinearIterator(this)
public enum class LastReturnedElement { None, Next, Previous }
internal class KotlinStdlibWrapperKoneMutableLinearIterator<E>(private val iterator: KoneMutableLinearIterator<E>): MutableListIterator<E> {
    private var lastReturnedElement = LastReturnedElement.None

    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext().also { lastReturnedElement = LastReturnedElement.Previous }
    override fun nextIndex(): Int = iterator.nextIndex().toInt()

    override fun hasPrevious(): Boolean = iterator.hasPrevious()
    override fun previous(): E = iterator.getAndMovePrevious().also { lastReturnedElement = LastReturnedElement.Next }
    override fun previousIndex(): Int = iterator.previousIndex().toInt()

    override fun add(element: E) {
        iterator.addPrevious(element)
    }
    override fun set(element: E) {
        when (lastReturnedElement) {
            LastReturnedElement.None -> throw NoSuchElementException("No element is defined to replace with set()")
            LastReturnedElement.Next -> iterator.setNext(element)
            LastReturnedElement.Previous -> iterator.setPrevious(element)
        }
        lastReturnedElement = LastReturnedElement.None
    }
    override fun remove() {
        when (lastReturnedElement) {
            LastReturnedElement.None -> throw NoSuchElementException("No element is defined to remove")
            LastReturnedElement.Next -> iterator.removeNext()
            LastReturnedElement.Previous -> iterator.removePrevious()
        }
        lastReturnedElement = LastReturnedElement.None
    }
}
// endregion

// region Iterables
public fun <E> KoneIterable<E>.asKotlinStdlib(): Iterable<E> = KotlinStdlibWrapperIterable(this)
internal class KotlinStdlibWrapperIterable<E>(private val iterable: KoneIterable<E>): Iterable<E> {
    override fun iterator(): Iterator<E> = iterable.iterator().asKotlinStdlib()
}

public fun <E> KoneReversibleRemovableIterable<E>.asKotlinStdlib(): MutableIterable<E> = KotlinStdlibWrapperReversibleRemovableIterable(this)
internal class KotlinStdlibWrapperReversibleRemovableIterable<E>(private val iterable: KoneReversibleRemovableIterable<E>): MutableIterable<E> {
    override fun iterator(): MutableIterator<E> = iterable.iterator().asKotlinStdlib()
}
// endregion

// region Iterable collections
public fun <E> KoneIterableCollection<E>.asKotlinStdlib(): Collection<E> = KotlinStdlibWrapperKoneIterableCollection(this)
internal class KotlinStdlibWrapperKoneIterableCollection<out E>(private val collection: KoneIterableCollection<E>): Collection<E> {
    override val size: Int get() = collection.size.toInt()
    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKone())

    override fun iterator(): Iterator<E> = collection.iterator().asKotlinStdlib()
}

public fun <E> KoneReversibleMutableIterableCollection<E>.asKotlinStdlib(): MutableCollection<E> = KotlinStdlibWrapperKoneReversibleMutableIterableCollection(this)
internal class KotlinStdlibWrapperKoneReversibleMutableIterableCollection<E>(private val collection: KoneReversibleMutableIterableCollection<E>): MutableCollection<E> {
    override val size: Int get() = collection.size.toInt()

    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKone())

    override fun iterator(): MutableIterator<E> = collection.iterator().asKotlinStdlib()

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

public fun <E> KoneIterableList<E>.asKotlinStdlib(): Collection<E> = KotlinStdlibWrapperKoneIterableList(this)
@Suppress("LeakingThis")
internal open class KotlinStdlibWrapperKoneIterableList<out E>(
    private val list: KoneIterableList<E>,
    val fromIndex: UInt = 0u,
    open val untilIndex: UInt = list.size
): List<E> {
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
        require(index in fromIndex ..< untilIndex)
        return list[fromIndex + index]
    }

    override fun indexOf(element: @UnsafeVariance E): Int =
        list.indexThat { index, currentElement -> index == untilIndex || (index in fromIndex ..< untilIndex && element == currentElement) }
            .let { if (it != untilIndex) (it - fromIndex).toInt() else -1 }
    override fun lastIndexOf(element: @UnsafeVariance E): Int =
        list.lastIndexThat { index, currentElement -> index == (fromIndex-1u) || (index in fromIndex ..< untilIndex && element == currentElement) }
            .let { if (it != fromIndex-1u) (it - fromIndex).toInt() else -1 }

    override fun iterator(): Iterator<E> = WrapperIterator(iterator = list.iterator(), fromIndex = fromIndex, untilIndex = untilIndex)
    override fun listIterator(): ListIterator<E> =
        WrapperIterator(
            iterator = list.iterator(),
            fromIndex = fromIndex,
            untilIndex = untilIndex
        )
    override fun listIterator(index: Int): ListIterator<E> {
        require(index <= size)
        return WrapperIterator(
            iterator = list.iterator(),
            fromIndex = fromIndex,
            untilIndex = untilIndex,
            currentIndex = index.toUInt() + fromIndex
        )
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> =
        KotlinStdlibWrapperKoneIterableList(list = list, fromIndex = fromIndex.toUInt() + this.fromIndex, untilIndex = toIndex.toUInt() + this.fromIndex)

    class WrapperIterator<E>(val iterator: KoneLinearIterator<E>, val fromIndex: UInt, val untilIndex: UInt, var currentIndex: UInt = fromIndex): ListIterator<E> {
        override fun hasNext(): Boolean = currentIndex < untilIndex - 1u
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
    private val list: KoneMutableIterableList<E>,
    fromIndex: UInt = 0u,
    override var untilIndex: UInt = list.size
): MutableList<E>, KotlinStdlibWrapperKoneIterableList<E>(list, fromIndex, untilIndex) {
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
// endregion
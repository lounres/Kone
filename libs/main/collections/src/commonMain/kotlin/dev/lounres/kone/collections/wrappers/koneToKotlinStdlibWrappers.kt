/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*


// region Iterators
public fun <E> KoneIterator<E>.asKotlinStdlib(): Iterator<E> = KotlinStdlibWrapperKoneIterator(this)
internal class KotlinStdlibWrapperKoneIterator<out E>(private val iterator: KoneIterator<E>): Iterator<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneIterator($iterator)"

    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext()
}

public fun <E> KoneReversibleRemovableIterator<E>.asKotlinStdlib(): MutableIterator<E> = KotlinStdlibWrapperKoneReversibleRemovableIterator(this)
internal class KotlinStdlibWrapperKoneReversibleRemovableIterator<out E>(private val iterator: KoneReversibleRemovableIterator<E>): MutableIterator<E> {
    override fun toString(): String = "KotlinStdlibWrapperKoneReversibleRemovableIterator($iterator)"

    private var canRemovePrevious = false
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): E = iterator.getAndMoveNext().also { canRemovePrevious = true }

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
    override fun toString(): String = "KotlinStdlibWrapperKoneLinearIterator($iterator)"

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
    override fun toString(): String = "KotlinStdlibWrapperKoneMutableLinearIterator($iterator)"

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
    override fun toString(): String = "KotlinStdlibWrapperIterable($iterable)"

    override fun iterator(): Iterator<E> = iterable.iterator().asKotlinStdlib()
}

public fun <E> KoneReversibleRemovableIterable<E>.asKotlinStdlib(): MutableIterable<E> = KotlinStdlibWrapperReversibleRemovableIterable(this)
internal class KotlinStdlibWrapperReversibleRemovableIterable<E>(private val iterable: KoneReversibleRemovableIterable<E>): MutableIterable<E> {
    override fun toString(): String = "KotlinStdlibWrapperReversibleRemovableIterable($iterable)"

    override fun iterator(): MutableIterator<E> = iterable.iterator().asKotlinStdlib()
}
// endregion
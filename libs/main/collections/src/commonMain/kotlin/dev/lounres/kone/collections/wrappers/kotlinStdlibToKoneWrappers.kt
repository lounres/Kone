/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.wrappers

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.*
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


// region Iterators
public fun <E> Iterator<E>.asKone(): KoneIterator<E> = KoneWrapperIterator(this)
internal class KoneWrapperIterator<out E>(private val iterator: Iterator<E>): KoneIterator<E> {
    override fun toString(): String = "KoneWrapperIterator($iterator)"

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
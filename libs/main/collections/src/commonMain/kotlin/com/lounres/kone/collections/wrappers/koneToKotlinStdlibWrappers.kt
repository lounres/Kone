/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections.wrappers

import com.lounres.kone.collections.*


public fun <E> KoneIterator<E>.asKotlinStdlib(): Iterator<E> = KotlinStdlibWrapperKoneIterator(this)
internal class KotlinStdlibWrapperKoneIterator<out E>(private val iterator: KoneIterator<E>): Iterator<E> {
    override fun next(): E = iterator.next()
    override fun hasNext(): Boolean = iterator.hasNext()
}

public fun <E> KoneRemovableIterator<E>.asKotlinStdlib(): MutableIterator<E> = KotlinStdlibWrapperKoneRemovableIterator(this)
internal class KotlinStdlibWrapperKoneRemovableIterator<out E>(private val iterator: KoneRemovableIterator<E>): MutableIterator<E> {
    override fun next(): E = iterator.next()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun remove() = iterator.remove()
}

public fun <E> KoneLinearIterator<E>.asKotlinStdlib(): ListIterator<E> = KotlinStdlibWrapperKoneLinearIterator(this)
internal class KotlinStdlibWrapperKoneLinearIterator<out E>(private val iterator: KoneLinearIterator<E>): ListIterator<E> {
    override fun next(): E = iterator.next()
    override fun nextIndex(): Int = iterator.nextIndex().toInt()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun previous(): E = iterator.previous()
    override fun previousIndex(): Int = iterator.previousIndex().toInt()
    override fun hasPrevious(): Boolean = iterator.hasPrevious()
}

public fun <E> KoneMutableLinearIterator<E>.asKotlinStdlib(): MutableListIterator<E> = KotlinStdlibWrapperKoneMutableLinearIterator(this)
internal class KotlinStdlibWrapperKoneMutableLinearIterator<E>(private val iterator: KoneMutableLinearIterator<E>): MutableListIterator<E> {
    override fun next(): E = iterator.next()
    override fun nextIndex(): Int = iterator.nextIndex().toInt()
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun previous(): E = iterator.previous()
    override fun previousIndex(): Int = iterator.previousIndex().toInt()
    override fun hasPrevious(): Boolean = iterator.hasPrevious()

    override fun add(element: E) = iterator.add(element)
    override fun set(element: E) = iterator.set(element)
    override fun remove() = iterator.remove()
}

public fun <E> KoneIterable<E>.asKotlinStdlib(): Iterable<E> = KotlinStdlibWrapperIterable(this)
internal class KotlinStdlibWrapperIterable<E>(private val iterable: KoneIterable<E>): Iterable<E> {
    override fun iterator(): Iterator<E> = iterable.iterator().asKotlinStdlib()
}

public fun <E> KoneRemovableIterable<E>.asKotlinStdlib(): MutableIterable<E> = KotlinStdlibWrapperRemovableIterable(this)
internal class KotlinStdlibWrapperRemovableIterable<E>(private val iterable: KoneRemovableIterable<E>): MutableIterable<E> {
    override fun iterator(): MutableIterator<E> = iterable.iterator().asKotlinStdlib()
}

public fun <E> KoneIterableCollection<E>.asKotlinStdlib(): Collection<E> = KotlinStdlibWrapperKoneIterableCollection(this)
internal class KotlinStdlibWrapperKoneIterableCollection<out E>(private val collection: KoneIterableCollection<E>): Collection<E> {
    override val size: Int get() = collection.size.toInt()
    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = collection.containsAll(elements.asKone())

    override fun iterator(): Iterator<E> = collection.iterator().asKotlinStdlib()
}

// TODO: Maybe, create other conversions.
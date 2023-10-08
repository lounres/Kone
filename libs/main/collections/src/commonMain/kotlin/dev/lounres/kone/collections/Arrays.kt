/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import kotlin.jvm.JvmInline


internal open class KoneInlineArrayIterator<E>(val array: Array<E>): KoneLinearIterator<E> {
    protected var index = 0

    override fun hasNext(): Boolean = index < array.size
    override fun next(): E {
        if (index == array.size) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
        return array[index++]
    }
    override fun nextIndex(): UInt = index.toUInt()

    override fun hasPrevious(): Boolean = index > 0
    override fun previous(): E {
        if (index == 0) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
        return array[--index]
    }
    override fun previousIndex(): UInt = (index - 1).toUInt()
}

// KT-42977
@JvmInline
public value class KoneInlineArray<out E>(protected val array: Array<out E>): KoneIterableList<E> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: @UnsafeVariance E): Boolean = element in array
    public override operator fun get(index: UInt): E = array[index.toInt()]
    override fun lastIndexOf(element: @UnsafeVariance E): UInt = array.lastIndexOf(element).toUInt()
    override fun indexOf(element: @UnsafeVariance E): UInt = array.indexOf(element).toUInt()
    public override operator fun iterator(): KoneLinearIterator<E> = KoneInlineArrayIterator(array)
}
public inline fun <reified E> KoneInlineArray(size: UInt, init: (UInt) -> E): KoneInlineArray<E> = KoneInlineArray(Array(size.toInt()) { init(it.toUInt()) })

internal class KoneMutableInlineArrayIterator<E>(array: Array<E>): KoneInlineArrayIterator<E>(array), KoneSettableLinearIterator<E> {
    protected var lastIndex = -1

    override fun next(): E {
        if (index == array.size) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
        lastIndex = index
        return array[index++]
    }

    override fun previous(): E {
        if (index == 0) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
        lastIndex = --index
        return array[index]
    }

    override fun set(element: E) {
        check(lastIndex >= 0)
        array[lastIndex] = element
    }
}

@JvmInline
public value class KoneMutableInlineArray<E>(private val array: Array<E>): KoneSettableIterableList<E> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: E): Boolean = array.contains(element)
    public override operator fun get(index: UInt): E = array[index.toInt()]
    override fun lastIndexOf(element: E): UInt = array.lastIndexOf(element).toUInt()
    override fun indexOf(element: E): UInt = array.indexOf(element).toUInt()
    public override operator fun set(index: UInt, element: E) { array[index.toInt()] = element }
    public override operator fun iterator(): KoneSettableLinearIterator<E> = KoneMutableInlineArrayIterator(array)
}
public inline fun <reified E> KoneMutableInlineArray(size: UInt, init: (UInt) -> E): KoneMutableInlineArray<E> = KoneMutableInlineArray(Array(size.toInt()) { init(it.toUInt()) })
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import kotlin.jvm.JvmInline


// KT-42977
@JvmInline
public value class KoneArray<out E>(protected val array: Array<out E>): KoneIterableList<E> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: @UnsafeVariance E): Boolean = element in array
    public override operator fun get(index: UInt): E = array[index.toInt()]
    public override operator fun iterator(): KoneLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    internal open class Iterator<E>(val array: Array<E>, protected var index: Int = 0): KoneLinearIterator<E> {

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
}
public inline fun <reified E> KoneArray(size: UInt, init: (UInt) -> E): KoneArray<E> = KoneArray(Array(size.toInt()) { init(it.toUInt()) })

@JvmInline
public value class KoneMutableArray<E>(private val array: Array<E>): KoneSettableIterableList<E> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: E): Boolean = array.contains(element)
    public override operator fun get(index: UInt): E = array[index.toInt()]
    public override operator fun set(index: UInt, element: E) { array[index.toInt()] = element }
    public override operator fun iterator(): KoneSettableLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    internal class Iterator<E>(array: Array<E>, index: Int = 0): KoneArray.Iterator<E>(array, index), KoneSettableLinearIterator<E> {
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
}
public inline fun <reified E> KoneMutableArray(size: UInt, init: (UInt) -> E): KoneMutableArray<E> = KoneMutableArray(Array(size.toInt()) { init(it.toUInt()) })

// KT-42977
@JvmInline
public value class KoneUIntArray(protected val array: IntArray): KoneIterableList<UInt> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: UInt): Boolean = element.toInt() in array
    public override operator fun get(index: UInt): UInt = array[index.toInt()].toUInt()
    public override operator fun iterator(): KoneLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    internal open class Iterator(val array: IntArray, protected var index: Int = 0): KoneLinearIterator<UInt> {

        override fun hasNext(): Boolean = index < array.size
        override fun next(): UInt {
            if (index == array.size) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
            return array[index++].toUInt()
        }
        override fun nextIndex(): UInt = index.toUInt()

        override fun hasPrevious(): Boolean = index > 0
        override fun previous(): UInt {
            if (index == 0) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
            return array[--index].toUInt()
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}
public inline fun KoneUIntArray(size: UInt, init: (UInt) -> UInt): KoneUIntArray = KoneUIntArray(IntArray(size.toInt()) { init(it.toUInt()).toInt() })

@JvmInline
public value class KoneMutableUIntArray(private val array: IntArray): KoneSettableIterableList<UInt> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: UInt): Boolean = element.toInt() in array
    public override operator fun get(index: UInt): UInt = array[index.toInt()].toUInt()
    public override operator fun set(index: UInt, element: UInt) { array[index.toInt()] = element.toInt() }
    public override operator fun iterator(): KoneSettableLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    internal class Iterator(array: IntArray, index: Int = 0): KoneUIntArray.Iterator(array, index), KoneSettableLinearIterator<UInt> {
        protected var lastIndex = -1

        override fun next(): UInt {
            if (index == array.size) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
            lastIndex = index
            return array[index++].toUInt()
        }

        override fun previous(): UInt {
            if (index == 0) throw NoSuchElementException("Index $index out of bounds for length ${array.size}")
            lastIndex = --index
            return array[index].toUInt()
        }

        override fun set(element: UInt) {
            check(lastIndex >= 0)
            array[lastIndex] = element.toInt()
        }
    }
}
public inline fun KoneMutableUIntArray(size: UInt, init: (UInt) -> UInt): KoneMutableUIntArray = KoneMutableUIntArray(IntArray(size.toInt()) { init(it.toUInt()).toInt() })
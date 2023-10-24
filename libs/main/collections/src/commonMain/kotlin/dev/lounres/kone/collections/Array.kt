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
    override fun contains(element: @UnsafeVariance E): Boolean = element in array
    public override operator fun get(index: UInt): E = array[index.toInt()]
    public override operator fun iterator(): KoneLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal open class Iterator<E>(open val array: Array<out E>, protected var index: Int = 0): KoneLinearIterator<E> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): E = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())

        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): E = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableArray<E>(private val array: Array<E>): KoneSettableIterableList<E> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: E): Boolean = array.contains(element)
    public override operator fun get(index: UInt): E = array[index.toInt()]
    public override operator fun set(index: UInt, element: E) { array[index.toInt()] = element }
    public override operator fun iterator(): KoneSettableLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal class Iterator<E>(override val array: Array<E>, index: Int = 0): KoneArray.Iterator<E>(array, index), KoneSettableLinearIterator<E> {
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// KT-42977
@JvmInline
public value class KoneUIntArray(protected val array: UIntArray): KoneIterableList<UInt> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UInt): Boolean = element in array
    public override operator fun get(index: UInt): UInt = array[index.toInt()]
    public override operator fun iterator(): KoneLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = buildString {
        append('[')
        if (size >= 0u) append(array[0])
        for (i in 1..<array.size) {
            append(", ")
            append(array[i].toUInt())
        }
        append(']')
    }

    // KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal open class Iterator(val array: UIntArray, protected var index: Int = 0): KoneLinearIterator<UInt> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): UInt = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())

        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UInt = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableUIntArray(private val array: UIntArray): KoneSettableIterableList<UInt> {
    // KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UInt): Boolean = element in array
    public override operator fun get(index: UInt): UInt = array[index.toInt()].toUInt()
    public override operator fun set(index: UInt, element: UInt) { array[index.toInt()] = element }
    public override operator fun iterator(): KoneSettableLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = buildString {
        append('[')
        if (size >= 0u) append(array[0])
        for (i in 1..<array.size) {
            append(", ")
            append(array[i].toUInt())
        }
        append(']')
    }

    // KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal class Iterator(array: UIntArray, index: Int = 0): KoneUIntArray.Iterator(array, index), KoneSettableLinearIterator<UInt> {
        override fun setNext(element: UInt) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UInt) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}
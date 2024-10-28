/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import kotlin.jvm.JvmInline


// FIXME: KT-42977
@JvmInline
public value class KoneArray<E>(internal val array: Array<out E>): KoneIterableList<E>, KoneDefaultList<E> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: @UnsafeVariance E): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): E =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: E): UInt {
        var i = 0u
        while (i < size) {
            if (array[i.toInt()] == element) break
            i++
        }
        return i
    }
    override fun lastIndexOf(element: E): UInt {
        var i = size - 1u
        while (i != UInt.MAX_VALUE) {
            if (array[i.toInt()] == element) break
            i--
        }
        return i
    }
    public override operator fun iterator(): KoneLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // FIXME: KT-24874
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
public value class KoneMutableArray<E>(internal val array: Array<E>): KoneSettableIterableList<E>, KoneDefaultList<E> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: @UnsafeVariance E): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): E =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: E) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }

    public override operator fun iterator(): KoneSettableLinearIterator<E> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // FIXME: KT-24874
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

// FIXME: KT-42977
@JvmInline
public value class KoneByteArray(internal val array: ByteArray): KoneIterableList<Byte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Byte): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Byte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Byte): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Byte): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Byte> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Byte> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: ByteArray, protected var index: Int = 0): KoneLinearIterator<Byte> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Byte = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Byte = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableByteArray(internal val array: ByteArray): KoneSettableIterableList<Byte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Byte): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Byte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Byte) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Byte): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Byte): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Byte> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Byte> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: ByteArray, index: Int = 0): KoneByteArray.Iterator(array, index), KoneSettableLinearIterator<Byte> {
        override fun setNext(element: Byte) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Byte) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneShortArray(internal val array: ShortArray): KoneIterableList<Short> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Short): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Short =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Short): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Short): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Short> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Short> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal open class Iterator(val array: ShortArray, protected var index: Int = 0): KoneLinearIterator<Short> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Short = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())

        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Short = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableShortArray(internal val array: ShortArray): KoneSettableIterableList<Short> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Short): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Short =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Short) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Short): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Short): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Short> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Short> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }

    override fun toString(): String = array.contentToString()

    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }

    internal class Iterator(array: ShortArray, index: Int = 0): KoneShortArray.Iterator(array, index), KoneSettableLinearIterator<Short> {
        override fun setNext(element: Short) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Short) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneIntArray(internal val array: IntArray): KoneIterableList<Int> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Int): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Int =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Int): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Int): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Int> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Int> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: IntArray, protected var index: Int = 0): KoneLinearIterator<Int> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Int = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Int = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableIntArray(internal val array: IntArray): KoneSettableIterableList<Int> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Int): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Int =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Int) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Int): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Int): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Int> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Int> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: IntArray, index: Int = 0): KoneIntArray.Iterator(array, index), KoneSettableLinearIterator<Int> {
        override fun setNext(element: Int) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Int) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneLongArray(internal val array: LongArray): KoneIterableList<Long> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Long): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Long =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Long): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Long): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Long> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Long> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: LongArray, protected var index: Int = 0): KoneLinearIterator<Long> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Long = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Long = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableLongArray(internal val array: LongArray): KoneSettableIterableList<Long> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Long): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Long =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Long) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Long): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Long): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Long> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Long> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: LongArray, index: Int = 0): KoneLongArray.Iterator(array, index), KoneSettableLinearIterator<Long> {
        override fun setNext(element: Long) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Long) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneFloatArray(internal val array: FloatArray): KoneIterableList<Float> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Float): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Float =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Float): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Float): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Float> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Float> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: FloatArray, protected var index: Int = 0): KoneLinearIterator<Float> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Float = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Float = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableFloatArray(internal val array: FloatArray): KoneSettableIterableList<Float> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Float): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Float =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Float) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Float): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Float): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Float> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Float> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: FloatArray, index: Int = 0): KoneFloatArray.Iterator(array, index), KoneSettableLinearIterator<Float> {
        override fun setNext(element: Float) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Float) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneDoubleArray(internal val array: DoubleArray): KoneIterableList<Double> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Double): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Double =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: Double): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Double): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<Double> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Double> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: DoubleArray, protected var index: Int = 0): KoneLinearIterator<Double> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Double = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Double = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableDoubleArray(internal val array: DoubleArray): KoneSettableIterableList<Double> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: Double): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): Double =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Double) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: Double): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: Double): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<Double> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Double> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: DoubleArray, index: Int = 0): KoneDoubleArray.Iterator(array, index), KoneSettableLinearIterator<Double> {
        override fun setNext(element: Double) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Double) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneUByteArray(internal val array: UByteArray): KoneIterableList<UByte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UByte): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UByte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: UByte): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UByte): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<UByte> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<UByte> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: UByteArray, protected var index: Int = 0): KoneLinearIterator<UByte> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): UByte = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UByte = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableUByteArray(internal val array: UByteArray): KoneSettableIterableList<UByte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UByte): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UByte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UByte) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: UByte): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UByte): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<UByte> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<UByte> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: UByteArray, index: Int = 0): KoneUByteArray.Iterator(array, index), KoneSettableLinearIterator<UByte> {
        override fun setNext(element: UByte) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UByte) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneUShortArray(internal val array: UShortArray): KoneIterableList<UShort> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UShort): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UShort =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: UShort): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UShort): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<UShort> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<UShort> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: UShortArray, protected var index: Int = 0): KoneLinearIterator<UShort> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): UShort = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UShort = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableUShortArray(internal val array: UShortArray): KoneSettableIterableList<UShort> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UShort): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UShort =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UShort) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: UShort): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UShort): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<UShort> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<UShort> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: UShortArray, index: Int = 0): KoneUShortArray.Iterator(array, index), KoneSettableLinearIterator<UShort> {
        override fun setNext(element: UShort) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UShort) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
@JvmInline
public value class KoneUIntArray(internal val array: UIntArray): KoneIterableList<UInt> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UInt): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UInt =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: UInt): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UInt): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
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
public value class KoneMutableUIntArray(internal val array: UIntArray): KoneSettableIterableList<UInt> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: UInt): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): UInt =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UInt) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: UInt): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: UInt): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<UInt> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<UInt> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(array[0])
        for (i in 1..<array.size) {
            append(", ")
            append(array[i])
        }
        append(']')
    }
    
    // FIXME: KT-24874
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

// FIXME: KT-42977
@JvmInline
public value class KoneULongArray(internal val array: ULongArray): KoneIterableList<ULong> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: ULong): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): ULong =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    override fun indexOf(element: ULong): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: ULong): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneLinearIterator<ULong> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<ULong> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal open class Iterator(val array: ULongArray, protected var index: Int = 0): KoneLinearIterator<ULong> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): ULong = if (hasNext()) array[index] else noElementException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else noElementException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): ULong = if (hasPrevious()) array[index - 1] else noElementException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

@JvmInline
public value class KoneMutableULongArray(internal val array: ULongArray): KoneSettableIterableList<ULong> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    override fun contains(element: ULong): Boolean {
        for (currentElement in array) if (element == currentElement) return true
        return false
    }
    public override operator fun get(index: UInt): ULong =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else noElementException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: ULong) {
        if (index !in 0u ..< array.size.toUInt()) noElementException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
    override fun indexOf(element: ULong): UInt =
        indexThat { _, currentElement -> currentElement == element }
    override fun lastIndexOf(element: ULong): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
    public override operator fun iterator(): KoneSettableLinearIterator<ULong> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<ULong> {
        require(index <= size)
        return Iterator(array, index.toInt())
    }
    
    override fun toString(): String = array.contentToString()
    
    // FIXME: KT-24874
//    override fun hashCode(): Int = array.contentHashCode()
//    override fun equals(other: Any?): Boolean {
//        if (other !is KoneMutableUIntArray<*>) return false
//        return array.contentEquals(other.array)
//    }
    
    internal class Iterator(array: ULongArray, index: Int = 0): KoneULongArray.Iterator(array, index), KoneSettableLinearIterator<ULong> {
        override fun setNext(element: ULong) {
            if (!hasNext()) noElementException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: ULong) {
            if (!hasPrevious()) noElementException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}
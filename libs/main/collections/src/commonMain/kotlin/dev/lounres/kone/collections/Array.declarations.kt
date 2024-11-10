/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import kotlin.jvm.JvmInline


// FIXME: KT-42977
/**
 * An immutable wrapper for standard [Array] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneArray<Element>(internal val array: Array<out Element>): KoneList<Element> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Element =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun iterator(): KoneLinearIterator<Element> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> {
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

    internal open class Iterator<Element>(open val array: Array<out Element>, protected var index: Int = 0): KoneLinearIterator<Element> {
        override fun hasNext(): Boolean = index < array.size
        override fun getNext(): Element = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())

        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Element = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [Array] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableArray<Element>(internal val array: Array<Element>): KoneSettableList<Element> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Element =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Element) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }

    public override operator fun iterator(): KoneSettableLinearIterator<Element> = Iterator(array)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> {
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

    internal class Iterator<Element>(override val array: Array<Element>, index: Int = 0): KoneArray.Iterator<Element>(array, index), KoneSettableLinearIterator<Element> {
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [ByteArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneByteArray(internal val array: ByteArray): KoneList<Byte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Byte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Byte = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Byte = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [ByteArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableByteArray(internal val array: ByteArray): KoneSettableList<Byte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Byte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Byte) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Byte) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [ShortArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneShortArray(internal val array: ShortArray): KoneList<Short> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Short =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Short = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())

        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Short = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [ShortArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableShortArray(internal val array: ShortArray): KoneSettableList<Short> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Short =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Short) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Short) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [IntArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneIntArray(internal val array: IntArray): KoneList<Int> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Int =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Int = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Int = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [IntArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableIntArray(internal val array: IntArray): KoneSettableList<Int> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Int =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Int) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Int) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [LongArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneLongArray(internal val array: LongArray): KoneList<Long> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Long =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Long = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Long = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [LongArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableLongArray(internal val array: LongArray): KoneSettableList<Long> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Long =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Long) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Long) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [FloatArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneFloatArray(internal val array: FloatArray): KoneList<Float> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Float =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Float = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Float = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [FloatArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableFloatArray(internal val array: FloatArray): KoneSettableList<Float> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Float =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Float) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Float) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [DoubleArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneDoubleArray(internal val array: DoubleArray): KoneList<Double> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Double =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): Double = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): Double = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [DoubleArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableDoubleArray(internal val array: DoubleArray): KoneSettableList<Double> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): Double =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: Double) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: Double) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [UByteArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneUByteArray(internal val array: UByteArray): KoneList<UByte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UByte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): UByte = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UByte = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [UByteArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableUByteArray(internal val array: UByteArray): KoneSettableList<UByte> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UByte =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UByte) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UByte) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [UShortArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneUShortArray(internal val array: UShortArray): KoneList<UShort> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UShort =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): UShort = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UShort = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [UShortArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableUShortArray(internal val array: UShortArray): KoneSettableList<UShort> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UShort =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UShort) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UShort) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [UIntArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneUIntArray(internal val array: UIntArray): KoneList<UInt> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UInt =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): UInt = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): UInt = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [UIntArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableUIntArray(internal val array: UIntArray): KoneSettableList<UInt> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): UInt =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: UInt) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: UInt) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}

// FIXME: KT-42977
/**
 * An immutable wrapper for standard [ULongArray] with unsigned indexation that also implements [KoneList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneULongArray(internal val array: ULongArray): KoneList<ULong> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): ULong =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
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
        override fun getNext(): ULong = if (hasNext()) array[index] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index++
        }
        override fun nextIndex(): UInt = if (hasNext()) index.toUInt() else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        
        override fun hasPrevious(): Boolean = index > 0
        override fun getPrevious(): ULong = if (hasPrevious()) array[index - 1] else indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            index--
        }
        override fun previousIndex(): UInt = (index - 1).toUInt()
    }
}

/**
 * A wrapper for standard [ULongArray] with unsigned indexation that also implements [KoneSettableList].
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
public value class KoneMutableULongArray(internal val array: ULongArray): KoneSettableList<ULong> {
    // FIXME: KT-30915
//    public constructor(size: UInt, init: (UInt) -> ULong): this(ULongArray(size.toInt()) { init(it.toUInt()) })
    
    public override val size: UInt get() = array.size.toUInt()
    public override operator fun get(index: UInt): ULong =
        if (index in 0u ..< array.size.toUInt()) array[index.toInt()]
        else indexOutOfBoundsException(index, array.size.toUInt())
    public override operator fun set(index: UInt, element: ULong) {
        if (index !in 0u ..< array.size.toUInt()) indexOutOfBoundsException(index, array.size.toUInt())
        array[index.toInt()] = element
    }
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
            if (!hasNext()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index] = element
        }
        override fun setPrevious(element: ULong) {
            if (!hasPrevious()) indexOutOfBoundsException(index.toUInt(), array.size.toUInt())
            array[index - 1] = element
        }
    }
}
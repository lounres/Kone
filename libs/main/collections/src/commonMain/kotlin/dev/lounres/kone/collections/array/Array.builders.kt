/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.array

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.getAndMoveNext


// region General arrays

/**
 * Returns a [KoneMutableArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <reified Element> KoneMutableArray(size: UInt, initializer: (UInt) -> Element): KoneMutableArray<Element> =
    KoneMutableArray(Array(size.toInt()) { initializer(it.toUInt()) })

public inline fun <reified Element> KoneMutableArray(indices: UIntRange, initializer: (UInt) -> Element): KoneMutableArray<Element> =
    KoneMutableArray(Array((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <reified Element> KoneArray(size: UInt, initializer: (UInt) -> Element): KoneArray<Element> =
    KoneArray(Array(size.toInt()) { initializer(it.toUInt()) })

public inline fun <reified Element> KoneArray(indices: UIntRange, initializer: (UInt) -> Element): KoneArray<Element> =
    KoneArray(Array((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun <reified Element> KoneMutableArray.Companion.of(vararg elements: Element): KoneMutableArray<Element> =
    KoneMutableArray(elements as Array<Element>)

public inline fun <reified Element> KoneArray.Companion.of(vararg elements: Element): KoneArray<Element> =
    KoneArray(elements as Array<Element>)

public inline fun <reified Element> KoneIterable<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray(size) { iterator.getAndMoveNext() }
}

public inline fun <reified Element> KoneIterable<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Boolean

/**
 * Returns a [KoneMutableBooleanArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableBooleanArray(size: UInt, initializer: (UInt) -> Boolean): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableBooleanArray] of provided [size] of `false`s.
 */
public fun KoneMutableBooleanArray(size: UInt): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray(size.toInt()))

public inline fun KoneMutableBooleanArray(indices: UIntRange, initializer: (UInt) -> Boolean): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableBooleanArray(indices: UIntRange): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneBooleanArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneBooleanArray(size: UInt, initializer: (UInt) -> Boolean): KoneBooleanArray =
    KoneBooleanArray(BooleanArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneBooleanArray] of provided [size] of `false`s.
 */
public fun KoneBooleanArray(size: UInt): KoneBooleanArray =
    KoneBooleanArray(BooleanArray(size.toInt()))

public inline fun KoneBooleanArray(indices: UIntRange, initializer: (UInt) -> Boolean): KoneBooleanArray =
    KoneBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneBooleanArray(indices: UIntRange): KoneBooleanArray =
    KoneBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableBooleanArray.Companion.of(vararg elements: Boolean): KoneMutableBooleanArray =
    KoneMutableBooleanArray(elements)

public fun KoneBooleanArray.Companion.of(vararg elements: Boolean): KoneBooleanArray =
    KoneBooleanArray(elements)

public fun KoneIterable<Boolean>.toKoneMutableBooleanArray(): KoneMutableBooleanArray {
    val iterator = iterator()
    return KoneMutableBooleanArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Boolean>.toKoneBooleanArray(): KoneBooleanArray {
    val iterator = iterator()
    return KoneBooleanArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Char

/**
 * Returns a [KoneMutableCharArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableCharArray(size: UInt, initializer: (UInt) -> Char): KoneMutableCharArray =
    KoneMutableCharArray(CharArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableCharArray] of provided [size] of `false`s.
 */
public fun KoneMutableCharArray(size: UInt): KoneMutableCharArray =
    KoneMutableCharArray(CharArray(size.toInt()))

public inline fun KoneMutableCharArray(indices: UIntRange, initializer: (UInt) -> Char): KoneMutableCharArray =
    KoneMutableCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableCharArray(indices: UIntRange): KoneMutableCharArray =
    KoneMutableCharArray(CharArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneCharArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneCharArray(size: UInt, initializer: (UInt) -> Char): KoneCharArray =
    KoneCharArray(CharArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneCharArray] of provided [size] of `false`s.
 */
public fun KoneCharArray(size: UInt): KoneCharArray =
    KoneCharArray(CharArray(size.toInt()))

public inline fun KoneCharArray(indices: UIntRange, initializer: (UInt) -> Char): KoneCharArray =
    KoneCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneCharArray(indices: UIntRange): KoneCharArray =
    KoneCharArray(CharArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableCharArray.Companion.of(vararg elements: Char): KoneMutableCharArray =
    KoneMutableCharArray(elements)

public fun KoneCharArray.Companion.of(vararg elements: Char): KoneCharArray =
    KoneCharArray(elements)

public fun KoneIterable<Char>.toKoneMutableCharArray(): KoneMutableCharArray {
    val iterator = iterator()
    return KoneMutableCharArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Char>.toKoneCharArray(): KoneCharArray {
    val iterator = iterator()
    return KoneCharArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Byte

/**
 * Returns a [KoneMutableByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableByteArray(size: UInt, initializer: (UInt) -> Byte): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableByteArray] of provided [size] of zeros.
 */
public fun KoneMutableByteArray(size: UInt): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()))

public inline fun KoneMutableByteArray(indices: UIntRange, initializer: (UInt) -> Byte): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableByteArray(indices: UIntRange): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneByteArray(size: UInt, initializer: (UInt) -> Byte): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneByteArray] of provided [size] of zeros.
 */
public fun KoneByteArray(size: UInt): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()))

public inline fun KoneByteArray(indices: UIntRange, initializer: (UInt) -> Byte): KoneByteArray =
    KoneByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneByteArray(indices: UIntRange): KoneByteArray =
    KoneByteArray(ByteArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableByteArray.Companion.of(vararg elements: Byte): KoneMutableByteArray =
    KoneMutableByteArray(elements)

public fun KoneByteArray.Companion.of(vararg elements: Byte): KoneByteArray =
    KoneByteArray(elements)

public fun KoneIterable<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Short

/**
 * Returns a [KoneMutableShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableShortArray(size: UInt, initializer: (UInt) -> Short): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableShortArray] of provided [size] of zeros.
 */
public fun KoneMutableShortArray(size: UInt): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()))

public inline fun KoneMutableShortArray(indices: UIntRange, initializer: (UInt) -> Short): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableShortArray(indices: UIntRange): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneShortArray(size: UInt, initializer: (UInt) -> Short): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneShortArray] of provided [size] of zeros.
 */
public fun KoneShortArray(size: UInt): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()))

public inline fun KoneShortArray(indices: UIntRange, initializer: (UInt) -> Short): KoneShortArray =
    KoneShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneShortArray(indices: UIntRange): KoneShortArray =
    KoneShortArray(ShortArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableShortArray.Companion.of(vararg elements: Short): KoneMutableShortArray =
    KoneMutableShortArray(elements)

public fun KoneShortArray.Companion.of(vararg elements: Short): KoneShortArray =
    KoneShortArray(elements)

public fun KoneIterable<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Int

/**
 * Returns a [KoneMutableIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableIntArray(size: UInt, initializer: (UInt) -> Int): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableIntArray] of provided [size] of zeros.
 */
public fun KoneMutableIntArray(size: UInt): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()))

public inline fun KoneMutableIntArray(indices: UIntRange, initializer: (UInt) -> Int): KoneMutableIntArray =
    KoneMutableIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableIntArray(indices: UIntRange): KoneMutableIntArray =
    KoneMutableIntArray(IntArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneIntArray(size: UInt, initializer: (UInt) -> Int): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneIntArray] of provided [size] of zeros.
 */
public fun KoneIntArray(size: UInt): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()))

public inline fun KoneIntArray(indices: UIntRange, initializer: (UInt) -> Int): KoneIntArray =
    KoneIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneIntArray(indices: UIntRange): KoneIntArray =
    KoneIntArray(IntArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableIntArray.Companion.of(vararg elements: Int): KoneMutableIntArray =
    KoneMutableIntArray(elements)

public fun KoneIntArray.Companion.of(vararg elements: Int): KoneIntArray =
    KoneIntArray(elements)

public fun KoneIterable<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Long

/**
 * Returns a [KoneMutableLongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableLongArray(size: UInt, initializer: (UInt) -> Long): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableLongArray] of provided [size] of zeros.
 */
public fun KoneMutableLongArray(size: UInt): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()))

public inline fun KoneMutableLongArray(indices: UIntRange, initializer: (UInt) -> Long): KoneMutableLongArray =
    KoneMutableLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableLongArray(indices: UIntRange): KoneMutableLongArray =
    KoneMutableLongArray(LongArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneLongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneLongArray(size: UInt, initializer: (UInt) -> Long): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneLongArray] of provided [size] of zeros.
 */
public fun KoneLongArray(size: UInt): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()))

public inline fun KoneLongArray(indices: UIntRange, initializer: (UInt) -> Long): KoneLongArray =
    KoneLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneLongArray(indices: UIntRange): KoneLongArray =
    KoneLongArray(LongArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableLongArray.Companion.of(vararg elements: Long): KoneMutableLongArray =
    KoneMutableLongArray(elements)

public fun KoneLongArray.Companion.of(vararg elements: Long): KoneLongArray =
    KoneLongArray(elements)

public fun KoneIterable<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Float

/**
 * Returns a [KoneMutableFloatArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableFloatArray(size: UInt, initializer: (UInt) -> Float): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableFloatArray] of provided [size] of zeros.
 */
public fun KoneMutableFloatArray(size: UInt): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()))

public inline fun KoneMutableFloatArray(indices: UIntRange, initializer: (UInt) -> Float): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableFloatArray(indices: UIntRange): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneFloatArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneFloatArray(size: UInt, initializer: (UInt) -> Float): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneFloatArray] of provided [size] of zeros.
 */
public fun KoneFloatArray(size: UInt): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()))

public inline fun KoneFloatArray(indices: UIntRange, initializer: (UInt) -> Float): KoneFloatArray =
    KoneFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneFloatArray(indices: UIntRange): KoneFloatArray =
    KoneFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableFloatArray.Companion.of(vararg elements: Float): KoneMutableFloatArray =
    KoneMutableFloatArray(elements)

public fun KoneFloatArray.Companion.of(vararg elements: Float): KoneFloatArray =
    KoneFloatArray(elements)

public fun KoneIterable<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region Double

/**
 * Returns a [KoneMutableDoubleArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableDoubleArray(size: UInt, initializer: (UInt) -> Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableDoubleArray] of provided [size] of zeros.
 */
public fun KoneMutableDoubleArray(size: UInt): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()))

public inline fun KoneMutableDoubleArray(indices: UIntRange, initializer: (UInt) -> Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableDoubleArray(indices: UIntRange): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneDoubleArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneDoubleArray(size: UInt, initializer: (UInt) -> Double): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneDoubleArray] of provided [size] of zeros.
 */
public fun KoneDoubleArray(size: UInt): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()))

public inline fun KoneDoubleArray(indices: UIntRange, initializer: (UInt) -> Double): KoneDoubleArray =
    KoneDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneDoubleArray(indices: UIntRange): KoneDoubleArray =
    KoneDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableDoubleArray.Companion.of(vararg elements: Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(elements)

public fun KoneDoubleArray.Companion.of(vararg elements: Double): KoneDoubleArray =
    KoneDoubleArray(elements)

public fun KoneIterable<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region UByte

/**
 * Returns a [KoneMutableUByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUByteArray(size: UInt, initializer: (UInt) -> UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableUByteArray] of provided [size] of zeros.
 */
public fun KoneMutableUByteArray(size: UInt): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()))

public inline fun KoneMutableUByteArray(indices: UIntRange, initializer: (UInt) -> UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableUByteArray(indices: UIntRange): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneUByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUByteArray(size: UInt, initializer: (UInt) -> UByte): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneUByteArray] of provided [size] of zeros.
 */
public fun KoneUByteArray(size: UInt): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()))

public inline fun KoneUByteArray(indices: UIntRange, initializer: (UInt) -> UByte): KoneUByteArray =
    KoneUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneUByteArray(indices: UIntRange): KoneUByteArray =
    KoneUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableUByteArray.Companion.of(vararg elements: UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(elements)

public fun KoneUByteArray.Companion.of(vararg elements: UByte): KoneUByteArray =
    KoneUByteArray(elements)

public fun KoneIterable<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region UShort

/**
 * Returns a [KoneMutableUShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUShortArray(size: UInt, initializer: (UInt) -> UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableUShortArray] of provided [size] of zeros.
 */
public fun KoneMutableUShortArray(size: UInt): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()))

public inline fun KoneMutableUShortArray(indices: UIntRange, initializer: (UInt) -> UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableUShortArray(indices: UIntRange): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneUShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUShortArray(size: UInt, initializer: (UInt) -> UShort): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneUShortArray] of provided [size] of zeros.
 */
public fun KoneUShortArray(size: UInt): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()))

public inline fun KoneUShortArray(indices: UIntRange, initializer: (UInt) -> UShort): KoneUShortArray =
    KoneUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneUShortArray(indices: UIntRange): KoneUShortArray =
    KoneUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableUShortArray.Companion.of(vararg elements: UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(elements)

public fun KoneUShortArray.Companion.of(vararg elements: UShort): KoneUShortArray =
    KoneUShortArray(elements)

public fun KoneIterable<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region UInt

/**
 * Returns a [KoneMutableUIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUIntArray(size: UInt, initializer: (UInt) -> UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableUIntArray] of provided [size] of zeros.
 */
public fun KoneMutableUIntArray(size: UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()))

public inline fun KoneMutableUIntArray(indices: UIntRange, initializer: (UInt) -> UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableUIntArray(indices: UIntRange): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneUIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUIntArray(size: UInt, initializer: (UInt) -> UInt): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneUIntArray] of provided [size] of zeros.
 */
public fun KoneUIntArray(size: UInt): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()))

public inline fun KoneUIntArray(indices: UIntRange, initializer: (UInt) -> UInt): KoneUIntArray =
    KoneUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneUIntArray(indices: UIntRange): KoneUIntArray =
    KoneUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableUIntArray.Companion.of(vararg elements: UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(elements)

public fun KoneUIntArray.Companion.of(vararg elements: UInt): KoneUIntArray =
    KoneUIntArray(elements)

public fun KoneIterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size) { iterator.getAndMoveNext() }
}

// endregion

// region ULong

/**
 * Returns a [KoneMutableULongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableULongArray(size: UInt, initializer: (UInt) -> ULong): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneMutableULongArray] of provided [size] of zeros.
 */
public fun KoneMutableULongArray(size: UInt): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()))

public inline fun KoneMutableULongArray(indices: UIntRange, initializer: (UInt) -> ULong): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneMutableULongArray(indices: UIntRange): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray((indices.last + 1u - indices.first).toInt()))

/**
 * Returns a [KoneULongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneULongArray(size: UInt, initializer: (UInt) -> ULong): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()) { initializer(it.toUInt()) })

/**
 * Returns a [KoneULongArray] of provided [size] of zeros.
 */
public fun KoneULongArray(size: UInt): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()))

public inline fun KoneULongArray(indices: UIntRange, initializer: (UInt) -> ULong): KoneULongArray =
    KoneULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public fun KoneULongArray(indices: UIntRange): KoneULongArray =
    KoneULongArray(ULongArray((indices.last + 1u - indices.first).toInt()))

public fun KoneMutableULongArray.Companion.of(vararg elements: ULong): KoneMutableULongArray =
    KoneMutableULongArray(elements)

public fun KoneULongArray.Companion.of(vararg elements: ULong): KoneULongArray =
    KoneULongArray(elements)

public fun KoneIterable<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size) { iterator.getAndMoveNext() }
}

// endregion
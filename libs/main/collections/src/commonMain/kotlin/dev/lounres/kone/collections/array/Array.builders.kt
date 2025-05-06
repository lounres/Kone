/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")
@file:OptIn(DelicateImmutableArrayConstructor::class)

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

/**
 * Returns a [KoneArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <reified Element> KoneArray(size: UInt, initializer: (UInt) -> Element): KoneArray<Element> =
    KoneArray(Array(size.toInt()) { initializer(it.toUInt()) })

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
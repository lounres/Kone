/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.array

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.getAndMoveNext


// region General arrays

public inline fun <reified Element> KoneMutableArray(size: UInt, init: (UInt) -> Element): KoneMutableArray<Element> =
    KoneMutableArray(Array(size.toInt()) { init(it.toUInt()) })

public inline fun <reified Element> KoneArray(size: UInt, init: (UInt) -> Element): KoneArray<Element> =
    KoneArray(Array(size.toInt()) { init(it.toUInt()) })

public inline fun <reified Element> koneMutableArrayOf(vararg elements: Element): KoneMutableArray<Element> =
    KoneMutableArray(elements as Array<Element>)

public inline fun <reified Element> koneArrayOf(vararg elements: Element): KoneArray<Element> =
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

public inline fun KoneMutableByteArray(size: UInt, init: (UInt) -> Byte): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableByteArray(size: UInt): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()))

public inline fun KoneByteArray(size: UInt, init: (UInt) -> Byte): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()) { init(it.toUInt()) })

public fun KoneByteArray(size: UInt): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()))

public fun koneMutableByteArrayOf(vararg elements: Byte): KoneMutableByteArray =
    KoneMutableByteArray(elements)

public fun koneByteArrayOf(vararg elements: Byte): KoneByteArray =
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

public inline fun KoneMutableShortArray(size: UInt, init: (UInt) -> Short): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableShortArray(size: UInt): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()))

public inline fun KoneShortArray(size: UInt, init: (UInt) -> Short): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()) { init(it.toUInt()) })

public fun KoneShortArray(size: UInt): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()))

public fun koneMutableShortArrayOf(vararg elements: Short): KoneMutableShortArray =
    KoneMutableShortArray(elements)

public fun koneShortArrayOf(vararg elements: Short): KoneShortArray =
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

public inline fun KoneMutableIntArray(size: UInt, init: (UInt) -> Int): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableIntArray(size: UInt): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()))

public inline fun KoneIntArray(size: UInt, init: (UInt) -> Int): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()) { init(it.toUInt()) })

public fun KoneIntArray(size: UInt): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()))

public fun koneMutableIntArrayOf(vararg elements: Int): KoneMutableIntArray =
    KoneMutableIntArray(elements)

public fun koneIntArrayOf(vararg elements: Int): KoneIntArray =
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

public inline fun KoneMutableLongArray(size: UInt, init: (UInt) -> Long): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableLongArray(size: UInt): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()))

public inline fun KoneLongArray(size: UInt, init: (UInt) -> Long): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()) { init(it.toUInt()) })

public fun KoneLongArray(size: UInt): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()))

public fun koneMutableLongArrayOf(vararg elements: Long): KoneMutableLongArray =
    KoneMutableLongArray(elements)

public fun koneLongArrayOf(vararg elements: Long): KoneLongArray =
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

public inline fun KoneMutableFloatArray(size: UInt, init: (UInt) -> Float): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableFloatArray(size: UInt): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()))

public inline fun KoneFloatArray(size: UInt, init: (UInt) -> Float): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()) { init(it.toUInt()) })

public fun KoneFloatArray(size: UInt): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()))

public fun koneMutableFloatArrayOf(vararg elements: Float): KoneMutableFloatArray =
    KoneMutableFloatArray(elements)

public fun koneFloatArrayOf(vararg elements: Float): KoneFloatArray =
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

public inline fun KoneMutableDoubleArray(size: UInt, init: (UInt) -> Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableDoubleArray(size: UInt): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()))

public inline fun KoneDoubleArray(size: UInt, init: (UInt) -> Double): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()) { init(it.toUInt()) })

public fun KoneDoubleArray(size: UInt): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()))

public fun koneMutableDoubleArrayOf(vararg elements: Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(elements)

public fun koneDoubleArrayOf(vararg elements: Double): KoneDoubleArray =
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

public inline fun KoneMutableUByteArray(size: UInt, init: (UInt) -> UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableUByteArray(size: UInt): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()))

public inline fun KoneUByteArray(size: UInt, init: (UInt) -> UByte): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()) { init(it.toUInt()) })

public fun KoneUByteArray(size: UInt): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()))

public fun koneMutableUByteArrayOf(vararg elements: UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(elements)

public fun koneUByteArrayOf(vararg elements: UByte): KoneUByteArray =
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

public inline fun KoneMutableUShortArray(size: UInt, init: (UInt) -> UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableUShortArray(size: UInt): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()))

public inline fun KoneUShortArray(size: UInt, init: (UInt) -> UShort): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()) { init(it.toUInt()) })

public fun KoneUShortArray(size: UInt): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()))

public fun koneMutableUShortArrayOf(vararg elements: UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(elements)

public fun koneUShortArrayOf(vararg elements: UShort): KoneUShortArray =
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

public inline fun KoneMutableUIntArray(size: UInt, init: (UInt) -> UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableUIntArray(size: UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()))

public inline fun KoneUIntArray(size: UInt, init: (UInt) -> UInt): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })

public fun KoneUIntArray(size: UInt): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()))

public fun koneMutableUIntArrayOf(vararg elements: UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(elements)

public fun koneUIntArrayOf(vararg elements: UInt): KoneUIntArray =
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

public inline fun KoneMutableULongArray(size: UInt, init: (UInt) -> ULong): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()) { init(it.toUInt()) })

public fun KoneMutableULongArray(size: UInt): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()))

public inline fun KoneULongArray(size: UInt, init: (UInt) -> ULong): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()) { init(it.toUInt()) })

public fun KoneULongArray(size: UInt): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()))

public fun koneMutableULongArrayOf(vararg elements: ULong): KoneMutableULongArray =
    KoneMutableULongArray(elements)

public fun koneULongArrayOf(vararg elements: ULong): KoneULongArray =
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
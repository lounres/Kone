/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneArrayGrowableList


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
    if (this is KoneList<Element>) return this.toKoneMutableArray()
    if (this is KoneSet<Element>) return this.toKoneMutableArray()

    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneMutableArray()
}
public inline fun <reified Element> KoneList<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray(size) { iterator.next() }
}
public inline fun <reified Element> KoneSet<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray(size) { iterator.next() }
}

public inline fun <reified Element> KoneIterable<Element>.toKoneArray(): KoneArray<Element> {
    if (this is KoneList<Element>) return this.toKoneArray()
    if (this is KoneSet<Element>) return this.toKoneArray()

    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneArray()
}
public inline fun <reified Element> KoneList<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray(size) { iterator.next() }
}
public inline fun <reified Element> KoneSet<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray(size) { iterator.next() }
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
    if (this is KoneList<Byte>) return this.toKoneMutableByteArray()
    if (this is KoneSet<Byte>) return this.toKoneMutableByteArray()
    
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneMutableByteArray()
}
public fun KoneList<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size) { iterator.next() }
}
public fun KoneSet<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size) { iterator.next() }
}

public fun KoneIterable<Byte>.toKoneByteArray(): KoneByteArray {
    if (this is KoneList<Byte>) return this.toKoneByteArray()
    if (this is KoneSet<Byte>) return this.toKoneByteArray()
    
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneByteArray()
}
public fun KoneList<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size) { iterator.next() }
}
public fun KoneSet<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size) { iterator.next() }
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
    if (this is KoneList<Short>) return this.toKoneMutableShortArray()
    if (this is KoneSet<Short>) return this.toKoneMutableShortArray()
    
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneMutableShortArray()
}
public fun KoneList<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size) { iterator.next() }
}
public fun KoneSet<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size) { iterator.next() }
}

public fun KoneIterable<Short>.toKoneShortArray(): KoneShortArray {
    if (this is KoneList<Short>) return this.toKoneShortArray()
    if (this is KoneSet<Short>) return this.toKoneShortArray()
    
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneShortArray()
}
public fun KoneList<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size) { iterator.next() }
}
public fun KoneSet<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size) { iterator.next() }
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
    if (this is KoneList<Int>) return this.toKoneMutableIntArray()
    if (this is KoneSet<Int>) return this.toKoneMutableIntArray()
    
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneMutableIntArray()
}
public fun KoneList<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size) { iterator.next() }
}
public fun KoneSet<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size) { iterator.next() }
}

public fun KoneIterable<Int>.toKoneIntArray(): KoneIntArray {
    if (this is KoneList<Int>) return this.toKoneIntArray()
    if (this is KoneSet<Int>) return this.toKoneIntArray()
    
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneIntArray()
}
public fun KoneList<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size) { iterator.next() }
}
public fun KoneSet<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size) { iterator.next() }
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
    if (this is KoneList<Long>) return this.toKoneMutableLongArray()
    if (this is KoneSet<Long>) return this.toKoneMutableLongArray()
    
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneMutableLongArray()
}
public fun KoneList<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size) { iterator.next() }
}
public fun KoneSet<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size) { iterator.next() }
}

public fun KoneIterable<Long>.toKoneLongArray(): KoneLongArray {
    if (this is KoneList<Long>) return this.toKoneLongArray()
    if (this is KoneSet<Long>) return this.toKoneLongArray()
    
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneLongArray()
}
public fun KoneList<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size) { iterator.next() }
}
public fun KoneSet<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size) { iterator.next() }
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
    if (this is KoneList<Float>) return this.toKoneMutableFloatArray()
    if (this is KoneSet<Float>) return this.toKoneMutableFloatArray()
    
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneMutableFloatArray()
}
public fun KoneList<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size) { iterator.next() }
}
public fun KoneSet<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size) { iterator.next() }
}

public fun KoneIterable<Float>.toKoneFloatArray(): KoneFloatArray {
    if (this is KoneList<Float>) return this.toKoneFloatArray()
    if (this is KoneSet<Float>) return this.toKoneFloatArray()
    
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneFloatArray()
}
public fun KoneList<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size) { iterator.next() }
}
public fun KoneSet<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size) { iterator.next() }
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
    if (this is KoneList<Double>) return this.toKoneMutableDoubleArray()
    if (this is KoneSet<Double>) return this.toKoneMutableDoubleArray()
    
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneMutableDoubleArray()
}
public fun KoneList<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size) { iterator.next() }
}
public fun KoneSet<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size) { iterator.next() }
}

public fun KoneIterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    if (this is KoneList<Double>) return this.toKoneDoubleArray()
    if (this is KoneSet<Double>) return this.toKoneDoubleArray()
    
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneDoubleArray()
}
public fun KoneList<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size) { iterator.next() }
}
public fun KoneSet<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size) { iterator.next() }
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
    if (this is KoneList<UByte>) return this.toKoneMutableUByteArray()
    if (this is KoneSet<UByte>) return this.toKoneMutableUByteArray()
    
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneMutableUByteArray()
}
public fun KoneList<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size) { iterator.next() }
}
public fun KoneSet<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size) { iterator.next() }
}

public fun KoneIterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    if (this is KoneList<UByte>) return this.toKoneUByteArray()
    if (this is KoneSet<UByte>) return this.toKoneUByteArray()
    
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneUByteArray()
}
public fun KoneList<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size) { iterator.next() }
}
public fun KoneSet<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size) { iterator.next() }
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
    if (this is KoneList<UShort>) return this.toKoneMutableUShortArray()
    if (this is KoneSet<UShort>) return this.toKoneMutableUShortArray()
    
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneMutableUShortArray()
}
public fun KoneList<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size) { iterator.next() }
}
public fun KoneSet<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size) { iterator.next() }
}

public fun KoneIterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    if (this is KoneList<UShort>) return this.toKoneUShortArray()
    if (this is KoneSet<UShort>) return this.toKoneUShortArray()
    
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneUShortArray()
}
public fun KoneList<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size) { iterator.next() }
}
public fun KoneSet<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size) { iterator.next() }
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
    if (this is KoneList<UInt>) return this.toKoneMutableUIntArray()
    if (this is KoneSet<UInt>) return this.toKoneMutableUIntArray()

    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneMutableUIntArray()
}
public fun KoneList<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size) { iterator.next() }
}
public fun KoneSet<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size) { iterator.next() }
}

public fun KoneIterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    if (this is KoneList<UInt>) return this.toKoneUIntArray()
    if (this is KoneSet<UInt>) return this.toKoneUIntArray()

    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneUIntArray()
}
public fun KoneList<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size) { iterator.next() }
}
public fun KoneSet<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size) { iterator.next() }
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
    if (this is KoneList<ULong>) return this.toKoneMutableULongArray()
    if (this is KoneSet<ULong>) return this.toKoneMutableULongArray()
    
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneMutableULongArray()
}
public fun KoneList<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size) { iterator.next() }
}
public fun KoneSet<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size) { iterator.next() }
}

public fun KoneIterable<ULong>.toKoneULongArray(): KoneULongArray {
    if (this is KoneList<ULong>) return this.toKoneULongArray()
    if (this is KoneSet<ULong>) return this.toKoneULongArray()
    
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneULongArray()
}
public fun KoneList<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size) { iterator.next() }
}
public fun KoneSet<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size) { iterator.next() }
}

// endregion
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.comparison.defaultEquality


// region General arrays

public inline fun <reified E> KoneMutableArray(size: UInt, init: (UInt) -> E): KoneMutableArray<E> =
    KoneMutableArray(Array(size.toInt()) { init(it.toUInt()) })

public inline fun <reified E> KoneArray(size: UInt, init: (UInt) -> E): KoneArray<E> =
    KoneArray(Array(size.toInt()) { init(it.toUInt()) })

public inline fun <reified E> koneMutableArrayOf(vararg elements: E): KoneMutableArray<E> =
    KoneMutableArray(elements as Array<E>)

public inline fun <reified E> koneArrayOf(vararg elements: E): KoneArray<E> =
    KoneArray(elements as Array<E>)

public inline fun <reified E> KoneIterable<E>.toKoneMutableArray(): KoneMutableArray<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.add(element)
    return result.toKoneMutableArray()
}
public inline fun <reified E> Iterable<E>.toKoneMutableArray(): KoneMutableArray<E> {
    if (this is Collection<E>) return this.toKoneMutableArray()

    val result = KoneGrowableArrayList(elementContext = defaultEquality<E>())
    for (element in this) result.add(element)
    return result.toKoneMutableArray()
}
public inline fun <reified E> KoneIterableCollection<E>.toKoneMutableArray(): KoneMutableArray<E> {
    val iterator = iterator()
    return KoneMutableArray(size) { iterator.next() }
}
public inline fun <reified E> Collection<E>.toKoneMutableArray(): KoneMutableArray<E> {
    val iterator = iterator()
    return KoneMutableArray(size.toUInt()) { iterator.next() }
}
public inline fun <reified E> KoneList<E>.toKoneMutableArray(): KoneMutableArray<E> =
    KoneMutableArray(size) { this[it] }
public inline fun <reified E> KoneIterableList<E>.toKoneMutableArray(): KoneMutableArray<E> {
    val iterator = iterator()
    return KoneMutableArray(size) { iterator.next() }
}

public inline fun <reified E> KoneIterable<E>.toKoneArray(): KoneArray<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.add(element)
    return result.toKoneArray()
}
public inline fun <reified E> Iterable<E>.toKoneArray(): KoneArray<E> {
    if (this is Collection<E>) return this.toKoneArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.add(element)
    return result.toKoneArray()
}
public inline fun <reified E> KoneIterableCollection<E>.toKoneArray(): KoneArray<E> {
    val iterator = iterator()
    return KoneArray(size) { iterator.next() }
}
public inline fun <reified E> Collection<E>.toKoneArray(): KoneArray<E> {
    val iterator = iterator()
    return KoneArray(size.toUInt()) { iterator.next() }
}
public inline fun <reified E> KoneList<E>.toKoneArray(): KoneArray<E> =
    KoneArray(size) { this[it] }
public inline fun <reified E> KoneIterableList<E>.toKoneArray(): KoneArray<E> {
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
    if (this is KoneIterableCollection<Byte>) return this.toKoneMutableByteArray()
    
    val result = KoneGrowableArrayList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneMutableByteArray()
}
public fun Iterable<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    if (this is Collection<Byte>) return this.toKoneMutableByteArray()
    
    val result = KoneGrowableArrayList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneMutableByteArray()
}
public fun KoneIterableCollection<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size) { iterator.next() }
}
public fun Collection<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Byte>.toKoneMutableByteArray(): KoneMutableByteArray =
    KoneMutableByteArray(size) { this[it] }
public fun KoneIterableList<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size) { iterator.next() }
}

public fun KoneIterable<Byte>.toKoneByteArray(): KoneByteArray {
    if (this is KoneIterableCollection<Byte>) return this.toKoneByteArray()
    
    val result = KoneGrowableArrayList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneByteArray()
}
public fun Iterable<Byte>.toKoneByteArray(): KoneByteArray {
    if (this is Collection<Byte>) return this.toKoneByteArray()
    
    val result = KoneGrowableArrayList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneByteArray()
}
public fun KoneIterableCollection<Byte>.toKoneUIntArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size) { iterator.next() }
}
public fun Collection<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Byte>.toKoneUIntArray(): KoneByteArray =
    KoneByteArray(size) { this[it] }
public fun KoneIterableList<Byte>.toKoneUIntArray(): KoneByteArray {
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
    if (this is KoneIterableCollection<Short>) return this.toKoneMutableShortArray()
    
    val result = KoneGrowableArrayList<Short>()
    for (element in this) result.add(element)
    return result.toKoneMutableShortArray()
}
public fun Iterable<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    if (this is Collection<Short>) return this.toKoneMutableShortArray()
    
    val result = KoneGrowableArrayList<Short>()
    for (element in this) result.add(element)
    return result.toKoneMutableShortArray()
}
public fun KoneIterableCollection<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size) { iterator.next() }
}
public fun Collection<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Short>.toKoneMutableShortArray(): KoneMutableShortArray =
    KoneMutableShortArray(size) { this[it] }
public fun KoneIterableList<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size) { iterator.next() }
}

public fun KoneIterable<Short>.toKoneShortArray(): KoneShortArray {
    if (this is KoneIterableCollection<Short>) return this.toKoneShortArray()
    
    val result = KoneGrowableArrayList<Short>()
    for (element in this) result.add(element)
    return result.toKoneShortArray()
}
public fun Iterable<Short>.toKoneShortArray(): KoneShortArray {
    if (this is Collection<Short>) return this.toKoneShortArray()
    
    val result = KoneGrowableArrayList<Short>()
    for (element in this) result.add(element)
    return result.toKoneShortArray()
}
public fun KoneIterableCollection<Short>.toKoneUIntArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size) { iterator.next() }
}
public fun Collection<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Short>.toKoneUIntArray(): KoneShortArray =
    KoneShortArray(size) { this[it] }
public fun KoneIterableList<Short>.toKoneUIntArray(): KoneShortArray {
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
    if (this is KoneIterableCollection<Int>) return this.toKoneMutableIntArray()
    
    val result = KoneGrowableArrayList<Int>()
    for (element in this) result.add(element)
    return result.toKoneMutableIntArray()
}
public fun Iterable<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    if (this is Collection<Int>) return this.toKoneMutableIntArray()
    
    val result = KoneGrowableArrayList<Int>()
    for (element in this) result.add(element)
    return result.toKoneMutableIntArray()
}
public fun KoneIterableCollection<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size) { iterator.next() }
}
public fun Collection<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Int>.toKoneMutableIntArray(): KoneMutableIntArray =
    KoneMutableIntArray(size) { this[it] }
public fun KoneIterableList<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size) { iterator.next() }
}

public fun KoneIterable<Int>.toKoneIntArray(): KoneIntArray {
    if (this is KoneIterableCollection<Int>) return this.toKoneIntArray()
    
    val result = KoneGrowableArrayList<Int>()
    for (element in this) result.add(element)
    return result.toKoneIntArray()
}
public fun Iterable<Int>.toKoneIntArray(): KoneIntArray {
    if (this is Collection<Int>) return this.toKoneIntArray()
    
    val result = KoneGrowableArrayList<Int>()
    for (element in this) result.add(element)
    return result.toKoneIntArray()
}
public fun KoneIterableCollection<Int>.toKoneUIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size) { iterator.next() }
}
public fun Collection<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Int>.toKoneUIntArray(): KoneIntArray =
    KoneIntArray(size) { this[it] }
public fun KoneIterableList<Int>.toKoneUIntArray(): KoneIntArray {
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
    if (this is KoneIterableCollection<Long>) return this.toKoneMutableLongArray()
    
    val result = KoneGrowableArrayList<Long>()
    for (element in this) result.add(element)
    return result.toKoneMutableLongArray()
}
public fun Iterable<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    if (this is Collection<Long>) return this.toKoneMutableLongArray()
    
    val result = KoneGrowableArrayList<Long>()
    for (element in this) result.add(element)
    return result.toKoneMutableLongArray()
}
public fun KoneIterableCollection<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size) { iterator.next() }
}
public fun Collection<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Long>.toKoneMutableLongArray(): KoneMutableLongArray =
    KoneMutableLongArray(size) { this[it] }
public fun KoneIterableList<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size) { iterator.next() }
}

public fun KoneIterable<Long>.toKoneLongArray(): KoneLongArray {
    if (this is KoneIterableCollection<Long>) return this.toKoneLongArray()
    
    val result = KoneGrowableArrayList<Long>()
    for (element in this) result.add(element)
    return result.toKoneLongArray()
}
public fun Iterable<Long>.toKoneLongArray(): KoneLongArray {
    if (this is Collection<Long>) return this.toKoneLongArray()
    
    val result = KoneGrowableArrayList<Long>()
    for (element in this) result.add(element)
    return result.toKoneLongArray()
}
public fun KoneIterableCollection<Long>.toKoneUIntArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size) { iterator.next() }
}
public fun Collection<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Long>.toKoneUIntArray(): KoneLongArray =
    KoneLongArray(size) { this[it] }
public fun KoneIterableList<Long>.toKoneUIntArray(): KoneLongArray {
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
    if (this is KoneIterableCollection<Float>) return this.toKoneMutableFloatArray()
    
    val result = KoneGrowableArrayList<Float>()
    for (element in this) result.add(element)
    return result.toKoneMutableFloatArray()
}
public fun Iterable<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    if (this is Collection<Float>) return this.toKoneMutableFloatArray()
    
    val result = KoneGrowableArrayList<Float>()
    for (element in this) result.add(element)
    return result.toKoneMutableFloatArray()
}
public fun KoneIterableCollection<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size) { iterator.next() }
}
public fun Collection<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray =
    KoneMutableFloatArray(size) { this[it] }
public fun KoneIterableList<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size) { iterator.next() }
}

public fun KoneIterable<Float>.toKoneFloatArray(): KoneFloatArray {
    if (this is KoneIterableCollection<Float>) return this.toKoneFloatArray()
    
    val result = KoneGrowableArrayList<Float>()
    for (element in this) result.add(element)
    return result.toKoneFloatArray()
}
public fun Iterable<Float>.toKoneFloatArray(): KoneFloatArray {
    if (this is Collection<Float>) return this.toKoneFloatArray()
    
    val result = KoneGrowableArrayList<Float>()
    for (element in this) result.add(element)
    return result.toKoneFloatArray()
}
public fun KoneIterableCollection<Float>.toKoneUIntArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size) { iterator.next() }
}
public fun Collection<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Float>.toKoneUIntArray(): KoneFloatArray =
    KoneFloatArray(size) { this[it] }
public fun KoneIterableList<Float>.toKoneUIntArray(): KoneFloatArray {
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
    if (this is KoneIterableCollection<Double>) return this.toKoneMutableDoubleArray()
    
    val result = KoneGrowableArrayList<Double>()
    for (element in this) result.add(element)
    return result.toKoneMutableDoubleArray()
}
public fun Iterable<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    if (this is Collection<Double>) return this.toKoneMutableDoubleArray()
    
    val result = KoneGrowableArrayList<Double>()
    for (element in this) result.add(element)
    return result.toKoneMutableDoubleArray()
}
public fun KoneIterableCollection<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size) { iterator.next() }
}
public fun Collection<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray =
    KoneMutableDoubleArray(size) { this[it] }
public fun KoneIterableList<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size) { iterator.next() }
}

public fun KoneIterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    if (this is KoneIterableCollection<Double>) return this.toKoneDoubleArray()
    
    val result = KoneGrowableArrayList<Double>()
    for (element in this) result.add(element)
    return result.toKoneDoubleArray()
}
public fun Iterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    if (this is Collection<Double>) return this.toKoneDoubleArray()
    
    val result = KoneGrowableArrayList<Double>()
    for (element in this) result.add(element)
    return result.toKoneDoubleArray()
}
public fun KoneIterableCollection<Double>.toKoneUIntArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size) { iterator.next() }
}
public fun Collection<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<Double>.toKoneUIntArray(): KoneDoubleArray =
    KoneDoubleArray(size) { this[it] }
public fun KoneIterableList<Double>.toKoneUIntArray(): KoneDoubleArray {
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
    if (this is KoneIterableCollection<UByte>) return this.toKoneMutableUByteArray()
    
    val result = KoneGrowableArrayList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneMutableUByteArray()
}
public fun Iterable<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    if (this is Collection<UByte>) return this.toKoneMutableUByteArray()
    
    val result = KoneGrowableArrayList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneMutableUByteArray()
}
public fun KoneIterableCollection<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size) { iterator.next() }
}
public fun Collection<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray =
    KoneMutableUByteArray(size) { this[it] }
public fun KoneIterableList<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size) { iterator.next() }
}

public fun KoneIterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    if (this is KoneIterableCollection<UByte>) return this.toKoneUByteArray()
    
    val result = KoneGrowableArrayList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneUByteArray()
}
public fun Iterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    if (this is Collection<UByte>) return this.toKoneUByteArray()
    
    val result = KoneGrowableArrayList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneUByteArray()
}
public fun KoneIterableCollection<UByte>.toKoneUIntArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size) { iterator.next() }
}
public fun Collection<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UByte>.toKoneUIntArray(): KoneUByteArray =
    KoneUByteArray(size) { this[it] }
public fun KoneIterableList<UByte>.toKoneUIntArray(): KoneUByteArray {
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
    if (this is KoneIterableCollection<UShort>) return this.toKoneMutableUShortArray()
    
    val result = KoneGrowableArrayList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneMutableUShortArray()
}
public fun Iterable<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    if (this is Collection<UShort>) return this.toKoneMutableUShortArray()
    
    val result = KoneGrowableArrayList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneMutableUShortArray()
}
public fun KoneIterableCollection<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size) { iterator.next() }
}
public fun Collection<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray =
    KoneMutableUShortArray(size) { this[it] }
public fun KoneIterableList<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size) { iterator.next() }
}

public fun KoneIterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    if (this is KoneIterableCollection<UShort>) return this.toKoneUShortArray()
    
    val result = KoneGrowableArrayList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneUShortArray()
}
public fun Iterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    if (this is Collection<UShort>) return this.toKoneUShortArray()
    
    val result = KoneGrowableArrayList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneUShortArray()
}
public fun KoneIterableCollection<UShort>.toKoneUIntArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size) { iterator.next() }
}
public fun Collection<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UShort>.toKoneUIntArray(): KoneUShortArray =
    KoneUShortArray(size) { this[it] }
public fun KoneIterableList<UShort>.toKoneUIntArray(): KoneUShortArray {
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
    if (this is KoneIterableCollection<UInt>) return this.toKoneMutableUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneMutableUIntArray()
}
public fun Iterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    if (this is Collection<UInt>) return this.toKoneMutableUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneMutableUIntArray()
}
public fun KoneIterableCollection<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size) { iterator.next() }
}
public fun Collection<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray =
    KoneMutableUIntArray(size) { this[it] }
public fun KoneIterableList<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size) { iterator.next() }
}

public fun KoneIterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    if (this is KoneIterableCollection<UInt>) return this.toKoneUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneUIntArray()
}
public fun Iterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    if (this is Collection<UInt>) return this.toKoneUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneUIntArray()
}
public fun KoneIterableCollection<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size) { iterator.next() }
}
public fun Collection<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<UInt>.toKoneUIntArray(): KoneUIntArray =
    KoneUIntArray(size) { this[it] }
public fun KoneIterableList<UInt>.toKoneUIntArray(): KoneUIntArray {
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
    if (this is KoneIterableCollection<ULong>) return this.toKoneMutableULongArray()
    
    val result = KoneGrowableArrayList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneMutableULongArray()
}
public fun Iterable<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    if (this is Collection<ULong>) return this.toKoneMutableULongArray()
    
    val result = KoneGrowableArrayList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneMutableULongArray()
}
public fun KoneIterableCollection<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size) { iterator.next() }
}
public fun Collection<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<ULong>.toKoneMutableULongArray(): KoneMutableULongArray =
    KoneMutableULongArray(size) { this[it] }
public fun KoneIterableList<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size) { iterator.next() }
}

public fun KoneIterable<ULong>.toKoneULongArray(): KoneULongArray {
    if (this is KoneIterableCollection<ULong>) return this.toKoneULongArray()
    
    val result = KoneGrowableArrayList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneULongArray()
}
public fun Iterable<ULong>.toKoneULongArray(): KoneULongArray {
    if (this is Collection<ULong>) return this.toKoneULongArray()
    
    val result = KoneGrowableArrayList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneULongArray()
}
public fun KoneIterableCollection<ULong>.toKoneUIntArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size) { iterator.next() }
}
public fun Collection<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size.toUInt()) { iterator.next() }
}
public fun KoneList<ULong>.toKoneUIntArray(): KoneULongArray =
    KoneULongArray(size) { this[it] }
public fun KoneIterableList<ULong>.toKoneUIntArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size) { iterator.next() }
}

// endregion
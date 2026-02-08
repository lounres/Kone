/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array


// TODO: Think about what to add for Kone arrays

public fun <Element> KoneMutableArray<Element>.toKoneArray(): KoneArray<Element> = KoneArray(array.copyOf())
public fun KoneMutableBooleanArray.toKoneBooleanArray(): KoneBooleanArray = KoneBooleanArray(array.copyOf())
public fun KoneMutableCharArray.toKoneCharArray(): KoneCharArray = KoneCharArray(array.copyOf())
public fun KoneMutableByteArray.toKoneByteArray(): KoneByteArray = KoneByteArray(array.copyOf())
public fun KoneMutableShortArray.toKoneShortArray(): KoneShortArray = KoneShortArray(array.copyOf())
public fun KoneMutableIntArray.toKoneIntArray(): KoneIntArray = KoneIntArray(array.copyOf())
public fun KoneMutableLongArray.toKoneLongArray(): KoneLongArray = KoneLongArray(array.copyOf())
public fun KoneMutableFloatArray.toKoneFloatArray(): KoneFloatArray = KoneFloatArray(array.copyOf())
public fun KoneMutableDoubleArray.toKoneDoubleArray(): KoneDoubleArray = KoneDoubleArray(array.copyOf())
public fun KoneMutableUByteArray.toKoneUByteArray(): KoneUByteArray = KoneUByteArray(array.copyOf())
public fun KoneMutableUShortArray.toKoneUShortArray(): KoneUShortArray = KoneUShortArray(array.copyOf())
public fun KoneMutableUIntArray.toKoneUIntArray(): KoneUIntArray = KoneUIntArray(array.copyOf())
public fun KoneMutableULongArray.toKoneULongArray(): KoneULongArray = KoneULongArray(array.copyOf())

public fun <Element> KoneMutableArray<Element>.asKoneArray(): KoneArray<Element> = KoneArray(array)
public fun KoneMutableBooleanArray.asKoneBooleanArray(): KoneBooleanArray = KoneBooleanArray(array)
public fun KoneMutableCharArray.asKoneCharArray(): KoneCharArray = KoneCharArray(array)
public fun KoneMutableByteArray.asKoneByteArray(): KoneByteArray = KoneByteArray(array)
public fun KoneMutableShortArray.asKoneShortArray(): KoneShortArray = KoneShortArray(array)
public fun KoneMutableIntArray.asKoneIntArray(): KoneIntArray = KoneIntArray(array)
public fun KoneMutableLongArray.asKoneLongArray(): KoneLongArray = KoneLongArray(array)
public fun KoneMutableFloatArray.asKoneFloatArray(): KoneFloatArray = KoneFloatArray(array)
public fun KoneMutableDoubleArray.asKoneDoubleArray(): KoneDoubleArray = KoneDoubleArray(array)
public fun KoneMutableUByteArray.asKoneUByteArray(): KoneUByteArray = KoneUByteArray(array)
public fun KoneMutableUShortArray.asKoneUShortArray(): KoneUShortArray = KoneUShortArray(array)
public fun KoneMutableUIntArray.asKoneUIntArray(): KoneUIntArray = KoneUIntArray(array)
public fun KoneMutableULongArray.asKoneULongArray(): KoneULongArray = KoneULongArray(array)

// TODO: Wait for actual decision with equals operator
public infix fun KoneByteArray.contentEquals(other: KoneByteArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneBooleanArray.contentEquals(other: KoneBooleanArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneCharArray.contentEquals(other: KoneCharArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneShortArray.contentEquals(other: KoneShortArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneIntArray.contentEquals(other: KoneIntArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneLongArray.contentEquals(other: KoneLongArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneFloatArray.contentEquals(other: KoneFloatArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneDoubleArray.contentEquals(other: KoneDoubleArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneUByteArray.contentEquals(other: KoneUByteArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneUShortArray.contentEquals(other: KoneUShortArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneUIntArray.contentEquals(other: KoneUIntArray): Boolean = this.array.contentEquals(other.array)
public infix fun KoneULongArray.contentEquals(other: KoneULongArray): Boolean = this.array.contentEquals(other.array)

// TODO: Wait for actual decision with hashCode operator
public fun KoneByteArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneBooleanArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneCharArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneShortArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneIntArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneLongArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneFloatArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneDoubleArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneUByteArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneUShortArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneUIntArray.contentHashCode(): Int = this.array.contentHashCode()
public fun KoneULongArray.contentHashCode(): Int = this.array.contentHashCode()

public fun KoneArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneMutableArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneBooleanArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableBooleanArray.isEmpty(): Boolean = size == 0u
public fun KoneCharArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableCharArray.isEmpty(): Boolean = size == 0u
public fun KoneByteArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableByteArray.isEmpty(): Boolean = size == 0u
public fun KoneShortArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableShortArray.isEmpty(): Boolean = size == 0u
public fun KoneIntArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableIntArray.isEmpty(): Boolean = size == 0u
public fun KoneLongArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableLongArray.isEmpty(): Boolean = size == 0u
public fun KoneFloatArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableFloatArray.isEmpty(): Boolean = size == 0u
public fun KoneDoubleArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableDoubleArray.isEmpty(): Boolean = size == 0u
public fun KoneUByteArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableUByteArray.isEmpty(): Boolean = size == 0u
public fun KoneUShortArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableUShortArray.isEmpty(): Boolean = size == 0u
public fun KoneUIntArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableUIntArray.isEmpty(): Boolean = size == 0u
public fun KoneULongArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableULongArray.isEmpty(): Boolean = size == 0u

public fun KoneArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneBooleanArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableBooleanArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneCharArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableCharArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneByteArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableByteArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneShortArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableShortArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneLongArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableLongArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneFloatArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableFloatArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneDoubleArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableDoubleArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneUByteArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableUByteArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneUShortArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableUShortArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneUIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableUIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneULongArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableULongArray.isNotEmpty(): Boolean = !isEmpty()
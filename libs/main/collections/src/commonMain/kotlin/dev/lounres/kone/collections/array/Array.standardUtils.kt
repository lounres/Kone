/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array


// TODO: Think about what to add for Kone arrays

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Element> KoneMutableArray<Element>.toKoneArray(): KoneArray<Element> = KoneArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableByteArray.toKoneByteArray(): KoneByteArray = KoneByteArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableShortArray.toKoneShortArray(): KoneShortArray = KoneShortArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableIntArray.toKoneIntArray(): KoneIntArray = KoneIntArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableLongArray.toKoneLongArray(): KoneLongArray = KoneLongArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableFloatArray.toKoneFloatArray(): KoneFloatArray = KoneFloatArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableDoubleArray.toKoneDoubleArray(): KoneDoubleArray = KoneDoubleArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableUByteArray.toKoneUByteArray(): KoneUByteArray = KoneUByteArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableUShortArray.toKoneUShortArray(): KoneUShortArray = KoneUShortArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableUIntArray.toKoneUIntArray(): KoneUIntArray = KoneUIntArray(array.copyOf())
@OptIn(DelicateImmutableArrayConstructor::class)
public fun KoneMutableULongArray.toKoneULongArray(): KoneULongArray = KoneULongArray(array.copyOf())

@DelicateImmutableArrayConstructor
public fun <Element> KoneMutableArray<Element>.asKoneArray(): KoneArray<Element> = KoneArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableByteArray.asKoneByteArray(): KoneByteArray = KoneByteArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableShortArray.asKoneShortArray(): KoneShortArray = KoneShortArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableIntArray.asKoneIntArray(): KoneIntArray = KoneIntArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableLongArray.asKoneLongArray(): KoneLongArray = KoneLongArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableFloatArray.asKoneFloatArray(): KoneFloatArray = KoneFloatArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableDoubleArray.asKoneDoubleArray(): KoneDoubleArray = KoneDoubleArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableUByteArray.asKoneUByteArray(): KoneUByteArray = KoneUByteArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableUShortArray.asKoneUShortArray(): KoneUShortArray = KoneUShortArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableUIntArray.asKoneUIntArray(): KoneUIntArray = KoneUIntArray(array)
@DelicateImmutableArrayConstructor
public fun KoneMutableULongArray.asKoneULongArray(): KoneULongArray = KoneULongArray(array)

// TODO: Wait for actual decision with equals operator
public infix fun KoneByteArray.contentEquals(other: KoneByteArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneShortArray.contentEquals(other: KoneShortArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneIntArray.contentEquals(other: KoneIntArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneLongArray.contentEquals(other: KoneLongArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneFloatArray.contentEquals(other: KoneFloatArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneDoubleArray.contentEquals(other: KoneDoubleArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneUByteArray.contentEquals(other: KoneUByteArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneUShortArray.contentEquals(other: KoneUShortArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneUIntArray.contentEquals(other: KoneUIntArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }
public infix fun KoneULongArray.contentEquals(other: KoneULongArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }

public fun KoneArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneMutableArray<*>.isEmpty(): Boolean = size == 0u
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
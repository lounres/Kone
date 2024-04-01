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
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.common.*
import dev.lounres.kone.collections.common.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.next


public inline fun <reified E> KoneIterable<E>.toKoneMutableArray(): KoneMutableArray<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneMutableArray()
}
public inline fun <reified E> Iterable<E>.toKoneMutableArray(): KoneMutableArray<E> {
    if (this is Collection<E>) return this.toKoneMutableArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
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
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneArray()
}
public inline fun <reified E> Iterable<E>.toKoneArray(): KoneArray<E> {
    if (this is Collection<E>) return this.toKoneArray()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
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

public fun KoneIterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    if (this is KoneIterableCollection<UInt>) return this.toKoneMutableUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneMutableUIntArray()
}
public fun Iterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    if (this is Collection<UInt>) return this.toKoneMutableUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
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
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneUIntArray()
}
public fun Iterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    if (this is Collection<UInt>) return this.toKoneUIntArray()

    val result = KoneGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
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
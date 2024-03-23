/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.implementations.KoneContextualGrowableArrayList
import dev.lounres.kone.collections.next


public inline fun <reified E> KoneIterable<E>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> {
    if (this is KoneContextualIterableCollection<E, *>) return this.toKoneContextualMutableArray()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualMutableArray()
}
public inline fun <reified E> Iterable<E>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> {
    if (this is Collection<E>) return this.toKoneContextualMutableArray()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualMutableArray()
}
public inline fun <reified E> KoneContextualIterableCollection<E, *>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> {
    val iterator = iterator()
    return KoneContextualMutableArray(size) { iterator.next() }
}
public inline fun <reified E> Collection<E>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> {
    val iterator = iterator()
    return KoneContextualMutableArray(size.toUInt()) { iterator.next() }
}
public inline fun <reified E> KoneContextualList<E, *>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> =
    KoneContextualMutableArray(size) { this[it] }
public inline fun <reified E> KoneContextualIterableList<E, *>.toKoneContextualMutableArray(): KoneContextualMutableArray<E> {
    val iterator = iterator()
    return KoneContextualMutableArray(size) { iterator.next() }
}

public inline fun <reified E> KoneIterable<E>.toKoneContextualArray(): KoneContextualArray<E> {
    if (this is KoneContextualIterableCollection<E, *>) return this.toKoneContextualArray()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualArray()
}
public inline fun <reified E> Iterable<E>.toKoneContextualArray(): KoneContextualArray<E> {
    if (this is Collection<E>) return this.toKoneContextualArray()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualArray()
}
public inline fun <reified E> KoneContextualIterableCollection<E, *>.toKoneContextualArray(): KoneContextualArray<E> {
    val iterator = iterator()
    return KoneContextualArray(size) { iterator.next() }
}
public inline fun <reified E> Collection<E>.toKoneContextualArray(): KoneContextualArray<E> {
    val iterator = iterator()
    return KoneContextualArray(size.toUInt()) { iterator.next() }
}
public inline fun <reified E> KoneContextualList<E, *>.toKoneContextualArray(): KoneContextualArray<E> =
    KoneContextualArray(size) { this[it] }
public inline fun <reified E> KoneContextualIterableList<E, *>.toKoneContextualArray(): KoneContextualArray<E> {
    val iterator = iterator()
    return KoneContextualArray(size) { iterator.next() }
}

public fun KoneIterable<UInt>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray {
    if (this is KoneContextualIterableCollection<UInt, *>) return this.toKoneContextualMutableUIntArray()

    val result = KoneContextualGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualMutableUIntArray()
}
public fun Iterable<UInt>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray {
    if (this is Collection<UInt>) return this.toKoneContextualMutableUIntArray()

    val result = KoneContextualGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualMutableUIntArray()
}
public fun KoneContextualIterableCollection<UInt, *>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray {
    val iterator = iterator()
    return KoneContextualMutableUIntArray(size) { iterator.next() }
}
public fun Collection<UInt>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray {
    val iterator = iterator()
    return KoneContextualMutableUIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneContextualList<UInt, *>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray =
    KoneContextualMutableUIntArray(size) { this[it] }
public fun KoneContextualIterableList<UInt, *>.toKoneContextualMutableUIntArray(): KoneContextualMutableUIntArray {
    val iterator = iterator()
    return KoneContextualMutableUIntArray(size) { iterator.next() }
}

public fun KoneIterable<UInt>.toKoneContextualUIntArray(): KoneContextualUIntArray {
    if (this is KoneContextualIterableCollection<UInt, *>) return this.toKoneContextualUIntArray()

    val result = KoneContextualGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualUIntArray()
}
public fun Iterable<UInt>.toKoneContextualUIntArray(): KoneContextualUIntArray {
    if (this is Collection<UInt>) return this.toKoneContextualUIntArray()

    val result = KoneContextualGrowableArrayList<UInt>()
    for (element in this) result.addAtTheEnd(element)
    return result.toKoneContextualUIntArray()
}
public fun KoneContextualIterableCollection<UInt, *>.toKoneContextualUIntArray(): KoneContextualUIntArray {
    val iterator = iterator()
    return KoneContextualUIntArray(size) { iterator.next() }
}
public fun Collection<UInt>.toKoneContextualUIntArray(): KoneContextualUIntArray {
    val iterator = iterator()
    return KoneContextualUIntArray(size.toUInt()) { iterator.next() }
}
public fun KoneContextualList<UInt, *>.toKoneContextualUIntArray(): KoneContextualUIntArray =
    KoneContextualUIntArray(size) { this[it] }
public fun KoneContextualIterableList<UInt, *>.toKoneContextualUIntArray(): KoneContextualUIntArray {
    val iterator = iterator()
    return KoneContextualUIntArray(size) { iterator.next() }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard

import dev.lounres.kone.collections.next


public fun <E> KoneMutableArray<E>.toArray(): KoneArray<E> = KoneArray(array)
public fun KoneMutableUIntArray.toUIntArray(): KoneUIntArray = KoneUIntArray(array)

// TODO: Wait for actual decision with equals operator
public infix fun KoneUIntArray.contentEquals(other: KoneUIntArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }

public fun KoneArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneMutableArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneUIntArray.isEmpty(): Boolean = size == 0u
public fun KoneMutableUIntArray.isEmpty(): Boolean = size == 0u

public fun KoneArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneUIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneMutableUIntArray.isNotEmpty(): Boolean = !isEmpty()

public fun <E> KoneArray<E>.containsAll(elements: KoneIterableCollection<E>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}
public fun <E> KoneMutableArray<E>.containsAll(elements: KoneIterableCollection<E>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}

public fun <E> KoneArray<E>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement == element }
public fun <E> KoneMutableArray<E>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement == element }

public fun <E> KoneArray<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement == element }
public fun <E> KoneMutableArray<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement == element }
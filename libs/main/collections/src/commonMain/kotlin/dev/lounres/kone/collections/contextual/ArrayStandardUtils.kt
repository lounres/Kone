/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality


public fun <E> KoneContextualMutableArray<E>.toKoneContextualArray(): KoneContextualArray<E> = KoneContextualArray(array)
public fun KoneContextualMutableUIntArray.toKoneContextualUIntArray(): KoneContextualUIntArray = KoneContextualUIntArray(array)

// TODO: Wait for actual decision with equals operator
public infix fun KoneContextualUIntArray.contentEquals(other: KoneContextualUIntArray): Boolean =
    this.size == other.size && (0u ..< this.size).all { this[it] == other[it] }

public fun KoneContextualArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneContextualMutableArray<*>.isEmpty(): Boolean = size == 0u
public fun KoneContextualUIntArray.isEmpty(): Boolean = size == 0u
public fun KoneContextualMutableUIntArray.isEmpty(): Boolean = size == 0u

public fun KoneContextualArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneContextualMutableArray<*>.isNotEmpty(): Boolean = !isEmpty()
public fun KoneContextualUIntArray.isNotEmpty(): Boolean = !isEmpty()
public fun KoneContextualMutableUIntArray.isNotEmpty(): Boolean = !isEmpty()

context(Equality<E>)
public fun <E> KoneContextualArray<E>.containsAll(elements: KoneContextualIterableCollection<E, *>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}
context(Equality<E>)
public fun <E> KoneContextualMutableArray<E>.containsAll(elements: KoneContextualIterableCollection<E, *>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}

public fun <E> KoneContextualArray<E>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement == element }
public fun <E> KoneContextualMutableArray<E>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement == element }

public fun <E> KoneContextualArray<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement == element }
public fun <E> KoneContextualMutableArray<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement == element }
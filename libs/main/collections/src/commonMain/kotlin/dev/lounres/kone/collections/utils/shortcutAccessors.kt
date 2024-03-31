/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.isEmpty


public operator fun <E> KoneList<E>.component1(): E = get(0u)
public operator fun <E> KoneList<E>.component2(): E = get(1u)
public operator fun <E> KoneList<E>.component3(): E = get(2u)
public operator fun <E> KoneList<E>.component4(): E = get(3u)
public operator fun <E> KoneList<E>.component5(): E = get(4u)

public val KoneList<*>.indices: UIntRange
    get() = 0u ..< size

public val KoneList<*>.lastIndex: UInt
    get() = size - 1u

public fun <E> KoneIterable<E>.first(): E =
    iterator().let { if (!it.hasNext()) throw NoSuchElementException() else it.getNext() }
public fun <E> KoneList<E>.first(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[0u]
public fun <E> KoneIterableList<E>.first(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[0u]

public fun <E> KoneList<E>.last(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[lastIndex]

public fun <E> KoneIterable<E>.single(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw IllegalArgumentException("Iterable is empty")
    val value = iterator.next()
    if (iterator.hasNext()) throw IllegalArgumentException("Iterable has more than one element")
    return value
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualList
import dev.lounres.kone.collections.contextual.isEmpty


public operator fun <E> KoneContextualList<E, *>.component1(): E = get(0u)
public operator fun <E> KoneContextualList<E, *>.component2(): E = get(1u)
public operator fun <E> KoneContextualList<E, *>.component3(): E = get(2u)
public operator fun <E> KoneContextualList<E, *>.component4(): E = get(3u)
public operator fun <E> KoneContextualList<E, *>.component5(): E = get(4u)

public val KoneContextualList<*, *>.indices: UIntRange
    get() = 0u ..< size

public val KoneContextualList<*, *>.lastIndex: UInt
    get() = size - 1u

public fun <E> KoneIterable<E>.first(): E =
    iterator().let { if (!it.hasNext()) throw NoSuchElementException() else it.getNext() }
public fun <E> KoneContextualList<E, *>.first(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[0u]
public fun <E> KoneContextualIterableList<E, *>.first(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[0u]

public fun <E> KoneContextualList<E, *>.last(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[lastIndex]

public fun <E> KoneIterable<E>.single(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw IllegalArgumentException("Iterable is empty")
    val value = iterator.next()
    if (iterator.hasNext()) throw IllegalArgumentException("Iterable has more than one element")
    return value
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.lastIndex


public operator fun <E> KoneList<E>.component1(): E = get(0u)
public operator fun <E> KoneList<E>.component2(): E = get(1u)
public operator fun <E> KoneList<E>.component3(): E = get(2u)
public operator fun <E> KoneList<E>.component4(): E = get(3u)
public operator fun <E> KoneList<E>.component5(): E = get(4u)
public operator fun <E> KoneList<E>.component6(): E = get(5u)
public operator fun <E> KoneList<E>.component7(): E = get(6u)
public operator fun <E> KoneList<E>.component8(): E = get(7u)
public operator fun <E> KoneList<E>.component9(): E = get(8u)
public operator fun <E> KoneList<E>.component10(): E = get(9u)
public operator fun <E> KoneList<E>.component11(): E = get(10u)
public operator fun <E> KoneList<E>.component12(): E = get(11u)
public operator fun <E> KoneList<E>.component13(): E = get(12u)
public operator fun <E> KoneList<E>.component14(): E = get(13u)
public operator fun <E> KoneList<E>.component15(): E = get(14u)

public fun <E> KoneIterable<E>.first(): E =
    iterator().let { if (!it.hasNext()) throw NoSuchElementException("Iterable is empty.") else it.getNext() }

public fun <E> KoneList<E>.last(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[lastIndex]

public fun <E> KoneIterable<E>.single(): E =
    when {
        size == 0u -> throw IllegalArgumentException("Iterable is empty")
        size == 1u -> iterator().getNext()
        else -> throw IllegalArgumentException("Iterable has more than one element")
    }
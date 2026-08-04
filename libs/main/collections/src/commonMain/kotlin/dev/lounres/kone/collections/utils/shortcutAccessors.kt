/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterable.isEmpty
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.sequence.KoneSequence


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

public fun <E> KoneIterator<E>.first(): E =
    if (!hasNext()) throw NoSuchElementException("Iterable is empty.") else getNext()

public fun <E> KoneIterable<E>.first(): E =
    iterator().let { if (!it.hasNext()) throw NoSuchElementException("Iterable is empty.") else it.getNext() }

public fun <E> KoneSequence<E>.first(): E =
    iterator().let { if (!it.hasNext()) throw NoSuchElementException("Iterable is empty.") else it.getNext() }

public fun <E> KoneList<E>.last(): E =
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    else this[lastIndex]

public fun <E> KoneIterator<E>.single(): E {
    while (hasNext()) moveNext()
    if (!hasNext()) throw IllegalArgumentException("Iterable has no element matching the predicate.")
    val result = getNext()
    moveNext()
    while (hasNext()) moveNext()
    if (hasNext()) throw IllegalArgumentException("Iterable has more than one element matching the predicate.")
    return result
}

public fun <E> KoneIterable<E>.single(): E =
    when {
        size == 0u -> throw IllegalArgumentException("Iterable is empty.")
        size == 1u -> iterator().getNext()
        else -> throw IllegalArgumentException("Iterable has more than one element.")
    }

public fun <E> KoneSequence<E>.single(): E = iterator().single()

public fun <E> KoneIterator<E>.single(predicate: (E) -> Boolean): E {
    while (hasNext() && !predicate(getNext())) moveNext()
    if (!hasNext()) throw IllegalArgumentException("Iterable has no element matching the predicate.")
    val result = getNext()
    moveNext()
    while (hasNext() && !predicate(getNext())) moveNext()
    if (hasNext()) throw IllegalArgumentException("Iterable has more than one element matching the predicate.")
    return result
}

public fun <E> KoneIterable<E>.single(predicate: (E) -> Boolean): E = iterator().single(predicate)

public fun <E> KoneSequence<E>.single(predicate: (E) -> Boolean): E = iterator().single(predicate)
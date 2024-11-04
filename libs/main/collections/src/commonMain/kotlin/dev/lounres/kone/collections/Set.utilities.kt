/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.forEach


public fun <E> KoneSet<E>.isEmpty(): Boolean = size == 0u
public fun <E> KoneSet<E>.isNotEmpty(): Boolean = !isEmpty()

public fun <E> KoneSet<E>.containsAllFrom(elements: Iterable<E>): Boolean = elements.all { it in this }

public fun <E> KoneMutableSet<E>.addAllFrom(elements: KoneIterable<E>) {
    elements.forEach { add(it) }
}
public fun <E> KoneMutableSet<E>.addAllFrom(elements: KoneList<E>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
public fun <E> KoneMutableSet<E>.addAllFrom(elements: KoneSet<E>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
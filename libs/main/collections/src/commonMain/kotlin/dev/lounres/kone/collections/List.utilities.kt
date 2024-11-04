/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.forEach


public fun <E> KoneList<E>.isEmpty(): Boolean = size == 0u
public fun <E> KoneList<E>.isNotEmpty(): Boolean = !isEmpty()

public fun <E> KoneMutableList<E>.addAllFrom(elements: KoneIterable<E>) {
    elements.forEach { add(it) }
}
public fun <E> KoneMutableList<E>.addAllFrom(elements: KoneList<E>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
public fun <E> KoneMutableList<E>.addAllFrom(elements: KoneSet<E>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

public val KoneList<*>.lastIndex: UInt get() = size - 1u
public val KoneList<*>.indices: UIntRange get() = 0u ..< size
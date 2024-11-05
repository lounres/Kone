/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.forEach


public fun <Element> KoneList<Element>.isEmpty(): Boolean = size == 0u
public fun <Element> KoneList<Element>.isNotEmpty(): Boolean = !isEmpty()

public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneIterable<Element>) {
    elements.forEach { add(it) }
}
public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneList<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneSet<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

public val KoneList<*>.lastIndex: UInt get() = size - 1u
public val KoneList<*>.indices: UIntRange get() = 0u ..< size
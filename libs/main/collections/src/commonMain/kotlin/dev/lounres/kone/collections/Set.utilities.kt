/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.forEach


public fun <Element> KoneSet<Element>.isEmpty(): Boolean = size == 0u
public fun <Element> KoneSet<Element>.isNotEmpty(): Boolean = !isEmpty()

public fun <Element> KoneSet<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterable<Element>) {
    elements.forEach { add(it) }
}
public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneList<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneSet<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
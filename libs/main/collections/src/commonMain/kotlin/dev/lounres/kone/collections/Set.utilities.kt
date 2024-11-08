/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.all


public fun <Element> KoneSet<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterable<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}
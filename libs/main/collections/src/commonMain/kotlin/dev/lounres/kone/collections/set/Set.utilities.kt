/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.utils.all


public fun <Element> KoneSet<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterator<Element>) {
    while (elements.hasNext()) add(elements.getAndMoveNext())
}

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterable<Element>) {
    val iterator = elements.iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneSequence<Element>) {
    addAllFrom(elements.iterator())
}

public fun <Element> KoneMutableSet<Element>.removeAllFrom(elements: KoneIterator<Element>) {
    while (elements.hasNext()) remove(elements.getAndMoveNext())
}

public fun <Element> KoneMutableSet<Element>.removeAllFrom(elements: KoneIterable<Element>) {
    removeAllFrom(elements.iterator())
}

public fun <Element> KoneMutableSet<Element>.removeAllFrom(elements: KoneSequence<Element>) {
    removeAllFrom(elements.iterator())
}
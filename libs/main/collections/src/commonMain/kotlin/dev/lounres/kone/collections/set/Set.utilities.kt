/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.sequence.KoneSequence
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.repeat


public fun <Element> KoneSet<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterator<Element>) {
    while (elements.hasNext()) add(elements.getAndMoveNext())
}

public inline fun <Element> KoneMutableSet<Element>.addSeveral(number: UInt, builder: (index: UInt) -> Element) {
    repeat(number) { add(builder(it)) }
}

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneIterable<Element>) {
    val iterator = elements.iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneMutableSet<Element>.addAllFrom(elements: KoneSequence<Element>) {
    addAllFrom(elements.iterator())
}

public inline fun <Element> KoneRemovableSet<Element>.removeAllThat(predicate: (element: Element) -> Boolean) {
    val remover = this.iterator()
    while (remover.hasNext()) {
        if (predicate(remover.getNext())) remover.removeNext()
        else remover.moveNext()
    }
}

public inline fun <Element> KoneRemovableSet<Element>.retainAllThat(predicate: (element: Element) -> Boolean) {
    removeAllThat { element -> !predicate(element) }
}

public fun <Element> KoneRemovableSet<Element>.removeAllFrom(elements: KoneIterator<Element>) {
    while (elements.hasNext()) remove(elements.getAndMoveNext())
}

public fun <Element> KoneRemovableSet<Element>.removeAllFrom(elements: KoneIterable<Element>) {
    removeAllFrom(elements.iterator())
}

public fun <Element> KoneRemovableSet<Element>.removeAllFrom(elements: KoneSequence<Element>) {
    removeAllFrom(elements.iterator())
}
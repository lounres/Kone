/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.DelicateBulkElementsRemoverAPI
import dev.lounres.kone.collections.DelicateSeveralElementsInserterAPI
import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.sequence.KoneSequence
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.neq
import dev.lounres.kone.repeat


/**
 * Returns the element at the provided [index] or `null` if [index] is out of bounds.
 */
public fun <Element> KoneList<Element>.getOrNull(index: UInt): Element? = if (index < size) this[index] else null
/**
 * Returns the element at the provided [index] wrapped in [Some] or [None] if [index] is out of bounds.
 */
public fun <Element> KoneList<Element>.getMaybe(index: UInt): Maybe<Element> = if (index < size) Some(this[index]) else None
/**
 * Returns the element at the provided [index] or the result of [block] if [index] is out of bounds.
 */
public fun <Element> KoneList<Element>.getOrElse(index: UInt, block: () -> Element): Element = if (index < size) this[index] else block()

@OptIn(DelicateSeveralElementsInserterAPI::class)
public inline fun <Element> KoneMutableList<Element>.addSeveral(number: UInt, builder: (index: UInt) -> Element) {
    val inserter = startAddingSeveralAt(index = 0u, number = number)
    try {
        repeat(number) { inserter.insert(builder(it)) }
    } finally {
        inserter.close()
    }
}
@OptIn(DelicateSeveralElementsInserterAPI::class)
public inline fun <Element> KoneMutableList<Element>.addSeveralAt(index: UInt, number: UInt, builder: (index: UInt) -> Element) {
    val inserter = startAddingSeveralAt(index = index, number = number)
    try {
        repeat(number) { inserter.insert(builder(it)) }
    } finally {
        inserter.close()
    }
}
/**
 * Adds provided [elements] at the end of the ordered collection.
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation adds places with indices from `size` to `size + elements.size` exclusive and puts the values in it.
 */
public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneIterator<Element>) {
    while (elements.hasNext()) add(elements.getAndMoveNext())
}

/**
 * Adds provided [elements] at the end of the ordered collection.
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation adds places with indices from `size` to `size + elements.size` exclusive and puts the values in it.
 */
public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneIterable<Element>) {
    val iterator = elements.iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

/**
 * Adds provided [elements] at the end of the ordered collection.
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation adds places with indices from `size` to `size + elements.size` exclusive and puts the values in it.
 */
public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneSequence<Element>) {
    addAllFrom(elements.iterator())
}

/**
 * Adds provided [elements] before element with index [index].
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation:
 * - for each place with index at least [index] increases its index by `elements.size`,
 * - adds places with indices from [index] to `index + elements.size`,
 * - and puts the values in the added places.
 *
 * When [index] is equal to [size][KoneMutableList.size] the element is added at the end.
 *
 * If [index] is greater than [size][KoneMutableList.size], [IndexOutOfBoundsException] is thrown.
 *
 * @throws IndexOutOfBoundsException when index is greater than [size][KoneMutableList.size].
 */
public fun <Element> KoneMutableList<Element>.addAllFromAt(index: UInt, elements: KoneIterator<Element>) {
    var index = index
    while (elements.hasNext()) addAt(index++, elements.getAndMoveNext())
}

/**
 * Adds provided [elements] before element with index [index].
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation:
 * - for each place with index at least [index] increases its index by `elements.size`,
 * - adds places with indices from [index] to `index + elements.size`,
 * - and puts the values in the added places.
 *
 * When [index] is equal to [size][KoneMutableList.size] the element is added at the end.
 *
 * If [index] is greater than [size][KoneMutableList.size], [IndexOutOfBoundsException] is thrown.
 *
 * @throws IndexOutOfBoundsException when index is greater than [size][KoneMutableList.size].
 */
public fun <Element> KoneMutableList<Element>.addAllFromAt(index: UInt, elements: KoneIterable<Element>) {
    val iterator = elements.iterator()
    addSeveralAt(index, elements.size) { iterator.getAndMoveNext() }
}

/**
 * Adds provided [elements] before element with index [index].
 *
 * For each index from `0` to [size][KoneMutableList.size] there is exactly one corresponding place for a value.
 * And this operation:
 * - for each place with index at least [index] increases its index by `elements.size`,
 * - adds places with indices from [index] to `index + elements.size`,
 * - and puts the values in the added places.
 *
 * When [index] is equal to [size][KoneMutableList.size] the element is added at the end.
 *
 * If [index] is greater than [size][KoneMutableList.size], [IndexOutOfBoundsException] is thrown.
 *
 * @throws IndexOutOfBoundsException when index is greater than [size][KoneMutableList.size].
 */
public fun <Element> KoneMutableList<Element>.addAllFromAt(index: UInt, elements: KoneSequence<Element>) {
    addAllFromAt(index, elements.iterator())
}

@OptIn(DelicateBulkElementsRemoverAPI::class)
public inline fun <Element> KoneMutableList<Element>.removeAllThat(predicate: (element: Element) -> Boolean) {
    val remover = this.startBulkyRemoving()
    try {
        while (remover.hasNext()) {
            if (predicate(remover.getNext())) remover.removeNext()
            else remover.moveNext()
        }
    } finally {
        remover.close()
    }
}
@OptIn(DelicateBulkElementsRemoverAPI::class)
public inline fun <Element> KoneMutableList<Element>.removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
    val remover = this.startBulkyRemoving()
    try {
        while (remover.hasNext()) {
            if (predicate(remover.nextIndex(), remover.getNext())) remover.removeNext()
            else remover.moveNext()
        }
    } finally {
        remover.close()
    }
}
/**
 * Finds first element equal to the provided [element] with respect to context [Equality]
 * and removes it.
 *
 * If there is no equal element, the list is not modified.
 * If there are several equal elements, only the first one is found and removed.
 */
context(_: Equality<Element>)
public fun <Element> KoneMutableList<Element>.remove(element: Element) {
    val iterator = iterator()
    while (iterator.hasNext() && iterator.getNext() neq element) iterator.moveNext()
    if (iterator.hasNext()) iterator.removeNext()
}

/**
 * Iterates over the collection and retains only the elements matching the [predicate].
 */
public inline fun <Element> KoneMutableList<Element>.retainAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
    removeAllThatIndexed { index, element -> !predicate(index, element) }
}
/**
 * Iterates over the collection and retains only the elements matching the [predicate].
 */
public inline fun <Element> KoneMutableList<Element>.retainAllThat(predicate: (element: Element) -> Boolean) {
    removeAllThat { element -> !predicate(element) }
}

/**
 * Returns the last index of elements in the list.
 *
 * If there are no elements in the list, [UInt.MAX_VALUE] is returned.
 */
public val KoneList<*>.lastIndex: UInt get() = size - 1u
/**
 * Returns the list's indices range.
 */
public val KoneList<*>.indices: UIntRange get() = 0u ..< size
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.contextualOf
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.suppliedTypes.SuppliedType


public fun <E, R : Comparable<R>> KoneIterable<E>.maxOfOrNull(selector: (E) -> R): R? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

// TODO: Add `(min|max)Maybe` and `(min|max)OrNull`

public fun <E : Comparable<E>> KoneIterable<E>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (minElement > nextElement) {
            minElement = nextElement
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E : Comparable<E>> KoneIterable<E>.minList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(nextElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E : Comparable<E>> KoneIterable<E>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (maxElement < nextElement) {
            maxElement = nextElement
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E : Comparable<E>> KoneIterable<E>.maxList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(nextElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (minElement gt nextElement) {
            minElement = nextElement
        }
    } while (iterator.hasNext())
    return minElement
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.minList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        when (minElement compareWith nextElement) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (maxElement lt nextElement) {
            maxElement = nextElement
        }
    } while (iterator.hasNext())
    return maxElement
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.maxList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        when(maxElement compareWith nextElement) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E> KoneIterable<E>.minWith(comparator: Comparator<E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E> KoneIterable<E>.minListWith(comparator: Comparator<E>): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        when (comparator.compare(minElement, nextElement)) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E> KoneIterable<E>.maxWith(comparator: Comparator<E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E> KoneIterable<E>.maxListWith(comparator: Comparator<E>): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        when (comparator.compare(maxElement, nextElement)) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minValue
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(nextValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextValue)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(nextValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextValue)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minValue
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (minValue compareWith nextValue) {
            ComparisonResult.Equal -> minList.add(nextValue)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextValue)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (maxValue compareWith nextValue) {
            ComparisonResult.Equal -> maxList.add(nextValue)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextValue)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R> KoneIterable<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minValue
}

public fun <E, R> KoneIterable<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(minValue, nextValue)) {
            ComparisonResult.Equal -> minList.add(nextValue)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextValue)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R> KoneIterable<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

public fun <E, R> KoneIterable<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(maxValue, nextValue)) {
            ComparisonResult.Equal -> maxList.add(nextValue)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextValue)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(nextElement)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(nextElement)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minElement
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        
        when (minValue compareWith nextValue) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxElement
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (maxValue compareWith nextValue) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R> KoneIterable<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E, R> KoneIterable<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(minValue, nextValue)) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R> KoneIterable<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E, R> KoneIterable<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return KoneList.empty()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(maxValue, nextValue)) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E> KoneIterable<E>.hasDuplicates(
    elementEquality: Equality<E> = defaultEquality(),
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): Boolean {
    val setOfElements = KoneMutableSet.of(
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}

context(_: KoneContextRegistry)
public fun <E> KoneIterable<E>.hasDuplicatesContextual(elementType: SuppliedType): Boolean {
    val setOfElements = KoneMutableSet.contextualOf<E>(elementType)
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
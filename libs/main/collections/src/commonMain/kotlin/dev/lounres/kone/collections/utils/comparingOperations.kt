/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
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
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.suppliedTypes.SuppliedType


// TODO: Add `(min|max)Maybe` and `(min|max)OrNull`

public fun <E : Comparable<E>> KoneIterator<E>.min(): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    do {
        val nextElement = getAndMoveNext()
        if (minElement > nextElement) {
            minElement = nextElement
        }
    } while (hasNext())
    return minElement
}

public fun <E : Comparable<E>> KoneIterable<E>.min(): E = iterator().min()

public fun <E : Comparable<E>> KoneSequence<E>.min(): E = iterator().min()

public fun <E : Comparable<E>> KoneIterator<E>.minList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(nextElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
        }
    } while (hasNext())
    return minList
}

public fun <E : Comparable<E>> KoneIterable<E>.minList(): KoneList<E> = iterator().minList()

public fun <E : Comparable<E>> KoneSequence<E>.minList(): KoneList<E> = iterator().minList()

public fun <E : Comparable<E>> KoneIterator<E>.max(): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    do {
        val nextElement = getAndMoveNext()
        if (maxElement < nextElement) {
            maxElement = nextElement
        }
    } while (hasNext())
    return maxElement
}

public fun <E : Comparable<E>> KoneIterable<E>.max(): E = iterator().max()

public fun <E : Comparable<E>> KoneSequence<E>.max(): E = iterator().max()

public fun <E : Comparable<E>> KoneIterator<E>.maxList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(nextElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
        }
    } while (hasNext())
    return maxList
}

public fun <E : Comparable<E>> KoneIterable<E>.maxList(): KoneList<E> = iterator().maxList()

public fun <E : Comparable<E>> KoneSequence<E>.maxList(): KoneList<E> = iterator().maxList()

context(_: Order<E>)
public fun <E> KoneIterator<E>.min(): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    do {
        val nextElement = getAndMoveNext()
        if (minElement gt nextElement) {
            minElement = nextElement
        }
    } while (hasNext())
    return minElement
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.min(): E = iterator().min()

context(_: Order<E>)
public fun <E> KoneSequence<E>.min(): E = iterator().min()

context(_: Order<E>)
public fun <E> KoneIterator<E>.minList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()

        when (minElement compareWith nextElement) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.minList(): KoneList<E> = iterator().minList()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minList(): KoneList<E> = iterator().minList()

context(_: Order<E>)
public fun <E> KoneIterator<E>.max(): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    do {
        val nextElement = getAndMoveNext()
        if (maxElement lt nextElement) {
            maxElement = nextElement
        }
    } while (hasNext())
    return maxElement
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.max(): E = iterator().max()

context(_: Order<E>)
public fun <E> KoneSequence<E>.max(): E = iterator().max()

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()

        when(maxElement compareWith nextElement) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList
}

context(_: Order<E>)
public fun <E : Comparable<E>> KoneIterable<E>.maxList(): KoneList<E> = iterator().maxList()

context(_: Order<E>)
public fun <E : Comparable<E>> KoneSequence<E>.maxList(): KoneList<E> = iterator().maxList()

public fun <E> KoneIterator<E>.minWith(comparator: Comparator<E>): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    do {
        val nextElement = getAndMoveNext()
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
        }
    } while (hasNext())
    return minElement
}

public fun <E> KoneIterable<E>.minWith(comparator: Comparator<E>): E = iterator().minWith(comparator)

public fun <E> KoneSequence<E>.minWith(comparator: Comparator<E>): E = iterator().minWith(comparator)

public fun <E> KoneIterator<E>.minListWith(comparator: Comparator<E>): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()

        when (comparator.compare(minElement, nextElement)) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList
}

public fun <E> KoneIterable<E>.minListWith(comparator: Comparator<E>): KoneList<E> = iterator().minListWith(comparator)

public fun <E> KoneSequence<E>.minListWith(comparator: Comparator<E>): KoneList<E> = iterator().minListWith(comparator)

public fun <E> KoneIterator<E>.maxWith(comparator: Comparator<E>): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    do {
        val nextElement = getAndMoveNext()
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
        }
    } while (hasNext())
    return maxElement
}

public fun <E> KoneIterable<E>.maxWith(comparator: Comparator<E>): E = iterator().maxWith(comparator)

public fun <E> KoneSequence<E>.maxWith(comparator: Comparator<E>): E = iterator().maxWith(comparator)

public fun <E> KoneIterator<E>.maxListWith(comparator: Comparator<E>): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()

        when (comparator.compare(maxElement, nextElement)) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList
}

public fun <E> KoneIterable<E>.maxListWith(comparator: Comparator<E>): KoneList<E> = iterator().maxListWith(comparator)

public fun <E> KoneSequence<E>.maxListWith(comparator: Comparator<E>): KoneList<E> = iterator().maxListWith(comparator)

public fun <E, R : Comparable<R>> KoneIterator<E>.minOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.minListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.maxOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.minOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.minListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.maxOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

public fun <E, R> KoneIterator<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public fun <E, R> KoneIterable<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().minWithOf(comparator, selector)

public fun <E, R> KoneSequence<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().minWithOf(comparator, selector)

public fun <E, R> KoneIterator<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(minElement))
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

public fun <E, R> KoneIterable<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().minListWithOf(comparator, selector)

public fun <E, R> KoneSequence<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().minListWithOf(comparator, selector)

public fun <E, R> KoneIterator<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public fun <E, R> KoneIterable<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().maxWithOf(comparator, selector)

public fun <E, R> KoneSequence<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().maxWithOf(comparator, selector)

public fun <E, R> KoneIterator<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxValue)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

public fun <E, R> KoneIterable<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().maxListWithOf(comparator, selector)

public fun <E, R> KoneSequence<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().maxListWithOf(comparator, selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.minBy(selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return minElement
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.minListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.maxBy(selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return maxElement
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

public fun <E, R : Comparable<R>> KoneIterator<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

public fun <E, R : Comparable<R>> KoneSequence<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.minBy(selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return minElement
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.minListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.maxBy(selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return maxElement
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

context(_: Order<R>)
public fun <E, R> KoneIterator<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

context(_: Order<R>)
public fun <E, R> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

context(_: Order<R>)
public fun <E, R> KoneSequence<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

public fun <E, R> KoneIterator<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return minElement
}

public fun <E, R> KoneIterable<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().minWithBy(comparator, selector)

public fun <E, R> KoneSequence<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().minWithBy(comparator, selector)

public fun <E, R> KoneIterator<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    val minList = KoneMutableList.of(minElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return minList
}

public fun <E, R> KoneIterable<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().minListWithBy(comparator, selector)

public fun <E, R> KoneSequence<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().minListWithBy(comparator, selector)

public fun <E, R> KoneIterator<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return maxElement
}

public fun <E, R> KoneIterable<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().maxWithBy(comparator, selector)

public fun <E, R> KoneSequence<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().maxWithBy(comparator, selector)

public fun <E, R> KoneIterator<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    val maxList = KoneMutableList.of(maxElement)
    do {
        val nextElement = getAndMoveNext()
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
    } while (hasNext())
    return maxList
}

public fun <E, R> KoneIterable<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().maxListWithBy(comparator, selector)

public fun <E, R> KoneSequence<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().maxListWithBy(comparator, selector)

public fun <E> KoneIterator<E>.hasDuplicates(
    elementEquality: Equality<E> = Equality.defaultFor(),
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
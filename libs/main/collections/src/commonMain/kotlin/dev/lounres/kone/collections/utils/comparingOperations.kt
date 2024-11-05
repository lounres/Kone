/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.*

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
    if (!iterator.hasNext()) return emptyKoneList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    val minList = koneMutableListOf(minElement)
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
    if (!iterator.hasNext()) return emptyKoneList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    val maxList = koneMutableListOf(maxElement)
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

context(Order<E>)
public fun <E> KoneIterable<E>.min(): E {
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

context(Order<E>)
public fun <E> KoneIterable<E>.minList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    val minList = koneMutableListOf(minElement)
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

context(Order<E>)
public fun <E> KoneIterable<E>.max(): E {
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

context(Order<E>)
public fun <E> KoneIterable<E>.maxList(): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    val maxList = koneMutableListOf(maxElement)
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
    if (!iterator.hasNext()) return emptyKoneList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    val minList = koneMutableListOf(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(minElement, nextElement)
        when {
            comparisonResult == ComparisonResult.Equal -> minList.add(nextElement)
            comparisonResult == ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(nextElement)
            }
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
    if (!iterator.hasNext()) return emptyKoneList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    val maxList = koneMutableListOf(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(maxElement, nextElement)
        when {
            comparisonResult == ComparisonResult.Equal -> maxList.add(nextElement)
            comparisonResult == ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(nextElement)
            }
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
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(minElement))
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minValue)
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
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxValue)
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.minOf(selector: (E) -> R): R {
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(minElement))
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minValue)
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.maxOf(selector: (E) -> R): R {
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxValue)
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
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(minElement))
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == ComparisonResult.Equal -> minList.add(nextValue)
            comparisonResult == ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextValue)
            }
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
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(selector(maxElement))
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxValue)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == ComparisonResult.Equal -> maxList.add(nextValue)
            comparisonResult == ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextValue)
            }
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
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minElement)
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
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxElement)
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.minBy(selector: (E) -> R): E {
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minElement)
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.maxBy(selector: (E) -> R): E {
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

context(Order<R>)
public fun <E, R> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxElement)
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
    if (!iterator.hasNext()) return emptyKoneList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(minElement)
    var minValue = selector(minElement)
    val minList = koneMutableListOf(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == ComparisonResult.Equal -> minList.add(nextElement)
            comparisonResult == ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(nextElement)
            }
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
    if (!iterator.hasNext()) return emptyKoneList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneListOf(maxElement)
    var maxValue = selector(maxElement)
    val maxList = koneMutableListOf(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == ComparisonResult.Equal -> maxList.add(nextElement)
            comparisonResult == ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(nextElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E> KoneIterable<E>.hasDuplicates(elementContext: Equality<E> = defaultEquality()): Boolean {
    val setOfElements = koneMutableSetOf(elementContext = elementContext)
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
/*
 * Copyright � 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 *
 * THE FILE IS AUTOMATICALLY GENERATED. DON'T CHANGE IT MANUALLY.
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality

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

public fun <E : Comparable<E>> KoneList<E>.min(): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (minElement > nextElement) {
            minElement = nextElement
        }
    } while (index < size)
    return minElement
}

public fun <E : Comparable<E>> KoneIterableList<E>.min(): E {
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

public fun <E : Comparable<E>> KoneIterable<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E : Comparable<E>> KoneList<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

public fun <E : Comparable<E>> KoneIterableList<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
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

public fun <E : Comparable<E>> KoneList<E>.max(): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (maxElement < nextElement) {
            maxElement = nextElement
        }
    } while (index < size)
    return maxElement
}

public fun <E : Comparable<E>> KoneIterableList<E>.max(): E {
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

public fun <E : Comparable<E>> KoneIterable<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E : Comparable<E>> KoneList<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E : Comparable<E>> KoneIterableList<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
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
public fun <E> KoneList<E>.min(): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (minElement > nextElement) {
            minElement = nextElement
        }
    } while (index < size)
    return minElement
}

context(Order<E>)
public fun <E> KoneIterableList<E>.min(): E {
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
public fun <E> KoneIterable<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

context(Order<E>)
public fun <E> KoneList<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

context(Order<E>)
public fun <E> KoneIterableList<E>.minList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
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
public fun <E> KoneList<E>.max(): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (maxElement < nextElement) {
            maxElement = nextElement
        }
    } while (index < size)
    return maxElement
}

context(Order<E>)
public fun <E> KoneIterableList<E>.max(): E {
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
public fun <E> KoneIterable<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(Order<E>)
public fun <E> KoneList<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

context(Order<E>)
public fun <E> KoneIterableList<E>.maxList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
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
        if (comparator.compare(minElement, nextElement) > 0) {
            minElement = nextElement
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E> KoneList<E>.minWith(comparator: Comparator<E>): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (comparator.compare(minElement, nextElement) > 0) {
            minElement = nextElement
        }
    } while (index < size)
    return minElement
}

public fun <E> KoneIterableList<E>.minWith(comparator: Comparator<E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (comparator.compare(minElement, nextElement) > 0) {
            minElement = nextElement
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E> KoneIterable<E>.minListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(minElement, nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E> KoneList<E>.minListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = comparator.compare(minElement, nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

public fun <E> KoneIterableList<E>.minListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(minElement, nextElement)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.removeAll()
                minList.add(minElement)
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
        if (comparator.compare(maxElement, nextElement) < 0) {
            maxElement = nextElement
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E> KoneList<E>.maxWith(comparator: Comparator<E>): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var index = 1u
    do {
        val nextElement = get(index++)
        if (comparator.compare(maxElement, nextElement) < 0) {
            maxElement = nextElement
        }
    } while (index < size)
    return maxElement
}

public fun <E> KoneIterableList<E>.maxWith(comparator: Comparator<E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    do {
        val nextElement = iterator.getAndMoveNext()
        if (comparator.compare(maxElement, nextElement) < 0) {
            maxElement = nextElement
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E> KoneIterable<E>.maxListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(maxElement, nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E> KoneList<E>.maxListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)

        val comparisonResult = comparator.compare(maxElement, nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E> KoneIterableList<E>.maxListWith(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<E>): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()

        val comparisonResult = comparator.compare(maxElement, nextElement)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.removeAll()
                maxList.add(maxElement)
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

public fun <E, R : Comparable<R>> KoneList<E>.minOf(selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val minElement = get(0u)
    if (size == 1u) return selector(minElement)
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (index < size)
    return minValue
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.minOf(selector: (E) -> R): R {
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

public fun <E, R : Comparable<R>> KoneIterable<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneList<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val minElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (index < size)
    return minList
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
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

public fun <E, R : Comparable<R>> KoneList<E>.maxOf(selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val maxElement = get(0u)
    if (size == 1u) return selector(maxElement)
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (index < size)
    return maxValue
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.maxOf(selector: (E) -> R): R {
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

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneList<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
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
public fun <E, R> KoneList<E>.minOf(selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val minElement = get(0u)
    if (size == 1u) return selector(minElement)
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (index < size)
    return minValue
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.minOf(selector: (E) -> R): R {
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
public fun <E, R> KoneIterable<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (iterator.hasNext())
    return minList
}

context(Order<R>)
public fun <E, R> KoneList<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val minElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (index < size)
    return minList
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.minListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
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
public fun <E, R> KoneList<E>.maxOf(selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val maxElement = get(0u)
    if (size == 1u) return selector(maxElement)
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (index < size)
    return maxValue
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.maxOf(selector: (E) -> R): R {
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
public fun <E, R> KoneIterable<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(Order<R>)
public fun <E, R> KoneList<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (index < size)
    return maxList
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.maxListOf(elementContext: Equality<R> = defaultEquality(), selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
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
        if (comparator.compare(minValue, nextValue) > 0) {
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minValue
}

public fun <E, R> KoneList<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val minElement = get(0u)
    if (size == 1u) return selector(minElement)
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) > 0) {
            minValue = nextValue
        }
    } while (index < size)
    return minValue
}

public fun <E, R> KoneIterableList<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) > 0) {
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minValue
}

public fun <E, R> KoneIterable<E>.minListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R> KoneList<E>.minListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val minElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
            }
        }
    } while (index < size)
    return minList
}

public fun <E, R> KoneIterableList<E>.minListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(minElement), elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.removeAll()
                minList.add(minValue)
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
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

public fun <E, R> KoneList<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (size == 0u) throw NoSuchElementException()
    val maxElement = get(0u)
    if (size == 1u) return selector(maxElement)
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxValue = nextValue
        }
    } while (index < size)
    return maxValue
}

public fun <E, R> KoneIterableList<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return selector(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxValue
}

public fun <E, R> KoneIterable<E>.maxListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R> KoneList<E>.maxListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    if (size == 0u) return emptyKoneIterableList()
    val maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E, R> KoneIterableList<E>.maxListWithOf(elementContext: Equality<R> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(selector(maxElement), elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxValue, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxValue)
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

public fun <E, R : Comparable<R>> KoneList<E>.minBy(selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (index < size)
    return minElement
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.minBy(selector: (E) -> R): E {
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

public fun <E, R : Comparable<R>> KoneIterable<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R : Comparable<R>> KoneList<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
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

public fun <E, R : Comparable<R>> KoneList<E>.maxBy(selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (index < size)
    return maxElement
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.maxBy(selector: (E) -> R): E {
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

public fun <E, R : Comparable<R>> KoneIterable<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R : Comparable<R>> KoneList<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E, R : Comparable<R>> KoneIterableList<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
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
public fun <E, R> KoneList<E>.minBy(selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (index < size)
    return minElement
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.minBy(selector: (E) -> R): E {
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
public fun <E, R> KoneIterable<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

context(Order<R>)
public fun <E, R> KoneList<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.minListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
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
public fun <E, R> KoneList<E>.maxBy(selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (index < size)
    return maxElement
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.maxBy(selector: (E) -> R): E {
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
public fun <E, R> KoneIterable<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

context(Order<R>)
public fun <E, R> KoneList<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

context(Order<R>)
public fun <E, R> KoneIterableList<E>.maxListBy(elementContext: Equality<E> = defaultEquality(), selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
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
        if (comparator.compare(minValue, nextValue) > 0) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E, R> KoneList<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var minElement = get(0u)
    if (size == 1u) return minElement
    var minValue = selector(minElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) > 0) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (index < size)
    return minElement
}

public fun <E, R> KoneIterableList<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return minElement
    var minValue = selector(minElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) > 0) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (iterator.hasNext())
    return minElement
}

public fun <E, R> KoneIterable<E>.minListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public fun <E, R> KoneList<E>.minListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var minElement = get(0u)
    if (size == 1u) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
            }
        }
    } while (index < size)
    return minList
}

public fun <E, R> KoneIterableList<E>.minListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var minElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(minElement, elementContext = elementContext)
    var minValue = selector(minElement)
    val minList = koneMutableIterableListOf(minElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(minValue, nextValue)
        when {
            comparisonResult == 0 -> minList.add(minElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minValue = nextValue
                minList.removeAll()
                minList.add(minElement)
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
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E, R> KoneList<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    if (size == 0u) throw NoSuchElementException()
    var maxElement = get(0u)
    if (size == 1u) return maxElement
    var maxValue = selector(maxElement)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (index < size)
    return maxElement
}

public fun <E, R> KoneIterableList<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return maxElement
    var maxValue = selector(maxElement)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) < 0) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (iterator.hasNext())
    return maxElement
}

public fun <E, R> KoneIterable<E>.maxListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E, R> KoneList<E>.maxListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    if (size == 0u) return emptyKoneIterableList()
    var maxElement = get(0u)
    if (size == 1u) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    var index = 1u
    do {
        val nextElement = get(index++)
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (index < size)
    return maxList
}

public fun <E, R> KoneIterableList<E>.maxListWithBy(elementContext: Equality<E> = defaultEquality(), comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    var maxElement = iterator.getAndMoveNext()
    if (!iterator.hasNext()) return koneIterableListOf(maxElement, elementContext = elementContext)
    var maxValue = selector(maxElement)
    val maxList = koneMutableIterableListOf(maxElement, elementContext = elementContext)
    do {
        val nextElement = iterator.getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = comparator.compare(maxValue, nextValue)
        when {
            comparisonResult == 0 -> maxList.add(maxElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxValue = nextValue
                maxList.removeAll()
                maxList.add(maxElement)
            }
        }
    } while (iterator.hasNext())
    return maxList
}

public fun <E> KoneIterable<E>.hasDuplicates(elementContext: Equality<E> = defaultEquality()): Boolean {
    val setOfElements = koneMutableIterableSetOf(elementContext = elementContext)
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}

public fun <E> KoneList<E>.hasDuplicates(elementContext: Equality<E> = defaultEquality()): Boolean {
    val setOfElements = koneMutableIterableSetOf(elementContext = elementContext)
    for (index in indices) {
        val element = this[index]
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}

public fun <E> KoneIterableList<E>.hasDuplicates(elementContext: Equality<E> = defaultEquality()): Boolean {
    val setOfElements = koneMutableIterableSetOf(elementContext = elementContext)
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
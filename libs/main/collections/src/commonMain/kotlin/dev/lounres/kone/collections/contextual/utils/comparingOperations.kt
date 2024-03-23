/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualList
import dev.lounres.kone.collections.contextual.isEmpty
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order


// TODO: Add mins and maxs for `...OrNull` and `...Maybe` suffixes
// TODO: Add mins and maxs lists collectors (it provides all elements giving the min/max). See `minListWithBy` for example

public fun <E: Comparable<E>> KoneIterable<E>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (min > e) min = e
    }
    return min
}
public fun <E: Comparable<E>> KoneContextualList<E, *>.min(): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (min > e) min = e
    }
    return min
}
public fun <E: Comparable<E>> KoneContextualIterableList<E, *>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (min > e) min = e
    }
    return min
}

public fun <E: Comparable<E>> KoneIterable<E>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (max < e) max = e
    }
    return max
}
public fun <E: Comparable<E>> KoneContextualList<E, *>.max(): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (max < e) max = e
    }
    return max
}
public fun <E: Comparable<E>> KoneContextualIterableList<E, *>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (max < e) max = e
    }
    return max
}

context(Order<E>)
public fun <E> KoneIterable<E>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (min > e) min = e
    }
    return min
}
context(Order<E>)
public fun <E> KoneContextualList<E, *>.min(): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (min > e) min = e
    }
    return min
}
context(Order<E>)
public fun <E> KoneContextualIterableList<E, *>.min(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (min > e) min = e
    }
    return min
}

context(Order<E>)
public fun <E> KoneIterable<E>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (max < e) max = e
    }
    return max
}
context(Order<E>)
public fun <E> KoneContextualList<E, *>.max(): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (max < e) max = e
    }
    return max
}
context(Order<E>)
public fun <E> KoneContextualIterableList<E, *>.max(): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (max < e) max = e
    }
    return max
}

public fun <E> KoneIterable<E>.minWith(comparator: Comparator<in E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public fun <E> KoneContextualList<E, *>.minWith(comparator: Comparator<in E>): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public fun <E> KoneContextualIterableList<E, *>.minWith(comparator: Comparator<in E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}

public fun <E> KoneIterable<E>.maxWith(comparator: Comparator<in E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public fun <E> KoneContextualList<E, *>.maxWith(comparator: Comparator<in E>): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public fun <E> KoneContextualIterableList<E, *>.maxWith(comparator: Comparator<in E>): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = iterator.getAndMoveNext()
    while (iterator.hasNext()) {
        val e = iterator.getAndMoveNext()
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}

public inline fun <E, R: Comparable<R>> KoneIterable<E>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (min > e) min = e
    }
    return min
}
public inline fun <E, R: Comparable<R>> KoneContextualList<E, *>.minOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (min > e) min = e
    }
    return min
}
public inline fun <E, R: Comparable<R>> KoneContextualIterableList<E, *>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (min > e) min = e
    }
    return min
}

public inline fun <E, R: Comparable<R>> KoneIterable<E>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (max < e) max = e
    }
    return max
}
public inline fun <E, R: Comparable<R>> KoneContextualList<E, *>.maxOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (max < e) max = e
    }
    return max
}
public inline fun <E, R: Comparable<R>> KoneContextualIterableList<E, *>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (max < e) max = e
    }
    return max
}

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (min > e) min = e
    }
    return min
}
context(Order<R>)
public inline fun <E, R> KoneContextualList<E, *>.minOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (min > e) min = e
    }
    return min
}
context(Order<R>)
public inline fun <E, R> KoneContextualIterableList<E, *>.minOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (min > e) min = e
    }
    return min
}

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (max < e) max = e
    }
    return max
}
context(Order<R>)
public inline fun <E, R> KoneContextualList<E, *>.maxOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (max < e) max = e
    }
    return max
}
context(Order<R>)
public inline fun <E, R> KoneContextualIterableList<E, *>.maxOf(selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (max < e) max = e
    }
    return max
}

public inline fun <E, R> KoneIterable<E>.minWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public inline fun <E, R> KoneContextualList<E, *>.minWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public inline fun <E, R> KoneContextualIterableList<E, *>.minWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}

public inline fun <E, R> KoneIterable<E>.maxWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public inline fun <E, R> KoneContextualList<E, *>.maxWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public inline fun <E, R> KoneContextualIterableList<E, *>.maxWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var max = selector(iterator.getAndMoveNext())
    while (iterator.hasNext()) {
        val e = selector(iterator.getAndMoveNext())
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}

public inline fun <E, R: Comparable<R>> KoneIterable<E>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}
public inline fun <E, R: Comparable<R>> KoneContextualList<E, *>.minBy(selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var minElem = get(0u)
    if (size == 1u) return minElem
    var minValue = selector(minElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (index < size)
    return minElem
}
public inline fun <E, R: Comparable<R>> KoneContextualIterableList<E, *>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}

public inline fun <E, R: Comparable<R>> KoneIterable<E>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}
public inline fun <E, R: Comparable<R>> KoneContextualList<E, *>.maxBy(selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var maxElem = get(0u)
    if (size == 1u) return maxElem
    var maxValue = selector(maxElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (index < size)
    return maxElem
}
public inline fun <E, R: Comparable<R>> KoneContextualIterableList<E, *>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}
context(Order<R>)
public inline fun <E, R> KoneContextualList<E, *>.minBy(selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var minElem = get(0u)
    if (size == 1u) return minElem
    var minValue = selector(minElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (index < size)
    return minElem
}
context(Order<R>)
public inline fun <E, R> KoneContextualIterableList<E, *>.minBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneContextualIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}
context(Order<R>)
public inline fun <E, R> KoneContextualList<E, *>.minListBy(selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    if (isEmpty()) return emptyKoneContextualIterableList()
    val firstElem = get(0u)
    if (size == 1u) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (index < size)
    return minList
}
context(Order<R>)
public inline fun <E, R> KoneContextualIterableList<E, *>.minListBy(selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneContextualIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}
context(Order<R>)
public inline fun <E, R> KoneContextualList<E, *>.maxBy(selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var maxElem = get(0u)
    if (size == 1u) return maxElem
    var maxValue = selector(maxElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (index < size)
    return maxElem
}
context(Order<R>)
public inline fun <E, R> KoneContextualIterableList<E, *>.maxBy(selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}

public inline fun <E, R> KoneIterable<E>.minWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparator.compare(minValue, v) > 0) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}
public inline fun <E, R> KoneContextualList<E, *>.minWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var minElem = get(0u)
    if (size == 1u) return minElem
    var minValue = selector(minElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (comparator.compare(minValue, v) > 0) {
            minElem = e
            minValue = v
        }
    } while (index < size)
    return minElem
}
public inline fun <E, R> KoneContextualIterableList<E, *>.minWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparator.compare(minValue, v) > 0) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}

public inline fun <E, R> KoneIterable<E>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneContextualIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}
public inline fun <E, R> KoneContextualList<E, *>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    if (isEmpty()) return emptyKoneContextualIterableList()
    val firstElem = get(0u)
    if (size == 1u) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (index < size)
    return minList
}
public inline fun <E, R> KoneContextualIterableList<E, *>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneContextualIterableList<E, Equality<E>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneContextualIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneContextualIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneContextualMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.addAtTheEnd(e)
            }
            comparisonResult == 0 -> {
                minList.addAtTheEnd(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}

public inline fun <E, R> KoneIterable<E>.maxWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparator.compare(maxValue, v) < 0) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}
public inline fun <E, R> KoneContextualList<E, *>.maxWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    if (isEmpty()) throw NoSuchElementException()
    var maxElem = get(0u)
    if (size == 1u) return maxElem
    var maxValue = selector(maxElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        if (comparator.compare(maxValue, v) < 0) {
            maxElem = e
            maxValue = v
        }
    } while (index < size)
    return maxElem
}
public inline fun <E, R> KoneContextualIterableList<E, *>.maxWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparator.compare(maxValue, v) < 0) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}

context(Equality<E>)
public fun <E> KoneIterable<E>.hasDuplicates(): Boolean {
    val setOfElements = koneContextualMutableIterableSetOf<E>()
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
context(Equality<E>)
public fun <E> KoneContextualList<E, *>.hasDuplicates(): Boolean {
    val setOfElements = koneContextualMutableIterableSetOf<E>()
    setOf<Int>()
    for (index in indices) {
        val element = this[index]
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
context(Equality<E>)
public fun <E> KoneContextualIterableList<E, *>.hasDuplicates(): Boolean {
    val setOfElements = koneContextualMutableIterableSetOf<E>()
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
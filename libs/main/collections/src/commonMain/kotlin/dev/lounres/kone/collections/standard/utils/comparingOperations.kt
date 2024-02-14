/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.standard.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.standard.*
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
public fun <E: Comparable<E>> KoneList<E>.min(): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (min > e) min = e
    }
    return min
}
public fun <E: Comparable<E>> KoneIterableList<E>.min(): E {
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
public fun <E: Comparable<E>> KoneList<E>.max(): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (max < e) max = e
    }
    return max
}
public fun <E: Comparable<E>> KoneIterableList<E>.max(): E {
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
public fun <E> KoneList<E>.min(): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (min > e) min = e
    }
    return min
}
context(Order<E>)
public fun <E> KoneIterableList<E>.min(): E {
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
public fun <E> KoneList<E>.max(): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (max < e) max = e
    }
    return max
}
context(Order<E>)
public fun <E> KoneIterableList<E>.max(): E {
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
public fun <E> KoneList<E>.minWith(comparator: Comparator<in E>): E {
    if (isEmpty()) throw NoSuchElementException()
    var min = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public fun <E> KoneIterableList<E>.minWith(comparator: Comparator<in E>): E {
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
public fun <E> KoneList<E>.maxWith(comparator: Comparator<in E>): E {
    if (isEmpty()) throw NoSuchElementException()
    var max = get(0u)
    for (index in 1u ..< size) {
        val e = get(index)
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public fun <E> KoneIterableList<E>.maxWith(comparator: Comparator<in E>): E {
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
public inline fun <E, R: Comparable<R>> KoneList<E>.minOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (min > e) min = e
    }
    return min
}
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.minOf(selector: (E) -> R): R {
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
public inline fun <E, R: Comparable<R>> KoneList<E>.maxOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (max < e) max = e
    }
    return max
}
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.maxOf(selector: (E) -> R): R {
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
public inline fun <E, R> KoneList<E>.minOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (min > e) min = e
    }
    return min
}
context(Order<R>)
public inline fun <E, R> KoneIterableList<E>.minOf(selector: (E) -> R): R {
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
public inline fun <E, R> KoneList<E>.maxOf(selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (max < e) max = e
    }
    return max
}
context(Order<R>)
public inline fun <E, R> KoneIterableList<E>.maxOf(selector: (E) -> R): R {
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
public inline fun <E, R> KoneList<E>.minWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var min = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (comparator.compare(min, e) > 0) min = e
    }
    return min
}
public inline fun <E, R> KoneIterableList<E>.minWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
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
public inline fun <E, R> KoneList<E>.maxWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
    if (isEmpty()) throw NoSuchElementException()
    var max = selector(get(0u))
    for (index in 1u ..< size) {
        val e = selector(get(index))
        if (comparator.compare(max, e) < 0) max = e
    }
    return max
}
public inline fun <E, R> KoneIterableList<E>.maxWithOf(comparator: Comparator<in R>, selector: (E) -> R): R {
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
public inline fun <E, R: Comparable<R>> KoneList<E>.minBy(selector: (E) -> R): E {
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
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.minBy(selector: (E) -> R): E {
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
public inline fun <E, R: Comparable<R>> KoneList<E>.maxBy(selector: (E) -> R): E {
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
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.maxBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneList<E>.minBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterableList<E>.minBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}
context(Order<R>)
public inline fun <E, R> KoneList<E>.minListBy(selector: (E) -> R): KoneIterableList<E> {
    if (isEmpty()) return emptyKoneIterableList()
    val firstElem = get(0u)
    if (size == 1u) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
            }
        }
    } while (index < size)
    return minList
}
context(Order<R>)
public inline fun <E, R> KoneIterableList<E>.minListBy(selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = minValue.compareTo(v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
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
public inline fun <E, R> KoneList<E>.maxBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterableList<E>.maxBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneList<E>.minWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterableList<E>.minWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
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

public inline fun <E, R> KoneIterable<E>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
            }
        }
    } while (iterator.hasNext())
    return minList
}
public inline fun <E, R> KoneList<E>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneIterableList<E> {
    if (isEmpty()) return emptyKoneIterableList()
    val firstElem = get(0u)
    if (size == 1u) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    var index = 1u
    do {
        val e = get(index++)
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
            }
        }
    } while (index < size)
    return minList
}
public inline fun <E, R> KoneIterableList<E>.minListWithBy(comparator: Comparator<in R>, selector: (E) -> R): KoneIterableList<E> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyKoneIterableList()
    val firstElem = iterator.next()
    if (!iterator.hasNext()) return koneIterableListOf(firstElem)
    var minValue = selector(firstElem)
    val minList = koneMutableIterableListOf(firstElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        val comparisonResult = comparator.compare(minValue, v)
        when {
            comparisonResult > 0 -> {
                minValue = v
                minList.clear()
                minList.add(e)
            }
            comparisonResult == 0 -> {
                minList.add(e)
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
public inline fun <E, R> KoneList<E>.maxWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterableList<E>.maxWithBy(comparator: Comparator<in R>, selector: (E) -> R): E {
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

public fun <E> KoneIterable<E>.hasDuplicates(): Boolean {
    val setOfElements = koneMutableIterableSetOf<E>()
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
public fun <E> KoneList<E>.hasDuplicates(): Boolean {
    val setOfElements = koneMutableIterableSetOf<E>()
    setOf<Int>()
    for (index in indices) {
        val element = this[index]
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
public fun <E> KoneIterableList<E>.hasDuplicates(): Boolean {
    val setOfElements = koneMutableIterableSetOf<E>()
    for (element in this) {
        if (element in setOfElements) return true
        setOfElements.add(element)
    }
    return false
}
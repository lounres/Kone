/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.empty
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.array.toKoneUIntArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.contextualOf
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
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


public inline fun <E : Comparable<E>> KoneIterator<E>.minOrElse(default: () -> E): E {
    if (!hasNext()) return default()
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

public inline fun <E : Comparable<E>> KoneIterable<E>.minOrElse(default: () -> E): E = iterator().minOrElse(default)

public inline fun <E : Comparable<E>> KoneSequence<E>.minOrElse(default: () -> E): E = iterator().minOrElse(default)

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

public fun <E : Comparable<E>> KoneIterator<E>.minOrNull(): E? {
    if (!hasNext()) return null
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

public fun <E : Comparable<E>> KoneIterable<E>.minOrNull(): E? = iterator().minOrNull()

public fun <E : Comparable<E>> KoneSequence<E>.minOrNull(): E? = iterator().minOrNull()

public fun <E : Comparable<E>> KoneIterator<E>.minMaybe(): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    do {
        val nextElement = getAndMoveNext()
        if (minElement > nextElement) {
            minElement = nextElement
        }
    } while (hasNext())
    return Some(minElement)
}

public fun <E : Comparable<E>> KoneIterable<E>.minMaybe(): Maybe<E> = iterator().minMaybe()

public fun <E : Comparable<E>> KoneSequence<E>.minMaybe(): Maybe<E> = iterator().minMaybe()

public fun <E : Comparable<E>> KoneIterator<E>.minList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()

        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minList.add(nextElement)
            comparisonResult > 0 -> {
                minElement = nextElement
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public fun <E : Comparable<E>> KoneIterable<E>.minList(): KoneList<E> = iterator().minList()

public fun <E : Comparable<E>> KoneSequence<E>.minList(): KoneList<E> = iterator().minList()

public inline fun <E : Comparable<E>> KoneIterator<E>.minIndexOrElse(default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement > nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E : Comparable<E>> KoneList<E>.minIndexOrElse(default: () -> UInt): UInt = iterator().minIndexOrElse(default)

public inline fun <E : Comparable<E>> KoneSequence<E>.minIndexOrElse(default: () -> UInt): UInt = iterator().minIndexOrElse(default)

public fun <E : Comparable<E>> KoneIterator<E>.minIndex(): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement > nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public fun <E : Comparable<E>> KoneList<E>.minIndex(): UInt = iterator().minIndex()

public fun <E : Comparable<E>> KoneSequence<E>.minIndex(): UInt = iterator().minIndex()

public fun <E : Comparable<E>> KoneIterator<E>.minIndexOrNull(): UInt? {
    if (!hasNext()) return null
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement > nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public fun <E : Comparable<E>> KoneList<E>.minIndexOrNull(): UInt? = iterator().minIndexOrNull()

public fun <E : Comparable<E>> KoneSequence<E>.minIndexOrNull(): UInt? = iterator().minIndexOrNull()

public fun <E : Comparable<E>> KoneIterator<E>.minIndexList(): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minIndexList = KoneArrayGrowableList<UInt>()
    minIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        val comparisonResult = minElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> minIndexList.add(currentIndex)
            comparisonResult > 0 -> {
                minElement = nextElement
                minIndexList.dispose()
                minIndexList = KoneArrayGrowableList()
                minIndexList.add(currentIndex)
            }
        }
    } while (hasNext())
    return minIndexList.toKoneUIntArray()
}

public fun <E : Comparable<E>> KoneList<E>.minIndexList(): KoneUIntArray = iterator().minIndexList()

public fun <E : Comparable<E>> KoneSequence<E>.minIndexList(): KoneUIntArray = iterator().minIndexList()

public inline fun <E : Comparable<E>> KoneIterator<E>.maxOrElse(default: () -> E): E {
    if (!hasNext()) return default()
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

public inline fun <E : Comparable<E>> KoneIterable<E>.maxOrElse(default: () -> E): E = iterator().maxOrElse(default)

public inline fun <E : Comparable<E>> KoneSequence<E>.maxOrElse(default: () -> E): E = iterator().maxOrElse(default)

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

public fun <E : Comparable<E>> KoneIterator<E>.maxOrNull(): E? {
    if (!hasNext()) return null
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

public fun <E : Comparable<E>> KoneIterable<E>.maxOrNull(): E? = iterator().maxOrNull()

public fun <E : Comparable<E>> KoneSequence<E>.maxOrNull(): E? = iterator().maxOrNull()

public fun <E : Comparable<E>> KoneIterator<E>.maxMaybe(): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    do {
        val nextElement = getAndMoveNext()
        if (maxElement < nextElement) {
            maxElement = nextElement
        }
    } while (hasNext())
    return Some(maxElement)
}

public fun <E : Comparable<E>> KoneIterable<E>.maxMaybe(): Maybe<E> = iterator().maxMaybe()

public fun <E : Comparable<E>> KoneSequence<E>.maxMaybe(): Maybe<E> = iterator().maxMaybe()

public fun <E : Comparable<E>> KoneIterator<E>.maxList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()

        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxList.add(nextElement)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public fun <E : Comparable<E>> KoneIterable<E>.maxList(): KoneList<E> = iterator().maxList()

public fun <E : Comparable<E>> KoneSequence<E>.maxList(): KoneList<E> = iterator().maxList()

public inline fun <E : Comparable<E>> KoneIterator<E>.maxIndexOrElse(default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement < nextElement) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E : Comparable<E>> KoneList<E>.maxIndexOrElse(default: () -> UInt): UInt = iterator().maxIndexOrElse(default)

public inline fun <E : Comparable<E>> KoneSequence<E>.maxIndexOrElse(default: () -> UInt): UInt = iterator().maxIndexOrElse(default)

public fun <E : Comparable<E>> KoneIterator<E>.maxIndex(): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement < nextElement) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public fun <E : Comparable<E>> KoneList<E>.maxIndex(): UInt = iterator().maxIndex()

public fun <E : Comparable<E>> KoneSequence<E>.maxIndex(): UInt = iterator().maxIndex()

public fun <E : Comparable<E>> KoneIterator<E>.maxIndexOrNull(): UInt? {
    if (!hasNext()) return null
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement < nextElement) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public fun <E : Comparable<E>> KoneList<E>.maxIndexOrNull(): UInt? = iterator().maxIndexOrNull()

public fun <E : Comparable<E>> KoneSequence<E>.maxIndexOrNull(): UInt? = iterator().maxIndexOrNull()

public fun <E : Comparable<E>> KoneIterator<E>.maxIndexList(): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        val comparisonResult = maxElement.compareTo(nextElement)
        when {
            comparisonResult == 0 -> maxIndexList.add(currentIndex)
            comparisonResult < 0 -> {
                maxElement = nextElement
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

public fun <E : Comparable<E>> KoneList<E>.maxIndexList(): KoneUIntArray = iterator().maxIndexList()

public fun <E : Comparable<E>> KoneSequence<E>.maxIndexList(): KoneUIntArray = iterator().maxIndexList()

context(_: Order<E>)
public inline fun <E> KoneIterator<E>.minOrElse(default: () -> E): E {
    if (!hasNext()) return default()
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
public inline fun <E> KoneIterable<E>.minOrElse(default: () -> E): E = iterator().minOrElse(default)

context(_: Order<E>)
public inline fun <E> KoneSequence<E>.minOrElse(default: () -> E): E = iterator().minOrElse(default)

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
public fun <E> KoneIterator<E>.minOrNull(): E? {
    if (!hasNext()) return null
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
public fun <E> KoneIterable<E>.minOrNull(): E? = iterator().minOrNull()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minOrNull(): E? = iterator().minOrNull()

context(_: Order<E>)
public fun <E> KoneIterator<E>.minMaybe(): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    do {
        val nextElement = getAndMoveNext()
        if (minElement gt nextElement) {
            minElement = nextElement
        }
    } while (hasNext())
    return Some(minElement)
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.minMaybe(): Maybe<E> = iterator().minMaybe()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minMaybe(): Maybe<E> = iterator().minMaybe()

context(_: Order<E>)
public fun <E> KoneIterator<E>.minList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()

        when (minElement compareWith nextElement) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.minList(): KoneList<E> = iterator().minList()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minList(): KoneList<E> = iterator().minList()

context(_: Order<E>)
public inline fun <E> KoneIterator<E>.minIndexOrElse(default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement gt nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<E>)
public inline fun <E> KoneList<E>.minIndexOrElse(default: () -> UInt): UInt = iterator().minIndexOrElse(default)

context(_: Order<E>)
public inline fun <E> KoneSequence<E>.minIndexOrElse(default: () -> UInt): UInt = iterator().minIndexOrElse(default)

context(_: Order<E>)
public fun <E> KoneIterator<E>.minIndex(): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement gt nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<E>)
public fun <E> KoneList<E>.minIndex(): UInt = iterator().minIndex()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minIndex(): UInt = iterator().minIndex()

context(_: Order<E>)
public fun <E> KoneIterator<E>.minIndexOrNull(): UInt? {
    if (!hasNext()) return null
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (minElement gt nextElement) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<E>)
public fun <E> KoneList<E>.minIndexOrNull(): UInt? = iterator().minIndexOrNull()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minIndexOrNull(): UInt? = iterator().minIndexOrNull()

context(_: Order<E>)
public fun <E> KoneIterator<E>.minIndexList(): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minIndexList = KoneArrayGrowableList<UInt>()
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        when (minElement compareWith nextElement) {
            ComparisonResult.Equal -> minIndexList.add(currentIndex)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minIndexList.dispose()
                minIndexList = KoneArrayGrowableList()
                minIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minIndexList.toKoneUIntArray()
}

context(_: Order<E>)
public fun <E> KoneList<E>.minIndexList(): KoneUIntArray = iterator().minIndexList()

context(_: Order<E>)
public fun <E> KoneSequence<E>.minIndexList(): KoneUIntArray = iterator().minIndexList()

context(_: Order<E>)
public inline fun <E> KoneIterator<E>.maxOrElse(default: () -> E): E {
    if (!hasNext()) return default()
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
public inline fun <E> KoneIterable<E>.maxOrElse(default: () -> E): E = iterator().maxOrElse(default)

context(_: Order<E>)
public inline fun <E> KoneSequence<E>.maxOrElse(default: () -> E): E = iterator().maxOrElse(default)

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
public fun <E> KoneIterator<E>.maxOrNull(): E? {
    if (!hasNext()) return null
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
public fun <E> KoneIterable<E>.maxOrNull(): E? = iterator().maxOrNull()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxOrNull(): E? = iterator().maxOrNull()

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxMaybe(): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    do {
        val nextElement = getAndMoveNext()
        if (maxElement lt nextElement) {
            maxElement = nextElement
        }
    } while (hasNext())
    return Some(maxElement)
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.maxMaybe(): Maybe<E> = iterator().maxMaybe()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxMaybe(): Maybe<E> = iterator().maxMaybe()

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxList(): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()

        when(maxElement compareWith nextElement) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

context(_: Order<E>)
public fun <E> KoneIterable<E>.maxList(): KoneList<E> = iterator().maxList()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxList(): KoneList<E> = iterator().maxList()

context(_: Order<E>)
public inline fun <E> KoneIterator<E>.maxIndexOrElse(default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement lt nextElement) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<E>)
public inline fun <E> KoneList<E>.maxIndexOrElse(default: () -> UInt): UInt = iterator().maxIndexOrElse(default)

context(_: Order<E>)
public inline fun <E> KoneSequence<E>.maxIndexOrElse(default: () -> UInt): UInt = iterator().maxIndexOrElse(default)

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxIndex(): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement lt nextElement) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<E>)
public fun <E> KoneList<E>.maxIndex(): UInt = iterator().maxIndex()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxIndex(): UInt = iterator().maxIndex()

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxIndexOrNull(): UInt? {
    if (!hasNext()) return null
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (maxElement lt nextElement) {
            maxElement = nextElement
            maxIndex++
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<E>)
public fun <E> KoneList<E>.maxIndexOrNull(): UInt? = iterator().maxIndexOrNull()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxIndexOrNull(): UInt? = iterator().maxIndexOrNull()

context(_: Order<E>)
public fun <E> KoneIterator<E>.maxIndexList(): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        when(maxElement compareWith nextElement) {
            ComparisonResult.Equal -> maxIndexList.add(currentIndex)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

context(_: Order<E>)
public fun <E> KoneList<E>.maxIndexList(): KoneUIntArray = iterator().maxIndexList()

context(_: Order<E>)
public fun <E> KoneSequence<E>.maxIndexList(): KoneUIntArray = iterator().maxIndexList()

public inline fun <E> KoneIterator<E>.minWithOrElse(comparator: Comparator<E>, default: () -> E): E {
    if (!hasNext()) return default()
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

public inline fun <E> KoneIterable<E>.minWithOrElse(comparator: Comparator<E>, default: () -> E): E = iterator().minWithOrElse(comparator, default)

public inline fun <E> KoneSequence<E>.minWithOrElse(comparator: Comparator<E>, default: () -> E): E = iterator().minWithOrElse(comparator, default)

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

public fun <E> KoneIterator<E>.minWithOrNull(comparator: Comparator<E>): E? {
    if (!hasNext()) return null
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

public fun <E> KoneIterable<E>.minWithOrNull(comparator: Comparator<E>): E? = iterator().minWithOrNull(comparator)

public fun <E> KoneSequence<E>.minWithOrNull(comparator: Comparator<E>): E? = iterator().minWithOrNull(comparator)

public fun <E> KoneIterator<E>.minWithMaybe(comparator: Comparator<E>): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    do {
        val nextElement = getAndMoveNext()
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
        }
    } while (hasNext())
    return Some(minElement)
}

public fun <E> KoneIterable<E>.minWithMaybe(comparator: Comparator<E>): Maybe<E> = iterator().minWithMaybe(comparator)

public fun <E> KoneSequence<E>.minWithMaybe(comparator: Comparator<E>): Maybe<E> = iterator().minWithMaybe(comparator)

public fun <E> KoneIterator<E>.minListWith(comparator: Comparator<E>): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()

        when (comparator.compare(minElement, nextElement)) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public fun <E> KoneIterable<E>.minListWith(comparator: Comparator<E>): KoneList<E> = iterator().minListWith(comparator)

public fun <E> KoneSequence<E>.minListWith(comparator: Comparator<E>): KoneList<E> = iterator().minListWith(comparator)

public inline fun <E> KoneIterator<E>.minIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E> KoneList<E>.minIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt = iterator().minIndexWithOrElse(comparator, default)

public inline fun <E> KoneSequence<E>.minIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt = iterator().minIndexWithOrElse(comparator, default)

public fun <E> KoneIterator<E>.minIndexWith(comparator: Comparator<E>): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public fun <E> KoneList<E>.minIndexWith(comparator: Comparator<E>): UInt = iterator().minIndexWith(comparator)

public fun <E> KoneSequence<E>.minIndexWith(comparator: Comparator<E>): UInt = iterator().minIndexWith(comparator)

public fun <E> KoneIterator<E>.minIndexWithOrNull(comparator: Comparator<E>): UInt? {
    if (!hasNext()) return null
    var minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(minElement, nextElement) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public fun <E> KoneList<E>.minIndexWithOrNull(comparator: Comparator<E>): UInt? = iterator().minIndexWithOrNull(comparator)

public fun <E> KoneSequence<E>.minIndexWithOrNull(comparator: Comparator<E>): UInt? = iterator().minIndexWithOrNull(comparator)

public fun <E> KoneIterator<E>.minIndexListWith(comparator: Comparator<E>): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minIndexList = KoneArrayGrowableList<UInt>()
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        when (comparator.compare(minElement, nextElement)) {
            ComparisonResult.Equal -> minIndexList.add(currentIndex)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minElement = nextElement
                minIndexList.dispose()
                minIndexList = KoneArrayGrowableList()
                minIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minIndexList.toKoneUIntArray()
}

public fun <E> KoneList<E>.minIndexListWith(comparator: Comparator<E>): KoneUIntArray = iterator().minIndexListWith(comparator)

public fun <E> KoneSequence<E>.minIndexListWith(comparator: Comparator<E>): KoneUIntArray = iterator().minIndexListWith(comparator)

public inline fun <E> KoneIterator<E>.maxWithOrElse(comparator: Comparator<E>, default: () -> E): E {
    if (!hasNext()) return default()
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

public inline fun <E> KoneIterable<E>.maxWithOrElse(comparator: Comparator<E>, default: () -> E): E = iterator().maxWithOrElse(comparator, default)

public inline fun <E> KoneSequence<E>.maxWithOrElse(comparator: Comparator<E>, default: () -> E): E = iterator().maxWithOrElse(comparator, default)

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

public fun <E> KoneIterator<E>.maxWithOrNull(comparator: Comparator<E>): E? {
    if (!hasNext()) return null
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

public fun <E> KoneIterable<E>.maxWithOrNull(comparator: Comparator<E>): E? = iterator().maxWithOrNull(comparator)

public fun <E> KoneSequence<E>.maxWithOrNull(comparator: Comparator<E>): E? = iterator().maxWithOrNull(comparator)

public fun <E> KoneIterator<E>.maxWithMaybe(comparator: Comparator<E>): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    do {
        val nextElement = getAndMoveNext()
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
        }
    } while (hasNext())
    return Some(maxElement)
}

public fun <E> KoneIterable<E>.maxWithMaybe(comparator: Comparator<E>): Maybe<E> = iterator().maxWithMaybe(comparator)

public fun <E> KoneSequence<E>.maxWithMaybe(comparator: Comparator<E>): Maybe<E> = iterator().maxWithMaybe(comparator)

public fun <E> KoneIterator<E>.maxListWith(comparator: Comparator<E>): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()

        when (comparator.compare(maxElement, nextElement)) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public fun <E> KoneIterable<E>.maxListWith(comparator: Comparator<E>): KoneList<E> = iterator().maxListWith(comparator)

public fun <E> KoneSequence<E>.maxListWith(comparator: Comparator<E>): KoneList<E> = iterator().maxListWith(comparator)

public inline fun <E> KoneIterator<E>.maxIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt {
    if (!hasNext()) return default()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E> KoneList<E>.maxIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt = iterator().maxIndexWithOrElse(comparator, default)

public inline fun <E> KoneSequence<E>.maxIndexWithOrElse(comparator: Comparator<E>, default: () -> UInt): UInt = iterator().maxIndexWithOrElse(comparator, default)

public fun <E> KoneIterator<E>.maxIndexWith(comparator: Comparator<E>): UInt {
    if (!hasNext()) throw NoSuchElementException()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public fun <E> KoneList<E>.maxIndexWith(comparator: Comparator<E>): UInt = iterator().maxIndexWith(comparator)

public fun <E> KoneSequence<E>.maxIndexWith(comparator: Comparator<E>): UInt = iterator().maxIndexWith(comparator)

public fun <E> KoneIterator<E>.maxIndexWithOrNull(comparator: Comparator<E>): UInt? {
    if (!hasNext()) return null
    var maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        if (comparator.compare(maxElement, nextElement) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public fun <E> KoneList<E>.maxIndexWithOrNull(comparator: Comparator<E>): UInt? = iterator().maxIndexWithOrNull(comparator)

public fun <E> KoneSequence<E>.maxIndexWithOrNull(comparator: Comparator<E>): UInt? = iterator().maxIndexWithOrNull(comparator)

public fun <E> KoneIterator<E>.maxIndexListWith(comparator: Comparator<E>): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    var maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        currentIndex++
        
        when (comparator.compare(maxElement, nextElement)) {
            ComparisonResult.Equal -> maxIndexList.add(currentIndex)
            ComparisonResult.LeftIsLessThanRight -> {
                maxElement = nextElement
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

public fun <E> KoneList<E>.maxIndexListWith(comparator: Comparator<E>): KoneUIntArray = iterator().maxIndexListWith(comparator)

public fun <E> KoneSequence<E>.maxIndexListWith(comparator: Comparator<E>): KoneUIntArray = iterator().maxIndexListWith(comparator)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().minOfOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().minOfOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minOfOrNull(selector: (E) -> R): R? {
    if (!hasNext()) return null
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minOfOrNull(selector: (E) -> R): R? = iterator().minOfOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minOfOrNull(selector: (E) -> R): R? = iterator().minOfOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minOfMaybe(selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minValue)
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minOfMaybe(selector: (E) -> R): Maybe<R> = iterator().minOfMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minOfMaybe(selector: (E) -> R): Maybe<R> = iterator().minOfMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(minValue)
    var minList = KoneArrayGrowableList<R>()
    minList.add(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(nextValue)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextValue)
            }
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().maxOfOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().maxOfOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxOfOrNull(selector: (E) -> R): R? {
    if (!hasNext()) return null
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxOfOrNull(selector: (E) -> R): R? = iterator().maxOfOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxOfOrNull(selector: (E) -> R): R? = iterator().maxOfOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxValue)
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> = iterator().maxOfMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> = iterator().maxOfMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(maxValue)
    var maxList = KoneArrayGrowableList<R>()
    maxList.add(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(nextValue)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextValue)
            }
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
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
public inline fun <E, R> KoneIterable<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().minOfOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().minOfOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
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
public inline fun <E, R> KoneIterable<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minOf(selector: (E) -> R): R = iterator().minOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minOfOrNull(selector: (E) -> R): R? {
    if (!hasNext()) return null
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
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
public inline fun <E, R> KoneIterable<E>.minOfOrNull(selector: (E) -> R): R? = iterator().minOfOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minOfOrNull(selector: (E) -> R): R? = iterator().minOfOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minOfMaybe(selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minValue)
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.minOfMaybe(selector: (E) -> R): Maybe<R> = iterator().minOfMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minOfMaybe(selector: (E) -> R): Maybe<R> = iterator().minOfMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(minValue)
    var minList = KoneArrayGrowableList<R>()
    minList.add(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (minValue compareWith nextValue) {
            ComparisonResult.Equal -> minList.add(nextValue)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextValue)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minListOf(selector: (E) -> R): KoneList<R> = iterator().minListOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
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
public inline fun <E, R> KoneIterable<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().maxOfOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxOfOrElse(default: () -> R, selector: (E) -> R): R = iterator().maxOfOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxOf(selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
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
public inline fun <E, R> KoneIterable<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxOf(selector: (E) -> R): R = iterator().maxOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxOfOrNull(selector: (E) -> R): R? {
    if (!hasNext()) return null
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
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
public inline fun <E, R> KoneIterable<E>.maxOfOrNull(selector: (E) -> R): R? = iterator().maxOfOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxOfOrNull(selector: (E) -> R): R? = iterator().maxOfOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxValue)
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> = iterator().maxOfMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxOfMaybe(selector: (E) -> R): Maybe<R> = iterator().maxOfMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxListOf(selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(maxValue)
    var maxList = KoneArrayGrowableList<R>()
    maxList.add(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (maxValue compareWith nextValue) {
            ComparisonResult.Equal -> maxList.add(nextValue)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextValue)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxListOf(selector: (E) -> R): KoneList<R> = iterator().maxListOf(selector)

public inline fun <E, R> KoneIterator<E>.minWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R> KoneIterable<E>.minWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R = iterator().minWithOfOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.minWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R = iterator().minWithOfOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R> KoneIterable<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().minWithOf(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().minWithOf(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? {
    if (!hasNext()) return null
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return minValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (hasNext())
    return minValue
}

public inline fun <E, R> KoneIterable<E>.minWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? = iterator().minWithOfOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? = iterator().minWithOfOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minValue)
}

public inline fun <E, R> KoneIterable<E>.minWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> = iterator().minWithOfMaybe(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> = iterator().minWithOfMaybe(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var minValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(minValue)
    var minList = KoneArrayGrowableList<R>()
    minList.add(minValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(minValue, nextValue)) {
            ComparisonResult.Equal -> minList.add(nextValue)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextValue)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public inline fun <E, R> KoneIterable<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().minListWithOf(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().minListWithOf(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R {
    if (!hasNext()) return default()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R> KoneIterable<E>.maxWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R = iterator().maxWithOfOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.maxWithOfOrElse(comparator: Comparator<R>, default: () -> R, selector: (E) -> R): R = iterator().maxWithOfOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R {
    if (!hasNext()) throw NoSuchElementException()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R> KoneIterable<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().maxWithOf(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithOf(comparator: Comparator<R>, selector: (E) -> R): R = iterator().maxWithOf(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? {
    if (!hasNext()) return null
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return maxValue
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (hasNext())
    return maxValue
}

public inline fun <E, R> KoneIterable<E>.maxWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? = iterator().maxWithOfOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithOfOrNull(comparator: Comparator<R>, selector: (E) -> R): R? = iterator().maxWithOfOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> {
    if (!hasNext()) return None
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return Some(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxValue)
}

public inline fun <E, R> KoneIterable<E>.maxWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> = iterator().maxWithOfMaybe(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithOfMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<R> = iterator().maxWithOfMaybe(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> {
    if (!hasNext()) return KoneList.empty()
    var maxValue = selector(getAndMoveNext())
    if (!hasNext()) return KoneList.of(maxValue)
    var maxList = KoneArrayGrowableList<R>()
    maxList.add(maxValue)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(maxValue, nextValue)) {
            ComparisonResult.Equal -> maxList.add(nextValue)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextValue)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public inline fun <E, R> KoneIterable<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().maxListWithOf(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxListWithOf(comparator: Comparator<R>, selector: (E) -> R): KoneList<R> = iterator().maxListWithOf(comparator, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minByOrElse(default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minByOrElse(default: () -> E, selector: (E) -> R): E = iterator().minByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minByOrElse(default: () -> E, selector: (E) -> R): E = iterator().minByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minBy(selector: (E) -> R): E {
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minByOrNull(selector: (E) -> R): E? {
    if (!hasNext()) return null
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minByOrNull(selector: (E) -> R): E? = iterator().minByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minByOrNull(selector: (E) -> R): E? = iterator().minByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minByMaybe(selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue > nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minElement)
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minByMaybe(selector: (E) -> R): Maybe<E> = iterator().minByMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minByMaybe(selector: (E) -> R): Maybe<E> = iterator().minByMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minList.add(nextElement)
            comparisonResult > 0 -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (minValue > nextValue) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minIndexBy(selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (minValue > nextValue) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.minIndexBy(selector: (E) -> R): UInt = iterator().minIndexBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minIndexBy(selector: (E) -> R): UInt = iterator().minIndexBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minIndexByOrNull(selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        if (minValue > nextValue) {
            minValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.minIndexByOrNull(selector: (E) -> R): UInt? = iterator().minIndexByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minIndexyOrNull(selector: (E) -> R): UInt? = iterator().minIndexByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minValue = selector(minElement)
    var minIndexList = KoneArrayGrowableList<UInt>()
    minIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        
        val comparisonResult = minValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> minIndexList.add(currentIndex)
            comparisonResult > 0 -> {
                minValue = nextValue
                minIndexList.dispose()
                minIndexList = KoneArrayGrowableList()
                minIndexList.add(currentIndex)
            }
        }
    } while (hasNext())
    return minIndexList.toKoneUIntArray()
}

public inline fun <E, R : Comparable<R>> KoneList<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().minIndexListBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().minIndexListBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E = iterator().maxByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E = iterator().maxByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxBy(selector: (E) -> R): E {
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxByOrNull(selector: (E) -> R): E? {
    if (!hasNext()) return null
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

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxByOrNull(selector: (E) -> R): E? = iterator().maxByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxByOrNull(selector: (E) -> R): E? = iterator().maxByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxByMaybe(selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue < nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxElement)
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxByMaybe(selector: (E) -> R): Maybe<E> = iterator().maxByMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxByMaybe(selector: (E) -> R): Maybe<E> = iterator().maxByMaybe(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxList.add(nextElement)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public inline fun <E, R : Comparable<R>> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (maxValue < nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexByOrElse(default, selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxIndexBy(selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (maxValue < nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.maxIndexBy(selector: (E) -> R): UInt = iterator().maxIndexBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxIndexBy(selector: (E) -> R): UInt = iterator().maxIndexBy(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxIndexByOrNull(selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (maxValue < nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R : Comparable<R>> KoneList<E>.maxIndexByOrNull(selector: (E) -> R): UInt? = iterator().maxIndexByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxIndexByOrNull(selector: (E) -> R): UInt? = iterator().maxIndexByOrNull(selector)

public inline fun <E, R : Comparable<R>> KoneIterator<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxValue = selector(maxElement)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        
        val comparisonResult = maxValue.compareTo(nextValue)
        when {
            comparisonResult == 0 -> maxIndexList.add(currentIndex)
            comparisonResult < 0 -> {
                maxValue = nextValue
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

public inline fun <E, R : Comparable<R>> KoneList<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().maxIndexListBy(selector)

public inline fun <E, R : Comparable<R>> KoneSequence<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().maxIndexListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minByOrElse(default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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
public inline fun <E, R> KoneIterable<E>.minByOrElse(default: () -> E, selector: (E) -> R): E = iterator().minByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minByOrElse(default: () -> E, selector: (E) -> R): E = iterator().minByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterable<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minBy(selector: (E) -> R): E = iterator().minBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minByOrNull(selector: (E) -> R): E? {
    if (!hasNext()) return null
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
public inline fun <E, R> KoneIterable<E>.minByOrNull(selector: (E) -> R): E? = iterator().minByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minByOrNull(selector: (E) -> R): E? = iterator().minByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minByMaybe(selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (minValue gt nextValue) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minElement)
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.minByMaybe(selector: (E) -> R): Maybe<E> = iterator().minByMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minByMaybe(selector: (E) -> R): Maybe<E> = iterator().minByMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        
        when (minValue compareWith nextValue) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minListBy(selector: (E) -> R): KoneList<E> = iterator().minListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        if (minValue gt nextValue) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minIndexBy(selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (minValue gt nextValue) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.minIndexBy(selector: (E) -> R): UInt = iterator().minIndexBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minIndexBy(selector: (E) -> R): UInt = iterator().minIndexBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minIndexByOrNull(selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (minValue gt nextValue) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.minIndexByOrNull(selector: (E) -> R): UInt? = iterator().minIndexByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minIndexByOrNull(selector: (E) -> R): UInt? = iterator().minIndexByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minValue = selector(minElement)
    var minIndexList = KoneArrayGrowableList<UInt>()
    minIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        
        when (minValue compareWith nextValue) {
            ComparisonResult.Equal -> minIndexList.add(currentIndex)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minIndexList.dispose()
                minIndexList = KoneArrayGrowableList()
                minIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minIndexList.toKoneUIntArray()
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().minIndexListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.minIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().minIndexListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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
public inline fun <E, R> KoneIterable<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E = iterator().maxByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxByOrElse(default: () -> E, selector: (E) -> R): E = iterator().maxByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxBy(selector: (E) -> R): E {
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
public inline fun <E, R> KoneIterable<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxBy(selector: (E) -> R): E = iterator().maxBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxByOrNull(selector: (E) -> R): E? {
    if (!hasNext()) return null
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
public inline fun <E, R> KoneIterable<E>.maxByOrNull(selector: (E) -> R): E? = iterator().maxByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxByOrNull(selector: (E) -> R): E? = iterator().maxByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxByMaybe(selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (maxValue lt nextValue) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxElement)
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.maxByMaybe(selector: (E) -> R): Maybe<E> = iterator().maxByMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxByMaybe(selector: (E) -> R): Maybe<E> = iterator().maxByMaybe(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxListBy(selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (maxValue compareWith nextValue) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxListBy(selector: (E) -> R): KoneList<E> = iterator().maxListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (maxValue lt nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxIndexByOrElse(default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexByOrElse(default, selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxIndexBy(selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (maxValue lt nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.maxIndexBy(selector: (E) -> R): UInt = iterator().maxIndexBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxIndexBy(selector: (E) -> R): UInt = iterator().maxIndexBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxIndexByOrNull(selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        if (maxValue lt nextValue) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.maxIndexByOrNull(selector: (E) -> R): UInt? = iterator().maxIndexByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxIndexByOrNull(selector: (E) -> R): UInt? = iterator().maxIndexByOrNull(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterator<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxValue = selector(maxElement)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        
        when (maxValue compareWith nextValue) {
            ComparisonResult.Equal -> maxIndexList.add(currentIndex)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

context(_: Order<R>)
public inline fun <E, R> KoneList<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().maxIndexListBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneSequence<E>.maxIndexListBy(selector: (E) -> R): KoneUIntArray = iterator().maxIndexListBy(selector)

public inline fun <E, R> KoneIterator<E>.minWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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

public inline fun <E, R> KoneIterable<E>.minWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E = iterator().minWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.minWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E = iterator().minWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
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

public inline fun <E, R> KoneIterable<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().minWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().minWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? {
    if (!hasNext()) return null
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

public inline fun <E, R> KoneIterable<E>.minWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? = iterator().minWithByOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? = iterator().minWithByOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var minElement = getAndMoveNext()
    if (!hasNext()) return Some(minElement)
    var minValue = selector(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minElement = nextElement
            minValue = nextValue
        }
    } while (hasNext())
    return Some(minElement)
}

public inline fun <E, R> KoneIterable<E>.minWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> = iterator().minWithByMaybe(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> = iterator().minWithByMaybe(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(minElement)
    var minValue = selector(minElement)
    var minList = KoneArrayGrowableList<E>()
    minList.add(minElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(minValue, nextValue)) {
            ComparisonResult.Equal -> minList.add(nextElement)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(nextElement)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toOptimizedList()
}

public inline fun <E, R> KoneIterable<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().minListWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().minListWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E, R> KoneList<E>.minIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.minIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt = iterator().minIndexWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.minIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E, R> KoneList<E>.minIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt = iterator().minIndexWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt = iterator().minIndexWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val minElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var minValue = selector(minElement)
    var minIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(minValue, nextValue) == ComparisonResult.LeftIsGreaterThanRight) {
            minValue = nextValue
            minIndex = currentIndex
        }
    } while (hasNext())
    return minIndex
}

public inline fun <E, R> KoneList<E>.minIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? = iterator().minIndexWithByOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? = iterator().minIndexWithByOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.minIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val minElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var minValue = selector(minElement)
    var minList = KoneArrayGrowableList<UInt>()
    minList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        
        when (comparator.compare(minValue, nextValue)) {
            ComparisonResult.Equal -> minList.add(currentIndex)
            ComparisonResult.LeftIsGreaterThanRight -> {
                minValue = nextValue
                minList.dispose()
                minList = KoneArrayGrowableList()
                minList.add(currentIndex)
            }
            ComparisonResult.LeftIsLessThanRight -> {}
        }
    } while (hasNext())
    return minList.toKoneUIntArray()
}

public inline fun <E, R> KoneList<E>.minIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray = iterator().minIndexListWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.minIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray = iterator().minIndexListWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E {
    if (!hasNext()) return default()
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

public inline fun <E, R> KoneIterable<E>.maxWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E = iterator().maxWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.maxWithByOrElse(comparator: Comparator<R>, default: () -> E, selector: (E) -> R): E = iterator().maxWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E {
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

public inline fun <E, R> KoneIterable<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().maxWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithBy(comparator: Comparator<R>, selector: (E) -> R): E = iterator().maxWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? {
    if (!hasNext()) return null
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

public inline fun <E, R> KoneIterable<E>.maxWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? = iterator().maxWithByOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): E? = iterator().maxWithByOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> {
    if (!hasNext()) return None
    var maxElement = getAndMoveNext()
    if (!hasNext()) return Some(maxElement)
    var maxValue = selector(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxElement = nextElement
            maxValue = nextValue
        }
    } while (hasNext())
    return Some(maxElement)
}

public inline fun <E, R> KoneIterable<E>.maxWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> = iterator().maxWithByMaybe(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxWithByMaybe(comparator: Comparator<R>, selector: (E) -> R): Maybe<E> = iterator().maxWithByMaybe(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> {
    if (!hasNext()) return KoneList.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneList.of(maxElement)
    var maxValue = selector(maxElement)
    var maxList = KoneArrayGrowableList<E>()
    maxList.add(maxElement)
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)

        when (comparator.compare(maxValue, nextValue)) {
            ComparisonResult.Equal -> maxList.add(nextElement)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxList.dispose()
                maxList = KoneArrayGrowableList()
                maxList.add(nextElement)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxList.toOptimizedList()
}

public inline fun <E, R> KoneIterable<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().maxListWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = iterator().maxListWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt {
    if (!hasNext()) return default()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R> KoneList<E>.maxIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneSequence<E>.maxIndexWithByOrElse(comparator: Comparator<R>, default: () -> UInt, selector: (E) -> R): UInt = iterator().maxIndexWithByOrElse(comparator, default, selector)

public inline fun <E, R> KoneIterator<E>.maxIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt {
    if (!hasNext()) throw NoSuchElementException()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R> KoneList<E>.maxIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt = iterator().maxIndexWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxIndexWithBy(comparator: Comparator<R>, selector: (E) -> R): UInt = iterator().maxIndexWithBy(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? {
    if (!hasNext()) return null
    val maxElement = getAndMoveNext()
    if (!hasNext()) return 0u
    var maxValue = selector(maxElement)
    var maxIndex = 0u
    var currentIndex = 0u
    do {
        val nextValue = selector(getAndMoveNext())
        currentIndex++
        if (comparator.compare(maxValue, nextValue) == ComparisonResult.LeftIsLessThanRight) {
            maxValue = nextValue
            maxIndex = currentIndex
        }
    } while (hasNext())
    return maxIndex
}

public inline fun <E, R> KoneList<E>.maxIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? = iterator().maxIndexWithByOrNull(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxIndexWithByOrNull(comparator: Comparator<R>, selector: (E) -> R): UInt? = iterator().maxIndexWithByOrNull(comparator, selector)

public inline fun <E, R> KoneIterator<E>.maxIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray {
    if (!hasNext()) return KoneUIntArray.empty()
    val maxElement = getAndMoveNext()
    if (!hasNext()) return KoneUIntArray.of(0u)
    var maxValue = selector(maxElement)
    var maxIndexList = KoneArrayGrowableList<UInt>()
    maxIndexList.add(0u)
    var currentIndex = 0u
    do {
        val nextElement = getAndMoveNext()
        val nextValue = selector(nextElement)
        currentIndex++
        
        when (comparator.compare(maxValue, nextValue)) {
            ComparisonResult.Equal -> maxIndexList.add(currentIndex)
            ComparisonResult.LeftIsLessThanRight -> {
                maxValue = nextValue
                maxIndexList.dispose()
                maxIndexList = KoneArrayGrowableList()
                maxIndexList.add(currentIndex)
            }
            ComparisonResult.LeftIsGreaterThanRight -> {}
        }
    } while (hasNext())
    return maxIndexList.toKoneUIntArray()
}

public inline fun <E, R> KoneList<E>.maxIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray = iterator().maxIndexListWithBy(comparator, selector)

public inline fun <E, R> KoneSequence<E>.maxIndexListWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneUIntArray = iterator().maxIndexListWithBy(comparator, selector)

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
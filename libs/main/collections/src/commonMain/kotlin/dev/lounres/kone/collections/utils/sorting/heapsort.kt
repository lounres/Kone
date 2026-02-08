/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils.sorting

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.toKoneSettableList
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt


// TODO: Add outer variants: variants that create and use separate list for sorting and just assign result to target

@PublishedApi
internal fun <E> KoneSettableList<E>.swap(i: UInt, j: UInt) {
    this[i] = this[j].also { this[j] = this[i] }
}

public fun <E: Comparable<E>> KoneSettableList<E>.heapsort() {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    this[firstChildIndex] > this[index] && this[firstChildIndex] > this[secondChildIndex] -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    this[secondChildIndex] > this[index] -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && this[firstChildIndex] > this[index] -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && this[secondChildIndex] > this[index] -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

context(order: Order<E>)
public fun <E> KoneSettableList<E>.heapsort() {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    this[firstChildIndex] gt this[index] && this[firstChildIndex] gt this[secondChildIndex] -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    this[secondChildIndex] gt this[index] -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && this[firstChildIndex] gt this[index] -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && this[secondChildIndex] gt this[index] -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

public fun <E> KoneSettableList<E>.heapsortWith(comparator: Comparator<E>) {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsGreaterThanRight && comparator.compare(this[firstChildIndex], this[secondChildIndex]) == ComparisonResult.LeftIsGreaterThanRight -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    comparator.compare(this[secondChildIndex], this[index]) == ComparisonResult.LeftIsGreaterThanRight -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsGreaterThanRight -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsGreaterThanRight -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

public fun <E: Comparable<E>> KoneSettableList<E>.heapsortDescending() {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    this[firstChildIndex] < this[index] && this[firstChildIndex] < this[secondChildIndex] -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    this[secondChildIndex] < this[index] -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && this[firstChildIndex] < this[index] -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && this[secondChildIndex] < this[index] -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

context(order: Order<E>)
public fun <E> KoneSettableList<E>.heapsortDescending() {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    this[firstChildIndex] lt this[index] && this[firstChildIndex] lt this[secondChildIndex] -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    this[secondChildIndex] lt this[index] -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && this[firstChildIndex] lt this[index] -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && this[secondChildIndex] lt this[index] -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

public fun <E> KoneSettableList<E>.heapsortWithDescending(comparator: Comparator<E>) {
    when (size) {
        0u -> return
        1u -> return
    }
    tailrec fun siftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsLessThanRight && comparator.compare(this[firstChildIndex], this[secondChildIndex]) == ComparisonResult.LeftIsLessThanRight -> {
                        swap(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
                    }
                    comparator.compare(this[secondChildIndex], this[index]) == ComparisonResult.LeftIsLessThanRight -> {
                        swap(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
                    }
                }
            firstChildIndex < heapSize && comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsLessThanRight -> {
                swap(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex, heapSize)
            }
            secondChildIndex < heapSize && comparator.compare(this[firstChildIndex], this[index]) == ComparisonResult.LeftIsLessThanRight -> {
                swap(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex, heapSize)
            }
        }
    }
    for (index in (size / 2u - 1u) downTo 0u) siftTheNodeUpToTheLeaf(index, size)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        siftTheNodeUpToTheLeaf(0u, index)
    }
}

@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.heapsortBySiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    selector(this[firstChildIndex]) > selector(this[index]) && selector(this[firstChildIndex]) > selector(this[secondChildIndex]) -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    
                    selector(this[secondChildIndex]) > selector(this[index]) -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            
            firstChildIndex < heapSize && selector(this[firstChildIndex]) > selector(this[index]) -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            
            secondChildIndex < heapSize && selector(this[secondChildIndex]) > selector(this[index]) -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

public inline fun <E, R: Comparable<R>> KoneSettableList<E>.heapsortBy(selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortBySiftTheNodeUpToTheLeaf(index, size, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortBySiftTheNodeUpToTheLeaf(0u, index, selector)
    }
}

@PublishedApi
context(order: Order<R>)
internal inline fun <E, R> KoneSettableList<E>.heapsortBySiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    selector(this[firstChildIndex]) gt selector(this[index]) && selector(this[firstChildIndex]) gt selector(
                        this[secondChildIndex]
                    ) -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    
                    selector(this[secondChildIndex]) gt selector(this[index]) -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            
            firstChildIndex < heapSize && selector(this[firstChildIndex]) gt selector(this[index]) -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            
            secondChildIndex < heapSize && selector(this[secondChildIndex]) gt selector(this[index]) -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

context(order: Order<R>)
public inline fun <E, R> KoneSettableList<E>.heapsortBy(selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortBySiftTheNodeUpToTheLeaf(index, size, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortBySiftTheNodeUpToTheLeaf(0u, index, selector)
    }
}

@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.heapsortWithBySiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, comparator: Comparator<R>, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsGreaterThanRight && comparator.compare(selector(this[firstChildIndex]), selector(this[secondChildIndex])) == ComparisonResult.LeftIsGreaterThanRight -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    comparator.compare(selector(this[secondChildIndex]), selector(this[index])) == ComparisonResult.LeftIsGreaterThanRight -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            firstChildIndex < heapSize && comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsGreaterThanRight -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            secondChildIndex < heapSize && comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsGreaterThanRight -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

public inline fun <E, R> KoneSettableList<E>.heapsortWithBy(comparator: Comparator<R>, selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortWithBySiftTheNodeUpToTheLeaf(index, size, comparator, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortWithBySiftTheNodeUpToTheLeaf(0u, index, comparator, selector)
    }
}

@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.heapsortByDescendingSiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    selector(this[firstChildIndex]) < selector(this[index]) && selector(this[firstChildIndex]) < selector(this[secondChildIndex]) -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    
                    selector(this[secondChildIndex]) < selector(this[index]) -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            
            firstChildIndex < heapSize && selector(this[firstChildIndex]) < selector(this[index]) -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            
            secondChildIndex < heapSize && selector(this[secondChildIndex]) < selector(this[index]) -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

public inline fun <E, R: Comparable<R>> KoneSettableList<E>.heapsortByDescending(selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortByDescendingSiftTheNodeUpToTheLeaf(index, size, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortByDescendingSiftTheNodeUpToTheLeaf(0u, index, selector)
    }
}

@PublishedApi
context(order: Order<R>)
internal inline fun <E, R> KoneSettableList<E>.heapsortByDescendingSiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    selector(this[firstChildIndex]) lt selector(this[index]) && selector(this[firstChildIndex]) lt selector(
                        this[secondChildIndex]
                    ) -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    
                    selector(this[secondChildIndex]) lt selector(this[index]) -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            
            firstChildIndex < heapSize && selector(this[firstChildIndex]) lt selector(this[index]) -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            
            secondChildIndex < heapSize && selector(this[secondChildIndex]) lt selector(this[index]) -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

context(order: Order<R>)
public inline fun <E, R> KoneSettableList<E>.heapsortByDescending(selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortByDescendingSiftTheNodeUpToTheLeaf(index, size, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortByDescendingSiftTheNodeUpToTheLeaf(0u, index, selector)
    }
}

@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.heapsortWithByDescendingSiftTheNodeUpToTheLeaf(index: UInt, heapSize: UInt, comparator: Comparator<R>, selector: (E) -> R) {
    var index = index
    while (true) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < heapSize && secondChildIndex < heapSize ->
                when {
                    comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsLessThanRight && comparator.compare(selector(this[firstChildIndex]), selector(this[secondChildIndex])) == ComparisonResult.LeftIsLessThanRight -> {
                        swap(firstChildIndex, index)
                        index = firstChildIndex
                        continue
                    }
                    comparator.compare(selector(this[secondChildIndex]), selector(this[index])) == ComparisonResult.LeftIsLessThanRight -> {
                        swap(secondChildIndex, index)
                        index = secondChildIndex
                        continue
                    }
                }
            firstChildIndex < heapSize && comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsLessThanRight -> {
                swap(firstChildIndex, index)
                index = firstChildIndex
                continue
            }
            secondChildIndex < heapSize && comparator.compare(selector(this[firstChildIndex]), selector(this[index])) == ComparisonResult.LeftIsLessThanRight -> {
                swap(secondChildIndex, index)
                index = secondChildIndex
                continue
            }
        }
        break
    }
}

public inline fun <E, R> KoneSettableList<E>.heapsortWithByDescending(comparator: Comparator<R>, selector: (E) -> R) {
    when (size) {
        0u -> return
        1u -> return
    }
    for (index in (size / 2u - 1u) downTo 0u) heapsortWithByDescendingSiftTheNodeUpToTheLeaf(index, size, comparator, selector)
    for (index in (size - 1u) downTo 1u) {
        swap(0u, index)
        heapsortWithByDescendingSiftTheNodeUpToTheLeaf(0u, index, comparator, selector)
    }
}

public fun <E: Comparable<E>> KoneIterable<E>.heapsorted(): KoneList<E> =
    toKoneSettableList().apply { heapsort() }

context(_: Order<E>)
public fun <E> KoneIterable<E>.heapsorted(): KoneList<E> =
    toKoneSettableList().apply { heapsort() }

public fun <E> KoneIterable<E>.heapsortedWith(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { heapsortWith(comparator) }

public fun <E: Comparable<E>> KoneIterable<E>.heapsortedDescending(): KoneList<E> =
    toKoneSettableList().apply { heapsortDescending() }

context(_: Order<E>)
public fun <E> KoneIterable<E>.heapsortedDescending(): KoneList<E> =
    toKoneSettableList().apply { heapsortDescending() }

public fun <E> KoneIterable<E>.heapsortedWithDescending(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { heapsortWithDescending(comparator) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.heapsortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortBy(selector) }

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.heapsortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortBy(selector) }

public inline fun <E, R> KoneIterable<E>.heapsortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortWithBy(comparator, selector) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.heapsortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortByDescending(selector) }

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.heapsortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortByDescending(selector) }

public inline fun <E, R> KoneIterable<E>.heapsortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { heapsortWithByDescending(comparator, selector) }
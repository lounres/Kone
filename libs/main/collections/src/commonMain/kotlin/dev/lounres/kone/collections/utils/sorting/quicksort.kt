/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils.sorting

import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableLinkedList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.list.toKoneSettableList
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareTo
import kotlin.jvm.JvmInline


// TODO: Add outer variants: variants that create and use separate list for sorting and just assign result to target
// TODO: Quicksort does not work for now. Fix it.

@PublishedApi
//@JvmInline
internal /*value*/ data class RangeToSort(val from: UInt, val to: UInt)

public fun <E: Comparable<E>> KoneSettableList<E>.quicksort() {
    fun swap(i: UInt, j: UInt) {
        this[i] = this[j].also { this[j] = this[i] }
    }
    fun divide(from: UInt, to: UInt): UInt {
        val pivot = this[(from + to) / 2u]
        
        var i = from
        var j = to
        
        TODO()
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        if (from < to) sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle) sortQueue.addLast(RangeToSort(from, middle))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}
context(_: Order<E>)
public fun <E> KoneSettableList<E>.quicksort() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] < valueInTheMiddle) i++
            while (this[j] > valueInTheMiddle) j--
            if (i < j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from + 1u < middle) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}
public fun <E> KoneSettableList<E>.quicksortWith(comparator: Comparator<E>) {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (comparator.compare(this[i], valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
            while (comparator.compare(this[j], valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
            if (i < j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from + 1u < middle) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}

public fun <E: Comparable<E>> KoneSettableList<E>.quicksortDescending() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] > valueInTheMiddle) i++
            while (this[j] < valueInTheMiddle) j--
            if (i < j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from + 1u < middle) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}
context(_: Order<E>)
public fun <E> KoneSettableList<E>.quicksortDescending() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] > valueInTheMiddle) i++
            while (this[j] < valueInTheMiddle) j--
            if (i < j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from + 1u < middle) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}
public fun <E> KoneSettableList<E>.quicksortWithDescending(comparator: Comparator<E>) {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (comparator.compare(this[i], valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
            while (comparator.compare(this[j], valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
            if (i < j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from + 1u < middle) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle + 1u < to) sortQueue.addLast(RangeToSort(middle + 1u, to))
        }
    }
    quickSort(0u, lastIndex)
}

// TODO: Move inside the following `sortBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.quicksortDivide(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) < valueInTheMiddle) i++
        while (selector(this[j]) > valueInTheMiddle) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
public inline fun <E, R: Comparable<R>> KoneSettableList<E>.quicksortBy(selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivide(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
// TODO: Move inside the following `sortBy` function when local inline functions will be ready
@PublishedApi
context(_: Order<R>)
internal inline fun <E, R> KoneSettableList<E>.quicksortDivide(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) < valueInTheMiddle) i++
        while (selector(this[j]) > valueInTheMiddle) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
context(_: Order<R>)
public inline fun <E, R> KoneSettableList<E>.quicksortBy(selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivide(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quicksortDivide(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (comparator.compare(selector(this[i]), valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
public inline fun <E, R> KoneSettableList<E>.quicksortWithBy(comparator: Comparator<R>, selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivide(from, to, comparator, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}

// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.quicksortDivideDescending(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) > valueInTheMiddle) i++
        while (selector(this[j]) < valueInTheMiddle) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
public inline fun <E, R: Comparable<R>> KoneSettableList<E>.quicksortByDescending(selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivideDescending(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
@PublishedApi
context(_: Order<R>)
internal inline fun <E, R> KoneSettableList<E>.quicksortDivideDescending(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) > valueInTheMiddle) i++
        while (selector(this[j]) < valueInTheMiddle) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
context(_: Order<R>)
public inline fun <E, R> KoneSettableList<E>.quicksortByDescending(selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivideDescending(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quicksortDivideDescending(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (comparator.compare(selector(this[i]), valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) j--
        if (i < j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
public inline fun <E, R> KoneSettableList<E>.quicksortWithByDescending(comparator: Comparator<R>, selector: (E) -> R) {
    val sortQueue: KoneDeque<RangeToSort> = KoneListBackedDeque(KoneArrayGrowableLinkedList())
    sortQueue.addLast(RangeToSort(0u, lastIndex))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = quicksortDivideDescending(from, to, comparator, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}

public fun <E: Comparable<E>> KoneIterable<E>.quicksorted(): KoneList<E> =
    toKoneSettableList().apply { quicksort() }

context(_: Order<E>)
public fun <E> KoneIterable<E>.quicksorted(): KoneList<E> =
    toKoneSettableList().apply { quicksort() }

public fun <E> KoneIterable<E>.quicksortedWith(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { quicksortWith(comparator) }

public fun <E: Comparable<E>> KoneIterable<E>.quicksortedDescending(): KoneList<E> =
    toKoneSettableList().apply { quicksortDescending() }

context(_: Order<E>)
public fun <E> KoneIterable<E>.quicksortedDescending(): KoneList<E> =
    toKoneSettableList().apply { quicksortDescending() }

public fun <E> KoneIterable<E>.quicksortedWithDescending(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { quicksortWithDescending(comparator) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.quicksortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortBy(selector) }

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.quicksortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortBy(selector) }

public inline fun <E, R> KoneIterable<E>.quicksortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortWithBy(comparator, selector) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.quicksortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortByDescending(selector) }

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.quicksortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortByDescending(selector) }

public inline fun <E, R> KoneIterable<E>.quicksortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { quicksortWithByDescending(comparator, selector) }
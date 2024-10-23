/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison

import kotlin.jvm.JvmInline


/**
 * Context of [linear (total) order](https://en.wikipedia.org/wiki/Total_order).
 */
public fun interface Order<in E> : Equality<E> {
    public infix operator fun E.compareTo(other: E): Int
}

context(Order<E>)
public inline infix fun <E> E.greaterThan(other: E): Boolean = this > other
context(Order<E>)
public inline infix fun <E> E.greaterThanOrEqual(other: E): Boolean = this >= other
context(Order<E>)
public inline infix fun <E> E.lessThen(other: E): Boolean = this < other
context(Order<E>)
public inline infix fun <E> E.lessThenOrEqual(other: E): Boolean = this <= other
context(Order<E>)
public inline infix fun <E> E.gt(other: E): Boolean = this > other
context(Order<E>)
public inline infix fun <E> E.geq(other: E): Boolean = this >= other
context(Order<E>)
public inline infix fun <E> E.lt(other: E): Boolean = this < other
context(Order<E>)
public inline infix fun <E> E.leq(other: E): Boolean = this <= other

context(Order<E>)
public fun <E> min(a: E, b: E): E = if (a <= b) a else b
context(Order<E>)
public fun <E> max(a: E, b: E): E = if (a >= b) a else b
context(Order<E>)
public fun <E> min(vararg elements: E): E {
    if (elements.isEmpty()) throw IllegalArgumentException("Cannot calculate minimum of an empty collection of elements")
    return elements.reduce { a, b -> min(a, b) }
}
context(Order<E>)
public fun <E> max(vararg elements: E): E {
    if (elements.isEmpty()) throw IllegalArgumentException("Cannot calculate maximum of an empty collection of elements")
    return elements.reduce { a, b -> max(a, b) }
}

public fun <E: Comparable<E>> defaultOrder(): Order<E> = DefaultOrderOnComparables
public inline fun <E: Comparable<E>> defaultComparator(): Comparator<E> = naturalOrder()
public fun <E> Order<E>.asComparator(): Comparator<E> = Comparator { left, right -> left.compareTo(right) }
context(Order<E>)
public val <E> comparator: Comparator<E> get() = Comparator { left, right -> left.compareTo(right) }
public fun <E> Comparator<E>.asOrder(): Order<E> = Order { other -> compare(this, other) }
context(Order<E>)
public fun <T, E> compareByOrdered(vararg selectors: (T) -> E): Comparator<T> {
    require(selectors.isNotEmpty())
    return Comparator { a, b ->
        for (s in selectors) {
            val diff = s(a).compareTo(s(b))
            if (diff != 0) return@Comparator diff
        }
        0
    }
}

@JvmInline
public value class ClosedRange<out E>(public val start: E, public val endInclusive: E)
@JvmInline
public value class OpenEndedRange<out E>(public val start: E, public val endExclusive: E)

public operator fun <E> E.rangeTo(other: E): ClosedRange<E> = ClosedRange(this, other)
public operator fun <E> E.rangeUntil(other: E): OpenEndedRange<E> = OpenEndedRange(this, other)

context(Order<E>)
public operator fun <E> ClosedRange<E>.contains(element: E): Boolean = element >= start && element <= endInclusive
context(Order<E>)
public operator fun <E> OpenEndedRange<E>.contains(element: E): Boolean = element >= start && element < endExclusive
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison


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
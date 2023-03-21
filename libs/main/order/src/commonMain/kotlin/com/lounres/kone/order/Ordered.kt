/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.order

import com.lounres.kone.computationalContext.ComputationalContext


/**
 * Context of [linear (total) order](https://en.wikipedia.org/wiki/Total_order).
 */
public interface Ordered<in E>: ComputationalContext{
    public infix operator fun E.compareTo(other: E): Int
}

context(Ordered<E>)
public infix fun <E> E.greaterThan(other: E): Boolean = this > other
context(Ordered<E>)
public infix fun <E> E.greaterThanOrEqual(other: E): Boolean = this >= other
context(Ordered<E>)
public infix fun <E> E.lessThen(other: E): Boolean = this < other
context(Ordered<E>)
public infix fun <E> E.lessThenOrEqual(other: E): Boolean = this <= other
context(Ordered<E>)
public infix fun <E> E.gt(other: E): Boolean = this > other
context(Ordered<E>)
public infix fun <E> E.geq(other: E): Boolean = this >= other
context(Ordered<E>)
public infix fun <E> E.lt(other: E): Boolean = this < other
context(Ordered<E>)
public infix fun <E> E.leq(other: E): Boolean = this <= other

context(Ordered<E>)
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
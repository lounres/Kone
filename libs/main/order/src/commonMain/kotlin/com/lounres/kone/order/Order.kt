/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.order

import com.lounres.kone.context.KoneContext


/**
 * Context of [linear (total) order](https://en.wikipedia.org/wiki/Total_order).
 */
public interface Order<in E> : KoneContext {
    public infix operator fun E.compareTo(other: E): Int
}

public val <E> Order<E>.comparator: Comparator<E> get() = Comparator { a, b -> a.compareTo(b) }
public fun <T, E> Order<E>.compareByOrdered(vararg selectors: (T) -> E): Comparator<T> {
    require(selectors.isNotEmpty())
    return Comparator { a, b ->
        for (s in selectors) {
            val diff = s(a).compareTo(s(b))
            if (diff != 0) return@Comparator diff
        }
        0
    }
}
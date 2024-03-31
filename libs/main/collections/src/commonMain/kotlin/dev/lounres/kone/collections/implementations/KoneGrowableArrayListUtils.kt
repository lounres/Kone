/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneGrowableArrayList(context: Equality<E> = defaultEquality()): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(size = 0u, context = context)

public fun <E> KoneGrowableArrayList(initialCapacity: UInt, context: Equality<E> = defaultEquality()): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = context,
    )

public fun <E> KoneGrowableArrayList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneGrowableArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = context,
    )
}
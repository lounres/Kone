/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneGrowableLinkedArrayList(context: Equality<E> = defaultEquality()): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(size = 0u, context = context)

public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt, context: Equality<E> = defaultEquality()): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = context,
    )

public fun <E> KoneGrowableLinkedArrayList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = context,
    )
}
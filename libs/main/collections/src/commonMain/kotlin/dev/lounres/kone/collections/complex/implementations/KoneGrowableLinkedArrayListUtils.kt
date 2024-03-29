/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality


public fun <E> KoneGrowableLinkedArrayList(context: Equality<E>): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(size = 0u, context = context)

context(Equality<E>)
public fun <E> KoneGrowableLinkedArrayList(): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(size = 0u, context = this@Equality)

public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt, context: Equality<E>): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = context,
    )

context(Equality<E>)
public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = this@Equality,
    )

public fun <E> KoneGrowableLinkedArrayList(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = context,
    )
}

context(Equality<E>)
public fun <E> KoneGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = this@Equality,
    )
}
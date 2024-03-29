/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality


public fun <E> KoneGrowableArrayList(context: Equality<E>): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(size = 0u, context = context)

context(Equality<E>)
public fun <E> KoneGrowableArrayList(): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(size = 0u, context = this@Equality)

public fun <E> KoneGrowableArrayList(initialCapacity: UInt, context: Equality<E>): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = context,
    )

context(Equality<E>)
public fun <E> KoneGrowableArrayList(initialCapacity: UInt): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        context = this@Equality,
    )

public fun <E> KoneGrowableArrayList(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): KoneGrowableArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = context,
    )
}

context(Equality<E>)
public fun <E> KoneGrowableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = this@Equality,
    )
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E, EC: Equality<E>> KoneGrowableArrayList(elementContext: EC): KoneGrowableArrayList<E, EC> =
    KoneGrowableArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneGrowableArrayList(): KoneGrowableArrayList<E, Equality<E>> =
    KoneGrowableArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneGrowableArrayList(initialCapacity: UInt, elementContext: EC): KoneGrowableArrayList<E, EC> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = elementContext,
    )

public fun <E> KoneGrowableArrayList(initialCapacity: UInt): KoneGrowableArrayList<E, Equality<E>> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = defaultEquality(),
    )

public inline fun <E, EC: Equality<E>> KoneGrowableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneGrowableArrayList<E, EC> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public inline fun <E> KoneGrowableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableArrayList<E, Equality<E>> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}
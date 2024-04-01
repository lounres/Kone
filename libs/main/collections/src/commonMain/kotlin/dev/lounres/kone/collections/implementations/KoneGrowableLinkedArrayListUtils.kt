/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
    KoneGrowableLinkedArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneGrowableLinkedArrayList(): KoneGrowableLinkedArrayList<E, Equality<E>> =
    KoneGrowableLinkedArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(initialCapacity: UInt, elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = elementContext,
    )

public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt): KoneGrowableLinkedArrayList<E, Equality<E>> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E, EC> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E, Equality<E>> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo


public fun <E> KoneGrowableLinkedArrayList(): KoneGrowableLinkedArrayList<E> = KoneGrowableLinkedArrayList(size = 0u)

public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt): KoneGrowableLinkedArrayList<E> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <E> KoneGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
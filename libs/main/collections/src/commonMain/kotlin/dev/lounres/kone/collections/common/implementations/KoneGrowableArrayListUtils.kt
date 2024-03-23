/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo


public fun <E> KoneGrowableArrayList(): KoneGrowableArrayList<E> = KoneGrowableArrayList(size = 0u)

public fun <E> KoneGrowableArrayList(initialCapacity: UInt): KoneGrowableArrayList<E> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <E> KoneGrowableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.contextual.KoneContextualMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo


public fun <E> KoneContextualGrowableArrayList(): KoneContextualGrowableArrayList<E> = KoneContextualGrowableArrayList(size = 0u)

public fun <E> KoneContextualGrowableArrayList(initialCapacity: UInt): KoneContextualGrowableArrayList<E> =
    KoneContextualGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <E> KoneContextualGrowableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneContextualGrowableArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneContextualGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneContextualMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
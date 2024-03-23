/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.contextual.KoneContextualMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo


public fun <E> KoneContextualGrowableLinkedArrayList(): KoneContextualGrowableLinkedArrayList<E> = KoneContextualGrowableLinkedArrayList(size = 0u)

public fun <E> KoneContextualGrowableLinkedArrayList(initialCapacity: UInt): KoneContextualGrowableLinkedArrayList<E> =
    KoneContextualGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <E> KoneContextualGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneContextualGrowableLinkedArrayList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneContextualGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneContextualMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
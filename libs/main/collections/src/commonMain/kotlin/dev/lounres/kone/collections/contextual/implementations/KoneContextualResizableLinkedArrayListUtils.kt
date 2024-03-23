/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.contextual.KoneContextualMutableArray
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import kotlin.math.max


public fun <E> KoneContextualResizableLinkedArrayList(): KoneContextualResizableLinkedArrayList<E> = KoneContextualResizableLinkedArrayList(size = 0u)

public fun <E> KoneContextualResizableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneContextualResizableLinkedArrayList<E> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneContextualResizableLinkedArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneContextualMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import kotlin.math.min


public fun <E> KoneResizableLinkedList(): KoneResizableLinkedList<E> = KoneResizableLinkedList(size = 0u)

public fun <E> KoneResizableLinkedList(size: UInt, initializer: (index: UInt) -> E): KoneResizableLinkedList<E> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(min(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableLinkedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}
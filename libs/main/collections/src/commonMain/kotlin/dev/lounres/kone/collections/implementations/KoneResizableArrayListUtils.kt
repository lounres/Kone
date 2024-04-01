/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlin.math.max


public fun <E, EC: Equality<E>> KoneResizableArrayList(elementContext: EC): KoneResizableArrayList<E, EC> =
    KoneResizableArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableArrayList(): KoneResizableArrayList<E, Equality<E>> =
    KoneResizableArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneResizableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneResizableArrayList<E, EC> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneResizableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneResizableArrayList<E, Equality<E>> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}
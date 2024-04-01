/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlin.math.max


public fun <E, EC: Equality<E>> KoneResizableLinkedArrayList(elementContext: EC): KoneResizableLinkedArrayList<E, EC> =
    KoneResizableLinkedArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableLinkedArrayList(): KoneResizableLinkedArrayList<E, Equality<E>> =
    KoneResizableLinkedArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneResizableLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneResizableLinkedArrayList<E, EC> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableLinkedArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext
    )
}

public fun <E> KoneResizableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneResizableLinkedArrayList<E, Equality<E>> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableLinkedArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality()
    )
}
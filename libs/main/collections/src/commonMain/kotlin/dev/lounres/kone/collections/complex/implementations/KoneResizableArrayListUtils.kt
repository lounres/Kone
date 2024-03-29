/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.comparison.Equality
import kotlin.math.max


public fun <E> KoneResizableArrayList(context: Equality<E>): KoneResizableArrayList<E> =
    KoneResizableArrayList(size = 0u, context = context)

context(Equality<E>)
public fun <E> KoneResizableArrayList(): KoneResizableArrayList<E> =
    KoneResizableArrayList(size = 0u, context = this@Equality)

public fun <E> KoneResizableArrayList(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): KoneResizableArrayList<E> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = context,
    )
}

context(Equality<E>)
public fun <E> KoneResizableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneResizableArrayList<E> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        context = this@Equality,
    )
}
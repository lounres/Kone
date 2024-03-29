/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.comparison.Equality


public fun <E> KoneFixedCapacityArrayList(capacity: UInt, context: Equality<E>): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
        context = context,
    )

context(Equality<E>)
public fun <E> KoneFixedCapacityArrayList(capacity: UInt): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
        context = this@Equality,
    )

public fun <E> KoneFixedCapacityArrayList(size: UInt, initializer: (index: UInt) -> E, context: Equality<E>): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        context = context,
    )

context(Equality<E>)
public fun <E> KoneFixedCapacityArrayList(size: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        context = this@Equality,
    )

public fun <E> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E, context: Equality<E>): KoneFixedCapacityArrayList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        context = context,
    )
}

context(Equality<E>)
public fun <E> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        context = this@Equality,
    )
}
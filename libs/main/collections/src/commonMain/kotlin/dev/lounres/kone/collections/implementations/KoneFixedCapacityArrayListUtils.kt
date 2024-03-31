/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneFixedCapacityArrayList(capacity: UInt, context: Equality<E> = defaultEquality()): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
        context = context,
    )

public fun <E> KoneFixedCapacityArrayList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        context = context,
    )

public fun <E> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        context = context,
    )
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneFixedCapacityLinkedArrayList(capacity: UInt, context: Equality<E> = defaultEquality()): KoneFixedCapacityLinkedArrayList<E> =
    KoneFixedCapacityLinkedArrayList(
        size = 0u,
        capacity = capacity,
        context = context,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E> =
    KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        context = context,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, capacity: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        context = context,
    )
}
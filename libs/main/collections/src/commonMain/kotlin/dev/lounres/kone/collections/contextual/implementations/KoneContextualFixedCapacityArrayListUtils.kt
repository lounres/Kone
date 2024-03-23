/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.contextual.KoneContextualMutableArray


public fun <E> KoneContextualFixedCapacityArrayList(capacity: UInt): KoneContextualFixedCapacityArrayList<E> =
    KoneContextualFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
    )

public fun <E> KoneContextualFixedCapacityArrayList(size: UInt, initializer: (index: UInt) -> E): KoneContextualFixedCapacityArrayList<E> =
    KoneContextualFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneContextualMutableArray(size) { if (it < size) initializer(it) else null }
    )

public fun <E> KoneContextualFixedCapacityArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneContextualFixedCapacityArrayList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneContextualFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneContextualMutableArray(capacity) { if (it < size) initializer(it) else null }
    )
}
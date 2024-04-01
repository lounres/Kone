/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(capacity: UInt, elementContext: EC): KoneFixedCapacityLinkedArrayList<E, EC> =
    KoneFixedCapacityLinkedArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(capacity: UInt): KoneFixedCapacityLinkedArrayList<E, Equality<E>> =
    KoneFixedCapacityLinkedArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> =
    KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, Equality<E>> =
    KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(size: UInt, capacity: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, Equality<E>> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}
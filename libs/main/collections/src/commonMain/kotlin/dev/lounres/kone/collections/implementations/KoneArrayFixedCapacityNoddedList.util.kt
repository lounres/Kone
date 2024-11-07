/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayFixedCapacityNoddedList(capacity: UInt): KoneArrayFixedCapacityNoddedList<Element> =
    KoneArrayFixedCapacityNoddedList(
        size = 0u,
        capacity = capacity,
    )

public inline fun <Element> KoneArrayFixedCapacityNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityNoddedList<Element> =
    KoneArrayFixedCapacityNoddedList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
    )

public inline fun <Element> KoneArrayFixedCapacityNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityNoddedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityNoddedList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
    )
}

internal class KoneArrayFixedCapacityNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayFixedCapacityNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )
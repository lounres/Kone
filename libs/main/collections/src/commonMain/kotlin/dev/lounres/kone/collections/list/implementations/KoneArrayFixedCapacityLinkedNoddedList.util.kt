/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.producers.KoneFixedCapacityMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


/**
 * Returns an empty [KoneArrayFixedCapacityLinkedNoddedList] of provided [capacity].
 */
public fun <Element> KoneArrayFixedCapacityLinkedNoddedList(capacity: UInt): KoneArrayFixedCapacityLinkedNoddedList<Element> =
    KoneArrayFixedCapacityLinkedNoddedList(
        size = 0u,
        capacity = capacity,
    )

/**
 * Returns a [KoneArrayFixedCapacityLinkedNoddedList] of provided [size] (and equal capacity) of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public fun <Element> KoneArrayFixedCapacityLinkedNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityLinkedNoddedList<Element> =
    KoneArrayFixedCapacityLinkedNoddedList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) KoneArrayFixedCapacityLinkedNoddedList.Node(initializer(it), it) else null },
    )

/**
 * Returns a [KoneArrayFixedCapacityLinkedNoddedList] of provided [size] and [capacity] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public fun <Element> KoneArrayFixedCapacityLinkedNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityLinkedNoddedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityLinkedNoddedList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) KoneArrayFixedCapacityLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

/**
 * Producer of [KoneArrayFixedCapacityLinkedNoddedList].
 */
internal object KoneArrayFixedCapacityLinkedNoddedListProducer : KoneFixedCapacityMutableNoddedListProducer {
    override fun <Element> produce(capacity: UInt): KoneArrayFixedCapacityLinkedNoddedList<Element> =
        KoneArrayFixedCapacityLinkedNoddedList(capacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> =
        KoneArrayFixedCapacityLinkedNoddedList(capacity = initialCapacity, size = number, initializer = builder)
}

public fun KoneArrayFixedCapacityLinkedNoddedList.Companion.producer(): KoneFixedCapacityMutableNoddedListProducer = KoneArrayFixedCapacityLinkedNoddedListProducer

internal class KoneArrayFixedCapacityLinkedNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayFixedCapacityLinkedNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayFixedCapacityLinkedNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayFixedCapacityLinkedNoddedList<E> =
        KoneArrayFixedCapacityLinkedNoddedList(size, initializer)
}
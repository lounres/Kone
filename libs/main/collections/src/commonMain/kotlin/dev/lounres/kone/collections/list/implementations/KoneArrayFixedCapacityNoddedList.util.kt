/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


/**
 * Returns an empty [KoneArrayFixedCapacityNoddedList] of provided [capacity].
 */
public fun <Element> KoneArrayFixedCapacityNoddedList(capacity: UInt): KoneArrayFixedCapacityNoddedList<Element> =
    KoneArrayFixedCapacityNoddedList(
        size = 0u,
        data = KoneMutableArray(capacity) { null },
    )

/**
 * Returns a [KoneArrayFixedCapacityNoddedList] of provided [size] (and equal capacity) of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneArrayFixedCapacityNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityNoddedList<Element> =
    KoneArrayFixedCapacityNoddedList(
        size = size,
        data = KoneMutableArray(size) { if (it < size) KoneArrayFixedCapacityNoddedList.Node(initializer(it), it) else null },
    )

/**
 * Returns a [KoneArrayFixedCapacityNoddedList] of provided [size] and [capacity] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneArrayFixedCapacityNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityNoddedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityNoddedList(
        size = size,
        data = KoneMutableArray(capacity) { if (it < size) KoneArrayFixedCapacityNoddedList.Node(initializer(it), it) else null },
    )
}

/**
 * Producer of [KoneArrayFixedCapacityNoddedList].
 */
internal object KoneArrayFixedCapacityNoddedListProducer : KoneFixedCapacityMutableNoddedListProducer {
    override fun <Element> produce(capacity: UInt): KoneArrayFixedCapacityNoddedList<Element> =
        KoneArrayFixedCapacityNoddedList(capacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayFixedCapacityNoddedList<Element> =
        KoneArrayFixedCapacityNoddedList(capacity = initialCapacity, size = number, initializer = builder)
}

public fun KoneArrayFixedCapacityNoddedList.Companion.producer(): KoneFixedCapacityMutableNoddedListProducer = KoneArrayFixedCapacityNoddedListProducer

internal class KoneArrayFixedCapacityNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayFixedCapacityNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayFixedCapacityNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayFixedCapacityNoddedList<E> =
        KoneArrayFixedCapacityNoddedList(size, initializer)
}
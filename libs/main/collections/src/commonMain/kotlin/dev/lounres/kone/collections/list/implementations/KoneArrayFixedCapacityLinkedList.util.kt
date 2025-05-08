/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.producers.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


/**
 * Returns an empty [KoneArrayFixedCapacityLinkedList] of provided [capacity].
 */
public fun <Element> KoneArrayFixedCapacityLinkedList(capacity: UInt): KoneArrayFixedCapacityLinkedList<Element> =
    KoneArrayFixedCapacityLinkedList(
        size = 0u,
        capacity = capacity,
    )

/**
 * Returns a [KoneArrayFixedCapacityLinkedList] of provided [size] (and equal capacity) of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public fun <Element> KoneArrayFixedCapacityLinkedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityLinkedList<Element> =
    KoneArrayFixedCapacityLinkedList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
    )

/**
 * Returns a [KoneArrayFixedCapacityLinkedList] of provided [size] and [capacity] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public fun <Element> KoneArrayFixedCapacityLinkedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityLinkedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityLinkedList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
    )
}

/**
 * Producer of [KoneArrayFixedCapacityLinkedList].
 */
internal object KoneArrayFixedCapacityLinkedListProducer : KoneFixedCapacityMutableListProducer {
    override fun <E> produce(capacity: UInt): KoneArrayFixedCapacityLinkedList<E> = KoneArrayFixedCapacityLinkedList(capacity)
    override fun <E> produceBy(capacity: UInt, number: UInt, builder: (UInt) -> E): KoneMutableList<E> =
        KoneArrayFixedCapacityLinkedList(size =  number, capacity = capacity, initializer = builder)
}

public fun KoneArrayFixedCapacityLinkedList.Companion.producer(): KoneFixedCapacityMutableListProducer = KoneArrayFixedCapacityLinkedListProducer

internal class KoneFixedCapacityLinkedArrayListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayFixedCapacityLinkedList<E>>(), DeserializationStrategy<KoneArrayFixedCapacityLinkedList<E>> {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayFixedCapacityLinkedList",
            elementSerializer = elementSerializer
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayFixedCapacityLinkedList<E> =
        KoneArrayFixedCapacityLinkedList(size, initializer)
}
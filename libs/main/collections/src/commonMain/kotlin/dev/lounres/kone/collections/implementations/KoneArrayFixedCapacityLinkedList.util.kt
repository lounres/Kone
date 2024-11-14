/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.producers.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
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
public object KoneArrayFixedCapacityLinkedListProducer : KoneFixedCapacityMutableListProducer {
    override fun <E> produce(capacity: UInt): KoneArrayFixedCapacityLinkedList<E> = KoneArrayFixedCapacityLinkedList(capacity)
    override fun <E> produceBy(capacity: UInt, number: UInt, builder: (UInt) -> E): KoneMutableList<E> =
        KoneArrayFixedCapacityLinkedList(size =  number, capacity = capacity, initializer = builder)
}

internal class KoneArrayFixedCapacityLinkedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayFixedCapacityLinkedList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneFixedCapacityLinkedArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneFixedCapacityLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneFixedCapacityLinkedArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> =
//        KoneFixedCapacityLinkedArrayList(size, elementContext, initializer)
//}
//
//internal class KoneFixedCapacityLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneFixedCapacityLinkedArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityLinkedArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneFixedCapacityLinkedArrayList<E, EC> =
//        KoneFixedCapacityLinkedArrayList(elementList.size, elementContext) { elementList[it] }
//}
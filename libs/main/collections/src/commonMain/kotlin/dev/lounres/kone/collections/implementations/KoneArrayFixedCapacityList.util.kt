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


public fun <Element> KoneArrayFixedCapacityList(capacity: UInt): KoneArrayFixedCapacityList<Element> =
    KoneArrayFixedCapacityList(
        size = 0u,
        capacity = capacity,
    )

public inline fun <Element> KoneArrayFixedCapacityList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityList<Element> =
    KoneArrayFixedCapacityList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
    )

public inline fun <Element> KoneArrayFixedCapacityList(capacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayFixedCapacityListProducer : KoneFixedCapacityMutableListProducer {
    override fun <Element> produce(capacity: UInt): KoneArrayFixedCapacityList<Element> = KoneArrayFixedCapacityList(capacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableList<Element> =
        KoneArrayFixedCapacityList(initialCapacity, number, builder)
}

internal class KoneArrayFixedCapacityListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayFixedCapacityList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneFixedCapacityArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneFixedCapacityArrayList<E, EC>>(), DeserializationStrategy<KoneFixedCapacityArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneFixedCapacityArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneFixedCapacityArrayList<E, EC> =
//        KoneFixedCapacityArrayList(size, elementContext, initializer)
//}
//
//internal class KoneFixedCapacityArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneFixedCapacityArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneFixedCapacityArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneFixedCapacityArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneFixedCapacityArrayList<E, EC> =
//        KoneFixedCapacityArrayList(elementList.size, elementContext) { elementList[it] }
//}
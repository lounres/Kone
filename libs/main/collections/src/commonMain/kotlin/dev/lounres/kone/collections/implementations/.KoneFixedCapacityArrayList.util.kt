/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneFixedCapacityArrayList(capacity: UInt): KoneFixedCapacityArrayList<Element> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
    )

public inline fun <Element> KoneFixedCapacityArrayList(size: UInt, initializer: (index: UInt) -> Element): KoneFixedCapacityArrayList<Element> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
    )

public inline fun <Element> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneFixedCapacityArrayList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
    )
}

internal class KoneFixedCapacityArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList<data>",
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
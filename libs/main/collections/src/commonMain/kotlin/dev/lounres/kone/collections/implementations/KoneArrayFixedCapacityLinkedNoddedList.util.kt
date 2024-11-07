/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <E> KoneArrayFixedCapacityLinkedNoddedList(capacity: UInt): KoneArrayFixedCapacityLinkedNoddedList<E> =
    KoneArrayFixedCapacityLinkedNoddedList(
        size = 0u,
        capacity = capacity,
    )

public fun <E> KoneArrayFixedCapacityLinkedNoddedList(size: UInt, initializer: (index: UInt) -> E): KoneArrayFixedCapacityLinkedNoddedList<E> =
    KoneArrayFixedCapacityLinkedNoddedList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
    )

public fun <E> KoneArrayFixedCapacityLinkedNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneArrayFixedCapacityLinkedNoddedList<E> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityLinkedNoddedList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
    )
}

internal class KoneArrayFixedCapacityLinkedNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayFixedCapacityLinkedNoddedList<data>",
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
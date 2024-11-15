/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableLinkedNoddedList(): KoneArrayGrowableLinkedNoddedList<Element> =
    KoneArrayGrowableLinkedNoddedList(size = 0u)

public fun <Element> KoneArrayGrowableLinkedNoddedList(initialCapacity: UInt): KoneArrayGrowableLinkedNoddedList<Element> =
    KoneArrayGrowableLinkedNoddedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneArrayGrowableLinkedNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableLinkedNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

public fun <Element> KoneArrayGrowableLinkedNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(capacity)
    return KoneArrayGrowableLinkedNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

public object KoneArrayGrowableLinkedNoddedListProducer : KoneGrowableMutableNoddedListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableLinkedNoddedList<Element> =
        KoneArrayGrowableLinkedNoddedList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> =
        KoneArrayGrowableLinkedNoddedList(size = number, capacity = initialCapacity, initializer = builder)
}

internal class KoneArrayGrowableLinkedNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayGrowableLinkedNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneGrowableLinkedArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneGrowableLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneGrowableLinkedArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGrowableLinkedArrayList<E, EC> =
//        KoneGrowableLinkedArrayList(size, elementContext, initializer)
//}
//
//internal class KoneGrowableLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneGrowableLinkedArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneGrowableLinkedArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
//        KoneGrowableLinkedArrayList(elementList.size, elementContext) { elementList[it] }
//}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <Element> KoneArrayResizableLinkedNoddedList(): KoneArrayResizableLinkedNoddedList<Element> =
    KoneArrayResizableLinkedNoddedList(size = 0u)

public inline fun <Element> KoneArrayResizableLinkedNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableLinkedNoddedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    TODO("Not yet implemented")
//    return KoneArrayResizableLinkedNoddedList(
//        size = size,
//        dataSizeNumber = dataSizeNumber,
//        sizeUpperBound = sizeUpperBound,
//        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
//    )
}

public object KoneArrayResizableLinkedNoddedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneArrayResizableLinkedNoddedList<Element> = KoneArrayResizableLinkedNoddedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArrayResizableLinkedNoddedList<Element> =
        KoneArrayResizableLinkedNoddedList(number, builder)
}

internal class KoneArrayResizableLinkedNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableLinkedNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneResizableLinkedArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneResizableLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneResizableLinkedArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneResizableLinkedArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableLinkedArrayList<E, EC> =
//        KoneResizableLinkedArrayList(size, elementContext, initializer)
//}
//
//internal class KoneResizableLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableLinkedArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneResizableLinkedArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneResizableLinkedArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableLinkedArrayList<E, EC> =
//        KoneResizableLinkedArrayList(elementList.size, elementContext) { elementList[it] }
//}
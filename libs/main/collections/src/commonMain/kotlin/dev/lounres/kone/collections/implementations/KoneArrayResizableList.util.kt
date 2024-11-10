/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <Element> KoneArrayResizableList(): KoneArrayResizableList<Element> =
    KoneArrayResizableList(size = 0u)

public inline fun <Element> KoneArrayResizableList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayResizableListProducer : KoneResizableMutableListProducer {
    override fun <E> produce(): KoneArrayResizableList<E> = KoneArrayResizableList()
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList(number, builder)
}

internal class KoneArrayResizableListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneResizableArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneResizableArrayList<E, EC>>(), DeserializationStrategy<KoneResizableArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneResizableArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableArrayList<E, EC> =
//        KoneResizableArrayList(size, elementContext, initializer)
//}
//
//internal class KoneResizableArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneResizableArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneResizableArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableArrayList<E, EC> =
//        KoneResizableArrayList(elementList.size, elementContext) { elementList[it] }
//}
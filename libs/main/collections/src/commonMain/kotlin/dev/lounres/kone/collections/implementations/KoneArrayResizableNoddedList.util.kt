/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


//public fun <Element> KoneArrayResizableNoddedList(): KoneArrayResizableList<Element> =
//    KoneArrayResizableList(size = 0u)
//
//public inline fun <Element> KoneArrayResizableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableList<Element> {
//    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
//    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
//    return KoneArrayResizableList(
//        size = size,
//        dataSizeNumber = dataSizeNumber,
//        sizeUpperBound = sizeUpperBound,
//        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
//    )
//}

internal class KoneArrayResizableNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneArrayResizableNoddedListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneArrayResizableNoddedList<E, EC>>(), DeserializationStrategy<KoneArrayResizableNoddedList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneArrayResizableNoddedListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableNoddedList<E, EC> =
//        KoneArrayResizableNoddedList(size, elementContext, initializer)
//}
//
//internal class KoneArrayResizableNoddedListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneArrayResizableNoddedList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableNoddedList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneArrayResizableNoddedList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneArrayResizableNoddedList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneArrayResizableNoddedList<E, EC> =
//        KoneArrayResizableNoddedList(elementList.size, elementContext) { elementList[it] }
//}
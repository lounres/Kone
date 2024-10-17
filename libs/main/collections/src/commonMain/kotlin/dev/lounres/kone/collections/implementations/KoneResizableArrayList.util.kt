/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.DefaultKoneIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableCollectionSerializerTemplate
import dev.lounres.kone.collections.serializers.KoneIterableCollectionWithContextSerializerTemplate
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <E, EC: Equality<E>> KoneResizableArrayList(elementContext: EC): KoneResizableArrayList<E, EC> =
    KoneResizableArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableArrayList(): KoneResizableArrayList<E, Equality<E>> =
    KoneResizableArrayList(size = 0u, elementContext = defaultEquality())

public inline fun <E, EC: Equality<E>> KoneResizableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneResizableArrayList<E, EC> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public inline fun <E> KoneResizableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneResizableArrayList<E, Equality<E>> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}

internal class KoneResizableArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneResizableArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneResizableArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneResizableArrayList<E, EC>>(), DeserializationStrategy<KoneResizableArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneResizableArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableArrayList<E, EC> =
        KoneResizableArrayList(size, elementContext, initializer)
}

internal class KoneResizableArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneResizableArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneResizableArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableArrayList<E, EC> =
        KoneResizableArrayList(elementList.size, elementContext) { elementList[it] }
}
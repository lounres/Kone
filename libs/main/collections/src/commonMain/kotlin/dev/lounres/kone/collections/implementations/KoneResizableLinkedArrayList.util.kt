/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <E, EC: Equality<E>> KoneResizableLinkedArrayList(elementContext: EC): KoneResizableLinkedArrayList<E, EC> =
    KoneResizableLinkedArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableLinkedArrayList(): KoneResizableLinkedArrayList<E, Equality<E>> =
    KoneResizableLinkedArrayList(size = 0u, elementContext = defaultEquality())

public inline fun <E, EC: Equality<E>> KoneResizableLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneResizableLinkedArrayList<E, EC> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableLinkedArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext
    )
}

public inline fun <E> KoneResizableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneResizableLinkedArrayList<E, Equality<E>> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneResizableLinkedArrayList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality()
    )
}

internal class KoneResizableLinkedArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneResizableLinkedArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneResizableLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneResizableLinkedArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneResizableLinkedArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableLinkedArrayList<E, EC> =
        KoneResizableLinkedArrayList(size, elementContext, initializer)
}

internal class KoneResizableLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableLinkedArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneResizableLinkedArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneResizableLinkedArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableLinkedArrayList<E, EC> =
        KoneResizableLinkedArrayList(elementList.size, elementContext) { elementList[it] }
}
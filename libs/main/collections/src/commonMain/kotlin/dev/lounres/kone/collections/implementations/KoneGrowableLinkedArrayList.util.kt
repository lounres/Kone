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


public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
    KoneGrowableLinkedArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneGrowableLinkedArrayList(): KoneGrowableLinkedArrayList<E, Equality<E>> =
    KoneGrowableLinkedArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(initialCapacity: UInt, elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = elementContext,
    )

public fun <E> KoneGrowableLinkedArrayList(initialCapacity: UInt): KoneGrowableLinkedArrayList<E, Equality<E>> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneGrowableLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E, EC> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableLinkedArrayList<E, Equality<E>> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}

internal class KoneGrowableLinkedArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneGrowableLinkedArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneGrowableLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneGrowableLinkedArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGrowableLinkedArrayList<E, EC> =
        KoneGrowableLinkedArrayList(size, elementContext, initializer)
}

internal class KoneGrowableLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneGrowableLinkedArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneGrowableLinkedArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
        KoneGrowableLinkedArrayList(elementList.size, elementContext) { elementList[it] }
}
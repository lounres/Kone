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


public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(capacity: UInt, elementContext: EC): KoneFixedCapacityLinkedArrayList<E, EC> =
    KoneFixedCapacityLinkedArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(capacity: UInt): KoneFixedCapacityLinkedArrayList<E, Equality<E>> =
    KoneFixedCapacityLinkedArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> =
    KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, Equality<E>> =
    KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityLinkedArrayList(size: UInt, capacity: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneFixedCapacityLinkedArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityLinkedArrayList<E, Equality<E>> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityLinkedArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}

internal class KoneFixedCapacityLinkedArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityLinkedArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneFixedCapacityLinkedArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneFixedCapacityLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneFixedCapacityLinkedArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneFixedCapacityLinkedArrayList<E, EC> =
        KoneFixedCapacityLinkedArrayList(size, elementContext, initializer)
}

internal class KoneFixedCapacityLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneFixedCapacityLinkedArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityLinkedArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneFixedCapacityLinkedArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneFixedCapacityLinkedArrayList<E, EC> =
        KoneFixedCapacityLinkedArrayList(elementList.size, elementContext) { elementList[it] }
}
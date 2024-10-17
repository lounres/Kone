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


public fun <E, EC: Equality<E>> KoneFixedCapacityArrayList(capacity: UInt, elementContext: EC): KoneFixedCapacityArrayList<E, EC> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityArrayList(capacity: UInt): KoneFixedCapacityArrayList<E, Equality<E>> =
    KoneFixedCapacityArrayList(
        size = 0u,
        capacity = capacity,
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E, EC> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )

public fun <E> KoneFixedCapacityArrayList(size: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E, Equality<E>> =
    KoneFixedCapacityArrayList(
        size = size,
        capacity = size,
        data = KoneMutableArray(size) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )

public fun <E, EC: Equality<E>> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E, EC> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public fun <E> KoneFixedCapacityArrayList(size: UInt, capacity: UInt, initializer: (index: UInt) -> E): KoneFixedCapacityArrayList<E, Equality<E>> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneFixedCapacityArrayList(
        size = size,
        capacity = capacity,
        data = KoneMutableArray(capacity) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}

internal class KoneFixedCapacityArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneFixedCapacityArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneFixedCapacityArrayList<E, EC>>(), DeserializationStrategy<KoneFixedCapacityArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneFixedCapacityArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneFixedCapacityArrayList<E, EC> =
        KoneFixedCapacityArrayList(size, elementContext, initializer)
}

internal class KoneFixedCapacityArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneFixedCapacityArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneFixedCapacityArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneFixedCapacityArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneFixedCapacityArrayList<E, EC> =
        KoneFixedCapacityArrayList(elementList.size, elementContext) { elementList[it] }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.serializers.DefaultKoneIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableCollectionSerializerTemplate
import dev.lounres.kone.collections.serializers.KoneIterableCollectionWithContextSerializerTemplate
import dev.lounres.kone.collections.utils.indices
import dev.lounres.kone.collections.utils.optimizeReadOnlyList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <E, EC: Equality<E>> KoneListBackedSet(elementContext: EC, backingListFabric: (EC) -> KoneIterableList<E>): KoneListBackedSet<E, EC> =
    KoneListBackedSet(elementContext, backingListFabric(elementContext))

public inline fun <E> KoneListBackedSet(backingListFabric: (Equality<E>) -> KoneIterableList<E>): KoneListBackedSet<E, Equality<E>> =
    KoneListBackedSet(defaultEquality(), backingListFabric(defaultEquality()))

internal class KoneListBackedSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneListBackedSet<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneListBackedSetSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneListBackedSet<E, EC>>(), DeserializationStrategy<KoneListBackedSet<E, EC>> {
    override val descriptor: SerialDescriptor = KoneListBackedSetDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneListBackedSet<E, EC> =
        KoneListBackedSet(
            elementContext,
            KoneFixedCapacityArrayList(size, elementContext)
                .apply {
                    (0u..<size).forEach {
                        val element = initializer(it)
                        if (element !in this ) add(element)
                    }
                }.optimizeReadOnlyList(elementContext)
        )
}

internal class KoneListBackedSetWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneListBackedSet<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneListBackedSet",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneListBackedSet<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneListBackedSet<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneListBackedSet<E, EC> =
        KoneListBackedSet(
            elementContext,
            KoneFixedCapacityArrayList(elementList.size, elementContext)
                .apply {
                    elementList.indices.forEach {
                        val element = elementList[it]
                        if (element !in this ) add(element)
                    }
                }.optimizeReadOnlyList(elementContext)
        )
}
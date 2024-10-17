/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.serializers.DefaultKoneIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableCollectionSerializerTemplate
import dev.lounres.kone.collections.serializers.KoneIterableCollectionWithContextSerializerTemplate
import dev.lounres.kone.collections.toKoneMutableIterableList
import dev.lounres.kone.collections.utils.indices
import dev.lounres.kone.comparison.Equality
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <E, EC: Equality<E>> KoneMutableListBackedSet(elementContext: EC, backingListFabric: (EC) -> KoneMutableIterableList<E>): KoneMutableListBackedSet<E, EC> =
    KoneMutableListBackedSet(elementContext, backingListFabric(elementContext))

public fun <E, EC: Equality<E>> KoneMutableListBackedSet(elementContext: EC): KoneMutableListBackedSet<E, EC> =
    KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList(elementContext = elementContext))

internal class KoneMutableListBackedSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedSet<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneMutableListBackedSetSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneMutableListBackedSet<E, EC>>(), DeserializationStrategy<KoneMutableListBackedSet<E, EC>> {
    override val descriptor: SerialDescriptor = KoneMutableListBackedSetDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneMutableListBackedSet<E, EC> =
        KoneMutableListBackedSet(
            elementContext,
            KoneFixedCapacityArrayList(size, elementContext)
                .apply {
                    (0u..<size).forEach {
                        val element = initializer(it)
                        if (element !in this ) add(element)
                    }
                }.toKoneMutableIterableList(elementContext)
        )
}

internal class KoneMutableListBackedSetWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneMutableListBackedSet<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedSet",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneMutableListBackedSet<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneMutableListBackedSet<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneMutableListBackedSet<E, EC> =
        KoneMutableListBackedSet(
            elementContext,
            KoneFixedCapacityArrayList(elementList.size, elementContext)
                .apply {
                    elementList.indices.forEach {
                        val element = elementList[it]
                        if (element !in this ) add(element)
                    }
                }.toKoneMutableIterableList(elementContext)
        )
}
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
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <E> KoneLazyList(size: UInt, generator: (index: UInt) -> E): KoneLazyList<E, Equality<E>> =
    KoneLazyList(
        size = size,
        elementContext = defaultEquality(),
        generator = generator,
    )

internal class KoneLazyListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneLazyList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneLazyListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneLazyList<E, EC>>(), DeserializationStrategy<KoneLazyList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneLazyListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneLazyList<E, EC> =
        KoneLazyList(size, elementContext, initializer)
}

internal class KoneLazyListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneLazyList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneLazyList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneLazyList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneLazyList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneLazyList<E, EC> =
        KoneLazyList(elementList.size, elementContext) { elementList[it] }
}
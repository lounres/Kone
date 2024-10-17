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


public fun <E> KoneVirtualList(size: UInt, generator: (UInt) -> E): KoneVirtualList<E, Equality<E>> =
    KoneVirtualList(size = size, elementContext = defaultEquality(), generator = generator)

internal class KoneVirtualListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneVirtualList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneVirtualListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneVirtualList<E, EC>>(), DeserializationStrategy<KoneVirtualList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneVirtualListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneVirtualList<E, EC> =
        KoneVirtualList(size, elementContext, initializer)
}

internal class KoneVirtualListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneVirtualList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneVirtualList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneVirtualList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneVirtualList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneVirtualList<E, EC> =
        KoneVirtualList(elementList.size, elementContext) { elementList[it] }
}
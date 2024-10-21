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
import dev.lounres.kone.repeat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <E> KoneLinkedGCList(): KoneLinkedGCList<E, Equality<E>> =
    KoneLinkedGCList(elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneLinkedGCList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneLinkedGCList<E, EC> =
    KoneLinkedGCList(elementContext).apply {
        repeat(size) { add(initializer(it)) }
    }

public fun <E> KoneLinkedGCList(size: UInt, initializer: (index: UInt) -> E): KoneLinkedGCList<E, Equality<E>> =
    KoneLinkedGCList<E, _>(defaultEquality()).apply {
        for (index in 0u ..< size) add(initializer(index))
    }

internal class KoneLinkedGCListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneLinkedGCList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneLinkedGCListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneLinkedGCList<E, EC>>(), DeserializationStrategy<KoneLinkedGCList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneLinkedGCListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneLinkedGCList<E, EC> =
        KoneLinkedGCList(size, elementContext, initializer)
}

internal class KoneLinkedGCListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneLinkedGCList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneLinkedGCList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneLinkedGCList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneLinkedGCList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneLinkedGCList<E, EC> =
        KoneLinkedGCList(elementList.size, elementContext) { elementList[it] }
}
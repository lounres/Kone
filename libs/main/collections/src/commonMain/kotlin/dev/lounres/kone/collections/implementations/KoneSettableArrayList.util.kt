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


public inline fun <E, EC: Equality<E>> KoneSettableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneSettableArrayList<E, EC> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), elementContext = elementContext)

public inline fun <E> KoneSettableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneSettableArrayList<E, Equality<E>> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), elementContext = defaultEquality())

internal class KoneSettableArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneSettableArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneSettableArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneSettableArrayList<E, EC>>(), DeserializationStrategy<KoneSettableArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneSettableArrayListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneSettableArrayList<E, EC> =
        KoneSettableArrayList(size, elementContext, initializer)
}

internal class KoneSettableArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneSettableArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneSettableArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneSettableArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneSettableArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneSettableArrayList<E, EC> =
        KoneSettableArrayList(elementList.size, elementContext) { elementList[it] }
}
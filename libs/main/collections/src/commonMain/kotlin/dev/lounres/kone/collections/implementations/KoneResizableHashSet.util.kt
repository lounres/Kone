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
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <E, EC: Hashing<E>> KoneResizableHashSet(elementContext: EC): KoneResizableHashSet<E, EC> =
    KoneResizableHashSet(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableHashSet(): KoneResizableHashSet<E, Hashing<E>> =
    KoneResizableHashSet(size = 0u, elementContext = defaultHashing())

internal class KoneResizableHashSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneResizableHashSet<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneResizableHashSetSerializer<E, EC: Hashing<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializerTemplate<E, KoneResizableHashSet<E, EC>>(), DeserializationStrategy<KoneResizableHashSet<E, EC>> {
    override val descriptor: SerialDescriptor = KoneResizableHashSetDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableHashSet<E, EC> =
        KoneResizableHashSet(elementContext).apply {
            (0u..<size).forEach { add(initializer(it)) }
        }
}

internal class KoneResizableHashSetWithContextSerializer<E, EC: Hashing<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableHashSet<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableHashSet",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneResizableHashSet<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneResizableHashSet<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableHashSet<E, EC> =
        KoneResizableHashSet(elementContext).apply {
            addAllFrom(elementList)
        }
}
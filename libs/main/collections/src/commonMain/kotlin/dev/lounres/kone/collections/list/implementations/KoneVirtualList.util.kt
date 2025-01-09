/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


internal class KoneVirtualListDescriptor(elementDescriptor: SerialDescriptor):
    KoneListImplementationDescriptor(
        implementationName = "KoneVirtualList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneVirtualListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneVirtualList<E>>(), DeserializationStrategy<KoneVirtualList<E>> {
    override val descriptor: SerialDescriptor = KoneVirtualListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneVirtualList<E> =
        KoneVirtualList(size, initializer)
}
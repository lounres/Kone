/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneGrowableMutableList
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableLinkedList(): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(size = 0u)

public fun <Element> KoneArrayGrowableLinkedList(initialCapacity: UInt): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneArrayGrowableLinkedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public fun <Element> KoneArrayGrowableLinkedList(initialCapacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    require(size <= initialCapacity) { "Provided initial capacity must not be less than provided size" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayGrowableLinkedListProducer : KoneGrowableMutableListProducer {
    override fun <E> produce(initialCapacity: UInt): KoneArrayGrowableLinkedList<E> = KoneArrayGrowableLinkedList(initialCapacity)
    override fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E> =
        KoneArrayGrowableLinkedList(initialCapacity, number, builder)
}

internal class KoneArrayGrowableLinkedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneListImplementationDescriptor(
        implementationName = "KoneArrayGrowableLinkedList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArrayGrowableLinkedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayGrowableLinkedList<E>>(), DeserializationStrategy<KoneArrayGrowableLinkedList<E>> {
    override val descriptor: SerialDescriptor = KoneArrayGrowableLinkedListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayGrowableLinkedList<E> =
        KoneArrayGrowableLinkedList(size, initializer)
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableSerializerTemplate
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableNoddedList(): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(size = 0u)

public fun <Element> KoneArrayGrowableNoddedList(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public inline fun <Element> KoneArrayGrowableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

public inline fun <Element> KoneArrayGrowableNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(capacity)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

public object KoneArrayGrowableNoddedListProducer : KoneGrowableMutableNoddedListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList(size = number, capacity = initialCapacity, initializer = builder)
}

internal class KoneArrayGrowableNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneIterableDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayGrowableNoddedList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArrayGrowableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayGrowableNoddedList<E>>(), DeserializationStrategy<KoneArrayGrowableNoddedList<E>> {
    override val descriptor: SerialDescriptor = KoneArrayGrowableNoddedListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayGrowableNoddedList<E> =
        KoneArrayGrowableNoddedList(size, initializer)
}
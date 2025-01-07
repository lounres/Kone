/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableSerializerTemplate
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <Element> KoneArrayResizableNoddedList(): KoneArrayResizableNoddedList<Element> =
    KoneArrayResizableNoddedList(size = 0u)

public inline fun <Element> KoneArrayResizableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableNoddedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableNoddedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayResizableNoddedList.Node(initializer(it), it) else null },
    )
}

public object KoneArrayResizableNoddedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneArrayResizableNoddedList<Element> = KoneArrayResizableNoddedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArrayResizableNoddedList<Element> =
        KoneArrayResizableNoddedList(size = number, initializer = builder)
}

internal class KoneArrayResizableNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneIterableDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableNoddedList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArrayResizableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableNoddedList<E>>(), DeserializationStrategy<KoneArrayResizableNoddedList<E>> {
    override val descriptor: SerialDescriptor = KoneArrayResizableNoddedListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableNoddedList<E> =
        KoneArrayResizableNoddedList(size, initializer)
}
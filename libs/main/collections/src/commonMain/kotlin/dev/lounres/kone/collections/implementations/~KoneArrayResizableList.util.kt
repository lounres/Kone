/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableSerializerTemplate
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <Element> KoneArrayResizableList(): KoneArrayResizableList<Element> =
    KoneArrayResizableList(size = 0u)

public inline fun <Element> KoneArrayResizableList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayResizableListProducer : KoneResizableMutableListProducer {
    override fun <E> produce(): KoneArrayResizableList<E> = KoneArrayResizableList()
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList(number, builder)
}

internal class KoneArrayResizableListDescriptor(elementDescriptor: SerialDescriptor):
    KoneIterableDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayResizableList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArrayResizableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableList<E>>(), DeserializationStrategy<KoneArrayResizableList<E>> {
    override val descriptor: SerialDescriptor = KoneArrayResizableListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList(size, initializer)
}
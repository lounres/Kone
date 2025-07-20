/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
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

internal object KoneArrayResizableListProducer : KoneResizableMutableListProducer {
    override fun <E> produce(): KoneArrayResizableList<E> = KoneArrayResizableList()
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList(number, builder)
}

public fun KoneArrayResizableList.Companion.producer(): KoneResizableMutableListProducer = KoneArrayResizableListProducer

internal class KoneArrayResizableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayResizableList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList(size, initializer)
}
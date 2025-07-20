/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneGrowableMutableList
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableList(): KoneArrayGrowableList<Element> =
    KoneArrayGrowableList(size = 0u)

public fun <Element> KoneArrayGrowableList(initialCapacity: UInt): KoneArrayGrowableList<Element> =
    KoneArrayGrowableList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public inline fun <Element> KoneArrayGrowableList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public inline fun <Element> KoneArrayGrowableList(initialCapacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableList<Element> {
    require(size <= initialCapacity) { "Provided initial capacity must not be less than provided size" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity)
    return KoneArrayGrowableList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

internal object KoneArrayGrowableListProducer : KoneGrowableMutableListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableList<Element> = KoneArrayGrowableList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element> =
        KoneArrayGrowableList(initialCapacity, number, builder)
}

public fun KoneArrayGrowableList.Companion.producer(): KoneGrowableMutableListProducer = KoneArrayGrowableListProducer

internal class KoneArrayGrowableListSerializer<Element>(
    override val elementSerializer: KSerializer<Element>,
): KoneIterableSerializerTemplate<Element, KoneArrayGrowableList<Element>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayGrowableList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneArrayGrowableList<Element> =
        KoneArrayGrowableList(size, initializer)
}
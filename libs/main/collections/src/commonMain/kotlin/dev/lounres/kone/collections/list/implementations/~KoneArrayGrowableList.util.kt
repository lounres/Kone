/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializationStrategyTemplate
import dev.lounres.kone.collections.list.KoneGrowableMutableList
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure


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

public object KoneArrayGrowableListProducer : KoneGrowableMutableListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableList<Element> = KoneArrayGrowableList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element> =
        KoneArrayGrowableList(initialCapacity, number, builder)
}

internal class KoneArrayGrowableListDescriptor(elementDescriptor: SerialDescriptor):
    KoneListImplementationDescriptor(
        implementationName = "KoneArrayGrowableList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArrayGrowableListSerializer<Element>(
    override val elementSerializer: KSerializer<Element>,
): KoneIterableSerializationStrategyTemplate<Element, KoneArrayGrowableList<Element>>(), KSerializer<KoneArrayGrowableList<Element>> {
    override val descriptor: SerialDescriptor = KoneArrayGrowableListDescriptor(elementSerializer.descriptor)

    override fun deserialize(decoder: Decoder): KoneArrayGrowableList<Element> =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                KoneArrayGrowableList(size.toUInt()) {
                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
                }
            } else {
                val builder = KoneArrayGrowableList<Element>()
                while (true) {
                    val index = decodeElementIndex(descriptor)
                    if (index == CompositeDecoder.DECODE_DONE) break
                    builder.add(decodeSerializableElement(descriptor, index, elementSerializer))
                }
                builder
            }
        }
}
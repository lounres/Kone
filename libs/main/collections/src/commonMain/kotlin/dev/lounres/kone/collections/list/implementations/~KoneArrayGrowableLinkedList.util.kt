/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneGrowableMutableList
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableLinkedList(): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(size = 0u)

public fun <Element> KoneArrayGrowableLinkedList(initialCapacity: UInt): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(
        size = 0u,
        sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneArrayGrowableLinkedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    val sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(size)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public fun <Element> KoneArrayGrowableLinkedList.Companion.generate(initialCapacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    require(size <= initialCapacity) { "Provided initial capacity must not be less than provided size" }
    val sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(initialCapacity)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

internal object KoneArrayGrowableLinkedListProducer : KoneGrowableMutableListProducer {
    override fun <E> produce(initialCapacity: UInt): KoneArrayGrowableLinkedList<E> = KoneArrayGrowableLinkedList(initialCapacity)
    override fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E> =
        KoneArrayGrowableLinkedList.generate(initialCapacity, number, builder)
}

public fun KoneArrayGrowableLinkedList.Companion.producer(): KoneGrowableMutableListProducer = KoneArrayGrowableLinkedListProducer

internal class KoneArrayGrowableLinkedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayGrowableLinkedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayGrowableLinkedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayGrowableLinkedList<E> =
        KoneArrayGrowableLinkedList.generate(size, initializer)
}
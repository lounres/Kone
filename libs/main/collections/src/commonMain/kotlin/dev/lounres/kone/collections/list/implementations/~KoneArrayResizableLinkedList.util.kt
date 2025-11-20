/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayResizableLinkedList(): KoneArrayResizableLinkedList<Element> =
    KoneArrayResizableLinkedList(size = 0u)

public inline fun <Element> KoneArrayResizableLinkedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableLinkedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableLinkedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

internal object KoneArrayResizableLinkedListProducer : KoneResizableMutableListProducer {
    override fun <Element> produce(): KoneArrayResizableLinkedList<Element> = KoneArrayResizableLinkedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArrayResizableLinkedList<Element> =
        KoneArrayResizableLinkedList.generate(number, builder)
}

public fun KoneArrayResizableLinkedList.Companion.producer(): KoneResizableMutableListProducer = KoneArrayResizableLinkedListProducer

internal class KoneArrayResizableLinkedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableLinkedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayResizableLinkedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableLinkedList<E> =
        KoneArrayResizableLinkedList.generate(size, initializer)
}
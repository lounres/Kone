/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.math.max


public fun <Element> KoneArrayResizableLinkedNoddedList(): KoneArrayResizableLinkedNoddedList<Element> =
    KoneArrayResizableLinkedNoddedList(size = 0u)

public inline fun <Element> KoneArrayResizableLinkedNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableLinkedNoddedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(max(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableLinkedNoddedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayResizableLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

@PublishedApi
internal object KoneArrayResizableLinkedNoddedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneArrayResizableLinkedNoddedList<Element> = KoneArrayResizableLinkedNoddedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArrayResizableLinkedNoddedList<Element> =
        KoneArrayResizableLinkedNoddedList(number, builder)
}

public fun KoneArrayResizableLinkedNoddedList.Companion.producer(): KoneResizableMutableNoddedListProducer = KoneArrayResizableLinkedNoddedListProducer

internal class KoneArrayResizableLinkedNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableLinkedNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayResizableLinkedNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableLinkedNoddedList<E> =
        KoneArrayResizableLinkedNoddedList(size, initializer)
}
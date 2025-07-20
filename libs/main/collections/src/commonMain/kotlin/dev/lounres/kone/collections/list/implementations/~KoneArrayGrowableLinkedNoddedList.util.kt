/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.implementations.powerOf2GreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableLinkedNoddedList(): KoneArrayGrowableLinkedNoddedList<Element> =
    KoneArrayGrowableLinkedNoddedList(size = 0u)

public fun <Element> KoneArrayGrowableLinkedNoddedList(initialCapacity: UInt): KoneArrayGrowableLinkedNoddedList<Element> =
    KoneArrayGrowableLinkedNoddedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneArrayGrowableLinkedNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableLinkedNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

public fun <Element> KoneArrayGrowableLinkedNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> {
    require(size <= capacity) { "Cannot initialize KoneFixedCapacityArrayList with size $size and capacity $capacity, because size is greater than capacity" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(capacity)
    return KoneArrayGrowableLinkedNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableLinkedNoddedList.Node(initializer(it), it) else null },
    )
}

@PublishedApi
internal object KoneArrayGrowableLinkedNoddedListProducer : KoneGrowableMutableNoddedListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableLinkedNoddedList<Element> =
        KoneArrayGrowableLinkedNoddedList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayGrowableLinkedNoddedList<Element> =
        KoneArrayGrowableLinkedNoddedList(size = number, capacity = initialCapacity, initializer = builder)
}

public fun KoneArrayGrowableLinkedNoddedList.Companion.producer(): KoneGrowableMutableNoddedListProducer = KoneArrayGrowableLinkedNoddedListProducer

internal class KoneArrayGrowableLinkedNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayGrowableLinkedNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayGrowableLinkedNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayGrowableLinkedNoddedList<E> =
        KoneArrayGrowableLinkedNoddedList(size, initializer)
}
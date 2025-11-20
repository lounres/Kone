/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableNoddedList(): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(size = 0u)

public fun <Element> KoneArrayGrowableNoddedList(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(
        size = 0u,
        sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(initialCapacity),
    )

public inline fun <Element> KoneArrayGrowableNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(size)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

public inline fun <Element> KoneArrayGrowableNoddedList.Companion.generate(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(capacity)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

internal object KoneArrayGrowableNoddedListProducer : KoneGrowableMutableNoddedListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList.generate(size = number, capacity = initialCapacity, initializer = builder)
}

public fun KoneArrayGrowableNoddedList.Companion.producer(): KoneGrowableMutableNoddedListProducer = KoneArrayGrowableNoddedListProducer

internal class KoneArrayGrowableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayGrowableNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayGrowableNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayGrowableNoddedList<E> =
        KoneArrayGrowableNoddedList.generate(size, initializer)
}
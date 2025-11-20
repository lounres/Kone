/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayResizableNoddedList(): KoneArrayResizableNoddedList<Element> =
    KoneArrayResizableNoddedList(size = 0u)

public inline fun <Element> KoneArrayResizableNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableNoddedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableNoddedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) KoneArrayResizableNoddedList.Node(initializer(it), it) else null },
    )
}

public inline fun <Element> KoneArrayResizableNoddedList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArrayResizableNoddedList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    var current = initialElement
    return KoneArrayResizableNoddedList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) {
            when {
                it == 0u -> KoneArrayResizableNoddedList.Node(current, it)
                it < size -> KoneArrayResizableNoddedList.Node(inducer(it, current).also { current = it }, it)
                else -> null
            }
        },
    )
}

internal object KoneArrayResizableNoddedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneArrayResizableNoddedList<Element> = KoneArrayResizableNoddedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArrayResizableNoddedList<Element> =
        KoneArrayResizableNoddedList.generate(size = number, initializer = builder)
}

public fun KoneArrayResizableNoddedList.Companion.producer(): KoneResizableMutableNoddedListProducer = KoneArrayResizableNoddedListProducer

internal class KoneArrayResizableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayResizableNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayResizableNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayResizableNoddedList<E> =
        KoneArrayResizableNoddedList.generate(size, initializer)
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.implementations.powerOf2IndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterable.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayResizableList(): KoneArrayResizableList<Element> =
    KoneArrayResizableList(size = 0u)

public inline fun <Element> KoneArrayResizableList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayResizableList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public inline fun <Element> KoneArrayResizableList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneArrayResizableList<Element> {
    val size = indices.last - indices.first + 1u
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) { if (it < size) initializer(it + indices.first) else null },
    )
}

public inline fun <Element> KoneArrayResizableList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArrayResizableList<Element> {
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    var current = initialElement
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) {
            when {
                it == 0u -> current
                it < size -> inducer(it, current).also { current = it }
                else -> null
            }
        },
    )
}

public inline fun <Element> KoneArrayResizableList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArrayResizableList<Element> {
    val size = indices.last - indices.first + 1u
    val dataSizeNumber = powerOf2IndexGreaterOrEqualTo(maxOf(size, 2u)) - 1u
    val sizeUpperBound = POWERS_OF_2[dataSizeNumber + 1u]
    var current = initialElement
    return KoneArrayResizableList(
        size = size,
        dataSizeNumber = dataSizeNumber,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray.generate(sizeUpperBound) {
            when {
                it == 0u -> current
                it < size -> inducer(it + indices.first, current).also { current = it }
                else -> null
            }
        },
    )
}

internal object KoneArrayResizableListProducer : KoneResizableMutableListProducer {
    override fun <E> produce(): KoneArrayResizableList<E> = KoneArrayResizableList()
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneArrayResizableList<E> =
        KoneArrayResizableList.generate(number, builder)
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
        KoneArrayResizableList.generate(size, initializer)
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


/**
 * Returns an empty [KoneArrayFixedCapacityList] of provided [capacity].
 */
public fun <Element> KoneArrayFixedCapacityList(capacity: UInt): KoneArrayFixedCapacityList<Element> =
    KoneArrayFixedCapacityList(
        size = 0u,
        data = KoneMutableArray.generate(capacity) { null }
    )

/**
 * Returns a [KoneArrayFixedCapacityList] of provided [size] (and equal capacity) of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneArrayFixedCapacityList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityList<Element> =
    KoneArrayFixedCapacityList(
        size = size,
        data = KoneMutableArray.generate(size) { if (it < size) initializer(it) else null },
    )

/**
 * Returns a [KoneArrayFixedCapacityList] of provided [size] and [capacity] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneArrayFixedCapacityList.Companion.generate(capacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayFixedCapacityList<Element> {
    require(size <= capacity) { "Cannot initialize KoneArrayFixedCapacityList with size $size and capacity $capacity, because size is greater than capacity" }
    return KoneArrayFixedCapacityList(
        size = size,
        data = KoneMutableArray.generate(capacity) { if (it < size) initializer(it) else null },
    )
}

public inline fun <Element> KoneArrayFixedCapacityList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArrayFixedCapacityList<Element> {
    var current = initialElement
    return KoneArrayFixedCapacityList(
        size = size,
        data = KoneMutableArray.generate(size) {
            when {
                it == 0u -> current
                it < size -> inducer(it, current).also { current = it }
                else -> null
            }
        },
    )
}

public inline fun <Element> KoneArrayFixedCapacityList.Companion.induce(capacity: UInt, size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArrayFixedCapacityList<Element> {
    require(size <= capacity) { "Cannot initialize KoneArrayFixedCapacityList with size $size and capacity $capacity, because size is greater than capacity" }
    var current = initialElement
    return KoneArrayFixedCapacityList(
        size = size,
        data = KoneMutableArray.generate(capacity) {
            when {
                it == 0u -> current
                it < size -> inducer(it, current).also { current = it }
                else -> null
            }
        },
    )
}

/**
 * Producer of [KoneArrayFixedCapacityList].
 */
internal object KoneArrayFixedCapacityListProducer : KoneFixedCapacityMutableListProducer {
    override fun <Element> produce(capacity: UInt): KoneArrayFixedCapacityList<Element> = KoneArrayFixedCapacityList(capacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableList<Element> =
        KoneArrayFixedCapacityList.generate(initialCapacity, number, builder)
}

public fun KoneArrayFixedCapacityList.Companion.producer(): KoneFixedCapacityMutableListProducer = KoneArrayFixedCapacityListProducer

internal class KoneArrayFixedCapacityListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArrayFixedCapacityList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArrayFixedCapacityList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArrayFixedCapacityList<E> =
        KoneArrayFixedCapacityList.generate(size, initializer)
}
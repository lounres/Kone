/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneSettableListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


/**
 * Returns a [KoneArraySettableList] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneArraySettableList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableList<Element> =
    KoneArraySettableList(KoneMutableArray.generate(size, initializer))

public inline fun <Element> KoneArraySettableList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneArraySettableList<Element> =
    KoneArraySettableList(KoneMutableArray.generate(indices, initializer))

public inline fun <Element> KoneArraySettableList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArraySettableList<Element> {
    if (size == 0u) return KoneArraySettableList(KoneMutableArray.generate(0u) { error("Unreachable") })
    var current = initialElement
    return KoneArraySettableList(KoneMutableArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } })
}

public inline fun <Element> KoneArraySettableList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArraySettableList<Element> {
    if (indices.isEmpty()) return KoneArraySettableList(KoneMutableArray.generate(0u) { error("Unreachable") })
    var current = initialElement
    return KoneArraySettableList(KoneMutableArray.generate(indices.last - indices.first + 1u) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } })
}

/**
 * Producer of [KoneArraySettableList].
 */
internal object KoneArraySettableListProducer : KoneSettableListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableList<Element> = KoneArraySettableList.generate(number, builder)
}

public fun KoneArraySettableList.Companion.producer(): KoneSettableListProducer = KoneArraySettableListProducer

internal class KoneArraySettableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArraySettableList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArraySettableList",
            elementDescriptor = elementSerializer.descriptor,
        )
    
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArraySettableList<E> =
        KoneArraySettableList.generate(size, initializer)
}
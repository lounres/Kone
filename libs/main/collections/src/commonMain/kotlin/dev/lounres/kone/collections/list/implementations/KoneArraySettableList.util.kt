/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.producers.KoneSettableListProducer
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
public inline fun <Element> KoneArraySettableList(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableList<Element> =
    KoneArraySettableList(KoneMutableArray(size, initializer))

/**
 * Producer of [KoneArraySettableList].
 */
public object KoneArraySettableListProducer : KoneSettableListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableList<Element> = KoneArraySettableList(number, builder)
}

internal class KoneArraySettableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArraySettableList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArraySettableList",
            elementSerializer = elementSerializer,
        )
    
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArraySettableList<E> =
        KoneArraySettableList(size, initializer)
}
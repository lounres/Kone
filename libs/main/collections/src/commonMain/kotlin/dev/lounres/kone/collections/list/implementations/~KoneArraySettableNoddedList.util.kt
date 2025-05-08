/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.producers.KoneSettableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <Element> KoneArraySettableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableNoddedList<Element> =
    KoneArraySettableNoddedList(KoneMutableArray(size) { KoneArraySettableNoddedList.Node(initializer(it), it) })

internal object KoneArraySettableNoddedListProducer : KoneSettableNoddedListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableNoddedList<Element> = KoneArraySettableNoddedList(number, builder)
}

public fun KoneArraySettableNoddedList.Companion.producer(): KoneSettableNoddedListProducer = KoneArraySettableNoddedListProducer

internal class KoneArraySettableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArraySettableNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArraySettableNoddedList",
            elementSerializer = elementSerializer
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArraySettableNoddedList<E> =
        KoneArraySettableNoddedList(size, initializer)
}
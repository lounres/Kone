/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneSettableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableSerializerTemplate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <Element> KoneArraySettableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableNoddedList<Element> =
    KoneArraySettableNoddedList(KoneMutableArray(size) { KoneArraySettableNoddedList.Node(initializer(it), it) })

public object KoneArraySettableNoddedListProducer : KoneSettableNoddedListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableNoddedList<Element> = KoneArraySettableNoddedList(number, builder)
}

internal class KoneArraySettableNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneIterableDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArraySettableNoddedList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneArraySettableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArraySettableNoddedList<E>>() {
    override val descriptor: SerialDescriptor = KoneArraySettableNoddedListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArraySettableNoddedList<E> =
        KoneArraySettableNoddedList(size, initializer)
}
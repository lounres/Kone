/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterable.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneSettableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <Element> KoneArraySettableNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableNoddedList<Element> =
    KoneArraySettableNoddedList(KoneMutableArray.generate(size) { KoneArraySettableNoddedList.Node(initializer(it), it) })

public inline fun <Element> KoneArraySettableNoddedList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneArraySettableNoddedList<Element> =
    KoneArraySettableNoddedList(KoneMutableArray.generate(indices.last + 1u - indices.first) { KoneArraySettableNoddedList.Node(initializer(it), it + indices.first) })

public inline fun <Element> KoneArraySettableNoddedList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArraySettableNoddedList<Element> {
    var current = initialElement
    return KoneArraySettableNoddedList(KoneMutableArray.generate(size) { if (it == 0u) KoneArraySettableNoddedList.Node(current, it) else KoneArraySettableNoddedList.Node(inducer(it, current).also { current = it }, it) })
}

public inline fun <Element> KoneArraySettableNoddedList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArraySettableNoddedList<Element> {
    var current = initialElement
    return KoneArraySettableNoddedList(
        KoneMutableArray.generate(indices.last + 1u - indices.first) {
            if (it == 0u) KoneArraySettableNoddedList.Node(current, it)
            else KoneArraySettableNoddedList.Node(inducer(it + indices.first, current).also { new -> current = new }, it)
        }
    )
}

internal object KoneArraySettableNoddedListProducer : KoneSettableNoddedListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableNoddedList<Element> = KoneArraySettableNoddedList.generate(number, builder)
}

public fun KoneArraySettableNoddedList.Companion.producer(): KoneSettableNoddedListProducer = KoneArraySettableNoddedListProducer

internal class KoneArraySettableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneArraySettableNoddedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneArraySettableNoddedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneArraySettableNoddedList<E> =
        KoneArraySettableNoddedList.generate(size, initializer)
}
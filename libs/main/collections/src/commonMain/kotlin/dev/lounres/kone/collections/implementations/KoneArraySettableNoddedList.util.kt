/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneSettableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <Element> KoneArraySettableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArraySettableNoddedList<Element> =
    KoneArraySettableNoddedList(KoneMutableArray(size, initializer))

public object KoneArraySettableNoddedListProducer : KoneSettableNoddedListProducer {
    override fun <Element> produce(): KoneArraySettableNoddedList<Element> = KoneArraySettableNoddedList(0u) { error("For some reason throwing builder was called") }
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneArraySettableNoddedList<Element> = KoneArraySettableNoddedList(number, builder)
}

internal class KoneArraySettableNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArraySettableNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneListProducer
import dev.lounres.kone.collections.producers.KoneSettableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public inline fun <Element> KoneSettableArrayNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableArrayNoddedList<Element> =
    KoneSettableArrayNoddedList(KoneMutableArray(size, initializer))

public object KoneSettableArrayNoddedListProducer : KoneSettableNoddedListProducer {
    override fun <Element> produce(): KoneSettableArrayNoddedList<Element> = KoneSettableArrayNoddedList(0u) { error("For some reason throwing builder was called") }
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneSettableArrayNoddedList<Element> = KoneSettableArrayNoddedList(number, builder)
}

internal class KoneSettableArrayNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneSettableArrayNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )
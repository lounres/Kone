/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.producers

import dev.lounres.kone.collections.utils.component1
import dev.lounres.kone.collections.utils.component2
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.MDList2Wrapper
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2Wrapper
import dev.lounres.kone.multidimensionalCollections.of
import kotlin.jvm.JvmInline


public interface MDList2Producer {
    public fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (row: UInt, column: UInt) -> Element
    ): MDList2<Element>
}

public interface SettableMDList2Producer : MDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (row: UInt, column: UInt) -> Element
    ): SettableMDList2<Element>
}

@JvmInline
internal value class MDList2ProducerWrapper(val producer: MDListProducer) : MDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (row: UInt, column: UInt) -> Element
    ): MDList2<Element> =
        MDList2Wrapper(producer.produceBy(MDSize.of(rowNumber, columnNumber)) { [row, column] -> initializer(row, column) })
}

@JvmInline
internal value class SettableMDList2ProducerWrapper(val producer: SettableMDListProducer) : SettableMDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (row: UInt, column: UInt) -> Element
    ): SettableMDList2<Element> =
        SettableMDList2Wrapper(producer.produceBy(MDSize.of(rowNumber, columnNumber)) { [row, column] -> initializer(row, column) })
}

public fun MDListProducer.as2D(): MDList2Producer = MDList2ProducerWrapper(this)
public fun SettableMDListProducer.as2D(): SettableMDList2Producer = SettableMDList2ProducerWrapper(this)
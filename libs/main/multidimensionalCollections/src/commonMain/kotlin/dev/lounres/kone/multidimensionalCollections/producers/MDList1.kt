/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.producers

import dev.lounres.kone.collections.utils.component1
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList1Wrapper
import dev.lounres.kone.multidimensionalCollections.MDShape
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1Wrapper
import kotlin.jvm.JvmInline


public interface MDList1Producer {
    public fun <Element> produceBy(
        size: UInt,
        initializer: (index: UInt) -> Element
    ): MDList1<Element>
}

public interface SettableMDList1Producer : MDList1Producer {
    override fun <Element> produceBy(
        size: UInt,
        initializer: (index: UInt) -> Element
    ): SettableMDList1<Element>
}

@JvmInline
internal value class MDList1ProducerWrapper(val producer: MDListProducer) : MDList1Producer {
    override fun <Element> produceBy(size: UInt, initializer: (UInt) -> Element): MDList1<Element> =
        MDList1Wrapper(producer.produceBy(MDShape(size)) { (index) -> initializer(index) })
}

@JvmInline
internal value class SettableMDList1ProducerWrapper(val producer: SettableMDListProducer) : SettableMDList1Producer {
    override fun <Element> produceBy(size: UInt, initializer: (UInt) -> Element): SettableMDList1<Element> =
        SettableMDList1Wrapper(producer.produceBy(MDShape(size)) { (index) -> initializer(index) })
}

public fun MDListProducer.as1D(): MDList1Producer = MDList1ProducerWrapper(this)
public fun SettableMDListProducer.as1D(): SettableMDList1Producer = SettableMDList1ProducerWrapper(this)

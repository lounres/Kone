/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.MDShape
import dev.lounres.kone.multidimensionalCollections.MDShapeOffsetting
import dev.lounres.kone.multidimensionalCollections.producers.MDList1Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDList2Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDListProducer


public object LazyArrayMDListProducer : MDListProducer {
    override fun <Element> produceBy(
        shape: MDShape,
        offsetting: MDShapeOffsetting,
        initializer: (KoneUIntArray) -> Element
    ): MDList<Element> = LazyArrayMDList(shape, offsetting, initializer)
}

public object LazyArrayMDList1Producer : MDList1Producer {
    override fun <Element> produceBy(size: UInt, initializer: (UInt) -> Element): MDList1<Element> =
        LazyArrayMDList1(size, initializer)
}

public object LazyArrayMDList2Producer : MDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (UInt, UInt) -> Element
    ): MDList2<Element> = LazyArrayMDList2(rowNumber, columnNumber, initializer)
}
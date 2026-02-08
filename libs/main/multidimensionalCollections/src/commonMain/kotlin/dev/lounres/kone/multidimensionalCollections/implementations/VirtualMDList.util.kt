/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.MDSizeOffsetting
import dev.lounres.kone.multidimensionalCollections.producers.MDList1Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDList2Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDListProducer


public object VirtualMDListProducer : MDListProducer {
    override fun <Element> produceBy(
        size: MDSize,
        offsetting: MDSizeOffsetting,
        initializer: (MDIndex) -> Element
    ): MDList<Element> = VirtualMDList(size, initializer)
}

public object VirtualMDList1Producer : MDList1Producer {
    override fun <Element> produceBy(size: UInt, initializer: (UInt) -> Element): MDList1<Element> =
        VirtualMDList1(size, initializer)
}

public object VirtualMDList2Producer : MDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (UInt, UInt) -> Element
    ): MDList2<Element> = VirtualMDList2(rowNumber, columnNumber, initializer)
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.producers

import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.SettableMDList
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.MDSizeOffsetting
import dev.lounres.kone.multidimensionalCollections.MDSizeStrides


public interface MDListProducer {
    public fun <Element> produceBy(
        size: MDSize,
        offsetting: MDSizeOffsetting = MDSizeStrides(size),
        initializer: (index: MDIndex) -> Element
    ): MDList<Element>
}

public interface SettableMDListProducer : MDListProducer {
    override fun <Element> produceBy(
        size: MDSize,
        offsetting: MDSizeOffsetting,
        initializer: (index: MDIndex) -> Element
    ): SettableMDList<Element>
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.producers

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.SettableMDList
import dev.lounres.kone.multidimensionalCollections.MDShape
import dev.lounres.kone.multidimensionalCollections.MDShapeOffsetting
import dev.lounres.kone.multidimensionalCollections.MDShapeStrides


public interface MDListProducer {
    public fun <Element> produceBy(
        shape: MDShape,
        offsetting: MDShapeOffsetting = MDShapeStrides(shape),
        initializer: (index: KoneUIntArray) -> Element
    ): MDList<Element>
}

public interface SettableMDListProducer : MDListProducer {
    override fun <Element> produceBy(
        shape: MDShape,
        offsetting: MDShapeOffsetting,
        initializer: (index: KoneUIntArray) -> Element
    ): SettableMDList<Element>
}
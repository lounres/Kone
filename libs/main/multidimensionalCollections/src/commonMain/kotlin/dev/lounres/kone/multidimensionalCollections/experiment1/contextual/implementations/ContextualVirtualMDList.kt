/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDListTransformer


public class ContextualVirtualMDList<E>(
    override val shape: Shape,
    private val generator: (index: KoneUIntArray) -> E
): ContextualMDList<E, Equality<E>> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: KoneUIntArray): E = generator(index)
}

public class ContextualVirtualMDList1<E>(
    override val size: UInt,
    private val generator: (index: UInt) -> E
): ContextualMDList1<E, Equality<E>> {
    override fun get(index: UInt): E = generator(index)
}

public class ContextualVirtualMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): ContextualMDList2<E, Equality<E>> {
    override fun get(rowIndex: UInt, columnIndex: UInt): E = generator(rowIndex, columnIndex)
}

public class ContextualVirtualMDListTransformer<E>: ContextualMDListTransformer<E, Equality<E>> {
    override fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualVirtualMDList<E> =
        ContextualVirtualMDList(shape = shape, generator = initializer)
    override fun mdList1(size: UInt, initializer: (index: UInt) -> E): ContextualVirtualMDList1<E> =
        ContextualVirtualMDList1(size = size, generator = initializer)
    override fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualVirtualMDList2<E> =
        ContextualVirtualMDList2(rowNumber = rowNumber, columnNumber = columnNumber, generator = initializer)

    override fun hashCode(): Int = 820311443 // Just a random `Int`eger
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ContextualVirtualMDListTransformer<*>
    }
    override fun toString(): String = "VirtualMDListTransformer"
}
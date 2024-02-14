/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.implementations

import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.experiment1.*


public class VirtualMDList<E>(
    override val shape: Shape,
    private val generator: (index: UIntArray) -> E
): MDList<E> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: UIntArray): E = generator(index)
}

public class VirtualMDList1<E>(
    override val size: UInt,
    private val generator: (index: UInt) -> E
): MDList1<E> {
    override fun get(index: UInt): E = generator(index)
}

public class VirtualMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): MDList2<E> {
    override fun get(rowIndex: UInt, columnIndex: UInt): E = generator(rowIndex, columnIndex)
}

public data object VirtualMDListTransformer: MDListTransformer {
    override fun <E> mdList(shape: Shape, initializer: (index: UIntArray) -> E): VirtualMDList<E> =
        VirtualMDList(shape = shape, generator = initializer)
    override fun <E> mdList1(size: UInt, initializer: (index: UInt) -> E): VirtualMDList1<E> =
        VirtualMDList1(size = size, generator = initializer)
    override fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): VirtualMDList2<E> =
        VirtualMDList2(rowNumber = rowNumber, columnNumber = columnNumber, generator = initializer)
}
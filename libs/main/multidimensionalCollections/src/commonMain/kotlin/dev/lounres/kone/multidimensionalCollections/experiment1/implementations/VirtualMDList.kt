/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.implementations

import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList2


public class VirtualMDList<E>(
    override val shape: Shape,
    private val generator: (index: KoneUIntArray) -> E
): MDList<E> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: KoneUIntArray): E = generator(index)
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
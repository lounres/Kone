/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.common.implementations

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.experiment1.common.MDList
import dev.lounres.kone.multidimensionalCollections.experiment1.common.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.common.MDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.common.MDListTransformer


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

public class VirtualMDListTransformer<E>: MDListTransformer<E> {
    override fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): VirtualMDList<E> =
        VirtualMDList(shape = shape, generator = initializer)
    override fun mdList1(size: UInt, initializer: (index: UInt) -> E): VirtualMDList1<E> =
        VirtualMDList1(size = size, generator = initializer)
    override fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): VirtualMDList2<E> =
        VirtualMDList2(rowNumber = rowNumber, columnNumber = columnNumber, generator = initializer)

    override fun hashCode(): Int = 820311443 // Just a random `Int`eger
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is VirtualMDListTransformer<*>
    }
    override fun toString(): String = "VirtualMDListTransformer"
}
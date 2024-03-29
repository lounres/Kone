/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.complex.implementations

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDListTransformer


public class VirtualMDList<E>(
    override val shape: Shape,
    override val context: Equality<E>,
    private val generator: (index: KoneUIntArray) -> E
): MDList<E> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: KoneUIntArray): E = generator(index)
}

public class VirtualMDList1<E>(
    override val size: UInt,
    override val context: Equality<E>,
    private val generator: (index: UInt) -> E
): MDList1<E> {
    override fun get(index: UInt): E = generator(index)
}

public class VirtualMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    override val context: Equality<E>,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): MDList2<E> {
    override fun get(rowIndex: UInt, columnIndex: UInt): E = generator(rowIndex, columnIndex)
}

public class VirtualMDListTransformer<E>: MDListTransformer<E> {
    override fun mdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): VirtualMDList<E> =
        VirtualMDList(shape = shape, context = context, generator = initializer)
    override fun mdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): VirtualMDList1<E> =
        VirtualMDList1(size = size, context = context, generator = initializer)
    override fun mdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): VirtualMDList2<E> =
        VirtualMDList2(rowNumber = rowNumber, columnNumber = columnNumber, context = context, generator = initializer)

    override fun hashCode(): Int = 820311443 // Just a random `Int`eger
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is VirtualMDListTransformer<*>
    }
    override fun toString(): String = "VirtualMDListTransformer"
}
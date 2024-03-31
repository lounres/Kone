/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.complex.implementations

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDListTransformer


public class VirtualMDList<E>(
    override val shape: Shape,
    private val context: Equality<E>,
    private val generator: (index: KoneUIntArray) -> E
): MDList<E> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }
    override fun contains(element: E): Boolean {
        for (index in ShapeStrides(shape)) if (context { element eq get(index) }) return true
        return false
    }

    override fun get(index: KoneUIntArray): E = generator(index)
}

public class VirtualMDList1<E>(
    override val size: UInt,
    private val context: Equality<E>,
    private val generator: (index: UInt) -> E
): MDList1<E> {
    override fun get(index: UInt): E = generator(index)
    override fun contains(element: E): Boolean {
        for (index in 0u..<size) if (context { element eq get(index) }) return true
        return false
    }
}

public class VirtualMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val context: Equality<E>,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): MDList2<E> {
    override fun get(rowIndex: UInt, columnIndex: UInt): E = generator(rowIndex, columnIndex)
    override fun contains(element: E): Boolean {
        for (row in 0u..<rowNumber) for (column in 0u..<columnNumber) if (context { element eq get(row, column) }) return true
        return false
    }
}

public data object VirtualMDListTransformer: MDListTransformer {
    override fun <E> mdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): VirtualMDList<E> =
        VirtualMDList(shape = shape, context = context, generator = initializer)
    override fun <E> mdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): VirtualMDList1<E> =
        VirtualMDList1(size = size, context = context, generator = initializer)
    override fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): VirtualMDList2<E> =
        VirtualMDList2(rowNumber = rowNumber, columnNumber = columnNumber, context = context, generator = initializer)

//    override fun hashCode(): Int = 820311443 // Just a random `Int`eger
//    override fun equals(other: Any?): Boolean = this === other
    override fun toString(): String = "VirtualMDListTransformer"
}
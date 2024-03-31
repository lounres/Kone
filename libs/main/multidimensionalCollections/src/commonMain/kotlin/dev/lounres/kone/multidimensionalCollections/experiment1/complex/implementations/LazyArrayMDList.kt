/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.complex.implementations

import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.SettableMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.SettableMDListTransformer
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


public class LazyArrayMDList<E>(
    override val shape: Shape,
    private val offsetting: ShapeOffsetting = ShapeStrides(shape),
    private val context: Equality<E>,
    private val generator: (index: KoneUIntArray) -> E
): SettableMDList<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(offsetting.linearSize) { None }

    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }
    override fun contains(element: E): Boolean {
        for (index in offsetting) if (context { element eq get(index) }) return true
        return false
    }

    override fun get(index: KoneUIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return offsetting.offset(index).let { offset -> buffer[offset].orElse { generator(index).also { buffer[offset] = Some(it) } } }
    }

    override fun set(index: KoneUIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        buffer[offsetting.offset(index)] = Some(element)
    }
}

public class LazyArrayMDList1<E>(
    override val size: UInt,
    private val context: Equality<E>,
    private val generator: (index: UInt) -> E
): SettableMDList1<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(size) { None }

    override fun contains(element: E): Boolean {
        for (index in 0u..<size) if (context { element eq get(index) }) return true
        return false
    }

    override fun get(index: UInt): E {
        if (index >= size) throw IndexOutOfShapeException(shape = shape, index = Shape(index))
        return buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) throw IndexOutOfShapeException(shape = shape, index = Shape(index))
        buffer[index] = Some(element)
    }
}

public class LazyArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val context: Equality<E>,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): SettableMDList2<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(size) { None }

    override fun contains(element: E): Boolean {
        for (row in 0u..<rowNumber) for (column in 0u..<columnNumber) if (context { element eq get(row, column) }) return true
        return false
    }

    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) throw IndexOutOfShapeException(shape = shape, index = Shape(rowIndex, columnIndex))
        val offsst = rowIndex + columnIndex * rowNumber
        return buffer[offsst].orElse { generator(rowIndex, columnIndex).also { buffer[offsst] = Some(it) } }
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) throw IndexOutOfShapeException(shape = shape, index = Shape(rowIndex, columnIndex))
        buffer[rowIndex + columnIndex * rowNumber] = Some(element)
    }
}

public data object LazyArrayMDListTransformer : SettableMDListTransformer {
    override fun <E> settableMdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): SettableMDList<E> =
        LazyArrayMDList(shape = shape, context = context, generator = initializer)
    override fun <E> settableMdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): SettableMDList1<E> =
        LazyArrayMDList1(size = size, context = context, generator = initializer)
    override fun <E> settableMdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        LazyArrayMDList2(rowNumber = rowNumber, columnNumber = columnNumber, context = context, generator = initializer)

//    override fun hashCode(): Int = 1489807662 // Just a random `Int`eger
//    override fun equals(other: Any?): Boolean = this === other
    override fun toString(): String = "LazyArrayMDListTransformer"
}
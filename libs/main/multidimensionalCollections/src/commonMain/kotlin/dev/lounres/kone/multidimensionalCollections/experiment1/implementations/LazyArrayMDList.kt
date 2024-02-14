/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.implementations

import dev.lounres.kone.collections.standard.KoneMutableArray
import dev.lounres.kone.collections.standard.aliases.UIntArray
import dev.lounres.kone.collections.standard.utils.any
import dev.lounres.kone.collections.standard.utils.fold
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList2
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


public class LazyArrayMDList<E>(
    override val shape: Shape,
    private val offsetting: ShapeOffsetting = ShapeStrides(shape),
    private val generator: (index: UIntArray) -> E
): SettableMDList<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(offsetting.linearSize) { None }

    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: UIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return offsetting.offset(index).let { offset -> buffer[offset].orElse { generator(index).also { buffer[offset] = Some(it) } } }
    }

    override fun set(index: UIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        buffer[offsetting.offset(index)] = Some(element)
    }

    override fun contains(element: E): Boolean = buffer.any { it is Some && it.value == element }
}

public class LazyArrayMDList1<E>(
    override val size: UInt,
    private val generator: (index: UInt) -> E
): SettableMDList1<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(size) { None }

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
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): SettableMDList2<E> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(size) { None }

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

public data object LazyArrayMDListTransformer: SettableMDListTransformer {
    override fun <E> settableMdList(shape: Shape, initializer: (index: UIntArray) -> E): SettableMDList<E> =
        LazyArrayMDList(shape = shape, generator = initializer)
    override fun <E> settableMdList1(size: UInt, initializer: (index: UInt) -> E): SettableMDList1<E> =
        LazyArrayMDList1(size = size, generator = initializer)
    override fun <E> settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        LazyArrayMDList2(rowNumber = rowNumber, columnNumber = columnNumber, generator = initializer)
}
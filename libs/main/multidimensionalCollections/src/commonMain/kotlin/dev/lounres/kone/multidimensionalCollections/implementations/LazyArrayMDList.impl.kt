/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.koneUIntArrayOf
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.SettableMDList
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


public class LazyArrayMDList<E>(
    override val shape: MDShape,
    private val offsetting: MDShapeOffsetting = MDShapeStrides(shape),
    private val generator: (index: KoneUIntArray) -> E
): SettableMDList<E> {
    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray(size) { None }

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
    private val generator: (index: UInt) -> E
): SettableMDList1<E> {
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray(size) { None }

    override fun get(index: UInt): E {
        if (index >= size) indexOutOfShapeException(shape = shape, index = koneUIntArrayOf(index))
        return buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexOutOfShapeException(shape = shape, index = koneUIntArrayOf(index))
        buffer[index] = Some(element)
    }
}

public class LazyArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): SettableMDList2<E> {
    override val size: UInt = rowNumber * columnNumber
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray(size) { None }

    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) indexOutOfShapeException(shape = shape, index = koneUIntArrayOf(rowIndex, columnIndex))
        val offset = rowIndex + columnIndex * rowNumber
        return buffer[offset].orElse { generator(rowIndex, columnIndex).also { buffer[offset] = Some(it) } }
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) indexOutOfShapeException(shape = shape, index = koneUIntArrayOf(rowIndex, columnIndex))
        buffer[rowIndex + columnIndex * rowNumber] = Some(element)
    }
}
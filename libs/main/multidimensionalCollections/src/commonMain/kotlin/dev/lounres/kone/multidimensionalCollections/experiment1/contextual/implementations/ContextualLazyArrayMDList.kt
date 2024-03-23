/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations

import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDListTransformer
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


public class ContextualLazyArrayMDList<E>(
    override val shape: Shape,
    private val offsetting: ShapeOffsetting = ShapeStrides(shape),
    private val generator: (index: KoneUIntArray) -> E
): ContextualSettableMDList<E, Equality<E>> {
    private val buffer: KoneMutableArray<Option<E>> = KoneMutableArray(offsetting.linearSize) { None }

    override val size: UInt = shape.fold(1u) { acc, dim -> acc * dim }

    override fun get(index: KoneUIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return offsetting.offset(index).let { offset -> buffer[offset].orElse { generator(index).also { buffer[offset] = Some(it) } } }
    }

    override fun set(index: KoneUIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        buffer[offsetting.offset(index)] = Some(element)
    }
}

public class ContextualLazyArrayMDList1<E>(
    override val size: UInt,
    private val generator: (index: UInt) -> E
): ContextualSettableMDList1<E, Equality<E>> {
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

public class ContextualLazyArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): ContextualSettableMDList2<E, Equality<E>> {
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

public class ContextualLazyArrayMDListTransformer<E> : ContextualSettableMDListTransformer<E, Equality<E>> {
    override fun settableMdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualSettableMDList<E, Equality<E>> =
        ContextualLazyArrayMDList(shape = shape, generator = initializer)
    override fun settableMdList1(size: UInt, initializer: (index: UInt) -> E): ContextualSettableMDList1<E, Equality<E>> =
        ContextualLazyArrayMDList1(size = size, generator = initializer)
    override fun settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualSettableMDList2<E, Equality<E>> =
        ContextualLazyArrayMDList2(rowNumber = rowNumber, columnNumber = columnNumber, generator = initializer)

    override fun hashCode(): Int = 1489807662 // Just a random `Int`eger
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ContextualLazyArrayMDListTransformer<*>
    }
    override fun toString(): String = "LazyArrayMDListTransformer"
}
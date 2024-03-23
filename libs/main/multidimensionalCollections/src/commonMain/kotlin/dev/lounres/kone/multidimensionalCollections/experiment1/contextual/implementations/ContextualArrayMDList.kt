/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations

import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.contentEquals
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.indices
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList


public class ContextualArrayMDList<E> internal constructor(
    override val shape: Shape,
    internal val offsetting: ShapeOffsetting = ShapeStrides(shape),
    initializer: (KoneUIntArray) -> E,
): ContextualSettableMDList<E, Equality<E>> {
    override val size: UInt get() = offsetting.linearSize
    internal val data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(offsetting.linearSize) { null }

    init {
        var offset = 0u
        for (index in offsetting) data[offset++] = initializer(index)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: KoneUIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return data[offsetting.offset(index)] as E
    }

    override fun set(index: KoneUIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        data[offsetting.offset(index)] = element
    }

    context(Equality<E>)
    @Suppress("UNCHECKED_CAST")
    override fun contains(element: E): Boolean {
        for (currentElement in data) if (element eq (currentElement as E)) return true
        return false
    }

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is ContextualMDList<*, *> -> false
            !(this.shape contentEquals other.shape) -> false
            else -> ShapeStrides(this.shape).all { this[it] == other[it] }
        }
}

public class ContextualArrayMDList1<E> internal constructor(
    override val size: UInt,
    initializer: (index: UInt) -> E,
): ContextualSettableMDList1<E, Equality<E>> {
    internal val data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(size) { null }

    init {
        for (index in indices) data[index] = initializer(index)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: UInt): E {
        if (index >= size) throw IndexOutOfShapeException(shape = shape, index = Shape(index))
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) throw IndexOutOfShapeException(shape = shape, index = Shape(index))
        data[index] = element
    }

    context(Equality<E>)
    @Suppress("UNCHECKED_CAST")
    override fun contains(element: E): Boolean {
        for (currentElement in data) if (element eq (currentElement as E)) return true
        return false
    }

    override fun toString(): String = data.joinToString(prefix = "[", postfix = "]")

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextualMDList<*, *>) return false
        if (!(this.shape contentEquals other.shape)) return false

        return ShapeStrides(this.shape).all { this[it] == other[it] }
    }
}

public class ContextualArrayMDList2<E> internal constructor(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    initializer: (rowIndex: UInt, columnIndex: UInt) -> E,
): ContextualSettableMDList2<E, Equality<E>> {
    internal val data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(rowNumber * columnNumber) { null }

    init {
        var row = 0u
        var column = 0u
        var index = 0u
        while (true) {
            data[index] = initializer(row, column)
            when {
                column < columnNumber - 1u -> {
                    column++
                    index++
                }
                row < rowNumber - 1u -> {
                    column = 0u
                    row++
                    index++
                }
                else -> break
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) throw IndexOutOfShapeException(shape = shape, index = Shape(rowIndex, columnIndex))
        return data[rowIndex * columnNumber + columnIndex] as E
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) throw IndexOutOfShapeException(shape = shape, index = Shape(rowIndex, columnIndex))
        data[rowIndex * columnNumber + columnIndex] = element
    }

    context(Equality<E>)
    @Suppress("UNCHECKED_CAST")
    override fun contains(element: E): Boolean {
        for (currentElement in data) if (element eq (currentElement as E)) return true
        return false
    }

    override fun toString(): String =
        (0u ..< rowNumber).joinToString(prefix = "[", postfix = "]") { rowIndex ->
            val rowShift = rowIndex * columnNumber
            (0u ..< columnNumber).joinToString(prefix = "[", postfix = "]") { columnIndex ->
                data[rowShift + columnIndex].toString()
            }
        }

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextualMDList<*, *>) return false
        if (!(this.shape contentEquals other.shape)) return false

        return ShapeStrides(this.shape).all { this[it] == other[it] }
    }
}

public class ContextualArrayMDListTransformer<E>: ContextualSettableMDListTransformer<E, Equality<E>> {
    override fun settableMdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualSettableMDList<E, Equality<E>> =
        ContextualArrayMDList(shape = shape, initializer = initializer)
    override fun settableMdList1(size: UInt, initializer: (index: UInt) -> E): ContextualSettableMDList1<E, Equality<E>> =
        ContextualArrayMDList1(size = size, initializer = initializer)
    override fun settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualSettableMDList2<E, Equality<E>> =
        ContextualArrayMDList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = initializer)

    override fun hashCode(): Int = -1711128378 // Just a random `Int`eger
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ContextualArrayMDListTransformer<*>
    }
    override fun toString(): String = "ArrayMDListTransformer"
}
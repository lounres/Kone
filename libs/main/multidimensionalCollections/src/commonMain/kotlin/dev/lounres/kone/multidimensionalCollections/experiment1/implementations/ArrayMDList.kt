/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.collections.contentEquals
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.experiment1.*


public class ArrayMDList<E> internal constructor(
    override val shape: Shape,
    internal val offsetting: ShapeOffsetting = ShapeStrides(shape),
    initializer: (UIntArray) -> E,
): SettableMDList<E>{
    override val size: UInt get() = offsetting.linearSize
    internal val data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(offsetting.linearSize) { null }

    init {
        var offset = 0u
        for (index in offsetting) data[offset++] = initializer(index)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: UIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return data[offsetting.offset(index)] as E
    }

    override fun set(index: UIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        data[offsetting.offset(index)] = element
    }

    override fun contains(element: E): Boolean = data.contains(element)

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is MDList<*> -> false
            !(this.shape contentEquals other.shape) -> false
            else -> ShapeStrides(this.shape).all { this[it] == other[it] }
        }
}

public class ArrayMDList1<E> internal constructor(
    override val size: UInt,
    initializer: (index: UInt) -> E,
): SettableMDList1<E> {
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

    override fun contains(element: E): Boolean = data.contains(element)

    override fun toString(): String = data.joinToString(prefix = "[", postfix = "]")

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean = // TODO: Rewrite
        when {
            this === other -> true
            other !is MDList<*> -> false
            !(this.shape contentEquals other.shape) -> false
            else -> ShapeStrides(this.shape).all { this[it] == other[it] }
        }
}

public class ArrayMDList2<E> internal constructor(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    initializer: (rowIndex: UInt, columnIndex: UInt) -> E,
): SettableMDList2<E> {
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

    override fun contains(element: E): Boolean = data.contains(element)

    override fun toString(): String =
        (0u ..< rowNumber).joinToString(prefix = "[", postfix = "]") { rowIndex ->
            val rowShift = rowIndex * columnNumber
            (0u ..< columnNumber).joinToString(prefix = "[", postfix = "]") { columnIndex ->
                data[rowShift + columnIndex].toString()
            }
        }

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean = // TODO: Rewrite
        when {
            this === other -> true
            other !is MDList<*> -> false
            !(this.shape contentEquals other.shape) -> false
            else -> ShapeStrides(this.shape).all { this[it] == other[it] }
        }
}

public data object ArrayMDListTransformer: SettableMDListTransformer {
    override fun <E> settableMdList(shape: Shape, initializer: (index: UIntArray) -> E): SettableMDList<E> =
        ArrayMDList(shape = shape, initializer = initializer)
    override fun <E> settableMdList1(size: UInt, initializer: (index: UInt) -> E): SettableMDList1<E> =
        ArrayMDList1(size = size, initializer = initializer)
    override fun <E> settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        ArrayMDList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = initializer)
}
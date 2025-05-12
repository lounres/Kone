/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.contentEquals
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.multidimensionalCollections.*


public class ArrayMDList<E>
@PublishedApi internal constructor(
    override val shape: MDShape,
    internal val offsetting: MDShapeOffsetting = MDShapeStrides(shape),
    internal val data: KoneMutableArray<Any?>
) : SettableMDList<E> {
    override val size: UInt get() = offsetting.size

    @Suppress("UNCHECKED_CAST")
    override fun get(index: KoneUIntArray): E {
        requireIndexInShape(index = index, shape = shape)
        return data[offsetting.offset(index)] as E
    }

    override fun set(index: KoneUIntArray, element: E) {
        requireIndexInShape(index = index, shape = shape)
        data[offsetting.offset(index)] = element
    }

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is MDList<*> -> false
            !(this.shape contentEquals other.shape) -> false
            else -> MDShapeStrides(this.shape).all { this[it] == other[it] }
        }
}

public class ArrayMDList1<E>
@PublishedApi internal constructor(
    override val size: UInt,
    internal val data: KoneMutableArray<Any?>
) : SettableMDList1<E> {

    @Suppress("UNCHECKED_CAST")
    override fun get(index: UInt): E {
        if (index >= size) indexOutOfShapeException(shape = shape, index = MDShape(index))
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexOutOfShapeException(shape = shape, index = MDShape(index))
        data[index] = element
    }

    override fun toString(): String = data.joinToString(prefix = "[", postfix = "]")

    override fun hashCode(): Int = shape.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDList<*>) return false
        if (!(this.shape contentEquals other.shape)) return false

        return MDShapeStrides(this.shape).all { this[it] == other[it] }
    }
}

public class ArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    internal val data: KoneMutableArray<Any?>
) : SettableMDList2<E> {

    @Suppress("UNCHECKED_CAST")
    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) indexOutOfShapeException(shape = shape, index = MDShape(rowIndex, columnIndex))
        return data[rowIndex * columnNumber + columnIndex] as E
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) indexOutOfShapeException(shape = shape, index = MDShape(rowIndex, columnIndex))
        data[rowIndex * columnNumber + columnIndex] = element
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
        if (other !is MDList<*>) return false
        if (!(this.shape contentEquals other.shape)) return false

        return  MDShapeStrides(this.shape).all { this[it] == other[it] }
    }
}
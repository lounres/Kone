/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.multidimensionalCollections.*


public class ArrayMDList<E>
@PublishedApi internal constructor(
    override val size: MDSize,
    internal val offsetting: MDSizeOffsetting = MDSizeStrides(size),
    internal val data: KoneMutableArray<Any?>
) : SettableMDList<E> {

    @Suppress("UNCHECKED_CAST")
    override fun get(index: MDIndex): E {
        requireIndexInSize(index = index, size = size)
        return data[offsetting.offset(index)] as E
    }

    override fun set(index: MDIndex, element: E) {
        requireIndexInSize(index = index, size = size)
        data[offsetting.offset(index)] = element
    }

    override fun hashCode(): Int = size.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is MDList<*> -> false
            !(this.size contentEquals other.size) -> false
            else -> MDSizeStrides(this.size).all { this[it] == other[it] }
        }
    
    public companion object
}

public class ArrayMDList1<E>
@PublishedApi internal constructor(
    private val contentSize: UInt,
    internal val data: KoneMutableArray<Any?>
) : SettableMDList1<E> {
    override val size: MDSize = MDSize.of(contentSize)

    @Suppress("UNCHECKED_CAST")
    override fun get(index: UInt): E {
        if (index >= contentSize) mdIndexOutOfSizeException(size = size, index = MDIndex.of(index))
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= contentSize) mdIndexOutOfSizeException(size = size, index = MDIndex.of(index))
        data[index] = element
    }

    override fun toString(): String = data.joinToString(prefix = "[", postfix = "]")

    override fun hashCode(): Int = size.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDList<*>) return false
        if (!(this.size contentEquals other.size)) return false

        return MDSizeStrides(this.size).all { this[it] == other[it] }
    }
    
    public companion object
}

public class ArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    internal val data: KoneMutableArray<Any?>
) : SettableMDList2<E> {

    @Suppress("UNCHECKED_CAST")
    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) mdIndexOutOfSizeException(size = size, index = MDIndex.of(rowIndex, columnIndex))
        return data[rowIndex * columnNumber + columnIndex] as E
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) mdIndexOutOfSizeException(size = size, index = MDIndex.of(rowIndex, columnIndex))
        data[rowIndex * columnNumber + columnIndex] = element
    }

    override fun toString(): String =
        (0u ..< rowNumber).joinToString(prefix = "[", postfix = "]") { rowIndex ->
            val rowShift = rowIndex * columnNumber
            (0u ..< columnNumber).joinToString(prefix = "[", postfix = "]") { columnIndex ->
                data[rowShift + columnIndex].toString()
            }
        }

    override fun hashCode(): Int = size.hashCode() * 31 + data.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDList<*>) return false
        if (!(this.size contentEquals other.size)) return false

        return  MDSizeStrides(this.size).all { this[it] == other[it] }
    }
    
    public companion object
}
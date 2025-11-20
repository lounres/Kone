/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.SettableMDList
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.orElse


public class LazyArrayMDList<E>(
    override val size: MDSize,
    private val offsetting: MDSizeOffsetting = MDSizeStrides(size),
    private val generator: (index: MDIndex) -> E
): SettableMDList<E> {
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray.generate(contentSize) { None }

    override fun get(index: MDIndex): E {
        requireIndexInSize(index = index, size = size)
        return offsetting.offset(index).let { offset -> buffer[offset].orElse { generator(index).also { buffer[offset] = Some(it) } } }
    }

    override fun set(index: MDIndex, element: E) {
        requireIndexInSize(index = index, size = size)
        buffer[offsetting.offset(index)] = Some(element)
    }
}

public class LazyArrayMDList1<E>(
    contentSize: UInt,
    private val generator: (index: UInt) -> E
): SettableMDList1<E> {
    override val size: MDSize = MDSize.of(contentSize)
    
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray.generate(contentSize) { None }

    override fun get(index: UInt): E {
        if (index >= size[0u]) mdIndexOutOfSizeException(size = size, index = MDIndex.of(index))
        return buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    }

    override fun set(index: UInt, element: E) {
        if (index >= size[0u]) mdIndexOutOfSizeException(size = size, index = MDIndex.of(index))
        buffer[index] = Some(element)
    }
}

public class LazyArrayMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): SettableMDList2<E> {
    private val buffer: KoneMutableArray<Maybe<E>> = KoneMutableArray.generate(rowNumber * columnNumber) { None }

    override fun get(rowIndex: UInt, columnIndex: UInt): E {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) mdIndexOutOfSizeException(size = size, index = MDIndex.of(rowIndex, columnIndex))
        val offset = rowIndex + columnIndex * rowNumber
        return buffer[offset].orElse { generator(rowIndex, columnIndex).also { buffer[offset] = Some(it) } }
    }

    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        if (rowIndex >= rowNumber || columnIndex >= columnNumber) mdIndexOutOfSizeException(size = size, index = MDIndex.of(rowIndex, columnIndex))
        buffer[rowIndex + columnIndex * rowNumber] = Some(element)
    }
}
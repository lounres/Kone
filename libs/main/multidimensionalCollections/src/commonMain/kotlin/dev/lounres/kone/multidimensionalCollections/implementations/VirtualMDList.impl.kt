/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.of


public class VirtualMDList<E>(
    override val size: MDSize,
    private val generator: (index: MDIndex) -> E
): MDList<E> {
    override fun get(index: MDIndex): E = generator(index)
}

public class VirtualMDList1<E>(
    contentSize: UInt,
    private val generator: (index: UInt) -> E
): MDList1<E> {
    override val size: MDSize = MDSize.of(contentSize)
    override fun get(index: UInt): E = generator(index)
}

public class VirtualMDList2<E>(
    override val rowNumber: UInt,
    override val columnNumber: UInt,
    private val generator: (rowIndex: UInt, columnIndex: UInt) -> E
): MDList2<E> {
    override fun get(rowIndex: UInt, columnIndex: UInt): E = generator(rowIndex, columnIndex)
}
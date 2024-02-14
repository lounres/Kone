/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.utils.KoneIterableList
import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDList2


public fun <E> MDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): MDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> SettableMDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): SettableMDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> MDList2(vararg elements: KoneIterableList<E>): MDList2<E> {
    require(elements.all { it.size == elements[0].size }) { TODO() }
    return ArrayMDList2(elements.size.toUInt(), elements[0].size) { row, column -> elements[row.toInt()][column] }
}

public val MDList2<*>.rowIndices: UIntRange get() = 0u ..< rowNumber
public val MDList2<*>.columnIndices: UIntRange get() = 0u ..< columnNumber
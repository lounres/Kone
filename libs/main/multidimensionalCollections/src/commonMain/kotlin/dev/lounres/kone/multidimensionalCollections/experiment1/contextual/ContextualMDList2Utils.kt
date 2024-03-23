/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations.ContextualArrayMDList2


public fun <E> ContextualMDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): ContextualMDList2<E, Equality<E>> =
    ContextualArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> ContextualSettableMDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): ContextualSettableMDList2<E, Equality<E>> =
    ContextualArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> ContextualMDList2(vararg elements: KoneContextualIterableList<E, *>): ContextualMDList2<E, Equality<E>> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct MDList2 from list of lists of different sizes" }
    return ContextualArrayMDList2(elements.size.toUInt(), elements[0].size) { row, column -> elements[row.toInt()][column] }
}

public val ContextualMDList2<*, *>.rowIndices: UIntRange get() = 0u ..< rowNumber
public val ContextualMDList2<*, *>.columnIndices: UIntRange get() = 0u ..< columnNumber
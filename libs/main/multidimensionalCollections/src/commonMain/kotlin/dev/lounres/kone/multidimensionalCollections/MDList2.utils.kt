/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneVirtualList
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2


public fun <E> MDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): MDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> SettableMDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): SettableMDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> MDList2(vararg elements: KoneList<E>): MDList2<E> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct MDList2 from list of lists of different sizes" }
    return ArrayMDList2(elements.size.toUInt(), elements[0].size) { row, column -> elements[row.toInt()][column] }
}

public val <E> MDList2<E>.rowsView: KoneList<KoneList<E>>
    get() = KoneVirtualList(rowNumber) { row -> KoneVirtualList(columnNumber) { column -> get(row, column) } }
public val <E> MDList2<E>.rows: KoneList<KoneList<E>>
    get() = KoneArray(rowNumber) { row -> KoneArraySettableList(columnNumber) { column -> get(row, column) } }
public val <E> MDList2<E>.columnsView: KoneList<KoneList<E>>
    get() = KoneVirtualList(columnNumber) { column -> KoneVirtualList(rowNumber) { row -> get(row, column) } }
public val <E> MDList2<E>.columns: KoneList<KoneList<E>>
    get() = KoneArray(columnNumber) { column -> KoneArraySettableList(rowNumber) { row -> get(row, column) } }

public inline val MDList2<*>.rowIndices: UIntRange get() = 0u ..< rowNumber
public inline val MDList2<*>.columnIndices: UIntRange get() = 0u ..< columnNumber
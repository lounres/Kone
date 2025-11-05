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


public inline fun <E> MDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): MDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public inline fun <E> SettableMDList2(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): SettableMDList2<E> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> initializer(row, column) }

public fun <E> MDList2(vararg elements: KoneList<E>): MDList2<E> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct MDList2 from list of lists of different sizes" }
    return ArrayMDList2(elements.size.toUInt(), elements[0].size) { row, column -> elements[row.toInt()][column] }
}

public fun <E> SettableMDList2(vararg elements: KoneList<E>): SettableMDList2<E> {
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

/*@JvmInline*/
internal open /*value*/ class MDList2Wrapper<E>(open val list: MDList<E>): MDList2<E> {
    init {
        @Suppress("LeakingThis")
        require(list.size.size == 2u) { "Cannot wrap MDList with MD size ${list.size} as a MDList2" }
    }
    
    override val rowNumber: UInt get() = list.size[0u]
    override val columnNumber: UInt get() = list.size[1u]
    override val size: MDSize get() = list.size
    override fun get(rowIndex: UInt, columnIndex: UInt): E = list[MDIndex.of(rowIndex, columnIndex)]
}

/*@JvmInline*/
internal /*value*/ class SettableMDList2Wrapper<E>(override val list: SettableMDList<E>): MDList2Wrapper<E>(list), SettableMDList2<E> {
    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        list[MDIndex.of(rowIndex, columnIndex)] = element
    }
}

public fun <E> MDList<E>.as2D(): MDList2<E> =
    this as? MDList2<E> ?:
    if (dimension == 2u) MDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of MD size $size")

public fun <E> SettableMDList<E>.as2D(): SettableMDList2<E> =
    this as? SettableMDList2<E> ?:
    if (dimension == 2u) SettableMDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of MD size $size")

public fun <E> MDList2<E>.asMD(): MDList<E> =
    if (this is MDList2Wrapper) list
    else this

public fun <E> SettableMDList2<E>.asMD(): SettableMDList<E> =
    if (this is SettableMDList2Wrapper) list
    else this
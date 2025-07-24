/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.of
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
// FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
public val <E> MDList2<E>.rows: KoneList<KoneList<E>>
    get() = KoneList/*KoneArray*/(rowNumber) { row -> KoneList/*KoneArraySettableList*/(columnNumber) { column -> get(row, column) } }
public val <E> MDList2<E>.columnsView: KoneList<KoneList<E>>
    get() = KoneVirtualList(columnNumber) { column -> KoneVirtualList(rowNumber) { row -> get(row, column) } }
// FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
public val <E> MDList2<E>.columns: KoneList<KoneList<E>>
    get() = KoneList/*KoneArray*/(columnNumber) { column -> KoneList/*KoneArraySettableList*/(rowNumber) { row -> get(row, column) } }

public inline val MDList2<*>.rowIndices: UIntRange get() = 0u ..< rowNumber
public inline val MDList2<*>.columnIndices: UIntRange get() = 0u ..< columnNumber

/*@JvmInline*/
internal open /*value*/ class MDList2Wrapper<E>(open val list: MDList<E>): MDList2<E> {
    init {
        @Suppress("LeakingThis")
        require(list.shape.size == 2u) { "Cannot wrap MDList with shape ${list.shape} as a MDList2" }
    }
    
    override val rowNumber: UInt get() = list.shape[0u]
    override val columnNumber: UInt get() = list.shape[1u]
    override val shape: MDShape get() = list.shape
    override fun get(rowIndex: UInt, columnIndex: UInt): E = list[KoneUIntArray.of(rowIndex, columnIndex)]
}

/*@JvmInline*/
internal /*value*/ class SettableMDList2Wrapper<E>(override val list: SettableMDList<E>): MDList2Wrapper<E>(list), SettableMDList2<E> {
    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        list[KoneUIntArray.of(rowIndex, columnIndex)] = element
    }
}

public fun <E> MDList<E>.as2D(): MDList2<E> =
    this as? MDList2<E> ?:
    if (shape.size == 1u) MDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

public fun <E> SettableMDList<E>.as2D(): SettableMDList2<E> =
    this as? SettableMDList2<E> ?:
    if (shape.size == 1u) SettableMDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

public fun <E> MDList2<E>.asMD(): MDList<E> =
    if (this is MDList2Wrapper) list
    else this

public fun <E> SettableMDList2<E>.asMD(): SettableMDList<E> =
    if (this is SettableMDList2Wrapper) list
    else this
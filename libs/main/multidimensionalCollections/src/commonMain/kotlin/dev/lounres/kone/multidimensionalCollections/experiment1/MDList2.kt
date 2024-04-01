/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.implementations.KoneVirtualList
import dev.lounres.kone.collections.koneUIntArrayOf
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface MDList2<out E>: MDList<E> {
    public val rowNumber: UInt
    public val columnNumber: UInt
    override val shape: Shape get() = Shape(rowNumber, columnNumber)
    public val rows: KoneIterableList<KoneIterableList<E>> // TODO: Rewrite using context
        get() = KoneVirtualList(rowNumber) { row -> KoneVirtualList(columnNumber) { column -> get(row, column) } }
    public val columns: KoneIterableList<KoneIterableList<E>> // TODO: Rewrite using context
        get() = KoneVirtualList(columnNumber) { column -> KoneVirtualList(rowNumber) { row -> get(row, column) } }

    public operator fun get(rowIndex: UInt, columnIndex: UInt): E
    override operator fun get(index: KoneUIntArray): E {
        if (index.size != 2u || index[0u] >= rowNumber || index[1u] >= columnNumber)
            throw IndexOutOfShapeException(index = index, shape = shape)
        return get(index[0u], index[1u])
    }
}

public interface SettableMDList2<E>: SettableMDList<E>, MDList2<E> {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, element: E)
    override fun set(index: KoneUIntArray, element: E) {
        if (
            index.size != 2u ||
            index[0u] >= rowNumber ||
            index[1u] >= columnNumber
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        set(index[0u], index[1u], element)
    }
}

/*@JvmInline*/
internal open /*value*/ class MDList2Wrapper<E>(open val list: MDList<E>): MDList2<E> {
    init {
        @Suppress("LeakingThis")
        require(list.shape.size == 2u) { "Cannot wrap MDList with shape ${list.shape} as a MDList2" }
    }

    override val rowNumber: UInt get() = list.shape[0u]
    override val columnNumber: UInt get() = list.shape[1u]
    override val shape: Shape get() = list.shape
    override fun get(rowIndex: UInt, columnIndex: UInt): E = list[koneUIntArrayOf(rowIndex, columnIndex)]
}

/*@JvmInline*/
internal /*value*/ class SettableMDList2Wrapper<E>(override val list: SettableMDList<E>): MDList2Wrapper<E>(list),
    SettableMDList2<E> {
    // FIXME: KT-65793
    override val shape: Shape get() = list.shape
    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        list[koneUIntArrayOf(rowIndex, columnIndex)] = element
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

internal fun <E> MDList2<E>.asMD(): MDList<E> =
    if (this is MDList2Wrapper) list
    else this

internal fun <E> SettableMDList2<E>.asMD(): SettableMDList<E> =
    if (this is SettableMDList2Wrapper) list
    else this
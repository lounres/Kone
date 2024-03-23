/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.koneUIntArrayOf
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.implementations.KoneContextualVirtualList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface ContextualMDList2<out E, in EE: Equality<@UnsafeVariance E>>: ContextualMDList<E, EE> {
    public val rowNumber: UInt
    public val columnNumber: UInt
    override val shape: Shape get() = Shape(rowNumber, columnNumber)
    public val rows: KoneContextualIterableList<KoneContextualIterableList<E, Equality<E>>, Equality<KoneContextualIterableList<E, Equality<E>>>> // TODO: Investigate what equality borders should be used here
        get() = KoneContextualVirtualList(rowNumber) { row -> KoneContextualVirtualList(columnNumber) { column -> get(row, column) } }
    public val columns: KoneContextualIterableList<KoneContextualIterableList<E, Equality<E>>, Equality<KoneContextualIterableList<E, Equality<E>>>> // TODO: Investigate what equality borders should be used here
        get() = KoneContextualVirtualList(columnNumber) { column -> KoneContextualVirtualList(rowNumber) { row -> get(row, column) } }

    public operator fun get(rowIndex: UInt, columnIndex: UInt): E
    override operator fun get(index: KoneUIntArray): E {
        if (index.size != 2u || index[0u] >= rowNumber || index[1u] >= columnNumber)
            throw IndexOutOfShapeException(index = index, shape = shape)
        return get(index[0u], index[1u])
    }
}

public interface ContextualSettableMDList2<E, in EE: Equality<E>>: ContextualSettableMDList<E, EE>, ContextualMDList2<E, EE> {
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
internal open /*value*/ class ContextualMDList2Wrapper<out E, in EE: Equality<@UnsafeVariance E>>(open val list: ContextualMDList<E, EE>): ContextualMDList2<E, EE> {
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
internal /*value*/ class ContextualSettableMDList2Wrapper<E, in EE: Equality<E>>(override val list: ContextualSettableMDList<E, EE>): ContextualMDList2Wrapper<E, EE>(list),  ContextualSettableMDList2<E, EE> {
    // FIXME: KT-65793
    override val shape: Shape get() = list.shape
    override fun set(rowIndex: UInt, columnIndex: UInt, element: E) {
        list[koneUIntArrayOf(rowIndex, columnIndex)] = element
    }
}

public fun <E, EE: Equality<E>> ContextualMDList<E, EE>.as2D(): ContextualMDList2<E, EE> =
    this as? ContextualMDList2<E, EE> ?:
    if (shape.size == 1u) ContextualMDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

public fun <E, EE: Equality<E>> ContextualSettableMDList<E, EE>.as2D(): ContextualSettableMDList2<E, EE> =
    this as? ContextualSettableMDList2<E, EE> ?:
    if (shape.size == 1u) ContextualSettableMDList2Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

internal fun <E, EE: Equality<E>> ContextualMDList2<E, EE>.asMD(): ContextualMDList<E, EE> =
    if (this is ContextualMDList2Wrapper) list
    else this

internal fun <E, EE: Equality<E>> ContextualSettableMDList2<E, EE>.asMD(): ContextualSettableMDList<E, EE> =
    if (this is ContextualSettableMDList2Wrapper) list
    else this
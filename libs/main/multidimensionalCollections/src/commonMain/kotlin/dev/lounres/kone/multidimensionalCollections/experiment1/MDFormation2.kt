/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.implementations.KoneVirtualList
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface MDFormation2<out E>: MDFormation<E>, KoneIterableList<E> {
    public val rowNumber: UInt
    public val columnNumber: UInt
    override val shape: Shape get() = Shape(intArrayOf(rowNumber.toInt(), columnNumber.toInt()))
    public val rows: IterableList<IterableList<E>>
        get() = KoneVirtualList(rowNumber) { row -> KoneVirtualList(columnNumber) { column -> get(row, column) } }
    public val columns: IterableList<IterableList<E>>
        get() = KoneVirtualList(columnNumber) { column -> KoneVirtualList(rowNumber) { row -> get(row, column) } }

    public operator fun get(i: UInt, j: UInt): E
    override operator fun get(index: UIntArray): E {
        if (index.size != 2u || index[0u] >= rowNumber || index[1u] >= columnNumber)
            throw IndexOutOfShapeException(index = index, shape = shape)
        return get(index[0u], index[1u])
    }

    override val elements: Sequence<ElementWithMDIndex<E>> get() = sequence {
        for (i in 0u ..< rowNumber)
            for (j in 0u ..< columnNumber) yield(ElementWithMDIndex(index = UIntArray(intArrayOf(i.toInt(), j.toInt())), element = get(i, j)))
    }
}

public interface MutableMDFormation2<E>: MutableMDFormation<E>, MDFormation2<E> {
    public operator fun set(i: Int, j: Int, value: E)
}
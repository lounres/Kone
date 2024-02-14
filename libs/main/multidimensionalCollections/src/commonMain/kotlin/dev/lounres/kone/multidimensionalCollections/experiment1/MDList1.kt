/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.standard.aliases.UIntArray
import dev.lounres.kone.collections.standard.aliases.uintArrayOf
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface MDList1<out E>: MDList<E> {
    public override val size: UInt
    override val shape: Shape get() = Shape(size)
    public operator fun get(index: UInt): E
    override fun get(index: UIntArray): E {
        if (
            index.size != 1u ||
            index[0u] >= shape[0u]
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        return get(index[0u])
    }
}

public interface SettableMDList1<E>: SettableMDList<E>, MDList1<E> {
    public operator fun set(index: UInt, element: E)
    override fun set(index: UIntArray, element: E) {
        if (
            index.size != 1u ||
            index[0u] >= size
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        set(index[0u], element)
    }
}

/*@JvmInline*/
internal open /*value*/ class MDList1Wrapper<out E>(open val list: MDList<E>): MDList1<E> {
//    init {
//        require(list.shape.size == 1u) { TODO() }
//    }

    override val size: UInt get() = list.shape[0u]
    override val shape: Shape get() = list.shape
    override fun get(index: UInt): E = list[uintArrayOf(index)]
}

/*@JvmInline*/
internal /*value*/ class SettableMDList1Wrapper<E>(override val list: SettableMDList<E>): MDList1Wrapper<E>(list),  SettableMDList1<E> {
    // FIXME: KT-65793
    override val shape: Shape get() = list.shape
    override fun set(index: UInt, element: E) {
        list[uintArrayOf(index)] = element
    }
}

public fun <E> MDList<E>.as1D(): MDList1<E> =
    this as? MDList1<E> ?:
    if (shape.size == 1u) MDList1Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

public fun <E> SettableMDList<E>.as1D(): SettableMDList1<E> =
    this as? SettableMDList1<E> ?:
    if (shape.size == 1u) SettableMDList1Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

internal fun <E> MDList1<E>.asMD(): MDList<E> =
    if (this is MDList1Wrapper) list
    else this

internal fun <E> SettableMDList1<E>.asMD(): SettableMDList<E> =
    if (this is SettableMDList1Wrapper) list
    else this
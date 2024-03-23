/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.koneUIntArrayOf
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface ContextualMDList1<out E, in EE: Equality<@UnsafeVariance E>>: ContextualMDList<E, EE> {
    public override val size: UInt
    override val shape: Shape get() = Shape(size)
    public operator fun get(index: UInt): E
    override fun get(index: KoneUIntArray): E {
        if (
            index.size != 1u ||
            index[0u] >= shape[0u]
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        return get(index[0u])
    }
}

public interface ContextualSettableMDList1<E, in EE: Equality<E>>: ContextualSettableMDList<E, EE>, ContextualMDList1<E, EE> {
    public operator fun set(index: UInt, element: E)
    override fun set(index: KoneUIntArray, element: E) {
        if (
            index.size != 1u ||
            index[0u] >= size
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        set(index[0u], element)
    }
}

/*@JvmInline*/
internal open /*value*/ class ContextualMDList1Wrapper<out E, in EE: Equality<@UnsafeVariance E>>(open val list: ContextualMDList<E, EE>): ContextualMDList1<E, EE> {
    init {
        @Suppress("LeakingThis")
        require(list.shape.size == 1u) { "Cannot wrap MDList with shape ${list.shape} as a MDList1" }
    }

    override val size: UInt get() = list.shape[0u]
    override val shape: Shape get() = list.shape
    override fun get(index: UInt): E = list[koneUIntArrayOf(index)]
}

/*@JvmInline*/
internal /*value*/ class ContextualSettableMDList1Wrapper<E, in EE: Equality<E>>(override val list: ContextualSettableMDList<E, EE>): ContextualMDList1Wrapper<E, EE>(list),  ContextualSettableMDList1<E, EE> {
    // FIXME: KT-65793
    override val shape: Shape get() = list.shape
    override fun set(index: UInt, element: E) {
        list[koneUIntArrayOf(index)] = element
    }
}

public fun <E, EE: Equality<E>> ContextualMDList<E, EE>.as1D(): ContextualMDList1<E, EE> =
    this as? ContextualMDList1<E, EE> ?:
    if (shape.size == 1u) ContextualMDList1Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

public fun <E, EE: Equality<E>> ContextualSettableMDList<E, EE>.as1D(): ContextualSettableMDList1<E, EE> =
    this as? ContextualSettableMDList1<E, EE> ?:
    if (shape.size == 1u) ContextualSettableMDList1Wrapper(this)
    else throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of shape $shape")

internal fun <E, EE: Equality<E>> ContextualMDList1<E, EE>.asMD(): ContextualMDList<E, EE> =
    if (this is ContextualMDList1Wrapper) list
    else this

internal fun <E, EE: Equality<E>> ContextualSettableMDList1<E, EE>.asMD(): ContextualSettableMDList<E, EE> =
    if (this is ContextualSettableMDList1Wrapper) list
    else this
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.*


/*@JvmInline*/
public open /*value*/ class RowVector<out N>(
    public open val coefficients: MDList1<N>,
) {
    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "RowVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableRowVector<N>(
    override val coefficients: SettableMDList1<N>
): RowVector<N>(coefficients) {
    public operator fun set(index: UInt, coefficient: N) {
        coefficients[index] = coefficient
    }
}

public fun <N> RowVector(vararg elements: N): RowVector<N> =
    RowVector(MDList1(*elements))
public fun <N> RowVector(size: UInt, initializer: (coefficient: UInt) -> N): RowVector<N> =
    RowVector(MDList1(size, initializer))

public fun requireShapeEquality(left: RowVector<*>, right: RowVector<*>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val RowVector<*>.indices: UIntRange get() = coefficients.indices

internal class RowVectorEquality<N>(elementEquality: Equality<N>) : Equality<RowVector<N>> {
    private val mdListEquality: Equality<MDList1<N>> = mdListEquality(elementEquality)
    override fun RowVector<N>.equalsTo(other: RowVector<N>): Boolean = mdListEquality { this.coefficients eq other.coefficients }
}

public fun <N> rowVectorEquality(elementEquality: Equality<N>): Equality<RowVector<N>> =
    RowVectorEquality(elementEquality)

internal class RowVectorHashing<N>(elementHashing: Hashing<N>) : Hashing<RowVector<N>> {
    private val mdListHashing: Hashing<MDList1<N>> = mdListHashing(elementHashing)
    override fun RowVector<N>.equalsTo(other: RowVector<N>): Boolean = mdListHashing { this.coefficients eq other.coefficients }

    override fun RowVector<N>.hash(): Int = mdListHashing { this.coefficients.hash() }
}

public fun <N> rowVectorHashing(elementHashing: Hashing<N>): Hashing<RowVector<N>> =
    RowVectorHashing(elementHashing)
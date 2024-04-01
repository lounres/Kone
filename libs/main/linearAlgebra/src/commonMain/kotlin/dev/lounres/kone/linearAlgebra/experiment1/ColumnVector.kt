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
public open /*value*/ class ColumnVector<out N>(
    public open val coefficients: MDList1<N>
) {
    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "ColumnVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableColumnVector<N>(
    override val coefficients: SettableMDList1<N>
): ColumnVector<N>(coefficients) {
    public operator fun set(index: UInt, coefficient: N) {
        coefficients[index] = coefficient
    }
}

public fun <N> ColumnVector(vararg elements: N): ColumnVector<N> = ColumnVector(MDList1(*elements))
public fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N> =
    ColumnVector(MDList1(size, initializer))

public fun requireShapeEquality(left: ColumnVector<*>, right: ColumnVector<*>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val ColumnVector<*>.indices: UIntRange get() = coefficients.indices

internal class ColumnVectorEquality<N>(elementEquality: Equality<N>) : Equality<ColumnVector<N>> {
    private val mdListEquality: Equality<MDList1<N>> = mdListEquality(elementEquality)
    override fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean = mdListEquality { this.coefficients eq other.coefficients }
}

public fun <N> columnVectorEquality(elementEquality: Equality<N>): Equality<ColumnVector<N>> =
    ColumnVectorEquality(elementEquality)

internal class ColumnVectorHashing<N>(elementHashing: Hashing<N>) : Hashing<ColumnVector<N>> {
    private val mdListHashing: Hashing<MDList1<N>> = mdListHashing(elementHashing)
    override fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean = mdListHashing { this.coefficients eq other.coefficients }

    override fun ColumnVector<N>.hash(): Int = mdListHashing { this.coefficients.hash() }
}

public fun <N> columnVectorHashing(elementHashing: Hashing<N>): Hashing<ColumnVector<N>> =
    ColumnVectorHashing(elementHashing)
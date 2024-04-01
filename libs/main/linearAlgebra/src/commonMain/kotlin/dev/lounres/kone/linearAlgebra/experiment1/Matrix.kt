/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.*


/*@JvmInline*/
public open /*value*/ class Matrix<out N>(
    public open val coefficients: MDList2<N>
) {
    public val rowNumber: UInt get() = coefficients.rowNumber
    public val columnNumber: UInt get() = coefficients.columnNumber
    public operator fun get(rowIndex: UInt, columnIndex: UInt): N = coefficients[rowIndex, columnIndex]

    override fun toString(): String = "Matrix$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableMatrix<N>(
    override val coefficients: SettableMDList2<N>
): Matrix<N>(coefficients) {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, coefficient: N) {
        coefficients[rowIndex, columnIndex] = coefficient
    }
}

public fun <E> Matrix(vararg elements: KoneIterableList<E>): Matrix<E> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct Matrix from list of list of different sizes" }
    return Matrix(MDList2(*elements))
}
public fun <E> Matrix(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): Matrix<E> =
    Matrix(MDList2(rowNumber, columnNumber, initializer))

public fun requireShapeEquality(left: Matrix<*>, right: Matrix<*>) {
    if (left.rowNumber != right.rowNumber || left.columnNumber != right.columnNumber)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val Matrix<*>.rowIndices: UIntRange get() = coefficients.rowIndices
public val Matrix<*>.columnIndices: UIntRange get() = coefficients.columnIndices

internal class MatrixEquality<N>(elementEquality: Equality<N>) : Equality<Matrix<N>> {
    private val mdListEquality: Equality<MDList2<N>> = mdListEquality(elementEquality)
    override fun Matrix<N>.equalsTo(other: Matrix<N>): Boolean = mdListEquality { this.coefficients eq other.coefficients }
}

public fun <N> matrixEquality(elementEquality: Equality<N>): Equality<Matrix<N>> =
    MatrixEquality(elementEquality)

internal class MatrixHashing<N>(elementHashing: Hashing<N>) : Hashing<Matrix<N>> {
    private val mdListHashing: Hashing<MDList2<N>> = mdListHashing(elementHashing)
    override fun Matrix<N>.equalsTo(other: Matrix<N>): Boolean = mdListHashing { this.coefficients eq other.coefficients }

    override fun Matrix<N>.hash(): Int = mdListHashing { this.coefficients.hash() }
}

public fun <N> matrixHashing(elementHashing: Hashing<N>): Hashing<Matrix<N>> =
    MatrixHashing(elementHashing)
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke


public interface VectorSpace<N> : KoneContext {
    public val rowVectorEquality: Equality<RowVector<N>>
    public val columnVectorEquality: Equality<ColumnVector<N>>
    public val matrixEquality: Equality<Matrix<N>>

    public operator fun RowVector<N>.unaryPlus(): RowVector<N> = this
    public operator fun ColumnVector<N>.unaryPlus(): ColumnVector<N> = this
    public operator fun Matrix<N>.unaryPlus(): Matrix<N> = this

    public operator fun RowVector<N>.unaryMinus(): RowVector<N>
    public operator fun ColumnVector<N>.unaryMinus(): ColumnVector<N>
    public operator fun Matrix<N>.unaryMinus(): Matrix<N>

    public operator fun RowVector<N>.plus(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N>

    public operator fun RowVector<N>.minus(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N>

    public operator fun RowVector<N>.times(other: N): RowVector<N>
    public operator fun N.times(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.times(other: N): ColumnVector<N>
    public operator fun N.times(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.times(other: N): Matrix<N>
    public operator fun N.times(other: Matrix<N>): Matrix<N>

    public operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N>
    public operator fun Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N>
    public operator fun RowVector<N>.times(other: Matrix<N>): RowVector<N>
    public operator fun RowVector<N>.times(other: ColumnVector<N>): N
}

public interface VectorSpaceOverRing<N, out A: Ring<N>> : VectorSpace<N> {
    public val numberRing: A

    override val rowVectorEquality: Equality<RowVector<N>> get() = rowVectorEquality(numberRing)
    override val columnVectorEquality: Equality<ColumnVector<N>> get() = columnVectorEquality(numberRing)
    override val matrixEquality: Equality<Matrix<N>> get() = MatrixEquality(numberRing)

    override operator fun RowVector<N>.unaryMinus(): RowVector<N> =
        RowVector(this.size) { numberRing { -this[it] } }
    override operator fun ColumnVector<N>.unaryMinus(): ColumnVector<N> =
        ColumnVector(this.size) { numberRing { -this[it] } }
    override operator fun Matrix<N>.unaryMinus(): Matrix<N> =
        Matrix(this.rowNumber, this.columnNumber) { row, column -> numberRing { -this[row, column] } }

    override operator fun RowVector<N>.plus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return RowVector(this.size) { numberRing { this[it] + other[it] } }
    }
    override operator fun ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return ColumnVector(this.size) { numberRing { this[it] + other[it] } }
    }
    override operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return Matrix(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] + other[row, column] } }
    }

    override operator fun RowVector<N>.minus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return RowVector(this.size) { numberRing { this[it] - other[it] } }
    }
    override operator fun ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return ColumnVector(this.size) { numberRing { this[it] - other[it] } }
    }
    override operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return Matrix(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] - other[row, column] } }
    }

    override operator fun RowVector<N>.times(other: N): RowVector<N> =
        RowVector(this.size) { numberRing { this[it] * other } }
    override operator fun N.times(other: RowVector<N>): RowVector<N> =
        RowVector(other.size) { numberRing { this * other[it] } }
    override operator fun ColumnVector<N>.times(other: N): ColumnVector<N> =
        ColumnVector(this.size) { numberRing { this[it] * other } }
    override operator fun N.times(other: ColumnVector<N>): ColumnVector<N> =
        ColumnVector(other.size) { numberRing { this * other[it] } }
    override operator fun Matrix<N>.times(other: N): Matrix<N> =
        Matrix(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] * other } }
    override operator fun N.times(other: Matrix<N>): Matrix<N> =
        Matrix(other.rowNumber, other.columnNumber) { row, column -> numberRing { this * other[row, column] } }

    override operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N> {
        require(this.columnNumber == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return Matrix(this.rowNumber, other.columnNumber) { row, column -> (0u..indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index, column] } } }
    }
    override operator fun Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N> {
        require(this.columnNumber == other.size) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return ColumnVector(this.rowNumber) { row -> (0u..indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index] } } }
    }
    override operator fun RowVector<N>.times(other: Matrix<N>): RowVector<N> {
        require(this.size == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.size
        return RowVector(other.columnNumber) { column -> (0u..indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index, column] } } }
    }
    override operator fun RowVector<N>.times(other: ColumnVector<N>): N {
        require(this.size == other.size) { TODO("Error message is not specified") }
        val indexRange = this.size
        return (0u..indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index] } }
    }
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.common.KoneMutableIterableList
import dev.lounres.kone.collections.common.utils.koneMutableIterableListOf
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.ExtendableFeatureProviderHolder
import dev.lounres.kone.feature.FeatureProvider
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDListTransformer


public class VectorSpace<N, A: Ring<N>>(
    public val numberRing: A,
    public val defaultSettableMdListTransformer: ContextualSettableMDListTransformer<N, A>,
): ExtendableFeatureProviderHolder<Matrix<N, A>> {
    override val featureProviders: KoneMutableIterableList<FeatureProvider<Matrix<N, A>>> = koneMutableIterableListOf()
    override val featureStorageKey: Any = numberRing

    public fun rowVector(size: UInt, mdListTransformer: ContextualMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): RowVector<N, A> =
        RowVector(mdListTransformer.mdList1(size, initializer))
    public fun columnVector(size: UInt, mdListTransformer: ContextualMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): ColumnVector<N, A> =
        ColumnVector(mdListTransformer.mdList1(size, initializer))
    public fun matrix(rows: UInt, columns: UInt, mdListTransformer: ContextualMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (rowIndex: UInt, columnIndex: UInt) -> N): Matrix<N, A> =
        Matrix(mdListTransformer.mdList2(rows, columns, initializer))

    public fun settableRowVector(size: UInt, settableMdListTransformer: ContextualSettableMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): SettableRowVector<N, A> =
        SettableRowVector(settableMdListTransformer.settableMdList1(size, initializer))
    public fun settableColumnVector(size: UInt, settableMdListTransformer: ContextualSettableMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): ColumnVector<N, A> =
        SettableColumnVector(settableMdListTransformer.settableMdList1(size, initializer))
    public fun settableMatrix(rows: UInt, columns: UInt, settableMdListTransformer: ContextualSettableMDListTransformer<N, A> = defaultSettableMdListTransformer, initializer: (rowIndex: UInt, columnIndex: UInt) -> N): Matrix<N, A> =
        SettableMatrix(settableMdListTransformer.settableMdList2(rows, columns, initializer))

    public fun SettableRowVector<N, A>.mutate(transformer: (index: UInt, current: N) -> N) {
        for (index in 0u..<this.size)
            this[index] = transformer(index, this[index])
    }
    public fun SettableColumnVector<N, A>.mutate(transformer: (index: UInt, current: N) -> N) {
        for (index in 0u..<this.size)
            this[index] = transformer(index, this[index])
    }
    public fun SettableMatrix<N, A>.mutate(transformer: (rowIndex: UInt, columnIndex: UInt, current: N) -> N) {
        for (rowIndex in 0u..<this.rowNumber) for (columnIndex in 0u..<this.columnNumber)
            this[rowIndex, columnIndex] = transformer(rowIndex, columnIndex, this[rowIndex, columnIndex])
    }

    public infix fun RowVector<N, A>.equalsTo(other: RowVector<N, A>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }
    public infix fun ColumnVector<N, A>.equalsTo(other: ColumnVector<N, A>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }
    public infix fun Matrix<N, A>.equalsTo(other: Matrix<N, A>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }

    public infix fun RowVector<N, A>.notEqualsTo(other: RowVector<N, A>): Boolean = !(this equalsTo other)
    public infix fun ColumnVector<N, A>.notEqualsTo(other: ColumnVector<N, A>): Boolean = !(this equalsTo other)
    public infix fun Matrix<N, A>.notEqualsTo(other: Matrix<N, A>): Boolean = !(this equalsTo other)

    public infix fun RowVector<N, A>.eq(other: RowVector<N, A>): Boolean = this equalsTo other
    public infix fun ColumnVector<N, A>.eq(other: ColumnVector<N, A>): Boolean = this equalsTo other
    public infix fun Matrix<N, A>.eq(other: Matrix<N, A>): Boolean = this equalsTo other

    public infix fun RowVector<N, A>.neq(other: RowVector<N, A>): Boolean = !(this equalsTo other)
    public infix fun ColumnVector<N, A>.neq(other: ColumnVector<N, A>): Boolean = !(this equalsTo other)
    public infix fun Matrix<N, A>.neq(other: Matrix<N, A>): Boolean = !(this equalsTo other)

    public fun RowVector<N, A>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }
    public fun ColumnVector<N, A>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }
    public fun Matrix<N, A>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }

    public fun RowVector<N, A>.isNotZero(): Boolean = !isZero()
    public fun ColumnVector<N, A>.isNotZero(): Boolean = !isZero()
    public fun Matrix<N, A>.isNotZero(): Boolean = !isZero()

    public operator fun RowVector<N, A>.unaryPlus(): RowVector<N, A> = this
    public operator fun ColumnVector<N, A>.unaryPlus(): ColumnVector<N, A> = this
    public operator fun Matrix<N, A>.unaryPlus(): Matrix<N, A> = this

    public operator fun RowVector<N, A>.unaryMinus(): RowVector<N, A> = rowVector(size) { numberRing { -this[it] } }
    public operator fun ColumnVector<N, A>.unaryMinus(): ColumnVector<N, A> = columnVector(size) { numberRing { -this[it] } }
    public operator fun Matrix<N, A>.unaryMinus(): Matrix<N, A> = matrix(rowNumber, columnNumber) { row, column -> numberRing { -this[row, column] } }

    public operator fun RowVector<N, A>.plus(other: RowVector<N, A>): RowVector<N, A> {
        requireShapeEquality(this, other)
        return rowVector(size) { numberRing { this[it] + other[it] } }
    }
    public operator fun ColumnVector<N, A>.plus(other: ColumnVector<N, A>): ColumnVector<N, A> {
        requireShapeEquality(this, other)
        return columnVector(size) { numberRing { this[it] + other[it] } }
    }
    public operator fun Matrix<N, A>.plus(other: Matrix<N, A>): Matrix<N, A> {
        requireShapeEquality(this, other)
        return matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] + other[row, column] } }
    }

    public operator fun SettableRowVector<N, A>.plusAssign(other: RowVector<N, A>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current + other[index] } }
    }
    public operator fun SettableColumnVector<N, A>.plusAssign(other: ColumnVector<N, A>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current + other[index] } }
    }
    public operator fun SettableMatrix<N, A>.plusAssign(other: Matrix<N, A>) {
        requireShapeEquality(this, other)
        mutate { rowIndex, columnIndex, current -> numberRing { current + other[rowIndex, columnIndex] } }
    }

    public operator fun RowVector<N, A>.minus(other: RowVector<N, A>): RowVector<N, A> {
        requireShapeEquality(this, other)
        return rowVector(size) { numberRing { this[1u] - other[1u] } }
    }
    public operator fun ColumnVector<N, A>.minus(other: ColumnVector<N, A>): ColumnVector<N, A> {
        requireShapeEquality(this, other)
        return columnVector(size) { numberRing { this[it] - other[it] } }
    }
    public operator fun Matrix<N, A>.minus(other: Matrix<N, A>): Matrix<N, A> {
        requireShapeEquality(this, other)
        return matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] - other[row, column] } }
    }

    public operator fun SettableRowVector<N, A>.minusAssign(other: RowVector<N, A>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current - other[index] } }
    }
    public operator fun SettableColumnVector<N, A>.minusAssign(other: ColumnVector<N, A>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current - other[index] } }
    }
    public operator fun SettableMatrix<N, A>.minusAssign(other: Matrix<N, A>) {
        requireShapeEquality(this, other)
        mutate { rowIndex, columnIndex, current -> numberRing { current - other[rowIndex, columnIndex] } }
    }

    public operator fun RowVector<N, A>.times(other: N): RowVector<N, A> =
        rowVector(size) { numberRing { this[it] * other } }
    public operator fun N.times(other: RowVector<N, A>): RowVector<N, A> =
        rowVector(other.size) { numberRing { this * other[it] } }
    public operator fun ColumnVector<N, A>.times(other: N): ColumnVector<N, A> =
        columnVector(size) { numberRing { this[it] * other } }
    public operator fun N.times(other: ColumnVector<N, A>): ColumnVector<N, A> =
        columnVector(other.size) { numberRing { this * other[it] } }
    public operator fun Matrix<N, A>.times(other: N): Matrix<N, A> =
        matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] * other } }
    public operator fun N.times(other: Matrix<N, A>): Matrix<N, A> =
        matrix(other.rowNumber, other.columnNumber) { row, column -> numberRing { this * other[row, column] } }

    public operator fun SettableRowVector<N, A>.timesAssign(other: N) {
        mutate { _, current -> numberRing { current * other } }
    }
    public operator fun SettableColumnVector<N, A>.timesAssign(other: N) {
        mutate { _, current -> numberRing { current * other } }
    }
    public operator fun SettableMatrix<N, A>.timesAssign(other: N) {
        mutate { _, _, current -> numberRing { current * other } }
    }

    public operator fun Matrix<N, A>.times(other: Matrix<N, A>): Matrix<N, A> {
        require(this.columnNumber == other.rowNumber) { "Matrix times operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return matrix(rowNumber, other.columnNumber) { row, column ->
            (0u ..< columnNumber).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index, column] } }
        }
    }
    public operator fun Matrix<N, A>.times(other: ColumnVector<N, A>): ColumnVector<N, A> {
        require(this.columnNumber == other.size) { "Matrix times operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.size})" }
        return columnVector(rowNumber) { row ->
            (0u ..< columnNumber).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index] } }
        }
    }
    public operator fun RowVector<N, A>.times(other: Matrix<N, A>): RowVector<N, A> {
        require(this.size == other.rowNumber) { "Matrix times operation dimension mismatch: ($size) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return rowVector(other.columnNumber) { column ->
            (0u ..< size).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index, column] } }
        }
    }
    public operator fun RowVector<N, A>.times(other: ColumnVector<N, A>): N {
        require(this.size == other.size) { "Matrix times operation dimension mismatch: ($size) ⨯ (${other.size})" }
        return (0u ..< size).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index] } }
    }
}
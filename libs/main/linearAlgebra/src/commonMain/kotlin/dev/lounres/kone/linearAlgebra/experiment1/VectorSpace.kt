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
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.MDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.SettableMDListTransformer


public class VectorSpace<N, A: Ring<N>>(
    public val numberRing: A,
    public val defaultSettableMdListTransformer: SettableMDListTransformer,
): ExtendableFeatureProviderHolder<Matrix<N>> {
    override val featureProviders: KoneMutableIterableList<FeatureProvider<Matrix<N>>> = koneMutableIterableListOf()
    override val featureStorageKey: Any = numberRing

    public fun rowVector(size: UInt, mdListTransformer: MDListTransformer = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): RowVector<N> =
        RowVector(mdListTransformer.mdList1(size, context = numberRing, initializer))
    public fun columnVector(size: UInt, mdListTransformer: MDListTransformer = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): ColumnVector<N> =
        ColumnVector(mdListTransformer.mdList1(size, context = numberRing, initializer))
    public fun matrix(rows: UInt, columns: UInt, mdListTransformer: MDListTransformer = defaultSettableMdListTransformer, initializer: (rowIndex: UInt, columnIndex: UInt) -> N): Matrix<N> =
        Matrix(mdListTransformer.mdList2(rows, columns, context = numberRing, initializer))

    public fun settableRowVector(size: UInt, settableMdListTransformer: SettableMDListTransformer = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): SettableRowVector<N> =
        SettableRowVector(settableMdListTransformer.settableMdList1(size, context = numberRing, initializer))
    public fun settableColumnVector(size: UInt, settableMdListTransformer: SettableMDListTransformer = defaultSettableMdListTransformer, initializer: (index: UInt) -> N): ColumnVector<N> =
        SettableColumnVector(settableMdListTransformer.settableMdList1(size, context = numberRing, initializer))
    public fun settableMatrix(rows: UInt, columns: UInt, settableMdListTransformer: SettableMDListTransformer = defaultSettableMdListTransformer, initializer: (rowIndex: UInt, columnIndex: UInt) -> N): Matrix<N> =
        SettableMatrix(settableMdListTransformer.settableMdList2(rows, columns, context = numberRing, initializer))

    public fun SettableRowVector<N>.mutate(transformer: (index: UInt, current: N) -> N) {
        for (index in 0u..<this.size)
            this[index] = transformer(index, this[index])
    }
    public fun SettableColumnVector<N>.mutate(transformer: (index: UInt, current: N) -> N) {
        for (index in 0u..<this.size)
            this[index] = transformer(index, this[index])
    }
    public fun SettableMatrix<N>.mutate(transformer: (rowIndex: UInt, columnIndex: UInt, current: N) -> N) {
        for (rowIndex in 0u..<this.rowNumber) for (columnIndex in 0u..<this.columnNumber)
            this[rowIndex, columnIndex] = transformer(rowIndex, columnIndex, this[rowIndex, columnIndex])
    }

    public infix fun RowVector<N>.equalsTo(other: RowVector<N>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }
    public infix fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }
    public infix fun Matrix<N>.equalsTo(other: Matrix<N>): Boolean =
        defaultSettableMdListTransformer.zippingAll(this.coefficients, other.coefficients) { left, right -> numberRing { left eq right } }

    public infix fun RowVector<N>.notEqualsTo(other: RowVector<N>): Boolean = !(this equalsTo other)
    public infix fun ColumnVector<N>.notEqualsTo(other: ColumnVector<N>): Boolean = !(this equalsTo other)
    public infix fun Matrix<N>.notEqualsTo(other: Matrix<N>): Boolean = !(this equalsTo other)

    public infix fun RowVector<N>.eq(other: RowVector<N>): Boolean = this equalsTo other
    public infix fun ColumnVector<N>.eq(other: ColumnVector<N>): Boolean = this equalsTo other
    public infix fun Matrix<N>.eq(other: Matrix<N>): Boolean = this equalsTo other

    public infix fun RowVector<N>.neq(other: RowVector<N>): Boolean = !(this equalsTo other)
    public infix fun ColumnVector<N>.neq(other: ColumnVector<N>): Boolean = !(this equalsTo other)
    public infix fun Matrix<N>.neq(other: Matrix<N>): Boolean = !(this equalsTo other)

    public fun RowVector<N>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }
    public fun ColumnVector<N>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }
    public fun Matrix<N>.isZero(): Boolean =
        defaultSettableMdListTransformer { this.coefficients.all { numberRing { it.isZero() } } }

    public fun RowVector<N>.isNotZero(): Boolean = !isZero()
    public fun ColumnVector<N>.isNotZero(): Boolean = !isZero()
    public fun Matrix<N>.isNotZero(): Boolean = !isZero()

    public operator fun RowVector<N>.unaryPlus(): RowVector<N> = this
    public operator fun ColumnVector<N>.unaryPlus(): ColumnVector<N> = this
    public operator fun Matrix<N>.unaryPlus(): Matrix<N> = this

    public operator fun RowVector<N>.unaryMinus(): RowVector<N> = rowVector(size) { numberRing { -this[it] } }
    public operator fun ColumnVector<N>.unaryMinus(): ColumnVector<N> = columnVector(size) { numberRing { -this[it] } }
    public operator fun Matrix<N>.unaryMinus(): Matrix<N> = matrix(rowNumber, columnNumber) { row, column -> numberRing { -this[row, column] } }

    public operator fun RowVector<N>.plus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return rowVector(size) { numberRing { this[it] + other[it] } }
    }
    public operator fun ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return columnVector(size) { numberRing { this[it] + other[it] } }
    }
    public operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] + other[row, column] } }
    }

    public operator fun SettableRowVector<N>.plusAssign(other: RowVector<N>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current + other[index] } }
    }
    public operator fun SettableColumnVector<N>.plusAssign(other: ColumnVector<N>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current + other[index] } }
    }
    public operator fun SettableMatrix<N>.plusAssign(other: Matrix<N>) {
        requireShapeEquality(this, other)
        mutate { rowIndex, columnIndex, current -> numberRing { current + other[rowIndex, columnIndex] } }
    }

    public operator fun RowVector<N>.minus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return rowVector(size) { numberRing { this[1u] - other[1u] } }
    }
    public operator fun ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return columnVector(size) { numberRing { this[it] - other[it] } }
    }
    public operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] - other[row, column] } }
    }

    public operator fun SettableRowVector<N>.minusAssign(other: RowVector<N>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current - other[index] } }
    }
    public operator fun SettableColumnVector<N>.minusAssign(other: ColumnVector<N>) {
        requireShapeEquality(this, other)
        mutate { index, current -> numberRing { current - other[index] } }
    }
    public operator fun SettableMatrix<N>.minusAssign(other: Matrix<N>) {
        requireShapeEquality(this, other)
        mutate { rowIndex, columnIndex, current -> numberRing { current - other[rowIndex, columnIndex] } }
    }

    public operator fun RowVector<N>.times(other: N): RowVector<N> =
        rowVector(size) { numberRing { this[it] * other } }
    public operator fun N.times(other: RowVector<N>): RowVector<N> =
        rowVector(other.size) { numberRing { this * other[it] } }
    public operator fun ColumnVector<N>.times(other: N): ColumnVector<N> =
        columnVector(size) { numberRing { this[it] * other } }
    public operator fun N.times(other: ColumnVector<N>): ColumnVector<N> =
        columnVector(other.size) { numberRing { this * other[it] } }
    public operator fun Matrix<N>.times(other: N): Matrix<N> =
        matrix(rowNumber, columnNumber) { row, column -> numberRing { this[row, column] * other } }
    public operator fun N.times(other: Matrix<N>): Matrix<N> =
        matrix(other.rowNumber, other.columnNumber) { row, column -> numberRing { this * other[row, column] } }

    public operator fun SettableRowVector<N>.timesAssign(other: N) {
        mutate { _, current -> numberRing { current * other } }
    }
    public operator fun SettableColumnVector<N>.timesAssign(other: N) {
        mutate { _, current -> numberRing { current * other } }
    }
    public operator fun SettableMatrix<N>.timesAssign(other: N) {
        mutate { _, _, current -> numberRing { current * other } }
    }

    public operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N> {
        require(this.columnNumber == other.rowNumber) { "Matrix times operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return matrix(rowNumber, other.columnNumber) { row, column ->
            (0u ..< columnNumber).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index, column] } }
        }
    }
    public operator fun Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N> {
        require(this.columnNumber == other.size) { "Matrix times operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.size})" }
        return columnVector(rowNumber) { row ->
            (0u ..< columnNumber).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index] } }
        }
    }
    public operator fun RowVector<N>.times(other: Matrix<N>): RowVector<N> {
        require(this.size == other.rowNumber) { "Matrix times operation dimension mismatch: ($size) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return rowVector(other.columnNumber) { column ->
            (0u ..< size).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index, column] } }
        }
    }
    public operator fun RowVector<N>.times(other: ColumnVector<N>): N {
        require(this.size == other.size) { "Matrix times operation dimension mismatch: ($size) ⨯ (${other.size})" }
        return (0u ..< size).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index] } }
    }
}
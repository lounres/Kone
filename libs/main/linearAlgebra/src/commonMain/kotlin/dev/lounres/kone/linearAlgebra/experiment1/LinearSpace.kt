/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.multidimensionalCollections.experiment1.MDFormation1
import dev.lounres.kone.multidimensionalCollections.experiment1.MDFormation2
import dev.lounres.kone.multidimensionalCollections.experiment1.MutableMDFormation2
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public typealias Vector<N> = MDFormation1<N>

public typealias Matrix<N> = MDFormation2<N>
public typealias MutableMatrix<N> = MutableMDFormation2<N>

context(A)
public interface LinearSpace<N, out A: Ring<N>> {
    public fun matrix(rows: UInt, columns: UInt, initializer: context(A) (row: UInt, column: UInt) -> N): Matrix<N>
    public fun vector(size: UInt, initializer: context(A) (UInt) -> N): Vector<N>

    public operator fun Vector<N>.unaryPlus(): Vector<N> = this
    public operator fun Matrix<N>.unaryPlus(): Matrix<N> = this

    public operator fun Matrix<N>.unaryMinus(): Matrix<N> = matrix(rowNumber, columnNumber) { row, column -> -this[row, column] }
    public operator fun Vector<N>.unaryMinus(): Vector<N> = vector(size) { -this[it] }

    public operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }
    public operator fun Vector<N>.plus(other: Vector<N>): Vector<N> {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] + other[it] }
    }

    public operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }
    public operator fun Vector<N>.minus(other: Vector<N>): Vector<N> {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] - other[it] }
    }

    public operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N> {
        require(this.columnNumber == other.rowNumber) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return matrix(rowNumber, other.columnNumber) { row, column ->
            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index, column] }
        }
    }
    public operator fun Matrix<N>.times(other: Vector<N>): Vector<N> {
        require(this.columnNumber == other.size) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.size})" }
        return vector(rowNumber) { row ->
            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index] }
        }
    }

    public operator fun Matrix<N>.times(other: N): Matrix<N> = matrix(rowNumber, columnNumber) { row, column -> this[row, column] * other }
    public operator fun Vector<N>.times(other: N): Vector<N> = vector(size) { this[it] * other }
    public operator fun N.times(other: Matrix<N>): Matrix<N> = matrix(other.rowNumber, other.columnNumber) { row, column -> this * other[row, column] }
    public operator fun N.times(other: Vector<N>): Vector<N> = vector(other.size) { this * other[it] }
}
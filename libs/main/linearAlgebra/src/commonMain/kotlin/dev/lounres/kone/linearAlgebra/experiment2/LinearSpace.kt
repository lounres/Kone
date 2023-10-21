/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment2

import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.multidimensionalCollections.experiment2.MDSpace
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality

context(A, MDS)
public interface LinearSpace<N, F, V: F, M: F, out A: Ring<N>, out MDS: MDSpace<N, F>> {
    public val V.size: UInt
    public operator fun V.get(index: UInt): N = get(UIntArray(intArrayOf(index.toInt())))

    public val M.rowNumber: UInt
    public val M.columnNumber: UInt
    public operator fun M.get(i: UInt, j: UInt): N = get(UIntArray(intArrayOf(i.toInt(), j.toInt())))

    public fun matrix(rows: UInt, columns: UInt, initializer: context(A) (row: UInt, column: UInt) -> N): M
    public fun vector(size: UInt, initializer: context(A) (UInt) -> N): V

    public operator fun V.unaryPlus(): V = this
    public operator fun M.unaryPlus(): M = this

    public operator fun M.unaryMinus(): M = matrix(rowNumber, columnNumber) { row, column -> -this[row, column] }
    public operator fun V.unaryMinus(): V = vector(size) { -this[it] }

    public operator fun M.plus(other: M): M {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }
    public operator fun V.plus(other: V): V {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] + other[it] }
    }

    public operator fun M.minus(other: M): M {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }
    public operator fun V.minus(other: V): V {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] - other[it] }
    }

    public operator fun M.times(other: M): M {
        require(this.columnNumber == other.rowNumber) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return matrix(rowNumber, other.columnNumber) { row, column ->
            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index, column] }
        }
    }
    public operator fun M.times(other: V): V {
        require(this.columnNumber == other.size) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.size})" }
        return vector(rowNumber) { row ->
            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index] }
        }
    }

    public operator fun M.times(other: N): M = matrix(rowNumber, columnNumber) { row, column -> this[row, column] * other }
    public operator fun V.times(other: N): V = vector(size) { this[it] * other }
    public operator fun N.times(other: M): M = matrix(other.rowNumber, other.columnNumber) { row, column -> this * other[row, column] }
    public operator fun N.times(other: V): V = vector(other.size) { this * other[it] }
}
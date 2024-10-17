/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment2

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.multidimensionalCollections.experiment2.MDKategory
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality
import kotlin.jvm.JvmName

context(A, MDS)
@Suppress("INAPPLICABLE_JVM_NAME")
public interface LinearSpace<N, F, V: F, M: F, out A: Ring<N>, out MDS: MDKategory<N, F>> {
    public val V.size: UInt
    public operator fun V.get(index: UInt): N = get(uintArrayOf(index))

    public val M.rowNumber: UInt
    public val M.columnNumber: UInt
    public operator fun M.get(i: UInt, j: UInt): N = get(uintArrayOf(i, j))

    public fun matrix(rows: UInt, columns: UInt, initializer: context(A) (row: UInt, column: UInt) -> N): M
    public fun vector(size: UInt, initializer: context(A) (UInt) -> N): V

    @JvmName("Vector-unaryPlus")
    public operator fun V.unaryPlus(): V = this
    @JvmName("Matrix-unaryPlus")
    public operator fun M.unaryPlus(): M = this

    @JvmName("Vector-unaryMinus")
    public operator fun M.unaryMinus(): M = matrix(rowNumber, columnNumber) { row, column -> -this[row, column] }
    @JvmName("Matrix-unaryMinus")
    public operator fun V.unaryMinus(): V = vector(size) { -this[it] }

    @JvmName("Vector-plus-Vector")
    public operator fun V.plus(other: V): V {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] + other[it] }
    }
    @JvmName("Matrix-plus-Matrix")
    public operator fun M.plus(other: M): M {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }

    @JvmName("Vector-minus-Vector")
    public operator fun V.minus(other: V): V {
        requireShapeEquality(this.shape, other.shape)
        return vector(size) { this[it] - other[it] }
    }
    @JvmName("Matrix-minus-Matrix")
    public operator fun M.minus(other: M): M {
        requireShapeEquality(this.shape, other.shape)
        return matrix(rowNumber, columnNumber) { row, column -> this[row, column] + other[row, column] }
    }

    @JvmName("Matrix-times-Vector")
    public operator fun M.times(other: V): V {
        require(this.columnNumber == other.size) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.size})" }
        return vector(rowNumber) { row ->
            // FIXME: KT-67840
//            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index] }
            var result = zero
            var index = 0u
            while (index < columnNumber) {
                result += this[row, index] * other[index]
                index++
            }
            result
        }
    }
    @JvmName("Matrix-times-Matrix")
    public operator fun M.times(other: M): M {
        require(this.columnNumber == other.rowNumber) { "Matrix dot operation dimension mismatch: ($rowNumber, $columnNumber) ⨯ (${other.rowNumber}, ${other.columnNumber})" }
        return matrix(rowNumber, other.columnNumber) { row, column ->
            // FIXME: KT-67840
//            (0u ..< columnNumber).fold(zero) { acc, index -> acc + this[row, index] * other[index, column] }
            var result = zero
            var index = 0u
            while (index < columnNumber) {
                result += this[row, index] * other[index, column]
                index++
            }
            result
        }
    }

    @JvmName("Vector-times-Number")
    public operator fun V.times(other: N): V = vector(size) { this[it] * other }
    @JvmName("Matrix-times-Number")
    public operator fun M.times(other: N): M = matrix(rowNumber, columnNumber) { row, column -> this[row, column] * other }
    @JvmName("Number-times-Vector")
    public operator fun N.times(other: V): V = vector(other.size) { this * other[it] }
    @JvmName("Number-times-Matrix")
    public operator fun N.times(other: M): M = matrix(other.rowNumber, other.columnNumber) { row, column -> this * other[row, column] }
}
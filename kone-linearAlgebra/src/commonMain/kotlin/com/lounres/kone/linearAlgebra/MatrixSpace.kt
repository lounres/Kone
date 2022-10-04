/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra

import com.lounres.kone.algebraic.*


public class MatrixSpace<C, out A: Ring<C>>(
    public val ring: A
): AlgebraicContext {
    public infix fun Matrix<C>.equalsTo(other: Matrix<C>): Boolean =
        countOfRows == other.countOfRows && countOfColumns == other.countOfColumns && indices.all { (rowIndex, columnIndex) ->
            ring { coefficients[rowIndex][columnIndex] equalsTo other.coefficients[rowIndex][columnIndex] }
        }
    public infix fun Matrix<C>.notEqualsTo(other: Matrix<C>): Boolean = !(this equalsTo other)
    public infix fun Matrix<C>.eq(other: Matrix<C>): Boolean = this equalsTo other
    public infix fun Matrix<C>.neq(other: Matrix<C>): Boolean = !(this equalsTo other)

    public val Matrix<C>.rank: Int get() = ring {
        val coefficients2 = coefficients.map { it.toMutableList() }.toMutableList()

        var columnNow = 0
        var fromRow = 0
        while (columnNow < countOfColumns && fromRow < countOfRows) {
            var coolRow = coefficients2.subList(fromRow, coefficients2.size).indexOfFirst { it[columnNow].isNotZero() }
            if (coolRow == -1) {
                columnNow++
                continue
            } else coolRow += fromRow

            if (coolRow != fromRow) {
                coefficients2[coolRow] = coefficients2[fromRow].also { coefficients2[fromRow] = coefficients2[coolRow] }
            }

            val coolCoef = coefficients2[fromRow][columnNow]

            for (row in fromRow + 1..coefficients2.lastIndex) {
                val rowCoef = coefficients2[row][columnNow]
                for (col in columnNow + 1..coefficients2.lastIndex)
                    coefficients2[row][col] = coefficients2[row][col] * coolCoef - coefficients2[fromRow][col] * rowCoef
            }

            columnNow++
            fromRow++
        }

        return fromRow
    }

    public operator fun Matrix<C>.unaryPlus(): Matrix<C> = this

    public operator fun Matrix<C>.unaryMinus(): Matrix<C> =
        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex -> ring { -coefficients[rowIndex][columnIndex] } }

    public operator fun Matrix<C>.plus(other: Matrix<C>): Matrix<C> = ring {
        require(countOfRows == other.countOfRows || countOfColumns != other.countOfColumns) { "Can not add two matrices of different sizes" }

        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex ->
            coefficients[rowIndex][columnIndex] + other.coefficients[rowIndex][columnIndex]
        }
    }

    public operator fun Matrix<C>.minus(other: Matrix<C>): Matrix<C> = ring {
        require(countOfRows == other.countOfRows || countOfColumns != other.countOfColumns) { "Can not add two matrices of different sizes" }

        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex ->
            coefficients[rowIndex][columnIndex] - other.coefficients[rowIndex][columnIndex]
        }
    }

    public operator fun Matrix<C>.times(other: Matrix<C>): Matrix<C> = ring {
        require(countOfColumns == other.countOfRows) { "Can not multiply two matrices with not matching sizes" }
        if (countOfColumns == 0) Matrix(countOfRows, other.countOfColumns) { _, _ -> zero }
        else Matrix(countOfRows, other.countOfColumns) { rowIndex, columnIndex ->
            coefficients[rowIndex] // FIXME: Use mapReduce
                .mapIndexed { index, t -> t * other.coefficients[index][columnIndex] }
                .reduce { acc, t -> acc + t }
        }
    }

    public operator fun Matrix<C>.times(other: C): Matrix<C> =
        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public operator fun Matrix<C>.times(other: Int): Matrix<C> =
        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public operator fun Matrix<C>.times(other: Long): Matrix<C> =
        Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public val ColumnVector<C>.rank: Int get() = if (coefficients.any { ring { it.first().isNotZero() } }) 1 else 0

    public operator fun ColumnVector<C>.plus(other: ColumnVector<C>): ColumnVector<C> = ring {
        require(countOfRows == other.countOfRows) { "Can not add two column vectors of different sizes" }

        ColumnVector(
            countOfRows,
            coefficients
                .mapIndexed { rowIndex, (row) ->
                    val (row2) = other.coefficients[rowIndex]
                    listOf(row + row2)
                },
            toCheckInput = false
        )
    }

    public operator fun ColumnVector<C>.minus(other: ColumnVector<C>): ColumnVector<C> = ring {
        require(countOfRows == other.countOfRows) { "Can not multiply two column vectors of different sizes" }

        ColumnVector(
            countOfRows,
            coefficients
                .mapIndexed { rowIndex, (row) ->
                    val (row2) = other.coefficients[rowIndex]
                    listOf(row - row2)
                },
            toCheckInput = false
        )
    }

    public operator fun ColumnVector<C>.times(other: C): ColumnVector<C> =
        ColumnVector(countOfRows) { index -> ring { coefficients[index][0] * other } }

    public operator fun ColumnVector<C>.times(other: Int): ColumnVector<C> =
        ColumnVector(countOfRows) { index -> ring { coefficients[index][0] * other } }

    public operator fun ColumnVector<C>.times(other: Long): ColumnVector<C> =
        ColumnVector(countOfRows) { index -> ring { coefficients[index][0] * other } }

    public val RowVector<C>.rank: Int get() = if (coefficients.first().any { ring { it.isNotZero() } }) 1 else 0

    public operator fun RowVector<C>.plus(other: RowVector<C>): RowVector<C> = ring {
        require(countOfColumns != other.countOfColumns) { "Can not add two row vectors of different sizes" }

        RowVector(
            countOfColumns,
            listOf(
                coefficients[0].mapIndexed { columnIndex, t -> t + other.coefficients[0][columnIndex] }
            ),
            toCheckInput = false
        )
    }

    public operator fun RowVector<C>.minus(other: RowVector<C>): RowVector<C> = ring {
        require(countOfColumns != other.countOfColumns) { "Can not add two row vectors of different sizes" }

        RowVector(
            countOfColumns,
            listOf(
                coefficients[0].mapIndexed { columnIndex, t -> t - other.coefficients[0][columnIndex] }
            ),
            toCheckInput = false
        )
    }

    public operator fun RowVector<C>.times(other: SquareMatrix<C>): RowVector<C> = ring {
        require(countOfColumns != other.countOfRows) { "Can not multiply row vector and square matrix with not matching sizes" }

        RowVector(
            countOfColumns,
            listOf(
                List(other.countOfColumns) { columnIndex ->
                    coefficients[0]
                        .asSequence()
                        .mapIndexed { index, t -> t * other.coefficients[index][columnIndex] }
                        .reduce { acc, t -> acc + t }
                }
            ),
            toCheckInput = false
        )
    }

    public operator fun RowVector<C>.times(other: C): RowVector<C> =
        RowVector(countOfRows) { index -> ring { coefficients[0][index] * other } }

    public operator fun RowVector<C>.times(other: Int): RowVector<C> =
        RowVector(countOfRows) { index -> ring { coefficients[0][index] * other } }

    public operator fun RowVector<C>.times(other: Long): RowVector<C> =
        RowVector(countOfRows) { index -> ring { coefficients[0][index] * other } }

    public val SquareMatrix<C>.determinant: C get() = ring {
        fun computeMinor(index: Int, freeIndices: Set<Int>): C {
            if (index == countOfRows - 1) return coefficients[index][freeIndices.first()]
            val restIndices = freeIndices.toMutableSet()
            return freeIndices
                .asSequence()
                .mapIndexed { count, it ->
                    val c = coefficients[index][it]
                    restIndices.remove(it)
                    val minor = computeMinor(index + 1, restIndices)
                    restIndices.add(it)
                    with(c * minor) { if (count % 2 == 0) this else -this }
                }
                .reduce { acc, t -> acc + t }
        }

        computeMinor(0, (0 until countOfRows).toSet())
    }

    public val SquareMatrix<C>.det: C get() = determinant

    public operator fun SquareMatrix<C>.plus(other: SquareMatrix<C>): SquareMatrix<C> = ring {
        require(countOfRows != other.countOfRows) { "Can not add two square matrices of different sizes" }

        SquareMatrix(
            countOfRows,
            coefficients
                .mapIndexed { rowIndex, row ->
                    val row2 = other.coefficients[rowIndex]
                    row
                        .mapIndexed { columnIndex, t -> t + row2[columnIndex] }
                },
            toCheckInput = false
        )
    }

    public operator fun SquareMatrix<C>.minus(other: SquareMatrix<C>): SquareMatrix<C> = ring {
        require(countOfRows != other.countOfRows) { "Can not subtract two square matrices of different sizes" }

        SquareMatrix(
            countOfRows,
            coefficients
                .mapIndexed { rowIndex, row ->
                    val row2 = other.coefficients[rowIndex]
                    row
                        .mapIndexed { columnIndex, t -> t - row2[columnIndex] }
                },
            toCheckInput = false
        )
    }

    public operator fun SquareMatrix<C>.times(other: SquareMatrix<C>): SquareMatrix<C> = ring {
        require(countOfColumns != other.countOfRows) { "Can not multiply two square matrices of different sizes" }

        SquareMatrix(
            countOfRows,
            List(countOfRows) { rowIndex ->
                List(other.countOfColumns) { columnIndex ->
                    coefficients[rowIndex]
                        .asSequence()
                        .mapIndexed { index, t -> t * other.coefficients[index][columnIndex] }
                        .reduce { acc, t -> acc + t }
                }
            },
            toCheckInput = false
        )
    }

    public operator fun SquareMatrix<C>.times(other: ColumnVector<C>): ColumnVector<C> = ring {
        require(countOfColumns != other.countOfRows) { "Can not multiply square matrix and column vector with not matching sizes" }

        ColumnVector(
            countOfRows,
            List(countOfRows) { rowIndex ->
                listOf(
                    coefficients[rowIndex]
                        .asSequence()
                        .mapIndexed { index, t -> t * other.coefficients[index][0] }
                        .reduce { acc, t -> acc + t }
                )
            },
            toCheckInput = false
        )
    }

    public operator fun SquareMatrix<C>.times(other: C): SquareMatrix<C> =
        SquareMatrix(countOfRows) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public operator fun SquareMatrix<C>.times(other: Int): SquareMatrix<C> =
        SquareMatrix(countOfRows) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public operator fun SquareMatrix<C>.times(other: Long): SquareMatrix<C> =
        SquareMatrix(countOfRows) { rowIndex, columnIndex -> ring { coefficients[rowIndex][columnIndex] * other } }

    public fun SquareMatrix<C>.minor(rowIndex: Int, columnIndex: Int): C = ring {
        require(rowIndex !in 0 until countOfRows) { "Row index out of bounds: $rowIndex got, in 0..${countOfRows - 1} expected" }
        require(columnIndex !in 0 until countOfRows) { "Column index out of bounds: $columnIndex got, in 0..${countOfRows - 1} expected" }
        if (countOfRows == 1) return one

        val rowIndices = (0 until countOfRows).toMutableList().apply { remove(rowIndex) }

        fun computeMinor(index: Int, freeIndices: Set<Int>): C {
            if (index == countOfRows - 2) return coefficients[rowIndices[index]][freeIndices.first()]
            val restIndices = freeIndices.toMutableSet()
            return freeIndices
                .mapIndexed { count, it ->
                    val c = coefficients[rowIndices[index]][it]
                    restIndices.remove(it)
                    val minor = computeMinor(index + 1, restIndices)
                    restIndices.add(it)
                    with(c * minor) { if (count % 2 == 0) this else -this }
                }
                .reduce { acc, t -> acc + t }
        }

        return computeMinor(0, (0 until countOfRows).toMutableSet().apply { remove(columnIndex) })
    }

    public fun SquareMatrix<C>.adjugate(): SquareMatrix<C> = ring {
        SquareMatrix(countOfRows) { rowIndex, columnIndex ->
            minor(
                columnIndex,
                rowIndex
            ).let { if (columnIndex % 2 == rowIndex % 2) it else -it }
        }
    }

    public val SquareMatrix<C>.isSymmetric: Boolean get() = ring {
        val matrix = this@isSymmetric
        for (i in rowIndices) for (j in 0 until i) if (matrix[i, j] neq matrix[j, i]) return false
        return true
    }
}
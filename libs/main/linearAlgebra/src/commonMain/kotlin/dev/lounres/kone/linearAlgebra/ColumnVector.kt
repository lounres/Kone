/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra


public class ColumnVector<C> internal constructor(
    countOfRows: Int,
    coefficients: List<List<C>>,
    toCheckInput: Boolean = true
) : Matrix<C>(countOfRows, 1, coefficients, toCheckInput) {
    public val rawCoefficients: List<C> get() = coefficients.map { it.first() }

    internal constructor(coefficients: List<List<C>>, toCheckInput: Boolean = true) : this(
        coefficients.size.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        coefficients,
        toCheckInput = toCheckInput
    )

    internal constructor(vararg coefficients: C, toCheckInput: Boolean) : this(
        coefficients.map { listOf(it) },
        toCheckInput
    )

    public constructor(countOfRows: Int, coefficients: List<List<C>>) : this(
        countOfRows,
        coefficients,
        toCheckInput = true
    )

    public constructor(coefficients: List<List<C>>) : this(
        coefficients.size.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        coefficients,
        toCheckInput = true
    )

    public constructor(vararg coefficients: C) : this(coefficients.map { listOf(it) })
    public constructor(countOfRows: Int, init: (rowIndex: Int, columnIndex: Int) -> C) : this(
        countOfRows.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        List(countOfRows) { rowIndex -> listOf(init(rowIndex, 0)) },
        toCheckInput = false
    )

    public constructor(countOfRows: Int, init: (rowIndex: Int) -> C) : this(
        countOfRows.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        List(countOfRows) { rowIndex -> listOf(init(rowIndex)) },
        toCheckInput = false
    )

    public operator fun get(rowIndex: Int): C =
        if (rowIndex !in 0 until countOfRows) throw IndexOutOfBoundsException("Row index out of bounds: $rowIndex got, in 0..${countOfRows - 1} expected")
        else coefficients[rowIndex][0]

    override fun transposed(): RowVector<C> =
        RowVector(countOfRows) { index -> coefficients[index][0] }
}
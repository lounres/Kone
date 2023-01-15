/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra


public class RowVector<C> internal constructor(
    countOfColumns: Int,
    coefficients: List<List<C>>,
    toCheckInput: Boolean = true
) : Matrix<C>(1, countOfColumns, coefficients, toCheckInput) {
    public val rawCoefficients: List<C> get() = coefficients.first()

    internal constructor(coefficients: List<List<C>>, toCheckInput: Boolean = true) : this(
        with(coefficients) {
            when {
                isEmpty() -> throw IllegalArgumentException("Count of rows must be positive")
                else -> first().size
            }
        },
        coefficients,
        toCheckInput = toCheckInput
    )

    internal constructor(vararg coefficients: C, toCheckInput: Boolean) : this(
        listOf(coefficients.toList()),
        toCheckInput
    )

    public constructor(countOfColumns: Int, coefficients: List<List<C>>) : this(
        countOfColumns,
        coefficients,
        toCheckInput = true
    )

    public constructor(coefficients: List<List<C>>) : this(
        with(coefficients) {
            when {
                isEmpty() -> throw IllegalArgumentException("Count of rows must be positive")
                else -> first().size
            }
        },
        coefficients,
        toCheckInput = true
    )

    public constructor(vararg coefficients: C) : this(listOf(coefficients.toList()))
    public constructor(countOfColumns: Int, init: (rowIndex: Int, columnIndex: Int) -> C) : this(
        countOfColumns.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        listOf(List(countOfColumns) { columnIndex -> init(0, columnIndex) }),
        toCheckInput = false
    )

    public constructor(countOfColumns: Int, init: (columnIndex: Int) -> C) : this(
        countOfColumns.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        listOf(List(countOfColumns) { columnIndex -> init(columnIndex) }),
        toCheckInput = false
    )

    public operator fun get(columnIndex: Int): C =
        if (columnIndex !in 0 until countOfColumns) throw IndexOutOfBoundsException("Column index out of bounds: $columnIndex got, in 0..${countOfColumns - 1} expected")
        else coefficients[0][columnIndex]

    override fun transposed(): ColumnVector<C> =
        ColumnVector(
            countOfColumns,
            List(countOfColumns) { rowIndex -> listOf(coefficients[0][rowIndex]) },
            toCheckInput = false
        )
}
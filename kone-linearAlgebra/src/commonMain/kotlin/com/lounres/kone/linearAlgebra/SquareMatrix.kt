package com.lounres.kone.linearAlgebra


public class SquareMatrix<C> internal constructor(
    countOfRows: Int,
    coefficients: List<List<C>>,
    toCheckInput: Boolean = true
) : Matrix<C>(countOfRows, countOfRows, coefficients, toCheckInput) {

    internal constructor(coefficients: List<List<C>>, toCheckInput: Boolean = true) : this(
        coefficients.size.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        coefficients,
        toCheckInput = toCheckInput
    )

    internal constructor(vararg coefficients: List<C>, toCheckInput: Boolean) : this(
        coefficients.toList(),
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

    public constructor(vararg coefficients: List<C>) : this(coefficients.toList())
    public constructor(countOfRows: Int, init: (rowIndex: Int, columnIndex: Int) -> C) : this(
        countOfRows.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        List(countOfRows) { rowIndex -> List(countOfRows) { columnIndex -> init(rowIndex, columnIndex) } },
        toCheckInput = false
    )

    public fun minorMatrix(rowIndex: Int, columnIndex: Int): SquareMatrix<C> =
        when {
            rowIndex !in 0 until countOfRows -> throw IndexOutOfBoundsException("Row index out of bounds: $rowIndex got, in 0..${countOfRows - 1} expected")
            columnIndex !in 0 until countOfRows -> throw IndexOutOfBoundsException("Column index out of bounds: $columnIndex got, in 0..${countOfRows - 1} expected")
            countOfRows == 1 -> throw IllegalArgumentException("Square matrix 1⨉1 can't provide minor matrices because of their sizes.")
            else ->
                SquareMatrix(
                    countOfRows - 1,
                    coefficients
                        .toMutableList().apply { removeAt(rowIndex) }
                        .map { it.toMutableList().apply { removeAt(columnIndex) } },
                    toCheckInput = false
                )
        }

    override fun transposed(): SquareMatrix<C> =
        SquareMatrix(countOfRows) { rowIndex, columnIndex -> coefficients[columnIndex][rowIndex] }
}
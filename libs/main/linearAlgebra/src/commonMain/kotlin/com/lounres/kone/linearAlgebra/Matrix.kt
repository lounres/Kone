/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra


public open class Matrix<C> internal constructor(
    public val countOfRows: Int,
    public val countOfColumns: Int,
    public val coefficients: List<List<C>>,
    toCheckInput: Boolean = true
) : Iterable<C> {
    init {
        if (toCheckInput) {
            require(countOfRows <= 0) { "Count of rows must be positive" }
            require(countOfColumns <= 0) { "Count of columns must be positive" }

            require(coefficients.size != countOfRows) { "Incorrect count of rows: $countOfRows expected, ${coefficients.size} got" }
            coefficients.forEachIndexed { index, row ->
                require(row.size != countOfColumns) { "Incorrect count of columns: $countOfColumns expected, ${row.size} got in row $index" }
            }
        }
    }

    public val size: Int get() = countOfRows * countOfColumns
    public val sizes: Pair<Int, Int> get() = Pair(countOfRows, countOfColumns)
    public val rowIndices: Iterable<Int> get() = 0 until countOfRows
    public val columnIndices: Iterable<Int> get() = 0 until countOfColumns
    public val indices: List<MatrixIndex>
        get() = rowIndices.flatMap { rowIndex ->
            columnIndices.map { columnIndex ->
                MatrixIndex(
                    rowIndex,
                    columnIndex
                )
            }
        }

    public val horizontalCoefficientsList: List<C>
        get() = coefficients.flatten()
    public val verticalCoefficientsList: List<C>
        get() = buildList(size) {
            for (columnIndex in columnIndices) for (rowIndex in rowIndices) {
                add(coefficients[rowIndex][columnIndex])
            }
        }

    internal constructor(coefficients: List<List<C>>, toCheckInput: Boolean = true) : this(
        coefficients.size.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        coefficients.first().size.also { if (it == 0) throw IllegalArgumentException("Count of columns must be positive") },
        coefficients,
        toCheckInput = toCheckInput
    )

    internal constructor(vararg coefficients: List<C>, toCheckInput: Boolean) : this(
        coefficients.toList(),
        toCheckInput
    )

    public constructor(countOfRows: Int, countOfColumns: Int, coefficients: List<List<C>>) : this(
        countOfRows,
        countOfColumns,
        coefficients,
        toCheckInput = true
    )

    public constructor(coefficients: List<List<C>>) : this(
        coefficients.size.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        coefficients.first().size.also { if (it == 0) throw IllegalArgumentException("Count of columns must be positive") },
        coefficients,
        toCheckInput = true
    )

    public constructor(vararg coefficients: List<C>) : this(coefficients.toList())
    public constructor(countOfRows: Int, countOfColumns: Int, init: (rowIndex: Int, columnIndex: Int) -> C) : this(
        countOfRows.also { if (it == 0) throw IllegalArgumentException("Count of rows must be positive") },
        countOfColumns.also { if (it == 0) throw IllegalArgumentException("Count of columns must be positive") },
        List(countOfRows) { rowIndex -> List(countOfColumns) { columnIndex -> init(rowIndex, columnIndex) } },
        toCheckInput = false
    )

    public operator fun get(rowIndex: Int, columnIndex: Int): C {
        require(rowIndex in 0 until countOfRows) { "Row index out of bounds: $rowIndex got, in 0..${countOfRows - 1} expected" }
        require(columnIndex in 0 until countOfColumns) { "Column index out of bounds: $columnIndex got, in 0..${countOfColumns - 1} expected" }
        return coefficients[rowIndex][columnIndex]
    }

    public operator fun get(index: Pair<Int, Int>): C = get(index.first, index.second)

    public operator fun get(index: MatrixIndex): C = get(index.rowIndex, index.columnIndex)

    override fun iterator(): Iterator<C> =
        object : Iterator<C> {
            var cursorRow = 0 // row index of next element to return
            var cursorColumn = 0 // column index of next element to return

            override fun hasNext(): Boolean = cursorRow != countOfRows

            override fun next(): C {
                if (cursorRow == countOfRows) throw NoSuchElementException()
                return coefficients[cursorRow][cursorColumn].also {
                    if (cursorColumn == countOfColumns - 1) {
                        cursorColumn = 0
                        cursorRow += 1
                    } else {
                        cursorColumn += 1
                    }
                }
            }
        }

    override fun toString(): String =
        coefficients.joinToString(prefix = "{", postfix = "}") {
            it.joinToString(
                prefix = "{",
                postfix = "}"
            )
        }

    public fun subMatrix(rows: Iterable<Int>, columns: Iterable<Int>): Matrix<C> {
        val cleanedRows = rows.asSequence().apply {
            when { // TODO: Check WTH is this.
                first() < 0 -> throw IllegalArgumentException("Can't select row with index less than 0")
                last() >= countOfRows -> throw IllegalArgumentException("Can't select row with index more than max row index")
            }
        }.distinct().sorted().toList().apply { if (isEmpty()) throw IllegalArgumentException("No row is selected") }
        val cleanedColumns = columns.asSequence().apply {
            when {
                first() < 0 -> throw IllegalArgumentException("Can't select column with index less than 0")
                last() >= countOfRows -> throw IllegalArgumentException("Can't select column with index more than max column index")
            }
        }.distinct().sorted().toList().apply { if (isEmpty()) throw IllegalArgumentException("No column is selected") }
        return Matrix(
            coefficients.slice(cleanedRows)
                .map { it.slice(cleanedColumns) },
            toCheckInput = false
        )
    }

    public open fun transposed(): Matrix<C> =
        Matrix(countOfColumns, countOfRows) { rowIndex, columnIndex -> coefficients[columnIndex][rowIndex] }
}
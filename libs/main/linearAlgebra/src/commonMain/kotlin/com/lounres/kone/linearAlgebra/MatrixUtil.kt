/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra

import com.lounres.kone.algebraic.*
import com.lounres.kone.linearAlgebra.Matrix
import com.lounres.kone.linearAlgebra.MatrixIndex

//// region Operator extensions
//
//// region Field case
//
////operator fun <T : Field<T>> Matrix<T>.div(other: T): Matrix<T> =
////    if (other.isZero()) throw ArithmeticException("/ by zero")
////    else
////        Matrix(
////            countOfRows,
////            countOfColumns,
////            coefficients
////                .map {
////                    it.map {
////                        it / other
////                    }
////                },
////            toCheckInput = false
////        )
//
//// endregion
//
//// endregion
//
//// region Transformations
//
//data class LeftGaussianEliminationResult<T : Field<T>>(
//    val result: Matrix<T>,
//    val leftTransformation: SquareMatrix<T>,
//    val leftInverseTransformation: SquareMatrix<T>,
//)
//
//fun <T : Field<T>> Matrix<T>.leftGaussianEliminationWithTransformation(): LeftGaussianEliminationResult<T> {
//    val ZERO = ringZero
//    val ONE = ringOne
//
//    val resultCoefficients = coefficients.map { it.toMutableList() }.toMutableList()
//    val transformationCoefs =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfRows) { columnIndex -> if (rowIndex == columnIndex) ONE else ZERO } }
//    val inverseTransformationCoefs =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfRows) { columnIndex -> if (rowIndex == columnIndex) ONE else ZERO } }
//
//    var countOfProcessedRows = 0
//    for (columnNow in 0 until countOfColumns) {
//        var coolRow =
//            resultCoefficients
//                .asSequence()
//                .drop(countOfProcessedRows)
//                .indexOfFirst { it[columnNow].isNotZero() }
//        if (coolRow == -1) continue
//
//        coolRow += countOfProcessedRows
//
//        if (coolRow != countOfProcessedRows) {
//            resultCoefficients[coolRow] = resultCoefficients[countOfProcessedRows].also {
//                resultCoefficients[countOfProcessedRows] = resultCoefficients[countOfProcessedRows]
//            }
//            transformationCoefs[coolRow] = transformationCoefs[countOfProcessedRows].also {
//                transformationCoefs[countOfProcessedRows] = transformationCoefs[countOfProcessedRows]
//            }
//            inverseTransformationCoefs[coolRow] = inverseTransformationCoefs[countOfProcessedRows].also {
//                inverseTransformationCoefs[countOfProcessedRows] = inverseTransformationCoefs[countOfProcessedRows]
//            }
//        }
//
//        val coolCoef = resultCoefficients[countOfProcessedRows][columnNow]
//
//        for (col in 0 until countOfColumns) resultCoefficients[countOfProcessedRows][col] /= coolCoef
//        for (col in 0 until countOfRows) {
//            transformationCoefs[countOfProcessedRows][col] /= coolCoef
//            inverseTransformationCoefs[countOfProcessedRows][col] *= coolCoef
//        }
//
//        for (row in 0 until countOfRows) {
//            if (row == countOfProcessedRows) continue
//            val rowCoef = resultCoefficients[row][columnNow]
//            for (col in 0 until countOfColumns) resultCoefficients[row][col] -= resultCoefficients[countOfProcessedRows][col] * rowCoef
//            for (col in 0 until countOfRows) {
//                transformationCoefs[row][col] -= transformationCoefs[countOfProcessedRows][col] * rowCoef
//                inverseTransformationCoefs[countOfProcessedRows][col] += inverseTransformationCoefs[row][col] * rowCoef
//            }
//        }
//
//        ++countOfProcessedRows
//    }
//
//    return LeftGaussianEliminationResult(
//        result = Matrix(
//            countOfRows = countOfRows,
//            countOfColumns = countOfColumns,
//            coefficients = resultCoefficients,
//            toCheckInput = false
//        ),
//        leftTransformation = SquareMatrix(
//            countOfRows = countOfRows,
//            coefficients = transformationCoefs,
//            toCheckInput = false
//        ),
//        leftInverseTransformation = SquareMatrix(
//            countOfRows = countOfRows,
//            coefficients = List(countOfRows) { rowIndex -> List(countOfRows) { columnIndex -> inverseTransformationCoefs[columnIndex][rowIndex] } },
//            toCheckInput = false
//        )
//    )
//}
//
//fun <T : Field<T>> Matrix<T>.leftGaussianElimination(): Matrix<T> {
//    val resultCoefficients = coefficients.map { it.toMutableList() }.toMutableList()
//
//    var countOfProcessedRows = 0
//    for (columnNow in 0 until countOfColumns) {
//        var coolRow =
//            resultCoefficients
//                .asSequence()
//                .drop(countOfProcessedRows)
//                .indexOfFirst { it[columnNow].isNotZero() }
//        if (coolRow == -1) continue
//
//        coolRow += countOfProcessedRows
//
//        if (coolRow != countOfProcessedRows) {
//            resultCoefficients[coolRow] = resultCoefficients[countOfProcessedRows].also {
//                resultCoefficients[countOfProcessedRows] = resultCoefficients[countOfProcessedRows]
//            }
//        }
//
//        val coolCoef = resultCoefficients[countOfProcessedRows][columnNow]
//
//        for (col in 0 until countOfColumns) resultCoefficients[countOfProcessedRows][col] /= coolCoef
//
//        for (row in 0 until countOfRows) {
//            if (row == countOfProcessedRows) continue
//            val rowCoef = resultCoefficients[row][columnNow]
//            for (col in 0 until countOfColumns) resultCoefficients[row][col] -= resultCoefficients[countOfProcessedRows][col] * rowCoef
//        }
//
//        ++countOfProcessedRows
//    }
//
//    return Matrix(
//        countOfRows = countOfRows,
//        countOfColumns = countOfColumns,
//        coefficients = resultCoefficients,
//        toCheckInput = false
//    )
//}
//
//data class RightGaussianEliminationResult<T : Field<T>>(
//    val result: Matrix<T>,
//    val rightTransformation: SquareMatrix<T>,
//    val rightInverseTransformation: SquareMatrix<T>,
//)
//
//fun <T : Field<T>> Matrix<T>.rightGaussianEliminationWithTransformation(): RightGaussianEliminationResult<T> {
//    val countOfRows = this.countOfColumns
//    val countOfColumns = this.countOfRows
//    val ZERO = ringZero
//    val ONE = ringOne
//
//    val resultCoefficients =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfColumns) { columnIndex -> coefficients[columnIndex][rowIndex] } }
//    val transformationCoefs =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfRows) { columnIndex -> if (rowIndex == columnIndex) ONE else ZERO } }
//    val inverseTransformationCoefs =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfRows) { columnIndex -> if (rowIndex == columnIndex) ONE else ZERO } }
//
//    var countOfProcessedRows = 0
//    for (columnNow in 0 until countOfColumns) {
//        var coolRow =
//            resultCoefficients
//                .asSequence()
//                .drop(countOfProcessedRows)
//                .indexOfFirst { it[columnNow].isNotZero() }
//        if (coolRow == -1) continue
//
//        coolRow += countOfProcessedRows
//
//        if (coolRow != countOfProcessedRows) {
//            resultCoefficients[coolRow] = resultCoefficients[countOfProcessedRows].also {
//                resultCoefficients[countOfProcessedRows] = resultCoefficients[countOfProcessedRows]
//            }
//            transformationCoefs[coolRow] = transformationCoefs[countOfProcessedRows].also {
//                transformationCoefs[countOfProcessedRows] = transformationCoefs[countOfProcessedRows]
//            }
//            inverseTransformationCoefs[coolRow] = inverseTransformationCoefs[countOfProcessedRows].also {
//                inverseTransformationCoefs[countOfProcessedRows] = inverseTransformationCoefs[countOfProcessedRows]
//            }
//        }
//
//        val coolCoef = resultCoefficients[countOfProcessedRows][columnNow]
//
//        for (col in 0 until countOfColumns) resultCoefficients[countOfProcessedRows][col] /= coolCoef
//        for (col in 0 until countOfRows) {
//            transformationCoefs[countOfProcessedRows][col] /= coolCoef
//            inverseTransformationCoefs[countOfProcessedRows][col] *= coolCoef
//        }
//
//        for (row in 0 until countOfRows) {
//            if (row == countOfProcessedRows) continue
//            val rowCoef = resultCoefficients[row][columnNow]
//            for (col in 0 until countOfColumns) resultCoefficients[row][col] -= resultCoefficients[countOfProcessedRows][col] * rowCoef
//            for (col in 0 until countOfRows) {
//                transformationCoefs[row][col] -= transformationCoefs[countOfProcessedRows][col] * rowCoef
//                inverseTransformationCoefs[countOfProcessedRows][col] += inverseTransformationCoefs[row][col] * rowCoef
//            }
//        }
//
//        ++countOfProcessedRows
//    }
//
//    return RightGaussianEliminationResult(
//        result = Matrix(
//            countOfRows = countOfColumns,
//            countOfColumns = countOfRows,
//            coefficients = List(countOfColumns) { rowIndex -> List(countOfRows) { columnIndex -> resultCoefficients[columnIndex][rowIndex] } },
//            toCheckInput = false
//        ),
//        rightTransformation = SquareMatrix(
//            countOfRows = countOfRows,
//            coefficients = List(countOfRows) { rowIndex -> List(countOfRows) { columnIndex -> transformationCoefs[columnIndex][rowIndex] } },
//            toCheckInput = false
//        ),
//        rightInverseTransformation = SquareMatrix(
//            countOfRows = countOfRows,
//            coefficients = inverseTransformationCoefs,
//            toCheckInput = false
//        )
//    )
//}
//
//fun <T : Field<T>> Matrix<T>.rightGaussianElimination(): Matrix<T> {
//    val countOfRows = this.countOfColumns
//    val countOfColumns = this.countOfRows
//
//    val resultCoefficients =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfColumns) { columnIndex -> coefficients[columnIndex][rowIndex] } }
//
//    var countOfProcessedRows = 0
//    for (columnNow in 0 until countOfColumns) {
//        var coolRow =
//            resultCoefficients
//                .asSequence()
//                .drop(countOfProcessedRows)
//                .indexOfFirst { it[columnNow].isNotZero() }
//        if (coolRow == -1) continue
//
//        coolRow += countOfProcessedRows
//
//        if (coolRow != countOfProcessedRows) {
//            resultCoefficients[coolRow] = resultCoefficients[countOfProcessedRows].also {
//                resultCoefficients[countOfProcessedRows] = resultCoefficients[countOfProcessedRows]
//            }
//        }
//
//        val coolCoef = resultCoefficients[countOfProcessedRows][columnNow]
//
//        for (col in 0 until countOfColumns) resultCoefficients[countOfProcessedRows][col] /= coolCoef
//
//        for (row in 0 until countOfRows) {
//            if (row == countOfProcessedRows) continue
//            val rowCoef = resultCoefficients[row][columnNow]
//            for (col in 0 until countOfColumns) resultCoefficients[row][col] -= resultCoefficients[countOfProcessedRows][col] * rowCoef
//        }
//
//        ++countOfProcessedRows
//    }
//
//    return Matrix(
//        countOfRows = countOfColumns,
//        countOfColumns = countOfRows,
//        coefficients = List(countOfColumns) { rowIndex -> List(countOfRows) { columnIndex -> resultCoefficients[columnIndex][rowIndex] } },
//        toCheckInput = false
//    )
//}
//
//data class DiagonalizationResult<T : Field<T>>(
//    val result: Matrix<T>,
//    val leftTransformation: SquareMatrix<T>,
//    val rightTransformation: SquareMatrix<T>,
//    val leftInverseTransformation: SquareMatrix<T>,
//    val rightInverseTransformation: SquareMatrix<T>,
//)
//
//fun <T : Field<T>> Matrix<T>.diagonalizedWithTransformations(): DiagonalizationResult<T> {
//    val (leftGaussedResult, leftTransformation, leftInverseTransformation) = leftGaussianEliminationWithTransformation()
//    val (result, rightTransformation, rightInverseTransformation) = leftGaussedResult.rightGaussianEliminationWithTransformation()
//    return DiagonalizationResult(
//        result = result,
//        leftTransformation = leftTransformation,
//        rightTransformation = rightTransformation,
//        leftInverseTransformation = leftInverseTransformation,
//        rightInverseTransformation = rightInverseTransformation
//    )
//}
//
//fun <T : Field<T>> Matrix<T>.diagonalized(): Matrix<T> = this.leftGaussianElimination().rightGaussianElimination()
//
//// TODO: Generate same functions for other types of matrices
//
//// endregion
//
//// region Collections
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.getOrElse(rowIndex: Int, columnIndex: Int, defaultValue: (Int, Int) -> T): T =
//    when {
//        rowIndex !in 0 until countOfRows -> defaultValue(rowIndex, columnIndex)
//        columnIndex !in 0 until countOfColumns -> defaultValue(rowIndex, columnIndex)
//        else -> coefficients[rowIndex][columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.getOrElse(index: Pair<Int, Int>, defaultValue: (Pair<Int, Int>) -> T): T =
//    when {
//        index.first !in 0 until countOfRows -> defaultValue(index)
//        index.second !in 0 until countOfColumns -> defaultValue(index)
//        else -> coefficients[index.first][index.second]
//    }
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.getOrElse(index: MatrixIndex, defaultValue: (MatrixIndex) -> T): T =
//    when {
//        index.rowIndex !in 0 until countOfRows -> defaultValue(index)
//        index.columnIndex !in 0 until countOfColumns -> defaultValue(index)
//        else -> coefficients[index.rowIndex][index.columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> Matrix<T>.getOrNull(rowIndex: Int, columnIndex: Int): T? =
//    when {
//        rowIndex !in 0 until countOfRows -> null
//        columnIndex !in 0 until countOfColumns -> null
//        else -> coefficients[rowIndex][columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> Matrix<T>.getOrNull(index: Pair<Int, Int>): T? =
//    when {
//        index.first !in 0 until countOfRows -> null
//        index.second !in 0 until countOfColumns -> null
//        else -> coefficients[index.first][index.second]
//    }
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> Matrix<T>.getOrNull(index: MatrixIndex): T? =
//    when {
//        index.rowIndex !in 0 until countOfRows -> null
//        index.columnIndex !in 0 until countOfColumns -> null
//        else -> coefficients[index.rowIndex][index.columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or throws an [IndexOutOfBoundsException] if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAt(rowIndex: Int, columnIndex: Int): T = get(rowIndex, columnIndex)
//
/////**
//// * Returns an element at the given [index] or throws an [IndexOutOfBoundsException] if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAt(index: Pair<Int, Int>): T = get(index)
//
/////**
//// * Returns an element at the given [index] or throws an [IndexOutOfBoundsException] if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAt(index: MatrixIndex): T = get(index)
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrElse(
//    rowIndex: Int,
//    columnIndex: Int,
//    defaultValue: (Int, Int) -> T
//): T =
//    getOrElse(rowIndex, columnIndex, defaultValue)
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrElse(
//    index: Pair<Int, Int>,
//    defaultValue: (Pair<Int, Int>) -> T
//): T =
//    getOrElse(index, defaultValue)
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrElse(index: MatrixIndex, defaultValue: (MatrixIndex) -> T): T =
//    getOrElse(index, defaultValue)
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrNull(rowIndex: Int, columnIndex: Int): T? =
//    getOrNull(rowIndex, columnIndex)
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrNull(index: Pair<Int, Int>): T? = getOrNull(index)
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//@Suppress("NOTHING_TO_INLINE")
//inline fun <T : Ring<T>> Matrix<T>.matrixElementAtOrNull(index: MatrixIndex): T? = getOrNull(index)
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> Matrix<T>.mapMatrix(crossinline transform: (T) -> S): Matrix<S> =
//    Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex -> transform(coefficients[rowIndex][columnIndex]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> Matrix<T>.mapMatrixIndexed(crossinline transform: (rowIndex: Int, columnIndex: Int, T) -> S): Matrix<S> =
//    Matrix(countOfRows, countOfColumns) { rowIndex, columnIndex ->
//        transform(
//            rowIndex,
//            columnIndex,
//            coefficients[rowIndex][columnIndex]
//        )
//    }
//
/////**
//// * Returns a lazy [Iterable] that wraps each element of the original collection
//// * into an [IndexedValue] containing the index of that element and the element itself.
//// */
//fun <T : Ring<T>> Matrix<T>.withMatrixIndex(): Iterable<IndexedMatrixValue<T>> =
//    object : Iterable<IndexedMatrixValue<T>> {
//        override fun iterator(): Iterator<IndexedMatrixValue<T>> =
//            object : Iterator<IndexedMatrixValue<T>> {
//                var cursorRow = 0 // row index of next element to return
//                var cursorColumn = 0 // column index of next element to return
//
//                override fun hasNext(): Boolean = cursorRow != countOfRows
//
//                override fun next(): IndexedMatrixValue<T> {
//                    if (cursorRow == countOfRows) throw NoSuchElementException()
//                    return IndexedMatrixValue(
//                        MatrixIndex(cursorRow, cursorColumn),
//                        coefficients[cursorRow][cursorColumn].also {
//                            if (cursorColumn == countOfColumns - 1) {
//                                cursorColumn = 0
//                                cursorRow += 1
//                            } else {
//                                cursorColumn += 1
//                            }
//                        }
//                    )
//                }
//            }
//    }
//
/////**
//// * Returns `true` if all elements match the given [predicate].
//// */
//inline fun <T : Ring<T>> Matrix<T>.allMatrix(predicate: (T) -> Boolean): Boolean {
//    for (element in this) if (!predicate(element)) return false
//    return true
//}
//
/////**
//// * Returns `true` if all elements match the given [predicate].
//// */
//inline fun <T : Ring<T>> Matrix<T>.allMatrixIndexed(predicate: (rowIndex: Int, columnIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (!predicate(
//            index.rowIndex,
//            index.columnIndex,
//            element
//        )
//    ) return false
//    return true
//}
//
/////**
//// * Returns `true` if at least one element matches the given [predicate].
//// */
//inline fun <T : Ring<T>> Matrix<T>.anyMatrix(predicate: (element: T) -> Boolean): Boolean {
//    for (element in this) if (predicate(element)) return true
//    return false
//}
//
/////**
//// * Returns `true` if at least one element matches the given [predicate].
//// */
//inline fun <T : Ring<T>> Matrix<T>.anyMatrixIndexed(predicate: (rowIndex: Int, columnIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (predicate(
//            index.rowIndex,
//            index.columnIndex,
//            element
//        )
//    ) return true
//    return false
//}
//
/////**
//// * Accumulates value starting with [initial] value and applying [operation] from left to right
//// * to current accumulator value and each element.
//// *
//// * Returns the specified [initial] value if the collection is empty.
//// *
//// * @param [operation] function that takes current accumulator value and an element, and calculates the next accumulator value.
//// */
//inline fun <T : Ring<T>, R> Matrix<T>.foldMatrix(initial: R, operation: (acc: R, T) -> R): R {
//    var accumulator = initial
//    for (element in this) accumulator = operation(accumulator, element)
//    return accumulator
//}
//
/////**
//// * Accumulates value starting with [initial] value and applying [operation] from left to right
//// * to current accumulator value and each element with its index in the original collection.
//// *
//// * Returns the specified [initial] value if the collection is empty.
//// *
//// * @param [operation] function that takes the index of an element, current accumulator value
//// * and the element itself, and calculates the next accumulator value.
//// */
//inline fun <T : Ring<T>, R> Matrix<T>.foldMatrixIndexed(
//    initial: R,
//    operation: (rowIndex: Int, columnIndex: Int, acc: R, T) -> R
//): R {
//    var accumulator = initial
//    for ((index, element) in this.withMatrixIndex()) accumulator =
//        operation(index.rowIndex, index.columnIndex, accumulator, element)
//    return accumulator
//}
//
/////**
//// * Performs the given [action] on each element.
//// */
//inline fun <T : Ring<T>> Matrix<T>.forMatrixEach(action: (T) -> Unit) {
//    for (element in this) action(element)
//}
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> Matrix<T>.forMatrixEachIndexed(action: (rowIndex: Int, columnIndex: Int, T) -> Unit) {
//    for ((index, item) in this.withMatrixIndex()) action(index.rowIndex, index.columnIndex, item)
//}
//
/////**
//// * Performs the given [action] on each element and returns the collection itself afterwards.
//// */
//inline fun <T : Ring<T>> Matrix<T>.onMatrixEach(action: (T) -> Unit): Matrix<T> = apply { forMatrixEach(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> Matrix<T>.onMatrixEachIndexed(action: (rowIndex: Int, columnIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
//// endregion
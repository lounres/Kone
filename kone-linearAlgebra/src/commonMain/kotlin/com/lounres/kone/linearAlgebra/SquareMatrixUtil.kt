/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra


//// region Operator extensions
//
//// region Field case
//
//operator fun <T : Field<T>> SquareMatrix<T>.div(other: T): SquareMatrix<T> =
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    else
//        SquareMatrix(countOfRows) { rowIndex, columnIndex -> coefficients[rowIndex][columnIndex] / other }
//
//fun <T : Field<T>> SquareMatrix<T>.reciprocalOrNull(): SquareMatrix<T>? {
//    val coefficients2 = coefficients.map { it.toMutableList() }.toMutableList()
//    val ZERO = ringZero
//    val ONE = ringOne
//    val resultCoefficients =
//        MutableList(countOfRows) { rowIndex -> MutableList(countOfRows) { columnIndex -> if (rowIndex == columnIndex) ONE else ZERO } }
//
//    for (columnNow in 0 until countOfColumns) {
//        var coolRow = coefficients2.asSequence().drop(columnNow).indexOfFirst { it[columnNow].isNotZero() }
//        if (coolRow == -1) return null
//
//        coolRow += columnNow
//
//        if (coolRow != columnNow) {
//            coefficients2[coolRow] = coefficients2[columnNow].also { coefficients2[columnNow] = coefficients2[coolRow] }
//            resultCoefficients[coolRow] =
//                resultCoefficients[columnNow].also { resultCoefficients[columnNow] = resultCoefficients[coolRow] }
//        }
//
//        val coolCoef = coefficients2[columnNow][columnNow]
//
//        for (col in 0 until countOfColumns) {
//            coefficients2[columnNow][col] /= coolCoef
//            resultCoefficients[columnNow][col] /= coolCoef
//        }
//
//        for (row in 0 until countOfRows) {
//            if (row == columnNow) continue
//            val rowCoef = coefficients2[row][columnNow]
//            for (col in 0 until countOfColumns) {
//                coefficients2[row][col] = coefficients2[row][col] - coefficients2[columnNow][col] * rowCoef
//                resultCoefficients[row][col] =
//                    resultCoefficients[row][col] - resultCoefficients[columnNow][col] * rowCoef
//            }
//        }
//    }
//
//    return SquareMatrix(
//        countOfRows,
//        resultCoefficients,
//        toCheckInput = false
//    )
//}
//
//fun <T : Field<T>> SquareMatrix<T>.reciprocal(): SquareMatrix<T> =
//    reciprocalOrNull() ?: throw IllegalArgumentException("The square matrix has no reciprocal")
//
//// endregion
//
//// Constants
//
//operator fun <T : Ring<T>> T.times(other: SquareMatrix<T>): SquareMatrix<T> =
//    SquareMatrix(other.countOfRows) { rowIndex, columnIndex -> this * other.coefficients[rowIndex][columnIndex] }
//
//operator fun <T : Ring<T>> Integer.times(other: SquareMatrix<T>): SquareMatrix<T> =
//    SquareMatrix(other.countOfRows) { rowIndex, columnIndex -> this * other.coefficients[rowIndex][columnIndex] }
//
//operator fun <T : Ring<T>> Int.times(other: SquareMatrix<T>): SquareMatrix<T> =
//    SquareMatrix(other.countOfRows) { rowIndex, columnIndex -> this * other.coefficients[rowIndex][columnIndex] }
//
//operator fun <T : Ring<T>> Long.times(other: SquareMatrix<T>): SquareMatrix<T> =
//    SquareMatrix(other.countOfRows) { rowIndex, columnIndex -> this * other.coefficients[rowIndex][columnIndex] }
//
//// endregion
//
//// endregion
//
//// region Collections
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> SquareMatrix<T>.mapMatrix(crossinline transform: (T) -> S): SquareMatrix<S> =
//    SquareMatrix(countOfRows) { rowIndex, columnIndex -> transform(coefficients[rowIndex][columnIndex]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> SquareMatrix<T>.mapMatrixIndexed(crossinline transform: (rowIndex: Int, columnIndex: Int, T) -> S): SquareMatrix<S> =
//    SquareMatrix(countOfRows) { rowIndex, columnIndex ->
//        transform(
//            rowIndex,
//            columnIndex,
//            coefficients[rowIndex][columnIndex]
//        )
//    }
//
/////**
//// * Performs the given [action] on each element and returns the collection itself afterwards.
//// */
//inline fun <T : Ring<T>> SquareMatrix<T>.onMatrixEach(action: (T) -> Unit): Matrix<T> = apply { forMatrixEach(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> SquareMatrix<T>.onMatrixEachIndexed(action: (rowIndex: Int, columnIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
//// endregion
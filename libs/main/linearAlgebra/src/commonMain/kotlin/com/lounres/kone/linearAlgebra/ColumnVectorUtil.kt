/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra

import com.lounres.kone.algebraic.*
import com.lounres.kone.linearAlgebra.ColumnVector

//// region Operator extensions
//
//// region Field case
//
//operator fun <T : Field<T>> ColumnVector<T>.div(other: T): ColumnVector<T> =
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    else
//        ColumnVector(
//            countOfRows,
//            coefficients
//                .map {
//                    it.map {
//                        it / other
//                    }
//                },
//            toCheckInput = false
//        )
//
//// endregion
//
//// Constants
//
//operator fun <T : Ring<T>> T.times(other: ColumnVector<T>): ColumnVector<T> =
//    ColumnVector(
//        other.countOfRows,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Integer.times(other: ColumnVector<T>): ColumnVector<T> =
//    ColumnVector(
//        other.countOfRows,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Int.times(other: ColumnVector<T>): ColumnVector<T> =
//    ColumnVector(
//        other.countOfRows,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Long.times(other: ColumnVector<T>): ColumnVector<T> =
//    ColumnVector(
//        other.countOfRows,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//// endregion
//
//// endregion
//
//// region Collections
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.getOrElse(rowIndex: Int, defaultValue: (Int) -> T): T =
//    when {
//        rowIndex !in 0 until countOfRows -> defaultValue(rowIndex)
//        else -> coefficients[rowIndex][0]
//    }
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> ColumnVector<T>.getOrNull(rowIndex: Int): T? =
//    when {
//        rowIndex !in 0 until countOfRows -> null
//        else -> coefficients[rowIndex][0]
//    }
//
/////**
//// * Returns an element at the given [index] or throws an [IndexOutOfBoundsException] if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> ColumnVector<T>.matrixElementAt(rowIndex: Int): T = get(rowIndex)
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.matrixElementAtOrElse(rowIndex: Int, defaultValue: (Int) -> T): T =
//    getOrElse(rowIndex, defaultValue)
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> ColumnVector<T>.matrixElementAtOrNull(rowIndex: Int): T? = getOrNull(rowIndex)
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> ColumnVector<T>.mapMatrix(crossinline transform: (T) -> S): ColumnVector<S> =
//    ColumnVector(countOfColumns) { rowIndex, columnIndex -> transform(coefficients[rowIndex][columnIndex]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> ColumnVector<T>.mapMatrixIndexed(crossinline transform: (rowIndex: Int, T) -> S): ColumnVector<S> =
//    ColumnVector(countOfColumns) { rowIndex -> transform(rowIndex, coefficients[rowIndex][0]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> ColumnVector<T>.mapMatrixIndexed(crossinline transform: (rowIndex: Int, columnIndex: Int, T) -> S): ColumnVector<S> =
//    ColumnVector(countOfColumns) { rowIndex, columnIndex ->
//        transform(
//            rowIndex,
//            columnIndex,
//            coefficients[rowIndex][columnIndex]
//        )
//    }
//
/////**
//// * Returns `true` if all elements match the given [predicate].
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.allMatrixIndexed(predicate: (rowIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (!predicate(index.rowIndex, element)) return false
//    return true
//}
//
/////**
//// * Returns `true` if at least one element matches the given [predicate].
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.anyMatrixIndexed(predicate: (rowIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (predicate(index.rowIndex, element)) return true
//    return false
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
//inline fun <T : Ring<T>, R> ColumnVector<T>.foldMatrixIndexed(
//    initial: R,
//    operation: (rowIndex: Int, acc: R, T) -> R
//): R {
//    var accumulator = initial
//    for ((index, element) in this.withMatrixIndex()) accumulator = operation(index.rowIndex, accumulator, element)
//    return accumulator
//}
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.forMatrixEachIndexed(action: (rowIndex: Int, T) -> Unit) {
//    for ((index, item) in this.withMatrixIndex()) action(index.rowIndex, item)
//}
//
/////**
//// * Performs the given [action] on each element and returns the collection itself afterwards.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.onMatrixEach(action: (T) -> Unit): Matrix<T> = apply { forMatrixEach(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.onMatrixEachIndexed(action: (rowIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> ColumnVector<T>.onMatrixEachIndexed(action: (rowIndex: Int, columnIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
//// endregion
/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra

//// region Operator extensions
//
//// region Field case
//
//operator fun <T : Field<T>> RowVector<T>.div(other: T): RowVector<T> =
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    else
//        RowVector(
//            countOfColumns,
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
//operator fun <T : Ring<T>> T.times(other: RowVector<T>): RowVector<T> =
//    RowVector(
//        other.countOfColumns,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Integer.times(other: RowVector<T>): RowVector<T> =
//    RowVector(
//        other.countOfColumns,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Int.times(other: RowVector<T>): RowVector<T> =
//    RowVector(
//        other.countOfColumns,
//        other.coefficients
//            .map {
//                it.map {
//                    this * it
//                }
//            },
//        toCheckInput = false
//    )
//
//operator fun <T : Ring<T>> Long.times(other: RowVector<T>): RowVector<T> =
//    RowVector(
//        other.countOfColumns,
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
//inline fun <T : Ring<T>> RowVector<T>.getOrElse(columnIndex: Int, defaultValue: (Int) -> T): T =
//    when {
//        columnIndex !in 0 until countOfColumns -> defaultValue(columnIndex)
//        else -> coefficients[0][columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> RowVector<T>.getOrNull(columnIndex: Int): T? =
//    when {
//        columnIndex !in 0 until countOfColumns -> null
//        else -> coefficients[0][columnIndex]
//    }
//
/////**
//// * Returns an element at the given [index] or throws an [IndexOutOfBoundsException] if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> RowVector<T>.matrixElementAt(columnIndex: Int): T = get(columnIndex)
//
/////**
//// * Returns an element at the given [index] or the result of calling the [defaultValue] function if the [index] is out of bounds of this list.
//// */
//inline fun <T : Ring<T>> RowVector<T>.matrixElementAtOrElse(columnIndex: Int, defaultValue: (Int) -> T): T =
//    getOrElse(columnIndex, defaultValue)
//
/////**
//// * Returns an element at the given [index] or `null` if the [index] is out of bounds of this list.
//// */
//fun <T : Ring<T>> RowVector<T>.matrixElementAtOrNull(columnIndex: Int): T? = getOrNull(columnIndex)
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> RowVector<T>.mapMatrix(crossinline transform: (T) -> S): RowVector<S> =
//    RowVector(countOfColumns) { rowIndex, columnIndex -> transform(coefficients[rowIndex][columnIndex]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> RowVector<T>.mapMatrixIndexed(crossinline transform: (columnIndex: Int, T) -> S): RowVector<S> =
//    RowVector(countOfColumns) { columnIndex -> transform(columnIndex, coefficients[0][columnIndex]) }
//
///**
// * Returns a matrix containing the results of applying the given [transform] function
// * to each element in the original matrix.
// */
//inline fun <T : Ring<T>, S : Ring<S>> RowVector<T>.mapMatrixIndexed(crossinline transform: (rowIndex: Int, columnIndex: Int, T) -> S): RowVector<S> =
//    RowVector(countOfColumns) { rowIndex, columnIndex ->
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
//inline fun <T : Ring<T>> RowVector<T>.allMatrixIndexed(predicate: (columnIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (!predicate(index.columnIndex, element)) return false
//    return true
//}
//
/////**
//// * Returns `true` if at least one element matches the given [predicate].
//// */
//inline fun <T : Ring<T>> RowVector<T>.anyMatrixIndexed(predicate: (columnIndex: Int, T) -> Boolean): Boolean {
//    for ((index, element) in this.withMatrixIndex()) if (predicate(index.columnIndex, element)) return true
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
//inline fun <T : Ring<T>, R> RowVector<T>.foldMatrixIndexed(
//    initial: R,
//    operation: (columnIndex: Int, acc: R, T) -> R
//): R {
//    var accumulator = initial
//    for ((index, element) in this.withMatrixIndex()) accumulator = operation(index.columnIndex, accumulator, element)
//    return accumulator
//}
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> RowVector<T>.forMatrixEachIndexed(action: (columnIndex: Int, T) -> Unit) {
//    for ((index, item) in this.withMatrixIndex()) action(index.columnIndex, item)
//}
//
/////**
//// * Performs the given [action] on each element and returns the collection itself afterwards.
//// */
//inline fun <T : Ring<T>> RowVector<T>.onMatrixEach(action: (T) -> Unit): Matrix<T> = apply { forMatrixEach(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> RowVector<T>.onMatrixEachIndexed(action: (columnIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
/////**
//// * Performs the given [action] on each element, providing sequential index with the element,
//// * and returns the collection itself afterwards.
//// * @param [action] function that takes the index of an element and the element itself
//// * and performs the action on the element.
//// */
//inline fun <T : Ring<T>> RowVector<T>.onMatrixEachIndexed(action: (rowIndex: Int, columnIndex: Int, T) -> Unit): Matrix<T> =
//    apply { forMatrixEachIndexed(action) }
//
//// endregion
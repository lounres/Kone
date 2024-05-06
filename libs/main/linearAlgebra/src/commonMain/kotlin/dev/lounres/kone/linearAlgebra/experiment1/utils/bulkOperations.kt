/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.linearAlgebra.experiment1.ColumnVector
import dev.lounres.kone.linearAlgebra.experiment1.Matrix
import dev.lounres.kone.linearAlgebra.experiment1.RowVector
import dev.lounres.kone.multidimensionalCollections.experiment1.utils.*


public inline fun <E> ColumnVector<E>.forEach(block: (value: E) -> Unit) {
    coefficients.forEach(block)
}

public inline fun <E> RowVector<E>.forEach(block: (value: E) -> Unit) {
    coefficients.forEach(block)
}

public inline fun <E> Matrix<E>.forEach(block: (value: E) -> Unit) {
    coefficients.forEach(block)
}

public inline fun <E> ColumnVector<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    coefficients.forEachIndexed(block)
}

public inline fun <E> RowVector<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    coefficients.forEachIndexed(block)
}

public inline fun <E> Matrix<E>.forEachIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Unit) {
    coefficients.forEachIndexed(block)
}

public inline fun <E> ColumnVector<E>.withEach(block: E.() -> Unit) {
    coefficients.withEach(block)
}

public inline fun <E> RowVector<E>.withEach(block: E.() -> Unit) {
    coefficients.withEach(block)
}

public inline fun <E> Matrix<E>.withEach(block: E.() -> Unit) {
    coefficients.withEach(block)
}

public inline fun <E> ColumnVector<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    coefficients.withEachIndexed(block)
}

public inline fun <E> RowVector<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    coefficients.withEachIndexed(block)
}

public inline fun <E> Matrix<E>.withEachIndexed(block: E.(rowIndex: UInt, columnIndex: UInt) -> Unit) {
    coefficients.withEachIndexed(block)
}

public inline fun <E> ColumnVector<E>.any(block: (value: E) -> Boolean): Boolean = coefficients.any(block)

public inline fun <E> RowVector<E>.any(block: (value: E) -> Boolean): Boolean = coefficients.any(block)

public inline fun <E> Matrix<E>.any(block: (value: E) -> Boolean): Boolean = coefficients.any(block)

public inline fun <E> ColumnVector<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.anyIndexed(block)

public inline fun <E> RowVector<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.anyIndexed(block)

public inline fun <E> Matrix<E>.anyIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean = coefficients.anyIndexed(block)

public inline fun <E> ColumnVector<E>.all(block: (value: E) -> Boolean): Boolean = coefficients.all(block)

public inline fun <E> RowVector<E>.all(block: (value: E) -> Boolean): Boolean = coefficients.all(block)

public inline fun <E> Matrix<E>.all(block: (value: E) -> Boolean): Boolean = coefficients.all(block)

public inline fun <E> ColumnVector<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.allIndexed(block)

public inline fun <E> RowVector<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.allIndexed(block)

public inline fun <E> Matrix<E>.allIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean = coefficients.allIndexed(block)

public inline fun <E> ColumnVector<E>.none(block: (value: E) -> Boolean): Boolean = coefficients.none(block)

public inline fun <E> RowVector<E>.none(block: (value: E) -> Boolean): Boolean = coefficients.none(block)

public inline fun <E> Matrix<E>.none(block: (value: E) -> Boolean): Boolean = coefficients.none(block)

public inline fun <E> ColumnVector<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.noneIndexed(block)

public inline fun <E> RowVector<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coefficients.noneIndexed(block)

public inline fun <E> Matrix<E>.noneIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean = coefficients.noneIndexed(block)

public inline fun <E, R> ColumnVector<E>.map(transform: (E) -> R): ColumnVector<R> = ColumnVector(coefficients.map(transform))

public inline fun <E, R> ColumnVector<E>.mapIndexed(transform: (index: UInt, E) -> R): ColumnVector<R> = ColumnVector(coefficients.mapIndexed(transform))

public inline fun <E, R> RowVector<E>.map(transform: (E) -> R): RowVector<R> = RowVector(coefficients.map(transform))

public inline fun <E, R> RowVector<E>.mapIndexed(transform: (index: UInt, E) -> R): RowVector<R> = RowVector(coefficients.mapIndexed(transform))

public inline fun <E, R> Matrix<E>.map(transform: (E) -> R): Matrix<R> = Matrix(coefficients.map(transform))

public inline fun <E, R> Matrix<E>.mapIndexed(transform: (rowIndex: UInt, columnIndex: UInt, E) -> R): Matrix<R> = Matrix(coefficients.mapIndexed(transform))

public inline fun <E, R> ColumnVector<E>.fold(initial: R, operation: (acc: R, E) -> R): R = coefficients.fold(initial, operation)

public inline fun <E, R> RowVector<E>.fold(initial: R, operation: (acc: R, E) -> R): R = coefficients.fold(initial, operation)

public inline fun <E, R> Matrix<E>.fold(initial: R, operation: (acc: R, E) -> R): R = coefficients.fold(initial, operation)

public inline fun <E, R> ColumnVector<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R = coefficients.foldIndexed(initial, operation)

public inline fun <E, R> RowVector<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R = coefficients.foldIndexed(initial, operation)

public inline fun <E, R> Matrix<E>.foldIndexed(initial: R, operation: (rowIndex: UInt, columnIndex: UInt, acc: R, E) -> R): R = coefficients.foldIndexed(initial, operation)

// TODO: Add `reduce`-like extensions

context(Ring<E>)
public fun <E> ColumnVector<E>.sum(): E = coefficients.sum()

context(Ring<E>)
public fun <E> RowVector<E>.sum(): E = coefficients.sum()

context(Ring<E>)
public fun <E> Matrix<E>.sum(): E = coefficients.sum()

context(Ring<A>)
public inline fun <E, A> ColumnVector<E>.sumOf(selector: (E) -> A): A = coefficients.sumOf(selector)

context(Ring<A>)
public inline fun <E, A> RowVector<E>.sumOf(selector: (E) -> A): A = coefficients.sumOf(selector)

context(Ring<A>)
public inline fun <E, A> Matrix<E>.sumOf(selector: (E) -> A): A = coefficients.sumOf(selector)

context(Ring<A>)
public inline fun <E, A> ColumnVector<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = coefficients.sumOfIndexed(selector)

context(Ring<A>)
public inline fun <E, A> RowVector<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = coefficients.sumOfIndexed(selector)

context(Ring<A>)
public inline fun <E, A> Matrix<E>.sumOfIndexed(selector: (rowIndex: UInt, columnIndex: UInt, E) -> A): A = coefficients.sumOfIndexed(selector)

// TODO: Add bulk operations
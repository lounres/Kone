/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.columnIndices
import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDList
import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.indices
import dev.lounres.kone.multidimensionalCollections.experiment1.rowIndices


public inline fun <E> MDList<E>.forEach(block: (value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(get(index))
}

public inline fun <E> MDList<E>.forEachIndexed(block: (index: KoneUIntArray, value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(index, get(index))
}

public inline fun <E> MDList1<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    for (index in indices) block(index, get(index))
}

public inline fun <E> MDList2<E>.forEachIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Unit) {
    for (row in rowIndices) for (column in columnIndices) block(row, column, get(row, column))
}

public inline fun <E> MDList<E>.withEach(block: E.() -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block()
}

public inline fun <E> MDList<E>.withEachIndexed(block: E.(index: KoneUIntArray) -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block(index)
}

public inline fun <E> MDList1<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    for (index in indices) get(index).block(index)
}

public inline fun <E> MDList2<E>.withEachIndexed(block: E.(rowIndex: UInt, columnIndex: UInt) -> Unit) {
    for (row in rowIndices) for (column in columnIndices) get(row, column).block(row, column)
}

public inline fun <E> MDList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return true
    return false
}

public inline fun <E> MDList<E>.anyIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return true
    return false
}

public inline fun <E> MDList1<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in indices) if (block(index, get(index))) return true
    return false
}

public inline fun <E> MDList2<E>.anyIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean {
    for (row in rowIndices) for (column in columnIndices) if (block(row, column, get(row, column))) return true
    return false
}

public inline fun <E> MDList<E>.all(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (!block(get(index))) return false
    return true
}

public inline fun <E> MDList<E>.allIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (!block(index, get(index))) return false
    return true
}

public inline fun <E> MDList1<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in indices) if (!block(index, get(index))) return false
    return true
}

public inline fun <E> MDList2<E>.allIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean {
    for (row in rowIndices) for (column in columnIndices) if (!block(row, column, get(row, column))) return false
    return true
}

public inline fun <E> MDList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return false
    return true
}

public inline fun <E> MDList<E>.noneIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return false
    return true
}

public inline fun <E> MDList1<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in indices) if (block(index, get(index))) return false
    return true
}

public inline fun <E> MDList2<E>.noneIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Boolean): Boolean {
    for (row in rowIndices) for (column in columnIndices) if (block(row, column, get(row, column))) return false
    return true
}

public inline fun <E, R> MDList<E>.map(transform: (E) -> R): MDList<R> =
    ArrayMDList(shape) { transform(get(it)) }

public inline fun <E, R> MDList<E>.mapIndexed(transform: (index: KoneUIntArray, E) -> R): MDList<R> =
    ArrayMDList(shape) { transform(it, get(it)) }

public inline fun <E, R> MDList1<E>.map(transform: (E) -> R): MDList1<R> =
    ArrayMDList1(size) { transform(get(it)) }

public inline fun <E, R> MDList1<E>.mapIndexed(transform: (index: UInt, E) -> R): MDList1<R> =
    ArrayMDList1(size) { transform(it, get(it)) }

public inline fun <E, R> MDList2<E>.map(transform: (E) -> R): MDList2<R> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> transform(get(row, column)) }

public inline fun <E, R> MDList2<E>.mapIndexed(transform: (rowIndex: UInt, columnIndex: UInt, E) -> R): MDList2<R> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> transform(row, column, get(row, column)) }

public inline fun <E, R> MDList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in ShapeStrides(shape)) accumulator = operation(accumulator, get(index))
    return accumulator
}

public inline fun <E, R> MDList<E>.foldIndexed(initial: R, operation: (index: KoneUIntArray, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in ShapeStrides(shape)) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

public inline fun <E, R> MDList1<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in indices) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

public inline fun <E, R> MDList2<E>.foldIndexed(initial: R, operation: (rowIndex: UInt, columnIndex: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (row in rowIndices) for (column in columnIndices) accumulator = operation(row, column, accumulator, get(row, column))
    return accumulator
}

// TODO: Add `reduce`-like extensions

context(Ring<E>)
public fun <E> MDList<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<A>)
public inline fun <E, A> MDList<E>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<A>)
public inline fun <E, A> MDList<E>.sumOfIndexed(selector: (index: KoneUIntArray, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }

context(Ring<A>)
public inline fun <E, A> MDList1<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index: UInt, acc, e -> acc + selector(index, e) }

context(Ring<A>)
public inline fun <E, A> MDList2<E>.sumOfIndexed(selector: (rowIndex: UInt, columnIndex: UInt, E) -> A): A = foldIndexed(zero) { row, column, acc, e -> acc + selector(row, column, e) }

// TODO: Add bulk operations
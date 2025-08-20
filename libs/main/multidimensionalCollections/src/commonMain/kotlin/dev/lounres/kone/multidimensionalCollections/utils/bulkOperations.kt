/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.utils

import dev.lounres.kone.algebraic.Monoid
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.MDSizeStrides
import dev.lounres.kone.multidimensionalCollections.columnIndices
import dev.lounres.kone.multidimensionalCollections.contentSize
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
import dev.lounres.kone.multidimensionalCollections.indices
import dev.lounres.kone.multidimensionalCollections.rowIndices


public inline fun <E> MDList<E>.forEach(block: (value: E) -> Unit) {
    for (index in MDSizeStrides(size)) block(get(index))
}

public inline fun <E> MDList<E>.forEachIndexed(block: (index: MDIndex, value: E) -> Unit) {
    for (index in MDSizeStrides(size)) block(index, get(index))
}

public inline fun <E> MDList1<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    for (index in indices) block(index, get(index))
}

public inline fun <E> MDList2<E>.forEachIndexed(block: (rowIndex: UInt, columnIndex: UInt, value: E) -> Unit) {
    for (row in rowIndices) for (column in columnIndices) block(row, column, get(row, column))
}

public inline fun <E> MDList<E>.withEach(block: E.() -> Unit) {
    for (index in MDSizeStrides(size)) get(index).block()
}

public inline fun <E> MDList<E>.withEachIndexed(block: E.(index: MDIndex) -> Unit) {
    for (index in MDSizeStrides(size)) get(index).block(index)
}

public inline fun <E> MDList1<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    for (index in indices) get(index).block(index)
}

public inline fun <E> MDList2<E>.withEachIndexed(block: E.(rowIndex: UInt, columnIndex: UInt) -> Unit) {
    for (row in rowIndices) for (column in columnIndices) get(row, column).block(row, column)
}

public inline fun <E> MDList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (index in MDSizeStrides(size)) if (block(get(index))) return true
    return false
}

public inline fun <E> MDList<E>.anyIndexed(block: (index: MDIndex, value: E) -> Boolean): Boolean {
    for (index in MDSizeStrides(size)) if (block(index, get(index))) return true
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
    for (index in MDSizeStrides(size)) if (!block(get(index))) return false
    return true
}

public inline fun <E> MDList<E>.allIndexed(block: (index: MDIndex, value: E) -> Boolean): Boolean {
    for (index in MDSizeStrides(size)) if (!block(index, get(index))) return false
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
    for (index in MDSizeStrides(size)) if (block(get(index))) return false
    return true
}

public inline fun <E> MDList<E>.noneIndexed(block: (index: MDIndex, value: E) -> Boolean): Boolean {
    for (index in MDSizeStrides(size)) if (block(index, get(index))) return false
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
    ArrayMDList(size) { transform(get(it)) }

public inline fun <E, R> MDList<E>.mapIndexed(transform: (index: MDIndex, E) -> R): MDList<R> =
    ArrayMDList(size) { transform(it, get(it)) }

public inline fun <E, R> MDList1<E>.map(transform: (E) -> R): MDList1<R> =
    ArrayMDList1(contentSize) { transform(get(it)) }

public inline fun <E, R> MDList1<E>.mapIndexed(transform: (index: UInt, E) -> R): MDList1<R> =
    ArrayMDList1(contentSize) { transform(it, get(it)) }

public inline fun <E, R> MDList2<E>.map(transform: (E) -> R): MDList2<R> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> transform(get(row, column)) }

public inline fun <E, R> MDList2<E>.mapIndexed(transform: (rowIndex: UInt, columnIndex: UInt, E) -> R): MDList2<R> =
    ArrayMDList2(rowNumber, columnNumber) { row, column -> transform(row, column, get(row, column)) }

public inline fun <E, R> MDList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in MDSizeStrides(size)) accumulator = operation(accumulator, get(index))
    return accumulator
}

public inline fun <E, R> MDList<E>.foldIndexed(initial: R, operation: (index: MDIndex, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in MDSizeStrides(size)) accumulator = operation(index, accumulator, get(index))
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

context(monoid: Monoid<E>)
public fun <E> MDList<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList<E>.sumOf(selector: (E) -> A): A = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList<E>.sumOfIndexed(selector: (index: MDIndex, E) -> A): A = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList1<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(monoid.zero) { index: UInt, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList2<E>.sumOfIndexed(selector: (rowIndex: UInt, columnIndex: UInt, E) -> A): A = foldIndexed(monoid.zero) { row, column, acc, e -> acc + selector(row, column, e) }

// TODO: Add bulk operations
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList


public inline fun <E> ContextualMDList<E, *>.forEach(block: (value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(get(index))
}

public inline fun <E> ContextualMDList<E, *>.forEachIndexed(block: (index: KoneUIntArray, value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(index, get(index))
}

public inline fun <E> ContextualMDList<E, *>.withEach(block: E.() -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block()
}

public inline fun <E> ContextualMDList<E, *>.withEachIndexed(block: E.(index: KoneUIntArray) -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block(index)
}

public inline fun <E> ContextualMDList<E, *>.any(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return true
    return false
}

public inline fun <E> ContextualMDList<E, *>.anyIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return true
    return false
}

public inline fun <E> ContextualMDList<E, *>.all(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (!block(get(index))) return false
    return true
}

public inline fun <E> ContextualMDList<E, *>.allIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (!block(index, get(index))) return false
    return true
}

public inline fun <E> ContextualMDList<E, *>.none(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return false
    return true
}

public inline fun <E> ContextualMDList<E, *>.noneIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return false
    return true
}

public inline fun <E, R> ContextualMDList<E, *>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in ShapeStrides(shape)) accumulator = operation(accumulator, get(index))
    return accumulator
}

public inline fun <E, R> ContextualMDList<E, *>.foldIndexed(initial: R, operation: (index: KoneUIntArray, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in ShapeStrides(shape)) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

// TODO: Add `reduce`-like extensions

context(Ring<E>)
public fun <E> ContextualMDList<E, *>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<A>)
public fun <E, A> ContextualMDList<E, *>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<A>)
public inline fun <E, A> ContextualMDList<E, *>.sumOfIndexed(selector: (index: KoneUIntArray, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }

// TODO: Add bulk operations
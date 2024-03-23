/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.common.utils

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.common.MDList


public inline fun <E> MDList<E>.forEach(block: (value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(get(index))
}

public inline fun <E> MDList<E>.forEachIndexed(block: (index: KoneUIntArray, value: E) -> Unit) {
    for (index in ShapeStrides(shape)) block(index, get(index))
}

public inline fun <E> MDList<E>.withEach(block: E.() -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block()
}

public inline fun <E> MDList<E>.withEachIndexed(block: E.(index: KoneUIntArray) -> Unit) {
    for (index in ShapeStrides(shape)) get(index).block(index)
}

public inline fun <E> MDList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return true
    return false
}

public inline fun <E> MDList<E>.anyIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return true
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

public inline fun <E> MDList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(get(index))) return false
    return true
}

public inline fun <E> MDList<E>.noneIndexed(block: (index: KoneUIntArray, value: E) -> Boolean): Boolean {
    for (index in ShapeStrides(shape)) if (block(index, get(index))) return false
    return true
}

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

// TODO: Add bulk operations
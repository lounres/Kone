/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.common.KoneIterableList
import dev.lounres.kone.collections.common.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.next
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


public inline fun <E> KoneIterable<E>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}

public inline fun <E> KoneIterable<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
}

public inline fun <E> KoneIterable<E>.withEach(block: E.() -> Unit) {
    for (element in this) element.block()
}

public inline fun <E> KoneIterable<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    var index = 0u
    for (element in this) element.block(index++)
}

public inline fun <E> KoneIterable<E>.any(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return true
    return false
}

public inline fun <E> KoneIterable<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}

public inline fun <E> KoneIterable<E>.all(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (!block(element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (!block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.none(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.first(predicate: (E) -> Boolean): E {
    for (element in this) if (predicate(element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <E> KoneIterable<E>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (element in this) if (predicate(element)) return element
    return null
}

public inline fun <E> KoneIterable<E>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
    for (element in this) if (predicate(element)) return Some(element)
    return None
}

public inline fun <E, R> KoneIterable<E>.firstOf(transform: (E) -> R, predicate: (R) -> Boolean): R {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <E, R> KoneIterable<E>.firstOfOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    return null
}

public inline fun <E, R> KoneIterable<E>.firstOfMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Option<R> {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}

public inline fun <E, R> KoneIterable<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneIterable<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    var index = 0u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduce(operation: (acc: R, E) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return Some(accumulator)
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = iterator.next()
    var index = 1u
    while (iterator.hasNext()) {
        accumulator = operation(index, accumulator, iterator.next())
        index++
    }
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = iterator.next()
    var index = 1u
    while (iterator.hasNext()) {
        accumulator = operation(index, accumulator, iterator.next())
        index++
    }
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = iterator.next()
    var index = 1u
    while (iterator.hasNext()) {
        accumulator = operation(index, accumulator, iterator.next())
        index++
    }
    return Some(accumulator)
}

// TODO: Add `reduce`-like extensions

context(Ring<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<A>)
public fun <E, A> KoneIterable<E>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<A>)
public inline fun <E, A> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
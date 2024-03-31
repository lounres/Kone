/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import kotlin.math.min


// TODO: Add operations for array value classes

public fun <E> KoneIterable<E>.take(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    var count = 0u
    val list = KoneGrowableArrayList<E>(n, context = context)
    for (item in this) {
        list.add(item)
        if (++count == n)
            break
    }
    return list.optimizeReadOnlyList(context = context)
}
public fun <E> KoneList<E>.take(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    return KoneIterableList(size, context = context) { this[it] }
}
public fun <E> KoneIterableCollection<E>.take(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    val iterator = iterator()
    return KoneIterableList(size, context = context) { iterator.getAndMoveNext() }
}
public fun <E> KoneIterableList<E>.take(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    val iterator = iterator()
    return KoneIterableList(size, context = context) { iterator.getAndMoveNext() }
}

public fun <E> KoneIterable<E>.drop(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList(context = context)
    val list = KoneGrowableArrayList<E>(context = context)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList(context = context)
}
public fun <E> KoneIterableCollection<E>.drop(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    val list = KoneGrowableArrayList<E>(resultSize, context = context)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList(context = context)
}
public fun <E> KoneList<E>.drop(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    return KoneIterableList(resultSize, context = context) { this[it + n] }
}
public fun <E> KoneIterableList<E>.drop(n: UInt, context: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    val list = KoneGrowableArrayList<E>(resultSize, context = context)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList()
}

public inline fun <E> KoneIterable<E>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}
public inline fun <E> KoneList<E>.forEach(block: (value: E) -> Unit) {
    for (index in 0u ..< size) block(get(index))
}
public inline fun <E> KoneIterableList<E>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}

public inline fun <E> KoneIterable<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
}
public inline fun <E> KoneList<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    for (index in 0u ..< size) block(index, get(index))
}
public inline fun <E> KoneIterableList<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
}

public inline fun <E> KoneIterable<E>.withEach(block: E.() -> Unit) {
    for (element in this) element.block()
}
public inline fun <E> KoneList<E>.withEach(block: E.() -> Unit) {
    for (index in 0u ..< size) get(index).block()
}
public inline fun <E> KoneIterableList<E>.withEach(block: E.() -> Unit) {
    for (element in this) element.block()
}

public inline fun <E> KoneIterable<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    var index = 0u
    for (element in this) element.block(index++)
}
public inline fun <E> KoneList<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    for (index in 0u ..< size) get(index).block(index)
}
public inline fun <E> KoneIterableList<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    var index = 0u
    for (element in this) element.block(index++)
}

public inline fun <E> KoneIterable<E>.any(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return true
    return false
}
public inline fun <E> KoneList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return true
    return false
}
public inline fun <E> KoneIterableList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return true
    return false
}

public inline fun <E> KoneIterable<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}
public inline fun <E> KoneList<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return true
    return false
}
public inline fun <E> KoneIterableList<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}

public inline fun <E> KoneIterable<E>.all(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (!block(element)) return false
    return true
}
public inline fun <E> KoneList<E>.all(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(get(index))) return false
    return true
}
public inline fun <E> KoneIterableList<E>.all(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (!block(element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (!block(currentIndex++, element)) return false
    return true
}
public inline fun <E> KoneList<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(index, get(index))) return false
    return true
}
public inline fun <E> KoneIterableList<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (!block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.none(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return false
    return true
}
public inline fun <E> KoneList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return false
    return true
}
public inline fun <E> KoneIterableList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return false
    return true
}
public inline fun <E> KoneList<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return false
    return true
}
public inline fun <E> KoneIterableList<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneIterable<E>.first(predicate: (E) -> Boolean): E {
    for (element in this) if (predicate(element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}
public inline fun <E> KoneList<E>.first(predicate: (E) -> Boolean): E {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return element
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}
public inline fun <E> KoneIterableList<E>.first(predicate: (E) -> Boolean): E {
    for (element in this) if (predicate(element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <E> KoneIterable<E>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (element in this) if (predicate(element)) return element
    return null
}
public inline fun <E> KoneList<E>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return element
    }
    return null
}
public inline fun <E> KoneIterableList<E>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (element in this) if (predicate(element)) return element
    return null
}

public inline fun <E> KoneIterable<E>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
    for (element in this) if (predicate(element)) return Some(element)
    return None
}
public inline fun <E> KoneList<E>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return Some(element)
    }
    return None
}
public inline fun <E> KoneIterableList<E>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
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
public inline fun <E, R> KoneList<E>.firstOf(transform: (E) -> R, predicate: (R) -> Boolean): R {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return result
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}
public inline fun <E, R> KoneIterableList<E>.firstOf(transform: (E) -> R, predicate: (R) -> Boolean): R {
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
public inline fun <E, R> KoneList<E>.firstOfOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return result
    }
    return null
}
public inline fun <E, R> KoneIterableList<E>.firstOfOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
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
public inline fun <E, R> KoneList<E>.firstOfMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Option<R> {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}
public inline fun <E, R> KoneIterableList<E>.firstOfMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Option<R> {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}

public inline fun <E, R, D: KoneExtendableCollection<R>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<R>> KoneList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<R>> KoneIterableList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}

public inline fun <E, R, D: KoneExtendableList<R>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}
public inline fun <E, R, D: KoneExtendableList<R>> KoneList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableList<R>> KoneIterableList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}

public inline fun <E, R, D: KoneExtendableCollection<R>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<R>> KoneList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(index, element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<R>> KoneIterableList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R, D: KoneExtendableList<R>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneExtendableList<R>> KoneList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(index, element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableList<R>> KoneIterableList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R> KoneIterable<E>.map(context: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> =
    mapTo(koneMutableIterableListOf(context = context), transform)
public inline fun <E, R> KoneList<E>.map(context: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> =
    mapTo(koneMutableIterableListOf(context = context), transform)
public inline fun <E, R> KoneIterableList<E>.map(context: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> =
    mapTo(koneMutableIterableListOf(context = context), transform)

public inline fun <E, R> KoneIterable<E>.mapIndexed(context: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> =
    mapIndexedTo(koneMutableIterableListOf(context = context), transform)
public inline fun <E, R> KoneList<E>.mapIndexed(context: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> =
    mapIndexedTo(koneMutableIterableListOf(context = context), transform)
public inline fun <E, R> KoneIterableList<E>.mapIndexed(context: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> =
    mapIndexedTo(koneMutableIterableListOf(context = context), transform)

public inline fun <E, D: KoneExtendableCollection<E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
public inline fun <E, D: KoneExtendableCollection<E>> KoneList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (index in indices) {
        val item = this[index]
        if (predicate(item)) destination.add(item)
    }
    return destination
}
public inline fun <E, D: KoneExtendableCollection<E>> KoneIterableList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

public inline fun <E, D: KoneExtendableList<E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
public inline fun <E, D: KoneExtendableList<E>> KoneList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (index in indices) {
        val item = this[index]
        if (predicate(item)) destination.add(item)
    }
    return destination
}
public inline fun <E, D: KoneExtendableList<E>> KoneIterableList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

public inline fun <E> KoneIterable<E>.filter(context: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(context = context), predicate)
public inline fun <E> KoneList<E>.filter(context: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(context = context), predicate)
public inline fun <E> KoneIterableList<E>.filter(context: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(context = context), predicate)

public inline fun <E, R> KoneIterable<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}
public inline fun <E, R> KoneList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in 0u ..< size) accumulator = operation(accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneIterableList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneList<E>.foldRight(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneIterableList<E>.foldRight(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    val iterator = iteratorFrom(size)
    while (iterator.hasPrevious()) accumulator = operation(accumulator, iterator.getAndMovePrevious())
    return accumulator
}

public inline fun <E, R> KoneIterable<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    var index = 0u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}
public inline fun <E, R> KoneList<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in 0u ..< size) accumulator = operation(index, accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneIterableList<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    var index = 0u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneList<E>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(index, accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneIterableList<E>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    val iterator = iteratorFrom(size)
    var index = size
    while (iterator.hasPrevious()) accumulator = operation(--index, accumulator, iterator.getAndMovePrevious())
    return accumulator
}

public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, context: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneGrowableArrayList(1u, context = context) { initial }
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
        result.add(accumulator)
    }
    return result
}
public inline fun <E, R> KoneList<E>.runningFold(initial: R, context: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneIterableCollection<E>.runningFold(initial: R, context: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneIterableList<E>.runningFold(initial: R, context: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

public inline fun <E, R> KoneIterable<E>.runningFoldIndexed(initial: R, context: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneGrowableArrayList(1u, context = context) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result.add(accumulator)
        index++
    }
    return result
}
public inline fun <E, R> KoneList<E>.runningFoldIndexed(initial: R, context: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(index, accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneIterableCollection<E>.runningFoldIndexed(initial: R, context: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneIterableList<E>.runningFoldIndexed(initial: R, context: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, context = context) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

// TODO: Add `runningFoldRight` and `runningFoldRightIndexed`

public inline fun <E: R, R> KoneIterable<E>.reduce(operation: (acc: R, E) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return accumulator
}
public inline fun <E: R, R> KoneList<E>.reduce(operation: (acc: R, E) -> R): R {
    if (size == 0u) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneIterableList<E>.reduce(operation: (acc: R, E) -> R): R {
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
public inline fun <E: R, R> KoneList<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    if (size == 0u) return null
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneIterableList<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
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
public inline fun <E: R, R> KoneList<E>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
    if (size == 0u) return None
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return Some(accumulator)
}
public inline fun <E: R, R> KoneIterableList<E>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
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
public inline fun <E: R, R> KoneList<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    if (size == 0u) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneIterableList<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
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
public inline fun <E: R, R> KoneList<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    if (size == 0u) return null
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneIterableList<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
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
public inline fun <E: R, R> KoneList<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
    if (size == 0u) return None
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return Some(accumulator)
}
public inline fun <E: R, R> KoneIterableList<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
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

// TODO: Add summing extensions for primitives

context(Ring<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(zero) { acc, e -> acc + e }
context(Ring<E>)
public fun <E> KoneList<E>.sum(): E = fold(zero) { acc, e -> acc + e }
context(Ring<E>)
public fun <E> KoneIterableList<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<A>)
public fun <E, A> KoneIterable<E>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }
context(Ring<A>)
public fun <E, A> KoneList<E>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }
context(Ring<A>)
public fun <E, A> KoneIterableList<E>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<A>)
public inline fun <E, A> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
context(Ring<A>)
public inline fun <E, A> KoneList<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
context(Ring<A>)
public inline fun <E, A> KoneIterableList<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
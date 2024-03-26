/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("DuplicatedCode")

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.implementations.KoneContextualGrowableArrayList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.getAndMovePrevious
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import kotlin.math.min


// TODO: Add operations for array value classes

public fun <E> KoneIterable<E>.take(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    var count = 0u
    val list = KoneContextualGrowableArrayList<E>(n)
    for (item in this) {
        list.addAtTheEnd(item)
        if (++count == n)
            break
    }
    return list.optimizeReadOnlyList()
}
public fun <E> KoneContextualList<E, *>.take(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneContextualIterableList()
    return KoneContextualIterableList(size) { this[it] }
}
public fun <E> KoneContextualIterableCollection<E, *>.take(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneContextualIterableList()
    val iterator = iterator()
    return KoneContextualIterableList(size) { iterator.getAndMoveNext() }
}
public fun <E> KoneContextualIterableList<E, *>.take(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneContextualIterableList()
    val iterator = iterator()
    return KoneContextualIterableList(size) { iterator.getAndMoveNext() }
}

public fun <E> KoneIterable<E>.drop(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    if (n == 0u) return toKoneContextualIterableList()
    val list = KoneContextualGrowableArrayList<E>()
    var count = 0u
    for (item in this) {
        if (count >= n) list.addAtTheEnd(item) else ++count
    }
    return list.optimizeReadOnlyList()
}
public fun <E> KoneContextualIterableCollection<E, *>.drop(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    if (n == 0u) return toKoneContextualIterableList()
    if (n >= size) return emptyKoneContextualIterableList()
    val resultSize = size - n
    val list = KoneContextualGrowableArrayList<E>(resultSize)
    var count = 0u
    for (item in this) {
        if (count >= n) list.addAtTheEnd(item) else ++count
    }
    return list.optimizeReadOnlyList()
}
public fun <E> KoneContextualList<E, *>.drop(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    if (n == 0u) return toKoneContextualIterableList()
    if (n >= size) return emptyKoneContextualIterableList()
    val resultSize = size - n
    return KoneContextualIterableList(resultSize) { this[it + n] }
}
public fun <E> KoneContextualIterableList<E, *>.drop(n: UInt): KoneContextualIterableList<E, Equality<E>> {
    if (n == 0u) return toKoneContextualIterableList()
    if (n >= size) return emptyKoneContextualIterableList()
    val resultSize = size - n
    val list = KoneContextualGrowableArrayList<E>(resultSize)
    var count = 0u
    for (item in this) {
        if (count >= n) list.addAtTheEnd(item) else ++count
    }
    return list.optimizeReadOnlyList()
}

public inline fun <E> KoneContextualList<E, *>.forEach(block: (value: E) -> Unit) {
    for (index in 0u ..< size) block(get(index))
}
public inline fun <E> KoneContextualIterableList<E, *>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}

public inline fun <E> KoneContextualList<E, *>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    for (index in 0u ..< size) block(index, get(index))
}
public inline fun <E> KoneContextualIterableList<E, *>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
}

public inline fun <E> KoneContextualList<E, *>.withEach(block: E.() -> Unit) {
    for (index in 0u ..< size) get(index).block()
}
public inline fun <E> KoneContextualIterableList<E, *>.withEach(block: E.() -> Unit) {
    for (element in this) element.block()
}

public inline fun <E> KoneContextualList<E, *>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    for (index in 0u ..< size) get(index).block(index)
}
public inline fun <E> KoneContextualIterableList<E, *>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    var index = 0u
    for (element in this) element.block(index++)
}

public inline fun <E> KoneContextualList<E, *>.any(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return true
    return false
}
public inline fun <E> KoneContextualIterableList<E, *>.any(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return true
    return false
}

public inline fun <E> KoneContextualList<E, *>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return true
    return false
}
public inline fun <E> KoneContextualIterableList<E, *>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}

public inline fun <E> KoneContextualList<E, *>.all(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(get(index))) return false
    return true
}
public inline fun <E> KoneContextualIterableList<E, *>.all(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (!block(element)) return false
    return true
}

public inline fun <E> KoneContextualList<E, *>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(index, get(index))) return false
    return true
}
public inline fun <E> KoneContextualIterableList<E, *>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (!block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneContextualList<E, *>.none(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return false
    return true
}
public inline fun <E> KoneContextualIterableList<E, *>.none(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return false
    return true
}

public inline fun <E> KoneContextualList<E, *>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return false
    return true
}
public inline fun <E> KoneContextualIterableList<E, *>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneContextualList<E, *>.first(predicate: (E) -> Boolean): E {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return element
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}
public inline fun <E> KoneContextualIterableList<E, *>.first(predicate: (E) -> Boolean): E {
    for (element in this) if (predicate(element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <E> KoneContextualList<E, *>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return element
    }
    return null
}
public inline fun <E> KoneContextualIterableList<E, *>.firstOrNull(predicate: (E) -> Boolean): E? {
    for (element in this) if (predicate(element)) return element
    return null
}

public inline fun <E> KoneContextualList<E, *>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
    for (index in 0u ..< size) {
        val element = get(index)
        if (predicate(element)) return Some(element)
    }
    return None
}
public inline fun <E> KoneContextualIterableList<E, *>.firstMaybe(predicate: (E) -> Boolean): Option<E> {
    for (element in this) if (predicate(element)) return Some(element)
    return None
}

public inline fun <E, R> KoneContextualList<E, *>.firstOf(transform: (E) -> R, predicate: (R) -> Boolean): R {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return result
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}
public inline fun <E, R> KoneContextualIterableList<E, *>.firstOf(transform: (E) -> R, predicate: (R) -> Boolean): R {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <E, R> KoneContextualList<E, *>.firstOfOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return result
    }
    return null
}
public inline fun <E, R> KoneContextualIterableList<E, *>.firstOfOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    return null
}

public inline fun <E, R> KoneContextualList<E, *>.firstOfMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Option<R> {
    for (index in 0u ..< size) {
        val element = get(index)
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}
public inline fun <E, R> KoneContextualIterableList<E, *>.firstOfMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Option<R> {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}

context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}
context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneContextualList<E, *>.mapTo(destination: D, transform: (E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(element))
    }
    return destination
}
context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneContextualIterableList<E, *>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}

public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.addAtTheEnd(transform(element))
    return destination
}
public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneContextualList<E, *>.mapTo(destination: D, transform: (E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.addAtTheEnd(transform(element))
    }
    return destination
}
public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneContextualIterableList<E, *>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.addAtTheEnd(transform(element))
    return destination
}

context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}
context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneContextualList<E, *>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(index, element))
    }
    return destination
}
context(RE)
public inline fun <E, R, RE: Equality<R>, D: KoneContextualExtendableCollection<R, RE>> KoneContextualIterableList<E, *>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.addAtTheEnd(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneContextualList<E, *>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.addAtTheEnd(transform(index, element))
    }
    return destination
}
public inline fun <E, R, D: KoneContextualExtendableList<R, *>> KoneContextualIterableList<E, *>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.addAtTheEnd(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R> KoneIterable<E>.map(transform: (E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapTo(koneContextualMutableIterableListOf(), transform)
public inline fun <E, R> KoneContextualList<E, *>.map(transform: (E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapTo(koneContextualMutableIterableListOf(), transform)
public inline fun <E, R> KoneContextualIterableList<E, *>.map(transform: (E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapTo(koneContextualMutableIterableListOf(), transform)

public inline fun <E, R> KoneIterable<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapIndexedTo(koneContextualMutableIterableListOf(), transform)
public inline fun <E, R> KoneContextualList<E, *>.mapIndexed(transform: (index: UInt, E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapIndexedTo(koneContextualMutableIterableListOf(), transform)
public inline fun <E, R> KoneContextualIterableList<E, *>.mapIndexed(transform: (index: UInt, E) -> R): KoneContextualIterableList<R, Equality<R>> =
    mapIndexedTo(koneContextualMutableIterableListOf(), transform)

context(EE)
public inline fun <E, EE: Equality<E>, D: KoneContextualExtendableCollection<E, EE>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
context(EE)
public inline fun <E, EE: Equality<E>, D: KoneContextualExtendableCollection<E, EE>> KoneContextualList<E, *>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (index in indices) {
        val item = this[index]
        if (predicate(item)) destination.add(item)
    }
    return destination
}
context(EE)
public inline fun <E, EE: Equality<E>, D: KoneContextualExtendableCollection<E, EE>> KoneContextualIterableList<E, *>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

public inline fun <E, D: KoneContextualExtendableList<E, *>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.addAtTheEnd(item)
    return destination
}
public inline fun <E, D: KoneContextualExtendableList<E, *>> KoneContextualList<E, *>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (index in indices) {
        val item = this[index]
        if (predicate(item)) destination.addAtTheEnd(item)
    }
    return destination
}
public inline fun <E, D: KoneContextualExtendableList<E, *>> KoneContextualIterableList<E, *>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.addAtTheEnd(item)
    return destination
}

public inline fun <E> KoneIterable<E>.filter(predicate: (E) -> Boolean): KoneContextualIterableList<E, Equality<E>> =
    filterTo(koneContextualMutableIterableListOf(), predicate)
public inline fun <E> KoneContextualList<E, *>.filter(predicate: (E) -> Boolean): KoneContextualIterableList<E, Equality<E>> =
    filterTo(koneContextualMutableIterableListOf(), predicate)
public inline fun <E> KoneContextualIterableList<E, *>.filter(predicate: (E) -> Boolean): KoneContextualIterableList<E, Equality<E>> =
    filterTo(koneContextualMutableIterableListOf(), predicate)

public inline fun <E, R> KoneContextualList<E, *>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in 0u ..< size) accumulator = operation(accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneContextualIterableList<E, *>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneContextualList<E, *>.foldRight(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneContextualIterableList<E, *>.foldRight(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    val iterator = iteratorFrom(size)
    while (iterator.hasPrevious()) accumulator = operation(accumulator, iterator.getAndMovePrevious())
    return accumulator
}

public inline fun <E, R> KoneContextualList<E, *>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in 0u ..< size) accumulator = operation(index, accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneContextualIterableList<E, *>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    var index = 0u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneContextualList<E, *>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(index, accumulator, get(index))
    return accumulator
}
public inline fun <E, R> KoneContextualIterableList<E, *>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    val iterator = iteratorFrom(size)
    var index = size
    while (iterator.hasPrevious()) accumulator = operation(--index, accumulator, iterator.getAndMovePrevious())
    return accumulator
}

public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualGrowableArrayList(1u) { initial }
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
        result.addAtTheEnd(accumulator)
    }
    return result
}
public inline fun <E, R> KoneContextualList<E, *>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneContextualIterableCollection<E, *>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneContextualIterableList<E, *>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

public inline fun <E, R> KoneIterable<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualGrowableArrayList(1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result.addAtTheEnd(accumulator)
        index++
    }
    return result
}
public inline fun <E, R> KoneContextualList<E, *>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(index, accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneContextualIterableCollection<E, *>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneContextualIterableList<E, *>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneContextualIterableList<R, Equality<R>> {
    val result = KoneContextualSettableIterableList(size + 1u) { initial }
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

public inline fun <E: R, R> KoneContextualList<E, *>.reduce(operation: (acc: R, E) -> R): R {
    if (size == 0u) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduce(operation: (acc: R, E) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return accumulator
}

public inline fun <E: R, R> KoneContextualList<E, *>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    if (size == 0u) return null
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return accumulator
}

public inline fun <E: R, R> KoneContextualList<E, *>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
    if (size == 0u) return None
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(accumulator, this[index])
    }
    return Some(accumulator)
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = iterator.next()
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, iterator.next())
    }
    return Some(accumulator)
}

public inline fun <E: R, R> KoneContextualList<E, *>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    if (size == 0u) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
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

public inline fun <E: R, R> KoneContextualList<E, *>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    if (size == 0u) return null
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return accumulator
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
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

public inline fun <E: R, R> KoneContextualList<E, *>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
    if (size == 0u) return None
    var accumulator: R = this[0u]
    for (index in 1u ..< size) {
        accumulator = operation(index, accumulator, this[index])
    }
    return Some(accumulator)
}
public inline fun <E: R, R> KoneContextualIterableList<E, *>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
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
public fun <E> KoneContextualList<E, *>.sum(): E = fold(zero) { acc, e -> acc + e }
context(Ring<E>)
public fun <E> KoneContextualIterableList<E, *>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<A>)
public fun <E, A> KoneContextualList<E, *>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }
context(Ring<A>)
public fun <E, A> KoneContextualIterableList<E, *>.sumOf(selector: (E) -> A): A = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<A>)
public inline fun <E, A> KoneContextualList<E, *>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
context(Ring<A>)
public inline fun <E, A> KoneContextualIterableList<E, *>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
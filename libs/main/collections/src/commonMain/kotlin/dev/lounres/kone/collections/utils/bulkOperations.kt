/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList
import dev.lounres.kone.collections.implementations.KoneLazyList
import dev.lounres.kone.collections.implementations.KoneVirtualList
import dev.lounres.kone.collections.utils.divide
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextUInt


// TODO: Add operations for array value classes

public fun <E, D: KoneExtendableCollection<in E>> KoneIterable<E>.copyTo(destination: D): D {
    for (element in this) destination.add(element)
    return destination
}

public fun <E, D: KoneExtendableCollection<in E>> KoneList<E>.copyTo(destination: D): D {
    for (index in indices) {
        val element = this[index]
        destination.add(element)
    }
    return destination
}

public fun <E, D: KoneExtendableCollection<in E>> KoneIterableList<E>.copyTo(destination: D): D {
    for (element in this) destination.add(element)
    return destination
}

public operator fun <E> KoneExtendableCollection<E>.plusAssign(elements: KoneIterableCollection<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneExtendableCollection<E>.plusAssign(elements: KoneList<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneExtendableCollection<E>.plusAssign(elements: KoneIterableList<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneExtendableCollection<E>.plusAssign(element: E) {
    add(element)
}

public fun <E> KoneIterable<E>.take(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    var count = 0u
    val list = KoneGrowableArrayList(elementContext = elementContext)
    for (item in this) {
        list.add(item)
        if (++count == n)
            break
    }
    return list.optimizeReadOnlyList(elementContext = elementContext)
}
public fun <E> KoneList<E>.take(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    return KoneIterableList(size, elementContext = elementContext) { this[it] }
}
public fun <E> KoneIterableCollection<E>.take(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    val iterator = iterator()
    return KoneIterableList(size, elementContext = elementContext) { iterator.getAndMoveNext() }
}
public fun <E> KoneIterableList<E>.take(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneIterableList()
    val iterator = iterator()
    return KoneIterableList(size, elementContext = elementContext) { iterator.getAndMoveNext() }
}

public fun <E> KoneList<E>.takeVirtually(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneVirtualList(if (size >= n) n else size, elementContext) { this[it] }

public fun <E> KoneList<E>.takeLazily(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneLazyList(if (size >= n) n else size, elementContext) { this[it] }

public fun <E> KoneIterable<E>.drop(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList(elementContext = elementContext)
    val list = KoneGrowableArrayList(elementContext = elementContext)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList(elementContext = elementContext)
}
public fun <E> KoneIterableCollection<E>.drop(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    val list = KoneGrowableArrayList(resultSize, elementContext = elementContext)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList(elementContext = elementContext)
}
public fun <E> KoneList<E>.drop(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    return KoneIterableList(resultSize, elementContext = elementContext) { this[it + n] }
}
public fun <E> KoneIterableList<E>.drop(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> {
    if (n == 0u) return toKoneIterableList()
    if (n >= size) return emptyKoneIterableList()
    val resultSize = size - n
    val list = KoneGrowableArrayList(resultSize, elementContext = elementContext)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.optimizeReadOnlyList()
}

public fun <E> KoneList<E>.dropVirtually(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneVirtualList(if (size >= n) size - n else 0u, elementContext) { this[it + n] }

public fun <E> KoneList<E>.dropLazily(n: UInt, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneLazyList(if (size >= n) size - n else 0u, elementContext) { this[it + n] }

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

public inline fun <E> KoneIterable<E>.count(block: (value: E) -> Boolean): UInt {
    var count = 0u
    for (element in this) if (block(element)) count++
    return count
}
public inline fun <E> KoneList<E>.count(block: (value: E) -> Boolean): UInt {
    var count = 0u
    for (index in 0u ..< size) if (block(get(index))) count++
    return count
}
public inline fun <E> KoneIterableList<E>.count(block: (value: E) -> Boolean): UInt {
    var count = 0u
    for (element in this) if (block(element)) count++
    return count
}

public inline fun <E> KoneIterable<E>.countIndexed(block: (index: UInt, value: E) -> Boolean): UInt {
    var count = 0u
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) count++
    return count
}
public inline fun <E> KoneList<E>.countIndexed(block: (index: UInt, value: E) -> Boolean): UInt {
    var count = 0u
    for (index in 0u ..< size) if (block(index, get(index))) count++
    return count
}
public inline fun <E> KoneIterableList<E>.countIndexed(block: (index: UInt, value: E) -> Boolean): UInt {
    var count = 0u
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) count++
    return count
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

public fun <E> KoneIterableCollection<E>.random(random: Random): E {
    val index = random.nextUInt(0u, size)
    val iterator = iterator()
    for (i in 0u..<index) iterator.moveNext()
    return iterator.getNext()
}

public fun <E> KoneList<E>.random(random: Random): E = get(random.nextUInt(0u, size))

public fun <E> KoneIterableList<E>.random(random: Random): E = get(random.nextUInt(0u, size))

public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneIterableList<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}

public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    for (index in indices) {
        val element = this[index]
        destination.add(transform(index, element))
    }
    return destination
}
public inline fun <E, R, D: KoneExtendableCollection<in R>> KoneIterableList<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R> KoneIterable<E>.map(elementContext: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> =
    mapTo(koneMutableIterableListOf(elementContext = elementContext), transform)
public inline fun <E, R> KoneList<E>.map(elementContext: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> =
    KoneSettableIterableList(size = size, elementContext = elementContext) { transform(get(it)) }
public inline fun <E, R> KoneIterableList<E>.map(elementContext: Equality<R> = defaultEquality(), transform: (E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    return KoneSettableIterableList(size = size, elementContext = elementContext) { transform(iterator.next()) }
}

public inline fun <E, R> KoneList<E>.mapVirtually(elementContext: Equality<R> = defaultEquality(), crossinline transform: (E) -> R): KoneIterableList<R> =
    KoneVirtualList(size = size, elementContext = elementContext) { transform(get(it)) }

public inline fun <E, R> KoneList<E>.mapLazily(elementContext: Equality<R> = defaultEquality(), crossinline transform: (E) -> R): KoneIterableList<R> =
    KoneLazyList(size = size, elementContext = elementContext) { transform(get(it)) }

public inline fun <E, R> KoneIterable<E>.mapIndexed(elementContext: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> =
    mapIndexedTo(koneMutableIterableListOf(elementContext = elementContext), transform)
public inline fun <E, R> KoneList<E>.mapIndexed(elementContext: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> =
    KoneSettableIterableList(size = size, elementContext = elementContext) { transform(it, get(it)) }
public inline fun <E, R> KoneIterableList<E>.mapIndexed(elementContext: Equality<R> = defaultEquality(), transform: (index: UInt, E) -> R): KoneIterableList<R> {
    val iterator = iterator()
    return KoneSettableIterableList(size = size, elementContext = elementContext) { transform(it, iterator.next()) }
}

public inline fun <E, R> KoneList<E>.mapIndexedVirtually(elementContext: Equality<R> = defaultEquality(), crossinline transform: (index: UInt, E) -> R): KoneIterableList<R> =
    KoneVirtualList(size = size, elementContext = elementContext) { transform(it, get(it)) }

public inline fun <E, R> KoneList<E>.mapIndexedLazily(elementContext: Equality<R> = defaultEquality(), crossinline transform: (index: UInt, E) -> R): KoneIterableList<R> =
    KoneLazyList(size = size, elementContext = elementContext) { transform(it, get(it)) }

public inline fun <E, D: KoneExtendableCollection<in E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
public inline fun <E, D: KoneExtendableCollection<in E>> KoneList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (index in indices) {
        val item = this[index]
        if (predicate(item)) destination.add(item)
    }
    return destination
}
public inline fun <E, D: KoneExtendableCollection<in E>> KoneIterableList<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

public inline fun <E> KoneIterable<E>.filter(elementContext: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(elementContext = elementContext), predicate)
public inline fun <E> KoneList<E>.filter(elementContext: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(elementContext = elementContext), predicate)
public inline fun <E> KoneIterableList<E>.filter(elementContext: Equality<E> = defaultEquality(), predicate: (E) -> Boolean): KoneIterableList<E> =
    filterTo(koneMutableIterableListOf(elementContext = elementContext), predicate)

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

public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneGrowableArrayList(1u, elementContext = elementContext) { initial }
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
        result.add(accumulator)
    }
    return result
}
public inline fun <E, R> KoneList<E>.runningFold(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneIterableCollection<E>.runningFold(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneIterableList<E>.runningFold(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

public inline fun <E, R> KoneIterable<E>.runningFoldIndexed(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneGrowableArrayList(1u, elementContext = elementContext) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result.add(accumulator)
        index++
    }
    return result
}
public inline fun <E, R> KoneList<E>.runningFoldIndexed(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
    var accumulator = initial
    for (index in indices) {
        accumulator = operation(index, accumulator, this[index])
        result[index + 1u] = accumulator
    }
    return result
}
public inline fun <E, R> KoneIterableCollection<E>.runningFoldIndexed(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}
public inline fun <E, R> KoneIterableList<E>.runningFoldIndexed(initial: R, elementContext: Equality<R> = defaultEquality(), operation: (index: UInt, acc: R, E) -> R): KoneIterableList<R> {
    val result = KoneSettableIterableList(size + 1u, elementContext = elementContext) { initial }
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
    for (element in iterator) accumulator = operation(accumulator, element)
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
    for (element in iterator) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = iterator.next()
    for (element in iterator) accumulator = operation(accumulator, element)
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
    for (element in iterator) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceMaybe(operation: (acc: R, E) -> R): Option<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = iterator.next()
    for (element in iterator) accumulator = operation(accumulator, element)
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
    for (element in iterator) accumulator = operation(accumulator, element)
    return Some(accumulator)
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = iterator.next()
    var index = 1u
    for (element in iterator) accumulator = operation(index++, accumulator, element)
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
    for (element in iterator) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = iterator.next()
    var index = 1u
    for (element in iterator) accumulator = operation(index++, accumulator, element)
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
    for (element in iterator) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Option<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = iterator.next()
    var index = 1u
    for (element in iterator) accumulator = operation(index++, accumulator, element)
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
    for (element in iterator) accumulator = operation(index++, accumulator, element)
    return Some(accumulator)
}

// TODO: Add `reduce`-like extensions. Like `runningReduce`.

// TODO: Add summing and multiplying extensions for primitives. Maybe.

context(Ring<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(zero) { acc, e -> acc + e }
context(Ring<E>)
public fun <E> KoneList<E>.sum(): E = fold(zero) { acc, e -> acc + e }
context(Ring<E>)
public fun <E> KoneIterableList<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<N>)
public fun <E, N> KoneIterable<E>.sumOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc + selector(e) }
context(Ring<N>)
public fun <E, N> KoneList<E>.sumOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc + selector(e) }
context(Ring<N>)
public fun <E, N> KoneIterableList<E>.sumOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<N>)
public inline fun <E, N> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
context(Ring<N>)
public inline fun <E, N> KoneList<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }
context(Ring<N>)
public inline fun <E, N> KoneIterableList<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }

context(Ring<E>)
public fun <E> KoneIterable<E>.product(): E = fold(one) { acc, e -> acc * e }
context(Ring<E>)
public fun <E> KoneList<E>.product(): E = fold(one) { acc, e -> acc * e }
context(Ring<E>)
public fun <E> KoneIterableList<E>.product(): E = fold(one) { acc, e -> acc * e }

context(Ring<N>)
public fun <E, N> KoneIterable<E>.productOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc * selector(e) }
context(Ring<N>)
public fun <E, N> KoneList<E>.productOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc * selector(e) }
context(Ring<N>)
public fun <E, N> KoneIterableList<E>.productOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc * selector(e) }

context(Ring<N>)
public inline fun <E, N> KoneIterable<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc * selector(index, e) }
context(Ring<N>)
public inline fun <E, N> KoneList<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc * selector(index, e) }
context(Ring<N>)
public inline fun <E, N> KoneIterableList<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc * selector(index, e) }

public inline fun <E, K, D : KoneMutableMap<in K, KoneMutableIterableList<E>>> KoneIterable<E>.groupByTo(destination: D, elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = elementContext) }
        accumulator.add(element)
    }
    return destination
}
public inline fun <E, K, D : KoneMutableMap<in K, KoneMutableIterableList<E>>> KoneList<E>.groupByTo(destination: D, elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): D {
    for (index in indices) {
        val element = get(index)
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = elementContext) }
        accumulator.add(element)
    }
    return destination
}
public inline fun <E, K, D : KoneMutableMap<in K, KoneMutableIterableList<E>>> KoneIterableList<E>.groupByTo(destination: D, elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = elementContext) }
        accumulator.add(element)
    }
    return destination
}

public inline fun <E, K, V, D : KoneMutableMap<in K, KoneMutableIterableList<V>>> KoneIterable<E>.groupByTo(destination: D, valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = valueContext) }
        accumulator.add(valueTransform(element))
    }
    return destination
}
public inline fun <E, K, V, D : KoneMutableMap<in K, KoneMutableIterableList<V>>> KoneList<E>.groupByTo(destination: D, valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (index in indices) {
        val element = get(index)
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = valueContext) }
        accumulator.add(valueTransform(element))
    }
    return destination
}
public inline fun <E, K, V, D : KoneMutableMap<in K, KoneMutableIterableList<V>>> KoneIterableList<E>.groupByTo(destination: D, valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneGrowableArrayList(elementContext = valueContext) }
        accumulator.add(valueTransform(element))
    }
    return destination
}

public inline fun <E, K> KoneIterable<E>.groupBy(keyContext: Equality<K> = defaultEquality(), elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, KoneIterableList<E>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(elementContext)), elementContext = elementContext, keySelector = keySelector)
public inline fun <E, K> KoneList<E>.groupBy(keyContext: Equality<K> = defaultEquality(), elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, KoneIterableList<E>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(elementContext)), elementContext = elementContext, keySelector = keySelector)
public inline fun <E, K> KoneIterableList<E>.groupBy(keyContext: Equality<K> = defaultEquality(), elementContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, KoneIterableList<E>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(elementContext)), elementContext = elementContext, keySelector = keySelector)

public inline fun <E, K, V> KoneIterable<E>.groupBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, KoneIterableList<V>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(valueContext)), valueContext = valueContext, keySelector = keySelector, valueTransform = valueTransform)
public inline fun <E, K, V> KoneList<E>.groupBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, KoneIterableList<V>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(valueContext)), valueContext = valueContext, keySelector = keySelector, valueTransform = valueTransform)
public inline fun <E, K, V> KoneIterableList<E>.groupBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, KoneIterableList<V>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext, valueContext = koneIterableListEquality(valueContext)), valueContext = valueContext, keySelector = keySelector, valueTransform = valueTransform)

@PublishedApi
internal data class RangeToSort(val from: UInt, val to: UInt)

public fun <E: Comparable<E>> KoneSettableList<E>.sort() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] < valueInTheMiddle) i++
            while (this[j] > valueInTheMiddle) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}
context(Order<E>)
public fun <E> KoneSettableList<E>.sort() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] < valueInTheMiddle) i++
            while (this[j] > valueInTheMiddle) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}
public fun <E> KoneSettableList<E>.sortWith(comparator: Comparator<E>) {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (comparator.compare(this[i], valueInTheMiddle) < 0) i++
            while (comparator.compare(this[j], valueInTheMiddle) > 0) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}

public fun <E: Comparable<E>> KoneSettableList<E>.sortDescending() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] > valueInTheMiddle) i++
            while (this[j] < valueInTheMiddle) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}
context(Order<E>)
public fun <E> KoneSettableList<E>.sortDescending() {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (this[i] > valueInTheMiddle) i++
            while (this[j] < valueInTheMiddle) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}
public fun <E> KoneSettableList<E>.sortWithDescending(comparator: Comparator<E>) {
    fun divide(from: UInt, to: UInt): UInt {
        var i = from
        var j = to
        val valueInTheMiddle = this[(from + to) / 2u]
        while (true) {
            while (comparator.compare(this[i], valueInTheMiddle) > 0) i++
            while (comparator.compare(this[j], valueInTheMiddle) < 0) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
        sortQueue.addLast(RangeToSort(from, to))
        while (sortQueue.isNotEmpty()) {
            val (from, to) = sortQueue.popFirst()
            val middle = divide(from, to)
            if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
            if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
        }
    }
    quickSort(0u, lastIndex)
}

// TODO: Move inside the following `sortBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.divide(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) < valueInTheMiddle) i++
        while (selector(this[j]) > valueInTheMiddle) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.quickSort(from: UInt, to: UInt, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divide(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
public inline fun <E, R: Comparable<R>> KoneSettableList<E>.sortBy(selector: (E) -> R) {
    quickSort(0u, lastIndex, selector)
}
// TODO: Move inside the following `sortBy` function when local inline functions will be ready
context(Order<R>)
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.divide(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) < valueInTheMiddle) i++
        while (selector(this[j]) > valueInTheMiddle) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortBy` function when local inline functions will be ready
context(Order<R>)
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quickSort(from: UInt, to: UInt, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divide(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
context(Order<R>)
public inline fun <E, R> KoneSettableList<E>.sortBy(selector: (E) -> R) {
    quickSort(0u, lastIndex, selector)
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.divide(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (comparator.compare(selector(this[i]), valueInTheMiddle) < 0) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) > 0) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quickSort(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divide(from, to, comparator, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
public inline fun <E, R> KoneSettableList<E>.sortWithBy(comparator: Comparator<R>, selector: (E) -> R) {
    quickSort(0u, lastIndex, comparator, selector)
}

// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.divideDescending(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) > valueInTheMiddle) i++
        while (selector(this[j]) < valueInTheMiddle) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R: Comparable<R>> KoneSettableList<E>.quickSortDescending(from: UInt, to: UInt, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divideDescending(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
public inline fun <E, R: Comparable<R>> KoneSettableList<E>.sortByDescending(selector: (E) -> R) {
    quickSortDescending(0u, lastIndex, selector)
}
// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
context(Order<R>)
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.divideDescending(from: UInt, to: UInt, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (selector(this[i]) > valueInTheMiddle) i++
        while (selector(this[j]) < valueInTheMiddle) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortByDescending` function when local inline functions will be ready
context(Order<R>)
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quickSortDescending(from: UInt, to: UInt, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divideDescending(from, to, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
context(Order<R>)
public inline fun <E, R> KoneSettableList<E>.sortByDescending(selector: (E) -> R) {
    quickSortDescending(0u, lastIndex, selector)
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.divideDescending(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R): UInt {
    var i = from
    var j = to
    val valueInTheMiddle = selector(this[(from + to) / 2u])
    while (true) {
        while (comparator.compare(selector(this[i]), valueInTheMiddle) > 0) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) < 0) j--
        if (i <= j) {
            this[i] = this[j].also { this[j] = this[i] }
            i++
            j--
        } else break
    }
    return i
}
// TODO: Move inside the following `sortWithBy` function when local inline functions will be ready
@PublishedApi
internal inline fun <E, R> KoneSettableList<E>.quickSortDescending(from: UInt, to: UInt, comparator: Comparator<R>, selector: (E) -> R) {
    val sortQueue: KoneDequeue<RangeToSort> = KoneGrowableLinkedArrayList()
    sortQueue.addLast(RangeToSort(from, to))
    while (sortQueue.isNotEmpty()) {
        val (from, to) = sortQueue.popFirst()
        val middle = divideDescending(from, to, comparator, selector)
        if (from < middle - 1u) sortQueue.addLast(RangeToSort(from, middle - 1u))
        if (middle < to) sortQueue.addLast(RangeToSort(middle, to))
    }
}
public inline fun <E, R> KoneSettableList<E>.sortWithByDescending(comparator: Comparator<R>, selector: (E) -> R) {
    quickSortDescending(0u, lastIndex, comparator, selector)
}

public fun <E: Comparable<E>> KoneIterable<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
public fun <E: Comparable<E>> KoneIterableCollection<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
public fun <E: Comparable<E>> KoneList<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
public fun <E: Comparable<E>> KoneIterableList<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }

context(Order<E>)
public fun <E> KoneIterable<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
context(Order<E>)
public fun <E> KoneIterableCollection<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
context(Order<E>)
public fun <E> KoneList<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }
context(Order<E>)
public fun <E> KoneIterableList<E>.sorted(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sort() }

public fun <E> KoneIterable<E>.sortedWith(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWith(comparator) }
public fun <E> KoneIterableCollection<E>.sortedWith(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWith(comparator) }
public fun <E> KoneList<E>.sortedWith(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWith(comparator) }
public fun <E> KoneIterableList<E>.sortedWith(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWith(comparator) }

public fun <E: Comparable<E>> KoneIterable<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
public fun <E: Comparable<E>> KoneIterableCollection<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
public fun <E: Comparable<E>> KoneList<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
public fun <E: Comparable<E>> KoneIterableList<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }

context(Order<E>)
public fun <E> KoneIterable<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
context(Order<E>)
public fun <E> KoneIterableCollection<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
context(Order<E>)
public fun <E> KoneList<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }
context(Order<E>)
public fun <E> KoneIterableList<E>.sortedDescending(): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortDescending() }

public fun <E> KoneIterable<E>.sortedWithDescending(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithDescending(comparator) }
public fun <E> KoneIterableCollection<E>.sortedWithDescending(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithDescending(comparator) }
public fun <E> KoneList<E>.sortedWithDescending(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithDescending(comparator) }
public fun <E> KoneIterableList<E>.sortedWithDescending(comparator: Comparator<E>): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithDescending(comparator) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
public inline fun <E, R: Comparable<R>> KoneIterableCollection<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
public inline fun <E, R: Comparable<R>> KoneList<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
context(Order<R>)
public inline fun <E, R> KoneIterableCollection<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
context(Order<R>)
public inline fun <E, R> KoneList<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }
context(Order<R>)
public inline fun <E, R> KoneIterableList<E>.sortedBy(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortBy(selector) }

public inline fun <E, R> KoneIterable<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithBy(comparator, selector) }
public inline fun <E, R> KoneIterableCollection<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithBy(comparator, selector) }
public inline fun <E, R> KoneList<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithBy(comparator, selector) }
public inline fun <E, R> KoneIterableList<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithBy(comparator, selector) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
public inline fun <E, R: Comparable<R>> KoneIterableCollection<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
public inline fun <E, R: Comparable<R>> KoneList<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
public inline fun <E, R: Comparable<R>> KoneIterableList<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
context(Order<R>)
public inline fun <E, R> KoneIterableCollection<E>.sortedBDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
context(Order<R>)
public inline fun <E, R> KoneList<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }
context(Order<R>)
public inline fun <E, R> KoneIterableList<E>.sortedByDescending(selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortByDescending(selector) }

public inline fun <E, R> KoneIterable<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithByDescending(comparator, selector) }
public inline fun <E, R> KoneIterableCollection<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithByDescending(comparator, selector) }
public inline fun <E, R> KoneList<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithByDescending(comparator, selector) }
public inline fun <E, R> KoneIterableList<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneIterableList<E> =
    toKoneSettableIterableList().apply { sortWithByDescending(comparator, selector) }

// https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
public fun <E> KoneSettableList<E>.shuffle(random: Random = Random) {
    for (i in 0u ..< size - 1u) {
        val j = random.nextUInt(i, size)
        this[i] = this[j].also { this[j] = this[i] }
    }
}

public fun <E> KoneIterable<E>.shuffled(random: Random = Random): KoneIterableList<E> =
    toKoneSettableIterableList().apply { shuffle(random) }
public fun <E> KoneIterableCollection<E>.shuffled(random: Random = Random): KoneIterableList<E> =
    toKoneSettableIterableList().apply { shuffle(random) }
public fun <E> KoneList<E>.shuffled(random: Random = Random): KoneIterableList<E> =
    toKoneSettableIterableList().apply { shuffle(random) }
public fun <E> KoneIterableList<E>.shuffled(random: Random = Random): KoneIterableList<E> =
    toKoneSettableIterableList().apply { shuffle(random) }
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.dequeue.KoneDequeue
import dev.lounres.kone.collections.dequeue.isNotEmpty
import dev.lounres.kone.collections.dequeue.popFirst
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.emptyKoneList
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableLinkedList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.koneMutableListOf
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.noElementMatchingThePredicateException
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.list.toKoneSettableList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrSet
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.comparison.*
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.Some
import dev.lounres.kone.repeat
import kotlin.jvm.JvmInline
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextUInt


// TODO: Add operations for array value classes
// TODO: Add operations for iterators
// TODO: Add slicing operations (like `.count(from = 5u, to = 7u) { ... }`)

public fun <E, D: KoneMutableList<in E>> KoneIterable<E>.copyTo(destination: D): D {
    for (element in this) destination.add(element)
    return destination
}
public fun <E, D: KoneMutableSet<in E>> KoneIterable<E>.copyTo(destination: D): D {
    for (element in this) destination.add(element)
    return destination
}

public operator fun <E> KoneMutableList<E>.plusAssign(elements: KoneIterable<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(elements: KoneIterable<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableList<E>.plusAssign(element: E) {
    add(element)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(element: E) {
    add(element)
}

public fun <E> KoneIterable<E>.take(n: UInt): KoneList<E> {
    val size = min(size, n)
    if (size == 0u) return emptyKoneList()
    val iterator = iterator()
    return KoneList(size) { iterator.getAndMoveNext() }
}

public fun <E> KoneIterable<E>.drop(n: UInt): KoneList<E> {
    if (n == 0u) return toKoneList()
    if (n >= size) return emptyKoneList()
    val resultSize = size - n
    val list = KoneArrayFixedCapacityList<E>(resultSize)
    var count = 0u
    for (item in this) {
        if (count >= n) list.add(item) else ++count
    }
    return list.toOptimizedList()
}

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

public inline fun <E> KoneIterable<E>.count(predicate: (value: E) -> Boolean): UInt {
    var count = 0u
    for (element in this) if (predicate(element)) count++
    return count
}

public inline fun <E> KoneIterable<E>.countIndexed(predicate: (index: UInt, value: E) -> Boolean): UInt {
    var count = 0u
    var currentIndex = 0u
    for (element in this) if (predicate(currentIndex++, element)) count++
    return count
}

// TODO: Think about moving `KoneIterableList<E>.first*` extensions inside `KoneIterableList` interface
//  like it is done for `indexThat`.

public inline fun <E> KoneIterable<E>.firstThat(predicate: (E) -> Boolean): E {
    for (element in this) if (predicate(element)) return element
    noElementMatchingThePredicateException()
}

public inline fun <E> KoneList<E>.lastThat(predicate: (E) -> Boolean): E {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(element)) return element
        backIterator.movePrevious()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E> KoneList<E>.firstThatIndexed(predicate: (index: UInt, E) -> Boolean): E {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        if (predicate(iterator.nextIndex(), element)) return element
        iterator.moveNext()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E> KoneList<E>.lastThatIndexed(predicate: (index: UInt, E) -> Boolean): E {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(backIterator.previousIndex(), element)) return element
        backIterator.movePrevious()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E> KoneIterable<E>.firstThatOrNull(predicate: (E) -> Boolean): E? {
    for (element in this) if (predicate(element)) return element
    return null
}

public inline fun <E> KoneList<E>.lastThatOrNull(predicate: (E) -> Boolean): E? {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(element)) return element
        backIterator.movePrevious()
    }
    return null
}

public inline fun <E> KoneList<E>.firstThatIndexedOrNull(predicate: (index: UInt, E) -> Boolean): E? {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        if (predicate(iterator.nextIndex(), element)) return element
        iterator.moveNext()
    }
    return null
}

public inline fun <E> KoneList<E>.lastThatIndexedOrNull(predicate: (index: UInt, E) -> Boolean): E? {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(backIterator.previousIndex(), element)) return element
        backIterator.movePrevious()
    }
    return null
}

public inline fun <E> KoneIterable<E>.firstThatMaybe(predicate: (E) -> Boolean): Maybe<E> {
    for (element in this) if (predicate(element)) return Some(element)
    return None
}

public inline fun <E> KoneList<E>.lastThatMaybe(predicate: (E) -> Boolean): Maybe<E> {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(element)) return Some(element)
        backIterator.movePrevious()
    }
    return None
}

public inline fun <E> KoneList<E>.firstThatIndexedMaybe(predicate: (index: UInt, E) -> Boolean): Maybe<E> {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        if (predicate(iterator.nextIndex(), element)) return Some(element)
        iterator.moveNext()
    }
    return None
}

public inline fun <E> KoneList<E>.lastThatIndexedMaybe(predicate: (index: UInt, E) -> Boolean): Maybe<E> {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        if (predicate(backIterator.previousIndex(), element)) return Some(element)
        backIterator.movePrevious()
    }
    return None
}

public inline fun <E, R> KoneIterable<E>.firstOfThat(transform: (E) -> R, predicate: (R) -> Boolean): R {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    noElementMatchingThePredicateException()
}

public inline fun <E, R> KoneList<E>.lastOfThat(transform: (E) -> R, predicate: (R) -> Boolean): R {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(element)
        if (predicate(result)) return result
        backIterator.movePrevious()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E, R> KoneList<E>.firstOfThatIndexed(transform: (index: UInt, E) -> R, predicate: (R) -> Boolean): R {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        val result = transform(iterator.nextIndex(), element)
        if (predicate(result)) return result
        iterator.moveNext()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E, R> KoneList<E>.lastOfThatIndexed(transform: (index: UInt, E) -> R, predicate: (R) -> Boolean): R {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(backIterator.previousIndex(), element)
        if (predicate(result)) return result
        backIterator.movePrevious()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E, R> KoneIterable<E>.firstOfThatOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return result
    }
    return null
}

public inline fun <E, R> KoneList<E>.lastOfThatOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(element)
        if (predicate(result)) return result
        backIterator.movePrevious()
    }
    return null
}

public inline fun <E, R> KoneList<E>.firstOfThatIndexedOrNull(transform: (index: UInt, E) -> R, predicate: (R) -> Boolean): R? {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        val result = transform(iterator.nextIndex(), element)
        if (predicate(result)) return result
        iterator.moveNext()
    }
    return null
}

public inline fun <E, R> KoneList<E>.lastOfThatIndexedOrNull(transform: (index:UInt, E) -> R, predicate: (R) -> Boolean): R? {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(backIterator.previousIndex(), element)
        if (predicate(result)) return result
        backIterator.movePrevious()
    }
    return null
}

public inline fun <E, R> KoneIterable<E>.firstOfThatMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Maybe<R> {
    for (element in this) {
        val result = transform(element)
        if (predicate(result)) return Some(result)
    }
    return None
}

public inline fun <E, R> KoneList<E>.lastOfThatMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Maybe<R> {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(element)
        if (predicate(result)) return Some(result)
        backIterator.movePrevious()
    }
    return None
}

public inline fun <E, R> KoneList<E>.firstOfThatIndexedMaybe(transform: (index: UInt, E) -> R, predicate: (R) -> Boolean): Maybe<R> {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        val result = transform(iterator.nextIndex(), element)
        if (predicate(result)) return Some(result)
        iterator.moveNext()
    }
    return None
}

public inline fun <E, R> KoneList<E>.lastOfThatIndexedMaybe(transform: (index: UInt, E) -> R, predicate: (R) -> Boolean): Maybe<R> {
    val backIterator = iteratorFrom(size)
    while (backIterator.hasPrevious()) {
        val element = backIterator.getPrevious()
        val result = transform(backIterator.previousIndex(), element)
        if (predicate(result)) return Some(result)
        backIterator.movePrevious()
    }
    return None
}

public inline fun <E> KoneList<E>.firstIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
    var iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        if (predicate(iterator.nextIndex(), element)) break
        iterator.moveNext()
    }
    return iterator.nextIndex()
}

context(Equality<E>)
public fun <E> KoneList<E>.firstIndexOf(element: E): UInt = firstIndexThat { _, currentElement -> element eq currentElement }

public inline fun <E> KoneList<E>.lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
    var iterator = iteratorFrom(size)
    while (iterator.hasPrevious()) {
        val element = iterator.getPrevious()
        if (predicate(iterator.previousIndex(), element)) break
        iterator.movePrevious()
    }
    return iterator.previousIndex()
}

context(Equality<E>)
public fun <E> KoneList<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, currentElement -> element eq currentElement }

public fun <E> KoneIterable<E>.random(random: Random): E {
    val index = random.nextUInt(0u, size)
    val iterator = iterator()
    repeat(index) { iterator.moveNext() }
    return iterator.getNext()
}

public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapTo(destination: D, transform: (E) -> R): D {
    for (element in this) destination.add(transform(element))
    return destination
}

public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    for (element in this) destination.add(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R> KoneIterable<E>.map(transform: (E) -> R): KoneList<R> = mapTo(koneMutableListOf(), transform)

public inline fun <E, R> KoneIterable<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneList<R> =
    mapIndexedTo(koneMutableListOf(), transform)

public inline fun <E, D: KoneMutableList<in E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
public inline fun <E, D: KoneMutableSet<in E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

public inline fun <E> KoneIterable<E>.filter(predicate: (E) -> Boolean): KoneList<E> =
    filterTo(koneMutableListOf(), predicate)

public inline fun <E, R> KoneIterator<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}
public inline fun <E, R> KoneIterable<E>.fold(initial: R, operation: (acc: R, E) -> R): R =
    iterator().fold(initial, operation)

public inline fun <E, R> KoneList<E>.foldRight(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(accumulator, get(index))
    return accumulator
}

public inline fun <E, R> KoneIterator<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    var index = 0u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}
public inline fun <E, R> KoneIterable<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R =
    iterator().foldIndexed(initial, operation)

public inline fun <E, R> KoneList<E>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

public inline fun <E, R> KoneIterator<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneList<R> {
    val result = KoneArrayGrowableList(1u) { initial }
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
        result.add(accumulator)
    }
    return result.toOptimizedList()
}
public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneList<R> {
    val result = KoneSettableList(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

public inline fun <E, R> KoneIterator<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneList<R> {
    val result = KoneArrayGrowableList(1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result.add(accumulator)
        index++
    }
    return result
}
public inline fun <E, R> KoneIterable<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneList<R> {
    val result = KoneSettableList(size + 1u) { initial }
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

public inline fun <E: R, R> KoneIterator<E>.reduce(operation: (acc: R, E) -> R): R {
    if (!this.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}
public inline fun <E: R, R> KoneIterable<E>.reduce(operation: (acc: R, E) -> R): R =
    iterator().reduce(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    if (!this.hasNext()) return null
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}
public inline fun <E: R, R> KoneIterable<E>.reduceOrNull(operation: (acc: R, E) -> R): R? =
    iterator().reduceOrNull(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceMaybe(operation: (acc: R, E) -> R): Maybe<R> {
    if (!this.hasNext()) return None
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return Some(accumulator)
}
public inline fun <E: R, R> KoneIterable<E>.reduceMaybe(operation: (acc: R, E) -> R): Maybe<R> =
    iterator().reduceMaybe(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    if (!this.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}
public inline fun <E: R, R> KoneIterable<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R =
    iterator().reduceIndexed(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    if (!this.hasNext()) return null
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}
public inline fun <E: R, R> KoneIterable<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? =
    iterator().reduceIndexedOrNull(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Maybe<R> {
    if (!this.hasNext()) return None
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return Some(accumulator)
}
public inline fun <E: R, R> KoneIterable<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Maybe<R> =
    iterator().reduceIndexedMaybe(operation)

// TODO: Add `reduce`-like extensions. Like `reduceRight`, `runningReduce`, and `runningReduceRight`.

// TODO: Add summing and multiplying extensions for primitives. Maybe.

context(Ring<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(Ring<N>)
public fun <E, N> KoneIterable<E>.sumOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc + selector(e) }

context(Ring<N>)
public inline fun <E, N> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }

context(Ring<E>)
public fun <E> KoneIterable<E>.product(): E = fold(one) { acc, e -> acc * e }

context(Ring<N>)
public fun <E, N> KoneIterable<E>.productOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc * selector(e) }

context(Ring<N>)
public inline fun <E, N> KoneIterable<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc * selector(index, e) }

public inline fun <E, K, D : KoneMutableMap<in K, KoneMutableList<E>>> KoneIterable<E>.groupByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneArrayGrowableList() }
        accumulator.add(element)
    }
    return destination
}

public inline fun <E, K, V, D : KoneMutableMap<in K, KoneMutableList<V>>> KoneIterable<E>.groupByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneArrayGrowableList() }
        accumulator.add(valueTransform(element))
    }
    return destination
}

public inline fun <E, K> KoneIterable<E>.groupBy(keyContext: Equality<K> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, KoneList<E>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext), keySelector = keySelector)

public inline fun <E, K, V> KoneIterable<E>.groupBy(keyContext: Equality<K> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, KoneList<V>> =
    groupByTo(destination = koneMutableMapOf(keyContext = keyContext), keySelector = keySelector, valueTransform = valueTransform)

@PublishedApi
@JvmInline
internal value class RangeToSort(val from: UInt, val to: UInt) {
    operator fun component1(): UInt = from
    operator fun component2(): UInt = to
}

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
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
            while (comparator.compare(this[i], valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
            while (comparator.compare(this[j], valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
            while (comparator.compare(this[i], valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
            while (comparator.compare(this[j], valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
            if (i <= j) {
                this[i] = this[j].also { this[j] = this[i] }
                i++
                j--
            } else break
        }
        return i
    }
    fun quickSort(from: UInt, to: UInt) {
        val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
        while (comparator.compare(selector(this[i]), valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) j--
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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
        while (comparator.compare(selector(this[i]), valueInTheMiddle) == ComparisonResult.LeftIsGreaterThanRight) i++
        while (comparator.compare(selector(this[j]), valueInTheMiddle) == ComparisonResult.LeftIsLessThanRight) j--
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
    val sortQueue: KoneDequeue<RangeToSort> = KoneArrayGrowableLinkedList()
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

public fun <E: Comparable<E>> KoneIterable<E>.sorted(): KoneList<E> =
    toKoneSettableList().apply { sort() }

context(Order<E>)
public fun <E> KoneIterable<E>.sorted(): KoneList<E> =
    toKoneSettableList().apply { sort() }

public fun <E> KoneIterable<E>.sortedWith(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { sortWith(comparator) }

public fun <E: Comparable<E>> KoneIterable<E>.sortedDescending(): KoneList<E> =
    toKoneSettableList().apply { sortDescending() }

context(Order<E>)
public fun <E> KoneIterable<E>.sortedDescending(): KoneList<E> =
    toKoneSettableList().apply { sortDescending() }

public fun <E> KoneIterable<E>.sortedWithDescending(comparator: Comparator<E>): KoneList<E> =
    toKoneSettableList().apply { sortWithDescending(comparator) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortBy(selector) }

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortBy(selector) }

public inline fun <E, R> KoneIterable<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortWithBy(comparator, selector) }

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortByDescending(selector) }

context(Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortByDescending(selector) }

public inline fun <E, R> KoneIterable<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> =
    toKoneSettableList().apply { sortWithByDescending(comparator, selector) }

// https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
public fun <E> KoneSettableList<E>.shuffle(random: Random = Random) {
    for (i in 0u ..< size - 1u) {
        val j = random.nextUInt(i, size)
        this[i] = this[j].also { this[j] = this[i] }
    }
}

public fun <E> KoneIterable<E>.shuffled(random: Random = Random): KoneList<E> =
    toKoneSettableList().apply { shuffle(random) }
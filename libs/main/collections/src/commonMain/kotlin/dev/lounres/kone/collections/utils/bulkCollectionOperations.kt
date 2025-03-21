/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.noElementMatchingThePredicateException
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.utils.sorting.heapsort
import dev.lounres.kone.collections.utils.sorting.heapsortBy
import dev.lounres.kone.collections.utils.sorting.heapsortByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortDescending
import dev.lounres.kone.collections.utils.sorting.heapsortWith
import dev.lounres.kone.collections.utils.sorting.heapsortWithBy
import dev.lounres.kone.collections.utils.sorting.heapsortWithByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortWithDescending
import dev.lounres.kone.collections.utils.sorting.heapsorted
import dev.lounres.kone.collections.utils.sorting.heapsortedBy
import dev.lounres.kone.collections.utils.sorting.heapsortedByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedWith
import dev.lounres.kone.collections.utils.sorting.heapsortedWithBy
import dev.lounres.kone.collections.utils.sorting.heapsortedWithByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedWithDescending
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.eq
import dev.lounres.kone.repeat
import dev.lounres.kone.util.suppliedTypes.SuppliedType
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
    val newSize = min(size, n)
    if (newSize == 0u) return emptyKoneList()
    val iterator = iterator()
    return KoneList(newSize) { iterator.getAndMoveNext() }
}

public fun <E> KoneIterable<E>.takeLast(n: UInt): KoneList<E> {
    val newSize = min(size, n)
    if (newSize == 0u) return emptyKoneList()
    val iterator = iterator()
    repeat(size - newSize) { iterator.moveNext() }
    return KoneList(newSize) { iterator.getAndMoveNext() }
}

public fun <E> KoneList<E>.takeLast(n: UInt): KoneList<E> {
    val newSize = min(size, n)
    if (newSize == 0u) return emptyKoneList()
    val result = KoneMutableArray<Any?>(newSize) { null }
    var currentIndex = newSize - 1u
    val iterator = iteratorFrom(size)
    repeat(newSize) {
        result[currentIndex] = iterator.getPrevious()
        iterator.movePrevious()
        currentIndex--
    }
    return KoneArraySettableList(result)
}

public fun <E> KoneIterable<E>.drop(n: UInt): KoneList<E> = takeLast(size - n)
public fun <E> KoneList<E>.drop(n: UInt): KoneList<E> = takeLast(size - n)
public fun <E> KoneIterable<E>.dropLast(n: UInt): KoneList<E> = take(size - n)

public fun <E> KoneList<E>.slice(fromIndex: UInt, toIndex: UInt): KoneList<E> {
    if (toIndex < fromIndex) return emptyKoneList()
    val iterator = iteratorFrom(fromIndex)
    return KoneList(toIndex - fromIndex) { iterator.getAndMoveNext() }
}

public fun <E> KoneSettableList<E>.reverse() {
    if (size <= 1u) return
    
    val forwardIterator = iterator()
    val backwardIterator = iteratorFrom(size)
    repeat(size / 2u) {
        val forward = forwardIterator.getNext()
        val backward = backwardIterator.getPrevious()
        forwardIterator.setNext(backward)
        forwardIterator.moveNext()
        backwardIterator.setPrevious(forward)
        backwardIterator.movePrevious()
    }
}

public fun <E> KoneIterable<E>.reversed(): KoneList<E> {
    if (isEmpty()) return emptyKoneList()
    
    val result = KoneMutableArray<Any?>(size) { null }
    var currentIndex = size - 1u
    val iterator = iterator()
    while (iterator.hasNext()) {
        result[currentIndex] = iterator.getNext()
        iterator.moveNext()
        currentIndex--
    }
    return KoneArraySettableList(result)
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
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.getNext()
        if (predicate(iterator.nextIndex(), element)) break
        iterator.moveNext()
    }
    return if (iterator.hasNext()) iterator.nextIndex() else size
}

context(_: Equality<E>)
public fun <E> KoneList<E>.firstIndexOf(element: E): UInt = firstIndexThat { _, currentElement -> element eq currentElement }

public inline fun <E> KoneList<E>.lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
    val iterator = iteratorFrom(size)
    while (iterator.hasPrevious()) {
        val element = iterator.getPrevious()
        if (predicate(iterator.previousIndex(), element)) break
        iterator.movePrevious()
    }
    return if (iterator.hasPrevious()) iterator.previousIndex() else UInt.MAX_VALUE
}

context(_: Equality<E>)
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

// TODO: Reimplement using just KoneArraySettableList
public inline fun <E, R> KoneIterable<E>.map(transform: (E) -> R): KoneList<R> = mapTo(koneMutableListOf(), transform)

public inline fun <E, R> KoneIterable<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneList<R> =
    mapIndexedTo(koneMutableListOf(), transform)

public fun <E> KoneIterable<KoneIterable<E>>.flatten(): KoneList<E> {
    val result = koneMutableListOf<E>()
    for (iterable in this) result.addAllFrom(iterable)
    return result
}

public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D {
    for (element in this) destination.addAllFrom(transform(element))
    return destination
}
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D {
    for (element in this) destination.addAllFrom(transform(element))
    return destination
}

public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D {
    var currentIndex = 0u
    for (element in this) destination.addAllFrom(transform(currentIndex++, element))
    return destination
}
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D {
    var currentIndex = 0u
    for (element in this) destination.addAllFrom(transform(currentIndex++, element))
    return destination
}

public inline fun <E, R> KoneIterable<E>.flatMap(transform: (E) -> KoneIterable<R>): KoneList<R> = flatMapTo(koneMutableListOf(), transform)

public inline fun <E, R> KoneIterable<E>.flatMapIndexed(transform: (index: UInt, E) -> KoneIterable<R>): KoneList<R> =
    flatMapIndexedTo(koneMutableListOf(), transform)

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

context(_: Semiring<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(zero) { acc, e -> acc + e }

context(_: Semiring<N>)
public fun <E, N> KoneIterable<E>.sumOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc + selector(e) }

context(_: Semiring<N>)
public inline fun <E, N> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(zero) { index, acc, e -> acc + selector(index, e) }

context(_: Semiring<E>)
public fun <E> KoneIterable<E>.product(): E = fold(one) { acc, e -> acc * e }

context(_: Semiring<N>)
public fun <E, N> KoneIterable<E>.productOf(selector: (E) -> N): N = fold(zero) { acc, e -> acc * selector(e) }

context(_: Semiring<N>)
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

public inline fun <E, K> KoneIterable<E>.groupBy(
    keyEquality: Equality<K> = defaultEquality(),
    keyHashing: Hashing<K>? = null,
    keyOrder: Order<K>? = null,
    keySelector: (E) -> K
): KoneMap<K, KoneList<E>> =
    groupByTo(
        destination = koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector
    )

context(_: KoneContextRegistry)
public inline fun <E, K> KoneIterable<E>.groupContextualBy(
    keyType: SuppliedType<K>,
    keySelector: (E) -> K
): KoneMap<K, KoneList<E>> =
    groupByTo(
        destination = koneContextualMutableMapOf(
            keyType = keyType,
        ),
        keySelector = keySelector
    )

public inline fun <E, K, V> KoneIterable<E>.groupBy(
    keyEquality: Equality<K> = defaultEquality(),
    keyHashing: Hashing<K>? = null,
    keyOrder: Order<K>? = null,
    keySelector: (E) -> K,
    valueTransform: (E) -> V,
): KoneMap<K, KoneList<V>> =
    groupByTo(
        destination = koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

context(_: KoneContextRegistry)
public inline fun <E, K, V> KoneIterable<E>.groupContextualBy(
    keyType: SuppliedType<K>,
    keySelector: (E) -> K,
    valueTransform: (E) -> V,
): KoneMap<K, KoneList<V>> =
    groupByTo(
        destination = koneContextualMutableMapOf(
            keyType = keyType,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

public fun <E: Comparable<E>> KoneSettableList<E>.sort() {
    heapsort()
}

context(_: Order<E>)
public fun <E> KoneSettableList<E>.sort() {
    heapsort()
}

public fun <E> KoneSettableList<E>.sortWith(comparator: Comparator<E>) {
    heapsortWith(comparator)
}

public fun <E: Comparable<E>> KoneSettableList<E>.sortDescending() {
    heapsortDescending()
}

context(_: Order<E>)
public fun <E> KoneSettableList<E>.sortDescending() {
    heapsortDescending()
}

public fun <E> KoneSettableList<E>.sortWithDescending(comparator: Comparator<E>) {
    heapsortWithDescending(comparator)
}

public inline fun <E, R: Comparable<R>> KoneSettableList<E>.sortBy(selector: (E) -> R) {
    heapsortBy(selector)
}

context(_: Order<R>)
public inline fun <E, R> KoneSettableList<E>.sortBy(selector: (E) -> R) {
    heapsortBy(selector)
}

public inline fun <E, R> KoneSettableList<E>.sortWithBy(comparator: Comparator<R>, selector: (E) -> R) {
    heapsortWithBy(comparator, selector)
}

public inline fun <E, R: Comparable<R>> KoneSettableList<E>.sortByDescending(selector: (E) -> R) {
    heapsortByDescending(selector)
}

context(_: Order<R>)
public inline fun <E, R> KoneSettableList<E>.sortByDescending(selector: (E) -> R) {
    heapsortByDescending(selector)
}

public inline fun <E, R> KoneSettableList<E>.sortWithByDescending(comparator: Comparator<R>, selector: (E) -> R) {
    heapsortWithByDescending(comparator, selector)
}

public fun <E: Comparable<E>> KoneIterable<E>.sorted(): KoneList<E> = heapsorted()

context(_: Order<E>)
public fun <E> KoneIterable<E>.sorted(): KoneList<E> = heapsorted()

public fun <E> KoneIterable<E>.sortedWith(comparator: Comparator<E>): KoneList<E> = heapsortedWith(comparator)

public fun <E: Comparable<E>> KoneIterable<E>.sortedDescending(): KoneList<E> = heapsortedDescending()

context(_: Order<E>)
public fun <E> KoneIterable<E>.sortedDescending(): KoneList<E> = heapsortedDescending()

public fun <E> KoneIterable<E>.sortedWithDescending(comparator: Comparator<E>): KoneList<E> = heapsortedWithDescending(comparator)

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneList<E> = heapsortedBy(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedBy(selector: (E) -> R): KoneList<E> = heapsortedBy(selector)

public inline fun <E, R> KoneIterable<E>.sortedWithBy(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = heapsortedWithBy(comparator, selector)

public inline fun <E, R: Comparable<R>> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneList<E> = heapsortedByDescending(selector)

context(_: Order<R>)
public inline fun <E, R> KoneIterable<E>.sortedByDescending(selector: (E) -> R): KoneList<E> = heapsortedByDescending(selector)

public inline fun <E, R> KoneIterable<E>.sortedWithByDescending(comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = heapsortedWithByDescending(comparator, selector)

// https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
public fun <E> KoneSettableList<E>.shuffle(random: Random = Random) {
    for (i in 0u ..< size - 1u) {
        val j = random.nextUInt(i, size)
        this[i] = this[j].also { this[j] = this[i] }
    }
}

public fun <E> KoneIterable<E>.shuffled(random: Random = Random): KoneList<E> =
    toKoneSettableList().apply { shuffle(random) }
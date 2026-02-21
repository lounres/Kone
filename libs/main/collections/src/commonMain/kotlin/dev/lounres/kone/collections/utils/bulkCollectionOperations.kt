/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.empty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.iterables.empty.KoneEmptySettableLinearIterator
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.noElementMatchingThePredicateException
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.removeAllFrom
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
import dev.lounres.kone.maybe.ifSome
import dev.lounres.kone.maybe.isSome
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import dev.lounres.kone.repeat
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.random.Random
import kotlin.random.nextUInt


// TODO: Add operations for array value classes
// TODO: Add slicing operations (like `.count(from = 5u, to = 7u) { ... }`)

@IgnorableReturnValue
public fun <E, D: KoneMutableList<in E>> KoneIterator<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}
@IgnorableReturnValue
public fun <E, D: KoneMutableSet<in E>> KoneIterator<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}

@IgnorableReturnValue
public fun <E, D: KoneMutableList<in E>> KoneIterable<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}
@IgnorableReturnValue
public fun <E, D: KoneMutableSet<in E>> KoneIterable<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}

@IgnorableReturnValue
public fun <E, D: KoneMutableList<in E>> KoneSequence<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}
@IgnorableReturnValue
public fun <E, D: KoneMutableSet<in E>> KoneSequence<E>.copyTo(destination: D): D {
    destination.addAllFrom(this)
    return destination
}

public operator fun <E> KoneMutableList<E>.plusAssign(elements: KoneIterator<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(elements: KoneIterator<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableList<E>.plusAssign(elements: KoneIterable<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(elements: KoneIterable<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableList<E>.plusAssign(elements: KoneSequence<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(elements: KoneSequence<E>) {
    addAllFrom(elements)
}

public operator fun <E> KoneMutableList<E>.plusAssign(element: E) {
    add(element)
}

public operator fun <E> KoneMutableSet<E>.plusAssign(element: E) {
    add(element)
}

public operator fun <E> KoneMutableSet<E>.minusAssign(elements: KoneIterator<E>) {
    removeAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.minusAssign(elements: KoneIterable<E>) {
    removeAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.minusAssign(elements: KoneSequence<E>) {
    removeAllFrom(elements)
}

public operator fun <E> KoneMutableSet<E>.minusAssign(element: E) {
    remove(element)
}

private class KoneTakeIterator<Element>(
    private val source: KoneIterator<Element>,
    private val n: UInt,
) : KoneIterator<Element> {
    private var nextIndex = 0u
    override fun hasNext(): Boolean = nextIndex < n && source.hasNext()
    override fun getNext(): Element {
        if (!hasNext()) noNextElementInIteratorException()
        return source.getNext()
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        source.moveNext()
    }
}

public fun <E> KoneIterator<E>.take(n: UInt): KoneIterator<E> = KoneTakeIterator(this, n)

public fun <E> KoneIterable<E>.take(n: UInt): KoneList<E> {
    val newSize = minOf(size, n)
    if (newSize == 0u) return KoneList.empty()
    val iterator = iterator()
    return KoneList.generate(newSize) { iterator.getAndMoveNext() }
}

private class KoneTakeSequence<Element>(
    private val source: KoneSequence<Element>,
    private val n: UInt,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = source.iterator().take(n)
}

public fun <E> KoneSequence<E>.take(n: UInt): KoneSequence<E> = KoneTakeSequence(this, n)

public fun <E> KoneIterator<E>.takeLast(n: UInt): KoneIterator<E> {
    val deque = KoneDeque.empty<E>()
    while (hasNext()) {
        deque.addLast(getAndMoveNext())
        if (deque.size > n) deque.removeFirst()
    }
    return KoneList.generate(deque.size) { deque.popFirst() }.iterator()
}

public fun <E> KoneIterable<E>.takeLast(n: UInt): KoneList<E> {
    val newSize = minOf(size, n)
    if (newSize == 0u) return KoneList.empty()
    val iterator = iterator()
    repeat(size - newSize) { iterator.moveNext() }
    return KoneList.generate(newSize) { iterator.getAndMoveNext() }
}

public fun <E> KoneList<E>.takeLast(n: UInt): KoneList<E> {
    val newSize = minOf(size, n)
    if (newSize == 0u) return KoneList.empty()
    val result = KoneMutableArray.generate<Any?>(newSize) { null }
    var currentIndex = newSize - 1u
    val iterator = iteratorFrom(size)
    repeat(newSize) {
        result[currentIndex] = iterator.getPrevious()
        iterator.movePrevious()
        currentIndex--
    }
    return KoneArraySettableList(result)
}

private class KoneTakeLastSequence<Element>(
    private val source: KoneSequence<Element>,
    private val n: UInt,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = source.iterator().takeLast(n)
}

public fun <E> KoneSequence<E>.takeLast(n: UInt): KoneSequence<E> = KoneTakeLastSequence(this, n)

public fun <E> KoneIterator<E>.drop(n: UInt): KoneIterator<E> {
    var index = 0u
    while (index < n && hasNext()) {
        moveNext()
        index++
    }
    return this
}

public fun <E> KoneIterable<E>.drop(n: UInt): KoneList<E> = takeLast(size - minOf(size, n))

public fun <E> KoneList<E>.drop(n: UInt): KoneList<E> = takeLast(size - minOf(size, n))

private class KoneDropSequence<Element>(
    private val source: KoneSequence<Element>,
    private val n: UInt,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = source.iterator().drop(n)
}

public fun <E> KoneSequence<E>.drop(n: UInt): KoneSequence<E> = KoneDropSequence(this, n)

public fun <E> KoneIterator<E>.dropLast(n: UInt): KoneIterator<E> {
    val cache = KoneArrayGrowableList<E>()
    while (hasNext()) cache.add(getAndMoveNext())
    return if (cache.size < n) KoneEmptySettableLinearIterator else KoneList.generate(cache.size - n) { cache[it] }.iterator()
}

public fun <E> KoneIterable<E>.dropLast(n: UInt): KoneList<E> = take(size - minOf(size, n))

private class KoneDropLastSequence<Element>(
    private val source: KoneSequence<Element>,
    private val n: UInt,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = source.iterator().dropLast(n)
}

public fun <E> KoneSequence<E>.dropLast(n: UInt): KoneSequence<E> = KoneDropLastSequence(this, n)

public fun <E> KoneList<E>.slice(fromIndex: UInt, untilIndex: UInt): KoneList<E> {
    if (untilIndex <= fromIndex) return KoneList.empty()
    val iterator = iteratorFrom(fromIndex)
    return KoneList.generate(untilIndex - fromIndex) { iterator.getAndMoveNext() }
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
    if (isEmpty()) return KoneList.empty()
    
    val result = KoneMutableArray.generate<Any?>(size) { null }
    var currentIndex = size - 1u
    val iterator = iterator()
    while (iterator.hasNext()) {
        result[currentIndex] = iterator.getNext()
        iterator.moveNext()
        currentIndex--
    }
    return KoneArraySettableList(result)
}

public inline fun <E> KoneIterator<E>.forEach(block: (value: E) -> Unit) {
    while (hasNext()) block(getAndMoveNext())
}

public inline fun <E> KoneIterable<E>.forEach(block: (value: E) -> Unit) {
    iterator().forEach(block)
}

public inline fun <E> KoneSequence<E>.forEach(block: (value: E) -> Unit) {
    iterator().forEach(block)
}

public inline fun <E> KoneIterator<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    while (hasNext()) block(index++, getAndMoveNext())
}

public inline fun <E> KoneIterable<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    iterator().forEachIndexed(block)
}

public inline fun <E> KoneSequence<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    iterator().forEachIndexed(block)
}

public inline fun <E> KoneIterator<E>.withEach(block: E.() -> Unit) {
    while (hasNext()) getAndMoveNext().block()
}

public inline fun <E> KoneIterable<E>.withEach(block: E.() -> Unit) {
    iterator().withEach(block)
}

public inline fun <E> KoneSequence<E>.withEach(block: E.() -> Unit) {
    iterator().withEach(block)
}

public inline fun <E> KoneIterator<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    var index = 0u
    while (hasNext()) getAndMoveNext().block(index++)
}

public inline fun <E> KoneIterable<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    iterator().withEachIndexed(block)
}

public inline fun <E> KoneSequence<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    iterator().withEachIndexed(block)
}

public inline fun <E> KoneIterator<E>.any(block: (value: E) -> Boolean): Boolean {
    while (hasNext()) if (block(getNext())) return true else moveNext()
    return false
}

public inline fun <E> KoneIterable<E>.any(block: (value: E) -> Boolean): Boolean = iterator().any(block)

public inline fun <E> KoneSequence<E>.any(block: (value: E) -> Boolean): Boolean = iterator().any(block)

public inline fun <E> KoneIterator<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    while (hasNext()) if (block(currentIndex++, getNext())) return true else moveNext()
    return false
}

public inline fun <E> KoneIterable<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().anyIndexed(block)

public inline fun <E> KoneSequence<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().anyIndexed(block)

public fun KoneIterator<Boolean>.any(): Boolean = any { it }

public fun KoneIterable<Boolean>.any(): Boolean = any { it }

public fun KoneSequence<Boolean>.any(): Boolean = any { it }

public inline fun <E> KoneIterator<E>.all(block: (value: E) -> Boolean): Boolean {
    while (hasNext()) if (!block(getNext())) return false else moveNext()
    return true
}

public inline fun <E> KoneIterable<E>.all(block: (value: E) -> Boolean): Boolean = iterator().all(block)

public inline fun <E> KoneSequence<E>.all(block: (value: E) -> Boolean): Boolean = iterator().all(block)

public inline fun <E> KoneIterator<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    while (hasNext()) if (!block(currentIndex++, getNext())) return false else moveNext()
    return true
}

public inline fun <E> KoneIterable<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().allIndexed(block)

public inline fun <E> KoneSequence<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().allIndexed(block)

public fun KoneIterator<Boolean>.all(): Boolean = all { it }

public fun KoneIterable<Boolean>.all(): Boolean = all { it }

public fun KoneSequence<Boolean>.all(): Boolean = all { it }

public inline fun <E> KoneIterator<E>.none(block: (value: E) -> Boolean): Boolean {
    while (hasNext()) if (block(getNext())) return false else moveNext()
    return true
}

public inline fun <E> KoneIterable<E>.none(block: (value: E) -> Boolean): Boolean = iterator().none(block)

public inline fun <E> KoneSequence<E>.none(block: (value: E) -> Boolean): Boolean = iterator().none(block)

public inline fun <E> KoneIterator<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    while (hasNext()) if (block(currentIndex++, getNext())) return false else moveNext()
    return true
}

public inline fun <E> KoneIterable<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().noneIndexed(block)

public inline fun <E> KoneSequence<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = iterator().noneIndexed(block)

public fun KoneIterator<Boolean>.none(): Boolean = none { it }

public fun KoneIterable<Boolean>.none(): Boolean = none { it }

public fun KoneSequence<Boolean>.none(): Boolean = none { it }

public inline fun <E> KoneIterator<E>.count(predicate: (value: E) -> Boolean): UInt {
    var count = 0u
    while (hasNext()) if (predicate(getAndMoveNext())) count++
    return count
}

public inline fun <E> KoneIterable<E>.count(predicate: (value: E) -> Boolean): UInt = iterator().count(predicate)

public inline fun <E> KoneSequence<E>.count(predicate: (value: E) -> Boolean): UInt = iterator().count(predicate)

public inline fun <E> KoneIterator<E>.countIndexed(predicate: (index: UInt, value: E) -> Boolean): UInt {
    var count = 0u
    var currentIndex = 0u
    while (hasNext()) if (predicate(currentIndex++, getAndMoveNext())) count++
    return count
}

public inline fun <E> KoneIterable<E>.countIndexed(predicate: (index: UInt, value: E) -> Boolean): UInt = iterator().countIndexed(predicate)

public inline fun <E> KoneSequence<E>.countIndexed(predicate: (index: UInt, value: E) -> Boolean): UInt = iterator().countIndexed(predicate)

public fun KoneIterator<Boolean>.count(): UInt = count { it }

public fun KoneIterable<Boolean>.count(): UInt = count { it }

public fun KoneSequence<Boolean>.count(): UInt = count { it }

public inline fun <E> KoneIterator<E>.firstThat(predicate: (E) -> Boolean): E {
    while (hasNext()) {
        val element = getNext()
        if (predicate(element)) return element else moveNext()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E> KoneIterable<E>.firstThat(predicate: (E) -> Boolean): E = iterator().firstThat(predicate)

public inline fun <E> KoneSequence<E>.firstThat(predicate: (E) -> Boolean): E = iterator().firstThat(predicate)

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

public inline fun <E> KoneIterator<E>.firstThatOrNull(predicate: (E) -> Boolean): E? {
    while (hasNext()) {
        val element = getNext()
        if (predicate(element)) return element else moveNext()
    }
    return null
}

public inline fun <E> KoneIterable<E>.firstThatOrNull(predicate: (E) -> Boolean): E? = iterator().firstThatOrNull(predicate)

public inline fun <E> KoneSequence<E>.firstThatOrNull(predicate: (E) -> Boolean): E? = iterator().firstThatOrNull(predicate)

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

public inline fun <E> KoneIterator<E>.firstThatMaybe(predicate: (E) -> Boolean): Maybe<E> {
    while (hasNext()) {
        val element = getNext()
        if (predicate(element)) return Some(element) else moveNext()
    }
    return None
}

public inline fun <E> KoneIterable<E>.firstThatMaybe(predicate: (E) -> Boolean): Maybe<E> = iterator().firstThatMaybe(predicate)

public inline fun <E> KoneSequence<E>.firstThatMaybe(predicate: (E) -> Boolean): Maybe<E> = iterator().firstThatMaybe(predicate)

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

public inline fun <E, R> KoneIterator<E>.firstOfThat(transform: (E) -> R, predicate: (R) -> Boolean): R {
    while (hasNext()) {
        val result = transform(getNext())
        if (predicate(result)) return result else moveNext()
    }
    noElementMatchingThePredicateException()
}

public inline fun <E, R> KoneIterable<E>.firstOfThat(transform: (E) -> R, predicate: (R) -> Boolean): R = iterator().firstOfThat(transform, predicate)

public inline fun <E, R> KoneSequence<E>.firstOfThat(transform: (E) -> R, predicate: (R) -> Boolean): R = iterator().firstOfThat(transform, predicate)

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

public inline fun <E, R> KoneIterator<E>.firstOfThatOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? {
    while (hasNext()) {
        val result = transform(getNext())
        if (predicate(result)) return result else moveNext()
    }
    return null
}

public inline fun <E, R> KoneIterable<E>.firstOfThatOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? = iterator().firstOfThatOrNull(transform, predicate)

public inline fun <E, R> KoneSequence<E>.firstOfThatOrNull(transform: (E) -> R, predicate: (R) -> Boolean): R? = iterator().firstOfThatOrNull(transform, predicate)

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

public inline fun <E, R> KoneIterator<E>.firstOfThatMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Maybe<R> {
    while (hasNext()) {
        val result = transform(getNext())
        if (predicate(result)) return Some(result) else moveNext()
    }
    return None
}

public inline fun <E, R> KoneIterable<E>.firstOfThatMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Maybe<R> = iterator().firstOfThatMaybe(transform, predicate)

public inline fun <E, R> KoneSequence<E>.firstOfThatMaybe(transform: (E) -> R, predicate: (R) -> Boolean): Maybe<R> = iterator().firstOfThatMaybe(transform, predicate)

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

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterator<E>.mapTo(destination: D, transform: (E) -> R): D {
    while (hasNext()) destination.add(transform(getAndMoveNext()))
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterator<E>.mapTo(destination: D, transform: (E) -> R): D {
    while (hasNext()) destination.add(transform(getAndMoveNext()))
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapTo(destination: D, crossinline transform: (E) -> R): D {
    val iterator = iterator()
    destination.addSeveral(size) { transform(iterator.getAndMoveNext()) }
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapTo(destination: D, crossinline transform: (E) -> R): D {
    val iterator = iterator()
    destination.addSeveral(size) { transform(iterator.getAndMoveNext()) }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapToInline(destination: D, transform: (E) -> R): D {
    val iterator = iterator()
    repeat(size) { destination.add(transform(iterator.getAndMoveNext())) }
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapToInline(destination: D, transform: (E) -> R): D {
    val iterator = iterator()
    repeat(size) { destination.add(transform(iterator.getAndMoveNext())) }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneSequence<E>.mapTo(destination: D, transform: (E) -> R): D = iterator().mapTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneSequence<E>.mapTo(destination: D, transform: (E) -> R): D = iterator().mapTo(destination, transform)

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterator<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    while (hasNext()) destination.add(transform(currentIndex++, getAndMoveNext()))
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterator<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D {
    var currentIndex = 0u
    while (hasNext()) destination.add(transform(currentIndex++, getAndMoveNext()))
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapIndexedTo(destination: D, crossinline transform: (index: UInt, E) -> R): D {
    val iterator = iterator()
    var currentIndex = 0u
    destination.addSeveral(size) { transform(currentIndex++, iterator.getAndMoveNext()) }
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapIndexedTo(destination: D, crossinline transform: (index: UInt, E) -> R): D {
    val iterator = iterator()
    var currentIndex = 0u
    destination.addSeveral(size) { transform(currentIndex++, iterator.getAndMoveNext()) }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.mapIndexedToInline(destination: D, crossinline transform: (index: UInt, E) -> R): D {
    val iterator = iterator()
    var currentIndex = 0u
    repeat(size) { destination.add(transform(currentIndex++, iterator.getAndMoveNext())) }
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapIndexedToInline(destination: D, crossinline transform: (index: UInt, E) -> R): D {
    val iterator = iterator()
    var currentIndex = 0u
    repeat(size) { destination.add(transform(currentIndex++, iterator.getAndMoveNext())) }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneSequence<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D = iterator().mapIndexedTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneSequence<E>.mapIndexedTo(destination: D, transform: (index: UInt, E) -> R): D = iterator().mapIndexedTo(destination, transform)

private class KoneMapIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (Element) -> Result,
) : KoneIterator<Result> {
    override fun hasNext(): Boolean = source.hasNext()
    override fun getNext(): Result = transform(source.getNext())
    override fun moveNext() {
        source.moveNext()
    }
}

public fun <E, R> KoneIterator<E>.map(transform: (E) -> R): KoneIterator<R> = KoneMapIterator(this, transform)

public inline fun <E, R> KoneIterable<E>.map(transform: (E) -> R): KoneList<R> {
    val iterator = iterator()
    return KoneList.generate(size) { transform(iterator.getAndMoveNext()) }
}

private class KoneMapSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val transform: (Element) -> Result,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().map(transform)
}

public fun <E, R> KoneSequence<E>.map(transform: (E) -> R): KoneSequence<R> = KoneMapSequence(this, transform)

private class KoneMapIndexedIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (UInt, Element) -> Result,
) : KoneIterator<Result> {
    private var index = 0u
    override fun hasNext(): Boolean = source.hasNext()
    override fun getNext(): Result = transform(index, source.getNext())
    override fun moveNext() {
        source.moveNext()
        index++
    }
}

public fun <E, R> KoneIterator<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneIterator<R> = KoneMapIndexedIterator(this, transform)

public inline fun <E, R> KoneIterable<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneList<R> {
    val iterator = iterator()
    var currentIndex = 0u
    return KoneList.generate(size) { transform(currentIndex++, iterator.getAndMoveNext()) }
}

private class KoneMapIndexedSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val transform: (UInt, Element) -> Result,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().mapIndexed(transform)
}

public fun <E, R> KoneSequence<E>.mapIndexed(transform: (index: UInt, E) -> R): KoneSequence<R> = KoneMapIndexedSequence(this, transform)

private class KoneChunkedIterator<Element>(
    private val source: KoneIterator<Element>,
    private val chunkSize: UInt,
) : KoneIterator<KoneList<Element>> {
    private var currentChunk: KoneList<Element>? = null
    
    override fun hasNext(): Boolean = currentChunk != null || source.hasNext()
    override fun getNext(): KoneList<Element> {
        if (!hasNext()) noNextElementInIteratorException()
        if (currentChunk != null) return currentChunk!!
        
        val newChunk = KoneArrayFixedCapacityList<Element>(chunkSize)
        while (newChunk.size < chunkSize && source.hasNext()) newChunk.add(source.getAndMoveNext())
        
        currentChunk = newChunk
        return newChunk
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        if (currentChunk != null) {
            currentChunk = null
            return
        }
        
        var newChunkSize = 0u
        while (newChunkSize < chunkSize && source.hasNext()) {
            source.moveNext()
            newChunkSize++
        }
    }
}

public fun <E> KoneIterator<E>.chunked(size: UInt): KoneIterator<KoneList<E>> = KoneChunkedIterator(this, size)

private class KoneChunkedLambdaIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val chunkSize: UInt,
    private val mapper: (KoneList<Element>) -> Result
) : KoneIterator<Result> {
    private var currentChunk: Maybe<Result> = None
    
    override fun hasNext(): Boolean = currentChunk.isSome() || source.hasNext()
    override fun getNext(): Result {
        if (!hasNext()) noNextElementInIteratorException()
        currentChunk.ifSome { return it }
        
        val newChunkPremapped = KoneArrayFixedCapacityList<Element>(chunkSize)
        while (newChunkPremapped.size < chunkSize && source.hasNext()) newChunkPremapped.add(source.getAndMoveNext())
        val newChunk = mapper(newChunkPremapped)
        
        currentChunk = Some(newChunk)
        return newChunk
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        if (currentChunk.isSome()) {
            currentChunk = None
            return
        }
        
        var newChunkSize = 0u
        while (newChunkSize < chunkSize && source.hasNext()) {
            source.moveNext()
            newChunkSize++
        }
    }
}

public fun <E, R> KoneIterator<E>.chunked(size: UInt, mapper: (chunk: KoneList<E>) -> R): KoneIterator<R> = KoneChunkedLambdaIterator(this, size, mapper)

public fun <E> KoneIterable<E>.chunked(size: UInt): KoneList<KoneList<E>> {
    val fullChunks = this@chunked.size / size
    val allChunks = if (this@chunked.size % size != 0u) fullChunks + 1u else fullChunks
    val chunks = KoneArrayFixedCapacityList<KoneList<E>>(allChunks)
    val iterator = iterator()
    repeat(fullChunks) {
        chunks += KoneList.generate(size) { iterator.getAndMoveNext() }
    }
    if (allChunks > fullChunks) {
        val lastChunk = KoneArrayFixedCapacityList<E>(size)
        while (iterator.hasNext()) lastChunk += iterator.getAndMoveNext()
        chunks += lastChunk
    }
    return chunks
}

public fun <E, R> KoneIterable<E>.chunked(size: UInt, mapper: (chunk: KoneList<E>) -> R): KoneList<R> {
    val fullChunks = this@chunked.size / size
    val allChunks = if (this@chunked.size % size != 0u) fullChunks + 1u else fullChunks
    val chunks = KoneArrayFixedCapacityList<R>(allChunks)
    val iterator = iterator()
    repeat(fullChunks) {
        chunks += mapper(KoneList.generate(size) { iterator.getAndMoveNext() })
    }
    if (allChunks > fullChunks) {
        val lastChunk = KoneArrayFixedCapacityList<E>(size)
        while (iterator.hasNext()) lastChunk += iterator.getAndMoveNext()
        chunks += mapper(lastChunk)
    }
    return chunks
}

private class KoneChunkedSequence<Element>(
    private val source: KoneSequence<Element>,
    private val chunkSize: UInt,
) : KoneSequence<KoneList<Element>> {
    override fun iterator(): KoneIterator<KoneList<Element>> = source.iterator().chunked(chunkSize)
}

public fun <E> KoneSequence<E>.chunked(size: UInt): KoneSequence<KoneList<E>> = KoneChunkedSequence(this, size)

private class KoneChunkedLambdaSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val chunkSize: UInt,
    private val mapper: (chunk: KoneList<Element>) -> Result,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().chunked(chunkSize, mapper)
}

public fun <E, R> KoneSequence<E>.chunked(size: UInt, mapper: (chunk: KoneList<E>) -> R): KoneSequence<R> = KoneChunkedLambdaSequence(this, size, mapper)

private class KoneWindowedIterator<Element>(
    private val source: KoneIterator<Element>,
    private val windowSize: UInt,
    private val windowStep: UInt,
) : KoneIterator<KoneList<Element>> {
    init {
        require(windowStep > 0u) { TODO() }
    }
    
    private var currentWindow: KoneList<Element>? = null
    
    init {
        val initialWindow = KoneArrayFixedCapacityList<Element>(windowSize)
        while (initialWindow.size < windowSize && source.hasNext()) initialWindow += source.getAndMoveNext()
        if (initialWindow.size == windowSize) currentWindow = initialWindow
    }
    
    override fun hasNext(): Boolean = currentWindow != null
    override fun getNext(): KoneList<Element> = currentWindow ?: noNextElementInIteratorException()
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        
        val previousWindow = currentWindow!!
        val nextWindow = KoneArrayFixedCapacityList<Element>(windowSize)
        for (i in windowStep ..< windowSize) nextWindow += previousWindow[i]
        while (nextWindow.size < windowSize && source.hasNext()) nextWindow += source.getAndMoveNext()
        
        currentWindow = if (nextWindow.size == windowSize) nextWindow else null
    }
}

public fun <E> KoneIterator<E>.windowed(size: UInt, step: UInt = 1u): KoneIterator<KoneList<E>> = KoneWindowedIterator(this, size, step)

private class KoneWindowedLambdaIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val windowSize: UInt,
    private val windowStep: UInt,
    private val mapper: (window: KoneList<Element>) -> Result,
) : KoneIterator<Result> {
    init {
        require(windowStep > 0u) { TODO() }
    }
    
    private data class State<Element, Result>(val window: KoneList<Element>, val result: Result)
    
    private var currentState: State<Element, Result>? = null
    
    init {
        val initialWindow = KoneArrayFixedCapacityList<Element>(windowSize)
        while (initialWindow.size < windowSize && source.hasNext()) initialWindow += source.getAndMoveNext()
        if (initialWindow.size == windowSize) currentState = State(initialWindow, mapper(initialWindow))
    }
    
    override fun hasNext(): Boolean = currentState != null
    override fun getNext(): Result = (currentState ?: noNextElementInIteratorException()).result
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        
        val previousWindow = currentState!!.window
        val nextWindow = KoneArrayFixedCapacityList<Element>(windowSize)
        for (i in windowStep ..< windowSize) nextWindow += previousWindow[i]
        while (nextWindow.size < windowSize && source.hasNext()) nextWindow += source.getAndMoveNext()
        
        currentState = if (nextWindow.size == windowSize) State(nextWindow, mapper(nextWindow)) else null
    }
}

public fun <E, R> KoneIterator<E>.windowed(size: UInt, step: UInt = 1u, mapper: (window: KoneList<E>) -> R): KoneIterator<R> = KoneWindowedLambdaIterator(this, size, step, mapper)

public fun <E> KoneIterable<E>.windowed(size: UInt, step: UInt = 1u): KoneList<KoneList<E>> {
    if (this.size < size) return KoneList.empty()
    val iterator = iterator()
    var currentWindow = KoneList.generate(size) { iterator.getAndMoveNext() }
    val result = KoneArrayFixedCapacityList<KoneList<E>>((this.size - size) / step + 1u)
    result += currentWindow
    repeat((this.size - size) / step) {
        val nextWindow = KoneArrayFixedCapacityList<E>(size)
        for (i in step ..< size) nextWindow += currentWindow[i]
        repeat(step) { nextWindow += iterator.getAndMoveNext() }
        currentWindow = nextWindow
        result += currentWindow
    }
    return result
}

public fun <E, R> KoneIterable<E>.windowed(size: UInt, step: UInt = 1u, mapper: (window: KoneList<E>) -> R): KoneList<R> {
    if (this.size < size) return KoneList.empty()
    val iterator = iterator()
    var currentWindow = KoneList.generate(size) { iterator.getAndMoveNext() }
    val result = KoneArrayFixedCapacityList<R>((this.size - size) / step + 1u)
    result += mapper(currentWindow)
    repeat((this.size - size) / step) {
        val nextWindow = KoneArrayFixedCapacityList<E>(size)
        for (i in step ..< size) nextWindow += currentWindow[i]
        repeat(step) { nextWindow += iterator.getAndMoveNext() }
        currentWindow = nextWindow
        result += mapper(currentWindow)
    }
    return result
}

private class KoneWindowedSequence<Element>(
    private val source: KoneSequence<Element>,
    private val size: UInt,
    private val step: UInt,
): KoneSequence<KoneList<Element>> {
    override fun iterator(): KoneIterator<KoneList<Element>> = source.iterator().windowed(size, step)
}

public fun <E> KoneSequence<E>.windowed(size: UInt, step: UInt = 1u): KoneSequence<KoneList<E>> = KoneWindowedSequence(this, size, step)

private class KoneWindowedLambdaSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val size: UInt,
    private val step: UInt,
    private val mapper: (window: KoneList<Element>) -> Result,
): KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().windowed(size, step, mapper)
}

public fun <E, R> KoneSequence<E>.windowed(size: UInt, step: UInt = 1u, mapper: (window: KoneList<E>) -> R): KoneSequence<R> = KoneWindowedLambdaSequence(this, size, step, mapper)

// TODO: Think about other flattening operations

private class KoneFlattenIteratorIterator<Element>(
    private val source: KoneIterator<KoneIterator<Element>>,
) : KoneIterator<Element> {
    override fun hasNext(): Boolean {
        while (true) {
            if (!source.hasNext()) return false
            if (source.getNext().hasNext()) return true
            source.moveNext()
        }
    }
    override fun getNext(): Element =
        if (!hasNext()) noNextElementInIteratorException()
        else source.getNext().getNext()
    override fun moveNext() {
        while (true) {
            if (!source.hasNext()) noNextElementInIteratorException()
            if (!source.getNext().hasNext()) source.moveNext()
            else break
        }
        source.getNext().moveNext()
    }
}

public fun <E> KoneIterator<KoneIterator<E>>.flatten(): KoneIterator<E> = KoneFlattenIteratorIterator(this)

public fun <E> KoneIterable<KoneIterable<E>>.flatten(): KoneList<E> {
    val result = KoneArrayGrowableList<E>()
    for (iterable in this) result.addAllFrom(iterable)
    return result
}

private class KoneFlattenSequenceIterator<Element>(
    private val source: KoneIterator<KoneSequence<Element>>,
) : KoneIterator<Element> {
    private var iterator: KoneIterator<Element> = KoneIterator.empty()
    override fun hasNext(): Boolean {
        while (true) {
            if (iterator.hasNext()) return true
            if (!source.hasNext()) return false
            iterator = source.getNext().iterator()
            source.moveNext()
        }
    }
    override fun getNext(): Element =
        if (!hasNext()) noNextElementInIteratorException()
        else iterator.getNext()
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        else iterator.moveNext()
    }
}

private class KoneFlattenSequenceSequence<Element>(
    private val source: KoneSequence<KoneSequence<Element>>,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = KoneFlattenSequenceIterator(source.iterator())
}

public fun <E> KoneSequence<KoneSequence<E>>.flatten(): KoneSequence<E> = KoneFlattenSequenceSequence(this)

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterator<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D {
    while (hasNext()) destination.addAllFrom(transform(getAndMoveNext()))
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterator<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D {
    while (hasNext()) destination.addAllFrom(transform(getAndMoveNext()))
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D =
    iterator().flatMapTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D =
    iterator().flatMapTo(destination, transform)

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneSequence<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D =
    iterator().flatMapTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneSequence<E>.flatMapTo(destination: D, transform: (E) -> KoneIterable<R>): D =
    iterator().flatMapTo(destination, transform)

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterator<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D {
    var currentIndex = 0u
    while (hasNext()) destination.addAllFrom(transform(currentIndex++, getAndMoveNext()))
    return destination
}
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterator<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D {
    var currentIndex = 0u
    while (hasNext()) destination.addAllFrom(transform(currentIndex++, getAndMoveNext()))
    return destination
}

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneIterable<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D =
    iterator().flatMapIndexedTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D =
    iterator().flatMapIndexedTo(destination, transform)

@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableList<in R>> KoneSequence<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D =
    iterator().flatMapIndexedTo(destination, transform)
@IgnorableReturnValue
public inline fun <E, R, D: KoneMutableSet<in R>> KoneSequence<E>.flatMapIndexedTo(destination: D, transform: (index: UInt, E) -> KoneIterable<R>): D =
    iterator().flatMapIndexedTo(destination, transform)

private class KoneFlatMapIteratorIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (Element) -> KoneIterator<Result>,
) : KoneIterator<Result> {
    var nextIterator: KoneIterator<Result> = KoneIterator.empty()
    override fun hasNext(): Boolean {
        while (true) {
            if (nextIterator.hasNext()) return true
            if (!source.hasNext()) return false
            nextIterator = transform(source.getAndMoveNext())
        }
    }
    override fun getNext(): Result =
        if (!hasNext()) noNextElementInIteratorException()
        else nextIterator.getNext()
    override fun moveNext() {
        while (true) {
            if (nextIterator.hasNext()) {
                nextIterator.moveNext()
                break
            }
            if (!source.hasNext()) noNextElementInIteratorException()
            nextIterator = transform(source.getAndMoveNext())
        }
    }
}

public fun <E, R> KoneIterator<E>.flatMap(transform: (E) -> KoneIterator<R>): KoneIterator<R> = KoneFlatMapIteratorIterator(this, transform)

public inline fun <E, R> KoneIterable<E>.flatMap(transform: (E) -> KoneIterable<R>): KoneList<R> = flatMapTo(KoneArrayGrowableList(), transform)

private class KoneFlatMapSequenceIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (Element) -> KoneSequence<Result>,
) : KoneIterator<Result> {
    var nextIterator: KoneIterator<Result> = KoneIterator.empty()
    override fun hasNext(): Boolean {
        while (true) {
            if (nextIterator.hasNext()) return true
            if (!source.hasNext()) return false
            nextIterator = transform(source.getAndMoveNext()).iterator()
        }
    }
    override fun getNext(): Result =
        if (!hasNext()) noNextElementInIteratorException()
        else nextIterator.getNext()
    override fun moveNext() {
        while (true) {
            if (nextIterator.hasNext()) {
                nextIterator.moveNext()
                break
            }
            if (!source.hasNext()) noNextElementInIteratorException()
            nextIterator = transform(source.getAndMoveNext()).iterator()
        }
    }
}

private class KoneFlatMapSequenceSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val transform: (Element) -> KoneSequence<Result>,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = KoneFlatMapSequenceIterator(source.iterator(), transform)
}

public fun <E, R> KoneSequence<E>.flatMap(transform: (E) -> KoneSequence<R>): KoneSequence<R> = KoneFlatMapSequenceSequence(this, transform)

private class KoneFlatMapIndexedIteratorIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (UInt, Element) -> KoneIterator<Result>,
) : KoneIterator<Result> {
    var nextIndex: UInt = 0u
    var nextIterator: KoneIterator<Result> = KoneIterator.empty()
    override fun hasNext(): Boolean {
        while (true) {
            if (nextIterator.hasNext()) return true
            if (!source.hasNext()) return false
            nextIterator = transform(nextIndex++, source.getAndMoveNext())
        }
    }
    override fun getNext(): Result =
        if (!hasNext()) noNextElementInIteratorException()
        else nextIterator.getNext()
    override fun moveNext() {
        while (true) {
            if (nextIterator.hasNext()) {
                nextIterator.moveNext()
                break
            }
            if (!source.hasNext()) noNextElementInIteratorException()
            nextIterator = transform(nextIndex++, source.getAndMoveNext())
        }
    }
}

public fun <E, R> KoneIterator<E>.flatMapIndexed(transform: (index: UInt, E) -> KoneIterator<R>): KoneIterator<R> = KoneFlatMapIndexedIteratorIterator(this, transform)

public inline fun <E, R> KoneIterable<E>.flatMapIndexed(transform: (index: UInt, E) -> KoneIterable<R>): KoneList<R> = flatMapIndexedTo(KoneArrayGrowableList(), transform)

private class KoneFlatMapIndexedSequenceIterator<Element, Result>(
    private val source: KoneIterator<Element>,
    private val transform: (UInt, Element) -> KoneSequence<Result>,
) : KoneIterator<Result> {
    var nextIndex: UInt = 0u
    var nextIterator: KoneIterator<Result> = KoneIterator.empty()
    override fun hasNext(): Boolean {
        while (true) {
            if (nextIterator.hasNext()) return true
            if (!source.hasNext()) return false
            nextIterator = transform(nextIndex++, source.getAndMoveNext()).iterator()
        }
    }
    override fun getNext(): Result =
        if (!hasNext()) noNextElementInIteratorException()
        else nextIterator.getNext()
    override fun moveNext() {
        while (true) {
            if (nextIterator.hasNext()) {
                nextIterator.moveNext()
                break
            }
            if (!source.hasNext()) noNextElementInIteratorException()
            nextIterator = transform(nextIndex++, source.getAndMoveNext()).iterator()
        }
    }
}

private class KoneFlatMapIndexedSequenceSequence<Element, Result>(
    private val source: KoneSequence<Element>,
    private val transform: (UInt, Element) -> KoneSequence<Result>,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = KoneFlatMapIndexedSequenceIterator(source.iterator(), transform)
}

public fun <E, R> KoneSequence<E>.flatMapIndexed(transform: (index: UInt, E) -> KoneSequence<R>): KoneSequence<R> = KoneFlatMapIndexedSequenceSequence(this, transform)

@IgnorableReturnValue
public inline fun <E, D: KoneMutableList<in E>> KoneIterator<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    while (hasNext()) {
        val item = getNext()
        if (predicate(item)) destination.add(item)
    }
    return destination
}
@IgnorableReturnValue
public inline fun <E, D: KoneMutableSet<in E>> KoneIterator<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    while (hasNext()) {
        val item = getNext()
        if (predicate(item)) destination.add(item)
    }
    return destination
}

@IgnorableReturnValue
public inline fun <E, D: KoneMutableList<in E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
@IgnorableReturnValue
public inline fun <E, D: KoneMutableSet<in E>> KoneIterable<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

@IgnorableReturnValue
public inline fun <E, D: KoneMutableList<in E>> KoneSequence<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}
@IgnorableReturnValue
public inline fun <E, D: KoneMutableSet<in E>> KoneSequence<E>.filterTo(destination: D, predicate: (E) -> Boolean): D {
    for (item in this) if (predicate(item)) destination.add(item)
    return destination
}

private class KoneFilterIterator<Element>(
    private val source: KoneIterator<Element>,
    private val predicate: (Element) -> Boolean,
) : KoneIterator<Element> {
    override fun hasNext(): Boolean {
        while (true) {
            if (!source.hasNext()) return false
            if (predicate(source.getNext())) return true
            source.moveNext()
        }
    }
    override fun getNext(): Element {
        if (!hasNext()) noNextElementInIteratorException()
        return source.getNext()
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        source.moveNext()
    }
}

public fun <E> KoneIterator<E>.filter(predicate: (E) -> Boolean): KoneIterator<E> = KoneFilterIterator(this, predicate)

public inline fun <E> KoneIterable<E>.filter(predicate: (E) -> Boolean): KoneList<E> = filterTo(KoneArrayFixedCapacityList(size), predicate).toOptimizedList()

private class KoneFilterSequence<Element>(
    private val source: KoneSequence<Element>,
    private val predicate: (Element) -> Boolean,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = source.iterator().filter(predicate)
}

public fun <E> KoneSequence<E>.filter(predicate: (E) -> Boolean): KoneSequence<E> = KoneFilterSequence(this, predicate)

public inline fun <E, R> KoneIterator<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E, R> KoneIterable<E>.fold(initial: R, operation: (acc: R, E) -> R): R =
    iterator().fold(initial, operation)

public inline fun <E, R> KoneSequence<E>.fold(initial: R, operation: (acc: R, E) -> R): R =
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

public inline fun <E, R> KoneSequence<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R =
    iterator().foldIndexed(initial, operation)

public inline fun <E, R> KoneList<E>.foldRightIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R {
    var accumulator = initial
    for (index in size-1u downTo 0u) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

@Suppress("UNCHECKED_CAST")
private class KoneRunningFoldIterator<Element, Result>(
    accumulator: Result,
    private val source: KoneIterator<Element>,
    private val operation: (Result, Element) -> Result,
) : KoneIterator<Result> {
    private var hasNextFlag = true
    private var accumulator: Result? = accumulator
    override fun hasNext(): Boolean = hasNextFlag
    override fun getNext(): Result {
        if (!hasNext()) noNextElementInIteratorException()
        return accumulator as Result
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        if (!source.hasNext()) {
            hasNextFlag = false
            accumulator = null
        }
        accumulator = operation(accumulator as Result, source.getAndMoveNext())
    }
}

public fun <E, R> KoneIterator<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneIterator<R> = KoneRunningFoldIterator(initial, this, operation)

public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneList<R> {
    val result = KoneSettableList.generate(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

private class KoneRunningFoldSequence<Element, Result>(
    private val initial: Result,
    private val source: KoneSequence<Element>,
    private val operation: (Result, Element) -> Result,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().runningFold(initial, operation)
}

public fun <E, R> KoneSequence<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneSequence<R> = KoneRunningFoldSequence(initial, this, operation)

@Suppress("UNCHECKED_CAST")
private class KoneRunningFoldIndexedIterator<Element, Result>(
    accumulator: Result,
    private val source: KoneIterator<Element>,
    private val operation: (UInt, Result, Element) -> Result,
) : KoneIterator<Result> {
    private var hasNextFlag = true
    private var index = 0u
    private var accumulator: Result? = accumulator
    override fun hasNext(): Boolean = hasNextFlag
    override fun getNext(): Result {
        if (!hasNext()) noNextElementInIteratorException()
        return accumulator as Result
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        if (!source.hasNext()) {
            hasNextFlag = false
            accumulator = null
        }
        accumulator = operation(index++, accumulator as Result, source.getAndMoveNext())
    }
}

public fun <E, R> KoneIterator<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneIterator<R> = KoneRunningFoldIndexedIterator(initial, this, operation)

public inline fun <E, R> KoneIterable<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneList<R> {
    val result = KoneSettableList.generate(size + 1u) { initial }
    var accumulator = initial
    var index = 0u
    for (element in this) {
        accumulator = operation(index, accumulator, element)
        result[index + 1u] = accumulator
        index++
    }
    return result
}

private class KoneRunningFoldIndexedSequence<Element, Result>(
    private val initial: Result,
    private val source: KoneSequence<Element>,
    private val operation: (UInt, Result, Element) -> Result,
) : KoneSequence<Result> {
    override fun iterator(): KoneIterator<Result> = source.iterator().runningFoldIndexed(initial, operation)
}

public fun <E, R> KoneSequence<E>.runningFoldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): KoneSequence<R> = KoneRunningFoldIndexedSequence(initial, this, operation)

// TODO: Add `runningFoldRight` and `runningFoldRightIndexed`

public inline fun <E: R, R> KoneIterator<E>.reduce(operation: (acc: R, E) -> R): R {
    if (!this.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduce(operation: (acc: R, E) -> R): R = iterator().reduce(operation)

public inline fun <E: R, R> KoneSequence<E>.reduce(operation: (acc: R, E) -> R): R = iterator().reduce(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceOrNull(operation: (acc: R, E) -> R): R? {
    if (!this.hasNext()) return null
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceOrNull(operation: (acc: R, E) -> R): R? = iterator().reduceOrNull(operation)

public inline fun <E: R, R> KoneSequence<E>.reduceOrNull(operation: (acc: R, E) -> R): R? = iterator().reduceOrNull(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceMaybe(operation: (acc: R, E) -> R): Maybe<R> {
    if (!this.hasNext()) return None
    var accumulator: R = this.getAndMoveNext()
    for (element in this) accumulator = operation(accumulator, element)
    return Some(accumulator)
}

public inline fun <E: R, R> KoneIterable<E>.reduceMaybe(operation: (acc: R, E) -> R): Maybe<R> = iterator().reduceMaybe(operation)

public inline fun <E: R, R> KoneSequence<E>.reduceMaybe(operation: (acc: R, E) -> R): Maybe<R> = iterator().reduceMaybe(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R {
    if (!this.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R = iterator().reduceIndexed(operation)

public inline fun <E: R, R> KoneSequence<E>.reduceIndexed(operation: (index: UInt, acc: R, E) -> R): R = iterator().reduceIndexed(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? {
    if (!this.hasNext()) return null
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return accumulator
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? = iterator().reduceIndexedOrNull(operation)

public inline fun <E: R, R> KoneSequence<E>.reduceIndexedOrNull(operation: (index: UInt, acc: R, E) -> R): R? = iterator().reduceIndexedOrNull(operation)

public inline fun <E: R, R> KoneIterator<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Maybe<R> {
    if (!this.hasNext()) return None
    var accumulator: R = this.getAndMoveNext()
    var index = 1u
    for (element in this) accumulator = operation(index++, accumulator, element)
    return Some(accumulator)
}

public inline fun <E: R, R> KoneIterable<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Maybe<R> = iterator().reduceIndexedMaybe(operation)

public inline fun <E: R, R> KoneSequence<E>.reduceIndexedMaybe(operation: (index: UInt, acc: R, E) -> R): Maybe<R> = iterator().reduceIndexedMaybe(operation)

// TODO: Add `reduce`-like extensions. Like `reduceRight`, `runningReduce`, and `runningReduceRight`.

// TODO: Add summing and multiplying extensions for primitives. Maybe.

context(monoid: Monoid<E>)
public fun <E> KoneIterator<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<E>)
public fun <E> KoneSequence<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<N>)
public fun <E, N> KoneIterator<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public fun <E, N> KoneIterable<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public fun <E, N> KoneSequence<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneIterator<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneSequence<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(ring: Semiring<E>)
public fun <E> KoneIterator<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<E>)
public fun <E> KoneIterable<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<E>)
public fun <E> KoneSequence<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<N>)
public fun <E, N> KoneIterator<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public fun <E, N> KoneIterable<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public fun <E, N> KoneSequence<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneIterator<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneIterable<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneSequence<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

@IgnorableReturnValue
public inline fun <E, K, D : KoneMutableMap<in K, KoneMutableList<E>>> KoneIterable<E>.groupByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneArrayGrowableList() }
        accumulator.add(element)
    }
    return destination
}

@IgnorableReturnValue
public inline fun <E, K, V, D : KoneMutableMap<in K, KoneMutableList<V>>> KoneIterable<E>.groupByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val accumulator = destination.getOrSet(key) { KoneArrayGrowableList() }
        accumulator.add(valueTransform(element))
    }
    return destination
}

public inline fun <E, K> KoneIterable<E>.groupBy(
    keyEquality: Equality<K> = Equality.defaultFor(),
    keyHashing: Hashing<K>? = null,
    keyOrder: Order<K>? = null,
    keySelector: (E) -> K
): KoneMap<K, KoneList<E>> =
    groupByTo(
        destination = KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector
    )

context(_: KoneContextRegistry)
public inline fun <E, K> KoneIterable<E>.groupContextualBy(
    keyType: SuppliedType,
    keySelector: (E) -> K
): KoneMap<K, KoneList<E>> =
    groupByTo(
        destination = KoneMutableMap.contextualOf(
            keyType = keyType,
        ),
        keySelector = keySelector
    )

public inline fun <E, K, V> KoneIterable<E>.groupBy(
    keyEquality: Equality<K> = Equality.defaultFor(),
    keyHashing: Hashing<K>? = null,
    keyOrder: Order<K>? = null,
    keySelector: (E) -> K,
    valueTransform: (E) -> V,
): KoneMap<K, KoneList<V>> =
    groupByTo(
        destination = KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

context(_: KoneContextRegistry)
public inline fun <E, K, V> KoneIterable<E>.groupContextualBy(
    keyType: SuppliedType,
    keySelector: (E) -> K,
    valueTransform: (E) -> V,
): KoneMap<K, KoneList<V>> =
    groupByTo(
        destination = KoneMutableMap.contextualOf(
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
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.collectionOperations


/**
 * Returns the first element in this iterable for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.firstThat(predicate: (index: Int, T) -> Boolean): T {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

/**
 * Returns the first element in this iterable for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.firstThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the zero-based index of the first element in this iterable for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element in iteration order.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this list for which [predicate] returns `true`.
 *
 * Unlike the [Iterable] overload, this implementation iterates without allocating an indexed view.
 *
 * @param T The element type of this list.
 * @receiver This list to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> List<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the last element in this iterable for which [predicate] returns `true`.
 *
 * The entire iterable is scanned; the [predicate] receives the zero-based index and each element.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for elements considered as candidates.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.lastThat(predicate: (index: Int, T) -> Boolean): T {
    var last: T? = null
    var found = false
    forEachIndexed { index, t ->
        if (predicate(index, t)) {
            last = t
            found = true
        }
    }
    if (!found) throw NoSuchElementException("Collection contains no element matching the predicate.")
    @Suppress("UNCHECKED_CAST")
    return last as T
}

/**
 * Returns the last element in this list for which [predicate] returns `true`.
 *
 * Unlike the [Iterable] overload, this implementation walks the list from end to start.
 *
 * @param T The element type of this list.
 * @receiver This list to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun <T> List<T>.lastThat(predicate: (index: Int, T) -> Boolean): T {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(iterator.nextIndex(), element)) return element
    }
    throw NoSuchElementException("List contains no element matching the predicate.")
}

/**
 * Returns the last element in this iterable for which [predicate] returns `true`, or `null` if none match.
 *
 * The entire iterable is scanned; the [predicate] receives the zero-based index and each element.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for elements considered as candidates.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    var last: T? = null
    forEachIndexed { index, t -> if (predicate(index, t)) last = t }
    return last
}

/**
 * Returns the last element in this list for which [predicate] returns `true`, or `null` if none match.
 *
 * Unlike the [Iterable] overload, this implementation walks the list from end to start.
 *
 * @param T The element type of this list.
 * @receiver This list to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun <T> List<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(iterator.nextIndex(), element)) return element
    }
    return null
}

/**
 * Returns the zero-based index of the last element in this iterable for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element in iteration order.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to search in iteration order.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> Iterable<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    var lastIndex = -1
    this.forEachIndexed { index, t -> if (predicate(index, t)) lastIndex = index }
    return lastIndex
}

/**
 * Returns the zero-based index of the last element in this list for which [predicate] returns `true`.
 *
 * Unlike the [Iterable] overload, this implementation walks the list from end to start.
 *
 * @param T The element type of this list.
 * @receiver This list to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> List<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious())
        if (predicate(iterator.previousIndex(), iterator.previous()))
            return iterator.nextIndex()
    return -1
}

/**
 * Counts elements in this iterable that satisfy [predicate] within the index range starting at [from].
 *
 * Iteration stops when the current index reaches [to] (the element at index [to] is not counted).
 * If [from] is greater than or equal to [to], returns `0`. Requires [to] to be at least `-1`.
 *
 * @param T The element type of this iterable.
 * @receiver This iterable to count over in iteration order.
 * @param from The inclusive lower bound on element indices to consider. Defaults to `0`.
 * @param to The exclusive upper bound on element indices; iteration stops when this index is reached. Defaults to `-1`.
 * @param predicate Called with each element whose index is at least [from] and strictly less than [to]; should return `true` for elements to count.
 * @return The number of elements in the considered index range that satisfy [predicate].
 */
public inline fun <T> Iterable<T>.count(from: Int = 0, to: Int = -1, predicate: (T) -> Boolean): Int {
    require(to >= -1) { /* TODO */ }
    if (from >= to) return 0

    var count = 0
    forEachIndexed { index, t ->
        when {
            index < from -> return@forEachIndexed
            to == -1 || index == to -> return count
            predicate(t) -> count++
        }
    }
    return count
}

/**
 * Counts elements in the sublist `[from, to)` that satisfy [predicate].
 *
 * @param T The element type of this list.
 * @receiver This list to count over.
 * @param from The inclusive start index of the range to count in. Defaults to `0`.
 * @param to The exclusive end index of the range to count in. Defaults to [size].
 * @param predicate Called with each element in the range; should return `true` for elements to count.
 * @return The number of elements in `[from, to)` that satisfy [predicate].
 */
public inline fun <T> List<T>.count(from: Int = 0, to: Int = size, predicate: (T) -> Boolean): Int =
    subList(from, to).count(predicate)

/**
 * Returns the element at index `5` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `5`.
 * @throws IndexOutOfBoundsException if this list has fewer than six elements.
 */
public operator fun <T> List<T>.component6(): T = get(5)

/**
 * Returns the element at index `6` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `6`.
 * @throws IndexOutOfBoundsException if this list has fewer than seven elements.
 */
public operator fun <T> List<T>.component7(): T = get(6)

/**
 * Returns the element at index `7` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `7`.
 * @throws IndexOutOfBoundsException if this list has fewer than eight elements.
 */
public operator fun <T> List<T>.component8(): T = get(7)

/**
 * Returns the element at index `8` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `8`.
 * @throws IndexOutOfBoundsException if this list has fewer than nine elements.
 */
public operator fun <T> List<T>.component9(): T = get(8)

/**
 * Returns the element at index `9` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `9`.
 * @throws IndexOutOfBoundsException if this list has fewer than ten elements.
 */
public operator fun <T> List<T>.component10(): T = get(9)

/**
 * Returns the element at index `10` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `10`.
 * @throws IndexOutOfBoundsException if this list has fewer than eleven elements.
 */
public operator fun <T> List<T>.component11(): T = get(10)

/**
 * Returns the element at index `11` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `11`.
 * @throws IndexOutOfBoundsException if this list has fewer than twelve elements.
 */
public operator fun <T> List<T>.component12(): T = get(11)

/**
 * Returns the element at index `12` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `12`.
 * @throws IndexOutOfBoundsException if this list has fewer than thirteen elements.
 */
public operator fun <T> List<T>.component13(): T = get(12)

/**
 * Returns the element at index `13` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `13`.
 * @throws IndexOutOfBoundsException if this list has fewer than fourteen elements.
 */
public operator fun <T> List<T>.component14(): T = get(13)

/**
 * Returns the element at index `14` for destructuring declarations.
 *
 * @param T The element type of this list.
 * @receiver This list to destructure.
 * @return The element at index `14`.
 * @throws IndexOutOfBoundsException if this list has fewer than fifteen elements.
 */
public operator fun <T> List<T>.component15(): T = get(14)
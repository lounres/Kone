/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.collectionOperations


/**
 * Returns the first element in this object array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.firstThat(predicate: (index: Int, T) -> Boolean): T {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this byte array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This byte array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun ByteArray.firstThat(predicate: (index: Int, Byte) -> Boolean): Byte {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this short array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This short array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun ShortArray.firstThat(predicate: (index: Int, Short) -> Boolean): Short {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this int array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This int array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun IntArray.firstThat(predicate: (index: Int, Int) -> Boolean): Int {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this long array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This long array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun LongArray.firstThat(predicate: (index: Int, Long) -> Boolean): Long {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this float array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This float array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun FloatArray.firstThat(predicate: (index: Int, Float) -> Boolean): Float {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this double array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This double array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun DoubleArray.firstThat(predicate: (index: Int, Double) -> Boolean): Double {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this boolean array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This boolean array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun BooleanArray.firstThat(predicate: (index: Int, Boolean) -> Boolean): Boolean {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this char array for which [predicate] returns `true`.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This char array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun CharArray.firstThat(predicate: (index: Int, Char) -> Boolean): Char {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the first element in this object array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.firstThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this byte array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This byte array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun ByteArray.firstThatOrNull(predicate: (index: Int, Byte) -> Boolean): Byte? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this short array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This short array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun ShortArray.firstThatOrNull(predicate: (index: Int, Short) -> Boolean): Short? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this int array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This int array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun IntArray.firstThatOrNull(predicate: (index: Int, Int) -> Boolean): Int? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this long array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This long array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun LongArray.firstThatOrNull(predicate: (index: Int, Long) -> Boolean): Long? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this float array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This float array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun FloatArray.firstThatOrNull(predicate: (index: Int, Float) -> Boolean): Float? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this double array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This double array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun DoubleArray.firstThatOrNull(predicate: (index: Int, Double) -> Boolean): Double? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this boolean array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This boolean array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun BooleanArray.firstThatOrNull(predicate: (index: Int, Boolean) -> Boolean): Boolean? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the first element in this char array for which [predicate] returns `true`, or `null` if none match.
 *
 * The [predicate] receives the zero-based index of each element together with the element itself.
 *
 * @receiver This char array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The first matching element, or `null` if no element satisfies [predicate].
 */
public inline fun CharArray.firstThatOrNull(predicate: (index: Int, Char) -> Boolean): Char? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

/**
 * Returns the zero-based index of the first element in this object array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this byte array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This byte array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun ByteArray.firstIndexThat(predicate: (index: Int, Byte) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this short array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This short array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun ShortArray.firstIndexThat(predicate: (index: Int, Short) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this int array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This int array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun IntArray.firstIndexThat(predicate: (index: Int, Int) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this long array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This long array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun LongArray.firstIndexThat(predicate: (index: Int, Long) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this float array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This float array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun FloatArray.firstIndexThat(predicate: (index: Int, Float) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this double array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This double array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun DoubleArray.firstIndexThat(predicate: (index: Int, Double) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this boolean array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This boolean array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun BooleanArray.firstIndexThat(predicate: (index: Int, Boolean) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the zero-based index of the first element in this char array for which [predicate] returns `true`.
 *
 * The [predicate] receives the index and each element from start to end.
 *
 * @receiver This char array to search from start to end.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the first matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun CharArray.firstIndexThat(predicate: (index: Int, Char) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

/**
 * Returns the last element in this object array for which [predicate] returns `true`.
 *
 * The object array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.lastThat(predicate: (index: Int, T) -> Boolean): T {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this byte array for which [predicate] returns `true`.
 *
 * The byte array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This byte array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun ByteArray.lastThat(predicate: (index: Int, Byte) -> Boolean): Byte {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this short array for which [predicate] returns `true`.
 *
 * The short array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This short array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun ShortArray.lastThat(predicate: (index: Int, Short) -> Boolean): Short {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this int array for which [predicate] returns `true`.
 *
 * The int array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This int array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun IntArray.lastThat(predicate: (index: Int, Int) -> Boolean): Int {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this long array for which [predicate] returns `true`.
 *
 * The long array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This long array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun LongArray.lastThat(predicate: (index: Int, Long) -> Boolean): Long {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this float array for which [predicate] returns `true`.
 *
 * The float array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This float array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun FloatArray.lastThat(predicate: (index: Int, Float) -> Boolean): Float {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this double array for which [predicate] returns `true`.
 *
 * The double array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This double array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun DoubleArray.lastThat(predicate: (index: Int, Double) -> Boolean): Double {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this boolean array for which [predicate] returns `true`.
 *
 * The boolean array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This boolean array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun BooleanArray.lastThat(predicate: (index: Int, Boolean) -> Boolean): Boolean {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this char array for which [predicate] returns `true`.
 *
 * The char array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This char array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element.
 * @throws NoSuchElementException if no element satisfies [predicate].
 */
public inline fun CharArray.lastThat(predicate: (index: Int, Char) -> Boolean): Char {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

/**
 * Returns the last element in this object array for which [predicate] returns `true`, or `null` if none match.
 *
 * The object array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this byte array for which [predicate] returns `true`, or `null` if none match.
 *
 * The byte array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This byte array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun ByteArray.lastThatOrNull(predicate: (index: Int, Byte) -> Boolean): Byte? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this short array for which [predicate] returns `true`, or `null` if none match.
 *
 * The short array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This short array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun ShortArray.lastThatOrNull(predicate: (index: Int, Short) -> Boolean): Short? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this int array for which [predicate] returns `true`, or `null` if none match.
 *
 * The int array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This int array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun IntArray.lastThatOrNull(predicate: (index: Int, Int) -> Boolean): Int? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this long array for which [predicate] returns `true`, or `null` if none match.
 *
 * The long array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This long array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun LongArray.lastThatOrNull(predicate: (index: Int, Long) -> Boolean): Long? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this float array for which [predicate] returns `true`, or `null` if none match.
 *
 * The float array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This float array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun FloatArray.lastThatOrNull(predicate: (index: Int, Float) -> Boolean): Float? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this double array for which [predicate] returns `true`, or `null` if none match.
 *
 * The double array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This double array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun DoubleArray.lastThatOrNull(predicate: (index: Int, Double) -> Boolean): Double? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this boolean array for which [predicate] returns `true`, or `null` if none match.
 *
 * The boolean array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This boolean array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun BooleanArray.lastThatOrNull(predicate: (index: Int, Boolean) -> Boolean): Boolean? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the last element in this char array for which [predicate] returns `true`, or `null` if none match.
 *
 * The char array is scanned from end to start; the [predicate] receives the zero-based index and each element.
 *
 * @receiver This char array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the element to return.
 * @return The last matching element, or `null` if no element satisfies [predicate].
 */
public inline fun CharArray.lastThatOrNull(predicate: (index: Int, Char) -> Boolean): Char? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

/**
 * Returns the zero-based index of the last element in this object array for which [predicate] returns `true`.
 *
 * The object array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @param T The element type of this object array.
 * @receiver This object array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun <T> Array<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this byte array for which [predicate] returns `true`.
 *
 * The byte array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This byte array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun ByteArray.lastIndexThat(predicate: (index: Int, Byte) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this short array for which [predicate] returns `true`.
 *
 * The short array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This short array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun ShortArray.lastIndexThat(predicate: (index: Int, Short) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this int array for which [predicate] returns `true`.
 *
 * The int array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This int array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun IntArray.lastIndexThat(predicate: (index: Int, Int) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this long array for which [predicate] returns `true`.
 *
 * The long array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This long array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun LongArray.lastIndexThat(predicate: (index: Int, Long) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this float array for which [predicate] returns `true`.
 *
 * The float array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This float array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun FloatArray.lastIndexThat(predicate: (index: Int, Float) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this double array for which [predicate] returns `true`.
 *
 * The double array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This double array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun DoubleArray.lastIndexThat(predicate: (index: Int, Double) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this boolean array for which [predicate] returns `true`.
 *
 * The boolean array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This boolean array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun BooleanArray.lastIndexThat(predicate: (index: Int, Boolean) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

/**
 * Returns the zero-based index of the last element in this char array for which [predicate] returns `true`.
 *
 * The char array is scanned from end to start; the [predicate] receives the index and each element.
 *
 * @receiver This char array to search from end to start.
 * @param predicate Called with the index and each element; should return `true` for the index to return.
 * @return The index of the last matching element, or `-1` if no element satisfies [predicate].
 */
public inline fun CharArray.lastIndexThat(predicate: (index: Int, Char) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

//================================================================================================

/**
 * Counts elements in this int array that satisfy [predicate] within the index range `[from, to]`.
 *
 * Indices are clamped to `[0, size]`. The range is inclusive on both ends.
 *
 * @receiver This int array to count over.
 * @param from The inclusive lower bound on indices to consider. Defaults to `0`.
 * @param to The inclusive upper bound on indices to consider. Defaults to [size].
 * @param predicate Called with each element in the range; should return `true` for elements to count.
 * @return The number of elements in the range that satisfy [predicate].
 */
public inline fun IntArray.count(from: Int = 0, to: Int = size, predicate: (Int) -> Boolean): Int {
    var count = 0
    for (i in maxOf(0, from) .. minOf(size, to)) if (predicate(this[i])) count++
    return count
}

/**
 * Counts elements in this boolean array that satisfy [predicate] within the index range `[from, to]`.
 *
 * Indices are clamped to `[0, size]`. The range is inclusive on both ends.
 *
 * @receiver This boolean array to count over.
 * @param from The inclusive lower bound on indices to consider. Defaults to `0`.
 * @param to The inclusive upper bound on indices to consider. Defaults to [size].
 * @param predicate Called with each element in the range; should return `true` for elements to count.
 * @return The number of elements in the range that satisfy [predicate].
 */
public inline fun BooleanArray.count(from: Int = 0, to: Int = size, predicate: (Boolean) -> Boolean): Int {
    var count = 0
    for (i in maxOf(0, from) .. minOf(size, to)) if (predicate(this[i])) count++
    return count
}
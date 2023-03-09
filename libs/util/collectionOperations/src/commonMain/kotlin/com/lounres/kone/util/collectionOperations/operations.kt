/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.util.collectionOperations


public inline fun <T> Iterable<T>.firstThat(predicate: (index: Int, T) -> Boolean): T {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <T> Iterable<T>.firstThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun <T> Iterable<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun <T> List<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    var index = 0
    for (item in this) {
        if (predicate(index, item)) return index
        index++
    }
    return -1
}

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

public inline fun <T> List<T>.lastThat(predicate: (index: Int, T) -> Boolean): T {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(iterator.nextIndex(), element)) return element
    }
    throw NoSuchElementException("List contains no element matching the predicate.")
}

public inline fun <T> Iterable<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    var last: T? = null
    forEachIndexed { index, t -> if (predicate(index, t)) last = t }
    return last
}

public inline fun <T> List<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(iterator.nextIndex(), element)) return element
    }
    return null
}

public inline fun <T> Iterable<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    var lastIndex = -1
    this.forEachIndexed { index, t -> if (predicate(index, t)) lastIndex = index }
    return lastIndex
}

public inline fun <T> List<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious())
        if (predicate(iterator.previousIndex(), iterator.previous()))
            return iterator.nextIndex()
    return -1
}

public inline fun <T> Iterable<T>.count(from: Int = 0, to: Int = -1, predicate: (T) -> Boolean): Int {
    require(to >= -1) { /* TODO */ }
    if (from > to) return 0

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

public inline fun <T> List<T>.count(from: Int = 0, to: Int = size, predicate: (T) -> Boolean): Int =
    subList(from, to).count(predicate)

public operator fun <T> List<T>.component6(): T = get(5)
public operator fun <T> List<T>.component7(): T = get(6)
public operator fun <T> List<T>.component8(): T = get(7)
public operator fun <T> List<T>.component9(): T = get(8)
public operator fun <T> List<T>.component10(): T = get(9)
public operator fun <T> List<T>.component11(): T = get(10)
public operator fun <T> List<T>.component12(): T = get(11)
public operator fun <T> List<T>.component13(): T = get(12)
public operator fun <T> List<T>.component14(): T = get(13)
public operator fun <T> List<T>.component15(): T = get(14)
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
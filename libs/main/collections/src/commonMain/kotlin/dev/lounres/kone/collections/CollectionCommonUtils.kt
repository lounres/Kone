/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

//import dev.lounres.kone.collections.implementations.KoneGrowableArrayList



// region KoneIterable
public inline fun <E> KoneIterable<E>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}

public inline fun <E> KoneIterable<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
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

public inline fun <E, R> KoneIterable<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

//public inline fun <E, R> KoneIterable<E>.runningFold(initial: R, operation: (acc: R, E) -> R): KoneIterableList<R> {
//    val result = KoneGrowableArrayList<R>(/*estimatedSize + 1u*/).apply { add(initial) }
//    var accumulator = initial
//    for (element in this) {
//        accumulator = operation(accumulator, element)
//        result.add(accumulator)
//    }
//    return result
//}
// endregion

// region KoneList
public inline fun <E> KoneList<E>.forEach(block: (value: E) -> Unit) {
    for (index in 0u ..< size) block(get(index))
}

public inline fun <E> KoneList<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    for (index in 0u ..< size) block(index, get(index))
}

public inline fun <E> KoneList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return true
    return false
}

public inline fun <E> KoneList<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return true
    return false
}

public inline fun <E> KoneList<E>.all(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(get(index))) return false
    return true
}

public inline fun <E> KoneList<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (!block(index, get(index))) return false
    return true
}

public inline fun <E> KoneList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(get(index))) return false
    return true
}

public inline fun <E> KoneList<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    for (index in 0u ..< size) if (block(index, get(index))) return false
    return true
}

public inline fun <E, R> KoneList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (index in 0u ..< size) accumulator = operation(accumulator, get(index))
    return accumulator
}
// endregion

// region KoneIterableList
public inline fun <E> KoneIterableList<E>.forEach(block: (value: E) -> Unit) {
    for (element in this) block(element)
}

public inline fun <E> KoneIterableList<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    var index = 0u
    for (element in this) block(index++, element)
}

public inline fun <E> KoneIterableList<E>.any(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return true
    return false
}

public inline fun <E> KoneIterableList<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}

public inline fun <E> KoneIterableList<E>.all(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (!block(element)) return false
    return true
}

public inline fun <E> KoneIterableList<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (!block(currentIndex++, element)) return false
    return true
}

public inline fun <E> KoneIterableList<E>.none(block: (value: E) -> Boolean): Boolean {
    for (element in this) if (block(element)) return false
    return true
}

public inline fun <E> KoneIterableList<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return false
    return true
}

public inline fun <E, R> KoneIterableList<E>.fold(initial: R, operation: (acc: R, E) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}
// endregion
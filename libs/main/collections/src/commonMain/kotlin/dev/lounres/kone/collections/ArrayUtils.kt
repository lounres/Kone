/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public inline fun KoneUIntArray.anyIndexed(block: (index: UInt, value: UInt) -> Boolean): Boolean {
    var currentIndex = 0u
    for (element in this) if (block(currentIndex++, element)) return true
    return false
}

public inline fun KoneUIntArray.runningFold(initial: UInt, operation: (acc: UInt, UInt) -> UInt): KoneUIntArray {
    val result = UIntArray(size.toInt() + 1) { initial }
    var accumulator = initial
    for (index in 0u ..< size) {
        accumulator = operation(accumulator, get(index))
        result[index.toInt() + 1] = accumulator
    }
    return KoneUIntArray(result)
}

public inline fun KoneUIntArray.first(predicate: (UInt) -> Boolean): UInt {
    for (element in this) if (predicate(element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public fun KoneUIntArray.min(): UInt {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var min = iterator.next()
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (min > e) min = e
    }
    return min
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public inline fun KoneUIntArray.runningFold(initial: UInt, operation: (acc: UInt, UInt) -> UInt): KoneUIntArray {
    val result = IntArray(size.toInt() + 1) { initial.toInt() }
    var accumulator = initial
    for (index in 0u ..< size) {
        accumulator = operation(accumulator, get(index))
        result[index.toInt()] = accumulator.toInt()
    }
    return KoneUIntArray(result)
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.internal

import dev.lounres.kone.scope
import kotlin.jvm.JvmInline


@JvmInline
public value class ULongX(public val first: ULong, public val second: ULong)

public inline fun add(left: ULong, right: ULong): ULongX {
    val first = left + right
    return ULongX(
        first = first,
        second = if (first < left) 1uL else 0uL
    )
}

public inline fun add(left: ULong, middle: ULong, right: ULong): ULongX {
    var first = left + middle
    var second = 0uL
    if (first < middle) second++
    first += right
    if (first < right) second++
    return ULongX(
        first = first,
        second = second
    )
}

public inline fun multiply(left: ULong, right: ULong): ULongX {
    val left1 = left and 0b0000000000000000000000000000000011111111111111111111111111111111uL
    val left2 = left shr 32
    val right1 = right and 0b0000000000000000000000000000000011111111111111111111111111111111uL
    val right2 = right shr 32
    
    var resultFirst = left1 * right1
    var resultSecond = left2 * right2
    
    val left1right2 = left1 * right2
    val left1right2part1 = left1right2 shl 32
    val left1right2part2 = left1right2 shr 32
    
    val left2right1 = left2 * right1
    val left2right1part1 = left2right1 shl 32
    val left2right1part2 = left2right1 shr 32
    
    scope {
        val result = add(resultFirst, left1right2part1, left2right1part1)
        resultFirst = result.first
        resultSecond += result.second
    }
    
    resultSecond += left1right2part2
    resultSecond += left2right1part2
    
    return ULongX(resultFirst, resultSecond)
}
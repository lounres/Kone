/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algorithms.relations


public enum class ComparisonResult2 {
    LeftIsGreaterThanRight, LeftIsLessThanRight, Equal;
}

public fun ComparisonResult2.asKotlinComparisonResult2(): Int =
    when (this) {
        ComparisonResult2.LeftIsGreaterThanRight -> 1
        ComparisonResult2.LeftIsLessThanRight -> -1
        ComparisonResult2.Equal -> 0
    }

public fun Int.asComparisonResult2(): ComparisonResult2 =
    when {
        this > 0 -> ComparisonResult2.LeftIsGreaterThanRight
        this < 0 -> ComparisonResult2.LeftIsLessThanRight
        else -> ComparisonResult2.Equal
    }
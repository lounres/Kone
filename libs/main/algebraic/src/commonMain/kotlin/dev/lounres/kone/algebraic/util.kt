/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.ComparisonResult
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.geq
import dev.lounres.kone.comparison.gt
import dev.lounres.kone.comparison.leq
import dev.lounres.kone.comparison.lt


/**
 * Checks if [this] number is positive in the ordered ring.
 */
context(A)
public fun <N, A> N.isPositive(): Boolean where A: Ring<N>, A: Order<N> = this gt zero
/**
 * Checks if [this] number is non-positive in the ordered ring.
 */
context(A)
public fun <N, A> N.isNonPositive(): Boolean where A: Ring<N>, A: Order<N> = this geq zero
/**
 * Checks if [this] number is negative in the ordered ring.
 */
context(A)
public fun <N, A> N.isNegative(): Boolean where A: Ring<N>, A: Order<N> = this lt zero
/**
 * Checks if [this] number is non-negative in the ordered ring.
 */
context(A)
public fun <N, A> N.isNonNegative(): Boolean where A: Ring<N>, A: Order<N> = this leq zero

/**
 * Returns value of (mathematical) `sign` function. I.e. returns `1` if [this] number is positive,
 * `-1` if [this] number is negative, or `0` if [this] number is zero.
 */
context(A)
public val <N, A> N.sign: Int where A: Ring<N>, A: Order<N> get() {
    return when(this.compareWith(zero)) {
        ComparisonResult.LeftIsGreaterThanRight -> 1
        ComparisonResult.LeftIsLessThanRight ->  -1
        ComparisonResult.Equal -> 0
    }
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.geq
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.leq
import dev.lounres.kone.relations.lt


/**
 * Checks if [this] number is positive in the ordered semiring.
 */
context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> Number.isPositive(): Boolean = this gt zero
/**
 * Checks if [this] number is non-positive in the ordered semiring.
 */
context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> Number.isNonPositive(): Boolean = this leq zero
/**
 * Checks if [this] number is negative in the ordered semiring.
 */
context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> Number.isNegative(): Boolean = this lt zero
/**
 * Checks if [this] number is non-negative in the ordered semiring.
 */
context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> Number.isNonNegative(): Boolean = this geq zero

/**
 * Returns value of (mathematical) `sign` function.
 * I.e. returns `1` if [this] number is positive, `-1` if [this] number is negative, or `0` if [this] number is zero.
 */
context(_: Semiring<Number>, _: Order<Number>)
public val <Number> Number.sign: Int
    get() = when(this.compareWith(zero)) {
        ComparisonResult.LeftIsGreaterThanRight -> 1
        ComparisonResult.LeftIsLessThanRight ->  -1
        ComparisonResult.Equal -> 0
    }

/**
 * Returns absolute value of the [number].
 * I.e. if the [number] is non-negative it is returned, otherwise its negation is returned.
 */
context(_: Ring<Number>, _: Order<Number>)
public fun <Number> abs(number: Number): Number =
    if (number.isNonNegative()) number else -number
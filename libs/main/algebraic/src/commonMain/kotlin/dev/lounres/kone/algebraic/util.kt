/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.asKotlinComparisonResult
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.geq
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.leq
import dev.lounres.kone.relations.lt


/**
 * Checks if [this] number is positive in the ordered semiring.
 */
context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.isPositive(): Boolean = this gt ring.zero
/**
 * Checks if [this] number is non-positive in the ordered semiring.
 */
context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.isNonPositive(): Boolean = this leq ring.zero
/**
 * Checks if [this] number is negative in the ordered semiring.
 */
context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.isNegative(): Boolean = this lt ring.zero
/**
 * Checks if [this] number is non-negative in the ordered semiring.
 */
context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.isNonNegative(): Boolean = this geq ring.zero

public enum class Sign(
    private val signInt: Int
) {
    Negative(-1), Zero(0), Positive(1);
    public fun toSignInt(): Int = signInt
}

public fun Sign.isPositive(): Boolean = this == Sign.Positive
public fun Sign.isZero(): Boolean = this == Sign.Zero
public fun Sign.isNegative(): Boolean = this == Sign.Negative
public fun Sign.isNonPositive(): Boolean = this != Sign.Positive
public fun Sign.isNonZero(): Boolean = this != Sign.Zero
public fun Sign.isNonNegative(): Boolean = this != Sign.Negative

public operator fun Sign.unaryMinus(): Sign =
    when (this) {
        Negative -> Positive
        Zero -> Zero
        Positive -> Negative
    }

public operator fun Sign.times(other: Sign): Sign =
    when {
        this == Zero || other == Zero -> Zero
        (this == Positive) == (other == Positive) -> Positive
        else -> Negative
    }

context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.sign(): Sign =
    when (this.compareWith(ring.zero)) {
        ComparisonResult.LeftIsGreaterThanRight -> Sign.Positive
        ComparisonResult.LeftIsLessThanRight -> Sign.Negative
        ComparisonResult.Equal -> Sign.Zero
    }

/**
 * Returns value of (mathematical) `sign` function.
 * I.e. returns `1` if [this] number is positive, `-1` if [this] number is negative, or `0` if [this] number is zero.
 */
context(ring: Monoid<Number>, _: Order<Number>)
public fun <Number> Number.signInt(): Int = this.compareWith(ring.zero).asKotlinComparisonResult()

/**
 * Returns absolute value of the [number].
 * I.e. if the [number] is non-negative it is returned, otherwise its negation is returned.
 */
context(_: Group<Number>, _: Order<Number>)
public fun <Number> abs(number: Number): Number =
    if (number.isNonNegative()) number else -number
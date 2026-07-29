/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.invoke
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

/**
 * Represents the sign of a number: negative, zero, or positive.
 *
 * @param signInt the integer representation of the sign: -1 for negative, 0 for zero, 1 for positive
 */
public enum class Sign(
    private val signInt: Int
) {
    Negative(-1), Zero(0), Positive(1);
    /**
     * Returns the integer representation of this sign.
     *
     * @receiver the sign to convert
     * @return -1 for [Negative], 0 for [Zero], 1 for [Positive]
     */
    public fun toSignInt(): Int = signInt
}

/**
 * Checks if this sign is positive.
 *
 * @receiver the sign to check
 * @return `true` if this sign is [Sign.Positive], `false` otherwise
 */
public fun Sign.isPositive(): Boolean = this == Sign.Positive
/**
 * Checks if this sign is zero.
 *
 * @receiver the sign to check
 * @return `true` if this sign is [Sign.Zero], `false` otherwise
 */
public fun Sign.isZero(): Boolean = this == Sign.Zero
/**
 * Checks if this sign is negative.
 *
 * @receiver the sign to check
 * @return `true` if this sign is [Sign.Negative], `false` otherwise
 */
public fun Sign.isNegative(): Boolean = this == Sign.Negative
/**
 * Checks if this sign is non-positive (i.e. zero or negative).
 *
 * @receiver the sign to check
 * @return `true` if this sign is not [Sign.Positive], `false` otherwise
 */
public fun Sign.isNonPositive(): Boolean = this != Sign.Positive
/**
 * Checks if this sign is non-zero.
 *
 * @receiver the sign to check
 * @return `true` if this sign is not [Sign.Zero], `false` otherwise
 */
public fun Sign.isNonZero(): Boolean = this != Sign.Zero
/**
 * Checks if this sign is non-negative (i.e. zero or positive).
 *
 * @receiver the sign to check
 * @return `true` if this sign is not [Sign.Negative], `false` otherwise
 */
public fun Sign.isNonNegative(): Boolean = this != Sign.Negative

/**
 * Returns the negation of this sign.
 *
 * @receiver the sign to negate
 * @return [Sign.Positive] if this is [Sign.Negative], [Sign.Zero] if this is [Sign.Zero], [Sign.Negative] if this is [Sign.Positive]
 */
public operator fun Sign.unaryMinus(): Sign =
    when (this) {
        Negative -> Positive
        Zero -> Zero
        Positive -> Negative
    }

/**
 * Multiplies this sign by another sign.
 *
 * @receiver the first sign
 * @param other the second sign
 * @return [Sign.Positive] if signs are equal and non-zero, [Sign.Negative] if signs differ and non-zero, [Sign.Zero] if either sign is zero
 */
public operator fun Sign.times(other: Sign): Sign =
    when {
        this == Zero || other == Zero -> Zero
        this == other -> Positive
        else -> Negative
    }

/**
 * Returns the sign of this number.
 *
 * @param ring the monoid providing the zero element for comparison
 * @param _ the order relation for comparison
 * @receiver the number whose sign is to be determined
 * @param Number the type of numbers being compared
 * @return [Sign.Positive] if this number is greater than zero, [Sign.Negative] if less than zero, [Sign.Zero] if equal to zero
 */
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
 * Returns absolute value of [this number][this].
 * I.e. if [this number][this] is non-negative it is returned, otherwise its negation is returned.
 */
context(group: Group<Number>, _: Order<Number>)
public fun <Number> Number.absoluteValue(): Number = group.numberUnaryMinus { if (this.isNonNegative()) this else -this }
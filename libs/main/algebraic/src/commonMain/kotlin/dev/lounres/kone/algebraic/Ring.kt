/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingMinus
import dev.lounres.kone.algebraic.util.doublingPlus
import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.comparison.Equality


/**
 * Describes a context that represents [mathematical commutative ring](https://en.wikipedia.org/wiki/Ring_(mathematics)).
 * It means that it provides operations like `+` (both unary and binary), `-` (both unary and binary), `*`, `power`,
 * and some other that satisfy axioms of ring like `a + b == b + a` or `a * (b * c) == (a * b) * c`.
 *
 * Usual structures like integers or real numbers satisfy the axioms (one says that they *form commutative rings*),
 * so you don't need to worry about fully understanding the concept of commutative ring.
 * It won't be true only the moment you introduce such structures as a set of matrices 2⨯2 with usual operations
 * you learn on any course of linear algebra.
 *
 * Obviously the [Ring] interface extends the [Equality] interface because otherwise there is no understanding
 * of the mathematical operations.
 *
 * Such contexts are used instead of usual [plus], [minus], [times] and other overloadings for several reasons.
 * Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Ring<N>: Equality<N> {
    // region Constants
    /**
     * Represents zero element (a.k.a *neutral additive element*).
     */
    public val zero: N
    /**
     * Represents unit element (a.k.a *neutral multiplicative element*).
     */
    public val one: N
    // endregion

    // region Equality
    /**
     * Checks that [this] number is a zero in the context of the [Ring].
     */
    public fun N.isZero(): Boolean = this equalsTo zero
    /**
     * Checks that [this] number is a one in the context of the [Ring].
     */
    public fun N.isOne(): Boolean = this equalsTo one
    /**
     * Checks that [this] number is not a zero in the context of the [Ring].
     */
    // FIXME: KT-5351
    public fun N.isNotZero(): Boolean = !isZero()
    /**
     * Checks that [this] number is not a one in the context of the [Ring].
     */
    // FIXME: KT-5351
    public fun N.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: Int): N = one doublingTimes arg
    /**
     * Converts instance of [UInt] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: UInt): N = one doublingTimes arg
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: Long): N = one doublingTimes arg
    /**
     * Converts instance of [ULong] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: ULong): N = one doublingTimes arg
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val Int.value: N get() = valueOf(this)
    /**
     * Converts instance of [UInt] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val UInt.value: N get() = valueOf(this)
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val Long.value: N get() = valueOf(this)
    /**
     * Converts instance of [ULong] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val ULong.value: N get() = valueOf(this)
    // endregion

    // region Number-Int operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun N.plus(other: Int): N = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun N.minus(other: Int): N = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun N.times(other: Int): N = this * other.value
    // endregion

    // region Number-UInt operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun N.plus(other: UInt): N = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun N.minus(other: UInt): N = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun N.times(other: UInt): N = this * other.value
    // endregion

    // region Number-Long operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun N.plus(other: Long): N = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun N.minus(other: Long): N = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun N.times(other: Long): N = this * other.value
    // endregion

    // region Number-ULong operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun N.plus(other: ULong): N = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun N.minus(other: ULong): N = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun N.times(other: ULong): N = this * other.value
    // endregion

    // region Int-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun Int.plus(other: N): N = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun Int.minus(other: N): N = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun Int.times(other: N): N = this.value * other
    // endregion

    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun UInt.plus(other: N): N = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun UInt.minus(other: N): N = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun UInt.times(other: N): N = this.value * other
    // endregion

    // region Long-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun Long.plus(other: N): N = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun Long.minus(other: N): N = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun Long.times(other: N): N = this.value * other
    // endregion

    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun ULong.plus(other: N): N = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun ULong.minus(other: N): N = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun ULong.times(other: N): N = this.value * other
    // endregion

    // region Number-Number operations
    /**
     * Returns the same value.
     */
    public operator fun N.unaryPlus(): N = this
    /**
     * Inverses [this] value in terms of the [Ring].
     */
    public operator fun N.unaryMinus(): N
    /**
     * Sums [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun N.plus(other: N): N
    /**
     * Subtracts [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun N.minus(other: N): N
    /**
     * Multiplies [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun N.times(other: N): N
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: N, exponent: UInt): N = base squaringPower exponent
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: N, exponent: ULong): N = base squaringPower exponent
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun N.pow(exponent: UInt): N = power(this, exponent)
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun N.pow(exponent: ULong): N = power(this, exponent)
    // endregion
}
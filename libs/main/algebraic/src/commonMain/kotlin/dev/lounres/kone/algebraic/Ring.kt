/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

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
public interface Ring<Number> : Equality<Number> {
    // region Constants
    /**
     * Represents zero element (a.k.a *neutral additive element*).
     */
    public val zero: Number
    /**
     * Represents unit element (a.k.a *neutral multiplicative element*).
     */
    public val one: Number
    // endregion

    // region Equality
    /**
     * Checks that [this] number is a zero in the context of the [Ring].
     */
    public fun Number.isZero(): Boolean = this equalsTo zero
    /**
     * Checks that [this] number is a one in the context of the [Ring].
     */
    public fun Number.isOne(): Boolean = this equalsTo one
    /**
     * Checks that [this] number is not a zero in the context of the [Ring].
     */
    // FIXME: KT-5351
    public fun Number.isNotZero(): Boolean = !isZero()
    /**
     * Checks that [this] number is not a one in the context of the [Ring].
     */
    // FIXME: KT-5351
    public fun Number.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: Int): Number = one doublingTimes arg
    /**
     * Converts instance of [UInt] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: UInt): Number = one doublingTimes arg
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: Long): Number = one doublingTimes arg
    /**
     * Converts instance of [ULong] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [arg] number of units.
     */
    public fun valueOf(arg: ULong): Number = one doublingTimes arg
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val Int.value: Number get() = valueOf(this)
    /**
     * Converts instance of [UInt] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val UInt.value: Number get() = valueOf(this)
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val Long.value: Number get() = valueOf(this)
    /**
     * Converts instance of [ULong] to an element of the [Ring] it is equal to.
     *
     * The result is equal to sum of [this] number of units.
     */
    public val ULong.value: Number get() = valueOf(this)
    // endregion

    // region Number-Int operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun Number.plus(other: Int): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun Number.minus(other: Int): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun Number.times(other: Int): Number = this * other.value
    // endregion

    // region Number-UInt operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun Number.plus(other: UInt): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun Number.minus(other: UInt): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun Number.times(other: UInt): Number = this * other.value
    // endregion

    // region Number-Long operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun Number.plus(other: Long): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun Number.minus(other: Long): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun Number.times(other: Long): Number = this * other.value
    // endregion

    // region Number-ULong operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`
     */
    public operator fun Number.plus(other: ULong): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`
     */
    public operator fun Number.minus(other: ULong): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun Number.times(other: ULong): Number = this * other.value
    // endregion

    // region Int-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun Int.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun Int.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun Int.times(other: Number): Number = this.value * other
    // endregion

    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun UInt.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun UInt.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun UInt.times(other: Number): Number = this.value * other
    // endregion

    // region Long-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun Long.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun Long.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun Long.times(other: Number): Number = this.value * other
    // endregion

    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`
     */
    public operator fun ULong.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`
     */
    public operator fun ULong.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`
     */
    public operator fun ULong.times(other: Number): Number = this.value * other
    // endregion

    // region Number-Number operations
    /**
     * Inverses [this] value in terms of the [Ring].
     */
    public operator fun Number.unaryMinus(): Number
    /**
     * Sums [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun Number.plus(other: Number): Number
    /**
     * Subtracts [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun Number.minus(other: Number): Number
    /**
     * Multiplies [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun Number.times(other: Number): Number
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: Number, exponent: UInt): Number = base squaringPower exponent
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: Number, exponent: ULong): Number = base squaringPower exponent
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun Number.pow(exponent: UInt): Number = power(this, exponent)
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun Number.pow(exponent: ULong): Number = power(this, exponent)
    // endregion
}


// region Constants
context(ring: Ring<Number>)
public val <Number> zero: Number get() = ring.zero
context(ring: Ring<Number>)
public val <Number> one: Number get() = ring.one
// endregion

// region Equality
context(ring: Ring<Number>)
public fun <Number> Number.isZero(): Boolean = with(ring) { this@isZero.isZero() }
context(ring: Ring<Number>)
public fun <Number> Number.isOne(): Boolean = with(ring) { this@isOne.isOne() }
// FIXME: KT-5351
context(ring: Ring<Number>)
public fun <Number> Number.isNotZero(): Boolean = with(ring) { this@isNotZero.isNotZero() }
// FIXME: KT-5351
context(ring: Ring<Number>)
public fun <Number> Number.isNotOne(): Boolean = with(ring) { this@isNotOne.isNotOne() }
// endregion

// region Integers conversion
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: Int): Number = ring.valueOf(arg)
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: UInt): Number = ring.valueOf(arg)
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: Long): Number = ring.valueOf(arg)
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: ULong): Number = ring.valueOf(arg)
context(ring: Ring<Number>)
public val <Number> Int.value: Number get() = with(ring) { this@value.value }
context(ring: Ring<Number>)
public val <Number> UInt.value: Number get() = with(ring) { this@value.value }
context(ring: Ring<Number>)
public val <Number> Long.value: Number get() = with(ring) { this@value.value }
context(ring: Ring<Number>)
public val <Number> ULong.value: Number get() = with(ring) { this@value.value }
// endregion

// region Number-Int operations
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Int): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Int): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: Int): Number = with(ring) { this@times * other }
// endregion

// region Number-UInt operations
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: UInt): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: UInt): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: UInt): Number = with(ring) { this@times * other }
// endregion

// region Number-Long operations
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Long): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Long): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: Long): Number = with(ring) { this@times * other }
// endregion

// region Number-ULong operations
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: ULong): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: ULong): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: ULong): Number = with(ring) { this@times * other }
// endregion

// region Int-Number operations
context(ring: Ring<Number>)
public operator fun <Number> Int.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Int.minus(other: Number): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Int.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region UInt-Number operations
context(ring: Ring<Number>)
public operator fun <Number> UInt.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> UInt.minus(other: Number): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> UInt.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region Long-Number operations
context(ring: Ring<Number>)
public operator fun <Number> Long.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Long.minus(other: Number): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Long.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region ULong-Number operations
context(ring: Ring<Number>)
public operator fun <Number> ULong.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> ULong.minus(other: Number): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> ULong.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region Number-Number operations
context(ring: Ring<Number>)
public operator fun <Number> Number.unaryMinus(): Number = with(ring) { -this@unaryMinus }
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Number): Number = with(ring) { this@minus - other }
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: Number): Number = with(ring) { this@times * other }
context(ring: Ring<Number>)
public fun <Number> power(base: Number, exponent: UInt): Number = ring.power(base, exponent)
context(ring: Ring<Number>)
public fun <Number> power(base: Number, exponent: ULong): Number = ring.power(base, exponent)
context(ring: Ring<Number>)
public infix fun <Number> Number.pow(exponent: UInt): Number = with(ring) { this@pow pow exponent }
context(ring: Ring<Number>)
public infix fun <Number> Number.pow(exponent: ULong): Number = with(ring) { this@pow pow exponent }
// endregion
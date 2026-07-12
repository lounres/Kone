/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.internal.asDigit
import dev.lounres.kone.algebraic.internal.possibleDigits
import dev.lounres.kone.collections.array.KoneULongArray
import dev.lounres.kone.collections.array.isEmpty
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.slice
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.relations.Equality
import kotlinx.serialization.Serializable


//@Serializable
////@JvmInline // There might be a problem with the MFVC and context parameters. See KT-72538 for more.
//public /*value*/ data class BigLong(public val sign: Sign, public val absoluteValue: UBigLong) {
//    init {
//        UBigLong.context.numberIsZero {
//            require(sign.isZero() == absoluteValue.isZero()) { "Sign must be 0 iff absolute value is zero as well. Actual sign is $sign, actual absolute value is $absoluteValue" }
//        }
//    }
//
//    public val magnitude: KoneULongArray get() = absoluteValue.magnitude
//
//    override fun toString(): String = toString(10u)
//
//    public companion object
//}
//
//public fun BigLong.Companion.from(sign: Sign, magnitude: KoneULongArray): BigLong {
//    val magnitude = magnitude.removeLeadingZeros()
//    require(sign.isZero() == magnitude.all { it == 0uL }) { "sign must be 0 iff magnitude does not contain non-zero elements" }
//    return BigLong(sign, UBigLong(magnitude))
//}
//public fun BigLong.Companion.from(sign: Sign, vararg magnitude: ULong): BigLong {
//    val magnitude = KoneULongArray(magnitude).removeLeadingZeros()
//    require(sign.isZero() == magnitude.isEmpty()) { "sign must be 0 iff magnitude does not contain non-zero elements" }
//    return BigLong(sign, UBigLong(magnitude))
//}
//
//public object BigLongContext: Reification<BigLong>, Equality<BigLong>, Order<BigLong>, Hashing<BigLong>, EuclideanRing<BigLong> {
//    // region Reification
//    override fun contains(element: Any?): Boolean = element is BigLong
//    override fun reifyMaybe(element: Any?): Maybe<BigLong> = if (element is BigLong) Some(element) else None
//    override fun reifyOrNull(element: Any?): BigLong? = element as? BigLong
//    override fun reify(element: Any?): BigLong = element as? BigLong ?: reificationException()
//    // endregion
//
//    // region Order
//    override fun BigLong.compareWith(other: BigLong): ComparisonResult = context(UBigLong.context) {
//        when {
//            this.sign.isPositive() && other.sign.isPositive() -> this.absoluteValue compareWith other.absoluteValue
//            this.sign.isPositive() && other.sign.isZero() -> ComparisonResult.LeftIsGreaterThanRight
//            this.sign.isPositive() && other.sign.isNegative() -> ComparisonResult.LeftIsGreaterThanRight
//            this.sign.isZero() && other.sign.isPositive() -> ComparisonResult.LeftIsLessThanRight
//            this.sign.isZero() && other.sign.isZero() -> ComparisonResult.Equal
//            this.sign.isZero() && other.sign.isNegative() -> ComparisonResult.LeftIsGreaterThanRight
//            this.sign.isNegative() && other.sign.isPositive() -> ComparisonResult.LeftIsLessThanRight
//            this.sign.isNegative() && other.sign.isZero() -> ComparisonResult.LeftIsLessThanRight
//            this.sign.isNegative() && other.sign.isNegative() -> other.absoluteValue compareWith this.absoluteValue
//            else -> error("Unexpected internal case")
//        }
//    }
//    // endregion
//
//    // region Hashing
//    override fun BigLong.hash(): Int = context(UBigLong.context) { absoluteValue.hash() }
//    // endregion
//
//    // region Constants
//    override val zero: BigLong = BigLong(Zero, UBigLong.context.zero)
//    override val one: BigLong = BigLong(Positive, UBigLong.context.one)
//    // endregion
//
//    // region Equality
//    override fun BigLong.equalsTo(other: BigLong): Boolean = this.sign == other.sign && context(UBigLong.context) { this.absoluteValue equalsTo other.absoluteValue }
//    override val numberIsZero: IsZero<BigLong> = IsZero { this.sign.isZero() }
//    override val numberIsOne: IsOne<BigLong> = IsOne { this.sign.isPositive() && UBigLong.context.numberIsOne { this.absoluteValue.isOne() } }
//    // endregion
//
//    // region Conversion
//    override fun valueOf(arg: Int): BigLong = when ((Int.order()) { arg compareWith 0 }) {
//        ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = UBigLong.context.valueOf(arg.toUInt()))
//        ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = UBigLong.context.valueOf((-arg).toUInt()))
//        ComparisonResult.Equal -> zero
//    }
//    override fun valueOf(arg: UInt): BigLong = if (arg == 0u) zero else BigLong(sign = Positive, absoluteValue = UBigLong.context.valueOf(arg))
//    override fun valueOf(arg: Long): BigLong = when ((Long.order()) { arg compareWith 0 }) {
//        ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = UBigLong.context.valueOf(arg.toULong()))
//        ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = UBigLong.context.valueOf(arg.toULong()))
//        ComparisonResult.Equal -> zero
//    }
//    override fun valueOf(arg: ULong): BigLong = if (arg == 0uL) zero else BigLong(sign = Positive, absoluteValue = UBigLong.context.valueOf(arg))
//    public fun valueOf(arg: UBigLong): BigLong = UBigLong.context.numberIsZero {
//        if (arg.isZero()) zero else BigLong(sign = Positive, absoluteValue = arg)
//    }
//    // endregion
//
//    // region BigLong-UBigLong operations
//    public operator fun BigLong.plus(other: UBigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.sign.isPositive() && other.isPositive() -> BigLong(sign = Positive, absoluteValue = this.absoluteValue + other)
//            this.sign.isPositive() && other.isZero() -> this
//            this.sign.isPositive() && other.isNegative() -> error("Unexpected internal case")
//            this.sign.isZero() && other.isPositive() -> valueOf(other)
//            this.sign.isZero() && other.isZero() -> zero
//            this.sign.isZero() && other.isNegative() -> error("Unexpected internal case")
//            this.sign.isNegative() && other.isPositive() ->
//                when (this.absoluteValue compareWith other) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Negative, absoluteValue = this.absoluteValue - other)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Positive, absoluteValue = other - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isNegative() && other.isZero() -> this
//            this.sign.isNegative() && other.isNegative() -> error("Unexpected internal case")
//            else -> error("Unexpected internal case")
//        }
//    }
//    public operator fun BigLong.minus(other: UBigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.sign.isPositive() && other.isNegative() -> error("Unexpected internal case")
//            this.sign.isPositive() && other.isZero() -> this
//            this.sign.isPositive() && other.isPositive() ->
//                when (this.absoluteValue compareWith other) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = this.absoluteValue - other)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = other - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isZero() && other.isNegative() -> error("Unexpected internal case")
//            this.sign.isZero() && other.isZero() -> zero
//            this.sign.isZero() && other.isPositive() -> -valueOf(other)
//            this.sign.isNegative() && other.isNegative() -> error("Unexpected internal case")
//            this.sign.isNegative() && other.isZero() -> this
//            this.sign.isNegative() && other.isPositive() -> BigLong(sign = Negative, absoluteValue = this.absoluteValue + other)
//            else -> error("Unexpected internal case")
//        }
//    }
//    public operator fun BigLong.times(other: UBigLong): BigLong =
//        if (this.sign.isZero() || context(UBigLong.context) { other.isZero() }) zero
//        else BigLong(sign = this.sign, absoluteValue = context(UBigLong.context) { this.absoluteValue * other })
//    public infix fun BigLong.divrem(other: UBigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
//        if (other.isZero()) divisionByZero()
//        if (this.sign.isZero()) return EuclideanDivisionResult(zero, zero)
//
//        val result = this.absoluteValue divrem other
//
//        if (result.remainder.isNotZero()) {
//            if (this.isNonPositive()) EuclideanDivisionResult(
//                quotient = BigLong(sign = Negative, absoluteValue = result.quotient + 1u),
//                remainder = BigLong(sign = Positive, absoluteValue = other - result.remainder),
//            ) else EuclideanDivisionResult(
//                quotient = valueOf(result.quotient),
//                remainder = valueOf(result.remainder),
//            )
//        } else {
//            if (this.isNonPositive()) EuclideanDivisionResult(
//                quotient = BigLong(sign = Negative, absoluteValue = result.quotient),
//                remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//            ) else EuclideanDivisionResult(
//                quotient = BigLong(sign = Positive, absoluteValue = result.quotient),
//                remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//            )
//        }
//    }
//    public operator fun BigLong.div(other: UBigLong): BigLong = context(UBigLong.context) {
//        if (other.isZero()) divisionByZero()
//        if (this.sign.isZero()) return zero
//
//        val result = this.absoluteValue divrem other
//
//        if (result.remainder.isNotZero()) {
//            if (this.isNonPositive()) BigLong(sign = Negative, absoluteValue = result.quotient + 1u)
//            else valueOf(result.quotient)
//        } else {
//            if (this.isNonPositive()) BigLong(sign = Negative, absoluteValue = result.quotient)
//            else valueOf(result.quotient)
//        }
//    }
//    public operator fun BigLong.rem(other: UBigLong): BigLong = context(UBigLong.context) {
//        if (other.isZero()) divisionByZero()
//        if (this.sign.isZero()) return zero
//
//        val result = this.absoluteValue % other
//
//        if (result.isNotZero()) {
//            if (this.isNonPositive()) BigLong(sign = Positive, absoluteValue = other - result)
//            else valueOf(result)
//        } else {
//            if (this.isNonPositive()) BigLong(sign = Zero, absoluteValue = result)
//            else valueOf(result)
//        }
//    }
//    // endregion
//
//    // region UBigLong-BigLong operations
//    public operator fun UBigLong.plus(other: BigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.isPositive() && other.sign.isPositive() -> BigLong(sign = Positive, absoluteValue = this + other.absoluteValue)
//            this.isPositive() && other.sign.isZero() -> valueOf(this)
//            this.isPositive() && other.sign.isNegative() ->
//                when (this compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = this - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = other.absoluteValue - this)
//                    ComparisonResult.Equal -> zero
//                }
//            this.isZero() && other.sign.isPositive() -> other
//            this.isZero() && other.sign.isZero() -> zero
//            this.isZero() && other.sign.isNegative() -> other
//            this.isNegative() && other.sign.isPositive() -> error("Unexpected internal case")
//            this.isNegative() && other.sign.isZero() -> error("Unexpected internal case")
//            this.isNegative() && other.sign.isNegative() -> error("Unexpected internal case")
//            else -> error("Unexpected internal case")
//        }
//    }
//    public operator fun UBigLong.minus(other: BigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.isPositive() && other.sign.isNegative() -> BigLong(sign = Positive, absoluteValue = this + other.absoluteValue)
//            this.isPositive() && other.sign.isZero() -> valueOf(this)
//            this.isPositive() && other.sign.isPositive() ->
//                when (this compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = this - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = other.absoluteValue - this)
//                    ComparisonResult.Equal -> zero
//                }
//            this.isZero() && other.sign.isNegative() -> -other
//            this.isZero() && other.sign.isZero() -> zero
//            this.isZero() && other.sign.isPositive() -> -other
//            this.isNegative() && other.sign.isNegative() -> error("Unexpected internal case")
//            this.isNegative() && other.sign.isZero() -> error("Unexpected internal case")
//            this.isNegative() && other.sign.isPositive() -> error("Unexpected internal case")
//            else -> error("Unexpected internal case")
//        }
//    }
//    public operator fun UBigLong.times(other: BigLong): BigLong =
//        if (context(UBigLong.context) { this.isZero() } || other.sign.isZero()) zero
//        else BigLong(sign = other.sign, absoluteValue = context(UBigLong.context) { this * other.absoluteValue })
//    public infix fun UBigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.isZero()) return EuclideanDivisionResult(zero, zero)
//
//        val result = this divrem other.absoluteValue
//        val actualResult =
//            if (result.remainder.isNotZero()) {
//                if (other.sign.isNonPositive()) EuclideanDivisionResult(
//                    quotient = BigLong(sign = Negative, absoluteValue = result.quotient + 1u),
//                    remainder = BigLong(sign = Positive, absoluteValue = other.absoluteValue - result.remainder),
//                ) else EuclideanDivisionResult(
//                    quotient = valueOf(result.quotient),
//                    remainder = valueOf(result.remainder),
//                )
//            } else {
//                if (other.sign.isNonPositive()) EuclideanDivisionResult(
//                    quotient = BigLong(sign = Negative, absoluteValue = result.quotient),
//                    remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//                ) else EuclideanDivisionResult(
//                    quotient = BigLong(sign = Positive, absoluteValue = result.quotient),
//                    remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//                )
//            }
//
//        if (other.sign.isNegative()) {
//            EuclideanDivisionResult(
//                quotient = -actualResult.quotient,
//                remainder = -actualResult.remainder,
//            )
//        } else actualResult
//    }
//    public operator fun UBigLong.div(other: BigLong): BigLong = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.isZero()) return zero
//
//        val result = this divrem other.absoluteValue
//        val actualResult =
//            if (result.remainder.isNotZero()) {
//                if (other.sign.isNonPositive()) BigLong(sign = Negative, absoluteValue = result.quotient + 1u)
//                else valueOf(result.quotient)
//            } else {
//                if (other.sign.isNonPositive()) BigLong(sign = Negative, absoluteValue = result.quotient)
//                else valueOf(result.quotient)
//            }
//
//        if (other.sign.isNegative()) -actualResult else actualResult
//    }
//    public operator fun UBigLong.rem(other: BigLong): BigLong = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.isZero()) return zero
//
//        val result = this % other.absoluteValue
//        val actualResult =
//            if (result.isNotZero()) {
//                if (other.sign.isNonPositive()) BigLong(sign = Positive, absoluteValue = other.absoluteValue - result)
//                else valueOf(result)
//            } else BigLong(sign = Zero, absoluteValue = result)
//
//        if (other.sign.isNegative()) -actualResult else actualResult
//    }
//    // endregion
//
//    // region BigLong-BigLong operations
//    override fun BigLong.unaryMinus(): BigLong = BigLong(-sign, absoluteValue)
//    override fun BigLong.plus(other: BigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.sign.isPositive() && other.sign.isPositive() -> BigLong(sign = Positive, absoluteValue = this.absoluteValue + other.absoluteValue)
//            this.sign.isPositive() && other.sign.isZero() -> this
//            this.sign.isPositive() && other.sign.isNegative() ->
//                when (this.absoluteValue compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = this.absoluteValue - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = other.absoluteValue - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isZero() && other.sign.isPositive() -> other
//            this.sign.isZero() && other.sign.isZero() -> zero
//            this.sign.isZero() && other.sign.isNegative() -> other
//            this.sign.isNegative() && other.sign.isPositive() ->
//                when (this.absoluteValue compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Negative, absoluteValue = this.absoluteValue - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Positive, absoluteValue = other.absoluteValue - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isNegative() && other.sign.isZero() -> this
//            this.sign.isNegative() && other.sign.isNegative() -> BigLong(sign = Negative, absoluteValue = this.absoluteValue + other.absoluteValue)
//            else -> error("Unexpected internal case")
//        }
//    }
//    override fun BigLong.minus(other: BigLong): BigLong = context(UBigLong.context) {
//        when {
//            this.sign.isPositive() && other.sign.isNegative() -> BigLong(sign = Positive, absoluteValue = this.absoluteValue + other.absoluteValue)
//            this.sign.isPositive() && other.sign.isZero() -> this
//            this.sign.isPositive() && other.sign.isPositive() ->
//                when (this.absoluteValue compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Positive, absoluteValue = this.absoluteValue - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Negative, absoluteValue = other.absoluteValue - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isZero() && other.sign.isNegative() -> -other
//            this.sign.isZero() && other.sign.isZero() -> zero
//            this.sign.isZero() && other.sign.isPositive() -> -other
//            this.sign.isNegative() && other.sign.isNegative() ->
//                when (this.absoluteValue compareWith other.absoluteValue) {
//                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = Negative, absoluteValue = this.absoluteValue - other.absoluteValue)
//                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = Positive, absoluteValue = other.absoluteValue - this.absoluteValue)
//                    ComparisonResult.Equal -> zero
//                }
//            this.sign.isNegative() && other.sign.isZero() -> this
//            this.sign.isNegative() && other.sign.isPositive() -> BigLong(sign = Negative, absoluteValue = this.absoluteValue + other.absoluteValue)
//            else -> error("Unexpected internal case")
//        }
//    }
//    override fun BigLong.times(other: BigLong): BigLong =
//        if (this.sign.isZero() || other.sign.isZero()) zero
//        else BigLong(sign = this.sign * other.sign, absoluteValue = context(UBigLong.context) { this.absoluteValue * other.absoluteValue })
//    override fun BigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.sign.isZero()) return EuclideanDivisionResult(zero, zero)
//
//        val result = this.absoluteValue divrem other.absoluteValue
//        val actualResult =
//            if (result.remainder.isNotZero()) {
//                if (this.sign != other.sign) EuclideanDivisionResult(
//                    quotient = BigLong(sign = Negative, absoluteValue = result.quotient + 1u),
//                    remainder = BigLong(sign = Positive, absoluteValue = other.absoluteValue - result.remainder),
//                ) else EuclideanDivisionResult(
//                    quotient = valueOf(result.quotient),
//                    remainder = valueOf(result.remainder),
//                )
//            } else {
//                if (this.sign != other.sign) EuclideanDivisionResult(
//                    quotient = BigLong(sign = Negative, absoluteValue = result.quotient),
//                    remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//                ) else EuclideanDivisionResult(
//                    quotient = BigLong(sign = Positive, absoluteValue = result.quotient),
//                    remainder = BigLong(sign = Zero, absoluteValue = result.remainder),
//                )
//            }
//
//        if (other.sign.isNegative()) {
//            EuclideanDivisionResult(
//                quotient = -actualResult.quotient,
//                remainder = -actualResult.remainder,
//            )
//        } else actualResult
//    }
//    override fun BigLong.div(other: BigLong): BigLong = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.sign.isZero()) return zero
//
//        val result = this.absoluteValue divrem other.absoluteValue
//        val actualResult =
//            if (result.remainder.isNotZero()) {
//                if (this.sign != other.sign) BigLong(sign = Negative, absoluteValue = result.quotient + 1u)
//                else valueOf(result.quotient)
//            } else {
//                if (this.sign != other.sign) BigLong(sign = Negative, absoluteValue = result.quotient)
//                else valueOf(result.quotient)
//            }
//
//        if (other.sign.isNegative()) -actualResult else actualResult
//    }
//    override fun BigLong.rem(other: BigLong): BigLong = context(UBigLong.context) {
//        if (other.sign.isZero()) divisionByZero()
//        if (this.sign.isZero()) return zero
//
//        val result = this.absoluteValue % other.absoluteValue
//        val actualResult =
//            if (result.isNotZero()) {
//                if (this.sign != other.sign) BigLong(sign = Positive, absoluteValue = other.absoluteValue - result)
//                else valueOf(result)
//            } else BigLong(sign = Zero, absoluteValue = result)
//
//        if (other.sign.isNegative()) -actualResult else actualResult
//    }
//    override fun power(base: BigLong, exponent: UInt): BigLong =
//        if (base.sign.isZero()) zero
//        else BigLong(
//            sign = if (base.sign.isPositive() || exponent % 2u == 0u) Positive else Negative,
//            absoluteValue = UBigLong.context.power(base.absoluteValue, exponent)
//        )
//    override fun power(base: BigLong, exponent: ULong): BigLong =
//        if (base.sign.isZero()) zero
//        else BigLong(
//            sign = if (base.sign.isPositive() || exponent % 2u == 0uL) Positive else Negative,
//            absoluteValue = UBigLong.context.power(base.absoluteValue, exponent)
//        )
//    // endregion
//}
//
//// TODO: Replace with context-providing functions that hide the ccontext object
//public val BigLong.Companion.context: BigLongContext get() = BigLongContext
//
//public fun String.toBigLong(radix: UInt = 10u): BigLong {
//    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
//    if (this.isEmpty()) numberFormatException(this, radix)
//    if (this == "0") return BigLong.context.zero
//
//    val sign: Sign
//    val digits: String
//    if (this[0] == '-') {
//        sign = Negative
//        digits = this.substring(1, this.length)
//    } else {
//        sign = Positive
//        digits = this
//    }
//
//    if (context(Equality.defaultFor<Char>()) { digits.first() !in possibleDigits.slice(1u, radix) }) numberFormatException(this, radix)
//    if (context(Equality.defaultFor<Char>()) { digits.any { it !in possibleDigits.slice(0u, radix) } }) numberFormatException(this, radix)
//
//    var result = UBigLong.context.zero
//    for (char in digits) result = context(UBigLong.context) { result * radix + char.asDigit() }
//
//    return BigLong(sign, result)
//}
//
//public fun BigLong.toString(radix: UInt): String {
//    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
//
//    UBigLong.context {
//        if (this.sign.isZero()) return "0"
//
//        return buildString {
//            val radix = valueOf(radix)
//
//            var result = this@toString.absoluteValue
//            while (result.isNotZero()) {
//                (val newResult = quotient, val digit = remainder) = result divrem radix
//                append(possibleDigits[digit.toUInt()])
//                result = newResult
//            }
//
//            if (this@toString.sign.isNegative()) append('-')
//        }.reversed()
//    }
//}
//
//// region Conversion
//context(context: BigLongContext)
//public fun valueOf(arg: UBigLong): BigLong = context.valueOf(arg)
//// endregion
//
//// region BigLong-UBigLong operations
//context(context: BigLongContext)
//public operator fun BigLong.plus(other: UBigLong): BigLong = with(context) { this@plus + other }
//context(context: BigLongContext)
//public operator fun BigLong.minus(other: UBigLong): BigLong = with(context) { this@minus - other }
//context(context: BigLongContext)
//public operator fun BigLong.times(other: UBigLong): BigLong = with(context) { this@times * other }
//context(context: BigLongContext)
//public infix fun BigLong.divrem(other: UBigLong): EuclideanDivisionResult<BigLong> = with(context) { this@divrem divrem other }
//context(context: BigLongContext)
//public operator fun BigLong.div(other: UBigLong): BigLong = with(context) { this@div / other }
//context(context: BigLongContext)
//public operator fun BigLong.rem(other: UBigLong): BigLong = with(context) { this@rem % other }
//// endregion
//
//// region UBigLong-BigLong operations
//context(context: BigLongContext)
//public operator fun UBigLong.plus(other: BigLong): BigLong = with(context) { this@plus + other }
//context(context: BigLongContext)
//public operator fun UBigLong.minus(other: BigLong): BigLong = with(context) { this@minus - other }
//context(context: BigLongContext)
//public operator fun UBigLong.times(other: BigLong): BigLong = with(context) { this@times * other }
//context(context: BigLongContext)
//public infix fun UBigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = with(context) { this@divrem divrem other }
//context(context: BigLongContext)
//public operator fun UBigLong.div(other: BigLong): BigLong = with(context) { this@div / other }
//context(context: BigLongContext)
//public operator fun UBigLong.rem(other: BigLong): BigLong = with(context) { this@rem % other }
//// endregion
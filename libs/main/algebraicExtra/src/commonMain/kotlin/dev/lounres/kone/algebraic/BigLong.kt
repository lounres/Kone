/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.internal.asDigit
import dev.lounres.kone.algebraic.internal.possibleDigits
import dev.lounres.kone.collections.array.KoneULongArray
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.slice
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
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
public value class BigLong(public val sign: Int, public val absoluteValue: UBigLong) {
    init {
        context(UBigLong.context) {
            require(sign == 0 || sign == 1 || sign == -1) { "sign must be 0, or 1, or -1" }
            require((sign == 0) == absoluteValue.isZero()) { "sign must be 0 iff absolute value is zero as well" }
        }
    }
    
    public val magnitude: KoneULongArray get() = absoluteValue.magnitude
    
//    override fun toString(): String {
//    }
    
    public companion object
}

public fun BigLong.Companion.from(sign: Int, array: KoneULongArray): BigLong {
    require(sign == 0 || sign == 1 || sign == -1) { "sign must be 0, or 1, or -1" }
    require((sign == 0) == array.all { it == 0uL }) { "sign must be 0 iff magnitude does not contain non-zero elements" }
    return BigLong(sign, UBigLong.from(array))
}
public fun BigLong.Companion.from(sign: Int, vararg array: ULong): BigLong {
    require(sign == 0 || sign == 1 || sign == -1) { "sign must be 0, or 1, or -1" }
    require((sign == 0) == array.isEmpty()) { "sign must be 0 iff magnitude does not contain non-zero elements" }
    return BigLong(sign, UBigLong.from(KoneULongArray(array)))
}

public object BigLongContext: Reification<BigLong>, EuclideanRing<BigLong>, Order<BigLong>, Hashing<BigLong> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is BigLong
    override fun reifyMaybe(element: Any?): Maybe<BigLong> = if (element is BigLong) Some(element) else None
    override fun reifyOrNull(element: Any?): BigLong? = element as? BigLong
    override fun reify(element: Any?): BigLong = element as? BigLong ?: reificationException()
    // endregion
    
    // region Order
    override fun BigLong.compareWith(other: BigLong): ComparisonResult = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == 1 -> this.absoluteValue compareWith other.absoluteValue
            this.sign == 1 && other.sign == 0 -> ComparisonResult.LeftIsGreaterThanRight
            this.sign == 1 && other.sign == -1 -> ComparisonResult.LeftIsGreaterThanRight
            this.sign == 0 && other.sign == 1 -> ComparisonResult.LeftIsLessThanRight
            this.sign == 0 && other.sign == 0 -> ComparisonResult.Equal
            this.sign == 0 && other.sign == -1 -> ComparisonResult.LeftIsGreaterThanRight
            this.sign == -1 && other.sign == 1 -> ComparisonResult.LeftIsLessThanRight
            this.sign == -1 && other.sign == 0 -> ComparisonResult.LeftIsLessThanRight
            this.sign == -1 && other.sign == -1 -> other.absoluteValue compareWith this.absoluteValue
            else -> error("Unexpected internal case")
        }
    }
    // endregion
    
    // region Hashing
    override fun BigLong.hash(): Int = context(UBigLong.context) { absoluteValue.hash() }
    // endregion
    
    // region Constants
    override val zero: BigLong = BigLong(0, UBigLong.context.zero)
    override val one: BigLong = BigLong(1, UBigLong.context.one)
    // endregion
    
    // region Equality
    override fun BigLong.equalsTo(other: BigLong): Boolean = this.sign == other.sign && context(UBigLong.context) { this.absoluteValue equalsTo other.absoluteValue }
    override fun BigLong.isZero(): Boolean = this.sign == 0
    override fun BigLong.isOne(): Boolean = this.sign == 1 && context(UBigLong.context) { this.absoluteValue.isOne() }
    // endregion
    
    // region Conversion
    override fun valueOf(arg: Int): BigLong = when (with(Int.context) { arg compareWith 0 }) {
        ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = UBigLong.context.valueOf(arg.toUInt()))
        ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = UBigLong.context.valueOf((-arg).toUInt()))
        ComparisonResult.Equal -> zero
    }
    override fun valueOf(arg: UInt): BigLong = if (arg == 0u) zero else BigLong(sign = 1, absoluteValue = UBigLong.context.valueOf(arg))
    override fun valueOf(arg: Long): BigLong = when (with(Long.context) { arg compareWith 0 }) {
        ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = UBigLong.context.valueOf(arg.toULong()))
        ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = UBigLong.context.valueOf(arg.toULong()))
        ComparisonResult.Equal -> zero
    }
    override fun valueOf(arg: ULong): BigLong = if (arg == 0uL) zero else BigLong(sign = 1, absoluteValue = UBigLong.context.valueOf(arg))
    public fun valueOf(arg: UBigLong): BigLong = context(UBigLong.context) {
        if (arg.isZero()) zero else BigLong(sign = 1, absoluteValue = arg)
    }
    public val UBigLong.value: BigLong get() = valueOf(this)
    // endregion
    
    // region BigLong-UBigLong operations
    public operator fun BigLong.plus(other: UBigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == 1 -> BigLong(sign = 1, absoluteValue = this.absoluteValue + other)
            this.sign == 1 && other.sign == 0 -> this
            this.sign == 1 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == 0 && other.sign == 1 -> other.value
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == 1 ->
                when (this.absoluteValue compareWith other) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = -1, absoluteValue = this.absoluteValue - other)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = 1, absoluteValue = other - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == -1 && other.sign == 0 -> this
            this.sign == -1 && other.sign == -1 -> error("Unexpected internal case")
            else -> error("Unexpected internal case")
        }
    }
    public operator fun BigLong.minus(other: UBigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == 1 && other.sign == 0 -> this
            this.sign == 1 && other.sign == 1 ->
                when (this.absoluteValue compareWith other) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = this.absoluteValue - other)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = other - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == 0 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == 1 -> -other.value
            this.sign == -1 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == 0 -> this
            this.sign == -1 && other.sign == 1 -> BigLong(sign = -1, absoluteValue = this.absoluteValue + other)
            else -> error("Unexpected internal case")
        }
    }
    public operator fun BigLong.times(other: UBigLong): BigLong =
        if (this.sign == 0 || context(UBigLong.context) { other.isZero() }) zero
        else BigLong(sign = this.sign, absoluteValue = context(UBigLong.context) { this.absoluteValue * other })
    public infix fun BigLong.divrem(other: UBigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
        if (other.isZero()) divisionByZero()
        if (this.sign == 0) return EuclideanDivisionResult(zero, zero)
        
        val result = this.absoluteValue divrem other
        
        if (result.remainder.isNotZero()) {
            if (this.sign != 1) EuclideanDivisionResult(
                quotient = BigLong(sign = -1, absoluteValue = result.quotient + 1u),
                remainder = BigLong(sign = 1, absoluteValue = other - result.remainder),
            ) else EuclideanDivisionResult(
                quotient = result.quotient.value,
                remainder = result.remainder.value,
            )
        } else {
            if (this.sign != 1) EuclideanDivisionResult(
                quotient = BigLong(sign = -1, absoluteValue = result.quotient),
                remainder = BigLong(sign = 0, absoluteValue = result.remainder),
            ) else EuclideanDivisionResult(
                quotient = BigLong(sign = 1, absoluteValue = result.quotient),
                remainder = BigLong(sign = 0, absoluteValue = result.remainder),
            )
        }
    }
    public operator fun BigLong.div(other: UBigLong): BigLong = context(UBigLong.context) {
        if (other.isZero()) divisionByZero()
        if (this.sign == 0) return zero
        
        val result = this.absoluteValue divrem other
        
        if (result.remainder.isNotZero()) {
            if (this.sign != 1) BigLong(sign = -1, absoluteValue = result.quotient + 1u)
            else result.quotient.value
        } else {
            if (this.sign != 1) BigLong(sign = -1, absoluteValue = result.quotient)
            else result.quotient.value
        }
    }
    public operator fun BigLong.rem(other: UBigLong): BigLong = context(UBigLong.context) {
        if (other.isZero()) divisionByZero()
        if (this.sign == 0) return zero
        
        val result = this.absoluteValue % other
        
        if (result.isNotZero()) {
            if (this.sign != 1) BigLong(sign = 1, absoluteValue = other - result)
            else result.value
        } else {
            if (this.sign != 1) BigLong(sign = 0, absoluteValue = result)
            else result.value
        }
    }
    // endregion
    
    // region UBigLong-BigLong operations
    public operator fun UBigLong.plus(other: BigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == 1 -> BigLong(sign = 1, absoluteValue = this + other.absoluteValue)
            this.sign == 1 && other.sign == 0 -> this.value
            this.sign == 1 && other.sign == -1 ->
                when (this compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = this - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = other.absoluteValue - this)
                    ComparisonResult.Equal -> zero
                }
            this.sign == 0 && other.sign == 1 -> other
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == -1 -> other
            this.sign == -1 && other.sign == 1 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == 0 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == -1 -> error("Unexpected internal case")
            else -> error("Unexpected internal case")
        }
    }
    public operator fun UBigLong.minus(other: BigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == -1 -> BigLong(sign = 1, absoluteValue = this + other.absoluteValue)
            this.sign == 1 && other.sign == 0 -> this.value
            this.sign == 1 && other.sign == 1 ->
                when (this compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = this - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = other.absoluteValue - this)
                    ComparisonResult.Equal -> zero
                }
            this.sign == 0 && other.sign == -1 -> -other
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == 1 -> -other
            this.sign == -1 && other.sign == -1 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == 0 -> error("Unexpected internal case")
            this.sign == -1 && other.sign == 1 -> error("Unexpected internal case")
            else -> error("Unexpected internal case")
        }
    }
    public operator fun UBigLong.times(other: BigLong): BigLong =
        if (context(UBigLong.context) { this.isZero() } || other.sign == 0) zero
        else BigLong(sign = other.sign, absoluteValue = context(UBigLong.context) { this * other.absoluteValue })
    public infix fun UBigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.isZero()) return EuclideanDivisionResult(zero, zero)
        
        val result = this divrem other.absoluteValue
        val actualResult =
            if (result.remainder.isNotZero()) {
                if (1 != other.sign) EuclideanDivisionResult(
                    quotient = BigLong(sign = -1, absoluteValue = result.quotient + 1u),
                    remainder = BigLong(sign = 1, absoluteValue = other.absoluteValue - result.remainder),
                ) else EuclideanDivisionResult(
                    quotient = result.quotient.value,
                    remainder = result.remainder.value,
                )
            } else {
                if (1 != other.sign) EuclideanDivisionResult(
                    quotient = BigLong(sign = -1, absoluteValue = result.quotient),
                    remainder = BigLong(sign = 0, absoluteValue = result.remainder),
                ) else EuclideanDivisionResult(
                    quotient = BigLong(sign = 1, absoluteValue = result.quotient),
                    remainder = BigLong(sign = 0, absoluteValue = result.remainder),
                )
            }
        
        if (other.sign == -1) {
            EuclideanDivisionResult(
                quotient = -actualResult.quotient,
                remainder = -actualResult.remainder,
            )
        } else actualResult
    }
    public operator fun UBigLong.div(other: BigLong): BigLong = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.isZero()) return zero
        
        val result = this divrem other.absoluteValue
        val actualResult =
            if (result.remainder.isNotZero()) {
                if (1 != other.sign) BigLong(sign = -1, absoluteValue = result.quotient + 1u)
                else result.quotient.value
            } else {
                if (1 != other.sign) BigLong(sign = -1, absoluteValue = result.quotient)
                else result.quotient.value
            }
        
        if (other.sign == -1) -actualResult else actualResult
    }
    public operator fun UBigLong.rem(other: BigLong): BigLong = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.isZero()) return zero
        
        val result = this % other.absoluteValue
        val actualResult =
            if (result.isNotZero()) {
                if (1 != other.sign) BigLong(sign = 1, absoluteValue = other.absoluteValue - result)
                else result.value
            } else BigLong(sign = 0, absoluteValue = result)
        
        if (other.sign == -1) -actualResult else actualResult
    }
    // endregion
    
    // region BigLong-BigLong operations
    override fun BigLong.unaryMinus(): BigLong = BigLong(-sign, absoluteValue)
    override fun BigLong.plus(other: BigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == 1 -> BigLong(sign = 1, absoluteValue = this.absoluteValue + other.absoluteValue)
            this.sign == 1 && other.sign == 0 -> this
            this.sign == 1 && other.sign == -1 ->
                when (this.absoluteValue compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = this.absoluteValue - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = other.absoluteValue - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == 0 && other.sign == 1 -> other
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == -1 -> other
            this.sign == -1 && other.sign == 1 ->
                when (this.absoluteValue compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = -1, absoluteValue = this.absoluteValue - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = 1, absoluteValue = other.absoluteValue - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == -1 && other.sign == 0 -> this
            this.sign == -1 && other.sign == -1 -> BigLong(sign = -1, absoluteValue = this.absoluteValue + other.absoluteValue)
            else -> error("Unexpected internal case")
        }
    }
    override fun BigLong.minus(other: BigLong): BigLong = context(UBigLong.context) {
        when {
            this.sign == 1 && other.sign == -1 -> BigLong(sign = 1, absoluteValue = this.absoluteValue + other.absoluteValue)
            this.sign == 1 && other.sign == 0 -> this
            this.sign == 1 && other.sign == 1 ->
                when (this.absoluteValue compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = 1, absoluteValue = this.absoluteValue - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = -1, absoluteValue = other.absoluteValue - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == 0 && other.sign == -1 -> -other
            this.sign == 0 && other.sign == 0 -> zero
            this.sign == 0 && other.sign == 1 -> -other
            this.sign == -1 && other.sign == -1 ->
                when (this.absoluteValue compareWith other.absoluteValue) {
                    ComparisonResult.LeftIsGreaterThanRight -> BigLong(sign = -1, absoluteValue = this.absoluteValue - other.absoluteValue)
                    ComparisonResult.LeftIsLessThanRight -> BigLong(sign = 1, absoluteValue = other.absoluteValue - this.absoluteValue)
                    ComparisonResult.Equal -> zero
                }
            this.sign == -1 && other.sign == 0 -> this
            this.sign == -1 && other.sign == 1 -> BigLong(sign = -1, absoluteValue = this.absoluteValue + other.absoluteValue)
            else -> error("Unexpected internal case")
        }
    }
    override fun BigLong.times(other: BigLong): BigLong =
        if (this.sign == 0 || other.sign == 0) zero
        else BigLong(sign = this.sign * other.sign, absoluteValue = context(UBigLong.context) { this.absoluteValue * other.absoluteValue })
    override fun BigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.sign == 0) return EuclideanDivisionResult(zero, zero)
        
        val result = this.absoluteValue divrem other.absoluteValue
        val actualResult =
            if (result.remainder.isNotZero()) {
                if (this.sign != other.sign) EuclideanDivisionResult(
                    quotient = BigLong(sign = -1, absoluteValue = result.quotient + 1u),
                    remainder = BigLong(sign = 1, absoluteValue = other.absoluteValue - result.remainder),
                ) else EuclideanDivisionResult(
                    quotient = result.quotient.value,
                    remainder = result.remainder.value,
                )
            } else {
                if (this.sign != other.sign) EuclideanDivisionResult(
                    quotient = BigLong(sign = -1, absoluteValue = result.quotient),
                    remainder = BigLong(sign = 0, absoluteValue = result.remainder),
                ) else EuclideanDivisionResult(
                    quotient = BigLong(sign = 1, absoluteValue = result.quotient),
                    remainder = BigLong(sign = 0, absoluteValue = result.remainder),
                )
            }
        
        if (other.sign == -1) {
            EuclideanDivisionResult(
                quotient = -actualResult.quotient,
                remainder = -actualResult.remainder,
            )
        } else actualResult
    }
    override fun BigLong.div(other: BigLong): BigLong = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.sign == 0) return zero
        
        val result = this.absoluteValue divrem other.absoluteValue
        val actualResult =
            if (result.remainder.isNotZero()) {
                if (this.sign != other.sign) BigLong(sign = -1, absoluteValue = result.quotient + 1u)
                else result.quotient.value
            } else {
                if (this.sign != other.sign) BigLong(sign = -1, absoluteValue = result.quotient)
                else result.quotient.value
            }
        
        if (other.sign == -1) -actualResult else actualResult
    }
    override fun BigLong.rem(other: BigLong): BigLong = context(UBigLong.context) {
        if (other.sign == 0) divisionByZero()
        if (this.sign == 0) return zero
        
        val result = this.absoluteValue % other.absoluteValue
        val actualResult =
            if (result.isNotZero()) {
                if (this.sign != other.sign) BigLong(sign = 1, absoluteValue = other.absoluteValue - result)
                else result.value
            } else BigLong(sign = 0, absoluteValue = result)
        
        if (other.sign == -1) -actualResult else actualResult
    }
    override fun power(base: BigLong, exponent: UInt): BigLong =
        if (base.sign == 0) zero
        else BigLong(
            sign = when {
                base.sign == 1 -> 1
                exponent % 2u == 0u -> 1
                else -> -1
            },
            absoluteValue = UBigLong.context.power(base.absoluteValue, exponent)
        )
    override fun power(base: BigLong, exponent: ULong): BigLong =
        if (base.sign == 0) zero
        else BigLong(
            sign = when {
                base.sign == 1 -> 1
                exponent % 2u == 0uL -> 1
                else -> -1
            },
            absoluteValue = UBigLong.context.power(base.absoluteValue, exponent)
        )
    // endregion
}

public val BigLong.Companion.context: BigLongContext get() = BigLongContext

public fun String.toBigLong(radix: UInt = 10u): BigLong {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    if (this.isEmpty()) numberFormatException(this, radix)
    if (this == "0") return BigLong.context.zero
    
    val sign: Int
    val digits: String
    if (this[0] == '-') {
        sign = -1
        digits = this.substring(1, this.length)
    } else {
        sign = 1
        digits = this
    }
    
    if (context(Equality.defaultFor<Char>()) { digits.first() !in possibleDigits.slice(1u, radix) }) numberFormatException(this, radix)
    if (context(Equality.defaultFor<Char>()) { digits.any { it !in possibleDigits.slice(0u, radix) } }) numberFormatException(this, radix)
    
    var result = UBigLong.context.zero
    for (char in digits) result = context(UBigLong.context) { result * radix + char.asDigit() }
    
    return BigLong(sign, result)
}

public fun BigLong.toString(radix: UInt): String {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    
    context(UBigLong.context) {
        if (this.sign == 0) return "0"
        
        return buildString {
            if (this@toString.sign == -1) append('-')
            
            val radix = radix.value
            
            var result = this@toString.absoluteValue
            while (result.isNotZero()) {
                val (newResult, digit) = result divrem radix
                append(possibleDigits[digit.toUInt()])
                result = newResult
            }
        }.reversed()
    }
}

// region Conversion
context(context: BigLongContext)
public fun valueOf(arg: UBigLong): BigLong = context.valueOf(arg)
context(context: BigLongContext)
public val UBigLong.value: BigLong get() = with(context) { this@value.value }
// endregion

// region BigLong-UBigLong operations
context(context: BigLongContext)
public operator fun BigLong.plus(other: UBigLong): BigLong = with(context) { this@plus + other }
context(context: BigLongContext)
public operator fun BigLong.minus(other: UBigLong): BigLong = with(context) { this@minus - other }
context(context: BigLongContext)
public operator fun BigLong.times(other: UBigLong): BigLong = with(context) { this@times * other }
context(context: BigLongContext)
public infix fun BigLong.divrem(other: UBigLong): EuclideanDivisionResult<BigLong> = with(context) { this@divrem divrem other }
context(context: BigLongContext)
public operator fun BigLong.div(other: UBigLong): BigLong = with(context) { this@div / other }
context(context: BigLongContext)
public operator fun BigLong.rem(other: UBigLong): BigLong = with(context) { this@rem % other }
// endregion

// region UBigLong-BigLong operations
context(context: BigLongContext)
public operator fun UBigLong.plus(other: BigLong): BigLong = with(context) { this@plus + other }
context(context: BigLongContext)
public operator fun UBigLong.minus(other: BigLong): BigLong = with(context) { this@minus - other }
context(context: BigLongContext)
public operator fun UBigLong.times(other: BigLong): BigLong = with(context) { this@times * other }
context(context: BigLongContext)
public infix fun UBigLong.divrem(other: BigLong): EuclideanDivisionResult<BigLong> = with(context) { this@divrem divrem other }
context(context: BigLongContext)
public operator fun UBigLong.div(other: BigLong): BigLong = with(context) { this@div / other }
context(context: BigLongContext)
public operator fun UBigLong.rem(other: BigLong): BigLong = with(context) { this@rem % other }
// endregion
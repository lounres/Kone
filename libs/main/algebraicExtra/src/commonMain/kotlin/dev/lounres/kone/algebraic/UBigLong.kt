/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.internal.add
import dev.lounres.kone.algebraic.internal.asDigit
import dev.lounres.kone.algebraic.internal.multiply
import dev.lounres.kone.algebraic.internal.possibleDigits
import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.slice
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.lt
import dev.lounres.kone.relations.reificationException
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.max
import kotlin.math.min


@Serializable
@JvmInline
public value class UBigLong internal constructor(public val magnitude: KoneULongArray) {
    override fun toString(): String = toString(10u)
    
    public companion object
}

internal fun KoneMutableULongArray.removeLeadingZeros(): KoneULongArray {
    var currentIndex = this.size - 1u
    while (currentIndex != UInt.MAX_VALUE) {
        if (this[currentIndex] != 0uL) break
        currentIndex--
    }
    return KoneULongArray(currentIndex + 1u) { this[it] }
}

internal fun KoneULongArray.removeLeadingZeros(): KoneULongArray {
    var currentIndex = this.size - 1u
    while (currentIndex != UInt.MAX_VALUE) {
        if (this[currentIndex] != 0uL) break
        currentIndex--
    }
    return KoneULongArray(currentIndex + 1u) { this[it] }
}

internal val ULONG_BIT_SIZE = ULong.SIZE_BITS.toUInt()

public fun UBigLong.Companion.from(array: KoneULongArray): UBigLong = UBigLong(array.removeLeadingZeros())
public fun UBigLong.Companion.from(vararg array: ULong): UBigLong = UBigLong(KoneMutableULongArray(array).removeLeadingZeros())

public object UBigLongContext: Reification<UBigLong>, EuclideanSemiring<UBigLong>, ExtendedSemiring<UBigLong>,
    Order<UBigLong>, Hashing<UBigLong> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UBigLong
    override fun reifyMaybe(element: Any?): Maybe<UBigLong> = if (element is UBigLong) Some(element) else None
    override fun reifyOrNull(element: Any?): UBigLong? = element as? UBigLong
    override fun reify(element: Any?): UBigLong = element as? UBigLong ?: reificationException()
    // endregion
    
    // region Order
    override fun UBigLong.compareWith(other: UBigLong): ComparisonResult {
        if (this.magnitude.size > other.magnitude.size) return ComparisonResult.LeftIsGreaterThanRight
        if (this.magnitude.size < other.magnitude.size) return ComparisonResult.LeftIsLessThanRight
        if (this.magnitude.size == 0u) return ComparisonResult.Equal
        
        for (index in this.magnitude.lastIndex downTo 0u)
            when {
                this.magnitude[index] > other.magnitude[index] -> return ComparisonResult.LeftIsGreaterThanRight
                this.magnitude[index] < other.magnitude[index] -> return ComparisonResult.LeftIsLessThanRight
            }
        
        return ComparisonResult.Equal
    }
    // endregion
    
    // region Hashing
    override fun UBigLong.hash(): Int = this.magnitude.contentHashCode()
    // endregion
    
    // region Constants
    override val zero: UBigLong = UBigLong(KoneULongArray.of())
    override val one: UBigLong = UBigLong(KoneULongArray.of(1uL))
    // endregion
    
    // region Equality
    override fun UBigLong.equalsTo(other: UBigLong): Boolean = this.magnitude contentEquals other.magnitude
    override fun UBigLong.isZero(): Boolean = this.magnitude.isEmpty()
    override fun UBigLong.isOne(): Boolean = this.magnitude.let { it.size == 1u && it[0u] == 1uL }
    // endregion
    
    // region Conversion
    override fun valueOf(arg: UInt): UBigLong = if (arg == 0u) UBigLong(KoneULongArray.of()) else UBigLong(KoneULongArray.of(arg.toULong()))
    override fun valueOf(arg: ULong): UBigLong = if (arg == 0uL) UBigLong(KoneULongArray.of()) else UBigLong(KoneULongArray.of(arg))
    // endregion
    
    // region UBigLong-UInt operations
    public operator fun UBigLong.div(other: UInt): UBigLong = this / valueOf(other)
    // endregion

    // region UBigLong-ULong operations
    public operator fun UBigLong.div(other: ULong): UBigLong = this / valueOf(other)
    // endregion

    // region UInt-UBigLong operations
    public operator fun UInt.div(other: UBigLong): UBigLong = valueOf(this) / other
    // endregion

    // region ULong-UBigLong operations
    public operator fun ULong.div(other: UBigLong): UBigLong = valueOf(this) / other
    // endregion

    // region UBigLong-UBigLong operations
    override operator fun UBigLong.plus(other: UBigLong): UBigLong {
        val maxSize = max(this.magnitude.size, other.magnitude.size)
        val result = KoneMutableULongArray(maxSize + 1u)
        var carry = 0uL
        for (index in 0u ..< maxSize) {
            val additionResult = add(
                this.magnitude.let { if (it.size > index) it[index] else 0uL },
                other.magnitude.let { if (it.size > index) it[index] else 0uL },
                carry
            )
            result[index] = additionResult.first
            carry = additionResult.second
        }
        result[maxSize] = carry
        return UBigLong(result.removeLeadingZeros())
    }
    override operator fun UBigLong.minus(other: UBigLong): UBigLong {
        if (this lt other) negativeSubtractionResultInExtendedSemiring()

        val result = KoneMutableULongArray(this.magnitude.size) { this.magnitude[it] }
        var anticarry = 0uL
        
        for (index in 0u ..< result.size) {
            var nextAnticarry = 0uL
            val otherValue = other.magnitude.let { if (it.size > index) it[index] else 0uL }
            if (result[index] < otherValue) nextAnticarry++
            result[index] -= otherValue
            if (result[index] < anticarry) nextAnticarry++
            result[index] -= anticarry
            anticarry = nextAnticarry
        }
        
        return UBigLong(result.removeLeadingZeros())
    }
    // TODO: Experiment with Karatsuba algorithm, Toom–Cook algorithm and FFT-based algorithms
    override operator fun UBigLong.times(other: UBigLong): UBigLong {
        if (this.isZero() || other.isZero()) return zero
        if (this.isOne()) return other
        if (other.isOne()) return this
        
        val result = KoneMutableULongArray(this.magnitude.size + other.magnitude.size)
        for (thisIndex in 0u ..< this.magnitude.size) for (otherIndex in 0u ..< other.magnitude.size) {
            val productResult = multiply(this.magnitude[thisIndex], other.magnitude[otherIndex])
            var carry = productResult.second
            val additionResult = add(result[thisIndex + otherIndex], productResult.first)
            result[thisIndex + otherIndex] = additionResult.first
            carry += additionResult.second
            
            for (index in thisIndex + otherIndex + 1u ..< result.size) {
                val carryAdditionResult = add(result[index], carry)
                result[index] = carryAdditionResult.first
                carry = carryAdditionResult.second
            }
            check(carry == 0uL) { "For some reason there is carry at the top" }
        }
    
        return UBigLong(result.removeLeadingZeros())
    }
    // TODO: Experiment with https://en.wikipedia.org/wiki/Division_algorithm#Integer_division_(unsigned)_with_remainder
    override infix fun UBigLong.divrem(other: UBigLong): EuclideanDivisionResult<UBigLong> {
        if (other.magnitude.isEmpty()) divisionByZero()
        if (this.magnitude.isEmpty()) return EuclideanDivisionResult(zero, zero)
        if (other.magnitude.size > this.magnitude.size) return EuclideanDivisionResult(
            quotient = zero,
            remainder = this,
        )

        val dividend = KoneMutableULongArray(other.magnitude.size + 1u)
        val quotient = KoneMutableULongArray(this.magnitude.size - other.magnitude.size + 1u)
        fun shiftLeftDividendByOneBit() {
            for (index in dividend.lastIndex downTo 1u) {
                dividend[index] = (dividend[index] shl 1) or (dividend[index - 1u] shr 63)
            }
            dividend[0u] = dividend[0u] shl 1
        }
        fun shiftLeftQuotientByOneBit() {
            for (index in quotient.lastIndex downTo 1u) {
                quotient[index] = (quotient[index] shl 1) or (quotient[index - 1u] shr 63)
            }
            quotient[0u] = quotient[0u] shl 1
        }
        fun addOneToDividend() {
            val firstAdditionResult = add(dividend[0u], 1u)
            var carry = firstAdditionResult.second
            dividend[0u] = firstAdditionResult.first
            
            for (index in 1u ..< dividend.size) {
                val result = add(dividend[index], carry)
                carry = result.second
                dividend[index] = result.first
            }
        }
        fun addOneToQuotient() {
            val firstAdditionResult = add(quotient[0u], 1u)
            var carry = firstAdditionResult.second
            quotient[0u] = firstAdditionResult.first
            
            for (index in 1u ..< quotient.size) {
                val result = add(quotient[index], carry)
                carry = result.second
                quotient[index] = result.first
            }
        }
        fun isDividendAtLeastDivisor(): Boolean {
            if (dividend[other.magnitude.size] != 0uL) return true
            for (index in other.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > other.magnitude[index]) return true
                if (dividend[index] < other.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = other.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in this.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (this.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            shiftLeftQuotientByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
                addOneToQuotient()
            }
        }
        
        return EuclideanDivisionResult(
            quotient = UBigLong(quotient.removeLeadingZeros()),
            remainder = UBigLong(dividend.removeLeadingZeros()),
        )
    }
    override operator fun UBigLong.div(other: UBigLong): UBigLong {
        if (other.magnitude.isEmpty()) divisionByZero()
        if (this.magnitude.isEmpty()) return zero
        if (other.magnitude.size > this.magnitude.size) return zero
        
        val dividend = KoneMutableULongArray(other.magnitude.size + 1u)
        val quotient = KoneMutableULongArray(this.magnitude.size - other.magnitude.size + 1u)
        fun shiftLeftDividendByOneBit() {
            for (index in dividend.lastIndex downTo 1u) {
                dividend[index] = (dividend[index] shl 1) or (dividend[index - 1u] shr 63)
            }
            dividend[0u] = dividend[0u] shl 1
        }
        fun shiftLeftQuotientByOneBit() {
            for (index in quotient.lastIndex downTo 1u) {
                quotient[index] = (quotient[index] shl 1) or (quotient[index - 1u] shr 63)
            }
            quotient[0u] = quotient[0u] shl 1
        }
        fun addOneToDividend() {
            val firstAdditionResult = add(dividend[0u], 1u)
            var carry = firstAdditionResult.second
            dividend[0u] = firstAdditionResult.first
            
            for (index in 1u ..< dividend.size) {
                val result = add(dividend[index], carry)
                carry = result.second
                dividend[index] = result.first
            }
        }
        fun addOneToQuotient() {
            val firstAdditionResult = add(quotient[0u], 1u)
            var carry = firstAdditionResult.second
            quotient[0u] = firstAdditionResult.first
            
            for (index in 1u ..< quotient.size) {
                val result = add(quotient[index], carry)
                carry = result.second
                quotient[index] = result.first
            }
        }
        fun isDividendAtLeastDivisor(): Boolean {
            if (dividend[other.magnitude.size] != 0uL) return true
            for (index in other.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > other.magnitude[index]) return true
                if (dividend[index] < other.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = other.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in this.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (this.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            shiftLeftQuotientByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
                addOneToQuotient()
            }
        }
        
        return UBigLong(quotient.removeLeadingZeros())
    }
    override operator fun UBigLong.rem(other: UBigLong): UBigLong {
        if (other.magnitude.isEmpty()) divisionByZero()
        if (this.magnitude.isEmpty()) return zero
        if (other.magnitude.size > this.magnitude.size) return this
        
        val dividend = KoneMutableULongArray(other.magnitude.size + 1u)
        fun shiftLeftDividendByOneBit() {
            for (index in dividend.lastIndex downTo 1u) {
                dividend[index] = (dividend[index] shl 1) or (dividend[index - 1u] shr 63)
            }
            dividend[0u] = dividend[0u] shl 1
        }
        fun addOneToDividend() {
            val firstAdditionResult = add(dividend[0u], 1u)
            var carry = firstAdditionResult.second
            dividend[0u] = firstAdditionResult.first
            
            for (index in 1u ..< dividend.size) {
                val result = add(dividend[index], carry)
                carry = result.second
                dividend[index] = result.first
            }
        }
        fun isDividendAtLeastDivisor(): Boolean {
            if (dividend[other.magnitude.size] != 0uL) return true
            for (index in other.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > other.magnitude[index]) return true
                if (dividend[index] < other.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = other.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in this.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (this.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
            }
        }
        
        return UBigLong(dividend.removeLeadingZeros())
    }
    // TODO: Experiment with FFT-based exponentiation
//    override fun power(base: UBigLong, exponent: UInt): UBigLong = base squaringPower exponent
//    override fun power(base: UBigLong, exponent: ULong): UBigLong = base squaringPower exponent
    // endregion
    
    // region Bitwise operations
    public infix fun UBigLong.shr(bitCount: UInt): UBigLong {
        if (this.isZero()) return zero
        if (bitCount == 0u) return this

        val fullShifts = bitCount / ULONG_BIT_SIZE
        val partialShift = bitCount % ULONG_BIT_SIZE

        return when {
            fullShifts >= this.magnitude.size -> zero
            partialShift == 0u -> UBigLong(KoneULongArray(this.magnitude.size - fullShifts) { this.magnitude[it + fullShifts] })
            this.magnitude[this.magnitude.size - 1u] shr partialShift.toInt() != 0uL ->
                UBigLong(
                    KoneULongArray(this.magnitude.size - fullShifts) {
                        when {
                            it < this.magnitude.size - fullShifts - 1u ->
                                (this.magnitude[it + fullShifts + 1u] shl (ULONG_BIT_SIZE - partialShift).toInt()) or (this.magnitude[it + fullShifts] shr partialShift.toInt())
                            else -> this.magnitude[it + fullShifts] shr partialShift.toInt()
                        }
                    }
                )
            else ->
                UBigLong(
                    KoneULongArray(this.magnitude.size - fullShifts - 1u) {
                        (this.magnitude[it + fullShifts + 1u] shl (ULONG_BIT_SIZE - partialShift).toInt()) or (this.magnitude[it + fullShifts] shr partialShift.toInt())
                    }
                )
        }
    }
    public infix fun UBigLong.shl(bitCount: UInt): UBigLong {
        if (this.isZero()) return zero
        if (bitCount == 0u) return this
        
        val fullShifts = bitCount / ULONG_BIT_SIZE
        val partialShift = bitCount % ULONG_BIT_SIZE
        
        return when {
            partialShift == 0u -> UBigLong(
                KoneULongArray(this.magnitude.size + fullShifts) {
                    if (it < fullShifts) 0uL else this.magnitude[it - fullShifts]
                }
            )
            this.magnitude[this.magnitude.size - 1u] shr (ULONG_BIT_SIZE - partialShift).toInt() != 0uL ->
                UBigLong(
                    KoneULongArray(this.magnitude.size + fullShifts + 1u) {
                        when {
                            it < fullShifts -> 0uL
                            it < fullShifts + 1u -> this.magnitude[it - fullShifts] shl partialShift.toInt()
                            it < this.magnitude.size + fullShifts ->
                                (this.magnitude[it - fullShifts] shl partialShift.toInt()) or (this.magnitude[it - fullShifts - 1u] shr (ULONG_BIT_SIZE - partialShift).toInt())
                            else -> this.magnitude[it - fullShifts - 1u] shr (ULONG_BIT_SIZE - partialShift).toInt()
                        }
                    }
                )
            else ->
                UBigLong(
                    KoneULongArray(this.magnitude.size + fullShifts) {
                        when {
                            it < fullShifts -> 0uL
                            it < fullShifts + 1u -> this.magnitude[it - fullShifts] shl partialShift.toInt()
                            else ->
                                (this.magnitude[it - fullShifts] shl partialShift.toInt()) or (this.magnitude[it - fullShifts - 1u] shr (ULONG_BIT_SIZE - partialShift).toInt())
                        }
                    }
                )
        }
    }
    public infix fun UBigLong.and(other: UBigLong): UBigLong =
        UBigLong(
            KoneULongArray(min(this.magnitude.size, other.magnitude.size)) {
                this.magnitude[it] and other.magnitude[it]
            }.removeLeadingZeros()
        )
    public infix fun UBigLong.or(other: UBigLong): UBigLong =
        UBigLong(
            KoneULongArray(max(this.magnitude.size, other.magnitude.size)) {
                when {
                    it >= this.magnitude.size -> other.magnitude[it]
                    it >= other.magnitude.size -> this.magnitude[it]
                    else -> this.magnitude[it] or other.magnitude[it]
                }
            }
        )
    public infix fun UBigLong.xor(other: UBigLong): UBigLong =
        UBigLong(
            KoneULongArray(max(this.magnitude.size, other.magnitude.size)) {
                when {
                    it >= this.magnitude.size -> other.magnitude[it]
                    it >= other.magnitude.size -> this.magnitude[it]
                    else -> this.magnitude[it] xor other.magnitude[it]
                }
            }.removeLeadingZeros()
        )
    // endregion
}

public val UBigLong.Companion.context: UBigLongContext get() = UBigLongContext

// region UBigLong-UBigLong operations
context(context: UBigLongContext)
public infix fun UBigLong.divrem(other: UBigLong): EuclideanDivisionResult<UBigLong> = with(context) { this@divrem divrem other }
context(context: UBigLongContext)
public operator fun UBigLong.div(other: UBigLong): UBigLong = with(context) { this@div / other }
context(context: UBigLongContext)
public operator fun UBigLong.rem(other: UBigLong): UBigLong = with(context) { this@rem % other }
// endregion

// region Bitwise operations
context(context: UBigLongContext)
public infix fun UBigLong.shr(bitCount: UInt): UBigLong = with(context) { this@shr shr bitCount }
context(context: UBigLongContext)
public infix fun UBigLong.shl(bitCount: UInt): UBigLong = with(context) { this@shl shl bitCount }
context(context: UBigLongContext)
public infix fun UBigLong.and(other: UBigLong): UBigLong = with(context) { this@and and other }
context(context: UBigLongContext)
public infix fun UBigLong.or(other: UBigLong): UBigLong = with(context) { this@or or other }
context(context: UBigLongContext)
public infix fun UBigLong.xor(other: UBigLong): UBigLong = with(context) { this@xor xor other }
// endregion

public fun UBigLong.toULong(): ULong = this.magnitude.let { if (it.isEmpty()) 0uL else it[0u] }
public fun UBigLong.toUInt(): UInt = this.toULong().toUInt()

public fun ULong.toUBigLong(): UBigLong = context(UBigLong.context) { valueOf(this) }
public fun UInt.toUBigLong(): UBigLong = context(UBigLong.context) { valueOf(this) }

public fun String.toUBigLong(radix: UInt = 10u): UBigLong {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    if (this.isEmpty()) numberFormatException(this, radix)
    if (this == "0") return UBigLong.context.zero
    if (context(Equality.defaultFor<Char>()) { this.first() !in possibleDigits.slice(1u, radix) }) numberFormatException(this, radix)
    if (context(Equality.defaultFor<Char>()) { this.any { it !in possibleDigits.slice(0u, radix) } }) numberFormatException(this, radix)
    
    var result = UBigLong.context.zero
    for (char in this) result = context(UBigLong.context) { result * radix + char.asDigit() }
    
    return result
}

public fun UBigLong.toString(radix: UInt): String {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    
    context(UBigLong.context) {
        if (this.isZero()) return "0"
        
        return buildString {
            val radix = valueOf(radix)
            
            var result = this@toString
            while (result.isNotZero()) {
                val (newResult, digit) = result divrem radix
                append(possibleDigits[digit.toUInt()])
                result = newResult
            }
        }.reversed()
    }
}
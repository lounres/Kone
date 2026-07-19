/*
 * Copyright © 2026 Gleb Minaev
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
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
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
    return KoneULongArray.generate(currentIndex + 1u) { this[it] }
}

internal fun KoneULongArray.removeLeadingZeros(): KoneULongArray {
    var currentIndex = this.size - 1u
    while (currentIndex != UInt.MAX_VALUE) {
        if (this[currentIndex] != 0uL) break
        currentIndex--
    }
    return KoneULongArray.generate(currentIndex + 1u) { this[it] }
}

internal val ULONG_BIT_SIZE = ULong.SIZE_BITS.toUInt()

public fun UBigLong.Companion.from(array: KoneULongArray): UBigLong = UBigLong(array.removeLeadingZeros())
public fun UBigLong.Companion.from(vararg array: ULong): UBigLong = UBigLong(KoneMutableULongArray(array).removeLeadingZeros())

public object UBigLongContext: Reification<UBigLong>, Equality<UBigLong>, Order<UBigLong>, Hashing<UBigLong>, EuclideanSemiring<UBigLong>, ExtendedSemiring<UBigLong> {
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
    override val numberIsZero: IsZero<UBigLong> = IsZero { it.magnitude.isEmpty() }
    override val numberIsOne: IsOne<UBigLong> = IsOne { it.magnitude.let { it.size == 1u && it[0u] == 1uL } }
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
    override val numberPlusNumber: Plus<UBigLong, UBigLong, UBigLong> = Plus { left, right ->
        val maxSize = maxOf(left.magnitude.size, right.magnitude.size)
        val result = KoneMutableULongArray.fill(maxSize + 1u)
        var carry = 0uL
        for (index in 0u ..< maxSize) {
            val additionResult = add(
                left.magnitude.let { if (it.size > index) it[index] else 0uL },
                right.magnitude.let { if (it.size > index) it[index] else 0uL },
                carry
            )
            result[index] = additionResult.first
            carry = additionResult.second
        }
        result[maxSize] = carry
        UBigLong(result.removeLeadingZeros())
    }
    override val numberMinusNumber: Minus<UBigLong, UBigLong, UBigLong> = Minus { left, right ->
        if (left lt right) negativeSubtractionResultInExtendedSemiring()
        
        val result = KoneMutableULongArray.generate(left.magnitude.size) { left.magnitude[it] }
        var anticarry = 0uL
        
        for (index in 0u ..< result.size) {
            var nextAnticarry = 0uL
            val otherValue = right.magnitude.let { if (it.size > index) it[index] else 0uL }
            if (result[index] < otherValue) nextAnticarry++
            result[index] -= otherValue
            if (result[index] < anticarry) nextAnticarry++
            result[index] -= anticarry
            anticarry = nextAnticarry
        }
        
        UBigLong(result.removeLeadingZeros())
    }
    // TODO: Experiment with Karatsuba algorithm, Toom–Cook algorithm and FFT-based algorithms
    override val numberTimesNumber: Times<UBigLong, UBigLong, UBigLong> = Times { left, right ->
        KoneContext.unwrap(this)
        
        if (left.isZero() || right.isZero()) return@Times zero
        if (left.isOne()) return@Times right
        if (right.isOne()) return@Times left
        
        val result = KoneMutableULongArray.fill(left.magnitude.size + right.magnitude.size)
        for (thisIndex in 0u ..< left.magnitude.size) for (otherIndex in 0u ..< right.magnitude.size) {
            val productResult = multiply(left.magnitude[thisIndex], right.magnitude[otherIndex])
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
        
        UBigLong(result.removeLeadingZeros())
    }
    // TODO: Experiment with https://en.wikipedia.org/wiki/Division_algorithm#Integer_division_(unsigned)_with_remainder
    override val numberDivideRemainderNumber: DivideRemainder<UBigLong, UBigLong, EuclideanDivisionResult<UBigLong>> = DivideRemainder { left, right ->
        KoneContext.unwrap(this@UBigLongContext)
        
        if (right.magnitude.isEmpty()) divisionByZero()
        if (left.magnitude.isEmpty()) return@DivideRemainder EuclideanDivisionResult(zero, zero)
        if (right.magnitude.size > left.magnitude.size) return@DivideRemainder EuclideanDivisionResult(
            quotient = zero,
            remainder = left,
        )
        
        val dividend = KoneMutableULongArray.fill(right.magnitude.size + 1u)
        val quotient = KoneMutableULongArray.fill(left.magnitude.size - right.magnitude.size + 1u)
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
            if (dividend[right.magnitude.size] != 0uL) return true
            for (index in right.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > right.magnitude[index]) return true
                if (dividend[index] < right.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = right.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in left.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (left.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            shiftLeftQuotientByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
                addOneToQuotient()
            }
        }
        
        EuclideanDivisionResult(
            quotient = UBigLong(quotient.removeLeadingZeros()),
            remainder = UBigLong(dividend.removeLeadingZeros()),
        )
    }
    override val numberDivideNumber: Divide<UBigLong, UBigLong, UBigLong> = Divide { left, right ->
        KoneContext.unwrap(this@UBigLongContext)
        
        if (right.magnitude.isEmpty()) divisionByZero()
        if (left.magnitude.isEmpty()) return@Divide zero
        if (right.magnitude.size > left.magnitude.size) return@Divide zero
        
        val dividend = KoneMutableULongArray.fill(right.magnitude.size + 1u)
        val quotient = KoneMutableULongArray.fill(left.magnitude.size - right.magnitude.size + 1u)
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
            if (dividend[right.magnitude.size] != 0uL) return true
            for (index in right.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > right.magnitude[index]) return true
                if (dividend[index] < right.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = right.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in left.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (left.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            shiftLeftQuotientByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
                addOneToQuotient()
            }
        }
        
        UBigLong(quotient.removeLeadingZeros())
    }
    override val numberRemainderNumber: Remainder<UBigLong, UBigLong, UBigLong> = Remainder { left, right ->
        KoneContext.unwrap(this@UBigLongContext)
        
        if (right.magnitude.isEmpty()) divisionByZero()
        if (left.magnitude.isEmpty()) return@Remainder zero
        if (right.magnitude.size > left.magnitude.size) return@Remainder left
        
        val dividend = KoneMutableULongArray.fill(right.magnitude.size + 1u)
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
            if (dividend[right.magnitude.size] != 0uL) return true
            for (index in right.magnitude.lastIndex downTo 0u) {
                if (dividend[index] > right.magnitude[index]) return true
                if (dividend[index] < right.magnitude[index]) return false
            }
            return true
        }
        fun subtractDivisorFromDividend() {
            var anticarry = 0uL
            
            for (index in 0u ..< dividend.size) {
                var nextAnticarry = 0uL
                val otherValue = right.magnitude.let { if (it.size > index) it[index] else 0uL }
                if (dividend[index] < otherValue) nextAnticarry++
                dividend[index] -= otherValue
                if (dividend[index] < anticarry) nextAnticarry++
                dividend[index] -= anticarry
                anticarry = nextAnticarry
            }
        }
        
        for (index in left.magnitude.lastIndex downTo 0u) for (bitIndex in 63 downTo 0) {
            val bit = (left.magnitude[index] shr bitIndex) and 1u
            
            shiftLeftDividendByOneBit()
            if (bit == 1uL) addOneToDividend()
            if (isDividendAtLeastDivisor()) {
                subtractDivisorFromDividend()
            }
        }
        
        UBigLong(dividend.removeLeadingZeros())
    }
    // TODO: Experiment with FFT-based exponentiation
//    override val powerNumberUInt: Power<UBigLong, UInt, UBigLong>
//    override val powerNumberULong: Power<UBigLong, ULong, UBigLong>
    // endregion
    
    // region Bitwise operations
    public infix fun UBigLong.shr(bitCount: UInt): UBigLong {
        if (numberIsZero { this.isZero() }) return zero
        if (bitCount == 0u) return this

        val fullShifts = bitCount / ULONG_BIT_SIZE
        val partialShift = bitCount % ULONG_BIT_SIZE

        return when {
            fullShifts >= this.magnitude.size -> zero
            partialShift == 0u -> UBigLong(KoneULongArray.generate(this.magnitude.size - fullShifts) { this.magnitude[it + fullShifts] })
            this.magnitude[this.magnitude.size - 1u] shr partialShift.toInt() != 0uL ->
                UBigLong(
                    KoneULongArray.generate(this.magnitude.size - fullShifts) {
                        when {
                            it < this.magnitude.size - fullShifts - 1u ->
                                (this.magnitude[it + fullShifts + 1u] shl (ULONG_BIT_SIZE - partialShift).toInt()) or (this.magnitude[it + fullShifts] shr partialShift.toInt())
                            else -> this.magnitude[it + fullShifts] shr partialShift.toInt()
                        }
                    }
                )
            else ->
                UBigLong(
                    KoneULongArray.generate(this.magnitude.size - fullShifts - 1u) {
                        (this.magnitude[it + fullShifts + 1u] shl (ULONG_BIT_SIZE - partialShift).toInt()) or (this.magnitude[it + fullShifts] shr partialShift.toInt())
                    }
                )
        }
    }
    public infix fun UBigLong.shl(bitCount: UInt): UBigLong {
        if (numberIsZero { this.isZero() }) return zero
        if (bitCount == 0u) return this
        
        val fullShifts = bitCount / ULONG_BIT_SIZE
        val partialShift = bitCount % ULONG_BIT_SIZE
        
        return when {
            partialShift == 0u -> UBigLong(
                KoneULongArray.generate(this.magnitude.size + fullShifts) {
                    if (it < fullShifts) 0uL else this.magnitude[it - fullShifts]
                }
            )
            this.magnitude[this.magnitude.size - 1u] shr (ULONG_BIT_SIZE - partialShift).toInt() != 0uL ->
                UBigLong(
                    KoneULongArray.generate(this.magnitude.size + fullShifts + 1u) {
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
                    KoneULongArray.generate(this.magnitude.size + fullShifts) {
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
            KoneULongArray.generate(minOf(this.magnitude.size, other.magnitude.size)) {
                this.magnitude[it] and other.magnitude[it]
            }.removeLeadingZeros()
        )
    public infix fun UBigLong.or(other: UBigLong): UBigLong =
        UBigLong(
            KoneULongArray.generate(maxOf(this.magnitude.size, other.magnitude.size)) {
                when {
                    it >= this.magnitude.size -> other.magnitude[it]
                    it >= other.magnitude.size -> this.magnitude[it]
                    else -> this.magnitude[it] or other.magnitude[it]
                }
            }
        )
    public infix fun UBigLong.xor(other: UBigLong): UBigLong =
        UBigLong(
            KoneULongArray.generate(maxOf(this.magnitude.size, other.magnitude.size)) {
                when {
                    it >= this.magnitude.size -> other.magnitude[it]
                    it >= other.magnitude.size -> this.magnitude[it]
                    else -> this.magnitude[it] xor other.magnitude[it]
                }
            }.removeLeadingZeros()
        )
    // endregion
}

// TODO: Replace with context-providing functions that hide the ccontext object
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

public fun ULong.toUBigLong(): UBigLong = UBigLong.context.valueOf(this)
public fun UInt.toUBigLong(): UBigLong = UBigLong.context.valueOf(this)

public fun String.toUBigLong(radix: UInt = 10u): UBigLong {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    if (this.isEmpty()) numberFormatException(this, radix)
    if (this == "0") return UBigLong.context.zero
    if (context(Equality.defaultFor<Char>()) { this.first() !in possibleDigits.slice(1u, radix) }) numberFormatException(this, radix)
    if (context(Equality.defaultFor<Char>()) { this.any { it !in possibleDigits.slice(0u, radix) } }) numberFormatException(this, radix)
    
    var result = UBigLong.context.zero
    for (char in this) result = context(UBigLong.context.numberTimesUInt, UBigLong.context.numberPlusUInt) { result * radix + char.asDigit() }
    
    return result
}

public fun UBigLong.toString(radix: UInt): String {
    require(radix in 2u .. 36u) { "radix $radix was not in valid range 2..36" }
    KoneContext.unwrap(UBigLong.context)
    
    if (this@toString.isZero()) return "0"
    
    return buildString {
        val radix = UBigLong.context.valueOf(radix)
        
        var result = this@toString
        while (result.isNotZero()) {
            (val newResult = quotient, val digit = remainder) = result divrem radix
            append(possibleDigits[digit.toUInt()])
            result = newResult
        }
    }.reversed()
}
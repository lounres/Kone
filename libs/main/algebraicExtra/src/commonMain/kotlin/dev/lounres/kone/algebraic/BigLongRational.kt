/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.numberTheory.gcd
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withSuperkeys
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
//@JvmInline
public /*value*/ data class BigLongRational internal constructor(
    public val numerator: BigLong,
    public val denominator: UBigLong,
) {
    override fun toString(): String =
        if ((UBigLong.context) { denominator.isOne() }) "$numerator"
        else "$numerator/$denominator"
    
    public companion object {
        public val context: BigLongRationalContext get() = BigLongRationalContext
    }
}

public fun BigLongRational.Companion.from(numerator: BigLong, denominator: UBigLong = UBigLong.context.one): BigLongRational {
    if (context(UBigLong.context) { denominator.isZero() }) divisionByZero()
    
    val greatestCommonDivisor = context(BigLong.context) { gcd(numerator, valueOf(denominator)).absoluteValue }
    
    return BigLongRational(
        numerator = context(BigLong.context) { numerator / greatestCommonDivisor },
        denominator = context(UBigLong.context) { denominator / greatestCommonDivisor },
    )
}

@JvmInline
internal value class BigLongBigLongQuotientsByGCD(val first: BigLong, val second: BigLong) {
    operator fun component1(): BigLong = first
    operator fun component2(): BigLong = second
}

internal fun divideByGCD(first: BigLong, second: BigLong): BigLongBigLongQuotientsByGCD = context(BigLong.context) {
    val gcd = gcd(first, second).absoluteValue
    
    if (context(UBigLong.context) { gcd.isZero() }) BigLongBigLongQuotientsByGCD(BigLong.context.zero, BigLong.context.zero)
    else BigLongBigLongQuotientsByGCD(
        first = first / gcd,
        second = second / gcd,
    )
}

@JvmInline
internal value class UBigLongBigLongQuotientsByGCD(val first: UBigLong, val second: BigLong) {
    operator fun component1(): UBigLong = first
    operator fun component2(): BigLong = second
}

internal fun divideByGCD(first: UBigLong, second: BigLong): UBigLongBigLongQuotientsByGCD = context(UBigLong.context, BigLong.context) {
    val gcd = gcd(first, second.absoluteValue)
    
    if (gcd.isZero()) UBigLongBigLongQuotientsByGCD(UBigLong.context.zero, BigLong.context.zero)
    else UBigLongBigLongQuotientsByGCD(
        first = first / gcd,
        second = second / gcd,
    )
}

@JvmInline
internal value class BigLongUBigLongQuotientsByGCD(val first: BigLong, val second: UBigLong) {
    operator fun component1(): BigLong = first
    operator fun component2(): UBigLong = second
}

internal fun divideByGCD(first: BigLong, second: UBigLong): BigLongUBigLongQuotientsByGCD = context(UBigLong.context, BigLong.context) {
    val gcd = gcd(first.absoluteValue, second)
    
    if (gcd.isZero()) BigLongUBigLongQuotientsByGCD(BigLong.context.zero, UBigLong.context.zero)
    else BigLongUBigLongQuotientsByGCD(
        first = first / gcd,
        second = second / gcd,
    )
}

@JvmInline
internal value class UBigLongUBigLongQuotientsByGCD(val first: UBigLong, val second: UBigLong) {
    operator fun component1(): UBigLong = first
    operator fun component2(): UBigLong = second
}

internal fun divideByGCD(first: UBigLong, second: UBigLong): UBigLongUBigLongQuotientsByGCD = context(UBigLong.context) {
    val gcd = gcd(first, second)
    
    if (context(UBigLong.context) { gcd.isZero() }) UBigLongUBigLongQuotientsByGCD(UBigLong.context.zero, UBigLong.context.zero)
    else UBigLongUBigLongQuotientsByGCD(
        first = first / gcd,
        second = second / gcd,
    )
}

// TODO: Check if GCDs really speed up the computations
public data object BigLongRationalContext : Reification<BigLongRational>, Field<BigLongRational>, Order<BigLongRational>, Hashing<BigLongRational> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is BigLongRational
    override fun reifyMaybe(element: Any?): Maybe<BigLongRational> = if (element is BigLongRational) Some(element) else None
    override fun reifyOrNull(element: Any?): BigLongRational? = element as? BigLongRational
    override fun reify(element: Any?): BigLongRational = element as? BigLongRational ?: reificationException()
    // endregion
    
    // region Constants
    public override val zero: BigLongRational = BigLongRational(BigLong.context.zero, UBigLong.context.one)
    public override val one: BigLongRational = BigLongRational(BigLong.context.one, UBigLong.context.one)
    // endregion
    
    // region Hashing
    override fun BigLongRational.hash(): Int = context(UBigLong.context) { numerator.absoluteValue.hash() * 31 + denominator.hash() }
    // endregion
    
    // region Order
    override fun BigLongRational.compareWith(other: BigLongRational): ComparisonResult = context(BigLong.context) {
        val (thisReducedNumerator, otherReducedNumerator) = divideByGCD(numerator, other.numerator)
        val (thisReducedDenominator, otherReducedDenominator) = divideByGCD(denominator, other.denominator)
        
        (thisReducedNumerator * otherReducedDenominator) compareWith (otherReducedNumerator * thisReducedDenominator)
    }
    // endregion
    
    // region Equality
    override fun BigLongRational.equalsTo(other: BigLongRational): Boolean =
        context(BigLong.context) { this.numerator equalsTo other.numerator } &&
                context(UBigLong.context) { this.denominator equalsTo other.denominator }
    override fun BigLongRational.isZero(): Boolean = context(BigLong.context) { this.numerator.isZero() }
    override fun BigLongRational.isOne(): Boolean =
        this.numerator.sign.isPositive() && context(UBigLong.context) { this.numerator.absoluteValue equalsTo this.denominator }
    // endregion
    
    // region Integers conversion
    public override fun valueOf(arg: Int): BigLongRational = BigLongRational(BigLong.context.valueOf(arg), UBigLong.context.one)
    public override fun valueOf(arg: UInt): BigLongRational = BigLongRational(BigLong.context.valueOf(arg), UBigLong.context.one)
    public override fun valueOf(arg: Long): BigLongRational = BigLongRational(BigLong.context.valueOf(arg), UBigLong.context.one)
    public override fun valueOf(arg: ULong): BigLongRational = BigLongRational(BigLong.context.valueOf(arg), UBigLong.context.one)
    // endregion
    
    // region BigLongRational-Int operations
    override fun BigLongRational.plus(other: Int): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator + valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.minus(other: Int): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator - valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.times(other: Int): BigLongRational = context(BigLong.context) {
        val (reducedDenominator, reducedOther) = divideByGCD(denominator, BigLong.context.valueOf(other))
        return BigLongRational(
            numerator = numerator * reducedOther,
            denominator = reducedDenominator,
        )
    }
    override fun BigLongRational.div(other: Int): BigLongRational = context(Int.context, UBigLong.context) {
        if (other == 0) divisionByZero()
        val sign = this.numerator.sign * other.sign()
        val (reducedNumerator, reducedOther) = divideByGCD(numerator.absoluteValue, UBigLong.context.valueOf(abs(other).toUInt()))
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            denominator * reducedOther,
        )
    }
    // endregion
    
    // region BigLongRational-UInt operations
    override fun BigLongRational.plus(other: UInt): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator + valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.minus(other: UInt): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator - valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.times(other: UInt): BigLongRational = context(BigLong.context) {
        val (reducedDenominator, reducedOther) = divideByGCD(denominator, UBigLong.context.valueOf(other))
        return BigLongRational(
            numerator = numerator * reducedOther,
            denominator = reducedDenominator,
        )
    }
    override fun BigLongRational.div(other: UInt): BigLongRational = context(UBigLong.context) {
        if (other == 0u) divisionByZero()
        val (reducedNumerator, reducedOther) = divideByGCD(numerator, UBigLong.context.valueOf(other))
        return BigLongRational(
            reducedNumerator,
            denominator * reducedOther,
        )
    }
    // endregion
    
    // region BigLongRational-Long operations
    override fun BigLongRational.plus(other: Long): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator + valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.minus(other: Long): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator - valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.times(other: Long): BigLongRational = context(BigLong.context) {
        val (reducedDenominator, reducedOther) = divideByGCD(denominator, BigLong.context.valueOf(other))
        return BigLongRational(
            numerator = numerator * reducedOther,
            denominator = reducedDenominator,
        )
    }
    override fun BigLongRational.div(other: Long): BigLongRational = context(Long.context, UBigLong.context) {
        if (other == 0L) divisionByZero()
        val sign = this.numerator.sign * other.sign()
        val (reducedNumerator, reducedOther) = divideByGCD(numerator.absoluteValue, UBigLong.context.valueOf(abs(other).toULong()))
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            denominator * reducedOther,
        )
    }
    // endregion
    
    // region BigLongRational-ULong operations
    override fun BigLongRational.plus(other: ULong): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator + valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.minus(other: ULong): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = numerator - valueOf(denominator) * other,
            denominator = denominator,
        )
    }
    override fun BigLongRational.times(other: ULong): BigLongRational = context(BigLong.context) {
        val (reducedDenominator, reducedOther) = divideByGCD(denominator, UBigLong.context.valueOf(other))
        return BigLongRational(
            numerator = numerator * reducedOther,
            denominator = reducedDenominator,
        )
    }
    override fun BigLongRational.div(other: ULong): BigLongRational = context(UBigLong.context) {
        if (other == 0uL) divisionByZero()
        val (reducedNumerator, reducedOther) = divideByGCD(numerator, UBigLong.context.valueOf(other))
        return BigLongRational(
            reducedNumerator,
            denominator * reducedOther,
        )
    }
    // endregion
    
    // region Int-BigLongRational operations
    override fun Int.plus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) + other.numerator,
            denominator = other.denominator,
        )
    }
    override fun Int.minus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) - other.numerator,
            denominator = other.denominator,
        )
    }
    override fun Int.times(other: BigLongRational): BigLongRational = context(BigLong.context) {
        val (reducedThis, reducedDenominator) = divideByGCD(BigLong.context.valueOf(this), other.denominator)
        return BigLongRational(
            numerator = reducedThis * other.numerator,
            denominator = reducedDenominator,
        )
    }
    override fun Int.div(other: BigLongRational): BigLongRational = context(Int.context, UBigLong.context) {
        if (other.isZero()) divisionByZero()
        val sign = this.sign() * other.numerator.sign
        val (reducedThis, reducedNumerator) = divideByGCD(UBigLong.context.valueOf(abs(this).toUInt()), other.numerator.absoluteValue)
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            other.denominator * reducedThis,
        )
    }
    // endregion
    
    // region UInt-BigLongRational operations
    override fun UInt.plus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) + other.numerator,
            denominator = other.denominator,
        )
    }
    override fun UInt.minus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) - other.numerator,
            denominator = other.denominator,
        )
    }
    override fun UInt.times(other: BigLongRational): BigLongRational = context(BigLong.context) {
        val (reducedThis, reducedDenominator) = divideByGCD(UBigLong.context.valueOf(this), other.denominator)
        return BigLongRational(
            numerator = reducedThis * other.numerator,
            denominator = reducedDenominator,
        )
    }
    override fun UInt.div(other: BigLongRational): BigLongRational = context(UBigLong.context) {
        if (other.isZero()) divisionByZero()
        val sign = other.numerator.sign
        val (reducedThis, reducedNumerator) = divideByGCD(UBigLong.context.valueOf(this), other.numerator.absoluteValue)
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            other.denominator * reducedThis,
        )
    }
    // endregion
    
    // region Long-BigLongRational operations
    override fun Long.plus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) + other.numerator,
            denominator = other.denominator,
        )
    }
    override fun Long.minus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) - other.numerator,
            denominator = other.denominator,
        )
    }
    override fun Long.times(other: BigLongRational): BigLongRational = context(BigLong.context) {
        val (reducedThis, reducedDenominator) = divideByGCD(BigLong.context.valueOf(this), other.denominator)
        return BigLongRational(
            numerator = reducedThis * other.numerator,
            denominator = reducedDenominator,
        )
    }
    override fun Long.div(other: BigLongRational): BigLongRational = context(Long.context, UBigLong.context) {
        if (other.isZero()) divisionByZero()
        val sign = this.sign() * other.numerator.sign
        val (reducedThis, reducedNumerator) = divideByGCD(UBigLong.context.valueOf(abs(this).toULong()), other.numerator.absoluteValue)
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            other.denominator * reducedThis,
        )
    }
    // endregion
    
    // region ULong-BigLongRational operations
    override fun ULong.plus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) + other.numerator,
            denominator = other.denominator,
        )
    }
    override fun ULong.minus(other: BigLongRational): BigLongRational = context(BigLong.context) {
        BigLongRational(
            numerator = this * valueOf(other.denominator) - other.numerator,
            denominator = other.denominator,
        )
    }
    override fun ULong.times(other: BigLongRational): BigLongRational = context(BigLong.context) {
        val (reducedThis, reducedDenominator) = divideByGCD(UBigLong.context.valueOf(this), other.denominator)
        return BigLongRational(
            numerator = reducedThis * other.numerator,
            denominator = reducedDenominator,
        )
    }
    override fun ULong.div(other: BigLongRational): BigLongRational = context(UBigLong.context) {
        if (other.isZero()) divisionByZero()
        val sign = other.numerator.sign
        val (reducedThis, reducedNumerator) = divideByGCD(UBigLong.context.valueOf(this), other.numerator.absoluteValue)
        return BigLongRational(
            BigLong(sign, reducedNumerator),
            other.denominator * reducedThis,
        )
    }
    // endregion
    
    // region BigLongRational-BigLongRational operations
    override fun BigLongRational.unaryMinus(): BigLongRational =
        BigLongRational(
            numerator = context(BigLong.context) { -numerator },
            denominator = denominator,
        )
    override fun BigLongRational.plus(other: BigLongRational): BigLongRational = context(UBigLong.context, BigLong.context) {
        val denominatorsGcd = gcd(denominator, other.denominator)
        val reducedThisDenominator = denominator / denominatorsGcd
        val reducedOtherDenominator = other.denominator / denominatorsGcd
        val numeratorCandidate = numerator * reducedOtherDenominator + reducedThisDenominator * other.numerator
        val (reducedNumeratorCandidate, reducedDenominatorGcd) = divideByGCD(numeratorCandidate, denominatorsGcd)
        return BigLongRational(
            numerator = reducedNumeratorCandidate,
            denominator = reducedThisDenominator * reducedOtherDenominator * reducedDenominatorGcd,
        )
    }
    override fun BigLongRational.minus(other: BigLongRational): BigLongRational = context(UBigLong.context, BigLong.context) {
        val denominatorsGcd = gcd(denominator, other.denominator)
        val reducedThisDenominator = denominator / denominatorsGcd
        val reducedOtherDenominator = other.denominator / denominatorsGcd
        val numeratorCandidate = numerator * reducedOtherDenominator - reducedThisDenominator * other.numerator
        val (reducedNumeratorCandidate, reducedDenominatorGcd) = divideByGCD(numeratorCandidate, denominatorsGcd)
        return BigLongRational(
            numerator = reducedNumeratorCandidate,
            denominator = reducedThisDenominator * reducedOtherDenominator * reducedDenominatorGcd,
        )
    }
    override fun BigLongRational.times(other: BigLongRational): BigLongRational = context(UBigLong.context, BigLong.context) {
        val (reducedThisDenominator, reducedOtherNumeratorGcd) = divideByGCD(denominator, other.numerator)
        val (reducedOtherDenominator, reducedThisNumeratorGcd) = divideByGCD(other.denominator, numerator)
        return BigLongRational(
            numerator = reducedThisNumeratorGcd * reducedOtherNumeratorGcd,
            denominator = reducedThisDenominator * reducedOtherDenominator,
        )
    }
    override fun BigLongRational.div(other: BigLongRational): BigLongRational = context(BigLong.context, UBigLong.context) {
        if (other.numerator.isZero()) divisionByZero()
        val sign = this.numerator.sign * other.numerator.sign
        val (reducedThisNumerator, reducedOtherNumerator) = divideByGCD(this.numerator.absoluteValue, other.numerator.absoluteValue)
        val (reducedThisDenominator, reducedOtherDenominator) = divideByGCD(this.denominator, other.denominator)
        return BigLongRational(
            BigLong(sign, reducedThisNumerator * reducedOtherDenominator),
            reducedThisDenominator * reducedOtherNumerator,
        )
    }
    // endregion
}

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun BigLongRationalContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val bigLongRationalSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.BigLongRational",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in BigLongRationalContext>>(
        Reification.Key(bigLongRationalSuppliedType),
        Field.Key(bigLongRationalSuppliedType),
        Order.Key(bigLongRationalSuppliedType),
        Hashing.Key(bigLongRationalSuppliedType),
    ).forEach {
        it.withSuperkeys correspondsTo BigLongRationalContext
    }
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.LongRing.compareTo
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.numberTheory.gcd
import kotlin.jvm.JvmField


@Suppress("NAME_SHADOWING")
public class Rational {
    @JvmField
    public val numerator: Long
    @JvmField
    public val denominator: Long

    internal constructor(numerator: Long, denominator: Long, toCheckInput: Boolean = true) {
        if (toCheckInput) {
            if (denominator == 0L) throw ArithmeticException("/ by zero")

            val greatestCommonDivisor = gcd(numerator, denominator).let { if (denominator < 0L) -it else it }

            this.numerator = numerator / greatestCommonDivisor
            this.denominator = denominator / greatestCommonDivisor
        } else {
            this.numerator = numerator
            this.denominator = denominator
        }
    }

    public constructor(numerator: Long, denominator: Long) {
        if (denominator == 0L) throw ArithmeticException("/ by zero")

        val greatestCommonDivider = gcd(numerator, denominator).let { if (denominator < 0L) -it else it }

        this.numerator = numerator / greatestCommonDivider
        this.denominator = denominator / greatestCommonDivider
    }
    public constructor(numerator: Long, denominator: Int) : this(numerator, denominator.toLong())
    public constructor(numerator: Int, denominator: Long) : this(numerator.toLong(), denominator)
    public constructor(numerator: Int, denominator: Int) : this(numerator.toLong(), denominator.toLong())
    public constructor(numerator: Long) {
        this.numerator = numerator
        this.denominator = 1L
    }
    public constructor(numerator: Int) : this(numerator.toLong())

    override fun equals(other: Any?): Boolean =
        if (other is Rational) numerator == other.numerator && denominator == other.denominator
        else false

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun toString(): String = if (denominator == 1L) "$numerator" else "$numerator/$denominator"

    public companion object {
        public val field: RationalField = RationalField
    }
}

public data object RationalField : Field<Rational>, Order<Rational>, Hashing<Rational> {
    // region Constants
    public override val zero: Rational = Rational(0L)
    public override val one: Rational = Rational(1L)
    // endregion

    // region Equality, comparison, and hashing
    public override infix fun Rational.equalsTo(other: Rational): Boolean = this == other
    public override fun Rational.isZero(): Boolean = numerator == 0L
    public override fun Rational.isOne(): Boolean = numerator == 1L && denominator == 1L

    public override fun Rational.compareTo(other: Rational): Int {
        val numeratorGcd = gcd(this.numerator, other.numerator)
        val denominatorGcd = gcd(this.denominator, other.denominator)

        return ((this.numerator / numeratorGcd) * (other.denominator / denominatorGcd)) compareTo ((other.numerator / numeratorGcd) * (this.denominator / denominatorGcd))
    }
    public override fun Rational.hash(): Int = numerator.toInt() xor denominator.toInt()
    // endregion

    // region Integers conversion
    public override fun valueOf(arg: Int): Rational = Rational(arg.toLong())
    public override fun valueOf(arg: Long): Rational = Rational(arg)
    // endregion
    
    // region Rational-Int operations
    public override operator fun Rational.plus(other: Int): Rational =
        Rational(
            numerator + denominator * other.toLong(),
            denominator,
            toCheckInput = false
        )
    public override operator fun Rational.minus(other: Int): Rational =
        Rational(
            numerator - denominator * other.toLong(),
            denominator,
            toCheckInput = false
        )
    @Suppress("NAME_SHADOWING")
    public override operator fun Rational.times(other: Int): Rational {
        val other = other.toLong()
        val denominatorAndOtherGcd = gcd(denominator, other)
        return Rational(
            numerator * (other / denominatorAndOtherGcd),
            denominator / denominatorAndOtherGcd,
            toCheckInput = false
        )
    }
    @Suppress("NAME_SHADOWING")
    public override operator fun Rational.div(other: Int): Rational {
        val other = other.toLong()
        val numeratorAndOtherGcd = gcd(numerator, other)
        return Rational(
            numerator / numeratorAndOtherGcd,
            denominator * (other / numeratorAndOtherGcd),
            toCheckInput = false
        )
    }
    // endregion

    // region Rational-Long operations
    public override operator fun Rational.plus(other: Long): Rational =
        Rational(
            numerator + denominator * other,
            denominator,
            toCheckInput = false
        )
    public override operator fun Rational.minus(other: Long): Rational =
        Rational(
            numerator - denominator * other,
            denominator,
            toCheckInput = false
        )
    public override operator fun Rational.times(other: Long): Rational {
        val denominatorAndOtherGcd = gcd(denominator, other)
        return Rational(
            numerator * (other / denominatorAndOtherGcd),
            denominator / denominatorAndOtherGcd,
            toCheckInput = false
        )
    }
    public override operator fun Rational.div(other: Long): Rational {
        val numeratorAndOtherGcd = gcd(numerator, other)
        return Rational(
            numerator / numeratorAndOtherGcd,
            denominator * (other / numeratorAndOtherGcd),
            toCheckInput = false
        )
    }
    // endregion

    // region Int-Rational operations
    public override operator fun Int.plus(other: Rational): Rational =
        Rational(
            other.denominator * this.toLong() + other.numerator,
            other.denominator,
            toCheckInput = false
        )
    public override operator fun Int.minus(other: Rational): Rational =
        Rational(
            other.denominator * this.toLong() - other.numerator,
            other.denominator,
            toCheckInput = false
        )
    public override operator fun Int.times(other: Rational): Rational {
        val thiz = this.toLong()
        val thisAndOtherDenominatorGcd = gcd(thiz, other.denominator)
        return Rational(
            other.numerator * (thiz / thisAndOtherDenominatorGcd),
            other.denominator / thisAndOtherDenominatorGcd,
            toCheckInput = false
        )
    }
    public override operator fun Int.div(other: Rational): Rational {
        val thiz = this.toLong()
        val thisAndOtherNumeratorGcd = gcd(other.numerator, thiz)
        return Rational(
            other.denominator * (thiz / thisAndOtherNumeratorGcd),
            other.numerator / thisAndOtherNumeratorGcd,
            toCheckInput = false
        )
    }
    // endregion

    // region Long-Rational operations
    public override operator fun Long.plus(other: Rational): Rational =
        Rational(
            other.denominator * this + other.numerator,
            other.denominator,
            toCheckInput = false
        )
    public override operator fun Long.minus(other: Rational): Rational =
        Rational(
            other.denominator * this - other.numerator,
            other.denominator,
            toCheckInput = false
        )
    public override operator fun Long.times(other: Rational): Rational {
        val thisAndOtherDenominatorGcd = gcd(this, other.denominator)
        return Rational(
            other.numerator * (this / thisAndOtherDenominatorGcd),
            other.denominator / thisAndOtherDenominatorGcd,
            toCheckInput = false
        )
    }
    public override operator fun Long.div(other: Rational): Rational {
        val thisAndOtherNumeratorGcd = gcd(other.numerator, this)
        return Rational(
            other.denominator * (this / thisAndOtherNumeratorGcd),
            other.numerator / thisAndOtherNumeratorGcd,
            toCheckInput = false
        )
    }
    // endregion

    // region Rational-Rational operations
    public override operator fun Rational.unaryMinus(): Rational = Rational(-numerator, denominator, false)
    public override operator fun Rational.plus(other: Rational): Rational {
        val denominatorsGcd = gcd(denominator, other.denominator)
        val dividedThisDenominator = denominator / denominatorsGcd
        val dividedOtherDenominator = other.denominator / denominatorsGcd
        val numeratorCandidate = numerator * dividedOtherDenominator + dividedThisDenominator * other.numerator
        val secondGcd = gcd(numeratorCandidate, denominatorsGcd)
        return Rational(
            numeratorCandidate / secondGcd,
            dividedThisDenominator * (other.denominator / secondGcd),
            toCheckInput = false
        )
    }
    public override operator fun Rational.minus(other: Rational): Rational {
        val denominatorsGcd = gcd(denominator, other.denominator)
        val dividedThisDenominator = denominator / denominatorsGcd
        val dividedOtherDenominator = other.denominator / denominatorsGcd
        val numeratorCandidate = numerator * dividedOtherDenominator - dividedThisDenominator * other.numerator
        val secondGcd = gcd(numeratorCandidate, denominatorsGcd)
        return Rational(
            numeratorCandidate / secondGcd,
            dividedThisDenominator * (other.denominator / secondGcd),
            toCheckInput = false
        )
    }
    public override operator fun Rational.times(other: Rational): Rational {
        val thisDenominatorAndOtherNumeratorGcd = gcd(denominator, other.numerator)
        val otherDenominatorAndThisNumeratorGcd = gcd(other.denominator, numerator)
        return Rational(
            (numerator / otherDenominatorAndThisNumeratorGcd) * (other.numerator / thisDenominatorAndOtherNumeratorGcd),
            (denominator / thisDenominatorAndOtherNumeratorGcd) * (other.denominator / otherDenominatorAndThisNumeratorGcd),
            toCheckInput = false
        )
    }
    public override operator fun Rational.div(other: Rational): Rational {
        val denominatorsGcd = gcd(denominator, other.denominator)
        val numeratorsGcd = gcd(numerator, other.numerator)
        return Rational(
            (numerator / numeratorsGcd) * (other.denominator / denominatorsGcd),
            (denominator / denominatorsGcd) * (other.numerator / numeratorsGcd)
        )
    }
    // endregion
}
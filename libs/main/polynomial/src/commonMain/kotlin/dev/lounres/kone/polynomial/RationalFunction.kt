/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.context
import kotlin.jvm.JvmName


public interface RationalFunction<Polynomial> {
    public val numerator: Polynomial
    public val denominator: Polynomial
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> : Field<RationalFunctionType> {
    // region Rational functions constants
    public val rationalFunctionZero: RationalFunctionType get() = zero
    public val rationalFunctionOne: RationalFunctionType get() = one
    // endregion

    // region Integer-to-Rational-Function conversion
    override fun valueOf(value: Int): RationalFunctionType
    override fun valueOf(value: UInt): RationalFunctionType
    override fun valueOf(value: Long): RationalFunctionType
    override fun valueOf(value: ULong): RationalFunctionType
    public fun rationalFunctionValueOf(value: Int): RationalFunctionType = valueOf(value)
    public fun rationalFunctionValueOf(value: UInt): RationalFunctionType = valueOf(value)
    public fun rationalFunctionValueOf(value: Long): RationalFunctionType = valueOf(value)
    public fun rationalFunctionValueOf(value: ULong): RationalFunctionType = valueOf(value)
    public val Int.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    public val UInt.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    public val Long.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    public val ULong.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    // endregion

    // region Number-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfNumber")
    public fun rationalFunctionValueOf(value: Number): RationalFunctionType
    @get:JvmName("rationalFunctionValueNumber")
    public val Number.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    // endregion

    // region Polynomial-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfPolynomial")
    public fun rationalFunctionValueOf(value: Polynomial): RationalFunctionType
    @get:JvmName("rationalFunctionValuePolynomial")
    public val Polynomial.rationalFunctionValue: RationalFunctionType get() = rationalFunctionValueOf(this)
    // endregion

    // region Number-Rational-Function operations
    @JvmName("plusNumberRational")
    public operator fun Number.plus(other: RationalFunctionType): RationalFunctionType
    @JvmName("minusNumberRational")
    public operator fun Number.minus(other: RationalFunctionType): RationalFunctionType
    @JvmName("timesNumberRational")
    public operator fun Number.times(other: RationalFunctionType): RationalFunctionType
    @JvmName("divNumberRational")
    public operator fun Number.div(other: RationalFunctionType): RationalFunctionType
    // endregion

    // region Rational-Function-Number operations
    @JvmName("plusRationalNumber")
    public operator fun RationalFunctionType.plus(other: Number): RationalFunctionType
    @JvmName("minusRationalNumber")
    public operator fun RationalFunctionType.minus(other: Number): RationalFunctionType
    @JvmName("timesRationalNumber")
    public operator fun RationalFunctionType.times(other: Number): RationalFunctionType
    @JvmName("divRationalNumber")
    public operator fun RationalFunctionType.div(other: Number): RationalFunctionType
    // endregion

    // region Polynomial-Rational-Function operations
    @JvmName("plusPolynomialRational")
    public operator fun Polynomial.plus(other: RationalFunctionType): RationalFunctionType
    @JvmName("minusPolynomialRational")
    public operator fun Polynomial.minus(other: RationalFunctionType): RationalFunctionType
    @JvmName("timesPolynomialRational")
    public operator fun Polynomial.times(other: RationalFunctionType): RationalFunctionType
    @JvmName("divPolynomialRational")
    public operator fun Polynomial.div(other: RationalFunctionType): RationalFunctionType
    // endregion

    // region Rational-Function-Polynomial operations
    @JvmName("plusRationalPolynomial")
    public operator fun RationalFunctionType.plus(other: Polynomial): RationalFunctionType
    @JvmName("minusRationalPolynomial")
    public operator fun RationalFunctionType.minus(other: Polynomial): RationalFunctionType
    @JvmName("timesRationalPolynomial")
    public operator fun RationalFunctionType.times(other: Polynomial): RationalFunctionType
    @JvmName("divRationalPolynomial")
    public operator fun RationalFunctionType.div(other: Polynomial): RationalFunctionType
    // endregion

    // region Polynomial operations
    public operator fun Polynomial.div(other: Polynomial): RationalFunctionType
    // endregion

    // region Rational Function properties
    public val RationalFunctionType.numeratorDegree: UInt
    public val RationalFunctionType.denominatorDegree: UInt
    // endregion
}

// region Rational functions constants
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionZero: RationalFunctionType
    get() = with(rationalFunctionSpace) { rationalFunctionZero }
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionOne: RationalFunctionType
    get() = with(rationalFunctionSpace) { rationalFunctionOne }
// endregion

// region Integer-to-Rational-Function conversion
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: Int): RationalFunctionType = rationalFunctionSpace.rationalFunctionValueOf(value)
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: UInt): RationalFunctionType = rationalFunctionSpace.rationalFunctionValueOf(value)
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: Long): RationalFunctionType = rationalFunctionSpace.rationalFunctionValueOf(value)
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: ULong): RationalFunctionType = rationalFunctionSpace.rationalFunctionValueOf(value)
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Int.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> UInt.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Long.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> ULong.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
// endregion

// region Number-to-Rational-Function conversion
@JvmName("rationalFunctionValueOfNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: Number): RationalFunctionType = with(rationalFunctionSpace) { rationalFunctionValueOf(value) }
@get:JvmName("rationalFunctionValueNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public val <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Number.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
// endregion

// region Polynomial-to-Rational-Function conversion
@JvmName("rationalFunctionValueOfPolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(value: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { rationalFunctionValueOf(value) }
@get:JvmName("rationalFunctionValuePolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
// endregion

// region Number-Rational-Function operations
@JvmName("plusNumberRational")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Number.plus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusNumberRational")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Number.minus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesNumberRational")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Number.times(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divNumberRational")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Number.div(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Rational-Function-Number operations
@JvmName("plusRationalNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.plus(other: Number): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusRationalNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.minus(other: Number): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesRationalNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.times(other: Number): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divRationalNumber")
context(rationalFunctionSpace: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>)
public operator fun <Number, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.div(other: Number): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Polynomial-Rational-Function operations
@JvmName("plusPolynomialRational")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.plus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusPolynomialRational")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.minus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesPolynomialRational")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.times(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divPolynomialRational")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.div(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Rational-Function-Polynomial operations
@JvmName("plusRationalPolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.plus(other: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusRationalPolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.minus(other: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesRationalPolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.times(other: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divRationalPolynomial")
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.div(other: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Polynomial operations
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public operator fun <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Polynomial.div(other: Polynomial): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Rational Function properties
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.numeratorDegree: UInt get() = with(rationalFunctionSpace) { this@numeratorDegree.numeratorDegree }
context(rationalFunctionSpace: RationalFunctionSpace<*, Polynomial, RationalFunctionType>)
public val <Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.denominatorDegree: UInt get() = with(rationalFunctionSpace) { this@denominatorDegree.denominatorDegree }
// endregion

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        Number,
        Polynomial,
        RationalFunctionType: RationalFunction<Polynomial>,
> : RationalFunctionSpace<Number, Polynomial, RationalFunctionType> {
    protected abstract val polynomialSpace: PolynomialSpace<Number, Polynomial>
    protected abstract fun constructRationalFunction(numerator: Polynomial, denominator: Polynomial = polynomialSpace.one) : RationalFunctionType
    
    // region Equality
    override fun RationalFunctionType.equalsTo(other: RationalFunctionType): Boolean =
        context(polynomialSpace) { this.numerator * other.denominator equalsTo this.denominator * other.numerator }
    override fun RationalFunctionType.isZero(): Boolean = context(polynomialSpace) { numerator.isZero() }
    override fun RationalFunctionType.isOne(): Boolean = context(polynomialSpace) { numerator equalsTo denominator }
    // endregion

    // region Rational Function constants
    final override val zero: RationalFunctionType by lazy { constructRationalFunction(polynomialSpace.zero) }
    final override val one: RationalFunctionType by lazy { constructRationalFunction(polynomialSpace.one) }
    // endregion

    // region Integer-to-Rational-Function conversion
    final override fun valueOf(value: Int): RationalFunctionType = constructRationalFunction(polynomialSpace.valueOf(value))
    final override fun valueOf(value: UInt): RationalFunctionType = constructRationalFunction(polynomialSpace.valueOf(value))
    final override fun valueOf(value: Long): RationalFunctionType = constructRationalFunction(polynomialSpace.valueOf(value))
    final override fun valueOf(value: ULong): RationalFunctionType = constructRationalFunction(polynomialSpace.valueOf(value))
    // endregion

    // region Number-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfNumber")
    final override fun rationalFunctionValueOf(value: Number): RationalFunctionType = constructRationalFunction(polynomialSpace.polynomialValueOf(value))
    // endregion

    // region Polynomial-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfPolynomial")
    final override fun rationalFunctionValueOf(value: Polynomial): RationalFunctionType = constructRationalFunction(value)
    // endregion

    // region Rational-Function-Int operations
    final override operator fun RationalFunctionType.plus(other: Int): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.minus(other: Int): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.times(other: Int): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    final override operator fun RationalFunctionType.div(other: Int): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion
    
    // region Rational-Function-UInt operations
    final override operator fun RationalFunctionType.plus(other: UInt): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.minus(other: UInt): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.times(other: UInt): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    final override operator fun RationalFunctionType.div(other: UInt): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion

    // region Rational-Function-Long operations
    final override operator fun RationalFunctionType.plus(other: Long): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.minus(other: Long): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.times(other: Long): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    final override operator fun RationalFunctionType.div(other: Long): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion
    
    // region Rational-Function-Long operations
    final override operator fun RationalFunctionType.plus(other: ULong): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.minus(other: ULong): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    final override operator fun RationalFunctionType.times(other: ULong): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    final override operator fun RationalFunctionType.div(other: ULong): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion

    // region Int-Rational-Function operations
    final override operator fun Int.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this@plus + other.numerator },
            other.denominator
        )
    final override operator fun Int.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this@minus - other.numerator },
            other.denominator
        )
    final override operator fun Int.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this@times * other.numerator },
            other.denominator
        )
    final override operator fun Int.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this@div * other.denominator },
            other.numerator
        )
    // endregion
    
    // region Int-Rational-Function operations
    final override operator fun UInt.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this@plus + other.numerator },
            other.denominator
        )
    final override operator fun UInt.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this@minus - other.numerator },
            other.denominator
        )
    final override operator fun UInt.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this@times * other.numerator },
            other.denominator
        )
    final override operator fun UInt.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this@div * other.denominator },
            other.numerator
        )
    // endregion

    // region Long-Rational-Function operations
    final override operator fun Long.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this + other.numerator },
            other.denominator
        )
    final override operator fun Long.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this - other.numerator },
            other.denominator
        )
    final override operator fun Long.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.numerator },
            other.denominator
        )
    final override operator fun Long.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.denominator },
            other.numerator
        )
    // endregion
    
    // region Long-Rational-Function operations
    final override operator fun ULong.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this + other.numerator },
            other.denominator
        )
    final override operator fun ULong.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this - other.numerator },
            other.denominator
        )
    final override operator fun ULong.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.numerator },
            other.denominator
        )
    final override operator fun ULong.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Number-Rational-Function operations
    @JvmName("plusNumberRational")
    final override operator fun Number.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this + other.numerator },
            other.denominator
        )
    @JvmName("minusNumberRational")
    final override operator fun Number.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this - other.numerator },
            other.denominator
        )
    @JvmName("timesNumberRational")
    final override operator fun Number.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@times * other.numerator },
            other.denominator
        )
    @JvmName("divNumberRational")
    final override operator fun Number.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@div * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Number operations
    @JvmName("plusRationalNumber")
    final override operator fun RationalFunctionType.plus(other: Number): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    @JvmName("minusRationalNumber")
    final override operator fun RationalFunctionType.minus(other: Number): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    @JvmName("timesRationalNumber")
    final override operator fun RationalFunctionType.times(other: Number): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    @JvmName("divRationalNumber")
    final override operator fun RationalFunctionType.div(other: Number): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion

    // region Polynomial-Polynomial operations
    @JvmName("divPolynomialPolynomial")
    final override operator fun Polynomial.div(other: Polynomial): RationalFunctionType = constructRationalFunction(this, other)
    // endregion

    // region Polynomial-Rational-Function operations
    @JvmName("plusPolynomialRational")
    final override operator fun Polynomial.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this + other.numerator },
            other.denominator
        )
    @JvmName("minusPolynomialRational")
    final override operator fun Polynomial.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { other.denominator * this - other.numerator },
            other.denominator
        )
    @JvmName("timesPolynomialRational")
    final override operator fun Polynomial.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.numerator },
            other.denominator
        )
    @JvmName("divPolynomialRational")
    final override operator fun Polynomial.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Polynomial operations
    @JvmName("plusRationalPolynomial")
    final override operator fun RationalFunctionType.plus(other: Polynomial): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    @JvmName("minusRationalPolynomial")
    final override operator fun RationalFunctionType.minus(other: Polynomial): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    @JvmName("timesRationalPolynomial")
    final override operator fun RationalFunctionType.times(other: Polynomial): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other },
            denominator
        )
    @JvmName("divRationalPolynomial")
    final override operator fun RationalFunctionType.div(other: Polynomial): RationalFunctionType =
        constructRationalFunction(
            numerator,
            context(polynomialSpace) { denominator * other }
        )
    // endregion

    // region Rational-Function-Rational-Function operations
    final override operator fun RationalFunctionType.unaryMinus(): RationalFunctionType = context(polynomialSpace) { constructRationalFunction(-numerator, denominator) }
    final override operator fun RationalFunctionType.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other.denominator + denominator * other.numerator },
            context(polynomialSpace) { denominator * other.denominator }
        )
    final override operator fun RationalFunctionType.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other.denominator - denominator * other.numerator },
            context(polynomialSpace) { denominator * other.denominator }
        )
    final override operator fun RationalFunctionType.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other.numerator },
            context(polynomialSpace) { denominator * other.denominator }
        )
    final override operator fun RationalFunctionType.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            context(polynomialSpace) { numerator * other.denominator },
            context(polynomialSpace) { denominator * other.numerator }
        )
    final override fun power(base: RationalFunctionType, exponent: UInt): RationalFunctionType =
        constructRationalFunction(
            polynomialSpace.power(base.numerator, exponent),
            polynomialSpace.power(base.denominator, exponent),
        )
    final override fun power(base: RationalFunctionType, exponent: ULong): RationalFunctionType =
        constructRationalFunction(
            polynomialSpace.power(base.numerator, exponent),
            polynomialSpace.power(base.denominator, exponent),
        )
    // endregion
    
    // region Rational Function properties
    final override val RationalFunctionType.numeratorDegree: UInt get() = with(polynomialSpace) { this@numeratorDegree.numerator.degree }
    final override val RationalFunctionType.denominatorDegree: UInt get() = with(polynomialSpace) { this@denominatorDegree.denominator.degree }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
    Number,
    Variable,
    Polynomial,
    RationalFunctionType: RationalFunction<Polynomial>,
> : RationalFunctionSpace<Number, Polynomial, RationalFunctionType> {
    // region Variable-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfVariable")
    public fun rationalFunctionValueOf(variable: Variable): RationalFunctionType
    @get:JvmName("rationalFunctionValueVariable")
    public val Variable.rationalFunctionValue: RationalFunctionType
    // endregion

    // region Variable-Rational-Function operations
    @JvmName("plusVariableRational")
    public operator fun Variable.plus(other: RationalFunctionType): RationalFunctionType
    @JvmName("minusVariableRational")
    public operator fun Variable.minus(other: RationalFunctionType): RationalFunctionType
    @JvmName("timesVariableRational")
    public operator fun Variable.times(other: RationalFunctionType): RationalFunctionType
    @JvmName("divVariableRational")
    public operator fun Variable.div(other: RationalFunctionType): RationalFunctionType
    // endregion

    // region Rational-Function-Variable operations
    @JvmName("plusRationalVariable")
    public operator fun RationalFunctionType.plus(other: Variable): RationalFunctionType
    @JvmName("minusRationalVariable")
    public operator fun RationalFunctionType.minus(other: Variable): RationalFunctionType
    @JvmName("timesRationalVariable")
    public operator fun RationalFunctionType.times(other: Variable): RationalFunctionType
    @JvmName("divRationalVariable")
    public operator fun RationalFunctionType.div(other: Variable): RationalFunctionType
    // endregion

    // region Rational Function properties
    public val RationalFunctionType.variables: KoneSet<Variable>
    public val RationalFunctionType.numberOfVariables: UInt get() = variables.size
    // endregion
}

// region Variable-to-Rational-Function conversion
@JvmName("rationalFunctionValueOfVariable")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> rationalFunctionValueOf(variable: Variable): RationalFunctionType = rationalFunctionSpace.rationalFunctionValueOf(variable)
//context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
//@get:JvmName("rationalFunctionValueVariable")
//public val <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Variable.rationalFunctionValue: RationalFunctionType get() = with(rationalFunctionSpace) { this@rationalFunctionValue.rationalFunctionValue }
// endregion

// region Variable-Rational-Function operations
@JvmName("plusVariableRational")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Variable.plus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusVariableRational")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Variable.minus(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesVariableRational")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Variable.times(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divVariableRational")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> Variable.div(other: RationalFunctionType): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Rational-Function-Variable operations
@JvmName("plusRationalVariable")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.plus(other: Variable): RationalFunctionType = with(rationalFunctionSpace) { this@plus + other }
@JvmName("minusRationalVariable")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.minus(other: Variable): RationalFunctionType = with(rationalFunctionSpace) { this@minus - other }
@JvmName("timesRationalVariable")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.times(other: Variable): RationalFunctionType = with(rationalFunctionSpace) { this@times * other }
@JvmName("divRationalVariable")
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public operator fun <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.div(other: Variable): RationalFunctionType = with(rationalFunctionSpace) { this@div / other }
// endregion

// region Rational Function properties
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public val <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.variables: KoneSet<Variable> get() = with(rationalFunctionSpace) { this@variables.variables }
context(rationalFunctionSpace: MultivariateRationalFunctionSpace<*, Variable, Polynomial, RationalFunctionType>)
public val <Variable, Polynomial, RationalFunctionType: RationalFunction<Polynomial>> RationalFunctionType.numberOfVariables: UInt get() = with(rationalFunctionSpace) { this@numberOfVariables.numberOfVariables }
// endregion

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
    Number,
    Variable,
    Polynomial,
    RationalFunctionType: RationalFunction<Polynomial>,
> : MultivariateRationalFunctionSpace<Number, Variable, Polynomial, RationalFunctionType>,  PolynomialSpaceOfFractions<Number, Polynomial, RationalFunctionType>() {
    abstract override val polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>
    
    // region Variable-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfVariable")
    final override fun rationalFunctionValueOf(variable: Variable): RationalFunctionType = constructRationalFunction(polynomialSpace.polynomialValueOf(variable))
    @get:JvmName("rationalFunctionValueVariable")
    final override val Variable.rationalFunctionValue: RationalFunctionType get() = constructRationalFunction(with(polynomialSpace) { this@rationalFunctionValue.polynomialValue })
    // endregion

    // region Variable-Rational-Function operations
    // FIXME: KT-79139
    @JvmName("plusVariableRational")
    final override operator fun Variable.plus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@plus * other.denominator + other.numerator },
            other.denominator
        )
    // FIXME: KT-79139
    @JvmName("minusVariableRational")
    final override operator fun Variable.minus(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@minus * other.denominator - other.numerator },
            other.denominator
        )
    // FIXME: KT-79139
    @JvmName("timesVariableRational")
    final override operator fun Variable.times(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@times * other.numerator },
            other.denominator
        )
    // FIXME: KT-79139
    @JvmName("divVariableRational")
    final override operator fun Variable.div(other: RationalFunctionType): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { this@div * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Variable operations
    // FIXME: KT-79139
    @JvmName("plusRationalVariable")
    final override operator fun RationalFunctionType.plus(other: Variable): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { numerator + denominator * other },
            denominator
        )
    // FIXME: KT-79139
    @JvmName("minusRationalVariable")
    final override operator fun RationalFunctionType.minus(other: Variable): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { numerator - denominator * other },
            denominator
        )
    // FIXME: KT-79139
    @JvmName("timesRationalVariable")
    final override operator fun RationalFunctionType.times(other: Variable): RationalFunctionType =
        constructRationalFunction(
            with(polynomialSpace) { numerator * other },
            denominator
        )
    // FIXME: KT-79139
    @JvmName("divRationalVariable")
    final override operator fun RationalFunctionType.div(other: Variable): RationalFunctionType =
        constructRationalFunction(
            numerator,
            with(polynomialSpace) { denominator * other }
        )
    // endregion
    
    // region Rational Function properties
    final override val RationalFunctionType.variables: KoneSet<Variable>
        get() = context(polynomialSpace) {
            KoneSet.build {
                +numerator.variables
                +denominator.variables
            }
        }
    // endregion
}
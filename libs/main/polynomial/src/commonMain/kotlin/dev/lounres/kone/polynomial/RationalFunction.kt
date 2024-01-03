/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import kotlin.js.JsName
import kotlin.jvm.JvmName


public interface RationalFunction<N, P> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<N, P, RF: RationalFunction<N, P>, out A: Ring<N>, out PS: PolynomialSpace<N, P, A>> : Field<RF> {
    // region Context accessors
    public val polynomialSpace: PS
    public val numericalRing: A get() = polynomialSpace.numericalRing
    // endregion

    // region Rational functions constants
    public val rationalFunctionZero: RF get() = zero
    public val rationalFunctionOne: RF get() = one
    // endregion

    // region Equality
    public override infix fun RF.equalsTo(other: RF): Boolean = polynomialSpace { numerator * other.denominator equalsTo denominator * other.numerator }
    public override infix fun RF.eq(other: RF): Boolean = this equalsTo other
    public override fun RF.isZero(): Boolean = polynomialSpace { numerator equalsTo polynomialZero }
    public override fun RF.isOne(): Boolean = polynomialSpace { numerator equalsTo denominator }
    // endregion

    // region Integer-to-Rational-Function conversion
    public override fun valueOf(value: Int): RF = polynomialSpace { rationalFunctionValueOf(polynomialValueOf(value)) }
    public override fun valueOf(value: Long): RF = polynomialSpace { rationalFunctionValueOf(polynomialValueOf(value)) }
    public fun rationalFunctionValueOf(value: Int): RF = valueOf(value)
    public fun rationalFunctionValueOf(value: Long): RF = valueOf(value)
    public val Int.rationalFunctionValue: RF get() = valueOf(this)
    public val Long.rationalFunctionValue: RF get() = valueOf(this)
    // endregion

    // region Number-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfNumber")
    public fun rationalFunctionValueOf(value: N): RF = polynomialSpace { rationalFunctionValueOf(polynomialValueOf(value)) }
    @get:JvmName("rationalFunctionValueNumber")
    public val N.rationalFunctionValue: RF get() = rationalFunctionValueOf(this)
    // endregion

    // region Polynomial-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfPolynomial")
    public fun rationalFunctionValueOf(value: P): RF = one * value
    @get:JvmName("rationalFunctionValuePolynomial")
    public val P.rationalFunctionValue: RF get() = rationalFunctionValueOf(this)
    // endregion

    // region Number-Rational-Function operations
    @JvmName("plusNumberRational")
    public operator fun N.plus(other: RF): RF
    @JvmName("minusNumberRational")
    public operator fun N.minus(other: RF): RF
    @JvmName("timesNumberRational")
    public operator fun N.times(other: RF): RF
    @JvmName("divNumberRational")
    public operator fun N.div(other: RF): RF
    // endregion

    // region Rational-Function-Number operations
    @JvmName("plusRationalNumber")
    public operator fun RF.plus(other: N): RF
    @JvmName("minusRationalNumber")
    public operator fun RF.minus(other: N): RF
    @JvmName("timesRationalNumber")
    public operator fun RF.times(other: N): RF
    @JvmName("divRationalNumber")
    public operator fun RF.div(other: N): RF
    // endregion

    // region Polynomial-Rational-Function operations
    @JvmName("plusPolynomialRational")
    public operator fun P.plus(other: RF): RF
    @JvmName("minusPolynomialRational")
    public operator fun P.minus(other: RF): RF
    @JvmName("timesPolynomialRational")
    public operator fun P.times(other: RF): RF
    @JvmName("divPolynomialRational")
    public operator fun P.div(other: RF): RF
    // endregion

    // region Rational-Function-Polynomial operations
    @JvmName("plusRationalPolynomial")
    public operator fun RF.plus(other: P): RF
    @JvmName("minusRationalPolynomial")
    public operator fun RF.minus(other: P): RF
    @JvmName("timesRationalPolynomial")
    public operator fun RF.times(other: P): RF
    @JvmName("divRationalPolynomial")
    public operator fun RF.div(other: P): RF
    // endregion

    // region Polynomial operations
    public operator fun P.div(other: P): RF
    // endregion

    // region Rational-Function-Rational-Function operations
    public override operator fun RF.unaryPlus(): RF = this
    public override operator fun RF.unaryMinus(): RF
    public override operator fun RF.plus(other: RF): RF
    public override operator fun RF.minus(other: RF): RF
    public override operator fun RF.times(other: RF): RF
    public override operator fun RF.div(other: RF): RF
    // endregion

    // region Rational Function properties
    public val RF.numeratorDegree: Int get() = polynomialSpace { numerator.degree }
    public val RF.denominatorDegree: Int get() = polynomialSpace { denominator.degree }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        N,
        P,
        RF: RationalFunction<N, P>,
        out A: Ring<N>,
        out PS: PolynomialSpace<N, P, A>
        > : RationalFunctionSpace<N, P, RF, A, PS> {

    protected abstract fun constructRationalFunction(numerator: P, denominator: P = polynomialSpace.polynomialOne) : RF

    // region Rational Function constants
    public override val zero: RF by lazy { constructRationalFunction(polynomialSpace.polynomialZero) }
    public override val one: RF by lazy { constructRationalFunction(polynomialSpace.polynomialOne) }
    // endregion

    // region Integer-to-Rational-Function conversion
    public override fun valueOf(value: Int): RF = constructRationalFunction(polynomialSpace.polynomialValueOf(value))
    public override fun valueOf(value: Long): RF = constructRationalFunction(polynomialSpace.polynomialValueOf(value))
    // endregion

    // region Number-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfNumber")
    public override fun rationalFunctionValueOf(value: N): RF = constructRationalFunction(polynomialSpace.polynomialValueOf(value))
    // endregion

    // region Polynomial-to-Rational-Function conversion
    @JvmName("rationalFunctionValueOfPolynomial")
    public override fun rationalFunctionValueOf(value: P): RF = constructRationalFunction(value)
    // endregion

    // region Rational-Function-Int operations
    public override operator fun RF.plus(other: Int): RF =
        constructRationalFunction(
            polynomialSpace { numerator + denominator * other },
            denominator
        )
    public override operator fun RF.minus(other: Int): RF =
        constructRationalFunction(
            polynomialSpace { numerator - denominator * other },
            denominator
        )
    public override operator fun RF.times(other: Int): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other },
            denominator
        )
    public override operator fun RF.div(other: Int): RF =
        constructRationalFunction(
            numerator,
            polynomialSpace { denominator * other }
        )
    // endregion

    // region Rational-Function-Long operations
    public override operator fun RF.plus(other: Long): RF =
        constructRationalFunction(
            polynomialSpace { numerator + denominator * other },
            denominator
        )
    public override operator fun RF.minus(other: Long): RF =
        constructRationalFunction(
            polynomialSpace { numerator - denominator * other },
            denominator
        )
    public override operator fun RF.times(other: Long): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other },
            denominator
        )
    public override operator fun RF.div(other: Long): RF =
        constructRationalFunction(
            numerator,
            polynomialSpace { denominator * other }
        )
    // endregion

    // region Int-Rational-Function operations
    public override operator fun Int.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this + other.numerator },
            other.denominator
        )
    public override operator fun Int.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this - other.numerator },
            other.denominator
        )
    public override operator fun Int.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.numerator },
            other.denominator
        )
    public override operator fun Int.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Long-Rational-Function operations
    public override operator fun Long.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this + other.numerator },
            other.denominator
        )
    public override operator fun Long.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this - other.numerator },
            other.denominator
        )
    public override operator fun Long.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.numerator },
            other.denominator
        )
    public override operator fun Long.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Number-Rational-Function operations
    @JvmName("plusNumberRational")
    public override operator fun N.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this + other.numerator },
            other.denominator
        )
    @JvmName("minusNumberRational")
    public override operator fun N.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this - other.numerator },
            other.denominator
        )
    @JvmName("timesNumberRational")
    public override operator fun N.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.numerator },
            other.denominator
        )
    @JvmName("divNumberRational")
    public override operator fun N.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Number operations
    @JvmName("plusRationalNumber")
    public override operator fun RF.plus(other: N): RF =
        constructRationalFunction(
            polynomialSpace { numerator + denominator * other },
            denominator
        )
    @JvmName("minusRationalNumber")
    public override operator fun RF.minus(other: N): RF =
        constructRationalFunction(
            polynomialSpace { numerator - denominator * other },
            denominator
        )
    @JvmName("timesRationalNumber")
    public override operator fun RF.times(other: N): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other },
            denominator
        )
    @JvmName("divRationalNumber")
    public override operator fun RF.div(other: N): RF =
        constructRationalFunction(
            numerator,
            polynomialSpace { denominator * other }
        )
    // endregion

    // region Polynomial-Polynomial operations
    @JvmName("divPolynomialPolynomial")
    public override operator fun P.div(other: P): RF = constructRationalFunction(this, other)
    // endregion

    // region Polynomial-Rational-Function operations
    @JvmName("plusPolynomialRational")
    public override operator fun P.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this + other.numerator },
            other.denominator
        )
    @JvmName("minusPolynomialRational")
    public override operator fun P.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { other.denominator * this - other.numerator },
            other.denominator
        )
    @JvmName("timesPolynomialRational")
    public override operator fun P.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.numerator },
            other.denominator
        )
    @JvmName("divPolynomialRational")
    public override operator fun P.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Polynomial operations
    @JvmName("plusRationalPolynomial")
    public override operator fun RF.plus(other: P): RF =
        constructRationalFunction(
            polynomialSpace { numerator + denominator * other },
            denominator
        )
    @JvmName("minusRationalPolynomial")
    public override operator fun RF.minus(other: P): RF =
        constructRationalFunction(
            polynomialSpace { numerator - denominator * other },
            denominator
        )
    @JvmName("timesRationalPolynomial")
    public override operator fun RF.times(other: P): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other },
            denominator
        )
    @JvmName("divRationalPolynomial")
    public override operator fun RF.div(other: P): RF =
        constructRationalFunction(
            numerator,
            polynomialSpace { denominator * other }
        )
    // endregion

    // region Rational-Function-Rational-Function operations
    public override operator fun RF.unaryMinus(): RF = constructRationalFunction(polynomialSpace { -numerator }, denominator)
    public override operator fun RF.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other.denominator + denominator * other.numerator },
            polynomialSpace { denominator * other.denominator }
        )
    public override operator fun RF.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other.denominator - denominator * other.numerator },
            polynomialSpace { denominator * other.denominator }
        )
    public override operator fun RF.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other.numerator },
            polynomialSpace { denominator * other.denominator }
        )
    public override operator fun RF.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other.denominator },
            polynomialSpace { denominator * other.numerator }
        )
    public override fun power(base: RF, exponent: UInt): RF =
        constructRationalFunction(
            polynomialSpace.power(base.numerator, exponent),
            polynomialSpace.power(base.denominator, exponent),
        )
    public override fun power(base: RF, exponent: ULong): RF =
        constructRationalFunction(
            polynomialSpace.power(base.numerator, exponent),
            polynomialSpace.power(base.denominator, exponent),
        )
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
        N,
        V,
        P,
        RF: RationalFunction<N, P>,
        out A: Ring<N>,
        out PS: MultivariatePolynomialSpace<N, V, P, A>
        > : RationalFunctionSpace<N, P, RF, A, PS> {

    // region Variable-to-Polynomial conversion
    @JvmName("polynomialValueOfVariable")
    public fun polynomialValueOf(variable: V): P = polynomialSpace.run { polynomialValueOf(variable) }
    @get:JvmName("polynomialValueVariable")
    public val V.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Variable-to-Rational-Function conversion
    @JvmName("valueOfVariable")
    public fun valueOf(variable: V): RF = rationalFunctionValueOf(polynomialValueOf(variable))
    @get:JvmName("valueVariable")
    public val V.value: RF get() = valueOf(this)
    // endregion

    // region Variable-Rational-Function operations
    @JvmName("plusVariableRational")
    public operator fun V.plus(other: RF): RF
    @JvmName("minusVariableRational")
    public operator fun V.minus(other: RF): RF
    @JvmName("timesVariableRational")
    public operator fun V.times(other: RF): RF
    @JvmName("divVariableRational")
    public operator fun V.div(other: RF): RF
    // endregion

    // region Rational-Function-Variable operations
    @JvmName("plusRationalVariable")
    public operator fun RF.plus(other: V): RF
    @JvmName("minusRationalVariable")
    public operator fun RF.minus(other: V): RF
    @JvmName("timesRationalVariable")
    public operator fun RF.times(other: V): RF
    @JvmName("divRationalVariable")
    public operator fun RF.div(other: V): RF
    // endregion

    // region Rational Function properties
    public val RF.variables: Set<V> get() = polynomialSpace { numerator.variables union denominator.variables }
    public val RF.countOfVariables: Int get() = variables.size
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
        N,
        V,
        P,
        RF: RationalFunction<N, P>,
        out A: Ring<N>,
        out PS: MultivariatePolynomialSpace<N, V, P, A>
        > : MultivariateRationalFunctionSpace<N, V, P, RF, A, PS>,  PolynomialSpaceOfFractions<N, P, RF, A, PS>() {

    // region Variable-Rational-Function operations
    @JvmName("plusVariableRational")
    public override operator fun V.plus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator + other.numerator },
            other.denominator
        )
    @JvmName("minusVariableRational")
    public override operator fun V.minus(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator - other.numerator },
            other.denominator
        )
    @JvmName("timesVariableRational")
    public override operator fun V.times(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.numerator },
            other.denominator
        )
    @JvmName("divVariableRational")
    public override operator fun V.div(other: RF): RF =
        constructRationalFunction(
            polynomialSpace { this * other.denominator },
            other.numerator
        )
    // endregion

    // region Rational-Function-Variable operations
    @JvmName("plusRationalVariable")
    public override operator fun RF.plus(other: V): RF =
        constructRationalFunction(
            polynomialSpace { numerator + denominator * other },
            denominator
        )
    @JvmName("minusRationalVariable")
    public override operator fun RF.minus(other: V): RF =
        constructRationalFunction(
            polynomialSpace { numerator - denominator * other },
            denominator
        )
    @JvmName("timesRationalVariable")
    public override operator fun RF.times(other: V): RF =
        constructRationalFunction(
            polynomialSpace { numerator * other },
            denominator
        )
    @JvmName("divRationalVariable")
    public override operator fun RF.div(other: V): RF =
        constructRationalFunction(
            numerator,
            polynomialSpace { denominator * other }
        )
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverField<
        N,
        P,
        RF: RationalFunction<N, P>,
        out A: Field<N>,
        out PS: PolynomialSpaceOverField<N, P, A>
        > : RationalFunctionSpace<N, P, RF, A, PS> {

    // region Polynomial-Number operations
    @JvmName("divPolynomialNumber")
    @JsName("divPolynomialNumber")
    public operator fun P.div(other: N): P = polynomialSpace.run { this@div / other }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceOverField<
        N,
        V,
        P,
        RF: RationalFunction<N, P>,
        out A: Field<N>,
        out PS: MultivariatePolynomialSpaceOverField<N, V, P, A>
        > : RationalFunctionSpaceOverField<N, P, RF, A, PS>, MultivariateRationalFunctionSpace<N, V, P, RF, A, PS>
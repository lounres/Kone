/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.FieldOperations
import dev.lounres.kone.algebraic.RingOperations
import kotlin.js.JsName
import kotlin.jvm.JvmName


public interface RationalFunction<C, P: Polynomial<C>> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<C, P: Polynomial<C>, RF: RationalFunction<C, P>, out A: RingOperations<C>, out PS: PolynomialSpace<C, P, A>> : FieldOperations<RF> {
    // region Context accessors
    public val ring: A get() = this@A
    public val polynomialSpace: PS get() = this@PS
    // endregion

    // region Rational functions constants
    public val rationalFunctionZero: RF get() = zero
    public val rationalFunctionOne: RF get() = one
    // endregion

    // region Equality
    public override infix fun RF.equalsTo(other: RF): Boolean = numerator * other.denominator equalsTo denominator * other.numerator
    public override infix fun RF.eq(other: RF): Boolean = this equalsTo other
    public override fun RF.isZero(): Boolean = numerator equalsTo polynomialZero
    public override fun RF.isOne(): Boolean = numerator equalsTo denominator
    // endregion

    // region Integer-to-Polynomial conversion
    public fun polynomialValueOf(value: Int): P = polynomialSpace.run { valueOf(value) }
    public fun polynomialValueOf(value: Long): P = polynomialSpace.run { valueOf(value) }
    public val Int.polynomialValue: P get() = polynomialValueOf(this)
    public val Long.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Integer-to-Rational-Function conversion
    public override fun valueOf(value: Int): RF = rationalFunctionValueOf(polynomialValueOf(value))
    public override fun valueOf(value: Long): RF = rationalFunctionValueOf(polynomialValueOf(value))
    public fun rationalFunctionValueOf(value: Int): RF = rationalFunctionValueOf(polynomialValueOf(value))
    public fun rationalFunctionValueOf(value: Long): RF = rationalFunctionValueOf(polynomialValueOf(value))
    public val Int.rationalFunctionValue: RF get() = valueOf(this)
    public val Long.rationalFunctionValue: RF get() = valueOf(this)
    // endregion

    // region Constant-to-Polynomial conversion
    public fun polynomialValueOf(value: C): P = polynomialOne * value
    public val C.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Constant-to-Rational-Function conversion
    public fun rationalFunctionValueOf(value: C): RF = rationalFunctionValueOf(polynomialValueOf(value))
    public val C.rationalFunctionValue: RF get() = rationalFunctionValueOf(this)
    // endregion

    // region Polynomial-to-Rational-Function conversion
    public fun rationalFunctionValueOf(value: P): RF = one * value
    public val P.rationalFunctionValue: RF get() = rationalFunctionValueOf(this)
    // endregion

    // region Constant-Rational-Function operations
    @JvmName("plusConstantRational")
    public operator fun C.plus(other: RF): RF
    @JvmName("minusConstantRational")
    public operator fun C.minus(other: RF): RF
    @JvmName("timesConstantRational")
    public operator fun C.times(other: RF): RF
    @JvmName("divConstantRational")
    public operator fun C.div(other: RF): RF
    // endregion

    // region Rational-Function-Constant operations
    @JvmName("plusRationalConstant")
    public operator fun RF.plus(other: C): RF
    @JvmName("minusRationalConstant")
    public operator fun RF.minus(other: C): RF
    @JvmName("timesRationalConstant")
    public operator fun RF.times(other: C): RF
    @JvmName("divRationalConstant")
    public operator fun RF.div(other: C): RF
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
    public val RF.numeratorDegree: Int get() = numerator.degree
    public val RF.denominatorDegree: Int get() = denominator.degree
    // endregion
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out A: RingOperations<C>,
        out PS: PolynomialSpace<C, P, A>
        > : RationalFunctionSpace<C, P, RF, A, PS> {

    protected abstract fun constructRationalFunction(numerator: P, denominator: P = polynomialOne) : RF

    // region Rational Function constants
    public override val zero: RF by lazy { constructRationalFunction(polynomialZero) }
    public override val one: RF by lazy { constructRationalFunction(polynomialOne) }
    // endregion

    // region Integer-to-Rational-Function conversion
    public override fun valueOf(value: Int): RF = constructRationalFunction(polynomialValueOf(value))
    public override fun valueOf(value: Long): RF = constructRationalFunction(polynomialValueOf(value))
    // endregion

    // region Constant-to-Rational-Function conversion
    public override fun rationalFunctionValueOf(value: C): RF = constructRationalFunction(polynomialValueOf(value))
    // endregion

    // region Polynomial-to-Rational-Function conversion
    public override fun rationalFunctionValueOf(value: P): RF = constructRationalFunction(value)
    // endregion

    // region Rational-Function-Int operations
    public override operator fun RF.plus(other: Int): RF =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    public override operator fun RF.minus(other: Int): RF =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    public override operator fun RF.times(other: Int): RF =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    public override operator fun RF.div(other: Int): RF =
        constructRationalFunction(
            numerator,
            denominator * other
        )
    // endregion

    // region Rational-Function-Long operations
    public override operator fun RF.plus(other: Long): RF =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    public override operator fun RF.minus(other: Long): RF =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    public override operator fun RF.times(other: Long): RF =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    public override operator fun RF.div(other: Long): RF =
        constructRationalFunction(
            numerator,
            denominator * other
        )
    // endregion

    // region Int-Rational-Function operations
    public override operator fun Int.plus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    public override operator fun Int.minus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    public override operator fun Int.times(other: RF): RF =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    public override operator fun Int.div(other: RF): RF =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    // endregion

    // region Long-Rational-Function operations
    public override operator fun Long.plus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    public override operator fun Long.minus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    public override operator fun Long.times(other: RF): RF =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    public override operator fun Long.div(other: RF): RF =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    // endregion

    // region Constant-Rational-Function operations
    @JvmName("plusConstantRational")
    public override operator fun C.plus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    @JvmName("minusConstantRational")
    public override operator fun C.minus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    @JvmName("timesConstantRational")
    public override operator fun C.times(other: RF): RF =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    @JvmName("divConstantRational")
    public override operator fun C.div(other: RF): RF =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    // endregion

    // region Rational-Function-Constant operations
    @JvmName("plusRationalConstant")
    public override operator fun RF.plus(other: C): RF =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalConstant")
    public override operator fun RF.minus(other: C): RF =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalConstant")
    public override operator fun RF.times(other: C): RF =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    @JvmName("divRationalConstant")
    public override operator fun RF.div(other: C): RF =
        constructRationalFunction(
            numerator,
            denominator * other
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
            other.denominator * this + other.numerator,
            other.denominator
        )
    @JvmName("minusPolynomialRational")
    public override operator fun P.minus(other: RF): RF =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    @JvmName("timesPolynomialRational")
    public override operator fun P.times(other: RF): RF =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    @JvmName("divPolynomialRational")
    public override operator fun P.div(other: RF): RF =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    // endregion

    // region Rational-Function-Polynomial operations
    @JvmName("plusRationalPolynomial")
    public override operator fun RF.plus(other: P): RF =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalPolynomial")
    public override operator fun RF.minus(other: P): RF =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalPolynomial")
    public override operator fun RF.times(other: P): RF =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    @JvmName("divRationalPolynomial")
    public override operator fun RF.div(other: P): RF =
        constructRationalFunction(
            numerator,
            denominator * other
        )
    // endregion

    // region Rational-Function-Rational-Function operations
    public override operator fun RF.unaryMinus(): RF = constructRationalFunction(-numerator, denominator)
    public override operator fun RF.plus(other: RF): RF =
        constructRationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun RF.minus(other: RF): RF =
        constructRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun RF.times(other: RF): RF =
        constructRationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun RF.div(other: RF): RF =
        constructRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )
    public override fun power(base: RF, exponent: UInt): RF =
        constructRationalFunction(
            power(base.numerator, exponent),
            power(base.denominator, exponent),
        )
    public override fun power(base: RF, exponent: ULong): RF =
        constructRationalFunction(
            power(base.numerator, exponent),
            power(base.denominator, exponent),
        )
    // endregion
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out A: RingOperations<C>,
        out PS: MultivariatePolynomialSpace<C, V, P, A>
        > : RationalFunctionSpace<C, P, RF, A, PS> {

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
    public val RF.variables: Set<V> get() = numerator.variables union denominator.variables
    public val RF.countOfVariables: Int get() = variables.size
    // endregion
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out A: RingOperations<C>,
        out PS: MultivariatePolynomialSpace<C, V, P, A>
        > : MultivariateRationalFunctionSpace<C, V, P, RF, A, PS>,  PolynomialSpaceOfFractions<C, P, RF, A, PS>() {

    // region Variable-Rational-Function operations
    @JvmName("plusVariableRational")
    public override operator fun V.plus(other: RF): RF =
        constructRationalFunction(
            this * other.denominator + other.numerator,
            other.denominator
        )
    @JvmName("minusVariableRational")
    public override operator fun V.minus(other: RF): RF =
        constructRationalFunction(
            this * other.denominator - other.numerator,
            other.denominator
        )
    @JvmName("timesVariableRational")
    public override operator fun V.times(other: RF): RF =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    @JvmName("divVariableRational")
    public override operator fun V.div(other: RF): RF =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    // endregion

    // region Rational-Function-Variable operations
    @JvmName("plusRationalVariable")
    public override operator fun RF.plus(other: V): RF =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalVariable")
    public override operator fun RF.minus(other: V): RF =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalVariable")
    public override operator fun RF.times(other: V): RF =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    @JvmName("divRationalVariable")
    public override operator fun RF.div(other: V): RF =
        constructRationalFunction(
            numerator,
            denominator * other
        )
    // endregion
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverField<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out A: FieldOperations<C>,
        out PS: PolynomialSpaceOverField<C, P, A>
        > : RationalFunctionSpace<C, P, RF, A, PS> {

    // region Polynomial-Constant operations
    @JvmName("divPolynomialConstant")
    @JsName("divPolynomialConstant")
    public operator fun P.div(other: C): P = polynomialSpace.run { this@div.div(other) }
    // endregion
}

context(A, PS)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceOverField<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out A: FieldOperations<C>,
        out PS: MultivariatePolynomialSpaceOverField<C, V, P, A>
        > : RationalFunctionSpaceOverField<C, P, RF, A, PS>, MultivariateRationalFunctionSpace<C, V, P, RF, A, PS>
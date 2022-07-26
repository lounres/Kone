/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.algebraic.util.rightMultiplyByDoubling
import kotlin.jvm.JvmName


public interface RationalFunction<C, P: Polynomial<C>> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<C, P: Polynomial<C>, R: RationalFunction<C, P>> : Ring<R> {

    public val constantZero: C
    public val constantOne: C

    public val polynomialZero: P
    public val polynomialOne: P

    public override val zero: R
    public override val one: R

    // region Equality
    @JvmName("equalsToConstantConstant")
    public infix fun C.equalsTo(other: C): Boolean = this == other
    @JvmName("eqConstantConstant")
    public infix fun C.eq(other: C): Boolean = this equalsTo other
    @JvmName("isZeroConstant")
    public fun C.isZero(): Boolean = this equalsTo constantZero
    @JvmName("isOneConstant")
    public fun C.isOne(): Boolean = this equalsTo constantOne
    @JvmName("isNotZeroConstant")
    public fun C.isNotZero(): Boolean = !isZero()
    @JvmName("isNotOneConstant")
    public fun C.isNotOne(): Boolean = !isOne()
    // endregion

    // region Equality
    @JvmName("equalsToPolynomialPolynomial")
    public infix fun P.equalsTo(other: P): Boolean = this == other
    @JvmName("eqPolynomialPolynomial")
    public infix fun P.eq(other: P): Boolean = this equalsTo other
    @JvmName("isZeroPolynomial")
    public fun P.isZero(): Boolean = this equalsTo polynomialZero
    @JvmName("isOnePolynomial")
    public fun P.isOne(): Boolean = this equalsTo polynomialOne
    @JvmName("isNotZeroPolynomial")
    public fun P.isNotZero(): Boolean = !isZero()
    @JvmName("isNotOnePolynomial")
    public fun P.isNotOne(): Boolean = !isOne()
    // endregion

    // region Equality
    public override infix fun R.equalsTo(other: R): Boolean = numerator * other.denominator equalsTo denominator * other.numerator
    public override infix fun R.eq(other: R): Boolean = this equalsTo other
    public override fun R.isZero(): Boolean = numerator equalsTo polynomialZero
    public override fun R.isOne(): Boolean = numerator equalsTo denominator
    public override fun R.isNotZero(): Boolean = !isZero()
    public override fun R.isNotOne(): Boolean = !isOne()
    // endregion

    public fun constantValueOf(value: Int): C = constantOne * value
    public fun constantValueOf(value: Long): C = constantOne * value
    public val Int.constantValue: C get() = constantValueOf(this)
    public val Long.constantValue: C get() = constantValueOf(this)

    public fun polynomialValueOf(value: Int): P = polynomialValueOf(constantValueOf(value))
    public fun polynomialValueOf(value: Long): P = polynomialValueOf(constantValueOf(value))
    public val Int.polynomialValue: P get() = polynomialValueOf(this)
    public val Long.polynomialValue: P get() = polynomialValueOf(this)

    public override fun valueOf(value: Int): R = valueOf(polynomialValueOf(constantValueOf(value)))
    public override fun valueOf(value: Long): R = valueOf(polynomialValueOf(constantValueOf(value)))
    public override val Int.value: R get() = valueOf(this)
    public override val Long.value: R get() = valueOf(this)

    public fun polynomialValueOf(value: C): P = polynomialOne * value
    public val C.polynomialValue: P get() = polynomialValueOf(this)

    public fun valueOf(value: C): R = valueOf(polynomialValueOf(value))
    public val C.value: R get() = valueOf(this)

    public fun valueOf(value: P): R = one * value
    public val P.value: R get() = valueOf(this)

    @JvmName("plusConstantInt")
    public operator fun C.plus(other: Int): C
    @JvmName("minusConstantInt")
    public operator fun C.minus(other: Int): C
    @JvmName("timesConstantInt")
    public operator fun C.times(other: Int): C

    @JvmName("plusConstantLong")
    public operator fun C.plus(other: Long): C
    @JvmName("minusConstantLong")
    public operator fun C.minus(other: Long): C
    @JvmName("timesConstantLong")
    public operator fun C.times(other: Long): C

    @JvmName("plusIntConstant")
    public operator fun Int.plus(other: C): C
    @JvmName("minusIntConstant")
    public operator fun Int.minus(other: C): C
    @JvmName("timesIntConstant")
    public operator fun Int.times(other: C): C

    @JvmName("plusLongConstant")
    public operator fun Long.plus(other: C): C
    @JvmName("minusLongConstant")
    public operator fun Long.minus(other: C): C
    @JvmName("timesLongConstant")
    public operator fun Long.times(other: C): C

    @JvmName("plusPolynomialInt")
    public operator fun P.plus(other: Int): P
    @JvmName("minusPolynomialInt")
    public operator fun P.minus(other: Int): P
    @JvmName("timesPolynomialInt")
    public operator fun P.times(other: Int): P

    @JvmName("plusPolynomialInt")
    public operator fun P.plus(other: Long): P
    @JvmName("minusPolynomialInt")
    public operator fun P.minus(other: Long): P
    @JvmName("timesPolynomialInt")
    public operator fun P.times(other: Long): P

    @JvmName("plusIntPolynomial")
    public operator fun Int.plus(other: P): P
    @JvmName("minusIntPolynomial")
    public operator fun Int.minus(other: P): P
    @JvmName("timesIntPolynomial")
    public operator fun Int.times(other: P): P

    @JvmName("plusLongPolynomial")
    public operator fun Long.plus(other: P): P
    @JvmName("minusLongPolynomial")
    public operator fun Long.minus(other: P): P
    @JvmName("timesLongPolynomial")
    public operator fun Long.times(other: P): P

    public override operator fun R.plus(other: Int): R
    public override operator fun R.minus(other: Int): R
    public override operator fun R.times(other: Int): R
    public operator fun R.div(other: Int): R

    public override operator fun R.plus(other: Long): R
    public override operator fun R.minus(other: Long): R
    public override operator fun R.times(other: Long): R
    public operator fun R.div(other: Long): R

    public override operator fun Int.plus(other: R): R
    public override operator fun Int.minus(other: R): R
    public override operator fun Int.times(other: R): R
    public operator fun Int.div(other: R): R

    public override operator fun Long.plus(other: R): R
    public override operator fun Long.minus(other: R): R
    public override operator fun Long.times(other: R): R
    public operator fun Long.div(other: R): R

    @JvmName("unaryPlusConstant")
    public operator fun C.unaryPlus(): C = this
    @JvmName("unaryMinusConstant")
    public operator fun C.unaryMinus(): C
    @JvmName("plusConstantConstant")
    public operator fun C.plus(other: C): C
    @JvmName("minusConstantConstant")
    public operator fun C.minus(other: C): C
    @JvmName("timesConstantConstant")
    public operator fun C.times(other: C): C
    @JvmName("powerConstant")
    public fun power(base: C, exponent: UInt): C = rightMultiplyByDoubling(base, exponent, { constantOne }, { left, right -> left * right })
    @JvmName("powerConstant")
    public fun power(base: C, exponent: ULong): C = rightMultiplyByDoubling(base, exponent, { constantOne }, { left, right -> left * right })
    @JvmName("powConstant")
    public infix fun C.pow(exponent: UInt): C = power(this, exponent)
    @JvmName("powConstant")
    public infix fun C.pow(exponent: ULong): C = power(this, exponent)

    @JvmName("plusConstantPolynomial")
    public operator fun C.plus(other: P): P
    @JvmName("minusConstantPolynomial")
    public operator fun C.minus(other: P): P
    @JvmName("timesConstantPolynomial")
    public operator fun C.times(other: P): P

    @JvmName("plusPolynomialConstant")
    public operator fun P.plus(other: C): P
    @JvmName("minusPolynomialConstant")
    public operator fun P.minus(other: C): P
    @JvmName("timesPolynomialConstant")
    public operator fun P.times(other: C): P

    @JvmName("plusConstantRational")
    public operator fun C.plus(other: R): R
    @JvmName("minusConstantRational")
    public operator fun C.minus(other: R): R
    @JvmName("timesConstantRational")
    public operator fun C.times(other: R): R
    @JvmName("divConstantRational")
    public operator fun C.div(other: R): R

    @JvmName("plusRationalConstant")
    public operator fun R.plus(other: C): R
    @JvmName("minusRationalConstant")
    public operator fun R.minus(other: C): R
    @JvmName("timesRationalConstant")
    public operator fun R.times(other: C): R
    @JvmName("divRationalConstant")
    public operator fun R.div(other: C): R

    @JvmName("unaryPlusPolynomial")
    public operator fun P.unaryPlus(): P = this
    @JvmName("unaryMinusPolynomial")
    public operator fun P.unaryMinus(): P
    @JvmName("plusPolynomialPolynomial")
    public operator fun P.plus(other: P): P
    @JvmName("minusPolynomialPolynomial")
    public operator fun P.minus(other: P): P
    @JvmName("timesPolynomialPolynomial")
    public operator fun P.times(other: P): P
    @JvmName("divPolynomialPolynomial")
    public operator fun P.div(other: P): R
    @JvmName("powerPolynomial")
    public fun power(base: P, exponent: UInt): P = rightMultiplyByDoubling(base, exponent, { polynomialOne }, { left, right -> left * right })
    @JvmName("powerPolynomial")
    public fun power(base: P, exponent: ULong): P = rightMultiplyByDoubling(base, exponent, { polynomialOne }, { left, right -> left * right })
    @JvmName("powPolynomial")
    public infix fun P.pow(exponent: UInt): P = power(this, exponent)
    @JvmName("powPolynomial")
    public infix fun P.pow(exponent: ULong): P = power(this, exponent)

    @JvmName("plusPolynomialRational")
    public operator fun P.plus(other: R): R
    @JvmName("minusPolynomialRational")
    public operator fun P.minus(other: R): R
    @JvmName("timesPolynomialRational")
    public operator fun P.times(other: R): R
    @JvmName("divPolynomialRational")
    public operator fun P.div(other: R): R

    @JvmName("plusRationalPolynomial")
    public operator fun R.plus(other: P): R
    @JvmName("minusRationalPolynomial")
    public operator fun R.minus(other: P): R
    @JvmName("timesRationalPolynomial")
    public operator fun R.times(other: P): R
    @JvmName("divRationalPolynomial")
    public operator fun R.div(other: P): R

    public override operator fun R.unaryPlus(): R = this
    public override operator fun R.unaryMinus(): R
    public override operator fun R.plus(other: R): R
    public override operator fun R.minus(other: R): R
    public override operator fun R.times(other: R): R
    public operator fun R.div(other: R): R
    public override fun power(base: R, exponent: UInt): R
    public override fun power(base: R, exponent: ULong): R
    public override infix fun R.pow(exponent: UInt): R = power(this, exponent)
    public override infix fun R.pow(exponent: ULong): R = power(this, exponent)

    public val P.degree: Int

    public val R.numeratorDegree: Int get() = numerator.degree
    public val R.denominatorDegree: Int get() = denominator.degree
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverRing<
        C,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        out A: Ring<C>
        > : RationalFunctionSpace<C, P, R> {

    public val ring: A

    public override val constantZero: C get() = ring.zero
    public override val constantOne: C get() = ring.one

    // region Equality
    @JvmName("equalsToConstantConstant")
    public override infix fun C.equalsTo(other: C): Boolean = with(ring) { this@C equalsTo other }
    @JvmName("eqConstantConstant")
    public override infix fun C.eq(other: C): Boolean = with(ring) { this@C eq other }
    @JvmName("isZeroConstant")
    public override fun C.isZero(): Boolean = with(ring) { this@C.isZero() }
    @JvmName("isOneConstant")
    public override fun C.isOne(): Boolean = with(ring) { this@C.isOne() }
    @JvmName("isNotZeroConstant")
    public override fun C.isNotZero(): Boolean = with(ring) { this@C.isNotZero() }
    @JvmName("isNotOneConstant")
    public override fun C.isNotOne(): Boolean = with(ring) { this@C.isNotOne() }
    // endregion

    public override fun constantValueOf(value: Int): C = with(ring) { valueOf(value) }
    public override fun constantValueOf(value: Long): C = with(ring) { valueOf(value) }
    public override val Int.constantValue: C get() = with(ring) { this@constantValue.value }
    public override val Long.constantValue: C get() = with(ring) { this@constantValue.value }

    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = with(ring) { addMultipliedByDoubling(this@plus, one, other) }
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = with(ring) { addMultipliedByDoubling(this@minus, one, -other) }
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = with(ring) { multiplyByDoubling(this@times, other) }

    @JvmName("plusConstantLong")
    public override operator fun C.plus(other: Long): C = with(ring) { addMultipliedByDoubling(this@plus, one, other) }
    @JvmName("minusConstantLong")
    public override operator fun C.minus(other: Long): C = with(ring) { addMultipliedByDoubling(this@minus, one, -other) }
    @JvmName("timesConstantLong")
    public override operator fun C.times(other: Long): C = with(ring) { multiplyByDoubling(this@times, other) }

    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = with(ring) { addMultipliedByDoubling(other, one, this@plus) }
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = with(ring) { addMultipliedByDoubling(-other, one, this@minus) }
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = with(ring) { multiplyByDoubling(other, this@times) }

    @JvmName("plusLongConstant")
    public override operator fun Long.plus(other: C): C = with(ring) { addMultipliedByDoubling(other, one, this@plus) }
    @JvmName("minusLongConstant")
    public override operator fun Long.minus(other: C): C = with(ring) { addMultipliedByDoubling(-other, one, this@minus) }
    @JvmName("timesLongConstant")
    public override operator fun Long.times(other: C): C = with(ring) { multiplyByDoubling(other, this@times) }

    @JvmName("unaryPlusConstant")
    public override operator fun C.unaryPlus(): C = with(ring) { +this@unaryPlus }
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = with(ring) { -this@unaryMinus }
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = with(ring) { this@plus + other }
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = with(ring) { this@minus - other }
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = with(ring) { this@times * other }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: UInt) : C = with(ring) { power(base, exponent) }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: ULong): C = with(ring) { power(base, exponent) }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: UInt): C = with(ring) { this@C pow exponent }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: ULong): C = with(ring) { this@C pow exponent }
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverPolynomialSpace<
        C,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        out AP: PolynomialSpace<C, P>,
        > : RationalFunctionSpace<C, P, R> {

    public val polynomialRing: AP

    public override val constantZero: C get() = with(polynomialRing) { constantZero }
    public override val constantOne: C get() = with(polynomialRing) { constantOne }

    public override val polynomialZero: P get() = with(polynomialRing) { zero }
    public override val polynomialOne: P get() = with(polynomialRing) { one }

    // region Equality
    @JvmName("equalsToConstantConstant")
    public override infix fun C.equalsTo(other: C): Boolean = with(polynomialRing) { this@C equalsTo other }
    @JvmName("eqConstantConstant")
    public override infix fun C.eq(other: C): Boolean = with(polynomialRing) { this@C eq other }
    @JvmName("isZeroConstant")
    public override fun C.isZero(): Boolean = with(polynomialRing) { this@C.isZero() }
    @JvmName("isOneConstant")
    public override fun C.isOne(): Boolean = with(polynomialRing) { this@C.isOne() }
    @JvmName("isNotZeroConstant")
    public override fun C.isNotZero(): Boolean = with(polynomialRing) { this@C.isNotZero() }
    @JvmName("isNotOneConstant")
    public override fun C.isNotOne(): Boolean = with(polynomialRing) { this@C.isNotOne() }
    // endregion

    // region Equality
    @JvmName("equalsToPolynomialPolynomial")
    public override infix fun P.equalsTo(other: P): Boolean = with(polynomialRing) { this@P equalsTo other }
    @JvmName("eqPolynomialPolynomial")
    public override infix fun P.eq(other: P): Boolean = with(polynomialRing) { this@P eq other }
    @JvmName("isZeroPolynomial")
    public override fun P.isZero(): Boolean = with(polynomialRing) { this@P.isZero() }
    @JvmName("isOnePolynomial")
    public override fun P.isOne(): Boolean = with(polynomialRing) { this@P.isOne() }
    @JvmName("isNotZeroPolynomial")
    public override fun P.isNotZero(): Boolean = with(polynomialRing) { this@P.isNotZero() }
    @JvmName("isNotOnePolynomial")
    public override fun P.isNotOne(): Boolean = with(polynomialRing) { this@P.isNotOne() }
    // endregion

    public override fun constantValueOf(value: Int): C = with(polynomialRing) { constantValueOf(value) }
    public override fun constantValueOf(value: Long): C = with(polynomialRing) { constantValueOf(value) }
    public override val Int.constantValue: C get() = with(polynomialRing) { this@constantValue.constantValue }
    public override val Long.constantValue: C get() = with(polynomialRing) { this@constantValue.constantValue }

    public override fun polynomialValueOf(value: Int): P = with(polynomialRing) { valueOf(value) }
    public override fun polynomialValueOf(value: Long): P = with(polynomialRing) { valueOf(value) }
    public override val Int.polynomialValue: P get() = with(polynomialRing) { this@polynomialValue.value }
    public override val Long.polynomialValue: P get() = with(polynomialRing) { this@polynomialValue.value }

    public override fun polynomialValueOf(value: C): P = with(polynomialRing) { valueOf(value) }
    public override val C.polynomialValue: P get() = with(polynomialRing) { this@C.value }

    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = with(polynomialRing) { this@C + other }
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = with(polynomialRing) { this@C - other }
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = with(polynomialRing) { this@C * other }

    @JvmName("plusConstantLong")
    public override operator fun C.plus(other: Long): C = with(polynomialRing) { this@C + other }
    @JvmName("minusConstantLong")
    public override operator fun C.minus(other: Long): C = with(polynomialRing) { this@C - other }
    @JvmName("timesConstantLong")
    public override operator fun C.times(other: Long): C = with(polynomialRing) { this@C * other }

    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = with(polynomialRing) { this@Int + other }
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = with(polynomialRing) { this@Int - other }
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = with(polynomialRing) { this@Int * other }

    @JvmName("plusLongConstant")
    public override operator fun Long.plus(other: C): C = with(polynomialRing) { this@Long + other }
    @JvmName("minusLongConstant")
    public override operator fun Long.minus(other: C): C = with(polynomialRing) { this@Long - other }
    @JvmName("timesLongConstant")
    public override operator fun Long.times(other: C): C = with(polynomialRing) { this@Long * other }

    @JvmName("plusPolynomialInt")
    public override operator fun P.plus(other: Int): P = with(polynomialRing) { this@P + other }
    @JvmName("minusPolynomialInt")
    public override operator fun P.minus(other: Int): P = with(polynomialRing) { this@P - other }
    @JvmName("timesPolynomialInt")
    public override operator fun P.times(other: Int): P = with(polynomialRing) { this@P * other }

    @JvmName("plusPolynomialInt")
    public override operator fun P.plus(other: Long): P = with(polynomialRing) { this@P + other }
    @JvmName("minusPolynomialInt")
    public override operator fun P.minus(other: Long): P = with(polynomialRing) { this@P - other }
    @JvmName("timesPolynomialInt")
    public override operator fun P.times(other: Long): P = with(polynomialRing) { this@P * other }

    @JvmName("plusIntPolynomial")
    public override operator fun Int.plus(other: P): P = with(polynomialRing) { this@Int + other }
    @JvmName("minusIntPolynomial")
    public override operator fun Int.minus(other: P): P = with(polynomialRing) { this@Int - other }
    @JvmName("timesIntPolynomial")
    public override operator fun Int.times(other: P): P = with(polynomialRing) { this@Int * other }

    @JvmName("plusLongPolynomial")
    public override operator fun Long.plus(other: P): P = with(polynomialRing) { this@Long + other }
    @JvmName("minusLongPolynomial")
    public override operator fun Long.minus(other: P): P = with(polynomialRing) { this@Long - other }
    @JvmName("timesLongPolynomial")
    public override operator fun Long.times(other: P): P = with(polynomialRing) { this@Long * other }

    @JvmName("unaryPlusConstant")
    public override operator fun C.unaryPlus(): C = with(polynomialRing) { +this@C }
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = with(polynomialRing) { -this@C }
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = with(polynomialRing) { this@C + other }
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = with(polynomialRing) { this@C - other }
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = with(polynomialRing) { this@C * other }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: UInt): C = with(polynomialRing) { power(base, exponent) }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: ULong): C = with(polynomialRing) { power(base, exponent) }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: UInt): C = with(polynomialRing) { this@C pow exponent }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: ULong): C = with(polynomialRing) { this@C pow exponent }

    @JvmName("plusConstantPolynomial")
    public override operator fun C.plus(other: P): P = with(polynomialRing) { this@C + other }
    @JvmName("minusConstantPolynomial")
    public override operator fun C.minus(other: P): P = with(polynomialRing) { this@C - other }
    @JvmName("timesConstantPolynomial")
    public override operator fun C.times(other: P): P = with(polynomialRing) { this@C * other }

    @JvmName("plusPolynomialConstant")
    public override operator fun P.plus(other: C): P = with(polynomialRing) { this@P + other }
    @JvmName("minusPolynomialConstant")
    public override operator fun P.minus(other: C): P = with(polynomialRing) { this@P - other }
    @JvmName("timesPolynomialConstant")
    public override operator fun P.times(other: C): P = with(polynomialRing) { this@P * other }

    @JvmName("unaryPlusPolynomial")
    public override operator fun P.unaryPlus(): P = with(polynomialRing) { +this@P }
    @JvmName("unaryMinusPolynomial")
    public override operator fun P.unaryMinus(): P = with(polynomialRing) { -this@P }
    @JvmName("plusPolynomialPolynomial")
    public override operator fun P.plus(other: P): P = with(polynomialRing) { this@P + other }
    @JvmName("minusPolynomialPolynomial")
    public override operator fun P.minus(other: P): P = with(polynomialRing) { this@P - other }
    @JvmName("timesPolynomialPolynomial")
    public override operator fun P.times(other: P): P = with(polynomialRing) { this@P * other }
    @JvmName("powerPolynomial")
    public override fun power(base: P, exponent: UInt): P = with(polynomialRing) { power(base, exponent) }
    @JvmName("powerPolynomial")
    public override fun power(base: P, exponent: ULong): P = with(polynomialRing) { power(base, exponent) }
    @JvmName("powPolynomial")
    public override infix fun P.pow(exponent: UInt): P = with(polynomialRing) { this@P pow exponent }
    @JvmName("powPolynomial")
    public override infix fun P.pow(exponent: ULong): P = with(polynomialRing) { this@P pow exponent }

    public override val P.degree: Int get() = with(polynomialRing) { this@P.degree }
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        C,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        > : RationalFunctionSpace<C, P, R> {

    protected abstract fun constructRationalFunction(numerator: P, denominator: P = polynomialOne) : R

    public override val zero: R by lazy { constructRationalFunction(polynomialZero) }
    public override val one: R by lazy { constructRationalFunction(polynomialOne) }

    public override fun valueOf(value: Int): R = constructRationalFunction(polynomialValueOf(value))
    public override fun valueOf(value: Long): R = constructRationalFunction(polynomialValueOf(value))

    public override fun valueOf(value: C): R = constructRationalFunction(polynomialValueOf(value))

    public override fun valueOf(value: P): R = constructRationalFunction(value)

    public override operator fun R.plus(other: Int): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    public override operator fun R.minus(other: Int): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    public override operator fun R.times(other: Int): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    public override operator fun R.div(other: Int): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    public override operator fun R.plus(other: Long): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    public override operator fun R.minus(other: Long): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    public override operator fun R.times(other: Long): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    public override operator fun R.div(other: Long): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    public override operator fun Int.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    public override operator fun Int.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    public override operator fun Int.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    public override operator fun Int.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    public override operator fun Long.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    public override operator fun Long.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    public override operator fun Long.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    public override operator fun Long.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    @JvmName("plusConstantRational")
    public override operator fun C.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    @JvmName("minusConstantRational")
    public override operator fun C.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    @JvmName("timesConstantRational")
    public override operator fun C.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    @JvmName("divConstantRational")
    public override operator fun C.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    @JvmName("plusRationalConstant")
    public override operator fun R.plus(other: C): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalConstant")
    public override operator fun R.minus(other: C): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalConstant")
    public override operator fun R.times(other: C): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    @JvmName("divRationalConstant")
    public override operator fun R.div(other: C): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    @JvmName("divPolynomialPolynomial")
    public override operator fun P.div(other: P): R = constructRationalFunction(this, other)

    @JvmName("plusPolynomialRational")
    public override operator fun P.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    @JvmName("minusPolynomialRational")
    public override operator fun P.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    @JvmName("timesPolynomialRational")
    public override operator fun P.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    @JvmName("divPolynomialRational")
    public override operator fun P.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    @JvmName("plusRationalPolynomial")
    public override operator fun R.plus(other: P): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalPolynomial")
    public override operator fun R.minus(other: P): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalPolynomial")
    public override operator fun R.times(other: P): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    @JvmName("divRationalPolynomial")
    public override operator fun R.div(other: P): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    public override operator fun R.unaryMinus(): R = constructRationalFunction(-numerator, denominator)
    public override operator fun R.plus(other: R): R =
        constructRationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun R.minus(other: R): R =
        constructRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun R.times(other: R): R =
        constructRationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )
    public override operator fun R.div(other: R): R =
        constructRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )
    public override fun power(base: R, exponent: UInt): R =
        constructRationalFunction(
            power(base.numerator, exponent),
            power(base.denominator, exponent),
        )
    public override fun power(base: R, exponent: ULong): R =
        constructRationalFunction(
            power(base.numerator, exponent),
            power(base.denominator, exponent),
        )
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
        C,
        V,
        P: Polynomial<C>,
        R: RationalFunction<C, P>
        >: RationalFunctionSpace<C, P, R> {
    @JvmName("polynomialValueOfVariable")
    public fun polynomialValueOf(variable: V): P = +variable
    @get:JvmName("polynomialValueVariable")
    public val V.polynomialValue: P get() = polynomialValueOf(this)

    @JvmName("valueOfVariable")
    public fun valueOf(variable: V): R = valueOf(polynomialValueOf(variable))
    @get:JvmName("valueVariable")
    public val V.value: R get() = valueOf(this)
    
    @JvmName("plusVariableInt")
    public operator fun V.plus(other: Int): P
    @JvmName("minusVariableInt")
    public operator fun V.minus(other: Int): P
    @JvmName("timesVariableInt")
    public operator fun V.times(other: Int): P

    @JvmName("plusVariableLong")
    public operator fun V.plus(other: Long): P
    @JvmName("minusVariableLong")
    public operator fun V.minus(other: Long): P
    @JvmName("timesVariableLong")
    public operator fun V.times(other: Long): P

    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: V): P
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: V): P
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: V): P

    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: V): P
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: V): P
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: V): P

    @JvmName("plusVariableConstant")
    public operator fun V.plus(other: C): P
    @JvmName("minusVariableConstant")
    public operator fun V.minus(other: C): P
    @JvmName("timesVariableConstant")
    public operator fun V.times(other: C): P

    @JvmName("plusConstantVariable")
    public operator fun C.plus(other: V): P
    @JvmName("minusConstantVariable")
    public operator fun C.minus(other: V): P
    @JvmName("timesConstantVariable")
    public operator fun C.times(other: V): P

    @JvmName("unaryPlusVariable")
    public operator fun V.unaryPlus(): P
    @JvmName("unaryMinusVariable")
    public operator fun V.unaryMinus(): P
    @JvmName("plusVariableVariable")
    public operator fun V.plus(other: V): P
    @JvmName("minusVariableVariable")
    public operator fun V.minus(other: V): P
    @JvmName("timesVariableVariable")
    public operator fun V.times(other: V): P

    @JvmName("plusVariablePolynomial")
    public operator fun V.plus(other: P): P
    @JvmName("minusVariablePolynomial")
    public operator fun V.minus(other: P): P
    @JvmName("timesVariablePolynomial")
    public operator fun V.times(other: P): P

    @JvmName("plusPolynomialVariable")
    public operator fun P.plus(other: V): P
    @JvmName("minusPolynomialVariable")
    public operator fun P.minus(other: V): P
    @JvmName("timesPolynomialVariable")
    public operator fun P.times(other: V): P

    @JvmName("plusVariableRational")
    public operator fun V.plus(other: R): R
    @JvmName("minusVariableRational")
    public operator fun V.minus(other: R): R
    @JvmName("timesVariableRational")
    public operator fun V.times(other: R): R
    @JvmName("divVariableRational")
    public operator fun V.div(other: R): R

    @JvmName("plusRationalVariable")
    public operator fun R.plus(other: V): R
    @JvmName("minusRationalVariable")
    public operator fun R.minus(other: V): R
    @JvmName("timesRationalVariable")
    public operator fun R.times(other: V): R
    @JvmName("divRationalVariable")
    public operator fun R.div(other: V): R

    public val P.degrees: Map<V, UInt>
    public fun P.degreeBy(variable: V): UInt = degrees.getOrElse(variable) { 0u }
    public fun P.degreeBy(variables: Collection<V>): UInt
    public val P.variables: Set<V> get() = degrees.keys
    public val P.countOfVariables: Int get() = variables.size

    public val R.variables: Set<V> get() = numerator.variables union denominator.variables
    public val R.countOfVariables: Int get() = variables.size
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceOverMultivariatePolynomialSpace<
        C,
        V,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        out AP: MultivariatePolynomialSpace<C, V, P>,
        > : RationalFunctionSpaceOverPolynomialSpace<C, P, R, AP>, MultivariateRationalFunctionSpace<C, V, P, R> {
    @JvmName("polynomialValueOfVariable")
    public override fun polynomialValueOf(variable: V): P = with(polynomialRing) { this.valueOf(variable) }
    @get:JvmName("polynomialValueVariable")
    public override val V.polynomialValue: P get() = with(polynomialRing) { this@polynomialValue.value }
    
    @JvmName("plusVariableInt")
    public override operator fun V.plus(other: Int): P = with(polynomialRing) { this@V + other }
    @JvmName("minusVariableInt")
    public override operator fun V.minus(other: Int): P = with(polynomialRing) { this@V - other }
    @JvmName("timesVariableInt")
    public override operator fun V.times(other: Int): P = with(polynomialRing) { this@V * other }

    @JvmName("plusVariableLong")
    public override operator fun V.plus(other: Long): P = with(polynomialRing) { this@V + other }
    @JvmName("minusVariableLong")
    public override operator fun V.minus(other: Long): P = with(polynomialRing) { this@V - other }
    @JvmName("timesVariableLong")
    public override operator fun V.times(other: Long): P = with(polynomialRing) { this@V * other }

    @JvmName("plusIntVariable")
    public override operator fun Int.plus(other: V): P = with(polynomialRing) { this@Int + other }
    @JvmName("minusIntVariable")
    public override operator fun Int.minus(other: V): P = with(polynomialRing) { this@Int - other }
    @JvmName("timesIntVariable")
    public override operator fun Int.times(other: V): P = with(polynomialRing) { this@Int * other }

    @JvmName("plusLongVariable")
    public override operator fun Long.plus(other: V): P = with(polynomialRing) { this@Long + other }
    @JvmName("minusLongVariable")
    public override operator fun Long.minus(other: V): P = with(polynomialRing) { this@Long - other }
    @JvmName("timesLongVariable")
    public override operator fun Long.times(other: V): P = with(polynomialRing) { this@Long * other }

    @JvmName("plusVariableConstant")
    public override operator fun V.plus(other: C): P = with(polynomialRing) { this@V + other }
    @JvmName("minusVariableConstant")
    public override operator fun V.minus(other: C): P = with(polynomialRing) { this@V - other }
    @JvmName("timesVariableConstant")
    public override operator fun V.times(other: C): P = with(polynomialRing) { this@V * other }

    @JvmName("plusConstantVariable")
    public override operator fun C.plus(other: V): P = with(polynomialRing) { this@C + other }
    @JvmName("minusConstantVariable")
    public override operator fun C.minus(other: V): P = with(polynomialRing) { this@C - other }
    @JvmName("timesConstantVariable")
    public override operator fun C.times(other: V): P = with(polynomialRing) { this@C * other }

    @JvmName("unaryPlusVariable")
    public override operator fun V.unaryPlus(): P = with(polynomialRing) { +this@V }
    @JvmName("unaryMinusVariable")
    public override operator fun V.unaryMinus(): P = with(polynomialRing) { -this@V }
    @JvmName("plusVariableVariable")
    public override operator fun V.plus(other: V): P = with(polynomialRing) { this@V + other }
    @JvmName("minusVariableVariable")
    public override operator fun V.minus(other: V): P = with(polynomialRing) { this@V - other }
    @JvmName("timesVariableVariable")
    public override operator fun V.times(other: V): P = with(polynomialRing) { this@V * other }

    @JvmName("plusVariablePolynomial")
    public override operator fun V.plus(other: P): P = with(polynomialRing) { this@V + other }
    @JvmName("minusVariablePolynomial")
    public override operator fun V.minus(other: P): P = with(polynomialRing) { this@V - other }
    @JvmName("timesVariablePolynomial")
    public override operator fun V.times(other: P): P = with(polynomialRing) { this@V * other }

    @JvmName("plusPolynomialVariable")
    public override operator fun P.plus(other: V): P = with(polynomialRing) { this@P + other }
    @JvmName("minusPolynomialVariable")
    public override operator fun P.minus(other: V): P = with(polynomialRing) { this@P - other }
    @JvmName("timesPolynomialVariable")
    public override operator fun P.times(other: V): P = with(polynomialRing) { this@P * other }

    public override val P.degrees: Map<V, UInt> get() = with(polynomialRing) { degrees }
    public override fun P.degreeBy(variable: V): UInt = with(polynomialRing) { degreeBy(variable) }
    public override fun P.degreeBy(variables: Collection<V>): UInt = with(polynomialRing) { degreeBy(variables) }
    public override val P.variables: Set<V> get() = with(polynomialRing) { variables }
    public override val P.countOfVariables: Int get() = with(polynomialRing) { countOfVariables }
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
        C,
        V,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        > : MultivariateRationalFunctionSpace<C, V, P, R>,  PolynomialSpaceOfFractions<C, P, R>() {
    @JvmName("plusVariableRational")
    public override operator fun V.plus(other: R): R =
        constructRationalFunction(
            this * other.denominator + other.numerator,
            other.denominator
        )
    @JvmName("minusVariableRational")
    public override operator fun V.minus(other: R): R =
        constructRationalFunction(
            this * other.denominator - other.numerator,
            other.denominator
        )
    @JvmName("timesVariableRational")
    public override operator fun V.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )

    @JvmName("plusRationalVariable")
    public override operator fun R.plus(other: V): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    @JvmName("minusRationalVariable")
    public override operator fun R.minus(other: V): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    @JvmName("timesRationalVariable")
    public override operator fun R.times(other: V): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
}
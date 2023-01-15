/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.util.rightMultiplyByDoubling
import com.lounres.kone.algebraic.util.squaringPower
import kotlin.js.JsName
import kotlin.jvm.JvmName


public interface RationalFunction<C, P: Polynomial<C>> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<C, P: Polynomial<C>, RF: RationalFunction<C, P>> : Field<RF> {

    // region Constant constants
    public val constantZero: C
    public val constantOne: C
    // endregion

    // region Polynomial constants
    public val polynomialZero: P
    public val polynomialOne: P
    // endregion

    // region Rational Function constants
    public override val zero: RF
    public override val one: RF
    // endregion

    // region Equality
    @JvmName("equalsToConstantConstant")
    @JsName("equalsToConstantConstant")
    public infix fun C.equalsTo(other: C): Boolean = this == other
    // FIXME: KT-5351
    @JvmName("notEqualsToConstantConstant")
    @JsName("notEqualsToConstantConstant")
    public infix fun C.notEqualsTo(other: C): Boolean = !(this equalsTo other)
    @JvmName("eqConstantConstant")
    @JsName("eqConstantConstant")
    public infix fun C.eq(other: C): Boolean = this equalsTo other
    // FIXME: KT-5351
    @JvmName("neqConstantConstant")
    @JsName("neqConstantConstant")
    public infix fun C.neq(other: C): Boolean = !(this equalsTo other)
    @JvmName("isZeroConstant")
    @JsName("isZeroConstant")
    public fun C.isZero(): Boolean = this equalsTo constantZero
    @JvmName("isOneConstant")
    @JsName("isOneConstant")
    public fun C.isOne(): Boolean = this equalsTo constantOne
    // FIXME: KT-5351
    @JvmName("isNotZeroConstant")
    @JsName("isNotZeroConstant")
    public fun C.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    @JvmName("isNotOneConstant")
    @JsName("isNotOneConstant")
    public fun C.isNotOne(): Boolean = !isOne()
    // endregion

    // region Equality
    @JvmName("equalsToPolynomialPolynomial")
    public infix fun P.equalsTo(other: P): Boolean = this == other
    // FIXME: KT-5351
    @JvmName("notEqualsToPolynomialPolynomial")
    public infix fun P.notEqualsTo(other: P): Boolean = !(this equalsTo other)
    @JvmName("eqPolynomialPolynomial")
    public infix fun P.eq(other: P): Boolean = this equalsTo other
    // FIXME: KT-5351
    @JvmName("neqPolynomialPolynomial")
    public infix fun P.neq(other: P): Boolean = !(this equalsTo other)
    @JvmName("isZeroPolynomial")
    public fun P.isZero(): Boolean = this equalsTo polynomialZero
    @JvmName("isOnePolynomial")
    public fun P.isOne(): Boolean = this equalsTo polynomialOne
    // FIXME: KT-5351
    @JvmName("isNotZeroPolynomial")
    public fun P.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    @JvmName("isNotOnePolynomial")
    public fun P.isNotOne(): Boolean = !isOne()
    // endregion

    // region Equality
    public override infix fun RF.equalsTo(other: RF): Boolean = numerator * other.denominator equalsTo denominator * other.numerator
    public override infix fun RF.eq(other: RF): Boolean = this equalsTo other
    public override fun RF.isZero(): Boolean = numerator equalsTo polynomialZero
    public override fun RF.isOne(): Boolean = numerator equalsTo denominator
    // endregion

    // region Integer-to-Constant conversion
    public fun constantValueOf(value: Int): C = constantOne * value
    public fun constantValueOf(value: Long): C = constantOne * value
    public val Int.constantValue: C get() = constantValueOf(this)
    public val Long.constantValue: C get() = constantValueOf(this)
    // endregion

    // region Integer-to-Polynomial conversion
    public fun polynomialValueOf(value: Int): P = polynomialValueOf(constantValueOf(value))
    public fun polynomialValueOf(value: Long): P = polynomialValueOf(constantValueOf(value))
    public val Int.polynomialValue: P get() = polynomialValueOf(this)
    public val Long.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Integer-to-Rational-Function conversion
    public override fun valueOf(value: Int): RF = valueOf(polynomialValueOf(constantValueOf(value)))
    public override fun valueOf(value: Long): RF = valueOf(polynomialValueOf(constantValueOf(value)))
    public override val Int.value: RF get() = valueOf(this)
    public override val Long.value: RF get() = valueOf(this)
    // endregion

    // region Constant-to-Polynomial conversion
    public fun polynomialValueOf(value: C): P = polynomialOne * value
    public val C.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Constant-to-Rational-Function conversion
    public fun valueOf(value: C): RF = valueOf(polynomialValueOf(value))
    public val C.value: RF get() = valueOf(this)
    // endregion

    // region Polynomial-to-Rational-Function conversion
    public fun valueOf(value: P): RF = one * value
    public val P.value: RF get() = valueOf(this)
    // endregion

    // region Constant-Int operations
    @JvmName("plusConstantInt")
    @JsName("plusConstantInt")
    public operator fun C.plus(other: Int): C
    @JvmName("minusConstantInt")
    @JsName("minusConstantInt")
    public operator fun C.minus(other: Int): C
    @JvmName("timesConstantInt")
    @JsName("timesConstantInt")
    public operator fun C.times(other: Int): C
    // endregion

    // region Constant-Long operations
    @JvmName("plusConstantLong")
    @JsName("plusConstantLong")
    public operator fun C.plus(other: Long): C
    @JvmName("minusConstantLong")
    @JsName("minusConstantLong")
    public operator fun C.minus(other: Long): C
    @JvmName("timesConstantLong")
    @JsName("timesConstantLong")
    public operator fun C.times(other: Long): C
    // endregion

    // region Int-Constant operations
    @JvmName("plusIntConstant")
    @JsName("plusIntConstant")
    public operator fun Int.plus(other: C): C
    @JvmName("minusIntConstant")
    @JsName("minusIntConstant")
    public operator fun Int.minus(other: C): C
    @JvmName("timesIntConstant")
    @JsName("timesIntConstant")
    public operator fun Int.times(other: C): C
    // endregion

    // region Long-Constant operations
    @JvmName("plusLongConstant")
    @JsName("plusLongConstant")
    public operator fun Long.plus(other: C): C
    @JvmName("minusLongConstant")
    @JsName("minusLongConstant")
    public operator fun Long.minus(other: C): C
    @JvmName("timesLongConstant")
    @JsName("timesLongConstant")
    public operator fun Long.times(other: C): C
    // endregion

    // region Polynomial-Int operations
    @JvmName("plusPolynomialInt")
    public operator fun P.plus(other: Int): P
    @JvmName("minusPolynomialInt")
    public operator fun P.minus(other: Int): P
    @JvmName("timesPolynomialInt")
    public operator fun P.times(other: Int): P
    // endregion

    // region Polynomial-Long operations
    @JvmName("plusPolynomialInt")
    public operator fun P.plus(other: Long): P
    @JvmName("minusPolynomialInt")
    public operator fun P.minus(other: Long): P
    @JvmName("timesPolynomialInt")
    public operator fun P.times(other: Long): P
    // endregion

    // region Int-Polynomial operations
    @JvmName("plusIntPolynomial")
    public operator fun Int.plus(other: P): P
    @JvmName("minusIntPolynomial")
    public operator fun Int.minus(other: P): P
    @JvmName("timesIntPolynomial")
    public operator fun Int.times(other: P): P
    // endregion

    // region Long-Polynomial operations
    @JvmName("plusLongPolynomial")
    public operator fun Long.plus(other: P): P
    @JvmName("minusLongPolynomial")
    public operator fun Long.minus(other: P): P
    @JvmName("timesLongPolynomial")
    public operator fun Long.times(other: P): P
    // endregion

    // region Rational-Function-Int operations
    public override operator fun RF.plus(other: Int): RF
    public override operator fun RF.minus(other: Int): RF
    public override operator fun RF.times(other: Int): RF
    public override operator fun RF.div(other: Int): RF
    // endregion

    // region Rational-Function-Long operations
    public override operator fun RF.plus(other: Long): RF
    public override operator fun RF.minus(other: Long): RF
    public override operator fun RF.times(other: Long): RF
    public override operator fun RF.div(other: Long): RF
    // endregion

    // region Int-Rational-Function operations
    public override operator fun Int.plus(other: RF): RF
    public override operator fun Int.minus(other: RF): RF
    public override operator fun Int.times(other: RF): RF
    public override operator fun Int.div(other: RF): RF
    // endregion

    // region Long-Rational-Function operations
    public override operator fun Long.plus(other: RF): RF
    public override operator fun Long.minus(other: RF): RF
    public override operator fun Long.times(other: RF): RF
    public override operator fun Long.div(other: RF): RF
    // endregion

    // region Constant-Constant operations
    @JvmName("unaryPlusConstant")
    @JsName("unaryPlusConstant")
    public operator fun C.unaryPlus(): C = this
    @JvmName("unaryMinusConstant")
    @JsName("unaryMinusConstant")
    public operator fun C.unaryMinus(): C
    @JvmName("plusConstantConstant")
    @JsName("plusConstantConstant")
    public operator fun C.plus(other: C): C
    @JvmName("minusConstantConstant")
    @JsName("minusConstantConstant")
    public operator fun C.minus(other: C): C
    @JvmName("timesConstantConstant")
    @JsName("timesConstantConstant")
    public operator fun C.times(other: C): C
    @JvmName("powerConstant")
    @JsName("powerConstantUInt")
    public fun power(base: C, exponent: UInt): C = rightMultiplyByDoubling(base, exponent, { constantOne }, { left, right -> left * right })
    @JvmName("powerConstant")
    @JsName("powerConstantULong")
    public fun power(base: C, exponent: ULong): C = rightMultiplyByDoubling(base, exponent, { constantOne }, { left, right -> left * right })
    @JvmName("powConstant")
    @JsName("powConstantUInt")
    public infix fun C.pow(exponent: UInt): C = power(this, exponent)
    @JvmName("powConstant")
    @JsName("powConstantULong")
    public infix fun C.pow(exponent: ULong): C = power(this, exponent)
    // endregion

    // region Constant-Polynomial operations
    @JvmName("plusConstantPolynomial")
    public operator fun C.plus(other: P): P
    @JvmName("minusConstantPolynomial")
    public operator fun C.minus(other: P): P
    @JvmName("timesConstantPolynomial")
    public operator fun C.times(other: P): P
    // endregion

    // region Polynomial-Constant operations
    @JvmName("plusPolynomialConstant")
    public operator fun P.plus(other: C): P
    @JvmName("minusPolynomialConstant")
    public operator fun P.minus(other: C): P
    @JvmName("timesPolynomialConstant")
    public operator fun P.times(other: C): P
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

    // region Polynomial-Polynomial operations
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
    public operator fun P.div(other: P): RF
    @JvmName("powerPolynomial")
    public fun power(base: P, exponent: UInt): P = rightMultiplyByDoubling(base, exponent, { polynomialOne }, { left, right -> left * right })
    @JvmName("powerPolynomial")
    public fun power(base: P, exponent: ULong): P = rightMultiplyByDoubling(base, exponent, { polynomialOne }, { left, right -> left * right })
    @JvmName("powPolynomial")
    public infix fun P.pow(exponent: UInt): P = power(this, exponent)
    @JvmName("powPolynomial")
    public infix fun P.pow(exponent: ULong): P = power(this, exponent)
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

    // region Rational-Function-Rational-Function operations
    public override operator fun RF.unaryPlus(): RF = this
    public override operator fun RF.unaryMinus(): RF
    public override operator fun RF.plus(other: RF): RF
    public override operator fun RF.minus(other: RF): RF
    public override operator fun RF.times(other: RF): RF
    public override operator fun RF.div(other: RF): RF
    public override fun power(base: RF, exponent: UInt): RF = squaringPower(base, exponent)
    public override fun power(base: RF, exponent: ULong): RF = squaringPower(base, exponent)
    public override infix fun RF.pow(exponent: UInt): RF = power(this, exponent)
    public override infix fun RF.pow(exponent: ULong): RF = power(this, exponent)
    // endregion

    // region Polynomial properties
    public val P.degree: Int
    // endregion

    // region Rational Function properties
    public val RF.numeratorDegree: Int get() = numerator.degree
    public val RF.denominatorDegree: Int get() = denominator.degree
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceWithPolynomialSpace<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out PS: PolynomialSpace<C, P>,
        > : RationalFunctionSpace<C, P, RF> {

    public val polynomialRing: PS

    // region Constant constants
    public override val constantZero: C get() = polynomialRing { constantZero }
    public override val constantOne: C get() = polynomialRing { constantOne }
    // endregion

    // region Polynomial constants
    public override val polynomialZero: P get() = polynomialRing { zero }
    public override val polynomialOne: P get() = polynomialRing { one }
    // endregion

    // region Equality
    @JvmName("equalsToConstantConstant")
    public override infix fun C.equalsTo(other: C): Boolean = polynomialRing { this@equalsTo equalsTo other }
    @JvmName("eqConstantConstant")
    public override infix fun C.eq(other: C): Boolean = polynomialRing { this@eq eq other }
    @JvmName("isZeroConstant")
    public override fun C.isZero(): Boolean = polynomialRing { this@isZero.isZero() }
    @JvmName("isOneConstant")
    public override fun C.isOne(): Boolean = polynomialRing { this@isOne.isOne() }
    @JvmName("isNotZeroConstant")
    public override fun C.isNotZero(): Boolean = polynomialRing { this@isNotZero.isNotZero() }
    @JvmName("isNotOneConstant")
    public override fun C.isNotOne(): Boolean = polynomialRing { this@isNotOne.isNotOne() }
    // endregion

    // region Equality
    @JvmName("equalsToPolynomialPolynomial")
    public override infix fun P.equalsTo(other: P): Boolean = polynomialRing { this@equalsTo equalsTo other }
    @JvmName("eqPolynomialPolynomial")
    public override infix fun P.eq(other: P): Boolean = polynomialRing { this@eq eq other }
    @JvmName("isZeroPolynomial")
    public override fun P.isZero(): Boolean = polynomialRing { this@isZero.isZero() }
    @JvmName("isOnePolynomial")
    public override fun P.isOne(): Boolean = polynomialRing { this@isOne.isOne() }
    @JvmName("isNotZeroPolynomial")
    public override fun P.isNotZero(): Boolean = polynomialRing { this@isNotZero.isNotZero() }
    @JvmName("isNotOnePolynomial")
    public override fun P.isNotOne(): Boolean = polynomialRing { this@isNotOne.isNotOne() }
    // endregion

    // region Integer-to-Constant conversion
    public override fun constantValueOf(value: Int): C = polynomialRing { constantValueOf(value) }
    public override fun constantValueOf(value: Long): C = polynomialRing { constantValueOf(value) }
    public override val Int.constantValue: C get() = polynomialRing { this@constantValue.constantValue }
    public override val Long.constantValue: C get() = polynomialRing { this@constantValue.constantValue }
    // endregion

    // region Integer-to-Polynomial conversion
    public override fun polynomialValueOf(value: Int): P = polynomialRing { valueOf(value) }
    public override fun polynomialValueOf(value: Long): P = polynomialRing { valueOf(value) }
    public override val Int.polynomialValue: P get() = polynomialRing { this@polynomialValue.value }
    public override val Long.polynomialValue: P get() = polynomialRing { this@polynomialValue.value }
    // endregion

    // region Constant-to-Polynomial conversion
    public override fun polynomialValueOf(value: C): P = polynomialRing { valueOf(value) }
    public override val C.polynomialValue: P get() = polynomialRing { this@polynomialValue.value }
    // endregion

    // region Constant-Int operations
    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = polynomialRing { this@plus + other }
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = polynomialRing { this@minus - other }
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = polynomialRing { this@times * other }
    // endregion

    // region Constant-Long operations
    @JvmName("plusConstantLong")
    public override operator fun C.plus(other: Long): C = polynomialRing { this@plus + other }
    @JvmName("minusConstantLong")
    public override operator fun C.minus(other: Long): C = polynomialRing { this@minus - other }
    @JvmName("timesConstantLong")
    public override operator fun C.times(other: Long): C = polynomialRing { this@times * other }
    // endregion

    // region Int-Constant operations
    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = polynomialRing { this@plus + other }
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = polynomialRing { this@minus - other }
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = polynomialRing { this@times * other }
    // endregion

    // region Long-Constant operations
    @JvmName("plusLongConstant")
    public override operator fun Long.plus(other: C): C = polynomialRing { this@plus + other }
    @JvmName("minusLongConstant")
    public override operator fun Long.minus(other: C): C = polynomialRing { this@minus - other }
    @JvmName("timesLongConstant")
    public override operator fun Long.times(other: C): C = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-Int operations
    @JvmName("plusPolynomialInt")
    public override operator fun P.plus(other: Int): P = polynomialRing { this@plus + other }
    @JvmName("minusPolynomialInt")
    public override operator fun P.minus(other: Int): P = polynomialRing { this@minus - other }
    @JvmName("timesPolynomialInt")
    public override operator fun P.times(other: Int): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-Long operations
    @JvmName("plusPolynomialInt")
    public override operator fun P.plus(other: Long): P = polynomialRing { this@plus + other }
    @JvmName("minusPolynomialInt")
    public override operator fun P.minus(other: Long): P = polynomialRing { this@minus - other }
    @JvmName("timesPolynomialInt")
    public override operator fun P.times(other: Long): P = polynomialRing { this@times * other }
    // endregion

    // region Int-Polynomial operations
    @JvmName("plusIntPolynomial")
    public override operator fun Int.plus(other: P): P = polynomialRing { this@plus + other }
    @JvmName("minusIntPolynomial")
    public override operator fun Int.minus(other: P): P = polynomialRing { this@minus - other }
    @JvmName("timesIntPolynomial")
    public override operator fun Int.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Long-Polynomial operations
    @JvmName("plusLongPolynomial")
    public override operator fun Long.plus(other: P): P = polynomialRing { this@plus + other }
    @JvmName("minusLongPolynomial")
    public override operator fun Long.minus(other: P): P = polynomialRing { this@minus - other }
    @JvmName("timesLongPolynomial")
    public override operator fun Long.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Constant-Constant operations
    @JvmName("unaryPlusConstant")
    public override operator fun C.unaryPlus(): C = polynomialRing { +this@unaryPlus }
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = polynomialRing { -this@unaryMinus }
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = polynomialRing { this@plus + other }
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = polynomialRing { this@minus - other }
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = polynomialRing { this@times * other }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: UInt): C = polynomialRing { power(base, exponent) }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: ULong): C = polynomialRing { power(base, exponent) }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: UInt): C = polynomialRing { this@pow pow exponent }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: ULong): C = polynomialRing { this@pow pow exponent }
    // endregion

    // region Constant-Polynomial operations
    @JvmName("plusConstantPolynomial")
    public override operator fun C.plus(other: P): P = polynomialRing { this@plus + other }
    @JvmName("minusConstantPolynomial")
    public override operator fun C.minus(other: P): P = polynomialRing { this@minus - other }
    @JvmName("timesConstantPolynomial")
    public override operator fun C.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-Constant operations
    @JvmName("plusPolynomialConstant")
    public override operator fun P.plus(other: C): P = polynomialRing { this@plus + other }
    @JvmName("minusPolynomialConstant")
    public override operator fun P.minus(other: C): P = polynomialRing { this@minus - other }
    @JvmName("timesPolynomialConstant")
    public override operator fun P.times(other: C): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-Polynomial operations
    @JvmName("unaryPlusPolynomial")
    public override operator fun P.unaryPlus(): P = polynomialRing { +this@unaryPlus }
    @JvmName("unaryMinusPolynomial")
    public override operator fun P.unaryMinus(): P = polynomialRing { -this@unaryMinus }
    @JvmName("plusPolynomialPolynomial")
    public override operator fun P.plus(other: P): P = polynomialRing { this@plus + other }
    @JvmName("minusPolynomialPolynomial")
    public override operator fun P.minus(other: P): P = polynomialRing { this@minus - other }
    @JvmName("timesPolynomialPolynomial")
    public override operator fun P.times(other: P): P = polynomialRing { this@times * other }
    @JvmName("powerPolynomial")
    public override fun power(base: P, exponent: UInt): P = polynomialRing { power(base, exponent) }
    @JvmName("powerPolynomial")
    public override fun power(base: P, exponent: ULong): P = polynomialRing { power(base, exponent) }
    @JvmName("powPolynomial")
    public override infix fun P.pow(exponent: UInt): P = polynomialRing { this@pow pow exponent }
    @JvmName("powPolynomial")
    public override infix fun P.pow(exponent: ULong): P = polynomialRing { this@pow pow exponent }
    // endregion

    // region Polynomial properties
    public override val P.degree: Int get() = polynomialRing { this@degree.degree }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        > : RationalFunctionSpace<C, P, RF> {

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
    public override fun valueOf(value: C): RF = constructRationalFunction(polynomialValueOf(value))
    // endregion

    // region Polynomial-to-Rational-Function conversion
    public override fun valueOf(value: P): RF = constructRationalFunction(value)
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

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>
        > : RationalFunctionSpace<C, P, RF> {

    // region Variable-to-Polynomial conversion
    @JvmName("polynomialValueOfVariable")
    public fun polynomialValueOf(variable: V): P = +variable
    @get:JvmName("polynomialValueVariable")
    public val V.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Variable-to-Rational-Function conversion
    @JvmName("valueOfVariable")
    public fun valueOf(variable: V): RF = valueOf(polynomialValueOf(variable))
    @get:JvmName("valueVariable")
    public val V.value: RF get() = valueOf(this)
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public operator fun V.plus(other: Int): P
    @JvmName("minusVariableInt")
    public operator fun V.minus(other: Int): P
    @JvmName("timesVariableInt")
    public operator fun V.times(other: Int): P
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public operator fun V.plus(other: Long): P
    @JvmName("minusVariableLong")
    public operator fun V.minus(other: Long): P
    @JvmName("timesVariableLong")
    public operator fun V.times(other: Long): P
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: V): P
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: V): P
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: V): P
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: V): P
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: V): P
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: V): P
    // endregion

    // region Variable-Constant operations
    @JvmName("plusVariableConstant")
    public operator fun V.plus(other: C): P
    @JvmName("minusVariableConstant")
    public operator fun V.minus(other: C): P
    @JvmName("timesVariableConstant")
    public operator fun V.times(other: C): P
    // endregion

    // region Constant-Variable operations
    @JvmName("plusConstantVariable")
    public operator fun C.plus(other: V): P
    @JvmName("minusConstantVariable")
    public operator fun C.minus(other: V): P
    @JvmName("timesConstantVariable")
    public operator fun C.times(other: V): P
    // endregion

    // region Variable-Variable operation
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
    // endregion

    // region Variable-Polynomial operations
    @JvmName("plusVariablePolynomial")
    public operator fun V.plus(other: P): P
    @JvmName("minusVariablePolynomial")
    public operator fun V.minus(other: P): P
    @JvmName("timesVariablePolynomial")
    public operator fun V.times(other: P): P
    // endregion

    // region Polynomial-Variable operations
    @JvmName("plusPolynomialVariable")
    public operator fun P.plus(other: V): P
    @JvmName("minusPolynomialVariable")
    public operator fun P.minus(other: V): P
    @JvmName("timesPolynomialVariable")
    public operator fun P.times(other: V): P
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

    // region Polynomial properties
    public val P.degrees: Map<V, UInt>
    public fun P.degreeBy(variable: V): UInt = degrees.getOrElse(variable) { 0u }
    public fun P.degreeBy(variables: Collection<V>): UInt
    public val P.variables: Set<V> get() = degrees.keys
    public val P.countOfVariables: Int get() = variables.size
    // endregion

    // region Rational Function properties
    public val RF.variables: Set<V> get() = numerator.variables union denominator.variables
    public val RF.countOfVariables: Int get() = variables.size
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceWithMultivariatePolynomialSpace<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out PS: MultivariatePolynomialSpace<C, V, P>,
        > : RationalFunctionSpaceWithPolynomialSpace<C, P, RF, PS>, MultivariateRationalFunctionSpace<C, V, P, RF> {

    // region Variable-to-Polynomial conversion
    @JvmName("polynomialValueOfVariable")
    public override fun polynomialValueOf(variable: V): P = polynomialRing { this.valueOf(variable) }
    @get:JvmName("polynomialValueVariable")
    public override val V.polynomialValue: P get() = polynomialRing { this@polynomialValue.value }
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public override operator fun V.plus(other: Int): P = polynomialRing { this@plus + other }
    @JvmName("minusVariableInt")
    public override operator fun V.minus(other: Int): P = polynomialRing { this@minus - other }
    @JvmName("timesVariableInt")
    public override operator fun V.times(other: Int): P = polynomialRing { this@times * other }
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public override operator fun V.plus(other: Long): P = polynomialRing { this@plus + other }
    @JvmName("minusVariableLong")
    public override operator fun V.minus(other: Long): P = polynomialRing { this@minus - other }
    @JvmName("timesVariableLong")
    public override operator fun V.times(other: Long): P = polynomialRing { this@times * other }
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public override operator fun Int.plus(other: V): P = polynomialRing { this@plus + other }
    @JvmName("minusIntVariable")
    public override operator fun Int.minus(other: V): P = polynomialRing { this@minus - other }
    @JvmName("timesIntVariable")
    public override operator fun Int.times(other: V): P = polynomialRing { this@times * other }
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public override operator fun Long.plus(other: V): P = polynomialRing { this@plus + other }
    @JvmName("minusLongVariable")
    public override operator fun Long.minus(other: V): P = polynomialRing { this@minus - other }
    @JvmName("timesLongVariable")
    public override operator fun Long.times(other: V): P = polynomialRing { this@times * other }
    // endregion

    // region Variable-Constant operations
    @JvmName("plusVariableConstant")
    public override operator fun V.plus(other: C): P = polynomialRing { this@plus + other }
    @JvmName("minusVariableConstant")
    public override operator fun V.minus(other: C): P = polynomialRing { this@minus - other }
    @JvmName("timesVariableConstant")
    public override operator fun V.times(other: C): P = polynomialRing { this@times * other }
    // endregion

    // region Constant-Variable operations
    @JvmName("plusConstantVariable")
    public override operator fun C.plus(other: V): P = polynomialRing { this@plus + other }
    @JvmName("minusConstantVariable")
    public override operator fun C.minus(other: V): P = polynomialRing { this@minus - other }
    @JvmName("timesConstantVariable")
    public override operator fun C.times(other: V): P = polynomialRing { this@times * other }
    // endregion

    // region Variable-Variable operation
    @JvmName("unaryPlusVariable")
    public override operator fun V.unaryPlus(): P = polynomialRing { +this@unaryPlus }
    @JvmName("unaryMinusVariable")
    public override operator fun V.unaryMinus(): P = polynomialRing { -this@unaryMinus }
    @JvmName("plusVariableVariable")
    public override operator fun V.plus(other: V): P = polynomialRing { this@plus + other }
    @JvmName("minusVariableVariable")
    public override operator fun V.minus(other: V): P = polynomialRing { this@minus - other }
    @JvmName("timesVariableVariable")
    public override operator fun V.times(other: V): P = polynomialRing { this@times * other }
    // endregion

    // region Variable-Polynomial operations
    @JvmName("plusVariablePolynomial")
    public override operator fun V.plus(other: P): P = polynomialRing { this@plus + other }
    @JvmName("minusVariablePolynomial")
    public override operator fun V.minus(other: P): P = polynomialRing { this@minus - other }
    @JvmName("timesVariablePolynomial")
    public override operator fun V.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-Variable operations
    @JvmName("plusPolynomialVariable")
    public override operator fun P.plus(other: V): P = polynomialRing { this@plus + other }
    @JvmName("minusPolynomialVariable")
    public override operator fun P.minus(other: V): P = polynomialRing { this@minus - other }
    @JvmName("timesPolynomialVariable")
    public override operator fun P.times(other: V): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial properties
    public override val P.degrees: Map<V, UInt> get() = polynomialRing { degrees }
    public override fun P.degreeBy(variable: V): UInt = polynomialRing { degreeBy(variable) }
    public override fun P.degreeBy(variables: Collection<V>): UInt = polynomialRing { degreeBy(variables) }
    public override val P.variables: Set<V> get() = polynomialRing { variables }
    public override val P.countOfVariables: Int get() = polynomialRing { countOfVariables }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        > : MultivariateRationalFunctionSpace<C, V, P, RF>,  PolynomialSpaceOfFractions<C, P, RF>() {

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

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverField<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        > : RationalFunctionSpace<C, P, RF> {
    
    // region Constant-Int operations
    @JvmName("divConstantInt")
    @JsName("divConstantInt")
    public operator fun C.div(other: Int): C = this / other.constantValue
    // endregion

    // region Constant-Long operations
    @JvmName("divConstantLong")
    @JsName("divConstantLong")
    public operator fun C.div(other: Long): C = this / other.constantValue
    // endregion

    // region Int-Constant operations
    @JvmName("divIntConstant")
    @JsName("divIntConstant")
    public operator fun Int.div(other: C): C = this.constantValue / other
    // endregion

    // region Long-Constant operations
    @JvmName("divLongConstant")
    @JsName("divLongConstant")
    public operator fun Long.div(other: C): C = this.constantValue / other
    // endregion

    // region Constant-Int operations
    @JvmName("divPolynomialInt")
    @JsName("divPolynomialInt")
    public operator fun P.div(other: Int): P = this / other.constantValue
    // endregion

    // region Constant-Long operations
    @JvmName("divPolynomialLong")
    @JsName("divPolynomialLong")
    public operator fun P.div(other: Long): P = this / other.constantValue
    // endregion

    // region Constant-Constant operations
    @JvmName("divConstantConstant")
    @JsName("divConstantConstant")
    public operator fun C.div(other: C): C
    @get:JvmName("reciprocalConstant")
    @get:JsName("reciprocalConstant")
    public val C.reciprocal: C get() = constantOne / this
    @JvmName("powerConstantInt")
    @JsName("powerConstantInt")
    public fun power(base: C, exponent: Int): C =
        if (exponent >= 0) power(base, exponent.toUInt())
        else constantOne / power(base, (-exponent).toUInt())
    @JvmName("powerConstantLong")
    @JsName("powerConstantLong")
    public fun power(base: C, exponent: Long): C =
        if (exponent >= 0) power(base, exponent.toULong())
        else constantOne / power(base, (-exponent).toULong())
    @JvmName("powConstantInt")
    @JsName("powConstantInt")
    public infix fun C.pow(exponent: Int): C = power(this, exponent)
    @JvmName("powConstantLong")
    @JsName("powConstantLong")
    public infix fun C.pow(exponent: Long): C = power(this, exponent)
    // endregion

    // region Polynomial-Constant operations
    @JvmName("divPolynomialConstant")
    @JsName("divPolynomialConstant")
    public operator fun P.div(other: C): P
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceWithPolynomialSpaceOverField<
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out PS: PolynomialSpaceOverField<C, P>,
        > : RationalFunctionSpaceOverField<C, P, RF>, RationalFunctionSpaceWithPolynomialSpace<C, P, RF, PS> {
    
    // region Constant-Int operations
    @JvmName("divConstantInt")
    public override operator fun C.div(other: Int): C = polynomialRing { this@div / other }
    // endregion

    // region Constant-Long operations
    @JvmName("divConstantLong")
    public override operator fun C.div(other: Long): C = polynomialRing { this@div / other }
    // endregion

    // region Int-Constant operations
    @JvmName("divIntConstant")
    public override operator fun Int.div(other: C): C = polynomialRing { this@div / other }
    // endregion

    // region Long-Constant operations
    @JvmName("divLongConstant")
    public override operator fun Long.div(other: C): C = polynomialRing { this@div / other }
    // endregion

    // region Constant-Int operations
    @JvmName("divPolynomialInt")
    public override operator fun P.div(other: Int): P = polynomialRing { this@div / other }
    // endregion

    // region Constant-Long operations
    @JvmName("divPolynomialLong")
    public override operator fun P.div(other: Long): P = polynomialRing { this@div / other }
    // endregion

    // region Constant-Constant operations
    @JvmName("divConstantConstant")
    public override operator fun C.div(other: C): C = polynomialRing { this@div / other }
    @get:JvmName("reciprocalConstant")
    public override val C.reciprocal: C get() = polynomialRing { this@reciprocal.reciprocal }
    @JvmName("powerConstantInt")
    public override fun power(base: C, exponent: Int): C = polynomialRing { power(base, exponent) }
    @JvmName("powerConstantLong")
    public override fun power(base: C, exponent: Long): C = polynomialRing { power(base, exponent) }
    @JvmName("powConstantInt")
    public override infix fun C.pow(exponent: Int): C = polynomialRing { this@pow pow exponent }
    @JvmName("powConstantLong")
    public override infix fun C.pow(exponent: Long): C = polynomialRing { this@pow pow exponent }
    // endregion

    // region Polynomial-Constant operations
    @JvmName("divPolynomialConstant")
    public override operator fun P.div(other: C): P = polynomialRing { this@div / other }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceOverField<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>
        > : RationalFunctionSpaceOverField<C, P, RF>, MultivariateRationalFunctionSpace<C, V, P, RF> {

    // region Variable-Int operations
    @JvmName("divVariableInt")
    public operator fun V.div(other: Int): P
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public operator fun V.div(other: Long): P
    // endregion

    // region Variable-Constant operations
    @JvmName("divVariableConstant")
    public operator fun V.div(other: C): P
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceWithMultivariatePolynomialSpaceOverField<
        C,
        V,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        out PS: MultivariatePolynomialSpaceOverField<C, V, P>,
        > : MultivariateRationalFunctionSpaceWithMultivariatePolynomialSpace<C, V, P, RF, PS>, RationalFunctionSpaceWithPolynomialSpaceOverField<C, P, RF, PS>, MultivariateRationalFunctionSpaceOverField<C, V, P, RF> {

    // region Variable-Int operations
    @JvmName("divVariableInt")
    public override operator fun V.div(other: Int): P = polynomialRing { this@div / other }
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public override operator fun V.div(other: Long): P = polynomialRing { this@div / other }
    // endregion

    // region Variable-Constant operations
    @JvmName("divVariableConstant")
    public override operator fun V.div(other: C): P = polynomialRing { this@div / other }
    // endregion
}
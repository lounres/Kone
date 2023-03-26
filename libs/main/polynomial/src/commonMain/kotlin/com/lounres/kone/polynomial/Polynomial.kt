/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.algebraic.util.*
import com.lounres.kone.context.invoke
import kotlin.js.JsName
import kotlin.jvm.JvmName


public interface Polynomial<C>

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P: Polynomial<C>> : Ring<P> {
    // region Constant constants
    public val constantZero: C
    public val constantOne: C
    // endregion

    // region Polynomial constants
    public override val zero: P
    public override val one: P
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

    public override infix fun P.equalsTo(other: P): Boolean = this == other
    // FIXME: KT-5351
    public override infix fun P.notEqualsTo(other: P): Boolean = !(this equalsTo other)
    public override infix fun P.eq(other: P): Boolean = this equalsTo other
    // FIXME: KT-5351
    public override infix fun P.neq(other: P): Boolean = !(this equalsTo other)
    public override fun P.isZero(): Boolean = this equalsTo zero
    public override fun P.isOne(): Boolean = this equalsTo one
    // endregion

    // region Integer-to-Constant conversion
    public fun constantValueOf(value: Int): C = constantOne * value
    public fun constantValueOf(value: Long): C = constantOne * value
    public val Int.constantValue: C get() = constantValueOf(this)
    public val Long.constantValue: C get() = constantValueOf(this)
    // endregion

    // region Integer-to-Polynomial conversion
    public override fun valueOf(value: Int): P = valueOf(constantValueOf(value))
    public override fun valueOf(value: Long): P = valueOf(constantValueOf(value))
    public override val Int.value: P get() = valueOf(this)
    public override val Long.value: P get() = valueOf(this)
    // endregion

    // region Constant-to-Polynomial conversion
    @JvmName("valueOfConstant")
    public fun valueOf(value: C): P = one * value
    @get:JvmName("valueConstant")
    public val C.value: P get() = valueOf(this)
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
    public override operator fun P.plus(other: Int): P
    public override operator fun P.minus(other: Int): P
    public override operator fun P.times(other: Int): P
    // endregion

    // region Polynomial-Long operations
    public override operator fun P.plus(other: Long): P
    public override operator fun P.minus(other: Long): P
    public override operator fun P.times(other: Long): P
    // endregion

    // region Int-Polynomial operations
    public override operator fun Int.plus(other: P): P
    public override operator fun Int.minus(other: P): P
    public override operator fun Int.times(other: P): P
    // endregion

    // region Long-Polynomial operations
    public override operator fun Long.plus(other: P): P
    public override operator fun Long.minus(other: P): P
    public override operator fun Long.times(other: P): P
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
    @JvmName("powerConstantUInt")
    @JsName("powerConstantUInt")
    public fun power(base: C, exponent: UInt): C = rightMultiplyByDoubling(base, exponent, ::constantOne) { left, right -> left * right }
    @JvmName("powerConstantULong")
    @JsName("powerConstantULong")
    public fun power(base: C, exponent: ULong): C = rightMultiplyByDoubling(base, exponent, ::constantOne) { left, right -> left * right }
    @JvmName("powConstantUInt")
    @JsName("powConstantUInt")
    public infix fun C.pow(exponent: UInt): C = power(this, exponent)
    @JvmName("powConstantULong")
    @JsName("powConstantULong")
    public infix fun C.pow(exponent: ULong): C = power(this, exponent)
    // endregion

    // region Constant-Polynomial operations
    public operator fun C.plus(other: P): P
    public operator fun C.minus(other: P): P
    public operator fun C.times(other: P): P
    // endregion

    // region Polynomial-Constant operations
    public operator fun P.plus(other: C): P
    public operator fun P.minus(other: C): P
    public operator fun P.times(other: C): P
    // endregion

    // region Polynomial-Polynomial operations
    public override operator fun P.unaryPlus(): P = this
    public override operator fun P.unaryMinus(): P
    public override operator fun P.plus(other: P): P
    public override operator fun P.minus(other: P): P
    public override operator fun P.times(other: P): P
    public override fun power(base: P, exponent: UInt): P = squaringPower(base, exponent)
    public override fun power(base: P, exponent: ULong): P = squaringPower(base, exponent)
    public override infix fun P.pow(exponent: UInt): P = power(this, exponent)
    public override infix fun P.pow(exponent: ULong): P = power(this, exponent)
    // endregion

    // region Polynomial properties
    public val P.degree: Int
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceWithRing<C, P: Polynomial<C>, out A: Ring<C>> : PolynomialSpace<C, P> {

    public val ring: A

    // region Constant constants
    public override val constantZero: C get() = ring.zero
    public override val constantOne: C get() = ring.one
    // endregion

    // region Constants equality
    @JvmName("equalsToConstantConstant")
    public override infix fun C.equalsTo(other: C): Boolean = with(ring) { this@equalsTo equalsTo other }
    @JvmName("eqConstantConstant")
    public override infix fun C.eq(other: C): Boolean = with(ring) { this@eq eq other }
    @JvmName("isZeroConstant")
    public override fun C.isZero(): Boolean = with(ring) { this@isZero.isZero() }
    @JvmName("isOneConstant")
    public override fun C.isOne(): Boolean = with(ring) { this@isOne.isOne() }
    @JvmName("isNotZeroConstant")
    public override fun C.isNotZero(): Boolean = with(ring) { this@isNotZero.isNotZero() }
    @JvmName("isNotOneConstant")
    public override fun C.isNotOne(): Boolean = with(ring) { this@isNotOne.isNotOne() }
    // endregion

    // region Integer-to-Constant conversion
    public override fun constantValueOf(value: Int): C = with(ring) { valueOf(value) }
    public override fun constantValueOf(value: Long): C = with(ring) { valueOf(value) }
    public override val Int.constantValue: C get() = with(ring) { this@constantValue.value }
    public override val Long.constantValue: C get() = with(ring) { this@constantValue.value }
    // endregion

    // region Constant-Int operations
    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = with(ring) { this@plus + other }
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = with(ring) { this@minus - other }
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = with(ring) { this@times * other }
    // endregion

    // region Constant-Long operations
    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Long): C = with(ring) { this@plus + other }
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Long): C = with(ring) { this@minus - other }
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Long): C = with(ring) { this@times * other }
    // endregion

    // region Int-Constant operations
    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = with(ring) { this@plus + other }
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = with(ring) { this@minus - other }
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = with(ring) { this@times * other }
    // endregion

    // region Long-Constant operations
    @JvmName("plusIntConstant")
    public override operator fun Long.plus(other: C): C = with(ring) { this@plus + other }
    @JvmName("minusIntConstant")
    public override operator fun Long.minus(other: C): C = with(ring) { this@minus - other }
    @JvmName("timesIntConstant")
    public override operator fun Long.times(other: C): C = with(ring) { this@times * other }
    // endregion

    // region Constant-Constant operations
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = with(ring) { -this@unaryMinus }
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = with(ring) { this@plus + other }
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = with(ring) { this@minus - other }
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = with(ring) { this@times * other }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: UInt): C = with(ring) { power(base, exponent) }
    @JvmName("powerConstant")
    public override fun power(base: C, exponent: ULong): C = with(ring) { power(base, exponent) }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: UInt): C = with(ring) { this@pow pow exponent }
    @JvmName("powConstant")
    public override infix fun C.pow(exponent: ULong): C = with(ring) { this@pow pow exponent }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, P: Polynomial<C>>: PolynomialSpace<C, P> {
    // region Variable-to-Polynomial conversion
    @JvmName("valueOfVariable")
    public fun valueOf(variable: V): P = +variable
    @get:JvmName("valueVariable")
    public val V.value: P get() = valueOf(this)
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

    // region Variable-Variable operations
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

    // Polynomial properties
    public val P.degrees: Map<V, UInt>
    public fun P.degreeBy(variable: V): UInt = degrees.getOrElse(variable) { 0u }
    public fun P.degreeBy(variables: Collection<V>): UInt
    public val P.variables: Set<V> get() = degrees.keys
    public val P.countOfVariables: Int get() = variables.size
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceOverField<C, P: Polynomial<C>> : PolynomialSpace<C, P> {
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
public interface PolynomialSpaceWithField<C, P: Polynomial<C>, out A: Field<C>> : PolynomialSpaceOverField<C, P>, PolynomialSpaceWithRing<C, P, A> {
    // region Constant-Int operations
    @JvmName("divConstantInt")
    public override operator fun C.div(other: Int): C = ring { this@div / other }
    // endregion

    // region Constant-Long operations
    @JvmName("divConstantLong")
    public override operator fun C.div(other: Long): C = ring { this@div / other }
    // endregion

    // region Int-Constant operations
    @JvmName("divIntConstant")
    public override operator fun Int.div(other: C): C = ring { this@div / other }
    // endregion

    // region Long-Constant operations
    @JvmName("divLongConstant")
    public override operator fun Long.div(other: C): C = ring { this@div / other }
    // endregion

    // region Constant-Constant operations
    @JvmName("divConstantConstant")
    public override operator fun C.div(other: C): C = ring { this@div / other }
    public override val C.reciprocal: C get() = ring { this@reciprocal.reciprocal }
    @JvmName("powerConstantInt")
    public override fun power(base: C, exponent: Int): C = ring { power(base, exponent) }
    @JvmName("powerConstantLong")
    public override fun power(base: C, exponent: Long): C = ring { power(base, exponent) }
    @JvmName("powConstantInt")
    public override infix fun C.pow(exponent: Int): C = ring { this@pow pow exponent }
    @JvmName("powConstantLong")
    public override infix fun C.pow(exponent: Long): C = ring { this@pow pow exponent }
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<C, V, P: Polynomial<C>>: PolynomialSpaceOverField<C, P>, MultivariatePolynomialSpace<C, V, P> {
    // region Variable-Int operations
    @JvmName("divVariableInt")
    public operator fun V.div(other: Int): P = this * other.constantValue.reciprocal
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public operator fun V.div(other: Long): P = this * other.constantValue.reciprocal
    // endregion

    // region Variable-Constant operations
    @JvmName("divVariableConstant")
    public operator fun V.div(other: C): P = this * other.reciprocal
    // endregion
}
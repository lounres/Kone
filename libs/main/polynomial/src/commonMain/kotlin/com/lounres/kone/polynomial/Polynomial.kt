/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import kotlin.js.JsName
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P, out A: Ring<C>> : Ring<P> {
    // region Context accessors
    public val constantRing: A
    // endregion

    // region Constant constants
    public val constantZero: C get() = constantRing.zero
    public val constantOne: C get() = constantRing.one
    // endregion

    // region Polynomial constants
    public val polynomialZero: P get() = zero
    public val polynomialOne: P get() = one
    // endregion

    // region Integer-to-Constant conversion
    public fun constantValueOf(value: Int): C = constantRing.run { valueOf(value) }
    public fun constantValueOf(value: Long): C = constantRing.run { valueOf(value) }
    public val Int.constantValue: C get() = constantRing.run { this@constantValue.value }
    public val Long.constantValue: C get() = constantRing.run { this@constantValue.value }
    // endregion

    // region Integer-to-Polynomial conversion
    public override fun valueOf(value: Int): P = polynomialValueOf(constantValueOf(value))
    public override fun valueOf(value: Long): P = polynomialValueOf(constantValueOf(value))
    public fun polynomialValueOf(value: Int): P = valueOf(value)
    public fun polynomialValueOf(value: Long): P = valueOf(value)
    public val Int.polynomialValue: P get() = polynomialValueOf(this)
    public val Long.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Constant-to-Polynomial conversion
    @JvmName("polynomialValueOfConstant")
    public fun polynomialValueOf(value: C): P = one * value
    @get:JvmName("polynomialValueConstant")
    public val C.polynomialValue: P get() = polynomialValueOf(this)
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

    // region Polynomial-Polynomial operations
    public override operator fun P.unaryPlus(): P = this
    public override operator fun P.unaryMinus(): P
    public override operator fun P.plus(other: P): P
    public override operator fun P.minus(other: P): P
    public override operator fun P.times(other: P): P
    // endregion

    // region Polynomial properties
    public val P.degree: Int
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, P, out A: Ring<C>>: PolynomialSpace<C, P, A> {
    // region Variable-to-Polynomial conversion
    @JvmName("valueOfVariable")
    public fun polynomialValueOf(variable: V): P = +variable
    @get:JvmName("valueVariable")
    public val V.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public operator fun V.plus(other: Int): P = this + other.constantValue
    @JvmName("minusVariableInt")
    public operator fun V.minus(other: Int): P = this - other.constantValue
    @JvmName("timesVariableInt")
    public operator fun V.times(other: Int): P = this * other.constantValue
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public operator fun V.plus(other: Long): P = this + other.constantValue
    @JvmName("minusVariableLong")
    public operator fun V.minus(other: Long): P = this - other.constantValue
    @JvmName("timesVariableLong")
    public operator fun V.times(other: Long): P = this * other.constantValue
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: V): P = this.constantValue + other
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: V): P = this.constantValue - other
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: V): P = this.constantValue * other
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: V): P = this.constantValue + other
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: V): P = this.constantValue - other
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: V): P = this.constantValue * other
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

    // region Polynomial properties
    public val P.degrees: Map<V, UInt>
    public fun P.degreeBy(variable: V): UInt = degrees.getOrElse(variable) { 0u }
    public fun P.degreeBy(variables: Collection<V>): UInt
    public val P.variables: Set<V> get() = degrees.keys
    public val P.countOfVariables: Int get() = variables.size
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceOverField<C, P, out A: Field<C>> : PolynomialSpace<C, P, A> {
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

    // region Polynomial-Constant operations
    @JvmName("divPolynomialConstant")
    @JsName("divPolynomialConstant")
    public operator fun P.div(other: C): P
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<C, V, P, out A: Field<C>>: PolynomialSpaceOverField<C, P, A>, MultivariatePolynomialSpace<C, V, P, A> {
    // region Variable-Int operations
    @JvmName("divVariableInt")
    public operator fun V.div(other: Int): P = this * constantRing.run { other.constantValue.reciprocal }
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public operator fun V.div(other: Long): P = this * constantRing.run { other.constantValue.reciprocal }
    // endregion

    // region Variable-Constant operations
    @JvmName("divVariableConstant")
    public operator fun V.div(other: C): P = this * constantRing.run { other.reciprocal }
    // endregion
}
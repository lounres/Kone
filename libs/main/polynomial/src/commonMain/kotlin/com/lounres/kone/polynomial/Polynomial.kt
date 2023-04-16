/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import kotlin.js.JsName
import kotlin.jvm.JvmName


public interface Polynomial<C>

context(A)
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P: Polynomial<C>, out A: Ring<C>> : Ring<P> {
    // region Context accessors
    public val ring: A get() = this@A
    // endregion

    // region Constant constants
    public val constantZero: C get() = ring.zero
    public val constantOne: C get() = ring.one
    // endregion

    // region Integer-to-Constant conversion
    public fun constantValueOf(value: Int): C = ring.run { valueOf(value) }
    public fun constantValueOf(value: Long): C = ring.run { valueOf(value) }
    public val Int.constantValue: C get() = ring.run { this@constantValue.value }
    public val Long.constantValue: C get() = ring.run { this@constantValue.value }
    // endregion

    // region Integer-to-Polynomial conversion
    public override fun valueOf(value: Int): P = valueOf(constantValueOf(value))
    public override fun valueOf(value: Long): P = valueOf(constantValueOf(value))
    // endregion

    // region Constant-to-Polynomial conversion
    @JvmName("valueOfConstant")
    public fun valueOf(value: C): P = one * value
    @get:JvmName("valueConstant")
    public val C.value: P get() = valueOf(this)
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
    // endregion

    // region Polynomial properties
    public val P.degree: Int
    // endregion
}

context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, P: Polynomial<C>, out A: Ring<C>>: PolynomialSpace<C, P, A> {
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

context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceOverField<C, P: Polynomial<C>, out A: Field<C>> : PolynomialSpace<C, P, A> {
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

context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<C, V, P: Polynomial<C>, out A: Field<C>>: PolynomialSpaceOverField<C, P, A>, MultivariatePolynomialSpace<C, V, P, A> {
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
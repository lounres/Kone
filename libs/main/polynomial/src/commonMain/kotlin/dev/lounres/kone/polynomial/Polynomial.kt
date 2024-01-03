/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import kotlin.js.JsName
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<N, P, out A: Ring<N>> : Ring<P> {
    public val numericalRing: A

    // region Number constants
    public val numericalZero: N get() = numericalRing.zero
    public val numericalOne: N get() = numericalRing.one
    // endregion

    // region Polynomial constants
    public val polynomialZero: P get() = zero
    public val polynomialOne: P get() = one
    // endregion

    // region Integer-to-Number conversion
    public fun numericalValueOf(value: Int): N = numericalRing.run { valueOf(value) }
    public fun numericalValueOf(value: Long): N = numericalRing.run { valueOf(value) }
    public val Int.numericalValue: N get() = numericalRing.run { this@numericalValue.value }
    public val Long.numericalValue: N get() = numericalRing.run { this@numericalValue.value }
    // endregion

    // region Integer-to-Polynomial conversion
    public override fun valueOf(value: Int): P = polynomialValueOf(numericalValueOf(value))
    public override fun valueOf(value: Long): P = polynomialValueOf(numericalValueOf(value))
    public fun polynomialValueOf(value: Int): P = valueOf(value)
    public fun polynomialValueOf(value: Long): P = valueOf(value)
    public val Int.polynomialValue: P get() = polynomialValueOf(this)
    public val Long.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Number-to-Polynomial conversion
    @JvmName("polynomialValueOfNumber")
    public fun polynomialValueOf(value: N): P = one * value
    @get:JvmName("polynomialValueNumber")
    public val N.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Number-Polynomial operations
    @JvmName("plusNumberPolynomial")
    public operator fun N.plus(other: P): P
    @JvmName("minusNumberPolynomial")
    public operator fun N.minus(other: P): P
    @JvmName("timesNumberPolynomial")
    public operator fun N.times(other: P): P
    // endregion

    // region Polynomial-Number operations
    @JvmName("plusPolynomialNumber")
    public operator fun P.plus(other: N): P
    @JvmName("minusPolynomialNumber")
    public operator fun P.minus(other: N): P
    @JvmName("timesPolynomialNumber")
    public operator fun P.times(other: N): P
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
public interface MultivariatePolynomialSpace<N, V, P, out A: Ring<N>>: PolynomialSpace<N, P, A> {
    // region Variable-to-Polynomial conversion
    @JvmName("valueOfVariable")
    public fun polynomialValueOf(variable: V): P = +variable
    @get:JvmName("valueVariable")
    public val V.polynomialValue: P get() = polynomialValueOf(this)
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public operator fun V.plus(other: Int): P = this + other.numericalValue
    @JvmName("minusVariableInt")
    public operator fun V.minus(other: Int): P = this - other.numericalValue
    @JvmName("timesVariableInt")
    public operator fun V.times(other: Int): P = this * other.numericalValue
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public operator fun V.plus(other: Long): P = this + other.numericalValue
    @JvmName("minusVariableLong")
    public operator fun V.minus(other: Long): P = this - other.numericalValue
    @JvmName("timesVariableLong")
    public operator fun V.times(other: Long): P = this * other.numericalValue
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: V): P = this.numericalValue + other
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: V): P = this.numericalValue - other
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: V): P = this.numericalValue * other
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: V): P = this.numericalValue + other
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: V): P = this.numericalValue - other
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: V): P = this.numericalValue * other
    // endregion

    // region Variable-Number operations
    @JvmName("plusVariableNumber")
    public operator fun V.plus(other: N): P
    @JvmName("minusVariableNumber")
    public operator fun V.minus(other: N): P
    @JvmName("timesVariableNumber")
    public operator fun V.times(other: N): P
    // endregion

    // region Number-Variable operations
    @JvmName("plusNumberVariable")
    public operator fun N.plus(other: V): P
    @JvmName("minusNumberVariable")
    public operator fun N.minus(other: V): P
    @JvmName("timesNumberVariable")
    public operator fun N.times(other: V): P
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
public interface PolynomialSpaceOverField<N, P, out A: Field<N>> : PolynomialSpace<N, P, A> {
    // region Number-Int operations
    @JvmName("divPolynomialInt")
    @JsName("divPolynomialInt")
    public operator fun P.div(other: Int): P = this / other.numericalValue
    // endregion

    // region Number-Long operations
    @JvmName("divPolynomialLong")
    @JsName("divPolynomialLong")
    public operator fun P.div(other: Long): P = this / other.numericalValue
    // endregion

    // region Polynomial-Number operations
    @JvmName("divPolynomialNumber")
    @JsName("divPolynomialNumber")
    public operator fun P.div(other: N): P
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<N, V, P, out A: Field<N>>: PolynomialSpaceOverField<N, P, A>, MultivariatePolynomialSpace<N, V, P, A> {
    // region Variable-Int operations
    @JvmName("divVariableInt")
    public operator fun V.div(other: Int): P = numericalRing { this * other.numericalValue.reciprocal }
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public operator fun V.div(other: Long): P = numericalRing { this * other.numericalValue.reciprocal }
    // endregion

    // region Variable-Number operations
    @JvmName("divVariableNumber")
    public operator fun V.div(other: N): P = numericalRing { this * other.reciprocal }
    // endregion
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import fix.kotlin.js.JsName
//import kotlin.js.JsName
import kotlin.jvm.JvmName


public data class VariablePower<V>(val variable: V, val degree: UInt)
public data class Monomial<MS, C>(val signature: MS, val coefficient: C)

context(A)
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P, out A: Ring<C>> : Ring<P> {
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
    public val P.degree: UInt
    // endregion
}

context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, VP, MS, M, P, out A: Ring<C>>: PolynomialSpace<C, P, A> {
    // region Variable-to-Polynomial conversion
    @JvmName("valueOfVariable")
    public fun valueOf(variable: V): P = TODO("Default action is not yet implemented")
    @get:JvmName("valueVariable")
    public val V.value: P get() = valueOf(this)
    // endregion

    // region Variable-power-to-Polynomial conversion
    @JvmName("valueOfVariablePower")
    public fun valueOf(variable: VP): P = TODO("Default action is not yet implemented")
    @get:JvmName("valueVariablePower")
    public val VP.value: P get() = valueOf(this)
    // endregion

    // region Monomial-signature-to-Polynomial conversion
    @JvmName("valueOfMonomialSignature")
    public fun valueOf(variable: MS): P = TODO("Default action is not yet implemented")
    @get:JvmName("valueMonomialSignature")
    public val MS.value: P get() = valueOf(this)
    // endregion

    // region Monomial-to-Polynomial conversion
    @JvmName("valueOfMonomial")
    public fun valueOf(variable: M): P = TODO("Default action is not yet implemented")
    @get:JvmName("valueMonomial")
    public val M.value: P get() = valueOf(this)
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public operator fun V.plus(other: Int): P = this + other.constantValue
    @JvmName("minusVariableInt")
    public operator fun V.minus(other: Int): P = this - other.constantValue
    @JvmName("timesVariableInt")
    public operator fun V.times(other: Int): M = this * other.constantValue
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public operator fun V.plus(other: Long): P = this + other.constantValue
    @JvmName("minusVariableLong")
    public operator fun V.minus(other: Long): P = this - other.constantValue
    @JvmName("timesVariableLong")
    public operator fun V.times(other: Long): M = this * other.constantValue
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: V): P = this.constantValue + other
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: V): P = this.constantValue - other
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: V): M = this.constantValue * other
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: V): P = this.constantValue + other
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: V): P = this.constantValue - other
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: V): M = this.constantValue * other
    // endregion

    // region VariablePower-Int operations
    @JvmName("plusVariablePowerInt")
    public operator fun VP.plus(other: Int): P = this + other.constantValue
    @JvmName("minusVariablePowerInt")
    public operator fun VP.minus(other: Int): P = this - other.constantValue
    @JvmName("timesVariablePowerInt")
    public operator fun VP.times(other: Int): M = this * other.constantValue
    // endregion

    // region VariablePower-Long operations
    @JvmName("plusVariablePowerLong")
    public operator fun VP.plus(other: Long): P = this + other.constantValue
    @JvmName("minusVariablePowerLong")
    public operator fun VP.minus(other: Long): P = this - other.constantValue
    @JvmName("timesVariablePowerLong")
    public operator fun VP.times(other: Long): M = this * other.constantValue
    // endregion

    // region Int-VariablePower operations
    @JvmName("plusIntVariablePower")
    public operator fun Int.plus(other: VP): P = this.constantValue + other
    @JvmName("minusIntVariablePower")
    public operator fun Int.minus(other: VP): P = this.constantValue - other
    @JvmName("timesIntVariablePower")
    public operator fun Int.times(other: VP): M = this.constantValue * other
    // endregion

    // region Long-VariablePower operations
    @JvmName("plusLongVariablePower")
    public operator fun Long.plus(other: VP): P = this.constantValue + other
    @JvmName("minusLongVariablePower")
    public operator fun Long.minus(other: VP): P = this.constantValue - other
    @JvmName("timesLongVariablePower")
    public operator fun Long.times(other: VP): M = this.constantValue * other
    // endregion

    // region MonomialSignature-Int operations
    @JvmName("plusMonomialSignatureInt")
    public operator fun MS.plus(other: Int): P = this + other.constantValue
    @JvmName("minusMonomialSignatureInt")
    public operator fun MS.minus(other: Int): P = this - other.constantValue
    @JvmName("timesMonomialSignatureInt")
    public operator fun MS.times(other: Int): M = this * other.constantValue
    // endregion

    // region MonomialSignature-Long operations
    @JvmName("plusMonomialSignatureLong")
    public operator fun MS.plus(other: Long): P = this + other.constantValue
    @JvmName("minusMonomialSignatureLong")
    public operator fun MS.minus(other: Long): P = this - other.constantValue
    @JvmName("timesMonomialSignatureLong")
    public operator fun MS.times(other: Long): M = this * other.constantValue
    // endregion

    // region Int-MonomialSignature operations
    @JvmName("plusIntMonomialSignature")
    public operator fun Int.plus(other: MS): P = this.constantValue + other
    @JvmName("minusIntMonomialSignature")
    public operator fun Int.minus(other: MS): P = this.constantValue - other
    @JvmName("timesIntMonomialSignature")
    public operator fun Int.times(other: MS): M = this.constantValue * other
    // endregion

    // region Long-MonomialSignature operations
    @JvmName("plusLongMonomialSignature")
    public operator fun Long.plus(other: MS): P = this.constantValue + other
    @JvmName("minusLongMonomialSignature")
    public operator fun Long.minus(other: MS): P = this.constantValue - other
    @JvmName("timesLongMonomialSignature")
    public operator fun Long.times(other: MS): M = this.constantValue * other
    // endregion

    // region Monomial-Int operations
    @JvmName("plusMonomialInt")
    public operator fun M.plus(other: Int): P = this + other.constantValue
    @JvmName("minusMonomialInt")
    public operator fun M.minus(other: Int): P = this - other.constantValue
    @JvmName("timesMonomialInt")
    public operator fun M.times(other: Int): M = this * other.constantValue
    // endregion

    // region Monomial-Long operations
    @JvmName("plusMonomialLong")
    public operator fun M.plus(other: Long): P = this + other.constantValue
    @JvmName("minusMonomialLong")
    public operator fun M.minus(other: Long): P = this - other.constantValue
    @JvmName("timesMonomialLong")
    public operator fun M.times(other: Long): M = this * other.constantValue
    // endregion

    // region Int-Monomial operations
    @JvmName("plusIntMonomial")
    public operator fun Int.plus(other: M): P = this.constantValue + other
    @JvmName("minusIntMonomial")
    public operator fun Int.minus(other: M): P = this.constantValue - other
    @JvmName("timesIntMonomial")
    public operator fun Int.times(other: M): M = this.constantValue * other
    // endregion

    // region Long-Monomial operations
    @JvmName("plusLongMonomial")
    public operator fun Long.plus(other: M): P = this.constantValue + other
    @JvmName("minusLongMonomial")
    public operator fun Long.minus(other: M): P = this.constantValue - other
    @JvmName("timesLongMonomial")
    public operator fun Long.times(other: M): M = this.constantValue * other
    // endregion

    // region Variable-Constant operations
    @JvmName("plusVariableConstant")
    public operator fun V.plus(other: C): P
    @JvmName("minusVariableConstant")
    public operator fun V.minus(other: C): P
    @JvmName("timesVariableConstant")
    public operator fun V.times(other: C): M
    // endregion

    // region Constant-Variable operations
    @JvmName("plusConstantVariable")
    public operator fun C.plus(other: V): P
    @JvmName("minusConstantVariable")
    public operator fun C.minus(other: V): P
    @JvmName("timesConstantVariable")
    public operator fun C.times(other: V): M
    // endregion

    // region VariablePower-Constant operations
    @JvmName("plusVariablePowerConstant")
    public operator fun VP.plus(other: C): P
    @JvmName("minusVariablePowerConstant")
    public operator fun VP.minus(other: C): P
    @JvmName("timesVariablePowerConstant")
    public operator fun VP.times(other: C): M
    // endregion

    // region Constant-VariablePower operations
    @JvmName("plusConstantVariablePower")
    public operator fun C.plus(other: VP): P
    @JvmName("minusConstantVariablePower")
    public operator fun C.minus(other: VP): P
    @JvmName("timesConstantVariablePower")
    public operator fun C.times(other: VP): M
    // endregion

    // region MonomialSignature-Constant operations
    @JvmName("plusMonomialSignatureConstant")
    public operator fun MS.plus(other: C): P
    @JvmName("minusMonomialSignatureConstant")
    public operator fun MS.minus(other: C): P
    @JvmName("timesMonomialSignatureConstant")
    public operator fun MS.times(other: C): M
    // endregion

    // region Constant-MonomialSignature operations
    @JvmName("plusConstantMonomialSignature")
    public operator fun C.plus(other: MS): P
    @JvmName("minusConstantMonomialSignature")
    public operator fun C.minus(other: MS): P
    @JvmName("timesConstantMonomialSignature")
    public operator fun C.times(other: MS): M
    // endregion

    // region Monomial-Constant operations
    @JvmName("plusMonomialConstant")
    public operator fun M.plus(other: C): P
    @JvmName("minusMonomialConstant")
    public operator fun M.minus(other: C): P
    @JvmName("timesMonomialConstant")
    public operator fun M.times(other: C): M
    // endregion

    // region Constant-Monomial operations
    @JvmName("plusConstantMonomial")
    public operator fun C.plus(other: M): P
    @JvmName("minusConstantMonomial")
    public operator fun C.minus(other: M): P
    @JvmName("timesConstantMonomial")
    public operator fun C.times(other: M): M
    // endregion

    // region Variable-Variable operations
    @JvmName("unaryPlusVariable")
    public operator fun V.unaryPlus(): M
    @JvmName("unaryMinusVariable")
    public operator fun V.unaryMinus(): M
    @JvmName("plusVariableVariable")
    public operator fun V.plus(other: V): P
    @JvmName("minusVariableVariable")
    public operator fun V.minus(other: V): P
    @JvmName("timesVariableVariable")
    public operator fun V.times(other: V): MS
    // endregion

    // region Variable-Signature operations
    @JvmName("plusVariableSignature")
    public operator fun V.plus(other: VP): P
    @JvmName("minusVariableSignature")
    public operator fun V.minus(other: VP): P
    @JvmName("timesVariableSignature")
    public operator fun V.times(other: VP): MS
    // endregion

    // region Constant-Variable operations
    @JvmName("plusSignatureVariable")
    public operator fun VP.plus(other: V): P
    @JvmName("minusSignatureVariable")
    public operator fun VP.minus(other: V): P
    @JvmName("timesSignatureVariable")
    public operator fun VP.times(other: V): MS
    // endregion

    // region Variable-Signature operations
    @JvmName("plusVariableSignature")
    public operator fun V.plus(other: MS): P
    @JvmName("minusVariableSignature")
    public operator fun V.minus(other: MS): P
    @JvmName("timesVariableSignature")
    public operator fun V.times(other: MS): MS
    // endregion

    // region Constant-Variable operations
    @JvmName("plusSignatureVariable")
    public operator fun MS.plus(other: V): P
    @JvmName("minusSignatureVariable")
    public operator fun MS.minus(other: V): P
    @JvmName("timesSignatureVariable")
    public operator fun MS.times(other: V): MS
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
    public val VP.variable: V
    public val VP.power: UInt
    public fun variablePower(variable: V, power: UInt): VP

    public val MS.powers: Map<V, UInt>
    public val MS.powersCount: UInt
    public val MS.variables: Set<V>
    public fun MS.asIterable(): Iterable<VP>
    public operator fun MS.iterator(): Iterator<VP>
    public operator fun MS.get(signature: V): UInt
    public fun signatureOf(coefficients: Map<V, UInt>): MS
    public fun signatureOf(coefficients: Collection<VP>): MS
    public fun signatureOf(vararg coefficients: VP): MS

    public val P.coefficients: Map<MS, C>
    public val P.monomialCount: UInt
    public val P.signatures: Set<MS>
    public fun P.asIterable(): Iterable<M>
    public operator fun P.iterator(): Iterator<M>
    public operator fun P.get(signature: MS): C
    public fun polynomialOf(coefficients: Map<MS, C>): P
    public fun polynomialOf(coefficients: Collection<M>): P
    public fun polynomialOf(vararg coefficients: M): P
    // endregion
}

context(A)
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

context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<C, V, MS, P, out A: Field<C>>: PolynomialSpaceOverField<C, P, A>, MultivariatePolynomialSpace<C, V, P, A> {
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
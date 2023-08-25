/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.optional.Option
import com.lounres.kone.polynomial.MultivariatePolynomialSpace
import com.lounres.kone.polynomial.Polynomial
import kotlin.math.max


context(A)
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialManipulationSpace<C, V, VP, MS, MutMS: MS, M, P: Polynomial<C>, MutP: P, out A: Ring<C>>: MultivariatePolynomialSpace<C, V, P, A> {
    // region Manipulation
    public fun variablePower(variable: V, power: UInt): VP
    public val VP.variable: V
    public val VP.power: UInt
    @JvmName("VP_component1")
    public operator fun VP.component1(): V = variable
    @JvmName("VP_component2")
    public operator fun VP.component2(): UInt = power

    public fun signatureOf(vararg variablePowers: VP): MS
    public fun signatureOf(variablePowers: Collection<VP>): MS
    public val MS.size: Int
    public fun MS.isEmpty(): Boolean = size == 0
    public infix fun MS.containsVariable(variable: V): Boolean
    public operator fun MS.get(variable: V): UInt
    public fun MS.getOption(variable: V): Option<UInt>
    public val MS.variables: Set<V>
    public val MS.powers: Set<VP>
    public operator fun MS.iterator(): Iterator<VP> = powers.iterator()

    public fun mutableSignatureOf(vararg variablePowers: VP): MutMS
    public fun mutableSignatureOf(variablePowers: Collection<VP>): MutMS
    public fun MutMS.getAndSet(variable: V, power: UInt): UInt
    public operator fun MutMS.set(variable: V, power: UInt)
    public fun MutMS.getAndRemove(variable: V): Option<UInt>
    public fun MutMS.remove(variable: V)
    public fun MS.toMutable(): MutMS

    public fun monomial(signature: MS, coefficient: C): M
    public val M.signature: MS
    public val M.coefficient: C
    @JvmName("M_component2")
    public operator fun M.component1(): MS = signature
    @JvmName("M_component1")
    public operator fun M.component2(): C = coefficient

    public fun polynomialOf(vararg monomials: M): P
    public fun polynomialOf(monomials: Collection<M>): P
    public val P.size: Int
    public fun P.isEmpty(): Boolean
    public infix fun P.containsSignature(signature: MS): Boolean
    public operator fun P.get(signature: MS): C
    public fun P.getOption(signature: MS): Option<C>
    public val P.signatures: Set<MS>
    public val P.monomials: Set<M>
    public operator fun P.iterator(): Iterator<M>
    public fun P.toMutable(): MutP

    public fun mutablePolynomialOf(vararg monomials: M): MutP
    public fun mutablePolynomialOf(monomials: Collection<M>): MutP
    public fun MutP.getAndSet(signature: MS, coefficient: C): Option<C>
    public operator fun MutP.set(signature: MS, coefficient: C)
    public fun MutP.getAndRemove(signature: MS): Option<C>
    public fun MutP.remove(signature: MS)
    // endregion

    @JvmName("polynomialValueOfConstant")
    public override fun polynomialValueOf(value: C): P = polynomialOf(monomial(signatureOf(), value))
    @JvmName("valueOfVariable")
    public override fun polynomialValueOf(variable: V): P = polynomialOf(monomial(signatureOf(variablePower(variable, 1u)), constantOne))

    @JvmName("plusVariableConstant")
    public override operator fun V.plus(other: C): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), constantOne),
        monomial(signatureOf(), other),
    )
    @JvmName("minusVariableConstant")
    public override operator fun V.minus(other: C): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), constantOne),
        monomial(signatureOf(), -other),
    )
    @JvmName("timesVariableConstant")
    public override operator fun V.times(other: C): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), other),
    )

    @JvmName("plusConstantVariable")
    public override operator fun C.plus(other: V): P = polynomialOf(
        monomial(signatureOf(variablePower(other, 1u)), constantOne),
        monomial(signatureOf(), this),
    )
    @JvmName("minusConstantVariable")
    public override operator fun C.minus(other: V): P = polynomialOf(
        monomial(signatureOf(variablePower(other, 1u)), constantOne),
        monomial(signatureOf(), -this),
    )
    @JvmName("timesConstantVariable")
    public override operator fun C.times(other: V): P = polynomialOf(
        monomial(signatureOf(variablePower(other, 1u)), this),
    )

    public override operator fun C.plus(other: P): P = other.toMutable().apply {
        val emptySignature = signatureOf()
        if (this containsSignature emptySignature) this[emptySignature] = this@C + this[emptySignature]
        else this[emptySignature] = this@C
    }
    public override operator fun C.minus(other: P): P = other.toMutable().apply {
        for ((signature, coefficient) in this)
            if (!signature.isEmpty()) this[signature] = -coefficient

        val emptySignature = signatureOf()
        if (this containsSignature emptySignature) this[emptySignature] = this@C - this[emptySignature]
        else this[emptySignature] = this@C
    }
    public override operator fun C.times(other: P): P = other.toMutable().apply {
        for ((signature, coefficient) in this) this[signature] = this@C * coefficient
    }

    public override operator fun P.plus(other: C): P = this.toMutable().apply {
        val emptySignature = signatureOf()
        if (this containsSignature emptySignature) this[emptySignature] = this[emptySignature] + other
        else this[emptySignature] = other
    }
    public override operator fun P.minus(other: C): P = this.toMutable().apply {
        val emptySignature = signatureOf()
        if (this containsSignature emptySignature) this[emptySignature] = this[emptySignature] - other
        else this[emptySignature] = -other
    }
    public override operator fun P.times(other: C): P = this.toMutable().apply {
        for ((signature, coefficient) in this) this[signature] = coefficient * other
    }

    @JvmName("unaryPlusVariable")
    public override operator fun V.unaryPlus(): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), constantOne)
    )
    @JvmName("unaryMinusVariable")
    public override operator fun V.unaryMinus(): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), -constantOne)
    )
    @JvmName("plusVariableVariable")
    public override operator fun V.plus(other: V): P =
        if (this == other) polynomialOf(monomial(signatureOf(variablePower(this, 2u)), constantOne))
        else polynomialOf(
            monomial(signatureOf(variablePower(this, 2u)), constantOne),
            monomial(signatureOf(variablePower(other, 1u)), constantOne)
        )
    @JvmName("minusVariableVariable")
    public override operator fun V.minus(other: V): P =
        if (this == other) polynomialOf()
        else polynomialOf(
            monomial(signatureOf(variablePower(this, 2u)), constantOne),
            monomial(signatureOf(variablePower(other, 1u)), -constantOne)
        )
    @JvmName("timesVariableVariable")
    public override operator fun V.times(other: V): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u), variablePower(other, 1u)), constantOne),
    )

    @JvmName("plusVariablePolynomial")
    public override operator fun V.plus(other: P): P = other.toMutable().apply {
        val signature = signatureOf(variablePower(this@V, 1u))
        if (this containsSignature signature) this[signature] = constantOne + this[signature]
        else this[signature] = constantOne
    }
    @JvmName("minusVariablePolynomial")
    public override operator fun V.minus(other: P): P = other.toMutable().apply {
        val theSignature = signatureOf(variablePower(this@V, 1u))

        for ((signature, coefficient) in this)
            if (signature != theSignature) this[signature] = -coefficient

        if (this containsSignature theSignature) this[theSignature] = constantOne - this[theSignature]
        else this[theSignature] = constantOne
    }
    @JvmName("timesVariablePolynomial")
    public override operator fun V.times(other: P): P = /*mutablePolynomialOf().apply {
        for ((signature, coefficient) in other) this[signature + this@V] = coefficient
    }*/ TODO("Not yet implemented")

    @JvmName("plusPolynomialVariable")
    public override operator fun P.plus(other: V): P = this.toMutable().apply {
        val signature = signatureOf(variablePower(other, 1u))
        if (this containsSignature signature) this[signature] = this[signature] + constantOne
        else this[signature] = constantOne
    }
    @JvmName("minusPolynomialVariable")
    public override operator fun P.minus(other: V): P = this.toMutable().apply {
        val signature = signatureOf(variablePower(other, 1u))
        if (this containsSignature signature) this[signature] = this[signature] - constantOne
        else this[signature] = -constantOne
    }
    @JvmName("timesPolynomialVariable")
    public override operator fun P.times(other: V): P = /*mutablePolynomialOf().apply {
        for ((signature, coefficient) in this@P) this[signature + other] = coefficient
    }*/ TODO("Not yet implemented")

    public override operator fun P.unaryMinus(): P = this.toMutable().apply {
        for ((signature, coefficient) in this)
            if (!signature.isEmpty()) this[signature] = -coefficient
    }
    public override operator fun P.plus(other: P): P = this.toMutable().apply {
        for ((signature, coefficient) in other)
            if (this containsSignature signature) this[signature] = this[signature] + coefficient
            else this[signature] = coefficient
    }
    public override operator fun P.minus(other: P): P = this.toMutable().apply {
        for ((signature, coefficient) in other)
            if (this containsSignature signature) this[signature] = this[signature] - coefficient
            else this[signature] = -coefficient
    }
    public override operator fun P.times(other: P): P = mutablePolynomialOf().apply {
        for ((signature1, coefficient1) in this@P) for ((signature2, coefficient2) in other)
            TODO("Not yet implemented")
    }

    public override val P.degree: Int
        get() {
            var max = -1
            for ((signature) in this) max = max(max, signature.powers.sumOf { it.power }.toInt())
            return max
        }
}
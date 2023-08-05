/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.option.Option
import com.lounres.kone.option.getOrDefault
import com.lounres.kone.polynomial.*


context(A)
public interface MultivariatePolynomialManipulationSpace<C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, out A: Ring<C>>: MultivariatePolynomialSpace<C, V, P, A> {
    public data class VariablePower<V>(val variable: V, val power: UInt)
    public data class Monomial<C, MS>(val signature: MS, val coefficient: C)

    // region Manipulation
    public fun signatureOf(vararg variablePowers: VariablePower<V>): MS = mutableSignatureOf(*variablePowers)
    public fun signatureOf(variablePowers: Collection<VariablePower<V>>): MS = mutableSignatureOf(variablePowers)
    public val MS.size: Int
    public fun MS.isEmpty(): Boolean = size == 0
    public infix fun MS.containsVariable(variable: V): Boolean = get(variable) != Option.None
    public operator fun MS.get(variable: V): Option<UInt>
    public fun MS.getOrZero(variable: V): UInt = get(variable).getOrDefault(0u)
    public val MS.variables: Set<V>
    public val MS.powers: Set<VariablePower<V>>
    public operator fun MS.iterator(): Iterator<VariablePower<V>> = powers.iterator()

    public fun mutableSignatureOf(vararg variablePowers: VariablePower<V>): MutMS
    public fun mutableSignatureOf(variablePowers: Collection<VariablePower<V>>): MutMS
    public fun MutMS.getAndSet(variable: V, power: UInt): Option<UInt>
    public operator fun MutMS.set(variable: V, power: UInt) { getAndSet(variable, power) }
    public fun MutMS.getAndRemove(variable: V): Option<UInt>
    public fun MutMS.remove(variable: V) { getAndRemove(variable) }
    public fun MS.toMutable(): MutMS =
        mutableSignatureOf().apply { for ((v, power) in this@MS) this[v] = power }

    public fun polynomialOf(vararg monomials: Monomial<C, MS>): P = mutablePolynomialOf(*monomials)
    public fun polynomialOf(monomials: Collection<Monomial<C, MS>>): P = mutablePolynomialOf(monomials)
    public val P.size: Int
    public fun P.isEmpty(): Boolean = size == 0
    public infix fun P.containsSignature(signature: MS): Boolean = get(signature) != Option.None
    public fun P.get(signature: MS): Option<C>
    public fun P.getOrZero(signature: MS): C = get(signature).getOrDefault(constantZero)
    public val P.signatures: Set<MS>
    public val P.monomials: Set<Monomial<C, MS>>
    public operator fun P.iterator(): Iterator<Monomial<C, MS>> = monomials.iterator()
    public fun P.toMutable(): MutP =
        mutablePolynomialOf().apply { for((ms, c) in this@P) this[ms] = c }

    public fun mutablePolynomialOf(vararg monomials: Monomial<C, MS>): MutP
    public fun mutablePolynomialOf(monomials: Collection<Monomial<C, MS>>): MutP
    public fun MutP.getAndSet(signature: MS, coefficient: C): Option<C>
    public operator fun MutP.set(signature: MS, coefficient: C) { getAndSet(signature, coefficient) }
    public fun MutP.getAndRemove(signature: MS): Option<C>
    public fun MutP.remove(signature: MS) { getAndRemove(signature) }
    // endregion

    // ===========================================================================================

    override val zero: P get() = polynomialOf()
    override val one: P get() = polynomialOf(Monomial(signatureOf(), constantRing.one))

    public override infix fun P.equalsTo(other: P): Boolean = this.size == other.size && this.monomials.all { other.getOrZero(it.signature) eq it.coefficient }
    public override fun P.isZero(): Boolean = monomials.all { it.coefficient.isZero() }
    public override fun P.isOne(): Boolean = monomials.all { it.signature.isEmpty() || it.coefficient.isZero() }

    public override fun polynomialValueOf(value: C): P = polynomialOf(Monomial(signatureOf(), value))

    public override fun V.plus(other: Int): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), other.constantValue))
    public override fun V.minus(other: Int): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), (-other).constantValue))
    public override fun V.times(other: Int): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), other.constantValue))

    public override fun V.plus(other: Long): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), other.constantValue))
    public override fun V.minus(other: Long): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), (-other).constantValue))
    public override fun V.times(other: Long): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), other.constantValue))

    public override fun Int.plus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), constantOne), Monomial(signatureOf(), this.constantValue))
    public override fun Int.minus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), -constantOne), Monomial(signatureOf(), this.constantValue))
    public override fun Int.times(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), this.constantValue))

    public override fun Long.plus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), constantOne), Monomial(signatureOf(), this.constantValue))
    public override fun Long.minus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), constantOne), Monomial(signatureOf(), (-this).constantValue))
    public override fun Long.times(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), this.constantValue))

    public override fun P.plus(other: Int): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + other }
    public override fun P.minus(other: Int): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) - other }
    public override fun P.times(other: Int): P = this.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * other }

    public override fun P.plus(other: Long): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + other }
    public override fun P.minus(other: Long): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) - other }
    public override fun P.times(other: Long): P = this.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * other }

    public override fun Int.plus(other: P): P = other.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + this@plus }
    public override fun Int.minus(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = -this.getOrZero(ms); this[signatureOf()] = this@minus + this.getOrZero(signatureOf()) }
    public override fun Int.times(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * this@times }

    public override fun Long.plus(other: P): P = other.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + this@plus }
    public override fun Long.minus(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = -this.getOrZero(ms); this[signatureOf()] = this@minus + this.getOrZero(signatureOf()) }
    public override fun Long.times(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * this@times }

    public override fun V.plus(other: C): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), other))
    public override fun V.minus(other: C): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(), (-other)))
    public override fun V.times(other: C): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), other))

    public override fun C.plus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), constantOne), Monomial(signatureOf(), this))
    public override fun C.minus(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), -constantOne), Monomial(signatureOf(), this))
    public override fun C.times(other: V): P = polynomialOf(Monomial(signatureOf(VariablePower(other, 1u)), this))

    public override fun C.plus(other: P): P = other.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + this@plus }
    public override fun C.minus(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = -this.getOrZero(ms); this[signatureOf()] = this@minus + this.getOrZero(signatureOf()) }
    public override fun C.times(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * this@times }

    public override fun P.plus(other: C): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) + other }
    public override fun P.minus(other: C): P = this.toMutable().apply { this[signatureOf()] = this.getOrZero(signatureOf()) - other }
    public override fun P.times(other: C): P = this.toMutable().apply { for (ms in signatures) this[ms] = this.getOrZero(ms) * other }

    public override fun V.unaryPlus(): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne))
    public override fun V.unaryMinus(): P = polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), -constantOne))
    override fun V.plus(other: V): P =
        if (this == other) polynomialOf(Monomial(signatureOf(VariablePower(this, 2u)), constantOne))
        else polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(VariablePower(other, 1u)), constantOne))
    override fun V.minus(other: V): P =
        if (this == other) polynomialZero
        else polynomialOf(Monomial(signatureOf(VariablePower(this, 1u)), constantOne), Monomial(signatureOf(VariablePower(other, 1u)), -constantOne))
    override fun V.times(other: V): P =
        if (this == other) polynomialOf(Monomial(signatureOf(VariablePower(this, 2u)), constantOne))
        else polynomialOf(Monomial(signatureOf(VariablePower(this, 1u), VariablePower(other, 1u)), constantOne))

    override fun V.plus(other: P): P = other.toMutable().apply { this[signatureOf(VariablePower(this@plus, 1u))] = this.getOrZero(signatureOf(VariablePower(this@plus, 1u))) + constantOne }
    override fun V.minus(other: P): P = other.toMutable().apply { for (ms in signatures) this[ms] = -this.getOrZero(ms); this[signatureOf(VariablePower(this@minus, 1u))] = constantOne + this.getOrZero(signatureOf(VariablePower(this@minus, 1u))) }
    override fun V.times(other: P): P = TODO("Not yet implemented")

    override fun P.plus(other: V): P = TODO("Not yet implemented")
    override fun P.minus(other: V): P = TODO("Not yet implemented")
    override fun P.times(other: V): P = TODO("Not yet implemented")

    public override operator fun P.unaryMinus(): P = toMutable().apply { for (ms in signatures) this[ms] = -this.getOrZero(ms) }
    public override operator fun P.plus(other: P): P = this.toMutable().apply { for (ms in other.signatures) this[ms] = this.getOrZero(ms) + other.getOrZero(ms) }
    public override operator fun P.minus(other: P): P = this.toMutable().apply { for (ms in other.signatures) this[ms] = this.getOrZero(ms) - other.getOrZero(ms) }
    public override operator fun P.times(other: P): P = mutablePolynomialOf().apply {
        for (ms1 in this@P.signatures) for (ms2 in other.signatures) this[ms1 + ms2] = this.getOrZero(ms1 + ms2) + this@P.getOrZero(ms1) * other.getOrZero(ms2)
    }

    override val P.degree: Int
        get() = TODO("Not yet implemented")

    override fun P.degreeBy(variables: Collection<V>): UInt {
        TODO("Not yet implemented")
    }
}
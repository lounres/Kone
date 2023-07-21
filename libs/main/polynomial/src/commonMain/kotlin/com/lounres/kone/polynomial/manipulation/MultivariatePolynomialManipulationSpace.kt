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
    public data class Monomial<C, MS>(val coefficient: C, val signature: MS)

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

    public fun mutablePolynomialOf(vararg monomials: Monomial<C, MS>): MutP
    public fun mutablePolynomialOf(monomials: Collection<Monomial<C, MS>>): MutP
    public fun MutP.getAndSet(signature: MS, coefficient: C): Option<C>
    public operator fun MutP.set(signature: MS, coefficient: C) { getAndSet(signature, coefficient) }
    public fun MutP.getAndRemove(signature: MS): Option<C>
    public fun MutP.remove(signature: MS) { getAndRemove(signature) }

    // ===========================================================================================

    override val zero: P get() = polynomialOf()
    override val one: P get() = polynomialOf(Monomial(ring.one, signatureOf()))

    public override infix fun P.equalsTo(other: P): Boolean = TODO("Not yet implemented")
    public override fun P.isZero(): Boolean = monomials.all { it.coefficient.isZero() }
    public override fun P.isOne(): Boolean = monomials.all { it.signature.isEmpty() || it.coefficient.isZero() }

    public override fun valueOf(value: C): P = polynomialOf(Monomial(value, signatureOf()))

    
}
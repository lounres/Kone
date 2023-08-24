/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.optional.Option
import com.lounres.kone.polynomial.MultivariatePolynomialSpace
import com.lounres.kone.polynomial.Polynomial


context(A)
public interface MultivariatePolynomialManipulationSpace<C, V, VP, MS, MutMS: MS, M, P: Polynomial<C>, MutP: P, out A: Ring<C>>: MultivariatePolynomialSpace<C, V, P, A> {
    // region Manipulation
    public fun variablePower(variable: V, power: UInt): VP
    public val VP.variable: V
    public val VP.power: UInt

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

    public fun polynomialOf(vararg monomials: M): P
    public fun polynomialOf(monomials: Collection<M>): P
    public val P.size: Int
    public fun P.isEmpty(): Boolean
    public infix fun P.containsSignature(signature: MS): Boolean
    public fun P.get(signature: MS): C
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


}
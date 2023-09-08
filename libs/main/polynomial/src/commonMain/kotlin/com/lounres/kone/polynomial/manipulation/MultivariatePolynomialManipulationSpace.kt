/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.MultivariatePolynomialSpace


@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialManipulationSpace<C, V, VP, MS, MutMS: MS, M, P, MutP: P, out A: Ring<C>>: MultivariatePolynomialSpace<C, V, P, A> {
    // region Manipulation
    public fun variablePower(variable: V, power: UInt): VP

    public fun signatureOf(vararg variablePowers: VP): MS

    public fun monomial(signature: MS, coefficient: C): M

    public fun polynomialOf(vararg monomials: M): P
    // endregion

    @JvmName("unaryPlusVariable")
    public override operator fun V.unaryPlus(): P = polynomialOf(
        monomial(signatureOf(variablePower(this, 1u)), constantOne)
    )
}
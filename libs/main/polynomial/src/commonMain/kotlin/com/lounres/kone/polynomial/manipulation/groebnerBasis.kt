/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Field
import com.lounres.kone.order.Order
import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace.*
import com.lounres.kone.polynomial.Polynomial
import kotlin.jvm.JvmInline


@JvmInline
public value class Ideal<P>(public val basis: List<P>)

context(A, MultivariatePolynomialManipulationSpace<C, V, MS, MutMS, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, A: Field<C>> Ideal<P>.groebnerBasisByBuchbergersAlgorithm(): Ideal<P> {
    val basis = basis.toMutableList()
    var i = 1
    var j = 0
    while (i < basis.size) {
        val fi = basis[i]
        val fj = basis[j]

        val gi = fi.leadingTerm
        val gj = fj.leadingTerm

        val (_, ai, aj) = lcmWithMultipliers(gi.signature, gj.signature)

        var s = fi * polynomialOf(Monomial(ai, gj.coefficient)) - fj * polynomialOf(Monomial(aj, gi.coefficient)) // TODO: Implement multiplication on signatures and replace the expression with them.

        s = s.divRem(basis).remainder

        basis.add(s)

        j++
        if (j == i) {
            j = 0
            i++
        }
    }
    return Ideal(basis)
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial.manipulation

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.context.invoke
import dev.lounres.kone.order.Order
import kotlin.jvm.JvmInline


@JvmInline
public value class Ideal<P>(public val basis: List<P>)

context(MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> Ideal<P>.groebnerBasisByBuchbergersAlgorithm(): Ideal<P> = numericalRing {
    val basis = basis.toMutableList()
    var i = 1
    var j = 0
    while (i < basis.size) {
        val fi = basis[i]
        val fj = basis[j]

        val gi = fi.leadingTerm
        val gj = fj.leadingTerm

        val (_, ai, aj) = lcmWithMultipliers(gi.signature, gj.signature)

        var s = fi * polynomialOf(monomial(ai, gj.coefficient)) - fj * polynomialOf(monomial(aj, gi.coefficient)) // TODO: Implement multiplication on signatures and replace the expression with them.

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
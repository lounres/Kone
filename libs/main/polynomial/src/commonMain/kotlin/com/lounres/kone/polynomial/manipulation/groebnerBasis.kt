/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Field
import com.lounres.kone.context.invoke
import com.lounres.kone.order.Order
import kotlin.jvm.JvmInline


@JvmInline
public value class Ideal<P>(public val basis: List<P>)

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.groebnerBasisByBuchbergersAlgorithm(order: Order<MS>, ideal: Ideal<P>): Ideal<P> = constantRing {
    val basis = ideal.basis.toMutableList()
    var i = 1
    var j = 0
    while (i < basis.size) {
        val fi = basis[i]
        val fj = basis[j]

        val gi = leadingTerm(order, fi)
        val gj = leadingTerm(order, fj)

        val (_, ai, aj) = lcmWithMultipliers(gi.signature, gj.signature)

        var s = fi * polynomialOf(listOf(monomial(ai, gj.coefficient))) - fj * polynomialOf(listOf(monomial(aj, gi.coefficient))) // TODO: Implement multiplication on signatures and replace the expression with them.

        s = rem(order, s, basis)

        if (!s.isEmpty()) basis.add(s)

        j++
        if (j == i) {
            j = 0
            i++
        }
    }
    return Ideal(basis)
}
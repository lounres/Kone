/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.ideals

import com.lounres.kone.algebraic.Field
import com.lounres.kone.order.Order
import com.lounres.kone.polynomial.MultivariatePolynomialSpace
import com.lounres.kone.polynomial.leadingTerm


@JvmInline
public value class Ideal<P>(public val basis: List<P>)

context(A, MultivariatePolynomialSpace<C, V, MS, P, A>, Order<MS>)
public fun <C, V, MS, P, A: Field<C>> Ideal<P>.grobnerBasisByBuchbergerAlgorithm(): List<P> {
    val basis = basis.toMutableList()
    var i = 1
    var j = 0
    while (true) {
        val fi = basis[i]
        val fj = basis[j]

        val gi = fi.leadingTerm
        val gj = fj.leadingTerm

        val a = lcm(gi.signature, gj.signature)

        val s = (a/gi.signature) * fi * gj.coefficient - (a/gj.signature) * fj * gi.coefficient

        // TODO: Divide [s] with remainder by elements of the basis
        // TODO: Add the reminder to the basis


        j++
        if (j == i) {
            j = 0
            i++
            if (i == basis.size) break
        }
    }
    return basis
}
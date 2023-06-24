/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.ideals

import com.lounres.kone.algebraic.Field
import com.lounres.kone.order.Order
import com.lounres.kone.polynomial.MultivariatePolynomialSpace
import com.lounres.kone.polynomial.gcdWithDivisors
import com.lounres.kone.polynomial.lcmWithMultipliers
import com.lounres.kone.polynomial.leadingTerm


public data class QuotientAndRemainder<P>(val quotient: P, val remainder: P)

context(A, MultivariatePolynomialSpace<C, V, VP, MS, M, P, A>, Order<MS>)
@Suppress("SpellCheckingInspection")
public fun <C, V, VP, MS, M, P, A: Field<C>> P.divrem(divisor: P): QuotientAndRemainder<P> {
    val divisorLeadingTerm = divisor.leadingTerm

    var currentDivident = this
    var currentQuotient = this@MultivariatePolynomialSpace.zero
    var currentDividentLeadingTerm = currentDivident.leadingTerm
    var leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)

    while (leadingTermsGcd.divisor1.powersCount == 0u) {
        val quotientCoefficient = currentDividentLeadingTerm.coefficient / divisorLeadingTerm.coefficient
        currentDivident -= divisor * leadingTermsGcd.divisor2 * quotientCoefficient
        currentQuotient += monomial(leadingTermsGcd.divisor2, quotientCoefficient)
        currentDividentLeadingTerm = currentDivident.leadingTerm
        leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)
    }

    return QuotientAndRemainder(
        quotient = currentQuotient,
        remainder = currentDivident,
    )
}

context(A, MultivariatePolynomialSpace<C, V, VP, MS, M, P, A>, Order<MS>)
@Suppress("SpellCheckingInspection")
public operator fun <C, V, VP, MS, M, P, A: Field<C>> P.div(divisor: P): P {
    val divisorLeadingTerm = divisor.leadingTerm

    var currentDivident = this
    var currentQuotient = this@MultivariatePolynomialSpace.zero
    var currentDividentLeadingTerm = currentDivident.leadingTerm
    var leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)

    while (leadingTermsGcd.divisor1.powersCount == 0u) {
        val quotientCoefficient = currentDividentLeadingTerm.coefficient / divisorLeadingTerm.coefficient
        currentDivident -= divisor * leadingTermsGcd.divisor2 * quotientCoefficient
        currentQuotient += monomial(leadingTermsGcd.divisor2, quotientCoefficient)
        currentDividentLeadingTerm = currentDivident.leadingTerm
        leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)
    }

    return currentQuotient
}

context(A, MultivariatePolynomialSpace<C, V, VP, MS, M, P, A>, Order<MS>)
@Suppress("SpellCheckingInspection")
public operator fun <C, V, VP, MS, M, P, A: Field<C>> P.rem(divisor: P): P {
    val divisorLeadingTerm = divisor.leadingTerm

    var currentDivident = this
    var currentDividentLeadingTerm = currentDivident.leadingTerm
    var leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)

    while (leadingTermsGcd.divisor1.powersCount == 0u) {
        val quotientCoefficient = currentDividentLeadingTerm.coefficient / divisorLeadingTerm.coefficient
        currentDivident -= divisor * leadingTermsGcd.divisor2 * quotientCoefficient
        currentDividentLeadingTerm = currentDivident.leadingTerm
        leadingTermsGcd = gcdWithDivisors(divisorLeadingTerm.signature, currentDividentLeadingTerm.signature)
    }

    return currentDivident
}

@JvmInline
public value class Ideal<P>(public val basis: List<P>)

context(A, MultivariatePolynomialSpace<C, V, VP, MS, M, P, A>, Order<MS>)
public fun <C, V, VP, MS, M, P, A: Field<C>> Ideal<P>.grobnerBasisByBuchbergerAlgorithm(): List<P> {
    val basis = basis.toMutableList()
    var i = 1
    var j = 0
    while (true) {
        val fi = basis[i]
        val fj = basis[j]

        val gi = fi.leadingTerm
        val gj = fj.leadingTerm

        val (_, ai, aj) = lcmWithMultipliers(gi.signature, gj.signature)

        var s = fi * ai * gj.coefficient - fj * aj * gi.coefficient

        for (b in basis) s %= b

        basis.add(s)

        j++
        if (j == i) {
            j = 0
            i++
            if (i == basis.size) break
        }
    }
    return basis
}
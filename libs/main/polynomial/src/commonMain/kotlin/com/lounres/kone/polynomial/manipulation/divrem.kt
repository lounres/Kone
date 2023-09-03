/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Field
import com.lounres.kone.context.invoke
import com.lounres.kone.order.Order


public data class IncompleteQuotientAndRemainder<out P>(
    val incompleteQuotient: P,
    val remainder: P,
)

public fun <C, V, VP, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, VP, MS, MutMS, M, P, MutP, A>.leadDivRem(order: Order<MS>, that: P, divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(that.monomials)
    val divisorLeadingSignature = leadingSignature(order, divisor)
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]
    var remainderLeadingSignature = leadingSignature(order, remainder)

    while (isDivisibleBy(remainderLeadingSignature, divisorLeadingSignature)) {
        val quotCoefficient = constantRing { remainder[remainderLeadingSignature] / divisorLeadingCoefficient }
        val quotSignature = div(remainderLeadingSignature, divisorLeadingSignature)
        minusAssignProduct(remainder, divisor, polynomialOf(monomial(quotSignature, quotCoefficient)))
        remainderLeadingSignature = leadingSignature(order, remainder)
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.divRem(order: Order<MS>, that: P, divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(that.monomials)
    val remainder = mutablePolynomialOf()
    val divisorLeadingSignature = leadingSignature(order, divisor)
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]
    var dividendLeadingSignature = leadingSignature(order, dividend)

    while (!dividend.isEmpty()) {
        if (isDivisibleBy(dividendLeadingSignature, divisorLeadingSignature)) {
            val quotCoefficient = constantRing { remainder[dividendLeadingSignature] / divisorLeadingCoefficient }
            val quotSignature = div(dividendLeadingSignature, divisorLeadingSignature)
            minusAssignProduct(remainder, divisor, polynomialOf(monomial(quotSignature, quotCoefficient)))
            dividendLeadingSignature = leadingSignature(order, dividend)
            quotient[quotSignature] = quotCoefficient
        } else {
            remainder[dividendLeadingSignature] = dividend[dividendLeadingSignature]
            dividend.remove(dividendLeadingSignature)
        }
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.leadDivRem(order: Order<MS>, that: P, divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisorsList = divisors.toList()

    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(that.monomials)
    val divisorsLeadingSignatures = divisorsList.map { leadingSignature(order, it) }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }
    var remainderLeadingSignature = leadingSignature(order, remainder)

    while (true) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { divides(it, remainderLeadingSignature) }
        if (divisorIndex == -1) break

        val quotCoefficient = constantRing { remainder[remainderLeadingSignature] / divisorsLeadingCoefficients[divisorIndex] }
        val quotSignature = div(remainderLeadingSignature, divisorsLeadingSignatures[divisorIndex])
        minusAssignProduct(remainder, divisorsList[divisorIndex], polynomialOf(monomial(quotSignature, quotCoefficient)))
        remainderLeadingSignature = leadingSignature(order, remainder)
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.divRem(order: Order<MS>, that: P, divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisorsList = divisors.toList()

    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(that.monomials)
    val remainder = mutablePolynomialOf()
    val divisorsLeadingSignatures = divisorsList.map { leadingSignature(order, it) }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }
    var dividendLeadingSignature = leadingSignature(order, dividend)

    while (!dividend.isEmpty()) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { divides(it, dividendLeadingSignature) }

        if (divisorIndex != -1) {
            val quotCoefficient = constantRing { remainder[dividendLeadingSignature] / divisorsLeadingCoefficients[divisorIndex] }
            val quotSignature = div(dividendLeadingSignature, divisorsLeadingSignatures[divisorIndex])
            minusAssignProduct(remainder, divisorsList[divisorIndex], polynomialOf(monomial(quotSignature, quotCoefficient)))
            dividendLeadingSignature = leadingSignature(order, dividend)
            quotient[quotSignature] = quotCoefficient
        } else {
            remainder[dividendLeadingSignature] = dividend[dividendLeadingSignature]
            dividend.remove(dividendLeadingSignature)
        }
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}
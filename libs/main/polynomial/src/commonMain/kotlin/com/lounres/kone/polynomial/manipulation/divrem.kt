/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Field
import com.lounres.kone.order.Order


public data class IncompleteQuotientAndRemainder<out P>(
    val incompleteQuotient: P,
    val remainder: P,
)

context(A, MultivariatePolynomialManipulationSpace<C, V, VP, MS, MutMS, M, P, MutP, A>, Order<MS>)
public fun <C, V, VP, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> P.leadDivRem(divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(this.monomials)
    val divisorLeadingSignature = divisor.leadingSignature
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]
    var remainderLeadingSignature = remainder.leadingSignature

    while (remainderLeadingSignature isDivisibleBy divisorLeadingSignature) {
        val quotCoefficient = remainder[remainderLeadingSignature] / divisorLeadingCoefficient
        val quotSignature = remainderLeadingSignature.div(divisorLeadingSignature)
        remainder.minusAssignProduct(divisor, polynomialOf(monomial(quotSignature, quotCoefficient)))
        remainderLeadingSignature = remainder.leadingSignature
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

context(A, MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> P.divRem(divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(this.monomials)
    val remainder = mutablePolynomialOf()
    val divisorLeadingSignature = divisor.leadingSignature
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]
    var dividendLeadingSignature = dividend.leadingSignature

    while (!dividend.isEmpty()) {
        if (dividendLeadingSignature isDivisibleBy divisorLeadingSignature) {
            val quotCoefficient = remainder[dividendLeadingSignature] / divisorLeadingCoefficient
            val quotSignature = dividendLeadingSignature.div(divisorLeadingSignature)
            remainder.minusAssignProduct(divisor, polynomialOf(monomial(quotSignature, quotCoefficient)))
            dividendLeadingSignature = dividend.leadingSignature
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

context(A, MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> P.leadDivRem(divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisorsList = divisors.toList()

    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(this.monomials)
    val divisorsLeadingSignatures = divisorsList.map { it.leadingSignature }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }
    var remainderLeadingSignature = remainder.leadingSignature

    while (true) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { it divides remainderLeadingSignature }
        if (divisorIndex == -1) break

        val quotCoefficient = remainder[remainderLeadingSignature] / divisorsLeadingCoefficients[divisorIndex]
        val quotSignature = remainderLeadingSignature.div(divisorsLeadingSignatures[divisorIndex])
        remainder.minusAssignProduct(divisorsList[divisorIndex], polynomialOf(monomial(quotSignature, quotCoefficient)))
        remainderLeadingSignature = remainder.leadingSignature
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

context(A, MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> P.divRem(divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisorsList = divisors.toList()

    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(this.monomials)
    val remainder = mutablePolynomialOf()
    val divisorsLeadingSignatures = divisorsList.map { it.leadingSignature }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }
    var dividendLeadingSignature = dividend.leadingSignature

    while (!dividend.isEmpty()) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { it divides dividendLeadingSignature }

        if (divisorIndex != -1) {
            val quotCoefficient = remainder[dividendLeadingSignature] / divisorsLeadingCoefficients[divisorIndex]
            val quotSignature = dividendLeadingSignature.div(divisorsLeadingSignatures[divisorIndex])
            remainder.minusAssignProduct(divisorsList[divisorIndex], polynomialOf(monomial(quotSignature, quotCoefficient)))
            dividendLeadingSignature = dividend.leadingSignature
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
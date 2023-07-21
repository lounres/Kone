/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Field
import com.lounres.kone.order.Order
import com.lounres.kone.polynomial.Polynomial
import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace.*


public data class IncompleteQuotientAndRemainder<out P>(
    val incompleteQuotient: P,
    val remainder: P,
)

context(A, MultivariatePolynomialManipulationSpace<C, V, MS, MutMS, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, A: Field<C>> P.leadDivRem(divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(this.monomials)
    val divisorLeadingSignature = divisor.leadingSignature
    val divisorLeadingCoefficient = divisor.getOrZero(divisorLeadingSignature)
    var remainderLeadingSignature = remainder.leadingSignature

    while (remainderLeadingSignature isDivisibleBy divisorLeadingSignature) {
        val quotCoefficient = remainder.getOrZero(remainderLeadingSignature) / divisorLeadingCoefficient
        val quotSignature = remainderLeadingSignature - divisorLeadingSignature
        remainder.minusAssignProduct(divisor, polynomialOf(Monomial(quotCoefficient, quotSignature)))
        remainderLeadingSignature = remainder.leadingSignature
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

context(A, MultivariatePolynomialManipulationSpace<C, V, MS, MutMS, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, A: Field<C>> P.divRem(divisor: P): IncompleteQuotientAndRemainder<P> {
    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(this.monomials)
    val remainder = mutablePolynomialOf()
    val divisorLeadingSignature = divisor.leadingSignature
    val divisorLeadingCoefficient = divisor.getOrZero(divisorLeadingSignature)
    var dividendLeadingSignature = dividend.leadingSignature

    while (!dividend.isEmpty()) {
        if (dividendLeadingSignature isDivisibleBy divisorLeadingSignature) {
            val quotCoefficient = remainder.getOrZero(dividendLeadingSignature) / divisorLeadingCoefficient
            val quotSignature = dividendLeadingSignature - divisorLeadingSignature
            remainder.minusAssignProduct(divisor, polynomialOf(Monomial(quotCoefficient, quotSignature)))
            dividendLeadingSignature = dividend.leadingSignature
            quotient[quotSignature] = quotCoefficient
        } else {
            remainder[dividendLeadingSignature] = dividend.getOrZero(dividendLeadingSignature)
            dividend.remove(dividendLeadingSignature)
        }
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

context(A, MultivariatePolynomialManipulationSpace<C, V, MS, MutMS, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, A: Field<C>> P.leadDivRem(divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisors = divisors.toList()

    val quotient = mutablePolynomialOf()
    val remainder = mutablePolynomialOf(this.monomials)
    val divisorsLeadingSignatures = divisors.map { it.leadingSignature }
    val divisorsLeadingCoefficients = divisors.mapIndexed { i, divisor -> divisor.getOrZero(divisorsLeadingSignatures[i]) }
    var remainderLeadingSignature = remainder.leadingSignature

    while (true) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { it divides remainderLeadingSignature }
        if (divisorIndex == -1) break

        val quotCoefficient = remainder.getOrZero(remainderLeadingSignature) / divisorsLeadingCoefficients[divisorIndex]
        val quotSignature = remainderLeadingSignature - divisorsLeadingSignatures[divisorIndex]
        remainder.minusAssignProduct(divisors[divisorIndex], polynomialOf(Monomial(quotCoefficient, quotSignature)))
        remainderLeadingSignature = remainder.leadingSignature
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

context(A, MultivariatePolynomialManipulationSpace<C, V, MS, MutMS, P, MutP, A>, Order<MS>)
public fun <C, V, MS, MutMS: MS, P: Polynomial<C>, MutP: P, A: Field<C>> P.divRem(divisors: Collection<P>): IncompleteQuotientAndRemainder<P> {
    val divisors = divisors.toList()

    val quotient = mutablePolynomialOf()
    val dividend = mutablePolynomialOf(this.monomials)
    val remainder = mutablePolynomialOf()
    val divisorsLeadingSignatures = divisors.map { it.leadingSignature }
    val divisorsLeadingCoefficients = divisors.mapIndexed { i, divisor -> divisor.getOrZero(divisorsLeadingSignatures[i]) }
    var dividendLeadingSignature = dividend.leadingSignature

    while (!dividend.isEmpty()) {
        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { it divides dividendLeadingSignature }

        if (divisorIndex != -1) {
            val quotCoefficient = remainder.getOrZero(dividendLeadingSignature) / divisorsLeadingCoefficients[divisorIndex]
            val quotSignature = dividendLeadingSignature - divisorsLeadingSignatures[divisorIndex]
            remainder.minusAssignProduct(divisors[divisorIndex], polynomialOf(Monomial(quotCoefficient, quotSignature)))
            dividendLeadingSignature = dividend.leadingSignature
            quotient[quotSignature] = quotCoefficient
        } else {
            remainder[dividendLeadingSignature] = dividend.getOrZero(dividendLeadingSignature)
            dividend.remove(dividendLeadingSignature)
        }
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}
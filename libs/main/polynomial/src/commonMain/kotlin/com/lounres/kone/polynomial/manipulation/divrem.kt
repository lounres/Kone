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
    require(!divisor.isEmpty())

    val quotient = mutablePolynomialOf(listOf())
    val remainder = mutablePolynomialOf(that.monomials)
    val divisorLeadingSignature = leadingSignature(order, divisor)
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]

    while (true) {
        if (remainder.isEmpty()) break
        val remainderLeadingSignature = leadingSignature(order, remainder)

        if (!isDivisibleBy(remainderLeadingSignature, divisorLeadingSignature)) break

        val quotCoefficient = constantRing { remainder[remainderLeadingSignature] / divisorLeadingCoefficient }
        val quotSignature = div(remainderLeadingSignature, divisorLeadingSignature)
        minusAssignProduct(remainder, divisor, polynomialOf(listOf(monomial(quotSignature, quotCoefficient))))
        quotient[quotSignature] = quotCoefficient
    }

    return IncompleteQuotientAndRemainder(
        incompleteQuotient = quotient,
        remainder = remainder,
    )
}

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.divRem(order: Order<MS>, that: P, divisor: P): IncompleteQuotientAndRemainder<P> {
    require(!divisor.isEmpty())

    val quotient = mutablePolynomialOf(listOf())
    val dividend = that.toMutable()
    val remainder = mutablePolynomialOf(listOf())
    val divisorLeadingSignature = leadingSignature(order, divisor)
    val divisorLeadingCoefficient = divisor[divisorLeadingSignature]

    while (!dividend.isEmpty()) {
        val dividendLeadingSignature = leadingSignature(order, dividend)
        if (isDivisibleBy(dividendLeadingSignature, divisorLeadingSignature)) {
            val quotCoefficient = constantRing { dividend[dividendLeadingSignature] / divisorLeadingCoefficient }
            val quotSignature = div(dividendLeadingSignature, divisorLeadingSignature)
            minusAssignProduct(dividend, divisor, polynomialOf(listOf(monomial(quotSignature, quotCoefficient))))
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

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.leadRem(order: Order<MS>, that: P, divisors: Collection<P>): P {
    require(divisors.all { !it.isEmpty() })
    val divisorsList = divisors.toList()

    val remainder = mutablePolynomialOf(that.monomials)
    val divisorsLeadingSignatures = divisorsList.map { leadingSignature(order, it) }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }

    while (!remainder.isEmpty()) {
        val remainderLeadingSignature = leadingSignature(order, remainder)

        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { divides(it, remainderLeadingSignature) }
        if (divisorIndex == -1) break

        val quotCoefficient = constantRing { remainder[remainderLeadingSignature] / divisorsLeadingCoefficients[divisorIndex] }
        val quotSignature = div(remainderLeadingSignature, divisorsLeadingSignatures[divisorIndex])
        minusAssignProduct(remainder, divisorsList[divisorIndex], polynomialOf(listOf(monomial(quotSignature, quotCoefficient))))
    }

    return remainder
}

public fun <C, V, MS, MutMS: MS, M, P, MutP: P, A: Field<C>> MultivariatePolynomialManipulationSpace<C, V, *, MS, MutMS, M, P, MutP, A>.rem(order: Order<MS>, that: P, divisors: Collection<P>): P {
    require(divisors.all { !it.isEmpty() })
    val divisorsList = divisors.toList()

    val dividend = mutablePolynomialOf(that.monomials)
    val remainder = mutablePolynomialOf(listOf())
    val divisorsLeadingSignatures = divisorsList.map { leadingSignature(order, it) }
    val divisorsLeadingCoefficients = divisorsList.mapIndexed { i, divisor -> divisor[divisorsLeadingSignatures[i]] }

    while (!dividend.isEmpty()) {
        val dividendLeadingSignature = leadingSignature(order, dividend)

        val divisorIndex = divisorsLeadingSignatures.indexOfFirst { divides(it, dividendLeadingSignature) }

        if (divisorIndex != -1) {
            val quotCoefficient = constantRing { dividend[dividendLeadingSignature] / divisorsLeadingCoefficients[divisorIndex] }
            val quotSignature = div(dividendLeadingSignature, divisorsLeadingSignatures[divisorIndex])
            minusAssignProduct(dividend, divisorsList[divisorIndex], polynomialOf(listOf(monomial(quotSignature, quotCoefficient))))
        } else {
            remainder[dividendLeadingSignature] = dividend[dividendLeadingSignature]
            dividend.remove(dividendLeadingSignature)
        }
    }

    return remainder
}
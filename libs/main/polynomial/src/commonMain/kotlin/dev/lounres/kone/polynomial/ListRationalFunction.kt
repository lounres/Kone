/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.FieldOperations
import dev.lounres.kone.algebraic.RingOperations


/**
 * Represents univariate rational function that stores its numerator and denominator as [ListPolynomial]s.
 */
public data class ListRationalFunction<C>(
    public override val numerator: ListPolynomial<C>,
    public override val denominator: ListPolynomial<C>
) : RationalFunction<C, ListPolynomial<C>> {
    override fun toString(): String = "ListRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

/**
 * Arithmetic context for univariate rational functions with numerator and denominator represented as [ListPolynomial]s.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [RingOperations] of [C].
 */
context(A, PS)
public open class ListRationalFunctionSpace<C, out A : RingOperations<C>, out PS: ListPolynomialSpace<C, A>> :
    RationalFunctionSpace<C, ListPolynomial<C>, ListRationalFunction<C>, A, PS>,
    PolynomialSpaceOfFractions<C, ListPolynomial<C>, ListRationalFunction<C>, A, PS>() {

    /**
     * Constructor of [ListRationalFunction] from numerator and denominator [ListPolynomial].
     */
    override fun constructRationalFunction(numerator: ListPolynomial<C>, denominator: ListPolynomial<C>): ListRationalFunction<C> =
        ListRationalFunction(numerator, denominator)
}

public typealias DefaultListRationalFunctionSpace<C, A> = ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>

context(A, PS)
public class ListRationalFunctionSpaceOverField<C, out A : FieldOperations<C>, out PS: ListPolynomialSpaceOverField<C, A>> :
    ListRationalFunctionSpace<C, A, PS>(),
    RationalFunctionSpaceOverField<C, ListPolynomial<C>, ListRationalFunction<C>, A, PS>

public typealias DefaultListRationalFunctionSpaceOverField<C, A> = ListRationalFunctionSpaceOverField<C, A, ListPolynomialSpaceOverField<C, A>>
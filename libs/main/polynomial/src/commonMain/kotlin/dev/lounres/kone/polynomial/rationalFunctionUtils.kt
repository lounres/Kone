/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Ring


public data class RationalFunctionSpaceScope<
    Number,
    Polynomial,
    RationalFunctionType: RationalFunction<Polynomial>,
    out NumberContext: Ring<Number>,
    out PolynomialContext: PolynomialSpace<Number, Polynomial>,
    out RationalFunctionContext: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>,
>(
    public val numberContext: NumberContext,
    public val polynomialSpace: PolynomialContext,
    public val rationalFunctionSpace: RationalFunctionContext,
)

public inline operator fun <
    Number,
    Polynomial,
    RationalFunctionType: RationalFunction<Polynomial>,
    NumberContext: Ring<Number>,
    PolynomialContext: PolynomialSpace<Number, Polynomial>,
    RationalFunctionContext: RationalFunctionSpace<Number, Polynomial, RationalFunctionType>,
    Result
> RationalFunctionSpaceScope<Number, Polynomial, RationalFunctionType, NumberContext, PolynomialContext, RationalFunctionContext>.invoke(block: context(NumberContext, PolynomialContext, RationalFunctionContext) () -> Result): Result {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(numberContext, polynomialSpace, rationalFunctionSpace)
}
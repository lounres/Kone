/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.reversed


/**
 * Constructs a [ListPolynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
public fun <C> ListPolynomial(coefficients: KoneList<C>, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(if (reverse) coefficients.reversed() else coefficients)

/**
 * Constructs a [ListPolynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
public fun <C> ListPolynomial(vararg coefficients: C, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(KoneMutableArray(coefficients).let { if (reverse) it.reversed() else it })

/**
 * Represents [this] constant as a [ListPolynomial].
 */
public fun <C> C.asListPolynomial() : ListPolynomial<C> = ListPolynomial(KoneList.of(this))

///**
// * Constructs [ListRationalFunction] instance with numerator and denominator constructed with provided
// * [numeratorCoefficients] and [denominatorCoefficients]. The both collections of coefficients will be reversed if
// * [reverse] parameter is true.
// */
//public fun <C> ListRationalFunction(numeratorCoefficients: KoneList<C>, denominatorCoefficients: KoneList<C>, reverse: Boolean = false): ListRationalFunction<C> =
//    ListRationalFunction<C>(
//        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
//        ListPolynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } )
//    )
///**
// * Constructs [ListRationalFunction] instance with provided [numerator] and unit denominator.
// */
//context(polynomialSpace: ListPolynomialSpace<Number>)
//public fun <Number> ListRationalFunction(numerator: ListPolynomial<Number>): ListRationalFunction<Number> =
//    ListRationalFunction<Number>(numerator, polynomialSpace.one)
///**
// * Constructs [ListRationalFunction] instance with numerator constructed with provided [numeratorCoefficients] and unit
// * denominator. The collection of numerator coefficients will be reversed if [reverse] parameter is true.
// */
//context(polynomialSpace: ListPolynomialSpace<Number>)
//public fun <Number> ListRationalFunction(numeratorCoefficients: KoneList<Number>, reverse: Boolean = false): ListRationalFunction<Number> =
//    ListRationalFunction<Number>(
//        ListPolynomial(numeratorCoefficients.let { if (reverse) it.reversed() else it }),
//        polynomialSpace.one,
//    )
//
///**
// * Represents [this] constant as a rational function.
// */
//context(_: ListPolynomialSpace<C>)
//public fun <C> C.asListRationalFunction() : ListRationalFunction<C> = ListRationalFunction(asListPolynomial())
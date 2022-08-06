/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("LocalVariableName")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.Rational
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.testUtils.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.*


class ListPolynomialTest2: FreeSpec() {
    sealed interface PolynomialIntTestData<C> {
        val argumentPolynomialCoefficients: List<C>
        val argumentInt: Int
        val nonOptimizedResultPolynomialCoefficients: List<C>
        operator fun component1() = argumentPolynomialCoefficients
        operator fun component2() = argumentInt
        operator fun component3() = nonOptimizedResultPolynomialCoefficients
        data class EqualityTestData<C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
            val resultPolynomialCoefficients: List<C>,
        ): PolynomialIntTestData<C>
        data class SameTestData<C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
            val resultPolynomial: ListPolynomial<C>,
        ): PolynomialIntTestData<C>
        data class NoChangeTestData<C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
        ): PolynomialIntTestData<C>
    }
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
        resultPolynomialCoefficients: List<C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.EqualityTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomialCoefficients,
    )
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
        resultPolynomial: ListPolynomial<C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.SameTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomial,
    )
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.NoChangeTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
    )

    val rationalPolynomialSpace = Rational.field.listPolynomialSpace
    val intModuloPolynomialSpace = IntBoxModuloRing(35).listPolynomialSpace

    @JvmName("produceTestsInt")
    suspend fun <C, A: Ring<C>> ContainerScope.produceIntTests(
        testDataList: List<PolynomialIntTestData<C>>,
        polynomialSpace: ListPolynomialSpace<C, A>,
        polynomialArgumentCoefficientsTransform: (A.(C) -> C)? = null,
        operationToTest: ListPolynomialSpace<C, *>.(pol: ListPolynomial<C>, arg: Int) -> ListPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { data ->
            if (polynomialArgumentCoefficientsTransform === null) {
                when (data) {
                    is PolynomialIntTestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs), arg) } shouldBe ListPolynomial(expected)
                    }
                    is PolynomialIntTestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        ListPolynomial(coefs).let { polynomialSpace { operationToTest(it, arg) } shouldBeSameInstanceAs  it }
                    }
                    is PolynomialIntTestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs), arg) } shouldBeSameInstanceAs expected
                    }
                }
            } else {
                fun List<C>.update() = map { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it) }
                when (data) {
                    is PolynomialIntTestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe ListPolynomial(expected)
                    }
                    is PolynomialIntTestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe ListPolynomial(coefs)
                    }
                    is PolynomialIntTestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe expected
                    }
                }
            }
        }
    }

    val plusPolynomialIntTestData = listOf(
        PolynomialIntTestData(
            listOf(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
            -3,
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
        ),
        PolynomialIntTestData(
            listOf(Rational(-2), Rational(0), Rational(0), Rational(0)),
            2,
            listOf(Rational(0), Rational(0), Rational(0), Rational(0)),
            listOf(Rational(0), Rational(0), Rational(0), Rational(0)),
        ),
        PolynomialIntTestData(
            listOf(Rational(-2)),
            2,
            listOf(Rational(0)),
            listOf(Rational(0)),
        ),
        PolynomialIntTestData(
            listOf(),
            0,
            listOf(Rational(0)),
        ),
        PolynomialIntTestData(
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
            0,
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
        ),
        PolynomialIntTestData(
            listOf(Rational(-2), Rational(0), Rational(0), Rational(0)),
            1,
            listOf(Rational(-1), Rational(0), Rational(0), Rational(0)),
            listOf(Rational(-1), Rational(0), Rational(0), Rational(0)),
        ),
        PolynomialIntTestData(
            listOf(Rational(-2)),
            1,
            listOf(Rational(-1)),
            listOf(Rational(-1)),
        ),
        PolynomialIntTestData(
            listOf(),
            2,
            listOf(Rational(2)),
            listOf(Rational(2)),
        ),
    )

    val timesPolynomialIntTestData = listOf<PolynomialIntTestData<IntBox>>(
        PolynomialIntTestData(
            listOf(22, 26, 13, 15, 26).map { IntBox(it) },
            27,
            listOf(34, 2, 1, 20, 2).map { IntBox(it) },
            listOf(34, 2, 1, 20, 2).map { IntBox(it) },
        ),
        PolynomialIntTestData(
            listOf(7, 0, 49, 21, 14).map { IntBox(it) },
            35,
            listOf(0, 0, 0, 0, 0).map { IntBox(it) },
            listOf(0, 0, 0, 0, 0).map { IntBox(it) },
        ),
        PolynomialIntTestData(
            listOf(22, 26, 13, 15, 26).map { IntBox(it) },
            0,
            listOf(0, 0, 0, 0, 0).map { IntBox(it) },
            intModuloPolynomialSpace.zero,
        ),
        PolynomialIntTestData(
            listOf(22, 26, 13, 15, 26).map { IntBox(it) },
            1,
            listOf(22, 26, 13, 15, 26).map { IntBox(it) },
        ),
    )

    init {
        "Polynomial and Int" - {
            "plus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol + arg } }
            "minus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg) } }
            "times" - { produceIntTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> pol * arg } }
        }
        "Polynomial and Long" - {
            "plus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol + arg.toLong() } }
            "minus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg).toLong() } }
            "times" - { produceIntTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> pol * arg.toLong() } }
        }
        "Int and Polynomial" - {
            "plus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> arg + pol } }
            "minus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg - pol } }
            "times" - { produceIntTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> arg * pol } }
        }
        "Long and Polynomial" - {
            "plus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> arg.toLong() + pol } }
            "minus" - { produceIntTests(plusPolynomialIntTestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg.toLong() - pol } }
            "times" - { produceIntTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> arg.toLong() * pol } }
        }
    }

    suspend inline fun <C, A: Ring<C>> ContainerScope.produceConstantTests(
        testDataList: List<PolynomialIntTestData<C>>,
        polynomialSpace: ListPolynomialSpace<C, A>,
        crossinline polynomialArgumentCoefficientsTransform: A.(C) -> C = { it },
        crossinline operationToTest: ListPolynomialSpace<C, *>.(pol: ListPolynomial<C>, arg: C) -> ListPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { (coefs, arg, resultCoefs) ->
            polynomialSpace {
                operationToTest(
                    ListPolynomial(coefs.map { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it) }),
                    polynomialSpace.ring.valueOf(arg)
                )
            } shouldBe ListPolynomial(resultCoefs)
        }
    }

    init {
        "Polynomial and Constant" - {
            "plus" - { produceConstantTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol + arg } }
            "minus" - { produceConstantTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg) } }
            "times" - { produceConstantTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> pol * (arg) } }
        }
        "Constant and Polynomial" - {
            "plus" - { produceConstantTests(plusPolynomialIntTestData, rationalPolynomialSpace) { pol, arg -> arg + pol } }
            "minus" - { produceConstantTests(plusPolynomialIntTestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg - pol } }
            "times" - { produceConstantTests(timesPolynomialIntTestData, intModuloPolynomialSpace) { pol, arg -> arg * pol } }
        }

        data class PolynomialPolynomialTestData<C>(
            val firstArgumentCoefficients: List<C>,
            val secondArgumentCoefficients: List<C>,
            val resultCoefficients: List<C>,
        )

        val plusPolynomialPolynomialTestData = listOf(
            PolynomialPolynomialTestData( // (5/9 - 8/9 x - 8/7 x^2) + (-5/7 + 5/1 x + 5/8 x^2) ?= -10/63 + 37/9 x - 29/56 x^2
                listOf(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                listOf(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
                listOf(Rational(-10, 63), Rational(37, 9), Rational(-29, 56)),
            ),
            PolynomialPolynomialTestData( // (-2/9 - 8/3 x) + (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 5/12 x + 2/4 x^2
                listOf(Rational(-2, 9), Rational(-8, 3)),
                listOf(Rational(0), Rational(9, 4), Rational(2, 4)),
                listOf(Rational(-2, 9), Rational(-5, 12), Rational(2, 4)),
            ),
            PolynomialPolynomialTestData( // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) + (-6/3 - 7/2 x + 2/3 x^2) ?= -18/7 - 23/6 x + 2/3 x^2
                listOf(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)),
                listOf(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                listOf(Rational(-18, 7), Rational(-23, 6), Rational(2, 3), Rational(0)),
            ),
            PolynomialPolynomialTestData( // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
                listOf(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
                listOf(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
                listOf(Rational(0), Rational(0), Rational(0)),
            ),
        )
        val timesPolynomialPolynomialTestData = listOf(
            PolynomialPolynomialTestData( // (1 + x + x^2) * (1 - x + x^2) ?= 1 + x^2 + x^4
                listOf(1, 1, 1).map { IntBox(it) },
                listOf(1, -1, 1).map { IntBox(it) },
                listOf(1, 0, 1, 0, 1).map { IntBox(it) },
            ),
            PolynomialPolynomialTestData( // Spoiler: 5 * 7 = 0
                listOf(5, -25, 10).map { IntBox(it) },
                listOf(21, 14, -7).map { IntBox(it) },
                listOf(0, 0, 0, 0, 0).map { IntBox(it) },
            ),
        )

        "Polynomial ring" - {
            "unaryMinus" - {
                rationalPolynomialSpace {
                    -ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) shouldBe
                            ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7))
                    -ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7), Rational(0), Rational(0)) shouldBe
                            ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7), Rational(0), Rational(0))
                }
            }
            "plus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { ListPolynomial(first) + ListPolynomial(second) } shouldBe ListPolynomial(result)
                }
            }
            "minus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { ListPolynomial(first) - ListPolynomial(second.map { ring { -it } }) } shouldBe ListPolynomial(result)
                }
            }
            "times" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    timesPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    intModuloPolynomialSpace { ListPolynomial(first) * ListPolynomial(second) } shouldBe ListPolynomial(result)
                }
            }
        }
    }
}
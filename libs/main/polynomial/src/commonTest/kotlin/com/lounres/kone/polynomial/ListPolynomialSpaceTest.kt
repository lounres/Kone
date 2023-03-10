/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("LocalVariableName")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.*
import com.lounres.kone.polynomial.testUtils.not
import com.lounres.kone.polynomial.testUtils.o
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import com.lounres.kone.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs


class ListPolynomialSpaceTest: FreeSpec() {

    val rationalPolynomialSpace = Rational.field.listPolynomialSpace
    val timesIntModuloPolynomialSpace = IntModuloRing(35).listPolynomialSpace
    val divIntModuloPolynomialSpace = IntModuloField(23).listPolynomialSpace

    sealed interface PITestData<out C> {
        val argumentPolynomialCoefficients: List<C>
        val argumentInt: Int
        val nonOptimizedResultPolynomialCoefficients: List<C>
        operator fun component1() = argumentPolynomialCoefficients
        operator fun component2() = argumentInt
        operator fun component3() = nonOptimizedResultPolynomialCoefficients

        data class Equality<out C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
            val resultPolynomialCoefficients: List<C>,
        ): PITestData<C>
        data class Similarity<out C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
            val resultPolynomial: ListPolynomial<C>,
        ): PITestData<C>
        data class NoChange<out C>(
            override val argumentPolynomialCoefficients: List<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: List<C>,
        ): PITestData<C>
    }
    fun <C> PITestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
        resultPolynomialCoefficients: List<C>,
    ): PITestData<C> = PITestData.Equality(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomialCoefficients,
    )
    fun <C> PITestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
        resultPolynomial: ListPolynomial<C>,
    ): PITestData<C> = PITestData.Similarity(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomial,
    )
    fun <C> PITestData(
        argumentPolynomialCoefficients: List<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: List<C>,
    ): PITestData<C> = PITestData.NoChange(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
    )

    suspend fun <C, A: Ring<C>, PS: ListPolynomialSpace<C, A>> ContainerScope.produceIntTests(
        testDataList: List<PITestData<C>>,
        polynomialSpace: PS,
        polynomialArgumentCoefficientsTransform: (A.(C) -> C)? = null,
        operationToTest: PS.(pol: ListPolynomial<C>, arg: Int) -> ListPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { data ->
            if (polynomialArgumentCoefficientsTransform === null) {
                when (data) {
                    is PITestData.Equality -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs), arg) } shouldBe ListPolynomial(expected)
                    }
                    is PITestData.NoChange -> {
                        val (coefs, arg, _) = data
                        ListPolynomial(coefs).let { polynomialSpace { operationToTest(it, arg) } shouldBeSameInstanceAs it }
                    }
                    is PITestData.Similarity -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs), arg) } shouldBeSameInstanceAs expected
                    }
                }
            } else {
                fun List<C>.update() = map { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it) }
                when (data) {
                    is PITestData.Equality -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe ListPolynomial(expected)
                    }
                    is PITestData.NoChange -> {
                        val (coefs, arg, _) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe ListPolynomial(coefs)
                    }
                    is PITestData.Similarity -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(ListPolynomial(coefs.update()), arg) } shouldBe expected
                    }
                }
            }
        }
    }

    val plusPITestData = listOf(
        PITestData(
            listOf(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
            -3,
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
        ),
        PITestData(
            listOf(Rational(-2), Rational(0), Rational(0), Rational(0)),
            2,
            listOf(Rational(0), Rational(0), Rational(0), Rational(0)),
            listOf(Rational(0), Rational(0), Rational(0), Rational(0)),
        ),
        PITestData(
            listOf(Rational(-2)),
            2,
            listOf(Rational(0)),
            listOf(Rational(0)),
        ),
        PITestData(
            listOf(),
            0,
            listOf(Rational(0)),
        ),
        PITestData(
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
            0,
            listOf(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
        ),
        PITestData(
            listOf(Rational(-2), Rational(0), Rational(0), Rational(0)),
            1,
            listOf(Rational(-1), Rational(0), Rational(0), Rational(0)),
            listOf(Rational(-1), Rational(0), Rational(0), Rational(0)),
        ),
        PITestData(
            listOf(Rational(-2)),
            1,
            listOf(Rational(-1)),
            listOf(Rational(-1)),
        ),
        PITestData(
            listOf(),
            2,
            listOf(Rational(2)),
            listOf(Rational(2)),
        ),
    )

    val timesPITestData = listOf(
        PITestData(
            !listOf(22, 26, 13, 15, 26),
            27,
            !listOf(34, 2, 1, 20, 2),
            !listOf(34, 2, 1, 20, 2),
        ),
        PITestData(
            !listOf(7, 0, 49, 21, 14),
            35,
            !listOf(0, 0, 0, 0, 0),
            !listOf(0, 0, 0, 0, 0),
        ),
        PITestData(
            !listOf(22, 26, 13, 15, 26),
            0,
            !listOf(0, 0, 0, 0, 0),
            timesIntModuloPolynomialSpace.zero,
        ),
        PITestData(
            !listOf(22, 26, 13, 15, 26),
            1,
            !listOf(22, 26, 13, 15, 26),
        ),
    )

    val divPITestData = listOf(
        PITestData(
            !listOf(-9, -12, 9, 12, -18, -4, 7, -16, -5, -19),
            20,
            !listOf(3, 4, 20, 19, 6, 9, 13, 13, 17, 14),
            !listOf(3, 4, 20, 19, 6, 9, 13, 13, 17, 14),
        ),
        PITestData(
            !listOf(-12, 10, -16, -17, -11, -17, 8, -11, -15, 8),
            1,
            !listOf(11, 10, 7, 6, 12, 6, 8, 12, 8, 8),
        ),
    )

    init {
        "Polynomial and Int" - {
            "plus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol + arg } }
            "minus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg) } }
            "times" - { produceIntTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> pol * arg } }
            "div" - { produceIntTests(divPITestData, divIntModuloPolynomialSpace) { pol, arg -> pol / arg } }
        }
        "Polynomial and Long" - {
            "plus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol + arg.toLong() } }
            "minus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg).toLong() } }
            "times" - { produceIntTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> pol * arg.toLong() } }
            "div" - { produceIntTests(divPITestData, divIntModuloPolynomialSpace) { pol, arg -> pol / arg.toLong() } }
        }
        "Int and Polynomial" - {
            "plus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> arg + pol } }
            "minus" - { produceIntTests(plusPITestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg - pol } }
            "times" - { produceIntTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> arg * pol } }
        }
        "Long and Polynomial" - {
            "plus" - { produceIntTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> arg.toLong() + pol } }
            "minus" - { produceIntTests(plusPITestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg.toLong() - pol } }
            "times" - { produceIntTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> arg.toLong() * pol } }
        }
    }

    suspend inline fun <C, A: Ring<C>, PS: ListPolynomialSpace<C, A>> ContainerScope.produceConstantTests(
        testDataList: List<PITestData<C>>,
        polynomialSpace: PS,
        crossinline polynomialArgumentCoefficientsTransform: A.(C) -> C = { it },
        crossinline operationToTest: PS.(pol: ListPolynomial<C>, arg: C) -> ListPolynomial<C>
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
            "plus" - { produceConstantTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol + arg } }
            "minus" - { produceConstantTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> pol - (-arg) } }
            "times" - { produceConstantTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> pol * arg } }
            "div" - { produceIntTests(divPITestData, divIntModuloPolynomialSpace) { pol, arg -> pol / arg } }
        }
        "Constant and Polynomial" - {
            "plus" - { produceConstantTests(plusPITestData, rationalPolynomialSpace) { pol, arg -> arg + pol } }
            "minus" - { produceConstantTests(plusPITestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg - pol } }
            "times" - { produceConstantTests(timesPITestData, timesIntModuloPolynomialSpace) { pol, arg -> arg * pol } }
        }
    }


    data class PPTestData<C>(
        val firstArgumentCoefficients: List<C>,
        val secondArgumentCoefficients: List<C>,
        val resultCoefficients: List<C>,
    )

    val plusPPTestData = listOf(
        PPTestData( // (5/9 - 8/9 x - 8/7 x^2) + (-5/7 + 5/1 x + 5/8 x^2) ?= -10/63 + 37/9 x - 29/56 x^2
            listOf(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
            listOf(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
            listOf(Rational(-10, 63), Rational(37, 9), Rational(-29, 56)),
        ),
        PPTestData( // (-2/9 - 8/3 x) + (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 5/12 x + 2/4 x^2
            listOf(Rational(-2, 9), Rational(-8, 3)),
            listOf(Rational(0), Rational(9, 4), Rational(2, 4)),
            listOf(Rational(-2, 9), Rational(-5, 12), Rational(2, 4)),
        ),
        PPTestData( // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) + (-6/3 - 7/2 x + 2/3 x^2) ?= -18/7 - 23/6 x + 2/3 x^2
            listOf(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)),
            listOf(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
            listOf(Rational(-18, 7), Rational(-23, 6), Rational(2, 3), Rational(0)),
        ),
        PPTestData( // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
            listOf(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
            listOf(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
            listOf(Rational(0), Rational(0), Rational(0)),
        ),
    )

    val timesPPTestData = listOf(
        PPTestData( // (1 + x + x^2) * (1 - x + x^2) ?= 1 + x^2 + x^4
            !listOf(1, 1, 1),
            !listOf(1, -1, 1),
            !listOf(1, 0, 1, 0, 1),
        ),
        PPTestData( // Spoiler: 5 * 7 = 0
            !listOf(5, -25, 10),
            !listOf(21, 14, -7),
            !listOf(0, 0, 0, 0, 0),
        ),
    )

    init {
        "Polynomial ring" - {
            "unaryMinus" - {
                rationalPolynomialSpace {
                    "test 1" - {
                        -ListPolynomial(
                            Rational(5, 9),
                            Rational(-8, 9),
                            Rational(-8, 7)
                        ) shouldBe
                                ListPolynomial(
                                    Rational(-5, 9),
                                    Rational(8, 9),
                                    Rational(8, 7)
                                )
                    }
                    "test 2" - {
                        -ListPolynomial(
                            Rational(5, 9),
                            Rational(-8, 9),
                            Rational(-8, 7),
                            Rational(0),
                            Rational(0)
                        ) shouldBe
                                ListPolynomial(
                                    Rational(-5, 9),
                                    Rational(8, 9),
                                    Rational(8, 7),
                                    Rational(0),
                                    Rational(0)
                                )
                    }
                }
            }
            "plus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPPTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { ListPolynomial(first) + ListPolynomial(second) } shouldBe ListPolynomial(result)
                }
            }
            "minus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPPTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { ListPolynomial(first) - ListPolynomial(second.map { ring { -it } }) } shouldBe ListPolynomial(result)
                }
            }
            "times" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    timesPPTestData
                ) { (first, second, result) ->
                    timesIntModuloPolynomialSpace { ListPolynomial(first) * ListPolynomial(second) } shouldBe ListPolynomial(result)
                }
            }
        }
    }

    data class PPropertiesTestData<C>(
        val argumentsCoefficients: List<C>,
        val degree: Int,
    )

    val pPropertiesTestData = listOf(
        PPropertiesTestData(
            listOf(),
            -1
        ),
        PPropertiesTestData(
            listOf(o),
            0
        ),
        PPropertiesTestData(
            listOf(o, o),
            1
        ),
        PPropertiesTestData(
            listOf(o, o, o),
            2
        ),
        PPropertiesTestData(
            listOf(o, o, o, o),
            3
        ),
    )

    init {
        rationalPolynomialSpace {
            "Polynomial Properties" - {
                "degree" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        ListPolynomial(it.argumentsCoefficients).degree shouldBe it.degree
                    }
                }
            }
        }
    }
}
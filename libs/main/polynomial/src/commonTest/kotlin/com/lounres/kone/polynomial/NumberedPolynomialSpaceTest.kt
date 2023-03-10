/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.*
import com.lounres.kone.polynomial.testUtils.not
import com.lounres.kone.polynomial.testUtils.o
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import com.lounres.kone.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs


class NumberedPolynomialSpaceTest : FreeSpec() {

    val rationalPolynomialSpace = Rational.field.numberedPolynomialSpace
    val timesIntModuloPolynomialSpace = IntModuloRing(35).numberedPolynomialSpace
    val divIntModuloPolynomialSpace = IntModuloField(23).numberedPolynomialSpace

    sealed interface PITestData<out C> {
        val argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>
        val argumentInt: Int
        val nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>
        operator fun component1() = argumentPolynomialCoefficients
        operator fun component2() = argumentInt
        operator fun component3() = nonOptimizedResultPolynomialCoefficients

        data class EqualityTestData<out C>(
            override val argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
            val resultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        ): PITestData<C>
        data class SameTestData<out C>(
            override val argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
            val resultPolynomial: NumberedPolynomial<C>,
        ): PITestData<C>
        data class NoChangeTestData<out C>(
            override val argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        ): PITestData<C>
    }
    fun <C> PITestData(
        argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        resultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
    ): PITestData<C> = PITestData.EqualityTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomialCoefficients,
    )
    fun <C> PITestData(
        argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        resultPolynomial: NumberedPolynomial<C>,
    ): PITestData<C> = PITestData.SameTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomial,
    )
    fun <C> PITestData(
        argumentPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: NumberedPolynomialCoefficients<C>,
    ): PITestData<C> = PITestData.NoChangeTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
    )

    suspend fun <C, A: Ring<C>, PS: NumberedPolynomialSpace<C, A>> ContainerScope.produceIntTests(
        testDataList: List<PITestData<C>>,
        polynomialSpace: PS,
        polynomialArgumentCoefficientsTransform: (A.(C) -> C)? = null,
        operationToTest: PS.(pol: NumberedPolynomial<C>, arg: Int) -> NumberedPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { data ->
            if (polynomialArgumentCoefficientsTransform === null) {
                when (data) {
                    is PITestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(NumberedPolynomialAsIs(coefs), arg) } shouldBe NumberedPolynomialAsIs(expected)
                    }
                    is PITestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        NumberedPolynomialAsIs(coefs).let { polynomialSpace { operationToTest(it, arg) } shouldBeSameInstanceAs it }
                    }
                    is PITestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(NumberedPolynomialAsIs(coefs), arg) } shouldBeSameInstanceAs expected
                    }
                }
            } else {
                fun NumberedPolynomialCoefficients<C>.update() = mapValues { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it.value) }
                when (data) {
                    is PITestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(NumberedPolynomialAsIs(coefs.update()), arg) } shouldBe NumberedPolynomialAsIs(expected)
                    }
                    is PITestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        polynomialSpace { operationToTest(NumberedPolynomialAsIs(coefs.update()), arg) } shouldBe NumberedPolynomialAsIs(coefs)
                    }
                    is PITestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(NumberedPolynomialAsIs(coefs.update()), arg) } shouldBe expected
                    }
                }
            }
        }
    }

    val plusPITestData : List<PITestData<Rational>> = listOf(
        PITestData(
            mapOf(
                listOf<UInt>() to Rational(5, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            mapOf(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
        PITestData(
            mapOf(
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                listOf<UInt>() to Rational(-3, 1),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            mapOf(
                listOf<UInt>() to Rational(-3, 1),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
        PITestData(
            mapOf(
                listOf<UInt>() to Rational(27, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                listOf<UInt>() to Rational(0, 1),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            mapOf(
                listOf<UInt>() to Rational(0, 1),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
        PITestData(
            mapOf(
                listOf<UInt>() to Rational(27, 9),
                listOf(3u) to Rational(0),
                listOf(0u, 4u) to Rational(0),
            ),
            -3,
            mapOf(
                listOf<UInt>() to Rational(0),
                listOf(3u) to Rational(0),
                listOf(0u, 4u) to Rational(0),
            ),
            mapOf(
                listOf<UInt>() to Rational(0),
                listOf(3u) to Rational(0),
                listOf(0u, 4u) to Rational(0),
            ),
        ),
        PITestData(
            mapOf(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
        PITestData(
            mapOf(
                listOf<UInt>() to Rational(0, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                listOf<UInt>() to Rational(0, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
        PITestData(
            mapOf(
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                listOf<UInt>() to Rational(0, 1),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            ),
        ),
    )

    val timesPITestData : List<PITestData<BInt>> = listOf(
        PITestData(
            !mapOf(
                listOf<UInt>() to 22,
                listOf(3u) to 26,
                listOf(0u, 1u) to 13,
                listOf(1u) to 15,
                listOf(0u, 0u, 2u) to 26,
            ),
            27,
            !mapOf(
                listOf<UInt>() to 34,
                listOf(3u) to 2,
                listOf(0u, 1u) to 1,
                listOf(1u) to 20,
                listOf(0u, 0u, 2u) to 2,
            ),
            !mapOf(
                listOf<UInt>() to 34,
                listOf(3u) to 2,
                listOf(0u, 1u) to 1,
                listOf(1u) to 20,
                listOf(0u, 0u, 2u) to 2,
            ),
        ),
        PITestData(
            !mapOf(
                listOf<UInt>() to 7,
                listOf(3u) to 0,
                listOf(0u, 1u) to 49,
                listOf(1u) to 21,
                listOf(0u, 0u, 2u) to 14,
            ),
            15,
            !mapOf(
                listOf<UInt>() to 0,
                listOf(3u) to 0,
                listOf(0u, 1u) to 0,
                listOf(1u) to 0,
                listOf(0u, 0u, 2u) to 0,
            ),
            !mapOf(
                listOf<UInt>() to 0,
                listOf(3u) to 0,
                listOf(0u, 1u) to 0,
                listOf(1u) to 0,
                listOf(0u, 0u, 2u) to 0,
            ),
        ),
        PITestData(
            !mapOf(
                listOf<UInt>() to 22,
                listOf(3u) to 26,
                listOf(0u, 1u) to 13,
                listOf(1u) to 15,
                listOf(0u, 0u, 2u) to 26,
            ),
            0,
            !mapOf(
                listOf<UInt>() to 0,
                listOf(3u) to 0,
                listOf(0u, 1u) to 0,
                listOf(1u) to 0,
                listOf(0u, 0u, 2u) to 0,
            ),
            timesIntModuloPolynomialSpace.zero
        ),
        PITestData(
            !mapOf(
                listOf<UInt>() to 22,
                listOf(3u) to 26,
                listOf(0u, 1u) to 13,
                listOf(1u) to 15,
                listOf(0u, 0u, 2u) to 26,
            ),
            1,
            !mapOf(
                listOf<UInt>() to 22,
                listOf(3u) to 26,
                listOf(0u, 1u) to 13,
                listOf(1u) to 15,
                listOf(0u, 0u, 2u) to 26,
            ),
        ),
    )

    val divPITestData : List<PITestData<BInt>> = listOf( // TODO: Add test data for division tests
//        PolynomialIntTestData(
//            !listOf(-9, -12, 9, 12, -18, -4, 7, -16, -5, -19),
//            20,
//            !listOf(3, 4, 20, 19, 6, 9, 13, 13, 17, 14),
//            !listOf(3, 4, 20, 19, 6, 9, 13, 13, 17, 14),
//        ),
//        PolynomialIntTestData(
//            !listOf(-12, 10, -16, -17, -11, -17, 8, -11, -15, 8),
//            1,
//            !listOf(11, 10, 7, 6, 12, 6, 8, 12, 8, 8),
//        ),
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

    suspend inline fun <C, A: Ring<C>, PS: NumberedPolynomialSpace<C, A>> ContainerScope.produceConstantTests(
        testDataList: List<PITestData<C>>,
        polynomialSpace: PS,
        crossinline polynomialArgumentCoefficientsTransform: A.(C) -> C = { it },
        crossinline operationToTest: PS.(pol: NumberedPolynomial<C>, arg: C) -> NumberedPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { (coefs, arg, resultCoefs) ->
            polynomialSpace {
                operationToTest(
                    NumberedPolynomial<C>(coefs.mapValues { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it.value) }),
                    polynomialSpace.ring.valueOf(arg)
                )
            } shouldBe NumberedPolynomialAsIs(resultCoefs)
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
        val firstArgumentCoefficients: NumberedPolynomialCoefficients<C>,
        val secondArgumentCoefficients: NumberedPolynomialCoefficients<C>,
        val resultCoefficients: NumberedPolynomialCoefficients<C>,
    )

    val plusPPTestData : List<PPTestData<Rational>> = listOf(
        PPTestData(
            mapOf(
                listOf<UInt>() to Rational(6, 4),
                listOf(1u) to Rational(-2, 6),
                listOf(2u) to Rational(10, 6),
                listOf(0u, 1u) to Rational(17, 7),
                listOf(1u, 1u) to Rational(-7, 7),
                listOf(2u, 1u) to Rational(12, 5),
                listOf(0u, 2u) to Rational(12, 7),
                listOf(1u, 2u) to Rational(-10, 3),
                listOf(2u, 2u) to Rational(9, 8),
            ),
            mapOf(
                listOf<UInt>() to Rational(-20, 2),
                listOf(1u) to Rational(0, 9),
                listOf(2u) to Rational(-20, 7),
                listOf(0u, 1u) to Rational(-1, 9),
                listOf(1u, 1u) to Rational(2, 5),
                listOf(2u, 1u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(7, 9),
                listOf(1u, 2u) to Rational(5, 7),
                listOf(2u, 2u) to Rational(-2, 3),
            ),
            mapOf(
                listOf<UInt>() to Rational(-17, 2),
                listOf(1u) to Rational(-1, 3),
                listOf(2u) to Rational(-25, 21),
                listOf(0u, 1u) to Rational(146, 63),
                listOf(1u, 1u) to Rational(-3, 5),
                listOf(2u, 1u) to Rational(61, 15),
                listOf(0u, 2u) to Rational(157, 63),
                listOf(1u, 2u) to Rational(-55, 21),
                listOf(2u, 2u) to Rational(11, 24),
            ),
        ),
        PPTestData(
            mapOf(
                listOf<UInt>() to Rational(6, 4),
                listOf(1u) to Rational(-2, 6),
                listOf(2u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(12, 7),
                listOf(1u, 2u) to Rational(-10, 3),
                listOf(2u, 2u) to Rational(9, 8),
            ),
            mapOf(
                listOf<UInt>() to Rational(-20, 2),
                listOf(1u) to Rational(0, 9),
                listOf(2u) to Rational(-20, 7),
                listOf(0u, 1u) to Rational(-1, 9),
                listOf(1u, 1u) to Rational(2, 5),
                listOf(2u, 1u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(7, 9),
                listOf(1u, 2u) to Rational(5, 7),
                listOf(2u, 2u) to Rational(-2, 3),
            ),
            mapOf(
                listOf<UInt>() to Rational(-17, 2),
                listOf(1u) to Rational(-1, 3),
                listOf(2u) to Rational(-25, 21),
                listOf(0u, 1u) to Rational(-1, 9),
                listOf(1u, 1u) to Rational(2, 5),
                listOf(2u, 1u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(157, 63),
                listOf(1u, 2u) to Rational(-55, 21),
                listOf(2u, 2u) to Rational(11, 24),
            ),
        ),
        PPTestData(
            mapOf(
                listOf<UInt>() to Rational(6, 4),
                listOf(1u) to Rational(-2, 6),
                listOf(2u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(12, 7),
                listOf(1u, 2u) to Rational(-10, 3),
                listOf(2u, 2u) to Rational(9, 8),
            ),
            mapOf(
                listOf<UInt>() to Rational(-20, 2),
                listOf(1u) to Rational(0, 9),
                listOf(2u) to Rational(-20, 7),
                listOf(0u, 1u) to Rational(-1, 9),
                listOf(1u, 1u) to Rational(2, 5),
                listOf(2u, 1u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(0),
                listOf(1u, 2u) to Rational(0),
                listOf(2u, 2u) to Rational(0),
            ),
            mapOf(
                listOf<UInt>() to Rational(-17, 2),
                listOf(1u) to Rational(-1, 3),
                listOf(2u) to Rational(-25, 21),
                listOf(0u, 1u) to Rational(-1, 9),
                listOf(1u, 1u) to Rational(2, 5),
                listOf(2u, 1u) to Rational(10, 6),
                listOf(0u, 2u) to Rational(12, 7),
                listOf(1u, 2u) to Rational(-10, 3),
                listOf(2u, 2u) to Rational(9, 8),
            ),
        ),
        PPTestData(
            mapOf(
                listOf<UInt>() to Rational(6, 4),
                listOf(1u) to Rational(-2, 6),
                listOf(2u) to Rational(10, 6),
                listOf(0u, 1u) to Rational(17, 7),
                listOf(1u, 1u) to Rational(-7, 7),
                listOf(2u, 1u) to Rational(12, 5),
                listOf(0u, 2u) to Rational(12, 7),
                listOf(1u, 2u) to Rational(-10, 3),
                listOf(2u, 2u) to Rational(9, 8),
            ),
            mapOf(
                listOf<UInt>() to Rational(-6, 4),
                listOf(1u) to Rational(2, 6),
                listOf(2u) to Rational(-10, 6),
                listOf(0u, 1u) to Rational(-17, 7),
                listOf(1u, 1u) to Rational(7, 7),
                listOf(2u, 1u) to Rational(-12, 5),
                listOf(0u, 2u) to Rational(-12, 7),
                listOf(1u, 2u) to Rational(10, 3),
                listOf(2u, 2u) to Rational(-9, 8),
            ),
            mapOf(
                listOf<UInt>() to Rational(0),
                listOf(1u) to Rational(0),
                listOf(2u) to Rational(0),
                listOf(0u, 1u) to Rational(0),
                listOf(1u, 1u) to Rational(0),
                listOf(2u, 1u) to Rational(0),
                listOf(0u, 2u) to Rational(0),
                listOf(1u, 2u) to Rational(0),
                listOf(2u, 2u) to Rational(0),
            ),
        ),
    )

    val timesPPTestData : List<PPTestData<BInt>> = listOf(
        PPTestData( // (p + q + r) * (p^2 + q^2 + r^2 - pq - pr - qr) = p^3 + q^3 + r^3 - 3pqr
            !mapOf(
                listOf(1u) to 1,
                listOf(0u, 1u) to 1,
                listOf(0u, 0u, 1u) to 1,
            ),
            !mapOf(
                listOf(2u) to 1,
                listOf(0u, 2u) to 1,
                listOf(0u, 0u, 2u) to 1,
                listOf(1u, 1u) to -1,
                listOf(0u, 1u, 1u) to -1,
                listOf(1u, 0u, 1u) to -1,
            ),
            !mapOf(
                listOf(3u) to 1,
                listOf(0u, 3u) to 1,
                listOf(0u, 0u, 3u) to 1,
                listOf(1u, 2u) to 0,
                listOf(0u, 1u, 2u) to 0,
                listOf(2u, 0u, 1u) to 0,
                listOf(1u, 0u, 2u) to 0,
                listOf(2u, 1u) to 0,
                listOf(0u, 2u, 1u) to 0,
                listOf(1u, 1u, 1u) to 32,
            ),
        ),
        PPTestData( // Spoiler: 5 * 7 = 0
            !mapOf(
                listOf(1u) to 5,
                listOf(0u, 1u) to -25,
                listOf(0u, 0u, 1u) to 10,
            ),
            !mapOf(
                listOf(1u) to 21,
                listOf(0u, 1u) to 14,
                listOf(0u, 0u, 1u) to -7,
            ),
            !mapOf(
                listOf(2u) to 0,
                listOf(0u, 2u) to 0,
                listOf(0u, 0u, 2u) to 0,
                listOf(1u, 1u) to 0,
                listOf(0u, 1u, 1u) to 0,
                listOf(1u, 0u, 1u) to 0,
            ),
        ),
    )

    init {
        "Polynomial ring" - {
            "unaryMinus" - {
                rationalPolynomialSpace {
                    "test 1" - {
                        -NumberedPolynomialAsIs(
                            listOf(5u) to Rational(5, 9),
                            listOf<UInt>() to Rational(-8, 9),
                            listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(-8, 7),
                        ) shouldBe
                                NumberedPolynomialAsIs(
                                    listOf(5u) to Rational(-5, 9),
                                    listOf<UInt>() to Rational(8, 9),
                                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(8, 7),
                                )
                    }
                    "test 2" - {
                        -NumberedPolynomialAsIs(
                            listOf(5u) to Rational(5, 9),
                            listOf<UInt>() to Rational(-8, 9),
                            listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(-8, 7),
                            listOf(0u, 4u) to Rational(0),
                            listOf(5u) to Rational(0),
                        ) shouldBe
                                NumberedPolynomialAsIs(
                                    listOf(5u) to Rational(-5, 9),
                                    listOf<UInt>() to Rational(8, 9),
                                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(8, 7),
                                    listOf(0u, 4u) to Rational(0),
                                    listOf(5u) to Rational(0),
                                )
                    }
                }
            }
            "plus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPPTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { NumberedPolynomialAsIs(first) + NumberedPolynomialAsIs(second) } shouldBe NumberedPolynomialAsIs(result)
                }
            }
            "minus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPPTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { NumberedPolynomialAsIs(first) - NumberedPolynomialAsIs(second.mapValues { ring { -it.value } }) } shouldBe NumberedPolynomialAsIs(result)
                }
            }
            "times" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    timesPPTestData
                ) { (first, second, result) ->
                    timesIntModuloPolynomialSpace { NumberedPolynomialAsIs(first) * NumberedPolynomialAsIs(second) } shouldBe NumberedPolynomialAsIs(result)
                }
            }
        }
    }

    data class PPropertiesTestData<C>(
        val argumentCoefficients: NumberedPolynomialCoefficients<C>,
        val degree: Int,
        val degrees: NumberedMonomialSignature,
    )

    val pPropertiesTestData : List<PPropertiesTestData<Rational>> = listOf(
        PPropertiesTestData(
            mapOf(),
            -1,
            listOf(),
        ),
        PPropertiesTestData(
            mapOf(
                listOf<UInt>() to o
            ),
            0,
            listOf(),
        ),
        PPropertiesTestData(
            mapOf(
                listOf(1u, 2u, 3u) to o
            ),
            6,
            listOf(1u, 2u, 3u),
        ),
        PPropertiesTestData(
            mapOf(
                listOf(0u, 1u, 2u, 1u) to o
            ),
            4,
            listOf(0u, 1u, 2u, 1u),
        ),
        PPropertiesTestData(
            mapOf(
                listOf<UInt>() to o,
                listOf(0u, 1u) to o,
                listOf(2u, 0u, 1u) to o,
            ),
            3,
            listOf(2u, 1u, 1u),
        ),
        PPropertiesTestData(
            mapOf(
                listOf<UInt>() to o,
                listOf(1u, 2u) to o,
                listOf(0u, 1u, 2u) to o,
                listOf(2u, 0u, 1u) to o,
                listOf(0u, 0u, 0u, 4u) to o,
            ),
            4,
            listOf(2u, 2u, 2u, 4u),
        ),
    )

    init {
        rationalPolynomialSpace {
            "Polynomial properties" - {
                "lastVariable" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).lastVariable shouldBe it.degrees.lastIndex
                    }
                }
                "degree" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).degree shouldBe it.degree
                    }
                }
                "degrees" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).degrees shouldBe it.degrees
                    }
                }
                "degreeBy Variable" - {
                    fun NumberedPolynomial<Rational>.collectDegrees(
                        lastVariable: Int = this.lastVariable + 1
                    ): List<UInt> = List(lastVariable + 1) { degreeBy(it) }

                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).collectDegrees() shouldBe (it.degrees + 0u)
                    }
                }
                "degreeBy Collection" - {
                    fun NumberedPolynomial<Rational>.checkDegreeBy() {
                        val lastVariable = this.lastVariable + 1
                        val variablesCollectionSequence: Sequence<List<Int>> = sequence {
                            val appearances = MutableList(lastVariable + 1) { 0 }
                            while (true) {
                                yield(
                                    buildList {
                                        for ((variableIndex, count) in appearances.withIndex()) repeat(count) { add(variableIndex) }
                                    }
                                )
                                val indexChange = appearances.indexOfFirst { it < 4 }
                                if (indexChange == -1) break
                                appearances[indexChange] += 1
                                for (index in 0 until indexChange) appearances[index] = 0
                            }
                        }
                        for (variablesCollection in variablesCollectionSequence) {
                            val expected = coefficients.keys.maxOfOrNull { degs -> degs.withIndex().filter { (index, _) -> index in variablesCollection }.sumOf { it.value } } ?: 0u
                            val actual = degreeBy(variablesCollection)
                            if (actual != expected)
                                error("Incorrect answer for variable collection $variablesCollection: expected $expected, actual $actual")
                        }
                    }

                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).checkDegreeBy()
                    }
                }
                "countOfVariables" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        pPropertiesTestData
                    ) {
                        NumberedPolynomialAsIs(it.argumentCoefficients).countOfVariables shouldBe it.degrees.count { it != 0u }
                    }
                }
            }
        }
    }

    data class RFPropertiesTestData<C>(
        val argumentNumeratorCoefficients: NumberedPolynomialCoefficients<C>,
        val argumentDenominatorCoefficients: NumberedPolynomialCoefficients<C>? = null,
        val countOfVariables: Int
    )

    val rfPropertiesTestData = listOf(
        RFPropertiesTestData(
            mapOf(),
            countOfVariables = 0
        ),
        RFPropertiesTestData(
            mapOf(),
            mapOf(),
            countOfVariables = 0
        ),
        RFPropertiesTestData(
            mapOf(
                listOf<UInt>() to o
            ),
            countOfVariables = 0
        ),
        RFPropertiesTestData(
            mapOf(
                listOf(1u, 2u, 3u) to o
            ),
            countOfVariables = 3
        ),
        RFPropertiesTestData(
            mapOf(
                listOf(0u, 1u, 0u, 1u) to o
            ),
            mapOf(
                listOf(0u, 0u, 2u) to o
            ),
            countOfVariables = 3
        ),
        RFPropertiesTestData(
            mapOf(
                listOf<UInt>() to o,
                listOf(0u, 1u) to o,
                listOf(2u, 0u, 1u) to o,
            ),
            countOfVariables = 3
        ),
        RFPropertiesTestData(
            mapOf(
                listOf<UInt>() to o,
                listOf(1u, 2u) to o,
                listOf(2u, 0u, 1u) to o,
            ),
            mapOf(
                listOf(0u, 1u, 2u) to o,
                listOf(0u, 0u, 0u, 4u) to o,
            ),
            countOfVariables = 4
        ),
    )

    init {
        Rational.field.numberedRationalFunctionSpace {
            "Rational Functional properties" - {
                "countOfVariables" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}"},
                        rfPropertiesTestData
                    ) {
                        val rationalFunction =
                            if (it.argumentDenominatorCoefficients === null)
                                NumberedRationalFunction(it.argumentNumeratorCoefficients)
                            else
                                NumberedRationalFunction(it.argumentNumeratorCoefficients, it.argumentDenominatorCoefficients)
                        rationalFunction.countOfVariables shouldBe it.countOfVariables
                    }
                }
            }
        }
    }
}
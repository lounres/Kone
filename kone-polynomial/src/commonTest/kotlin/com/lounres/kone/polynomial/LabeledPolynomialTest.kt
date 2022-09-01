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
import kotlin.jvm.JvmName
import space.kscience.kmath.expressions.Symbol

// TODO: Тесты на конвертацию.

class LabeledPolynomialTest : FreeSpec() {

    val rationalPolynomialSpace = Rational.field.labeledPolynomialSpace
    val intModuloPolynomialSpace = IntBoxModuloRing(35).labeledPolynomialSpace

    init {
        rationalPolynomialSpace {
            "Variable and Int" - {
                "plus" - {
                    "test 1" {
                        x + 5 shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x + 0 shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        x - 5 shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(-5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x - 0 shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        x * 5 shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        x * 0 shouldBeSameInstanceAs zero
                    }
                }
            }
            "Variable and Long" - {
                "plus" - {
                    "test 1" {
                        x + 5L shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x + 0L shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        x - 5L shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(-5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x - 0L shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        x * 5L shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        x * 0L shouldBeSameInstanceAs zero
                    }
                }
            }
            "Int and Variable" - {
                "plus" - {
                    "test 1" {
                        5 + x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        0 + x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        5 - x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                    "test 2" {
                        0 - x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        5 * x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        0 * x shouldBeSameInstanceAs zero
                    }
                }
            }
            "Long and Variable" - {
                "plus" - {
                    "test 1" {
                        5L + x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        0L + x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        5L - x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                    "test 2" {
                        0L - x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        5L * x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        0L * x shouldBeSameInstanceAs zero
                    }
                }
            }
        }
    }
    
    sealed interface PolynomialIntTestData<C> {
        val argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>
        val argumentInt: Int
        val nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>
        operator fun component1() = argumentPolynomialCoefficients
        operator fun component2() = argumentInt
        operator fun component3() = nonOptimizedResultPolynomialCoefficients
        data class EqualityTestData<C>(
            override val argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
            val resultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        ): PolynomialIntTestData<C>
        data class SameTestData<C>(
            override val argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
            val resultPolynomial: LabeledPolynomial<C>,
        ): PolynomialIntTestData<C>
        data class NoChangeTestData<C>(
            override val argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
            override val argumentInt: Int,
            override val nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        ): PolynomialIntTestData<C>
    }
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        resultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.EqualityTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomialCoefficients,
    )
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        resultPolynomial: LabeledPolynomial<C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.SameTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
        resultPolynomial,
    )
    fun <C> PolynomialIntTestData(
        argumentPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
        argumentInt: Int,
        nonOptimizedResultPolynomialCoefficients: Map<Map<Symbol, UInt>, C>,
    ): PolynomialIntTestData<C> = PolynomialIntTestData.NoChangeTestData(
        argumentPolynomialCoefficients,
        argumentInt,
        nonOptimizedResultPolynomialCoefficients,
    )

    @JvmName("produceTestsInt")
    suspend fun <C, A: Ring<C>> ContainerScope.produceIntTests(
        testDataList: List<PolynomialIntTestData<C>>,
        polynomialSpace: LabeledPolynomialSpace<C, A>,
        polynomialArgumentCoefficientsTransform: (A.(C) -> C)? = null,
        operationToTest: LabeledPolynomialSpace<C, *>.(pol: LabeledPolynomial<C>, arg: Int) -> LabeledPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { data ->
            if (polynomialArgumentCoefficientsTransform === null) {
                when (data) {
                    is PolynomialIntTestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(LabeledPolynomialAsIs(coefs), arg) } shouldBe LabeledPolynomialAsIs(expected)
                    }
                    is PolynomialIntTestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        LabeledPolynomialAsIs(coefs).let { polynomialSpace { operationToTest(it, arg) } shouldBeSameInstanceAs it }
                    }
                    is PolynomialIntTestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(LabeledPolynomialAsIs(coefs), arg) } shouldBeSameInstanceAs expected
                    }
                }
            } else {
                fun Map<Map<Symbol, UInt>, C>.update() = mapValues { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it.value) }
                when (data) {
                    is PolynomialIntTestData.EqualityTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(LabeledPolynomialAsIs(coefs.update()), arg) } shouldBe LabeledPolynomialAsIs(expected)
                    }
                    is PolynomialIntTestData.NoChangeTestData -> {
                        val (coefs, arg, _) = data
                        polynomialSpace { operationToTest(LabeledPolynomialAsIs(coefs.update()), arg) } shouldBe LabeledPolynomialAsIs(coefs)
                    }
                    is PolynomialIntTestData.SameTestData -> {
                        val (coefs, arg, _, expected) = data
                        polynomialSpace { operationToTest(LabeledPolynomialAsIs(coefs.update()), arg) } shouldBe expected
                    }
                }
            }
        }
    }

    val plusPolynomialIntTestData = listOf(
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(5, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-3, 1),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-3, 1),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(27, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            -3,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0, 1),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0, 1),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(27, 9),
                mapOf(x to 3u) to Rational(0),
                mapOf(y to 4u) to Rational(0),
            ),
            -3,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0),
                mapOf(x to 3u) to Rational(0),
                mapOf(y to 4u) to Rational(0),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0),
                mapOf(x to 3u) to Rational(0),
                mapOf(y to 4u) to Rational(0),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
            0,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(y to 4u) to Rational(-8, 7),
            ),
        ),
    )

    val timesPolynomialIntTestData = listOf(
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to 22,
                mapOf(x to 3u) to 26,
                mapOf(y to 1u) to 13,
                mapOf(x to 1u) to 15,
                mapOf(z to 2u) to 26,
            ).mapValues { IntBox(it.value) },
            27,
            mapOf(
                mapOf<Symbol, UInt>() to 34,
                mapOf(x to 3u) to 2,
                mapOf(y to 1u) to 1,
                mapOf(x to 1u) to 20,
                mapOf(z to 2u) to 2,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf<Symbol, UInt>() to 34,
                mapOf(x to 3u) to 2,
                mapOf(y to 1u) to 1,
                mapOf(x to 1u) to 20,
                mapOf(z to 2u) to 2,
            ).mapValues { IntBox(it.value) },
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to 7,
                mapOf(x to 3u) to 0,
                mapOf(y to 1u) to 49,
                mapOf(x to 1u) to 21,
                mapOf(z to 2u) to 14,
            ).mapValues { IntBox(it.value) },
            15,
            mapOf(
                mapOf<Symbol, UInt>() to 0,
                mapOf(x to 3u) to 0,
                mapOf(y to 1u) to 0,
                mapOf(x to 1u) to 0,
                mapOf(z to 2u) to 0,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf<Symbol, UInt>() to 0,
                mapOf(x to 3u) to 0,
                mapOf(y to 1u) to 0,
                mapOf(x to 1u) to 0,
                mapOf(z to 2u) to 0,
            ).mapValues { IntBox(it.value) },
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to 22,
                mapOf(x to 3u) to 26,
                mapOf(y to 1u) to 13,
                mapOf(x to 1u) to 15,
                mapOf(z to 2u) to 26,
            ).mapValues { IntBox(it.value) },
            0,
            mapOf(
                mapOf<Symbol, UInt>() to 0,
                mapOf(x to 3u) to 0,
                mapOf(y to 1u) to 0,
                mapOf(x to 1u) to 0,
                mapOf(z to 2u) to 0,
            ).mapValues { IntBox(it.value) },
            intModuloPolynomialSpace.zero
        ),
        PolynomialIntTestData(
            mapOf(
                mapOf<Symbol, UInt>() to 22,
                mapOf(x to 3u) to 26,
                mapOf(y to 1u) to 13,
                mapOf(x to 1u) to 15,
                mapOf(z to 2u) to 26,
            ).mapValues { IntBox(it.value) },
            1,
            mapOf(
                mapOf<Symbol, UInt>() to 22,
                mapOf(x to 3u) to 26,
                mapOf(y to 1u) to 13,
                mapOf(x to 1u) to 15,
                mapOf(z to 2u) to 26,
            ).mapValues { IntBox(it.value) },
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

        rationalPolynomialSpace {
            "Variable and Constant" - {
                "plus" - {
                    "test 1" {
                        x + Rational(5) shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x + Rational(0) shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(0),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        x - Rational(5) shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(-5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        x - Rational(0) shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(0),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        x * Rational(5) shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        x * Rational(0) shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(0),
                        )
                    }
                }
            }
            "Constant and Variable" - {
                "plus" - {
                    "test 1" {
                        Rational(5) + x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                    "test 2" {
                        Rational(0) + x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(0),
                            mapOf(x to 1u) to Rational(1),
                        )
                    }
                }
                "minus" - {
                    "test 1" {
                        Rational(5) - x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(5),
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                    "test 2" {
                        Rational(0) - x shouldBe LabeledPolynomialAsIs(
                            mapOf<Symbol, UInt>() to Rational(0),
                            mapOf(x to 1u) to Rational(-1),
                        )
                    }
                }
                "times" - {
                    "test 1" {
                        Rational(5) * x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(5),
                        )
                    }
                    "test 2" {
                        Rational(0) * x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(0),
                        )
                    }
                }
            }
        }
    }

    suspend inline fun <C, A: Ring<C>> ContainerScope.produceConstantTests(
        testDataList: List<PolynomialIntTestData<C>>,
        polynomialSpace: LabeledPolynomialSpace<C, A>,
        crossinline polynomialArgumentCoefficientsTransform: A.(C) -> C = { it },
        crossinline operationToTest: LabeledPolynomialSpace<C, A>.(pol: LabeledPolynomial<C>, arg: C) -> LabeledPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { (coefs, arg, resultCoefs) ->
            polynomialSpace {
                operationToTest(
                    LabeledPolynomialAsIs(coefs.mapValues { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it.value) }),
                    polynomialSpace.ring.valueOf(arg)
                )
            } shouldBe LabeledPolynomialAsIs(resultCoefs)
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

        "Variable \"ring\"" - {
            rationalPolynomialSpace {
                "unaryPlus" {
                    +x shouldBe LabeledPolynomialAsIs(
                        mapOf(x to 1u) to Rational(1)
                    )
                }
                "unaryMinus" {
                    -x shouldBe LabeledPolynomialAsIs(
                        mapOf(x to 1u) to Rational(-1)
                    )
                }
                "plus" - {
                    "different variables" {
                        x + y shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                            mapOf(y to 1u) to Rational(1),
                        )
                    }
                    "same variable" {
                        x + x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(2),
                        )
                    }
                }
                "minus" - {
                    "different variables" {
                        x - y shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u) to Rational(1),
                            mapOf(y to 1u) to Rational(-1),
                        )
                    }
                    "same variable" {
                        x - x shouldBeSameInstanceAs zero
                    }
                }
                "times" - {
                    "different variables" {
                        x * y shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 1u, y to 1u) to Rational(1),
                        )
                    }
                    "same variable" {
                        x * x shouldBe LabeledPolynomialAsIs(
                            mapOf(x to 2u) to Rational(1),
                        )
                    }
                }
            }
        }
    }

    data class PolynomialVariableTestData<C>(
        val argCoefficients: Map<Map<Symbol, UInt>, C>,
        val argVariable: Symbol,
        val resultCoefficients: Map<Map<Symbol, UInt>, C>,
    )

    suspend inline fun <C, A: Ring<C>> ContainerScope.produceVariableTests(
        testDataList: List<PolynomialVariableTestData<C>>,
        polynomialSpace: LabeledPolynomialSpace<C, A>,
        crossinline polynomialArgumentCoefficientsTransform: A.(C) -> C = { it },
        crossinline polynomialResultCoefficientsTransform: A.(C) -> C = { it },
        crossinline operationToTest: LabeledPolynomialSpace<C, A>.(pol: LabeledPolynomial<C>, arg: Symbol) -> LabeledPolynomial<C>
    ) {
        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            testDataList
        ) { (coefs, arg, resultCoefs) ->
            polynomialSpace {
                operationToTest(
                    LabeledPolynomialAsIs(coefs.mapValues { polynomialSpace.ring.polynomialArgumentCoefficientsTransform(it.value) }),
                    arg
                )
            } shouldBe LabeledPolynomialAsIs(resultCoefs.mapValues { polynomialSpace.ring.polynomialResultCoefficientsTransform(it.value) })
        }
    }

    val plusPolynomialVariableTestData = listOf(
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            x,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(7, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            )
        ),
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            y,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(6, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            )
        ),
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            iota,
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
                mapOf(iota to 1u) to Rational(1),
            )
        ),
    )

    val timesPolynomialVariableTestData = listOf(
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            x,
            mapOf(
                mapOf(x to 1u) to Rational(-16, 4),
                mapOf(x to 2u) to Rational(4, 3),
                mapOf(x to 3u) to Rational(3, 8),
                mapOf(x to 1u, y to 1u) to Rational(-1, 7),
                mapOf(x to 2u, y to 1u) to Rational(-15, 3),
                mapOf(x to 3u, y to 1u) to Rational(6, 5),
                mapOf(x to 1u, y to 2u) to Rational(-13, 3),
                mapOf(x to 2u, y to 2u) to Rational(13, 4),
                mapOf(x to 3u, y to 2u) to Rational(11, 8),
            )
        ),
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            y,
            mapOf(
                mapOf(y to 1u) to Rational(-16, 4),
                mapOf(x to 1u, y to 1u) to Rational(4, 3),
                mapOf(x to 2u, y to 1u) to Rational(3, 8),
                mapOf(y to 2u) to Rational(-1, 7),
                mapOf(x to 1u, y to 2u) to Rational(-15, 3),
                mapOf(x to 2u, y to 2u) to Rational(6, 5),
                mapOf(y to 3u) to Rational(-13, 3),
                mapOf(x to 1u, y to 3u) to Rational(13, 4),
                mapOf(x to 2u, y to 3u) to Rational(11, 8),
            )
        ),
        PolynomialVariableTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-16, 4),
                mapOf(x to 1u) to Rational(4, 3),
                mapOf(x to 2u) to Rational(3, 8),
                mapOf(y to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u) to Rational(6, 5),
                mapOf(y to 2u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u) to Rational(11, 8),
            ),
            iota,
            mapOf(
                mapOf(iota to 1u) to Rational(-16, 4),
                mapOf(x to 1u, iota to 1u) to Rational(4, 3),
                mapOf(x to 2u, iota to 1u) to Rational(3, 8),
                mapOf(y to 1u, iota to 1u) to Rational(-1, 7),
                mapOf(x to 1u, y to 1u, iota to 1u) to Rational(-15, 3),
                mapOf(x to 2u, y to 1u, iota to 1u) to Rational(6, 5),
                mapOf(y to 2u, iota to 1u) to Rational(-13, 3),
                mapOf(x to 1u, y to 2u, iota to 1u) to Rational(13, 4),
                mapOf(x to 2u, y to 2u, iota to 1u) to Rational(11, 8),
            )
        ),
    )

    init {
        "Polynomial and Variable" - {
            "plus" - {
                produceVariableTests(plusPolynomialVariableTestData, rationalPolynomialSpace) { pol, arg -> pol + arg }
            }
            "minus" - {
                produceVariableTests(plusPolynomialVariableTestData, rationalPolynomialSpace, { -it }, { -it }) { pol, arg -> pol - arg }
            }
            "times" - {
                produceVariableTests(timesPolynomialVariableTestData, rationalPolynomialSpace) { pol, arg -> pol * arg }
            }
        }
        "Variable and Polynomial" - {
            "plus" - {
                produceVariableTests(plusPolynomialVariableTestData, rationalPolynomialSpace) { pol, arg -> arg + pol }
            }
            "minus" - {
                produceVariableTests(plusPolynomialVariableTestData, rationalPolynomialSpace, { -it }) { pol, arg -> arg - pol }
            }
            "times" - {
                produceVariableTests(timesPolynomialVariableTestData, rationalPolynomialSpace) { pol, arg -> arg * pol }
            }
        }
    }


    data class PolynomialPolynomialTestData<C>(
        val firstArgumentCoefficients: Map<Map<Symbol, UInt>, C>,
        val secondArgumentCoefficients: Map<Map<Symbol, UInt>, C>,
        val resultCoefficients: Map<Map<Symbol, UInt>, C>,
    )

    val plusPolynomialPolynomialTestData = listOf(
        PolynomialPolynomialTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(6, 4),
                mapOf(x to 1u) to Rational(-2, 6),
                mapOf(x to 2u) to Rational(10, 6),
                mapOf(y to 1u) to Rational(17, 7),
                mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                mapOf(x to 2u, y to 1u) to Rational(12, 5),
                mapOf(y to 2u) to Rational(12, 7),
                mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                mapOf(x to 2u, y to 2u) to Rational(9, 8),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-20, 2),
                mapOf(x to 1u) to Rational(0, 9),
                mapOf(x to 2u) to Rational(-20, 7),
                mapOf(y to 1u) to Rational(-1, 9),
                mapOf(x to 1u, y to 1u) to Rational(2, 5),
                mapOf(x to 2u, y to 1u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(7, 9),
                mapOf(x to 1u, y to 2u) to Rational(5, 7),
                mapOf(x to 2u, y to 2u) to Rational(-2, 3),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-17, 2),
                mapOf(x to 1u) to Rational(-1, 3),
                mapOf(x to 2u) to Rational(-25, 21),
                mapOf(y to 1u) to Rational(146, 63),
                mapOf(x to 1u, y to 1u) to Rational(-3, 5),
                mapOf(x to 2u, y to 1u) to Rational(61, 15),
                mapOf(y to 2u) to Rational(157, 63),
                mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                mapOf(x to 2u, y to 2u) to Rational(11, 24),
            ),
        ),
        PolynomialPolynomialTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(6, 4),
                mapOf(x to 1u) to Rational(-2, 6),
                mapOf(x to 2u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(12, 7),
                mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                mapOf(x to 2u, y to 2u) to Rational(9, 8),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-20, 2),
                mapOf(x to 1u) to Rational(0, 9),
                mapOf(x to 2u) to Rational(-20, 7),
                mapOf(y to 1u) to Rational(-1, 9),
                mapOf(x to 1u, y to 1u) to Rational(2, 5),
                mapOf(x to 2u, y to 1u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(7, 9),
                mapOf(x to 1u, y to 2u) to Rational(5, 7),
                mapOf(x to 2u, y to 2u) to Rational(-2, 3),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-17, 2),
                mapOf(x to 1u) to Rational(-1, 3),
                mapOf(x to 2u) to Rational(-25, 21),
                mapOf(y to 1u) to Rational(-1, 9),
                mapOf(x to 1u, y to 1u) to Rational(2, 5),
                mapOf(x to 2u, y to 1u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(157, 63),
                mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                mapOf(x to 2u, y to 2u) to Rational(11, 24),
            ),
        ),
        PolynomialPolynomialTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(6, 4),
                mapOf(x to 1u) to Rational(-2, 6),
                mapOf(x to 2u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(12, 7),
                mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                mapOf(x to 2u, y to 2u) to Rational(9, 8),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-20, 2),
                mapOf(x to 1u) to Rational(0, 9),
                mapOf(x to 2u) to Rational(-20, 7),
                mapOf(y to 1u) to Rational(-1, 9),
                mapOf(x to 1u, y to 1u) to Rational(2, 5),
                mapOf(x to 2u, y to 1u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(0),
                mapOf(x to 1u, y to 2u) to Rational(0),
                mapOf(x to 2u, y to 2u) to Rational(0),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-17, 2),
                mapOf(x to 1u) to Rational(-1, 3),
                mapOf(x to 2u) to Rational(-25, 21),
                mapOf(y to 1u) to Rational(-1, 9),
                mapOf(x to 1u, y to 1u) to Rational(2, 5),
                mapOf(x to 2u, y to 1u) to Rational(10, 6),
                mapOf(y to 2u) to Rational(12, 7),
                mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                mapOf(x to 2u, y to 2u) to Rational(9, 8),
            ),
        ),
        PolynomialPolynomialTestData(
            mapOf(
                mapOf<Symbol, UInt>() to Rational(6, 4),
                mapOf(x to 1u) to Rational(-2, 6),
                mapOf(x to 2u) to Rational(10, 6),
                mapOf(y to 1u) to Rational(17, 7),
                mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                mapOf(x to 2u, y to 1u) to Rational(12, 5),
                mapOf(y to 2u) to Rational(12, 7),
                mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                mapOf(x to 2u, y to 2u) to Rational(9, 8),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(-6, 4),
                mapOf(x to 1u) to Rational(2, 6),
                mapOf(x to 2u) to Rational(-10, 6),
                mapOf(y to 1u) to Rational(-17, 7),
                mapOf(x to 1u, y to 1u) to Rational(7, 7),
                mapOf(x to 2u, y to 1u) to Rational(-12, 5),
                mapOf(y to 2u) to Rational(-12, 7),
                mapOf(x to 1u, y to 2u) to Rational(10, 3),
                mapOf(x to 2u, y to 2u) to Rational(-9, 8),
            ),
            mapOf(
                mapOf<Symbol, UInt>() to Rational(0),
                mapOf(x to 1u) to Rational(0),
                mapOf(x to 2u) to Rational(0),
                mapOf(y to 1u) to Rational(0),
                mapOf(x to 1u, y to 1u) to Rational(0),
                mapOf(x to 2u, y to 1u) to Rational(0),
                mapOf(y to 2u) to Rational(0),
                mapOf(x to 1u, y to 2u) to Rational(0),
                mapOf(x to 2u, y to 2u) to Rational(0),
            ),
        ),
    )

    val timesPolynomialPolynomialTestData = listOf(
        PolynomialPolynomialTestData( // (p + q + r) * (p^2 + q^2 + r^2 - pq - pr - qr) = p^3 + q^3 + r^3 - 3pqr
            mapOf(
                mapOf(x to 1u) to 1,
                mapOf(y to 1u) to 1,
                mapOf(z to 1u) to 1,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf(x to 2u) to 1,
                mapOf(y to 2u) to 1,
                mapOf(z to 2u) to 1,
                mapOf(x to 1u, y to 1u) to -1,
                mapOf(y to 1u, z to 1u) to -1,
                mapOf(x to 1u, z to 1u) to -1,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf(x to 3u) to 1,
                mapOf(y to 3u) to 1,
                mapOf(z to 3u) to 1,
                mapOf(x to 1u, y to 2u) to 0,
                mapOf(y to 1u, z to 2u) to 0,
                mapOf(x to 2u, z to 1u) to 0,
                mapOf(x to 1u, z to 2u) to 0,
                mapOf(x to 2u, y to 1u) to 0,
                mapOf(y to 2u, z to 1u) to 0,
                mapOf(x to 1u, y to 1u, z to 1u) to -3,
            ).mapValues { IntBox(it.value) },
        ),
        PolynomialPolynomialTestData( // Spoiler: 5 * 7 = 0
            mapOf(
                mapOf(x to 1u) to 5,
                mapOf(y to 1u) to -25,
                mapOf(z to 1u) to 10,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf(x to 1u) to 21,
                mapOf(y to 1u) to 14,
                mapOf(z to 1u) to -7,
            ).mapValues { IntBox(it.value) },
            mapOf(
                mapOf(x to 2u) to 0,
                mapOf(y to 2u) to 0,
                mapOf(z to 2u) to 0,
                mapOf(x to 1u, y to 1u) to 0,
                mapOf(y to 1u, z to 1u) to 0,
                mapOf(x to 1u, z to 1u) to 0,
            ).mapValues { IntBox(it.value) },
        ),
    )

    init {
        "Polynomial ring" - {
            "unaryMinus" - {
                rationalPolynomialSpace {
                    "test 1" - {
                        -LabeledPolynomialAsIs(
                            mapOf(x to 5u) to Rational(5, 9),
                            mapOf<Symbol, UInt>() to Rational(-8, 9),
                            mapOf(iota to 13u) to Rational(-8, 7),
                        ) shouldBe
                                LabeledPolynomialAsIs(
                                    mapOf(x to 5u) to Rational(-5, 9),
                                    mapOf<Symbol, UInt>() to Rational(8, 9),
                                    mapOf(iota to 13u) to Rational(8, 7),
                                )
                    }
                    "test 2" - {
                        -LabeledPolynomialAsIs(
                            mapOf(x to 5u) to Rational(5, 9),
                            mapOf<Symbol, UInt>() to Rational(-8, 9),
                            mapOf(iota to 13u) to Rational(-8, 7),
                            mapOf(y to 4u) to Rational(0),
                            mapOf(x to 5u) to Rational(0),
                        ) shouldBe
                                LabeledPolynomialAsIs(
                                    mapOf(x to 5u) to Rational(-5, 9),
                                    mapOf<Symbol, UInt>() to Rational(8, 9),
                                    mapOf(iota to 13u) to Rational(8, 7),
                                    mapOf(y to 4u) to Rational(0),
                                    mapOf(x to 5u) to Rational(0),
                                )
                    }
                }
            }
            "plus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { LabeledPolynomialAsIs(first) + LabeledPolynomialAsIs(second) } shouldBe LabeledPolynomialAsIs(result)
                }
            }
            "minus" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    plusPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    rationalPolynomialSpace { LabeledPolynomialAsIs(first) - LabeledPolynomialAsIs(second.mapValues { ring { -it.value } }) } shouldBe LabeledPolynomialAsIs(result)
                }
            }
            "times" - {
                withData(
                    nameIndFn = { index, _ -> "test ${index + 1}" },
                    timesPolynomialPolynomialTestData
                ) { (first, second, result) ->
                    intModuloPolynomialSpace { LabeledPolynomialAsIs(first) * LabeledPolynomialAsIs(second) } shouldBe LabeledPolynomialAsIs(result)
                }
            }
        }
    }

    data class PolynomialPropertiesTestData<C>(
        val argumentsCoefficients: Map<Map<Symbol, UInt>, C>,
        val degree: Int,
        val degrees: Map<Symbol, UInt>,
    )

    val polynomialPropertiesTestData = listOf(
        PolynomialPropertiesTestData(
            mapOf(),
            -1,
            mapOf(),
        ),
        PolynomialPropertiesTestData(
            mapOf(
                mapOf<Symbol, UInt>() to o,
            ),
            0,
            mapOf(),
        ),
        PolynomialPropertiesTestData(
            mapOf(
                mapOf(x to 1u, y to 2u, z to 3u) to o,
            ),
            6,
            mapOf(x to 1u, y to 2u, z to 3u),
        ),
        PolynomialPropertiesTestData(
            mapOf(
                mapOf<Symbol, UInt>() to o,
                mapOf(y to 1u) to o,
                mapOf(x to 2u, z to 1u) to o,
            ),
            3,
            mapOf(x to 2u, y to 1u, z to 1u),
        ),
        PolynomialPropertiesTestData(
            mapOf(
                mapOf<Symbol, UInt>() to o,
                mapOf(x to 1u, y to 2u) to o,
                mapOf(y to 1u, z to 2u) to o,
                mapOf(x to 2u, z to 1u) to o,
                mapOf(t to 4u) to o,
            ),
            4,
            mapOf(x to 2u, y to 2u, z to 2u, t to 4u),
        ),
    )

    init {
        rationalPolynomialSpace {
            "Polynomial properties" - {
                "degree" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).degree shouldBe it.degree
                    }
                }
                "degrees" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).degrees shouldBe it.degrees
                    }
                }
                "degreeBy Variable" - {
                    fun LabeledPolynomial<Rational>.collectDegrees(
                        variables: Set<Symbol> = this.variables + iota
                    ): Map<Symbol, UInt> = variables.associateWith { degreeBy(it) }

                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).collectDegrees() shouldBe (it.degrees + (iota to 0u))
                    }
                }
                "degreeBy Collection" - {
                    fun LabeledPolynomial<Rational>.checkDegreeBy() {
                        val variables = variables.toList() + iota
                        val variablesCollectionSequence: Sequence<List<Symbol>> = sequence {
                            val appearances = MutableList(variables.size) { 0 }
                            while (true) {
                                yield(
                                    buildList {
                                        for ((variableIndex, count) in appearances.withIndex()) repeat(count) { add(variables[variableIndex]) }
                                    }
                                )
                                val indexChange = appearances.indexOfFirst { it < 4 }
                                if (indexChange == -1) break
                                appearances[indexChange] += 1
                                for (index in 0 until indexChange) appearances[index] = 0
                            }
                        }
                        for (variablesCollection in variablesCollectionSequence) {
                            val expected = coefficients.keys.maxOfOrNull { degs -> degs.filterKeys { it in variablesCollection }.values.sum() } ?: 0u
                            val actual = degreeBy(variablesCollection)
                            if (actual != expected)
                                error("Incorrect answer for variable collection $variablesCollection: expected $expected, actual $actual")
                        }
                    }

                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).checkDegreeBy()
                    }
                }
                "variables" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).variables shouldBe it.degrees.keys
                    }
                }
                "countOfVariables" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}" },
                        polynomialPropertiesTestData
                    ) {
                        LabeledPolynomialAsIs(it.argumentsCoefficients).countOfVariables shouldBe it.degrees.size
                    }
                }
            }
        }
    }

    data class RFPropertiesTestData<C>(
        val argumentNumeratorCoefficients: Map<Map<Symbol, UInt>, C>,
        val argumentDenominatorCoefficients: Map<Map<Symbol, UInt>, C>? = null,
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
                mapOf<Symbol, UInt>() to o,
            ),
            countOfVariables = 0
        ),
        RFPropertiesTestData(
            mapOf(
                mapOf(y to 1u, t to 1u) to o,
            ),
            mapOf(
                mapOf(z to 2u) to o
            ),
            countOfVariables = 3
        ),
        RFPropertiesTestData(
            mapOf(
                mapOf<Symbol, UInt>() to o,
                mapOf(y to 1u) to o,
                mapOf(x to 2u, z to 1u) to o,
            ),
            countOfVariables = 3
        ),
        RFPropertiesTestData(
            mapOf(
                mapOf<Symbol, UInt>() to o,
                mapOf(x to 1u, y to 2u) to o,
                mapOf(x to 2u, z to 1u) to o,
            ),
            mapOf(
                mapOf(y to 1u, z to 2u) to o,
                mapOf(t to 4u) to o,
            ),
            countOfVariables = 4
        ),
    )

    init {
        Rational.field.labeledRationalFunctionSpace {
            "Rational Functional properties" - {
                "countOfVariables" - {
                    withData(
                        nameIndFn = { index, _ -> "test ${index + 1}"},
                        rfPropertiesTestData
                    ) {
                        val rationalFunction =
                            if (it.argumentDenominatorCoefficients === null)
                                LabeledRationalFunction(it.argumentNumeratorCoefficients)
                            else
                                LabeledRationalFunction(it.argumentNumeratorCoefficients, it.argumentDenominatorCoefficients)
                        rationalFunction.countOfVariables shouldBe it.countOfVariables
                    }
                }
            }
        }
    }
}
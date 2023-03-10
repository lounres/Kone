/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Rational
import com.lounres.kone.algebraic.field
import com.lounres.kone.algebraic.invoke
import com.lounres.kone.annotations.UnstableKoneAPI
import com.lounres.kone.kotest.datatest.withData
import com.lounres.kone.polynomial.testUtils.plusOrMinus
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe


@Suppress("NAME_SHADOWING")
class NumberedUtilTest: FreeSpec({
    timeout = 5000
    "substitute" - {
        "polynomial <- double" - {
            data class PSubstituteDTestData(
                val receiverCoefs: NumberedPolynomialCoefficients<Double>,
                val args: Map<Int, Double>,
                val resultCoefs: NumberedPolynomialCoefficients<Double>
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteDTestData(
                    mapOf(
                        listOf<UInt>() to 1.0,
                        listOf(1u) to -2.0,
                        listOf(2u) to 1.0,
                    ),
                    mapOf(
                        0 to 1.0,
                    ),
                    mapOf(
                        emptyList<UInt>() to 0.0,
                    )
                ),
                PSubstituteDTestData(
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(1u) to 0.22997637465889875,
                        listOf(2u) to 0.32675302591924016,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(1u, 1u) to 0.5304946210170756,
                        listOf(2u, 1u) to 0.6244313712888998,
                        listOf(0u, 2u) to 0.2700930201481795,
                        listOf(1u, 2u) to -0.06962351375204712,
                        listOf(2u, 2u) to -0.015206988092131501,
                    ),
                    mapOf(),
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(1u) to 0.22997637465889875,
                        listOf(2u) to 0.32675302591924016,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(1u, 1u) to 0.5304946210170756,
                        listOf(2u, 1u) to 0.6244313712888998,
                        listOf(0u, 2u) to 0.2700930201481795,
                        listOf(1u, 2u) to -0.06962351375204712,
                        listOf(2u, 2u) to -0.015206988092131501,
                    )
                ),
                PSubstituteDTestData(
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(1u) to 0.22997637465889875,
                        listOf(2u) to 0.32675302591924016,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(1u, 1u) to 0.5304946210170756,
                        listOf(2u, 1u) to 0.6244313712888998,
                        listOf(0u, 2u) to 0.2700930201481795,
                        listOf(1u, 2u) to -0.06962351375204712,
                        listOf(2u, 2u) to -0.015206988092131501,
                    ),
                    mapOf(
                        0 to 0.0,
                    ),
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(0u, 2u) to 0.2700930201481795,
                    )
                ),
                PSubstituteDTestData(
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(1u) to 0.22997637465889875,
                        listOf(2u) to 0.32675302591924016,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(1u, 1u) to 0.5304946210170756,
                        listOf(2u, 1u) to 0.6244313712888998,
                        listOf(0u, 2u) to 0.2700930201481795,
                        listOf(1u, 2u) to -0.06962351375204712,
                        listOf(2u, 2u) to -0.015206988092131501,
                    ),
                    mapOf(
                        1 to 0.8400458576651112,
                    ),
                    mapOf(
                        listOf<UInt>() to 1.433510890645169,
                        listOf(1u) to 0.6264844682514724,
                        listOf(2u) to 0.8405727903771333,
                    )
                ),
                PSubstituteDTestData(
                    mapOf(
                        listOf<UInt>() to 0.8597048543814783,
                        listOf(1u) to 0.22997637465889875,
                        listOf(2u) to 0.32675302591924016,
                        listOf(0u, 1u) to 0.4561746111587508,
                        listOf(1u, 1u) to 0.5304946210170756,
                        listOf(2u, 1u) to 0.6244313712888998,
                        listOf(0u, 2u) to 0.2700930201481795,
                        listOf(1u, 2u) to -0.06962351375204712,
                        listOf(2u, 2u) to -0.015206988092131501,
                    ),
                    mapOf(
                        0 to 0.4846192734143442,
                        1 to 0.8400458576651112,
                    ),
                    mapOf(
                        listOf<UInt>() to 1.934530767358133,
                    )
                ),
            ) { (coefs, args, resultCoefs) ->
                Double.field {
                    val coefs = NumberedPolynomial(coefs)
                    val extra = 5 to 0.9211194782050933
                    val result = NumberedPolynomial(resultCoefs) plusOrMinus 0.000001
                    
                    withClue("without extra argument") {
                        coefs.substitute(args) shouldBe result
                    }
                    withClue("with extra argument") {
                        coefs.substitute(args + extra) shouldBe result
                    }
                }
            }
        }
        "polynomial <- constant" - {
            data class PSubstituteCTestData(
                val receiverCoefs: NumberedPolynomialCoefficients<Rational>,
                val arg: Map<Int, Rational>,
                val result: NumberedPolynomialCoefficients<Rational>
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteCTestData(
                    mapOf(
                        listOf<UInt>() to Rational(1),
                        listOf(1u) to Rational(-2),
                        listOf(2u) to Rational(1)
                    ),
                    mapOf(
                        0 to Rational(1)
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(0)
                    ),
                ),
                PSubstituteCTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(),
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                ),
                PSubstituteCTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to Rational(-2, 5),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(-83, 50),
                        listOf(0u, 1u) to Rational(29, 25),
                        listOf(0u, 2u) to Rational(3, 5),
                    ),
                ),
                PSubstituteCTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        1 to Rational(12, 9),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(-67, 18),
                        listOf(1u) to Rational(-70, 9),
                        listOf(2u) to Rational(88, 9),
                    ),
                ),
                PSubstituteCTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to Rational(-2, 5),
                        1 to Rational(12, 9),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(143, 150)
                    ),
                ),
                PSubstituteCTestData(
                    mapOf(
                        listOf(8u) to Rational(-3, 2),
                        listOf(7u, 1u) to Rational(8, 6),
                        listOf(6u, 2u) to Rational(14, 6),
                        listOf(5u, 3u) to Rational(-3, 1),
                        listOf(4u, 4u) to Rational(-19, 2),
                        listOf(3u, 5u) to Rational(9, 4),
                        listOf(2u, 6u) to Rational(5, 5),
                        listOf(1u, 7u) to Rational(18, 9),
                        listOf(0u, 8u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to Rational(-2, 5),
                        1 to Rational(12, 9),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(47639065216, 2562890625)
                    ),
                ),
            ) { (coefs, arg, result) ->
                Rational.field {
                    val coefs = NumberedPolynomial(coefs)
                    val extra = 5 to Rational(57, 179)
                    val result = NumberedPolynomial(result)
                    
                    withClue("without extra argument") {
                        coefs.substitute(this, arg) shouldBe result
                    }
                    withClue("with extra argument") {
                        coefs.substitute(this, arg + extra) shouldBe result
                    }
                }
            }
        }
        "polynomial <- polynomial" - {
            data class PSubstitutePTestData(
                val receiverCoefs: NumberedPolynomialCoefficients<Rational>,
                val argCoefs: Map<Int, NumberedPolynomialCoefficients<Rational>>,
                val resultCoefs: NumberedPolynomialCoefficients<Rational>
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(1),
                        listOf(1u) to Rational(-2),
                        listOf(2u) to Rational(1)
                    ),
                    mapOf(
                        0 to mapOf(
                            listOf<UInt>() to Rational(1)
                        ),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(0)
                    ),
                ),
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to mapOf(
                            listOf(1u) to Rational(-5, 1),
                            listOf(0u, 1u) to Rational(2, 8),
                        ),
                        1 to mapOf(
                            listOf(1u) to Rational(0, 5),
                            listOf(0u, 1u) to Rational(11, 7),
                        ),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(0u, 1u) to Rational(-92, 21),
                        listOf(0u, 2u) to Rational(-2627, 2352),
                        listOf(0u, 3u) to Rational(4565, 3136),
                        listOf(0u, 4u) to Rational(605, 1568),
                        listOf(1u) to Rational(-20, 3),
                        listOf(1u, 1u) to Rational(1445, 21),
                        listOf(1u, 2u) to Rational(-13145, 392),
                        listOf(1u, 3u) to Rational(-3025, 196),
                        listOf(2u) to Rational(175, 3),
                        listOf(2u, 1u) to Rational(2475, 28),
                        listOf(2u, 2u) to Rational(15125, 98),
                        listOf(3u) to Rational(0),
                        listOf(3u, 1u) to Rational(0),
                        listOf(4u) to Rational(0),
                    ),
                ),
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(),
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                ),
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to mapOf(
                            listOf<UInt>() to Rational(0, 6),
                            listOf(1u) to Rational(14, 8),
                            listOf(2u) to Rational(-14, 2),
                            listOf(0u, 1u) to Rational(-3, 5),
                            listOf(1u, 1u) to Rational(11, 1),
                            listOf(2u, 1u) to Rational(3, 7),
                            listOf(0u, 2u) to Rational(-3, 7),
                            listOf(1u, 2u) to Rational(-18, 5),
                            listOf(2u, 2u) to Rational(-9, 1),
                        ),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(7, 3),
                        listOf(2u) to Rational(-35, 16),
                        listOf(3u) to Rational(-343, 6),
                        listOf(4u) to Rational(343, 3),
                        listOf(0u, 1u) to Rational(-19, 5),
                        listOf(1u, 1u) to Rational(-823, 120),
                        listOf(2u, 1u) to Rational(1232417, 6720),
                        listOf(3u, 1u) to Rational(-9863, 24),
                        listOf(4u, 1u) to Rational(385, 4),
                        listOf(0u, 2u) to Rational(2439, 350),
                        listOf(1u, 2u) to Rational(-5793, 40),
                        listOf(2u, 2u) to Rational(1172113, 3360),
                        listOf(3u, 2u) to Rational(-13531, 40),
                        listOf(4u, 2u) to Rational(2824, 7),
                        listOf(0u, 3u) to Rational(3417, 700),
                        listOf(1u, 3u) to Rational(1191, 200),
                        listOf(2u, 3u) to Rational(8383, 28),
                        listOf(3u, 3u) to Rational(-220279, 280),
                        listOf(4u, 3u) to Rational(49179, 196),
                        listOf(0u, 4u) to Rational(57, 35),
                        listOf(1u, 4u) to Rational(-33771, 700),
                        listOf(2u, 4u) to Rational(196279, 1225),
                        listOf(3u, 4u) to Rational(-32259, 140),
                        listOf(4u, 4u) to Rational(23868, 49),
                        listOf(0u, 5u) to Rational(333, 196),
                        listOf(1u, 5u) to Rational(-204, 35),
                        listOf(2u, 5u) to Rational(-307233, 2450),
                        listOf(3u, 5u) to Rational(-12492, 35),
                        listOf(4u, 5u) to Rational(4563, 28),
                        listOf(0u, 6u) to Rational(45, 98),
                        listOf(1u, 6u) to Rational(54, 7),
                        listOf(2u, 6u) to Rational(1809, 35),
                        listOf(3u, 6u) to Rational(162),
                        listOf(4u, 6u) to Rational(405, 2),
                    ),
                ),
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        1 to mapOf(
                            listOf<UInt>() to Rational(-9, 2),
                            listOf(1u) to Rational(2, 7),
                            listOf(2u) to Rational(9, 1),
                            listOf(0u, 1u) to Rational(13, 1),
                            listOf(1u, 1u) to Rational(-1, 8),
                            listOf(2u, 1u) to Rational(2, 8),
                            listOf(0u, 2u) to Rational(19, 4),
                            listOf(1u, 2u) to Rational(15, 7),
                            listOf(2u, 2u) to Rational(-19, 4),
                        ),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(129, 4),
                        listOf(1u) to Rational(6817, 84),
                        listOf(2u) to Rational(-21445, 294),
                        listOf(3u) to Rational(-12151, 49),
                        listOf(4u) to Rational(-17789, 196),
                        listOf(5u) to Rational(1224, 7),
                        listOf(6u) to Rational(405, 2),
                        listOf(0u, 1u) to Rational(-156),
                        listOf(1u, 1u) to Rational(-2440, 7),
                        listOf(2u, 1u) to Rational(-1571, 112),
                        listOf(3u, 1u) to Rational(107515, 224),
                        listOf(4u, 1u) to Rational(64965, 112),
                        listOf(5u, 1u) to Rational(209, 56),
                        listOf(6u, 1u) to Rational(45, 4),
                        listOf(0u, 2u) to Rational(112),
                        listOf(1u, 2u) to Rational(1449, 8),
                        listOf(2u, 2u) to Rational(1306309, 3136),
                        listOf(3u, 2u) to Rational(483207, 1568),
                        listOf(4u, 2u) to Rational(1978437, 6272),
                        listOf(5u, 2u) to Rational(-18231, 224),
                        listOf(6u, 2u) to Rational(-6835, 32),
                        listOf(0u, 3u) to Rational(247, 2),
                        listOf(1u, 3u) to Rational(33771, 112),
                        listOf(2u, 3u) to Rational(2073, 7),
                        listOf(3u, 3u) to Rational(-23463, 224),
                        listOf(4u, 3u) to Rational(-33825, 112),
                        listOf(5u, 3u) to Rational(201, 224),
                        listOf(6u, 3u) to Rational(-95, 16),
                        listOf(0u, 4u) to Rational(361, 16),
                        listOf(1u, 4u) to Rational(3667, 56),
                        listOf(2u, 4u) to Rational(88729, 1568),
                        listOf(3u, 4u) to Rational(-2476, 49),
                        listOf(4u, 4u) to Rational(-23419, 196),
                        listOf(5u, 4u) to Rational(-323, 56),
                        listOf(6u, 4u) to Rational(1805, 32),
                    ),
                ),
                PSubstitutePTestData(
                    mapOf(
                        listOf<UInt>() to Rational(-3, 2),
                        listOf(1u) to Rational(8, 6),
                        listOf(2u) to Rational(14, 6),
                        listOf(0u, 1u) to Rational(-3, 1),
                        listOf(1u, 1u) to Rational(-19, 2),
                        listOf(2u, 1u) to Rational(9, 4),
                        listOf(0u, 2u) to Rational(5, 5),
                        listOf(1u, 2u) to Rational(18, 9),
                        listOf(2u, 2u) to Rational(5, 2),
                    ),
                    mapOf(
                        0 to mapOf(
                            listOf<UInt>() to Rational(0, 6),
                            listOf(1u) to Rational(14, 8),
                            listOf(2u) to Rational(-14, 2),
                            listOf(0u, 1u) to Rational(-3, 5),
                            listOf(1u, 1u) to Rational(11, 1),
                            listOf(2u, 1u) to Rational(3, 7),
                            listOf(0u, 2u) to Rational(-3, 7),
                            listOf(1u, 2u) to Rational(-18, 5),
                            listOf(2u, 2u) to Rational(-9, 1),
                        ),
                        1 to mapOf(
                            listOf<UInt>() to Rational(-9, 2),
                            listOf(1u) to Rational(2, 7),
                            listOf(2u) to Rational(9, 1),
                            listOf(0u, 1u) to Rational(13, 1),
                            listOf(1u, 1u) to Rational(-1, 8),
                            listOf(2u, 1u) to Rational(2, 8),
                            listOf(0u, 2u) to Rational(19, 4),
                            listOf(1u, 2u) to Rational(15, 7),
                            listOf(2u, 2u) to Rational(-19, 4),
                        ),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(129, 4),
                        listOf(1u) to Rational(48583, 336),
                        listOf(2u) to Rational(-913477, 1568),
                        listOf(3u) to Rational(-967567, 672),
                        listOf(4u) to Rational(4722043, 1344),
                        listOf(5u) to Rational(8855, 2),
                        listOf(6u) to Rational(-311971, 32),
                        listOf(7u) to Rational(-17325, 4),
                        listOf(8u) to Rational(19845, 2),
                        listOf(0u, 1u) to Rational(-827, 4),
                        listOf(1u, 1u) to Rational(191927, 840),
                        listOf(2u, 1u) to Rational(9592627, 2352),
                        listOf(3u, 1u) to Rational(-105400711, 53760),
                        listOf(4u, 1u) to Rational(-10054101459, 439040),
                        listOf(5u, 1u) to Rational(2127351, 128),
                        listOf(6u, 1u) to Rational(116680973, 3136),
                        listOf(7u, 1u) to Rational(-220445, 7),
                        listOf(8u, 1u) to Rational(-2655, 4),
                        listOf(0u, 2u) to Rational(30567, 100),
                        listOf(1u, 2u) to Rational(-156284953, 39200),
                        listOf(2u, 2u) to Rational(-57661541711, 6585600),
                        listOf(3u, 2u) to Rational(131931579, 3136),
                        listOf(4u, 2u) to Rational(98818124791, 3512320),
                        listOf(5u, 2u) to Rational(-94458855053, 878080),
                        listOf(6u, 2u) to Rational(13937705305, 1229312),
                        listOf(7u, 2u) to Rational(335706887, 21952),
                        listOf(8u, 2u) to Rational(23549165, 1568),
                        listOf(0u, 3u) to Rational(111367, 1400),
                        listOf(1u, 3u) to Rational(4937369, 700),
                        listOf(2u, 3u) to Rational(-4449423711, 274400),
                        listOf(3u, 3u) to Rational(-351873325703, 4390400),
                        listOf(4u, 3u) to Rational(23495875029, 307328),
                        listOf(5u, 3u) to Rational(17576300919, 878080),
                        listOf(6u, 3u) to Rational(230316993, 12544),
                        listOf(7u, 3u) to Rational(-191130515, 21952),
                        listOf(8u, 3u) to Rational(332435, 392),
                        listOf(0u, 4u) to Rational(-275084, 1225),
                        listOf(1u, 4u) to Rational(-266774603, 137200),
                        listOf(2u, 4u) to Rational(2176279167121, 30732800),
                        listOf(3u, 4u) to Rational(10904913303, 2195200),
                        listOf(4u, 4u) to Rational(-10769286147, 2195200),
                        listOf(5u, 4u) to Rational(-26277119793, 439040),
                        listOf(6u, 4u) to Rational(25859735869, 6146560),
                        listOf(7u, 4u) to Rational(38906289, 2744),
                        listOf(8u, 4u) to Rational(-3072025, 392),
                        listOf(0u, 5u) to Rational(9573, 98),
                        listOf(1u, 5u) to Rational(-4154651399, 548800),
                        listOf(2u, 5u) to Rational(3446069019, 548800),
                        listOf(3u, 5u) to Rational(-7851500623, 137200),
                        listOf(4u, 5u) to Rational(-53205142903, 1920800),
                        listOf(5u, 5u) to Rational(-31953611, 3430),
                        listOf(6u, 5u) to Rational(1447380313, 109760),
                        listOf(7u, 5u) to Rational(764158625, 21952),
                        listOf(8u, 5u) to Rational(1153515, 784),
                        listOf(0u, 6u) to Rational(1722351, 7840),
                        listOf(1u, 6u) to Rational(-164554821, 109760),
                        listOf(2u, 6u) to Rational(-79096147243, 7683200),
                        listOf(3u, 6u) to Rational(-624721089, 15680),
                        listOf(4u, 6u) to Rational(11147305567, 548800),
                        listOf(5u, 6u) to Rational(8318333679, 109760),
                        listOf(6u, 6u) to Rational(32981871553, 1536640),
                        listOf(7u, 6u) to Rational(-225359619, 21952),
                        listOf(8u, 6u) to Rational(-3973995, 392),
                        listOf(0u, 7u) to Rational(67203, 784),
                        listOf(1u, 7u) to Rational(39281469, 54880),
                        listOf(2u, 7u) to Rational(70162551, 27440),
                        listOf(3u, 7u) to Rational(413630709, 54880),
                        listOf(4u, 7u) to Rational(4640410269, 192080),
                        listOf(5u, 7u) to Rational(802712247, 54880),
                        listOf(6u, 7u) to Rational(-473517603, 27440),
                        listOf(7u, 7u) to Rational(-17055459, 1568),
                        listOf(8u, 7u) to Rational(-12825, 14),
                        listOf(0u, 8u) to Rational(16245, 1568),
                        listOf(1u, 8u) to Rational(503253, 2744),
                        listOf(2u, 8u) to Rational(125292591, 96040),
                        listOf(3u, 8u) to Rational(12033171, 2744),
                        listOf(4u, 8u) to Rational(154352673, 27440),
                        listOf(5u, 8u) to Rational(-1302291, 392),
                        listOf(6u, 8u) to Rational(-20265741, 1960),
                        listOf(7u, 8u) to Rational(-26163, 56),
                        listOf(8u, 8u) to Rational(146205, 32),
                    ),
                ),
            ) { (coefs, arg, result) ->
                Rational.field {
                    val coefs = NumberedPolynomial(coefs)
                    val arg = arg.mapValues { NumberedPolynomial(it.value) }
                    val extra = 5 to NumberedPolynomial(
                        listOf<UInt>() to Rational(-11, 3),
                        listOf(1u) to Rational(5, 2),
                        listOf(2u) to Rational(13, 7),
                        listOf(0u, 1u) to Rational(16, 9),
                        listOf(1u, 1u) to Rational(14, 7),
                        listOf(2u, 1u) to Rational(6, 1),
                        listOf(0u, 2u) to Rational(-14, 3),
                        listOf(1u, 2u) to Rational(-2, 7),
                        listOf(2u, 2u) to Rational(-10, 8),
                    )
                    val result = NumberedPolynomial(result)

                    withClue("without extra argument") {
                        coefs.substitute(this, arg) shouldBe result
                    }
                    withClue("with extra argument") {
                        coefs.substitute(this, (arg + extra)) shouldBe result
                    }
                }
            }
        }
        // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
        //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
        //  not r^(deg(p)(deg(p)+1)/2) as it is now.
        "!polynomial <- rational function" - {
            data class PSubstituteRFTestData(
                val receiverCoefs: NumberedPolynomialCoefficients<Rational>,
                val argCoefs: Map<Int, Pair<NumberedPolynomialCoefficients<Rational>, NumberedPolynomialCoefficients<Rational>>>,
                val resultNumCoefs: NumberedPolynomialCoefficients<Rational>,
                val resultDenCoefs: NumberedPolynomialCoefficients<Rational>,
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(1),
						listOf(1u) to Rational(-2),
						listOf(2u) to Rational(1)
                    ),
                    mapOf(
						0 to Pair(
							mapOf(
								listOf<UInt>() to Rational(1)
							),
							mapOf(
								listOf<UInt>() to Rational(1)
							)
						),
                    ),
                    mapOf(
                        listOf<UInt>() to Rational(0)
                    ),
                    mapOf(
						listOf<UInt>() to Rational(1)
                    ),
                ),
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(
						0 to Pair(
							mapOf(
								listOf(1u) to Rational(17, 7),
								listOf(0u, 1u) to Rational(-13, 1),
							),
							mapOf(
								listOf(1u) to Rational(-18, 6),
								listOf(0u, 1u) to Rational(11, 6),
							)
						),
						1 to Pair(
							mapOf(
								listOf(1u) to Rational(18, 5),
								listOf(0u, 1u) to Rational(-16, 3),
							),
							mapOf(
								listOf(1u) to Rational(-1, 1),
								listOf(0u, 1u) to Rational(-4, 1),
							)
						),
                    ),
                    mapOf(
						listOf(4u) to Rational(-194071, 4900),
						listOf(3u, 1u) to Rational(394811, 225),
						listOf(2u, 2u) to Rational(-444183161, 66150),
						listOf(1u, 3u) to Rational(70537618, 59535),
						listOf(0u, 4u) to Rational(9655504, 2835),
                    ),
                    mapOf(
						listOf(4u) to Rational(9, 1),
						listOf(3u, 1u) to Rational(61, 1),
						listOf(2u, 2u) to Rational(2137, 36),
						listOf(1u, 3u) to Rational(-1342, 9),
						listOf(0u, 4u) to Rational(484, 9),
                    ),
                ),
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(),
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(
						listOf<UInt>() to Rational(0, 1)
                    ),
                ),
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(
						0 to Pair(
							mapOf(
								listOf<UInt>() to Rational(17, 5),
								listOf(1u) to Rational(11, 6),
								listOf(2u) to Rational(14, 3),
								listOf(0u, 1u) to Rational(17, 1),
								listOf(1u, 1u) to Rational(12, 3),
								listOf(2u, 1u) to Rational(-6, 2),
								listOf(0u, 2u) to Rational(17, 1),
								listOf(1u, 2u) to Rational(-4, 3),
								listOf(2u, 2u) to Rational(2, 6),
							),
							mapOf(
								listOf<UInt>() to Rational(3, 5),
								listOf(1u) to Rational(3, 5),
								listOf(2u) to Rational(3, 7),
								listOf(0u, 1u) to Rational(-3, 8),
								listOf(1u, 1u) to Rational(-1, 1),
								listOf(2u, 1u) to Rational(17, 9),
								listOf(0u, 2u) to Rational(-8, 1),
								listOf(1u, 2u) to Rational(6, 4),
								listOf(2u, 2u) to Rational(10, 9),
							)
						),
                    ),
                    mapOf(
						listOf<UInt>() to Rational(-66677, 3500),
						listOf(1u) to Rational(-206281, 10500),
						listOf(2u) to Rational(-412567, 7056),
						listOf(3u) to Rational(-310081, 11025),
						listOf(4u) to Rational(-575996, 15435),
						listOf(0u, 1u) to Rational(-573701, 4200),
						listOf(1u, 1u) to Rational(-2239001, 25200),
						listOf(2u, 1u) to Rational(-8817889, 132300),
						listOf(3u, 1u) to Rational(2317919, 44100),
						listOf(4u, 1u) to Rational(1169471, 6615),
						listOf(0u, 2u) to Rational(-4057819, 33600),
						listOf(1u, 2u) to Rational(1373311, 12600),
						listOf(2u, 2u) to Rational(32433493, 52920),
						listOf(3u, 2u) to Rational(4998053, 33075),
						listOf(4u, 2u) to Rational(-2147779, 8820),
						listOf(0u, 3u) to Rational(2018481, 2240),
						listOf(1u, 3u) to Rational(941713, 1440),
						listOf(2u, 3u) to Rational(183749, 6615),
						listOf(3u, 3u) to Rational(-4631023, 15876),
						listOf(4u, 3u) to Rational(25609336, 178605),
						listOf(0u, 4u) to Rational(11886431, 6720),
						listOf(1u, 4u) to Rational(18433, 504),
						listOf(2u, 4u) to Rational(-39613331, 45360),
						listOf(3u, 4u) to Rational(681619, 5670),
						listOf(4u, 4u) to Rational(-864841, 20412),
						listOf(0u, 5u) to Rational(343535, 1008),
						listOf(1u, 5u) to Rational(-33583, 72),
						listOf(2u, 5u) to Rational(1194625, 9072),
						listOf(3u, 5u) to Rational(-62917, 2268),
						listOf(4u, 5u) to Rational(157645, 10206),
						listOf(0u, 6u) to Rational(-1381, 3),
						listOf(1u, 6u) to Rational(919, 36),
						listOf(2u, 6u) to Rational(-3053, 36),
						listOf(3u, 6u) to Rational(2125, 324),
						listOf(4u, 6u) to Rational(-236, 243)
                    ),
                    mapOf(
						listOf<UInt>() to Rational(0, 1),
						listOf<UInt>() to Rational(1, 4),
						listOf(1u) to Rational(-5, 3),
						listOf(2u) to Rational(35, 9),
						listOf(3u) to Rational(-100, 27),
						listOf(4u) to Rational(100, 81),
						listOf(0u, 1u) to Rational(-5, 3),
						listOf(1u, 1u) to Rational(14, 9),
						listOf(2u, 1u) to Rational(1874, 189),
						listOf(3u, 1u) to Rational(-620, 63),
						listOf(4u, 1u) to Rational(40, 63),
						listOf(0u, 2u) to Rational(16, 9),
						listOf(1u, 2u) to Rational(365, 21),
						listOf(2u, 2u) to Rational(112, 9),
						listOf(3u, 2u) to Rational(-464, 63),
						listOf(4u, 2u) to Rational(1996, 441),
						listOf(0u, 3u) to Rational(10, 3),
						listOf(1u, 3u) to Rational(118, 21),
						listOf(2u, 3u) to Rational(-272, 21),
						listOf(3u, 3u) to Rational(-764, 49),
						listOf(4u, 3u) to Rational(8, 7),
						listOf(0u, 4u) to Rational(1, 1),
						listOf(1u, 4u) to Rational(-10, 7),
						listOf(2u, 4u) to Rational(-171, 49),
						listOf(3u, 4u) to Rational(20, 7),
						listOf(4u, 4u) to Rational(4, 1)
                    ),
                ),
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(
						1 to Pair(
							mapOf(
								listOf<UInt>() to Rational(18, 5),
								listOf(1u) to Rational(-17, 5),
								listOf(2u) to Rational(-2, 7),
								listOf(0u, 1u) to Rational(6, 5),
								listOf(1u, 1u) to Rational(-5, 1),
								listOf(2u, 1u) to Rational(-9, 1),
								listOf(0u, 2u) to Rational(-8, 8),
								listOf(1u, 2u) to Rational(2, 7),
								listOf(2u, 2u) to Rational(-13, 7),
							),
							mapOf(
								listOf<UInt>() to Rational(-4, 8),
								listOf(1u) to Rational(15, 9),
								listOf(2u) to Rational(-10, 9),
								listOf(0u, 1u) to Rational(5, 3),
								listOf(1u, 1u) to Rational(4, 1),
								listOf(2u, 1u) to Rational(-2, 7),
								listOf(0u, 2u) to Rational(2, 2),
								listOf(1u, 2u) to Rational(-5, 7),
								listOf(2u, 2u) to Rational(-18, 9),
							)
						),
                    ),
                    mapOf(
						listOf<UInt>() to Rational(3539, 700),
						listOf(1u) to Rational(-307079, 6300),
						listOf(2u) to Rational(451609, 15120),
						listOf(3u) to Rational(35287733, 396900),
						listOf(4u) to Rational(-37242617, 396900),
						listOf(5u) to Rational(382747, 19845),
						listOf(6u) to Rational(-2407, 3969),
						listOf(0u, 1u) to Rational(-226, 175),
						listOf(1u, 1u) to Rational(-74113, 1890),
						listOf(2u, 1u) to Rational(250931, 1764),
						listOf(3u, 1u) to Rational(30071473, 99225),
						listOf(4u, 1u) to Rational(-286466, 1323),
						listOf(5u, 1u) to Rational(-2285282, 9261),
						listOf(6u, 1u) to Rational(17900, 441),
						listOf(0u, 2u) to Rational(3817, 3150),
						listOf(1u, 2u) to Rational(577568, 11025),
						listOf(2u, 2u) to Rational(9073553, 99225),
						listOf(3u, 2u) to Rational(-1415849, 79380),
						listOf(4u, 2u) to Rational(-124715629, 277830),
						listOf(5u, 2u) to Rational(-1328953, 1890),
						listOf(6u, 2u) to Rational(-297148, 1323),
						listOf(0u, 3u) to Rational(6043, 945),
						listOf(1u, 3u) to Rational(160381, 6615),
						listOf(2u, 3u) to Rational(-673249, 13230),
						listOf(3u, 3u) to Rational(-319255, 2058),
						listOf(4u, 3u) to Rational(-98144, 1029),
						listOf(5u, 3u) to Rational(-320239, 5145),
						listOf(6u, 3u) to Rational(400, 147),
						listOf(0u, 4u) to Rational(163, 63),
						listOf(1u, 4u) to Rational(-25183, 4410),
						listOf(2u, 4u) to Rational(-21369, 1372),
						listOf(3u, 4u) to Rational(127499, 30870),
						listOf(4u, 4u) to Rational(86971, 12348),
						listOf(5u, 4u) to Rational(-11129, 1470),
						listOf(6u, 4u) to Rational(544, 147)
                    ),
                    mapOf(
							listOf<UInt>() to Rational(0, 1),
							listOf<UInt>() to Rational(1, 4),
							listOf(1u) to Rational(-5, 3),
							listOf(2u) to Rational(35, 9),
							listOf(3u) to Rational(-100, 27),
							listOf(4u) to Rational(100, 81),
							listOf(0u, 1u) to Rational(-5, 3),
							listOf(1u, 1u) to Rational(14, 9),
							listOf(2u, 1u) to Rational(1874, 189),
							listOf(3u, 1u) to Rational(-620, 63),
							listOf(4u, 1u) to Rational(40, 63),
							listOf(0u, 2u) to Rational(16, 9),
							listOf(1u, 2u) to Rational(365, 21),
							listOf(2u, 2u) to Rational(112, 9),
							listOf(3u, 2u) to Rational(-464, 63),
							listOf(4u, 2u) to Rational(1996, 441),
							listOf(0u, 3u) to Rational(10, 3),
							listOf(1u, 3u) to Rational(118, 21),
							listOf(2u, 3u) to Rational(-272, 21),
							listOf(3u, 3u) to Rational(-764, 49),
							listOf(4u, 3u) to Rational(8, 7),
							listOf(0u, 4u) to Rational(1, 1),
							listOf(1u, 4u) to Rational(-10, 7),
							listOf(2u, 4u) to Rational(-171, 49),
							listOf(3u, 4u) to Rational(20, 7),
							listOf(4u, 4u) to Rational(4, 1)
                    ),
                ),
                PSubstituteRFTestData(
                    mapOf(
						listOf<UInt>() to Rational(15, 7),
						listOf(1u) to Rational(1, 5),
						listOf(2u) to Rational(-7, 4),
						listOf(0u, 1u) to Rational(-1, 9),
						listOf(1u, 1u) to Rational(-2, 7),
						listOf(2u, 1u) to Rational(17, 3),
						listOf(0u, 2u) to Rational(2, 6),
						listOf(1u, 2u) to Rational(-17, 6),
						listOf(2u, 2u) to Rational(-6, 2),
                    ),
                    mapOf(
						0 to Pair(
							mapOf(
								listOf<UInt>() to Rational(17, 5),
								listOf(1u) to Rational(11, 6),
								listOf(2u) to Rational(14, 3),
								listOf(0u, 1u) to Rational(17, 1),
								listOf(1u, 1u) to Rational(12, 3),
								listOf(2u, 1u) to Rational(-6, 2),
								listOf(0u, 2u) to Rational(17, 1),
								listOf(1u, 2u) to Rational(-4, 3),
								listOf(2u, 2u) to Rational(2, 6),
							),
							mapOf(
								listOf<UInt>() to Rational(3, 5),
								listOf(1u) to Rational(3, 5),
								listOf(2u) to Rational(3, 7),
								listOf(0u, 1u) to Rational(-3, 8),
								listOf(1u, 1u) to Rational(-1, 1),
								listOf(2u, 1u) to Rational(17, 9),
								listOf(0u, 2u) to Rational(-8, 1),
								listOf(1u, 2u) to Rational(6, 4),
								listOf(2u, 2u) to Rational(10, 9),
							)
						),
						1 to Pair(
							mapOf(
								listOf<UInt>() to Rational(18, 5),
								listOf(1u) to Rational(-17, 5),
								listOf(2u) to Rational(-2, 7),
								listOf(0u, 1u) to Rational(6, 5),
								listOf(1u, 1u) to Rational(-5, 1),
								listOf(2u, 1u) to Rational(-9, 1),
								listOf(0u, 2u) to Rational(-8, 8),
								listOf(1u, 2u) to Rational(2, 7),
								listOf(2u, 2u) to Rational(-13, 7),
							),
							mapOf(
								listOf<UInt>() to Rational(-4, 8),
								listOf(1u) to Rational(15, 9),
								listOf(2u) to Rational(-10, 9),
								listOf(0u, 1u) to Rational(5, 3),
								listOf(1u, 1u) to Rational(4, 1),
								listOf(2u, 1u) to Rational(-2, 7),
								listOf(0u, 2u) to Rational(2, 2),
								listOf(1u, 2u) to Rational(-5, 7),
								listOf(2u, 2u) to Rational(-18, 9),
							)
						),
                    ),
                    mapOf(
						listOf<UInt>() to Rational(-6443599, 10000),
						listOf(1u) to Rational(166251223, 210000),
						listOf(2u) to Rational(-4606805099, 3528000),
						listOf(3u) to Rational(51204379, 19600),
						listOf(4u) to Rational(-529045460659, 277830000),
						listOf(5u) to Rational(2630836709, 1488375),
						listOf(6u) to Rational(-42675691369, 25004700),
						listOf(7u) to Rational(495825223, 1250235),
						listOf(8u) to Rational(-22531756, 1750329),
						listOf(0u, 1u) to Rational(-2526552797, 420000),
						listOf(1u, 1u) to Rational(31108840471, 2520000),
						listOf(2u, 1u) to Rational(-4789740847, 1102500),
						listOf(3u, 1u) to Rational(186594307807, 11340000),
						listOf(4u, 1u) to Rational(-11677815943, 1488375),
						listOf(5u, 1u) to Rational(-181118486447, 27783000),
						listOf(6u, 1u) to Rational(-16123292162, 14586075),
						listOf(7u, 1u) to Rational(-140339343808, 26254935),
						listOf(8u, 1u) to Rational(4570171616, 5250987),
						listOf(0u, 2u) to Rational(-181436530573, 10080000),
						listOf(1u, 2u) to Rational(6700437957491, 105840000),
						listOf(2u, 2u) to Rational(-3527267461, 1417500),
						listOf(3u, 2u) to Rational(-38084563451, 5556600),
						listOf(4u, 2u) to Rational(-565662040631, 13891500),
						listOf(5u, 2u) to Rational(-35479071126397, 583443000),
						listOf(6u, 2u) to Rational(-11717559078469, 525098700),
						listOf(7u, 2u) to Rational(-2043385293517, 225042300),
						listOf(8u, 2u) to Rational(-3644439630451, 551353635),
						listOf(0u, 3u) to Rational(-1760423269, 126000),
						listOf(1u, 3u) to Rational(310176758299, 2352000),
						listOf(2u, 3u) to Rational(-907229584837, 21168000),
						listOf(3u, 3u) to Rational(-16717135885963, 95256000),
						listOf(4u, 3u) to Rational(-43762928025353, 333396000),
						listOf(5u, 3u) to Rational(-328427480571607, 3000564000),
						listOf(6u, 3u) to Rational(-7722675917197, 210039480),
						listOf(7u, 3u) to Rational(1713350137019, 1225230300),
						listOf(8u, 3u) to Rational(156695935643, 31505922),
						listOf(0u, 4u) to Rational(18362364269, 1008000),
						listOf(1u, 4u) to Rational(955674858553, 10584000),
						listOf(2u, 4u) to Rational(-71937470607371, 444528000),
						listOf(3u, 4u) to Rational(-34097985615163, 95256000),
						listOf(4u, 4u) to Rational(-340736178775883, 2000376000),
						listOf(5u, 4u) to Rational(-511324523441897, 10501974000),
						listOf(6u, 4u) to Rational(-125375649409151, 8821658160),
						listOf(7u, 4u) to Rational(-2813518533421, 1575296100),
						listOf(8u, 4u) to Rational(-17044089109, 5250987),
						listOf(0u, 5u) to Rational(600086461, 20160),
						listOf(1u, 5u) to Rational(-18959931367, 423360),
						listOf(2u, 5u) to Rational(-9178804929607, 44452800),
						listOf(3u, 5u) to Rational(-1460114275979, 5334336),
						listOf(4u, 5u) to Rational(-342533479090169, 4200789600),
						listOf(5u, 5u) to Rational(20335453022963, 4200789600),
						listOf(6u, 5u) to Rational(-21649775090197, 6301184400),
						listOf(7u, 5u) to Rational(-197301716069, 131274675),
						listOf(8u, 5u) to Rational(18711357470, 15752961),
						listOf(0u, 6u) to Rational(621417991, 100800),
						listOf(1u, 6u) to Rational(-159236792977, 2116800),
						listOf(2u, 6u) to Rational(-6602528890883, 66679200),
						listOf(3u, 6u) to Rational(-1086091664047, 19051200),
						listOf(4u, 6u) to Rational(3769375009003, 1680315840),
						listOf(5u, 6u) to Rational(-12920385574769, 1050197400),
						listOf(6u, 6u) to Rational(-90219591809287, 6301184400),
						listOf(7u, 6u) to Rational(656361553391, 1575296100),
						listOf(8u, 6u) to Rational(757900793, 2250423),
						listOf(0u, 7u) to Rational(-100770017, 15120),
						listOf(1u, 7u) to Rational(-316364851, 17640),
						listOf(2u, 7u) to Rational(-85118560057, 6667920),
						listOf(3u, 7u) to Rational(6286563719, 416745),
						listOf(4u, 7u) to Rational(26803885301, 1714608),
						listOf(5u, 7u) to Rational(-13767154393, 4286520),
						listOf(6u, 7u) to Rational(-3875138933, 1224720),
						listOf(7u, 7u) to Rational(65193755, 333396),
						listOf(8u, 7u) to Rational(90974351, 2500470),
						listOf(0u, 8u) to Rational(-3182197, 1260),
						listOf(1u, 8u) to Rational(24899923, 8820),
						listOf(2u, 8u) to Rational(-19999556, 19845),
						listOf(3u, 8u) to Rational(3276587, 3969),
						listOf(4u, 8u) to Rational(13719549239, 5000940),
						listOf(5u, 8u) to Rational(-961839938, 1250235),
						listOf(6u, 8u) to Rational(-198184871, 833490),
						listOf(7u, 8u) to Rational(230659711, 5000940),
						listOf(8u, 8u) to Rational(292447, 35721)
                    ),
                    mapOf(
						listOf<UInt>() to Rational(9, 100),
						listOf(1u) to Rational(-21, 50),
						listOf(2u) to Rational(293, 700),
						listOf(3u) to Rational(29, 210),
						listOf(4u) to Rational(3233, 8820),
						listOf(5u) to Rational(-289, 441),
						listOf(6u) to Rational(-1, 9),
						listOf(7u) to Rational(-20, 441),
						listOf(8u) to Rational(100, 441),
						listOf(0u, 1u) to Rational(-57, 80),
						listOf(1u, 1u) to Rational(-121, 400),
						listOf(2u, 1u) to Rational(37117, 8400),
						listOf(3u, 1u) to Rational(-4853, 3150),
						listOf(4u, 1u) to Rational(1166203, 132300),
						listOf(5u, 1u) to Rational(-2708, 567),
						listOf(6u, 1u) to Rational(-287159, 416745),
						listOf(7u, 1u) to Rational(-478204, 83349),
						listOf(8u, 1u) to Rational(176320, 83349),
						listOf(0u, 2u) to Rational(-6239, 6400),
						listOf(1u, 2u) to Rational(264211, 11200),
						listOf(2u, 2u) to Rational(-1591999, 100800),
						listOf(3u, 2u) to Rational(12450091, 529200),
						listOf(4u, 2u) to Rational(9230759, 226800),
						listOf(5u, 2u) to Rational(18995554, 2083725),
						listOf(6u, 2u) to Rational(136706258, 6251175),
						listOf(7u, 2u) to Rational(-120907496, 3750705),
						listOf(8u, 2u) to Rational(117200176, 15752961),
						listOf(0u, 3u) to Rational(5653, 320),
						listOf(1u, 3u) to Rational(-130853, 8400),
						listOf(2u, 3u) to Rational(-20939327, 151200),
						listOf(3u, 3u) to Rational(2566691, 25200),
						listOf(4u, 3u) to Rational(-68441519, 476280),
						listOf(5u, 3u) to Rational(2462904247, 12502350),
						listOf(6u, 3u) to Rational(353667161, 18753525),
						listOf(7u, 3u) to Rational(-1689134372, 26254935),
						listOf(8u, 3u) to Rational(35084104, 2250423),
						listOf(0u, 4u) to Rational(-3587, 300),
						listOf(1u, 4u) to Rational(-10513243, 33600),
						listOf(2u, 4u) to Rational(30766733, 176400),
						listOf(3u, 4u) to Rational(-65680021, 198450),
						listOf(4u, 4u) to Rational(-8108910547, 20003760),
						listOf(5u, 4u) to Rational(2922125159, 6251175),
						listOf(6u, 4u) to Rational(-4245279943, 131274675),
						listOf(7u, 4u) to Rational(-371946872, 3750705),
						listOf(8u, 4u) to Rational(61286752, 2250423),
						listOf(0u, 5u) to Rational(-20477, 160),
						listOf(1u, 5u) to Rational(215741, 1120),
						listOf(2u, 5u) to Rational(30785843, 31752),
						listOf(3u, 5u) to Rational(-357495959, 317520),
						listOf(4u, 5u) to Rational(-1611242993, 10001880),
						listOf(5u, 5u) to Rational(345925495, 500094),
						listOf(6u, 5u) to Rational(-755948411, 3750705),
						listOf(7u, 5u) to Rational(-108643496, 1250235),
						listOf(8u, 5u) to Rational(1122512, 35721),
						listOf(0u, 6u) to Rational(358037, 2880),
						listOf(1u, 6u) to Rational(3895837, 3360),
						listOf(2u, 6u) to Rational(359419201, 1270080),
						listOf(3u, 6u) to Rational(-158522587, 105840),
						listOf(4u, 6u) to Rational(10909002599, 20003760),
						listOf(5u, 6u) to Rational(76846972, 138915),
						listOf(6u, 6u) to Rational(-327696553, 1250235),
						listOf(7u, 6u) to Rational(-1687328, 35721),
						listOf(8u, 6u) to Rational(1016836, 35721),
						listOf(0u, 7u) to Rational(658, 3),
						listOf(1u, 7u) to Rational(48035, 168),
						listOf(2u, 7u) to Rational(-5777875, 5292),
						listOf(3u, 7u) to Rational(-7893899, 10584),
						listOf(4u, 7u) to Rational(10191652, 11907),
						listOf(5u, 7u) to Rational(2920121, 23814),
						listOf(6u, 7u) to Rational(-2699780, 11907),
						listOf(7u, 7u) to Rational(4556, 441),
						listOf(8u, 7u) to Rational(3440, 189),
						listOf(0u, 8u) to Rational(64, 1),
						listOf(1u, 8u) to Rational(-808, 7),
						listOf(2u, 8u) to Rational(-360895, 1764),
						listOf(3u, 8u) to Rational(257657, 882),
						listOf(4u, 8u) to Rational(3779917, 15876),
						listOf(5u, 8u) to Rational(-610279, 3969),
						listOf(6u, 8u) to Rational(-25091, 441),
						listOf(7u, 8u) to Rational(9560, 567),
						listOf(8u, 8u) to Rational(400, 81)
                    ),
                ),
            ) { (receiver, argCoefs, resultNum, resultDen) ->
                Rational.field {
                    val receiver = NumberedPolynomial(receiver)
                    val args = argCoefs.mapValues { NumberedRationalFunction(it.value.first, it.value.second) }
                    val extra = 5 to NumberedRationalFunction(
                        mapOf(
                            listOf<UInt>() to Rational(-2, 9),
                            listOf(1u) to Rational(-6, 3),
                            listOf(2u) to Rational(10, 9),
                            listOf(0u, 1u) to Rational(13, 3),
                            listOf(1u, 1u) to Rational(-12, 4),
                            listOf(2u, 1u) to Rational(3, 6),
                            listOf(0u, 2u) to Rational(2, 9),
                            listOf(1u, 2u) to Rational(7, 3),
                            listOf(2u, 2u) to Rational(16, 5),
                        ),
                        mapOf(
                            listOf<UInt>() to Rational(-6, 2),
                            listOf(1u) to Rational(6, 2),
                            listOf(2u) to Rational(2, 7),
                            listOf(0u, 1u) to Rational(-18, 1),
                            listOf(1u, 1u) to Rational(-11, 3),
                            listOf(2u, 1u) to Rational(7, 5),
                            listOf(0u, 2u) to Rational(8, 1),
                            listOf(1u, 2u) to Rational(6, 7),
                            listOf(2u, 2u) to Rational(17, 4),
                        )
                    )
                    val result = NumberedRationalFunction(resultNum, resultDen)

                    withClue("without extra argument") {
                        receiver.substitute(this, args) shouldBe result
                    }
                    withClue("with extra argument") {
                        receiver.substitute(this, args + extra) shouldBe result
                    }
                }
            }
        }
        "rational function <- double" - {
            data class RFSubstituteDTestData(
                val polynomialNumCoefs: NumberedPolynomialCoefficients<Double>,
                val polynomialDenCoefs: NumberedPolynomialCoefficients<Double>,
                val substitutionArgs: Map<Int, Double>,
                val resultNumCoefs: NumberedPolynomialCoefficients<Double>,
                val resultDenCoefs: NumberedPolynomialCoefficients<Double>,
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                RFSubstituteDTestData(
                    mapOf(
						listOf<UInt>() to 1.0,
						listOf(1u) to -2.0,
						listOf(2u) to 1.0,
                    ),
                    mapOf(
						listOf<UInt>() to 1.0,
                    ),
                    mapOf(
						0 to 1.0
                    ),
                    mapOf(
                        emptyList<UInt>() to 0.0
                    ),
                    mapOf(
						emptyList<UInt>() to 1.0
                    ),
                ),
                RFSubstituteDTestData(
                    mapOf(
						listOf<UInt>() to 6.593754860231304,
						listOf(1u) to -7.853302571550634,
						listOf(2u) to 1.2265042281530025,
						listOf(0u, 1u) to 3.762648877294904,
						listOf(1u, 1u) to -8.945144619305292,
						listOf(2u, 1u) to -5.141384718042281,
						listOf(0u, 2u) to 7.359794483988782,
						listOf(1u, 2u) to -4.3526172680518815,
						listOf(2u, 2u) to 0.907910924854372,
                    ),
                    mapOf(
						listOf<UInt>() to 9.533292132172562,
						listOf(1u) to -1.982814534018857,
						listOf(2u) to -5.974248303415283,
						listOf(0u, 1u) to 1.5876716499288879,
						listOf(1u, 1u) to -7.535152566659664,
						listOf(2u, 1u) to 0.7549300500153517,
						listOf(0u, 2u) to -5.242030058021028,
						listOf(1u, 2u) to -0.7265704289690582,
						listOf(2u, 2u) to -7.139677818189821,
                    ),
                    mapOf(),
                    mapOf(
						listOf<UInt>() to 6.593754860231304,
						listOf(1u) to -7.853302571550634,
						listOf(2u) to 1.2265042281530025,
						listOf(0u, 1u) to 3.762648877294904,
						listOf(1u, 1u) to -8.945144619305292,
						listOf(2u, 1u) to -5.141384718042281,
						listOf(0u, 2u) to 7.359794483988782,
						listOf(1u, 2u) to -4.3526172680518815,
						listOf(2u, 2u) to 0.907910924854372,
                    ),
                    mapOf(
						listOf<UInt>() to 9.533292132172562,
						listOf(1u) to -1.982814534018857,
						listOf(2u) to -5.974248303415283,
						listOf(0u, 1u) to 1.5876716499288879,
						listOf(1u, 1u) to -7.535152566659664,
						listOf(2u, 1u) to 0.7549300500153517,
						listOf(0u, 2u) to -5.242030058021028,
						listOf(1u, 2u) to -0.7265704289690582,
						listOf(2u, 2u) to -7.139677818189821,
                    ),
                ),
                RFSubstituteDTestData(
                    mapOf(
						listOf<UInt>() to 6.593754860231304,
						listOf(1u) to -7.853302571550634,
						listOf(2u) to 1.2265042281530025,
						listOf(0u, 1u) to 3.762648877294904,
						listOf(1u, 1u) to -8.945144619305292,
						listOf(2u, 1u) to -5.141384718042281,
						listOf(0u, 2u) to 7.359794483988782,
						listOf(1u, 2u) to -4.3526172680518815,
						listOf(2u, 2u) to 0.907910924854372,
                    ),
                    mapOf(
						listOf<UInt>() to 9.533292132172562,
						listOf(1u) to -1.982814534018857,
						listOf(2u) to -5.974248303415283,
						listOf(0u, 1u) to 1.5876716499288879,
						listOf(1u, 1u) to -7.535152566659664,
						listOf(2u, 1u) to 0.7549300500153517,
						listOf(0u, 2u) to -5.242030058021028,
						listOf(1u, 2u) to -0.7265704289690582,
						listOf(2u, 2u) to -7.139677818189821,
                    ),
                    mapOf(
						0 to -8.11707689492641,
					),
                    mapOf(
						listOf<UInt>() to 151.1502229133916,
						listOf(0u, 1u) to -262.3790170577034,
						listOf(0u, 2u) to 102.5097937392923,
                    ),
                    mapOf(
						listOf<UInt>() to -367.9969733169944,
						listOf(0u, 1u) to 112.4911133334554,
						listOf(0u, 2u) to -469.755906895345,
                    ),
                ),
                RFSubstituteDTestData(
                    mapOf(
						listOf<UInt>() to 6.593754860231304,
						listOf(1u) to -7.853302571550634,
						listOf(2u) to 1.2265042281530025,
						listOf(0u, 1u) to 3.762648877294904,
						listOf(1u, 1u) to -8.945144619305292,
						listOf(2u, 1u) to -5.141384718042281,
						listOf(0u, 2u) to 7.359794483988782,
						listOf(1u, 2u) to -4.3526172680518815,
						listOf(2u, 2u) to 0.907910924854372,
                    ),
                    mapOf(
						listOf<UInt>() to 9.533292132172562,
						listOf(1u) to -1.982814534018857,
						listOf(2u) to -5.974248303415283,
						listOf(0u, 1u) to 1.5876716499288879,
						listOf(1u, 1u) to -7.535152566659664,
						listOf(2u, 1u) to 0.7549300500153517,
						listOf(0u, 2u) to -5.242030058021028,
						listOf(1u, 2u) to -0.7265704289690582,
						listOf(2u, 2u) to -7.139677818189821,
                    ),
                    mapOf(
						1 to 0.795265651276015,
					),
                    mapOf(
						listOf<UInt>() to 14.24074356896978,
						listOf(1u) to -17.71987055153461,
						listOf(2u) to -2.288056483312383,
                    ),
                    mapOf(
						listOf<UInt>() to 7.480604285873397,
						listOf(1u) to -8.43478016688617,
						listOf(2u) to -9.88934943900592,
                    ),
                ),
                RFSubstituteDTestData(
                    mapOf(
						listOf<UInt>() to 6.593754860231304,
						listOf(1u) to -7.853302571550634,
						listOf(2u) to 1.2265042281530025,
						listOf(0u, 1u) to 3.762648877294904,
						listOf(1u, 1u) to -8.945144619305292,
						listOf(2u, 1u) to -5.141384718042281,
						listOf(0u, 2u) to 7.359794483988782,
						listOf(1u, 2u) to -4.3526172680518815,
						listOf(2u, 2u) to 0.907910924854372,
                    ),
                    mapOf(
						listOf<UInt>() to 9.533292132172562,
						listOf(1u) to -1.982814534018857,
						listOf(2u) to -5.974248303415283,
						listOf(0u, 1u) to 1.5876716499288879,
						listOf(1u, 1u) to -7.535152566659664,
						listOf(2u, 1u) to 0.7549300500153517,
						listOf(0u, 2u) to -5.242030058021028,
						listOf(1u, 2u) to -0.7265704289690582,
						listOf(2u, 2u) to -7.139677818189821,
                    ),
                    mapOf(
						0 to -8.11707689492641,
						1 to 0.795265651276015,
					),
                    mapOf(
						listOf<UInt>() to 7.321261307532708,
                    ),
                    mapOf(
						listOf<UInt>() to -575.6325831127576,
                    ),
                ),
            ) { (numCoefs, denCoefs, arg, resultNum, resultDen) ->
                Double.field {
                    val receiver = NumberedRationalFunction(numCoefs, denCoefs)
                    val extra = 5 to 0.9211194782050933
                    val result = NumberedRationalFunction(resultNum, resultDen) plusOrMinus 0.000001

                    withClue("without extra argument") {
                        receiver.substitute(arg) shouldBe result
                    }
                    withClue("with extra argument") {
                        receiver.substitute(arg + extra) shouldBe result
                    }
                }
            }
        }
//        "rational function <- constant" - {
//            data class RFSubstituteCTestData(
//                val polynomialNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val polynomialDenCoefs: NumberedPolynomialCoefficients<Rational>,
//                val substitutionArg: Map<Int, Rational>,
//                val resultNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val resultDenCoefs: NumberedPolynomialCoefficients<Rational>,
//            )
//
//            withData(
//                nameIndFn = { index, _ -> "test ${index + 1}" },
//                RFSubstituteCTestData(
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                ),
//                RFSubstituteCTestData(
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                    mapOf(),
//                ),
//            ) { (numCoefs, denCoefs, arg, resultNum, resultDen) ->
//				Rational.field {
//					val receiver = NumberedRationalFunction(numCoefs, denCoefs)
//					val extra = 5 to NumberedRationalFunction()
//					val result = NumberedRationalFunction(resultNum, resultDen)
//
//					withClue("without extra argument") {
//						receiver.substitute(this, arg) shouldBe result
//					}
//					withClue("with extra argument") {
//						receiver.substitute(this, arg + extra) shouldBe result
//					}
//				}
//            }
//        }
//        "rational function <- polynomial" - {
//            data class RFSubstitutePTestData(
//                val receiverNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val receiverDenCoefs: NumberedPolynomialCoefficients<Rational>,
//                val argCoefs: Map<Int, NumberedPolynomialCoefficients<Rational>>,
//                val resultNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val resultDenCoefs: NumberedPolynomialCoefficients<Rational>,
//            )
//
//            withData(
//                nameIndFn = { index, _ -> "test ${index + 1}" },
//                RFSubstitutePTestData(
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//                ),
//                RFSubstitutePTestData(
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//                ),
//            ) { (receiverNum, receiverDen, argCoefs, resultNum, resultDen) ->
//				Rational.field {
//					val receiver = NumberedRationalFunction(receiverNum, receiverDen)
//					val args = argCoefs.mapValues { NumberedPolynomial(it.value) }
//					val extra = NumberedPolynomial()
//					val result = NumberedRationalFunction(resultNum, resultDen)
//
//					withClue("without extra argument") {
//						receiver.substitute(this, args) shouldBe result
//					}
//					withClue("with extra argument") {
//						receiver.substitute(this, args + extra) shouldBe result
//					}
//				}
//            }
//        }
//        // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
//        //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
//        //  not r^(deg(p)(deg(p)+1)/2) as it is now.
//        "!rational function <- rational function" - {
//            data class RFSubstituteRFTestData(
//                val receiverNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val receiverDenCoefs: NumberedPolynomialCoefficients<Rational>,
//                val argCoefs: Map<Int, Pair<NumberedPolynomialCoefficients<Rational>, NumberedPolynomialCoefficients<Rational>>>,
//                val resultNumCoefs: NumberedPolynomialCoefficients<Rational>,
//                val resultDenCoefs: NumberedPolynomialCoefficients<Rational>,
//            )
//
//            withData(
//                nameIndFn = { index, _ -> "test ${index + 1}" },
//                RFSubstituteRFTestData(
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//                ),
//                RFSubstituteRFTestData(
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//					mapOf(
//					),
//                ),
//            ) { (receiverNum, receiverDen, argCoefs, resultNum, resultDen) ->
//				Rational.field {
//					val receiver = NumberedRationalFunction(receiverNum, receiverDen)
//					val args = argCoefs.mapValues { NumberedRationalFunction(it.value.first, it.value.second) }
//					val extra = NumberedRationalFunction()
//					val result = NumberedRationalFunction(resultNum, resultDen)
//
//					withClue("without extra argument") {
//						receiver.substitute(this, args) shouldBe result
//					}
//					withClue("with extra argument") {
//						receiver.substitute(this, args + extra) shouldBe result
//					}
//				}
//            }
//        }
    }
})
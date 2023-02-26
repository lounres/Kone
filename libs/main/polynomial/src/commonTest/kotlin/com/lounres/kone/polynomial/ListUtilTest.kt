/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Rational
import com.lounres.kone.annotations.UnstableKoneAPI
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage


@OptIn(UnstableKoneAPI::class)
class ListUtilTest : FreeSpec({
    "substitute" - {
        "polynomial <- double" - {
            data class PSubstituteDTestData(
                val receiverCoefs: List<Double>,
                val arg: Double,
                val result: Double
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteDTestData(
                    listOf(1.0, -2.0, 1.0),
                    1.0,
                    0.0
                ),
                PSubstituteDTestData(
                    listOf(0.625, 2.6666666666666665, 0.5714285714285714, 1.5),
                    0.2,
                    1.1931904761904761
                ),
                PSubstituteDTestData(
                    listOf(0.0, 2.6666666666666665, 0.5714285714285714, 1.5),
                    0.2,
                    0.5681904761904762
                ),
                PSubstituteDTestData(
                    listOf(0.625, 2.6666666666666665, 0.5714285714285714, 0.0),
                    0.2,
                    1.1811904761904761
                ),
                PSubstituteDTestData(
                    listOf(0.625, 2.6666666666666665, 0.0, 1.5),
                    0.2,
                    1.1703333333333332
                ),
            ) { (coefs, arg, result) ->
                ListPolynomial(coefs).substitute(arg) shouldBe (result plusOrMinus 0.000001)
            }
        }
        "polynomial <- constant" - {
            data class PSubstituteCTestData(
                val receiverCoefs: List<Rational>,
                val arg: Rational,
                val result: Rational
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteCTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    Rational(1),
                    Rational(0)
                ),
                PSubstituteCTestData(
                    listOf(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(3, 2)),
                    Rational(1, 5),
                    Rational(25057, 21000)
                ),
                PSubstituteCTestData(
                    listOf(Rational(0), Rational(8, 3), Rational(4, 7), Rational(3, 2)),
                    Rational(1, 5),
                    Rational(2983, 5250)
                ),
                PSubstituteCTestData(
                    listOf(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(0)),
                    Rational(1, 5),
                    Rational(4961, 4200)
                ),
                PSubstituteCTestData(
                    listOf(Rational(5, 8), Rational(8, 3), Rational(0), Rational(3, 2)),
                    Rational(1, 5),
                    Rational(3511, 3000)
                ),
            ) { (coefs, arg, result) ->
                ListPolynomial(coefs).substitute(Rational.field, arg) shouldBe result
            }
        }
        "polynomial <- polynomial" - {
            data class PSubstitutePTestData(
                val receiverCoefs: List<Rational>,
                val argCoefs: List<Rational>,
                val resultCoefs: List<Rational>
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstitutePTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(0)),
                ),
                PSubstitutePTestData(
                    listOf(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(2, 7)),
                    listOf(Rational(6, 9), Rational(1, 5)),
                    listOf(Rational(709, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
                ),
                PSubstitutePTestData(
                    listOf(Rational(0), Rational(9, 4), Rational(1, 3), Rational(2, 7)),
                    listOf(Rational(6, 9), Rational(1, 5)),
                    listOf(Rational(655, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
                ),
                PSubstitutePTestData(
                    listOf(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(0)),
                    listOf(Rational(6, 9), Rational(1, 5)),
                    listOf(Rational(677, 378), Rational(97, 180), Rational(1, 75), Rational(0)),
                ),
                PSubstitutePTestData(
                    listOf(Rational(1, 7), Rational(9, 4), Rational(0), Rational(2, 7)),
                    listOf(Rational(6, 9), Rational(1, 5)),
                    listOf(Rational(653, 378), Rational(221, 420), Rational(4, 175), Rational(2, 875)),
                ),
                PSubstitutePTestData(
                    listOf(Rational(0), Rational(9, 4), Rational(1, 3), Rational(0)),
                    listOf(Rational(6, 9), Rational(0)),
                    listOf(Rational(89, 54), Rational(0), Rational(0), Rational(0)),
                ),
            ) { (receiver, arg, result) ->
                ListPolynomial(receiver).substitute(Rational.field, ListPolynomial(arg)) shouldBe ListPolynomial(
                    result
                )
            }
        }
        // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
        //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
        //  not r^(deg(p)(deg(p)+1)/2) as it is now.
        "!polynomial <- rational function" - {
            data class PSubstituteRFTestData(
                val receiverCoefs: List<Rational>,
                val argNumCoefs: List<Rational>,
                val argDenCoefs: List<Rational>,
                val resultNumCoefs: List<Rational>,
                val resultDenCoefs: List<Rational>,
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                PSubstituteRFTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(0)),
                    listOf(Rational(1)),
                ),
                PSubstituteRFTestData(
                    listOf(
                        Rational(13, 3),
                        Rational(-9, 5),
                        Rational(5, 5)
                    ),
                    listOf(
                        Rational(15, 1),
                        Rational(6, 9),
                        Rational(-3, 7)
                    ),
                    listOf(
                        Rational(-13, 9),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(11, 3)
                    ),
                    listOf(
                        Rational(66349, 243),
                        Rational(-17873, 405),
                        Rational(173533, 3780),
                        Rational(-91141, 567),
                        Rational(5773909, 105840),
                        Rational(-23243, 630),
                        Rational(1573, 27)
                    ),
                    listOf(
                        Rational(169, 81),
                        Rational(-130, 27),
                        Rational(115, 18),
                        Rational(-797, 54),
                        Rational(1985, 144),
                        Rational(-55, 6),
                        Rational(121, 9)
                    ),
                ),
                PSubstituteRFTestData(
                    listOf(
                        Rational(0),
                        Rational(-9, 5),
                        Rational(5, 5)
                    ),
                    listOf(
                        Rational(0),
                        Rational(6, 9),
                        Rational(-3, 7)
                    ),
                    listOf(
                        Rational(0),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(11, 3)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(-14, 9),
                        Rational(31, 14),
                        Rational(-5077, 980),
                        Rational(99, 35)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(25, 9),
                        Rational(-25, 6),
                        Rational(1985, 144),
                        Rational(-55, 6),
                        Rational(121, 9)
                    ),
                ),
                PSubstituteRFTestData(
                    listOf(
                        Rational(13, 3),
                        Rational(-9, 5),
                        Rational(0)
                    ),
                    listOf(
                        Rational(15, 1),
                        Rational(6, 9),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-13, 9),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-898, 27),
                        Rational(271, 45),
                        Rational(-65, 12),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-13, 9),
                        Rational(5, 3),
                        Rational(-5, 4),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(0)
                    ),
                ),
                PSubstituteRFTestData(
                    listOf(
                        Rational(13, 3),
                        Rational(0),
                        Rational(5, 5)
                    ),
                    listOf(
                        Rational(15, 1),
                        Rational(0),
                        Rational(-3, 7)
                    ),
                    listOf(
                        Rational(-13, 9),
                        Rational(0),
                        Rational(0),
                        Rational(11, 3)
                    ),
                    listOf(
                        Rational(56872, 243),
                        Rational(0, 1),
                        Rational(-90, 7),
                        Rational(-3718, 81),
                        Rational(9, 49),
                        Rational(0, 1),
                        Rational(1573, 27)
                    ),
                    listOf(
                        Rational(169, 81),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(-286, 27),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(121, 9)
                    ),
                ),
            ) { (receiver, argNum, argDen, resultNum, resultDen) ->
                ListPolynomial(receiver).substitute(
                    Rational.field,
                    ListRationalFunction(argNum, argDen)
                ) shouldBe ListRationalFunction(resultNum, resultDen)
            }
        }
        "rational function <- double" - {
            data class RFSubstituteDTestData(
                val receiverNumCoefs: List<Double>,
                val receiverDenCoefs: List<Double>,
                val arg: Double,
                val result: Double
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                RFSubstituteDTestData(
                    listOf(1.0, -2.0, 1.0),
                    listOf(
                        -6.302012278484357,
                        5.831971885376948,
                        -9.271604788393432,
                        5.494387848015814,
                        -3.7187384450880785
                    ),
                    1.0,
                    0.0
                ),
                RFSubstituteDTestData(
                    listOf(-5.848840571263625, -1.660411278951134, -3.793740946372443, -9.624569269490076),
                    listOf(-2.9680680215311073, -1.862973627119981, 4.776550592888336, -2.7320154512368466),
                    -7.53452770353279,
                    2.693702616649797
                ),
                RFSubstituteDTestData(
                    listOf(0.0, -1.660411278951134, -3.793740946372443, -9.624569269490076),
                    listOf(0.0, -1.862973627119981, 4.776550592888336, -2.7320154512368466),
                    -7.53452770353279,
                    2.692226268901378
                ),
                RFSubstituteDTestData(
                    listOf(-5.848840571263625, -1.660411278951134, -3.793740946372443, 0.0),
                    listOf(-2.9680680215311073, -1.862973627119981, 4.776550592888336, 0.0),
                    -7.53452770353279,
                    -0.7394904842099175
                ),
                RFSubstituteDTestData(
                    listOf(-5.848840571263625, 0.0, 0.0, -9.624569269490076),
                    listOf(-2.9680680215311073, 0.0, 0.0, -2.7320154512368466),
                    -7.53452770353279,
                    3.526835209398159
                ),
            ) { (numCoefs, denCoefs, arg, result) ->
                ListRationalFunction(numCoefs, denCoefs).substitute(arg) shouldBe (result plusOrMinus 0.000001)
            }
        }
        "rational function <- constant" - {
            data class RFSubstituteCTestData(
                val receiverNumCoefs: List<Rational>,
                val receiverDenCoefs: List<Rational>,
                val arg: Rational,
                val result: Rational
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                RFSubstituteCTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    listOf(Rational(1)),
                    Rational(1),
                    Rational(0)
                ),
                RFSubstituteCTestData(
                    listOf(Rational(17, 7), Rational(18, 3), Rational(18, 8), Rational(9, 1)),
                    listOf(Rational(11, 9), Rational(-6, 5), Rational(-12, 7), Rational(2, 1)),
                    Rational(-7, 8),
                    Rational(1149615, 61306)
                ),
                RFSubstituteCTestData(
                    listOf(Rational(0), Rational(18, 3), Rational(18, 8), Rational(9, 1)),
                    listOf(Rational(0), Rational(-6, 5), Rational(-12, 7), Rational(2, 1)),
                    Rational(-7, 8),
                    Rational(3495, 586)
                ),
                RFSubstituteCTestData(
                    listOf(Rational(17, 7), Rational(18, 3), Rational(18, 8), Rational(0)),
                    listOf(Rational(11, 9), Rational(-6, 5), Rational(-12, 7), Rational(0)),
                    Rational(-7, 8),
                    Rational(-88605, 77392)
                ),
                RFSubstituteCTestData(
                    listOf(Rational(17, 7), Rational(0), Rational(0), Rational(9, 1)),
                    listOf(Rational(11, 9), Rational(0), Rational(0), Rational(2, 1)),
                    Rational(-7, 8),
                    Rational(116145, 3794)
                ),
            ) { (numCoefs, denCoefs, arg, result) ->
                ListRationalFunction(numCoefs, denCoefs).substitute(Rational.field, arg) shouldBe result
            }
        }
        "rational function <- polynomial" - {
            data class RFSubstitutePTestData(
                val receiverNumCoefs: List<Rational>,
                val receiverDenCoefs: List<Rational>,
                val argCoefs: List<Rational>,
                val resultNumCoefs: List<Rational>,
                val resultDenCoefs: List<Rational>,
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                RFSubstitutePTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(0)),
                    listOf(Rational(1)),
                ),
                RFSubstitutePTestData(
                    listOf(
                        Rational(2, 9),
                        Rational(11, 3),
                        Rational(-9, 4),
                        Rational(-6, 1),
                        Rational(-11, 6)
                    ),
                    listOf(
                        Rational(-2, 3),
                        Rational(-15, 4),
                        Rational(5, 9),
                        Rational(-5, 9)
                    ),
                    listOf(
                        Rational(-9, 1),
                        Rational(-1, 4),
                        Rational(2, 4)
                    ),
                    listOf(
                        Rational(-283303, 36),
                        Rational(-23593, 24),
                        Rational(368713, 192),
                        Rational(1455, 8),
                        Rational(-272171, 1536),
                        Rational(-2149, 192),
                        Rational(469, 64),
                        Rational(11, 48),
                        Rational(-11, 96)
                    ),
                    listOf(
                        Rational(5797, 12),
                        Rational(595, 16),
                        Rational(-5285, 72),
                        Rational(-745, 192),
                        Rational(1105, 288),
                        Rational(5, 48),
                        Rational(-5, 72)
                    ),
                ),
                RFSubstitutePTestData(
                    listOf(
                        Rational(0),
                        Rational(11, 3),
                        Rational(-9, 4),
                        Rational(-6, 1),
                        Rational(-11, 6)
                    ),
                    listOf(
                        Rational(0),
                        Rational(-15, 4),
                        Rational(5, 9),
                        Rational(-5, 9)
                    ),
                    listOf(
                        Rational(0),
                        Rational(-1, 4),
                        Rational(2, 4)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(-11, 12),
                        Rational(325, 192),
                        Rational(21, 32),
                        Rational(-1739, 1536),
                        Rational(227, 192),
                        Rational(-59, 64),
                        Rational(11, 48),
                        Rational(-11, 96)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(15, 16),
                        Rational(-265, 144),
                        Rational(-25, 192),
                        Rational(25, 288),
                        Rational(5, 48),
                        Rational(-5, 72)
                    ),
                ),
                RFSubstitutePTestData(
                    listOf(
                        Rational(2, 9),
                        Rational(11, 3),
                        Rational(-9, 4),
                        Rational(-6, 1),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-2, 3),
                        Rational(-15, 4),
                        Rational(5, 9),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-9, 1),
                        Rational(-1, 4),
                        Rational(0)
                    ),
                    listOf(
                        Rational(149723, 36),
                        Rational(8483, 24),
                        Rational(639, 64),
                        Rational(3, 32),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(0)
                    ),
                    listOf(
                        Rational(937, 12),
                        Rational(55, 16),
                        Rational(5, 144),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(0)
                    ),
                ),
                RFSubstitutePTestData(
                    listOf(
                        Rational(2, 9),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(-11, 6)
                    ),
                    listOf(
                        Rational(-2, 3),
                        Rational(0),
                        Rational(0),
                        Rational(-5, 9)
                    ),
                    listOf(
                        Rational(-9, 1),
                        Rational(0),
                        Rational(2, 4)
                    ),
                    listOf(
                        Rational(-216509, 18),
                        Rational(0, 1),
                        Rational(2673, 1),
                        Rational(0, 1),
                        Rational(-891, 4),
                        Rational(0, 1),
                        Rational(33, 4),
                        Rational(0, 1),
                        Rational(-11, 96)
                    ),
                    listOf(
                        Rational(1213, 3),
                        Rational(0, 1),
                        Rational(-135, 2),
                        Rational(0, 1),
                        Rational(15, 4),
                        Rational(0, 1),
                        Rational(-5, 72)
                    ),
                ),
            ) { (receiverNum, receiverDen, arg, resultNum, resultDen) ->
                ListRationalFunction(receiverNum, receiverDen).substitute(
                    Rational.field,
                    ListPolynomial(arg)
                ) shouldBe ListRationalFunction(resultNum, resultDen)
            }
        }
        // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
        //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
        //  not r^(deg(p)(deg(p)+1)/2) as it is now.
        "!rational function <- rational function" - {
            data class RFSubstituteRFTestData(
                val receiverNumCoefs: List<Rational>,
                val receiverDenCoefs: List<Rational>,
                val argNumCoefs: List<Rational>,
                val argDenCoefs: List<Rational>,
                val resultNumCoefs: List<Rational>,
                val resultDenCoefs: List<Rational>,
            )

            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                RFSubstituteRFTestData(
                    listOf(Rational(1), Rational(-2), Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(1)),
                    listOf(Rational(0)),
                    listOf(Rational(1)),
                ),
                RFSubstituteRFTestData(
                    listOf(
                        Rational(1, 1),
                        Rational(-10, 5),
                        Rational(18, 8),
                        Rational(-8, 8)
                    ),
                    listOf(
                        Rational(-14, 8),
                        Rational(-14, 8),
                        Rational(-19, 6),
                        Rational(14, 3),
                        Rational(8, 9)
                    ),
                    listOf(
                        Rational(14, 9),
                        Rational(-2, 5),
                        Rational(-14, 7)
                    ),
                    listOf(
                        Rational(-6, 4),
                        Rational(5, 9),
                        Rational(1, 8)
                    ),
                    listOf(
                        Rational(130087, 3888),
                        Rational(-2866333, 65610),
                        Rational(-5076229, 97200),
                        Rational(222136997, 3280500),
                        Rational(754719329, 20995200),
                        Rational(-12010283, 324000),
                        Rational(-2011967, 172800),
                        Rational(18607, 2880),
                        Rational(4705, 4096)
                    ),
                    listOf(
                        Rational(-143820355, 3779136),
                        Rational(73886869, 1574640),
                        Rational(1440175193, 15746400),
                        Rational(-5308968857, 52488000),
                        Rational(-186910083731, 2099520000),
                        Rational(125043463, 1555200),
                        Rational(5299123, 388800),
                        Rational(-213757, 15360),
                        Rational(1380785, 147456)
                    ),
                ),
                RFSubstituteRFTestData(
                    listOf(
                        Rational(0),
                        Rational(-10, 5),
                        Rational(18, 8),
                        Rational(-8, 8)
                    ),
                    listOf(
                        Rational(0),
                        Rational(-14, 8),
                        Rational(-19, 6),
                        Rational(14, 3),
                        Rational(8, 9)
                    ),
                    listOf(
                        Rational(0),
                        Rational(-2, 5),
                        Rational(-14, 7)
                    ),
                    listOf(
                        Rational(0),
                        Rational(5, 9),
                        Rational(1, 8)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(5173, 18225),
                        Rational(904291, 364500),
                        Rational(283127, 43200),
                        Rational(37189, 5760),
                        Rational(147, 128)
                    ),
                    listOf(
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(-163589, 911250),
                        Rational(-881831, 291600),
                        Rational(-10722229, 777600),
                        Rational(-640921, 46080),
                        Rational(86303, 9216)
                    ),
                ),
                RFSubstituteRFTestData(
                    listOf(
                        Rational(1, 1),
                        Rational(-10, 5),
                        Rational(18, 8),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-14, 8),
                        Rational(-14, 8),
                        Rational(-19, 6),
                        Rational(14, 3),
                        Rational(0)
                    ),
                    listOf(
                        Rational(14, 9),
                        Rational(-2, 5),
                        Rational(0)
                    ),
                    listOf(
                        Rational(-6, 4),
                        Rational(5, 9),
                        Rational(0)
                    ),
                    listOf(
                        Rational(445, 16),
                        Rational(-2011, 54),
                        Rational(1359199, 72900),
                        Rational(-135733, 32805),
                        Rational(2254, 6561),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1)
                    ),
                    listOf(
                        Rational(-2018387, 46656),
                        Rational(82316437, 1574640),
                        Rational(-9335047, 393660),
                        Rational(15765889, 3280500),
                        Rational(-242089, 656100),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1),
                        Rational(0, 1)
                    ),
                ),
                RFSubstituteRFTestData(
                    listOf(
                        Rational(1, 1),
                        Rational(0),
                        Rational(0),
                        Rational(-8, 8)
                    ),
                    listOf(
                        Rational(-14, 8),
                        Rational(0),
                        Rational(0),
                        Rational(0),
                        Rational(8, 9)
                    ),
                    listOf(
                        Rational(14, 9),
                        Rational(0),
                        Rational(-14, 7)
                    ),
                    listOf(
                        Rational(-6, 4),
                        Rational(0),
                        Rational(1, 8)
                    ),
                    listOf(
                        Rational(41635, 3888),
                        Rational(0, 1),
                        Rational(-279187, 11664),
                        Rational(0, 1),
                        Rational(103769, 3456),
                        Rational(0, 1),
                        Rational(-11017, 768),
                        Rational(0, 1),
                        Rational(4097, 4096)
                    ),
                    listOf(
                        Rational(-13811791, 3779136),
                        Rational(0, 1),
                        Rational(-9999395, 419904),
                        Rational(0, 1),
                        Rational(6376601, 124416),
                        Rational(0, 1),
                        Rational(-3668315, 82944),
                        Rational(0, 1),
                        Rational(2097089, 147456)
                    ),
                ),
            ) { (receiverNum, receiverDen, argNum, argDen, resultNum, resultDen) ->
                ListRationalFunction(receiverNum, receiverDen).substitute(
                    Rational.field,
                    ListRationalFunction(argNum, argDen)
                ) shouldBe ListRationalFunction(resultNum, resultDen)
            }
        }
    }
    "derivation" - {
        data class DerivationTestData(val derivationSeries: List<List<Rational>>) {
            constructor(vararg derivativeSeries: List<Rational>): this(derivativeSeries.toList())
        }

        val derivativeTestData = listOf(
            DerivationTestData(
                listOf(Rational(1), Rational(-2), Rational(1)),
                listOf(Rational(-2), Rational(2)),
                listOf(Rational(2)),
                listOf(),
            ),
            DerivationTestData(
                listOf(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
                listOf(Rational(8, 9), Rational(30, 7), Rational(-20, 3)),
                listOf(Rational(30, 7), Rational(-40, 3)),
                listOf(Rational(-40, 3)),
            ),
            DerivationTestData(
                listOf(Rational(0), Rational(0), Rational(0), Rational(-20, 9)),
                listOf(Rational(0), Rational(0), Rational(-20, 3)),
                listOf(Rational(0), Rational(-40, 3)),
                listOf(Rational(-40, 3)),
            ),
            DerivationTestData(
                listOf(Rational(-8, 3), Rational(8, 9), Rational(0), Rational(0)),
                listOf(Rational(8, 9), Rational(0), Rational(0)),
                listOf(Rational(0), Rational(0)),
                listOf(Rational(0)),
            ),
        )

        "derivative" - {
            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                derivativeTestData
            ) { test ->
                withData(
                    nameIndFn = { index, _ -> "p$index.derivative() ?= p${index+1}" },
                    (test.derivationSeries + listOf(emptyList())).windowed(2)
                ) { (lead, minor) ->
                    val leadPolynomial = ListPolynomial(lead)
                    val minorPolynomial = ListPolynomial(minor)

                    withClue("derivative of $leadPolynomial must be $minorPolynomial") {
                        leadPolynomial.derivative(Rational.field) shouldBe minorPolynomial
                    }
                }
            }
        }

        "nth derivative" - {
            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                derivativeTestData
            ) { test ->
                val series = test.derivationSeries + listOf(emptyList())
                withData(
                    nameFn = { (l, m) -> "p$l.nthDerivative(${m-l}) ?= p$m" },
                    series.indices.flatMap { m -> (0 until m).map { l -> l to m } }
                ) { (l, m) ->
                    val leadPolynomial = ListPolynomial(series[l])
                    val minorPolynomial = ListPolynomial(series[m])

                    withClue("${m-l}th derivative of $leadPolynomial must be $minorPolynomial") {
                        leadPolynomial.nthDerivative(Rational.field, m-l) shouldBe minorPolynomial
                    }
                }

                withData(
                    nameIndFn = { index, _ -> "p$index.nthDerivative(<negative integer>) throws?" },
                    test.derivationSeries
                ) {
                    for (order in -1 downTo -5)
                        shouldThrowExactly<IllegalArgumentException> {
                            ListPolynomial(it).nthDerivative(Rational.field, order)
                        }.shouldHaveMessage("Order of derivative must be non-negative")
                }
            }
        }

        val antiderivativeTestData = listOf(
            DerivationTestData(
                listOf(Rational(1), Rational(-2), Rational(1)),
                listOf(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            ),
            DerivationTestData(
                listOf(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
                listOf(Rational(0), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)),
                listOf(Rational(0), Rational(0), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
                listOf(Rational(0), Rational(0), Rational(0), Rational(-4, 9), Rational(1, 27), Rational(1, 28), Rational(-1, 54)),
            ),
            DerivationTestData(
                listOf(Rational(0), Rational(0), Rational(0), Rational(-20, 9)),
                listOf(Rational(0), Rational(0), Rational(0), Rational(0), Rational(-5, 9)),
                listOf(Rational(0), Rational(0), Rational(0), Rational(0), Rational(0), Rational(-1, 9)),
                listOf(Rational(0), Rational(0), Rational(0), Rational(0), Rational(0), Rational(0), Rational(-1, 54)),
            ),
            DerivationTestData(
                listOf(Rational(-8, 3), Rational(8, 9), Rational(0), Rational(0)),
                listOf(Rational(0), Rational(-8, 3), Rational(4, 9), Rational(0), Rational(0)),
                listOf(Rational(0), Rational(0), Rational(-4, 3), Rational(4, 27), Rational(0), Rational(0)),
                listOf(Rational(0), Rational(0), Rational(0), Rational(-4, 9), Rational(1, 27), Rational(0), Rational(0)),
            ),
        )

        "antiderivative" - {
            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                antiderivativeTestData
            ) { test ->
                withData(
                    nameIndFn = { index, _ -> "p$index.antiderivative() ?= p${index+1}" },
                    test.derivationSeries.windowed(2)
                ) { (minor, lead) ->
                    val minorPolynomial = ListPolynomial(minor)
                    val leadPolynomial = ListPolynomial(lead)

                    withClue("antiderivative of $minorPolynomial must be $leadPolynomial") {
                        minorPolynomial.antiderivative(Rational.field) shouldBe leadPolynomial
                    }
                }
            }
        }

        "nth antiderivative" - {
            withData(
                nameIndFn = { index, _ -> "test ${index + 1}" },
                antiderivativeTestData
            ) { test ->
                val series = test.derivationSeries
                withData(
                    nameFn = { (m, l) -> "p$m.nthAntiderivative(${l-m}) ?= p$l" },
                    series.indices.flatMap { l -> (0 until l).map { m -> m to l } }
                ) { (m, l) ->
                    val minorPolynomial = ListPolynomial(series[m])
                    val leadPolynomial = ListPolynomial(series[l])

                    withClue("${l-m}th antiderivative of $minorPolynomial must be $leadPolynomial") {
                        minorPolynomial.nthAntiderivative(Rational.field, l-m) shouldBe leadPolynomial
                    }
                }

                withData(
                    nameIndFn = { index, _ -> "p$index.nthAntiderivative(<negative integer>) throws?" },
                    test.derivationSeries
                ) {
                    for (order in -1 downTo -5)
                        shouldThrowExactly<IllegalArgumentException> {
                            ListPolynomial(it).nthAntiderivative(Rational.field, order)
                        }.shouldHaveMessage("Order of antiderivative must be non-negative")
                }
            }
        }
    }
})
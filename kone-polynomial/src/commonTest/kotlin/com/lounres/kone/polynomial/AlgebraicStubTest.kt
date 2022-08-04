/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.polynomial.testUtils.StringExprRing
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


class AlgebraicStubTest: FreeSpec({
    data class TestData<M, R>(val argument: M, val result: R) : WithDataTestName {
        override fun dataTestName(): String = "argument $argument"
    }
    StringExprRing {
        "UInt" - {
            "addMultipliedBySquaring" - {
                withData(
                    TestData(0u, !"57"),
                    TestData(1u, !"(57 + 179)"),
                    TestData(2u, !"(57 + (179 + 179))"),
                    TestData(3u, !"((57 + 179) + (179 + 179))"),
                    TestData(4u, !"(57 + ((179 + 179) + (179 + 179)))"),
                    TestData(5u, !"((57 + 179) + ((179 + 179) + (179 + 179)))"),
                    TestData(6u, !"((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(7u, !"(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(8u, !"(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))"),
                ) {(multiplier, result) ->
                    addMultipliedByDoubling(!57, !179, multiplier) shouldBe result
                }
            }
            "multiplyBySquaring" - {
                withData(
                    TestData(0u, !"0"),
                    TestData(1u, !"57"),
                    TestData(2u, !"(57 + 57)"),
                    TestData(3u, !"(57 + (57 + 57))"),
                    TestData(4u, !"((57 + 57) + (57 + 57))"),
                    TestData(5u, !"(57 + ((57 + 57) + (57 + 57)))"),
                    TestData(6u, !"((57 + 57) + ((57 + 57) + (57 + 57)))"),
                    TestData(7u, !"((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(8u, !"(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                ) {(multiplier, result) ->
                    multiplyByDoubling(!"57", multiplier) shouldBe result
                }
            }
            "multiplyExponentiatedBySquaring" - {
                withData(
                    TestData(0u, !"57"),
                    TestData(1u, !"(57 * 179)"),
                    TestData(2u, !"(57 * (179 * 179))"),
                    TestData(3u, !"((57 * 179) * (179 * 179))"),
                    TestData(4u, !"(57 * ((179 * 179) * (179 * 179)))"),
                    TestData(5u, !"((57 * 179) * ((179 * 179) * (179 * 179)))"),
                    TestData(6u, !"((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(7u, !"(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(8u, !"(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))"),
                ) {(exponent, result) ->
                    multiplyExponentiatedBySquaring(!"57", !"179", exponent) shouldBe result
                }
            }
            "exponentiateBySquaring" - {
                withData(
                    TestData(0u, !"1"),
                    TestData(1u, !"57"),
                    TestData(2u, !"(57 * 57)"),
                    TestData(3u, !"(57 * (57 * 57))"),
                    TestData(4u, !"((57 * 57) * (57 * 57))"),
                    TestData(5u, !"(57 * ((57 * 57) * (57 * 57)))"),
                    TestData(6u, !"((57 * 57) * ((57 * 57) * (57 * 57)))"),
                    TestData(7u, !"((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(8u, !"(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                ) {(exponent, result) ->
                    exponentiateBySquaring(!"57", exponent) shouldBe result
                }
            }
        }
        "Int" - {
            "addMultipliedBySquaring" - {
                withData(
                    TestData(0, !"57"),
                    TestData(1, !"(57 + 179)"),
                    TestData(-1, !"(57 + -179)"),
                    TestData(2, !"(57 + (179 + 179))"),
                    TestData(-2, !"(57 + (-179 + -179))"),
                    TestData(3, !"((57 + 179) + (179 + 179))"),
                    TestData(-3, !"((57 + -179) + (-179 + -179))"),
                    TestData(4, !"(57 + ((179 + 179) + (179 + 179)))"),
                    TestData(-4, !"(57 + ((-179 + -179) + (-179 + -179)))"),
                    TestData(5, !"((57 + 179) + ((179 + 179) + (179 + 179)))"),
                    TestData(-5, !"((57 + -179) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(6, !"((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(-6, !"((57 + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(7, !"(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(-7, !"(((57 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(8, !"(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))"),
                    TestData(-8, !"(57 + (((-179 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179))))"),
                ) {(multiplier, result) ->
                    addMultipliedByDoubling(!"57", !"179", multiplier) shouldBe result
                }
            }
            "multiplyBySquaring" - {
                withData(
                    TestData(0, !"0"),
                    TestData(1, !"57"),
                    TestData(-1, !"-57"),
                    TestData(2, !"(57 + 57)"),
                    TestData(-2, !"(-57 + -57)"),
                    TestData(3, !"(57 + (57 + 57))"),
                    TestData(-3, !"(-57 + (-57 + -57))"),
                    TestData(4, !"((57 + 57) + (57 + 57))"),
                    TestData(-4, !"((-57 + -57) + (-57 + -57))"),
                    TestData(5, !"(57 + ((57 + 57) + (57 + 57)))"),
                    TestData(-5, !"(-57 + ((-57 + -57) + (-57 + -57)))"),
                    TestData(6, !"((57 + 57) + ((57 + 57) + (57 + 57)))"),
                    TestData(-6, !"((-57 + -57) + ((-57 + -57) + (-57 + -57)))"),
                    TestData(7, !"((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(-7, !"((-57 + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))"),
                    TestData(8, !"(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(-8, !"(((-57 + -57) + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))"),
                ) {(multiplier, result) ->
                    multiplyByDoubling(!"57", multiplier) shouldBe result
                }
            }
            "multiplyExponentiatedBySquaring" - {
                withData(
                    TestData(0, !"57"),
                    TestData(1, !"(57 * 179)"),
                    TestData(-1, !"(57 * (1 / 179))"),
                    TestData(2, !"(57 * (179 * 179))"),
                    TestData(-2, !"(57 * ((1 / 179) * (1 / 179)))"),
                    TestData(3, !"((57 * 179) * (179 * 179))"),
                    TestData(-3, !"((57 * (1 / 179)) * ((1 / 179) * (1 / 179)))"),
                    TestData(4, !"(57 * ((179 * 179) * (179 * 179)))"),
                    TestData(-4, !"(57 * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(5, !"((57 * 179) * ((179 * 179) * (179 * 179)))"),
                    TestData(-5, !"((57 * (1 / 179)) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(6, !"((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(-6, !"((57 * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(7, !"(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(-7, !"(((57 * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(8, !"(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))"),
                    TestData(-8, !"(57 * ((((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179)))))"),
                ) {(exponent, result) ->
                    multiplyExponentiatedBySquaring(!"57", !"179", exponent) shouldBe result
                }
            }
            "exponentiateBySquaring" - {
                withData(
                    TestData(0, !"1"),
                    TestData(1, !"57"),
                    TestData(-1, !"(1 / 57)"),
                    TestData(2, !"(57 * 57)"),
                    TestData(-2, !"((1 / 57) * (1 / 57))"),
                    TestData(3, !"(57 * (57 * 57))"),
                    TestData(-3, !"((1 / 57) * ((1 / 57) * (1 / 57)))"),
                    TestData(4, !"((57 * 57) * (57 * 57))"),
                    TestData(-4, !"(((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57)))"),
                    TestData(5, !"(57 * ((57 * 57) * (57 * 57)))"),
                    TestData(-5, !"((1 / 57) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(6, !"((57 * 57) * ((57 * 57) * (57 * 57)))"),
                    TestData(-6, !"(((1 / 57) * (1 / 57)) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(7, !"((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(-7, !"(((1 / 57) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(8, !"(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(-8, !"((((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                ) {(exponent, result) ->
                    exponentiateBySquaring(!"57", exponent) shouldBe result
                }
            }
        }
        "ULong" - {
            "addMultipliedBySquaring" - {
                withData(
                    TestData(0uL, !"57"),
                    TestData(1uL, !"(57 + 179)"),
                    TestData(2uL, !"(57 + (179 + 179))"),
                    TestData(3uL, !"((57 + 179) + (179 + 179))"),
                    TestData(4uL, !"(57 + ((179 + 179) + (179 + 179)))"),
                    TestData(5uL, !"((57 + 179) + ((179 + 179) + (179 + 179)))"),
                    TestData(6uL, !"((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(7uL, !"(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(8uL, !"(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))"),
                ) {(multiplier, result) ->
                    addMultipliedByDoubling(!57, !179, multiplier) shouldBe result
                }
            }
            "multiplyBySquaring" - {
                withData(
                    TestData(0uL, !"0"),
                    TestData(1uL, !"57"),
                    TestData(2uL, !"(57 + 57)"),
                    TestData(3uL, !"(57 + (57 + 57))"),
                    TestData(4uL, !"((57 + 57) + (57 + 57))"),
                    TestData(5uL, !"(57 + ((57 + 57) + (57 + 57)))"),
                    TestData(6uL, !"((57 + 57) + ((57 + 57) + (57 + 57)))"),
                    TestData(7uL, !"((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(8uL, !"(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                ) {(multiplier, result) ->
                    multiplyByDoubling(!"57", multiplier) shouldBe result
                }
            }
            "multiplyExponentiatedBySquaring" - {
                withData(
                    TestData(0uL, !"57"),
                    TestData(1uL, !"(57 * 179)"),
                    TestData(2uL, !"(57 * (179 * 179))"),
                    TestData(3uL, !"((57 * 179) * (179 * 179))"),
                    TestData(4uL, !"(57 * ((179 * 179) * (179 * 179)))"),
                    TestData(5uL, !"((57 * 179) * ((179 * 179) * (179 * 179)))"),
                    TestData(6uL, !"((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(7uL, !"(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(8uL, !"(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))"),
                ) {(exponent, result) ->
                    multiplyExponentiatedBySquaring(!"57", !"179", exponent) shouldBe result
                }
            }
            "exponentiateBySquaring" - {
                withData(
                    TestData(0uL, !"1"),
                    TestData(1uL, !"57"),
                    TestData(2uL, !"(57 * 57)"),
                    TestData(3uL, !"(57 * (57 * 57))"),
                    TestData(4uL, !"((57 * 57) * (57 * 57))"),
                    TestData(5uL, !"(57 * ((57 * 57) * (57 * 57)))"),
                    TestData(6uL, !"((57 * 57) * ((57 * 57) * (57 * 57)))"),
                    TestData(7uL, !"((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(8uL, !"(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                ) {(exponent, result) ->
                    exponentiateBySquaring(!"57", exponent) shouldBe result
                }
            }
        }
        "Long" - {
            "addMultipliedBySquaring" - {
                withData(
                    TestData(0L, !"57"),
                    TestData(1L, !"(57 + 179)"),
                    TestData(-1L, !"(57 + -179)"),
                    TestData(2L, !"(57 + (179 + 179))"),
                    TestData(-2L, !"(57 + (-179 + -179))"),
                    TestData(3L, !"((57 + 179) + (179 + 179))"),
                    TestData(-3L, !"((57 + -179) + (-179 + -179))"),
                    TestData(4L, !"(57 + ((179 + 179) + (179 + 179)))"),
                    TestData(-4L, !"(57 + ((-179 + -179) + (-179 + -179)))"),
                    TestData(5L, !"((57 + 179) + ((179 + 179) + (179 + 179)))"),
                    TestData(-5L, !"((57 + -179) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(6L, !"((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(-6L, !"((57 + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(7L, !"(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))"),
                    TestData(-7L, !"(((57 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))"),
                    TestData(8L, !"(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))"),
                    TestData(-8L, !"(57 + (((-179 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179))))"),
                ) {(multiplier, result) ->
                    addMultipliedByDoubling(!"57", !"179", multiplier) shouldBe result
                }
            }
            "multiplyBySquaring" - {
                withData(
                    TestData(0L, !"0"),
                    TestData(1L, !"57"),
                    TestData(-1L, !"-57"),
                    TestData(2L, !"(57 + 57)"),
                    TestData(-2L, !"(-57 + -57)"),
                    TestData(3L, !"(57 + (57 + 57))"),
                    TestData(-3L, !"(-57 + (-57 + -57))"),
                    TestData(4L, !"((57 + 57) + (57 + 57))"),
                    TestData(-4L, !"((-57 + -57) + (-57 + -57))"),
                    TestData(5L, !"(57 + ((57 + 57) + (57 + 57)))"),
                    TestData(-5L, !"(-57 + ((-57 + -57) + (-57 + -57)))"),
                    TestData(6L, !"((57 + 57) + ((57 + 57) + (57 + 57)))"),
                    TestData(-6L, !"((-57 + -57) + ((-57 + -57) + (-57 + -57)))"),
                    TestData(7L, !"((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(-7L, !"((-57 + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))"),
                    TestData(8L, !"(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))"),
                    TestData(-8L, !"(((-57 + -57) + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))"),
                ) {(multiplier, result) ->
                    multiplyByDoubling(!"57", multiplier) shouldBe result
                }
            }
            "multiplyExponentiatedBySquaring" - {
                withData(
                    TestData(0L, !"57"),
                    TestData(1L, !"(57 * 179)"),
                    TestData(-1L, !"(57 * (1 / 179))"),
                    TestData(2L, !"(57 * (179 * 179))"),
                    TestData(-2L, !"(57 * ((1 / 179) * (1 / 179)))"),
                    TestData(3L, !"((57 * 179) * (179 * 179))"),
                    TestData(-3L, !"((57 * (1 / 179)) * ((1 / 179) * (1 / 179)))"),
                    TestData(4L, !"(57 * ((179 * 179) * (179 * 179)))"),
                    TestData(-4L, !"(57 * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(5L, !"((57 * 179) * ((179 * 179) * (179 * 179)))"),
                    TestData(-5L, !"((57 * (1 / 179)) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(6L, !"((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(-6L, !"((57 * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(7L, !"(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))"),
                    TestData(-7L, !"(((57 * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))"),
                    TestData(8L, !"(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))"),
                    TestData(-8L, !"(57 * ((((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179)))))"),
                ) {(exponent, result) ->
                    multiplyExponentiatedBySquaring(!"57", !"179", exponent) shouldBe result
                }
            }
            "exponentiateBySquaring" - {
                withData(
                    TestData(0L, !"1"),
                    TestData(1L, !"57"),
                    TestData(-1L, !"(1 / 57)"),
                    TestData(2L, !"(57 * 57)"),
                    TestData(-2L, !"((1 / 57) * (1 / 57))"),
                    TestData(3L, !"(57 * (57 * 57))"),
                    TestData(-3L, !"((1 / 57) * ((1 / 57) * (1 / 57)))"),
                    TestData(4L, !"((57 * 57) * (57 * 57))"),
                    TestData(-4L, !"(((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57)))"),
                    TestData(5L, !"(57 * ((57 * 57) * (57 * 57)))"),
                    TestData(-5L, !"((1 / 57) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(6L, !"((57 * 57) * ((57 * 57) * (57 * 57)))"),
                    TestData(-6L, !"(((1 / 57) * (1 / 57)) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(7L, !"((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(-7L, !"(((1 / 57) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                    TestData(8L, !"(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))"),
                    TestData(-8L, !"((((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))"),
                ) {(exponent, result) ->
                    exponentiateBySquaring(!"57", exponent) shouldBe result
                }
            }
        }
    }
})
/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


class RationalTest: FreeSpec() {
    sealed interface TestData {
        val numerator: Long
        val denominator: Long
        data class Results(
            override val numerator: Long,
            override val denominator: Long,
            val resultNumerator: Long,
            val resultDenominator: Long,
        ): TestData
        data class Throws(
            override val numerator: Long,
            override val denominator: Long,
        ): TestData
    }
    init {
        val testData = listOf(
            TestData.Results(27, 5, 27, 5),
            TestData.Results(27, 9, 3, 1),
            TestData.Results(-27, 5, -27, 5),
            TestData.Results(-27, 9, -3, 1),
            TestData.Results(27, -5, -27, 5),
            TestData.Results(27, -9, -3, 1),
            TestData.Results(-27, -5, 27, 5),
            TestData.Results(-27, -9, 3, 1),
            TestData.Throws(27, 0)
        )
        "test checking constructor" - {
            withData(
                nameIndFn = { index, it -> "test # ${index + 1}: numerator ${it.numerator}, denomiantor ${it.denominator}" },
                testData
            ) {
                when(it) {
                    is TestData.Results ->
                        with(Rational(it.numerator, it.denominator)) {
                            numerator shouldBe it.resultNumerator
                            denominator shouldBe it.resultDenominator
                        }
                    is TestData.Throws ->
                        shouldThrowWithMessage<ArithmeticException>("/ by zero") {
                            Rational(it.numerator, it.denominator)
                        }
                }
            }
        }
        "test non-checking constructor" - {
            withData(
                testData
            ) {
                with(Rational(it.numerator, it.denominator, toCheckInput = false)) {
                    numerator shouldBe it.numerator
                    denominator shouldBe it.denominator
                }
            }
        }
    }
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


class RationalTest: FreeSpec() {
    sealed interface ConstructorTestData {
        val numerator: Long
        val denominator: Long
        data class Results(
            override val numerator: Long,
            override val denominator: Long,
            val resultNumerator: Long,
            val resultDenominator: Long,
        ): ConstructorTestData
        data class Throws(
            override val numerator: Long,
            override val denominator: Long,
        ): ConstructorTestData
    }

    val constructorTestData = listOf(
        ConstructorTestData.Results(27, 5, 27, 5),
        ConstructorTestData.Results(27, 9, 3, 1),
        ConstructorTestData.Throws(27, 0)
    ).flatMap {
        when(it) {
            is ConstructorTestData.Results -> listOf(
                it,
                ConstructorTestData.Results(-it.numerator, it.denominator, -it.resultNumerator, it.resultDenominator),
                ConstructorTestData.Results(it.numerator, -it.denominator, -it.resultNumerator, it.resultDenominator),
                ConstructorTestData.Results(-it.numerator, -it.denominator, it.resultNumerator, it.resultDenominator),
            )
            is ConstructorTestData.Throws -> listOf(it)
        }
    }

    init {
        "checking constructor" - {
            withData(
                nameIndFn = { index, it -> "test # ${index + 1}: numerator ${it.numerator}, denomiantor ${it.denominator}" },
                constructorTestData
            ) {
                when(it) {
                    is ConstructorTestData.Results ->
                        with(Rational(it.numerator, it.denominator)) {
                            numerator shouldBe it.resultNumerator
                            denominator shouldBe it.resultDenominator
                        }
                    is ConstructorTestData.Throws ->
                        shouldThrowWithMessage<ArithmeticException>("/ by zero") {
                            Rational(it.numerator, it.denominator)
                        }
                }
            }
        }
        "non-checking constructor" - {
            withData(
                constructorTestData
            ) {
                with(Rational(it.numerator, it.denominator, toCheckInput = false)) {
                    numerator shouldBe it.numerator
                    denominator shouldBe it.denominator
                }
            }
        }
    }
}
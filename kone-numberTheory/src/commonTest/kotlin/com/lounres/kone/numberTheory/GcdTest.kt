/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.numberTheory

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


// TODO: Тесты для других gcd
class GcdTest: FreeSpec({
    data class GcdTestData<C>(
        val first: C,
        val second: C,
        val gcd: C,
        val firstCoef: C,
        val secondCoef: C,
    ) : WithDataTestName {
        override fun dataTestName(): String = "$first and $second"
        val bezoutCoefficientsWithGCD: BezoutCoefficientsWithGCD<C> get() = BezoutCoefficientsWithGCD(firstCoef, secondCoef, gcd)
    }

    val testData = listOf(
        GcdTestData(17, 5, 1, -2, 7),
        GcdTestData(27, 9, 9, 0, 1),
        GcdTestData(34, 8, 2, 1, -4),
        GcdTestData(48, 36, 12, 1, -1),
    ).flatMap {
        listOf(
            it,
            GcdTestData(-it.first, it.second, it.gcd, -it.firstCoef, it.secondCoef),
            GcdTestData(it.first, -it.second, it.gcd, it.firstCoef, -it.secondCoef),
            GcdTestData(-it.first, -it.second, it.gcd, -it.firstCoef, -it.secondCoef),
        )
    }

    "Int" - {
        "test GCD" - {
            withData(testData) {
                gcd(it.first, it.second) shouldBe it.gcd
            }
        }
        "test Bezout coefficients with GCD" - {
            withData(testData) {
                bezoutIdentityWithGCD(it.first, it.second) shouldBe it.bezoutCoefficientsWithGCD
            }
        }
    }
    "Long" - {
        val testData = testData.map {
            GcdTestData(
                it.first.toLong(),
                it.second.toLong(),
                it.gcd.toLong(),
                it.firstCoef.toLong(),
                it.secondCoef.toLong(),
            )
        }
        "test GCD" - {
            withData(testData) {
                gcd(it.first, it.second) shouldBe it.gcd
            }
        }
        "test Bezout coefficients with GCD" - {
            withData(testData) {
                bezoutIdentityWithGCD(it.first, it.second) shouldBe it.bezoutCoefficientsWithGCD
            }
        }
    }
})
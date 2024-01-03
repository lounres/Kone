///*
// * Copyright © 2023 Gleb Minaev
// * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
// */
//
//package dev.lounres.kone.numberTheory
//
//import io.kotest.assertions.assertSoftly
//import io.kotest.core.spec.style.FreeSpec
//import io.kotest.datatest.WithDataTestName
//import io.kotest.datatest.withData
//import io.kotest.matchers.shouldBe
//
//
//class GcdTest: FreeSpec({
//    data class GcdTestData<C>(
//        val first: C,
//        val second: C,
//        val gcd: C,
//        val firstCoef: C,
//        val secondCoef: C,
//    ) : WithDataTestName {
//        override fun dataTestName(): String = "$first and $second"
//        val bezoutCoefficientsWithGCD: BezoutCoefficientsWithGCD<C> get() = BezoutCoefficientsWithGCD(firstCoef, secondCoef, gcd)
//    }
//
//    val testData = listOf(
//        GcdTestData(17, 5, 1, -2, 7),
//        GcdTestData(27, 9, 9, 0, 1),
//        GcdTestData(34, 8, 2, 1, -4),
//        GcdTestData(48, 36, 12, 1, -1),
//    ).flatMap {
//        listOf(
//            it,
//            GcdTestData(-it.first, it.second, it.gcd, -it.firstCoef, it.secondCoef),
//            GcdTestData(it.first, -it.second, it.gcd, it.firstCoef, -it.secondCoef),
//            GcdTestData(-it.first, -it.second, it.gcd, -it.firstCoef, -it.secondCoef),
//        )
//    }
//
//    data class MultipleGcdTestData<C>(
//        val entries: List<C>,
//        val gcd: C
//    ) : WithDataTestName {
//        override fun dataTestName(): String = entries.let { if (it.isEmpty()) "<empty>" else it.joinToString() }
//    }
//
//    val multipleTestData = listOf(
//        MultipleGcdTestData(
//            listOf(),
//            0
//        ),
//        MultipleGcdTestData(
//            listOf(0),
//            0
//        ),
//        MultipleGcdTestData(
//            listOf(1),
//            1
//        ),
//        MultipleGcdTestData(
//            listOf(2),
//            2
//        ),
//        MultipleGcdTestData(
//            listOf(3),
//            3
//        ),
//        MultipleGcdTestData(
//            listOf(4),
//            4
//        ),
//        MultipleGcdTestData(
//            listOf(2, 4),
//            2
//        ),
//        MultipleGcdTestData(
//            listOf(2, 3),
//            1
//        ),
//        MultipleGcdTestData(
//            listOf(5, 7, 9),
//            1
//        ),
//        MultipleGcdTestData(
//            listOf(2, 4, 6),
//            2
//        ),
//        MultipleGcdTestData(
//            listOf(6, 10, 15),
//            1
//        ),
//    ).flatMap {
//        sequence {
//            val entries = it.entries
//            val size = entries.size
//            val state = MutableList(size) { false }
//            while(true) {
//                yield(
//                    MultipleGcdTestData(
//                        List(size) { index -> entries[index].let { if (state[index]) -it else it } },
//                        it.gcd
//                    )
//                )
//                val indexChange = state.indices.find { !state[it] && entries[it] != 0 } ?: break
//                state[indexChange] = true
//                for (index in 0 until indexChange) state[index] = false
//            }
//        }
//    }
//
//    "Int" - {
//        "GCD" - {
//            withData(testData) {
//                gcd(it.first, it.second) shouldBe it.gcd
//            }
//        }
//        "multiple GCD" - {
//            withData(multipleTestData) {
//                assertSoftly {
//                    gcd(it.entries) shouldBe it.gcd
//                    gcd(*it.entries.toIntArray()) shouldBe it.gcd
//                }
//            }
//        }
//        "Bezout coefficients with GCD" - {
//            withData(testData) {
//                bezoutIdentityWithGCD(it.first, it.second) shouldBe it.bezoutCoefficientsWithGCD
//            }
//        }
//    }
//
//    @Suppress("NAME_SHADOWING")
//    "Long" - {
//        val testData = testData.map {
//            GcdTestData(
//                it.first.toLong(),
//                it.second.toLong(),
//                it.gcd.toLong(),
//                it.firstCoef.toLong(),
//                it.secondCoef.toLong(),
//            )
//        }
//        val multipleTestData = multipleTestData.map {
//            MultipleGcdTestData(
//                it.entries.map { it.toLong() },
//                it.gcd.toLong()
//            )
//        }
//        "GCD" - {
//            withData(testData) {
//                gcd(it.first, it.second) shouldBe it.gcd
//            }
//        }
//        "multiple GCD" - {
//            withData(multipleTestData) {
//                assertSoftly {
//                    gcd(it.entries) shouldBe it.gcd
//                    gcd(*it.entries.toLongArray()) shouldBe it.gcd
//                }
//            }
//        }
//        "Bezout coefficients with GCD" - {
//            withData(testData) {
//                bezoutIdentityWithGCD(it.first, it.second) shouldBe it.bezoutCoefficientsWithGCD
//            }
//        }
//    }
//})
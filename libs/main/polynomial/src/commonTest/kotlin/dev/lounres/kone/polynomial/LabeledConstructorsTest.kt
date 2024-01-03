///*
// * Copyright © 2023 Gleb Minaev
// * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
// */
//
//package dev.lounres.kone.polynomial
//
//import dev.lounres.kone.algebraic.ring
//import dev.lounres.kone.annotations.ExperimentalKoneAPI
//import dev.lounres.kone.context.invoke
//import dev.lounres.kone.polynomial.testUtils.t
//import dev.lounres.kone.polynomial.testUtils.x
//import dev.lounres.kone.polynomial.testUtils.y
//import dev.lounres.kone.polynomial.testUtils.z
//import io.kotest.core.spec.style.FreeSpec
//import io.kotest.datatest.withData
//import io.kotest.matchers.shouldBe
//import space.kscience.kmath.expressions.Symbol
//
//
//class LabeledConstructorsTest: FreeSpec({
//    "test fabric constructor" - {
//        data class FabricTestData<C>(
//            val explicitConstructorArgument: Map<Map<Symbol, UInt>, C>,
//            val fabricArgument: Map<Map<Symbol, UInt>, C>,
//        )
//
//        withData(
//            nameIndFn = { index, _ -> "test ${index + 1}" },
//            FabricTestData(
//                mapOf(
//                    mapOf(x to 2u, z to 3u) to 5,
//                    mapOf(y to 1u) to -6,
//                ),
//                mapOf(
//                    mapOf(x to 2u, z to 3u) to 5,
//                    mapOf(y to 1u) to -6,
//                )
//            ),
//            FabricTestData(
//                mapOf(
//                    mapOf(x to 2u, z to 3u) to 5,
//                    mapOf(y to 1u) to -6,
//                ),
//                mapOf(
//                    mapOf(x to 2u, y to 0u, z to 3u, t to 0u) to 5,
//                    mapOf(x to 0u, y to 1u, z to 0u, t to 0u) to -6,
//                )
//            ),
//            FabricTestData(
//                mapOf(
//                    mapOf<Symbol, UInt>() to -1,
//                ),
//                mapOf(
//                    mapOf(x to 0u) to 5,
//                    mapOf(y to 0u, z to 0u) to -6,
//                )
//            ),
//            FabricTestData(
//                mapOf(
//                    mapOf<Symbol, UInt>() to 0,
//                ),
//                mapOf(
//                    mapOf(x to 0u) to 5,
//                    mapOf(y to 0u, z to 0u) to -5,
//                )
//            ),
//        ) { (expectedArgs, actualArgs) ->
//            Int.ring { LabeledPolynomial(actualArgs) } shouldBe LabeledPolynomialAsIs(expectedArgs)
//        }
//    }
//
//    @OptIn(ExperimentalKoneAPI::class)
//    "test DSL1" - {
//        data class DSL1TestData<C>(
//            val explicitConstructorArgument: Map<Map<Symbol, UInt>, C>,
//            val dsl1Builder: DSL1LabeledPolynomialBuilder<C>.() -> Unit
//        ) {
//            constructor(
//                vararg explicitConstructorArgument: Pair<Map<Symbol, UInt>, C>,
//                dsl1Builder: DSL1LabeledPolynomialBuilder<C>.() -> Unit
//            ): this(mapOf(*explicitConstructorArgument), dsl1Builder)
//        }
//        val polynomialSpace = Int.ring.labeledPolynomialSpace
//
//        withData(
//            nameIndFn = { index, _ -> "test ${index + 1}" },
//            DSL1TestData(
//                mapOf(x to 2u, z to 3u) to 5,
//                mapOf(y to 1u) to -6,
//            ) {
//                5 { x pow 2u; z pow 3u }
//                (-6) { y pow 1u }
//            },
//            DSL1TestData(
//                mapOf<Symbol, UInt>() to -1,
//            ) {
//                5 { }
//                (-6) { }
//            },
//            DSL1TestData(
//                mapOf(x to 2u) to -1,
//            ) {
//                5 { x pow 1u; x pow 1u }
//                (-6) { x pow 2u }
//            },
//            DSL1TestData(
//                mapOf(x to 2u) to -1,
//            ) {
//                5 { x pow 1u; x pow 1u }
//                (-6) { x pow 2u; z pow 0u }
//            },
//        ) { (explArgs, builder) ->
//            polynomialSpace { LabeledPolynomialDSL1(block = builder) } shouldBe LabeledPolynomialAsIs(explArgs)
//        }
//    }
//})
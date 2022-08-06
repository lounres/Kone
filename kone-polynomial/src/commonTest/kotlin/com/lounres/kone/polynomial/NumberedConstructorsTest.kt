/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalKoneAPI::class)

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.ring
import com.lounres.kone.annotations.ExperimentalKoneAPI
import com.lounres.kone.polynomial.testUtils.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


class NumberedConstructorsTest: FreeSpec({
    "test fabric constructor" - {
        data class FabricTestData<C>(
            val explicitConstructorArgument: Map<List<UInt>, C>,
            val fabricArgument: Map<List<UInt>, C>,
        )

        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            FabricTestData(
                mapOf(
                    listOf(2u, 0u, 3u) to 5,
                    listOf(0u, 1u) to -6,
                ),
                mapOf(
                    listOf(2u, 0u, 3u) to 5,
                    listOf(0u, 1u) to -6,
                )
            ),
            FabricTestData(
                mapOf(
                    listOf(2u, 0u, 3u) to 5,
                    listOf(0u, 1u) to -6,
                ),
                mapOf(
                    listOf(2u, 0u, 3u, 0u) to 5,
                    listOf(0u, 1u, 0u, 0u) to -6,
                )
            ),
            FabricTestData(
                mapOf(
                    listOf<UInt>() to -1,
                ),
                mapOf(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -6,
                )
            ),
            FabricTestData(
                mapOf(
                    listOf<UInt>() to 0,
                ),
                mapOf(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -5,
                )
            ),
        ) { (expectedArgs, actualArgs) ->
            Int.ring { NumberedPolynomial(actualArgs) } shouldBe NumberedPolynomialAsIs(expectedArgs)
        }
    }

    @OptIn(ExperimentalKoneAPI::class)
    "test DSL1" - {
        data class DSL1TestData<C>(
            val explicitConstructorArgument: Map<List<UInt>, C>,
            val dsl1Builder: DSL1NumberedPolynomialBuilder<C>.() -> Unit
        ) {
            constructor(
                vararg explicitConstructorArgument: Pair<List<UInt>, C>,
                dsl1Builder: DSL1NumberedPolynomialBuilder<C>.() -> Unit
            ): this(mapOf(*explicitConstructorArgument), dsl1Builder)
        }
        val polynomialSpace = Int.ring.numberedPolynomialSpace

        withData(
            nameIndFn = { index, _ -> "test ${index + 1}" },
            DSL1TestData(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ) {
                5 { 0 pow 2u; 2 pow 3u }
                (-6) { 1 pow 1u }
            },
            DSL1TestData(
                listOf<UInt>() to -1,
            ) {
                5 { }
                (-6) { }
            },
            DSL1TestData(
                listOf(2u) to -1,
            ) {
                5 { 0 pow 1u; 0 pow 1u }
                (-6) { 0 pow 2u }
            },
            DSL1TestData(
                listOf(2u) to -1,
            ) {
                5 { 0 pow 1u; 0 pow 1u }
                (-6) { 0 pow 2u; 2 pow 0u }
            },
        ) { (explArgs, builder) ->
            polynomialSpace { NumberedPolynomialDSL1(block = builder) } shouldBe NumberedPolynomialAsIs(explArgs)
        }
    }
})
/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalKoneAPI::class)

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.ring
import com.lounres.kone.annotations.ExperimentalKoneAPI
import kotlin.test.Test
import kotlin.test.assertEquals


class NumberedConstructorsTest {
    @Test
    fun testDSL1() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.ring.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 2u; 2 pow 3u }
                    (-6) { 1 pow 1u }
                }
            },
            "test 1"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to -1,
            ),
            Int.ring.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { }
                    (-6) { }
                }
            },
            "test 2"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u) to -1,
            ),
            Int.ring.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 1u; 0 pow 1u }
                    (-6) { 0 pow 2u }
                }
            },
            "test 3"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u) to -1,
            ),
            Int.ring.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 1u; 0 pow 1u }
                    (-6) { 0 pow 2u; 2 pow 0u }
                }
            },
            "test 3"
        )
    }
    @Test
    fun testFabric() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.ring {
                NumberedPolynomial(
                    listOf(2u, 0u, 3u) to 5,
                    listOf(0u, 1u) to -6,
                )
            },
            "test 1"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.ring {
                NumberedPolynomial(
                    listOf(2u, 0u, 3u, 0u) to 5,
                    listOf(0u, 1u, 0u, 0u) to -6,
                )
            },
            "test 2"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to -1,
            ),
            Int.ring {
                NumberedPolynomial(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -6,
                )
            },
            "test 3"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0,
            ),
            Int.ring {
                NumberedPolynomial(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -5,
                )
            },
            "test 4"
        )
    }
}
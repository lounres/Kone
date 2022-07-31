/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import space.kscience.kmath.expressions.Symbol
import com.lounres.kone.polynomial.testUtils.t
import com.lounres.kone.polynomial.testUtils.x
import com.lounres.kone.polynomial.testUtils.y
import com.lounres.kone.polynomial.testUtils.z
import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.ring
import com.lounres.kone.annotations.ExperimentalKoneAPI
import kotlin.test.Test
import kotlin.test.assertEquals

class LabeledConstructorsTest {
    @Test
    @ExperimentalKoneAPI
    fun testDSL1() {
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.ring.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 2u; z pow 3u }
                    (-6) { y pow 1u }
                }
            },
            "test 1"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to -1,
            ),
            Int.ring.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { }
                    (-6) { }
                }
            },
            "test 2"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u) to -1,
            ),
            Int.ring.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 1u; x pow 1u }
                    (-6) { x pow 2u }
                }
            },
            "test 3"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u) to -1,
            ),
            Int.ring.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 1u; x pow 1u }
                    (-6) { x pow 2u; z pow 0u }
                }
            },
            "test 3"
        )
    }
    @Test
    fun testFabric() {
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.ring {
                LabeledPolynomial(
                    mapOf(x to 2u, z to 3u) to 5,
                    mapOf(y to 1u) to -6,
                )
            },
            "test 1"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.ring {
                LabeledPolynomial(
                    mapOf(x to 2u, y to 0u, z to 3u, t to 0u) to 5,
                    mapOf(x to 0u, y to 1u, z to 0u, t to 0u) to -6,
                )
            },
            "test 2"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to -1,
            ),
            Int.ring {
                LabeledPolynomial(
                    mapOf(x to 0u) to 5,
                    mapOf(y to 0u, z to 0u) to -6,
                )
            },
            "test 3"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to 0,
            ),
            Int.ring {
                LabeledPolynomial(
                    mapOf(x to 0u) to 5,
                    mapOf(z to 0u, t to 0u) to -5,
                )
            },
            "test 4"
        )
    }
}
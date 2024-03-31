/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.LongRing
import dev.lounres.kone.algebraic.ring
import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.iterator
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.MatrixMinorComputerViaBruteForceFeatureProvider
import dev.lounres.kone.linearAlgebra.experiment1.vectorSpace
import kotlin.test.Test


class ConvexHullTest {
    @Test
    fun `MD gift wrapping test 1`(): Unit = Long.ring.vectorSpace {
        install(MatrixMinorComputerViaBruteForceFeatureProvider(numberRing))
        val actualPolytopeConstruction = buildAbstractPolytopicConstruction(spaceDimension = 3u, numberContext = Long.ring) {
            val vertices = koneIterableListOf(
                addVertex(Point(0, 0, 0L, context = Long.ring)),
                addVertex(Point(0, 0, 1L, context = Long.ring)),
                addVertex(Point(0, 0, 2L, context = Long.ring)),
                addVertex(Point(0, 1, 0L, context = Long.ring)),
                addVertex(Point(0, 1, 1L, context = Long.ring)),
                addVertex(Point(0, 1, 2L, context = Long.ring)),
                addVertex(Point(0, 2, 0L, context = Long.ring)),
                addVertex(Point(0, 2, 1L, context = Long.ring)),
                addVertex(Point(0, 2, 2L, context = Long.ring)),
                addVertex(Point(1, 0, 0L, context = Long.ring)),
                addVertex(Point(1, 0, 1L, context = Long.ring)),
                addVertex(Point(1, 0, 2L, context = Long.ring)),
                addVertex(Point(1, 1, 0L, context = Long.ring)),
                addVertex(Point(1, 1, 1L, context = Long.ring)),
                addVertex(Point(1, 1, 2L, context = Long.ring)),
                addVertex(Point(1, 2, 0L, context = Long.ring)),
                addVertex(Point(1, 2, 1L, context = Long.ring)),
                addVertex(Point(1, 2, 2L, context = Long.ring)),
                addVertex(Point(2, 0, 0L, context = Long.ring)),
                addVertex(Point(2, 0, 1L, context = Long.ring)),
                addVertex(Point(2, 0, 2L, context = Long.ring)),
                addVertex(Point(2, 1, 0L, context = Long.ring)),
                addVertex(Point(2, 1, 1L, context = Long.ring)),
                addVertex(Point(2, 1, 2L, context = Long.ring)),
                addVertex(Point(2, 2, 0L, context = Long.ring)),
                addVertex(Point(2, 2, 1L, context = Long.ring)),
                addVertex(Point(2, 2, 2L, context = Long.ring)),
            )

//            vertices.constructConvexHullByGiftWrapping2()
        }
//        val expectedPolytopeConstruction = buildAbstractPolytopicConstruction(spaceDimension = 3u) {
//            val p000 = addVertex(Point(0, 0, 0L))
//            val p001 = addVertex(Point(0, 0, 1L))
//            val p002 = addVertex(Point(0, 0, 2L))
//            val p010 = addVertex(Point(0, 1, 0L))
//            val p011 = addVertex(Point(0, 1, 1L))
//            val p012 = addVertex(Point(0, 1, 2L))
//            val p020 = addVertex(Point(0, 2, 0L))
//            val p021 = addVertex(Point(0, 2, 1L))
//            val p022 = addVertex(Point(0, 2, 2L))
//            val p100 = addVertex(Point(1, 0, 0L))
//            val p101 = addVertex(Point(1, 0, 1L))
//            val p102 = addVertex(Point(1, 0, 2L))
//            val p110 = addVertex(Point(1, 1, 0L))
//            val p111 = addVertex(Point(1, 1, 1L))
//            val p112 = addVertex(Point(1, 1, 2L))
//            val p120 = addVertex(Point(1, 2, 0L))
//            val p121 = addVertex(Point(1, 2, 1L))
//            val p122 = addVertex(Point(1, 2, 2L))
//            val p200 = addVertex(Point(2, 0, 0L))
//            val p201 = addVertex(Point(2, 0, 1L))
//            val p202 = addVertex(Point(2, 0, 2L))
//            val p210 = addVertex(Point(2, 1, 0L))
//            val p211 = addVertex(Point(2, 1, 1L))
//            val p212 = addVertex(Point(2, 1, 2L))
//            val p220 = addVertex(Point(2, 2, 0L))
//            val p221 = addVertex(Point(2, 2, 1L))
//            val p222 = addVertex(Point(2, 2, 2L))
//
//            val fx0 = koneIterableSetOf(p000, p002, p020, p022).let { addPolytope(it, koneIterableListOf(it)) }
//            val fx1 = koneIterableSetOf(p200, p202, p220, p222).let { addPolytope(it, koneIterableListOf(it)) }
//            val fy0 = koneIterableSetOf(p000, p002, p200, p202).let { addPolytope(it, koneIterableListOf(it)) }
//            val fy1 = koneIterableSetOf(p020, p022, p220, p222).let { addPolytope(it, koneIterableListOf(it)) }
//            val fz0 = koneIterableSetOf(p000, p200, p020, p220).let { addPolytope(it, koneIterableListOf(it)) }
//            val fz1 = koneIterableSetOf(p002, p202, p022, p222).let { addPolytope(it, koneIterableListOf(it)) }
//
//            koneIterableSetOf(p000, p002, p020, p022, p200, p202, p220, p222).let {
//                addPolytope(it, koneIterableListOf(it, koneIterableSetOf(fx0, fx1, fy0, fy1, fz0, fz1)))
//            }
//        }
    }
}
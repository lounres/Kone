/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.koneMutableListOf
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.set.comparison.koneSetEquality
import dev.lounres.kone.collections.set.koneMutableSetOf
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.polytopes.MutablePolytopicConstruction
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.RemovablePolytope
import dev.lounres.kone.computationalGeometry.polytopes.RemovableVertex
import dev.lounres.kone.computationalGeometry.utils.sumOf
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1


// TODO: Fix migration to new API.

//// TODO: For now the algorithm assumes that result is a triangulation (and there are no 4 or more cocyclic points)
////   and that there are at least 2 triangles in the triangulation
//context(numberRing: A, _: EuclideanKategory<N>, inputPolytope: MutablePolytopicConstruction<N>)
//public fun <N, A> KoneIterable<RemovableVertex<N>>.constructDelaunayTriangulation(): KoneList<RemovablePolytope<N>> where A: Ring<N>, A: Order<N> {
//    val theDimension = inputPolytope.spaceDimension + 1u
//    val outerPolytopeContext = polytopeContext
//    buildAbstractPolytopicConstruction<N>(theDimension, numberContext = numberRing) {
//        val simplicesMapping =
//            koneMutableMapOf<AbstractPolytope, P>(keyContext = defaultEquality())
//
//        val newPoints = this@constructDelaunayTriangulation.map { oldVertex ->
//            val newVertex =
//                addVertex(Point(MDList1(theDimension) { if (it < theDimension - 1u) oldVertex.position.coordinates[it] else oldVertex.position.sumOf { c -> c * c } }))
//            simplicesMapping[newVertex] = oldVertex
//            newVertex
//        }
//
//        val convexHull: AbstractPolytope =
//            defaultHashing<AbstractPolytope>().run { newPoints.constructConvexHullByGiftWrapping() }
//        val necessarySimplices = convexHull.facesOfDimension(convexHull.dimension - 1u).filter { simplex ->
//            val flag = KoneSettableList(simplex.dimension + 2u) { simplex }
//            flag[simplex.dimension + 1u] = convexHull
//            for (dim in simplex.dimension - 1u downTo 0u) {
//                flag[dim] = flag[dim + 1u].facesOfDimension(dim).first()
//            }
//            val startPoint = (flag[0u] as AbstractVertex).position
//            val basis = KoneSettableList(
//                simplex.dimension + 1u,
//            ) { dim -> flag[dim + 1u].vertices.firstThat { it !in flag[dim].vertices }.position - startPoint }
//            val ortogonalizedBasis = basis.gramSchmidtOrthogonalization()
//            val lastBasisVector = ortogonalizedBasis.last()
//            !((lastBasisVector dot basis.last()).isPositive() xor lastBasisVector.coordinates[theDimension - 1u].isPositive())
//        }
//
//        for (simplex in necessarySimplices) {
//            for (dim in 1u .. simplex.dimension - 1u) for (face in simplex.facesOfDimension(dim)) {
//                simplicesMapping[face] = inputPolytope.addPolytope(
//                    face.vertices.mapTo<AbstractVertex, V, _>(
//                        koneMutableSetOf<V>(
//                            elementContext = outerPolytopeContext
//                        )
//                    ) {
//                        @Suppress("UNCHECKED_CAST")
//                        simplicesMapping[it] as V
//                    },
//                    face.faces.mapTo(
//                        koneMutableListOf()
//                    ) { dimFaces ->
//                        dimFaces.mapTo<AbstractPolytope, P, _>(
//                            koneMutableSetOf<P>(
//                                elementContext = outerPolytopeContext
//                            )
//                        ) { simplicesMapping[it] }
//                    }
//                )
//            }
//            simplicesMapping[simplex] = inputPolytope.addPolytope(
//                simplex.vertices.mapTo<AbstractVertex, V, _>(
//                    koneMutableSetOf<V>(
//                        elementContext = outerPolytopeContext
//                    )
//                ) {
//                    @Suppress("UNCHECKED_CAST")
//                    simplicesMapping[it] as V
//                },
//                koneSetEquality(outerPolytopeContext).invoke {
//                    simplex.faces.mapTo(
//                        koneMutableListOf()
//                    ) { dimFaces ->
//                        dimFaces.mapTo<AbstractPolytope, P, _>(
//                            koneMutableSetOf<P>(
//                                elementContext = outerPolytopeContext
//                            )
//                        ) { simplicesMapping[it] }
//                    }
//                }
//            )
//        }
//
//        return necessarySimplices.map { simplicesMapping[it] }
//    }
//}
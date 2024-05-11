/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.computationalGeometry.utils.sumOf
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1


// TODO: For now the algorithm assumes that result is a triangulation (and there are no 4 or more cocyclic points)
//   and that there are at least 2 triangles in the triangulation
context(A, EuclideanSpace<N>, MutablePolytopicConstruction<N, P, V>)
public fun <N, A, P, V: P> KoneIterableCollection<V>.constructDelaunayTriangulation(): KoneIterableList<P> where A: Ring<N>, A: Order<N> {
    val theDimension = spaceDimension + 1u
    val outerPolytopeContext = this@MutablePolytopicConstruction.polytopeContext
    buildAbstractPolytopicConstruction<N>(theDimension, numberContext = this@A) {
        val simplicesMapping = koneMutableMapOf<AbstractPolytope, P>(keyContext = defaultEquality(), valueContext = outerPolytopeContext)

        val newPoints = this@constructDelaunayTriangulation.map(elementContext = defaultEquality()) { oldVertex ->
            val newVertex = addVertex(Point(MDList1(theDimension) { if (it < theDimension - 1u) oldVertex.position.coordinates[it] else oldVertex.position.sumOf { c -> c * c } }))
            simplicesMapping[newVertex] = oldVertex
            newVertex
        }

        val convexHull: AbstractPolytope = defaultHashing<AbstractPolytope>().run { newPoints.constructConvexHullByGiftWrapping() }
        val necessarySimplices = convexHull.facesOfDimension(convexHull.dimension - 1u).filter { simplex ->
            val flag = KoneSettableIterableList(simplex.dimension + 2u) { simplex }
            flag[simplex.dimension + 1u] = convexHull
            for (dim in simplex.dimension-1u downTo 0u) {
                flag[dim] = flag[dim+1u].facesOfDimension(dim).first()
            }
            val startPoint = (flag[0u] as AbstractVertex).position
            val basis = KoneSettableIterableList(simplex.dimension + 1u, elementContext = vectorEquality(this@A)) { dim -> flag[dim+1u].vertices.first { it !in flag[dim].vertices }.position - startPoint }
            val ortogonalizedBasis = basis.gramSchmidtOrthogonalization()
            val lastBasisVector = ortogonalizedBasis.last()
            !((lastBasisVector dot basis.last()).isPositive() xor lastBasisVector.coordinates[theDimension-1u].isPositive())
        }

        for (simplex in necessarySimplices) {
            for (dim in 1u .. simplex.dimension - 1u) for (face in simplex.facesOfDimension(dim)) {
                simplicesMapping[face] = this@MutablePolytopicConstruction.addPolytope(
                    face.vertices.mapTo<AbstractVertex, V, _>(
                        koneMutableIterableSetOf<V>(
                            elementContext = outerPolytopeContext
                        )
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        simplicesMapping[it] as V
                    },
                    face.faces.mapTo(koneMutableIterableListOf(elementContext = koneIterableSetEquality(outerPolytopeContext))) { dimFaces ->
                        dimFaces.mapTo<AbstractPolytope, P, _>(
                            koneMutableIterableSetOf<P>(
                                elementContext = outerPolytopeContext
                            )
                        ) { simplicesMapping[it] }
                    }
                )
            }
            simplicesMapping[simplex] = this@MutablePolytopicConstruction.addPolytope(
                simplex.vertices.mapTo<AbstractVertex, V, _>(
                    koneMutableIterableSetOf<V>(
                        elementContext = outerPolytopeContext
                    )
                ) {
                    @Suppress("UNCHECKED_CAST")
                    simplicesMapping[it] as V
                },
                koneIterableSetEquality(outerPolytopeContext).invoke {
                    simplex.faces.mapTo(koneMutableIterableListOf(elementContext = koneIterableSetEquality(outerPolytopeContext))) { dimFaces ->
                        dimFaces.mapTo<AbstractPolytope, P, _>(
                            koneMutableIterableSetOf<P>(
                                elementContext = outerPolytopeContext
                            )
                        ) { simplicesMapping[it] }
                    }
                }
            )
        }

        return necessarySimplices.map(elementContext = outerPolytopeContext) { simplicesMapping[it] }
    }
}
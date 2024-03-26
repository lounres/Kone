/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.collections.common.utils.first
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.utils.*
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.utils.sumOf


// TODO: For now the algorithm assumes that result is a triangulation (and there are no 4 or more cocyclic points)
//   and that there are at least 2 triangles in the triangulation
context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, A, P, V, PE>, A, PE)
public fun <N, A, P, V: P, PE: Equality<P>> KoneContextualIterableCollection<V, PE>.constructDelaunayTriangulation(): KoneContextualIterableList<P, Equality<P>> where A: Ring<N>, A: Order<N> {
    val theDimension = spaceDimension + 1u
    buildAbstractPolytopicConstruction<N, A>(theDimension) {
        defaultHashing<AbstractPolytope>().run {
            val simplicesMapping = koneContextualMutableMapOf<AbstractPolytope, P>()

            val newPoints = this@constructDelaunayTriangulation.map { oldVertex ->
                val newVertex = addVertex(Point(ContextualMDList1(theDimension) { if (it < theDimension - 1u) oldVertex.coordinates.coordinates[it] else oldVertex.coordinates.coordinates.coefficients.sumOf { c -> c * c } }))
                simplicesMapping[newVertex] = oldVertex
                newVertex
            }

            val convexHull = defaultHashing<AbstractPolytope>().invoke { newPoints.constructConvexHullByGiftWrapping2() }
            val necessarySimplices = convexHull.facesOfDimension(convexHull.dimension - 1u).filter { simplex ->
                val flag = KoneContextualSettableIterableList(simplex.dimension + 2u) { simplex }
                flag[simplex.dimension + 1u] = convexHull
                for (dim in simplex.dimension-1u downTo 0u) {
                    flag[dim] = flag[dim+1u].facesOfDimension(dim).first()
                }
                val startPoint = (flag[0u] as AbstractVertex).coordinates
                val basis = KoneContextualSettableIterableList(simplex.dimension + 1u) { dim -> flag[dim+1u].vertices.first { it !in flag[dim].vertices }.coordinates - startPoint }
                val ortogonalizedBasis = basis.gramSchmidtOrtogonalization()
                val lastBasisVector = ortogonalizedBasis.last()
                !((lastBasisVector dot basis.last()).isPositive() xor lastBasisVector.coordinates[theDimension-1u].isPositive())
            }

            for (simplex in necessarySimplices) {
                for (dim in 1u .. simplex.dimension - 1u) for (face in simplex.facesOfDimension(dim)) {
                    simplicesMapping[face] = this@MutablePolytopicConstruction.addPolytope(
                        face.vertices.mapTo<AbstractVertex, V, PE, _>(koneContextualMutableIterableSetOf<V>()) { simplicesMapping[it] as V },
                        koneContextualIterableSetEquality(this@PE).invoke {
                            face.faces.mapTo(koneContextualMutableIterableListOf()) { dimFaces ->
                                dimFaces.mapTo<AbstractPolytope, P, PE, _>(koneContextualMutableIterableSetOf<P>()) { simplicesMapping[it] }
                            }
                        }
                    )
                }
                simplicesMapping[simplex] = this@MutablePolytopicConstruction.addPolytope(
                    simplex.vertices.mapTo<AbstractVertex, V, PE, _>(koneContextualMutableIterableSetOf<V>()) { simplicesMapping[it] as V },
                    koneContextualIterableSetEquality(this@PE).invoke {
                        simplex.faces.mapTo(koneContextualMutableIterableListOf()) { dimFaces ->
                            dimFaces.mapTo<AbstractPolytope, P, PE, _>(koneContextualMutableIterableSetOf<P>()) { simplicesMapping[it] }
                        }
                    }
                )
            }

            return necessarySimplices.map { simplicesMapping[it] }
        }
    }
}
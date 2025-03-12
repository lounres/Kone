/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.map.associateBy
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.set.koneMutableReifiedSetOf
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.*
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point3
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.*
import dev.lounres.kone.computationalGeometry.utils.sumOf
import dev.lounres.kone.multidimensionalCollections.MDList1


// TODO: For now the algorithm assumes that result is a triangulation (and there are no 4 or more cocyclic points)
//   and that there are at least 2 triangles in the triangulation
context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
public fun <
    Number,
    Polytope: PolytopicConstruction2Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction2Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction2<Number, Polytope, Vertex>.constructDelaunayTriangulation2(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    vertices: KoneIterable<Vertex>,
): KoneList<Polytope> {
    val paraboloidDimension = 3u
    
    val paraboloidPolytopicConstruction = AbstractPolytopicConstruction3<Number>()
    
    val simplicesMapping = koneMutableMapOf<AbstractPolytopicConstruction3Polytope<Number>, Polytope>(keyEquality = absoluteEquality(), keyHashing = defaultHashing())
    val verticesMapping = vertices.associateBy(
        keyEquality = absoluteEquality(),
        keyHashing = defaultHashing(),
    ) { oldVertex ->
        val newVertex = paraboloidPolytopicConstruction.addVertex(Point3(MDList1(paraboloidDimension) { if (it < paraboloidDimension - 1u) oldVertex.position.coordinates[it] else oldVertex.position.sumOf { c -> c * c } }))
        simplicesMapping[newVertex.asPolytope()] = oldVertex.asPolytope()
        newVertex
    }

    val convexHull: AbstractPolytopicConstruction3Polytope<Number> =
        paraboloidPolytopicConstruction.constructConvexHullByGiftWrapping3(
            vertexReification = Reification(),
            vertexEquality = absoluteEquality(),
            vertexHashing = defaultHashing(),
            vertexOrder = null,
            polytopeReification = Reification(),
            polytopeEquality = absoluteEquality(),
            polytopeHashing = defaultHashing(),
            polytopeOrder = null,
            vertices = verticesMapping.keysView
        )
    val necessarySimplices = convexHull.facesOfDimension(convexHull.dimension - 1u).filter { simplex ->
        val flag = KoneSettableList(simplex.dimension + 2u) { simplex }
        flag[simplex.dimension + 1u] = convexHull
        for (dim in simplex.dimension - 1u downTo 0u) {
            flag[dim] = flag[dim + 1u].facesOfDimension(dim).first()
        }
        val startPoint = (flag[0u].vertices.single()).position
        val basis = KoneSettableList(
            simplex.dimension + 1u,
        ) { dim -> flag[dim + 1u].vertices.firstThat { it !in flag[dim].vertices }.position - startPoint }
        val ortogonalizedBasis = basis.gramSchmidtOrthogonalization()
        val lastBasisVector = ortogonalizedBasis.last()
        !((lastBasisVector dot basis.last()).isPositive() xor lastBasisVector.coordinates[paraboloidDimension - 1u].isPositive())
    }

    for (simplex in necessarySimplices) {
        for (dim in 1u .. simplex.dimension - 1u) for (face in simplex.facesOfDimension(dim)) if (face !in simplicesMapping.keysView)
            simplicesMapping[face] = this.addPolytope(
                dim,
                face.vertices.mapTo(
                    koneMutableReifiedSetOf(
                        elementReification = vertexReification,
                        elementEquality = vertexEquality,
                        elementHashing = vertexHashing,
                        elementOrder = vertexOrder,
                    )
                ) {
                    verticesMapping[it]
                },
                face.faces.map { dimFaces ->
                    dimFaces.mapTo(
                        koneMutableReifiedSetOf(
                            elementReification = polytopeReification,
                            elementEquality = polytopeEquality,
                            elementHashing = polytopeHashing,
                            elementOrder = polytopeOrder,
                        )
                    ) { simplicesMapping[it] }
                }
            )
        simplicesMapping[simplex] = this.addPolytope(
            simplex.dimension,
            simplex.vertices.mapTo(
                koneMutableReifiedSetOf(
                    elementReification = vertexReification,
                    elementEquality = vertexEquality,
                    elementHashing = vertexHashing,
                    elementOrder = vertexOrder,
                )
            ) {
                verticesMapping[it]
            },
            simplex.faces.map { dimFaces ->
                dimFaces.mapTo(
                    koneMutableReifiedSetOf(
                        elementReification = polytopeReification,
                        elementEquality = polytopeEquality,
                        elementHashing = polytopeHashing,
                        elementOrder = polytopeOrder,
                    )
                ) { simplicesMapping[it] }
            }
        )
    }

    return necessarySimplices.map { simplicesMapping[it] }
}
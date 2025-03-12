/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.list.koneListOf
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.set.comparison.koneSetEquality
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.compareWith
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.computationalGeometry.polytopes.ExtendablePolytopicConstruction
import dev.lounres.kone.computationalGeometry.polytopes.ExtendablePolytopicConstruction3
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction3Polytope
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction3Vertex
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstructionPolytope
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstructionVertex
import dev.lounres.kone.computationalGeometry.utils.any
import dev.lounres.kone.context
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.load
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.scope
import dev.lounres.kone.util.suppliedTypes.SuppliedType


// TODO: There is a problem: some mandatory contexts are used as implicit contexts taken from `KoneContextRegistry`.

context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
internal fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> giftWrapping3Atom(
    startPoint: Point<Number>,
    normalGiftWrapping3Vector: Vector<Number>,
    tangentGiftWrapping3Vector: Vector<Number>,
    otherPoints: KoneIterable<Vertex>,
): KoneList<Vertex> {
    data class TangentFraction(val numerator: Number, val denominator: Number)
    return otherPoints.minListWithBy(
        { left, right -> (left.numerator * right.denominator) compareWith (right.numerator * left.denominator) }
    ) {
        val v = it.position - startPoint
        TangentFraction(v dot tangentGiftWrapping3Vector, v dot normalGiftWrapping3Vector)
    }
}

// TODO: Docs
/*
 * Строит выпуклую оболочку точек в подпространстве.
 *
 * Принимает размерность подпространства, фасету искомой выпуклой оболочки и другие точки в подпространстве, не лежащие в этой фасете.
 */
context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
internal fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction3<Number, Polytope, Vertex>.giftWrapping3Increment(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    subspaceDimension: UInt,
    startFacet: Polytope,
    otherPoints: KoneIterable<Vertex>,
    computedFacesRegistry: KoneMutableMap<KoneSet<Vertex>, Polytope>,
): Polytope {
    require(subspaceDimension >= 1u) { "Can't define gift wrapping increment for subspace of dimension 0" }
    val allVertices = otherPoints.toKoneMutableReifiedSet(elementReification = vertexReification, elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder)
    allVertices.addAllFrom(startFacet.vertices)

    computedFacesRegistry.getOrNull(allVertices)?.let { return it }

    if (subspaceDimension == 1u) {
        val startKoneVertex = startFacet.vertices.single()
        val startPoint = startKoneVertex.position
        val endVertex = otherPoints.maxBy { // TODO: Может быть это можно оптимизировать
            val radiusVector = it.position - startPoint
            radiusVector dot radiusVector
        }
        val vertices = koneReifiedSetOf(startKoneVertex, endVertex, elementReification = vertexReification, elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder)
        return addPolytope(
            subspaceDimension,
            vertices,
            koneListOf(vertices.mapTo(koneMutableReifiedSetOf(elementReification = polytopeReification, elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)) { it.asPolytope() }),
        ).also { computedFacesRegistry[vertices] = it }
    }

    val restConvexHullFaces = KoneList(subspaceDimension) {
        koneMutableReifiedSetOf(elementReification = polytopeReification, elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)
    }

    for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(startFacet.facesOfDimension(dim))
    restConvexHullFaces[subspaceDimension-1u].add(startFacet)

    val facetsToProcess: KoneDeque<Polytope> = KoneListBackedDeque(KoneArrayResizableLinkedList())
    val subfacetsToProcess = koneMutableSetOf(elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)

    facetsToProcess.addLast(startFacet)
    subfacetsToProcess.addAllFrom(startFacet.facesOfDimension(subspaceDimension - 2u))

    while (facetsToProcess.isNotEmpty()) {
        val facet = facetsToProcess.popFirst()
        for (subfacet in facet.facesOfDimension(subspaceDimension - 2u)) if (subfacet in subfacetsToProcess) {
            val startPoint: Point<Number>
            val normalGiftWrapping3Vector: Vector<Number>
            val tangentGiftWrapping3Vector: Vector<Number>
            scope {
                val facetFlag = KoneSettableList(subspaceDimension) { facet }
                facetFlag[subspaceDimension - 2u] = subfacet
                if (subspaceDimension >= 3u) for (dim in subspaceDimension - 3u downTo 0u) {
                    facetFlag[dim] = facetFlag[dim+1u].facesOfDimension(dim).first()
                }
                startPoint = facetFlag[0u].vertices.single().position
                val basis = KoneList(subspaceDimension) { index ->
                    if (index < subspaceDimension - 1u) facetFlag[index + 1u].vertices.firstThat { it !in facetFlag[index].vertices }.position - startPoint
                    else allVertices.firstThat { it !in facet.vertices }.position - startPoint
                }
                val orthogonalizedBasis = basis.gramSchmidtOrthogonalization()
                tangentGiftWrapping3Vector = orthogonalizedBasis[subspaceDimension-2u]
                normalGiftWrapping3Vector = orthogonalizedBasis[subspaceDimension-1u]
            }

            val newVertices: KoneList<Vertex> = giftWrapping3Atom(
                startPoint = startPoint,
                normalGiftWrapping3Vector = normalGiftWrapping3Vector,
                tangentGiftWrapping3Vector = tangentGiftWrapping3Vector,
                otherPoints = buildKoneSet(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder) {
                    addAllFrom(allVertices)
                    removeAllFrom(subfacet.vertices)
                }
            )

            val newFacet: Polytope = giftWrapping3Increment(
                vertexReification = vertexReification,
                vertexEquality = vertexEquality,
                vertexHashing = vertexHashing,
                vertexOrder = vertexOrder,
                polytopeReification = polytopeReification,
                polytopeEquality = polytopeEquality,
                polytopeHashing = polytopeHashing,
                polytopeOrder = polytopeOrder,
                subspaceDimension = subspaceDimension - 1u,
                startFacet = subfacet,
                otherPoints = newVertices,
                computedFacesRegistry = computedFacesRegistry,
            )

            allVertices.removeAllThat { context(vertexEquality) { it in newVertices } && it !in newFacet.vertices }

            for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(newFacet.facesOfDimension(dim))
            restConvexHullFaces[subspaceDimension-1u].add(newFacet)

            facetsToProcess.addLast(newFacet)
            for (newSubfacet in newFacet.facesOfDimension(subspaceDimension - 2u))
                if (newSubfacet in subfacetsToProcess) subfacetsToProcess.remove(newSubfacet)
                else subfacetsToProcess.add(newSubfacet)
        }
    }

    check(subfacetsToProcess.isEmpty()) { "For some reason some subfacets are left after \"gift wrapping increment\" procedure" }

    // TODO: Separate polytope creation and polytope finding
    return addPolytope(subspaceDimension, allVertices, restConvexHullFaces).also { computedFacesRegistry[allVertices] = it }
}

internal data class Wrapping3Result<Number, Polytope, Vertex>(
    var polytope: Polytope,
    val computedFacesRegistry: KoneMutableMap<KoneSet<Vertex>, Polytope>,
    val startPoint: Point<Number>,
    val orthogonalizationState: GramSchmidtOrthogonalizationIntermediateState<Number>,
)

context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
internal fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction3<Number, Polytope, Vertex>.giftWrapping3Extension(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    subspaceDimension: UInt,
    wrappingResult: Wrapping3Result<Number, Polytope, Vertex>,
    normalVector: Vector<Number>,
    otherPoints: KoneIterable<Vertex>,
) {
    if (otherPoints.isEmpty()) return
    require(subspaceDimension >= 1u) { TODO("Error message is not specified") }

    val otherPoints = otherPoints.toKoneMutableSet(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder)
    var currentNormalVector = normalVector

    while (otherPoints.isNotEmpty()) {
        if (wrappingResult.orthogonalizationState.orthogonalizedBasis.size == subspaceDimension - 1u) {
            val resultingPolytope = giftWrapping3Increment(
                vertexReification = vertexReification,
                vertexEquality = vertexEquality,
                vertexHashing = vertexHashing,
                vertexOrder = vertexOrder,
                polytopeReification = polytopeReification,
                polytopeEquality = polytopeEquality,
                polytopeHashing = polytopeHashing,
                polytopeOrder = polytopeOrder,
                subspaceDimension = subspaceDimension,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
            )
            wrappingResult.polytope = resultingPolytope
            val newVector = otherPoints.first().position - wrappingResult.startPoint
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(newVector)
            return
        }

        val extendedOrthogonalizationState = wrappingResult.orthogonalizationState.clone(subspaceDimension)
        extendedOrthogonalizationState.gramSchmidtOrthogonalizationExtension(currentNormalVector)

        val tangentVector = otherPoints.firstOfThatOrNull({
            extendedOrthogonalizationState.gramSchmidtOrthogonalizationUsage(it.position - wrappingResult.startPoint)
        }) { it.any { it.isNotZero() } } ?: scope {
            val resultingPolytope = giftWrapping3Increment(
                vertexReification = vertexReification,
                vertexEquality = vertexEquality,
                vertexHashing = vertexHashing,
                vertexOrder = vertexOrder,
                polytopeReification = polytopeReification,
                polytopeEquality = polytopeEquality,
                polytopeHashing = polytopeHashing,
                polytopeOrder = polytopeOrder,
                subspaceDimension = wrappingResult.orthogonalizationState.orthogonalizedBasis.size + 1u,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
            )
            wrappingResult.polytope = resultingPolytope
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(otherPoints.first().position - wrappingResult.startPoint)
            return
        }

        val nextPoints = giftWrapping3Atom(
            startPoint = wrappingResult.startPoint,
            normalGiftWrapping3Vector = currentNormalVector,
            tangentGiftWrapping3Vector = tangentVector,
            otherPoints = otherPoints
        )

        giftWrapping3Extension(
            vertexReification = vertexReification,
            vertexEquality = vertexEquality,
            vertexHashing = vertexHashing,
            vertexOrder = vertexOrder,
            polytopeReification = polytopeReification,
            polytopeEquality = polytopeEquality,
            polytopeHashing = polytopeHashing,
            polytopeOrder = polytopeOrder,
            subspaceDimension = subspaceDimension - 1u,
            wrappingResult = wrappingResult,
            normalVector = currentNormalVector,
            otherPoints = nextPoints,
        )

        otherPoints.removeAllFrom(nextPoints)
        currentNormalVector = scope {
            val vectorOfTheExtensionDirection = nextPoints.first().position - wrappingResult.startPoint
            tangentVector * (currentNormalVector dot vectorOfTheExtensionDirection) - currentNormalVector * (tangentVector dot vectorOfTheExtensionDirection)
        }
    }
}

context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
internal fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction3<Number, Polytope, Vertex>.giftWrapping3Full(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    subspaceDimension: UInt,
    points: KoneIterable<Vertex>,
): Wrapping3Result<Number, Polytope, Vertex> {
    require(points.isNotEmpty()) { TODO("Error message is not specified") }
    if (subspaceDimension == 0u) {
        val theOnlyVertex = points.single()
        return Wrapping3Result(
            polytope = theOnlyVertex.asPolytope(),
            computedFacesRegistry = koneMutableMapOf(
                keyEquality = koneSetEquality(vertexEquality)
            ),
            startPoint = theOnlyVertex.position,
            orthogonalizationState = GramSchmidtOrthogonalizationIntermediateState(
                orthogonalizedBasis = KoneArrayFixedCapacityList(3u),
                product = one,
                exclusiveProducts = KoneArrayFixedCapacityList(3u)
            )
        )
    }

    val startPoints = points.minListBy { it.position.coordinates[subspaceDimension - 1u] }
    val wrappingResult = giftWrapping3Full(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = vertexHashing,
        vertexOrder = vertexOrder,
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = polytopeHashing,
        polytopeOrder = polytopeOrder,
        subspaceDimension = subspaceDimension - 1u,
        points = startPoints,
    )

    giftWrapping3Extension(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = vertexHashing,
        vertexOrder = vertexOrder,
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = polytopeHashing,
        polytopeOrder = polytopeOrder,
        subspaceDimension = subspaceDimension,
        wrappingResult = wrappingResult,
        normalVector = Vector(ColumnVector(3u) { if (it == subspaceDimension - 1u) one else zero }),
        otherPoints = points.toKoneMutableSet(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder).apply { removeAllFrom(startPoints) },
    )

    return wrappingResult
}

context(_: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
public fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction3<Number, Polytope, Vertex>.constructConvexHullByGiftWrapping3(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    vertices: KoneIterable<Vertex>,
): Polytope {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    return giftWrapping3Full(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = vertexHashing,
        vertexOrder = vertexOrder,
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = polytopeHashing,
        polytopeOrder = polytopeOrder,
        subspaceDimension = 3u,
        points = vertices,
    ).polytope
}

context(koneContextRegistry: KoneContextRegistry, _: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
public fun <
    Number,
    Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction3<Number, Polytope, Vertex>.constructConvexHullByGiftWrapping3(
    vertexSuppliedType: SuppliedType<Vertex>,
    polytopeSuppliedType: SuppliedType<Polytope>,
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    vertices: KoneIterable<Vertex>,
): Polytope {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    return giftWrapping3Full(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = koneContextRegistry.load(Hashing.Key(vertexSuppliedType)),
        vertexOrder = koneContextRegistry.load(Order.Key(vertexSuppliedType)),
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = koneContextRegistry.load(Hashing.Key(polytopeSuppliedType)),
        polytopeOrder = koneContextRegistry.load(Order.Key(polytopeSuppliedType)),
        subspaceDimension = 3u,
        points = vertices,
    ).polytope
}

/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.set.relations.equality
import dev.lounres.kone.collections.set.removeAllFrom
import dev.lounres.kone.collections.set.toKoneMutableReifiedSet
import dev.lounres.kone.collections.set.toKoneMutableSet
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstOfThatOrNull
import dev.lounres.kone.collections.utils.firstThat
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.maxBy
import dev.lounres.kone.collections.utils.minListBy
import dev.lounres.kone.collections.utils.minListWithBy
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.ExtendablePolytopicConstruction
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.scope


// TODO: There is a problem: some mandatory contexts are used as implicit contexts taken from `KoneContextRegistry`.

context(ring: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> giftWrappingAtom(
    startPoint: Point,
    normalGiftWrappingVector: Vector,
    tangentGiftWrappingVector: Vector,
    otherPoints: KoneIterable<Vertex>,
): KoneList<Vertex> {
    data class TangentFraction(val numerator: Number, val denominator: Number)
    return otherPoints.minListWithBy(
        { left, right -> (left.numerator * right.denominator) compareWith (right.numerator * left.denominator) }
    ) {
        val v = it.position - startPoint
        TangentFraction(v dot tangentGiftWrappingVector, v dot normalGiftWrappingVector)
    }
}

// TODO: Docs
/*
 * Строит выпуклую оболочку точек в подпространстве.
 *
 * Принимает размерность подпространства, фасету искомой выпуклой оболочки и другие точки в подпространстве, не лежащие в этой фасете.
 */
context(_: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Point, Polytope, Vertex>.giftWrappingIncrement(
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
        val vertices = KoneReifiedSet.of(startKoneVertex, endVertex, elementReification = vertexReification, elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder)
        return addPolytope(
            subspaceDimension,
            vertices,
            KoneList.of(vertices.mapTo(KoneMutableReifiedSet.of(elementReification = polytopeReification, elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)) { it.asPolytope() }),
        ).also { computedFacesRegistry[vertices] = it }
    }

    val restConvexHullFaces = KoneList(subspaceDimension) {
        KoneMutableReifiedSet.of(elementReification = polytopeReification, elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)
    }

    for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(startFacet.facesOfDimension(dim))
    restConvexHullFaces[subspaceDimension-1u].add(startFacet)

    val facetsToProcess: KoneDeque<Polytope> = KoneListBackedDeque(KoneArrayResizableLinkedList())
    val subfacetsToProcess = KoneMutableSet.of(elementEquality = polytopeEquality, elementHashing = polytopeHashing, elementOrder = polytopeOrder)

    facetsToProcess.addLast(startFacet)
    subfacetsToProcess.addAllFrom(startFacet.facesOfDimension(subspaceDimension - 2u))

    while (facetsToProcess.isNotEmpty()) {
        val facet = facetsToProcess.popFirst()
        for (subfacet in facet.facesOfDimension(subspaceDimension - 2u)) if (subfacet in subfacetsToProcess) {
            val startPoint: Point
            val normalGiftWrappingVector: Vector
            val tangentGiftWrappingVector: Vector
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
                tangentGiftWrappingVector = orthogonalizedBasis[subspaceDimension-2u]
                normalGiftWrappingVector = orthogonalizedBasis[subspaceDimension-1u]
            }

            val newVertices: KoneList<Vertex> = giftWrappingAtom<Number, Vector, Point, Polytope, Vertex>(
                startPoint = startPoint,
                normalGiftWrappingVector = normalGiftWrappingVector,
                tangentGiftWrappingVector = tangentGiftWrappingVector,
                otherPoints = KoneSet.build(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder) {
                    +allVertices
                    -subfacet.vertices
                }
            )

            val newFacet: Polytope = giftWrappingIncrement(
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

            allVertices.removeAllThat { vertexEquality { it in newVertices } && it !in newFacet.vertices }

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

internal data class WrappingResult<Number, Vector, Point, Polytope, Vertex>(
    var polytope: Polytope,
    val computedFacesRegistry: KoneMutableMap<KoneSet<Vertex>, Polytope>,
    val startPoint: Point,
    val orthogonalizationState: GramSchmidtOrthogonalizationIntermediateState<Number, Vector>,
)

context(_: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Point, Polytope, Vertex>.giftWrappingExtension(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    subspaceDimension: UInt,
    wrappingResult: WrappingResult<Number, Vector, Point, Polytope, Vertex>,
    normalVector: Vector,
    otherPoints: KoneIterable<Vertex>,
) {
    if (otherPoints.isEmpty()) return
    require(subspaceDimension >= 1u) { TODO("Error message is not specified") }

    val otherPoints = otherPoints.toKoneMutableSet(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder)
    var currentNormalVector = normalVector

    while (otherPoints.isNotEmpty()) {
        if (wrappingResult.orthogonalizationState.orthogonalizedBasis.size == subspaceDimension - 1u) {
            val resultingPolytope = giftWrappingIncrement(
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
        }) { it.isNotZero() } ?: scope {
            val resultingPolytope = giftWrappingIncrement(
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

        val nextPoints = giftWrappingAtom(
            startPoint = wrappingResult.startPoint,
            normalGiftWrappingVector = currentNormalVector,
            tangentGiftWrappingVector = tangentVector,
            otherPoints = otherPoints
        )

        giftWrappingExtension(
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

context(ring: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Point, Polytope, Vertex>.giftWrappingFull(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    subspaceDimension: UInt,
    points: KoneIterable<Vertex>,
): WrappingResult<Number, Vector, Point, Polytope, Vertex> {
    require(points.isNotEmpty()) { TODO("Error message is not specified") }
    if (subspaceDimension == 0u) {
        val theOnlyVertex = points.single()
        return WrappingResult(
            polytope = theOnlyVertex.asPolytope(),
            computedFacesRegistry = KoneMutableMap.of(
                keyEquality = KoneSet.equality(vertexEquality)
            ),
            startPoint = theOnlyVertex.position,
            orthogonalizationState = GramSchmidtOrthogonalizationIntermediateState(
                orthogonalizedBasis = KoneArrayFixedCapacityList(spaceDimension),
                product = ring.one,
                exclusiveProducts = KoneArrayFixedCapacityList(spaceDimension)
            )
        )
    }

    val somePoint = points.first().position
    val startPoints = points.minListBy { basis.decompose(it.position - somePoint)[subspaceDimension - 1u] }
    val wrappingResult = giftWrappingFull(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = vertexHashing,
        vertexOrder = vertexOrder,
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = polytopeHashing,
        polytopeOrder = polytopeOrder,
        basis = basis,
        subspaceDimension = subspaceDimension - 1u,
        points = startPoints,
    )

    giftWrappingExtension(
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
        normalVector = basis[subspaceDimension - 1u],
        otherPoints = points.toKoneMutableSet(elementEquality = vertexEquality, elementHashing = vertexHashing, elementOrder = vertexOrder).apply { removeAllFrom(startPoints) },
    )

    return wrappingResult
}

context(_: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Point, Polytope, Vertex>.constructConvexHullByGiftWrapping(
    vertexReification: Reification<Vertex>,
    vertexEquality: Equality<Vertex>,
    vertexHashing: Hashing<Vertex>?,
    vertexOrder: Order<Vertex>?,
    polytopeReification: Reification<Polytope>,
    polytopeEquality: Equality<Polytope>,
    polytopeHashing: Hashing<Polytope>?,
    polytopeOrder: Order<Polytope>?,
    vertices: KoneIterable<Vertex>,
    basis: VectorSpaceBasis.Finite<Number, Vector>,
): Polytope {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    return giftWrappingFull(
        vertexReification = vertexReification,
        vertexEquality = vertexEquality,
        vertexHashing = vertexHashing,
        vertexOrder = vertexOrder,
        polytopeReification = polytopeReification,
        polytopeEquality = polytopeEquality,
        polytopeHashing = polytopeHashing,
        polytopeOrder = polytopeOrder,
        basis = basis,
        subspaceDimension = spaceDimension,
        points = vertices,
    ).polytope
}

//context(koneContextRegistry: KoneContextRegistry, _: Ring<Number>, _: Order<Number>, _: EuclideanKategory<Number>)
//public fun <
//    Number,
//    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
//    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
//> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.constructConvexHullByGiftWrapping(
//    vertexSuppliedType: SuppliedType,
//    polytopeSuppliedType: SuppliedType,
//    vertices: KoneIterable<Vertex>,
//): Polytope {
//    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
//    return giftWrappingFull(
//        vertexReification = koneContextRegistry[Reification.Key<Vertex>(vertexSuppliedType)],
//        vertexEquality = koneContextRegistry[Equality.Key<Vertex>(vertexSuppliedType)],
//        vertexHashing = koneContextRegistry.getOrNull(Hashing.Key<Vertex>(vertexSuppliedType)),
//        vertexOrder = koneContextRegistry.getOrNull(Order.Key<Vertex>(vertexSuppliedType)),
//        polytopeReification = koneContextRegistry[Reification.Key<Polytope>(polytopeSuppliedType)],
//        polytopeEquality = koneContextRegistry[Equality.Key<Polytope>(polytopeSuppliedType)],
//        polytopeHashing = koneContextRegistry.getOrNull(Hashing.Key<Polytope>(polytopeSuppliedType)),
//        polytopeOrder = koneContextRegistry.getOrNull(Order.Key<Polytope>(polytopeSuppliedType)),
//        subspaceDimension = spaceDimension,
//        points = vertices,
//    ).polytope
//}

// TODO: Finish migration to new API.

///**
// * See [here](https://en.wikipedia.org/wiki/Graham_scan) for more.
// */
//context(EuclideanSpace<N, A>)
//public fun <N, A> Collection<Point2<N>>.convexHullByGrahamScan(): List<Point2<N>> where A: Ring<N>, A: Order<N> {
//    when (size) {
//        0 -> return emptyList()
//        1 -> return toList()
//    }
//
//
//    val centralPoint = this.minWith(lexicographic2DComparator)
//    val points = this.toMutableList()
//    points -= centralPoint
//    points.sortWith(
//        Comparator<Point2<N>> { p1, p2 -> numberRing { (p1 - centralPoint) cross (p2 - centralPoint) compareTo zero } }
//            .then(lexicographic2DComparator)
//    )
//
//    val iterator = points.iterator()
//    val result = mutableListOf(centralPoint, iterator.next())
//    for (nextPoint in iterator) {
//        while (true) {
//            val v1 = nextPoint - result[result.lastIndex]
//            val v2 = result[result.lastIndex] - result[result.lastIndex - 1]
//            val crossProduct = v1 cross v2
//            if (numberRing { crossProduct >= zero }) break
//            result.removeAt(result.lastIndex)
//        }
//        result += nextPoint
//    }
//    return result
//}
//
////context(R)
////public fun <C, R> Collection<Point<C>>.convexHullByDivideAndConquer(): List<Point<C>> where R : Ring<C>, R: Order<C> {
////    when (size) {
////        0 -> return emptyList()
////        1, 2 -> return this.toList()
////    }
////
////    val points = this.sortedWith(lexicographicComparator)
////    val upperHull = points.upperConvexHullByDivideAndConquer()
////    val lowerHull = points.asReversed().upperConvexHullByDivideAndConquer()
////    return upperHull + lowerHull
////}
////
////context(R)
////internal fun <C, R> List<Point<C>>.upperConvexHullByDivideAndConquer(): List<Point<C>> where R : Ring<C>, R: Order<C> {
////    when (size) {
////        0 -> return emptyList()
////        1, 2 -> return this
////    }
////
////    val leftUpperHull: List<Point<C>> = this.subList(0, size / 2).upperConvexHullByDivideAndConquer()
////    val rightUpperHull: List<Point<C>> = this.subList(size / 2, size).upperConvexHullByDivideAndConquer()
////    var
////}
//
///**
// * See [here](https://en.wikipedia.org/wiki/Quickhull) for more.
// */
//context(EuclideanSpace<N, A>)
//public fun <N, A> Collection<Point2<N>>.convexHullByQuickhull(): List<Point2<N>> where A: Ring<N>, A: Order<N> {
//    when (size) {
//        0 -> return emptyList()
//        1 -> return toList()
//    }
//
//    val leftPoint = this.minWith(lexicographic2DComparator)
//    val rightPoint = this.maxWith(lexicographic2DComparator)
//    val points = buildList {
//        addAll(this@convexHullByQuickhull)
//        remove(leftPoint)
//        remove(rightPoint)
//    }
//    val v = rightPoint - leftPoint
//    return buildList {
//        add(leftPoint)
//        addAll(convexHullByQuickhullInternalLogic(leftPoint, rightPoint, points.filter { numberRing { v cross (it - leftPoint) >= zero } }))
//        add(rightPoint)
//        addAll(convexHullByQuickhullInternalLogic(rightPoint, leftPoint, points.filter { numberRing { v cross (it - leftPoint) <= zero } }))
//    }
//}
//
///**
// * See [here](https://en.wikipedia.org/wiki/Quickhull) for more.
// */
//context(EuclideanSpace<N, A>)
//internal fun <N, A> convexHullByQuickhullInternalLogic(leftPoint: Point2<N>, rightPoint: Point2<N>, points: Collection<Point2<N>>): List<Point2<N>> where A: Ring<N>, A: Order<N> {
//    val v = rightPoint - leftPoint
//    if (points.none { numberRing { v cross (it - rightPoint) ge zero } }) return points.toList()
//    val nextPoint = points.maxWith(numberRing { compareByOrdered({ v cross (it - rightPoint) }) })
//    val newPoints = points - nextPoint
//
//    val leftV = nextPoint - leftPoint
//    val rightV = rightPoint - nextPoint
//    return buildList {
//        addAll(convexHullByQuickhullInternalLogic(leftPoint, nextPoint, newPoints.filter { numberRing { leftV cross (it - leftPoint) >= zero } }))
//        add(nextPoint)
//        addAll(convexHullByQuickhullInternalLogic(nextPoint, rightPoint, newPoints.filter { numberRing { rightV cross (it - nextPoint) >= zero } }))
//    }
//}
//
///**
// * See [here](https://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain) for more.
// */
//context(EuclideanSpace<N, A>)
//public fun <N, A> Collection<Point2<N>>.convexHullByMonotoneChain(): List<Point2<N>> where A: Ring<N>, A: Order<N> {
//    when (size) {
//        0 -> return emptyList()
//        1, 2 -> return toList()
//    }
//
//    val points = this.sortedWith(lexicographic2DComparator)
//
//    fun Iterator<Point2<N>>.generateHalfHull(): List<Point2<N>> {
//        val halfHull = mutableListOf(next(), next())
//        for (p in this) {
//            while (halfHull.size >= 2) {
//                val last = halfHull.last()
//                val beforeLast = halfHull[halfHull.lastIndex - 1]
//                if (numberRing { (p - last) cross (last - beforeLast) >= zero }) break
//                halfHull.removeAt(halfHull.lastIndex)
//            }
//            halfHull += p
//        }
//        return halfHull
//    }
//
//    val upperHull = points.iterator().generateHalfHull()
//    val lowerHull = points.asReversed().iterator().generateHalfHull()
//
//    return upperHull.subList(0, upperHull.lastIndex) + lowerHull.subList(0, lowerHull.lastIndex)
//}
//
////public fun Collection<Point>.aklToussaintHeuristic(): Collection<Point> {
////    val leftPoint = this.minWith(compareBy({ it.x }, { it.y }))
////    val rightPoint = this.maxWith(compareBy({ it.x }, { it.y }))
////    val topPoint = this.minWith(compareBy({ it.x }, { -it.y }))
////    val bottomPoint = this.maxWith(compareBy({ it.x }, { -it.y }))
////
////    return this.filter { !it.inTriangle(leftPoint, topPoint, rightPoint) && !it.inTriangle(leftPoint, bottomPoint, rightPoint) }
////}
//
//context(EuclideanSpace<N, A>)
//public fun <N, A> MutableSetHookable<Point2<N>>.convexHullBy(): ListHookable<Point2<N>> where A: Ring<N>, A: Order<N> {
//    TODO()
//}
//
//context(EuclideanSpace<N, A>)
//internal fun <N, A> ExtendableSetHookable<Point2<N>>.upperConvexHullBySweepingLine(): UpdateHookable<KoneList<Point2<N>>> where A: Ring<N>, A: Order<N> =
//    UpdateHooker(KoneResizableArrayList<Point2<N>>()).also {
//        var outputList by it
//        val upperHull = KoneResizableArrayList<Point2<N>>()
//
//        fun processPoint(point: Point2<N>) {
//            if (upperHull.size <= 1u) {
//                upperHull.add(point)
//                return
//            }
//            while (outputList.size > 1u) {
//                val last = outputList[outputList.size - 1u]
//                val preLast = outputList[outputList.size - 2u]
//                if (numberRing { ((point - last) cross (last - preLast)) geq zero }) break
//                outputList.removeAt(outputList.size - 1u)
//            }
//        }
//
//        hookUp(
//            ResponseBeforeAction { _, action ->
//                when(action) {
//                    is KoneSetAction.Add -> {
//                        processPoint(action.element)
//                    }
//                    is KoneSetAction.AddAll -> {
//                        for (point in action.elements) processPoint(point)
//                    }
//                }
//                outputList = upperHull
//            }
//        )
//    }

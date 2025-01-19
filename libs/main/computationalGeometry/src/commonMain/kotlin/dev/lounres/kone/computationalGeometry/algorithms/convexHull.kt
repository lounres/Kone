/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.list.koneListOf
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.buildKoneSet
import dev.lounres.kone.collections.set.comparison.koneSetEquality
import dev.lounres.kone.collections.set.koneMutableReifiedSetOf
import dev.lounres.kone.collections.set.koneMutableSetOf
import dev.lounres.kone.collections.set.koneReifiedSetOf
import dev.lounres.kone.collections.set.removeAllFrom
import dev.lounres.kone.collections.set.toKoneMutableSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.compareWith
import dev.lounres.kone.comparison.defaultReifiedEquality
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.ExtendablePolytopicConstruction
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstructionPolytope
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstructionVertex
import dev.lounres.kone.computationalGeometry.times
import dev.lounres.kone.computationalGeometry.utils.any
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.scope


context(_: NumberContext, _: EuclideanKategory<Number>)
internal fun <
    Number,
    NumberContext,
    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> giftWrappingAtom(
    startPoint: Point<Number>,
    normalGiftWrappingVector: Vector<Number>,
    tangentGiftWrappingVector: Vector<Number>,
    otherPoints: KoneIterable<Vertex>,
): KoneList<Vertex> where NumberContext: Ring<Number>, NumberContext: Order<Number> {
    data class TangentFraction(val numerator: Number, val denominator: Number)
    return otherPoints.minListWithBy(
        { left, right -> (left.numerator * right.denominator) compareWith (right.numerator * left.denominator) }
    ) {
        val v = it.position - startPoint
        TangentFraction(v dot tangentGiftWrappingVector, v dot normalGiftWrappingVector)
    }
}

// TODO: Docs
/**
 * Строит выпуклую оболочку точек в подпространстве.
 *
 * Принимает размерность подпространства, фасету искомой выпуклой оболочки и другие точки в подпространстве, не лежащие в этой фасете.
 */
context(_: NumberContext, _: EuclideanKategory<Number>)
internal fun <
    Number,
    NumberContext,
    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.giftWrappingIncrement(
    subspaceDimension: UInt,
    startFacet: Polytope,
    otherPoints: KoneIterable<Vertex>,
    computedFacesRegistry: KoneMutableMap<KoneSet<Vertex>, Polytope>,
    vertexContext: ReifiedEquality<Vertex>,
    polytopeContext: ReifiedEquality<Polytope>,
): Polytope where NumberContext: Ring<Number>, NumberContext: Order<Number> {
    require(subspaceDimension >= 1u) { "Can't define gift wrapping increment for subspace of dimension 0" }
    val allVertices = otherPoints.toKoneMutableSet(elementContext = absoluteEquality()).apply { addAllFrom(startFacet.vertices) }

    computedFacesRegistry.getOrNull(allVertices)?.let { return it }

    if (subspaceDimension == 1u) {
        val startKoneVertex = startFacet.vertices.single()
        val startPoint = startKoneVertex.position
        val endVertex = otherPoints.maxBy { // TODO: Может быть это можно оптимизировать
            val radiusVector = it.position - startPoint
            radiusVector dot radiusVector
        }
        val vertices = koneReifiedSetOf(startKoneVertex, endVertex, elementContext = vertexContext)
        return addPolytope(
            vertices,
            koneListOf(vertices.mapTo(koneMutableReifiedSetOf(polytopeContext)) { it.asPolytope() }),
        ).also { computedFacesRegistry[vertices] = it }
    }

    val convexHullVertices = koneMutableReifiedSetOf<Vertex>(elementContext = vertexContext)
    val restConvexHullFaces = KoneList(subspaceDimension) { index ->
        if (index == 0u) convexHullVertices.mapTo(koneMutableReifiedSetOf(polytopeContext)) { it.asPolytope() }
        else koneMutableReifiedSetOf(elementContext = polytopeContext)
    }

    for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(startFacet.facesOfDimension(dim))
    restConvexHullFaces[subspaceDimension-1u].add(startFacet)

    val facetsToProcess: KoneDeque<Polytope> = KoneArrayResizableLinkedList()
    val subfacetsToProcess = koneMutableSetOf<Polytope>(elementContext = polytopeContext)

    facetsToProcess.addLast(startFacet)
    subfacetsToProcess.addAllFrom(startFacet.facesOfDimension(subspaceDimension - 2u))

    while (facetsToProcess.isNotEmpty()) {
        val facet = facetsToProcess.popFirst()
        for (subfacet in facet.facesOfDimension(subspaceDimension - 2u)) if (subfacet in subfacetsToProcess) {
            val startPoint: Point<Number>
            val normalGiftWrappingVector: Vector<Number>
            val tangentGiftWrappingVector: Vector<Number>
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

            val newVertices: KoneList<Vertex> = giftWrappingAtom(
                startPoint = startPoint,
                normalGiftWrappingVector = normalGiftWrappingVector,
                tangentGiftWrappingVector = tangentGiftWrappingVector,
                otherPoints = buildKoneSet<Vertex>(elementContext = vertexContext) {
                    addAllFrom(allVertices)
                    removeAllFrom(subfacet.vertices)
                }
            )

            val newFacet: Polytope = giftWrappingIncrement(
                subspaceDimension = subspaceDimension - 1u,
                startFacet = subfacet,
                otherPoints = newVertices,
                computedFacesRegistry = computedFacesRegistry,
                vertexContext = vertexContext,
                polytopeContext = polytopeContext,
            )

            allVertices.removeAllThat { vertexContext { it in newVertices } && it !in newFacet.vertices }

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
    return addPolytope(convexHullVertices, restConvexHullFaces).also { computedFacesRegistry[convexHullVertices] = it }
}

internal data class WrappingResult<Number, Polytope, Vertex>(
    var polytope: Polytope,
    val computedFacesRegistry: KoneMutableMap<KoneSet<Vertex>, Polytope>,
    val startPoint: Point<Number>,
    val orthogonalizationState: GramSchmidtOrthogonalizationIntermediateState<Number>,
)

context(_: NumberContext, _: EuclideanKategory<Number>)
internal fun <
    Number,
    NumberContext,
    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.giftWrappingExtension(
    subspaceDimension: UInt,
    wrappingResult: WrappingResult<Number, Polytope, Vertex>,
    normalVector: Vector<Number>,
    otherPoints: KoneIterable<Vertex>,
    vertexContext: ReifiedEquality<Vertex>,
    polytopeContext: ReifiedEquality<Polytope>,
) where NumberContext: Ring<Number>, NumberContext: Order<Number> {
    if (otherPoints.isEmpty()) return
    require(subspaceDimension >= 1u) { TODO("Error message is not specified") }

    val otherPoints = otherPoints.toKoneMutableSet(elementContext = vertexContext)
    var currentNormalVector = normalVector

    while (otherPoints.isNotEmpty()) {
        if (wrappingResult.orthogonalizationState.orthogonalizedBasis.size == subspaceDimension - 1u) {
            val resultingPolytope = giftWrappingIncrement(
                subspaceDimension = subspaceDimension,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
                vertexContext = vertexContext,
                polytopeContext = polytopeContext,
            )
            wrappingResult.polytope = resultingPolytope
            val newVector = otherPoints.first().position - wrappingResult.startPoint
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(newVector)
            return
        }

        val extendedOrthogonalizationState = wrappingResult.orthogonalizationState.clone()
        extendedOrthogonalizationState.gramSchmidtOrthogonalizationExtension(currentNormalVector)

        val tangentVector = otherPoints.firstOfThatOrNull({
            extendedOrthogonalizationState.gramSchmidtOrthogonalizationUsage(it.position - wrappingResult.startPoint)
        }) { it.any { it.isNotZero() } } ?: scope {
            val resultingPolytope = giftWrappingIncrement(
                subspaceDimension = wrappingResult.orthogonalizationState.orthogonalizedBasis.size + 1u,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
                vertexContext = vertexContext,
                polytopeContext = polytopeContext,
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
            subspaceDimension = subspaceDimension - 1u,
            wrappingResult = wrappingResult,
            normalVector = currentNormalVector,
            otherPoints = nextPoints,
            vertexContext = vertexContext,
            polytopeContext = polytopeContext,
        )

        otherPoints.removeAllFrom(nextPoints)
        currentNormalVector = scope {
            val vectorOfTheExtensionDirection = nextPoints.first().position - wrappingResult.startPoint
            tangentVector * (currentNormalVector dot vectorOfTheExtensionDirection) - currentNormalVector * (tangentVector dot vectorOfTheExtensionDirection)
        }
    }
}

context(_: NumberContext, _: EuclideanKategory<Number>)
internal fun <
    Number,
    NumberContext,
    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.giftWrappingFull(
    subspaceDimension: UInt,
    points: KoneIterable<Vertex>,
    vertexContext: ReifiedEquality<Vertex>,
    polytopeContext: ReifiedEquality<Polytope>,
): WrappingResult<Number, Polytope, Vertex> where NumberContext: Ring<Number>, NumberContext: Order<Number> {
    require(points.isNotEmpty()) { TODO("Error message is not specified") }
    if (subspaceDimension == 0u) {
        val theOnlyVertex = points.single()
        return WrappingResult(
            polytope = theOnlyVertex.asPolytope(),
            computedFacesRegistry = koneMutableMapOf(
                keyContext = koneSetEquality(vertexContext)
            ),
            startPoint = theOnlyVertex.position,
            orthogonalizationState = GramSchmidtOrthogonalizationIntermediateState(
                orthogonalizedBasis = KoneArrayFixedCapacityList(spaceDimension),
                product = one,
                exclusiveProducts = KoneArrayFixedCapacityList(spaceDimension)
            )
        )
    }

    val startPoints = points.minListBy { it.position.coordinates[subspaceDimension - 1u] }
    val wrappingResult = giftWrappingFull(
        subspaceDimension = subspaceDimension - 1u,
        points = startPoints,
        vertexContext = vertexContext,
        polytopeContext = polytopeContext,
    )

    giftWrappingExtension(
        subspaceDimension = subspaceDimension,
        wrappingResult = wrappingResult,
        normalVector = dev.lounres.kone.computationalGeometry.Vector(ColumnVector(spaceDimension) { if (it == subspaceDimension - 1u) one else zero }),
        otherPoints = points.toKoneMutableSet(elementContext = vertexContext).apply { removeAllFrom(startPoints) },
        vertexContext = vertexContext,
        polytopeContext = polytopeContext,
    )

    return wrappingResult
}

context(_: NumberContext, _: EuclideanKategory<Number>)
public fun <
    Number,
    NumberContext,
    Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.constructConvexHullByGiftWrapping(
    vertices: KoneIterable<Vertex>,
    vertexContext: ReifiedEquality<Vertex>,
    polytopeContext: ReifiedEquality<Polytope>,
): Polytope where NumberContext: Ring<Number>, NumberContext: Order<Number> {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    return giftWrappingFull(
        subspaceDimension = spaceDimension,
        points = vertices,
        vertexContext = vertexContext,
        polytopeContext = polytopeContext,
    ).polytope
}

context(_: NumberContext, _: EuclideanKategory<Number>)
public inline fun <
    Number,
    NumberContext,
    reified Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    reified Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> ExtendablePolytopicConstruction<Number, Polytope, Vertex>.constructConvexHullByGiftWrapping(
    vertices: KoneIterable<Vertex>,
): Polytope where NumberContext: Ring<Number>, NumberContext: Order<Number> =
    constructConvexHullByGiftWrapping(vertices, defaultReifiedEquality(), defaultReifiedEquality())

// TODO: Finish migration to new API.

//public fun <N, A, P, V: P, PE: Equality<P>> KoneIterable<V, PE>.constructConvexHullByGiftWrapping4(euclideanSpace: EuclideanSpace<N, A>, construction: MutablePolytopicConstruction<N, A, P, V, PE>): P where A: Ring<N>, A: Order<N> =
//    with(euclideanSpace) { with(construction) { this@constructConvexHullByGiftWrapping4.giftWrapping(spaceDimension).polytope } }

///**
// * See [here](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) for more.
// */
//context(EuclideanSpace<N, A>, MutablePolytopicConstruction2<N, P, V>)
//public fun <N, A, P, V: P> Collection<V>.constructConvexHullByGiftWrapping() /* TODO: Think about what to return */ where A: Ring<N>, A: Order<N> {
//    when (this.size) {
//        0, 1 -> return
//        2 -> {
//            val theVertices = this.iterator().let { koneMutableSetOf(it.next(), it.next()) }
//            addPolytope(theVertices, koneListOf(theVertices))
//        }
//    }
//
//    val startPoint = this.minWith { o1: V, o2: V -> lexicographic2DComparator.compare(o1.coordinates, o2.coordinates) }
//    var currentPoint = startPoint
//    val result = mutableListOf<V>()
//    do {
//        val iterator = this.iterator()
//        var nextPoint = iterator.next()
//        if (nextPoint == currentPoint) nextPoint = iterator.next()
//        val nextPoints = mutableListOf(nextPoint)
//        for (p in iterator) {
//            if (p == currentPoint) continue
//            val product = (nextPoint.coordinates - currentPoint.coordinates) cross (p.coordinates - currentPoint.coordinates)
//            when {
//                numberRing { product >= zero } -> {
//                    nextPoints.clear()
//                    nextPoint = p
//                    nextPoints += p
//                }
//                numberRing { product eq zero } -> {
//                    nextPoints += p
//                }
//            }
//        }
//        nextPoints.sortWith { o1: V, o2: V -> lexicographic2DComparator.compare(o1.coordinates, o2.coordinates) }
//        result += currentPoint
//        currentPoint = nextPoints.removeAt(nextPoints.lastIndex)
//        result += nextPoints
//    } while (currentPoint != startPoint)
//
//    val resultSet = result.toSet()
//    val edges = mutableSetOf<P>()
//    for (i in result.indices) {
//        val theVertices = koneMutableSetOf(result[i], result[(i+1) % result.size])
//        edges += addPolytope(theVertices, koneListOf(theVertices))
//    }
//    if (true /* TODO: Replace with check that the convex hull is not degenerate */) addPolytope(resultSet.asKone(), koneListOf(resultSet.asKone(), edges.asKone()))
//}
//
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

/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.ColumnVector
import dev.lounres.kone.logging.koneLogger
import dev.lounres.kone.misc.scope
import dev.lounres.kone.multidimensionalCollections.experiment1.utils.any


context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, P, V>, PE)
internal fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.giftWrappingAtom(
    startPoint: Point<N>,
    normalGiftWrappingVector: Vector<N>,
    tangentGiftWrappingVector: Vector<N>,
): KoneIterableList<V> where A: Ring<N>, A: Order<N> {
    koneLogger.info(
        source = "dev.lounres.kone.computationalGeometry.giftWrappingAtom",
        items = {
            mapOf(
                "points" to this,
                "startPoint" to startPoint,
                "normalGiftWrappingVector" to normalGiftWrappingVector,
                "tangentGiftWrappingVector" to tangentGiftWrappingVector,
            )
        }
    ) { "Input data" }
    data class TangentFraction<N>(val numerator: N, val denominator: N)
    return this.minListWithBy(
        elementContext = this@PE,
        { left, right -> numberRing { (left.numerator * right.denominator) compareTo (right.numerator * left.denominator) } }
    ) {
        val v = it.coordinates - startPoint
        TangentFraction(v dot tangentGiftWrappingVector, v dot normalGiftWrappingVector)
    }.also {
        koneLogger.info(
            source = "dev.lounres.kone.computationalGeometry.giftWrappingAtom",
            items = {
                mapOf(
                    "points" to this,
                    "startPoint" to startPoint,
                    "normalGiftWrappingVector" to normalGiftWrappingVector,
                    "tangentGiftWrappingVector" to tangentGiftWrappingVector,
                    "result" to it,
                )
            }
        ) { "Result" }
    }
}

// TODO: Docs
/**
 * Строит выпуклую оболочку точек в подпространстве.
 *
 * Принимает размерность подпространства, фасету искомой выпуклой оболочки и другие точки в подпространстве, не лежащие в этой фасете.
 */
context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, P, V>, PE)
internal fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.giftWrappingIncrement(
    subspaceDimension: UInt,
    startFacet: P,
): P where A: Ring<N>, A: Order<N> {
    koneLogger.info(
        source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
        items = {
            mapOf(
                "subspaceDimension" to subspaceDimension,
                "startFacet" to startFacet,
                "otherPoints" to this,
            )
        }
    ) { "Started incrementation" }
    require(subspaceDimension >= 1u) { "Can't define gift wrapping increment for subspace of dimension 0" }
    if (subspaceDimension == 1u) {
        @Suppress("UNCHECKED_CAST")
        val startVertex = startFacet as V
        val startPoint = startVertex.coordinates
        val endVertex = numberRing {
            this.maxBy { // TODO: Может быть это можно оптимизировать
                val radiusVector = it.coordinates - startPoint
                radiusVector dot radiusVector
            }
        }
        val vertices = koneIterableSetOf(startVertex, endVertex)
        return addPolytope(vertices,
            koneIterableListOf(
                vertices.toKoneIterableSet<P>(elementContext = this@PE),
                elementContext = koneIterableSetEquality(this@PE)
            )
        ).also {
            koneLogger.info(
                source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
                items = {
                    mapOf(
                        "subspaceDimension" to subspaceDimension,
                        "startFacet" to startFacet,
                        "otherPoints" to this,
                        "result" to it
                    )
                }
            ) { "Increment 1D result" }
        }
    }

    val allVertices = (this@giftWrappingIncrement).toKoneMutableIterableSet(elementContext = this@PE).apply { addAll(startFacet.vertices) }

    val convexHullVertices = koneMutableIterableSetOf<V>(elementContext = this@PE)
    @Suppress("UNCHECKED_CAST")
    val convexHullFaces = KoneIterableList(subspaceDimension, elementContext = koneIterableSetEquality(this@PE)) { if (it == 0u) convexHullVertices as KoneMutableIterableSet<P> else koneMutableIterableSetOf(elementContext = this@PE) }

    for (dim in 0u .. subspaceDimension-2u) {
        convexHullFaces[dim].addAll(startFacet.facesOfDimension(dim))
    }
    convexHullFaces[subspaceDimension-1u].add(startFacet)

    val facetsToProcess = koneMutableIterableSetOf<P>(elementContext = this@PE) // TODO: Replace with queue
    val subfacetsToProcess = koneMutableIterableSetOf<P>(elementContext = this@PE)

    facetsToProcess.add(startFacet)
    subfacetsToProcess.addAll(startFacet.facesOfDimension(subspaceDimension - 2u))

    while (facetsToProcess.isNotEmpty()) {
        val facet = facetsToProcess.first()
        facetsToProcess.remove(facet)
        for (subfacet in facet.facesOfDimension(subspaceDimension - 2u)) if (subfacet in subfacetsToProcess) {
            val startPoint: Point<N>
            val normalGiftWrappingVector: Vector<N>
            val tangentGiftWrappingVector: Vector<N>
            scope {
                val facetFlag = KoneSettableIterableList(subspaceDimension) { facet }
                facetFlag[subspaceDimension - 2u] = subfacet
                if (subspaceDimension >= 3u) for (dim in subspaceDimension - 3u downTo 0u) {
                    facetFlag[dim] = facetFlag[dim+1u].facesOfDimension(dim).first()
                }
                @Suppress("UNCHECKED_CAST")
                startPoint = (facetFlag[0u] as V).coordinates
                koneLogger.info(
                    source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
                    items = {
                        mapOf(
                            "facetFlag" to facetFlag,
                            "otherPoints" to this,
                        )
                    }
                ) { "Increment facetFlag" }
                val basis = KoneIterableList(subspaceDimension) { index ->
                    if (index + 1u < subspaceDimension) {
                        val basisElement = facetFlag[index + 1u].vertices.first { it !in facetFlag[index].vertices }.coordinates - startPoint
                        koneLogger.debug(
                            source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
                            items = {
                                mapOf(
                                    "subspaceDimension" to subspaceDimension,
                                    "index" to index,
                                    "facetFlag[index]" to facetFlag[index],
                                    "facetFlag[index + 1u]" to facetFlag[index + 1u],
                                    "facetFlag[index].vertices" to facetFlag[index].vertices,
                                    "facetFlag[index + 1u].vertices" to facetFlag[index + 1u].vertices,
                                    "basis element" to basisElement,
                                )
                            }
                        ) {
                            "Basis element"
                        }
                        basisElement
                    } else {
                        val basisElement = allVertices.first { it !in facet.vertices }.coordinates - startPoint
                        koneLogger.debug(
                            source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
                            items = {
                                mapOf(
                                    "subspaceDimension" to subspaceDimension,
                                    "index" to index,
                                    "basis element" to basisElement,
                                )
                            }
                        ) {
                            "Basis element"
                        }
                        basisElement
                    }
                }
                val orthogonalizedBasis = basis.gramSchmidtOrtogonalization()
                koneLogger.debug(
                    source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
                    items = {
                        mapOf(
                            "subspaceDimension" to subspaceDimension,
                            "basis" to basis,
                            "orthogonalizedBasis" to orthogonalizedBasis,
                        )
                    }
                ) { "Increment orthogonalizedBasis" }
                tangentGiftWrappingVector = orthogonalizedBasis[subspaceDimension-2u]
                normalGiftWrappingVector = orthogonalizedBasis[subspaceDimension-1u]
            }

            val newVertices: KoneIterableList<V> = buildKoneIterableSet<V>(elementContext = this@PE) {
                addAll(allVertices)
                removeAllFrom(subfacet.vertices)
            }.giftWrappingAtom(startPoint, normalGiftWrappingVector, tangentGiftWrappingVector)

            val newFacet: P = newVertices.giftWrappingIncrement(subspaceDimension - 1u, subfacet)

            allVertices.removeAllThat { it in newVertices && it !in newFacet.vertices }

            for (dim in 0u .. subspaceDimension-2u) {
                convexHullFaces[dim].addAll(newFacet.facesOfDimension(dim))
            }
            convexHullFaces[subspaceDimension-1u].add(newFacet)

            facetsToProcess.add(newFacet)
            for (newSubfacet in newFacet.facesOfDimension(subspaceDimension - 2u))
                if (newSubfacet in subfacetsToProcess) subfacetsToProcess.remove(newSubfacet)
                else subfacetsToProcess.add(newSubfacet)
        }
    }

    check(subfacetsToProcess.isEmpty()) { "For some reason some subfacets are left after \"gift wrapping increment\" procedure" }

    // TODO: Separate polytope creation and polytope finding
    return addPolytope(convexHullVertices, convexHullFaces).also {
        koneLogger.info(
            source = "dev.lounres.kone.computationalGeometry.giftWrappingIncrement",
            items = {
                mapOf(
                    "subspaceDimension" to subspaceDimension,
                    "startFacet" to startFacet,
                    "otherPoints" to this,
                    "result" to it
                )
            }
        ) { "Increment result" }
    }
}

internal data class WrappingResult<N, NE: Equality<N>, P>(
    var polytope: P,
    val startPoint: Point<N>,
    val basis: KoneMutableIterableList<Vector<N>>,
    val orthogonalizationState: GramSchmidtOrtogonalizationIntermediateState<N, NE>,
)

context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, P, V>, PE)
internal fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.giftWrappingExtension(
    subspaceDimension: UInt,
    wrappingResult: WrappingResult<N, A, P>,
    normalVector: Vector<N>,
): WrappingResult<N, A, P> where A: Ring<N>, A: Order<N> {
    if (this.isEmpty()) return wrappingResult
    require(subspaceDimension >= 1u) { TODO("Error message is not specified") }

    val otherPoints = this.toKoneMutableIterableSet(elementContext = this@PE)
    var currentNormalVector = normalVector

    while (otherPoints.isNotEmpty()) {
        if (wrappingResult.basis.size == subspaceDimension - 1u) {
            val resultingPolytope = otherPoints.giftWrappingIncrement(
                subspaceDimension = subspaceDimension,
                startFacet = wrappingResult.polytope,
            )
            wrappingResult.polytope = resultingPolytope
            val newVector = otherPoints.first().coordinates - wrappingResult.startPoint
            wrappingResult.basis.add(newVector)
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(newVector)
            return wrappingResult
        }

        val extendedOrthogonalizationState = wrappingResult.orthogonalizationState.clone()
        this@EuclideanSpace {
            val newIndex = extendedOrthogonalizationState.orthogonalizedBasis.size
            extendedOrthogonalizationState.orthogonalizedBasis.add(currentNormalVector)
            val currentNorm = currentNormalVector dot currentNormalVector
            for (j in 0u..<newIndex) {
                extendedOrthogonalizationState.exclusiveProducts[j] *= currentNorm
            }
            extendedOrthogonalizationState.exclusiveProducts.add(extendedOrthogonalizationState.product)
            extendedOrthogonalizationState.product *= currentNorm
        }

        val tangentVector = otherPoints.firstOfOrNull({
            val vector = it.coordinates - wrappingResult.startPoint
            val extendedSize = extendedOrthogonalizationState.orthogonalizedBasis.size
            (0u..<extendedSize).fold(vector * extendedOrthogonalizationState.product) { acc, index ->
                val previousOrthogonalizedVector = extendedOrthogonalizationState.orthogonalizedBasis[index]
                acc - previousOrthogonalizedVector * (previousOrthogonalizedVector dot vector) * extendedOrthogonalizationState.exclusiveProducts[index]
            }
        }) { numberRing { it.coordinates.coefficients.any { it.isNotZero() } } } ?: scope {
            val resultingPolytope = otherPoints.giftWrappingIncrement(
                subspaceDimension = wrappingResult.basis.size + 1u,
                startFacet = wrappingResult.polytope,
            )
            wrappingResult.polytope = resultingPolytope
            val newVector = otherPoints.first().coordinates - wrappingResult.startPoint
            wrappingResult.basis.add(newVector)
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(newVector)
            return wrappingResult
        }

        val nextPoints = otherPoints.giftWrappingAtom(
            startPoint = wrappingResult.startPoint,
            normalGiftWrappingVector = currentNormalVector,
            tangentGiftWrappingVector = tangentVector
        )

        nextPoints.giftWrappingExtension(
            subspaceDimension = subspaceDimension - 1u,
            wrappingResult = wrappingResult,
            normalVector = currentNormalVector
        )

        otherPoints.removeAllFrom(nextPoints)
        currentNormalVector = scope {
            val vectorOfTheExtensionDirection = nextPoints.first().coordinates - wrappingResult.startPoint
            tangentVector * (currentNormalVector dot vectorOfTheExtensionDirection) - currentNormalVector * (tangentVector dot vectorOfTheExtensionDirection)
        }
    }

    return wrappingResult
}

context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, P, V>, PE)
internal fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.giftWrapping(
    subspaceDimension: UInt,
): WrappingResult<N, A, P> where A: Ring<N>, A: Order<N> {
    require(this.isNotEmpty()) { TODO("Error message is not specified") }
    if (subspaceDimension == 0u) {
        val theOnlyVertex = this.single()
        return WrappingResult(
            polytope = theOnlyVertex,
            startPoint = theOnlyVertex.coordinates,
            basis = KoneFixedCapacityArrayList(spaceDimension),
            orthogonalizationState = GramSchmidtOrtogonalizationIntermediateState(
                orthogonalizedBasis = KoneFixedCapacityArrayList(spaceDimension),
                product = numberRing.one,
                exclusiveProducts = KoneFixedCapacityArrayList(spaceDimension)
            )
        )
    }

    val startPoints = numberRing { this.minListBy { it.coordinates.coordinates[subspaceDimension - 1u] } }
    val wrappingResult = startPoints.giftWrapping(subspaceDimension - 1u)

    this.toKoneMutableIterableSet(elementContext = this@PE).apply { removeAllFrom(startPoints) }.giftWrappingExtension(
        subspaceDimension = subspaceDimension,
        wrappingResult = wrappingResult,
        normalVector = Vector(ColumnVector(spaceDimension) { numberRing { if (it == subspaceDimension - 1u) one else zero } })
    )

    return wrappingResult
}

context(EuclideanSpace<N, A>, MutablePolytopicConstruction<N, P, V>, PE)
public fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.constructConvexHullByGiftWrapping2(): P where A: Ring<N>, A: Order<N> =
    this.giftWrapping(spaceDimension).polytope

context(EuclideanSpace<N, A>, PE)
public fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V>.constructConvexHullByGiftWrapping3(construction: MutablePolytopicConstruction<N, P, V>): P where A: Ring<N>, A: Order<N> =
    with(construction) { this@constructConvexHullByGiftWrapping3.giftWrapping(spaceDimension).polytope }

//public fun <N, A, P, V: P, PE: Equality<P>> KoneIterableCollection<V, PE>.constructConvexHullByGiftWrapping4(euclideanSpace: EuclideanSpace<N, A>, construction: MutablePolytopicConstruction<N, A, P, V, PE>): P where A: Ring<N>, A: Order<N> =
//    with(euclideanSpace) { with(construction) { this@constructConvexHullByGiftWrapping4.giftWrapping(spaceDimension).polytope } }

///**
// * See [here](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) for more.
// */
//context(EuclideanSpace<N, A>, MutablePolytopicConstruction2<N, P, V>)
//public fun <N, A, P, V: P> Collection<V>.constructConvexHullByGiftWrapping() /* TODO: Think about what to return */ where A: Ring<N>, A: Order<N> {
//    when (this.size) {
//        0, 1 -> return
//        2 -> {
//            val theVertices = this.iterator().let { koneMutableIterableSetOf(it.next(), it.next()) }
//            addPolytope(theVertices, koneIterableListOf(theVertices))
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
//        val theVertices = koneMutableIterableSetOf(result[i], result[(i+1) % result.size])
//        edges += addPolytope(theVertices, koneIterableListOf(theVertices))
//    }
//    if (true /* TODO: Replace with check that the convex hull is not degenerate */) addPolytope(resultSet.asKone(), koneIterableListOf(resultSet.asKone(), edges.asKone()))
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
//    if (points.none { numberRing { v cross (it - rightPoint) > zero } }) return points.toList()
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
//internal fun <N, A> ExtendableSetHookable<Point2<N>>.upperConvexHullBySweepingLine(): UpdateHookable<KoneIterableList<Point2<N>>> where A: Ring<N>, A: Order<N> =
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

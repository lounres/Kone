/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.ModuleBasis
import dev.lounres.kone.algebraic.basis.ModuleBasisDecomposition
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.set.relations.equality
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.Polytope
import dev.lounres.kone.computationalGeometry.polytopes.Position
import dev.lounres.kone.computationalGeometry.polytopes.verticesOrSelf
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.*
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.SuppliedType


context(ring: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
> giftWrappingAtom(
    positionKey: Position<Point>,
    startPoint: Point,
    normalGiftWrappingVector: Vector,
    tangentGiftWrappingVector: Vector,
    otherPoints: KoneIterable<Polytope>,
): KoneList<Polytope> {
    data class TangentFraction(val numerator: Number, val denominator: Number)
    return otherPoints.minListWithBy(
        { left, right -> (left.numerator * right.denominator) compareWith (right.numerator * left.denominator) }
    ) {
        val v = it.properties[positionKey] - startPoint
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
> giftWrappingIncrement(
    positionKey: Position<Point>,
    subspaceDimension: UInt,
    startFacet: Polytope,
    otherPoints: KoneIterable<Polytope>,
    computedFacesRegistry: KoneMutableMap<KoneSet<Polytope>, Polytope>,
): Polytope {
    require(subspaceDimension >= 1u) { "Can't define gift wrapping increment for subspace of dimension 0" }
    val allVertices = otherPoints.toKoneMutableReifiedSet(
        elementReification = Reification.defaultFor(),
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )
    allVertices.addAllFrom(startFacet.verticesOrSelf)

    computedFacesRegistry.getOrNull(allVertices)?.let { return it }

    if (subspaceDimension == 1u) {
        val startPoint = startFacet.properties[positionKey]
        val endVertex = otherPoints.maxBy { // TODO: Может быть это можно оптимизировать
            val radiusVector = it.properties[positionKey] - startPoint
            radiusVector dot radiusVector
        }
        return Polytope(
            dimension = subspaceDimension,
            faces = KoneList.of(
                KoneReifiedSet.of(
                    startFacet,
                    endVertex,
                    elementReification = Reification.defaultFor(),
                    elementEquality = Equality.absoluteFor(),
                    elementHashing = Hashing.defaultFor(),
                )
            )
        ).also {
            computedFacesRegistry[it.verticesOrSelf] = it
        }
    }

    val restConvexHullFaces = KoneList.generate(subspaceDimension) {
        KoneMutableReifiedSet.of<Polytope>(
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    }

    for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(startFacet.faces[dim])
    restConvexHullFaces[subspaceDimension-1u].add(startFacet)

    val facetsToProcess: KoneDeque<Polytope> = KoneListBackedDeque(KoneArrayResizableLinkedList())
    val subfacetsToProcess = KoneMutableSet.of<Polytope>(
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )

    facetsToProcess.addLast(startFacet)
    subfacetsToProcess.addAllFrom(startFacet.faces[subspaceDimension - 2u])

    while (facetsToProcess.isNotEmpty()) {
        val facet = facetsToProcess.popFirst()
        for (subfacet in facet.faces[subspaceDimension - 2u]) if (subfacet in subfacetsToProcess) {
            val startPoint: Point
            val normalGiftWrappingVector: Vector
            val tangentGiftWrappingVector: Vector
            scope {
                val facetFlag = KoneSettableList.generate(subspaceDimension) { facet }
                facetFlag[subspaceDimension - 2u] = subfacet
                if (subspaceDimension >= 3u) for (dim in subspaceDimension - 3u downTo 0u) {
                    facetFlag[dim] = facetFlag[dim+1u].faces[dim].first()
                }
                startPoint = facetFlag[0u].properties[positionKey]
                val basis = KoneList.generate(subspaceDimension) { index ->
                    if (index < subspaceDimension - 1u) facetFlag[index + 1u].verticesOrSelf.firstThat { it !in facetFlag[index].verticesOrSelf }.properties[positionKey] - startPoint
                    else allVertices.firstThat { it !in facet.verticesOrSelf }.properties[positionKey] - startPoint
                }
                val orthogonalizedBasis = basis.gramSchmidtOrthogonalization()
                tangentGiftWrappingVector = orthogonalizedBasis[subspaceDimension-2u]
                normalGiftWrappingVector = orthogonalizedBasis[subspaceDimension-1u]
            }

            val newVertices: KoneList<Polytope> = giftWrappingAtom(
                positionKey = positionKey,
                startPoint = startPoint,
                normalGiftWrappingVector = normalGiftWrappingVector,
                tangentGiftWrappingVector = tangentGiftWrappingVector,
                otherPoints = KoneSet.build(
                    elementEquality = Equality.absoluteFor(),
                    elementHashing = Hashing.defaultFor(),
                ) {
                    +allVertices
                    -subfacet.verticesOrSelf
                }
            )

            val newFacet: Polytope = giftWrappingIncrement(
                positionKey = positionKey,
                subspaceDimension = subspaceDimension - 1u,
                startFacet = subfacet,
                otherPoints = newVertices,
                computedFacesRegistry = computedFacesRegistry,
            )

            allVertices.removeAllThat { (Equality.absoluteFor<Polytope>()) { it in newVertices } && it !in newFacet.verticesOrSelf }

            for (dim in 0u .. subspaceDimension-2u) restConvexHullFaces[dim].addAllFrom(newFacet.faces[dim])
            restConvexHullFaces[subspaceDimension-1u].add(newFacet)

            facetsToProcess.addLast(newFacet)
            for (newSubfacet in newFacet.faces[subspaceDimension - 2u])
                if (newSubfacet in subfacetsToProcess) subfacetsToProcess.remove(newSubfacet)
                else subfacetsToProcess.add(newSubfacet)
        }
    }

    check(subfacetsToProcess.isEmpty()) { "For some reason some subfacets are left after \"gift wrapping increment\" procedure" }

    return Polytope(dimension = subspaceDimension, faces = restConvexHullFaces).also {
        computedFacesRegistry[allVertices] = it
    }
}

internal data class WrappingResult<Number, Vector, Point>(
    var polytope: Polytope,
    val computedFacesRegistry: KoneMutableMap<KoneSet<Polytope>, Polytope>,
    val startPoint: Point,
    val orthogonalizationState: GramSchmidtOrthogonalizationIntermediateState<Number, Vector>,
)

context(_: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
> giftWrappingExtension(
    positionKey: Position<Point>,
    subspaceDimension: UInt,
    wrappingResult: WrappingResult<Number, Vector, Point>,
    normalVector: Vector,
    otherPoints: KoneIterable<Polytope>,
) {
    if (otherPoints.isEmpty()) return
    require(subspaceDimension >= 1u) { TODO("Error message is not specified") }

    val otherPoints = otherPoints.toKoneMutableSet(
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )
    var currentNormalVector = normalVector

    while (otherPoints.isNotEmpty()) {
        if (wrappingResult.orthogonalizationState.orthogonalizedBasis.size == subspaceDimension - 1u) {
            val resultingPolytope = giftWrappingIncrement(
                positionKey = positionKey,
                subspaceDimension = subspaceDimension,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
            )
            wrappingResult.polytope = resultingPolytope
            val newVector = otherPoints.first().properties[positionKey] - wrappingResult.startPoint
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(newVector)
            return
        }

        val extendedOrthogonalizationState = wrappingResult.orthogonalizationState.clone(subspaceDimension)
        extendedOrthogonalizationState.gramSchmidtOrthogonalizationExtension(currentNormalVector)

        val tangentVector = otherPoints.firstOfThatOrNull({
            extendedOrthogonalizationState.gramSchmidtOrthogonalizationUsage(it.properties[positionKey] - wrappingResult.startPoint)
        }) { it.isNotZero() } ?: scope {
            val resultingPolytope = giftWrappingIncrement(
                positionKey = positionKey,
                subspaceDimension = wrappingResult.orthogonalizationState.orthogonalizedBasis.size + 1u,
                startFacet = wrappingResult.polytope,
                otherPoints = otherPoints,
                computedFacesRegistry = wrappingResult.computedFacesRegistry,
            )
            wrappingResult.polytope = resultingPolytope
            wrappingResult.orthogonalizationState.gramSchmidtOrthogonalizationStep(otherPoints.first().properties[positionKey] - wrappingResult.startPoint)
            return
        }

        val nextPoints = giftWrappingAtom(
            positionKey = positionKey,
            startPoint = wrappingResult.startPoint,
            normalGiftWrappingVector = currentNormalVector,
            tangentGiftWrappingVector = tangentVector,
            otherPoints = otherPoints
        )

        giftWrappingExtension(
            positionKey = positionKey,
            subspaceDimension = subspaceDimension - 1u,
            wrappingResult = wrappingResult,
            normalVector = currentNormalVector,
            otherPoints = nextPoints,
        )

        otherPoints.removeAllFrom(nextPoints)
        currentNormalVector = scope {
            val vectorOfTheExtensionDirection = nextPoints.first().properties[positionKey] - wrappingResult.startPoint
            tangentVector * (currentNormalVector dot vectorOfTheExtensionDirection) - currentNormalVector * (tangentVector dot vectorOfTheExtensionDirection)
        }
    }
}

context(ring: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
internal fun <
    Number,
    Vector,
    Point,
> giftWrappingFull(
    positionKey: Position<Point>,
    basis: ModuleBasis.Finite<Number, Vector>,
    subspaceDimension: UInt,
    points: KoneIterable<Polytope>,
): WrappingResult<Number, Vector, Point> {
    require(points.isNotEmpty()) { TODO("Error message is not specified") }
    if (subspaceDimension == 0u) {
        val theOnlyVertex = points.single()
        return WrappingResult(
            polytope = theOnlyVertex,
            computedFacesRegistry = KoneMutableMap.of(
                keyEquality = KoneSet.equality(Equality.absoluteFor()),
            ),
            startPoint = theOnlyVertex.properties[positionKey],
            orthogonalizationState = GramSchmidtOrthogonalizationIntermediateState(
                orthogonalizedBasis = KoneArrayFixedCapacityList(basis.size),
                product = ring.one,
                exclusiveProducts = KoneArrayFixedCapacityList(basis.size)
            )
        )
    }

    val somePoint = points.first().properties[positionKey]
    val startPoints = points.minListBy { basis.decompose(it.properties[positionKey] - somePoint)[subspaceDimension - 1u] }
    val wrappingResult = giftWrappingFull(
        positionKey = positionKey,
        basis = basis,
        subspaceDimension = subspaceDimension - 1u,
        points = startPoints,
    )

    giftWrappingExtension(
        positionKey = positionKey,
        subspaceDimension = subspaceDimension,
        wrappingResult = wrappingResult,
        normalVector = basis[subspaceDimension - 1u],
        otherPoints = points.toKoneMutableSet(
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        ).apply { removeAllFrom(startPoints) },
    )

    return wrappingResult
}

context(_: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <
    Number,
    Vector,
    Point,
> constructConvexHullByGiftWrapping(
    pointType: SuppliedType,
    vertices: KoneIterable<Point>,
    basis: ModuleBasis.Finite<Number, Vector>,
): Polytope {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    val positionKey = Position<Point>(pointType)
    return giftWrappingFull(
        positionKey = positionKey,
        basis = basis,
        subspaceDimension = basis.size,
        points = vertices.map {
            Polytope(
                dimension = 0u,
                faces = KoneList.empty(),
                properties = Registry.build<Polytope> { positionKey correspondsTo it }
            )
        },
    ).polytope
}

context(ring: Ring<Number>, _: Order<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <
    Number,
    Vector,
    Point,
> constructConvexHullByGiftWrapping(
    pointType: SuppliedType,
    vertices: KoneIterable<Point>,
): Polytope {
    require(vertices.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
    val verticesIterator = vertices.iterator()
    val start = verticesIterator.getAndMoveNext()
    val vectors = verticesIterator.toKoneList().map { it - start }
    
    val result = GramSchmidtOrthogonalizationIntermediateState<Number, Vector>(
        orthogonalizedBasis = KoneArrayGrowableList(),
        product = ring.one,
        exclusiveProducts = KoneArrayGrowableList(),
    )
    for (vector in vectors) result.gramSchmidtOrthogonalizationStep(vector)
    
    return constructConvexHullByGiftWrapping(
        pointType = pointType,
        vertices = vertices,
        basis = object : ModuleBasis.Finite<Number, Vector> {
            override val size: UInt get() = result.orthogonalizedBasis.size
            override fun get(index: UInt): Vector = result.orthogonalizedBasis[index]
            override fun decompose(vector: Vector): ModuleBasisDecomposition.Result<Number, UInt> {
                return object : ModuleBasisDecomposition.Result<Number, UInt> {
                    override val scalar: Number get() = result.product
                    override fun get(index: UInt): Number = (result.orthogonalizedBasis[index] dot vector) * result.exclusiveProducts[index]
                }
            }
        }
    )
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

/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms.implementations

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.ModuleBasis
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
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.set.relations.equality
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.algorithms.*
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.polytopes.Polytope
import dev.lounres.kone.computationalGeometry.polytopes.Position
import dev.lounres.kone.computationalGeometry.polytopes.verticesOrSelf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.*
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


context(ring: Ring<Number>, _: Order<Number>, euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
private fun <
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
    KoneContext.unwrap(ring, euclideanSpace)
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
context(ring: Ring<Number>, _: Order<Number>, euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
private fun <
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
    KoneContext.unwrap(ring, euclideanSpace)
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
        val endVertex = otherPoints.maxBy<_, Number> { // TODO: Может быть это можно оптимизировать
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
                    this += allVertices
                    this -= facet.faces[0u]
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

private data class WrappingResult<Number, Vector, Point>(
    var polytope: Polytope,
    val computedFacesRegistry: KoneMutableMap<KoneSet<Polytope>, Polytope>,
    val startPoint: Point,
    val orthogonalizationState: GramSchmidtOrthogonalizationIntermediateState<Number, Vector>,
)

context(ring: Ring<Number>, _: Order<Number>, euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
private fun <
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
    KoneContext.unwrap(ring, euclideanSpace)
    
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

context(ring: Ring<Number>, _: Order<Number>, euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
private fun <
    Number,
    Vector,
    Point,
> giftWrappingFull(
    positionKey: Position<Point>,
    basis: ModuleBasis.Finite<Number, Vector>,
    subspaceDimension: UInt,
    points: KoneIterable<Polytope>,
): WrappingResult<Number, Vector, Point> {
    KoneContext.unwrap(ring, euclideanSpace)
    
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
    val startPoints = points.minListBy<_, Number> { basis.decompose(it.properties[positionKey] - somePoint)[subspaceDimension - 1u] }
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

@Suppliable
private class ConvexHullOverRingViaGiftWrappingComputer<Number, Vector, @Supply Point>(
    private val ring: Ring<Number>,
    private val order: Order<Number>,
    private val euclideanSpaceOverRing: EuclideanSpaceOverRing<Number, Vector, Point>,
) : ConvexHullOverRingComputer<Number, Vector, Point> {
    override fun KoneIterable<Point>.convexHull(
        basis: ModuleBasis.Finite<Number, Vector>
    ): Polytope {
        require(this.isNotEmpty()) { "Can't construct convex hull of an empty vertices collection." }
        val positionKey = Position<Point>()
        return context(ring, order, euclideanSpaceOverRing) {
            giftWrappingFull(
                positionKey = positionKey,
                basis = basis,
                subspaceDimension = basis.size,
                points = this.map {
                    Polytope(
                        dimension = 0u,
                        faces = KoneList.empty(),
                    ) {
                        positionKey correspondsTo it
                    }
                },
            ).polytope
        }
    }
}

@Suppliable
public fun <Number, Vector, @Supply Point> ConvexHullOverRingComputer.Companion.giftWrapping(
    ring: Ring<Number>,
    order: Order<Number>,
    euclideanSpaceOverRing: EuclideanSpaceOverRing<Number, Vector, Point>,
) : ConvexHullOverRingComputer<Number, Vector, Point> =
    ConvexHullOverRingViaGiftWrappingComputer(
        ring = ring,
        order = order,
        euclideanSpaceOverRing = euclideanSpaceOverRing,
    )

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Vector, @Supply Point> ConvexHullOverRingComputer.Companion.setGiftWrapping() {
    ConvexHullOverRingComputer.Key<Number, Vector, Point>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        giftWrapping(
            ring = koneContextRegistry[Ring.Key<Number>()],
            order = koneContextRegistry[Order.Key<Number>()],
            euclideanSpaceOverRing = koneContextRegistry[EuclideanSpaceOverRing.Key<Number, Vector, Point>()],
        )
    }
}
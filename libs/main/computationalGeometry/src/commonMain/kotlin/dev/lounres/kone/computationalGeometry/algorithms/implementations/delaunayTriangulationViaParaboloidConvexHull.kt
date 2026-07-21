/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.basis.ModuleBasis
import dev.lounres.kone.algebraic.basis.ModuleBasisDecomposition
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.computationalGeometry.algorithms.ConvexHullOverRingComputer
import dev.lounres.kone.computationalGeometry.algorithms.DelaunayTriangulationOverRingComputer
import dev.lounres.kone.computationalGeometry.algorithms.convexHull
import dev.lounres.kone.computationalGeometry.algorithms.gramSchmidtOrthogonalization
import dev.lounres.kone.computationalGeometry.polytopes.*
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private data class ParaboloidVector<Number, Vector>(
    val vector: Vector,
    val extraCoordinate: Number,
)

private data class ParaboloidPoint<out Number, out Point>(
    val point: Point,
    val extraCoordinate: Number,
)

private class ParaboloidEuclideanSpaceOverRing<Number, Vector, Point>(
    private val ring: Ring<Number>,
    private val initialEuclideanSpaceOverRing: EuclideanSpaceOverRing<Number, Vector, Point>,
) : EuclideanSpaceOverRing<Number, ParaboloidVector<Number, Vector>, ParaboloidPoint<Number, Point>> {
    // region Constants
    override val zero: ParaboloidVector<Number, Vector> = ParaboloidVector(
        vector = initialEuclideanSpaceOverRing.zero,
        extraCoordinate = ring.zero
    )
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<ParaboloidVector<Number, Vector>> = IsZero {
        ring.numberIsZero { it.extraCoordinate.isZero() } && initialEuclideanSpaceOverRing.numberIsZero { it.vector.isZero() }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<ParaboloidVector<Number, Vector>, Int, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberTimesInt { left.vector * right },
            extraCoordinate = ring.numberTimesInt { left.extraCoordinate * right }
        )
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<ParaboloidVector<Number, Vector>, UInt, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberTimesUInt { left.vector * right },
            extraCoordinate = ring.numberTimesUInt { left.extraCoordinate * right }
        )
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<ParaboloidVector<Number, Vector>, Long, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberTimesLong { left.vector * right },
            extraCoordinate = ring.numberTimesLong { left.extraCoordinate * right }
        )
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<ParaboloidVector<Number, Vector>, ULong, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberTimesULong { left.vector * right },
            extraCoordinate = ring.numberTimesULong { left.extraCoordinate * right }
        )
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<ParaboloidVector<Number, Vector>, Number, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.vectorTimesNumber { left.vector * right },
            extraCoordinate = ring.numberTimesNumber { left.extraCoordinate * right }
        )
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.intTimesNumber { left * right.vector },
            extraCoordinate = ring.intTimesNumber { left * right.extraCoordinate }
        )
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.uIntTimesNumber { left * right.vector },
            extraCoordinate = ring.uIntTimesNumber { left * right.extraCoordinate }
        )
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.longTimesNumber { left * right.vector },
            extraCoordinate = ring.longTimesNumber { left * right.extraCoordinate }
        )
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.uLongTimesNumber { left * right.vector },
            extraCoordinate = ring.uLongTimesNumber { left * right.extraCoordinate }
        )
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Times { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberTimesVector { left * right.vector },
            extraCoordinate = ring.numberTimesNumber { left * right.extraCoordinate }
        )
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = UnaryMinus {
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberUnaryMinus { -it.vector },
            extraCoordinate = ring.numberUnaryMinus { -it.extraCoordinate }
        )
    }
    override val numberPlusNumber: Plus<ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Plus { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberPlusNumber { left.vector + right.vector },
            extraCoordinate = ring.numberPlusNumber { left.extraCoordinate + right.extraCoordinate }
        )
    }
    override val numberMinusNumber: Minus<ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>> = Minus { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.numberMinusNumber { left.vector - right.vector },
            extraCoordinate = ring.numberMinusNumber { left.extraCoordinate - right.extraCoordinate }
        )
    }
    // endregion
    
    override val pointPlusVector: Plus<ParaboloidPoint<Number, Point>, ParaboloidVector<Number, Vector>, ParaboloidPoint<Number, Point>> = Plus { left, right ->
        ParaboloidPoint(
            point = initialEuclideanSpaceOverRing.pointPlusVector { left.point + right.vector },
            extraCoordinate = ring.numberPlusNumber { left.extraCoordinate + right.extraCoordinate }
        )
    }
    override val vectorPlusPoint: Plus<ParaboloidVector<Number, Vector>, ParaboloidPoint<Number, Point>, ParaboloidPoint<Number, Point>> = Plus { left, right ->
        ParaboloidPoint(
            point = initialEuclideanSpaceOverRing.vectorPlusPoint { left.vector + right.point },
            extraCoordinate = ring.numberPlusNumber { left.extraCoordinate + right.extraCoordinate }
        )
    }
    override val pointMinusVector: Minus<ParaboloidPoint<Number, Point>, ParaboloidVector<Number, Vector>, ParaboloidPoint<Number, Point>> = Minus { left, right ->
        ParaboloidPoint(
            point = initialEuclideanSpaceOverRing.pointMinusVector { left.point - right.vector },
            extraCoordinate = ring.numberMinusNumber { left.extraCoordinate - right.extraCoordinate }
        )
    }
    
    override val pointMinusPoint: Minus<ParaboloidPoint<Number, Point>, ParaboloidPoint<Number, Point>, ParaboloidVector<Number, Vector>> = Minus { left, right ->
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing.pointMinusPoint { left.point - right.point },
            extraCoordinate = ring.numberMinusNumber { left.extraCoordinate - right.extraCoordinate }
        )
    }
    
    override val vectorDotVector: Dot<ParaboloidVector<Number, Vector>, ParaboloidVector<Number, Vector>, Number> = Dot { left, right ->
        KoneContext.unwrap(ring, initialEuclideanSpaceOverRing)
        (left.vector dot right.vector) + left.extraCoordinate * right.extraCoordinate
    }
}

@Suppliable
private class DelaunayTriangulationOverRingComputerViaConvexHull<@Supply Number, Vector, @Supply Point>(
    private val ring: Ring<Number>,
    private val order: Order<Number>,
    private val euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>,
) : DelaunayTriangulationOverRingComputer<Number, Vector, Point> {
    private val positionKey = Position<Point>()
    private val paraboloidPositionKey = Position<ParaboloidPoint<Number, Point>>()
    
    private val paraboloidEuclideanSpaceOverRing = ParaboloidEuclideanSpaceOverRing(ring, euclideanSpace)
    
    private val paraboloidConvexHullOverRingComputer =
        ConvexHullOverRingComputer.giftWrapping(
            ring = ring,
            order = order,
            euclideanSpaceOverRing = paraboloidEuclideanSpaceOverRing,
        )
    
    override fun KoneIterable<Point>.delaunayTriangulation(
        basis: ModuleBasis.Finite<Number, Vector>
    ): PolytopicConstruction {
        require(this.isNotEmpty()) { "Can't construct Delaunay triangulation of an empty vertices collection." }
        KoneContext.unwrap(ring, order, euclideanSpace, paraboloidEuclideanSpaceOverRing, paraboloidConvexHullOverRingComputer)
        
        val verticesDimension: UInt = basis.size
        
        val startPosition = this@delaunayTriangulation.first()
        val convexHull: Polytope =
            this@delaunayTriangulation.map { oldPosition ->
                ParaboloidPoint(oldPosition, (oldPosition - startPosition).lengthSquared())
            }.convexHull(
                object : ModuleBasis.Finite<Number, ParaboloidVector<Number, Vector>> {
                    override val size: UInt get() = basis.size + 1u
                    override fun get(index: UInt): ParaboloidVector<Number, Vector> =
                        if (index < verticesDimension) ParaboloidVector(basis[index], ring.zero)
                        else ParaboloidVector(euclideanSpace.zero, ring.one)
                    override fun decompose(vector: ParaboloidVector<Number, Vector>): ModuleBasisDecomposition.Result<Number, UInt> =
                        object : ModuleBasisDecomposition.Result<Number, UInt> {
                            val decomposition by lazy { basis.decompose(vector.vector) }
                            override val scalar: Number get() = decomposition.scalar
                            override fun get(index: UInt): Number =
                                if (index < verticesDimension) decomposition[index]
                                else vector.extraCoordinate
                        }
                }
            )
        
        val simplicesMapping = KoneMutableMap.of<Polytope, Polytope>(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
        )
        
        return if (convexHull.dimension == verticesDimension)
            PolytopicConstruction.build {
                for (dim in 0u ..< convexHull.dimension) for (face in convexHull.faces[dim]) {
                    val newFace = Polytope(
                        dimension = dim,
                        faces = face.faces.map { dimFaces ->
                            dimFaces.mapTo(
                                KoneMutableReifiedSet.of(
                                    elementReification = Reification.defaultFor(),
                                )
                            ) {
                                simplicesMapping[it]
                            }
                        },
                    ) {
                        if (dim == 0u) positionKey correspondsTo face.properties[paraboloidPositionKey].point
                    }
                    simplicesMapping[face] = newFace
                }
                +Polytope(
                    dimension = convexHull.dimension,
                    faces = convexHull.faces.map { dimFaces ->
                        dimFaces.mapTo(
                            KoneMutableReifiedSet.of(
                                elementReification = Reification.defaultFor(),
                            )
                        ) {
                            simplicesMapping[it]
                        }
                    }
                )
            }
        else
            PolytopicConstruction.build {
                val necessarySimplices = convexHull.faces[convexHull.dimension - 1u].filter { simplex ->
                    val flag = KoneSettableList.generate(simplex.dimension + 2u) { simplex }
                    flag[simplex.dimension + 1u] = convexHull
                    for (dim in simplex.dimension - 1u downTo 0u) {
                        flag[dim] = flag[dim + 1u].faces[dim].first()
                    }
                    val startPoint = flag[0u].verticesOrSelf.single().properties[paraboloidPositionKey]
                    val basis = KoneSettableList.generate(
                        simplex.dimension + 1u,
                    ) { dim -> flag[dim + 1u].verticesOrSelf.firstThat { it !in flag[dim].verticesOrSelf }.properties[paraboloidPositionKey] - startPoint }
                    val ortogonalizedBasis = basis.gramSchmidtOrthogonalization()
                    val lastBasisVector = ortogonalizedBasis.last()
                    lastBasisVector.extraCoordinate.isPositive()
                }
                
                for (simplex in necessarySimplices) {
                    for (dim in 0u .. simplex.dimension - 1u) for (face in simplex.faces[dim]) if (face !in simplicesMapping.keysView) {
                        val polytope = Polytope(
                            dimension = dim,
                            faces = face.faces.map { dimFaces ->
                                dimFaces.mapTo(
                                    KoneMutableReifiedSet.of(
                                        elementReification = Reification.defaultFor(),
                                        elementEquality = Equality.absoluteFor(),
                                        elementHashing = Hashing.defaultFor(),
                                    )
                                ) {
                                    simplicesMapping[it]
                                }
                            },
                        ) {
                            if (dim == 0u) positionKey correspondsTo face.properties[paraboloidPositionKey].point
                        }
                        simplicesMapping[face] = polytope
                    }
                    +Polytope(
                        dimension = simplex.dimension,
                        faces = simplex.faces.map { dimFaces ->
                            dimFaces.mapTo(
                                KoneMutableReifiedSet.of(
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                )
                            ) {
                                simplicesMapping[it]
                            }
                        }
                    )
                }
            }
    }
}

@Suppliable
public fun <@Supply Number, Vector, @Supply Point> DelaunayTriangulationOverRingComputer.Companion.convexHull(
    ring: Ring<Number>,
    order: Order<Number>,
    euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>,
): DelaunayTriangulationOverRingComputer<Number, Vector, Point> =
    DelaunayTriangulationOverRingComputerViaConvexHull(
        ring = ring,
        order = order,
        euclideanSpace = euclideanSpace,
    )

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Vector, @Supply Point> DelaunayTriangulationOverRingComputer.Companion.setConvexHull() {
    DelaunayTriangulationOverRingComputer.Key<Number, Vector, Point>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        convexHull(
            ring = koneContextRegistry[Ring.Key<Number>()],
            order = koneContextRegistry[Order.Key<Number>()],
            euclideanSpace = koneContextRegistry[EuclideanSpaceOverRing.Key<Number, Vector, Point>()],
        )
    }
}
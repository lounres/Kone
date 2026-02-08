/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms.implementations

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.ModuleBasis
import dev.lounres.kone.algebraic.basis.ModuleBasisDecomposition
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
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
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstThat
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.algorithms.ConvexHullOverRingComputer
import dev.lounres.kone.computationalGeometry.algorithms.DelaunayTriangulationOverRingComputer
import dev.lounres.kone.computationalGeometry.algorithms.convexHull
import dev.lounres.kone.computationalGeometry.algorithms.gramSchmidtOrthogonalization
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.lengthSquared
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.polytopes.Polytope
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction
import dev.lounres.kone.computationalGeometry.polytopes.Position
import dev.lounres.kone.computationalGeometry.polytopes.build
import dev.lounres.kone.computationalGeometry.polytopes.verticesOrSelf
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


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
    override fun ParaboloidVector<Number, Vector>.equalsTo(other: ParaboloidVector<Number, Vector>): Boolean =
        initialEuclideanSpaceOverRing { this.vector eq other.vector } && ring { this.extraCoordinate eq other.extraCoordinate }
    override fun ParaboloidVector<Number, Vector>.isZero(): Boolean =
        initialEuclideanSpaceOverRing { this.vector.isZero() } && ring { this.extraCoordinate.isZero() }
    // FIXME: KT-5351
    override fun ParaboloidVector<Number, Vector>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun ParaboloidVector<Number, Vector>.times(other: UInt): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector * other },
            extraCoordinate = ring { this.extraCoordinate * other }
        )
    // endregion
    
    // region Vector-Int operations
    override operator fun ParaboloidVector<Number, Vector>.times(other: Int): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector * other },
            extraCoordinate = ring { this.extraCoordinate * other }
        )
    // endregion
    
    // region Vector-Long operations
    override operator fun ParaboloidVector<Number, Vector>.times(other: Long): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector * other },
            extraCoordinate = ring { this.extraCoordinate * other }
        )
    // endregion
    
    // region Vector-ULong operations
    override operator fun ParaboloidVector<Number, Vector>.times(other: ULong): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector * other },
            extraCoordinate = ring { this.extraCoordinate * other }
        )
    // endregion
    
    // region Vector-Number operations
    override fun ParaboloidVector<Number, Vector>.times(other: Number): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector * other },
            extraCoordinate = ring { this.extraCoordinate * other }
        )
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this * other.vector },
            extraCoordinate = ring { this * other.extraCoordinate }
        )
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this * other.vector },
            extraCoordinate = ring { this * other.extraCoordinate }
        )
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this * other.vector },
            extraCoordinate = ring { this * other.extraCoordinate }
        )
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this * other.vector },
            extraCoordinate = ring { this * other.extraCoordinate }
        )
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this * other.vector },
            extraCoordinate = ring { this * other.extraCoordinate }
        )
    // endregion
    
    // region Vector-Vector operations
    override operator fun ParaboloidVector<Number, Vector>.unaryMinus(): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { -this.vector },
            extraCoordinate = ring { -this.extraCoordinate }
        )
    override operator fun ParaboloidVector<Number, Vector>.plus(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector + other.vector },
            extraCoordinate = ring { this.extraCoordinate + other.extraCoordinate }
        )
    override operator fun ParaboloidVector<Number, Vector>.minus(other: ParaboloidVector<Number, Vector>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.vector - other.vector },
            extraCoordinate = ring { this.extraCoordinate - other.extraCoordinate }
        )
    // endregion
    
    override fun ParaboloidPoint<Number, Point>.plus(other: ParaboloidVector<Number, Vector>): ParaboloidPoint<Number, Point> =
        ParaboloidPoint(
            point = initialEuclideanSpaceOverRing { this.point + other.vector },
            extraCoordinate = ring { this.extraCoordinate + other.extraCoordinate }
        )
    
    override fun ParaboloidVector<Number, Vector>.plus(other: ParaboloidPoint<Number, Point>): ParaboloidPoint<Number, Point> =
        ParaboloidPoint(
            point = with(initialEuclideanSpaceOverRing) { this@plus.vector + other.point },
            extraCoordinate = ring { this.extraCoordinate + other.extraCoordinate }
        )
    
    override fun ParaboloidPoint<Number, Point>.minus(other: ParaboloidVector<Number, Vector>): ParaboloidPoint<Number, Point> =
        ParaboloidPoint(
            point = with(initialEuclideanSpaceOverRing) { this@minus.point - other.vector },
            extraCoordinate = ring { this.extraCoordinate - other.extraCoordinate }
        )
    
    override fun ParaboloidPoint<Number, Point>.minus(other: ParaboloidPoint<Number, Point>): ParaboloidVector<Number, Vector> =
        ParaboloidVector(
            vector = initialEuclideanSpaceOverRing { this.point - other.point },
            extraCoordinate = ring { this.extraCoordinate - other.extraCoordinate }
        )
    
    override fun ParaboloidVector<Number, Vector>.dot(other: ParaboloidVector<Number, Vector>): Number =
        ring { initialEuclideanSpaceOverRing { this.vector dot other.vector } + this.extraCoordinate * other.extraCoordinate }
}

private class DelaunayTriangulationOverRingComputerViaConvexHull<Number, Vector, Point>(
    numberType: SuppliedType,
    pointType: SuppliedType,
    private val ring: Ring<Number>,
    private val order: Order<Number>,
    private val euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>,
) : DelaunayTriangulationOverRingComputer<Number, Vector, Point> {
    val paraboloidPointType =
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.algorithms.ParaboloidPoint",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = pointType
                ),
            ),
            isNullable = false
        )
    
    private val positionKey = Position<Point>(pointType = pointType)
    private val paraboloidPositionKey = Position<ParaboloidPoint<Number, Point>>(pointType = paraboloidPointType)
    
    private val paraboloidEuclideanSpaceOverRing = ParaboloidEuclideanSpaceOverRing(ring, euclideanSpace)
    
    private val paraboloidConvexHullOverRingComputer =
        ConvexHullOverRingComputer.giftWrapping(
            pointType = paraboloidPointType,
            ring = ring,
            order = order,
            euclideanSpaceOverRing = paraboloidEuclideanSpaceOverRing,
        )
    
    override fun KoneIterable<Point>.delaunayTriangulation(
        basis: ModuleBasis.Finite<Number, Vector>
    ): PolytopicConstruction = context(ring, order, euclideanSpace) {
        require(this.isNotEmpty()) { "Can't construct Delaunay triangulation of an empty vertices collection." }
        
        val verticesDimension: UInt = basis.size
        
        val startPosition = this.first()
        val convexHull: Polytope = context(paraboloidEuclideanSpaceOverRing, paraboloidConvexHullOverRingComputer) {
            this.map { oldPosition ->
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
        }
        
        val simplicesMapping = KoneMutableMap.of<Polytope, Polytope>(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
        )
        
        if (convexHull.dimension == verticesDimension)
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
                    paraboloidEuclideanSpaceOverRing {
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

public fun <Number, Vector, Point> DelaunayTriangulationOverRingComputer.Companion.convexHull(
    numberType: SuppliedType,
    pointType: SuppliedType,
    ring: Ring<Number>,
    order: Order<Number>,
    euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>,
): DelaunayTriangulationOverRingComputer<Number, Vector, Point> =
    DelaunayTriangulationOverRingComputerViaConvexHull(
        numberType = numberType,
        pointType = pointType,
        ring = ring,
        order = order,
        euclideanSpace = euclideanSpace,
    )

context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun <Number, Vector, Point> DelaunayTriangulationOverRingComputer.Companion.setConvexHull(
    numberType: SuppliedType,
    vectorType: SuppliedType,
    pointType: SuppliedType,
) {
    DelaunayTriangulationOverRingComputer.Key<Number, Vector, Point>(numberType, vectorType, pointType) correspondsTo DelaunayTriangulationOverRingComputerViaConvexHull(
        numberType = numberType,
        pointType = pointType,
        ring = koneContextRegistryBuilder[Ring.Key<Number>(numberType)],
        order = koneContextRegistryBuilder[Order.Key<Number>(numberType)],
        euclideanSpace = koneContextRegistryBuilder[EuclideanSpaceOverRing.Key<Number, Vector, Point>(numberType, vectorType, pointType)],
    )
}
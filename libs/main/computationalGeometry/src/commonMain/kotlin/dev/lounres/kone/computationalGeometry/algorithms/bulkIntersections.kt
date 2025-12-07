/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.computationalGeometry.curves.Segment
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public fun interface BulkIntersectionComputer<in Target, out Result> {
    public fun intersect(leftIndex: UInt, left: Target, rightIndex: UInt, right: Target): Result
}

public typealias SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point> =
        BulkIntersectionComputer<Segment<Vector, Point>, SegmentWithSegmentIntersectionOverField<Number, Vector, Point>?>

public fun <Target, Result> IntersectionComputer<Target, Target, Result>.asBulkIntersectionComputer(): BulkIntersectionComputer<Target, Result> =
    { _, left, _, right -> intersect(left, right) }

public fun interface BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point> : KoneContext {
    public fun KoneList<Segment<Vector, Point>>.intersections(
        basis: VectorSpaceBasis.Finite<Number, Vector>,
        intersectionComputer: SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point>,
    ): KoneSequence<IntersectionResult<Number, Vector, Point>>
    
    public data class IntersectionResult<out Number, out Vector, out Point>(
        public val segmentIndex1: UInt,
        public val segment1: Segment<Vector, Point>,
        public val segmentIndex2: UInt,
        public val segment2: Segment<Vector, Point>,
        public val intersection: SegmentWithSegmentIntersectionOverField<Number, Vector, Point>,
    )
    
    public companion object;
    
    public class Key<Number, Vector, Point>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
        public val pointType: SuppliedType,
    ) : RegistryKey<BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.algorithms.BulkPlanarSegmentsIntersectionsOverFieldComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = pointType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.algorithms.BulkPlanarSegmentsIntersectionsOverFieldComputer.Key<$numberType, $vectorType, $pointType>"
    }
}

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.intersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point>,
): KoneSequence<BulkPlanarSegmentsIntersectionsOverFieldComputer.IntersectionResult<Number, Vector, Point>> =
    with(bulkIntersectionsComputer) { this@intersections.intersections(basis, intersectionComputer) }

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.intersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point>,
): KoneSequence<BulkPlanarSegmentsIntersectionsOverFieldComputer.IntersectionResult<Number, Vector, Point>> =
    intersections(basis, intersectionComputer.asBulkIntersectionComputer())

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.hasIntersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point>,
): Boolean = intersections(basis, intersectionComputer).iterator().hasNext()

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.hasIntersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point>,
): Boolean = intersections(basis, intersectionComputer).iterator().hasNext()
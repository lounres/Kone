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


public fun interface BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point> : KoneContext {
    public fun KoneList<Segment<Vector, Point>>.intersections(
        basis: VectorSpaceBasis.Finite<Number, Vector>,
        intersectionComputer: SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point>,
    ): KoneSequence<IntersectionResult<Number, Vector, Point>>
    
    public data class IntersectionResult<out Number, out Vector, out Point>(
        public val segmentIndex1: UInt,
        public val segment1: Segment<Vector, Point>,
        public val segmentIndex2: UInt,
        public val segment2: Segment<Vector, Point>,
        public val intersection: SegmentWithSegmentIntersectionOverField<Number, Vector, Point>,
    )
    
    public companion object
}

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.intersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point>,
): KoneSequence<BulkPlanarSegmentsIntersectionsOverFieldComputer.IntersectionResult<Number, Vector, Point>> =
    with(bulkIntersectionsComputer) { this@intersections.intersections(basis, intersectionComputer) }

context(bulkIntersectionsComputer: BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneList<Segment<Vector, Point>>.hasIntersections(
    basis: VectorSpaceBasis.Finite<Number, Vector>,
    intersectionComputer: SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point>,
): Boolean = intersections(basis, intersectionComputer).iterator().hasNext()
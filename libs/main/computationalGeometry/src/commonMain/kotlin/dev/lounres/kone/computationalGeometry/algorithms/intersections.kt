/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.computationalGeometry.curves.Segment


public fun interface IntersectionComputer<in Left, in Right, out Result> {
    public fun intersect(left: Left, right: Right): Result
}

//public sealed interface SegmentWithSegmentIntersectionOverRing<out Number, out Vector, out Point> {
//    public data class SinglePoint<out Number, out Vector, out Point>(
//        public val
//    ) : SegmentWithSegmentIntersectionOverRing<Number, Vector, Point>
//}

public sealed interface SegmentWithSegmentIntersectionOverField<out Number, out Vector, out Point> {
    public data class SinglePoint<out Number, out Point>(
        public val point: Point,
        public val leftSegmentStep: Number,
        public val rightSegmentStep: Number,
    ) : SegmentWithSegmentIntersectionOverField<Number, Nothing, Point>
    public data class Subsegment<out Number, out Vector, out Point>(
        public val segment: Segment<Vector, Point>,
        public val segmentInLeftSteps: Segment<Number, Number>,
        public val segmentInRightSteps: Segment<Number, Number>,
    ) : SegmentWithSegmentIntersectionOverField<Number, Vector, Point>
}

public typealias SegmentWithSegmentIntersectionOverFieldComputer<Number, Vector, Point> =
        IntersectionComputer<Segment<Vector, Point>, Segment<Vector, Point>, SegmentWithSegmentIntersectionOverField<Number, Vector, Point>?>

//public sealed interface LineWithLineIntersectionInSteps<out Number> {
//    public data object TheLinesAreParallel : LineWithLineIntersectionInSteps<Nothing>
//    public data object TheLinesAreCollinear: LineWithLineIntersectionInSteps<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Number>(val step1: Number, val step2: Number) : LineWithLineIntersectionInSteps<Number>
//}
//
////context(_: Field<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
////public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersectInSteps(other: Line2<N, VectorContent, PointContent>): LineWithLineIntersectionInSteps<N> {
////    val det = this.direction cross other.direction
////    val dif = other.start - this.start
////
////    if (det.isZero())
////        return if ((this.direction cross dif).isNotZero()) LineWithLineIntersectionInSteps.TheLinesAreParallel
////        else LineWithLineIntersectionInSteps.TheLinesAreCollinear
////
////    val t1 = (dif cross other.direction) / det
////    val t2 = (dif cross this.direction) / det
////    return LineWithLineIntersectionInSteps.TheLinesAreInGeneralPosition(t1, t2)
////}
//
//public sealed interface LineWithLineIntersection<out Point> {
//    public data object TheLinesAreParallel : LineWithLineIntersection<Nothing>
//    public data object TheLinesAreCollinear: LineWithLineIntersection<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Point>(val intersection: Point) : LineWithLineIntersection<Point>
//}
//
////context(_: Field<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
////public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersect(other: Line2<N, VectorContent, PointContent>): LineWithLineIntersection<N, PointContent> =
////    when (val resultInSteps = this.intersectInSteps(other)) {
////        LineWithLineIntersectionInSteps.TheLinesAreParallel ->
////            LineWithLineIntersection.TheLinesAreParallel
////        LineWithLineIntersectionInSteps.TheLinesAreCollinear ->
////            LineWithLineIntersection.TheLinesAreCollinear
////        is LineWithLineIntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
////            LineWithLineIntersection.TheLinesAreInGeneralPosition(this.start + this.direction * resultInSteps.step1)
////    }
//
//public sealed interface LineWithSegmentIntersectionInSteps<out Number> {
//    public data object TheLinesAreParallel : LineWithSegmentIntersectionInSteps<Nothing>
//    public data object TheLinesAreCollinear : LineWithSegmentIntersectionInSteps<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Number>(val step1: Number, val step2: Number) : LineWithSegmentIntersectionInSteps<Number>
//}
//
//public sealed interface LineWithSegmentIntersection<out Point> {
//    public data object TheLinesAreParallel : LineWithSegmentIntersection<Nothing>
//    public data object TheLinesAreCollinear : LineWithSegmentIntersection<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Point>(val intersection: Maybe<Point>) : LineWithSegmentIntersection<Point>
//}
//
////context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
////public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersect(other: Segment2<N, VectorContent, PointContent>): LineWithSegmentIntersection<N, VectorContent, PointContent> =
////    when (val resultInSteps = this.intersectInSteps(Line2(other.start, other.direction))) {
////        LineWithLineIntersectionInSteps.TheLinesAreParallel ->
////            LineWithSegmentIntersection.TheLinesAreParallel
////        LineWithLineIntersectionInSteps.TheLinesAreCollinear ->
////            LineWithSegmentIntersection.TheLinesAreCollinear(other)
////        is LineWithLineIntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
////            LineWithSegmentIntersection.TheLinesAreInGeneralPosition(
////                if (resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
////            )
////    }
//
//public sealed interface SegmentWithLineIntersectionInSteps<out Number> {
//    public data object TheLinesAreParallel : SegmentWithLineIntersectionInSteps<Nothing>
//    public data object TheLinesAreCollinear : SegmentWithLineIntersectionInSteps<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Number>(val step1: Number, val step2: Number) : SegmentWithLineIntersectionInSteps<Number>
//}
//
//public sealed interface SegmentWithLineIntersection<out Point> {
//    public data object TheLinesAreParallel : SegmentWithLineIntersection<Nothing>
//    public data object TheLinesAreCollinear : SegmentWithLineIntersection<Nothing>
//    public data class TheLinesAreInGeneralPosition<out Point>(val intersection: Maybe<Point>) : SegmentWithLineIntersection<Point>
//}
//
////context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
////public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2<N, VectorContent, PointContent>.intersect(other: Line2<N, VectorContent, PointContent>): SegmentWithLineIntersection<N, VectorContent, PointContent> =
////    when (val resultInSteps = Line2(this.start, this.direction).intersectInSteps(other)) {
////        LineWithLineIntersectionInSteps.TheLinesAreParallel ->
////            SegmentWithLineIntersection.TheLinesAreParallel
////        LineWithLineIntersectionInSteps.TheLinesAreCollinear ->
////            SegmentWithLineIntersection.TheLinesAreCollinear(this)
////        is LineWithLineIntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
////            SegmentWithLineIntersection.TheLinesAreInGeneralPosition(
////                if (resultInSteps.step1 in zero..one && resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
////            )
////    }
//
//public sealed interface SegmentWithSegmentIntersection<out Vector, out Point> {
//    public data object TheLinesAreParallel : SegmentWithSegmentIntersection<Nothing, Nothing>
//    public data class TheLinesAreCollinear<out Vector, out Point>(val intersection: Segment<Vector, Point>?): SegmentWithSegmentIntersection<Vector, Point>
//    public data class TheLinesAreInGeneralPosition<out Point>(val intersection: Maybe<Point>) : SegmentWithSegmentIntersection<Nothing, Point>
//}
//
////context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
////public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2<N, VectorContent, PointContent>.intersect(other: Segment2<N, VectorContent, PointContent>): SegmentWithSegmentIntersection<N, VectorContent, PointContent> =
////    when (val resultInSteps = Line2(this.start, this.direction).intersectInSteps(Line2(other.start, other.direction))) {
////        TheLinesAreParallel -> SegmentWithSegmentIntersection.TheLinesAreParallel
////        TheLinesAreCollinear -> {
////            val startMoment = ((other.start - this.start) dot this.direction) / (this.direction dot this.direction)
////            val endMoment = ((other.start + other.direction - this.start) dot this.direction) / (this.direction dot this.direction)
////
////            val intersectionStart = maxOf(startMoment, zero)
////            val intersectionEnd = minOf(endMoment, one)
////
////            SegmentWithSegmentIntersection.TheLinesAreCollinear(
////                if (intersectionStart leq intersectionEnd) Segment2(this.start + this.direction * intersectionStart, this.direction * (intersectionEnd - intersectionStart))
////                else null
////            )
////        }
////        is TheLinesAreInGeneralPosition<N> ->
////            SegmentWithSegmentIntersection.TheLinesAreInGeneralPosition(
////                if (resultInSteps.step1 in zero..one && resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
////            )
////    }
//
////public interface Linear2IntersectionsComputer<Number, Vector, Point> : KoneContext {
////    public fun intersectionOfInSteps(line1: Line<Vector, Point>, line2: Line<Vector, Point>): LineWithLineIntersectionInSteps<Number>
////    public fun intersectionOf(line1: Line<Vector, Point>, line2: Line<Vector, Point>): LineWithLineIntersection<Point>
////    public fun intersectionOf(line1: Line<Vector, Point>, segment2: Segment<Vector, Point>): LineWithSegmentIntersection<Vector, Point>
////    public fun intersectionOf(segment1: Segment<Vector, Point>, line2: Line<Vector, Point>): SegmentWithLineIntersection<Vector, Point>
////    public fun intersectionOf(segment1: Segment<Vector, Point>, segment2: Segment<Vector, Point>): SegmentWithSegmentIntersection<Vector, Point>
////}
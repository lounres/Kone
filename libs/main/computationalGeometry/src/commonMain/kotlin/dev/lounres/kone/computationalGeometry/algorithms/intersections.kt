/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.computationalGeometry.curves.Line2
import dev.lounres.kone.computationalGeometry.curves.Segment2
import dev.lounres.kone.computationalGeometry.utils.cross
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.contains
import dev.lounres.kone.relations.leq
import dev.lounres.kone.relations.maxOf
import dev.lounres.kone.relations.minOf
import dev.lounres.kone.relations.rangeTo


public sealed interface Line2WithLine2IntersectionInSteps<out N> {
    public data object TheLinesAreParallel : Line2WithLine2IntersectionInSteps<Nothing>
    public data object TheLinesAreCollinear: Line2WithLine2IntersectionInSteps<Nothing>
    public data class TheLinesAreInGeneralPosition<out N>(val step1: N, val step2: N) : Line2WithLine2IntersectionInSteps<N>
}

context(_: Field<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersectInSteps(other: Line2<N, VectorContent, PointContent>): Line2WithLine2IntersectionInSteps<N> {
    val det = this.direction cross other.direction
    val dif = other.start - this.start
    
    if (det.isZero())
        return if ((this.direction cross dif).isNotZero()) Line2WithLine2IntersectionInSteps.TheLinesAreParallel
        else Line2WithLine2IntersectionInSteps.TheLinesAreCollinear
    
    val t1 = (dif cross other.direction) / det
    val t2 = (dif cross this.direction) / det
    return Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition(t1, t2)
}

public sealed interface Line2WithLine2Intersection<out N, out PointContent: MDList1<N>> {
    public data object TheLinesAreParallel : Line2WithLine2Intersection<Nothing, Nothing>
    public data object TheLinesAreCollinear: Line2WithLine2Intersection<Nothing, Nothing>
    public data class TheLinesAreInGeneralPosition<out N, out PointContent: MDList1<N>>(val intersection: Point2<N, PointContent>) : Line2WithLine2Intersection<N, PointContent>
}

context(_: Field<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersect(other: Line2<N, VectorContent, PointContent>): Line2WithLine2Intersection<N, PointContent> =
    when (val resultInSteps = this.intersectInSteps(other)) {
        Line2WithLine2IntersectionInSteps.TheLinesAreParallel ->
            Line2WithLine2Intersection.TheLinesAreParallel
        Line2WithLine2IntersectionInSteps.TheLinesAreCollinear ->
            Line2WithLine2Intersection.TheLinesAreCollinear
        is Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
            Line2WithLine2Intersection.TheLinesAreInGeneralPosition(this.start + this.direction * resultInSteps.step1)
    }

public sealed interface Line2WithSegment2Intersection<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>> {
    public data object TheLinesAreParallel : Line2WithSegment2Intersection<Nothing, Nothing, Nothing>
    public data class TheLinesAreCollinear<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>>(val intersection: Segment2<N, VectorContent, PointContent>): Line2WithSegment2Intersection<N, VectorContent, PointContent>
    public data class TheLinesAreInGeneralPosition<out N, out PointContent: MDList1<N>>(val intersection: Point2<N, PointContent>?) : Line2WithSegment2Intersection<N, Nothing, PointContent>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Line2<N, VectorContent, PointContent>.intersect(other: Segment2<N, VectorContent, PointContent>): Line2WithSegment2Intersection<N, VectorContent, PointContent> =
    when (val resultInSteps = this.intersectInSteps(Line2(other.start, other.direction))) {
        Line2WithLine2IntersectionInSteps.TheLinesAreParallel ->
            Line2WithSegment2Intersection.TheLinesAreParallel
        Line2WithLine2IntersectionInSteps.TheLinesAreCollinear ->
            Line2WithSegment2Intersection.TheLinesAreCollinear(other)
        is Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
            Line2WithSegment2Intersection.TheLinesAreInGeneralPosition(
                if (resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
            )
    }

public sealed interface Segment2WithLine2Intersection<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>> {
    public data object TheLinesAreParallel : Segment2WithLine2Intersection<Nothing, Nothing, Nothing>
    public data class TheLinesAreCollinear<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>>(val intersection: Segment2<N, VectorContent, PointContent>): Segment2WithLine2Intersection<N, VectorContent, PointContent>
    public data class TheLinesAreInGeneralPosition<out N, out PointContent: MDList1<N>>(val intersection: Point2<N, PointContent>?) : Segment2WithLine2Intersection<N, Nothing, PointContent>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2<N, VectorContent, PointContent>.intersect(other: Line2<N, VectorContent, PointContent>): Segment2WithLine2Intersection<N, VectorContent, PointContent> =
    when (val resultInSteps = Line2(this.start, this.direction).intersectInSteps(other)) {
        Line2WithLine2IntersectionInSteps.TheLinesAreParallel ->
            Segment2WithLine2Intersection.TheLinesAreParallel
        Line2WithLine2IntersectionInSteps.TheLinesAreCollinear ->
            Segment2WithLine2Intersection.TheLinesAreCollinear(this)
        is Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
            Segment2WithLine2Intersection.TheLinesAreInGeneralPosition(
                if (resultInSteps.step1 in zero..one && resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
            )
    }

public sealed interface Segment2WithSegment2Intersection<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>> {
    public data object TheLinesAreParallel : Segment2WithSegment2Intersection<Nothing, Nothing, Nothing>
    public data class TheLinesAreCollinear<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>>(val intersection: Segment2<N, VectorContent, PointContent>?): Segment2WithSegment2Intersection<N, VectorContent, PointContent>
    public data class TheLinesAreInGeneralPosition<out N, out PointContent: MDList1<N>>(val intersection: Point2<N, PointContent>?) : Segment2WithSegment2Intersection<N, Nothing, PointContent>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2<N, VectorContent, PointContent>.intersect(other: Segment2<N, VectorContent, PointContent>): Segment2WithSegment2Intersection<N, VectorContent, PointContent> =
    when (val resultInSteps = Line2(this.start, this.direction).intersectInSteps(Line2(other.start, other.direction))) {
        TheLinesAreParallel -> Segment2WithSegment2Intersection.TheLinesAreParallel
        TheLinesAreCollinear -> {
            val startMoment = ((other.start - this.start) dot this.direction) / (this.direction dot this.direction)
            val endMoment = ((other.start + other.direction - this.start) dot this.direction) / (this.direction dot this.direction)
            
            val intersectionStart = maxOf(startMoment, zero)
            val intersectionEnd = minOf(endMoment, one)
            
            Segment2WithSegment2Intersection.TheLinesAreCollinear(
                if (intersectionStart leq intersectionEnd) Segment2(this.start + this.direction * intersectionStart, this.direction * (intersectionEnd - intersectionStart))
                else null
            )
        }
        is TheLinesAreInGeneralPosition<N> ->
            Segment2WithSegment2Intersection.TheLinesAreInGeneralPosition(
                if (resultInSteps.step1 in zero..one && resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
            )
    }
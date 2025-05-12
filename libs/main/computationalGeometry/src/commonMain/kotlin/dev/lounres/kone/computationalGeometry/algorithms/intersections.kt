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
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.contains
import dev.lounres.kone.relations.leq
import dev.lounres.kone.relations.max
import dev.lounres.kone.relations.min
import dev.lounres.kone.relations.rangeTo


public sealed interface Line2WithLine2IntersectionInSteps<out N> {
    public data object TheLinesAreParallel : Line2WithLine2IntersectionInSteps<Nothing>
    public data object TheLinesAreCollinear: Line2WithLine2IntersectionInSteps<Nothing>
    public data class TheLinesAreInGeneralPosition<N>(val step1: N, val step2: N) : Line2WithLine2IntersectionInSteps<N>
}

context(_: Field<N>, _: EuclideanKategory2<N>)
public fun <N> Line2<N>.intersectInSteps(other: Line2<N>): Line2WithLine2IntersectionInSteps<N> {
    val det = this.direction cross other.direction
    val dif = other.start - this.start
    
    if (det.isZero())
        return if ((this.direction cross dif).isNotZero()) Line2WithLine2IntersectionInSteps.TheLinesAreParallel
        else Line2WithLine2IntersectionInSteps.TheLinesAreCollinear
    
    val t1 = (dif cross other.direction) / det
    val t2 = (dif cross this.direction) / det
    return Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition(t1, t2)
}

public sealed interface Line2WithLine2Intersection<out N> {
    public data object TheLinesAreParallel : Line2WithLine2Intersection<Nothing>
    public data object TheLinesAreCollinear: Line2WithLine2Intersection<Nothing>
    public data class TheLinesAreInGeneralPosition<N>(val intersection: Point2<N>) : Line2WithLine2Intersection<N>
}

context(_: Field<N>, _: EuclideanKategory2<N>)
public fun <N> Line2<N>.intersect(other: Line2<N>): Line2WithLine2Intersection<N> =
    when (val resultInSteps = this.intersectInSteps(other)) {
        Line2WithLine2IntersectionInSteps.TheLinesAreParallel ->
            Line2WithLine2Intersection.TheLinesAreParallel
        Line2WithLine2IntersectionInSteps.TheLinesAreCollinear ->
            Line2WithLine2Intersection.TheLinesAreCollinear
        is Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
            Line2WithLine2Intersection.TheLinesAreInGeneralPosition(this.start + this.direction * resultInSteps.step1)
    }

public sealed interface Line2WithSegment2Intersection<out N> {
    public data object TheLinesAreParallel : Line2WithSegment2Intersection<Nothing>
    public data class TheLinesAreCollinear<N>(val intersection: Segment2<N>): Line2WithSegment2Intersection<N>
    public data class TheLinesAreInGeneralPosition<N>(val intersection: Point2<N>?) : Line2WithSegment2Intersection<N>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N>)
public fun <N> Line2<N>.intersect(other: Segment2<N>): Line2WithSegment2Intersection<N> =
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

public sealed interface Segment2WithLine2Intersection<out N> {
    public data object TheLinesAreParallel : Segment2WithLine2Intersection<Nothing>
    public data class TheLinesAreCollinear<N>(val intersection: Segment2<N>): Segment2WithLine2Intersection<N>
    public data class TheLinesAreInGeneralPosition<N>(val intersection: Point2<N>?) : Segment2WithLine2Intersection<N>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N>)
public fun <N> Segment2<N>.intersect(other: Line2<N>): Segment2WithLine2Intersection<N> =
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

public sealed interface Segment2WithSegment2Intersection<out N> {
    public data object TheLinesAreParallel : Segment2WithSegment2Intersection<Nothing>
    public data class TheLinesAreCollinear<N>(val intersection: Segment2<N>?): Segment2WithSegment2Intersection<N>
    public data class TheLinesAreInGeneralPosition<N>(val intersection: Point2<N>?) : Segment2WithSegment2Intersection<N>
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N>)
public fun <N> Segment2<N>.intersect(other: Segment2<N>): Segment2WithSegment2Intersection<N> =
    when (val resultInSteps = Line2(this.start, this.direction).intersectInSteps(Line2(other.start, other.direction))) {
        Line2WithLine2IntersectionInSteps.TheLinesAreParallel ->
            Segment2WithSegment2Intersection.TheLinesAreParallel
        Line2WithLine2IntersectionInSteps.TheLinesAreCollinear -> {
            val startMoment = ((other.start - this.start) dot this.direction) / (this.direction dot this.direction)
            val endMoment = ((other.start + other.direction - this.start) dot this.direction) / (this.direction dot this.direction)
            
            val intersectionStart = max(startMoment, zero)
            val intersectionEnd = min(endMoment, one)
            
            Segment2WithSegment2Intersection.TheLinesAreCollinear(
                if (intersectionStart leq intersectionEnd) Segment2(this.start + this.direction * intersectionStart, this.direction * (intersectionEnd - intersectionStart))
                else null
            )
        }
        is Line2WithLine2IntersectionInSteps.TheLinesAreInGeneralPosition<N> ->
            Segment2WithSegment2Intersection.TheLinesAreInGeneralPosition(
                if (resultInSteps.step1 in zero..one && resultInSteps.step2 in zero..one) this.start + this.direction * resultInSteps.step1 else null
            )
    }
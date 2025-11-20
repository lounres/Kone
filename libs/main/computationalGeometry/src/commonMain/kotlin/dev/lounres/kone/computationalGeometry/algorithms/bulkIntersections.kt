/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.build
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.computationalGeometry.AffineSpaceOverField
import dev.lounres.kone.computationalGeometry.curves.Line
import dev.lounres.kone.computationalGeometry.curves.Segment
import dev.lounres.kone.computationalGeometry.curves.end
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.orElse
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.maxOf
import kotlin.context


public data class Intersection<Vector, Point>(
    public val segmentIndex1: UInt,
    public val segment1: Segment<Vector, Point>,
    public val segmentIndex2: UInt,
    public val segment2: Segment<Vector, Point>,
    public val intersection: Point,
)

public interface PairwiseSegmentsIntersectionsComputer<Vector, Point> {
    public fun KoneList<Segment<Vector, Point>>.allPairwiseIntersections(): KoneSequence<Intersection<Vector, Point>>
}

private sealed interface EventForBentleyOttmann<out Point> {
    data class SegmentStart<out Point>(val segmentIndex: UInt, val start: Point): EventForBentleyOttmann<Point>
    data class SegmentEnd<out Point>(val segmentIndex: UInt, val end: Point): EventForBentleyOttmann<Point>
    data class SegmentsIntersection<out Point>(val segmentIndex1: UInt, val segmentIndex2: UInt, val intersection: Point): EventForBentleyOttmann<Point>
}

private data class SegmentNodeForBentleyOttmann<Point>(
    val segmentIndex: UInt,
    var upperIntersection: HeapNode<EventForBentleyOttmann<Point>, Point>? = null,
    var lowerIntersection: HeapNode<EventForBentleyOttmann<Point>, Point>? = null,
)

private fun <Point> removeIntersectionFor(sSegmentNode: SegmentNodeForBentleyOttmann<Point>, tSegmentNode: SegmentNodeForBentleyOttmann<Point>) {
    val stIntersectionNode = sSegmentNode.upperIntersection
    check(stIntersectionNode === tSegmentNode.lowerIntersection) { "For some reason neighbors in the segments search tree do not share the same intersection" }
    if (stIntersectionNode != null) {
        stIntersectionNode.remove()
        sSegmentNode.upperIntersection = null
        tSegmentNode.lowerIntersection = null
    }
}


// TODO: Think about cases of collinear segments
// TODO: Think about cases of concurrent segments and/or coincidence of events points
/**
 * https://en.wikipedia.org/wiki/Bentley%E2%80%93Ottmann_algorithm
 */
private class PairwiseSegmentsIntersectionsComputerViaBentleyOttmann<N, Vector, Point>(
    private val numberField: Field<N>,
    private val numberOrder: Order<N>,
    private val euclideanSpace: AffineSpaceOverField<N, Vector, Point>,
    private val basis: VectorSpaceBasis.Finite<N, Vector>,
    private val linear2IntersectionsComputer: Linear2IntersectionsComputer<N, Vector, Point>,
) : PairwiseSegmentsIntersectionsComputer<Vector, Point> {
    private val pointOrder: Order<Point> = context(numberOrder, euclideanSpace) {
        Order { left, right ->
            val differenceBasisDecomposition = basis.decompose(right - left)
            when (differenceBasisDecomposition[0u] compareWith numberField.zero) {
                LeftIsGreaterThanRight -> ComparisonResult.LeftIsGreaterThanRight
                LeftIsLessThanRight -> ComparisonResult.LeftIsLessThanRight
                Equal -> differenceBasisDecomposition[1u] compareWith numberField.zero
            }
        }
    }
    
    private fun addIntersectionFor(
        segmentsList: KoneList<Segment<Vector, Point>>,
        eventsHeap: MinimumHeap<EventForBentleyOttmann<Point>, Point>,
        currentPriority: Point,
        sSegmentNode: SegmentNodeForBentleyOttmann<Point>,
        tSegmentNode: SegmentNodeForBentleyOttmann<Point>
    ) {
        val sSegmentIndex = sSegmentNode.segmentIndex
        val tSegmentIndex = tSegmentNode.segmentIndex
        val sSegment = segmentsList[sSegmentIndex]
        val tSegment = segmentsList[tSegmentIndex]
        when (val intersectionResult = linear2IntersectionsComputer.intersectionOf(sSegment, tSegment)) {
            TheLinesAreParallel -> {}
            is TheLinesAreCollinear<Vector, Point> -> {} // TODO: Think about cases of collinear segments
            is TheLinesAreInGeneralPosition<Point> -> {
                val stIntersectionPoint = intersectionResult.intersection
                if (stIntersectionPoint is Some && pointOrder { stIntersectionPoint.value gt currentPriority }) {
                    val event = EventForBentleyOttmann.SegmentsIntersection(
                        segmentIndex1 = sSegmentIndex,
                        segmentIndex2 = tSegmentIndex,
                        intersection = stIntersectionPoint.value,
                    )
                    val eventHeapNode = eventsHeap.add(event, stIntersectionPoint.value)
                    sSegmentNode.upperIntersection = eventHeapNode
                    tSegmentNode.lowerIntersection = eventHeapNode
                }
            }
        }
    }
    
    override fun KoneList<Segment<Vector, Point>>.allPairwiseIntersections(): KoneSequence<Intersection<Vector, Point>> = context(numberOrder, euclideanSpace, pointOrder) {
        val segments = this
        val eventsHeap: MinimumHeap<EventForBentleyOttmann<Point>, Point> = KoneBinaryGCMinimumHeap(pointOrder)
        val segmentsSearchTree = ConnectedSearchTreeForBentleyOttmann<SegmentNodeForBentleyOttmann<Point>>()
        val segmentsSearchTreeNodes = KoneSettableList.generate<SearchTreeNodeForBentleyOttmann<SegmentNodeForBentleyOttmann<Point>>?>(segments.size) { null }
        
        for ((index, segment) in segments.withIndex()) {
            val structuralStart = segment.start
            val structuralEnd = segment.end
            val start: Point
            val end: Point
            if (structuralStart lt structuralEnd) {
                start = structuralStart
                end = structuralEnd
            } else {
                start = structuralEnd
                end = structuralStart
            }
            eventsHeap.add(
                element = EventForBentleyOttmann.SegmentStart(
                    segmentIndex = index,
                    start = start,
                ),
                priority = start,
            )
            eventsHeap.add(
                element = EventForBentleyOttmann.SegmentEnd(
                    segmentIndex = index,
                    end = end,
                ),
                priority = end,
            )
        }
        
        KoneSequence.build {
            while (eventsHeap.size != 0u) {
                val currentEventNode = eventsHeap.popMinimum()
                val currentEventPriority = currentEventNode.priority
                val currentEvent = currentEventNode.element
                
                when (currentEvent) {
                    is EventForBentleyOttmann.SegmentStart<Point> -> {
                        val (sSegmentIndex, start) = currentEvent
                        val sSegment = segments[sSegmentIndex]
                        val verticalLine = Line(start, basis[1u])
                        val sSegmentPointY =
                            when (val intersectionResult = linear2IntersectionsComputer.intersectionOf(verticalLine, sSegment)) {
                                TheLinesAreParallel -> error("For some reason sweeping line does not intersect segment in process")
                                is TheLinesAreCollinear<Vector, Point> -> maxOf(
                                    basis.decompose(intersectionResult.intersection.start - start)[1u],
                                    basis.decompose(intersectionResult.intersection.end - start)[1u],
                                )
                                is TheLinesAreInGeneralPosition<Point> -> basis.decompose(intersectionResult.intersection.orElse { error("For some reason sweeping line does not intersect segment in process") } - start)[1u]
                            }
                        val sNode = segmentsSearchTree.add(SegmentNodeForBentleyOttmann(sSegmentIndex)) { t ->
                            val tSegmentIndex = t.segmentIndex
                            val tSegment = segments[tSegmentIndex]
                            val tSegmentPointY: N =
                                when (val intersectionResult = linear2IntersectionsComputer.intersectionOf(verticalLine, tSegment)) {
                                    TheLinesAreParallel -> error("For some reason sweeping line does not intersect segment in process")
                                    is TheLinesAreCollinear<Vector, Point> -> maxOf(
                                        basis.decompose(intersectionResult.intersection.start - start)[1u],
                                        basis.decompose(intersectionResult.intersection.end - start)[1u],
                                    )
                                    is TheLinesAreInGeneralPosition<Point> -> basis.decompose(intersectionResult.intersection.orElse { error("For some reason sweeping line does not intersect segment in process") } - start)[1u]
                                }
                            
                            numberOrder { tSegmentPointY.compareTo(sSegmentPointY) }
                        }
                        segmentsSearchTreeNodes[sSegmentIndex] = sNode
                        val rNode = sNode.previousNode
                        val tNode = sNode.nextNode
                        if (rNode != null && tNode != null) removeIntersectionFor(rNode.element, tNode.element)
                        if (rNode != null) addIntersectionFor(
                            segments,
                            eventsHeap,
                            currentEventPriority,
                            rNode.element,
                            sNode.element
                        )
                        if (tNode != null) addIntersectionFor(
                            segments,
                            eventsHeap,
                            currentEventPriority,
                            sNode.element,
                            tNode.element
                        )
                    }
                    
                    is EventForBentleyOttmann.SegmentEnd<Point> -> {
                        val (sSegmentIndex, _) = currentEvent
                        val sNode = segmentsSearchTreeNodes[sSegmentIndex]!!
                        val rNode = sNode.previousNode
                        val tNode = sNode.nextNode
                        sNode.remove()
                        segmentsSearchTreeNodes[sSegmentIndex] = null
                        if (rNode != null) removeIntersectionFor(rNode.element, sNode.element)
                        if (tNode != null) removeIntersectionFor(sNode.element, tNode.element)
                        if (rNode != null && tNode != null) addIntersectionFor(
                            segments,
                            eventsHeap,
                            currentEventPriority,
                            rNode.element,
                            tNode.element
                        )
                    }
                    
                    is EventForBentleyOttmann.SegmentsIntersection<Point> -> {
                        val (sSegmentIndex, tSegmentIndex, int) = currentEvent
                        yield(
                            Intersection(
                                segmentIndex1 = sSegmentIndex,
                                segment1 = segments[sSegmentIndex],
                                segmentIndex2 = tSegmentIndex,
                                segment2 = segments[tSegmentIndex],
                                intersection = int,
                            )
                        )
                        val sNode = segmentsSearchTreeNodes[sSegmentIndex]!!
                        val tNode = segmentsSearchTreeNodes[tSegmentIndex]!!
                        check(sNode.nextNode === tNode) { "Event of segments intersection happened between not neighbor nodes" }
                        check(sNode.element.upperIntersection?.element === currentEvent) { "Event of segments intersection happened but lower segment upper intersection is not the event" }
                        check(tNode.element.lowerIntersection?.element === currentEvent) { "Event of segments intersection happened but upper segment lower intersection is not the event" }
                        sNode.element.upperIntersection = null
                        tNode.element.lowerIntersection = null
                        
                        val rNode = sNode.previousNode
                        val uNode = tNode.nextNode
                        
                        if (rNode != null) {
                            removeIntersectionFor(rNode.element, sNode.element)
                            addIntersectionFor(segments, eventsHeap, currentEventPriority, rNode.element, tNode.element)
                        }
                        if (uNode != null) {
                            removeIntersectionFor(tNode.element, uNode.element)
                            addIntersectionFor(segments, eventsHeap, currentEventPriority, sNode.element, uNode.element)
                        }
                        
                        sNode.element = tNode.element.also { tNode.element = sNode.element }
                        segmentsSearchTreeNodes[sSegmentIndex] = tNode
                        segmentsSearchTreeNodes[tSegmentIndex] = sNode
                    }
                }
                
                check(eventsHeap.size == 0u || eventsHeap.takeMinimum().priority >= currentEventPriority) { "For some reason minimum event priority did not increase" }
            }
        }
    }
}
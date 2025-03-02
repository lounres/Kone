/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.compareTo
import dev.lounres.kone.comparison.gt
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.computationalGeometry.EuclideanKategory2
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.curves.Segment2
import dev.lounres.kone.computationalGeometry.curves.end
import dev.lounres.kone.computationalGeometry.utils.lexicographic2DOrder
import dev.lounres.kone.context


public data class Intersection<I>(public val index1: UInt, public val index2: UInt, public val intersection: I)

internal sealed interface EventForBentleyOttmann<N> {
    data class SegmentStart<N>(val segmentIndex: UInt, val start: Point2<N>): EventForBentleyOttmann<N>
    data class SegmentEnd<N>(val segmentIndex: UInt, val end: Point2<N>): EventForBentleyOttmann<N>
    data class SegmentsIntersection<N>(val segmentIndex1: UInt, val segmentIndex2: UInt, val intersection: Point2<N>): EventForBentleyOttmann<N>
}

internal data class SegmentNodeForBentleyOttmann<N>(
    val segmentIndex: UInt,
    var upperIntersection: HeapNode<EventForBentleyOttmann<N>, Point2<N>>? = null,
    var lowerIntersection: HeapNode<EventForBentleyOttmann<N>, Point2<N>>? = null,
)

private fun <N> removeIntersectionFor(sSegmentNode: SegmentNodeForBentleyOttmann<N>, tSegmentNode: SegmentNodeForBentleyOttmann<N>) {
    val stIntersectionNode = sSegmentNode.upperIntersection
    check(stIntersectionNode === tSegmentNode.lowerIntersection) { "For some reason neighbors in the segments search tree do not share the same intersection" }
    if (stIntersectionNode != null) {
        stIntersectionNode.remove()
        sSegmentNode.upperIntersection = null
        tSegmentNode.lowerIntersection = null
    }
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N>, _: Order<Point2<N>>)
private fun <N> addIntersectionFor(
    segmentsList: KoneList<Segment2<N>>,
    eventsHeap: MinimumHeap<EventForBentleyOttmann<N>, Point2<N>>,
    currentPriority: Point2<N>,
    sSegmentNode: SegmentNodeForBentleyOttmann<N>,
    tSegmentNode: SegmentNodeForBentleyOttmann<N>
) {
    val sSegmentIndex = sSegmentNode.segmentIndex
    val tSegmentIndex = tSegmentNode.segmentIndex
    val sSegment = segmentsList[sSegmentIndex]
    val tSegment = segmentsList[tSegmentIndex]
    when (val intersectionResult = sSegment.intersect(tSegment)) {
        Segment2WithSegment2Intersection.TheLinesAreParallel -> {}
        is Segment2WithSegment2Intersection.TheLinesAreCollinear<N> -> {} // TODO: Think about cases of collinear segments
        is Segment2WithSegment2Intersection.TheLinesAreInGeneralPosition<N> -> {
            val stIntersectionPoint = intersectionResult.intersection
            if (stIntersectionPoint != null && stIntersectionPoint gt currentPriority) {
                val event = EventForBentleyOttmann.SegmentsIntersection(
                    segmentIndex1 = sSegmentIndex,
                    segmentIndex2 = tSegmentIndex,
                    intersection = stIntersectionPoint,
                )
                val eventHeapNode = eventsHeap.add(event, stIntersectionPoint)
                sSegmentNode.upperIntersection = eventHeapNode
                tSegmentNode.lowerIntersection = eventHeapNode
            }
        }
    }
}

// TODO: Think about cases of collinear segments
// TODO: Think about cases of concurrent segments and/or coincidence of events points
/**
 * https://en.wikipedia.org/wiki/Bentley%E2%80%93Ottmann_algorithm
 */
context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N>)
public fun <N> KoneList<Segment2<N>>.allIntersectionByBentleyOttmann(): KoneList<Intersection<Point2<N>>> {
    val pointsOrder = lexicographic2DOrder
    val eventsHeap: MinimumHeap<EventForBentleyOttmann<N>, Point2<N>> = KoneBinaryGCMinimumHeap(pointsOrder)
    val segmentsSearchTree: ConnectedSearchTreeForBentleyOttmann<SegmentNodeForBentleyOttmann<N>> = TwoThreeTreeForBentleyOttmann()
    val segmentsSearchTreeNodes = KoneSettableList<SearchTreeNodeForBentleyOttmann<SegmentNodeForBentleyOttmann<N>>?>(this.size) { null }
    
    context(pointsOrder) {
        for ((index, segment) in this.withIndex()) {
            val structuralStart = segment.start
            val structuralEnd = segment.end
            val start: Point2<N>
            val end: Point2<N>
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
        
        val intersections = KoneArrayGrowableList<Intersection<Point2<N>>>()
        
        while (eventsHeap.size != 0u) {
            val currentEventNode = eventsHeap.popMinimum()
            val currentEventPriority = currentEventNode.priority
            val currentEvent = currentEventNode.element
            
            when (currentEvent) {
                is EventForBentleyOttmann.SegmentStart<N> -> {
                    val (sSegmentIndex, start) = currentEvent
                    val sSegment = this[sSegmentIndex]
                    val sSegmentPointY =
                        if (sSegment.direction.x.isNotZero()) sSegment.start.y + sSegment.direction.y / sSegment.direction.x * (start.x - sSegment.start.x)
                        else start.y
                    val sNode = segmentsSearchTree.add(SegmentNodeForBentleyOttmann(sSegmentIndex)) { t ->
                        val tSegmentIndex = t.segmentIndex
                        val tSegment = this[tSegmentIndex]
                        val tY =
                            if (tSegment.direction.x.isNotZero()) tSegment.start.y + tSegment.direction.y / tSegment.direction.x * (start.x - tSegment.start.x)
                            else start.y
                        
                        tY.compareTo(sSegmentPointY)
                    }
                    segmentsSearchTreeNodes[sSegmentIndex] = sNode
                    val rNode = sNode.previousNode
                    val tNode = sNode.nextNode
                    if (rNode != null && tNode != null) removeIntersectionFor(rNode.element, tNode.element)
                    if (rNode != null) addIntersectionFor(
                        this,
                        eventsHeap,
                        currentEventPriority,
                        rNode.element,
                        sNode.element
                    )
                    if (tNode != null) addIntersectionFor(
                        this,
                        eventsHeap,
                        currentEventPriority,
                        sNode.element,
                        tNode.element
                    )
                }
                
                is EventForBentleyOttmann.SegmentEnd<N> -> {
                    val (sSegmentIndex, _) = currentEvent
                    val sNode = segmentsSearchTreeNodes[sSegmentIndex]!!
                    val rNode = sNode.previousNode
                    val tNode = sNode.nextNode
                    sNode.remove()
                    segmentsSearchTreeNodes[sSegmentIndex] = null
                    if (rNode != null) removeIntersectionFor(rNode.element, sNode.element)
                    if (tNode != null) removeIntersectionFor(sNode.element, tNode.element)
                    if (rNode != null && tNode != null) addIntersectionFor(
                        this,
                        eventsHeap,
                        currentEventPriority,
                        rNode.element,
                        tNode.element
                    )
                }
                
                is EventForBentleyOttmann.SegmentsIntersection<N> -> {
                    val (sSegmentIndex, tSegmentIndex, int) = currentEvent
                    intersections += Intersection(
                        index1 = sSegmentIndex,
                        index2 = tSegmentIndex,
                        intersection = int,
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
                        addIntersectionFor(this, eventsHeap, currentEventPriority, rNode.element, tNode.element)
                    }
                    if (uNode != null) {
                        removeIntersectionFor(tNode.element, uNode.element)
                        addIntersectionFor(this, eventsHeap, currentEventPriority, sNode.element, uNode.element)
                    }
                    
                    sNode.element = tNode.element.also { tNode.element = sNode.element }
                    segmentsSearchTreeNodes[sSegmentIndex] = tNode
                    segmentsSearchTreeNodes[tSegmentIndex] = sNode
                }
            }
            
            check(eventsHeap.size == 0u || eventsHeap.takeMinimum().priority >= currentEventPriority) { "For some reason minimum event priority did not increase" }
        }
        
        return intersections
    }
}

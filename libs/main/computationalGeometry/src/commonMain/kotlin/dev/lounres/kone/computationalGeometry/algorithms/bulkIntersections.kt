/*
 * Copyright © 2025 Gleb Minaev
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
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.computationalGeometry.EuclideanKategory2
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.curves.Segment2
import dev.lounres.kone.computationalGeometry.curves.end
import dev.lounres.kone.computationalGeometry.utils.lexicographic2DOrder
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1


public data class Intersection<I>(public val index1: UInt, public val index2: UInt, public val intersection: I)

internal sealed interface EventForBentleyOttmann<out N, out PointContent: MDList1<N>> {
    data class SegmentStart<out N, out PointContent: MDList1<N>>(val segmentIndex: UInt, val start: Point2<N, PointContent>): EventForBentleyOttmann<N, PointContent>
    data class SegmentEnd<out N, out PointContent: MDList1<N>>(val segmentIndex: UInt, val end: Point2<N, PointContent>): EventForBentleyOttmann<N, PointContent>
    data class SegmentsIntersection<out N, out PointContent: MDList1<N>>(val segmentIndex1: UInt, val segmentIndex2: UInt, val intersection: Point2<N, PointContent>): EventForBentleyOttmann<N, PointContent>
}

internal data class SegmentNodeForBentleyOttmann<N, PointContent: MDList1<N>>(
    val segmentIndex: UInt,
    var upperIntersection: HeapNode<EventForBentleyOttmann<N, PointContent>, Point2<N, PointContent>>? = null,
    var lowerIntersection: HeapNode<EventForBentleyOttmann<N, PointContent>, Point2<N, PointContent>>? = null,
)

private fun <N, PointContent: MDList1<N>> removeIntersectionFor(sSegmentNode: SegmentNodeForBentleyOttmann<N, PointContent>, tSegmentNode: SegmentNodeForBentleyOttmann<N, PointContent>) {
    val stIntersectionNode = sSegmentNode.upperIntersection
    check(stIntersectionNode === tSegmentNode.lowerIntersection) { "For some reason neighbors in the segments search tree do not share the same intersection" }
    if (stIntersectionNode != null) {
        stIntersectionNode.remove()
        sSegmentNode.upperIntersection = null
        tSegmentNode.lowerIntersection = null
    }
}

context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>, _: Order<Point2<N, PointContent>>)
private fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> addIntersectionFor(
    segmentsList: KoneList<Segment2<N, VectorContent, PointContent>>,
    eventsHeap: MinimumHeap<EventForBentleyOttmann<N, PointContent>, Point2<N, PointContent>>,
    currentPriority: Point2<N, PointContent>,
    sSegmentNode: SegmentNodeForBentleyOttmann<N, PointContent>,
    tSegmentNode: SegmentNodeForBentleyOttmann<N, PointContent>
) {
    val sSegmentIndex = sSegmentNode.segmentIndex
    val tSegmentIndex = tSegmentNode.segmentIndex
    val sSegment = segmentsList[sSegmentIndex]
    val tSegment = segmentsList[tSegmentIndex]
    when (val intersectionResult = sSegment.intersect(tSegment)) {
        TheLinesAreParallel -> {}
        is TheLinesAreCollinear<N, VectorContent, PointContent> -> {} // TODO: Think about cases of collinear segments
        is TheLinesAreInGeneralPosition<N, PointContent> -> {
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
context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> KoneList<Segment2<N, VectorContent, PointContent>>.allIntersectionByBentleyOttmann(): KoneList<Intersection<Point2<N, PointContent>>> {
    val pointsOrder = lexicographic2DOrder
    val eventsHeap: MinimumHeap<EventForBentleyOttmann<N, PointContent>, Point2<N, PointContent>> = KoneBinaryGCMinimumHeap(pointsOrder)
    val segmentsSearchTree: ConnectedSearchTreeForBentleyOttmann<SegmentNodeForBentleyOttmann<N, PointContent>> = TwoThreeTreeForBentleyOttmann()
    val segmentsSearchTreeNodes = KoneSettableList<SearchTreeNodeForBentleyOttmann<SegmentNodeForBentleyOttmann<N, PointContent>>?>(this.size) { null }
    
    pointsOrder {
        for ((index, segment) in this.withIndex()) {
            val structuralStart = segment.start
            val structuralEnd = segment.end
            val start: Point2<N, PointContent>
            val end: Point2<N, PointContent>
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
        
        val intersections = KoneArrayGrowableList<Intersection<Point2<N, PointContent>>>()
        
        while (eventsHeap.size != 0u) {
            val currentEventNode = eventsHeap.popMinimum()
            val currentEventPriority = currentEventNode.priority
            val currentEvent = currentEventNode.element
            
            when (currentEvent) {
                is EventForBentleyOttmann.SegmentStart<N, PointContent> -> {
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
                
                is EventForBentleyOttmann.SegmentEnd<N, PointContent> -> {
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
                
                is EventForBentleyOttmann.SegmentsIntersection<N, PointContent> -> {
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

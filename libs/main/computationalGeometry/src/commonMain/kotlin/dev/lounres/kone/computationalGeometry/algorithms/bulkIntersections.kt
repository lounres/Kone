/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneSettableIterableList
import dev.lounres.kone.collections.MinimumHeap
import dev.lounres.kone.collections.implementations.BinaryGCMinimumHeap
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Segment2
import dev.lounres.kone.computationalGeometry.end


public data class Intersection<I>(public val index1: UInt, public val index2: UInt, public val intersection: I)

internal sealed interface Event<N> {
    data class SegmentStart<N>(val segmentIndex: UInt, val start: Point2<N>): Event<N>
    data class SegmentEnd<N>(val segmentIndex: UInt, val end: Point2<N>): Event<N>
    data class SegmentsIntersection<N>(val segmentIndex1: UInt, val segmentIndex2: UInt, val intersection: Point2<N>): Event<N>
}

internal data class SegmentNode<N>(
    val segmentIndex: UInt,
    var upperIntersection: HeapNode<Event<N>, N>? = null,
    var lowerIntersection: HeapNode<Event<N>, N>? = null,
)

private fun <N, A> removeIntersectionFor(sSegmentNode: SegmentNode<N>, tSegmentNode: SegmentNode<N>) where A: Field<N>, A: Order<N> {
    val stIntersectionNode = sSegmentNode.upperIntersection
    check(stIntersectionNode === tSegmentNode.lowerIntersection) { "For some reason neighbors in the segments search tree do not share the same intersection" }
    if (stIntersectionNode != null) {
        stIntersectionNode.remove()
        sSegmentNode.upperIntersection = null
        tSegmentNode.lowerIntersection = null
    }
}

context(A, EuclideanKategory<N>)
private fun <N, A> addIntersectionFor(
    segmentsList: KoneIterableList<Segment2<N>>,
    eventsHeap: MinimumHeap<Event<N>, N>,
    currentPriority: N,
    sSegmentNode: SegmentNode<N>,
    tSegmentNode: SegmentNode<N>
) where A: Field<N>, A: Order<N> {
    val sSegmentIndex = sSegmentNode.segmentIndex
    val tSegmentIndex = tSegmentNode.segmentIndex
    val sSegment = segmentsList[sSegmentIndex]
    val tSegment = segmentsList[tSegmentIndex]
    val stIntersectionPoint = when (val intersectionResult = sSegment.intersect(tSegment)) {
        Segment2WithSegment2Intersection.TheLinesAreParallel -> null
        is Segment2WithSegment2Intersection.TheLinesAreCollinear<N> -> null // TODO: Think about cases of collinear segments
        is Segment2WithSegment2Intersection.TheLinesAreInGeneralPosition<N> -> intersectionResult.intersection
    }
    if (stIntersectionPoint != null && stIntersectionPoint.x > currentPriority) {
        val event = Event.SegmentsIntersection(
            segmentIndex1 = sSegmentIndex,
            segmentIndex2 = tSegmentIndex,
            intersection = stIntersectionPoint,
        )
        val eventHeapNode = eventsHeap.add(event, stIntersectionPoint.x)
        sSegmentNode.upperIntersection = eventHeapNode
        tSegmentNode.lowerIntersection = eventHeapNode
    }
}

// TODO: Think about cases of events with the same x coordinate
// TODO: Think about cases of vertical segments
// TODO: Think about cases of collinear segments
context(A, EuclideanKategory<N>)
public fun <N, A> KoneIterableList<Segment2<N>>.allIntersectionByBentleyOttmann(): KoneIterableList<Intersection<Point2<N>>> where A: Field<N>, A: Order<N> {
    val eventsHeap: MinimumHeap<Event<N>, N> = BinaryGCMinimumHeap(defaultEquality(), this@A)
    val segmentsSearchTree: ConnectedSearchTreeForBentleyOttmann<SegmentNode<N>> = TwoThreeTreeForBentleyOttmann()
    val segmentsSearchTreeNodes = KoneSettableIterableList<SearchTreeNodeForBentleyOttmann<SegmentNode<N>>?>(this.size) { null }
    
    for ((index, segment) in this.withIndex()) {
        val structuralStart = segment.start
        val structuralEnd = segment.end
        val start = if (structuralStart.x < structuralEnd.x) structuralStart else structuralEnd
        val end = if (structuralStart.x > structuralEnd.x) structuralStart else structuralEnd
        eventsHeap.add(
            element = Event.SegmentStart(
                segmentIndex = index,
                start = start,
            ),
            priority = start.x,
        )
        eventsHeap.add(
            element = Event.SegmentEnd(
                segmentIndex = index,
                end = end,
            ),
            priority = end.x,
        )
    }
    
    val intersections = KoneGrowableArrayList<Intersection<Point2<N>>>() // TODO: Add correct equality
    
    var index = 0u
    while (eventsHeap.size != 0u) {
        println(index++)
        val currentEventPriority = eventsHeap.takeMinimum().priority
        val currentEvent = eventsHeap.popMinimum().element
        println(currentEvent)
        
        if (index == 100u) TODO()
        
        when (currentEvent) {
            is Event.SegmentStart<N> -> {
                val (sSegmentIndex, start) = currentEvent
                val sNode = segmentsSearchTree.add(SegmentNode(sSegmentIndex)) { s, t ->
                    val sSegmentIndex = s.segmentIndex
                    val tSegmentIndex = t.segmentIndex
                    val sSegment = this[sSegmentIndex]
                    val tSegment = this[tSegmentIndex]
                    val sY = sSegment.start.y + sSegment.direction.y / sSegment.direction.x * (start.x - sSegment.start.x)
                    val tY = tSegment.start.y + tSegment.direction.y / tSegment.direction.x * (start.x - tSegment.start.x)
                    
                    sY.compareTo(tY)
                }
                segmentsSearchTreeNodes[sSegmentIndex] = sNode
                val rNode = sNode.previousNode
                val tNode = sNode.nextNode
                if (rNode != null && tNode != null) removeIntersectionFor(rNode.element, tNode.element)
                if (rNode != null) addIntersectionFor(this, eventsHeap, currentEventPriority, rNode.element, sNode.element)
                if (tNode != null) addIntersectionFor(this, eventsHeap, currentEventPriority, sNode.element, tNode.element)
            }
            is Event.SegmentEnd<N> -> {
                val (sSegmentIndex, _) = currentEvent
                val sNode = segmentsSearchTreeNodes[sSegmentIndex]!!
                val rNode = sNode.previousNode
                val tNode = sNode.nextNode
                sNode.remove()
                segmentsSearchTreeNodes[sSegmentIndex] = null
                if (rNode != null) removeIntersectionFor(rNode.element, sNode.element)
                if (tNode != null) removeIntersectionFor(sNode.element, tNode.element)
                if (rNode != null && tNode != null) addIntersectionFor(this, eventsHeap, currentEventPriority, rNode.element, tNode.element)
            }
            is Event.SegmentsIntersection<N> -> {
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
        
        check(eventsHeap.size == 0u || eventsHeap.takeMinimum().priority > currentEventPriority) { "For some reason minimum event priority did not increase" }
    }
    
    return intersections
}

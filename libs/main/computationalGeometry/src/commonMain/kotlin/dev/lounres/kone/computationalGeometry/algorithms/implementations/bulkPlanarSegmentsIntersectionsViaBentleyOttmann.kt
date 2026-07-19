/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.heap.isEmpty
import dev.lounres.kone.collections.heap.isNotEmpty
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.build
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.computationalGeometry.AffineSpaceOverField
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.algorithms.BulkPlanarSegmentsIntersectionsOverFieldComputer
import dev.lounres.kone.computationalGeometry.algorithms.SegmentBulkIntersectionOverFieldComputer
import dev.lounres.kone.computationalGeometry.algorithms.SegmentWithSegmentIntersectionOverField
import dev.lounres.kone.computationalGeometry.curves.Segment
import dev.lounres.kone.computationalGeometry.curves.end
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private sealed interface EventForBentleyOttmann<out Number, out Vector, out Point> {
    data class SegmentStart<out Point>(val segmentIndex: UInt, val start: Point): EventForBentleyOttmann<Nothing, Nothing, Point>
    data class SegmentEnd<out Point>(val segmentIndex: UInt, val end: Point): EventForBentleyOttmann<Nothing, Nothing, Point>
    data class SegmentsIntersection<out Number, out Vector, out Point>(
        val segmentIndex1: UInt,
        val segmentIndex2: UInt,
        val intersection: SegmentWithSegmentIntersectionOverField<Number, Vector, Point>
    ): EventForBentleyOttmann<Number, Vector, Point>
}

private data class SegmentNodeForBentleyOttmann<Number, Vector, Point>(
    val segmentIndex: UInt,
    var upperIntersection: HeapNode<EventForBentleyOttmann<Number, Vector, Point>, Point>? = null,
    var lowerIntersection: HeapNode<EventForBentleyOttmann<Number, Vector, Point>, Point>? = null,
)

private fun removeIntersectionFor(sSegmentNode: SegmentNodeForBentleyOttmann<*, *, *>, tSegmentNode: SegmentNodeForBentleyOttmann<*, *, *>) {
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
private class BulkPlanarSegmentsIntersectionsOverFieldComputerViaBentleyOttmann<Number, Vector, Point>(
    private val numberField: Field<Number>,
    private val numberOrder: Order<Number>,
    private val euclideanSpace: AffineSpaceOverField<Number, Vector, Point>,
) : BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point> {
    context(_: Order<Point>, intersectionComputer: SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point>)
    private fun addIntersectionFor(
        segmentsList: KoneList<Segment<Vector, Point>>,
        eventsHeap: MinimumHeap<EventForBentleyOttmann<Number, Vector, Point>, Point>,
        currentPriority: Point,
        sSegmentNode: SegmentNodeForBentleyOttmann<Number, Vector, Point>,
        tSegmentNode: SegmentNodeForBentleyOttmann<Number, Vector, Point>
    ) {
        val sSegmentIndex = sSegmentNode.segmentIndex
        val tSegmentIndex = tSegmentNode.segmentIndex
        val sSegment = segmentsList[sSegmentIndex]
        val tSegment = segmentsList[tSegmentIndex]
        when (val intersectionResult = intersectionComputer.intersect(sSegmentIndex, sSegment, tSegmentIndex, tSegment)) {
            null -> {}
            is Subsegment<Number, Vector, Point> -> TODO() // TODO: Think about cases of collinear segments
            is SinglePoint<Number, Point> -> {
                val stIntersectionPoint = intersectionResult.point
                if (stIntersectionPoint gt currentPriority) {
                    val event = EventForBentleyOttmann.SegmentsIntersection(
                        segmentIndex1 = sSegmentIndex,
                        segmentIndex2 = tSegmentIndex,
                        intersection = intersectionResult,
                    )
                    val eventHeapNode = eventsHeap.add(event, stIntersectionPoint)
                    sSegmentNode.upperIntersection = eventHeapNode
                    tSegmentNode.lowerIntersection = eventHeapNode
                }
            }
        }
    }
    
    override fun KoneList<Segment<Vector, Point>>.intersections(
        basis: VectorSpaceBasis.Finite<Number, Vector>,
        intersectionComputer: SegmentBulkIntersectionOverFieldComputer<Number, Vector, Point>,
    ): KoneSequence<BulkPlanarSegmentsIntersectionsOverFieldComputer.IntersectionResult<Number, Vector, Point>> = context(numberOrder, numberField, euclideanSpace, intersectionComputer) {
        KoneContext.unwrap(numberField, euclideanSpace)
        
        val pointOrder = Order<Point> { left, right ->
            val differenceBasisDecomposition = basis.decompose(left - right)
            numberOrder {
                when (differenceBasisDecomposition[0u] compareWith numberField.zero) {
                    LeftIsGreaterThanRight -> LeftIsGreaterThanRight
                    LeftIsLessThanRight -> LeftIsLessThanRight
                    Equal -> differenceBasisDecomposition[1u] compareWith numberField.zero
                }
            }
        }
        
        pointOrder {
            val segments = this@intersections
            
            KoneSequence.build {
                val eventsHeap: MinimumHeap<EventForBentleyOttmann<Number, Vector, Point>, Point> = KoneBinaryGCMinimumHeap(pointOrder)
                val segmentsSearchTree = ConnectedSearchTreeForBentleyOttmann<SegmentNodeForBentleyOttmann<Number, Vector, Point>>()
                val segmentsSearchTreeNodes =
                    KoneSettableList.generate<SearchTreeNodeForBentleyOttmann<SegmentNodeForBentleyOttmann<Number, Vector, Point>>?>(
                        segments.size
                    ) { null }
                
                for ((val index, val segment = value) in segments.withIndex()) {
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
                
                while (eventsHeap.isNotEmpty()) {
                    val currentEventNode = eventsHeap.popMinimum()
                    val currentEventPriority = currentEventNode.priority
                    val currentEvent = currentEventNode.element
                    
                    when (currentEvent) {
                        is EventForBentleyOttmann.SegmentStart<Point> -> {
                            (val sSegmentIndex = segmentIndex, val start) = currentEvent
                            val sNode = segmentsSearchTree.add(SegmentNodeForBentleyOttmann(sSegmentIndex)) { t ->
                                val tSegmentIndex = t.segmentIndex
                                val tSegment = segments[tSegmentIndex]
                                val startDecomposition = basis.decompose(tSegment.start - start)
                                val directionDecomposition = basis.decompose(tSegment.direction)
                                if (directionDecomposition[0u].isZero()) {
                                    if (startDecomposition[0u].isZero()) TODO() // TODO: Think about cases of "vertical" segments
                                    else error("For some reason sweeping line does not intersect segment in process")
                                }
                                val intersectionStep = -startDecomposition[0u] / directionDecomposition[0u]
                                if (intersectionStep !in numberField.zero .. numberField.one) error("For some reason sweeping line does not intersect segment in process")
                                (startDecomposition[1u] + directionDecomposition[1u] * intersectionStep).sign()
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
                                sNode.element,
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
                            val sSegmentIndex = currentEvent.segmentIndex
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
                        
                        is EventForBentleyOttmann.SegmentsIntersection<Number, Vector, Point> -> {
                            (val sSegmentIndex = segmentIndex1, val tSegmentIndex = segmentIndex2, val int = intersection) = currentEvent
                            yield(
                                BulkPlanarSegmentsIntersectionsOverFieldComputer.IntersectionResult(
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
                                addIntersectionFor(
                                    segments,
                                    eventsHeap,
                                    currentEventPriority,
                                    rNode.element,
                                    tNode.element
                                )
                            }
                            if (uNode != null) {
                                removeIntersectionFor(tNode.element, uNode.element)
                                addIntersectionFor(
                                    segments,
                                    eventsHeap,
                                    currentEventPriority,
                                    sNode.element,
                                    uNode.element
                                )
                            }
                            
                            sNode.element = tNode.element.also { tNode.element = sNode.element }
                            segmentsSearchTreeNodes[sSegmentIndex] = tNode
                            segmentsSearchTreeNodes[tSegmentIndex] = sNode
                        }
                    }
                    
                    check(eventsHeap.isEmpty() || eventsHeap.takeMinimum().priority > currentEventPriority) { "For some reason minimum event priority did not increase" }
                }
            }
        }
    }
}

public fun <Number, Vector, Point> BulkPlanarSegmentsIntersectionsOverFieldComputer.Companion.bentleyOttmann(
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    euclideanSpace: AffineSpaceOverField<Number, Vector, Point>,
): BulkPlanarSegmentsIntersectionsOverFieldComputer<Number, Vector, Point> =
    BulkPlanarSegmentsIntersectionsOverFieldComputerViaBentleyOttmann(
        numberField = numberField,
        numberOrder = numberOrder,
        euclideanSpace = euclideanSpace,
    )

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Vector, @Supply Point> BulkPlanarSegmentsIntersectionsOverFieldComputer.Companion.setBentleyOttmann() {
    BulkPlanarSegmentsIntersectionsOverFieldComputer.Key<Number, Vector, Point>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        bentleyOttmann(
            numberField = koneContextRegistry[Field.Key<Number>()],
            numberOrder = koneContextRegistry[Order.Key<Number>()],
            euclideanSpace = koneContextRegistry[EuclideanSpaceOverField.Key<Number, Vector, Point>()],
        )
    }
}
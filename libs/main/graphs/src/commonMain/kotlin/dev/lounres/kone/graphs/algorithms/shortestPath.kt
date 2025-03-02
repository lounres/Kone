/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.list.emptyKoneList
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.map.getMaybe
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.geq
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.graphs.EdgeWeightedGraphEdge
import dev.lounres.kone.graphs.EdgeWeightedGraphVertex
import dev.lounres.kone.graphs.minus
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import kotlin.jvm.JvmInline


@JvmInline
public value class Path<out Weight, out Edge>(public val totalWeight: Weight, public val edges: KoneList<Edge>) {
    public operator fun component1(): Weight = totalWeight
    public operator fun component2(): KoneList<Edge> = edges
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
context(_: Ring<Weight>, weightsOrder: Order<Weight>)
public fun <
    Weight,
    Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>,
    Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>
> shortestPathsMapByDijkstra(
    from: Vertex,
): KoneMap<Vertex, Path<Weight, Edge>> {
    val verticesToCheck = KoneBinaryGCMinimumHeap<Vertex, Weight>(weightsOrder)
    val queueNodes = koneMutableMapOf<Vertex, HeapNode<Vertex, Weight>>(absoluteEquality())
    val paths = koneMutableMapOf<Vertex, Path<Weight, Edge>>(absoluteEquality())
    
    queueNodes[from] = verticesToCheck.add(from, zero)
    paths[from] = Path(zero, emptyKoneList())
    
    while (verticesToCheck.size != 0u) {
        val currentVertex = verticesToCheck.popMinimum().element
        val (currentWeight, currentPath) = paths[currentVertex]
        for (edge in currentVertex.incidentEdges) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getMaybe(neighbor)
            val alternativePath = KoneList(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weight
            when(currentPathToNeighbor) {
                None -> {
                    paths[neighbor] = Path(alternativeWeight, alternativePath)
                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                }
                is Some<Path<Weight, Edge>> -> if (alternativeWeight lt currentPathToNeighbor.value.totalWeight) {
                    paths[neighbor] = Path(alternativeWeight, alternativePath)
                    queueNodes[neighbor].priority = alternativeWeight
                }
            }
        }
    }
    
    return paths
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
context(_: Ring<Weight>, weightsOrder: Order<Weight>)
public fun <
    Weight,
    Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>,
    Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>
> shortestPathByDijkstra(from: Vertex, to: Vertex): Path<Weight, Edge>? {
    val verticesToCheck = KoneBinaryGCMinimumHeap<Vertex, Weight>(weightsOrder)
    val queueNodes = koneMutableMapOf<Vertex, HeapNode<Vertex, Weight>>(absoluteEquality())
    val paths = koneMutableMapOf<Vertex, Path<Weight, Edge>>(absoluteEquality())
    var optimalPathToTarget: Path<Weight, Edge>? = null
    
    queueNodes[from] = verticesToCheck.add(from, zero)
    paths[from] = Path(zero, emptyKoneList())
    
    while (verticesToCheck.size != 0u) {
        val currentVertexNode = verticesToCheck.popMinimum()
        if (optimalPathToTarget != null && currentVertexNode.priority geq optimalPathToTarget.totalWeight) break
        
        val currentVertex = currentVertexNode.element
        val (currentWeight, currentPath) = paths[currentVertex]
        for (edge in currentVertex.incidentEdges) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getMaybe(neighbor)
            val alternativePath = KoneList(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weight
            when(currentPathToNeighbor) {
                None -> {
                    paths[neighbor] = Path(alternativeWeight, alternativePath)
                    if (neighbor === to) optimalPathToTarget = Path(alternativeWeight, alternativePath)
                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                }
                is Some<Path<Weight, Edge>> -> if (alternativeWeight lt currentPathToNeighbor.value.totalWeight) {
                    paths[neighbor] = Path(alternativeWeight, alternativePath)
                    if (neighbor === to) optimalPathToTarget = Path(alternativeWeight, alternativePath)
                    queueNodes[neighbor].priority = alternativeWeight
                }
            }
        }
    }
    
    return optimalPathToTarget
}
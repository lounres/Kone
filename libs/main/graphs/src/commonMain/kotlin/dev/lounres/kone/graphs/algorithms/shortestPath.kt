/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.algebraic.CommutativeMonoid
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.geq
import dev.lounres.kone.relations.lt
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.ends
import dev.lounres.kone.graphs.incidentEdgesOf
import dev.lounres.kone.graphs.minus
import dev.lounres.kone.graphs.weightOfType
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.suppliedTypes.SuppliedType


//@JvmInline
public /*value*/ data class Path<out Weight>(public val totalWeight: Weight, public val edges: KoneList<HypergraphEdge>)

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
context(weightMonoid: CommutativeMonoid<Weight>, weightsOrder: Order<Weight>)
public fun <Weight> Hypergraph.shortestPathsMapByDijkstra(weightType: SuppliedType, from: HypergraphVertex): KoneMap<HypergraphVertex, Path<Weight>> {
    val verticesToCheck = KoneBinaryGCMinimumHeap<HypergraphVertex, Weight>(weightsOrder)
    val queueNodes = KoneMutableMap.of<HypergraphVertex, HeapNode<HypergraphVertex, Weight>>(Equality.absoluteFor())
    val paths = KoneMutableMap.of<HypergraphVertex, Path<Weight>>(Equality.absoluteFor())
    
    queueNodes[from] = verticesToCheck.add(from, weightMonoid.zero)
    paths[from] = Path(weightMonoid.zero, KoneList.empty())
    
    while (verticesToCheck.size != 0u) {
        val currentVertex = verticesToCheck.popMinimum().element
        val (currentWeight, currentPath) = paths[currentVertex]
        for (edge in incidentEdgesOf(currentVertex)) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getOrNull(neighbor)
            val alternativePath = KoneList.generate(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weightOfType(weightType)
            if (currentPathToNeighbor == null) {
                paths[neighbor] = Path(alternativeWeight, alternativePath)
                queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
            } else {
                if (alternativeWeight lt currentPathToNeighbor.totalWeight) {
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
context(weightMonoid: CommutativeMonoid<Weight>, weightsOrder: Order<Weight>)
public fun <Weight> Hypergraph.shortestPathByDijkstra(weightType: SuppliedType, from: HypergraphVertex, to: HypergraphVertex): Path<Weight>? {
    val verticesToCheck = KoneBinaryGCMinimumHeap<HypergraphVertex, Weight>(weightsOrder)
    val queueNodes = KoneMutableMap.of<HypergraphVertex, HeapNode<HypergraphVertex, Weight>>(Equality.absoluteFor())
    val paths = KoneMutableMap.of<HypergraphVertex, Path<Weight>>(Equality.absoluteFor())
    var optimalPathToTarget: Path<Weight>? = null
    
    queueNodes[from] = verticesToCheck.add(from, weightMonoid.zero)
    paths[from] = Path(weightMonoid.zero, KoneList.empty())
    
    while (verticesToCheck.size != 0u) {
        val currentVertexNode = verticesToCheck.popMinimum()
        if (optimalPathToTarget != null && currentVertexNode.priority geq optimalPathToTarget.totalWeight) break
        
        val currentVertex = currentVertexNode.element
        val (currentWeight, currentPath) = paths[currentVertex]
        for (edge in incidentEdgesOf(currentVertex)) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getOrNull(neighbor)
            val alternativePath = KoneList.generate(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weightOfType(weightType)
            if (currentPathToNeighbor == null) {
                paths[neighbor] = Path(alternativeWeight, alternativePath)
                if (neighbor === to) optimalPathToTarget = Path(alternativeWeight, alternativePath)
                queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
            } else {
                if (alternativeWeight lt currentPathToNeighbor.totalWeight) {
                    paths[neighbor] = Path(alternativeWeight, alternativePath)
                    if (neighbor === to) optimalPathToTarget = Path(alternativeWeight, alternativePath)
                    queueNodes[neighbor].priority = alternativeWeight
                }
            }
        }
    }
    
    return optimalPathToTarget
}
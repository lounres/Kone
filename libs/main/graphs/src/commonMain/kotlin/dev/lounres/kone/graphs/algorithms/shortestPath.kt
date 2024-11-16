/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.emptyKoneList
import dev.lounres.kone.collections.get
import dev.lounres.kone.collections.getMaybe
import dev.lounres.kone.collections.implementations.BinaryGCMinimumHeap
import dev.lounres.kone.collections.koneMutableMapOf
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.geq
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.context.invoke
import dev.lounres.kone.graphs.EdgeWeightedGraph
import dev.lounres.kone.graphs.GraphWithContext
import dev.lounres.kone.graphs.minus
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import kotlin.jvm.JvmInline


@JvmInline
public value class Path<E, W>(public val edges: KoneList<E>, public val weight: W) {
    public operator fun component1(): KoneList<E> = edges
    public operator fun component2(): W = weight
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
context(G, WA)
public fun <V, E, W, G, WA> shortestPathsMapByDijkstra(from: V): KoneMap<V, Path<E, W>>
where G: EdgeWeightedGraph<V, E, W>, G: GraphWithContext<V, *, E, *>, WA: Ring<W>, WA: Order<W> {
    val verticesToCheck = BinaryGCMinimumHeap<V, W, WA>(this@WA)
    val queueNodes = koneMutableMapOf<V, HeapNode<V, W>>(vertexContext)
    val paths = koneMutableMapOf<V, Path<E, W>>(vertexContext)
    
    queueNodes[from] = verticesToCheck.add(from, zero)
    paths[from] = Path(emptyKoneList(), zero)
    
    while (verticesToCheck.size != 0u) {
        val currentVertex = verticesToCheck.popMinimum().element
        val (currentPath, currentWeight) = paths[currentVertex]
        for (edge in currentVertex.incidentEdges) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getMaybe(neighbor)
            val alternativePath = KoneList(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weight
            when(currentPathToNeighbor) {
                None -> {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                }
                is Some<Path<E, W>> -> if (alternativeWeight lt currentPathToNeighbor.value.weight) {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
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
context(G, WA)
public fun <V, E, W, G, WA> shortestPathByDijkstra(from: V, to: V): Path<E, W>?
where G: EdgeWeightedGraph<V, E, W>, G: GraphWithContext<V, *, E, *>, WA: Ring<W>, WA: Order<W> {
    val verticesToCheck = BinaryGCMinimumHeap<V, W, WA>(this@WA)
    val queueNodes = koneMutableMapOf<V, HeapNode<V, W>>(vertexContext)
    val paths = koneMutableMapOf<V, Path<E, W>>(vertexContext)
    var optimalPathToTarget: Path<E, W>? = null
    
    queueNodes[from] = verticesToCheck.add(from, zero)
    paths[from] = Path(emptyKoneList(), zero)
    
    while (verticesToCheck.size != 0u) {
        val currentVertexNode = verticesToCheck.popMinimum()
        if (optimalPathToTarget != null && currentVertexNode.priority geq optimalPathToTarget.weight) break
        
        val currentVertex = currentVertexNode.element
        val (currentPath, currentWeight) = paths[currentVertex]
        for (edge in currentVertex.incidentEdges) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getMaybe(neighbor)
            val alternativePath = KoneList(currentPath.size + 1u) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weight
            when(currentPathToNeighbor) {
                None -> {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
                    if (vertexContext { neighbor eq to }) optimalPathToTarget = Path(alternativePath, alternativeWeight)
                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                }
                is Some<Path<E, W>> -> if (alternativeWeight lt currentPathToNeighbor.value.weight) {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
                    if (vertexContext { neighbor eq to }) optimalPathToTarget = Path(alternativePath, alternativeWeight)
                    queueNodes[neighbor].priority = alternativeWeight
                }
            }
        }
    }
    
    return optimalPathToTarget
}
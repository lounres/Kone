/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.emptyKoneIterableList
import dev.lounres.kone.collections.implementations.BinaryGCMinimumHeap
import dev.lounres.kone.collections.koneIterableListEquality
import dev.lounres.kone.collections.koneMutableMapOf
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.graphs.EdgeWeightedGraph
import dev.lounres.kone.graphs.GraphWithContext
import dev.lounres.kone.graphs.minus
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import kotlin.jvm.JvmInline


@JvmInline
public value class Path<E, W>(public val edges: KoneIterableList<E>, public val weight: W) {
    public operator fun component1(): KoneIterableList<E> = edges
    public operator fun component2(): W = weight
}

context(G, WA)
public fun <V, E, W, G, WA> shortestPathsMapByDijkstra(from: V): KoneMap<V, Path<E, W>>
where G: EdgeWeightedGraph<V, E, W>, G: GraphWithContext<V, *, E, *>, WA: Ring<W>, WA: Order<W> {
    val pathsEquality = koneIterableListEquality(edgeContext)
    
    val verticesToCheck = BinaryGCMinimumHeap(defaultEquality<V>(), this@WA)
    val queueNodes = koneMutableMapOf(vertexContext, defaultEquality<HeapNode<V, W>>())
    val paths = koneMutableMapOf(
        vertexContext,
        Equality<Path<E, W>> { left, right ->
            pathsEquality.invoke { left.edges eq right.edges } && left.weight eq right.weight
        }
    )
    
    queueNodes[from] = verticesToCheck.add(from, zero)
    paths[from] = Path(emptyKoneIterableList(), zero)
    
    while (verticesToCheck.size != 0u) {
        val currentVertex = verticesToCheck.popMinimum().element
        val (currentPath, currentWeight) = paths[currentVertex]
        for (edge in currentVertex.incidentEdges) {
            val neighbor = edge.ends - currentVertex
            val currentPathToNeighbor = paths.getMaybe(neighbor)
            val alternativePath = KoneIterableList(currentPath.size + 1u, edgeContext) { if (it < currentPath.size) currentPath[it] else edge }
            val alternativeWeight = currentWeight + edge.weight
            when(currentPathToNeighbor) {
                None -> {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                }
                is Some<Path<E, W>> -> if (alternativeWeight < currentPathToNeighbor.value.weight) {
                    paths[neighbor] = Path(alternativePath, alternativeWeight)
                    queueNodes[neighbor].priority = alternativeWeight
                }
            }
        }
    }
    
    return paths
}
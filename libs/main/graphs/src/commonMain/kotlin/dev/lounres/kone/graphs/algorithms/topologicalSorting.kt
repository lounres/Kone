/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.end
import dev.lounres.kone.graphs.incomingDegreeOf
import dev.lounres.kone.graphs.outgoingIncidentEdgesOf
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor


/**
 * Topological sort of oriented acyclic graph.
 */
public fun Hypergraph.sortVerticesTopologicallyByKahn(): KoneList<HypergraphVertex> {
    val verticesToProcess = KoneBinaryGCMinimumHeap<HypergraphVertex, UInt>(UInt.context)
    val result = KoneArrayFixedCapacityList<HypergraphVertex>(vertices.size)
    val verticesNodes = KoneMutableMap.of<HypergraphVertex, HeapNode<HypergraphVertex, UInt>>(Equality.defaultFor())
    
    for (vertex in vertices) verticesNodes[vertex] = verticesToProcess.add(vertex, incomingDegreeOf(vertex))
    
    while (verticesToProcess.size != 0u) {
        val currentVertexNode = verticesToProcess.popMinimum()
        val currentPriority = currentVertexNode.priority
        if (currentPriority != 0u) throw IllegalArgumentException("Cannot topologically sort a graph with cycles by Kahn's algorithm")
        
        val currentVertex = currentVertexNode.element
        verticesNodes.remove(currentVertex)
        result.add(currentVertex)
        for (edge in outgoingIncidentEdgesOf(currentVertex)) {
            val nextVertex = edge.end
            val nextVertexNode = verticesNodes[nextVertex]
            check(nextVertexNode.priority > 0u) { "Attempt to decrease in-degree that is already zero" }
            nextVertexNode.priority -= 1u
        }
    }
    
    return result
}
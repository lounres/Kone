/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.implementations.BinaryGCMinimumHeap
import dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList
import dev.lounres.kone.collections.koneMutableMapOf
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultOrder
import dev.lounres.kone.graphs.DigraphWithContext


public fun <V, E, G> G.sortVerticesTopologicallyByKahn(): KoneIterableList<V> where G: DigraphWithContext<V, *, E, *> {
    val verticesToProcess = BinaryGCMinimumHeap(vertexContext, defaultOrder<UInt>())
    val result = KoneFixedCapacityArrayList(vertices.size, vertexContext)
    val verticesNodes = koneMutableMapOf(vertexContext, defaultEquality<HeapNode<V, UInt>>())
    
    for (vertex in vertices) verticesNodes[vertex] = verticesToProcess.add(vertex, vertex.indegree)
    
    while (verticesToProcess.size != 0u) {
        val currentPriority = verticesToProcess.takeMinimum().priority
        if (currentPriority != 0u) throw IllegalArgumentException("Cannot topologically sort a graph with cycles by Kahn's algorithm")
        
        val currentVertex = verticesToProcess.popMinimum().element
        verticesNodes.remove(currentVertex)
        for (edge in currentVertex.outgoingEdges) {
            val nextVertex = edge.tail
            val nextVertexNode = verticesNodes[nextVertex]
            check(nextVertexNode.priority > 0u) { "Attempt to decrease indegree that is already zero" }
            nextVertexNode.priority -= 1u
        }
    }
    
    return result
}
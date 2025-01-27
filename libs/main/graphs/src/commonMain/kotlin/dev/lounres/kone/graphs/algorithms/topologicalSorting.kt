/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.heap.implementations.KoneGCBinaryMinimumHeap
import dev.lounres.kone.collections.map.koneMutableMapOf
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.defaultOrder
import dev.lounres.kone.graphs.Digraph
import dev.lounres.kone.graphs.DigraphVertex


public fun <Vertex: DigraphVertex<Vertex, *>> Digraph<Vertex, *>.sortVerticesTopologicallyByKahn(): KoneList<Vertex> {
    val verticesToProcess = KoneGCBinaryMinimumHeap<Vertex, UInt, Order<UInt>>(defaultOrder<UInt>())
    val result = KoneArrayFixedCapacityList<Vertex>(vertices.size)
    val verticesNodes = koneMutableMapOf<Vertex, HeapNode<Vertex, UInt>>(absoluteEquality())
    
    for (vertex in vertices) verticesNodes[vertex] = verticesToProcess.add(vertex, vertex.inDegree)
    
    while (verticesToProcess.size != 0u) {
        val currentVertexNode = verticesToProcess.popMinimum()
        val currentPriority = currentVertexNode.priority
        if (currentPriority != 0u) throw IllegalArgumentException("Cannot topologically sort a graph with cycles by Kahn's algorithm")
        
        val currentVertex = currentVertexNode.element
        verticesNodes.remove(currentVertex)
        for (edge in currentVertex.outgoingEdges) {
            val nextVertex = edge.tail
            val nextVertexNode = verticesNodes[nextVertex]
            check(nextVertexNode.priority > 0u) { "Attempt to decrease in-degree that is already zero" }
            nextVertexNode.priority -= 1u
        }
    }
    
    return result
}
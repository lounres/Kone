/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.isNotEmpty
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.algorithms.TopologicalSortingComputer
import dev.lounres.kone.graphs.algorithms.TopologicallySortedVertices
import dev.lounres.kone.graphs.algorithms.adjacentVerticesOf
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


/**
 * Topological sort of oriented acyclic graph.
 */
private object TopologicalSortingComputerViaDepthFirstSearch : TopologicalSortingComputer {
    private val nonExistentVertex = HypergraphVertex()
    private data class SearchLevel(var state: HypergraphVertex = nonExistentVertex, val nextElementsIterator: KoneIterator<HypergraphVertex>)
    
    override fun Hypergraph.sortVerticesTopologically(): TopologicallySortedVertices {
        val result = KoneArrayFixedCapacityList<HypergraphVertex>(vertices.size)
        
        // If vertex is absent, it is 'visited'.
        // If vertex is associated with 'true', it is in process of visiting.
        // If vertex is associated with 'false', it has not been met before.
        val isVertexMarked = KoneMutableMap.of<HypergraphVertex, Boolean>(Equality.absoluteFor())
        
        for (vertex in vertices) isVertexMarked[vertex] = false
        
        while (isVertexMarked.isNotEmpty()) {
            val startVertex = isVertexMarked.keysView.first()
            
            val stack = KoneArrayGrowableList<SearchLevel>()
            stack.add(SearchLevel(nextElementsIterator = KoneArray.of(startVertex).iterator()))
            
            while (stack.isNotEmpty()) {
                val lastLevel = stack.last()
                if (lastLevel.state !== nonExistentVertex) {
                    val lastLevelStateNode = isVertexMarked.getNodeOrNull(lastLevel.state)
                    check(lastLevelStateNode != null) { "For some reason, node in progress was marked as 'visited'." }
                    lastLevelStateNode.remove()
                    result += lastLevel.state
                }
                if (lastLevel.nextElementsIterator.hasNext()) {
                    val nextVertex = lastLevel.nextElementsIterator.getAndMoveNext()
                    lastLevel.state = nextVertex
                    val nextVertexNode = isVertexMarked.getNodeOrNull(nextVertex)
                    when {
                        nextVertexNode == null -> continue
                        nextVertexNode.value -> throw IllegalArgumentException("Cannot topologically sort a graph with cycles via depth-first search.")
                        else -> nextVertexNode.value = true
                    }
                    stack.add(SearchLevel(nextElementsIterator = adjacentVerticesOf(nextVertex).iterator()))
                } else {
                    stack.removeAt(stack.lastIndex)
                }
            }
        }
        
        return TopologicallySortedVertices(startToEnd = result)
    }
}

public fun TopologicalSortingComputer.Companion.depthFirstSearch(): TopologicalSortingComputer = TopologicalSortingComputerViaDepthFirstSearch

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun TopologicalSortingComputer.Companion.setDepthFirstSearch() {
    TopologicalSortingComputer.Key correspondsTo depthFirstSearch()
}
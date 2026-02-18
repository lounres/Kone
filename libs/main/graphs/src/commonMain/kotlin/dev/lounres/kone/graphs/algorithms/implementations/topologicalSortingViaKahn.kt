/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.isNotEmpty
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.graphs.*
import dev.lounres.kone.graphs.algorithms.TopologicalSortingComputer
import dev.lounres.kone.graphs.algorithms.TopologicallySortedVertices
import dev.lounres.kone.graphs.algorithms.incomingDegreeOf
import dev.lounres.kone.graphs.algorithms.outgoingIncidentEdgesOf
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


/**
 * Topological sort of oriented acyclic graph.
 */
private object TopologicalSortingComputerByKahn : TopologicalSortingComputer {
    override fun Hypergraph.sortVerticesTopologically(): TopologicallySortedVertices {
        val result = KoneArrayFixedCapacityList<HypergraphVertex>(vertices.size)
        val vertexIncomingDegrees = KoneMutableMap.of<HypergraphVertex, UInt>(Equality.absoluteFor())
        
        for (vertex in vertices) vertexIncomingDegrees[vertex] = incomingDegreeOf(vertex)
        
        var noIncomingDegreeVertices = KoneSet.build(Equality.absoluteFor()) {
            for (node in vertexIncomingDegrees.nodesView) if (node.value == 0u) {
                +node.key
                node.remove()
            }
        }
        
        while (noIncomingDegreeVertices.isNotEmpty()) {
            result.addAllFrom(noIncomingDegreeVertices)
            
            noIncomingDegreeVertices = KoneSet.build(Equality.absoluteFor()) {
                for (v in noIncomingDegreeVertices) for (e in outgoingIncidentEdgesOf(v)) {
                    val u = e.end
                    val uNode = vertexIncomingDegrees.getNodeOrNull(u)
                    check(uNode != null) { "Vertex was removed before all its incoming edges were removed." }
                    check(uNode.value != 0u) { "Vertex with zero indegree was not removed and its indegree is going to be decreased even more." }
                    uNode.value -= 1u
                    if (uNode.value == 0u) {
                        +uNode.key
                        uNode.remove()
                    }
                }
            }
        }
        
        if (vertexIncomingDegrees.isNotEmpty()) throw IllegalArgumentException("Cannot topologically sort a graph with cycles by Kahn's algorithm.")
        
        return TopologicallySortedVertices(startToEnd = result)
    }
}

public fun TopologicalSortingComputer.Companion.kahn(): TopologicalSortingComputer = TopologicalSortingComputerByKahn

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun TopologicalSortingComputer.Companion.setKahn() {
    TopologicalSortingComputer.Key correspondsTo kahn()
}
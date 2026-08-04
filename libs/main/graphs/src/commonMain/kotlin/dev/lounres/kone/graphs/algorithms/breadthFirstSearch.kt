/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterable.isNotEmpty
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.sequence.KoneSequence
import dev.lounres.kone.collections.sequence.build
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.end
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


public class BreadthFirstSearch @PublishedApi internal constructor(
    startVertex: HypergraphVertex,
    public val events: KoneSequence<Event>,
) {
    public val exploredVertices: KoneReifiedSet<HypergraphVertex> by lazy {
        KoneReifiedSet.build(elementEquality = Equality.absoluteFor()) {
            +startVertex
            for (event in events) +event.newVertex
        }
    }
    
    public data class Event(
        val currentExploredVertices: KoneReifiedSet<HypergraphVertex>,
        val vertexInProcess: HypergraphVertex,
        val newVertex: HypergraphVertex,
    )
}

public fun Hypergraph.breadthFirstSearch(startVertex: HypergraphVertex): BreadthFirstSearch =
    BreadthFirstSearch(
        startVertex = startVertex,
        events = KoneSequence.build {
            val queue = KoneListBackedDeque<HypergraphVertex>()
            queue.addLast(startVertex)
            val exploredVertices = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor<HypergraphVertex>())
            while (queue.isNotEmpty()) {
                val v = queue.popFirst()
                exploredVertices.add(v)
                for (u in adjacentVerticesOf(v)) if (u !in exploredVertices) {
                    yield(
                        BreadthFirstSearch.Event(
                            currentExploredVertices = exploredVertices,
                            vertexInProcess = v,
                            newVertex = u,
                        )
                    )
                    queue.addLast(u)
                }
            }
        },
    )

public fun Hypergraph.breadthFirstDirectedSearch(startVertex: HypergraphVertex): BreadthFirstSearch =
    BreadthFirstSearch(
        startVertex = startVertex,
        events = KoneSequence.build {
            val queue = KoneListBackedDeque<HypergraphVertex>()
            queue.addLast(startVertex)
            val exploredVertices = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor<HypergraphVertex>())
            while (queue.isNotEmpty()) {
                val v = queue.popFirst()
                exploredVertices.add(v)
                for (e in outgoingIncidentEdgesOf(v)) if (e.end !in exploredVertices) {
                    yield(
                        BreadthFirstSearch.Event(
                            currentExploredVertices = exploredVertices,
                            vertexInProcess = v,
                            newVertex = e.end,
                        )
                    )
                    queue.addLast(e.end)
                }
            }
        },
    )

public fun Hypergraph.connectedComponentOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphVertex> =
    this.breadthFirstSearch(vertex).exploredVertices

public fun Hypergraph.connectedComponents(): KoneList<KoneReifiedSet<HypergraphVertex>> =
    KoneList.build {
        val vertices = vertices.toKoneMutableSet(elementEquality = Equality.absoluteFor())
        
        while (vertices.isNotEmpty()) {
            val newComponent = connectedComponentOf(vertices.first())
            vertices.removeAllFrom(newComponent)
            add(newComponent)
        }
    }

public fun Hypergraph.vertexToConnectedComponentMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>> =
    KoneReifiedMap.build(keyEquality = Equality.absoluteFor()) {
        val vertices = vertices.toKoneMutableSet(elementEquality = Equality.absoluteFor())
        
        while (vertices.isNotEmpty()) {
            val newComponent = connectedComponentOf(vertices.first())
            vertices.removeAllFrom(newComponent)
            for (vertex in newComponent) set(vertex, newComponent)
        }
    }
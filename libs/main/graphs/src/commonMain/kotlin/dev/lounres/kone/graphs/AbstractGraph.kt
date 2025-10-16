/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.set.KoneMutableNoddedSet
import dev.lounres.kone.collections.set.KoneMutableSetNode
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.buildKoneSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableLinkedNoddedSet
import dev.lounres.kone.collections.set.koneMutableSetOf
import dev.lounres.kone.collections.utils.filterTo
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.defaultHashing
import kotlin.uuid.Uuid


public class AbstractGraphVertex internal constructor(
    graph: AbstractGraph,
) : RemovableGraphVertex<AbstractGraphVertex, AbstractGraphEdge> {
    private val id: Uuid = Uuid.random()
    
    private val graphNode: KoneMutableSetNode<AbstractGraphVertex> = graph.registerVertex(this)
    
    private val _incidentEdges: KoneMutableNoddedSet<AbstractGraphEdge> = KoneListBackedMutableLinkedNoddedSet(absoluteEquality())
    override val incidentEdges: KoneSet<AbstractGraphEdge> get() = _incidentEdges
    override val adjacentVertices: KoneSet<AbstractGraphVertex>
        get() = incidentEdges.mapTo(koneMutableSetOf(elementEquality = absoluteEquality())) { it.ends - this }
    
    internal fun registerEdge(edge: AbstractGraphEdge): KoneMutableSetNode<AbstractGraphEdge> =
        _incidentEdges.addNode(edge)
    
    override fun remove() {
        incidentEdges.toKoneList().forEach { it.remove() }
        graphNode.remove()
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytopicConstructionPolytope:${id.toHexString()}"
}

public class AbstractGraphEdge internal constructor(
    graph: AbstractGraph,
    private val startVertex: AbstractGraphVertex,
    private val endVertex: AbstractGraphVertex,
) : RemovableGraphEdge<AbstractGraphVertex, AbstractGraphEdge> {
    private val id: Uuid = Uuid.random()
    
    override val ends: EdgeEnds<AbstractGraphVertex> = EdgeEnds(startVertex, endVertex)
    
    private val edgeNode: KoneMutableSetNode<AbstractGraphEdge> = graph.registerEdge(this)
    private val startNode: KoneMutableSetNode<AbstractGraphEdge> = startVertex.registerEdge(this)
    private val endNode: KoneMutableSetNode<AbstractGraphEdge> = startVertex.registerEdge(this)
    
    override val adjacentEdges: KoneSet<AbstractGraphEdge>
        get() = buildKoneSet(elementEquality = absoluteEquality(), elementHashing = Hashing.defaultFor()) {
            startVertex.incidentEdges.filterTo(this) { it !== this@AbstractGraphEdge }
            endVertex.incidentEdges.filterTo(this) { it !== this@AbstractGraphEdge }
        }
    
    override fun remove() {
        edgeNode.remove()
        startNode.remove()
        endNode.remove()
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytopicConstructionPolytope:${id.toHexString()}"
}

public class AbstractGraph : MutableGraph<AbstractGraphVertex, AbstractGraphEdge> {
    private val _vertices: KoneMutableNoddedSet<AbstractGraphVertex> = KoneListBackedMutableLinkedNoddedSet(absoluteEquality())
    override val vertices: KoneSet<AbstractGraphVertex> get() = _vertices
    
    internal fun registerVertex(vertex: AbstractGraphVertex): KoneMutableSetNode<AbstractGraphVertex> = _vertices.addNode(vertex)
    
    private val _edges: KoneMutableNoddedSet<AbstractGraphEdge> = KoneListBackedMutableLinkedNoddedSet(absoluteEquality())
    override val edges: KoneSet<AbstractGraphEdge> get() = _edges
    
    internal fun registerEdge(edge: AbstractGraphEdge): KoneMutableSetNode<AbstractGraphEdge> = _edges.addNode(edge)
    
    override fun addVertex(): AbstractGraphVertex = AbstractGraphVertex(this)
    override fun addEdge(head: AbstractGraphVertex, tail: AbstractGraphVertex): AbstractGraphEdge = AbstractGraphEdge(this, head, tail)
}
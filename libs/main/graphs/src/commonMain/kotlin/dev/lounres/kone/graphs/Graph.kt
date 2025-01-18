/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.set.KoneReifiedSet


public interface GraphVertex<out Vertex: GraphVertex<Vertex, Edge>, out Edge: GraphEdge<Vertex, Edge>> {
    public val incidentEdges: KoneReifiedSet<Edge>
    public val adjacentVertices: KoneReifiedSet<Vertex>
    public val degree: UInt
        get() = incidentEdges.size
}

public interface GraphEdge<out Vertex: GraphVertex<Vertex, Edge>, out Edge: GraphEdge<Vertex, Edge>> {
    public val ends: EdgeEnds<Vertex>
    public val adjacentEdges: KoneReifiedSet<Edge>
}

public interface Graph<out Vertex: GraphVertex<Vertex, Edge>, out Edge: GraphEdge<Vertex, Edge>> {
    public val vertices: KoneReifiedSet<Vertex>
    public val edges: KoneReifiedSet<Edge>
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.set.KoneReifiedSet


public interface DigraphVertex<out Vertex: DigraphVertex<Vertex, Edge>, out Edge: DigraphEdge<Vertex, Edge>> : GraphVertex<Vertex, Edge> {
    public val outgoingEdges: KoneReifiedSet<Edge>
    public val incomingEdges: KoneReifiedSet<Edge>
    public val outDegree: UInt
        get() = outgoingEdges.size
    public val inDegree: UInt
        get() = incomingEdges.size
}

public interface DigraphEdge<out Vertex: DigraphVertex<Vertex, Edge>, out Edge: DigraphEdge<Vertex, Edge>> : GraphEdge<Vertex, Edge> {
    public val tail: Vertex // from
    public val head: Vertex // to
}

public interface Digraph<out Vertex: DigraphVertex<Vertex, Edge>, out Edge: DigraphEdge<Vertex, Edge>> : Graph<Vertex, Edge>
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs


public interface EdgeWeightedGraphVertex<out Weight, out Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>> : GraphVertex<Vertex, Edge>

public interface EdgeWeightedGraphEdge<out Weight, out Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>> : GraphEdge<Vertex, Edge> {
    public val weight: Weight
}

public interface EdgeWeightedGraph<out Weight, out Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>> : Graph<Vertex, Edge>
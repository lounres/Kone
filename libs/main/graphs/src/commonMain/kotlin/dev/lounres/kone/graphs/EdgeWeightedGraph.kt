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

public interface ExtendableEdgeWeightedGraph<out Weight, out Vertex: EdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: EdgeWeightedGraphEdge<Weight, Vertex, Edge>> : EdgeWeightedGraph<Weight, Vertex, Edge> {
    public fun addVertex(): Vertex
    public fun addEdge(head: @UnsafeVariance Vertex, tail: @UnsafeVariance Vertex): Edge
}

public interface RemovableEdgeWeightedGraphVertex<out Weight, out Vertex: RemovableEdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: RemovableEdgeWeightedGraphEdge<Weight, Vertex, Edge>> : EdgeWeightedGraphVertex<Weight, Vertex, Edge> {
    public fun remove()
}

public interface RemovableEdgeWeightedGraphEdge<out Weight, out Vertex: RemovableEdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: RemovableEdgeWeightedGraphEdge<Weight, Vertex, Edge>> : EdgeWeightedGraphEdge<Weight, Vertex, Edge> {
    public fun remove()
}

public interface ReducibleEdgeWeightedGraph<out Weight, out Vertex: RemovableEdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: RemovableEdgeWeightedGraphEdge<Weight, Vertex, Edge>> : EdgeWeightedGraph<Weight, Vertex, Edge>

public interface MutableEdgeWeightedGraph<out Weight, out Vertex: RemovableEdgeWeightedGraphVertex<Weight, Vertex, Edge>, out Edge: RemovableEdgeWeightedGraphEdge<Weight, Vertex, Edge>> : ExtendableEdgeWeightedGraph<Weight, Vertex, Edge>, ReducibleEdgeWeightedGraph<Weight, Vertex, Edge>
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs


public interface Graph<V, E> {
    public val vertices: Set<V>
    public val edges: Set<E>

    public val V.incidentEdges: Set<E>
    public val V.adjacentVertices: Set<V>

    public val E.ends: Pair<V, V>
    public val E.adjacentEdges: Set<E> get() = ends.first.incidentEdges + ends.second.incidentEdges
}

public interface Digraph<V, E>: Graph<V, E> {
    public val E.head: V
    public val E.tail: V
}

public inline fun <V, E> Graph<V, E>.breadthFirstSearch(start: V, onEach: (V) -> Unit): List<V> {
    val queue = ArrayDeque<V>()
    queue.add(start)
    val result = ArrayList<V>(vertices.size)
    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        result.add(v)
        for (u in v.adjacentVertices) if (u !in result) queue.add(u)
        onEach(v)
    }
    return result
}
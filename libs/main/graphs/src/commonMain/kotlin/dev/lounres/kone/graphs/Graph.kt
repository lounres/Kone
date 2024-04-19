/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.buildKoneIterableSet
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext


public interface Graph<V, E> : KoneContext {
    public val vertexContext: Equality<V>
    public val edgeContext: Equality<E>

    public val vertices: KoneIterableSet<V>
    public val edges: KoneIterableSet<E>

    public val V.incidentEdges: KoneIterableSet<E>
    public val V.adjacentVertices: KoneIterableSet<V>
    public val V.degree: UInt

    public val E.ends: Pair<V, V>
    public val E.adjacentEdges: KoneIterableSet<E>
        get() = buildKoneIterableSet(edgeContext) {
            val ends = ends
            addAllFrom(ends.first.incidentEdges)
            addAllFrom(ends.second.incidentEdges)
        }
}

public interface Digraph<V, E> : Graph<V, E> {
    public val V.outgoingEdges: Set<E>
    public val V.incomingEdges: Set<E>
    public val V.adjacentOutgoingVertices: Set<V>
    public val V.adjacentIncomingVertices: Set<V>
    public val V.outdegree: UInt
    public val V.indegree: UInt

    public val E.head: V
    public val E.tail: V
}

//context(Graph<V, E>)
//public inline fun <V, E> V.breadthFirstSearch(onEach: (vertex: V) -> Unit) {
//    val queue = ArrayDeque<V>()
//    queue.add(this)
//    val exploredVertices = HashSet<V>(vertices.size)
//    while (queue.isNotEmpty()) {
//        val v = queue.removeFirst()
//        exploredVertices.add(v)
//        for (u in v.adjacentVertices) if (u !in exploredVertices) queue.add(u)
//        onEach(v)
//    }
//}

//context(Graph<V, E>)
//public inline fun <V, E> V.depthFirstSearch(onEach: (vertex: V) -> Unit) {
//    val stack = ArrayDeque<Iterator<V>>()
//    stack.add(iterator { yield(this@V) })
//    val exploredVertices = HashSet<V>(vertices.size)
//    while (stack.isNotEmpty()) {
//        val lastIterator = stack.last()
//        if (lastIterator.hasNext()) {
//            val nextVertex = lastIterator.next()
//            if (nextVertex in exploredVertices) continue
//            onEach(nextVertex)
//            exploredVertices.add(nextVertex)
//            stack.add(nextVertex.adjacentVertices.iterator())
//        } else {
//            stack.removeLast()
//        }
//    }
//}
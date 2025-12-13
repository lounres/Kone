/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.adjacentVerticesOf
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


public inline fun Hypergraph.breadthFirstSearch(startVertex: HypergraphVertex, onEach: (previousVertex: HypergraphVertex, vertex: HypergraphVertex) -> Unit) {
    val queue = KoneListBackedDeque<HypergraphVertex>()
    queue.addLast(startVertex)
    val exploredVertices = KoneMutableSet.of(elementEquality = Equality.absoluteFor<HypergraphVertex>())
    while (queue.isNotEmpty()) {
        val v = queue.popFirst()
        exploredVertices.add(v)
        for (u in adjacentVerticesOf(v)) if (u !in exploredVertices) {
            onEach(v, u)
            queue.addLast(u)
        }
    }
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms


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
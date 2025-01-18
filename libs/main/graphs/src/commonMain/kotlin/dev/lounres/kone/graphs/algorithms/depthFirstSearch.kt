/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms


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
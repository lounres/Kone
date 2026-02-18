/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms


// TODO: Repeat after BFS

//@PublishedApi
//internal data class SearchLevel(var state: HypergraphVertex, val nextElementsIterator: KoneIterator<HypergraphVertex>)
//
//public inline fun Hypergraph.depthFirstSearch(start: HypergraphVertex, onEach: (stack: KoneList<HypergraphVertex>) -> Unit) {
//    val stack = KoneArrayGrowableList<SearchLevel>()
//    stack.add(SearchLevel(start, KoneArray.of(start).iterator()))
//    val exploredVertices = KoneMutableSet.of(elementEquality = Equality.absoluteFor<HypergraphVertex>())
//    while (stack.isNotEmpty()) {
//        val lastLevel = stack.last()
//        if (lastLevel.nextElementsIterator.hasNext()) {
//            val nextVertex = lastLevel.nextElementsIterator.getAndMoveNext()
//            lastLevel.state = nextVertex
//            if (nextVertex in exploredVertices) continue
//            onEach(stack.map { it.state })
//            exploredVertices.add(nextVertex)
//            stack.add(SearchLevel(nextVertex, adjacentVerticesOf(nextVertex).iterator()))
//        } else {
//            stack.removeAt(stack.lastIndex)
//        }
//    }
//}
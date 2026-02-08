/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.adjacentVerticesOf
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


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
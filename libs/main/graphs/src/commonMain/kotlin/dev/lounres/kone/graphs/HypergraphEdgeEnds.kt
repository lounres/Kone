/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSetIterator


@OptIn(DelicateCollectionsInheritanceAPI::class)
public data class HypergraphEdgeEnds(val vertex1: HypergraphVertex, val vertex2: HypergraphVertex) : KoneReifiedSet<HypergraphVertex> {
    override val size: UInt = if (vertex1 === vertex2) 1u else 2u
    
    override fun contains(element: @UnsafeVariance HypergraphVertex): Boolean =
        element === vertex1 || element === vertex2
    
    override fun iterator(): KoneSetIterator<HypergraphVertex> =
        if (size == 1u) SingleIterator(vertex1)
        else CoupleIterator(vertex1, vertex2)
    
    internal class SingleIterator<V>(val end: V) : KoneSetIterator<V> {
        var index: UInt = 0u
        override fun hasNext(): Boolean = index == 0u
        override fun getNext(): V =
            when (index) {
                0u -> end
                else -> noNextElementInIteratorException()
            }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            index++
        }
    }
    
    internal class CoupleIterator<V>(val start: V, val end: V) : KoneSetIterator<V> {
        var index: UInt = 0u
        override fun hasNext(): Boolean = index <= 1u
        override fun getNext(): V =
            when (index) {
                0u -> start
                1u -> end
                else -> noNextElementInIteratorException()
            }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            index++
        }
    }
}

public operator fun HypergraphEdgeEnds.minus(vertex: HypergraphVertex): HypergraphVertex =
    when {
        vertex === vertex1 -> vertex2
        vertex === vertex2 -> vertex1
        else -> error("The vertex $vertex is not part of the ends collection")
    }
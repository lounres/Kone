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
public data class EdgeEnds<out Vertex>(val vertex1: Vertex, val vertex2: Vertex) : KoneReifiedSet<Vertex> {
    override val size: UInt = if (vertex1 === vertex2) 1u else 2u
    
    override fun contains(element: @UnsafeVariance Vertex): Boolean =
        element === vertex1 || element === vertex2
    
    override fun iterator(): KoneSetIterator<Vertex> =
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

public operator fun <Vertex> EdgeEnds<Vertex>.minus(vertex: Vertex): Vertex =
    when {
        vertex === vertex1 -> vertex2
        vertex === vertex2 -> vertex1
        else -> error("The vertex $vertex is not part of the ends collection")
    }
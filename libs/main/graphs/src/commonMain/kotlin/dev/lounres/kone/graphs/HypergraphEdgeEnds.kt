/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.array.toKoneArray
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSetIterator
import dev.lounres.kone.registry.RegistryKey


@OptIn(DelicateCollectionsInheritanceAPI::class)
public data class HypergraphEdgeEnds(val vertex1: HypergraphVertex, val vertex2: HypergraphVertex) : KoneReifiedSet<HypergraphVertex> {
    override val size: UInt = if (vertex1 === vertex2) 1u else 2u
    
    override fun contains(element: @UnsafeVariance HypergraphVertex): Boolean =
        element === vertex1 || element === vertex2
    
    override fun iterator(): KoneSetIterator<HypergraphVertex> =
        if (size == 1u) SingleIterator(vertex1)
        else CoupleIterator(vertex1, vertex2)
    
    public companion object;
    
    private class SingleIterator<V>(private val end: V) : KoneSetIterator<V> {
        private var index: UInt = 0u
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
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[index = $index]"
    }
    
    private class CoupleIterator<V>(private val start: V, private val end: V) : KoneSetIterator<V> {
        private var index: UInt = 0u
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
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[index = $index]"
    }
}

public operator fun HypergraphEdgeEnds.minus(vertex: HypergraphVertex): HypergraphVertex =
    when {
        vertex === vertex1 -> vertex2
        vertex === vertex2 -> vertex1
        else -> error("The vertex $vertex is not part of the ends collection")
    }

public val HypergraphEdge.ends: HypergraphEdgeEnds
    get() {
        require(vertices.size == 2u) { TODO() }
        val vertices = vertices.toKoneArray()
        return HypergraphEdgeEnds(vertices[0u], vertices[1u])
    }

public enum class GraphEdgeDirection {
    FromFirstToSecond, FromSecondToFirst;
    
    public object Key : RegistryKey<GraphEdgeDirection>
}

public val HypergraphEdge.start: HypergraphVertex
    get() {
        require(vertices.size == 2u) { TODO() }
        val vertices = vertices.toKoneArray()
        return when (properties[GraphEdgeDirection.Key]) {
            FromFirstToSecond -> vertices[0u]
            FromSecondToFirst -> vertices[1u]
        }
    }

public val HypergraphEdge.end: HypergraphVertex
    get() {
        require(vertices.size == 2u) { TODO() }
        val vertices = vertices.toKoneArray()
        return when (properties[GraphEdgeDirection.Key]) {
            FromFirstToSecond -> vertices[1u]
            GraphEdgeDirection.FromSecondToFirst -> vertices[0u]
        }
    }
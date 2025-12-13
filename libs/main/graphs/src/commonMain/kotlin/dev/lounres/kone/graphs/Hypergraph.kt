/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.KoneMutableReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface Hypergraph {
    public val vertices: KoneReifiedSet<HypergraphVertex>
    public val edges: KoneReifiedSet<HypergraphEdge>
    
    public val properties: Registry get() = Registry.Empty
    
    public companion object
}

public interface MutableHypergraph : Hypergraph {
    public fun add(vertex: HypergraphVertex)
    public fun add(edge: HypergraphEdge)
    public fun remove(vertex: HypergraphVertex)
    public fun remove(edge: HypergraphEdge)
    
    override val properties: MutableRegistry
}

public interface HypergraphBuilder : MutableHypergraph {
    public operator fun HypergraphVertex.unaryPlus() { add(this) }
    public operator fun HypergraphEdge.unaryPlus() { add(this) }
    public operator fun HypergraphVertex.unaryMinus() { remove(this) }
    public operator fun HypergraphEdge.unaryMinus() { remove(this) }
}

@PublishedApi
internal class HypergraphBuilderImpl : HypergraphBuilder {
    override val vertices: KoneMutableReifiedSet<HypergraphVertex> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    override val edges: KoneMutableReifiedSet<HypergraphEdge> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    
    override fun add(vertex: HypergraphVertex) {
        vertices.add(vertex)
    }
    override fun add(edge: HypergraphEdge) {
        if (edge !in edges) {
            edges.add(edge)
            vertices.addAllFrom(edge.vertices)
        }
    }
    override fun remove(vertex: HypergraphVertex) {
        if (vertex in vertices) {
            vertices.remove(vertex)
            edges.removeAllThat { (Equality.absoluteFor<HypergraphVertex>()) { vertex in it.vertices } }
        }
    }
    override fun remove(edge: HypergraphEdge) {
        edges.remove(edge)
    }
    
    override val properties: MutableRegistry = MutableRegistry().apply {
    }
}

public inline fun Hypergraph.Companion.build(block: HypergraphBuilder.() -> Unit): Hypergraph {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return HypergraphBuilderImpl().apply {
        block()
        properties.apply {
            val vertexToIncidentEdgesMapping = KoneMutableReifiedMap.of<HypergraphVertex, KoneMutableReifiedSet<HypergraphEdge>>(
                keyEquality = Equality.absoluteFor(),
            )
            val vertexToAdjacentVerticesMapping = KoneMutableReifiedMap.of<HypergraphVertex, KoneMutableReifiedSet<HypergraphVertex>>(
                keyEquality = Equality.absoluteFor(),
            )
            
            for (vertex in vertices) {
                vertexToIncidentEdgesMapping[vertex] = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
                vertexToAdjacentVerticesMapping[vertex] = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
            }
            for (edge in edges) for (vertex in vertices) {
                vertexToIncidentEdgesMapping[vertex].add(edge)
                vertexToAdjacentVerticesMapping[vertex].addAllFrom(edge.vertices)
            }
            
            VertexToIncidentEdgesMapping correspondsTo vertexToIncidentEdgesMapping
            VertexToAdjacentVerticesMapping correspondsTo vertexToAdjacentVerticesMapping
        }
    }
}
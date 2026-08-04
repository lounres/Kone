/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.iterable.contains
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


public data object VertexToAdjacentVerticesMappingKey : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMapping"
}

public fun interface VertexToAdjacentVerticesMappingComputer : KoneContext {
    public fun Hypergraph.vertexToAdjacentVerticesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>>
    
    public companion object;
    
    public data object Key : RegistryKey<VertexToAdjacentVerticesMappingComputer> {
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMappingComputer.Key"
    }
}

context(vertexToAdjacentVerticesMappingComputer: VertexToAdjacentVerticesMappingComputer)
public fun Hypergraph.vertexToAdjacentVerticesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>> =
    with(vertexToAdjacentVerticesMappingComputer) { this@vertexToAdjacentVerticesMapping.vertexToAdjacentVerticesMapping() }

public fun Hypergraph.adjacentVerticesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphVertex> {
    val vertexToAdjacentVerticesMapping = this.properties.getOrNull(VertexToAdjacentVerticesMappingKey)
    if (vertexToAdjacentVerticesMapping != null) return vertexToAdjacentVerticesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) {
        val result = KoneMutableReifiedSet.of<HypergraphVertex>(
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
        for (edge in vertexToEdgesMapping[vertex])
            for (otherVertex in edge.vertices)
                if (vertex !== otherVertex)
                    result.add(otherVertex)
        return result
    }
    val result = KoneMutableReifiedSet.of<HypergraphVertex>(
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )
    for (edge in edges)
        if ((Equality.absoluteFor<HypergraphVertex>()) { vertex in edge.vertices })
            for (otherVertex in edge.vertices)
                if (vertex !== otherVertex)
                    result.add(otherVertex)
    return result
}
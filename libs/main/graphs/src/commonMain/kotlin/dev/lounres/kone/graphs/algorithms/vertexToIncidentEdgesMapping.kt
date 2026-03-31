/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.filterTo
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


public data object VertexToIncidentEdgesMappingKey : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingKey"
}

public fun interface VertexToIncidentEdgesMappingComputer : KoneContext {
    public fun Hypergraph.vertexToIncidentEdgesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>
    
    public companion object;
    
    public data object Key : RegistryKey<VertexToIncidentEdgesMappingComputer> {
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingComputer.Key"
    }
}

context(vertexToIncidentEdgesMappingComputer: VertexToIncidentEdgesMappingComputer)
public fun Hypergraph.vertexToIncidentEdgesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>> =
    with(vertexToIncidentEdgesMappingComputer) { this@vertexToIncidentEdgesMapping.vertexToIncidentEdgesMapping() }

public fun Hypergraph.incidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex]
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { (Equality.absoluteFor<HypergraphVertex>()) { vertex in it.vertices } }
}
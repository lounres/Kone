/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.count
import dev.lounres.kone.collections.utils.filterTo
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.end
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


public data object VertexToIncomingIncidentEdgesMappingKey : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToIncomingIncidentEdgesMapping"
}

public fun Hypergraph.incomingIncidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToIncomingEdgesMapping = this.properties.getOrNull(VertexToIncomingIncidentEdgesMappingKey)
    if (vertexToIncomingEdgesMapping != null) return vertexToIncomingEdgesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.end === vertex }
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.end === vertex }
}

public fun Hypergraph.incomingDegreeOf(vertex: HypergraphVertex): UInt {
    val vertexToIncomingEdgesMapping = this.properties.getOrNull(VertexToIncomingIncidentEdgesMappingKey)
    if (vertexToIncomingEdgesMapping != null) return vertexToIncomingEdgesMapping[vertex].size
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].count { it.end === vertex }
    return edges.count { it.end === vertex }
}
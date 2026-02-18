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
import dev.lounres.kone.graphs.start
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


public data object VertexToOutgoingIncidentEdgesMappingKey : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToOutgoingIncidentEdgesMapping"
}

public fun Hypergraph.outgoingIncidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToOutgoingEdgesMapping = this.properties.getOrNull(VertexToOutgoingIncidentEdgesMappingKey)
    if (vertexToOutgoingEdgesMapping != null) return vertexToOutgoingEdgesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.start === vertex }
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.start === vertex }
}

public fun Hypergraph.outgoingDegreeOf(vertex: HypergraphVertex): UInt {
    val vertexToOutgoingEdgesMapping = this.properties.getOrNull(VertexToOutgoingIncidentEdgesMappingKey)
    if (vertexToOutgoingEdgesMapping != null) return vertexToOutgoingEdgesMapping[vertex].size
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMappingKey)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].count { it.start === vertex }
    return edges.count { it.start === vertex }
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.array.toKoneArray
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.count
import dev.lounres.kone.collections.utils.filterTo
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmName


public val OwnedRegistry<HypergraphVertex>.name: String
    @JvmName("getHypergraphVertexOwnedRegistryName") get() = get(HypergraphVertex.NameKey)

public var MutableOwnedRegistry<HypergraphVertex>.name: String
    @JvmName("getHypergraphVertexMutableOwnedRegistryName") get() = get(HypergraphVertex.NameKey)
    @JvmName("setHypergraphVertexMutableOwnedRegistryName") set(value) { set(HypergraphVertex.NameKey, value) }

public var OwnedRegistryBuilder<HypergraphVertex>.name: String
    @JvmName("getHypergraphVertexOwnedRegistryBuilderName") get() = get(HypergraphVertex.NameKey)
    @JvmName("setHypergraphVertexOwnedRegistryBuilderName") set(value) { set(HypergraphVertex.NameKey, value) }

public val HypergraphVertex.name: String
    @JvmName("getHypergraphVertexName") get() = properties.name

public val OwnedRegistry<HypergraphEdge>.name: String
    @JvmName("getHypergraphEdgeOwnedRegistryName") get() = get(HypergraphEdge.NameKey)

public var MutableOwnedRegistry<HypergraphEdge>.name: String
    @JvmName("getHypergraphEdgeMutableOwnedRegistryName") get() = get(HypergraphEdge.NameKey)
    @JvmName("setHypergraphEdgeMutableOwnedRegistryName") set(value) { set(HypergraphEdge.NameKey, value) }

public var OwnedRegistryBuilder<HypergraphEdge>.name: String
    @JvmName("getHypergraphEdgeOwnedRegistryBuilderName") get() = get(HypergraphEdge.NameKey)
    @JvmName("setHypergraphEdgeOwnedRegistryBuilderName") set(value) { set(HypergraphEdge.NameKey, value) }

public val HypergraphEdge.name: String
    @JvmName("getHypergraphEdgeName") get() = properties.name

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
            FromSecondToFirst -> vertices[0u]
        }
    }

public data object VertexToIncidentEdgesMapping : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToIncidentEdgesMapping"
}

public fun Hypergraph.incidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex]
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { (Equality.absoluteFor<HypergraphVertex>()) { vertex in it.vertices } }
}

public data object VertexToAdjacentVerticesMapping : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToAdjacentVerticesMapping"
}

public fun Hypergraph.adjacentVerticesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphVertex> {
    val vertexToAdjacentVerticesMapping = this.properties.getOrNull(VertexToAdjacentVerticesMapping)
    if (vertexToAdjacentVerticesMapping != null) return vertexToAdjacentVerticesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
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

public data object VertexToOutgoingIncidentEdgesMapping : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToOutgoingIncidentEdgesMapping"
}
public data object VertexToIncomingIncidentEdgesMapping : RegistryKey<KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>>> {
    override fun toString(): String = "dev.lounres.kone.graphs.VertexToIncomingIncidentEdgesMapping"
}

public fun Hypergraph.outgoingIncidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToOutgoingEdgesMapping = this.properties.getOrNull(VertexToOutgoingIncidentEdgesMapping)
    if (vertexToOutgoingEdgesMapping != null) return vertexToOutgoingEdgesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.start === vertex }
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.start === vertex }
}

public fun Hypergraph.incomingIncidentEdgesOf(vertex: HypergraphVertex): KoneReifiedSet<HypergraphEdge> {
    val vertexToIncomingEdgesMapping = this.properties.getOrNull(VertexToIncomingIncidentEdgesMapping)
    if (vertexToIncomingEdgesMapping != null) return vertexToIncomingEdgesMapping[vertex]
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.end === vertex }
    return edges.filterTo(KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())) { it.end === vertex }
}

public fun Hypergraph.outgoingDegreeOf(vertex: HypergraphVertex): UInt {
    val vertexToOutgoingEdgesMapping = this.properties.getOrNull(VertexToOutgoingIncidentEdgesMapping)
    if (vertexToOutgoingEdgesMapping != null) return vertexToOutgoingEdgesMapping[vertex].size
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].count { it.start === vertex }
    return edges.count { it.start === vertex }
}

public fun Hypergraph.incomingDegreeOf(vertex: HypergraphVertex): UInt {
    val vertexToIncomingEdgesMapping = this.properties.getOrNull(VertexToIncomingIncidentEdgesMapping)
    if (vertexToIncomingEdgesMapping != null) return vertexToIncomingEdgesMapping[vertex].size
    val vertexToEdgesMapping = this.properties.getOrNull(VertexToIncidentEdgesMapping)
    if (vertexToEdgesMapping != null) return vertexToEdgesMapping[vertex].count { it.end === vertex }
    return edges.count { it.end === vertex }
}

public class EdgeWeightKey<Weight>(public val weightType: SuppliedType) : RegistryKey<Weight> {
    override fun equals(other: Any?): Boolean = other is EdgeWeightKey<*> && weightType == other.weightType
    override fun hashCode(): Int = weightType.hashCode()
    override fun toString(): String = "dev.lounres.kone.relations.Equality.Key<$weightType>"
}

public fun <Weight> HypergraphEdge.weightOfType(weightType: SuppliedType): Weight = properties[EdgeWeightKey<Weight>(weightType)]
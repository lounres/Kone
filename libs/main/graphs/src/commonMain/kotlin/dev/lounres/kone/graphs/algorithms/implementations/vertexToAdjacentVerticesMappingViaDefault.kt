/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.KoneMutableReifiedMap
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMappingComputer
import dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMappingKey
import dev.lounres.kone.graphs.algorithms.vertexToAdjacentVerticesMapping
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


private object VertexToAdjacentVerticesMappingComputerViaDefault : VertexToAdjacentVerticesMappingComputer {
    override fun Hypergraph.vertexToAdjacentVerticesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>> {
        val vertexToAdjacentVerticesMapping =
            KoneMutableReifiedMap.of<HypergraphVertex, KoneMutableReifiedSet<HypergraphVertex>>(
                keyEquality = Equality.absoluteFor(),
            )
        
        for (vertex in vertices) {
            vertexToAdjacentVerticesMapping.let { it[vertex] = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor()) }
        }
        for (edge in edges) for ((val i = index, val vertex = value) in edge.vertices.withIndex()) {
            vertexToAdjacentVerticesMapping.let {
                for ((val j = index, val otherVertex = value) in edge.vertices.withIndex()) if (i != j) it[vertex].add(otherVertex)
            }
        }
        
        return vertexToAdjacentVerticesMapping
    }
}

public fun VertexToAdjacentVerticesMappingComputer.Companion.default(): VertexToAdjacentVerticesMappingComputer = VertexToAdjacentVerticesMappingComputerViaDefault

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun VertexToAdjacentVerticesMappingComputer.Companion.setDefault() {
    VertexToAdjacentVerticesMappingComputer.Key correspondsTo RegisteredValueProvider.cached { default() }
}

context(_: MutableOwnedProviderRegistry<Hypergraph>, graph: Hypergraph.Provider)
public fun VertexToAdjacentVerticesMappingComputer.Companion.useDefault() {
    VertexToAdjacentVerticesMappingKey correspondsTo RegisteredValueProvider.cached {
        (default()) {
            graph.get().vertexToAdjacentVerticesMapping()
        }
    }
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingComputer
import dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingKey
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse


private class VertexToIncidentEdgesMappingComputerViaProperties(
    private val fallbackComputer: VertexToIncidentEdgesMappingComputer,
) : VertexToIncidentEdgesMappingComputer {
    override fun Hypergraph.vertexToIncidentEdgesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>> =
        properties.getOrElse(VertexToIncidentEdgesMappingKey) {
            with(fallbackComputer) {
                this@vertexToIncidentEdgesMapping.vertexToIncidentEdgesMapping()
            }
        }
}

public fun VertexToIncidentEdgesMappingComputer.Companion.properties(
    fallbackComputer: VertexToIncidentEdgesMappingComputer,
): VertexToIncidentEdgesMappingComputer = VertexToIncidentEdgesMappingComputerViaProperties(
    fallbackComputer = fallbackComputer,
)

public inline fun VertexToIncidentEdgesMappingComputer.Companion.properties(
    block: VertexToIncidentEdgesMappingComputer.Companion.() -> VertexToIncidentEdgesMappingComputer,
): VertexToIncidentEdgesMappingComputer = properties(
    fallbackComputer = block()
)

public fun VertexToIncidentEdgesMappingComputer.Companion.properties(
    koneContextRegistry: KoneContextRegistry,
): VertexToIncidentEdgesMappingComputer = properties(
    fallbackComputer = koneContextRegistry[VertexToIncidentEdgesMappingComputer.Key]
)

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun VertexToIncidentEdgesMappingComputer.Companion.setProperties(
    fallbackComputer: VertexToIncidentEdgesMappingComputer,
) {
    VertexToIncidentEdgesMappingComputer.Key correspondsTo RegisteredValueProvider.cached {
        properties(fallbackComputer = fallbackComputer)
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun VertexToIncidentEdgesMappingComputer.Companion.setProperties(
    block: VertexToIncidentEdgesMappingComputer.Companion.() -> VertexToIncidentEdgesMappingComputer,
) {
    VertexToIncidentEdgesMappingComputer.Key correspondsTo RegisteredValueProvider.cached {
        properties(block)
    }
}
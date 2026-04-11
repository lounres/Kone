/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMappingComputer
import dev.lounres.kone.graphs.algorithms.VertexToAdjacentVerticesMappingKey
import dev.lounres.kone.registry.*


private class VertexToAdjacentVerticesMappingComputerViaProperties(
    private val fallbackComputer: VertexToAdjacentVerticesMappingComputer,
) : VertexToAdjacentVerticesMappingComputer {
    override fun Hypergraph.vertexToAdjacentVerticesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphVertex>> =
        properties.getOrElse(VertexToAdjacentVerticesMappingKey) {
            with(fallbackComputer) {
                this@vertexToAdjacentVerticesMapping.vertexToAdjacentVerticesMapping()
            }
        }
}

public fun VertexToAdjacentVerticesMappingComputer.Companion.properties(
    fallbackComputer: VertexToAdjacentVerticesMappingComputer,
): VertexToAdjacentVerticesMappingComputer = VertexToAdjacentVerticesMappingComputerViaProperties(
    fallbackComputer = fallbackComputer,
)

public inline fun VertexToAdjacentVerticesMappingComputer.Companion.properties(
    block: VertexToAdjacentVerticesMappingComputer.Companion.() -> VertexToAdjacentVerticesMappingComputer,
): VertexToAdjacentVerticesMappingComputer = properties(
    fallbackComputer = block()
)

public fun VertexToAdjacentVerticesMappingComputer.Companion.properties(
    koneContextRegistry: KoneContextRegistry,
): VertexToAdjacentVerticesMappingComputer = properties(
    fallbackComputer = koneContextRegistry[VertexToAdjacentVerticesMappingComputer.Key]
)

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun VertexToAdjacentVerticesMappingComputer.Companion.setProperties(
    fallbackComputer: VertexToAdjacentVerticesMappingComputer,
) {
    VertexToAdjacentVerticesMappingComputer.Key correspondsTo RegisteredValueProvider.cached {
        properties(fallbackComputer = fallbackComputer)
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun VertexToAdjacentVerticesMappingComputer.Companion.setProperties(
    block: VertexToAdjacentVerticesMappingComputer.Companion.() -> VertexToAdjacentVerticesMappingComputer,
) {
    VertexToAdjacentVerticesMappingComputer.Key correspondsTo RegisteredValueProvider.cached {
        properties(block)
    }
}
/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.reversed
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.MutableHypergraph
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import kotlin.jvm.JvmName


public data class TopologicallySortedVertices(
    val startToEnd: KoneList<HypergraphVertex>,
) {
    val endToStart: KoneList<HypergraphVertex> by lazy { startToEnd.reversed() }
    
    public data object Key : RegistryKey<TopologicallySortedVertices> {
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.TopologicallySortedVertices.Key"
    }
}

public val OwnedProviderRegistry<out Hypergraph>.topologicallySortedVertices: TopologicallySortedVertices
    @JvmName("getHypergraphOwnedRegistryTopologicallySortedVertices") get() = get(TopologicallySortedVertices.Key)

public var MutableOwnedProviderRegistry<out MutableHypergraph>.topologicallySortedVertices: TopologicallySortedVertices
    @JvmName("getHypergraphMutableOwnedRegistryTopologicallySortedVertices") get() = get(TopologicallySortedVertices.Key)
    @JvmName("setHypergraphMutableOwnedRegistryTopologicallySortedVertices") set(value) { set(TopologicallySortedVertices.Key, value) }

//public var OwnedRegistryBuilder<Hypergraph>.topologicallySortedVertices: TopologicallySortedVertices
//    @JvmName("getHypergraphOwnedRegistryBuilderTopologicallySortedVertices") get() = get(TopologicallySortedVertices.Key)
//    @JvmName("setHypergraphOwnedRegistryBuilderTopologicallySortedVertices") set(value) { set(TopologicallySortedVertices.Key, value) }

public val Hypergraph.topologicallySortedVertices: TopologicallySortedVertices
    @JvmName("getHypergraphTopologicallySortedVertices") get() = properties.topologicallySortedVertices

// Sorts vertices with edges from start of the list to end
public fun interface TopologicalSortingComputer : KoneContext {
    public fun Hypergraph.sortVerticesTopologically(): TopologicallySortedVertices
    
    public companion object;
    
    public data object Key : RegistryKey<TopologicalSortingComputer> {
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.TopologicalSortingComputer.Key"
    }
}

context(computer: TopologicalSortingComputer)
public fun Hypergraph.sortVerticesTopologically(): TopologicallySortedVertices =
    with(computer) { this@sortVerticesTopologically.sortVerticesTopologically() }
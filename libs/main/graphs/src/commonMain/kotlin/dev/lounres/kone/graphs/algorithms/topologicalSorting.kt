/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.registry.RegistryKey


public fun interface TopologicalSortingComputer {
    public fun Hypergraph.sortVerticesTopologically(): KoneList<HypergraphVertex>
    
    public companion object;
    
    public data object Key : RegistryKey<TopologicalSortingComputer> {
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.TopologicalSortingComputer.Key"
    }
}

context(computer: TopologicalSortingComputer)
public fun Hypergraph.sortVerticesTopologically(): KoneList<HypergraphVertex> =
    with(computer) { this@sortVerticesTopologically.sortVerticesTopologically() }
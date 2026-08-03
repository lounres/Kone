/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.Path


public fun interface HypergraphShortestPathWithFixedEndsProvider<out Weight> {
    public fun get(): Path<Weight>?
}

public fun interface HypergraphShortestPathWithFixedStartProvider<out Weight> {
    public operator fun get(end: HypergraphVertex): Path<Weight>?
}

public fun interface HypergraphShortestPathWithoutFixedEndsProvider<out Weight> {
    public operator fun get(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>?
}

public fun <Weight> HypergraphShortestPathWithFixedStartProvider<Weight>.asHypergraphShortestPathWithFixedEndsProvider(
    end: HypergraphVertex
): HypergraphShortestPathWithFixedEndsProvider<Weight> = HypergraphShortestPathWithFixedEndsProvider { this[end] }

public fun <Weight> HypergraphShortestPathWithoutFixedEndsProvider<Weight>.asHypergraphShortestPathWithFixedEndsProvider(
    start: HypergraphVertex,
    end: HypergraphVertex
): HypergraphShortestPathWithFixedEndsProvider<Weight> = HypergraphShortestPathWithFixedEndsProvider { this[start, end] }

public fun <Weight> HypergraphShortestPathWithoutFixedEndsProvider<Weight>.asHypergraphShortestPathWithFixedStartProvider(
    start: HypergraphVertex
): HypergraphShortestPathWithFixedStartProvider<Weight> = HypergraphShortestPathWithFixedStartProvider { this[start, it] }

@GenerateKoneContextKey
public fun interface HypergraphShortestPathWithFixedEndsComputer<out Weight> : KoneContext {
    public fun Hypergraph.shortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphShortestPathWithFixedEndsProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphShortestPathWithFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphShortestPathWithFixedEndsProvider<Weight> =
    with(computer){ this@shortestPathWithFixedEndsProvider.shortestPathWithFixedEndsProvider(start, end) }

@GenerateKoneContextKey
public fun interface HypergraphShortestPathWithFixedStartComputer<out Weight> : KoneContext {
    public fun Hypergraph.shortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphShortestPathWithFixedStartProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphShortestPathWithFixedStartComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphShortestPathWithFixedStartProvider<Weight> =
    with(computer){ this@shortestPathWithFixedStartProvider.shortestPathWithFixedStartProvider(start) }

@GenerateKoneContextKey
public fun interface HypergraphShortestPathWithoutFixedEndsComputer<out Weight> : KoneContext {
    public fun Hypergraph.shortestPathWithoutFixedEndsProvider(): HypergraphShortestPathWithoutFixedEndsProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphShortestPathWithoutFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithoutFixedEndsProvider(): HypergraphShortestPathWithoutFixedEndsProvider<Weight> =
    with(computer){ this@shortestPathWithoutFixedEndsProvider.shortestPathWithoutFixedEndsProvider() }

context(_: HypergraphShortestPathWithFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    shortestPathWithFixedEndsProvider(start, end).get()

context(_: HypergraphShortestPathWithFixedStartComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    shortestPathWithFixedStartProvider(start)[end]

context(_: HypergraphShortestPathWithoutFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    shortestPathWithoutFixedEndsProvider()[start, end]
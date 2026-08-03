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


public fun interface HypergraphDirectedShortestPathWithFixedEndsProvider<out Weight> {
    public fun get(): Path<Weight>?
}

public fun interface HypergraphDirectedShortestPathWithFixedStartProvider<out Weight> {
    public operator fun get(end: HypergraphVertex): Path<Weight>?
}

public fun interface HypergraphDirectedShortestPathWithoutFixedEndsProvider<out Weight> {
    public operator fun get(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>?
}

public fun <Weight> HypergraphDirectedShortestPathWithFixedStartProvider<Weight>.asHypergraphDirectedShortestPathWithFixedEndsProvider(
    end: HypergraphVertex
): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight> = HypergraphDirectedShortestPathWithFixedEndsProvider { this[end] }

public fun <Weight> HypergraphDirectedShortestPathWithoutFixedEndsProvider<Weight>.asHypergraphDirectedShortestPathWithFixedEndsProvider(
    start: HypergraphVertex,
    end: HypergraphVertex
): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight> = HypergraphDirectedShortestPathWithFixedEndsProvider { this[start, end] }

public fun <Weight> HypergraphDirectedShortestPathWithoutFixedEndsProvider<Weight>.asHypergraphDirectedShortestPathWithFixedStartProvider(
    start: HypergraphVertex
): HypergraphDirectedShortestPathWithFixedStartProvider<Weight> = HypergraphDirectedShortestPathWithFixedStartProvider { this[start, it] }

@GenerateKoneContextKey
public fun interface HypergraphDirectedShortestPathWithFixedEndsComputer<out Weight> : KoneContext {
    public fun Hypergraph.directedShortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphDirectedShortestPathWithFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight> =
    with(computer){ this@directedShortestPathWithFixedEndsProvider.directedShortestPathWithFixedEndsProvider(start, end) }

@GenerateKoneContextKey
public fun interface HypergraphDirectedShortestPathWithFixedStartComputer<out Weight> : KoneContext {
    public fun Hypergraph.directedShortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphDirectedShortestPathWithFixedStartProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphDirectedShortestPathWithFixedStartComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphDirectedShortestPathWithFixedStartProvider<Weight> =
    with(computer){ this@directedShortestPathWithFixedStartProvider.directedShortestPathWithFixedStartProvider(start) }

@GenerateKoneContextKey
public fun interface HypergraphDirectedShortestPathWithoutFixedEndsComputer<out Weight> : KoneContext {
    public fun Hypergraph.directedShortestPathWithoutFixedEndsProvider(): HypergraphDirectedShortestPathWithoutFixedEndsProvider<Weight>
    
    public companion object;
}

context(computer: HypergraphDirectedShortestPathWithoutFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithoutFixedEndsProvider(): HypergraphDirectedShortestPathWithoutFixedEndsProvider<Weight> =
    with(computer){ this@directedShortestPathWithoutFixedEndsProvider.directedShortestPathWithoutFixedEndsProvider() }

context(_: HypergraphDirectedShortestPathWithFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    directedShortestPathWithFixedEndsProvider(start, end).get()

context(_: HypergraphDirectedShortestPathWithFixedStartComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    directedShortestPathWithFixedStartProvider(start)[end]

context(_: HypergraphDirectedShortestPathWithoutFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.directedShortestPathWithFixedEnds(start: HypergraphVertex, end: HypergraphVertex): Path<Weight>? =
    directedShortestPathWithoutFixedEndsProvider()[start, end]
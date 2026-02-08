/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


//@JvmInline
public /*value*/ data class Path<out Weight>(
    public val weight: Weight,
    public val vertices: KoneList<HypergraphVertex>,
    public val edges: KoneList<HypergraphEdge>,
)

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

public fun interface HypergraphShortestPathWithFixedEndsComputer<out Weight> {
    public fun Hypergraph.shortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphShortestPathWithFixedEndsProvider<Weight>
    
    public companion object;
    
    public class Key<Weight>(
        public val weightType: SuppliedType,
    ) : RegistryKey<HypergraphShortestPathWithFixedEndsComputer<Weight>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedEndsComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = OUT,
                        type = weightType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedEndsComputer.Key<$weightType>"
    }
}

context(computer: HypergraphShortestPathWithFixedEndsComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedEndsProvider(start: HypergraphVertex, end: HypergraphVertex): HypergraphShortestPathWithFixedEndsProvider<Weight> =
    with(computer){ this@shortestPathWithFixedEndsProvider.shortestPathWithFixedEndsProvider(start, end) }
    
public fun interface HypergraphShortestPathWithFixedStartComputer<out Weight> {
    public fun Hypergraph.shortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphShortestPathWithFixedStartProvider<Weight>
    
    public companion object;
    
    public class Key<Weight>(
        public val weightType: SuppliedType,
    ) : RegistryKey<HypergraphShortestPathWithFixedStartComputer<Weight>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedStartComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = OUT,
                        type = weightType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedStartComputer.Key<$weightType>"
    }
}

context(computer: HypergraphShortestPathWithFixedStartComputer<Weight>)
public fun <Weight> Hypergraph.shortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphShortestPathWithFixedStartProvider<Weight> =
    with(computer){ this@shortestPathWithFixedStartProvider.shortestPathWithFixedStartProvider(start) }

public fun interface HypergraphShortestPathWithoutFixedEndsComputer<out Weight> {
    public fun Hypergraph.shortestPathWithoutFixedEndsProvider(): HypergraphShortestPathWithoutFixedEndsProvider<Weight>
    
    public companion object;
    
    public class Key<Weight>(
        public val weightType: SuppliedType,
    ) : RegistryKey<HypergraphShortestPathWithoutFixedEndsComputer<Weight>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithoutFixedEndsComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = OUT,
                        type = weightType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithoutFixedEndsComputer.Key<$weightType>"
    }
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
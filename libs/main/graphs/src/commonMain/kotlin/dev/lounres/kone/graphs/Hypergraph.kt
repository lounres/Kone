/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph.Provider
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface Hypergraph {
    public val vertices: KoneReifiedSet<HypergraphVertex>
    public val edges: KoneReifiedSet<HypergraphEdge>
    
    public val properties: ProviderRegistry get() = ProviderRegistry.Empty
    
    public companion object;
    
    public fun interface Provider {
        public fun get(): Hypergraph
    }
    
    public fun interface Factory {
        @InternalApi
        public fun builder(): HypergraphBuilderWithProvider
        
        @InternalApi
        public interface Provider : Hypergraph.Provider {
            public fun initialize()
        }
        
        @InternalApi
        public data class HypergraphBuilderWithProvider(
            val builder: HypergraphBuilder,
            val provider: Provider
        )
        
        @RequiresOptIn(
            level = RequiresOptIn.Level.WARNING,
        )
        public annotation class InternalApi
        
        public data object Key : RegistryKey<Factory> {
            override fun toString(): String = "dev.lounres.kone.graphs.Hypergraph.Factory.Key"
        }
    }
}

@OptIn(Hypergraph.Factory.InternalApi::class)
public inline fun Hypergraph.Factory.Hypergraph(block: HypergraphBuilder.(graph: Provider) -> Unit): Hypergraph {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val (builder, provider) = builder()
    block(builder, provider)
    provider.initialize()
    return builder
}

public interface MutableHypergraph : Hypergraph {
    public fun add(vertex: HypergraphVertex)
    public fun add(edge: HypergraphEdge)
    public fun remove(vertex: HypergraphVertex)
    public fun remove(edge: HypergraphEdge)
    
    override val properties: MutableProviderRegistry
    
    public companion object
}

public inline fun MutableHypergraph.properties(block: MutableOwnedProviderRegistry<Hypergraph>.() -> Unit) {
    MutableOwnedProviderRegistry<Hypergraph>(this.properties).block()
}

public fun MutableHypergraph(
    properties: MutableProviderRegistry = MutableProviderRegistry(),
): MutableHypergraph = MutableHypergraphImpl(
    properties = properties,
)

private class MutableHypergraphImpl(
    override val properties: MutableProviderRegistry
) : MutableHypergraph {
    override val vertices: KoneMutableReifiedSet<HypergraphVertex> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    override val edges: KoneMutableReifiedSet<HypergraphEdge> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    
    override fun add(vertex: HypergraphVertex) {
        vertices.add(vertex)
    }
    override fun add(edge: HypergraphEdge) {
        if (edge !in edges) {
            edges.add(edge)
            vertices.addAllFrom(edge.vertices)
        }
    }
    override fun remove(vertex: HypergraphVertex) {
        if (vertex in vertices) {
            vertices.remove(vertex)
            edges.removeAllThat { (Equality.absoluteFor<HypergraphVertex>()) { vertex in it.vertices } }
        }
    }
    override fun remove(edge: HypergraphEdge) {
        edges.remove(edge)
    }
}

public interface HypergraphBuilder : MutableHypergraph {
    public operator fun HypergraphVertex.unaryPlus() { add(this) }
    public operator fun HypergraphEdge.unaryPlus() { add(this) }
    public operator fun HypergraphVertex.unaryMinus() { remove(this) }
    public operator fun HypergraphEdge.unaryMinus() { remove(this) }
}

@PublishedApi
internal class HypergraphBuilderImpl : HypergraphBuilder {
    override val vertices: KoneMutableReifiedSet<HypergraphVertex> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    override val edges: KoneMutableReifiedSet<HypergraphEdge> = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor())
    
    override fun add(vertex: HypergraphVertex) {
        vertices.add(vertex)
    }
    override fun add(edge: HypergraphEdge) {
        if (edge !in edges) {
            edges.add(edge)
            vertices.addAllFrom(edge.vertices)
        }
    }
    override fun remove(vertex: HypergraphVertex) {
        if (vertex in vertices) {
            vertices.remove(vertex)
            edges.removeAllThat { (Equality.absoluteFor<HypergraphVertex>()) { vertex in it.vertices } }
        }
    }
    override fun remove(edge: HypergraphEdge) {
        edges.remove(edge)
    }
    
    override val properties = MutableProviderRegistry()
}

public inline fun Hypergraph.Companion.build(block: HypergraphBuilder.() -> Unit): Hypergraph {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return HypergraphBuilderImpl().apply(block)
}

@OptIn(Hypergraph.Factory.InternalApi::class)
public fun Hypergraph.Companion.Factory(
    block: context(Provider) HypergraphBuilder.() -> Unit = {},
): Hypergraph.Factory =
    Hypergraph.Factory {
        val builder = HypergraphBuilderImpl()
        val provider = object : Hypergraph.Factory.Provider {
            private var isInitialized = false
            override fun get(): Hypergraph =
                if (isInitialized) builder
                else error("Hypergraph is not yet initialized but was requested by its properties.")
            override fun initialize() {
                isInitialized = true
            }
        }
        block(provider, builder)
        Hypergraph.Factory.HypergraphBuilderWithProvider(
            builder = builder,
            provider = provider,
        )
    }

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Hypergraph.Companion.setFactory(
    block: context(Provider) HypergraphBuilder.() -> Unit = {},
) {
    Hypergraph.Factory.Key correspondsTo RegisteredValueProvider.cached { Hypergraph.Factory(block) }
}
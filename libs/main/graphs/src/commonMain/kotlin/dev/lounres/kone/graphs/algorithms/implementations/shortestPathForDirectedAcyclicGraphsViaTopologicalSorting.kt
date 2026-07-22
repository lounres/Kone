/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeMonoid
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.map.associate
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.mapsTo
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.graphs.*
import dev.lounres.kone.graphs.HypergraphEdgeWeightOfTypeSuppliableTopLevelFunctions.weightOfType
import dev.lounres.kone.graphs.algorithms.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
@Suppliable
private class HypergraphDirectedShortestPathWithFixedEndsComputerForDirectedAcyclicGraphsViaTopologicalSorting<@Supply Weight>(
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
    private val topologicalSortingComputer: TopologicalSortingComputer,
) : HypergraphDirectedShortestPathWithFixedEndsComputer<Weight> {
    override fun Hypergraph.directedShortestPathWithFixedEndsProvider(
        start: HypergraphVertex,
        end: HypergraphVertex
    ): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight> {
        val lazyProvider by lazy {
            if (start === end)
                return@lazy Path(
                    weight = weightMonoid.zero,
                    vertices = KoneList.of(start),
                    edges = KoneList.empty(),
                )
            
            val topologicallySortedVertices =
                this.properties.getOrElse(TopologicallySortedVertices.Key) {
                    topologicalSortingComputer { this.sortVerticesTopologically() }
                }
            
            val verticesList = topologicallySortedVertices.startToEnd
            val vertexIndex = verticesList
                .withIndex()
                .associate(keyEquality = Equality.absoluteFor()) { it.value mapsTo it.index }
            val startIndex = vertexIndex[start]
            val endIndex = vertexIndex[end]
            
            if (endIndex < startIndex) return@lazy null
            
            val paths = KoneMutableArray.generate<Path<Weight>?>(startIndex .. endIndex) { null }
            paths[0u] = Path(
                weight = weightMonoid.zero,
                vertices = KoneList.of(start),
                edges = KoneList.empty(),
            )
            
            KoneContext.localUnwrap(weightMonoid, weightsOrder)
            for (vIndex in startIndex .. endIndex) {
                val vPath = paths[vIndex - startIndex] ?: continue
                if (paths.last().let { it != null && it.weight lt vPath.weight }) continue
                val v = verticesList[vIndex]
                for (e in outgoingIncidentEdgesOf(v)) {
                    val newWeight = vPath.weight + e.weightOfType<Weight>()
                    if (paths.last().let { it != null && it.weight lt newWeight }) continue
                    val u = e.end
                    val uIndex = vertexIndex[u]
                    check(uIndex >= vIndex) { "For some reason topological sorting returned incorrect result." }
                    if (uIndex !in vIndex + 1u .. endIndex) continue
                    val uPath = paths[uIndex - startIndex]
                    if (uPath == null || uPath.weight gt newWeight)
                        paths[uIndex - startIndex] = Path(
                            weight = newWeight,
                            vertices = KoneList.generate(vPath.vertices.size + 1u) { if (it < vPath.vertices.size) vPath.vertices[it] else u },
                            edges = KoneList.generate(vPath.edges.size + 1u) { if (it < vPath.edges.size) vPath.edges[it] else e },
                        )
                }
            }
            
            paths.last()
        }
        
        return HypergraphDirectedShortestPathWithFixedEndsProvider { lazyProvider }
    }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HypergraphDirectedShortestPathWithFixedEndsComputerForDirectedAcyclicGraphsViaTopologicalSortingSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Weight> HypergraphDirectedShortestPathWithFixedEndsComputer.Companion.forDirectedAcyclicGraphsViaTopologicalSorting(
        weightMonoid: CommutativeMonoid<Weight>,
        weightsOrder: Order<Weight>,
        topologicalSortingComputer: TopologicalSortingComputer,
    ): HypergraphDirectedShortestPathWithFixedEndsComputer<Weight> = HypergraphDirectedShortestPathWithFixedEndsComputerForDirectedAcyclicGraphsViaTopologicalSorting(
        weightMonoid = weightMonoid,
        weightsOrder = weightsOrder,
        topologicalSortingComputer = topologicalSortingComputer,
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Weight> HypergraphDirectedShortestPathWithFixedEndsComputer.Companion.setForDirectedAcyclicGraphsViaTopologicalSorting() {
        HypergraphDirectedShortestPathWithFixedEndsComputer.Key<Weight>() correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            forDirectedAcyclicGraphsViaTopologicalSorting(
                weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>()],
                weightsOrder = koneContextRegistry[Order.Key<Weight>()],
                topologicalSortingComputer = koneContextRegistry[TopologicalSortingComputer.Key],
            )
        }
    }
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
@Suppliable
private class HypergraphDirectedShortestPathWithFixedStartComputerForDirectedAcyclicGraphsViaTopologicalSorting<@Supply Weight>(
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
    private val topologicalSortingComputer: TopologicalSortingComputer,
) : HypergraphDirectedShortestPathWithFixedStartComputer<Weight> {
    override fun Hypergraph.directedShortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphDirectedShortestPathWithFixedStartProvider<Weight> =
        object : SynchronizedObject(), HypergraphDirectedShortestPathWithFixedStartProvider<Weight> {
            val verticesList by lazy {
                this@directedShortestPathWithFixedStartProvider.properties.getOrElse(TopologicallySortedVertices.Key) {
                    topologicalSortingComputer { this@directedShortestPathWithFixedStartProvider.sortVerticesTopologically() }
                }.startToEnd
            }
            
            val vertexIndex by lazy {
                verticesList
                    .withIndex()
                    .associate(keyEquality = Equality.absoluteFor()) { it.value mapsTo it.index }
            }
            val startIndex by lazy { vertexIndex[start] }
            
            val startPath by lazy {
                Path(
                    weight = weightMonoid.zero,
                    vertices = KoneList.of(start),
                    edges = KoneList.empty()
                )
            }
            
            val paths by lazy {
                KoneMutableArray.generate(
                    size = verticesList.size - startIndex,
                ) {
                    if (it == 0u) startPath else null
                }
            }
            var firstNotVisitedVertexIndexInPaths = 0u
            
            override fun get(end: HypergraphVertex): Path<Weight>? {
                if (end === start) return startPath
                
                val endIndex = vertexIndex[end]
                if (endIndex < startIndex) return null
                
                if (endIndex - startIndex <= firstNotVisitedVertexIndexInPaths) return paths[endIndex - startIndex]
                
                synchronized(this) {
                    KoneContext.localUnwrap(weightMonoid, weightsOrder)
                    while (firstNotVisitedVertexIndexInPaths + startIndex < endIndex) {
                        val vIndex = firstNotVisitedVertexIndexInPaths + startIndex
                        val vPath = paths[vIndex - startIndex] ?: continue
                        val v = verticesList[vIndex]
                        for (e in outgoingIncidentEdgesOf(v)) {
                            val newWeight = vPath.weight + e.weightOfType<Weight>()
                            val u = e.end
                            val uIndex = vertexIndex[u]
                            check(uIndex >= vIndex) { "For some reason topological sorting returned incorrect result." }
                            val uPath = paths[uIndex - startIndex]
                            if (uPath == null || uPath.weight gt newWeight)
                                paths[uIndex - startIndex] = Path(
                                    weight = newWeight,
                                    vertices = KoneList.generate(vPath.vertices.size + 1u) { if (it < vPath.vertices.size) vPath.vertices[it] else u },
                                    edges = KoneList.generate(vPath.edges.size + 1u) { if (it < vPath.edges.size) vPath.edges[it] else e },
                                )
                        }
                        firstNotVisitedVertexIndexInPaths++
                    }
                    
                    return paths[endIndex - startIndex]
                }
            }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HypergraphDirectedShortestPathWithFixedStartComputerForDirectedAcyclicGraphsViaTopologicalSortingSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Weight> HypergraphDirectedShortestPathWithFixedStartComputer.Companion.forDirectedAcyclicGraphsViaTopologicalSorting(
        weightMonoid: CommutativeMonoid<Weight>,
        weightsOrder: Order<Weight>,
        topologicalSortingComputer: TopologicalSortingComputer,
    ): HypergraphDirectedShortestPathWithFixedStartComputer<Weight> = HypergraphDirectedShortestPathWithFixedStartComputerForDirectedAcyclicGraphsViaTopologicalSorting(
        weightMonoid = weightMonoid,
        weightsOrder = weightsOrder,
        topologicalSortingComputer = topologicalSortingComputer,
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Weight> HypergraphDirectedShortestPathWithFixedStartComputer.Companion.setForDirectedAcyclicGraphsViaTopologicalSorting() {
        HypergraphDirectedShortestPathWithFixedStartComputer.Key<Weight>() correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            forDirectedAcyclicGraphsViaTopologicalSorting(
                weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>()],
                weightsOrder = koneContextRegistry[Order.Key<Weight>()],
                topologicalSortingComputer = koneContextRegistry[TopologicalSortingComputer.Key],
            )
        }
    }
}
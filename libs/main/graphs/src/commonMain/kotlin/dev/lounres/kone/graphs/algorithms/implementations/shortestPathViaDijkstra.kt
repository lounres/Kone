/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeMonoid
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeap
import dev.lounres.kone.collections.heap.isEmpty
import dev.lounres.kone.collections.heap.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.graphs.*
import dev.lounres.kone.graphs.HypergraphEdgeWeightOfTypeSuppliableTopLevelFunctions.weightOfType
import dev.lounres.kone.graphs.algorithms.*
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
@Suppliable
private class HypergraphShortestPathWithFixedEndsComputerByDijkstra<@Supply Weight>(
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
) : HypergraphShortestPathWithFixedEndsComputer<Weight> {
    override fun Hypergraph.shortestPathWithFixedEndsProvider(
        start: HypergraphVertex,
        end: HypergraphVertex
    ): HypergraphShortestPathWithFixedEndsProvider<Weight> {
        val lazyProvider by lazy {
            KoneContext.localUnwrap(weightMonoid, weightsOrder)
            
            val verticesToCheck = KoneBinaryGCMinimumHeap<HypergraphVertex, Weight>(weightsOrder)
            val queueNodes = KoneMutableMap.of<HypergraphVertex, HeapNode<HypergraphVertex, Weight>>(Equality.absoluteFor())
            val paths = KoneMutableMap.of<HypergraphVertex, Path<Weight>>(Equality.absoluteFor())
            var optimalPathToTarget: Path<Weight>? = null
            
            queueNodes[start] = verticesToCheck.add(start, weightMonoid.zero)
            paths[start] = Path(
                weight = weightMonoid.zero,
                vertices = KoneList.of(start),
                edges = KoneList.empty(),
            )
            
            while (verticesToCheck.isNotEmpty()) {
                val currentVertexNode = verticesToCheck.takeMinimum()
                if (optimalPathToTarget != null && currentVertexNode.priority geq optimalPathToTarget.weight) break
                currentVertexNode.remove()
                
                val currentVertex = currentVertexNode.element
                val currentPath = paths[currentVertex]
                val currentWeight = currentPath.weight
                for (edge in incidentEdgesOf(currentVertex)) {
                    val neighbor = edge.ends - currentVertex
                    val currentPathToNeighbor = paths.getOrNull(neighbor)
                    val alternativeWeight = currentWeight + edge.weightOfType()
                    val alternativePath = Path(
                        weight = alternativeWeight,
                        vertices = KoneList.generate(currentPath.vertices.size + 1u) { if (it < currentPath.vertices.size) currentPath.vertices[it] else neighbor },
                        edges = KoneList.generate(currentPath.edges.size + 1u) { if (it < currentPath.edges.size) currentPath.edges[it] else edge },
                    )
                    when {
                        currentPathToNeighbor == null -> {
                            paths[neighbor] = alternativePath
                            if (neighbor === end) optimalPathToTarget = alternativePath
                            queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                        }
                        alternativeWeight lt currentPathToNeighbor.weight -> {
                            paths[neighbor] = alternativePath
                            if (neighbor === end) optimalPathToTarget = alternativePath
                            queueNodes[neighbor].priority = alternativeWeight
                        }
                    }
                }
            }
            
            optimalPathToTarget
        }
        
        return HypergraphShortestPathWithFixedEndsProvider { lazyProvider }
    }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HypergraphShortestPathWithFixedEndsComputerDijkstraSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Weight> HypergraphShortestPathWithFixedEndsComputer.Companion.dijkstra(
        weightMonoid: CommutativeMonoid<Weight>,
        weightsOrder: Order<Weight>,
    ): HypergraphShortestPathWithFixedEndsComputer<Weight> = HypergraphShortestPathWithFixedEndsComputerByDijkstra(
        weightMonoid = weightMonoid,
        weightsOrder = weightsOrder,
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Weight> HypergraphShortestPathWithFixedEndsComputer.Companion.setDijkstra() {
        HypergraphShortestPathWithFixedEndsComputer.Key<Weight>() correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            dijkstra(
                weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>()],
                weightsOrder = koneContextRegistry[Order.Key<Weight>()],
            )
        }
    }
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
@Suppliable
private class HypergraphShortestPathWithFixedStartComputerByDijkstra<@Supply Weight>(
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
) : HypergraphShortestPathWithFixedStartComputer<Weight> {
    override fun Hypergraph.shortestPathWithFixedStartProvider(start: HypergraphVertex): HypergraphShortestPathWithFixedStartProvider<Weight> =
        object : SynchronizedObject(), HypergraphShortestPathWithFixedStartProvider<Weight> {
            val verticesToCheck = KoneBinaryGCMinimumHeap<HypergraphVertex, Weight>(weightsOrder)
            val queueNodes = KoneMutableMap.of<HypergraphVertex, HeapNode<HypergraphVertex, Weight>>(Equality.absoluteFor())
            val paths = KoneMutableMap.of<HypergraphVertex, Path<Weight>>(Equality.absoluteFor())
            
            init {
                queueNodes[start] = verticesToCheck.add(start, weightMonoid.zero)
                paths[start] = Path(
                    weight = weightMonoid.zero,
                    vertices = KoneList.of(start),
                    edges = KoneList.empty(),
                )
            }
            
            override fun get(end: HypergraphVertex): Path<Weight>? {
                val currentProcessedWeight = synchronized(this) {
                    if (verticesToCheck.isEmpty()) return paths.getOrNull(end)
                    else verticesToCheck.takeMinimum().priority
                }
                val currentPath = paths.getOrNull(end)
                if (currentPath != null && weightsOrder { currentPath.weight lt currentProcessedWeight }) return currentPath
                
                synchronized(this) {
                    KoneContext.localUnwrap(weightMonoid, weightsOrder)
                    var optimalPathToTarget: Path<Weight>? = paths.getOrNull(end)
                    
                    while (verticesToCheck.isNotEmpty()) {
                        val currentVertexNode = verticesToCheck.takeMinimum()
                        if (optimalPathToTarget != null && currentVertexNode.priority geq optimalPathToTarget.weight) break
                        currentVertexNode.remove()
                        
                        val currentVertex = currentVertexNode.element
                        val currentPath = paths[currentVertex]
                        val currentWeight = currentPath.weight
                        for (edge in incidentEdgesOf(currentVertex)) {
                            val neighbor = edge.ends - currentVertex
                            val currentPathToNeighbor = paths.getOrNull(neighbor)
                            val alternativeWeight = currentWeight + edge.weightOfType()
                            val alternativePath = Path(
                                weight = alternativeWeight,
                                vertices = KoneList.generate(currentPath.vertices.size + 1u) { if (it < currentPath.vertices.size) currentPath.vertices[it] else neighbor },
                                edges = KoneList.generate(currentPath.edges.size + 1u) { if (it < currentPath.edges.size) currentPath.edges[it] else edge },
                            )
                            when {
                                currentPathToNeighbor == null -> {
                                    paths[neighbor] = alternativePath
                                    if (neighbor === end) optimalPathToTarget = alternativePath
                                    queueNodes[neighbor] = verticesToCheck.add(neighbor, alternativeWeight)
                                }
                                
                                alternativeWeight lt currentPathToNeighbor.weight -> {
                                    paths[neighbor] = alternativePath
                                    if (neighbor === end) optimalPathToTarget = alternativePath
                                    queueNodes[neighbor].priority = alternativeWeight
                                }
                            }
                        }
                    }
                    
                    return optimalPathToTarget
                }
            }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HypergraphShortestPathWithFixedStartComputerDijkstraSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Weight> HypergraphShortestPathWithFixedStartComputer.Companion.dijkstra(
        weightMonoid: CommutativeMonoid<Weight>,
        weightsOrder: Order<Weight>,
    ): HypergraphShortestPathWithFixedStartComputer<Weight> = HypergraphShortestPathWithFixedStartComputerByDijkstra(
        weightMonoid = weightMonoid,
        weightsOrder = weightsOrder,
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Weight> HypergraphShortestPathWithFixedStartComputer.Companion.setDijkstra() {
        HypergraphShortestPathWithFixedStartComputer.Key<Weight>() correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            dijkstra(
                weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>()],
                weightsOrder = koneContextRegistry[Order.Key<Weight>()],
            )
        }
    }
}
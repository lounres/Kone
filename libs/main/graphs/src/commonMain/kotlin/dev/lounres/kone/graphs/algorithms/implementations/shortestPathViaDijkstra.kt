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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.Path
import dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedEndsComputer
import dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedEndsProvider
import dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedStartComputer
import dev.lounres.kone.graphs.algorithms.HypergraphShortestPathWithFixedStartProvider
import dev.lounres.kone.graphs.algorithms.incidentEdgesOf
import dev.lounres.kone.graphs.ends
import dev.lounres.kone.graphs.minus
import dev.lounres.kone.graphs.weightOfType
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.geq
import dev.lounres.kone.relations.lt
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
private class HypergraphShortestPathWithFixedEndsComputerByDijkstra<Weight>(
    private val weightType: SuppliedType,
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
) : HypergraphShortestPathWithFixedEndsComputer<Weight> {
    override fun Hypergraph.shortestPathWithFixedEndsProvider(
        start: HypergraphVertex,
        end: HypergraphVertex
    ): HypergraphShortestPathWithFixedEndsProvider<Weight> {
        val lazyProvider = lazy {
            context(weightMonoid, weightsOrder) {
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
                        val alternativeWeight = currentWeight + edge.weightOfType(weightType)
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
        }
        
        return HypergraphShortestPathWithFixedEndsProvider { lazyProvider.value }
    }
}

public fun <Weight> HypergraphShortestPathWithFixedEndsComputer.Companion.dijkstra(
    weightType: SuppliedType,
    weightMonoid: CommutativeMonoid<Weight>,
    weightsOrder: Order<Weight>,
): HypergraphShortestPathWithFixedEndsComputer<Weight> = HypergraphShortestPathWithFixedEndsComputerByDijkstra(
    weightType = weightType,
    weightMonoid = weightMonoid,
    weightsOrder = weightsOrder,
)

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Weight> HypergraphShortestPathWithFixedEndsComputer.Companion.setDijkstra(
    weightType: SuppliedType,
) {
    HypergraphShortestPathWithFixedEndsComputer.Key<Weight>(weightType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        dijkstra(
            weightType = weightType,
            weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>(weightType)],
            weightsOrder = koneContextRegistry[Order.Key<Weight>(weightType)],
        )
    }
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
private class HypergraphShortestPathWithFixedStartComputerByDijkstra<Weight>(
    private val weightType: SuppliedType,
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
                    context(weightMonoid, weightsOrder) {
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
                                val alternativeWeight = currentWeight + edge.weightOfType(weightType)
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
}

public fun <Weight> HypergraphShortestPathWithFixedStartComputer.Companion.dijkstra(
    weightType: SuppliedType,
    weightMonoid: CommutativeMonoid<Weight>,
    weightsOrder: Order<Weight>,
): HypergraphShortestPathWithFixedStartComputer<Weight> = HypergraphShortestPathWithFixedStartComputerByDijkstra(
    weightType = weightType,
    weightMonoid = weightMonoid,
    weightsOrder = weightsOrder,
)

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Weight> HypergraphShortestPathWithFixedStartComputer.Companion.setDijkstra(
    weightType: SuppliedType,
) {
    HypergraphShortestPathWithFixedStartComputer.Key<Weight>(weightType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        dijkstra(
            weightType = weightType,
            weightMonoid = koneContextRegistry[CommutativeMonoid.Key<Weight>(weightType)],
            weightsOrder = koneContextRegistry[Order.Key<Weight>(weightType)],
        )
    }
}
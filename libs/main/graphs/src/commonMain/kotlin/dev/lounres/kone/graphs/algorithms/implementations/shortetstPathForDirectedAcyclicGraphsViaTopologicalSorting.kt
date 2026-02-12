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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.Path
import dev.lounres.kone.graphs.algorithms.*
import dev.lounres.kone.graphs.end
import dev.lounres.kone.graphs.outgoingIncidentEdgesOf
import dev.lounres.kone.graphs.weightOfType
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
private class HypergraphDirectedShortestPathWithFixedEndsComputerForDirectedAcyclicGraphsViaTopologicalSorting<Weight>(
    private val weightType: SuppliedType,
    private val weightMonoid: CommutativeMonoid<Weight>,
    private val weightsOrder: Order<Weight>,
    private val topologicalSortingComputer: TopologicalSortingComputer,
) : HypergraphDirectedShortestPathWithFixedEndsComputer<Weight> {
    override fun Hypergraph.directedShortestPathWithFixedEndsProvider(
        start: HypergraphVertex,
        end: HypergraphVertex
    ): HypergraphDirectedShortestPathWithFixedEndsProvider<Weight> {
        val lazyProvider = lazy {
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
            
            context(weightMonoid, weightsOrder) {
                for (vIndex in startIndex .. endIndex) {
                    val vPath = paths[vIndex - startIndex] ?: continue
                    if (paths.last().let { it != null && it.weight lt vPath.weight }) continue
                    val v = verticesList[vIndex]
                    for (e in outgoingIncidentEdgesOf(v)) {
                        val newWeight = vPath.weight + e.weightOfType<Weight>(weightType)
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
            }
            
            paths.last()
        }
        
        return HypergraphDirectedShortestPathWithFixedEndsProvider { lazyProvider.value }
    }
}

public fun <Weight> HypergraphDirectedShortestPathWithFixedEndsComputer.Companion.forDirectedAcyclicGraphsViaTopologicalSorting(
    weightType: SuppliedType,
    weightMonoid: CommutativeMonoid<Weight>,
    weightsOrder: Order<Weight>,
    topologicalSortingComputer: TopologicalSortingComputer,
): HypergraphDirectedShortestPathWithFixedEndsComputer<Weight> = HypergraphDirectedShortestPathWithFixedEndsComputerForDirectedAcyclicGraphsViaTopologicalSorting(
    weightType = weightType,
    weightMonoid = weightMonoid,
    weightsOrder = weightsOrder,
    topologicalSortingComputer = topologicalSortingComputer,
)

context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun <Weight> HypergraphDirectedShortestPathWithFixedEndsComputer.Companion.setForDirectedAcyclicGraphsViaTopologicalSorting(
    weightType: SuppliedType,
) {
    HypergraphDirectedShortestPathWithFixedEndsComputer.Key<Weight>(weightType) correspondsTo forDirectedAcyclicGraphsViaTopologicalSorting(
        weightType = weightType,
        weightMonoid = koneContextRegistryBuilder[CommutativeMonoid.Key<Weight>(weightType)],
        weightsOrder = koneContextRegistryBuilder[Order.Key<Weight>(weightType)],
        topologicalSortingComputer = koneContextRegistryBuilder[TopologicalSortingComputer.Key],
    )
}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
private class HypergraphDirectedShortestPathWithFixedStartComputerForDirectedAcyclicGraphsViaTopologicalSorting<Weight>(
    private val weightType: SuppliedType,
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
                    context(weightMonoid, weightsOrder) {
                        while (firstNotVisitedVertexIndexInPaths + startIndex < endIndex) {
                            val vIndex = firstNotVisitedVertexIndexInPaths + startIndex
                            val vPath = paths[vIndex - startIndex] ?: continue
                            val v = verticesList[vIndex]
                            for (e in outgoingIncidentEdgesOf(v)) {
                                val newWeight = vPath.weight + e.weightOfType<Weight>(weightType)
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
                    }
                    
                    return paths[endIndex - startIndex]
                }
            }
        }
}

public fun <Weight> HypergraphDirectedShortestPathWithFixedStartComputer.Companion.forDirectedAcyclicGraphsViaTopologicalSorting(
    weightType: SuppliedType,
    weightMonoid: CommutativeMonoid<Weight>,
    weightsOrder: Order<Weight>,
    topologicalSortingComputer: TopologicalSortingComputer,
): HypergraphDirectedShortestPathWithFixedStartComputer<Weight> = HypergraphDirectedShortestPathWithFixedStartComputerForDirectedAcyclicGraphsViaTopologicalSorting(
    weightType = weightType,
    weightMonoid = weightMonoid,
    weightsOrder = weightsOrder,
    topologicalSortingComputer = topologicalSortingComputer,
)

context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun <Weight> HypergraphDirectedShortestPathWithFixedStartComputer.Companion.setForDirectedAcyclicGraphsViaTopologicalSorting(
    weightType: SuppliedType,
) {
    HypergraphDirectedShortestPathWithFixedStartComputer.Key<Weight>(weightType) correspondsTo forDirectedAcyclicGraphsViaTopologicalSorting(
        weightType = weightType,
        weightMonoid = koneContextRegistryBuilder[CommutativeMonoid.Key<Weight>(weightType)],
        weightsOrder = koneContextRegistryBuilder[Order.Key<Weight>(weightType)],
        topologicalSortingComputer = koneContextRegistryBuilder[TopologicalSortingComputer.Key],
    )
}
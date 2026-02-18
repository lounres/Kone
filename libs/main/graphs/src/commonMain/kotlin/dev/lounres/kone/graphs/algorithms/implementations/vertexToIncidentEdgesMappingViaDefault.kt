package dev.lounres.kone.graphs.algorithms.implementations

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.KoneMutableReifiedMap
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.graphs.Hypergraph
import dev.lounres.kone.graphs.HypergraphEdge
import dev.lounres.kone.graphs.HypergraphVertex
import dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingComputer
import dev.lounres.kone.graphs.algorithms.VertexToIncidentEdgesMappingKey
import dev.lounres.kone.graphs.algorithms.vertexToIncidentEdgesMapping
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor


private object VertexToIncidentEdgesMappingComputerViaDefault : VertexToIncidentEdgesMappingComputer {
    override fun Hypergraph.vertexToIncidentEdgesMapping(): KoneReifiedMap<HypergraphVertex, KoneReifiedSet<HypergraphEdge>> {
        val vertexToIncidentEdgesMapping =
            KoneMutableReifiedMap.of<HypergraphVertex, KoneMutableReifiedSet<HypergraphEdge>>(
                keyEquality = Equality.absoluteFor(),
            )
        
        for (vertex in vertices)
            vertexToIncidentEdgesMapping.let { it[vertex] = KoneMutableReifiedSet.of(elementEquality = Equality.absoluteFor()) }
        
        for (edge in edges) for (vertex in edge.vertices)
            vertexToIncidentEdgesMapping.let { it[vertex].add(edge) }
        
        return vertexToIncidentEdgesMapping
    }
}

public fun VertexToIncidentEdgesMappingComputer.Companion.default(): VertexToIncidentEdgesMappingComputer = VertexToIncidentEdgesMappingComputerViaDefault

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun VertexToIncidentEdgesMappingComputer.Companion.setDefault() {
    VertexToIncidentEdgesMappingComputer.Key correspondsTo RegisteredValueProvider.cached { default() }
}

context(_: MutableOwnedRegistry<Hypergraph>, graph: Hypergraph.Provider)
public fun VertexToIncidentEdgesMappingComputer.Companion.useDefault() {
    VertexToIncidentEdgesMappingKey correspondsTo RegisteredValueProvider.cached {
        (default()) {
            graph.get().vertexToIncidentEdgesMapping()
        }
    }
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.computationalGeometry.Point


public interface Polytope<out N> {
    public val dimension: UInt
    public val faces: KoneList<KoneReifiedSet<Polytope<N>>>
    public fun facesOfDimension(dim: UInt): KoneReifiedSet<Polytope<N>>
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope<N>>
    public val vertices: KoneReifiedSet<Vertex<N>>
    public val cofaces: KoneList<KoneReifiedSet<Polytope<N>>>
    public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<Polytope<N>>
}

public interface Vertex<out N> : Polytope<N> {
    public val position: Point<N>
}

public interface PolytopicConstruction<out N> {
    public val spaceDimension: UInt

    public val polytopes: KoneList<KoneReifiedSet<Polytope<N>>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope<N>>
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope<N>>

    public val vertices: KoneReifiedSet<Vertex<N>>
}

//public interface PolytopicConstruction2<N, P, V: P>: PolytopicConstruction<N, P, V> {
//    override val spaceDimension: UInt get() = 2u
//
//    override val V.position: Point2<N>
//}

public interface RemovablePolytope<out N> : Polytope<N> {
    public fun remove()
}

public interface RemovableVertex<out N> : Vertex<N>, RemovablePolytope<N>

public interface MutablePolytopicConstruction<N>: PolytopicConstruction<N> {
    override val polytopes: KoneList<KoneReifiedSet<RemovablePolytope<N>>>
    override fun polytopesOfDimension(dim: UInt): KoneReifiedSet<RemovablePolytope<N>>
    override operator fun get(dim: UInt): KoneReifiedSet<RemovablePolytope<N>>
    
    override val vertices: KoneReifiedSet<RemovableVertex<N>>
    
    public fun addPolytope(vertices: KoneSet<RemovableVertex<N>>, faces: KoneList<KoneSet<RemovablePolytope<N>>>): RemovablePolytope<N>

    public fun addVertex(position: Point<N>): RemovableVertex<N>
}

//public interface MutablePolytopicConstruction2<N, P, V: P>: PolytopicConstruction2<N, P, V> {
//    public fun addPolytope(vertices: KoneSet<V>, faces: KoneList<KoneSet<P>>): P
//
//    public fun addVertex(position: Point2<N>): V
//}



//context(numberEquality: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.equalsTo(other: PolytopicConstruction<N, P2, V2>): Boolean {
//    if (this.spaceDimension != other.spaceDimension) return false
//    if ((0u..this.spaceDimension).any { this.polytopes[it].size != other.polytopes[it].size }) return false
//
//    val thisToOtherPolytopesMapping =
//        KoneList(this.spaceDimension + 1u) { koneMutableMapOf<P1, P2>(keyContext = this.polytopeContext) }
//    @Suppress("UNCHECKED_CAST")
//    val thisToOtherVertexMapping = thisToOtherPolytopesMapping[0u] as KoneMutableMap<V1, V2>
//
//    val thisPointToVertexMapping = this.vertices.associateBy(keyContext = pointEquality(numberEquality)) { it.position }
//    val otherPointToVertexMapping = other.vertices.associateBy(keyContext = pointEquality(numberEquality)) { other { it.position } }
//    if (koneSetEquality(pointEquality(numberEquality)).invoke { thisPointToVertexMapping.keysView neq otherPointToVertexMapping.keysView }) return false
//    for ((point, thisVertex) in thisPointToVertexMapping) thisToOtherVertexMapping[thisVertex] = otherPointToVertexMapping[point]
//
//    for (dim in 1u..this.spaceDimension) {
//        val dimMapping = thisToOtherPolytopesMapping[dim]
//        val thisFacesToPolytopeMapping =
//            this.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { polytope ->
//                this {
//                    polytope.faces.mapIndexed { dim, dimPolytopes -> dimPolytopes.map { thisToOtherPolytopesMapping[dim][it] }.toKoneSet(other.polytopeContext) }
//                }
//            }
//        val otherFacesToPolytopeMapping =
//            other.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { other { it.faces } }
//        if (koneSetEquality(koneListEquality(koneSetEquality(other.polytopeContext))).invoke { thisFacesToPolytopeMapping.keysView neq otherFacesToPolytopeMapping.keysView }) return false
//        for ((faces, thisPolytope) in thisFacesToPolytopeMapping) {
//            dimMapping[thisPolytope] = koneListEquality(koneSetEquality(other.polytopeContext)).invoke { otherFacesToPolytopeMapping[faces] }
//        }
//    }
//
//    return true
//}
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.notEqualsTo(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    !(this equalsTo other)
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.eq(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    this equalsTo other
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.neq(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    !(this eq other)
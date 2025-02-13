/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point


public interface PolytopicConstructionPolytope<
    out Number,
    out Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> {
    public val dimension: UInt
    public val faces: KoneList<KoneReifiedSet<Polytope>>
    public fun facesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = faces[dim] // TODO: Add corresponding error
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = facesOfDimension(dim)
    public val vertices: KoneReifiedSet<Vertex>
    public val cofaces: KoneList<KoneReifiedSet<Polytope>>
    public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = cofaces[dim]
}

public interface PolytopicConstructionVertex<
    out Number,
    out Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> {
    public val position: Point<Number>
    public fun asPolytope(): Polytope
}

public interface PolytopicConstruction<
    out Number,
    out Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> {
    public val spaceDimension: UInt

    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = polytopes[dim] // TODO: Add corresponding error
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = polytopesOfDimension(dim)

    public val vertices: KoneReifiedSet<Vertex>
}

public interface ExtendablePolytopicConstruction<
    Number,
    out Polytope: PolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstructionVertex<Number, Polytope, Vertex>,
> : PolytopicConstruction<Number, Polytope, Vertex> {
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point<Number>): Vertex
}

public interface RemovablePolytopicConstructionPolytope<
    out Number,
    out Polytope: RemovablePolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstructionVertex<Number, Polytope, Vertex>,
> : PolytopicConstructionPolytope<Number, Polytope, Vertex> {
    public fun remove()
}

public interface RemovablePolytopicConstructionVertex<
    out Number,
    out Polytope: RemovablePolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstructionVertex<Number, Polytope, Vertex>,
> : PolytopicConstructionVertex<Number, Polytope, Vertex> {
    public fun remove()
}

public interface ReduciblePolytopicConstruction<
    Number,
    out Polytope: RemovablePolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstructionVertex<Number, Polytope, Vertex>,
> : PolytopicConstruction<Number, Polytope, Vertex>

public interface MutablePolytopicConstruction<
    Number,
    out Polytope: RemovablePolytopicConstructionPolytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstructionVertex<Number, Polytope, Vertex>,
> : ExtendablePolytopicConstruction<Number, Polytope, Vertex>, ReduciblePolytopicConstruction<Number, Polytope, Vertex>





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
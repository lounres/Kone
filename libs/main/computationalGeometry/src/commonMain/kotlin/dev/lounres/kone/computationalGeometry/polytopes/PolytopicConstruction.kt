/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet


public interface PolytopicConstruction<
    out Point,
    out Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    out Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> {
    public val spaceDimension: UInt

    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = polytopes[dim] // TODO: Add corresponding error
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = polytopesOfDimension(dim)

    public val vertices: KoneReifiedSet<Vertex>
    
    public interface Polytope<
        out Point,
        out PolytopeType: Polytope<Point, PolytopeType, VertexType>,
        out VertexType: Vertex<Point, PolytopeType, VertexType>,
    > {
        public val dimension: UInt
        public val faces: KoneList<KoneReifiedSet<PolytopeType>>
        public fun facesOfDimension(dim: UInt): KoneReifiedSet<PolytopeType> = faces[dim] // TODO: Add corresponding error
        public operator fun get(dim: UInt): KoneReifiedSet<PolytopeType> = facesOfDimension(dim)
        public val vertices: KoneReifiedSet<VertexType>
        public val cofaces: KoneList<KoneReifiedSet<PolytopeType>>
        public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<PolytopeType> = cofaces[dim - dimension - 1u]
    }
    
    public interface Vertex<
        out Point,
        out PolytopeType: Polytope<Point, PolytopeType, VertexType>,
        out VertexType: Vertex<Point, PolytopeType, VertexType>,
    > {
        public val position: Point
        public fun asPolytope(): PolytopeType
    }
}

public interface ExtendablePolytopicConstruction<
    Point,
    out Polytope: PolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    out Vertex: PolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> : PolytopicConstruction<Point, Polytope, Vertex> {
    @IgnorableReturnValue
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    @IgnorableReturnValue
    public fun addVertex(position: Point): Vertex
}

public interface ReduciblePolytopicConstruction<
    out Point,
    out Polytope: ReduciblePolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> : PolytopicConstruction<Point, Polytope, Vertex> {
    public interface Polytope<
        out Point,
        out PolytopeType: Polytope<Point, PolytopeType, VertexType>,
        out VertexType: Vertex<Point, PolytopeType, VertexType>,
    > : PolytopicConstruction.Polytope<Point, PolytopeType, VertexType> {
        public fun remove()
    }
    
    public interface Vertex<
        out Point,
        out PolytopeType: Polytope<Point, PolytopeType, VertexType>,
        out VertexType: Vertex<Point, PolytopeType, VertexType>,
    > : PolytopicConstruction.Vertex<Point, PolytopeType, VertexType> {
        public fun remove()
    }
}

public interface MutablePolytopicConstruction<
    Point,
    out Polytope: ReduciblePolytopicConstruction.Polytope<Point, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction.Vertex<Point, Polytope, Vertex>,
> : ExtendablePolytopicConstruction<Point, Polytope, Vertex>, ReduciblePolytopicConstruction<Point, Polytope, Vertex>





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
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point4
import dev.lounres.kone.multidimensionalCollections.MDList1


public interface PolytopicConstruction4<
    out Number,
    out PointContent: MDList1<Number>,
    out Polytope: PolytopicConstruction4.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4.Vertex<Number, PointContent, Polytope, Vertex>,
> {
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = polytopes[dim]
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = polytopesOfDimension(dim)
    
    public val vertices: KoneReifiedSet<Vertex>
    
    public interface Polytope<
        out Number,
        out PointContent: MDList1<Number>,
        out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
        out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
    > {
        public val dimension: UInt
        public val faces: KoneList<KoneReifiedSet<PolytopeType>>
        public fun facesOfDimension(dim: UInt): KoneReifiedSet<PolytopeType> = faces[dim]
        public operator fun get(dim: UInt): KoneReifiedSet<PolytopeType> = facesOfDimension(dim)
        public val vertices: KoneReifiedSet<VertexType>
        public val cofaces: KoneList<KoneReifiedSet<PolytopeType>>
        public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<PolytopeType> = cofaces[dim - dimension - 1u]
    }
    
    public interface Vertex<
        out Number,
        out PointContent: MDList1<Number>,
        out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
        out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
    > {
        public val position: Point4<Number, PointContent>
        public fun asPolytope(): PolytopeType
    }
}

public interface ExtendablePolytopicConstruction4<
    Number,
    PointContent: MDList1<Number>,
    out Polytope: PolytopicConstruction4.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4.Vertex<Number, PointContent, Polytope, Vertex>,
> : PolytopicConstruction4<Number, PointContent, Polytope, Vertex> {
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point4<Number, PointContent>): Vertex
}

public interface ReduciblePolytopicConstruction4<
    out Number,
    out PointContent: MDList1<Number>,
    out Polytope: ReduciblePolytopicConstruction4.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction4.Vertex<Number, PointContent, Polytope, Vertex>,
> : PolytopicConstruction4<Number, PointContent, Polytope, Vertex> {
    public interface Polytope<
        out Number,
        out PointContent: MDList1<Number>,
        out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
        out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
    > : PolytopicConstruction4.Polytope<Number, PointContent, PolytopeType, VertexType> {
        public fun remove()
    }
    
    public interface Vertex<
        out Number,
        out PointContent: MDList1<Number>,
        out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
        out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
    > : PolytopicConstruction4.Vertex<Number, PointContent, PolytopeType, VertexType> {
        public fun remove()
    }
}

public interface MutablePolytopicConstruction4<
    Number,
    PointContent: MDList1<Number>,
    out Polytope: ReduciblePolytopicConstruction4.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction4.Vertex<Number, PointContent, Polytope, Vertex>,
> : ExtendablePolytopicConstruction4<Number, PointContent, Polytope, Vertex>, ReduciblePolytopicConstruction4<Number, PointContent, Polytope, Vertex>
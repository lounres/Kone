/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.multidimensionalCollections.MDList1


public interface PolytopicConstruction2<
    out Number,
    out PointContent: MDList1<Number>,
    out Polytope: PolytopicConstruction2.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: PolytopicConstruction2.Vertex<Number, PointContent, Polytope, Vertex>,
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
        public val position: Point2<Number, PointContent>
        public fun asPolytope(): PolytopeType
    }
}

public interface ExtendablePolytopicConstruction2<
    Number,
    PointContent: MDList1<Number>,
    out Polytope: PolytopicConstruction2.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: PolytopicConstruction2.Vertex<Number, PointContent, Polytope, Vertex>,
> : PolytopicConstruction2<Number, PointContent, Polytope, Vertex> {
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point2<Number, PointContent>): Vertex
}

public interface ReduciblePolytopicConstruction2<
    out Number,
    out PointContent: MDList1<Number>,
    out Polytope: ReduciblePolytopicConstruction2.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction2.Vertex<Number, PointContent, Polytope, Vertex>,
> : PolytopicConstruction2<Number, PointContent, Polytope, Vertex> {
    public interface Polytope<
            out Number,
            out PointContent: MDList1<Number>,
            out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
            out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
            > : PolytopicConstruction2.Polytope<Number, PointContent, PolytopeType, VertexType> {
        public fun remove()
    }
    
    public interface Vertex<
            out Number,
            out PointContent: MDList1<Number>,
            out PolytopeType: Polytope<Number, PointContent, PolytopeType, VertexType>,
            out VertexType: Vertex<Number, PointContent, PolytopeType, VertexType>,
            > : PolytopicConstruction2.Vertex<Number, PointContent, PolytopeType, VertexType> {
        public fun remove()
    }
}

public interface MutablePolytopicConstruction2<
    Number,
    PointContent: MDList1<Number>,
    out Polytope: ReduciblePolytopicConstruction2.Polytope<Number, PointContent, Polytope, Vertex>,
    out Vertex: ReduciblePolytopicConstruction2.Vertex<Number, PointContent, Polytope, Vertex>,
> : ExtendablePolytopicConstruction2<Number, PointContent, Polytope, Vertex>, ReduciblePolytopicConstruction2<Number, PointContent, Polytope, Vertex>
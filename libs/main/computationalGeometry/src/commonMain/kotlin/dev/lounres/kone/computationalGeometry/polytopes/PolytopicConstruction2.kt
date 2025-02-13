/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point2


public interface PolytopicConstruction2Polytope<
        out Number,
        out Polytope: PolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: PolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > {
    public val dimension: UInt
    public val faces: KoneList<KoneReifiedSet<Polytope>>
    public fun facesOfDimension(dim: UInt): KoneReifiedSet<Polytope>
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope>
    public val vertices: KoneReifiedSet<Vertex>
    public val cofaces: KoneList<KoneReifiedSet<Polytope>>
    public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<Polytope>
}

public interface PolytopicConstruction2Vertex<
        out Number,
        out Polytope: PolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: PolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > {
    public val position: Point2<Number>
    public fun asPolytope(): Polytope
}

public interface PolytopicConstruction2<
        out Number,
        out Polytope: PolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: PolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > {
    public val spaceDimension: UInt
    
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope>
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope>
    
    public val vertices: KoneReifiedSet<Vertex>
}

public interface ExtendablePolytopicConstruction2<
        Number,
        out Polytope: PolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: PolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > : PolytopicConstruction2<Number, Polytope, Vertex> {
    public fun addPolytope(
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point<Number>): Vertex
}

public interface RemovablePolytopicConstruction2Polytope<
        out Number,
        out Polytope: RemovablePolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: RemovablePolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > : PolytopicConstruction2Polytope<Number, Polytope, Vertex> {
    public fun remove()
}

public interface RemovablePolytopicConstruction2Vertex<
        out Number,
        out Polytope: RemovablePolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: RemovablePolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > : PolytopicConstruction2Vertex<Number, Polytope, Vertex> {
    public fun remove()
}

public interface ReduciblePolytopicConstruction2<
        Number,
        out Polytope: RemovablePolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: RemovablePolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > : PolytopicConstruction2<Number, Polytope, Vertex>

public interface MutablePolytopicConstruction2<
        Number,
        out Polytope: RemovablePolytopicConstruction2Polytope<Number, Polytope, Vertex>,
        out Vertex: RemovablePolytopicConstruction2Vertex<Number, Polytope, Vertex>,
        > : ExtendablePolytopicConstruction2<Number, Polytope, Vertex>, ReduciblePolytopicConstruction2<Number, Polytope, Vertex>
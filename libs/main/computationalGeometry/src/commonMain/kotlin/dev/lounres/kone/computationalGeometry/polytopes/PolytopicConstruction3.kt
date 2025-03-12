/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point3


public interface PolytopicConstruction3Polytope<
    out Number,
    out Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> {
    public val dimension: UInt
    public val faces: KoneList<KoneReifiedSet<Polytope>>
    public fun facesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = faces[dim]
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = facesOfDimension(dim)
    public val vertices: KoneReifiedSet<Vertex>
    public val cofaces: KoneList<KoneReifiedSet<Polytope>>
    public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = cofaces[dim - dimension - 1u]
}

public interface PolytopicConstruction3Vertex<
    out Number,
    out Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> {
    public val position: Point3<Number>
    public fun asPolytope(): Polytope
}

public interface PolytopicConstruction3<
    out Number,
    out Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> {
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = polytopes[dim]
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = polytopesOfDimension(dim)
    
    public val vertices: KoneReifiedSet<Vertex>
}

public interface ExtendablePolytopicConstruction3<
    Number,
    out Polytope: PolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction3<Number, Polytope, Vertex> {
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point3<Number>): Vertex
}

public interface RemovablePolytopicConstruction3Polytope<
    out Number,
    out Polytope: RemovablePolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction3Polytope<Number, Polytope, Vertex> {
    public fun remove()
}

public interface RemovablePolytopicConstruction3Vertex<
    out Number,
    out Polytope: RemovablePolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction3Vertex<Number, Polytope, Vertex> {
    public fun remove()
}

public interface ReduciblePolytopicConstruction3<
    Number,
    out Polytope: RemovablePolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction3<Number, Polytope, Vertex>

public interface MutablePolytopicConstruction3<
    Number,
    out Polytope: RemovablePolytopicConstruction3Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction3Vertex<Number, Polytope, Vertex>,
> : ExtendablePolytopicConstruction3<Number, Polytope, Vertex>, ReduciblePolytopicConstruction3<Number, Polytope, Vertex>
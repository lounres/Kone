/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point4


public interface PolytopicConstruction4Polytope<
    out Number,
    out Polytope: PolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> {
    public val dimension: UInt
    public val faces: KoneList<KoneReifiedSet<Polytope>>
    public fun facesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = faces[dim]
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = facesOfDimension(dim)
    public val vertices: KoneReifiedSet<Vertex>
    public val cofaces: KoneList<KoneReifiedSet<Polytope>>
    public fun cofacesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = cofaces[dim - dimension - 1u]
}

public interface PolytopicConstruction4Vertex<
    out Number,
    out Polytope: PolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> {
    public val position: Point4<Number>
    public fun asPolytope(): Polytope
}

public interface PolytopicConstruction4<
    out Number,
    out Polytope: PolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> {
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    public fun polytopesOfDimension(dim: UInt): KoneReifiedSet<Polytope> = polytopes[dim]
    public operator fun get(dim: UInt): KoneReifiedSet<Polytope> = polytopesOfDimension(dim)
    
    public val vertices: KoneReifiedSet<Vertex>
}

public interface ExtendablePolytopicConstruction4<
    Number,
    out Polytope: PolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: PolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction4<Number, Polytope, Vertex> {
    public fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<@UnsafeVariance Vertex>,
        faces: KoneList<KoneReifiedSet<@UnsafeVariance Polytope>>
    ): Polytope
    
    public fun addVertex(position: Point4<Number>): Vertex
}

public interface RemovablePolytopicConstruction4Polytope<
    out Number,
    out Polytope: RemovablePolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction4Polytope<Number, Polytope, Vertex> {
    public fun remove()
}

public interface RemovablePolytopicConstruction4Vertex<
    out Number,
    out Polytope: RemovablePolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction4Vertex<Number, Polytope, Vertex> {
    public fun remove()
}

public interface ReduciblePolytopicConstruction4<
    Number,
    out Polytope: RemovablePolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> : PolytopicConstruction4<Number, Polytope, Vertex>

public interface MutablePolytopicConstruction4<
    Number,
    out Polytope: RemovablePolytopicConstruction4Polytope<Number, Polytope, Vertex>,
    out Vertex: RemovablePolytopicConstruction4Vertex<Number, Polytope, Vertex>,
> : ExtendablePolytopicConstruction4<Number, Polytope, Vertex>, ReduciblePolytopicConstruction4<Number, Polytope, Vertex>
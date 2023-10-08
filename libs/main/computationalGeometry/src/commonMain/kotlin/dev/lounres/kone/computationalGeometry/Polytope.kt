/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry


/**
 * See [polytope](https://en.wikipedia.org/wiki/Polytope) and [abstract polytope](https://en.wikipedia.org/wiki/Abstract_polytope)
 */
public interface Face {
    public val rank: UInt
    public val faces: List<Set<Face>>
    public val vertices: Set<Vertex>
}

public abstract class Vertex: Face {
    public final override val rank: UInt = 0u
    public final override val faces: List<Set<Face>> by lazy { listOf(vertices) }
    public final override val vertices: Set<Vertex> by lazy { setOf(this) }
}

public interface PolytopicConstruction<P, V: P> {
    public val P.rank: UInt
    public val P.faces: List<Set<P>>
    public val P.vertices: Set<V>

    public val V.coordinates: Nothing?
}

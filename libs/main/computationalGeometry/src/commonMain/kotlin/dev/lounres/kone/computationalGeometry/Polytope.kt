/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.context.KoneContext
import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction<N, P, V: P>: KoneContext {
    public val spaceDimension: UInt

    public val polytopes: List<Set<P>>
    public fun polytopesOfDimension(dim: UInt): Set<P>
    public operator fun get(dim: UInt): Set<P>
    public val P.dimension: UInt
    public val P.faces: List<Set<P>>
    public fun P.facesOfDimension(dim: UInt): Set<P>
    public operator fun P.get(dim: UInt): Set<P>
    public val P.vertices: Set<V>

    public val vertices: Set<V>
    public val V.coordinates: Point<N>
}

public interface PolytopicConstruction2D<N, P, V: P>: PolytopicConstruction<N, P, V> {
    override val spaceDimension: UInt get() = 2u

    override val V.coordinates: Point2<N>
}

public interface MutablePolytopicConstruction<N, P, V: P>: PolytopicConstruction<N, P, V> {
    public fun addPolytope(vertices: Set<V>, faces: List<Set<P>>): P
    public fun removePolytope(polytope: P)
    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point<N>): V
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, P, V: P> buildPolytopicConstruction(@BuilderInference builder: MutablePolytopicConstruction<N, P, V>.() -> Unit): PolytopicConstruction<N, P, V> = TODO()
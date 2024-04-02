/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke


public interface PolytopicConstruction<out N, P, V: P>: KoneContext {
    public val polytopeContext: Equality<P>

    public val spaceDimension: UInt

    public val polytopes: KoneIterableList<KoneIterableSet<P>>
    public fun polytopesOfDimension(dim: UInt): KoneIterableSet<P>
    public operator fun get(dim: UInt): KoneIterableSet<P>
    public val P.dimension: UInt
    public val P.faces: KoneIterableList<KoneIterableSet<P>>
    public fun P.facesOfDimension(dim: UInt): KoneIterableSet<P>
    public operator fun P.get(dim: UInt): KoneIterableSet<P>
    public val P.vertices: KoneIterableSet<V>
    public val P.cofaces: KoneIterableList<KoneIterableSet<P>>
    public fun P.cofacesOfDimension(dim: UInt): KoneIterableSet<P>

    public val vertices: KoneIterableSet<V>
    public val V.coordinates: Point<N>
}

internal interface PolytopicConstructionWithContexts<N, P, V: P> : PolytopicConstruction<N, P, V> {
    val numberContext: Equality<N>

    override val P.cofaces: KoneIterableList<KoneIterableSet<P>>
        get() =
            polytopes
                .drop(this.dimension + 1u)
                .map(elementContext = koneIterableSetEquality(polytopeContext)) { polytopes ->
                    polytopes.filterTo(koneMutableIterableSetOf(elementContext = polytopeContext)) { this in it.facesOfDimension(this.dimension) }
                }
    override fun P.cofacesOfDimension(dim: UInt): KoneIterableSet<P> =
        polytopes[dim].filterTo(koneMutableIterableSetOf(elementContext = polytopeContext)) { this in it.facesOfDimension(this.dimension) }
}

//public interface PolytopicConstruction2<N, P, V: P>: PolytopicConstruction<N, P, V> {
//    override val spaceDimension: UInt get() = 2u
//
//    override val V.coordinates: Point2<N>
//}

public interface MutablePolytopicConstruction<N, P, V: P>: PolytopicConstruction<N, P, V> {
    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
//    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point<N>): V
}

//public interface MutablePolytopicConstruction2<N, P, V: P>: PolytopicConstruction2<N, P, V> {
//    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
//    public fun removePolytope(polytope: P)
////    public operator fun P.set(dim: UInt, faces: Set<P>)
//
//    public fun addVertex(coordinates: Point2<N>): V
//}

context(NE)
public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.eq(other: PolytopicConstruction<N, P2, V2>): Boolean {
    if (this.spaceDimension != other.spaceDimension) return false
    if ((0u..this.spaceDimension).any { this.polytopes[it].size != other.polytopes[it].size }) return false

    val thisToOtherPolytopesMapping = KoneIterableList(this.spaceDimension + 1u, elementContext = koneMapEquality(keyEquality = this.polytopeContext, valueEquality = other.polytopeContext)) { koneMutableMapOf<P1, P2>(keyContext = this.polytopeContext, valueContext = other.polytopeContext) }
    @Suppress("UNCHECKED_CAST")
    val thisToOtherVertexMapping = thisToOtherPolytopesMapping[0u] as KoneMutableMap<V1, V2>

    val thisPointToVertexMapping = this.vertices.associateBy(keyContext = pointEquality(this@NE), valueContext = this.polytopeContext) { it.coordinates }
    val otherPointToVertexMapping = other.vertices.associateBy(keyContext = pointEquality(this@NE), valueContext = other.polytopeContext) { other { it.coordinates } }
    if (koneIterableSetEquality(pointEquality(this@NE)).invoke { thisPointToVertexMapping.keys neq otherPointToVertexMapping.keys }) return false
    for ((point, thisVertex) in thisPointToVertexMapping) {
        thisToOtherVertexMapping[thisVertex] =
            otherPointToVertexMapping[point]
    }

    for (dim in 1u..this.spaceDimension) {
        val dimMapping = thisToOtherPolytopesMapping[dim]
        val thisFacesToPolytopeMapping =
            this.polytopes[dim].associateBy(keyContext = koneIterableListEquality(koneIterableSetEquality(other.polytopeContext))) { polytope -> this { polytope.faces.mapIndexed { dim, dimPolytopes -> dimPolytopes.map { thisToOtherPolytopesMapping[dim][it] }.toKoneIterableSet(other.polytopeContext) } } }
        val otherFacesToPolytopeMapping =
            other.polytopes[dim].associateBy(keyContext = koneIterableListEquality(koneIterableSetEquality(other.polytopeContext))) { other { it.faces } }
        if (koneIterableSetEquality(koneIterableListEquality(koneIterableSetEquality(other.polytopeContext))).invoke { thisFacesToPolytopeMapping.keys neq otherFacesToPolytopeMapping.keys }) return false
        for ((faces, thisPolytope) in thisFacesToPolytopeMapping) {
            dimMapping[thisPolytope] = koneIterableListEquality(koneIterableSetEquality(other.polytopeContext)).invoke { otherFacesToPolytopeMapping[faces] }
        }
    }

    return true
}
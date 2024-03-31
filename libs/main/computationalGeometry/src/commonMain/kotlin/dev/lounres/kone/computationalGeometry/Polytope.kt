/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.koneIterableSetEquality
import dev.lounres.kone.collections.utils.drop
import dev.lounres.kone.collections.utils.filterTo
import dev.lounres.kone.collections.utils.koneMutableIterableSetOf
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext


public interface PolytopicConstruction<N, P, V: P>: KoneContext {
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

internal interface PolytopicConstructionFrame<N, P, V: P> : PolytopicConstruction<N, P, V> {
    val numberContext: Equality<N>
    val polytopeContext: Equality<P>

    override val P.cofaces: KoneIterableList<KoneIterableSet<P>>
        get() =
            polytopes
                .drop(this.dimension + 1u)
                .map(context = koneIterableSetEquality(polytopeContext)) { polytopes ->
                    polytopes.filterTo(koneMutableIterableSetOf(context = polytopeContext)) { this in it.facesOfDimension(this.dimension) }
                }
    override fun P.cofacesOfDimension(dim: UInt): KoneIterableSet<P> =
        polytopes[dim].filterTo(koneMutableIterableSetOf(context = polytopeContext)) { this in it.facesOfDimension(this.dimension) }
}

public interface PolytopicConstruction2<N, P, V: P>: PolytopicConstruction<N, P, V> {
    override val spaceDimension: UInt get() = 2u

    override val V.coordinates: Point2<N>
}

public interface MutablePolytopicConstruction<N, P, V: P>: PolytopicConstruction<N, P, V> {
    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point<N>): V
}

public interface MutablePolytopicConstruction2<N, P, V: P>: PolytopicConstruction2<N, P, V> {
    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point2<N>): V
}

//context(NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, PE1: Equality<P1>, P2, V2: P2, PE2: Equality<P2>> PolytopicConstruction<N, NE, P1, V1, PE1>.eq(other: PolytopicConstruction<N, NE, P2, V2, PE2>): Boolean {
//    if (this.numberContext != other.numberContext) return false // TODO: Check if this is a good idea: should we not provide the number context or not? And questions like this.
//    if (this.spaceDimension != other.spaceDimension) return false
//    if ((0u..this.spaceDimension).any { this.polytopes[it].size != other.polytopes[it].size }) return false
//
//    val thisToOtherPolytopesMapping = KoneIterableList(this.spaceDimension + 1u, context = koneMapEquality(keyEquality = this.polytopeContext, valueEquality = other.polytopeContext)) { koneMutableMapOf<P1, P2>(keyContext = this.polytopeContext, valueContext = other.polytopeContext) }
//    @Suppress("UNCHECKED_CAST")
//    val thisToOtherVertexMapping = thisToOtherPolytopesMapping[0u] as KoneMutableMap<V1, V2>
//
//    val thisPointToVertexMapping = this.vertices.associateBy(keyContext = pointEquality(this@NE), valueContext = this.polytopeContext) { it.coordinates }
//    val otherPointToVertexMapping = other.vertices.associateBy(keyContext = pointEquality(this@NE), valueContext = other.polytopeContext) { other { it.coordinates } }
//    if (koneIterableSetEquality(pointEquality(this@NE)).invoke { thisPointToVertexMapping.keys neq otherPointToVertexMapping.keys }) return false
//    for ((point, thisVertex) in thisPointToVertexMapping) {
//        thisToOtherVertexMapping[thisVertex] = pointEquality(this@NE).invoke { otherPointToVertexMapping[point] }
//    }
//
//    for (dim in 1u..this.spaceDimension) {
//        val dimMapping = thisToOtherPolytopesMapping[dim]
//        val thisFacesToPolytopeMapping = koneIterableListEquality(koneIterableSetEquality(other.polytopeContext)).invoke {
//            this.polytopes[dim].associateBy { polytope -> this { polytope.faces.mapIndexed { dim, dimPolytopes -> dimPolytopes.map { thisToOtherPolytopesMapping[dim][it] }.toKoneIterableSet() } } }
//        }
//        val otherFacesToPolytopeMapping = koneIterableListEquality(koneIterableSetEquality(this@PE2)).invoke {
//            other.polytopes[dim].associateBy { other.invoke { it.faces } }
//        }
//        if (koneIterableSetEquality(koneIterableListEquality(koneIterableSetEquality(this@PE2))).invoke { thisFacesToPolytopeMapping.keys neq otherFacesToPolytopeMapping.keys }) return false
//        for ((faces, thisPolytope) in thisFacesToPolytopeMapping) {
//            dimMapping[thisPolytope] = koneIterableListEquality(koneIterableSetEquality(this@PE2)).invoke { otherFacesToPolytopeMapping[faces] }
//        }
//    }
//
//    return true
//}
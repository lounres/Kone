/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke


public interface PolytopicConstruction<out N, P, V: P> : KoneContext {
    public val polytopeContext: Equality<P>

    public val spaceDimension: UInt

    public val polytopes: KoneList<KoneSet<P>>
    public fun polytopesOfDimension(dim: UInt): KoneSet<P>
    public operator fun get(dim: UInt): KoneSet<P>
    public val P.dimension: UInt
    public val P.faces: KoneList<KoneSet<P>>
    public fun P.facesOfDimension(dim: UInt): KoneSet<P>
    public operator fun P.get(dim: UInt): KoneSet<P>
    public val P.vertices: KoneSet<V>
    public val P.cofaces: KoneList<KoneSet<P>>
    public fun P.cofacesOfDimension(dim: UInt): KoneSet<P>

    public val vertices: KoneSet<V>
    public val V.position: Point<N>
}

public interface PolytopicConstructionWithContexts<N, out NC: Equality<N>, P, V: P> : PolytopicConstruction<N, P, V> {
    public val numberContext: NC
}

//public interface PolytopicConstruction2<N, P, V: P>: PolytopicConstruction<N, P, V> {
//    override val spaceDimension: UInt get() = 2u
//
//    override val V.position: Point2<N>
//}

public interface MutablePolytopicConstruction<N, P, V: P>: PolytopicConstruction<N, P, V> {
    public fun addPolytope(vertices: KoneSet<V>, faces: KoneList<KoneSet<P>>): P
//    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(position: Point<N>): V
}

//public interface MutablePolytopicConstruction2<N, P, V: P>: PolytopicConstruction2<N, P, V> {
//    public fun addPolytope(vertices: KoneSet<V>, faces: KoneList<KoneSet<P>>): P
//    public fun removePolytope(polytope: P)
////    public operator fun P.set(dim: UInt, faces: Set<P>)
//
//    public fun addVertex(position: Point2<N>): V
//}

context(NE)
public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.equalsTo(other: PolytopicConstruction<N, P2, V2>): Boolean {
    if (this.spaceDimension != other.spaceDimension) return false
    if ((0u..this.spaceDimension).any { this.polytopes[it].size != other.polytopes[it].size }) return false

    val thisToOtherPolytopesMapping = KoneList(this.spaceDimension + 1u) { koneMutableMapOf<P1, P2>(keyContext = this.polytopeContext) }
    @Suppress("UNCHECKED_CAST")
    val thisToOtherVertexMapping = thisToOtherPolytopesMapping[0u] as KoneMutableMap<V1, V2>

    val thisPointToVertexMapping = this.vertices.associateBy(keyContext = pointEquality(this@NE)) { it.position }
    val otherPointToVertexMapping = other.vertices.associateBy(keyContext = pointEquality(this@NE)) { other { it.position } }
    if (koneSetEquality(pointEquality(this@NE)).invoke { thisPointToVertexMapping.keysView neq otherPointToVertexMapping.keysView }) return false
    for ((point, thisVertex) in thisPointToVertexMapping) thisToOtherVertexMapping[thisVertex] = otherPointToVertexMapping[point]

    for (dim in 1u..this.spaceDimension) {
        val dimMapping = thisToOtherPolytopesMapping[dim]
        val thisFacesToPolytopeMapping =
            this.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { polytope ->
                this {
                    polytope.faces.mapIndexed { dim, dimPolytopes -> dimPolytopes.map { thisToOtherPolytopesMapping[dim][it] }.toKoneSet(other.polytopeContext) }
                }
            }
        val otherFacesToPolytopeMapping =
            other.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { other { it.faces } }
        if (koneSetEquality(koneListEquality(koneSetEquality(other.polytopeContext))).invoke { thisFacesToPolytopeMapping.keysView neq otherFacesToPolytopeMapping.keysView }) return false
        for ((faces, thisPolytope) in thisFacesToPolytopeMapping) {
            dimMapping[thisPolytope] = koneListEquality(koneSetEquality(other.polytopeContext)).invoke { otherFacesToPolytopeMapping[faces] }
        }
    }

    return true
}
context(NE)
public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.notEqualsTo(other: PolytopicConstruction<N, P2, V2>): Boolean =
    !(this equalsTo other)
context(NE)
public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.eq(other: PolytopicConstruction<N, P2, V2>): Boolean =
    this equalsTo other
context(NE)
public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.neq(other: PolytopicConstruction<N, P2, V2>): Boolean =
    !(this eq other)
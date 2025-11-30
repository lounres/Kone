/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.forEachIndexed
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.suppliedTypes.SuppliedType


@Suppress("EqualsOrHashCode")
public class Polytope(
    public val dimension: UInt,
    public val faces: KoneList<KoneReifiedSet<Polytope>>,
    public val properties: Registry = Registry.Empty,
) {
    init {
        require(faces.size == dimension) { "Cannot instantiate polytope: dimension parameter $dimension is not equal to size ${faces.size} of faces list" }
    }
    
    override fun equals(other: Any?): Boolean = this === other
}

public interface PolytopicConstruction {
    public val spaceDimension: UInt
    
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    
    public val properties: Registry get() = Registry.Empty
}

public interface MutablePolytopicConstruction : PolytopicConstruction {
    public fun add(polytope: Polytope)
    public fun remove(polytope: Polytope)
    override var properties: Registry
}

public fun MutablePolytopicConstruction(
    spaceDimension: UInt,
    properties: Registry = Registry.Empty,
): MutablePolytopicConstruction = MutablePolytopicConstructionImpl(
    spaceDimension = spaceDimension,
    properties = properties,
)

private class MutablePolytopicConstructionImpl(
    override val spaceDimension: UInt,
    override var properties: Registry
) : MutablePolytopicConstruction {
    final override val polytopes: KoneList<KoneReifiedSet<Polytope>>
        field: KoneList<KoneMutableReifiedSet<Polytope>> =
        KoneList.generate(spaceDimension + 1u) {
            KoneMutableReifiedSet.of(
                elementReification = Reification.defaultFor(),
                elementEquality = Equality.absoluteFor(),
                elementHashing = Hashing.defaultFor(),
            )
        }
    
    override fun add(polytope: Polytope) {
        require(polytope.dimension <= spaceDimension) { "Cannot add polytope of dimension ${polytope.dimension} in polytiopic construction of dimension $spaceDimension" }
        polytope.faces.forEachIndexed { dim, faces -> polytopes[dim].addAllFrom(faces) }
        polytopes[polytope.dimension].add(polytope)
    }
    
    override fun remove(polytope: Polytope) {
        if (polytope.dimension > spaceDimension) return
        polytopes[polytope.dimension].remove(polytope)
        for (dim in polytope.dimension + 1u .. spaceDimension)
            polytopes[dim].removeAllThat { polytope in it.faces[polytope.dimension] }
    }
}

public class Position<Point>(public val pointType: SuppliedType) : RegistryKey<Point> {
    override fun equals(other: Any?): Boolean = other is Position<*> && pointType == other.pointType
    override fun hashCode(): Int = pointType.hashCode()
}

public val Polytope.verticesOrSelf: KoneReifiedSet<Polytope>
    get() =
        if (dimension != 0u) faces[0u]
        else KoneReifiedSet.of(
            this,
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )





//context(numberEquality: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.equalsTo(other: PolytopicConstruction<N, P2, V2>): Boolean {
//    if (this.spaceDimension != other.spaceDimension) return false
//    if ((0u..this.spaceDimension).any { this.polytopes[it].size != other.polytopes[it].size }) return false
//
//    val thisToOtherPolytopesMapping =
//        KoneList(this.spaceDimension + 1u) { koneMutableMapOf<P1, P2>(keyContext = this.polytopeContext) }
//    @Suppress("UNCHECKED_CAST")
//    val thisToOtherVertexMapping = thisToOtherPolytopesMapping[0u] as KoneMutableMap<V1, V2>
//
//    val thisPointToVertexMapping = this.vertices.associateBy(keyContext = pointEquality(numberEquality)) { it.position }
//    val otherPointToVertexMapping = other.vertices.associateBy(keyContext = pointEquality(numberEquality)) { other { it.position } }
//    if (koneSetEquality(pointEquality(numberEquality)).invoke { thisPointToVertexMapping.keysView neq otherPointToVertexMapping.keysView }) return false
//    for ((point, thisVertex) in thisPointToVertexMapping) thisToOtherVertexMapping[thisVertex] = otherPointToVertexMapping[point]
//
//    for (dim in 1u..this.spaceDimension) {
//        val dimMapping = thisToOtherPolytopesMapping[dim]
//        val thisFacesToPolytopeMapping =
//            this.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { polytope ->
//                this {
//                    polytope.faces.mapIndexed { dim, dimPolytopes -> dimPolytopes.map { thisToOtherPolytopesMapping[dim][it] }.toKoneSet(other.polytopeContext) }
//                }
//            }
//        val otherFacesToPolytopeMapping =
//            other.polytopes[dim].associateBy(keyContext = koneListEquality(koneSetEquality(other.polytopeContext))) { other { it.faces } }
//        if (koneSetEquality(koneListEquality(koneSetEquality(other.polytopeContext))).invoke { thisFacesToPolytopeMapping.keysView neq otherFacesToPolytopeMapping.keysView }) return false
//        for ((faces, thisPolytope) in thisFacesToPolytopeMapping) {
//            dimMapping[thisPolytope] = koneListEquality(koneSetEquality(other.polytopeContext)).invoke { otherFacesToPolytopeMapping[faces] }
//        }
//    }
//
//    return true
//}
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.notEqualsTo(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    !(this equalsTo other)
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.eq(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    this equalsTo other
//context(_: NE)
//public infix fun <N, NE: Equality<N>, P1, V1: P1, P2, V2: P2> PolytopicConstruction<N, P1, V1>.neq(other: PolytopicConstruction<N, P2, V2>): Boolean =
//    !(this eq other)
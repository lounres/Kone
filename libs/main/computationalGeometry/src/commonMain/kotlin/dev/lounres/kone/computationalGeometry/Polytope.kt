/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.complex.*
import dev.lounres.kone.collections.complex.utils.*
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke
import dev.lounres.kone.logging.koneLogger
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction<N, out NE: Equality<N>, P, V: P, out PE: Equality<P>>: KoneContext {
    public val spaceDimension: UInt

    public val numberContext: NE
    public val polytopeContext: PE

    public val polytopes: KoneIterableList<KoneIterableSet<P>>
    public fun polytopesOfDimension(dim: UInt): KoneIterableSet<P>
    public operator fun get(dim: UInt): KoneIterableSet<P>
    public val P.dimension: UInt
    public val P.faces: KoneIterableList<KoneIterableSet<P>>
    public fun P.facesOfDimension(dim: UInt): KoneIterableSet<P>
    public operator fun P.get(dim: UInt): KoneIterableSet<P>
    public val P.vertices: KoneIterableSet<V>
    public val P.cofaces: KoneIterableList<KoneIterableSet<P>>
        get() =
            polytopes
                .drop(this.dimension + 1u)
                .map(context = koneIterableSetEquality(polytopeContext)) { polytopes ->
                    polytopes.filterTo(koneMutableIterableSetOf(context = polytopeContext)) { this in it.facesOfDimension(this.dimension) }
                }
    public fun P.cofacesOfDimension(dim: UInt): KoneIterableSet<P> =
        polytopes[dim].filterTo(koneMutableIterableSetOf(context = polytopeContext)) { this in it.facesOfDimension(this.dimension) }

    public val vertices: KoneIterableSet<V>
    public val V.coordinates: Point<N>
}

public interface PolytopicConstruction2<N, out NE: Equality<N>, P, V: P, out PE: Equality<P>>: PolytopicConstruction<N, NE, P, V, PE> {
    override val spaceDimension: UInt get() = 2u

    override val V.coordinates: Point2<N>
}

public interface MutablePolytopicConstruction<N, NE: Equality<N>, P, V: P, PE: Equality<P>>: PolytopicConstruction<N, NE, P, V, PE> {
    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point<N>): V
}

public interface MutablePolytopicConstruction2<N, NE: Equality<N>, P, V: P, PE: Equality<P>>: PolytopicConstruction2<N, NE, P, V, PE> {
    public fun addPolytope(vertices: KoneIterableSet<V>, faces: KoneIterableList<KoneIterableSet<P>>): P
    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    public fun addVertex(coordinates: Point2<N>): V
}

public open class AbstractPolytope internal constructor() {
    override fun toString(): String = "AbstractPolytope$${hashCode().toHexString()}"
}
public class AbstractVertex internal constructor(): AbstractPolytope() {
    override fun toString(): String = "AbstractVertex$${hashCode().toHexString()}"
}

public typealias AbstractPolytopicConstruction<N, NE> = PolytopicConstruction<N, NE, AbstractPolytope, AbstractVertex, Hashing<AbstractPolytope>>
public typealias MutableAbstractPolytopicConstruction<N, NE> = MutablePolytopicConstruction<N, NE, AbstractPolytope, AbstractVertex, Hashing<AbstractPolytope>>

public typealias AbstractPolytopicConstruction2<N, NE> = PolytopicConstruction2<N, NE, AbstractPolytope, AbstractVertex, Hashing<AbstractPolytope>>
public typealias MutableAbstractPolytopicConstruction2<N, NE> = MutablePolytopicConstruction2<N, NE, AbstractPolytope, AbstractVertex, Hashing<AbstractPolytope>>


// TODO: Replace the dummy implementation with accurate, checking one
@PublishedApi
internal class MutableAbstractPolytopicConstructionImpl<N, NE: Equality<N>>(
    override val spaceDimension: UInt,
    override val numberContext: NE,
) : MutableAbstractPolytopicConstruction<N, NE> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _polytopes = KoneIterableList(spaceDimension + 1u, context = defaultEquality()) { koneMutableIterableSetOf<AbstractPolytope>(context = defaultEquality()) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = defaultHashing(), valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneIterableSet<AbstractPolytope>>>(keyContext = defaultHashing(), valueContext = koneIterableListEquality(koneIterableSetEquality(defaultEquality())))
//    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = defaultHashing(), valueContext = defaultEquality())
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point<N>>(keyContext = defaultHashing(), valueContext = pointEquality(numberContext))

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes as KoneIterableList<KoneIterableSet<AbstractPolytope>> // TODO: Fix via making collections covariant
    override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _facesOf[this]
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = _verticesOf[this]

    override val vertices: KoneIterableSet<AbstractVertex> get() = _coordinatesOf.keys
    override val AbstractVertex.coordinates: Point<N> get() = _coordinatesOf[this]

    override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope =
        _polytopes[faces.size].firstOrNull { koneIterableListHashing(koneIterableSetHashing(polytopeContext)).invoke { it.faces eq faces } } ?: AbstractPolytope().also {
            koneLogger.info(
                items = {
                    mapOf(
                        "polytope" to it,
                        "vertices" to vertices,
                        "faces" to faces,
                    )
                }
            ) { "fun dev.lounres.kone.computationalGeometry.MutableAbstractPolytopicConstructionImpl.addPolytope: New polytope" }
            _polytopes[faces.size].add(it)
            _dimensionOf[it] = faces.size
            _facesOf[it] = faces
            _verticesOf[it] = vertices
        }
    override fun removePolytope(polytope: AbstractPolytope) {
        val dim = _dimensionOf[polytope]
        _polytopes[dim].remove(polytope)
        _dimensionOf.remove(polytope)
        _facesOf.remove(polytope)
        _verticesOf.remove(polytope)
        if (polytope is AbstractVertex) _coordinatesOf.remove(polytope)
    }
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    override fun addVertex(coordinates: Point<N>): AbstractVertex =
        _coordinatesOf.entries.firstOrNull { it.value == coordinates }?.key ?: AbstractVertex().also {
            koneLogger.info(
                items = {
                    mapOf(
                        "vertex" to it,
                        "coordinates" to coordinates,
                    )
                }
            ) { "fun dev.lounres.kone.computationalGeometry.MutableAbstractPolytopicConstructionImpl.addVertex: New vertex" }
            _polytopes[0u].add(it)
            _dimensionOf[it] = 0u
            _facesOf[it] = emptyKoneIterableList(context = defaultEquality())
            _verticesOf[it] = koneIterableSetOf(it, context = polytopeContext)
            _coordinatesOf[it] = coordinates
        }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, NE: Equality<N>> buildAbstractPolytopicConstruction(spaceDimension: UInt, numberContext: NE, @BuilderInference builder: MutableAbstractPolytopicConstruction<N, NE>.() -> Unit): AbstractPolytopicConstruction<N, NE> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return MutableAbstractPolytopicConstructionImpl<N, NE>(spaceDimension, numberContext = numberContext).also(builder)
}

@PublishedApi
internal class MutableAbstractPolytopicConstruction2Impl<N, NE: Equality<N>>(
    override val numberContext: NE,
) : MutableAbstractPolytopicConstruction2<N, NE> {
    override val spaceDimension: UInt = 2u

    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _polytopes = KoneIterableList(spaceDimension, context = defaultEquality()) { koneMutableIterableSetOf<AbstractPolytope>(context = defaultHashing()) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = defaultHashing(), valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneIterableSet<AbstractPolytope>>>(keyContext = defaultHashing(), valueContext = koneIterableListEquality(koneIterableSetEquality(defaultEquality())))
    //    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = defaultHashing(), valueContext = koneIterableSetEquality(defaultHashing()))
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point2<N>>(keyContext = defaultHashing(), valueContext = pointEquality(numberContext))

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes as KoneIterableList<KoneIterableSet<AbstractPolytope>> // TODO: Fix via making collections covariant
    override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _facesOf[this]
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = _verticesOf[this]

    override val vertices: KoneIterableSet<AbstractVertex> get() = _coordinatesOf.keys
    override val AbstractVertex.coordinates: Point2<N> get() = _coordinatesOf[this]

    override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope = AbstractPolytope().also {
        _polytopes[faces.size].add(it)
        _dimensionOf[it] = faces.size
        _facesOf[it] = faces
        _verticesOf[it] = vertices
    }
    override fun removePolytope(polytope: AbstractPolytope) {
        val dim = _dimensionOf[polytope]
        _polytopes[dim].remove(polytope)
        _dimensionOf.remove(polytope)
        _facesOf.remove(polytope)
        _verticesOf.remove(polytope)
        if (polytope is AbstractVertex) _coordinatesOf.remove(polytope)
    }
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    override fun addVertex(coordinates: Point2<N>): AbstractVertex = AbstractVertex().also {
        _polytopes[0u].add(it)
        _dimensionOf[it] = 0u
        _facesOf[it] = emptyKoneIterableList(context = koneIterableSetEquality(defaultHashing()))
        _verticesOf[it] = koneIterableSetOf(it, context = polytopeContext)
        _coordinatesOf[it] = coordinates
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, NE: Equality<N>> buildAbstractPolytopicConstruction2(numberContext: NE, @BuilderInference builder: context(Hashing<AbstractPolytope>) MutableAbstractPolytopicConstruction2<N, NE>.() -> Unit): AbstractPolytopicConstruction<N, NE> =
    MutableAbstractPolytopicConstruction2Impl<N, NE>(numberContext = numberContext).also { builder(defaultHashing(), it) }

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
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualIterableSet
import dev.lounres.kone.collections.contextual.koneContextualIterableListHashing
import dev.lounres.kone.collections.contextual.koneContextualIterableSetHashing
import dev.lounres.kone.collections.contextual.utils.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke
import dev.lounres.kone.logging.koneLogger
import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction<N, in NE: Equality<N>, P, V: P, in PE: Equality<P>>: KoneContext {
    public val spaceDimension: UInt

    context(PE)
    public val polytopes: KoneContextualIterableList<KoneContextualIterableSet<P, PE>, Equality<KoneContextualIterableSet<P, PE>>>
    context(PE)
    public fun polytopesOfDimension(dim: UInt): KoneContextualIterableSet<P, PE>
    context(PE)
    public operator fun get(dim: UInt): KoneContextualIterableSet<P, PE>
    context(PE)
    public val P.dimension: UInt
    context(PE)
    public val P.faces: KoneContextualIterableList<KoneContextualIterableSet<P, PE>, Equality<KoneContextualIterableSet<P, PE>>>
    context(PE)
    public fun P.facesOfDimension(dim: UInt): KoneContextualIterableSet<P, PE>
    context(PE)
    public operator fun P.get(dim: UInt): KoneContextualIterableSet<P, PE>
    context(PE)
    public val P.vertices: KoneContextualIterableSet<V, PE>
    context(PE)
    public val P.cofaces: KoneContextualIterableList<KoneContextualIterableSet<P, PE>, Equality<KoneContextualIterableSet<P, PE>>>
        get() =
            polytopes
                .drop(this.dimension + 1u)
                .map { polytopes -> polytopes.filterTo(koneContextualMutableIterableSetOf()) { this in it.facesOfDimension(this.dimension) } }
    context(PE)
    public fun P.cofacesOfDimension(dim: UInt): KoneContextualIterableSet<P, PE> =
        polytopes[dim].filterTo(koneContextualMutableIterableSetOf()) { this in it.facesOfDimension(this.dimension) }

    context(PE)
    public val vertices: KoneContextualIterableSet<V, PE>
    context(PE)
    public val V.coordinates: Point<N, NE>
}

public interface PolytopicConstruction2<N, in NE: Equality<N>, P, V: P, PE: Equality<P>>: PolytopicConstruction<N, NE, P, V, PE> {
    override val spaceDimension: UInt get() = 2u

    context(PE)
    override val V.coordinates: Point2<N, NE>
}

public interface MutablePolytopicConstruction<N, NE: Equality<N>, P, V: P, PE: Equality<P>>: PolytopicConstruction<N, NE, P, V, PE> {
    context(PE)
    public fun addPolytope(vertices: KoneContextualIterableSet<V, PE>, faces: KoneContextualIterableList<KoneContextualIterableSet<P, PE>, Equality<KoneContextualIterableSet<P, PE>>>): P
    context(PE)
    public fun removePolytope(polytope: P)
//    context(PE)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    context(NE, PE)
    public fun addVertex(coordinates: Point<N, NE>): V
}

public interface MutablePolytopicConstruction2<N, NE: Equality<N>, P, V: P, PE: Equality<P>>: PolytopicConstruction2<N, NE, P, V, PE> {
    context(PE)
    public fun addPolytope(vertices: KoneContextualIterableSet<V, PE>, faces: KoneContextualIterableList<KoneContextualIterableSet<P, PE>, Equality<KoneContextualIterableSet<P, PE>>>): P
    context(PE)
    public fun removePolytope(polytope: P)
//    public operator fun P.set(dim: UInt, faces: Set<P>)

    context(NE, PE)
    public fun addVertex(coordinates: Point2<N, NE>): V
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
internal class MutableAbstractPolytopicConstructionImpl<N, NE: Equality<N>>(override val spaceDimension: UInt) : MutableAbstractPolytopicConstruction<N, NE> {
    private val _polytopes = KoneContextualIterableList(spaceDimension + 1u) { koneContextualMutableIterableSetOf<AbstractPolytope>() }
    private val _dimensionOf = koneContextualMutableMapOf<AbstractPolytope, UInt>()
    private val _facesOf = koneContextualMutableMapOf<AbstractPolytope, KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>>>()
//    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneContextualMutableMapOf<AbstractPolytope, KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>>>()
    private val _coordinatesOf = koneContextualMutableMapOf<AbstractVertex, Point<N, NE>>()

    context(Hashing<AbstractPolytope>)
    override val polytopes: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>> get() = _polytopes
    context(Hashing<AbstractPolytope>)
    override fun polytopesOfDimension(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _polytopes[dim]
    context(Hashing<AbstractPolytope>)
    override fun get(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _polytopes[dim]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.faces: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>> get() = _facesOf[this]
    context(Hashing<AbstractPolytope>)
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _facesOf[this][dim]
    context(Hashing<AbstractPolytope>)
    override operator fun AbstractPolytope.get(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _facesOf[this][dim]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>> get() = _verticesOf[this]

    context(Hashing<AbstractPolytope>)
    override val vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>> get() = _coordinatesOf.keys
    context(Hashing<AbstractPolytope>)
    override val AbstractVertex.coordinates: Point<N, NE> get() = _coordinatesOf[this]

    context(Hashing<AbstractPolytope>)
    override fun addPolytope(vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>>, faces: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>>): AbstractPolytope =
        _polytopes[faces.size].firstOrNull { koneContextualIterableListHashing(koneContextualIterableSetHashing(this@Hashing)).invoke { it.faces eq faces } } ?: AbstractPolytope().also {
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
    context(Hashing<AbstractPolytope>)
    override fun removePolytope(polytope: AbstractPolytope) {
        val dim = _dimensionOf[polytope]
        _polytopes[dim].remove(polytope)
        _dimensionOf.remove(polytope)
        _facesOf.remove(polytope)
        _verticesOf.remove(polytope)
        if (polytope is AbstractVertex) _coordinatesOf.remove(polytope)
    }
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    context(NE, Hashing<AbstractPolytope>)
    override fun addVertex(coordinates: Point<N, NE>): AbstractVertex =
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
            _facesOf[it] = emptyKoneContextualIterableList()
            _verticesOf[it] = koneContextualIterableSetOf<AbstractVertex>(it)
            _coordinatesOf[it] = coordinates
        }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, NE: Equality<N>> buildAbstractPolytopicConstruction(spaceDimension: UInt, @BuilderInference builder: MutableAbstractPolytopicConstruction<N, NE>.() -> Unit): AbstractPolytopicConstruction<N, NE> =
    MutableAbstractPolytopicConstructionImpl<N, NE>(spaceDimension).also(builder)

@PublishedApi
internal class MutableAbstractPolytopicConstruction2Impl<N, NE: Equality<N>> : MutableAbstractPolytopicConstruction2<N, NE> {
    override val spaceDimension: UInt = 2u

    private val _polytopes = KoneContextualIterableList(spaceDimension) { koneContextualMutableIterableSetOf<AbstractPolytope>() }
    private val _dimensionOf = koneContextualMutableMapOf<AbstractPolytope, UInt>()
    private val _facesOf = koneContextualMutableMapOf<AbstractPolytope, KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>>>()
    //    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneContextualMutableMapOf<AbstractPolytope, KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>>>()
    private val _coordinatesOf = koneContextualMutableMapOf<AbstractVertex, Point2<N, NE>>()

    context(Hashing<AbstractPolytope>)
    override val polytopes: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>> get() = _polytopes
    context(Hashing<AbstractPolytope>)
    override fun polytopesOfDimension(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _polytopes[dim]
    context(Hashing<AbstractPolytope>)
    override fun get(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _polytopes[dim]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.faces: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>> get() = _facesOf[this]
    context(Hashing<AbstractPolytope>)
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _facesOf[this][dim]
    context(Hashing<AbstractPolytope>)
    override operator fun AbstractPolytope.get(dim: UInt): KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>> = _facesOf[this][dim]
    context(Hashing<AbstractPolytope>)
    override val AbstractPolytope.vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>> get() = _verticesOf[this]

    context(Hashing<AbstractPolytope>)
    override val vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>> get() = _coordinatesOf.keys
    context(Hashing<AbstractPolytope>)
    override val AbstractVertex.coordinates: Point2<N, NE> get() = _coordinatesOf[this]

    context(Hashing<AbstractPolytope>)
    override fun addPolytope(vertices: KoneContextualIterableSet<AbstractVertex, Hashing<AbstractPolytope>>, faces: KoneContextualIterableList<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>, Equality<KoneContextualIterableSet<AbstractPolytope, Hashing<AbstractPolytope>>>>): AbstractPolytope = AbstractPolytope().also {
        _polytopes[faces.size].add(it)
        _dimensionOf[it] = faces.size
        _facesOf[it] = faces
        _verticesOf[it] = vertices
    }
    context(Hashing<AbstractPolytope>)
    override fun removePolytope(polytope: AbstractPolytope) {
        val dim = _dimensionOf[polytope]
        _polytopes[dim].remove(polytope)
        _dimensionOf.remove(polytope)
        _facesOf.remove(polytope)
        _verticesOf.remove(polytope)
        if (polytope is AbstractVertex) _coordinatesOf.remove(polytope)
    }
//    context(Hashing<AbstractPolytope>)
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    context(NE, Hashing<AbstractPolytope>)
    override fun addVertex(coordinates: Point2<N, NE>): AbstractVertex = AbstractVertex().also {
        _polytopes[0u].add(it)
        _dimensionOf[it] = 0u
        _facesOf[it] = emptyKoneContextualIterableList()
        _verticesOf[it] = koneContextualIterableSetOf<AbstractVertex>(it)
        _coordinatesOf[it] = coordinates
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, NE: Equality<N>> buildAbstractPolytopicConstruction2(@BuilderInference builder: context(Hashing<AbstractPolytope>) MutableAbstractPolytopicConstruction2<N, NE>.() -> Unit): AbstractPolytopicConstruction<N, NE> =
    MutableAbstractPolytopicConstruction2Impl<N, NE>().also { builder(defaultHashing(), it) }
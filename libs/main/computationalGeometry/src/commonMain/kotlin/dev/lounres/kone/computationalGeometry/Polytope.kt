/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.logging.koneLogger
import kotlin.experimental.ExperimentalTypeInference


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
        get() =
            polytopes
                .drop(this.dimension + 1u)
                .map { polytopes -> polytopes.filterTo(koneMutableIterableSetOf()) { this in it.facesOfDimension(this.dimension) } }
    public fun P.superPolytopesOfDimension(dim: UInt): KoneIterableSet<P> =
        polytopes[dim].filterTo(koneMutableIterableSetOf()) { this in it.facesOfDimension(this.dimension) }

    public val vertices: KoneIterableSet<V>
    public val V.coordinates: Point<N>
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

public open class AbstractPolytope internal constructor() {
    override fun toString(): String = "AbstractPolytope$${hashCode().toHexString()}"
}
public class AbstractVertex internal constructor(): AbstractPolytope() {
    override fun toString(): String = "AbstractVertex$${hashCode().toHexString()}"
}

public typealias AbstractPolytopicConstruction<N> = PolytopicConstruction<N, AbstractPolytope, AbstractVertex>
public typealias MutableAbstractPolytopicConstruction<N> = MutablePolytopicConstruction<N, AbstractPolytope, AbstractVertex>

public typealias AbstractPolytopicConstruction2<N> = PolytopicConstruction2<N, AbstractPolytope, AbstractVertex>
public typealias MutableAbstractPolytopicConstruction2<N> = MutablePolytopicConstruction2<N, AbstractPolytope, AbstractVertex>


// Replace the dummy implementation with accurate, checking one
@PublishedApi
internal class MutableAbstractPolytopicConstructionImpl<N>(override val spaceDimension: UInt) : MutableAbstractPolytopicConstruction<N> {
    private val _polytopes = KoneIterableList(spaceDimension + 1u) { koneMutableIterableSetOf<AbstractPolytope>() }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>()
    private val _facesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneIterableSet<AbstractPolytope>>>()
//    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>()
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point<N>>()

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes
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
        _polytopes[faces.size].firstOrNull { it.faces == faces } ?: AbstractPolytope().also {
            koneLogger.info { "New polytope: $it, vertices $vertices, faces $faces\n" }
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
            koneLogger.info { "New vertex: $it, coordinates $coordinates\n" }
            _polytopes[0u].add(it)
            _dimensionOf[it] = 0u
            _facesOf[it] = emptyKoneIterableList()
            _verticesOf[it] = koneIterableSetOf(it)
            _coordinatesOf[it] = coordinates
        }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N> buildAbstractPolytopicConstruction(spaceDimension: UInt, @BuilderInference builder: MutableAbstractPolytopicConstruction<N>.() -> Unit): AbstractPolytopicConstruction<N> =
    MutableAbstractPolytopicConstructionImpl<N>(spaceDimension).apply(builder)

@PublishedApi
internal class MutableAbstractPolytopicConstruction2Impl<N> : MutableAbstractPolytopicConstruction2<N> {
    override val spaceDimension: UInt = 2u

    private val _polytopes = KoneIterableList(spaceDimension) { koneMutableIterableSetOf<AbstractPolytope>() }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>()
    private val _facesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneIterableSet<AbstractPolytope>>>()
    //    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>()
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point2<N>>()

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes
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
        _facesOf[it] = emptyKoneIterableList()
        _verticesOf[it] = koneIterableSetOf(it)
        _coordinatesOf[it] = coordinates
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N> buildAbstractPolytopicConstruction2(@BuilderInference builder: MutableAbstractPolytopicConstruction2<N>.() -> Unit): AbstractPolytopicConstruction<N> =
    MutableAbstractPolytopicConstruction2Impl<N>().apply(builder)
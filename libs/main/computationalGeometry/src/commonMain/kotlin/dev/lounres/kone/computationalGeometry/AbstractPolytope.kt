/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.logging.koneLogger
import dev.lounres.kone.util.uuid.randomUUID
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public open class AbstractPolytope internal constructor() {
    internal val id: String = randomUUID()
    override fun equals(other: Any?): Boolean = other is AbstractPolytope && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytope($id)"
}
public class AbstractVertex internal constructor(): AbstractPolytope() {
    override fun toString(): String = "AbstractVertex($id)"
}

public typealias AbstractPolytopicConstruction<N> = PolytopicConstruction<N, AbstractPolytope, AbstractVertex>
public typealias MutableAbstractPolytopicConstruction<N> = MutablePolytopicConstruction<N, AbstractPolytope, AbstractVertex>

public typealias AbstractPolytopicConstruction2<N> = PolytopicConstruction2<N, AbstractPolytope, AbstractVertex>
public typealias MutableAbstractPolytopicConstruction2<N> = MutablePolytopicConstruction2<N, AbstractPolytope, AbstractVertex>


// TODO: Replace the dummy implementation with accurate, checking one
@PublishedApi
internal class MutableAbstractPolytopicConstructionImpl<N>(
    override val spaceDimension: UInt,
    override val numberContext: Equality<N>,
) : PolytopicConstructionFrame<N, AbstractPolytope, AbstractVertex>, MutableAbstractPolytopicConstruction<N> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _polytopes = KoneIterableList(spaceDimension + 1u, elementContext = koneIterableSetEquality(polytopeContext)) { koneMutableIterableSetOf<AbstractPolytope>(elementContext = polytopeContext) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = polytopeContext, valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext))
    )
    //    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = polytopeContext, valueContext = koneIterableSetEquality(polytopeContext))
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point<N>>(keyContext = polytopeContext, valueContext = pointEquality(numberContext))

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
        _polytopes[faces.size].firstOrNull { koneIterableListHashing(koneIterableSetHashing(polytopeContext)).invoke { it.faces eq faces } } ?: AbstractPolytope().also {
            koneLogger.info(
                source = "dev.lounres.kone.computationalGeometry.MutableAbstractPolytopicConstructionImpl.addPolytope",
                items = {
                    mapOf(
                        "polytope" to it,
                        "vertices" to vertices,
                        "faces" to faces,
                    )
                }
            ) { "New polytope" }
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
                source = "dev.lounres.kone.computationalGeometry.MutableAbstractPolytopicConstructionImpl.addVertex",
                items = {
                    mapOf(
                        "vertex" to it,
                        "coordinates" to coordinates,
                    )
                }
            ) { "New vertex" }
            _polytopes[0u].add(it)
            _dimensionOf[it] = 0u
            _facesOf[it] = emptyKoneIterableList()
            _verticesOf[it] = koneIterableSetOf(it, elementContext = polytopeContext)
            _coordinatesOf[it] = coordinates
        }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N> buildAbstractPolytopicConstruction(spaceDimension: UInt, numberContext: Equality<N>, @BuilderInference builder: MutableAbstractPolytopicConstruction<N>.() -> Unit): AbstractPolytopicConstruction<N> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return MutableAbstractPolytopicConstructionImpl<N>(spaceDimension, numberContext = numberContext).also(builder)
}

@PublishedApi
internal class MutableAbstractPolytopicConstruction2Impl<N>(
    override val numberContext: Equality<N>,
) : PolytopicConstructionFrame<N, AbstractPolytope, AbstractVertex>, MutableAbstractPolytopicConstruction2<N> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    override val spaceDimension: UInt = 2u

    private val _polytopes = KoneIterableList(spaceDimension, elementContext = koneIterableSetEquality(polytopeContext)) { koneMutableIterableSetOf(elementContext = polytopeContext) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = polytopeContext, valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneIterableSet<AbstractPolytope>>>(keyContext = polytopeContext, valueContext = koneIterableListHashing(
        koneIterableSetHashing(polytopeContext)
    )
    )
    //    private val _superPolytopesOf = mutableMapOf<AbstractPolytope, List<MutableSet<AbstractPolytope>>>()
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = polytopeContext, valueContext = koneIterableSetEquality(polytopeContext))
    private val _coordinatesOf = koneMutableMapOf<AbstractVertex, Point2<N>>(keyContext = polytopeContext, valueContext = pointEquality(numberContext))

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
        _verticesOf[it] = koneIterableSetOf(it, elementContext = polytopeContext)
        _coordinatesOf[it] = coordinates
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <N, NE: Equality<N>> buildAbstractPolytopicConstruction2(numberContext: NE, @BuilderInference builder: context(Hashing<AbstractPolytope>) MutableAbstractPolytopicConstruction2<N>.() -> Unit): AbstractPolytopicConstruction<N> =
    MutableAbstractPolytopicConstruction2Impl<N>(numberContext = numberContext).also { builder(defaultHashing(), it) }
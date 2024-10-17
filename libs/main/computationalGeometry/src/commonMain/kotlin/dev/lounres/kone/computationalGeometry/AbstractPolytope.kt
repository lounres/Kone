/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalUuidApi::class)

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public open class AbstractPolytope
@DelicatePolytopicConstructionAPI constructor() {
    internal val id: Uuid = Uuid.random()
    override fun equals(other: Any?): Boolean = other is AbstractPolytope && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytope(${id.toHexString()})"
}
public class AbstractVertex
@DelicatePolytopicConstructionAPI constructor(): AbstractPolytope() {
    override fun toString(): String = "AbstractVertex(${id.toHexString()})"
}

public typealias AbstractPolytopicConstruction<N> = PolytopicConstruction<N, AbstractPolytope, AbstractVertex>
public typealias MutableAbstractPolytopicConstruction<N> = MutablePolytopicConstruction<N, AbstractPolytope, AbstractVertex>

public fun <N> MutableAbstractPolytopicConstruction(
    spaceDimension: UInt,
    numberContext: Equality<N>,
): MutableAbstractPolytopicConstruction<N> =
    MutableAbstractPolytopicConstructionImpl(
        spaceDimension = spaceDimension,
        numberContext = numberContext,
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <N> buildAbstractPolytopicConstruction(spaceDimension: UInt, numberContext: Equality<N>, @BuilderInference builder: MutableAbstractPolytopicConstruction<N>.() -> Unit): AbstractPolytopicConstruction<N> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return MutableAbstractPolytopicConstructionImpl(spaceDimension, numberContext = numberContext).also(builder)
}

@OptIn(DelicatePolytopicConstructionAPI::class)
@PublishedApi
internal class MutableAbstractPolytopicConstructionImpl<N, out NC: Equality<N>>(
    override val spaceDimension: UInt,
    override val numberContext: NC,
) : MutableAbstractPolytopicConstruction<N>, PolytopicConstructionWithContexts<N, NC, AbstractPolytope, AbstractVertex> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _polytopes = KoneIterableList(spaceDimension + 1u, elementContext = koneIterableSetEquality(polytopeContext)) { koneMutableIterableSetOf(elementContext = polytopeContext) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = polytopeContext, valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext)))
    private val _cofacesOf = koneMutableMapOf(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext)))
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = polytopeContext, valueContext = koneIterableSetEquality(polytopeContext))
    private val _positionOf = koneMutableMapOf<AbstractVertex, Point<N>>(keyContext = polytopeContext, valueContext = pointEquality(numberContext))

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes
    override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _facesOf[this]
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = _verticesOf[this]
    override val AbstractPolytope.cofaces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _cofacesOf[this]
    override fun AbstractPolytope.cofacesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _cofacesOf[this][dim - this.dimension - 1u]

    override val vertices: KoneIterableSet<AbstractVertex> get() = _positionOf.keysView
    override val AbstractVertex.position: Point<N> get() = _positionOf[this]

    // TODO: Replace the dummy implementation with accurate, checking one
    override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope {
        val newPolytopeRank = faces.size
        for (dim in 0u ..< newPolytopeRank) {
            require(faces[dim].isNotEmpty()) { "Can not construct $newPolytopeRank-dimensional polytope without $dim-dimensional faces" }
        }
        // TODO: Implement strongly connectedness requirement
        if (newPolytopeRank >= 1u) require((koneIterableSetHashing(polytopeContext)) { vertices.hash() == faces[0u].hash() && vertices eq faces[0u] }) { "" }
        for (dim in 1u ..< newPolytopeRank) {
            for (face in faces[dim]) {
                require(face in polytopes[dim]) { "Can not construct polytope with nonexistent faces: $face is not registered" }
                require(face.dimension == dim) { "Can not construct polytope with ${face.dimension}-dimensional polytope $face as $dim-dimensional face" }
                for (subDim in 0u ..< dim) {
                    for (subface in faces[subDim]) {
                        require(subface in faces[subDim]) { "Can not construct polytope that does not contain face $subface of its face $face" }
                    }
                }
            }
        }
        return AbstractPolytope().also {
            _polytopes[faces.size].add(it)
            _dimensionOf[it] = newPolytopeRank
            _facesOf[it] = faces
            _verticesOf[it] = vertices
        }
    }

//    override fun removePolytope(polytope: AbstractPolytope) {
//        val dim = _dimensionOf[polytope]
//        _polytopes[dim].remove(polytope)
//        _dimensionOf.remove(polytope)
//        _facesOf.remove(polytope)
//        _verticesOf.remove(polytope)
//        if (polytope is AbstractVertex) _positionOf.remove(polytope)
//    }
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    override fun addVertex(position: Point<N>): AbstractVertex {
        require(position.coordinates.size == spaceDimension) { "Can not create vertex at ${position.coordinates.size}-dimensional point $position in $spaceDimension-dimensional space" }
        return AbstractVertex().also {
            _polytopes[0u].add(it)
            _dimensionOf[it] = 0u
            _facesOf[it] = emptyKoneIterableList()
            _verticesOf[it] = koneIterableSetOf(it, elementContext = polytopeContext)
            _positionOf[it] = position
        }
    }
}

@Suppress("FunctionName")
@DelicatePolytopicConstructionAPI
public fun <N> UnsafeMutableAbstractPolytopicConstruction(
    spaceDimension: UInt,
    numberContext: Equality<N>,
): MutableAbstractPolytopicConstruction<N> =
    UnsafeMutableAbstractPolytopicConstructionImpl(
        spaceDimension = spaceDimension,
        numberContext = numberContext,
    )

@OptIn(ExperimentalTypeInference::class)
@DelicatePolytopicConstructionAPI
public inline fun <N> buildUnsafeAbstractPolytopicConstruction(spaceDimension: UInt, numberContext: Equality<N>, @BuilderInference builder: MutableAbstractPolytopicConstruction<N>.() -> Unit): AbstractPolytopicConstruction<N> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return UnsafeMutableAbstractPolytopicConstructionImpl(spaceDimension, numberContext = numberContext).also(builder)
}

@DelicatePolytopicConstructionAPI
@PublishedApi
internal class UnsafeMutableAbstractPolytopicConstructionImpl<N, out NC: Equality<N>>(
    override val spaceDimension: UInt,
    override val numberContext: NC,
) : MutableAbstractPolytopicConstruction<N>, PolytopicConstructionWithContexts<N, NC, AbstractPolytope, AbstractVertex> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _polytopes = KoneIterableList(spaceDimension + 1u, elementContext = koneIterableSetEquality(polytopeContext)) { koneMutableIterableSetOf(elementContext = polytopeContext) }
    private val _dimensionOf = koneMutableMapOf<AbstractPolytope, UInt>(keyContext = polytopeContext, valueContext = defaultEquality())
    private val _facesOf = koneMutableMapOf(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext)))
    private val _cofacesOf = koneMutableMapOf<AbstractPolytope, KoneIterableList<KoneMutableIterableSet<AbstractPolytope>>>(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext)))
    private val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = polytopeContext, valueContext = koneIterableSetEquality(polytopeContext))
    private val _positionOf = koneMutableMapOf<AbstractVertex, Point<N>>(keyContext = polytopeContext, valueContext = pointEquality(numberContext))

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _polytopes
    override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> = _polytopes[dim]
    override val AbstractPolytope.dimension: UInt get() = _dimensionOf[this]
    override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _facesOf[this]
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
    override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = _verticesOf[this]
    override val AbstractPolytope.cofaces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = _cofacesOf[this]
    override fun AbstractPolytope.cofacesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _cofacesOf[this][dim - this.dimension - 1u]

    override val vertices: KoneIterableSet<AbstractVertex> get() = _positionOf.keysView
    override val AbstractVertex.position: Point<N> get() = _positionOf[this]

    override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope =
        AbstractPolytope().also {
            _polytopes[faces.size].add(it)
            _dimensionOf[it] = faces.size
            _facesOf[it] = faces
            for (dim in faces.indices) for (face in faces[dim]) _cofacesOf[face][faces.size - dim - 1u].add(it)
            _cofacesOf[it] = KoneIterableList(spaceDimension - faces.size) { koneMutableIterableSetOf(elementContext = polytopeContext) }
            _verticesOf[it] = vertices
        }

//    override fun removePolytope(polytope: AbstractPolytope) {
//        val dim = _dimensionOf[polytope]
//        _polytopes[dim].remove(polytope)
//        _dimensionOf.remove(polytope)
//        _facesOf.remove(polytope)
//        _verticesOf.remove(polytope)
//        if (polytope is AbstractVertex) _positionOf.remove(polytope)
//    }
//    override operator fun AbstractPolytope.set(dim: UInt, faces: Set<AbstractPolytope>)

    override fun addVertex(position: Point<N>): AbstractVertex =
        AbstractVertex().also {
            _polytopes[0u].add(it)
            _dimensionOf[it] = 0u
            _facesOf[it] = emptyKoneIterableList()
            _cofacesOf[it] = KoneIterableList(spaceDimension) { koneMutableIterableSetOf(elementContext = polytopeContext) }
            _verticesOf[it] = koneIterableSetOf(it, elementContext = polytopeContext)
            _positionOf[it] = position
        }
}
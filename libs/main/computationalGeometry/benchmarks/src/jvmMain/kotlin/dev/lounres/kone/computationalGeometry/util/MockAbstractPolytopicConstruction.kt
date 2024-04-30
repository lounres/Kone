/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.util

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.illegalCall


@OptIn(DelicatePolytopicConstructionAPI::class)
class MockAbstractPolytopicConstruction<N, out NC: Hashing<N>>(
    override val spaceDimension: UInt,
    override val numberContext: NC,
) : MutableAbstractPolytopicConstruction<N>, PolytopicConstructionWithContexts<N, NC, AbstractPolytope, AbstractVertex> {
    override val polytopeContext: Hashing<AbstractPolytope> = defaultHashing()

    private val _positionOf = koneMutableMapOf<AbstractVertex, Point<N>>(keyContext = polytopeContext, valueContext = pointEquality(numberContext))

    override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'polytopes' value")
    override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'polytopesOfDimension' function with dim=$dim")
    override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> =  illegalCall("Convex hull algorithm tried calling 'polytopesOfDimension' function with dim=$dim")
    override val AbstractPolytope.dimension: UInt get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.dimension' value")
    override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.faces' value")
    override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> =  illegalCall("Convex hull algorithm tried calling 'AbstractPolytope.facesOfDimension' function with dim=$dim")
    override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'AbstractPolytope.get' function with dim=$dim")
    override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.vertices' value")
    override val AbstractPolytope.cofaces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.cofaces' value")
    override fun AbstractPolytope.cofacesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'AbstractPolytope.cofacesOfDimension' function with dim=$dim")

    override val vertices: KoneIterableSet<AbstractVertex> get() = illegalCall("Convex hull algorithm tried accessing 'vertices' value")
    override val AbstractVertex.position: Point<N> get() = _positionOf[this]

    override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope =
        illegalCall("Convex hull algorithm tried calling 'addPolytope' function with vertices=$vertices, faces=$faces")

    override fun addVertex(position: Point<N>): AbstractVertex {
        require(position.coordinates.size == spaceDimension) { "Can not create vertex at ${position.coordinates.size}-dimensional point $position in $spaceDimension-dimensional space" }
        return AbstractVertex().also {
            _positionOf[it] = position
        }
    }

    fun view() = View()

    inner class View : MutableAbstractPolytopicConstruction<N>, PolytopicConstructionWithContexts<N, NC, AbstractPolytope, AbstractVertex> {
        override val spaceDimension: UInt = this@MockAbstractPolytopicConstruction.spaceDimension
        override val numberContext: NC = this@MockAbstractPolytopicConstruction.numberContext
        override val polytopeContext: Hashing<AbstractPolytope> = this@MockAbstractPolytopicConstruction.polytopeContext

        private val _facesOf = koneMutableMapOf(keyContext = polytopeContext, valueContext = koneIterableListHashing(koneIterableSetHashing(polytopeContext)))
        val _verticesOf = koneMutableMapOf<AbstractPolytope, KoneIterableSet<AbstractVertex>>(keyContext = polytopeContext, valueContext = koneIterableSetHashing(polytopeContext))

        init {
            for (vertex in _positionOf.keys) _verticesOf[vertex] = koneIterableSetOf(vertex)
        }

        override val polytopes: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'polytopes' value")
        override fun polytopesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'polytopesOfDimension' function with dim=$dim")
        override fun get(dim: UInt): KoneIterableSet<AbstractPolytope> =  illegalCall("Convex hull algorithm tried calling 'polytopesOfDimension' function with dim=$dim")
        override val AbstractPolytope.dimension: UInt get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.dimension' value")
        override val AbstractPolytope.faces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.faces' value")
        override fun AbstractPolytope.facesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = _facesOf[this][dim]
        override operator fun AbstractPolytope.get(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'AbstractPolytope.get' function with dim=$dim")
        override val AbstractPolytope.vertices: KoneIterableSet<AbstractVertex> get() = _verticesOf[this]
        override val AbstractPolytope.cofaces: KoneIterableList<KoneIterableSet<AbstractPolytope>> get() = illegalCall("Convex hull algorithm tried accessing 'AbstractPolytope.cofaces' value")
        override fun AbstractPolytope.cofacesOfDimension(dim: UInt): KoneIterableSet<AbstractPolytope> = illegalCall("Convex hull algorithm tried calling 'AbstractPolytope.cofacesOfDimension' function with dim=$dim")

        override val vertices: KoneIterableSet<AbstractVertex> get() = illegalCall("Convex hull algorithm tried accessing 'vertices' value")
        override val AbstractVertex.position: Point<N> get() = _positionOf[this]

        override fun addPolytope(vertices: KoneIterableSet<AbstractVertex>, faces: KoneIterableList<KoneIterableSet<AbstractPolytope>>): AbstractPolytope =
            AbstractPolytope().also {
                _facesOf[it] = faces
                _verticesOf[it] = vertices
            }

        override fun addVertex(position: Point<N>): AbstractVertex =
            AbstractVertex().also {
                _facesOf[it] = emptyKoneIterableList()
                _positionOf[it] = position
            }
    }
}
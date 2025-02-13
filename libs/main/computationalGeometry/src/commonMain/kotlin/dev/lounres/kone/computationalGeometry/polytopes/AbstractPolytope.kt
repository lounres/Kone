/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalUuidApi::class)

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.emptyKoneList
import dev.lounres.kone.collections.set.KoneMutableNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSetNode
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableLinkedNoddedReifiedSet
import dev.lounres.kone.collections.set.koneReifiedSetOf
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.comparison.*
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractPolytopicConstructionPolytope<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction<Number>,
    override val dimension: UInt,
    override val vertices: KoneReifiedSet<AbstractPolytopicConstructionVertex<Number>>,
    override val faces: KoneList<KoneReifiedSet<AbstractPolytopicConstructionPolytope<Number>>>,
    private val correspondingVertex: AbstractPolytopicConstructionVertex<Number>?,
) : RemovablePolytopicConstructionPolytope<Number, AbstractPolytopicConstructionPolytope<Number>, AbstractPolytopicConstructionVertex<Number>> {
    internal val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstructionPolytope<Number>> =
        polytopicConstruction.registerPolytope(this)
    private val facesNodes: KoneList<KoneList<KoneMutableSetNode<AbstractPolytopicConstructionPolytope<Number>>>> =
        faces.map { it.map { it.registerCoface(this) } }
    
    private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstructionPolytope<Number>>> =
        KoneList(polytopicConstruction.spaceDimension - dimension) {
            KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
        }
    override val cofaces: KoneList<KoneReifiedSet<AbstractPolytopicConstructionPolytope<Number>>>
        get() = _cofaces
    
    internal fun registerCoface(coface: AbstractPolytopicConstructionPolytope<Number>): KoneMutableSetNode<AbstractPolytopicConstructionPolytope<Number>> =
        _cofaces[coface.dimension - dimension - 1u].addNode(coface)
    
    internal fun detach() {
        polytopicConstructionNode.remove()
        facesNodes.forEach { it.forEach { it.remove() } }
    }
    
    override fun remove() {
        cofaces.forEach { it.forEach { it.detach() } }
        this.detach()
        correspondingVertex?.detach()
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytopicConstructionPolytope:${id.toHexString()}"
}

public class AbstractPolytopicConstructionVertex<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction<Number>,
    override val position: Point<Number>,
) : RemovablePolytopicConstructionVertex<Number, AbstractPolytopicConstructionPolytope<Number>, AbstractPolytopicConstructionVertex<Number>> {
    internal val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstructionVertex<Number>> =
        polytopicConstruction.registerVertex(this)
    private val backingPolytope: AbstractPolytopicConstructionPolytope<Number> =
        AbstractPolytopicConstructionPolytope(
            polytopicConstruction = polytopicConstruction,
            dimension = 0u,
            vertices = koneReifiedSetOf(this),
            faces = emptyKoneList(),
            correspondingVertex = this,
        )
    override fun asPolytope(): AbstractPolytopicConstructionPolytope<Number> = backingPolytope
    
    internal fun detach() {
        polytopicConstructionNode.remove()
    }
    
    override fun remove() {
        backingPolytope.cofaces.forEach { it.forEach { it.detach() } }
        backingPolytope.detach()
        this.detach()
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "AbstractPolytopicConstructionVertex:${id.toHexString()}"
}

public class AbstractPolytopicConstruction<Number>(
    override val spaceDimension: UInt,
) : MutablePolytopicConstruction<Number, AbstractPolytopicConstructionPolytope<Number>, AbstractPolytopicConstructionVertex<Number>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstructionPolytope<Number>>> =
        KoneList(spaceDimension + 1u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality()) }
    override val polytopes: KoneList<KoneReifiedSet<AbstractPolytopicConstructionPolytope<Number>>> get() = _polytopes
    
    internal fun registerPolytope(polytope: AbstractPolytopicConstructionPolytope<Number>): KoneMutableSetNode<AbstractPolytopicConstructionPolytope<Number>> =
        _polytopes[polytope.dimension].addNode(polytope)
    
    private val _vertices: KoneMutableNoddedReifiedSet<AbstractPolytopicConstructionVertex<Number>> =
        KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
    override val vertices: KoneReifiedSet<AbstractPolytopicConstructionVertex<Number>> get() = _vertices
    
    internal fun registerVertex(vertex: AbstractPolytopicConstructionVertex<Number>): KoneMutableSetNode<AbstractPolytopicConstructionVertex<Number>> =
        _vertices.addNode(vertex)
    
    // TODO: Add conditional polytope validation
    override fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<AbstractPolytopicConstructionVertex<Number>>,
        faces: KoneList<KoneReifiedSet<AbstractPolytopicConstructionPolytope<Number>>>,
    ): AbstractPolytopicConstructionPolytope<Number> {
        check(dimension > 0u) { TODO("Error message is not yet provided") }
        check(faces.size == dimension) { TODO("Error message is not yet provided") }
        return AbstractPolytopicConstructionPolytope(
            polytopicConstruction = this,
            dimension = dimension,
            vertices = vertices,
            faces = faces,
            correspondingVertex = null,
        )
    }
    
    override fun addVertex(position: Point<Number>): AbstractPolytopicConstructionVertex<Number> {
        check(position.coordinates.size == spaceDimension)
        return AbstractPolytopicConstructionVertex(
            polytopicConstruction = this,
            position = position,
        )
    }
}

internal fun <Number> abstractPolytopicConstructionPolytopeSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstructionPolytope<Float>> =
    SuppliedType.Regular<AbstractPolytopicConstructionPolytope<Float>>(
        kClass = AbstractPolytopicConstructionPolytope::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

internal fun <Number> abstractPolytopicConstructionVertexSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstructionVertex<Float>> =
    SuppliedType.Regular<AbstractPolytopicConstructionVertex<Float>>(
        kClass = AbstractPolytopicConstructionVertex::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

public fun <Number> KoneContextRegistryBuilder.installAbstractPolytopicConstructionPropertiesFor(numberSuppliedType: SuppliedType<Number>) {
    val abstractPolytopicConstructionPolytopeSuppliedType = abstractPolytopicConstructionPolytopeSuppliedTypeFor(numberSuppliedType)
    val abstractPolytopicConstructionVertexSuppliedType = abstractPolytopicConstructionVertexSuppliedTypeFor(numberSuppliedType)
    
    installReificationFor(abstractPolytopicConstructionPolytopeSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstructionPolytopeSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstructionPolytopeSuppliedType) // TODO: Replace with optimised hashing
    
    installReificationFor(abstractPolytopicConstructionVertexSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstructionVertexSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstructionVertexSuppliedType) // TODO: Replace with optimised hashing
}
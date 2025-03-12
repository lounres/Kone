/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalUuidApi::class)

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.map.associateBy
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.*
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableLinkedNoddedReifiedSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.*
import dev.lounres.kone.computationalGeometry.Point4
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.reflect.KVariance
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractPolytopicConstruction4Polytope<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction4<Number>,
    override val dimension: UInt,
    override val vertices: KoneReifiedSet<AbstractPolytopicConstruction4Vertex<Number>>,
    override val faces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>>,
    private val correspondingVertex: AbstractPolytopicConstruction4Vertex<Number>?,
) : RemovablePolytopicConstruction4Polytope<Number, AbstractPolytopicConstruction4Polytope<Number>, AbstractPolytopicConstruction4Vertex<Number>> {
    private val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstruction4Polytope<Number>> =
        polytopicConstruction.registerPolytope(this)
    private val facesNodes: KoneList<KoneList<KoneMutableSetNode<AbstractPolytopicConstruction4Polytope<Number>>>> =
        faces.map { it.map { it.registerCoface(this) } }
    
    private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>> =
        KoneList(4u - dimension) {
            KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
        }
    override val cofaces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>>
        get() = _cofaces
    
    internal fun registerCoface(coface: AbstractPolytopicConstruction4Polytope<Number>): KoneMutableSetNode<AbstractPolytopicConstruction4Polytope<Number>> =
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
    override fun toString(): String = "AbstractPolytopicConstruction4Polytope:${id.toHexString()}"
}

public class AbstractPolytopicConstruction4Vertex<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction4<Number>,
    override val position: Point4<Number>,
) : RemovablePolytopicConstruction4Vertex<Number, AbstractPolytopicConstruction4Polytope<Number>, AbstractPolytopicConstruction4Vertex<Number>> {
    private val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstruction4Vertex<Number>> =
        polytopicConstruction.registerVertex(this)
    private val backingPolytope: AbstractPolytopicConstruction4Polytope<Number> =
        AbstractPolytopicConstruction4Polytope(
            polytopicConstruction = polytopicConstruction,
            dimension = 0u,
            vertices = koneReifiedSetOf(this),
            faces = emptyKoneList(),
            correspondingVertex = this,
        )
    override fun asPolytope(): AbstractPolytopicConstruction4Polytope<Number> = backingPolytope
    
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
    override fun toString(): String = "AbstractPolytopicConstruction4Vertex:${id.toHexString()}"
}

@Serializable(with = AbstractPolytopicConstruction4Serializer::class)
public class AbstractPolytopicConstruction4<Number> : MutablePolytopicConstruction4<Number, AbstractPolytopicConstruction4Polytope<Number>, AbstractPolytopicConstruction4Vertex<Number>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>> =
        KoneList(5u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality()) }
    override val polytopes: KoneList<KoneReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>> get() = _polytopes
    
    internal fun registerPolytope(polytope: AbstractPolytopicConstruction4Polytope<Number>): KoneMutableSetNode<AbstractPolytopicConstruction4Polytope<Number>> =
        _polytopes[polytope.dimension].addNode(polytope)
    
    private val _vertices: KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction4Vertex<Number>> =
        KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
    override val vertices: KoneReifiedSet<AbstractPolytopicConstruction4Vertex<Number>> get() = _vertices
    
    internal fun registerVertex(vertex: AbstractPolytopicConstruction4Vertex<Number>): KoneMutableSetNode<AbstractPolytopicConstruction4Vertex<Number>> =
        _vertices.addNode(vertex)
    
    // TODO: Add conditional polytope validation
    override fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<AbstractPolytopicConstruction4Vertex<Number>>,
        faces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction4Polytope<Number>>>,
    ): AbstractPolytopicConstruction4Polytope<Number> {
        check(dimension > 0u) { TODO("Error message is not yet provided") }
        check(faces.size == dimension) { TODO("Error message is not yet provided") }
        return AbstractPolytopicConstruction4Polytope(
            polytopicConstruction = this,
            dimension = dimension,
            vertices = vertices,
            faces = faces,
            correspondingVertex = null,
        )
    }
    
    override fun addVertex(position: Point4<Number>): AbstractPolytopicConstruction4Vertex<Number> {
        return AbstractPolytopicConstruction4Vertex(
            polytopicConstruction = this,
            position = position,
        )
    }
}

internal class AbstractPolytopicConstruction4Serializer<Number>(
    numberSerializer: KSerializer<Number>,
) : KSerializer<AbstractPolytopicConstruction4<Number>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )
    
    val pointsSerializer = KoneList.serializer(Point4.serializer(numberSerializer))
    
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstruction4Serializer") {
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }
    
    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction4<Number>) {
        val spaceDimension = 4u
        val vertices = value.vertices.toKoneList()
        val indexByVertex = vertices.indices.toKoneList().associateBy(keyEquality = absoluteEquality(), keyHashing = defaultHashing()) { vertices[it] }
        val points = vertices.map { it.position }
        val indexByPolytope = KoneList(spaceDimension + 1u) { dimension ->
            val polytopesOfDimension = value.polytopesOfDimension(dimension).toKoneList()
            polytopesOfDimension.indices.toKoneList().associateBy { polytopesOfDimension[it] }
        }
        val polytopes = value.polytopes.mapIndexed { dimension, polytopesOfDimension ->
            if (dimension == 0u) {
                vertices.indices.toKoneList().map { PolytopeDescription(vertices = koneListOf(it), faces = emptyKoneList()) }
            } else {
                polytopesOfDimension.map { polytope ->
                    PolytopeDescription(
                        vertices = polytope.vertices.map { indexByVertex[it] },
                        faces = polytope.faces.mapIndexed { subdimension, polytopes ->
                            val indexByPolytopeOfSubdimension = indexByPolytope[subdimension]
                            polytopes.map { indexByPolytopeOfSubdimension[it] }
                        }
                    )
                }
            }
        }
        
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, pointsSerializer, points)
            encodeSerializableElement(descriptor, 1, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())), polytopes)
        }
    }
    
    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction4<Number> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point4<Number>>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>
            
            if (decodeSequentially()) {
                points = decodeSerializableElement(descriptor, 0, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 1, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var pointsContender: KoneList<Point4<Number>>? = null
                var polytopeDescriptionsContender: KoneList<KoneList<PolytopeDescription>>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> pointsContender = decodeSerializableElement(descriptor, 1, pointsSerializer)
                        1 -> polytopeDescriptionsContender = decodeSerializableElement(descriptor, 4, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                points = pointsContender ?: error("Did not receive vertices")
                polytopeDescriptions = polytopeDescriptionsContender ?: error("Did not receive polytopes")
            }
            
            val polytopicConstruction = AbstractPolytopicConstruction4<Number>()
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstruction4Polytope<Number>>>(5u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. 4u)
                polytopes.add(
                    polytopeDescriptions[dimension].map { polytopeDescription ->
                        polytopicConstruction.addPolytope(
                            dimension = dimension,
                            vertices = polytopeDescription.vertices.mapTo(koneMutableReifiedSetOf(elementReification = Reification(), elementEquality = absoluteEquality(), elementHashing = defaultHashing())) { vertices[it] },
                            faces = polytopeDescription.faces.mapIndexed { subdimension, polytopesOfSubDimension ->
                                polytopesOfSubDimension.mapTo(koneMutableReifiedSetOf(elementReification = Reification(), elementEquality = absoluteEquality(), elementHashing = defaultHashing())) { polytopes[subdimension][it] }
                            },
                        )
                    }
                )
            
            polytopicConstruction
        }
}

internal fun <Number> abstractPolytopicConstruction4PolytopeSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstruction4Polytope<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstruction4Polytope<Number>>(
        kClass = AbstractPolytopicConstruction4Polytope::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

internal fun <Number> abstractPolytopicConstruction4VertexSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstruction4Vertex<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstruction4Vertex<Number>>(
        kClass = AbstractPolytopicConstruction4Vertex::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

public fun <Number> KoneContextRegistryBuilder.installAbstractPolytopicConstruction4PropertiesFor(numberSuppliedType: SuppliedType<Number>) {
    val abstractPolytopicConstruction4PolytopeSuppliedType = abstractPolytopicConstruction4PolytopeSuppliedTypeFor(numberSuppliedType)
    val abstractPolytopicConstruction4VertexSuppliedType = abstractPolytopicConstruction4VertexSuppliedTypeFor(numberSuppliedType)
    
    installReificationFor(abstractPolytopicConstruction4PolytopeSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstruction4PolytopeSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstruction4PolytopeSuppliedType) // TODO: Replace with optimised hashing
    
    installReificationFor(abstractPolytopicConstruction4VertexSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstruction4VertexSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstruction4VertexSuppliedType) // TODO: Replace with optimised hashing
}
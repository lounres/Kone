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
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.installAbsoluteEqualityFor
import dev.lounres.kone.relations.installDefaultHashingFor
import dev.lounres.kone.relations.installReificationFor
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
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
    private val id: Uuid = Uuid.random()
    
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
    private val id: Uuid = Uuid.random()
    
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

@Serializable(with = AbstractPolytopicConstructionSerializer::class)
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

internal class AbstractPolytopicConstructionSerializer<Number>(
    numberSerializer: KSerializer<Number>,
) : KSerializer<AbstractPolytopicConstruction<Number>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )
    
    val pointsSerializer = KoneList.serializer(Point.serializer(numberSerializer))
    
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstructionSerializer") {
            element<UInt>("spaceDimension")
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }
    
    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction<Number>) {
        val spaceDimension = value.spaceDimension
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
            encodeSerializableElement(descriptor, 0, UInt.serializer(), spaceDimension)
            encodeSerializableElement(descriptor, 1, pointsSerializer, points)
            encodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())), polytopes)
        }
    }
    
    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction<Number> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point<Number>>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>
            
            if (decodeSequentially()) {
                spaceDimension = decodeSerializableElement(descriptor, 0, UInt.serializer())
                points = decodeSerializableElement(descriptor, 1, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var spaceDimensionContender: UInt? = null
                var pointsContender: KoneList<Point<Number>>? = null
                var polytopeDescriptionsContender: KoneList<KoneList<PolytopeDescription>>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> spaceDimensionContender = decodeSerializableElement(descriptor, 0, UInt.serializer())
                        1 -> pointsContender = decodeSerializableElement(descriptor, 1, pointsSerializer)
                        2 -> polytopeDescriptionsContender = decodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                spaceDimension = spaceDimensionContender ?: error("Did not receive space dimension")
                points = pointsContender ?: error("Did not receive vertices")
                polytopeDescriptions = polytopeDescriptionsContender ?: error("Did not receive polytopes")
            }
            
            val polytopicConstruction = AbstractPolytopicConstruction<Number>(spaceDimension)
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstructionPolytope<Number>>>(spaceDimension + 1u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. spaceDimension)
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

internal fun <Number> abstractPolytopicConstructionPolytopeSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstructionPolytope<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstructionPolytope<Number>>(
        kClass = AbstractPolytopicConstructionPolytope::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

internal fun <Number> abstractPolytopicConstructionVertexSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstructionVertex<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstructionVertex<Number>>(
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
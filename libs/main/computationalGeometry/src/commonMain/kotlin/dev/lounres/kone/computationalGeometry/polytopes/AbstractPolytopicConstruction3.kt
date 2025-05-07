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
import dev.lounres.kone.computationalGeometry.Point3
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.installAbsoluteEqualityFor
import dev.lounres.kone.relations.installDefaultHashingFor
import dev.lounres.kone.relations.installReificationFor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
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


public class AbstractPolytopicConstruction3Polytope<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction3<Number>,
    override val dimension: UInt,
    override val vertices: KoneReifiedSet<AbstractPolytopicConstruction3Vertex<Number>>,
    override val faces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>>,
    private val correspondingVertex: AbstractPolytopicConstruction3Vertex<Number>?,
) : RemovablePolytopicConstruction3Polytope<Number, AbstractPolytopicConstruction3Polytope<Number>, AbstractPolytopicConstruction3Vertex<Number>> {
    private val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstruction3Polytope<Number>> =
        polytopicConstruction.registerPolytope(this)
    private val facesNodes: KoneList<KoneList<KoneMutableSetNode<AbstractPolytopicConstruction3Polytope<Number>>>> =
        faces.map { it.map { it.registerCoface(this) } }
    
    private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>> =
        KoneList(3u - dimension) {
            KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
        }
    override val cofaces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>>
        get() = _cofaces
    
    internal fun registerCoface(coface: AbstractPolytopicConstruction3Polytope<Number>): KoneMutableSetNode<AbstractPolytopicConstruction3Polytope<Number>> =
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
    override fun toString(): String = "AbstractPolytopicConstruction3Polytope:${id.toHexString()}"
}

public class AbstractPolytopicConstruction3Vertex<Number> internal constructor(
    polytopicConstruction: AbstractPolytopicConstruction3<Number>,
    override val position: Point3<Number>,
) : RemovablePolytopicConstruction3Vertex<Number, AbstractPolytopicConstruction3Polytope<Number>, AbstractPolytopicConstruction3Vertex<Number>> {
    private val id: Uuid = Uuid.random()
    
    private val polytopicConstructionNode: KoneMutableSetNode<AbstractPolytopicConstruction3Vertex<Number>> =
        polytopicConstruction.registerVertex(this)
    private val backingPolytope: AbstractPolytopicConstruction3Polytope<Number> =
        AbstractPolytopicConstruction3Polytope(
            polytopicConstruction = polytopicConstruction,
            dimension = 0u,
            vertices = KoneReifiedSet.of(this),
            faces = KoneList.empty(),
            correspondingVertex = this,
        )
    override fun asPolytope(): AbstractPolytopicConstruction3Polytope<Number> = backingPolytope
    
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
    override fun toString(): String = "AbstractPolytopicConstruction3Vertex:${id.toHexString()}"
}

@Serializable(with = AbstractPolytopicConstruction3Serializer::class)
public class AbstractPolytopicConstruction3<Number> : MutablePolytopicConstruction3<Number, AbstractPolytopicConstruction3Polytope<Number>, AbstractPolytopicConstruction3Vertex<Number>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>> =
        KoneList(4u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality()) }
    override val polytopes: KoneList<KoneReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>> get() = _polytopes
    
    internal fun registerPolytope(polytope: AbstractPolytopicConstruction3Polytope<Number>): KoneMutableSetNode<AbstractPolytopicConstruction3Polytope<Number>> =
        _polytopes[polytope.dimension].addNode(polytope)
    
    private val _vertices: KoneMutableNoddedReifiedSet<AbstractPolytopicConstruction3Vertex<Number>> =
        KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
    override val vertices: KoneReifiedSet<AbstractPolytopicConstruction3Vertex<Number>> get() = _vertices
    
    internal fun registerVertex(vertex: AbstractPolytopicConstruction3Vertex<Number>): KoneMutableSetNode<AbstractPolytopicConstruction3Vertex<Number>> =
        _vertices.addNode(vertex)
    
    // TODO: Add conditional polytope validation
    override fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<AbstractPolytopicConstruction3Vertex<Number>>,
        faces: KoneList<KoneReifiedSet<AbstractPolytopicConstruction3Polytope<Number>>>,
    ): AbstractPolytopicConstruction3Polytope<Number> {
        check(dimension > 0u) { TODO("Error message is not yet provided") }
        check(faces.size == dimension) { TODO("Error message is not yet provided") }
        return AbstractPolytopicConstruction3Polytope(
            polytopicConstruction = this,
            dimension = dimension,
            vertices = vertices,
            faces = faces,
            correspondingVertex = null,
        )
    }
    
    override fun addVertex(position: Point3<Number>): AbstractPolytopicConstruction3Vertex<Number> {
        return AbstractPolytopicConstruction3Vertex(
            polytopicConstruction = this,
            position = position,
        )
    }
}

internal class AbstractPolytopicConstruction3Serializer<Number>(
    numberSerializer: KSerializer<Number>,
) : KSerializer<AbstractPolytopicConstruction3<Number>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )
    
    val pointsSerializer = KoneList.serializer(Point3.serializer(numberSerializer))
    
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstruction3Serializer") {
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }
    
    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction3<Number>) {
        val spaceDimension = 3u
        val vertices = value.vertices.toKoneList()
        val indexByVertex = vertices.indices.toKoneList().associateBy(keyEquality = absoluteEquality(), keyHashing = defaultHashing()) { vertices[it] }
        val points = vertices.map { it.position }
        val indexByPolytope = KoneList(spaceDimension + 1u) { dimension ->
            val polytopesOfDimension = value.polytopesOfDimension(dimension).toKoneList()
            polytopesOfDimension.indices.toKoneList().associateBy { polytopesOfDimension[it] }
        }
        val polytopes = value.polytopes.mapIndexed { dimension, polytopesOfDimension ->
            if (dimension == 0u) {
                vertices.indices.toKoneList().map { PolytopeDescription(vertices = KoneList.of(it), faces = KoneList.empty()) }
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
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction3<Number> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point3<Number>>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>
            
            if (decodeSequentially()) {
                points = decodeSerializableElement(descriptor, 0, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 1, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var pointsContender: KoneList<Point3<Number>>? = null
                var polytopeDescriptionsContender: KoneList<KoneList<PolytopeDescription>>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> pointsContender = decodeSerializableElement(descriptor, 1, pointsSerializer)
                        1 -> polytopeDescriptionsContender = decodeSerializableElement(descriptor, 3, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                points = pointsContender ?: error("Did not receive vertices")
                polytopeDescriptions = polytopeDescriptionsContender ?: error("Did not receive polytopes")
            }
            
            val polytopicConstruction = AbstractPolytopicConstruction3<Number>()
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstruction3Polytope<Number>>>(4u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. 3u)
                polytopes.add(
                    polytopeDescriptions[dimension].map { polytopeDescription ->
                        polytopicConstruction.addPolytope(
                            dimension = dimension,
                            vertices = polytopeDescription.vertices.mapTo(KoneMutableReifiedSet.of(elementReification = Reification(), elementEquality = absoluteEquality(), elementHashing = defaultHashing())) { vertices[it] },
                            faces = polytopeDescription.faces.mapIndexed { subdimension, polytopesOfSubDimension ->
                                polytopesOfSubDimension.mapTo(KoneMutableReifiedSet.of(elementReification = Reification(), elementEquality = absoluteEquality(), elementHashing = defaultHashing())) { polytopes[subdimension][it] }
                            },
                        )
                    }
                )
            
            polytopicConstruction
        }
}

internal fun <Number> abstractPolytopicConstruction3PolytopeSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstruction3Polytope<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstruction3Polytope<Number>>(
        kClass = AbstractPolytopicConstruction3Polytope::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

internal fun <Number> abstractPolytopicConstruction3VertexSuppliedTypeFor(numberSuppliedType: SuppliedType<Number>): SuppliedType<AbstractPolytopicConstruction3Vertex<Number>> =
    SuppliedType.Regular<AbstractPolytopicConstruction3Vertex<Number>>(
        kClass = AbstractPolytopicConstruction3Vertex::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = numberSuppliedType,
            )
        ),
        isNullable = false,
    )

public fun <Number> KoneContextRegistryBuilder.installAbstractPolytopicConstruction3PropertiesFor(numberSuppliedType: SuppliedType<Number>) {
    val abstractPolytopicConstruction3PolytopeSuppliedType = abstractPolytopicConstruction3PolytopeSuppliedTypeFor(numberSuppliedType)
    val abstractPolytopicConstruction3VertexSuppliedType = abstractPolytopicConstruction3VertexSuppliedTypeFor(numberSuppliedType)
    
    installReificationFor(abstractPolytopicConstruction3PolytopeSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstruction3PolytopeSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstruction3PolytopeSuppliedType) // TODO: Replace with optimised hashing
    
    installReificationFor(abstractPolytopicConstruction3VertexSuppliedType)
    installAbsoluteEqualityFor(abstractPolytopicConstruction3VertexSuppliedType)
    installDefaultHashingFor(abstractPolytopicConstruction3VertexSuppliedType) // TODO: Replace with optimised hashing
}
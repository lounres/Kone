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
import dev.lounres.kone.computationalGeometry.Point4
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.setAbsoluteEqualityFor
import dev.lounres.kone.relations.setDefaultHashingFor
import dev.lounres.kone.relations.setReificationFor
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable(with = AbstractPolytopicConstruction4Serializer::class)
public class AbstractPolytopicConstruction4<Number, PointContent: MDList1<Number>> : MutablePolytopicConstruction4<Number, PointContent, AbstractPolytopicConstruction4.Polytope<Number, PointContent>, AbstractPolytopicConstruction4.Vertex<Number, PointContent>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<Polytope<Number, PointContent>>> =
        KoneList(5u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality()) }
    override val polytopes: KoneList<KoneReifiedSet<Polytope<Number, PointContent>>> get() = _polytopes
    
    internal fun registerPolytope(polytope: Polytope<Number, PointContent>): KoneMutableSetNode<Polytope<Number, PointContent>> =
        _polytopes[polytope.dimension].addNode(polytope)
    
    private val _vertices: KoneMutableNoddedReifiedSet<Vertex<Number, PointContent>> =
        KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
    override val vertices: KoneReifiedSet<Vertex<Number, PointContent>> get() = _vertices
    
    internal fun registerVertex(vertex: Vertex<Number, PointContent>): KoneMutableSetNode<Vertex<Number, PointContent>> =
        _vertices.addNode(vertex)
    
    // TODO: Add conditional polytope validation
    override fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<Vertex<Number, PointContent>>,
        faces: KoneList<KoneReifiedSet<Polytope<Number, PointContent>>>,
    ): Polytope<Number, PointContent> {
        check(dimension > 0u) { TODO("Error message is not yet provided") }
        check(faces.size == dimension) { TODO("Error message is not yet provided") }
        return Polytope(
            polytopicConstruction = this,
            dimension = dimension,
            vertices = vertices,
            faces = faces,
            correspondingVertex = null,
        )
    }
    
    override fun addVertex(position: Point4<Number, PointContent>): Vertex<Number, PointContent> {
        return Vertex(
            polytopicConstruction = this,
            position = position,
        )
    }
    
    public class Polytope<Number, PointContent: MDList1<Number>> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction4<Number, PointContent>,
        override val dimension: UInt,
        override val vertices: KoneReifiedSet<Vertex<Number, PointContent>>,
        override val faces: KoneList<KoneReifiedSet<Polytope<Number, PointContent>>>,
        private val correspondingVertex: Vertex<Number, PointContent>?,
    ) : ReduciblePolytopicConstruction4.Polytope<Number, PointContent, Polytope<Number, PointContent>, Vertex<Number, PointContent>> {
        private val id: Uuid = Uuid.random()
        
        private val polytopicConstructionNode: KoneMutableSetNode<Polytope<Number, PointContent>> =
            polytopicConstruction.registerPolytope(this)
        private val facesNodes: KoneList<KoneList<KoneMutableSetNode<Polytope<Number, PointContent>>>> =
            faces.map { it.map { it.registerCoface(this) } }
        
        private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<Polytope<Number, PointContent>>> =
            KoneList(4u - dimension) {
                KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality())
            }
        override val cofaces: KoneList<KoneReifiedSet<Polytope<Number, PointContent>>>
            get() = _cofaces
        
        internal fun registerCoface(coface: Polytope<Number, PointContent>): KoneMutableSetNode<Polytope<Number, PointContent>> =
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
    
    public class Vertex<Number, PointContent: MDList1<Number>> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction4<Number, PointContent>,
        override val position: Point4<Number, PointContent>,
    ) : ReduciblePolytopicConstruction4.Vertex<Number, PointContent, Polytope<Number, PointContent>, Vertex<Number, PointContent>> {
        private val id: Uuid = Uuid.random()
        
        private val polytopicConstructionNode: KoneMutableSetNode<Vertex<Number, PointContent>> =
            polytopicConstruction.registerVertex(this)
        private val backingPolytope: Polytope<Number, PointContent> =
            Polytope(
                polytopicConstruction = polytopicConstruction,
                dimension = 0u,
                vertices = KoneReifiedSet.of(this),
                faces = KoneList.empty(),
                correspondingVertex = this,
            )
        override fun asPolytope(): Polytope<Number, PointContent> = backingPolytope
        
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
}

internal class AbstractPolytopicConstruction4Serializer<Number, PointContent: MDList1<Number>>(
    numberSerializer: KSerializer<Number>,
    pointContentSerializer: KSerializer<PointContent>,
) : KSerializer<AbstractPolytopicConstruction4<Number, PointContent>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )

    val pointsSerializer = KoneList.serializer(Point4.serializer(numberSerializer, pointContentSerializer))

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstruction4Serializer", numberSerializer.descriptor) {
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }

    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction4<Number, PointContent>) {
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
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction4<Number, PointContent> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point4<Number, PointContent>>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>

            if (decodeSequentially()) {
                points = decodeSerializableElement(descriptor, 0, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 1, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var pointsContender: KoneList<Point4<Number, PointContent>>? = null
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

            val polytopicConstruction = AbstractPolytopicConstruction4<Number, PointContent>()
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstruction4.Polytope<Number, PointContent>>>(5u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. 4u)
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

internal fun <Number, PointContent: MDList1<Number>> abstractPolytopicConstruction4PolytopeSuppliedTypeFor(numberSuppliedType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction4Polytope",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberSuppliedType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = pointContentType,
            ),
        ),
        isNullable = false,
    )

internal fun <Number, PointContent: MDList1<Number>> abstractPolytopicConstruction4VertexSuppliedTypeFor(numberSuppliedType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction4Vertex",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberSuppliedType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = pointContentType,
            ),
        ),
        isNullable = false,
    )

public fun <Number, PointContent : MDList1<Number>> RegistryBuilder<KoneContextRegistry>.setAbstractPolytopicConstruction4PropertiesFor(
    numberSuppliedType: SuppliedType,
    pointContentType: SuppliedType
) {
    val abstractPolytopicConstruction4PolytopeSuppliedType = abstractPolytopicConstruction4PolytopeSuppliedTypeFor<Number, PointContent>(numberSuppliedType, pointContentType)
    val abstractPolytopicConstruction4VertexSuppliedType = abstractPolytopicConstruction4VertexSuppliedTypeFor<Number, PointContent>(numberSuppliedType, pointContentType)
    
    setReificationFor<AbstractPolytopicConstruction4.Polytope<Number, PointContent>>(abstractPolytopicConstruction4PolytopeSuppliedType)
    setAbsoluteEqualityFor<AbstractPolytopicConstruction4.Polytope<Number, PointContent>>(abstractPolytopicConstruction4PolytopeSuppliedType)
    setDefaultHashingFor<AbstractPolytopicConstruction4.Polytope<Number, PointContent>>(abstractPolytopicConstruction4PolytopeSuppliedType) // TODO: Replace with optimised hashing
    
    setReificationFor<AbstractPolytopicConstruction4.Vertex<Number, PointContent>>(abstractPolytopicConstruction4VertexSuppliedType)
    setAbsoluteEqualityFor<AbstractPolytopicConstruction4.Vertex<Number, PointContent>>(abstractPolytopicConstruction4VertexSuppliedType)
    setDefaultHashingFor<AbstractPolytopicConstruction4.Vertex<Number, PointContent>>(abstractPolytopicConstruction4VertexSuppliedType) // TODO: Replace with optimised hashing
}
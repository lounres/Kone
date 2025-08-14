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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
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
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable(with = AbstractPolytopicConstructionSerializer::class)
public class AbstractPolytopicConstruction<Number, PointContent: MDList1<Number>>(
    override val spaceDimension: UInt,
) : MutablePolytopicConstruction<Number, PointContent, AbstractPolytopicConstruction.Polytope<Number, PointContent>, AbstractPolytopicConstruction.Vertex<Number, PointContent>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<Polytope<Number, PointContent>>> =
        KoneList(spaceDimension + 1u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification(), absoluteEquality()) }
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
    
    override fun addVertex(position: Point<Number, PointContent>): Vertex<Number, PointContent> {
        check(position.coordinates.contentSize == spaceDimension)
        return Vertex(
            polytopicConstruction = this,
            position = position,
        )
    }
    
    public class Polytope<Number, PointContent: MDList1<Number>> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction<Number, PointContent>,
        override val dimension: UInt,
        override val vertices: KoneReifiedSet<Vertex<Number, PointContent>>,
        override val faces: KoneList<KoneReifiedSet<Polytope<Number, PointContent>>>,
        private val correspondingVertex: Vertex<Number, PointContent>?,
    ) : ReduciblePolytopicConstruction.Polytope<Number, PointContent, Polytope<Number, PointContent>, Vertex<Number, PointContent>> {
        private val id: Uuid = Uuid.random()
        
        private val polytopicConstructionNode: KoneMutableSetNode<Polytope<Number, PointContent>> =
            polytopicConstruction.registerPolytope(this)
        private val facesNodes: KoneList<KoneList<KoneMutableSetNode<Polytope<Number, PointContent>>>> =
            faces.map { it.map { it.registerCoface(this) } }
        
        private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<Polytope<Number, PointContent>>> =
            KoneList(polytopicConstruction.spaceDimension - dimension) {
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
        override fun toString(): String = "AbstractPolytopicConstructionPolytope:${id.toHexString()}"
    }
    
    public class Vertex<Number, PointContent: MDList1<Number>> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction<Number, PointContent>,
        override val position: Point<Number, PointContent>,
    ) : ReduciblePolytopicConstruction.Vertex<Number, PointContent, Polytope<Number, PointContent>, Vertex<Number, PointContent>> {
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
        override fun toString(): String = "AbstractPolytopicConstructionVertex:${id.toHexString()}"
    }
}

internal class AbstractPolytopicConstructionSerializer<Number, PointContent: MDList1<Number>>(
    numberSerializer: KSerializer<Number>,
    pointContentSerializer: KSerializer<PointContent>,
) : KSerializer<AbstractPolytopicConstruction<Number, PointContent>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )

    val pointsSerializer = KoneList.serializer(Point.serializer(numberSerializer, pointContentSerializer))

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstructionSerializer", numberSerializer.descriptor) {
            element<UInt>("spaceDimension")
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }

    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction<Number, PointContent>) {
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
            encodeSerializableElement(descriptor, 0, UInt.serializer(), spaceDimension)
            encodeSerializableElement(descriptor, 1, pointsSerializer, points)
            encodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())), polytopes)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction<Number, PointContent> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point<Number, PointContent>>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>

            if (decodeSequentially()) {
                spaceDimension = decodeSerializableElement(descriptor, 0, UInt.serializer())
                points = decodeSerializableElement(descriptor, 1, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var spaceDimensionContender: UInt? = null
                var pointsContender: KoneList<Point<Number, PointContent>>? = null
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

            val polytopicConstruction = AbstractPolytopicConstruction<Number, PointContent>(spaceDimension)
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstruction.Polytope<Number, PointContent>>>(spaceDimension + 1u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. spaceDimension)
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

internal fun <Number, PointContent: MDList1<Number>> abstractPolytopicConstructionPolytopeSuppliedTypeFor(numberType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction.Polytope",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = pointContentType,
            ),
        ),
        isNullable = false,
    )

internal fun <Number, PointContent: MDList1<Number>> abstractPolytopicConstructionVertexSuppliedTypeFor(numberSuppliedType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction.Vertex",
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

public fun <Number, PointContent: MDList1<Number>> RegistryBuilder<KoneContextRegistry>.setAbstractPolytopicConstructionPropertiesFor(
    numberType: SuppliedType,
    pointContentType: SuppliedType,
) {
    val abstractPolytopicConstructionPolytopeSuppliedType = abstractPolytopicConstructionPolytopeSuppliedTypeFor<Number, PointContent>(numberType, pointContentType)
    val abstractPolytopicConstructionVertexSuppliedType = abstractPolytopicConstructionVertexSuppliedTypeFor<Number, PointContent>(numberType, pointContentType)
    
    setReificationFor<AbstractPolytopicConstruction.Polytope<Number, PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType)
    setAbsoluteEqualityFor<AbstractPolytopicConstruction.Polytope<Number, PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType)
    setDefaultHashingFor<AbstractPolytopicConstruction.Polytope<Number, PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType) // TODO: Replace with optimised hashing
    
    setReificationFor<AbstractPolytopicConstruction.Vertex<Number, PointContent>>(abstractPolytopicConstructionVertexSuppliedType)
    setAbsoluteEqualityFor<AbstractPolytopicConstruction.Vertex<Number, PointContent>>(abstractPolytopicConstructionVertexSuppliedType)
    setDefaultHashingFor<AbstractPolytopicConstruction.Vertex<Number, PointContent>>(abstractPolytopicConstructionVertexSuppliedType) // TODO: Replace with optimised hashing
}
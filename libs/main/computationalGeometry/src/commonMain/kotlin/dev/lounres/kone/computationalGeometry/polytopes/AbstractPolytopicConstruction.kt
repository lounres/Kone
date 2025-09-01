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
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
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
public class AbstractPolytopicConstruction<Point>(
    override val spaceDimension: UInt,
) : MutablePolytopicConstruction<Point, AbstractPolytopicConstruction.Polytope<Point>, AbstractPolytopicConstruction.Vertex<Point>> {
    private val _polytopes: KoneList<KoneMutableNoddedReifiedSet<Polytope<Point>>> =
        KoneList(spaceDimension + 1u) { KoneListBackedMutableLinkedNoddedReifiedSet(Reification.defaultFor(), Equality.absoluteFor()) }
    override val polytopes: KoneList<KoneReifiedSet<Polytope<Point>>> get() = _polytopes
    
    internal fun registerPolytope(polytope: Polytope<Point>): KoneMutableSetNode<Polytope<Point>> =
        _polytopes[polytope.dimension].addNode(polytope)
    
    private val _vertices: KoneMutableNoddedReifiedSet<Vertex<Point>> =
        KoneListBackedMutableLinkedNoddedReifiedSet(Reification.defaultFor(), Equality.absoluteFor())
    override val vertices: KoneReifiedSet<Vertex<Point>> get() = _vertices
    
    internal fun registerVertex(vertex: Vertex<Point>): KoneMutableSetNode<Vertex<Point>> =
        _vertices.addNode(vertex)
    
    // TODO: Add conditional polytope validation
    @IgnorableReturnValue
    override fun addPolytope(
        dimension: UInt,
        vertices: KoneReifiedSet<Vertex<Point>>,
        faces: KoneList<KoneReifiedSet<Polytope<Point>>>,
    ): Polytope<Point> {
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
    
    @IgnorableReturnValue
    override fun addVertex(position: Point): Vertex<Point> {
        return Vertex(
            polytopicConstruction = this,
            position = position,
        )
    }
    
    public companion object;
    
    public class Polytope<Point> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction<Point>,
        override val dimension: UInt,
        override val vertices: KoneReifiedSet<Vertex<Point>>,
        override val faces: KoneList<KoneReifiedSet<Polytope<Point>>>,
        private val correspondingVertex: Vertex<Point>?,
    ) : ReduciblePolytopicConstruction.Polytope<Point, Polytope<Point>, Vertex<Point>> {
        private val id: Uuid = Uuid.random()
        
        private val polytopicConstructionNode: KoneMutableSetNode<Polytope<Point>> =
            polytopicConstruction.registerPolytope(this)
        private val facesNodes: KoneList<KoneList<KoneMutableSetNode<Polytope<Point>>>> =
            faces.map { it.map { it.registerCoface(this) } }
        
        private val _cofaces: KoneList<KoneMutableNoddedReifiedSet<Polytope<Point>>> =
            KoneList(polytopicConstruction.spaceDimension - dimension) {
                KoneListBackedMutableLinkedNoddedReifiedSet(Reification.defaultFor(), Equality.absoluteFor())
            }
        override val cofaces: KoneList<KoneReifiedSet<Polytope<Point>>>
            get() = _cofaces
        
        internal fun registerCoface(coface: Polytope<Point>): KoneMutableSetNode<Polytope<Point>> =
            _cofaces[coface.dimension - dimension - 1u].addNode(coface)
        
        internal fun detach() {
            polytopicConstructionNode.remove()
            facesNodes.forEach { it.forEach { it.remove() } }
        }
        
        override fun remove() {
            cofaces.flatten().forEach { it.detach() }
            this.detach()
            correspondingVertex?.detach()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String = "AbstractPolytopicConstructionPolytope:${id.toHexString()}"
        
        public companion object;
    }
    
    public class Vertex<Point> internal constructor(
        polytopicConstruction: AbstractPolytopicConstruction<Point>,
        override val position: Point,
    ) : ReduciblePolytopicConstruction.Vertex<Point, Polytope<Point>, Vertex<Point>> {
        private val id: Uuid = Uuid.random()
        
        private val polytopicConstructionNode: KoneMutableSetNode<Vertex<Point>> =
            polytopicConstruction.registerVertex(this)
        private val backingPolytope: Polytope<Point> =
            Polytope(
                polytopicConstruction = polytopicConstruction,
                dimension = 0u,
                vertices = KoneReifiedSet.of(this),
                faces = KoneList.empty(),
                correspondingVertex = this,
            )
        override fun asPolytope(): Polytope<Point> = backingPolytope
        
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
        
        public companion object;
    }
}

internal class AbstractPolytopicConstructionSerializer<Point>(
    pointSerializer: KSerializer<Point>,
) : KSerializer<AbstractPolytopicConstruction<Point>> {
    @Serializable
    private data class PolytopeDescription(
        val vertices: KoneList<UInt>,
        val faces: KoneList<KoneList<UInt>>,
    )

    val pointsSerializer = KoneList.serializer(pointSerializer)

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AbstractPolytopicConstructionSerializer", pointsSerializer.descriptor) {
            element<UInt>("spaceDimension")
            element("vertices", pointsSerializer.descriptor)
            element<KoneList<KoneList<PolytopeDescription>>>("polytopes")
        }

    override fun serialize(encoder: Encoder, value: AbstractPolytopicConstruction<Point>) {
        val spaceDimension = value.spaceDimension
        val vertices = value.vertices.toKoneList()
        val indexByVertex = vertices.indices.toKoneList().associateBy(keyEquality = Equality.absoluteFor(), keyHashing = Hashing.defaultFor()) { vertices[it] }
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
    override fun deserialize(decoder: Decoder): AbstractPolytopicConstruction<Point> =
        decoder.decodeStructure(descriptor) {
            val spaceDimension: UInt
            val points: KoneList<Point>
            val polytopeDescriptions: KoneList<KoneList<PolytopeDescription>>

            if (decodeSequentially()) {
                spaceDimension = decodeSerializableElement(descriptor, 0, UInt.serializer())
                points = decodeSerializableElement(descriptor, 1, pointsSerializer)
                polytopeDescriptions = decodeSerializableElement(descriptor, 2, KoneList.serializer(KoneList.serializer(PolytopeDescription.serializer())))
            } else {
                var spaceDimensionContender: UInt? = null
                var pointsContender: KoneList<Point>? = null
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

            val polytopicConstruction = AbstractPolytopicConstruction<Point>(spaceDimension)
            val vertices = points.map { polytopicConstruction.addVertex(it) }
            val polytopes = KoneArrayFixedCapacityList<KoneList<AbstractPolytopicConstruction.Polytope<Point>>>(spaceDimension + 1u)
            polytopes.add(polytopeDescriptions[0u].map { vertices[it.vertices.single()].asPolytope() })
            for (dimension in 1u .. spaceDimension)
                polytopes.add(
                    polytopeDescriptions[dimension].map { polytopeDescription ->
                        polytopicConstruction.addPolytope(
                            dimension = dimension,
                            vertices = polytopeDescription.vertices.mapTo(KoneMutableReifiedSet.of(elementReification = Reification.defaultFor(), elementEquality = Equality.absoluteFor(), elementHashing = Hashing.defaultFor())) { vertices[it] },
                            faces = polytopeDescription.faces.mapIndexed { subdimension, polytopesOfSubDimension ->
                                polytopesOfSubDimension.mapTo(KoneMutableReifiedSet.of(elementReification = Reification.defaultFor(), elementEquality = Equality.absoluteFor(), elementHashing = Hashing.defaultFor())) { polytopes[subdimension][it] }
                            },
                        )
                    }
                )

            polytopicConstruction
        }
}

//internal fun <PointContent: MDList1<Number>> abstractPolytopicConstructionPolytopeSuppliedTypeFor(numberType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
//    @OptIn(DelicateSuppliedTypeConstructor::class)
//    SuppliedType.Regular(
//        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction.Polytope",
//        typeArguments = listOf(
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = numberType,
//            ),
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = pointContentType,
//            ),
//        ),
//        isNullable = false,
//    )
//
//internal fun <PointContent: MDList1<Number>> abstractPolytopicConstructionVertexSuppliedTypeFor(numberSuppliedType: SuppliedType, pointContentType: SuppliedType): SuppliedType =
//    @OptIn(DelicateSuppliedTypeConstructor::class)
//    SuppliedType.Regular(
//        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction.Vertex",
//        typeArguments = listOf(
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = numberSuppliedType,
//            ),
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = pointContentType,
//            ),
//        ),
//        isNullable = false,
//    )
//
//public fun <PointContent: MDList1<Number>> RegistryBuilder<KoneContextRegistry>.setAbstractPolytopicConstructionPropertiesFor(
//    numberType: SuppliedType,
//    pointContentType: SuppliedType,
//) {
//    val abstractPolytopicConstructionPolytopeSuppliedType = abstractPolytopicConstructionPolytopeSuppliedTypeFor<PointContent>(numberType, pointContentType)
//    val abstractPolytopicConstructionVertexSuppliedType = abstractPolytopicConstructionVertexSuppliedTypeFor<PointContent>(numberType, pointContentType)
//
//    setReificationFor<AbstractPolytopicConstruction.Polytope<PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType)
//    setAbsoluteEqualityFor<AbstractPolytopicConstruction.Polytope<PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType)
//    setDefaultFor<AbstractPolytopicConstruction.Polytope<PointContent>>(abstractPolytopicConstructionPolytopeSuppliedType) // TODO: Replace with optimised hashing
//
//    setReificationFor<AbstractPolytopicConstruction.Vertex<PointContent>>(abstractPolytopicConstructionVertexSuppliedType)
//    setAbsoluteEqualityFor<AbstractPolytopicConstruction.Vertex<PointContent>>(abstractPolytopicConstructionVertexSuppliedType)
//    setDefaultFor<AbstractPolytopicConstruction.Vertex<PointContent>>(abstractPolytopicConstructionVertexSuppliedType) // TODO: Replace with optimised hashing
//}
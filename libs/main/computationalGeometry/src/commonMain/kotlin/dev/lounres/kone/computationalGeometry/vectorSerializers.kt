/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection


internal class VectorSerializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Vector<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector", columnVectorSerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: Vector<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }
    
    override fun deserialize(decoder: Decoder): Vector<N> =
        Vector(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Vector2Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Vector2<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector2", columnVectorSerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: Vector2<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }
    
    override fun deserialize(decoder: Decoder): Vector2<N> =
        Vector2(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Vector3Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Vector3<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector3", columnVectorSerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: Vector3<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }
    
    override fun deserialize(decoder: Decoder): Vector3<N> =
        Vector3(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Vector4Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Vector4<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector4", columnVectorSerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: Vector4<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }
    
    override fun deserialize(decoder: Decoder): Vector4<N> =
        Vector4(decoder.decodeSerializableValue(columnVectorSerializer))
}
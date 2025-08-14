/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class VectorSerializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Vector<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Vector<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Vector<N, Content> =
        Vector(decoder.decodeSerializableValue(contentSerializer))
}

internal class Vector2Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Vector2<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector2", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Vector2<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Vector2<N, Content> =
        Vector2(decoder.decodeSerializableValue(contentSerializer))
}

internal class Vector3Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Vector3<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector3", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Vector3<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Vector3<N, Content> =
        Vector3(decoder.decodeSerializableValue(contentSerializer))
}

internal class Vector4Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Vector4<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Vector4", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Vector4<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Vector4<N, Content> =
        Vector4(decoder.decodeSerializableValue(contentSerializer))
}
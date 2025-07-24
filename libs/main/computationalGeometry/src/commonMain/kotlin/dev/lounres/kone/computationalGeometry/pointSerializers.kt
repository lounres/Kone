/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class PointSerializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Point<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point", columnVectorSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point<N> =
        Point(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Point2Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Point2<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point2", columnVectorSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point2<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point2<N> =
        Point2(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Point3Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Point3<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point3", columnVectorSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point3<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point3<N> =
        Point3(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Point4Serializer<N>(
    numberSerializer: KSerializer<N>,
) : KSerializer<Point4<N>> {
    val columnVectorSerializer = ColumnVector.serializer(numberSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point4", columnVectorSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point4<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point4<N> =
        Point4(decoder.decodeSerializableValue(columnVectorSerializer))
}
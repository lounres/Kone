/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.multidimensionalCollections.MDList1
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class PointSerializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Point<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point<N, Content> =
        Point(decoder.decodeSerializableValue(contentSerializer))
}

internal class Point2Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Point2<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point2", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point2<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point2<N, Content> =
        Point2(decoder.decodeSerializableValue(contentSerializer))
}

internal class Point3Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Point3<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point3", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point3<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point3<N, Content> =
        Point3(decoder.decodeSerializableValue(contentSerializer))
}

internal class Point4Serializer<N, Content: MDList1<N>>(
    numberSerializer: KSerializer<N>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Point4<N, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point4", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point4<N, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coordinates)
    }

    override fun deserialize(decoder: Decoder): Point4<N, Content> =
        Point4(decoder.decodeSerializableValue(contentSerializer))
}
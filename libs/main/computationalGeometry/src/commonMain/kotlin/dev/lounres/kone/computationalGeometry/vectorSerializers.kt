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
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.computationalGeometry.Point", columnVectorSerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: Vector<N>) {
        encoder.encodeSerializableValue(columnVectorSerializer, value.coordinates)
    }
    
    override fun deserialize(decoder: Decoder): Vector<N> =
        Vector(decoder.decodeSerializableValue(columnVectorSerializer))
}

internal class Vector2Serializer<N>(
    private val numberSerializer: KSerializer<N>,
) : KSerializer<Vector2<N>> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor(
            serialName = "dev.lounres.kone.computationalGeometry.Point2",
            original = ListSerializer(numberSerializer).descriptor
        )
    
    override fun serialize(encoder: Encoder, value: Vector2<N>) {
        encoder.encodeCollection(descriptor, 2) {
            encodeSerializableElement(descriptor, 0, numberSerializer, value.x)
            encodeSerializableElement(descriptor, 1, numberSerializer, value.y)
        }
    }
    
    override fun deserialize(decoder: Decoder): Vector2<N> =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                check(size == 2) { "Cannot deserialize Point2: expected 2 element but got $size" }
                Vector2(
                    decodeSerializableElement(descriptor, 0, numberSerializer),
                    decodeSerializableElement(descriptor, 1, numberSerializer),
                )
            } else {
                var x: Maybe<N> = None
                var y: Maybe<N> = None
                while (true) {
                    val index = decodeElementIndex(descriptor)
                    when (index) {
                        CompositeDecoder.DECODE_DONE -> break
                        0 -> {
                            if (x != None) error("Cannot deserialize Point2: got several elements with index 0")
                            x = Some(decodeSerializableElement(descriptor, index, numberSerializer))
                        }
                        1 -> {
                            if (y != None) error("Cannot deserialize Point2: got several elements with index 0")
                            y = Some(decodeSerializableElement(descriptor, index, numberSerializer))
                        }
                        else -> error("Cannot deserialize Point2: got index $index out of range [0; 2)")
                    }
                }
                if (x == None) error("Cannot deserialize Point2: did not receive element with index 0")
                if (y == None) error("Cannot deserialize Point2: did not receive element with index 1")
                Vector2((x as Some<N>).value, (y as Some<N>).value)
            }
        }
}
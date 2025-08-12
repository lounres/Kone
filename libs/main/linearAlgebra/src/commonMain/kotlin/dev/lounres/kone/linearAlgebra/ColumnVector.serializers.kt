/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class ColumnVectorSerializer<E, Content: MDList1<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<ColumnVector<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.ColumnVector", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: ColumnVector<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): ColumnVector<E, Content> =
        ColumnVector(decoder.decodeSerializableValue(contentSerializer))
}

internal class SettableColumnVectorSerializer<E, Content: SettableMDList1<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<SettableColumnVector<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableColumnVector", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableColumnVector<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableColumnVector<E, Content> =
        SettableColumnVector(decoder.decodeSerializableValue(contentSerializer))
}
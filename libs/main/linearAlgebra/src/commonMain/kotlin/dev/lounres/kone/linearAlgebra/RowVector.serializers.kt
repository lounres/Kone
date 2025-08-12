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


internal class RowVectorSerializer<E, Content: MDList1<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<RowVector<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.RowVector", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: RowVector<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): RowVector<E, Content> =
        RowVector(decoder.decodeSerializableValue(contentSerializer))
}

internal class SettableRowVectorSerializer<E, Content: SettableMDList1<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<SettableRowVector<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableRowVector", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableRowVector<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableRowVector<E, Content> =
        SettableRowVector(decoder.decodeSerializableValue(contentSerializer))
}
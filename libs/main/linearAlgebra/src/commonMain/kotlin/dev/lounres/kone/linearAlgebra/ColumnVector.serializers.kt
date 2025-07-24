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


internal class ColumnVectorSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<ColumnVector<E>> {
    private val mdList1Serializer = MDList1.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.ColumnVector", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: ColumnVector<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): ColumnVector<E> =
        ColumnVector(decoder.decodeSerializableValue(mdList1Serializer))
}

internal class SettableColumnVectorSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableColumnVector<E>> {
    private val mdList1Serializer = SettableMDList1.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableColumnVector", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableColumnVector<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableColumnVector<E> =
        SettableColumnVector(decoder.decodeSerializableValue(mdList1Serializer))
}
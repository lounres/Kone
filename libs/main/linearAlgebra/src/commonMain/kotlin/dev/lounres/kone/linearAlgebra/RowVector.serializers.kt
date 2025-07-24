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


internal class RowVectorSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<RowVector<E>> {
    private val mdList1Serializer = MDList1.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.RowVector", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: RowVector<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): RowVector<E> =
        RowVector(decoder.decodeSerializableValue(mdList1Serializer))
}

internal class SettableRowVectorSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableRowVector<E>> {
    private val mdList1Serializer = SettableMDList1.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableRowVector", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableRowVector<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableRowVector<E> =
        SettableRowVector(decoder.decodeSerializableValue(mdList1Serializer))
}
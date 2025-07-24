/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class MatrixSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<Matrix<E>> {
    private val mdList1Serializer = MDList2.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.Matrix", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: Matrix<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): Matrix<E> =
        Matrix(decoder.decodeSerializableValue(mdList1Serializer))
}

internal class SettableMatrixSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableMatrix<E>> {
    private val mdList1Serializer = SettableMDList2.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableMatrix", mdList1Serializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableMatrix<E>) {
        encoder.encodeSerializableValue(mdList1Serializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableMatrix<E> =
        SettableMatrix(decoder.decodeSerializableValue(mdList1Serializer))
}
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


internal class MatrixSerializer<E, Content: MDList2<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<Matrix<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.Matrix", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Matrix<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): Matrix<E, Content> =
        Matrix(decoder.decodeSerializableValue(contentSerializer))
}

internal class SettableMatrixSerializer<E, Content: SettableMDList2<E>>(
    elementSerializer: KSerializer<E>,
    private val contentSerializer: KSerializer<Content>,
) : KSerializer<SettableMatrix<E, Content>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.linearAlgebra.SettableMatrix", contentSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableMatrix<E, Content>) {
        encoder.encodeSerializableValue(contentSerializer, value.coefficients)
    }

    override fun deserialize(decoder: Decoder): SettableMatrix<E, Content> =
        SettableMatrix(decoder.decodeSerializableValue(contentSerializer))
}
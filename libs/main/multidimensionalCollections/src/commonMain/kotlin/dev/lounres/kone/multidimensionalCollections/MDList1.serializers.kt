/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal class MDList1Serializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<MDList1<E>> {
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val settableListSerializer = KoneArraySettableList.serializer(elementSerializer)
    private val settableListSerializer = KoneArraySettableNoddedList.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList1Serializer", settableListSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: MDList1<E>) {
        // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//        val content = KoneArraySettableList(value.size) { value[it] }
        val content = KoneArraySettableNoddedList(value.size) { value[it] }
        encoder.encodeSerializableValue(settableListSerializer, content)
    }

    override fun deserialize(decoder: Decoder): MDList1<E> {
        val content = decoder.decodeSerializableValue(settableListSerializer)
        return ArrayMDList1(content.size) { content[it] }
    }
}

internal class SettableMDList1Serializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableMDList1<E>> {
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val settableListSerializer = KoneArraySettableList.serializer(elementSerializer)
    private val settableListSerializer = KoneArraySettableNoddedList.serializer(elementSerializer)

    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList1Serializer", settableListSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: SettableMDList1<E>) {
        // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//        val content = KoneArraySettableList(value.size) { value[it] }
        val content = KoneArraySettableNoddedList(value.size) { value[it] }
        encoder.encodeSerializableValue(settableListSerializer, content)
    }

    override fun deserialize(decoder: Decoder): SettableMDList1<E> {
        val content = decoder.decodeSerializableValue(settableListSerializer)
        return ArrayMDList1(content.size) { content[it] }
    }
}
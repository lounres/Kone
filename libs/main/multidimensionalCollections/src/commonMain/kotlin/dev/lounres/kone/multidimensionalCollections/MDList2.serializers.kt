/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.serializers.serializer
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
import dev.lounres.kone.scope
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*


internal class MDList2Serializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<MDList2<E>> {
    private val shapeSerializer = KoneUIntArray.serializer()
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableList<E>> =
//        KoneArraySettableList.serializer(elementSerializer)
    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableNoddedList<E>> =
        KoneArraySettableNoddedList.serializer(elementSerializer)
    private val contentDeserializationStrategy: DeserializationStrategy<KoneMutableArray<Any?>> =
        @Suppress("UNCHECKED_CAST") KoneMutableArray.serializer<Any, Any?>(elementSerializer as KSerializer<Any?>)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList", elementSerializer.descriptor) {
        element("shape", shapeSerializer.descriptor)
        element("content", contentDeserializationStrategy.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MDList2<E>) {
        val content = scope {
            val indicesIterator = MDShapeStrides(value.shape).iterator()
            // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//            KoneArraySettableList(value.size) { value[indicesIterator.next()] }
            KoneArraySettableNoddedList(value.size) { value[indicesIterator.next()] }
        }
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = 0,
                serializer = shapeSerializer,
                value = value.shape,
            )
            encodeSerializableElement(
                descriptor = descriptor,
                index = 1,
                serializer = contentSerializationStrategy,
                value = content,
            )
        }
    }

    override fun deserialize(decoder: Decoder): MDList2<E> =
        decoder.decodeStructure(descriptor) {
            val shape: MDShape
            val content: KoneMutableArray<Any?>
            if (decodeSequentially()) { // sequential decoding protocol
                shape = decodeSerializableElement(descriptor, 0, shapeSerializer)
                content = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
            } else {
                var shapeBuilder: MDShape? = null
                var contentBuilder: KoneMutableArray<Any?>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> shapeBuilder = decodeSerializableElement(descriptor, 0, shapeSerializer)
                        1 -> contentBuilder = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Cannot deserialize MDList2: got index $index out of range [0; 2)")
                    }
                }
                shape = shapeBuilder ?: error("Cannot deserialize MDList2: did not receive element with index 0")
                content = contentBuilder ?: error("Cannot deserialize MDList2: did not receive element with index 1")
            }
            check(shape.size == 2u) { "Cannot deserialize MDList2: received shape and content size mismatch" }
            check(content.size == shape[0u] * shape[1u]) { "Cannot deserialize MDList2: received shape and content size mismatch" }
            ArrayMDList2(rowNumber = shape[0u], columnNumber = shape[1u], data = content)
        }
}

internal class SettableMDList2Serializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableMDList2<E>> {
    private val shapeSerializer = KoneUIntArray.serializer()
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableList<E>> =
//        KoneArraySettableList.serializer(elementSerializer)
    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableNoddedList<E>> =
        KoneArraySettableNoddedList.serializer(elementSerializer)
    private val contentDeserializationStrategy: DeserializationStrategy<KoneMutableArray<Any?>> =
        @Suppress("UNCHECKED_CAST") KoneMutableArray.serializer<Any, Any?>(elementSerializer as KSerializer<Any?>)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList", elementSerializer.descriptor) {
        element("shape", shapeSerializer.descriptor)
        element("content", contentDeserializationStrategy.descriptor)
    }

    override fun serialize(encoder: Encoder, value: SettableMDList2<E>) {
        val content = scope {
            val indicesIterator = MDShapeStrides(value.shape).iterator()
            // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//            KoneArraySettableList(value.size) { value[indicesIterator.next()] }
            KoneArraySettableNoddedList(value.size) { value[indicesIterator.next()] }
        }
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = 0,
                serializer = shapeSerializer,
                value = value.shape,
            )
            encodeSerializableElement(
                descriptor = descriptor,
                index = 1,
                serializer = contentSerializationStrategy,
                value = content,
            )
        }
    }

    override fun deserialize(decoder: Decoder): SettableMDList2<E> =
        decoder.decodeStructure(descriptor) {
            val shape: MDShape
            val content: KoneMutableArray<Any?>
            if (decodeSequentially()) { // sequential decoding protocol
                shape = decodeSerializableElement(descriptor, 0, shapeSerializer)
                content = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
            } else {
                var shapeBuilder: MDShape? = null
                var contentBuilder: KoneMutableArray<Any?>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> shapeBuilder = decodeSerializableElement(descriptor, 0, shapeSerializer)
                        1 -> contentBuilder = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Cannot deserialize MDList2: got index $index out of range [0; 2)")
                    }
                }
                shape = shapeBuilder ?: error("Cannot deserialize MDList2: did not receive element with index 0")
                content = contentBuilder ?: error("Cannot deserialize MDList2: did not receive element with index 1")
            }
            check(shape.size == 2u) { "Cannot deserialize MDList2: received shape and content size mismatch" }
            check(content.size == shape[0u] * shape[1u]) { "Cannot deserialize MDList2: received shape and content size mismatch" }
            ArrayMDList2(rowNumber = shape[0u], columnNumber = shape[1u], data = content)
        }
}
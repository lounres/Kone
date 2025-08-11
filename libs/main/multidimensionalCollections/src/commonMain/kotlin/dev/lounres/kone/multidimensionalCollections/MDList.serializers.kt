/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.serializers.serializer
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList
import dev.lounres.kone.scope
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*


internal class MDListSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<MDList<E>> {
    private val mdSizeSerializer = MDSize.serializer()
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableList<E>> =
//        KoneArraySettableList.serializer(elementSerializer)
    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableNoddedList<E>> =
        KoneArraySettableNoddedList.serializer(elementSerializer)
    private val contentDeserializationStrategy: DeserializationStrategy<KoneMutableArray<Any?>> =
        @Suppress("UNCHECKED_CAST") KoneMutableArray.serializer<Any, Any?>(elementSerializer as KSerializer<Any?>)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList", elementSerializer.descriptor) {
        element("size", mdSizeSerializer.descriptor)
        element("content", contentDeserializationStrategy.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MDList<E>) {
        val content = scope {
            val indicesIterator = MDSizeStrides(value.size).iterator()
            // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//            KoneArraySettableList(value.size) { value[indicesIterator.next()] }
            KoneArraySettableNoddedList(value.contentSize) { value[indicesIterator.next()] }
        }
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = 0,
                serializer = mdSizeSerializer,
                value = value.size,
            )
            encodeSerializableElement(
                descriptor = descriptor,
                index = 1,
                serializer = contentSerializationStrategy,
                value = content,
            )
        }
    }

    override fun deserialize(decoder: Decoder): MDList<E> =
        decoder.decodeStructure(descriptor) {
            val size: MDSize
            val content: KoneMutableArray<Any?>
            if (decodeSequentially()) { // sequential decoding protocol
                size = decodeSerializableElement(descriptor, 0, mdSizeSerializer)
                content = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
            } else {
                var sizeBuilder: MDSize? = null
                var contentBuilder: KoneMutableArray<Any?>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> sizeBuilder = decodeSerializableElement(descriptor, 0, mdSizeSerializer)
                        1 -> contentBuilder = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Cannot deserialize MDList: got index $index out of range [0; 2)")
                    }
                }
                size = sizeBuilder ?: error("Cannot deserialize MDList: did not receive element with index 0")
                content = contentBuilder ?: error("Cannot deserialize MDList: did not receive element with index 1")
            }
            check(content.size == size.fold(1u) { acc, dim -> acc * dim }) { "Cannot deserialize MDList: received MD size and content size mismatch" }
            ArrayMDList(size = size, data = content)
        }
}

internal class SettableMDListSerializer<E>(
    elementSerializer: KSerializer<E>,
) : KSerializer<SettableMDList<E>> {
    private val mdSizeSerializer = MDSize.serializer()
    // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableList<E>> =
//        KoneArraySettableList.serializer(elementSerializer)
    private val contentSerializationStrategy: SerializationStrategy<KoneArraySettableNoddedList<E>> =
        KoneArraySettableNoddedList.serializer(elementSerializer)
    private val contentDeserializationStrategy: DeserializationStrategy<KoneMutableArray<Any?>> =
        @Suppress("UNCHECKED_CAST") KoneMutableArray.serializer<Any, Any?>(elementSerializer as KSerializer<Any?>)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.lounres.kone.multidimensionalCollections.MDList", elementSerializer.descriptor) {
        element("size", mdSizeSerializer.descriptor)
        element("content", contentDeserializationStrategy.descriptor)
    }

    override fun serialize(encoder: Encoder, value: SettableMDList<E>) {
        val content = scope {
            val indicesIterator = MDSizeStrides(value.size).iterator()
            // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1 (KT-79547)
//            KoneArraySettableList(value.size) { value[indicesIterator.next()] }
            KoneArraySettableNoddedList(value.contentSize) { value[indicesIterator.next()] }
        }
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = 0,
                serializer = mdSizeSerializer,
                value = value.size,
            )
            encodeSerializableElement(
                descriptor = descriptor,
                index = 1,
                serializer = contentSerializationStrategy,
                value = content,
            )
        }
    }

    override fun deserialize(decoder: Decoder): SettableMDList<E> =
        decoder.decodeStructure(descriptor) {
            val size: MDSize
            val content: KoneMutableArray<Any?>
            if (decodeSequentially()) { // sequential decoding protocol
                size = decodeSerializableElement(descriptor, 0, mdSizeSerializer)
                content = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
            } else {
                var sizeBuilder: MDSize? = null
                var contentBuilder: KoneMutableArray<Any?>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> sizeBuilder = decodeSerializableElement(descriptor, 0, mdSizeSerializer)
                        1 -> contentBuilder = decodeSerializableElement(descriptor, 1, contentDeserializationStrategy)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Cannot deserialize MDList: got index $index out of range [0; 2)")
                    }
                }
                size = sizeBuilder ?: error("Cannot deserialize MDList: did not receive element with index 0")
                content = contentBuilder ?: error("Cannot deserialize MDList: did not receive element with index 1")
            }
            check(content.size == size.fold(1u) { acc, dim -> acc * dim }) { "Cannot deserialize MDList: received MD size and content size mismatch" }
            ArrayMDList(size = size, data = content)
        }
}
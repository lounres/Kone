/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.serializers

import dev.lounres.kone.collections.KoneCollectionWithContext
import dev.lounres.kone.collections.KoneExtendableIterableCollection
import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableIterableCollection
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.KoneRemovableIterableCollection
import dev.lounres.kone.collections.KoneReversibleExtendableIterableCollection
import dev.lounres.kone.collections.KoneReversibleIterableCollection
import dev.lounres.kone.collections.KoneReversibleMutableIterableCollection
import dev.lounres.kone.collections.KoneReversibleRemovableIterableCollection
import dev.lounres.kone.collections.KoneSettableIterableList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayListSerializer
import dev.lounres.kone.collections.implementations.KoneResizableArrayListSerializer
import dev.lounres.kone.collections.implementations.KoneSettableArrayListSerializer
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.encoding.encodeStructure


// region Serializers templates

internal abstract class KoneIterableCollectionSerializationStrategy<E, in C: KoneIterableCollection<E>>: SerializationStrategy<C> {
    public abstract val elementSerializer: SerializationStrategy<E>
    
    final override fun serialize(encoder: Encoder, value: C) {
        encoder.encodeCollection(descriptor, value.size.toInt()) {
            for ((index, element) in value.withIndex())
                encodeSerializableElement(descriptor, index.toInt(), elementSerializer, element)
        }
    }
}

internal abstract class KoneIterableCollectionSerializerTemplate<E, C: KoneIterableCollection<E>>: KoneIterableCollectionSerializationStrategy<E, C>(), KSerializer<C> {
    abstract override val elementSerializer: KSerializer<E>
    public abstract fun buildCollection(size: UInt, initializer: (UInt) -> E): C
    
    final override fun deserialize(decoder: Decoder): C =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                buildCollection(size.toUInt()) {
                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
                }
            } else {
                val builder = KoneGrowableArrayList<E>()
                while (true) {
                    val index = decodeElementIndex(descriptor)
                    if (index == CompositeDecoder.DECODE_DONE) break
                    builder.add(decodeSerializableElement(descriptor, index, elementSerializer))
                }
                buildCollection(builder.size) { builder[it] }
            }
        }
}

internal abstract class KoneIterableCollectionWithContextSerializationStrategy<E, EC: Equality<E>, C>(
    collectionSerialName: String,
    elementDescriptor: SerialDescriptor,
): SerializationStrategy<C> where C: KoneIterableCollection<E>, C: KoneCollectionWithContext<E, EC> {
    public abstract val elementCollectionSerializer: SerializationStrategy<C>
    public abstract val elementContextSerializer: SerializationStrategy<EC>
    
    override val descriptor: SerialDescriptor by lazy {
        buildClassSerialDescriptor(
            collectionSerialName,
            elementDescriptor,
            elementContextSerializer.descriptor,
        ) {
            element("data", elementCollectionSerializer.descriptor)
            element("context", elementContextSerializer.descriptor)
        }
    }
    
    final override fun serialize(encoder: Encoder, value: C) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, elementCollectionSerializer, value)
            encodeSerializableElement(descriptor, 1, elementContextSerializer, value.elementContext)
        }
    }
}

internal abstract class KoneIterableCollectionWithContextSerializerTemplate<E, EC: Equality<E>, C>(
    collectionSerialName: String,
    elementDescriptor: SerialDescriptor,
): KSerializer<C>, KoneIterableCollectionWithContextSerializationStrategy<E, EC, C>(
    collectionSerialName,
    elementDescriptor,
) where C: KoneIterableCollection<E>, C: KoneCollectionWithContext<E, EC> {
    public abstract val elementSerializer: KSerializer<E>
    abstract override val elementContextSerializer: KSerializer<EC>
    public abstract fun result(elementList: KoneIterableList<E>, elementContext: EC): C
    
    override fun deserialize(decoder: Decoder): C =
        decoder.decodeStructure(descriptor) {
            val result: C
            if (decodeSequentially()) {
                val context = decodeSerializableElement(descriptor, 1, elementContextSerializer)
                val builder = decodeSerializableElement(descriptor, 0, KoneGrowableArrayListSerializer(elementSerializer, context))
                result = result(builder, context)
            } else {
                var builder: KoneGrowableArrayList<E, *>? = null
                var context: EC? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> builder = decodeSerializableElement(descriptor, 0, KoneGrowableArrayListSerializer(elementSerializer, defaultEquality()))
                        1 -> context = decodeSerializableElement(descriptor, 1, elementContextSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                result = result(builder!!, context!!)
            }
            result
        }
}

// endregion

// region Default serializers for common interfaces

internal class DefaultKoneIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneIterableCollection<E>>(), KSerializer<KoneIterableCollection<E>> {
    private val actualSerializer = KoneSettableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneReversibleIterableCollection<E>>(), KSerializer<KoneReversibleIterableCollection<E>> {
    private val actualSerializer = KoneSettableArrayListSerializer(elementSerializer, defaultEquality())

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneExtendableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneExtendableIterableCollection<E>>(), KSerializer<KoneExtendableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneExtendableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleExtendableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneReversibleExtendableIterableCollection<E>>(), KSerializer<KoneReversibleExtendableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleExtendableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneRemovableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneRemovableIterableCollection<E>>(), KSerializer<KoneRemovableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneRemovableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleRemovableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneReversibleRemovableIterableCollection<E>>(), KSerializer<KoneReversibleRemovableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleRemovableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneMutableIterableCollection<E>>(), KSerializer<KoneMutableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleMutableIterableCollectionSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneReversibleMutableIterableCollection<E>>(), KSerializer<KoneReversibleMutableIterableCollection<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleMutableIterableCollection<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneIterableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneIterableList<E>>(), KSerializer<KoneIterableList<E>> {
    private val actualSerializer = KoneSettableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneIterableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableIterableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneSettableIterableList<E>>(), KSerializer<KoneSettableIterableList<E>> {
    private val actualSerializer = KoneSettableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneSettableIterableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableIterableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableCollectionSerializationStrategy<E, KoneMutableIterableList<E>>(), KSerializer<KoneMutableIterableList<E>> {
    private val actualSerializer = KoneResizableArrayListSerializer(elementSerializer, defaultEquality())
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableIterableList<E> = actualSerializer.deserialize(decoder)
}

// endregion
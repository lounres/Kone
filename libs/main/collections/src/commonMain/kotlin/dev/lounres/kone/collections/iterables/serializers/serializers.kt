/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("FunctionName")

package dev.lounres.kone.collections.iterables.serializers

import dev.lounres.kone.collections.iterables.*
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArraySettableListSerializer
import dev.lounres.kone.collections.utils.withIndex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*


// region Serializers templates

internal abstract class KoneIterableSerializationStrategyTemplate<E, in C: KoneIterable<E>>: SerializationStrategy<C> {
    abstract val elementSerializer: SerializationStrategy<E>

    final override fun serialize(encoder: Encoder, value: C) {
        encoder.encodeCollection(descriptor, value.size.toInt()) {
            for ((index, element) in value.withIndex())
                encodeSerializableElement(descriptor, index.toInt(), elementSerializer, element)
        }
    }
}

internal abstract class KoneIterableSerializerTemplate<E, C: KoneIterable<E>>: KoneIterableSerializationStrategyTemplate<E, C>(), KSerializer<C> {
    abstract override val elementSerializer: KSerializer<E>
    abstract fun buildCollection(size: UInt, initializer: (UInt) -> E): C

    final override fun deserialize(decoder: Decoder): C =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                buildCollection(size.toUInt()) {
                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
                }
            } else {
                val builder = KoneArrayGrowableList<E>()
                while (true) {
                    val index = decodeElementIndex(descriptor)
                    if (index == CompositeDecoder.DECODE_DONE) break
                    builder.add(decodeSerializableElement(descriptor, index, elementSerializer))
                }
                buildCollection(builder.size) { builder[it] }
            }
        }
}

//internal abstract class KoneIterableCollectionWithContextSerializationStrategy<E, EC: Equality<E>, C>(
//    collectionSerialName: String,
//    elementDescriptor: SerialDescriptor,
//): SerializationStrategy<C> where C: KoneIterable<E>, C: KoneCollectionWithContext<E, EC> {
//    public abstract val elementCollectionSerializer: SerializationStrategy<C>
//    public abstract val elementContextSerializer: SerializationStrategy<EC>
//
//    override val descriptor: SerialDescriptor by lazy {
//        buildClassSerialDescriptor(
//            collectionSerialName,
//            elementDescriptor,
//            elementContextSerializer.descriptor,
//        ) {
//            element("data", elementCollectionSerializer.descriptor)
//            element("context", elementContextSerializer.descriptor)
//        }
//    }
//
//    final override fun serialize(encoder: Encoder, value: C) {
//        encoder.encodeStructure(descriptor) {
//            encodeSerializableElement(descriptor, 0, elementCollectionSerializer, value)
//            encodeSerializableElement(descriptor, 1, elementContextSerializer, value.elementContext)
//        }
//    }
//}
//
//internal abstract class KoneIterableCollectionWithContextSerializerTemplate<E, EC: Equality<E>, C>(
//    collectionSerialName: String,
//    elementDescriptor: SerialDescriptor,
//): KSerializer<C>, KoneIterableCollectionWithContextSerializationStrategy<E, EC, C>(
//    collectionSerialName,
//    elementDescriptor,
//) where C: KoneIterable<E>, C: KoneCollectionWithContext<E, EC> {
//    public abstract val elementSerializer: KSerializer<E>
//    abstract override val elementContextSerializer: KSerializer<EC>
//    public abstract fun result(elementList: KoneIterableList<E>, elementContext: EC): C
//
//    override fun deserialize(decoder: Decoder): C =
//        decoder.decodeStructure(descriptor) {
//            val result: C
//            if (decodeSequentially()) {
//                val context = decodeSerializableElement(descriptor, 1, elementContextSerializer)
//                val builder = decodeSerializableElement(descriptor, 0, KoneGrowableArrayListSerializer(elementSerializer, context))
//                result = result(builder, context)
//            } else {
//                var builder: KoneGrowableArrayList<E, *>? = null
//                var context: EC? = null
//                while (true) {
//                    when (val index = decodeElementIndex(descriptor)) {
//                        0 -> builder = decodeSerializableElement(descriptor, 0, KoneGrowableArrayListSerializer(elementSerializer, defaultEquality()))
//                        1 -> context = decodeSerializableElement(descriptor, 1, elementContextSerializer)
//                        CompositeDecoder.DECODE_DONE -> break
//                        else -> error("Unexpected index: $index")
//                    }
//                }
//                result = result(builder!!, context!!)
//            }
//            result
//        }
//}

// endregion

// region Default serializers for common interfaces

internal class DefaultKoneIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneIterable<E>>(), KSerializer<KoneIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleIterable<E>>(), KSerializer<KoneReversibleIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneReversibleIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneReversibleIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableIterable<E>>(), KSerializer<KoneSettableIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneSettableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneSettableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleSettableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleSettableIterable<E>>(), KSerializer<KoneReversibleSettableIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneReversibleSettableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneReversibleSettableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneExtendableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneExtendableIterable<E>>(), KSerializer<KoneExtendableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneExtendableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneExtendableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleExtendableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleExtendableIterable<E>>(), KSerializer<KoneReversibleExtendableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneReversibleExtendableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneReversibleExtendableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneRemovableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneRemovableIterable<E>>(), KSerializer<KoneRemovableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneRemovableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneRemovableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleRemovableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleRemovableIterable<E>>(), KSerializer<KoneReversibleRemovableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneReversibleRemovableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneReversibleRemovableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableIterable<E>>(), KSerializer<KoneMutableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneMutableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneMutableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleMutableIterable<E>>(), KSerializer<KoneReversibleMutableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneReversibleMutableIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneReversibleMutableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinearIterable<E>>(), KSerializer<KoneLinearIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneLinearIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableLinearMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableLinearIterable<E>>(), KSerializer<KoneSettableLinearIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneSettableLinearIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneSettableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneExtendableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneExtendableLinearIterable<E>>(), KSerializer<KoneExtendableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneExtendableLinearIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneExtendableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneRemovableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneRemovableLinearIterable<E>>(), KSerializer<KoneRemovableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneRemovableLinearIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneRemovableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinearIterable<E>>(), KSerializer<KoneMutableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = KoneIterableDeclarationDescriptor("KoneMutableLinearIterable", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneMutableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

// endregion
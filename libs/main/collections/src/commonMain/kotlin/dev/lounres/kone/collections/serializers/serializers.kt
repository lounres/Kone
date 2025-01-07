/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.serializers

import dev.lounres.kone.collections.KoneExtendableIterable
import dev.lounres.kone.collections.KoneExtendableLinearIterable
import dev.lounres.kone.collections.KoneGrowableMutableList
import dev.lounres.kone.collections.KoneGrowableMutableNoddedList
import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneLinearIterable
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableIterable
import dev.lounres.kone.collections.KoneMutableLinearIterable
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneNoddedList
import dev.lounres.kone.collections.KoneRemovableIterable
import dev.lounres.kone.collections.KoneRemovableLinearIterable
import dev.lounres.kone.collections.KoneReversibleExtendableIterable
import dev.lounres.kone.collections.KoneReversibleIterable
import dev.lounres.kone.collections.KoneReversibleMutableIterable
import dev.lounres.kone.collections.KoneReversibleRemovableIterable
import dev.lounres.kone.collections.KoneReversibleSettableIterable
import dev.lounres.kone.collections.KoneSettableIterable
import dev.lounres.kone.collections.KoneSettableLinearIterable
import dev.lounres.kone.collections.KoneSettableList
import dev.lounres.kone.collections.KoneSettableNoddedList
import dev.lounres.kone.collections.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.implementations.KoneArrayGrowableListSerializer
import dev.lounres.kone.collections.implementations.KoneArrayGrowableNoddedListSerializer
import dev.lounres.kone.collections.implementations.KoneArrayResizableListSerializer
import dev.lounres.kone.collections.implementations.KoneArrayResizableNoddedListSerializer
import dev.lounres.kone.collections.implementations.KoneArraySettableListSerializer
import dev.lounres.kone.collections.implementations.KoneArraySettableNoddedListSerializer
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.withIndex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection


// region Serializers templates

internal abstract class KoneIterableSerializationStrategyTemplate<E, in C: KoneIterable<E>>: SerializationStrategy<C> {
    public abstract val elementSerializer: SerializationStrategy<E>

    final override fun serialize(encoder: Encoder, value: C) {
        encoder.encodeCollection(descriptor, value.size.toInt()) {
            for ((index, element) in value.withIndex())
                encodeSerializableElement(descriptor, index.toInt(), elementSerializer, element)
        }
    }
}

internal abstract class KoneIterableSerializerTemplate<E, C: KoneIterable<E>>: KoneIterableSerializationStrategyTemplate<E, C>(), KSerializer<C> {
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

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleIterable<E>>(), KSerializer<KoneReversibleIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableIterable<E>>(), KSerializer<KoneSettableIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneSettableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleSettableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleSettableIterable<E>>(), KSerializer<KoneReversibleSettableIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleSettableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneExtendableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneExtendableIterable<E>>(), KSerializer<KoneExtendableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneExtendableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleExtendableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleExtendableIterable<E>>(), KSerializer<KoneReversibleExtendableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleExtendableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneRemovableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneRemovableIterable<E>>(), KSerializer<KoneRemovableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneRemovableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleRemovableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleRemovableIterable<E>>(), KSerializer<KoneReversibleRemovableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleRemovableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableIterable<E>>(), KSerializer<KoneMutableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneReversibleMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneReversibleMutableIterable<E>>(), KSerializer<KoneReversibleMutableIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneReversibleMutableIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinearIterable<E>>(), KSerializer<KoneLinearIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableLinearMutableIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableLinearIterable<E>>(), KSerializer<KoneSettableLinearIterable<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneSettableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneExtendableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneExtendableLinearIterable<E>>(), KSerializer<KoneExtendableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneExtendableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneRemovableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneRemovableLinearIterable<E>>(), KSerializer<KoneRemovableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneRemovableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableLinearIterableSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinearIterable<E>>(), KSerializer<KoneMutableLinearIterable<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableLinearIterable<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneList<E>>(), KSerializer<KoneList<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableList<E>>(), KSerializer<KoneSettableList<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneSettableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableList<E>>(), KSerializer<KoneMutableList<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneGrowableMutableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneGrowableMutableList<E>>(), KSerializer<KoneGrowableMutableList<E>> {
    private val actualSerializer = KoneArrayGrowableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneGrowableMutableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneNoddedList<E>>(), KSerializer<KoneNoddedList<E>> {
    private val actualSerializer = KoneArraySettableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableNoddedList<E>>(), KSerializer<KoneSettableNoddedList<E>> {
    private val actualSerializer = KoneArraySettableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneSettableNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableNoddedList<E>>(), KSerializer<KoneMutableNoddedList<E>> {
    private val actualSerializer = KoneArrayResizableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneMutableNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneGrowableMutableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneGrowableMutableNoddedList<E>>(), KSerializer<KoneGrowableMutableNoddedList<E>> {
    private val actualSerializer = KoneArrayGrowableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor get() = actualSerializer.descriptor
    override fun deserialize(decoder: Decoder): KoneGrowableMutableNoddedList<E> = actualSerializer.deserialize(decoder)
}

// endregion
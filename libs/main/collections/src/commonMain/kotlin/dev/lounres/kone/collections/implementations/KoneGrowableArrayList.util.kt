/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.DefaultKoneIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import dev.lounres.kone.collections.serializers.KoneIterableCollectionSerializationStrategy
import dev.lounres.kone.collections.serializers.KoneIterableCollectionWithContextSerializerTemplate
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure


public fun <E, EC: Equality<E>> KoneGrowableArrayList(elementContext: EC): KoneGrowableArrayList<E, EC> =
    KoneGrowableArrayList(size = 0u, elementContext = elementContext)

public fun <E> KoneGrowableArrayList(): KoneGrowableArrayList<E, Equality<E>> =
    KoneGrowableArrayList(size = 0u, elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneGrowableArrayList(initialCapacity: UInt, elementContext: EC): KoneGrowableArrayList<E, EC> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = elementContext,
    )

public fun <E> KoneGrowableArrayList(initialCapacity: UInt): KoneGrowableArrayList<E, Equality<E>> =
    KoneGrowableArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
        elementContext = defaultEquality(),
    )

public inline fun <E, EC: Equality<E>> KoneGrowableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneGrowableArrayList<E, EC> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = elementContext,
    )
}

public inline fun <E> KoneGrowableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableArrayList<E, Equality<E>> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
        elementContext = defaultEquality(),
    )
}

internal class KoneGrowableArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneGrowableArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

internal class KoneGrowableArrayListSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    public val elementContext: EC,
): KoneIterableCollectionSerializationStrategy<E, KoneGrowableArrayList<E, EC>>(), DeserializationStrategy<KoneGrowableArrayList<E, EC>> {
    override val descriptor: SerialDescriptor = KoneGrowableArrayListDescriptor(elementSerializer.descriptor)
    
    override fun deserialize(decoder: Decoder): KoneGrowableArrayList<E, EC> =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                KoneGrowableArrayList(size.toUInt(), elementContext) {
                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
                }
            } else {
                val builder = KoneGrowableArrayList<E, EC>(elementContext)
                while (true) {
                    val index = decodeElementIndex(descriptor)
                    if (index == CompositeDecoder.DECODE_DONE) break
                    builder.add(decodeSerializableElement(descriptor, index, elementSerializer))
                }
                builder
            }
        }
}

internal class KoneGrowableArrayListWithContextSerializer<E, EC: Equality<E>>(
    override val elementSerializer: KSerializer<E>,
    override val elementContextSerializer: KSerializer<EC>,
): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneGrowableArrayList<E, EC>>(
    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneGrowableArrayList",
    elementDescriptor = elementSerializer.descriptor,
), DeserializationStrategy<KoneGrowableArrayList<E, EC>> {
    override val elementCollectionSerializer: SerializationStrategy<KoneGrowableArrayList<E, EC>> =
        DefaultKoneIterableCollectionSerializer(elementSerializer)
    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneGrowableArrayList<E, EC> =
        KoneGrowableArrayList(elementList.size, elementContext) { elementList[it] }
}
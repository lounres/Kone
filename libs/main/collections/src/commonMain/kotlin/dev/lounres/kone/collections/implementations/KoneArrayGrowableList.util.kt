/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneGrowableMutableList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableList(): KoneArrayGrowableList<Element> =
    KoneArrayGrowableList(size = 0u)

public fun <Element> KoneArrayGrowableList(initialCapacity: UInt): KoneArrayGrowableList<Element> =
    KoneArrayGrowableList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public inline fun <Element> KoneArrayGrowableList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public inline fun <Element> KoneArrayGrowableList(initialCapacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableList<Element> {
    require(size <= initialCapacity) { "Provided initial capacity must not be less than provided size" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity)
    return KoneArrayGrowableList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayGrowableListProducer : KoneGrowableMutableListProducer {
    override fun <E> produce(initialCapacity: UInt): KoneArrayGrowableList<E> = KoneArrayGrowableList(initialCapacity)
    override fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E> =
        KoneArrayGrowableList(initialCapacity, number, builder)
}

internal class KoneArrayGrowableListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneArrayGrowableList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneArrayGrowableListSerializer<E>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializationStrategy<E, KoneArrayGrowableList<E, EC>>(), DeserializationStrategy<KoneArrayGrowableList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneArrayGrowableListDescriptor(elementSerializer.descriptor)
//
//    override fun deserialize(decoder: Decoder): KoneArrayGrowableList<E, EC> =
//        decoder.decodeStructure(descriptor) {
//            if (decodeSequentially()) {
//                val size = decodeCollectionSize(descriptor)
//                KoneArrayGrowableList(size.toUInt(), elementContext) {
//                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
//                }
//            } else {
//                val builder = KoneArrayGrowableList<E, EC>(elementContext)
//                while (true) {
//                    val index = decodeElementIndex(descriptor)
//                    if (index == CompositeDecoder.DECODE_DONE) break
//                    builder.add(decodeSerializableElement(descriptor, index, elementSerializer))
//                }
//                builder
//            }
//        }
//}
//
//internal class KoneArrayGrowableListWithContextSerializer<E>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneArrayGrowableList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneArrayGrowableList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneArrayGrowableList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneArrayGrowableList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneArrayGrowableList<E, EC> =
//        KoneArrayGrowableList(elementList.size, elementContext) { elementList[it] }
//}
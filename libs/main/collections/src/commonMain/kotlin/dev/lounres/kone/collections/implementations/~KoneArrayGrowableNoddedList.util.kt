/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableNoddedList(): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(size = 0u)

public fun <Element> KoneArrayGrowableNoddedList(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
    KoneArrayGrowableNoddedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public inline fun <Element> KoneArrayGrowableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

public inline fun <Element> KoneArrayGrowableNoddedList(size: UInt, capacity: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableNoddedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(capacity)
    return KoneArrayGrowableNoddedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) KoneArrayGrowableNoddedList.Node(initializer(it), it) else null },
    )
}

public object KoneArrayGrowableNoddedListProducer : KoneGrowableMutableNoddedListProducer {
    override fun <Element> produce(initialCapacity: UInt): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList(initialCapacity)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneArrayGrowableNoddedList<Element> =
        KoneArrayGrowableNoddedList(size = number, capacity = initialCapacity, initializer = builder)
}

internal class KoneGrowableArrayNoddedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneGrowableArrayNoddedList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneGrowableArrayListSerializer<E>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializationStrategy<E, KoneGrowableArrayList<E, EC>>(), DeserializationStrategy<KoneGrowableArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneGrowableArrayListDescriptor(elementSerializer.descriptor)
//
//    override fun deserialize(decoder: Decoder): KoneGrowableArrayList<E, EC> =
//        decoder.decodeStructure(descriptor) {
//            if (decodeSequentially()) {
//                val size = decodeCollectionSize(descriptor)
//                KoneGrowableArrayList(size.toUInt(), elementContext) {
//                    decodeSerializableElement(descriptor, it.toInt(), elementSerializer)
//                }
//            } else {
//                val builder = KoneGrowableArrayList<E, EC>(elementContext)
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
//internal class KoneGrowableArrayListWithContextSerializer<E>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneGrowableArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneGrowableArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneGrowableArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneGrowableArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneGrowableArrayList<E, EC> =
//        KoneGrowableArrayList(elementList.size, elementContext) { elementList[it] }
//}
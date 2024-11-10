/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneGrowableMutableList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneArrayGrowableLinkedList(): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(size = 0u)

public fun <Element> KoneArrayGrowableLinkedList(initialCapacity: UInt): KoneArrayGrowableLinkedList<Element> =
    KoneArrayGrowableLinkedList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneArrayGrowableLinkedList(size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public fun <Element> KoneArrayGrowableLinkedList(initialCapacity: UInt, size: UInt, initializer: (index: UInt) -> Element): KoneArrayGrowableLinkedList<Element> {
    require(size <= initialCapacity) { "Provided initial capacity must not be less than provided size" }
    val sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity)
    return KoneArrayGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
}

public object KoneArrayGrowableLinkedListProducer : KoneGrowableMutableListProducer {
    override fun <E> produce(initialCapacity: UInt): KoneArrayGrowableLinkedList<E> = KoneArrayGrowableLinkedList(initialCapacity)
    override fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E> =
        KoneArrayGrowableLinkedList(initialCapacity, number, builder)
}

internal class KoneGrowableLinkedArrayListDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneGrowableLinkedArrayListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneGrowableLinkedArrayList<E, EC>>(), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneGrowableLinkedArrayListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGrowableLinkedArrayList<E, EC> =
//        KoneGrowableLinkedArrayList(size, elementContext, initializer)
//}
//
//internal class KoneGrowableLinkedArrayListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneGrowableLinkedArrayList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneGrowableLinkedArrayList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneGrowableLinkedArrayList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneGrowableLinkedArrayList<E, EC> =
//        KoneGrowableLinkedArrayList(elementList.size, elementContext) { elementList[it] }
//}
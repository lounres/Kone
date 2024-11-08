/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneGrowableLinkedArrayList(): KoneGrowableLinkedArrayList<Element> =
    KoneGrowableLinkedArrayList(size = 0u)

public fun <Element> KoneGrowableLinkedArrayList(initialCapacity: UInt): KoneGrowableLinkedArrayList<Element> =
    KoneGrowableLinkedArrayList(
        size = 0u,
        sizeUpperBound = powerOf2GreaterOrEqualTo(initialCapacity),
    )

public fun <Element> KoneGrowableLinkedArrayList(size: UInt, initializer: (index: UInt) -> Element): KoneGrowableLinkedArrayList<Element> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedArrayList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null },
    )
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
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


internal class KoneListBackedSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneListBackedSet<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneListBackedSetSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneListBackedSet<E, EC>>(), DeserializationStrategy<KoneListBackedSet<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneListBackedSetDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneListBackedSet<E, EC> =
//        KoneListBackedSet(
//            elementContext,
//            KoneFixedCapacityArrayList(size, elementContext)
//                .apply {
//                    (0u..<size).forEach {
//                        val element = initializer(it)
//                        if (element !in this ) add(element)
//                    }
//                }.toOptimizedList(elementContext)
//        )
//}
//
//internal class KoneListBackedSetWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneListBackedSet<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneListBackedSet",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneListBackedSet<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneListBackedSet<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneListBackedSet<E, EC> =
//        KoneListBackedSet(
//            elementContext,
//            KoneFixedCapacityArrayList(elementList.size, elementContext)
//                .apply {
//                    elementList.indices.forEach {
//                        val element = elementList[it]
//                        if (element !in this ) add(element)
//                    }
//                }.toOptimizedList(elementContext)
//        )
//}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor


internal class KoneResizableHashSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneResizableHashSet<data>",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneResizableHashSetSerializer<E, EC: Hashing<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneResizableHashSet<E, EC>>(), DeserializationStrategy<KoneResizableHashSet<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneResizableHashSetDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneResizableHashSet<E, EC> =
//        KoneResizableHashSet(elementContext).apply {
//            (0u..<size).forEach { add(initializer(it)) }
//        }
//}
//
//internal class KoneResizableHashSetWithContextSerializer<E, EC: Hashing<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneResizableHashSet<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneResizableHashSet",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneResizableHashSet<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneResizableHashSet<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneResizableHashSet<E, EC> =
//        KoneResizableHashSet(elementContext).apply {
//            addAllFrom(elementList)
//        }
//}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations


//public fun <Element> KoneLinkedGCList(size: UInt, initializer: (index: UInt) -> Element): KoneLinkedGCList<Element> =
//    KoneLinkedGCList<Element>().apply {
//        for (index in 0u ..< size) add(initializer(index))
//    }
//
//internal class KoneLinkedGCListDescriptor(elementDescriptor: SerialDescriptor):
//    KoneCollectionDescriptor(
//        serialName = "dev.lounres.kone.collections.implementations.KoneLinkedGCList<data>",
//        elementDescriptor = elementDescriptor,
//    )

//internal class KoneLinkedGCListSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneLinkedGCList<E, EC>>(), DeserializationStrategy<KoneLinkedGCList<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneLinkedGCListDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneLinkedGCList<E, EC> =
//        KoneLinkedGCList(size, elementContext, initializer)
//}
//
//internal class KoneLinkedGCListWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneLinkedGCList<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneLinkedGCList",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneLinkedGCList<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneLinkedGCList<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneLinkedGCList<E, EC> =
//        KoneLinkedGCList(elementList.size, elementContext) { elementList[it] }
//}
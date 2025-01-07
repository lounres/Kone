/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.repeat


public fun <Element> KoneGCLinkedList(): KoneGCLinkedList<Element> = KoneGCLinkedList(size = 0u, startNode = null, endNode = null)

public inline fun <Element> KoneGCLinkedList(size: UInt, initializer: (index: UInt) -> Element): KoneGCLinkedList<Element> {
    val result = KoneGCLinkedList<Element>(size = size)
    var previousNode: KoneGCLinkedList.Node<Element>? = null
    repeat(size) {
        val newNode = result.Node(initializer(it))
        newNode._previousNode = previousNode
        previousNode?._nextNode = newNode
        if (it == 0u) result.start = newNode
        previousNode = newNode
    }
    result.end = previousNode
    return result
}

public object KoneGCLinkedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneGCLinkedList<Element> = KoneGCLinkedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> = KoneGCLinkedList(number, builder)
}

//internal class KoneLinkedGCListDescriptor(elementDescriptor: SerialDescriptor):
//    KoneCollectionDescriptor(
//        serialName = "dev.lounres.kone.collections.implementations.KoneLinkedGCList<data>",
//        elementDescriptor = elementDescriptor,
//    )
//
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
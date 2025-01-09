/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import dev.lounres.kone.repeat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


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

internal class KoneGCLinkedListDescriptor(elementDescriptor: SerialDescriptor):
    KoneListImplementationDescriptor(
        implementationName = "KoneGCLinkedList",
        elementDescriptor = elementDescriptor,
    )

internal class KoneGCLinkedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneGCLinkedList<E>>(), DeserializationStrategy<KoneGCLinkedList<E>> {
    override val descriptor: SerialDescriptor = KoneGCLinkedListDescriptor(elementSerializer.descriptor)
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGCLinkedList<E> =
        KoneGCLinkedList(size, initializer)
}
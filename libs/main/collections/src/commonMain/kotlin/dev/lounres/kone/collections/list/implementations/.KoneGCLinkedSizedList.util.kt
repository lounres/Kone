/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneGCLinkedSizedList(): KoneGCLinkedSizedList<Element> = KoneGCLinkedSizedList(size = 0u, startNode = null, endNode = null)

public inline fun <Element> KoneGCLinkedSizedList(size: UInt, initializer: (index: UInt) -> Element): KoneGCLinkedSizedList<Element> {
    val result = KoneGCLinkedSizedList<Element>(size = size)
    if (size == 0u) return result
    var currentNode = result.Node(initializer(0u))
    result.start = currentNode
    for (index in 1u ..< size) {
        val newNode = result.Node(initializer(index))
        newNode._previousNode = currentNode
        currentNode._nextNode = newNode
        currentNode = newNode
    }
    result.end = currentNode
    return result
}

public inline fun <Element> KoneGCLinkedSizedList(indices: UIntRange, initializer: (index: UInt) -> Element): KoneGCLinkedSizedList<Element> {
    val result = KoneGCLinkedSizedList<Element>(size = if (indices.isEmpty()) 0u else (indices.last + 1u - indices.first))
    if (indices.isEmpty()) return result
    var currentNode = result.Node(initializer(indices.first))
    result.start = currentNode
    for (index in (indices.first + 1u) .. indices.last) {
        val newNode = result.Node(initializer(index))
        newNode._previousNode = currentNode
        currentNode._nextNode = newNode
        currentNode = newNode
    }
    result.end = currentNode
    return result
}

internal object KoneGCLinkedSizedListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneGCLinkedSizedList<Element> = KoneGCLinkedSizedList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> = KoneGCLinkedSizedList(number, builder)
}

public fun KoneGCLinkedSizedList.Companion.producer(): KoneResizableMutableNoddedListProducer = KoneGCLinkedSizedListProducer

internal val KoneGCLinkedSizedListCopier: (KoneGCLinkedSizedList<Any?>) -> KoneGCLinkedSizedList<Any?> =
    copier@{ base ->
        if (base.isDisposed) disposedInstanceException()
        val result = KoneGCLinkedSizedList<Any?>(size = base.size)
        var currentNode = base.start ?: return@copier result
        var newCurrentNode = result.Node(currentNode.element)
        result.start = newCurrentNode
        while (currentNode._nextNode != null) {
            val nextNode = currentNode._nextNode!!
            val newNextNode = result.Node(nextNode.element)
            newCurrentNode._nextNode = newNextNode
            newNextNode._previousNode = newCurrentNode
            currentNode = nextNode
            newCurrentNode = newNextNode
        }
        result.end = newCurrentNode
        result
    }

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneGCLinkedSizedList.Companion.copier(): (KoneGCLinkedSizedList<Element>) -> KoneGCLinkedSizedList<Element> =
    KoneGCLinkedSizedListCopier as (KoneGCLinkedSizedList<Element>) -> KoneGCLinkedSizedList<Element>

//internal object KoneGCLinkedSizedListMelder: (KoneGCLinkedSizedList<Any?>, KoneGCLinkedSizedList<Any?>) -> KoneGCLinkedSizedList<Any?> {
//    override fun invoke(first: KoneGCLinkedSizedList<Any?>, second: KoneGCLinkedSizedList<Any?>): KoneGCLinkedSizedList<Any?> {
//        if (first.isDisposed || second.isDisposed) disposedInstanceException()
//        when {
//            first.size == 0u -> return second
//            second.size == 0u -> return first
//        }
//        val endOfFirst = first.end!!
//        val startOfSecond = second.start!!
//        endOfFirst._nextNode = startOfSecond
//        startOfSecond._previousNode = endOfFirst
//        // TODO: Update children's `list` reference
//        first.end = second.end
//        second.start = null
//        second.end = null
//        second.isDisposed = true
//        return first
//    }
//}
//
//@Suppress("UNCHECKED_CAST")
//public fun <Element> KoneGCLinkedSizedList.Companion.melder(): (KoneGCLinkedSizedList<Element>, KoneGCLinkedSizedList<Element>) -> KoneGCLinkedSizedList<Element> =
//    KoneGCLinkedSizedListMelder as (KoneGCLinkedSizedList<Element>, KoneGCLinkedSizedList<Element>) -> KoneGCLinkedSizedList<Element>

internal class KoneGCLinkedSizedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneGCLinkedSizedList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneGCLinkedSizedList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGCLinkedSizedList<E> =
        KoneGCLinkedSizedList(size, initializer)
}
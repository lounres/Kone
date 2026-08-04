/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.iterable.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneGCLinkedMeldableList(): KoneGCLinkedMeldableList<Element> = KoneGCLinkedMeldableList(startNode = null, endNode = null)

public inline fun <Element> KoneGCLinkedMeldableList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneGCLinkedMeldableList<Element> {
    val result = KoneGCLinkedMeldableList<Element>()
    if (size == 0u) return result
    var currentNode = KoneGCLinkedMeldableList.Node(initializer(0u))
    result.start = currentNode
    for (index in 1u ..< size) {
        val newNode = KoneGCLinkedMeldableList.Node(initializer(index))
        newNode._previousNode = currentNode
        currentNode._nextNode = newNode
        currentNode = newNode
    }
    result.end = currentNode
    return result
}

public inline fun <Element> KoneGCLinkedMeldableList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneGCLinkedMeldableList<Element> {
    val result = KoneGCLinkedMeldableList<Element>()
    if (indices.isEmpty()) return result
    var currentNode = KoneGCLinkedMeldableList.Node(initializer(indices.first))
    result.start = currentNode
    for (index in (indices.first + 1u) .. indices.last) {
        val newNode = KoneGCLinkedMeldableList.Node(initializer(index))
        newNode._previousNode = currentNode
        currentNode._nextNode = newNode
        currentNode = newNode
    }
    result.end = currentNode
    return result
}

internal object KoneGCLinkedMeldableListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneGCLinkedMeldableList<Element> = KoneGCLinkedMeldableList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> = KoneGCLinkedMeldableList.generate(number, builder)
}

public fun KoneGCLinkedMeldableList.Companion.producer(): KoneResizableMutableNoddedListProducer = KoneGCLinkedMeldableListProducer

public object KoneGCLinkedMeldableListCopier {
    public fun <Element> copy(base: KoneGCLinkedMeldableList<Element>): KoneGCLinkedMeldableList<Element> {
        if (base.isDisposed) disposedInstanceException()
        val result = KoneGCLinkedMeldableList<Element>()
        var currentNode = base.start ?: return result
        var newCurrentNode = KoneGCLinkedMeldableList.Node(currentNode.element)
        result.start = newCurrentNode
        while (currentNode._nextNode != null) {
            val nextNode = currentNode._nextNode!!
            val newNextNode = KoneGCLinkedMeldableList.Node(nextNode.element)
            newCurrentNode._nextNode = newNextNode
            newNextNode._previousNode = newCurrentNode
            currentNode = nextNode
            newCurrentNode = newNextNode
        }
        result.end = newCurrentNode
        return result
    }
}

internal val KoneGCLinkedMeldableListCopierErased: (KoneGCLinkedMeldableList<Any?>) -> KoneGCLinkedMeldableList<Any?> =
    { base -> KoneGCLinkedMeldableListCopier.copy(base) }

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneGCLinkedMeldableList.Companion.copier(): (KoneGCLinkedMeldableList<Element>) -> KoneGCLinkedMeldableList<Element> =
    KoneGCLinkedMeldableListCopierErased as (KoneGCLinkedMeldableList<Element>) -> KoneGCLinkedMeldableList<Element>

public object KoneGCLinkedMeldableListMelder {
    public fun <Element> meld(first: KoneGCLinkedMeldableList<Element>, second: KoneGCLinkedMeldableList<Element>): KoneGCLinkedMeldableList<Element> {
        if (first.isDisposed || second.isDisposed) disposedInstanceException()
        when {
            first.size == 0u -> return second
            second.size == 0u -> return first
        }
        val endOfFirst = first.end!!
        val startOfSecond = second.start!!
        endOfFirst._nextNode = startOfSecond
        startOfSecond._previousNode = endOfFirst
        first.end = second.end
        second.start = null
        second.end = null
        second.isDisposed = true
        return first
    }
}

internal val KoneGCLinkedMeldableListMelderErased: (KoneGCLinkedMeldableList<Any?>, KoneGCLinkedMeldableList<Any?>) -> KoneGCLinkedMeldableList<Any?> =
    { first, second -> KoneGCLinkedMeldableListMelder.meld(first, second) }

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneGCLinkedMeldableList.Companion.melder(): (KoneGCLinkedMeldableList<Element>, KoneGCLinkedMeldableList<Element>) -> KoneGCLinkedMeldableList<Element> =
    KoneGCLinkedMeldableListMelderErased as (KoneGCLinkedMeldableList<Element>, KoneGCLinkedMeldableList<Element>) -> KoneGCLinkedMeldableList<Element>

internal class KoneGCLinkedMeldableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneGCLinkedMeldableList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneGCLinkedMeldableList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneGCLinkedMeldableList<E> =
        KoneGCLinkedMeldableList.generate(size, initializer)
}
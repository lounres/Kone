/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.indices
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.list.serializers.KoneListImplementationDescriptor
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneTwoThreeTreeList(): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(rootHolder = null, firstNode = null, lastNode = null, size = 0u)

@PublishedApi
internal fun <Element> KoneTwoThreeTreeList(elements: KoneArraySettableList<Element>): KoneTwoThreeTreeList<Element> {
    if (elements.size == 0u) return KoneTwoThreeTreeList()

    val result = KoneTwoThreeTreeList<Element>(size = elements.size)
    val nodes = KoneArray.generate(elements.size) { KoneTwoThreeTreeList.Node(elements[it]) }
    for (i in nodes.indices) {
        if (i > 0u) nodes[i].previousNode = nodes[i-1u]
        if (i < nodes.lastIndex) nodes[i].nextNode = nodes[i+1u]
    }
    val tree = result.createTree(nodes)
    result.rootHolder = tree
    result.firstNode = nodes.first()
    result.lastNode = nodes.last()
    return result
}

public inline fun <Element> KoneTwoThreeTreeList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(KoneArraySettableList.generate(size, initializer))

public inline fun <Element> KoneTwoThreeTreeList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(KoneArraySettableList.generate(indices, initializer))

public inline fun <Element> KoneTwoThreeTreeList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(KoneArraySettableList.induce(size, initialElement, inducer))

public inline fun <Element> KoneTwoThreeTreeList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(KoneArraySettableList.induce(indices, initialElement, inducer))

internal object KoneTwoThreeTreeListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneTwoThreeTreeList<Element> = KoneTwoThreeTreeList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneTwoThreeTreeList<Element> =
        KoneTwoThreeTreeList.generate(size = number, initializer = builder)
}

public fun KoneTwoThreeTreeList.Companion.producer(): KoneResizableMutableNoddedListProducer = KoneTwoThreeTreeListProducer

internal class KoneTwoThreeTreeListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
): KoneIterableSerializerTemplate<E, KoneTwoThreeTreeList<E>>() {
    override val descriptor: SerialDescriptor =
        KoneListImplementationDescriptor(
            implementationName = "KoneTwoThreeTreeList",
            elementDescriptor = elementSerializer.descriptor,
        )
    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneTwoThreeTreeList<E> =
        KoneTwoThreeTreeList.generate(size, initializer)
}
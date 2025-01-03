/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneArray
import dev.lounres.kone.collections.indices
import dev.lounres.kone.collections.lastIndex
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last


public fun <Element> KoneTwoThreeTreeList(): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(rootHolder = null, firstNode = null, lastNode = null, size = 0u)

@PublishedApi
internal fun <Element> KoneTwoThreeTreeList(elements: KoneArraySettableList<Element>): KoneTwoThreeTreeList<Element> {
    if (elements.size == 0u) return KoneTwoThreeTreeList()
    
    val nodes = KoneArray(elements.size) { KoneTwoThreeTreeList.Node(elements[it]) }
    for (i in nodes.indices) {
        if (i > 0u) nodes[i].previousNode = nodes[i-1u]
        if (i < nodes.lastIndex) nodes[i].nextNode = nodes[i+1u]
    }
    val holdersRegistry = KoneArrayFixedCapacityList<KoneTwoThreeTreeList.NodeHolder<Element>>(nodes.size)
    val tree = KoneTwoThreeTreeList.createTree(nodes, holdersRegistry)
    return KoneTwoThreeTreeList(
        rootHolder = tree,
        firstNode = nodes.first(),
        lastNode = nodes.last(),
        size = elements.size,
    ).also {
        for (holder in holdersRegistry) holder.tree = it
    }
}

public inline fun <Element> KoneTwoThreeTreeList(size: UInt, initializer: (index: UInt) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList(KoneArraySettableList(size) { initializer(it) })

public object KoneTwoThreeTreeListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneTwoThreeTreeList<Element> = KoneTwoThreeTreeList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneTwoThreeTreeList<Element> =
        KoneTwoThreeTreeList(size = number, initializer = builder)
}
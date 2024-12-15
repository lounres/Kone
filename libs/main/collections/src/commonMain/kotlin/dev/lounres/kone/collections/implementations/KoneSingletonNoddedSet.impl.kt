/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonNoddedSet<Element, ElementContext: Equality<Element>>(
    val singleElement: Element,
    open val elementContext: ElementContext,
) : KoneNoddedSet<Element> {
    override val size: UInt get() = 1u
    override fun contains(element: Element): Boolean = elementContext { singleElement eq element }
    
    private val singleNode = Node()
    
    override val nodes: KoneIterable<KoneSetNode<Element>> get() = KoneSingletonSettableLinearIterable(singleNode)
    
    override fun iterator(): KoneIterator<Element> = KoneSingletonSettableLinearIterator(singleElement = singleElement)

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = singleElement.hashCode()
    override fun equals(other: Any?): Boolean = this === other
    
    inner class Node: KoneSetNode<Element> {
        override val isDetached: Boolean get() = false
        
        override val element: Element get() = singleElement
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
@PublishedApi
internal class KoneSingletonNoddedReifiedSet<Element, ElementContext: ReifiedEquality<Element>>(
    singleElement: Element,
    override val elementContext: ElementContext,
) : KoneSingletonNoddedSet<Element, ElementContext>(
    singleElement = singleElement,
    elementContext = elementContext,
), KoneNoddedReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementContext && elementContext { singleElement eq element }
}
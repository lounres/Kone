/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


internal class KoneSingletonNoddedSet<Element, ElementContext: Equality<Element>>(
    val singleElement: Element,
    override val elementContext: ElementContext,
) : KoneNoddedSet<Element>, KoneSetWithContext<Element, ElementContext> {
    override val size: UInt get() = 1u
    override fun contains(element: Element): Boolean = elementContext { singleElement eq element }
    
    private val singleNode = Node()
    
    override val nodes: KoneIterable<KoneSetNode<Element>> get() = KoneSingletonSettableLinearIterable(singleNode)
    
    override fun iterator(): KoneIterator<Element> = KoneSingletonSettableLinearIterator(singleElement = singleElement)

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = singleElement.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (other.size != 1u) return false

        return singleElement in other
    }
    
    inner class Node: KoneSetNode<Element> {
        override val element: Element get() = singleElement
    }
}
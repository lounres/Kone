/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.singleton

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.set.KoneNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneNoddedSet
import dev.lounres.kone.collections.set.KoneNoddedSetIterator
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSetIterator
import dev.lounres.kone.collections.set.KoneSetNode
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonNoddedSet<Element>(
    val singleElement: Element,
    open val elementEquality: Equality<Element>,
) : KoneNoddedSet<Element> {
    internal val singleNode = Node(this)
    
    override val size: UInt get() = 1u
    override fun contains(element: Element): Boolean = with(elementEquality) { singleElement eq element }
    override fun nodeOfOrNull(element: Element): KoneSetNode<Element>? =
        if (elementEquality { singleElement eq element }) singleNode else null
    override fun nodeOf(element: Element): KoneSetNode<Element> =
        if (elementEquality { singleElement eq element }) singleNode
        else noCorrespondingSetNodeException()
    
    override val nodesView: KoneReifiedSet<KoneSetNode<Element>> = Nodes(this)
    
    override fun iterator(): KoneNoddedSetIterator<Element> = Iterator(this)

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = singleElement.hashCode()
    override fun equals(other: Any?): Boolean = this === other
    
    internal class Node<Element>(
        val set: KoneSingletonNoddedSet<Element>,
    ) : KoneSetNode<Element> {
        override val isDetached: Boolean get() = false
        
        override val element: Element get() = set.singleElement
    }
    
    internal class Nodes<Element>(
        val set: KoneSingletonNoddedSet<Element>,
    ) : KoneReifiedSet<KoneSetNode<Element>> {
        override val size: UInt get() = 1u
        override fun contains(element: KoneSetNode<Element>): Boolean = element === set.singleNode
        
        override fun iterator(): KoneSetIterator<KoneSetNode<Element>> = Iterator(set)
        
        internal class Iterator<Element>(
            val set: KoneSingletonNoddedSet<Element>,
            var currentlyBeforeSingleElement: Boolean = true,
        ) : KoneSetIterator<Node<Element>> {
            override fun hasNext(): Boolean = currentlyBeforeSingleElement
            override fun getNext(): Node<Element> =
                if (!hasNext()) noNextElementInIteratorException()
                else set.singleNode
            override fun moveNext() {
                if (!hasNext()) noNextElementInIteratorException()
                currentlyBeforeSingleElement = false
            }
        }
    }
    
    internal class Iterator<Element>(
        val set: KoneSingletonNoddedSet<Element>,
        var currentlyBeforeSingleElement: Boolean = true,
    ) : KoneNoddedSetIterator<Element> {
        override fun hasNext(): Boolean = currentlyBeforeSingleElement
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else set.singleElement
        override fun getNextNode(): KoneSetNode<Element> =
            if (!hasNext()) noNextElementInIteratorException()
            else set.singleNode
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentlyBeforeSingleElement = false
        }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
@PublishedApi
internal class KoneSingletonNoddedReifiedSet<Element>(
    singleElement: Element,
    val elementReification: Reification<Element>,
    override val elementEquality: Equality<Element>,
) : KoneSingletonNoddedSet<Element>(
    singleElement = singleElement,
    elementEquality = elementEquality,
), KoneNoddedReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && elementEquality { singleElement eq element }
}
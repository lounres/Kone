/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.singleton

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.KoneSetIterator
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneSingletonSet<Element>(
    val singleElement: Element,
    open val elementEquality: Equality<Element>,
) : KoneSet<Element> {
    override val size: UInt get() = 1u
    override fun contains(element: Element): Boolean = context(elementEquality) { singleElement eq element }
    
    override fun iterator(): KoneSetIterator<Element> = Iterator(this)

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = singleElement.hashCode()
    override fun equals(other: Any?): Boolean = this === other
    
    internal class Iterator<Element>(
        val set: KoneSingletonSet<Element>,
        var currentlyBeforeSingleElement: Boolean = true,
    ) : KoneSetIterator<Element> {
        override fun hasNext(): Boolean = currentlyBeforeSingleElement
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else set.singleElement
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentlyBeforeSingleElement = false
        }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
@PublishedApi
internal class KoneSingletonReifiedSet<Element>(
    singleElement: Element,
    val elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
) : KoneSingletonSet<Element>(
    singleElement = singleElement,
    elementEquality = elementEquality,
), KoneReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && context(elementEquality) { singleElement eq element }
}
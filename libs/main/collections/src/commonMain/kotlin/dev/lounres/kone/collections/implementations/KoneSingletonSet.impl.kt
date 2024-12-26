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
internal open class KoneSingletonSet<Element>(
    val singleElement: Element,
    open val elementContext: Equality<Element>,
) : KoneSet<Element> {
    override val size: UInt get() = 1u
    override fun contains(element: Element): Boolean = elementContext { singleElement eq element }
    
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
    override val elementContext: ReifiedEquality<Element>,
) : KoneSingletonSet<Element>(
    singleElement = singleElement,
    elementContext = elementContext,
), KoneReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementContext && elementContext { singleElement eq element }
}
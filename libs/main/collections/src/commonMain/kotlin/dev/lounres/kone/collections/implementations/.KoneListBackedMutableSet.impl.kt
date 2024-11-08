/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.firstIndexOf
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat


//@Serializable(with = KoneMutableListBackedSetWithContextSerializer::class)
public class KoneMutableListBackedSet<Element, ElementContext: Equality<Element>> @PublishedApi internal constructor(
    override val elementContext: ElementContext,
    internal val backingList: KoneMutableList<Element>,
) : KoneMutableSet<Element>, KoneMutableSetWithContext<Element, ElementContext> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: Element): Boolean = elementContext { element in backingList }

    override fun add(element: Element) {
        if (elementContext { element !in backingList }) backingList.add(element)
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }

    override fun removeAll() {
        backingList.removeAll()
    }

    override fun remove(element: Element) {
        backingList.removeAt(elementContext { backingList.firstIndexOf(element) })
    }
    override fun removeAllThat(predicate: (element: Element) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<Element> = backingList.iterator()

    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('[')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext())
        for (element in iterator) {
            append(", ")
            append(element)
        }
        append(']')
    }
}
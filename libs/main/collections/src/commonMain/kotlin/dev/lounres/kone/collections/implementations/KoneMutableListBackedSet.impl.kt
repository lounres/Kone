/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat
import kotlinx.serialization.Serializable


//@Serializable(with = KoneMutableListBackedSetWithContextSerializer::class)
public class KoneMutableListBackedSet<E, EC: Equality<E>> @PublishedApi internal constructor(
    override val elementContext: EC,
    internal val backingList: KoneMutableList<E>,
) : KoneMutableSet<E>, KoneMutableSetWithContext<E, EC> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = elementContext { element in backingList }

    override fun add(element: E) {
        if (elementContext { element !in backingList }) backingList.add(element)
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> E) {
        repeat(number) { add(builder(it)) }
    }

    override fun removeAll() {
        backingList.removeAll()
    }

    override fun remove(element: E) {
        backingList.removeAt(elementContext { backingList.firstIndexOf(element) })
    }
    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<E> = backingList.iterator()

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
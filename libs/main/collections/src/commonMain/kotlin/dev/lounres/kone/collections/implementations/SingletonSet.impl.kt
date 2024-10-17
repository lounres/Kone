/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


internal class SingletonSet<E, EC: Equality<E>>(
    val singleElement: E,
    override val elementContext: EC,
) : KoneIterableSet<E>, KoneSetWithContext<E, EC> {
    override val size: UInt = 1u
    override fun contains(element: E): Boolean = elementContext { singleElement eq element }

    override fun iterator(): KoneIterator<E> = SingletonIterator(singleElement = singleElement)

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = singleElement.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (other.size != 1u) return false

        return singleElement in other
    }
}
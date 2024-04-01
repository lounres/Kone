/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


internal open class EmptyKoneIterableSetTemplate<E> : KoneIterableSet<E>, KoneSetWithContext<E, Equality<E>> {
    override val elementContext: Equality<E> = defaultEquality()

    override val size: UInt = 0u

    override fun contains(element: @UnsafeVariance E): Boolean = false

    override fun iterator(): KoneIterator<Nothing> = EmptyKoneIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneSet<*> && other.isEmpty()
}

internal object EmptyKoneIterableSet : EmptyKoneIterableSetTemplate<Nothing>()
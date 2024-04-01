/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneDefaultList
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.isEmpty


internal open class EmptyKoneIterableListTemplate<E> : KoneIterableList<E>, KoneDefaultList<E> {
    override val size: UInt = 0u

    override fun contains(element: @UnsafeVariance E): Boolean = false

    override fun get(index: UInt): Nothing = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")

    override fun iterator(): KoneLinearIterator<Nothing> = EmptyKoneIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean = other is KoneList<*> && other.isEmpty()
}

internal object EmptyKoneIterableList : EmptyKoneIterableListTemplate<Nothing>()
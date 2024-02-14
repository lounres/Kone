/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.isEmpty


//internal data object EmptyKoneIterableList: KoneIterableList<Nothing> {
//    override val size: UInt = 0u
//
//    override fun get(index: UInt): Nothing = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
//
//    override fun iterator(): KoneLinearIterator<Nothing> = EmptyKoneIterator
//
//    override fun toString(): String = "[]"
//}

internal open class EmptyKoneIterableListTemplate<out E>: KoneIterableList<E> {
    override val size: UInt = 0u

    override fun get(index: UInt): Nothing = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")

    override fun iterator(): KoneLinearIterator<Nothing> = EmptyKoneIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean = other is KoneList<*> && other.isEmpty()
}

internal object EmptyKoneIterableList: EmptyKoneIterableListTemplate<Nothing>()
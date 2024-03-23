/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.common.KoneIterableSet
import dev.lounres.kone.collections.common.KoneSet
import dev.lounres.kone.collections.common.isEmpty
import dev.lounres.kone.collections.implementations.EmptyKoneIterator


//internal data object EmptyKoneIterableSet: KoneIterableSet<Nothing> {
//    override val size: UInt = 0u
//
//    override fun contains(element: Nothing): Boolean = false
//
//    override fun iterator(): KoneIterator<Nothing> = EmptyKoneIterator
//
//    override fun toString(): String = "[]"
//}

internal open class EmptyKoneIterableSetTemplate<out E>: KoneIterableSet<E> {
    override val size: UInt = 0u

    override fun contains(element: @UnsafeVariance E): Boolean = false

    override fun iterator(): KoneIterator<Nothing> = EmptyKoneIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneSet<*> && other.isEmpty()
}

internal object EmptyKoneIterableSet: EmptyKoneIterableSetTemplate<Nothing>()
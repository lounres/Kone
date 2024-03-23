/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.contextual.KoneContextualIterableSet
import dev.lounres.kone.collections.contextual.KoneContextualSet
import dev.lounres.kone.collections.contextual.isEmpty
import dev.lounres.kone.collections.implementations.EmptyKoneIterator
import dev.lounres.kone.comparison.Equality


//internal data object EmptyKoneIterableSet: KoneIterableSet<Nothing> {
//    override val size: UInt = 0u
//
//    override fun contains(element: Nothing): Boolean = false
//
//    override fun iterator(): KoneIterator<Nothing> = EmptyKoneIterator
//
//    override fun toString(): String = "[]"
//}

internal open class EmptyKoneContextualIterableSetTemplate<out E>: KoneContextualIterableSet<E, Equality<E>> {
    override val size: UInt = 0u

    context(Equality<E>)
    override fun contains(element: @UnsafeVariance E): Boolean = false

    override fun iterator(): KoneIterator<Nothing> = EmptyKoneIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneContextualSet<*, *> && other.isEmpty()
}

internal object EmptyKoneContextualIterableSet: EmptyKoneContextualIterableSetTemplate<Nothing>()
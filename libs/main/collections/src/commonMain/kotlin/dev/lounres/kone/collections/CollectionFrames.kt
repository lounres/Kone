/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


internal interface KoneDefaultList<out E> : KoneList<E> {
    override fun contains(element: @UnsafeVariance E): Boolean =
        indexThat { _, currentElement -> currentElement == element } != size

    override fun indexOf(element: @UnsafeVariance E): UInt =
        indexThat { _, currentElement -> currentElement == element }

    override fun lastIndexOf(element: @UnsafeVariance E): UInt =
        lastIndexThat { _, currentElement -> currentElement == element }
}

internal interface KoneListWithContext<E> : KoneList<E> {
    val context: Equality<E>

    override fun contains(element: @UnsafeVariance E): Boolean =
        indexThat { _, currentElement -> context { currentElement eq element } } != size

    override fun indexOf(element: @UnsafeVariance E): UInt =
        indexThat { _, currentElement -> context { currentElement eq element } }

    override fun lastIndexOf(element: @UnsafeVariance E): UInt =
        lastIndexThat { _, currentElement -> context { currentElement eq element } }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


internal class SingletonList<E, out EC: Equality<E>>(
    val singleElement: E,
    override val elementContext: EC,
) : KoneIterableList<E>, KoneListWithContext<E, EC> {
    override val size: UInt = 1u
    override fun contains(element: E): Boolean = elementContext { element eq singleElement }

    override fun get(index: UInt): E {
        if (index >= 1u) indexException(index, size)
        return singleElement
    }

    override fun indexOf(element: E): UInt =
        if (elementContext { element eq singleElement }) 0u
        else 1u

    override fun lastIndexOf(element: E): UInt =
        if (elementContext { element eq singleElement }) 0u
        else UInt.MAX_VALUE

    override fun iterator(): KoneLinearIterator<E> = SingletonIterator(singleElement = singleElement)
}
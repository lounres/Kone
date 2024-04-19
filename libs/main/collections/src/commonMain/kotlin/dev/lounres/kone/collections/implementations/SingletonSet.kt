/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneSetWithContext
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


internal class SingletonSet<E, EC: Equality<E>>(
    val singleElement: E,
    override val elementContext: EC,
) : KoneIterableSet<E>, KoneSetWithContext<E, EC> {
    override val size: UInt = 1u
    override fun contains(element: E): Boolean = elementContext { singleElement eq element }

    override fun iterator(): KoneIterator<E> = SingletonIterator(singleElement = singleElement)
}
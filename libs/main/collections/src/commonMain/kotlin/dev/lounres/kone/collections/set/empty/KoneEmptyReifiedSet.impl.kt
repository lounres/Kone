/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.empty

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterable.isEmpty
import dev.lounres.kone.collections.set.KoneNoddedSetIterator
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneEmptyReifiedSetTemplate<Element> : KoneReifiedSet<Element> {
    override val size: UInt get() = 0u

    override fun contains(element: Element): Boolean = false

    override fun iterator(): KoneNoddedSetIterator<Element> = KoneEmptyNoddedSetIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneSet<*> && other.isEmpty()
}

internal object KoneEmptyReifiedSet : KoneEmptyReifiedSetTemplate<Nothing>()
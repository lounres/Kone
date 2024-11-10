/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneEmptyNoddedSetTemplate<Element> : KoneNoddedSet<Element>, KoneSetWithContext<Element, Equality<Element>> {
    override val elementContext: Equality<Element> get() = defaultEquality()

    override val size: UInt get() = 0u
    
    override val nodes: KoneIterable<KoneSetNode<Element>> get() = KoneEmptySettableLinearIterable

    override fun contains(element: @UnsafeVariance Element): Boolean = false

    override fun iterator(): KoneIterator<Nothing> = KoneEmptySettableLinearIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneSet<*> && other.isEmpty()
}

internal object KoneEmptyNoddedSet : KoneEmptyNoddedSetTemplate<Nothing>()
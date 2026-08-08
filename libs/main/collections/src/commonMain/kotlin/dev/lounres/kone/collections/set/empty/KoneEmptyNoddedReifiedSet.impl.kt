/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.empty

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.noCorrespondingSetNodeException
import dev.lounres.kone.collections.set.*


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneEmptyNoddedReifiedSetTemplate<Element> : KoneNoddedReifiedSet<Element> {
    override val size: UInt get() = 0u
    
    override val nodesView: KoneReifiedSet<KoneSetNode<Element>> get() = KoneEmptyReifiedSet

    override fun contains(element: Element): Boolean = false
    override fun nodeOfOrNull(element: Element): KoneSetNode<Element>? = null
    override fun nodeOf(element: Element): KoneSetNode<Element> = noCorrespondingSetNodeException()

    override fun iterator(): KoneNoddedSetIterator<Element> = KoneEmptyNoddedSetIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is KoneSet<*> && other.isEmpty()
}

internal object KoneEmptyNoddedReifiedSet : KoneEmptyNoddedReifiedSetTemplate<Nothing>()
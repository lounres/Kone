/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneListNode
import dev.lounres.kone.collections.KoneNoddedList
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.isEmpty


internal open class KoneEmptyNoddedListTemplate<Element> : KoneNoddedList<Element> {
    override val size: UInt = 0u
    
    override fun getNode(index: UInt): KoneListNode<Element> = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
    override fun get(index: UInt): Element = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")

    override fun iterator(): KoneLinearIterator<Nothing> = KoneEmptyLinearIterator
    override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> =
        if (index >= 0u) indexException(index, 0u)
        else KoneEmptyLinearIterator

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean = other is KoneList<*> && other.isEmpty()
}

internal object KoneEmptyNoddedList : KoneEmptyNoddedListTemplate<Nothing>()
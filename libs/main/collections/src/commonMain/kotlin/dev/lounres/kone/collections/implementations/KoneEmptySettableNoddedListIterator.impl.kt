/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneSettableListNode
import dev.lounres.kone.collections.KoneSettableNoddedListIterator


internal object KoneEmptySettableNoddedListIterator: KoneSettableNoddedListIterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun getNext(): Nothing = throw NoSuchElementException("Empty iterator has no next element")
    override fun getNextNode(): KoneSettableListNode<Nothing> = throw NoSuchElementException("Empty iterator has no next node")
    override fun moveNext() = throw NoSuchElementException("Empty iterator has no next element")
    override fun nextIndex(): UInt = throw NoSuchElementException("Empty iterator has no next index")
    override fun setNext(element: Nothing) = throw NoSuchElementException("Empty iterator has no next element")

    override fun hasPrevious(): Boolean = false
    override fun getPrevious(): Nothing = throw NoSuchElementException("Empty iterator has no previous element")
    override fun getPreviousNode(): KoneSettableListNode<Nothing> = throw NoSuchElementException("Empty iterator has no previous node")
    override fun movePrevious() = throw NoSuchElementException("Empty iterator has no previous element")
    override fun previousIndex(): UInt = throw NoSuchElementException("Empty iterator has no previous index")
    override fun setPrevious(element: Nothing) = throw NoSuchElementException("Empty iterator has no previous element")
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneSettableListNode
import dev.lounres.kone.collections.KoneSettableNoddedListIterator
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.noPreviousElementInIteratorException


internal object KoneEmptySettableNoddedListIterator: KoneSettableNoddedListIterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun getNext(): Nothing = noNextElementInIteratorException()
    override fun getNextNode(): KoneSettableListNode<Nothing> = noNextElementInIteratorException()
    override fun moveNext() = noNextElementInIteratorException()
    override fun nextIndex(): UInt = noNextElementInIteratorException()
    override fun setNext(element: Nothing) = noNextElementInIteratorException()

    override fun hasPrevious(): Boolean = false
    override fun getPrevious(): Nothing = noPreviousElementInIteratorException()
    override fun getPreviousNode(): KoneSettableListNode<Nothing> = noPreviousElementInIteratorException()
    override fun movePrevious() = noPreviousElementInIteratorException()
    override fun previousIndex(): UInt = noPreviousElementInIteratorException()
    override fun setPrevious(element: Nothing) = noPreviousElementInIteratorException()
}
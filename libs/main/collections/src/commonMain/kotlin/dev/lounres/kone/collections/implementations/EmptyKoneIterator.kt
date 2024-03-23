/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator

internal data object EmptyKoneIterator: KoneLinearIterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun getNext(): Nothing = throw NoSuchElementException("Empty iterator has no next element")
    override fun moveNext() = throw NoSuchElementException("Empty iterator has no next element")
    override fun nextIndex(): UInt = throw NoSuchElementException("Empty iterator has no next index")

    override fun hasPrevious(): Boolean = false
    override fun getPrevious(): Nothing = throw NoSuchElementException("Empty iterator has no previous element")
    override fun movePrevious() = throw NoSuchElementException("Empty iterator has no previous element")
    override fun previousIndex(): UInt = throw NoSuchElementException("Empty iterator has no previous index")
}
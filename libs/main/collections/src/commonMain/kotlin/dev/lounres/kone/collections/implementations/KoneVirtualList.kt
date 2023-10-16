/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException


public class KoneVirtualList<out E>(override val size: UInt, private val generator: (UInt) -> E): KoneIterableList<E> {
    override fun get(index: UInt): E = generator(index)

    override fun iterator(): KoneLinearIterator<E> = Iterator(size = size, generator = generator)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<E> = Iterator(size = size, currentIndex = index, generator = generator)

    internal class Iterator<E>(val size: UInt, var currentIndex: UInt = 0u, val generator: (UInt) -> E): KoneLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        // TODO: Move `hasX`, `moveX`, and `XIndex` methods to separate interface. They are the same as for KoneResizableArrayList.
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return generator(currentIndex)
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return generator(currentIndex - 1u)
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
    }
}
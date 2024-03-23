/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneSettableLinearIterator
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualList
import dev.lounres.kone.collections.contextual.KoneContextualMutableArray
import dev.lounres.kone.collections.contextual.KoneContextualSettableIterableList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


public class KoneContextualLazyList<E>(override val size: UInt, private val generator: (UInt) -> E): KoneContextualSettableIterableList<E, Equality<E>> {
    private val buffer: KoneContextualMutableArray<Option<E>> = KoneContextualMutableArray(size) { None }

    override fun get(index: UInt): E = buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    override fun set(index: UInt, element: E) {
        buffer[index] = Some(element)
    }

    override fun iterator(): KoneSettableLinearIterator<E> = Iterator(size = size, buffer = buffer, generator = generator)
    override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> = Iterator(size = size, currentIndex = index, buffer = buffer, generator = generator)

    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneContextualList<*, *>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneContextualLazyList<*> ->
                for (i in 0u..<size) {
                    if (this[i] != other[i]) return false
                }
            is KoneContextualIterableList<*, *> -> {
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    if (this[i] != otherIterator.getAndMoveNext()) return false
                }
            }
            else ->
                for (i in 0u..<size) {
                    if (this[i] != other[i]) return false
                }
        }

        return true
    }

    internal class Iterator<E>(val size: UInt, var currentIndex: UInt = 0u, val buffer: KoneContextualMutableArray<Option<E>>, val generator: (UInt) -> E): KoneSettableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        // TODO: Move `hasX`, `moveX`, and `XIndex` methods to separate interface. They are the same as for KoneResizableArrayList.
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return buffer[currentIndex].orElse { generator(currentIndex).also { buffer[currentIndex] = Some(it) } }
        }
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, size)
            buffer[currentIndex] = Some(element)
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return buffer[currentIndex - 1u].orElse { generator(currentIndex - 1u).also { buffer[currentIndex - 1u] = Some(it) } }
        }
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, size)
            buffer[currentIndex] = Some(element)
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
    }
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneSettableLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse
import dev.lounres.kone.repeat


//@Serializable(with = KoneLazyListWithContextSerializer::class)
public class KoneLazyList<Element>(
    override val size: UInt,
    private val generator: (index: UInt) -> Element,
) : KoneSettableList<Element> {
    private val buffer: KoneMutableArray<Option<Element>> = KoneMutableArray(size) { None }

    override fun get(index: UInt): Element = buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    override fun set(index: UInt, element: Element) {
        buffer[index] = Some(element)
    }

    override fun iterator(): KoneSettableLinearIterator<Element> = Iterator(size = size, buffer = buffer, generator = generator)
    override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> = Iterator(size = size, currentIndex = index, buffer = buffer, generator = generator)

    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneLazyList<*> ->
                repeat(size) {
                    if (this[it] != other[it]) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this[i] != otherIterator.getAndMoveNext()) return false
                }
            }
        }

        return true
    }

    internal class Iterator<E>(val size: UInt, var currentIndex: UInt = 0u, val buffer: KoneMutableArray<Option<E>>, val generator: (UInt) -> E): KoneSettableLinearIterator<E> {
        init {
            if (currentIndex > size) indexOutOfBoundsException(currentIndex, size)
        }
        // TODO: Move `hasX`, `moveX`, and `XIndex` methods to separate interface. They are the same as for KoneResizableArrayList.
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return buffer[currentIndex].orElse { generator(currentIndex).also { buffer[currentIndex] = Some(it) } }
        }
        override fun setNext(element: E) {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            buffer[currentIndex] = Some(element)
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            return buffer[currentIndex - 1u].orElse { generator(currentIndex - 1u).also { buffer[currentIndex - 1u] = Some(it) } }
        }
        override fun setPrevious(element: E) {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            buffer[currentIndex] = Some(element)
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, size)
    }
}
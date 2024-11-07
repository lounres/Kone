/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*


//@Serializable(with = KoneVirtualListWithContextSerializer::class)
public class KoneVirtualList<Element>(
    override val size: UInt,
    private val generator: (index: UInt) -> Element
) : KoneList<Element> {
    override fun get(index: UInt): Element = generator(index)

    override fun iterator(): KoneLinearIterator<Element> = Iterator(size = size, generator = generator)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> = Iterator(size = size, currentIndex = index, generator = generator)

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
            is KoneVirtualList<*> ->
                for (i in 0u..<size) {
                    if (this[i] != other[i]) return false
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

    internal class Iterator<E>(val size: UInt, var currentIndex: UInt = 0u, val generator: (UInt) -> E): KoneLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) indexException(currentIndex, size)
            return generator(currentIndex)
        }
        override fun moveNext() {
            if (!hasNext()) indexException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) indexException(currentIndex, size)
            return generator(currentIndex - 1u)
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, size)
    }
}
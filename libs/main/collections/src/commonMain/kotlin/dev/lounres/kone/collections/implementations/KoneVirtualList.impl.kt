/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.comparison.Equality
import kotlinx.serialization.Serializable


@Serializable(with = KoneVirtualListWithContextSerializer::class)
public class KoneVirtualList<E, EC: Equality<E>>(
    override val size: UInt,
    override val elementContext: EC,
    private val generator: (index: UInt) -> E
) : KoneListWithContext<E, EC>, KoneIterableList<E> {
    override fun get(index: UInt): E = generator(index)

    override fun iterator(): KoneLinearIterator<E> = Iterator(size = size, generator = generator)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<E> = Iterator(size = size, currentIndex = index, generator = generator)

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
            is KoneVirtualList<*, *> ->
                for (i in 0u..<size) {
                    if (this[i] != other[i]) return false
                }
            is KoneIterableList<*> -> {
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
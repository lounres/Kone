/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.repeat


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneSettableArrayListWithContextSerializer::class)
/*@JvmInline*/ // FIXME: Await support of `equals` and `hashCode` methods support in value classes and multifield value classes to make the class be value class
public /*value*/ class KoneArraySettableList<Element> @PublishedApi internal constructor(
    private val data: KoneMutableArray<Any?>,
) : KoneSettableList<Element>, Disposable {
    override val size: UInt get() = data.size

    override fun dispose() {
        repeat(size) { data[it] = null }
    }

    override fun get(index: UInt): Element {
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index] as Element
    }

    override fun set(index: UInt, element: Element) {
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index] = element
    }

    override fun iterator(): KoneSettableLinearIterator<Element> = Iterator(data)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> = Iterator(data, index)

    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneArraySettableList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
            }
        }

        return true
    }

    internal class Iterator<Element>(val data: KoneMutableArray<Any?>, var currentIndex: UInt = 0u): KoneSettableLinearIterator<Element> {
        init {
            if (currentIndex > data.size) indexOutOfBoundsException(currentIndex, data.size)
        }
        override fun hasNext(): Boolean = currentIndex < data.size
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, data.size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            data[currentIndex] = element
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, data.size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            data[currentIndex - 1u] = element
        }
    }
}
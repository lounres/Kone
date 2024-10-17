/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import kotlinx.serialization.Serializable


@Suppress("UNCHECKED_CAST")
@Serializable(with = KoneSettableArrayListWithContextSerializer::class)
/*@JvmInline*/ // FIXME: Await support of `equals` and `hashCode` methods support in value classes and multifield value classes to make the class be value class
public /*value*/ class KoneSettableArrayList<E, out EC: Equality<E>> @PublishedApi internal constructor(
    private val data: KoneMutableArray<Any?>,
    override val elementContext: EC,
) : KoneListWithContext<E, EC>, KoneSettableIterableList<E>, Disposable {
    override val size: UInt get() = data.size

    override fun dispose() {
        for (index in 0u ..< size) data[index] = null
    }

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return data[index] as E
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        data[index] = element
    }

    override fun iterator(): KoneSettableLinearIterator<E> = Iterator(data)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> = Iterator(data, index)

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
            is KoneSettableArrayList<*, *> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
                }
            is KoneIterableList<*> -> {
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
            }
            else ->
                for (i in 0u..<size) {
                    if (this.data[i] != other[i]) return false
                }
        }

        return true
    }

    internal class Iterator<E>(val data: KoneMutableArray<Any?>, var currentIndex: UInt = 0u): KoneSettableLinearIterator<E> {
        init {
            if (currentIndex > data.size) indexException(currentIndex, data.size)
        }
        override fun hasNext(): Boolean = currentIndex < data.size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, data.size)
            return data[currentIndex] as E
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, data.size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, data.size)
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, data.size)
            data[currentIndex] = element
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, data.size)
            return data[currentIndex - 1u] as E
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, data.size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, data.size)
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, data.size)
            data[currentIndex - 1u] = element
        }
    }
}
/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


@Suppress("UNCHECKED_CAST")
@JvmInline
public value class KoneSettableArrayList<E> internal constructor(private val data: KoneMutableArray<Any?>): KoneSettableIterableList<E> {
    override val size: UInt get() = data.size

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
        if (size >= 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
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
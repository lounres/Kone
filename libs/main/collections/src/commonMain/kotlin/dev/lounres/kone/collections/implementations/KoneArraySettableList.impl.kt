/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import kotlin.jvm.JvmInline


/**
 * Represents a usual (settable) array of elements of type [Element].
 *
 * # Implementation details
 *
 * This implementation just holds an array (of fixed size)
 * and proxies all operations straight to [KoneMutableArray].
 * The only difference between it and [KoneMutableArray] is that
 * the last one must have reified type parameter while the first one must not.
 *
 * ## Time complexity of operations
 *
 * | Operation                                                          | Worst case    | Average       |
 * |--------------------------------------------------------------------|---------------|---------------|
 * | [size]                                                             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [get]                                                              | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [set]                                                              | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator]                                                         | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iteratorFrom]                                                     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[hasNext][KoneSettableLinearIterator.hasNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[hasPrevious][KoneSettableLinearIterator.hasPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[getNext][KoneSettableLinearIterator.getNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[getPrevious][KoneSettableLinearIterator.getPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[moveNext][KoneSettableLinearIterator.moveNext]           | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[movePrevious][KoneSettableLinearIterator.movePrevious]   | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[setNext][KoneSettableLinearIterator.setNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[setPrevious][KoneSettableLinearIterator.setPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[nextIndex][KoneSettableLinearIterator.nextIndex]         | \(\Theta(1)\) | \(\Theta(1)\) |
 * | iterator.[previousIndex][KoneSettableLinearIterator.previousIndex] | \(\Theta(1)\) | \(\Theta(1)\) |
 *
 * @usesMathJax
 */
@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneSettableArrayListWithContextSerializer::class)
@JvmInline
@OptIn(DelicateCollectionsInheritanceAPI::class)
public value class KoneArraySettableList<Element> @PublishedApi internal constructor(
    private val data: KoneMutableArray<Any?>,
) : KoneSettableList<Element> {
    override val size: UInt get() = data.size

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
    
    // FIXME: Await support of `equals` and `hashCode` methods support in value classes and multifield value classes to make the class be value class
//    override fun hashCode(): Int {
//        var hashCode = 1
//        for (i in 0u..<size) {
//            hashCode = 31 * hashCode + this.data[i].hashCode()
//        }
//        return hashCode
//    }
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KoneList<*>) return false
//        if (this.size != other.size) return false
//
//        when (other) {
//            is KoneArraySettableList<*> ->
//                for (i in 0u..<size) {
//                    if (this.data[i] != other.data[i]) return false
//                }
//            else -> {
//                val otherIterator = other.iterator()
//                for (i in 0u ..< size) {
//                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
//                }
//            }
//        }
//
//        return true
//    }
    
    /**
     * [KoneArraySettableList]'s iterator.
     * It accepts underlying [data][KoneArraySettableList.data] array and
     * current index of next element (or [size][KoneArraySettableList.size] if its cursor is at the end)
     * and does its work as an iterator.
     */
    internal class Iterator<Element>(val data: KoneMutableArray<Any?>, var currentIndex: UInt = 0u): KoneSettableLinearIterator<Element> {
        init {
            if (currentIndex > data.size) indexOutOfBoundsException(currentIndex, data.size)
        }
        override fun hasNext(): Boolean = currentIndex < data.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            data[currentIndex] = element
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            data[currentIndex - 1u] = element
        }
    }
}
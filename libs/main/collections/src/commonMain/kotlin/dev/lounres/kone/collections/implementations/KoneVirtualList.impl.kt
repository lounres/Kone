/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.*


/**
 * Represents an immutable list which elements are not stored anywhere
 * but are generated with [generator] function instead on each access.
 *
 * # Implementation details
 *
 * This implementation holds only [size] value and [generator] function.
 * Each time value with index `i` is accessed, [generator] is invoked on `i`
 * and the result is returned. The result is not stored anywhere.
 *
 * ## Time complexity of operations
 *
 * Be aware that the formulas do not include [generator] time invocation!
 *
 * | Operation                                                  | Worst case    | Average       |
 * |------------------------------------------------------------|---------------|---------------|
 * | [size]                                                     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [get]                                                      | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator]                                                 | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iteratorFrom]                                             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.hasNext][KoneLinearIterator.hasNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.hasPrevious][KoneLinearIterator.hasPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.getNext][KoneLinearIterator.getNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.getPrevious][KoneLinearIterator.getPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.moveNext][KoneLinearIterator.moveNext]           | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.movePrevious][KoneLinearIterator.movePrevious]   | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.nextIndex][KoneLinearIterator.nextIndex]         | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.previousIndex][KoneLinearIterator.previousIndex] | \(\Theta(1)\) | \(\Theta(1)\) |
 *
 * @usesMathJax
 */
//@Serializable(with = KoneVirtualListWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneVirtualList<Element>(
    override val size: UInt,
    private val generator: (index: UInt) -> Element,
) : KoneList<Element> {
    override fun get(index: UInt): Element = generator(index)

    override fun iterator(): KoneLinearIterator<Element> = Iterator(size = size, generator = generator)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> =
        if (index > size) indexOutOfBoundsException(index, size)
        else Iterator(size = size, currentIndex = index, generator = generator)

    // TODO: Don't know what is the best solution: access-less `hashCode` and `equals` or generating all values.
//    override fun hashCode(): Int {
//        var hashCode = 1
//        for (i in 0u..<size) {
//            hashCode = 31 * hashCode + this[i].hashCode()
//        }
//        return hashCode
//    }
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KoneList<*>) return false
//        if (this.size != other.size) return false
//
//        when (other) {
//            is KoneVirtualList<*> ->
//                for (i in 0u..<size) {
//                    if (this[i] != other[i]) return false
//                }
//            else -> {
//                val otherIterator = other.iterator()
//                for (i in 0u ..< size) {
//                    if (this[i] != otherIterator.getAndMoveNext()) return false
//                }
//            }
//        }
//
//        return true
//    }

    internal class Iterator<Element>(val size: UInt, var currentIndex: UInt = 0u, val generator: (UInt) -> Element): KoneLinearIterator<Element> {
        init {
            if (currentIndex > size) indexOutOfBoundsException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return generator(currentIndex)
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return generator(currentIndex - 1u)
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
    }
}
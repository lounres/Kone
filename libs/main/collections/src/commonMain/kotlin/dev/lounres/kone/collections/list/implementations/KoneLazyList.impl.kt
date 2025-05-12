/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.KoneSettableLinearIterator
import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.orElse
import kotlinx.serialization.Serializable


/**
 * Represents a settable list which elements are either set and stored in an array or
 * marked to be initialised by the provided [generator].
 *
 * # Implementation details
 *
 * This implementation holds [size] value, [generator] function, and [buffer] array of size [size].
 * Each time value with index `i` is accessed, either the `i`th value from buffer is used or
 * [generator] is invoked on `i` to return resulting value.
 * The computed by [generator] value is stored in the [buffer] the moment [generator]'s invocation is finished.
 *
 * That means that at the beginning the [buffer] is empty.
 * Right after that access to any `i`th element would invoke [generator] on `i`,
 * store its result in [buffer] and return this result.
 * But if you at first set some value `x` to the `i`th index
 * then the value is stored in the `i`th position in [buffer].
 * And after that access `i`th element returns `x` without extra actions.
 *
 * To represent presence or absence of the `i`th value, [Some] and [None] are used respectively.
 *
 * ## Time complexity of operations
 *
 * Be aware that the formulas do not include [generator] time invocation!
 *
 * | Operation                                                          | Worst case    | Average       |
 * |--------------------------------------------------------------------|---------------|---------------|
 * | [size]                                                             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [get]                                                              | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [set]                                                              | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator]                                                         | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iteratorFrom]                                                     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.hasNext][KoneSettableLinearIterator.hasNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.hasPrevious][KoneSettableLinearIterator.hasPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.getNext][KoneSettableLinearIterator.getNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.getPrevious][KoneSettableLinearIterator.getPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.setNext][KoneSettableLinearIterator.setNext]             | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.setPrevious][KoneSettableLinearIterator.setPrevious]     | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.moveNext][KoneSettableLinearIterator.moveNext]           | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.movePrevious][KoneSettableLinearIterator.movePrevious]   | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.nextIndex][KoneSettableLinearIterator.nextIndex]         | \(\Theta(1)\) | \(\Theta(1)\) |
 * | [iterator.previousIndex][KoneSettableLinearIterator.previousIndex] | \(\Theta(1)\) | \(\Theta(1)\) |
 *
 * @usesMathJax
 */
@Serializable(with = KoneLazyListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneLazyList<Element>(
    override val size: UInt,
    private val generator: (index: UInt) -> Element,
) : KoneSettableList<Element> {
    private val buffer: KoneMutableArray<Maybe<Element>> = KoneMutableArray(size) { None }

    override fun get(index: UInt): Element = buffer[index].orElse { generator(index).also { buffer[index] = Some(it) } }
    override fun set(index: UInt, element: Element) {
        buffer[index] = Some(element)
    }

    override fun iterator(): KoneSettableLinearIterator<Element> =
        Iterator(
            size = size,
            currentIndex = 0u,
            buffer = buffer,
            generator = generator,
        )
    override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> =
        if (index > size) indexOutOfBoundsException(index, size)
        else Iterator(
            size = size,
            currentIndex = index,
            buffer = buffer,
            generator = generator,
        )
    
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
//            is KoneLazyList<*> ->
//                repeat(size) {
//                    if (this[it] != other[it]) return false
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

    internal class Iterator<Element>(
        val size: UInt,
        var currentIndex: UInt,
        val buffer: KoneMutableArray<Maybe<Element>>,
        val generator: (UInt) -> Element
    ): KoneSettableLinearIterator<Element> {
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return buffer[currentIndex].orElse { generator(currentIndex).also { buffer[currentIndex] = Some(it) } }
        }
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            buffer[currentIndex] = Some(element)
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return buffer[currentIndex - 1u].orElse { generator(currentIndex - 1u).also { buffer[currentIndex - 1u] = Some(it) } }
        }
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            buffer[currentIndex] = Some(element)
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
    }
}
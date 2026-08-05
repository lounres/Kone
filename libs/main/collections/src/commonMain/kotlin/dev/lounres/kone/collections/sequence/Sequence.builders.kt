/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.sequence

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.KoneIteratorBuilder
import dev.lounres.kone.collections.iterator.build
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.sequence.empty.KoneEmptySequence


/**
 * Returns an empty [KoneSequence].
 *
 * @param Element The type of elements.
 * @return An empty [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.empty(): KoneSequence<Element> = KoneEmptySequence

/**
 * Creates a finite [KoneSequence] of the given [size] by invoking the [initializer] function for each index on each element access.
 *
 * @param Element The type of elements.
 * @param size The number of elements in the sequence.
 * @param initializer The function that provides an element for each index.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneSequence<Element> =
    KoneFiniteGenerateSequence(size, initializer)

private class KoneFiniteGenerateSequence<Element>(
    private val size: UInt,
    private val initializer: (index: UInt) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)

    private class Iterator<Element>(private val sequence: KoneFiniteGenerateSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u

        override fun hasNext(): Boolean = currentIndex < sequence.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return sequence.initializer(currentIndex)
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
}

/**
 * Creates a [KoneSequence] for the given [indices] range by invoking the [initializer] function for each index on each element access.
 *
 * @param Element The type of elements.
 * @param indices The range of indices to generate elements for.
 * @param initializer The function that provides an element for each index.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneSequence<Element> =
    if (indices.first > indices.last) KoneSequence.empty() else KoneUIntRangeGenerateSequence(indices, initializer)

private class KoneUIntRangeGenerateSequence<Element>(
    private val indices: UIntRange,
    private val initializer: (index: UInt) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneUIntRangeGenerateSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = sequence.indices.first
        
        override fun hasNext(): Boolean = currentIndex <= sequence.indices.last
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return sequence.initializer(currentIndex)
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
}

/**
 * Creates an infinite [KoneSequence] by invoking the [initializer] function for each index (i.e. for each natural number from 0 to positive infinity).
 *
 * @param Element The type of elements.
 * @param initializer The function that provides an element for each index.
 * @return An infinite [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.generate(initializer: (index: UInt) -> Element): KoneSequence<Element> =
    KoneInfiniteGenerateSequence(initializer)

private class KoneInfiniteGenerateSequence<Element>(
    private val initializer: (index: UInt) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneInfiniteGenerateSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u
        
        override fun hasNext(): Boolean = true
        override fun getNext(): Element = sequence.initializer(currentIndex)
        override fun moveNext() {
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
}

/**
 * Creates a finite [KoneSequence] by induction: starting from [initialElement], each subsequent element
 * is produced by applying [inducer] to the previous element.
 *
 * I.e. it's a (lazily evaluated) sequence of elements `(e0, e1, e2, ...)` where `e0 = initialElement`,
 * `e1 = inducer(1u, e0)`, `e2 = inducer(2u, e1)`, and so on.
 *
 * @param Element The type of elements.
 * @param size The number of elements in the sequence.
 * @param initialElement The first element of the sequence.
 * @param inducer The function that produces the next element given the next index and previous element.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSequence<Element> =
    if (size == 0u) KoneSequence.empty() else KoneFiniteInduceSequence(size, initialElement, inducer)

private class KoneFiniteInduceSequence<Element>(
    private val size: UInt,
    private val initialElement: Element,
    private val inducer: (index: UInt, previous: Element) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneFiniteInduceSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u
        private var lastIndex = 0u
        private var lastElement: Element = sequence.initialElement
        
        private fun updateLastElement() {
            while (lastIndex < currentIndex) {
                lastIndex++
                lastElement = sequence.inducer(lastIndex, lastElement)
            }
        }
        
        override fun hasNext(): Boolean = currentIndex < sequence.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            updateLastElement()
            return lastElement
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex, last index = $lastIndex, last element = $lastElement]"
    }
}

/**
 * Creates a [KoneSequence] by induction over the given [indices] range: starting from [initialElement], each subsequent element
 * is produced by applying [inducer] to the previous element.
 *
 * I.e. it's a (lazily evaluated) sequence of elements `(e0, e1, e2, ...)` where `e0 = initialElement`,
 * `e1 = inducer(indices.start, e0)`, `e2 = inducer(indices.start + 1u, e1)`, and so on.
 *
 * @param Element The type of elements.
 * @param indices The range of indices to induce elements for.
 * @param initialElement The first element of the sequence.
 * @param inducer The function that produces the next element given the index and previous element.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSequence<Element> =
    if (indices.first > indices.last) KoneSequence.empty() else KoneUIntRangeInduceSequence(indices, initialElement, inducer)

private class KoneUIntRangeInduceSequence<Element>(
    private val indices: UIntRange,
    private val initialElement: Element,
    private val inducer: (index: UInt, previous: Element) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneUIntRangeInduceSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = sequence.indices.first
        private var lastIndex = 0u
        private var lastElement: Element = sequence.initialElement
        
        private fun updateLastElement() {
            while (lastIndex < currentIndex) {
                lastIndex++
                lastElement = sequence.inducer(lastIndex, lastElement)
            }
        }
        
        override fun hasNext(): Boolean = currentIndex <= sequence.indices.last
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            updateLastElement()
            return lastElement
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex, last index = $lastIndex, last element = $lastElement]"
    }
}

/**
 * Creates an infinite [KoneSequence] by induction: starting from [initialElement], each subsequent element
 * is produced by applying [inducer] to the previous element.
 *
 * I.e. it's a (lazily evaluated) sequence of elements `(e0, e1, e2, ...)` where `e0 = initialElement`,
 * `e1 = inducer(1u, e0)`, `e2 = inducer(2u, e1)`, and so on.
 *
 * @param Element The type of elements.
 * @param initialElement The first element of the sequence.
 * @param inducer The function that produces the next element given the index and previous element.
 * @return An infinite [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.induce(initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSequence<Element> =
    KoneInfiniteInduceSequence(initialElement, inducer)

private class KoneInfiniteInduceSequence<Element>(
    private val initialElement: Element,
    private val inducer: (index: UInt, previous: Element) -> Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneInfiniteInduceSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u
        private var lastIndex = 0u
        private var lastElement: Element = sequence.initialElement
        
        private fun updateLastElement() {
            while (lastIndex < currentIndex) {
                lastIndex++
                lastElement = sequence.inducer(lastIndex, lastElement)
            }
        }
        
        override fun hasNext(): Boolean = true
        override fun getNext(): Element {
            updateLastElement()
            return lastElement
        }
        override fun moveNext() {
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex, last index = $lastIndex, last element = $lastElement]"
    }
}

/**
 * Creates a finite [KoneSequence] of the given [size] filled with the specified [element].
 *
 * @param Element The type of elements.
 * @param size The number of elements in the sequence.
 * @param element The element to fill the sequence with.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.fill(size: UInt, element: Element): KoneSequence<Element> =
    KoneFiniteFillSequence(size, element)

private class KoneFiniteFillSequence<Element>(
    private val size: UInt,
    private val element: Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneFiniteFillSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u
        
        override fun hasNext(): Boolean = currentIndex < sequence.size
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else sequence.element
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
}

/**
 * Creates an infinite [KoneSequence] filled with the specified [element].
 *
 * @param Element The type of elements.
 * @param element The element to fill the sequence with.
 * @return An infinite [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.fill(element: Element): KoneSequence<Element> =
    KoneInfiniteFillSequence(element)

private class KoneInfiniteFillSequence<Element>(
    private val element: Element,
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val sequence: KoneInfiniteFillSequence<Element>) : KoneIterator<Element> {
        private var currentIndex = 0u
        
        override fun hasNext(): Boolean = true
        override fun getNext(): Element = sequence.element
        override fun moveNext() {
            currentIndex++
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
    }
}

/**
 * Creates a [KoneSequence] from the given vararg [elements].
 *
 * @param Element The type of elements.
 * @param elements The elements to include in the sequence.
 * @return A new [KoneSequence] instance containing the specified elements.
 */
public fun <Element> KoneSequence.Companion.of(vararg elements: Element): KoneSequence<Element> =
    KoneOfSequence(KoneArray(elements))

private class KoneOfSequence<Element>(
    private val elements: KoneArray<Element>
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = elements.iterator()
}

/**
 * Builds a [KoneSequence] using the provided coroutine-based [builder] function.
 *
 * @param Element The type of elements.
 * @param builder The suspending builder function that yields elements.
 * @return A new [KoneSequence] instance.
 */
public fun <Element> KoneSequence.Companion.build(builder: suspend KoneIteratorBuilder<Element>.() -> Unit): KoneSequence<Element> =
    KoneSequence { KoneIterator.build(builder) }

/**
 * Converts this [KoneIterator] to a [KoneSequence] by wrapping it.
 *
 * Be aware that this sequence returns the same iterator as it has acquired on each [KoneSequence.iterator] call.
 * Hence,
 * ```kotlin
 * val list = KoneList.of(1, 2, 3)
 *
 * val theSequence = list.asKoneSequence()
 * println(theSequence.take(1).toKoneList()) // prints "1"
 * println(theSequence.take(1).toKoneList()) // prints "1" again, because a new iterator was created and used!
 *
 * val theSequence = list.iterator().asKoneSequence()
 * println(theSequence.take(1).toKoneList()) // prints "1"
 * println(theSequence.take(1).toKoneList()) // prints "2", not "1", because wrapped iterator's state changed!
 * ```
 * It's a good method for one-time wrapping of the iterator in a call chain, but it's bad for reusing the sequence value.
 * If you need cached variant of the iterator-based sequence, use [KoneSequence.cached].
 * If you need to wrap an iterator that is acquired from an iterable, apply [KoneIterable.asKoneSequence] on the iterable straight.
 *
 * @param Element The type of elements.
 * @receiver The iterator to wrap into a sequence.
 * @return A [KoneSequence] that wraps this iterator.
 */
public fun <Element> KoneIterator<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { this }

/**
 * Converts this [KoneIterable] to a [KoneSequence] by returning [this] iterable's iterators.
 *
 * @param Element The type of elements.
 * @receiver The iterable to wrap into a sequence.
 * @return A [KoneSequence] that wraps this iterable's iterator.
 */
public fun <Element> KoneIterable<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { this.iterator() }
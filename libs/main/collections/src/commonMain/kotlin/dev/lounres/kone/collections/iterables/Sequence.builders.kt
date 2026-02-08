/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterables

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterables.empty.KoneEmptySequence
import dev.lounres.kone.collections.noNextElementInIteratorException


public fun <Element> KoneSequence.Companion.empty(): KoneSequence<Element> = KoneEmptySequence

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
    }
}

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
    }
}

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
    }
}

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
    }
}

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
    }
}

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
    }
}

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
    }
}

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
    }
}

public fun <Element> KoneSequence.Companion.of(vararg elements: Element): KoneSequence<Element> =
    KoneOfSequence(KoneArray(elements))

private class KoneOfSequence<Element>(
    private val elements: KoneArray<Element>
) : KoneSequence<Element> {
    override fun iterator(): KoneIterator<Element> = elements.iterator()
}

public fun <Element> KoneSequence.Companion.build(@BuilderInference builder: suspend KoneIteratorBuilder<Element>.() -> Unit): KoneSequence<Element> =
    KoneSequence { KoneIterator.build(builder) }

public fun <Element> KoneIterator<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { this }

public fun <Element> KoneIterable<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { this.iterator() }
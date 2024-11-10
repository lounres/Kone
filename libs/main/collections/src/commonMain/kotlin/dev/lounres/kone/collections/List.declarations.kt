/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.repeat


/**
 * Represents a finite collection of elements with some order on them.
 *
 * This interface's inheritors must have some specific structure
 * that provides optimised elements access or optimised elements iteration.
 * Without both of them (or with bad asymptotic like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneList<out Element> : KoneLinearIterable<Element> {
    /**
     * Returns element that is placed at the provided [index].
     *
     * For each index from `0` to [size] exclusive there is exactly one corresponding element.
     *
     * If index is at least [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is not less than [size].
     */
    public operator fun get(index: UInt): Element
    
    /**
     * Initiates an iterator over the collection's elements
     * with pointer between elements with indices `index - 1` and `index` correspondingly.
     *
     * In the iterator elements are iterated in the order of their indices.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    public fun iteratorFrom(index: UInt): KoneLinearIterator<Element>
    /**
     * Initiates an iterator over the collection's elements
     * with pointer before the first element.
     *
     * In the iterator elements are iterated in the order of their indices.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    override fun iterator(): KoneLinearIterator<Element> = iteratorFrom(0u)
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneSettableList<Element> : KoneList<Element> {
    public operator fun set(index: UInt, element: Element)
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableList<Element> : KoneSettableList<Element>, KoneMutableLinearIterable<Element> {
    public fun add(element: Element) { addAt(size, element) }
    public fun addAt(index: UInt, element: Element)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }
    public fun addSeveralAt(index: UInt, number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { addAt(index + it, builder(it)) }
    }
    
    public fun removeAt(index: UInt)
    public fun removeAllThat(predicate: (element: Element) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    public fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean)
    public fun removeAll()
    
    override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element>
    override fun iterator(): KoneMutableLinearIterator<Element> = iteratorFrom(0u)
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneGrowableMutableList<Element> : KoneMutableList<Element> {
    public fun ensureCapacity(minimalCapacity: UInt)
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedList<out Element> : KoneList<Element> {
    public fun getNode(index: UInt): KoneListNode<Element>
    override fun get(index: UInt): Element = getNode(index).element
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneSettableNoddedList<Element> : KoneNoddedList<Element>, KoneSettableList<Element> {
    override fun getNode(index: UInt): KoneSettableListNode<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedList<Element> : KoneSettableNoddedList<Element>, KoneMutableList<Element> {
    override fun getNode(index: UInt): KoneMutableListNode<Element>
    
    public fun addNode(element: Element): KoneMutableListNode<Element>
    override fun add(element: Element) { addNode(element) }
    public fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element>
    override fun addAt(index: UInt, element: Element) { addNodeAt(index, element) }
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneGrowableMutableNoddedList<Element> : KoneMutableNoddedList<Element>, KoneGrowableMutableList<Element>
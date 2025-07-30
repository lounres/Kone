/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque

import dev.lounres.kone.collections.EmptyDequeAccessException


/**
 * Represents a finite sequence of elements that can be extended or reduced from both its ends.
 *
 * This interface's inheritors must have some specific structure that provides optimised elements access and mutability.
 * Without it (or with bad time complexity like \(O(n)\)) the interface should not be used.
 */
public interface KoneDeque<Element> {
    /**
     * Number of elements in the collection.
     */
    public val size: UInt
    
    /**
     * Returns the first element.
     * The element is placed at the beginning of the sequence.
     *
     * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
     *
     * @throws EmptyDequeAccessException when the collection is empty.
     */
    public fun getFirst(): Element
    /**
     * Returns the last element.
     * The element is placed at the end of the sequence.
     *
     * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
     *
     * @throws EmptyDequeAccessException when the collection is empty.
     */
    public fun getLast(): Element
    /**
     * Adds the element to the beginning of the collection, making it the first element in the collection.
     */
    public fun addFirst(element: Element)
    /**
     * Adds the element to the end of the collection, making it the last element in the collection.
     */
    public fun addLast(element: Element)
    /**
     * Removes the first element, making the collection one element fewer.
     * The element is placed at the beginning of the sequence.
     *
     * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
     *
     * @throws EmptyDequeAccessException when the collection is empty.
     */
    public fun removeFirst()
    /**
     * Removes the last element, making the collection one element fewer.
     * The element is placed at the end of the sequence.
     *
     * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
     *
     * @throws EmptyDequeAccessException when the collection is empty.
     */
    public fun removeLast()
    /**
     * Removes all elements from the collection.
     */
    public fun removeAll()
    
    public companion object
}
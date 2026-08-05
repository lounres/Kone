/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterable.KoneLinearIterable
import dev.lounres.kone.collections.iterable.KoneMutableLinearIterable
import dev.lounres.kone.collections.iterable.KoneSettableLinearIterable
import dev.lounres.kone.collections.list.serializers.*
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.repeat
import kotlinx.serialization.Serializable


/**
 * Represents a finite collection of elements with some order on them.
 *
 * This interface's inheritors must have some specific structure
 * that provides optimized elements access or optimized elements iteration.
 * Without both of them (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneListSerializer::class)
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
    public fun iteratorFrom(index: UInt): KoneListIterator<Element>
    /**
     * Initiates an iterator over the collection's elements
     * with pointer before the first element.
     *
     * In the iterator elements are iterated in the order of their indices.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    override fun iterator(): KoneListIterator<Element> = iteratorFrom(0u)
    
    /**
     * Legacy equality operation. Must return `true` iff the following holds.
     * - [other] is [KoneList] as well.
     * - [other] has the same [size].
     * - For each index `i in 0u ..< size`, `this[i] == other[i]`.
     *
     * See [Equality] for idiomatic replacement and use this operation with caution.
     *
     * @param other Another element to check referential equality with.
     * @return The result of legacy equality check.
     */
    override fun equals(other: Any?): Boolean
    /**
     * Legacy hash computation operation. Must return result of the following pseudocode:
     * ```kotlin
     * val result = 1
     * for (i in 0u ..< size) result = result * 31 + this[i].hashCode()
     * ```
     *
     * See [Hashing] for idiomatic replacement and use this operation with caution.
     *
     * @return The result of legacy hash computation.
     */
    override fun hashCode(): Int
    /**
     * Represents the list as a string. Must return a string in the following form.
     * ```
     * "[<element at 0>, <element at 1>, ...]"
     * ```
     *
     * @return The string representation of the list.
     */
    override fun toString(): String
    
    public companion object
}

/**
 * Represents a finite collection of elements with some order on them
 * with possibility to replace element at the provided index.
 *
 * This interface's inheritors must have some specific structure
 * that provides optimized elements access or optimized elements iteration.
 * Without both of them (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneSettableListSerializer::class)
public interface KoneSettableList<Element> : KoneList<Element>, KoneSettableLinearIterable<Element> {
    /**
     * Sets another value at the place with the provided [index] with respect to inner order of elements.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value
     * which can be replaced via this operation.
     *
     * If index is at least [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is not less than [size].
     */
    public operator fun set(index: UInt, element: Element)
    
    override fun iteratorFrom(index: UInt): KoneSettableListIterator<Element>
    override fun iterator(): KoneSettableListIterator<Element> = iteratorFrom(0u)
    
    public companion object
}

/**
 * Represents a finite collection of elements with some order on them
 * with possibility to add, replace, and replace element at the provided index.
 *
 * This interface's inheritors must have some specific structure
 * that provides optimized elements access or optimized elements iteration.
 * Without both of them (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneMutableListSerializer::class)
public interface KoneMutableList<Element> : KoneSettableList<Element>, KoneMutableLinearIterable<Element> {
    /**
     * Adds provided [element] at the end of the ordered collection.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation adds a place with index [size] and puts the value in it.
     */
    public fun add(element: Element) { addAt(size, element) }
    /**
     * Adds provided [element] before element with index [index].
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - for each place with index at least [index] increases its index by one,
     * - adds a place with index [index],
     * - and puts the value in the added place.
     *
     * When [index] is equal to [size] the element is added at the end.
     *
     * If [index] is greater than [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is greater than [size].
     */
    public fun addAt(index: UInt, element: Element)
    /**
     * Adds provided [number] of elements at the end of the ordered collection.
     * `i`th new element is a result of `builder(i)`.
     * The builder is consecutively called on indices from `0` to [number] exclusive.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - adds places with indices from [size] to `size + number` exclusive,
     * - and puts result of `builder(i)` in a place with index `size + i`
     *   for each `i` from `0` to [number] exclusive.
     *
     * All [builder] invocations are computed consecutively on values from `0` to [number] exclusive
     * in their order starting with `0`.
     */
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }
    /**
     * Adds provided [number] of elements before element with index [index].
     * `i`th new element is a result of `builder(i)`.
     * The builder is consecutively called on indices from `0` to [number] exclusive.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - for each place with index at least [index] increases its index by [number],
     * - adds places with indices from [index] to `index + number` exclusive,
     * - and puts result of `builder(i)` in a place with index `index + i`
     *   for each `i` from `0` to [number] exclusive.
     *
     * All [builder] invocations are computed consecutively on values from `0` to [number] exclusive
     * in their order starting with `0`.
     *
     * When [index] is equal to [size] the elements are added at the end.
     *
     * If [index] is greater than [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is greater than [size].
     */
    public fun addSeveralAt(index: UInt, number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { addAt(index + it, builder(it)) }
    }
    
    /**
     * Removes element with the provided [index].
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - removes element and place with index [index],
     * - and for each place with index grater than [index] decreases its index by one.
     *
     * If [index] is at least [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is no less than [size].
     */
    public fun removeAt(index: UInt)
    /**
     * Removes elements that satisfy the provided [predicate].
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And for each index `i` from `0` to [size] this operation
     * if `predicate(element)` is false where `element` is the element with index `i`
     * removes the element and its place.
     * After that indices are reassigned to the rest places with in their corresponding order
     * starting from `0`.
     *
     * The [predicate] is called consecutively on elements of the collection in their order
     * starting with the first one (at index `0`).
     */
    public fun removeAllThat(predicate: (element: Element) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    /**
     * Removes elements that satisfy the provided [predicate].
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And for each index `i` from `0` to [size] this operation
     * if `predicate(i, element)` is false where `element` is the element with index `i`
     * removes the element and its place.
     * After that indices are reassigned to the rest places with in their corresponding order
     * starting from `0`.
     *
     * The [predicate] is called consecutively on elements of the collection in their order
     * starting with the first one (at index `0`).
     */
    public fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean)
    /**
     * Removes all elements and their places from the collection.
     */
    public fun removeAll()
    
    /**
     * Initiates a mutable iterator over the collection's elements
     * with pointer between elements with indices `index - 1` and `index` correspondingly.
     *
     * In the iterator elements are iterated in the order of their indices.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    override fun iteratorFrom(index: UInt): KoneMutableListIterator<Element>
    /**
     * Initiates a mutable iterator over the collection's elements
     * with pointer before the first element.
     *
     * In the iterator elements are iterated in the order of their indices.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    override fun iterator(): KoneMutableListIterator<Element> = iteratorFrom(0u)
    
    public companion object
}

/**
 * Represents a [KoneMutableList] which inner structure has a capacity that can be increased.
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneGrowableMutableListSerializer::class)
public interface KoneGrowableMutableList<Element> : KoneMutableList<Element> {
    /**
     * Increases inner structure's capacity so that it can hold [minimalCapacity] number of elements
     * without reinitialization of the capacity.
     */
    public fun ensureCapacity(minimalCapacity: UInt)
    
    public companion object
}

/**
 * Represents a nodded version of [KoneList].
 *
 * It means that there is exactly one [KoneListNode] corresponding to each place
 * that can effectively access the places element and index as well as
 * other things that can be found in its documentation.
 *
 * @see KoneList
 * @see KoneListNode
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneNoddedListSerializer::class)
public interface KoneNoddedList<out Element> : KoneList<Element> {
    /**
     * Returns node that corresponds to the place with the provided [index].
     */
    public fun getNode(index: UInt): KoneListNode<Element>
    /**
     * Returns element that is placed at the provided [index].
     *
     * For each index from `0` to [size] exclusive there is exactly one corresponding element.
     *
     * If index is at least [size], [IndexOutOfBoundsException] is thrown.
     *
     * Default implementation calls [getNode] and retrieves its node's [element][KoneListNode.element].
     *
     * @throws IndexOutOfBoundsException when index is not less than [size].
     */
    override fun get(index: UInt): Element = getNode(index).element
    
    override fun iterator(): KoneNoddedListIterator<Element>
    override fun iteratorFrom(index: UInt): KoneNoddedListIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneSettableList].
 * See [KoneNoddedList] for more.
 *
 * @see KoneSettableList
 * @see KoneSettableListNode
 * @see KoneNoddedList
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneSettableNoddedListSerializer::class)
public interface KoneSettableNoddedList<Element> : KoneNoddedList<Element>, KoneSettableList<Element> {
    override fun getNode(index: UInt): KoneSettableListNode<Element>
    
    override fun iterator(): KoneSettableNoddedListIterator<Element>
    override fun iteratorFrom(index: UInt): KoneSettableNoddedListIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneMutableList].
 * See [KoneNoddedList] for more.
 *
 * @see KoneMutableList
 * @see KoneMutableListNode
 * @see KoneNoddedList
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneMutableNoddedListSerializer::class)
public interface KoneMutableNoddedList<Element> : KoneSettableNoddedList<Element>, KoneMutableList<Element> {
    override fun getNode(index: UInt): KoneMutableListNode<Element>
    
    /**
     * Adds provided [element] at the end of the ordered collection
     * and returns its corresponding node.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation adds a place with index [size] and puts the value in it.
     */
    public fun addNode(element: Element): KoneMutableListNode<Element>
    /**
     * Adds provided [element] at the end of the ordered collection.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation adds a place with index [size] and puts the value in it.
     *
     * Default implementation just calls [addNode].
     */
    override fun add(element: Element) {
        val _ = addNode(element)
    }
    /**
     * Adds provided [element] before element with index [index]
     * and returns its corresponding node.
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - for each place with index at least [index] increases its index by one,
     * - adds a place with index [index],
     * - and puts the value in the added place.
     *
     * When [index] is equal to [size] the element is added at the end.
     *
     * If [index] is greater than [size], [IndexOutOfBoundsException] is thrown.
     *
     * @throws IndexOutOfBoundsException when index is greater than [size].
     */
    public fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element>
    /**
     * Adds provided [element] before element with index [index].
     *
     * For each index from `0` to [size] there is exactly one corresponding place for a value.
     * And this operation:
     * - for each place with index at least [index] increases its index by one,
     * - adds a place with index [index],
     * - and puts the value in the added place.
     *
     * When [index] is equal to [size] the element is added at the end.
     *
     * If [index] is greater than [size], [IndexOutOfBoundsException] is thrown.
     *
     * Default implementation just calls [addNodeAt].
     *
     * @throws IndexOutOfBoundsException when index is greater than [size].
     */
    override fun addAt(index: UInt, element: Element) {
        val _ = addNodeAt(index, element)
    }
    
    override fun iterator(): KoneMutableNoddedListIterator<Element>
    override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneGrowableMutableList].
 * See [KoneNoddedList] for more.
 *
 * @see KoneGrowableMutableList
 * @see KoneMutableListNode
 * @see KoneNoddedList
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
@Serializable(with = DefaultKoneGrowableMutableNoddedListSerializer::class)
public interface KoneGrowableMutableNoddedList<Element> : KoneMutableNoddedList<Element>, KoneGrowableMutableList<Element> {
    public companion object
}
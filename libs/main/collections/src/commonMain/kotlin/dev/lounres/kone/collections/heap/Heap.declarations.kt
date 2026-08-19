/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap

import dev.lounres.kone.collections.DetachedNodeException
import dev.lounres.kone.collections.iterable.KoneRemovableIterable
import dev.lounres.kone.collections.iterable.KoneReversibleRemovableIterable
import dev.lounres.kone.collections.set.KoneRemovableLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneRemovableReifiedSet
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order


/**
 * Represents a pair of an element and its (future) priority in the heap.
 *
 * This interface is used to represent entries for a heap when the heap is being constructed.
 */
public interface HeapEntry<out Element, out Priority> {
    /**
     * Returns the corresponding element of the entry.
     */
    public val element: Element
    /**
     * Returns the corresponding priority of the entry.
     */
    public val priority: Priority
    
    /**
     * Legacy equality operation. Must return the result of referential equality.
     *
     * See [Equality] for idiomatic replacement and use this operation with caution.
     *
     * @param other Another element to check referential equality with.
     * @return The result of legacy equality check.
     */
    override fun equals(other: Any?): Boolean
    /**
     * Legacy hash computation operation. Must return any [Int] value.
     *
     * See [Hashing] for idiomatic replacement and use this operation with caution.
     *
     * @return The result of legacy hash computation.
     */
    override fun hashCode(): Int
    /**
     * Represents the heap entry as a string. Must return a string in the following form.
     * ```
     * "<entry name>[element = <element>, priority = <priority>]"
     * ```
     * "Entry name" here can mean anything, but (FQ) name of the entry's class with system hash code (if there is any) is enough.
     *
     * @return The string representation of the node.
     */
    override fun toString(): String
}

/**
 * Returns simple [HeapEntry] instance that has the specified [element] and [priority].
 */
public fun <Element, Priority> HeapEntry(element: Element, priority: Priority): HeapEntry<Element, Priority> =
    HeapEntryImpl(element, priority)

internal class HeapEntryImpl<out Element, out Priority>(
    override val element: Element,
    override val priority: Priority,
) : HeapEntry<Element, Priority> {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = element.hashCode() * 31 + priority.hashCode()
    override fun toString(): String = "${super.toString()}[element = $element, priority = $priority]"
}

/**
 * Represents a node in the inner structure of [MinimumHeap]/[MaximumHeap].
 *
 * The node can be detached by removing the corresponding place from the collection it was defined in.
 * In that case only its element and priority are preserved
 * and other methods and properties throw [DetachedNodeException].
 *
 * @see MinimumHeap
 * @see MaximumHeap
 */
public interface HeapNode<Element, Priority> : HeapEntry<Element, Priority> {
    /**
     * Indicates if the node is detached from the structure it was a part of.
     */
    public val isDetached: Boolean
    
    /**
     * Returns element corresponding to that node.
     * Change of the element changes the corresponding element in the structure.
     *
     * When detached (that happens only when the corresponding place is removed)
     * holds the last element it was holding.
     * After detaching, the node just stores the element that can be changed.
     * And the changing won't modify the structure the node was detached from.
     */
    override var element: Element
    /**
     * Returns priority corresponding to that node.
     * Change of the priority changes the corresponding priority in the structure
     * and changes the inner structure to satisfy the heap property.
     *
     * When detached (that happens only when the corresponding place is removed)
     * holds the last priority it was holding.
     * After detaching, the node just stores the priority that can be changed.
     * And the changing won't modify the structure the node was detached from.
     *
     * > **Note for implementers!**
     * Usually heaps do not provide increment (in case of minimum-heap and decrement in case of maximum-heap)
     * of node's priority.
     * However, it can be implemented it by removement of the node with consequent addition of it with new priority.
     * Also, all popular implementations can do it without much hassle.
     */
    override var priority: Priority
    /**
     * Removes the corresponding place from the heap and detaches the node.
     *
     * The operation must be idempotent.
     * It means that calling this function again must do nothing at all.
     *
     * > **Note for implementers!**
     * Usually heaps do not provide deletion of non-root nodes.
     * However, it can be implemented using `Maybe<Priority>` instead of `Priority`
     * with a new order on the new elements that works in the same way on wrapped in `Some` values
     * and treats `None` as the least element (in case of minimum-heap or the greatest in case of maximum-heap)
     * by the following operations:
     * >  1. decrease priority of the element to `None` (so that the node becomes the only least (greatest) node),
     * >  2. pop the only minimum node.
     * >
     * > That means that it is possible to implement the heap in the way that it can remove arbitrary element.
     * Also, all popular implementations can do it without any problem of introducing new elements with new order.
     * But if for some reason you can not implement the logic without introduction of new elements with new order,
     * you can reimplement idea of Kotlin's [Result]:
     * >  1. Introduce `internal` object `LeastPriority`.
     * >  2. Instead of priority of type `Priority` store also `LeastPriority` by changing your type to `Any?`.
     * >  3. When you need to compare two nodes' priorities, at first, check whether each of them is `LeastPriority`
     * >    and if both of them are not the element, cast them to `Priority` and use provided order.
     * >  4. When you need to return the priority, just cast it to `Priority`.
     * >    It is not of the type `Priority` only when it is in process of the node removement.
     */
    public fun remove()
}

/**
 * In Kone, minimum heap is a structure that consists of:
 * 1. an [order][Order] on values called "priorities" (thus, heap is contextful)
 * 2. and a tree (in [computer science sense of the word](https://en.wikipedia.org/wiki/Tree_(abstract_data_type)))
 *     that holds one priority value and one "element" value in each its vertex and satisfies *heap property*:
 *     for any given vertex \(C\), if \(P\) is the parent vertex of \(C\), then the priority of \(P\) is less than or equal to the priority of \(C\).
 *
 * This interface's inheritors must have some specific structure that provides optimized minimum node access.
 * Without it (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
public interface MinimumHeap<Element, Priority> {
    /**
     * Number of elements in the collection.
     */
    public val size: UInt
    /**
     * Reified set that is a view on nodes of that heap.
     *
     * Because there is exactly one instance of heap node corresponding to each entry in the heap,
     * the collection is a reified set which equality is the absolute equality.
     */
    public val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>>
    /**
     * Iterable that is a view on elements of the heap.
     */
    public val elementsView: KoneRemovableIterable<Element>
    /**
     * Iterable that is a view on priorities of the heap.
     */
    public val prioritiesView: KoneRemovableIterable<Priority>
    
    /**
     * Adds the [element] with corresponding [priority] to the heap
     * and returns constructed node that corresponds to the element and the priority.
     */
    @IgnorableReturnValue
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    /**
     * Retrieves the root node. It is a node with the minimum priority.
     */
    public fun takeMinimum(): HeapNode<Element, Priority>
    /**
     * Retrieves the root node and removes it from the heap. It is a node with the minimum priority.
     */
    @IgnorableReturnValue
    public fun popMinimum(): HeapNode<Element, Priority>
    
    /**
     * Legacy equality operation. Must return the result of referential equality.
     *
     * See [Equality] for idiomatic replacement and use this operation with caution.
     *
     * @param other Another element to check referential equality with.
     * @return The result of legacy equality check.
     */
    override fun equals(other: Any?): Boolean
    /**
     * Legacy hash computation operation. Must return any [Int] value.
     *
     * See [Hashing] for idiomatic replacement and use this operation with caution.
     *
     * @return The result of legacy hash computation.
     */
    override fun hashCode(): Int
    /**
     * Represents the heap as a string. Must return a string in the following form.
     * ```
     * "<heap name>[size = <size>]"
     * ```
     * "Heap name" here can mean anything, but (FQ) name of the heap's class with system hash code (if there is any) is enough.
     *
     * @return The string representation of the node.
     */
    override fun toString(): String
}

/**
 * In Kone, maximum heap is a structure that consists of:
 * 1. an [order][Order] on values called "priorities" (thus, heap is contextful)
 * 2. and a tree (in [computer science sense of the word](https://en.wikipedia.org/wiki/Tree_(abstract_data_type)))
 *     that holds one priority value and one "element" value in each its vertex and satisfies *heap property*:
 *     for any given vertex \(C\), if \(P\) is the parent vertex of \(C\), then the priority of \(P\) is greater than or equal to the priority of \(C\).
 *
 * This interface's inheritors must have some specific structure that provides optimized minimum node access.
 * Without it (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
public interface MaximumHeap<Element, Priority> {
    /**
     * Number of elements in the collection.
     */
    public val size: UInt
    /**
     * Reified set that is a view on nodes of that heap.
     *
     * Because there is exactly one instance of heap node corresponding to each entry in the heap,
     * the collection is a reified set which equality is the absolute equality.
     */
    public val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>>
    /**
     * Iterable that is a view on elements of that heap.
     */
    public val elementsView: KoneRemovableIterable<Element>
    public val prioritiesView: KoneRemovableIterable<Priority>
    
    /**
     * Adds the [element] with corresponding [priority] to the heap
     * and returns constructed node that corresponds to the element and the priority.
     */
    @IgnorableReturnValue
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    /**
     * Retrieves the root node. It is a node with the maximum priority.
     */
    public fun takeMaximum(): HeapNode<Element, Priority>
    /**
     * Retrieves the root node and removes it from the heap. It is a node with the maximum priority.
     */
    @IgnorableReturnValue
    public fun popMaximum(): HeapNode<Element, Priority>
    
    /**
     * Legacy equality operation. Must return the result of referential equality.
     *
     * See [Equality] for idiomatic replacement and use this operation with caution.
     *
     * @param other Another element to check referential equality with.
     * @return The result of legacy equality check.
     */
    override fun equals(other: Any?): Boolean
    /**
     * Legacy hash computation operation. Must return any [Int] value.
     *
     * See [Hashing] for idiomatic replacement and use this operation with caution.
     *
     * @return The result of legacy hash computation.
     */
    override fun hashCode(): Int
    /**
     * Represents the heap as a string. Must return a string in the following form.
     * ```
     * "<heap name>[size = <size>]"
     * ```
     * "Heap name" here can mean anything, but (FQ) name of the heap's class with system hash code (if there is any) is enough.
     *
     * @return The string representation of the node.
     */
    override fun toString(): String
}

/**
 * Represents a node in the inner structure of [LinkedMinimumHeap]/[LinkedMaximumHeap].
 * See [HeapNode] for general definition.
 *
 * @see HeapNode
 * @see LinkedMinimumHeap
 * @see LinkedMaximumHeap
 */
public interface LinkedHeapNode<Element, Priority> : HeapNode<Element, Priority> {
    /**
     * Returns node corresponding to the next place from this node's corresponding place.
     * If there is no such node (i.e. this node is the last one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val nextNode: LinkedHeapNode<Element, Priority>?
    /**
     * Returns node corresponding to the previous place from this node's corresponding place.
     * If there is no such node (i.e. this node is the first one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val previousNode: LinkedHeapNode<Element, Priority>?
}

/**
 * Represents a linked minimum heap.
 * See [MinimumHeap] for general definition of minimum heap.
 *
 * Linked heap is a heap with structure of a linked list.
 * However, the order of elements in linked structure may be defined in different ways.
 * For example, it may be a chronological order
 * (i.e. new elements are added to the end of the order and reconstruction of the heap won't change the order).
 * Or a list representation order
 * (i.e. the first element is the root node, then its children go next, then their children go next, and so on
 * and reconstruction of the heap will change the order).
 * See implementations' documentations to get the behaviour you need.
 */
public interface LinkedMinimumHeap<Element, Priority> : MinimumHeap<Element, Priority> {
    /**
     * Linked reified set that is a view on nodes of that heap.
     *
     * The order of nodes in the linked list part of the structure coincides
     * with the order of the nodes in the heap itself.
     *
     * Because there is exactly one instance of heap node corresponding to each entry in the heap,
     * the collection is a reified set which equality is the absolute equality.
     */
    override val nodesView: KoneRemovableLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    /**
     * Reversible iterable that is a view on elements of that heap.
     *
     * The order of elements in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val elementsView: KoneReversibleRemovableIterable<Element>
    /**
     * Reversible iterable that is a view on priorities of that heap.
     *
     * The order of priorities in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val prioritiesView: KoneReversibleRemovableIterable<Priority>
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority>
    override fun takeMinimum(): LinkedHeapNode<Element, Priority>
    override fun popMinimum(): LinkedHeapNode<Element, Priority>
}

/**
 * Represents a linked minimum heap.
 * See [MaximumHeap] for general definition of minimum heap.
 *
 * Linked heap is a heap with structure of a linked list.
 * However, the order of elements in linked structure may be defined in different ways.
 * For example, it may be a chronological order
 * (i.e. new elements are added to the end of the order and reconstruction of the heap won't change the order).
 * Or a list representation order
 * (i.e. the first element is the root node, then its children go next, then their children go next, and so on
 * and reconstruction of the heap will change the order).
 * See implementations' documentations to get the behaviour you need.
 */
public interface LinkedMaximumHeap<Element, Priority> : MaximumHeap<Element, Priority> {
    /**
     * Linked reified set that is a view on nodes of that heap.
     *
     * The order of nodes in the linked list part of the structure coincides
     * with the order of the nodes in the heap itself.
     *
     * Because there is exactly one instance of heap node corresponding to each entry in the heap,
     * the collection is a reified set which equality is the absolute equality.
     */
    override val nodesView: KoneRemovableLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    /**
     * Reversible iterable that is a view on elements of that heap.
     *
     * The order of elements in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val elementsView: KoneReversibleRemovableIterable<Element>
    /**
     * Reversible iterable that is a view on priorities of that heap.
     *
     * The order of priorities in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val prioritiesView: KoneReversibleRemovableIterable<Priority>
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority>
    override fun takeMaximum(): LinkedHeapNode<Element, Priority>
    override fun popMaximum(): LinkedHeapNode<Element, Priority>
}
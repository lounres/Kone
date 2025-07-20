/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap

import dev.lounres.kone.collections.DetachedNodeException
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneReversibleIterable
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
}

/**
 * Returns simple [HeapEntry] instance that has the specified [element] and [priority].
 */
public fun <Element, Priority> HeapEntry(element: Element, priority: Priority): HeapEntry<Element, Priority> =
    HeapEntryImpl(element, priority)

internal data class HeapEntryImpl<out Element, out Priority>(
    override val element: Element,
    override val priority: Priority,
) : HeapEntry<Element, Priority> {
    override fun toString(): String = "HeapEntry(element=$element, priority=$priority)"
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
     */
    override var priority: Priority
    /**
     * Removes the corresponding place from the heap and detaches the node.
     *
     * The operation must be idempotent.
     * It means that calling this function again must do nothing at all.
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
 * This interface's inheritors must have some specific structure that provides optimised minimum node access.
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
    public val nodesView: KoneReifiedSet<HeapNode<Element, Priority>>
    /**
     * Iterable that is a view on elements of that heap.
     */
    public val elementsView: KoneIterable<Element>
    public val prioritiesView: KoneIterable<Priority>
    
    /**
     * Adds the [element] with corresponding [priority] to the heap
     * and returns constructed node that corresponds to the element and the priority.
     */
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    /**
     * Retrieves the root node. It is a node with the minimum priority.
     */
    public fun takeMinimum(): HeapNode<Element, Priority>
    /**
     * Retrieves the root node and removes it from the heap. It is a node with the minimum priority.
     */
    public fun popMinimum(): HeapNode<Element, Priority>
}

/**
 * In Kone, maximum heap is a structure that consists of:
 * 1. an [order][Order] on values called "priorities" (thus, heap is contextful)
 * 2. and a tree (in [computer science sense of the word](https://en.wikipedia.org/wiki/Tree_(abstract_data_type)))
 *     that holds one priority value and one "element" value in each its vertex and satisfies *heap property*:
 *     for any given vertex \(C\), if \(P\) is the parent vertex of \(C\), then the priority of \(P\) is greater than or equal to the priority of \(C\).
 *
 * This interface's inheritors must have some specific structure that provides optimised minimum node access.
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
    public val nodesView: KoneReifiedSet<HeapNode<Element, Priority>>
    /**
     * Iterable that is a view on elements of that heap.
     */
    public val elementsView: KoneIterable<Element>
    public val prioritiesView: KoneIterable<Priority>
    
    /**
     * Adds the [element] with corresponding [priority] to the heap
     * and returns constructed node that corresponds to the element and the priority.
     */
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    /**
     * Retrieves the root node. It is a node with the maximum priority.
     */
    public fun takeMaximum(): HeapNode<Element, Priority>
    /**
     * Retrieves the root node and removes it from the heap. It is a node with the maximum priority.
     */
    public fun popMaximum(): HeapNode<Element, Priority>
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
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    /**
     * Reversible iterable that is a view on elements of that heap.
     *
     * The order of elements in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val elementsView: KoneReversibleIterable<Element>
    override val prioritiesView: KoneReversibleIterable<Priority>
    
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
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    /**
     * Reversible iterable that is a view on elements of that heap.
     *
     * The order of elements in the iterable coincides with the order of the corresponding nodes in the heap itself.
     */
    override val elementsView: KoneReversibleIterable<Element>
    override val prioritiesView: KoneReversibleIterable<Priority>
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority>
    override fun takeMaximum(): LinkedHeapNode<Element, Priority>
    override fun popMaximum(): LinkedHeapNode<Element, Priority>
}
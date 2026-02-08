/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.searchTree

import dev.lounres.kone.collections.DetachedNodeException
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneReversibleIterable
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet


/**
 * Represents a node in the inner structure of [SearchTree].
 *
 * The node can be detached by removing the corresponding place from the collection it was defined in.
 * In that case only its element and priority are preserved
 * and other methods and properties throw [DetachedNodeException].
 *
 * @see SearchTree
 */
public interface SearchTreeNode<Element, out Priority> {
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
    public var element: Element
    /**
     * Returns priority corresponding to that node.
     * Change of the priority changes the corresponding priority in the structure
     * and changes the inner structure to satisfy the search tree property.
     *
     * When detached (that happens only when the corresponding place is removed)
     * holds the last priority it was holding.
     * After detaching, the node just stores the priority that can be changed.
     * And the changing won't modify the structure the node was detached from.
     */
    public val priority: Priority
    /**
     * Removes the corresponding place from the search tree and detaches the node.
     *
     * The operation must be idempotent.
     * It means that calling this function again must do nothing at all.
     */
    public fun remove()
}

/**
 * Represents a node in the inner structure of [LinkedSearchTree].
 * See [SearchTreeNode] for general definition.
 *
 * @see SearchTreeNode
 * @see LinkedSearchTree
 */
public interface LinkedSearchTreeNode<Element, out Priority> : SearchTreeNode<Element, Priority> {
    /**
     * Returns node corresponding to the next place from this node's corresponding place.
     * If there is no such node (i.e. this node is the last one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val nextNode: LinkedSearchTreeNode<Element, Priority>?
    /**
     * Returns node corresponding to the previous place from this node's corresponding place.
     * If there is no such node (i.e. this node is the first one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val previousNode: LinkedSearchTreeNode<Element, Priority>?
}

/**
 * Represents a result of search for specified priority in a search tree.
 * There are exactly five possible outcomes of the operation:
 * 1. The search tree is empty.
 *   In that case, the [Empty] object is returned.
 * 2. The priority coincides with some node's priority.
 *   In that case, the [Coincidence] instance with the node is returned.
 * 3. The priority is strictly less than the minimal priority.
 *   In that case, the [LessThanMinimum] instance with the minimal noded is returned.
 * 4. The priority is strictly greater than the maximal priority.
 *   In that case, the [GreaterThanMaximum] instance with the maximal noded is returned.
 * 5. The priority lies strictly between priorities of two neighbour nodes.
 *   In that case the [Between] instance with the [lowerBound][Between.lowerBound] and [upperBound][Between.upperBound] nodes is returned.
 */
public sealed interface SearchSegmentResult<out SearchTreeNode> {
    /**
     * Represents a case when the search tree is empty.
     * In that case, the object is returned.
     */
    public data object Empty : SearchSegmentResult<Nothing>
    /**
     * Represents a case when the priority coincides with some node's priority.
     * In that case, the class's instance with the node is returned.
     */
    public data class Coincidence<out SearchTreeNode>(val value: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    /**
     * Represents a case when the priority is strictly less than the minimal priority.
     * In that case, the class's instance with the minimal noded is returned.
     */
    public data class LessThanMinimum<out SearchTreeNode>(val minimum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    /**
     * Represents a case when the priority is strictly greater than the maximal priority.
     * In that case, the class's instance with the maximal noded is returned.
     */
    public data class GreaterThanMaximum<out SearchTreeNode>(val maximum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    /**
     * Represents a case when the priority lies strictly between priorities of two neighbour nodes.
     * In that case the class's instance with the [lowerBound][Between.lowerBound] and [upperBound][Between.upperBound] nodes is returned.
     */
    public data class Between<out SearchTreeNode>(val lowerBound: SearchTreeNode, val upperBound: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
}

/**
 * In Kone, search trees are structures that consist of:
 * 1. an order on values called "priorities" (thus, search tree is contextful)
 * 2. and a tree (in [computer science sense of the word](https://en.wikipedia.org/wiki/Tree_(abstract_data_type)))
 *     that holds several pairs of one priority value and one corresponding element in each its vertex
 *     and satisfies *search tree property*:
 *     - each vertex's children are linearly ordered,
 *     - each non-leaf vertex contains one pair less than the number of its children,
 *     - for any given non-leaf vertex \(A\),
 *         if \(C_0\), ..., \(C_n\) are children of \(A\) sorted increasingly and \(P_1\), ..., \(P_n\) are \(A\)'s priorities sorted increasingly as well,
 *         then \(C_0 \leqslant P_1 \leqslant C_1 \leqslant ... \leqslant C_{n-1} \leqslant P_n \leqslant C_n\)
 *         where \(C \leqslant P\) (\(P \leqslant C\)) means that all the priorities in a subtree with root \(C\) are less (greater) or equal to \(P\).
 *
 * This interface's inheritors must have some specific structure that provides optimised search.
 * Without it (or with bad time complexity like \(O(n)\)) the interface should not be used.
 *
 * @usesMathJax
 */
public interface SearchTree<Element, Priority> {
    /**
     * Number of elements in the collection.
     */
    public val size: UInt
    /**
     * Reified set that is a view on nodes of that search tree.
     *
     * Because there is exactly one instance of search tree node corresponding to each entry in the search tree,
     * the collection is a reified set which equality is the absolute equality.
     */
    public val nodesView: KoneReifiedSet<SearchTreeNode<Element, Priority>>
    /**
     * Iterable that is a view on elements of that search tree.
     */
    public val prioritiesView: KoneSet<Priority>
    public val elementsView: KoneIterable<Element>
    
    /**
     * Adds the [element] with corresponding [priority] to the search tree
     * and returns constructed node that corresponds to the element and the priority.
     */
    public fun add(element: Element, priority: Priority): SearchTreeNode<Element, Priority>
    /**
     * Finds and returns a node in the search tree with equal priority. If there is no such node, returns `null`.
     */
    public fun find(priority: Priority): SearchTreeNode<Element, Priority>?
    /**
     * Finds a segment in the search tree where the priority lies.
     * See [SearchSegmentResult] for description of the segment.
     */
    public fun findSegmentFor(priority: Priority): SearchSegmentResult<SearchTreeNode<Element, Priority>>
    
    /**
     * Checks if there is a node with the specified priority.
     */
    public operator fun contains(priority: Priority): Boolean = find(priority) != null
}

/**
 * Represents a linked search tree.
 * See [SearchTree] for general definition of a search tree.
 *
 * Linked search tree is a search tree with structure of a linked list.
 * However, the order of elements in linked structure may be defined in different ways.
 * For example, it may be a chronological order
 * (i.e. new elements are added to the end of the order).
 * Or an order induced by the search tree's inner order
 * (i.e. the first element is the node with minimal priority, then the node with next minimal priority, and so on).
 * See implementations' documentations to get the behaviour you need.
 */
public interface LinkedSearchTree<Element, Priority> : SearchTree<Element, Priority> {
    /**
     * Linked reified set that is a view on nodes of that search tree.
     *
     * The order of nodes in the linked list part of the structure coincides
     * with the order of the nodes in the search tree itself.
     *
     * Because there is exactly one instance of search tree node corresponding to each entry in the search tree,
     * the collection is a reified set which equality is the absolute equality.
     */
    override val nodesView: KoneLinkedReifiedSet<LinkedSearchTreeNode<Element, Priority>>
    /**
     * Reversible iterable that is a view on elements of that search tree.
     *
     * The order of elements in the iterable coincides with the order of the corresponding nodes in the search tree itself.
     */
    override val prioritiesView: KoneLinkedSet<Priority>
    override val elementsView: KoneReversibleIterable<Element>

    override fun add(element: Element, priority: Priority): LinkedSearchTreeNode<Element, Priority>
    override fun find(priority: Priority): LinkedSearchTreeNode<Element, Priority>?
    override fun findSegmentFor(priority: Priority): SearchSegmentResult<LinkedSearchTreeNode<Element, Priority>>
}
/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.searchTree

import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet


public interface SearchTreeNode<Element, out Priority> {
    public val isDetached: Boolean
    
    public var element: Element
    public val priority: Priority
    public fun remove()
}

public interface LinkedSearchTreeNode<Element, out Priority> : SearchTreeNode<Element, Priority> {
    public val nextNode: LinkedSearchTreeNode<Element, Priority>?
    public val previousNode: LinkedSearchTreeNode<Element, Priority>?
}

public sealed interface SearchSegmentResult<out SearchTreeNode> {
    public data object Empty : SearchSegmentResult<Nothing>
    public data class Coincidence<out SearchTreeNode>(val value: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class Between<out SearchTreeNode>(val lowerBound: SearchTreeNode, val upperBound: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class LessThanMinimum<out SearchTreeNode>(val minimum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class GreaterThanMaximum<out SearchTreeNode>(val maximum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
}

public interface SearchTree<Element, Priority> {
    public val size: UInt
    public val nodesView: KoneReifiedSet<SearchTreeNode<Element, Priority>>
    public val elementsView: KoneSet<Element>
    
    public fun add(element: Element, priority: Priority): SearchTreeNode<Element, Priority>
    public fun find(priority: Priority): SearchTreeNode<Element, Priority>?
    public fun findSegmentFor(priority: Priority): SearchSegmentResult<SearchTreeNode<Element, Priority>>
    
    public operator fun contains(priority: Priority): Boolean = find(priority) != null
}

public interface LinkedSearchTree<Element, Priority> : SearchTree<Element, Priority> {
    override val nodesView: KoneLinkedReifiedSet<LinkedSearchTreeNode<Element, Priority>>
    override val elementsView: KoneLinkedSet<Element>

    override fun add(element: Element, priority: Priority): LinkedSearchTreeNode<Element, Priority>
    override fun find(priority: Priority): LinkedSearchTreeNode<Element, Priority>?
    override fun findSegmentFor(priority: Priority): SearchSegmentResult<LinkedSearchTreeNode<Element, Priority>>
}
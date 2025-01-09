/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.searchTree

import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet


public interface SearchTreeNode<out Element> {
    public val isDetached: Boolean
    
    public val element: Element
    public fun remove()
}

public interface LinkedSearchTreeNode<out Element> : SearchTreeNode<Element> {
    public val nextNode: LinkedSearchTreeNode<Element>?
    public val previousNode: LinkedSearchTreeNode<Element>?
}

public sealed interface SearchSegmentResult<out SearchTreeNode> {
    public data object Empty : SearchSegmentResult<Nothing>
    public data class Coincidence<out SearchTreeNode>(val value: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class Between<out SearchTreeNode>(val lowerBound: SearchTreeNode, val upperBound: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class LessThanMinimum<out SearchTreeNode>(val minimum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
    public data class GreaterThanMaximum<out SearchTreeNode>(val maximum: SearchTreeNode) : SearchSegmentResult<SearchTreeNode>
}

public interface SearchTree<Element> {
    public val size: UInt
    public val nodesView: KoneReifiedSet<SearchTreeNode<Element>>
    public val elementsView: KoneSet<Element>
    
    public fun add(element: Element): SearchTreeNode<Element>
    public fun find(element: Element): SearchTreeNode<Element>?
    public fun findSegmentFor(element: Element): SearchSegmentResult<SearchTreeNode<Element>>?
    
    public operator fun contains(element: Element): Boolean = find(element) != null
}

public interface LinkedSearchTree<Element> : SearchTree<Element> {
    override val nodesView: KoneLinkedReifiedSet<LinkedSearchTreeNode<Element>>
    override val elementsView: KoneLinkedSet<Element>

    override fun add(element: Element): LinkedSearchTreeNode<Element>
    override fun find(element: Element): LinkedSearchTreeNode<Element>?
    override fun findSegmentFor(element: Element): SearchSegmentResult<LinkedSearchTreeNode<Element>>
}
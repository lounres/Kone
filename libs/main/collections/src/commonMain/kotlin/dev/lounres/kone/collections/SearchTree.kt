/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface SearchTreeNode<out E> {
    public val element: E
    public fun remove()
}

public interface LinkedSearchTreeNode<out E> : SearchTreeNode<E> {
    public val nextNode: LinkedSearchTreeNode<E>?
    public val previousNode: LinkedSearchTreeNode<E>?
}

public sealed interface SearchSegmentResult<out STN> {
    public data object Empty : SearchSegmentResult<Nothing>
    public data class Coincidence<out STN>(val value: STN) : SearchSegmentResult<STN>
    public data class Between<out STN>(val lowerBound: STN, val upperBound: STN) : SearchSegmentResult<STN>
    public data class LessThanMinimum<out STN>(val minimum: STN) : SearchSegmentResult<STN>
    public data class GreaterThanMaximum<out STN>(val maximum: STN) : SearchSegmentResult<STN>
}

public interface SearchTree<E> {
    public val size: UInt
    public val nodesView: KoneSet<SearchTreeNode<E>>
    public val elementsView: KoneSet<E>
    
    public fun add(element: E): SearchTreeNode<E>
    public fun find(element: E): SearchTreeNode<E>?
//    public fun findSegmentFor(element: E): SearchSegmentResult<SearchTreeNode<E>>?
    
    public operator fun contains(element: E): Boolean = find(element) != null
}

public interface LinkedSearchTree<E> : SearchTree<E> {
    override val nodesView: KoneLinkedSet<LinkedSearchTreeNode<E>>
    override val elementsView: KoneLinkedSet<E>
    
    override fun add(element: E): LinkedSearchTreeNode<E>
    override fun find(element: E): LinkedSearchTreeNode<E>?
    public fun findSegmentFor(element: E): SearchSegmentResult<LinkedSearchTreeNode<E>>
}
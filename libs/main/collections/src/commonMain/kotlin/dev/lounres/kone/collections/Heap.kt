/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface HeapNode<E, P> {
    public var element: E
    public var priority: P
    public fun remove()
}

public interface MinimumHeap<E, P> {
    public val size: UInt
    public val nodesView: KoneSet<HeapNode<E, P>>
    public val elementsView: KoneIterable<E>
    
    public fun add(element: E, priority: P): HeapNode<E, P>
    public fun takeMinimum(): HeapNode<E, P>
    public fun popMinimum(): HeapNode<E, P>
}

public interface MaximumHeap<E, P> {
    public val size: UInt
    public val nodesView: KoneSet<HeapNode<E, P>>
    public val elementsView: KoneIterable<E>
    
    public fun add(element: E, priority: P): HeapNode<E, P>
    public fun takeMaximum(): HeapNode<E, P>
    public fun popMaximum(): HeapNode<E, P>
}

public interface LinkedHeapNode<E, P> : HeapNode<E, P> {
    public val nextNode: LinkedHeapNode<E, P>?
    public val previousNode: LinkedHeapNode<E, P>?
}

public interface LinkedMinimumHeap<E, P> : MinimumHeap<E, P> {
    override val nodesView: KoneLinkedSet<LinkedHeapNode<E, P>>
    override val elementsView: KoneList<E>
    
    override fun add(element: E, priority: P): LinkedHeapNode<E, P>
    override fun takeMinimum(): LinkedHeapNode<E, P>
    override fun popMinimum(): LinkedHeapNode<E, P>
}

public interface LinkedMaximumHeap<E, P> : MaximumHeap<E, P> {
    override val nodesView: KoneLinkedSet<LinkedHeapNode<E, P>>
    override val elementsView: KoneList<E>
    
    override fun add(element: E, priority: P): LinkedHeapNode<E, P>
    override fun takeMaximum(): LinkedHeapNode<E, P>
    override fun popMaximum(): LinkedHeapNode<E, P>
}
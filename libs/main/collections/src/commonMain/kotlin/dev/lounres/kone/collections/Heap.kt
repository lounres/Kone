/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface HeapNode<Element, Priority> {
    public val isDetached: Boolean
    
    public var element: Element
    public var priority: Priority
    public fun remove()
}

public interface MinimumHeap<Element, Priority> {
    public val size: UInt
    public val nodesView: KoneReifiedSet<HeapNode<Element, Priority>>
    public val elementsView: KoneIterable<Element>
    
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    public fun takeMinimum(): HeapNode<Element, Priority>
    public fun popMinimum(): HeapNode<Element, Priority>
}

public interface MaximumHeap<Element, Priority> {
    public val size: UInt
    public val nodesView: KoneReifiedSet<HeapNode<Element, Priority>>
    public val elementsView: KoneIterable<Element>
    
    public fun add(element: Element, priority: Priority): HeapNode<Element, Priority>
    public fun takeMaximum(): HeapNode<Element, Priority>
    public fun popMaximum(): HeapNode<Element, Priority>
}

public interface LinkedHeapNode<Element, Priority> : HeapNode<Element, Priority> {
    public val nextNode: LinkedHeapNode<Element, Priority>?
    public val previousNode: LinkedHeapNode<Element, Priority>?
}

public interface LinkedMinimumHeap<Element, Priority> : MinimumHeap<Element, Priority> {
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    override val elementsView: KoneList<Element>
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority>
    override fun takeMinimum(): LinkedHeapNode<Element, Priority>
    override fun popMinimum(): LinkedHeapNode<Element, Priority>
}

public interface LinkedMaximumHeap<Element, Priority> : MaximumHeap<Element, Priority> {
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>>
    override val elementsView: KoneList<Element>
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority>
    override fun takeMaximum(): LinkedHeapNode<Element, Priority>
    override fun popMaximum(): LinkedHeapNode<Element, Priority>
}
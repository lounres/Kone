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
    public val nodesView: KoneIterableListSet<HeapNode<E, P>>
    public val elementsView: KoneIterableList<E>
    
    public fun add(element: E, priority: P): HeapNode<E, P>
    public fun takeMinimum(): HeapNode<E, P>
    public fun popMinimum(): HeapNode<E, P>
}

public interface MaximumHeap<E, P> {
    public val size: UInt
    public val nodesView: KoneIterableListSet<HeapNode<E, P>>
    public val elementsView: KoneIterableList<E>
    
    public fun add(element: E, priority: P): HeapNode<E, P>
    public fun takeMaximum(): HeapNode<E, P>
    public fun popMaximum(): HeapNode<E, P>
}
/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order


public interface HeapNode<out E, P> {
    public val element: E
    public var priority: P
    public fun remove()
}

public interface MinimumHeap<E, P>: KoneCollection<E> {
    public fun add(element: E, priority: P): HeapNode<E, P>
    public fun decreasePriority(element: E, newPriority: P)
    public fun popMinimum(): HeapNode<E, P>
}

public interface MinimumHeapWithContext<E, out EC: Equality<E>, P, out PC: Order<P>>: MinimumHeap<E, P> {
    public val elementContext: EC
    public val priorityContext: PC
}
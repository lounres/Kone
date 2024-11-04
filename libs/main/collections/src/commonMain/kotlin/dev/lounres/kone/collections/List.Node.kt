/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneListNode<out E> {
    public val element: E
    // TODO: Make a note in docs that this one might be non-constant time computable
    public val index: UInt
    
    public val nextNode: KoneListNode<E>?
    public val previousNode: KoneListNode<E>?
    
    public fun iteratorFromHere(): KoneLinearIterator<E>
}

public interface KoneSettableListNode<E> : KoneListNode<E> {
    override var element: E
    
    override val nextNode: KoneSettableListNode<E>?
    override val previousNode: KoneSettableListNode<E>?
    
    override fun iteratorFromHere(): KoneSettableLinearIterator<E>
}

public interface KoneMutableListNode<E> : KoneSettableListNode<E> {
    override var element: E
    public fun remove()
    
    override val nextNode: KoneMutableListNode<E>?
    override val previousNode: KoneMutableListNode<E>?
    
    override fun iteratorFromHere(): KoneMutableLinearIterator<E>
}
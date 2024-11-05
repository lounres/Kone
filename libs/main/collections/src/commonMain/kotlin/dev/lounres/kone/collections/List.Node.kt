/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneListNode<out Element> {
    public val element: Element
    // TODO: Make a note in docs that this one might be non-constant time computable
    public val index: UInt
    
    public val nextNode: KoneListNode<Element>?
    public val previousNode: KoneListNode<Element>?
    
    public fun iteratorFromBeforeHere(): KoneLinearIterator<Element>
    public fun iteratorFromAfterHere(): KoneLinearIterator<Element>
}

public interface KoneSettableListNode<Element> : KoneListNode<Element> {
    override var element: Element
    
    override val nextNode: KoneSettableListNode<Element>?
    override val previousNode: KoneSettableListNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneSettableLinearIterator<Element>
    override fun iteratorFromAfterHere(): KoneSettableLinearIterator<Element>
}

public interface KoneMutableListNode<Element> : KoneSettableListNode<Element> {
    override var element: Element
    public fun remove()
    
    override val nextNode: KoneMutableListNode<Element>?
    override val previousNode: KoneMutableListNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element>
    override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element>
}
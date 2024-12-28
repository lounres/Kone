/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneSetNode<out Element> {
    public val isDetached: Boolean
    
    public val element: Element
}

public interface KoneMutableSetNode<out Element> : KoneSetNode<Element> {
    public fun remove()
}

public interface KoneLinkedSetNode<out Element> : KoneSetNode<Element> {
    public val nextNode: KoneLinkedSetNode<Element>?
    public val previousNode: KoneLinkedSetNode<Element>?
    
    public fun iteratorFromBeforeHere(): KoneLinkedNoddedSetIterator<Element>
    public fun iteratorFromAfterHere(): KoneLinkedNoddedSetIterator<Element>
}

public interface KoneMutableLinkedSetNode<out Element> : KoneMutableSetNode<Element>, KoneLinkedSetNode<Element> {
    override val nextNode: KoneMutableLinkedSetNode<Element>?
    override val previousNode: KoneMutableLinkedSetNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneMutableLinkedNoddedSetIterator<Element>
    override fun iteratorFromAfterHere(): KoneMutableLinkedNoddedSetIterator<Element>
}
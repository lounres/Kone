/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: KoneSet is marked as covariant, but element context can't check equality for element of supertype
//  So there is need in checking that element context can work with arbitrary argument
public interface KoneSet<out Element> : KoneIterable<Element> {
    public val size: UInt
    
    public operator fun contains(element: @UnsafeVariance Element): Boolean
}

public interface KoneMutableSet<Element> : KoneSet<Element> {
    public fun add(element: Element)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element)
    
    public fun remove(element: Element)
    public fun removeAllThat(predicate: (element: Element) -> Boolean)
    public fun removeAll()
}

public interface KoneNoddedSet<out Element> : KoneSet<Element> {
    public val nodes: KoneIterable<KoneSetNode<Element>>
}

public interface KoneNoddedMutableSet<Element> : KoneMutableSet<Element>, KoneNoddedSet<Element> {
    override val nodes: KoneIterable<KoneMutableSetNode<Element>>
    
    public fun addNode(element: Element): KoneMutableSetNode<Element>
    override fun add(element: Element) { addNode(element) }
}

public interface KoneLinkedSet<out Element> : KoneSet<Element>, KoneList<Element>
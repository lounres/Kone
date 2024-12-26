/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.absoluteReifiedEquality


@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneSet<Element> : KoneIterable<Element> {
    public operator fun contains(element: Element): Boolean
    
    override fun iterator(): KoneSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableSet<Element> : KoneSet<Element> {
    public fun add(element: Element)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element)
    
    public fun remove(element: Element)
    public fun removeAllThat(predicate: (element: Element) -> Boolean)
    public fun removeAll()
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedSet<Element> : KoneSet<Element> {
    public val nodesView: KoneReifiedSet<KoneSetNode<Element>>
    public val nodes: KoneReifiedSet<KoneSetNode<Element>> get() = nodesView
    public fun nodeOfOrNull(element: @UnsafeVariance Element): KoneSetNode<Element>?
    public fun nodeOf(element: @UnsafeVariance Element): KoneSetNode<Element>
    
    override fun iterator(): KoneNoddedSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedSet<Element> : KoneMutableSet<Element>, KoneNoddedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneMutableSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneMutableSetNode<Element>>
        get() = nodesView.toKoneReifiedSet(absoluteReifiedEquality())
    public fun addNode(element: Element): KoneMutableSetNode<Element>
    override fun add(element: Element) { addNode(element) }
    
    override fun iterator(): KoneMutableNoddedSetIterator<Element>
}

// TODO: Think about abstractions for linked sets
//@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
//public interface KoneLinkedSet<Element> : KoneSet<Element>, KoneList<Element>
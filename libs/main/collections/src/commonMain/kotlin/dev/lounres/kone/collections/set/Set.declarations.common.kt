/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterables.KoneIterable
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
    override fun nodeOfOrNull(element: Element): KoneMutableSetNode<Element>?
    override fun nodeOf(element: Element): KoneMutableSetNode<Element>
    public fun addNode(element: Element): KoneMutableSetNode<Element>
    override fun add(element: Element) { addNode(element) }
    
    override fun iterator(): KoneMutableNoddedSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedSet<Element> : KoneSet<Element> {
    override fun iterator(): KoneLinkedSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedSet<Element> : KoneLinkedSet<Element>, KoneMutableSet<Element> {
    override fun iterator(): KoneMutableLinkedSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedNoddedSet<Element> : KoneNoddedSet<Element>, KoneLinkedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneLinkedSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneLinkedSetNode<Element>> get() = nodesView
    override fun nodeOfOrNull(element: @UnsafeVariance Element): KoneLinkedSetNode<Element>?
    override fun nodeOf(element: @UnsafeVariance Element): KoneLinkedSetNode<Element>
    
    override fun iterator(): KoneLinkedNoddedSetIterator<Element>
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedNoddedSet<Element> : KoneLinkedNoddedSet<Element>, KoneMutableLinkedSet<Element>, KoneMutableNoddedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneMutableLinkedSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneMutableLinkedSetNode<Element>>
        get() = nodesView.toKoneReifiedSet(absoluteReifiedEquality())
    override fun nodeOfOrNull(element: @UnsafeVariance Element): KoneMutableLinkedSetNode<Element>?
    override fun nodeOf(element: @UnsafeVariance Element): KoneMutableLinkedSetNode<Element>
    override fun addNode(element: Element): KoneMutableLinkedSetNode<Element>
    
    override fun iterator(): KoneMutableLinkedNoddedSetIterator<Element>
}
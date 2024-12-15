/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneSet<Element> : KoneIterable<Element> {
    public operator fun contains(element: Element): Boolean
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
    public val nodes: KoneIterable<KoneSetNode<Element>>
    // TODO: Maybe add the following methods
//    public fun nodeOfOrNull(element: @UnsafeVariance Element): KoneSetNode<Element>?
//    public fun nodeOf(element: @UnsafeVariance Element): KoneSetNode<Element> = nodeOfOrNull(element) ?: TODO()
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedMutableSet<Element> : KoneMutableSet<Element>, KoneNoddedSet<Element> {
    override val nodes: KoneIterable<KoneMutableSetNode<Element>>
    
    public fun addNode(element: Element): KoneMutableSetNode<Element>
    override fun add(element: Element) { addNode(element) }
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedSet<Element> : KoneSet<Element>, KoneList<Element>
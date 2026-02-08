/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.NoCorrespondingNodeException
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneRemovableIterable
import dev.lounres.kone.collections.iterables.KoneReversibleIterable
import dev.lounres.kone.collections.iterables.KoneReversibleRemovableIterable
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


// TODO: Add contracts on `toString()`, `equals` and `hashCode`.
/**
 * Represents a composition of [Equality] context
 * and an unordered collection of elements without repetitions with respect to the equality context.
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneSet<Element> : KoneIterable<Element> {
    /**
     * Checks if there is an element equal to the specified [element].
     */
    public operator fun contains(element: Element): Boolean
    
    override fun iterator(): KoneSetIterator<Element>
    
    public companion object
}

/**
 * Represents a composition of [Equality] context
 * and an unordered collection of elements without repetitions with respect to the equality context
 * with possibility to add and remove elements from it.
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableSet<Element> : KoneSet<Element>, KoneRemovableIterable<Element> {
    /**
     * Adds the [element] to the collection if there is no equal to it element in the collection already.
     */
    public fun add(element: Element)
    /**
     * Adds elements `builder(0u)`, `builder(1u)`, ..., `builder(number-1u)` to the collection.
     * Each element is added iff there is no equal to it element in the collection already.
     */
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element)
    
    /**
     * Removes an element equal to the specified [element] from the collection if there is such one
     * or does nothing otherwise.
     */
    public fun remove(element: Element)
    /**
     * Removes all elements satisfying the [predicate] from the collection.
     */
    public fun removeAllThat(predicate: (element: Element) -> Boolean)
    /**
     * Removes all elements from the collection.
     */
    public fun removeAll()
    
    public companion object
}

/**
 * Represents a nodded version of [KoneSet].
 *
 * It means that there is exactly one [KoneSetNode] corresponding to each place
 * that can effectively access the places element and index as well as
 * other things that can be found in its documentation.
 *
 * @see KoneSet
 * @see KoneSetNode
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedSet<Element> : KoneSet<Element> {
    /**
     * Reified set that is a view on nodes of that set.
     *
     * Because there is exactly one instance of set node corresponding to each element in the set,
     * the collection is a reified set which equality is the absolute equality.
     */
    public val nodesView: KoneReifiedSet<KoneSetNode<Element>>
    /**
     * Returns new reified set consisting of the current nodes of that set.
     *
     * Because there is exactly one instance of set node corresponding to each element in the set,
     * the collection is a reified set which equality is the absolute equality.
     */
    public val nodes: KoneReifiedSet<KoneSetNode<Element>> get() = nodesView
    /**
     * Returns a node that corresponds to the element in the set equal to the specified [element]
     * or `null` if there is no such element.
     */
    public fun nodeOfOrNull(element: @UnsafeVariance Element): KoneSetNode<Element>?
    /**
     * Returns a node that corresponds to the element in the set equal to the specified [element]
     * or throws [NoCorrespondingNodeException] if there is no such element.
     *
     * @throws NoCorrespondingNodeException if there is no element equal to the specified [element].
     */
    public fun nodeOf(element: @UnsafeVariance Element): KoneSetNode<Element>
    
    override fun iterator(): KoneNoddedSetIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneMutableSet].
 *
 * It means that there is exactly one [KoneMutableSetNode] corresponding to each place
 * that can effectively access the places element and index as well as
 * other things that can be found in its documentation.
 *
 * @see KoneMutableSet
 * @see KoneMutableSetNode
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedSet<Element> : KoneMutableSet<Element>, KoneNoddedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneMutableSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneMutableSetNode<Element>>
        get() = nodesView.toKoneReifiedSet(
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    override fun nodeOfOrNull(element: Element): KoneMutableSetNode<Element>?
    override fun nodeOf(element: Element): KoneMutableSetNode<Element>
    public fun addNode(element: Element): KoneMutableSetNode<Element>
    override fun add(element: Element) { addNode(element) }
    
    override fun iterator(): KoneMutableNoddedSetIterator<Element>
    
    public companion object
}

/**
 * Represents a composition of [Equality] context,
 * an unordered collection of elements without repetitions with respect to the equality context,
 * and structure of a linked list on the elements of the collection.
 *
 * However, the order of elements in the linked structure may be defined in different ways.
 * For example, it may be a chronological order
 * (i.e. new elements are added to the end of the order and reconstruction of the set won't change the order).
 * Or an order corresponding to the inner structure of the set
 * (for example, in the order induced by som specified [Order],
 * and reconstruction of the set will change the order).
 * See implementations' documentations to get the behaviour you need.
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedSet<Element> : KoneSet<Element>, KoneReversibleIterable<Element> {
    override fun iterator(): KoneLinkedSetIterator<Element>
    
    public companion object
}

/**
 * Represents a composition of [Equality] context,
 * an unordered collection of elements without repetitions with respect to the equality context
 * with possibility to add and remove elements from it,
 * and structure of a linked list on the elements of the collection.
 *
 * However, the order of elements in the linked structure may be defined in different ways.
 * For example, it may be a chronological order
 * (i.e. new elements are added to the end of the order and reconstruction of the set won't change the order).
 * Or an order corresponding to the inner structure of the set
 * (for example, in the order induced by som specified [Order],
 * and reconstruction of the set will change the order).
 * See implementations' documentations to get the behaviour you need.
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedSet<Element> : KoneLinkedSet<Element>, KoneMutableSet<Element>, KoneReversibleRemovableIterable<Element> {
    override fun iterator(): KoneMutableLinkedSetIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneLinkedSet].
 *
 * It means that there is exactly one [KoneLinkedSetNode] corresponding to each place
 * that can effectively access the places element and index as well as
 * other things that can be found in its documentation.
 *
 * @see KoneLinkedSet
 * @see KoneLinkedSetNode
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedNoddedSet<Element> : KoneNoddedSet<Element>, KoneLinkedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneLinkedSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneLinkedSetNode<Element>> get() = nodesView
    override fun nodeOfOrNull(element: @UnsafeVariance Element): KoneLinkedSetNode<Element>?
    override fun nodeOf(element: @UnsafeVariance Element): KoneLinkedSetNode<Element>
    
    override fun iterator(): KoneLinkedNoddedSetIterator<Element>
    
    public companion object
}

/**
 * Represents a nodded version of [KoneMutableLinkedSet].
 *
 * It means that there is exactly one [KoneMutableLinkedSetNode] corresponding to each place
 * that can effectively access the places element and index as well as
 * other things that can be found in its documentation.
 *
 * @see KoneMutableLinkedSet
 * @see KoneMutableLinkedSetNode
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedNoddedSet<Element> : KoneLinkedNoddedSet<Element>, KoneMutableLinkedSet<Element>, KoneMutableNoddedSet<Element> {
    override val nodesView: KoneReifiedSet<KoneMutableLinkedSetNode<Element>>
    override val nodes: KoneReifiedSet<KoneMutableLinkedSetNode<Element>>
        get() = nodesView.toKoneReifiedSet(
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    override fun nodeOfOrNull(element: @UnsafeVariance Element): KoneMutableLinkedSetNode<Element>?
    override fun nodeOf(element: @UnsafeVariance Element): KoneMutableLinkedSetNode<Element>
    override fun addNode(element: Element): KoneMutableLinkedSetNode<Element>
    
    override fun iterator(): KoneMutableLinkedNoddedSetIterator<Element>
    
    public companion object
}